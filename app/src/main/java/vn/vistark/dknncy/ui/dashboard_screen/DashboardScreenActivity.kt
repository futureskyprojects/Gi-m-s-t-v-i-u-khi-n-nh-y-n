package vn.vistark.dknncy.ui.dashboard_screen

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_dashboard_screen.*
import org.opencv.android.*
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.core.CvType
import org.opencv.core.Mat
import vn.vistark.dknncy.R
import vn.vistark.dknncy.data.arduino_community.ArduinoInit
import vn.vistark.dknncy.data.firebase.FirebaseConfig
import vn.vistark.dknncy.data.modal.Auto
import vn.vistark.dknncy.data.modal.CurrentDetail
import vn.vistark.dknncy.data.modal.DefaultTimerItem
import vn.vistark.dknncy.data.modal.TimerItem
import vn.vistark.dknncy.ui.dashboard_screen.timer_dialog.TimerDialog
import vn.vistark.dknncy.ui.dashboard_screen.timer_list.TimerListAdapter
import vn.vistark.dknncy.utils.CommonUtils
import vn.vistark.dknncy.utils.NetworkUtils
import vn.vistark.dknncy.utils.ScreenUtils
import vn.vistark.dknncy.utils.TimeUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DashboardScreenActivity : AppCompatActivity(), CvCameraViewListener2 {
    private val timerItems = ArrayList<TimerItem>()
    lateinit var adapter: TimerListAdapter
    private lateinit var noInternetDialog: ProgressDialog

    private var establishStartAt: Long = 0

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    var mRgba: Mat? = null

    //    var mRgbaF: Mat? = null
//    var mRgbaT: Mat? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_screen)
        ScreenUtils.hideTitleBarAndTransparentStatusBar(this)

        hideTimerListEmptyNotify()

        noInternetDialog = CommonUtils.showNoInternetDialog(this)
        establishTime()
        runningCurrentTime()

        initEvents()
        initAutoSync()

        initRecyclerView()

        initCamera()

        ArduinoInit.asyncCommunity(this)

        checking()
    }

    // Hiển thị hộp thoại xác nhận thoát hay không
    fun powerOffConfirm() {
        val exitConfirm = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        exitConfirm.titleText = "ĐÓNG ỨNG DỤNG?"
        exitConfirm.confirmText = "Đóng"
        exitConfirm.cancelText = "Hủy"
        // Bắt sự kiện khi người dùng nhấn đồng ý đóng
        exitConfirm.setConfirmClickListener { finish() }
        // Bắt sự kiện khi người dùng hủy đóng
        exitConfirm.setCancelClickListener { sweetAlertDialog -> sweetAlertDialog.dismiss() }
        //
        try {
            exitConfirm.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changeListByStateOfAutoPower(state: Boolean) {
        FirebaseConfig.autoRef.child(Auto.powerKey).setValue(state)
        if (state) {
            DefaultTimerItem.timerPortA.isPower = state
            DefaultTimerItem.timerPortB.isPower = state
            DefaultTimerItem.timerPortC.isPower = state
            DefaultTimerItem.timerPortD.isPower = state
            FirebaseConfig.updateData(DefaultTimerItem.timerPortA)
            FirebaseConfig.updateData(DefaultTimerItem.timerPortB)
            FirebaseConfig.updateData(DefaultTimerItem.timerPortC)
            FirebaseConfig.updateData(DefaultTimerItem.timerPortD)
        }

    }

    private fun initAutoSync() {
        Auto().apply {
            sync(FirebaseConfig.myRef, this@DashboardScreenActivity)
            powerSync(this@DashboardScreenActivity)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun runningCurrentTime() {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val date = Date(System.currentTimeMillis())
                updateCurrentTimeView(simpleDateFormat.format(date))
            }
        }, 1000, 1000)
    }

    // Cập nhật và hiển thị giờ đã khởi chạy
    private fun establishTime() {
        establishStartAt = System.currentTimeMillis()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val establishedTime = TimeUtils.millisToCounter(System.currentTimeMillis() - establishStartAt)
                updateEstablishTimeView(establishedTime)
                runOnUiThread {
                    if (NetworkUtils.isNetworkConnected(this@DashboardScreenActivity)) {
                        if (noInternetDialog.isShowing) {
                            noInternetDialog.dismiss()
//                            if (VideoTransfer.init()) {
//                                //
//                            } else {
//                                Toasty.error(this@DashboardScreenActivity, "Kết nối đến máy chủ hình ảnh thất bại, hãy khởi động lại", Toasty.LENGTH_SHORT, false).show()
//                            }
                        }
                    } else {
                        if (noInternetDialog.isShowing) {
                            noInternetDialog.show()
                        }
                    }
                }
            }
        }, 1000, 1000)
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                (mOpenCvCameraView as CameraBridgeViewBase).enableView()
                showMultiCamera()
            } else {
                super.onManagerConnected(status)
            }
        }
    }

    private fun initCamera() {
        (mOpenCvCameraView as CameraBridgeViewBase).visibility = SurfaceView.VISIBLE
        (mOpenCvCameraView as CameraBridgeViewBase).setCvCameraViewListener(this)
    }

    private fun initRecyclerView() {
        rvTimerList.setHasFixedSize(true)
        rvTimerList.layoutManager = LinearLayoutManager(this)

        adapter = TimerListAdapter(timerItems)

        rvTimerList.adapter = adapter

        // lắng nghe danh sách hẹn giờ
        FirebaseConfig.timerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                DefaultTimerItem.check(dataSnapshot)
                for (ds in dataSnapshot.children) {
                    val ti = TimerItem().parse(ds) ?: return
                    updateTimerItem(ti)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Lỗi khi đọc dữ liệu.", error.toException())
            }
        })

        // Xóa phần tử
        adapter.onLongClick = { ti ->
            if (ti.isDefault()) {
                Toasty.warning(this, "Không thể xóa hẹn giờ mặc định", Toasty.LENGTH_LONG, false).show()
            } else {
                // Nếu không phải là timer mặc định
                val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                sweetAlertDialog.setTitleText("XÁC NHẬN XÓA HẸN GIỜ?").show()
                sweetAlertDialog.confirmText = "Xóa"
                sweetAlertDialog.cancelText = "Đóng"
                sweetAlertDialog.setConfirmClickListener { s ->
                    FirebaseConfig.timerRef.child(ti.id.toString() + "").removeValue()
                    timerItems.remove(ti)
                    adapter.notifyDataSetChanged()
                    if (timerItems.indexOf(ti) < 0) {
                        Toasty.success(this, "Đã xóa").show()
                    }
                    s.dismiss()
                }
                sweetAlertDialog.setCancelClickListener { s -> s.dismiss() }
                sweetAlertDialog.show()
            }
        }

        // Sửa phần tử
        adapter.onClick = { ti ->
            TimerDialog.showEdit(this, ti)
        }
    }

    private fun updateTimerItem(timerItem: TimerItem) {
        val index = timerItems.indexOfFirst { x -> x.id == timerItem.id }
        if (index >= 0) {
            if (timerItems[index].isDiff(timerItem))
                timerItems[index].update(timerItem)
        } else {
            timerItems.add(timerItem)
        }
        adapter.notifyDataSetChanged()
    }


    fun showMultiCamera() {
        lnMultiCameraButton.visibility = View.GONE
        ivImageCaptured.visibility = View.GONE
        lnMultiCamLayout.visibility = View.VISIBLE
    }

    fun chooseShowCam1() {
        lnMultiCameraButton.visibility = View.VISIBLE
        ivImageCaptured.visibility = View.VISIBLE
        lnMultiCamLayout.visibility = View.GONE
    }

    fun initEvents() {
        // Bắt sự kiện khi nhấn vào nút đóng
        ivAppPowerOffButton.setOnClickListener { ivAppPowerOffButton.post { powerOffConfirm() } }

        // Bắt sự kiện khi nhấn vào nút thêm mới hẹn giờ
        btnAddNewTimer.setOnClickListener { TimerDialog.showAdd(this@DashboardScreenActivity) }

        // Bắt sự kiện khi nhấn vào nút nhiều camera
        lnMultiCameraButton.setOnClickListener { showMultiCamera() }

        // Sự kiện khi nhấn vào 1 khung hình của cam bất kỳ trong chế độ xem nhiều camera
        // Nhấn màn hình 1
        ivImageCapturedS1.setOnClickListener { chooseShowCam1() }
        // Nhấn màn hình 2,3,4
        val ivs = arrayOf(ivImageCapturedS2, ivImageCapturedS3, ivImageCapturedS4)
        for (v in ivs) {
            v.setOnClickListener { v.post { Toasty.info(this@DashboardScreenActivity, "Không nhận được tín hiệu từ camera này!", Toasty.LENGTH_SHORT, false).show() } }
        }

        // Sự kiện cho nút nguồn auto
        scTimerAutoPower.setOnCheckedChangeListener { compoundButton, b -> changeListByStateOfAutoPower(b) }
    }


    override fun onBackPressed() {
        powerOffConfirm()
    }

    fun updateAutoHumidity(start: String, end: String) {
        tvAutoHumidity.post { tvAutoHumidity.text = "$start - $end%" }
    }

    fun updateAutoTemp(start: String, end: String) {
        tvAutoTemp.post { tvAutoTemp.text = "$start - $end" }
    }

    val stateAutoPower: Boolean
        get() = scTimerAutoPower.isChecked

    fun updateTimerAutoPower(state: Boolean?) {
        scTimerAutoPower.post { scTimerAutoPower.isChecked = state ?: false }
    }

    fun updateHumidityView(humidity: String) {
        tvHumidity.post { tvHumidity.text = "$humidity%" }
    }

    fun updateTemperature(temperature: String) {
        tvTemperature.post { tvTemperature.text = "$temperature°C" }
    }

    fun updateCurrentTimeView(currentTime: String?) {
        tvCurrentTime.post { tvCurrentTime.text = currentTime }
    }

    fun updateEstablishTimeView(establedTime: String?) {
        tvEstablishTime.post { tvEstablishTime.text = establedTime }
    }

    fun showTimerListEmptyNotify() {
        tvTimerListEmptyNotify.visibility = View.VISIBLE
    }

    fun hideTimerListEmptyNotify() {
        tvTimerListEmptyNotify.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback)
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onPause() {
        super.onPause()
        (mOpenCvCameraView as CameraBridgeViewBase).disableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        (mOpenCvCameraView as CameraBridgeViewBase).disableView()
        Log.e(TAG, "onDestroy: Đã kết thúc.")
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRgba = Mat(height, width, CvType.CV_8UC4)
//        mRgbaF = Mat(height, width, CvType.CV_8UC4)
//        mRgbaT = Mat(width, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        mRgba?.release()
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat? {
        try {
            mRgba = inputFrame.rgba()
            val bmp = Bitmap.createBitmap(mRgba!!.cols(), mRgba!!.rows(), Bitmap.Config.ARGB_8888)
            println("Hiển thị BITMAP size ${mRgba?.size()?.width}")
            Utils.matToBitmap(mRgba, bmp)
            if (ivImageCaptured.visibility == View.VISIBLE) {
                println("Hiển thị 1 cam")
                // Nếu đang hiển thị khung nhìn cho 1 cam duy nhất
                ivImageCaptured.post { ivImageCaptured.setImageBitmap(bmp) }
            } else if (ivImageCapturedS1.visibility == View.VISIBLE) {
                println("Hiển thị nhiều cam")
                // Nếu có cho phép hiển thị cho cam 1 trong 4 cam
                ivImageCapturedS1.post { ivImageCapturedS1.setImageBitmap(bmp) }
            }
//            VideoTransfer.send(bmp)
        } catch (e: Exception) {
            e.printStackTrace()
            //e.printStackTrace();
        }
        return mRgba // This function must return
    }

    companion object {
        private val TAG = DashboardScreenActivity::class.java.simpleName
    }

    private fun checking() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                checkStateUpdateAndSendData()
            }
        }, 1000, 1000)
    }

    private fun checkStateUpdateAndSendData() {
        // Lặp trong danh sách các cổng
        for (i in timerItems.indices) {
            val previous = timerItems[i]
            if (scTimerAutoPower.isChecked) {
                // Nếu chế độ tự động được bật, những timer thủ công sẽ được bỏ qua
                if (!timerItems[i].isDefault()) continue

                // Nếu không phải là những timer thủ công, tiến hành kiếm tra điều kiện và bật/tắt
                val currHumidity = CurrentDetail.humidity.toIntOrNull() ?: return
                val currTemperature = CurrentDetail.temperature.toIntOrNull() ?: return

                // Máy bơm
                if (timerItems[i].isPump())
                    timerItems[i].isState =
                            (currHumidity < Auto.humidityStartValue ||
                                    currTemperature > Auto.tempEndValue) &&
                                    timerItems[i].isPower

                // Đèn
                else if (timerItems[i].isLight())
                    timerItems[i].isState =
                            (currTemperature < Auto.tempStartValue ||
                                    currHumidity > Auto.humidityEndValue) &&
                                    timerItems[i].isPower

                // Âm thanh
                else
                    timerItems[i].isState = timerItems[i].isPower
            } else {
                // Nếu chế độ tự động tắt, tiến hành tắt các timer tự động
                if (timerItems[i].isDefault()) {
                    println("${timerItems[i].id} là mặc định")
                    timerItems[i].isState = false
                    timerItems[i].isPower = false
                } else {
                    // Sau đó kiểm tra khung giờ bình thường
                    timerItems[i].isState =
                            TimeUtils.isInTimer(timerItems[i].start, timerItems[i].end) &&
                                    timerItems[i].isPower
                }
            }

            ArduinoInit.sendCommand(timerItems[i].port, timerItems[i].isState)
            FirebaseConfig.updateData(timerItems[i]) // -> Tự động sẽ được cập nhật
            if (previous.isDiff(timerItems[i])) {
                Log.d(TAG, "checkStateUpdateAndSendData: Đã cập nhập")
            }
            // Tiến hành gửi thông tin xuống arduino
        }
    }

}
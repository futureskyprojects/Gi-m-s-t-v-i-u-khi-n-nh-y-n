package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import es.dmoral.toasty.Toasty;
import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.data.modal.Auto;
import vn.vistark.giam_sat_nha_yen.ui.BaseAppCompatActivity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_dialog.TimerDialog;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.video_transfer.VideoTransfer;
import vn.vistark.giam_sat_nha_yen.utils.ScreenUtils;

public class DashboardScreenActivity extends AppCompatActivity implements BaseAppCompatActivity, CameraBridgeViewBase.CvCameraViewListener2 {
    private final static String TAG = DashboardScreenActivity.class.getSimpleName();

    // Danh sách các View sẽ được ánh xạ
    private ImageView ivAppPowerOffButton;
    private RecyclerView rvTimerList;
    private Button btnAddNewTimer;
    private TextView tvCurrentTime;
    private TextView tvEstablishTime;
    private TextView tvHumidity;
    private TextView tvTemperature;
    private TextView tvTimerListEmptyNotify;
    private TextView tvAutoTemp;
    private TextView tvAutoHumidity;
    private SwitchCompat scTimerAutoPower;

    private LinearLayout lnMultiCameraButton;
    private LinearLayout lnMultiCamLayout;
    private ImageView ivImageCapturedS1;
    private ImageView ivImageCapturedS2;
    private ImageView ivImageCapturedS3;
    private ImageView ivImageCapturedS4;

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;
    private ImageView ivImageCaptured;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;


    private DashboardScreenPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);
        ScreenUtils.hideTitleBarAndTransparentStatusBar(this);
        initViews();
        initPresenters();
        initEvents();
        initAutoSync();
        loadTimerList();
        initCamera();
        ArduinoCommunity.asyncCommunity(this);
    }

    private void initAutoSync() {
        new Auto().sync(FirebaseConfig.myRef, this);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                mOpenCvCameraView.enableView();
                showMultiCamera();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private void initCamera() {
        this.mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.mOpenCvCameraView.setCvCameraViewListener(this);
    }

    private void loadTimerList() {
        mPresenter.loadTimerList(rvTimerList);
    }

    @Override
    public void initViews() {
        ivAppPowerOffButton = findViewById(R.id.appPowerOff);
        rvTimerList = findViewById(R.id.timerList);
        btnAddNewTimer = findViewById(R.id.addNewTimerBtn);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.imageCaptureFrame);
        ivImageCaptured = findViewById(R.id.imageCaptured);
        tvCurrentTime = findViewById(R.id.currentTime);
        tvEstablishTime = findViewById(R.id.establishTime);
        tvHumidity = findViewById(R.id.humidity);
        tvTemperature = findViewById(R.id.temperature);
        tvTimerListEmptyNotify = findViewById(R.id.timerListEmptyNotify);
        lnMultiCameraButton = findViewById(R.id.multiCameraButton);
        lnMultiCamLayout = findViewById(R.id.multiCamLayout);
        ivImageCapturedS1 = findViewById(R.id.imageCapturedS1);
        ivImageCapturedS2 = findViewById(R.id.imageCapturedS2);
        ivImageCapturedS3 = findViewById(R.id.imageCapturedS3);
        ivImageCapturedS4 = findViewById(R.id.imageCapturedS4);
        tvAutoTemp = findViewById(R.id.autoTemp);
        tvAutoHumidity = findViewById(R.id.autoHumidity);
        scTimerAutoPower = findViewById(R.id.timerAutoPower);
    }

    public void showMultiCamera() {
        lnMultiCameraButton.post(new Runnable() {
            @Override
            public void run() {
                lnMultiCameraButton.setVisibility(View.GONE);
                ivImageCaptured.setVisibility(View.GONE);
                lnMultiCamLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void chooseShowCam1() {
        ivImageCapturedS1.post(new Runnable() {
            @Override
            public void run() {
                lnMultiCameraButton.setVisibility(View.VISIBLE);
                ivImageCaptured.setVisibility(View.VISIBLE);
                lnMultiCamLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void initEvents() {
        // Bắt sự kiện khi nhấn vào nút đóng
        ivAppPowerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivAppPowerOffButton.post(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.powerOffConfirm();
                    }
                });
            }
        });

        // Bắt sự kiện khi nhấn vào nút thêm mới hẹn giờ
        btnAddNewTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimerDialog.showAdd(DashboardScreenActivity.this);
            }
        });

        // Bắt sự kiện khi nhấn vào nút nhiều camera
        lnMultiCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMultiCamera();
            }
        });

        // Sự kiện khi nhấn vào 1 khung hình của cam bất kỳ trong chế độ xem nhiều camera
        // Nhấn màn hình 1
        ivImageCapturedS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseShowCam1();
            }
        });
        // Nhấn màn hình 2,3,4
        ImageView[] ivs = {ivImageCapturedS2, ivImageCapturedS3, ivImageCapturedS4};
        for (final ImageView v : ivs) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.info(DashboardScreenActivity.this, "Không nhận được tín hiệu từ camera này!", Toasty.LENGTH_SHORT, false).show();
                        }
                    });
                }
            });
        }

        // Sự kiện cho nút nguồn auto
        scTimerAutoPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mPresenter.changeListByStateOfAutoPower(b);
            }
        });
    }


    @Override
    public void initPresenters() {
        mPresenter = new DashboardScreenPresenter(this);
    }

    @Override
    public void onBackPressed() {
        mPresenter.powerOffConfirm();
    }

    public void updateAutoHumidity(final String start, final String end) {
        tvAutoHumidity.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tvAutoHumidity.setText(start + " - " + end + "%");
            }
        });
    }

    public void updateAutoTemp(final String start, final String end) {
        tvAutoTemp.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tvAutoTemp.setText(start + " - " + end);
            }
        });
    }

    public boolean getStateAutoPower() {
        return scTimerAutoPower.isChecked();
    }

    public void updateTimerAutoPower(final Boolean state) {
        scTimerAutoPower.post(new Runnable() {
            @Override
            public void run() {
                scTimerAutoPower.setChecked(state);
            }
        });
    }

    public void updateHumidityView(final String humidity) {
        tvHumidity.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tvHumidity.setText(humidity + "%");
            }
        });
    }

    public void updateTemperature(final String temperature) {
        tvTemperature.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tvTemperature.setText(temperature + "°C");
            }
        });
    }

    public void updateCurrentTimeView(final String currentTime) {
        tvCurrentTime.post(new Runnable() {
            @Override
            public void run() {
                tvCurrentTime.setText(currentTime);
            }
        });
    }

    public void updateEstablishTimeView(final String establedTime) {
        tvEstablishTime.post(new Runnable() {
            @Override
            public void run() {
                tvEstablishTime.setText(establedTime);
            }
        });
    }

    public void showTimerListEmptyNotify() {
        tvTimerListEmptyNotify.setVisibility(View.VISIBLE);
    }

    public void hideTimerListEmptyNotify() {
        tvTimerListEmptyNotify.setVisibility(View.GONE);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: Đã dừng lại");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        Log.e(TAG, "onDestroy: Đã kết thúc.");
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        try {
            mRgba = inputFrame.rgba();
            final Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mRgba, bmp);
            if (ivImageCaptured.getVisibility() == View.VISIBLE) {
                // Nếu đang hiển thị khung nhìn cho 1 cam duy nhất
                ivImageCaptured.post(new Runnable() {
                    @Override
                    public void run() {
                        ivImageCaptured.setImageBitmap(bmp);
                    }
                });
            } else if (ivImageCapturedS1.getVisibility() == View.VISIBLE) {
                // Nếu có cho phép hiển thị cho cam 1 trong 4 cam
                ivImageCapturedS1.post(new Runnable() {
                    @Override
                    public void run() {
                        ivImageCapturedS1.setImageBitmap(bmp);
                    }
                });
            }
            VideoTransfer.send(bmp);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return mRgba; // This function must return
    }


}

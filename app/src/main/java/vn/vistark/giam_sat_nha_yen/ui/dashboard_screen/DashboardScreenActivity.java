package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.ui.BaseAppCompatActivity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_dialog.TimerDialog;
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
        loadTimerList();
        initCamera();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
                ivImageCaptured.setVisibility(View.VISIBLE);
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
    }


    @Override
    public void initPresenters() {
        mPresenter = new DashboardScreenPresenter(this);
    }

    @Override
    public void onBackPressed() {
        mPresenter.powerOffConfirm();
    }


    public void updateHumidityView(final String humidity) {
        tvHumidity.post(new Runnable() {
            @Override
            public void run() {
                tvHumidity.setText(humidity);
            }
        });
    }

    public void updateTemperature(final String temperature) {
        tvTemperature.post(new Runnable() {
            @Override
            public void run() {
                tvTemperature.setText(temperature);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
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
        mRgba = inputFrame.rgba();
//        Core.flip(mRgba, mRgba, -1);

        Bitmap bmp = null;
//        Mat tmp = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC4, new Scalar(4));
        try {
//            Imgproc.cvtColor(mRgba, tmp, Imgproc.COLOR_RGB2BGRA);
//            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mRgba, bmp);
            final Bitmap finalBmp = bmp;
            ivImageCaptured.post(new Runnable() {
                @Override
                public void run() {
                    ivImageCaptured.setImageBitmap(finalBmp);
                }
            });
        } catch (CvException e) {
            Log.d("Exception", e.getMessage());
        }
        return mRgba; // This function must return
    }
}

package demo.speech.shenma.autocollect;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.mysql.jdbc.Connection;
//import com.mysql.jdbc.ResultSet;
//import com.mysql.jdbc.Statement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "chenwei.MainActivity";

    /**
     * 资产编号
     */
    private String asset_num="auto_pub0013";

//    20:f4:1b:48:91:a2   备份
    //9英寸
    //1024 X 600

    private int MSG_COLLECT_OK = 11;
    private int MSG_COLLECT_CANCEL = 12;

    private int MSG_COLLECT_UN_EXIST = 13;

    private Handler mHandler = new Handler(){

        public void handleMessage(Message msg) {
//            super.handleMessage(msg);


            if(msg.what == MSG_COLLECT_OK){
                Toast.makeText(getApplication(),"收集成功",Toast.LENGTH_SHORT).show();
            } else if(msg.what == MSG_COLLECT_CANCEL){
                Toast.makeText(getApplication(),"采集失败  ，  "+msg.obj,Toast.LENGTH_SHORT).show();
            } else if(msg.what == MSG_COLLECT_UN_EXIST){
                Toast.makeText(getApplication(),"车机管理平台没有该资产编号",Toast.LENGTH_SHORT).show();
            }
        }
    };


    private RelativeLayout rlRoot;
    private TextView tvVendor;
    private String mVendor;

    private EditText mEditAssetNum;

    private String gpu_renderer;
    private String gpu_vendor;
    private String gpu_version;

    private GLSurfaceView mGlSurfaceView;
    private GLSurfaceView.Renderer mGlRenderer = new GLSurfaceView.Renderer() {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {// TODO Auto-generated method stub
//            Log.d(TAG, "gl renderer: "+gl.glGetString(GL10.GL_RENDERER));
//            Log.d(TAG, "gl vendor: "+gl.glGetString(GL10.GL_VENDOR));
//            Log.d(TAG, "gl version: "+gl.glGetString(GL10.GL_VERSION));
//            Log.d(TAG, "gl extensions: "+gl.glGetString(GL10.GL_EXTENSIONS));

            gpu_renderer=gl.glGetString(GL10.GL_RENDERER);
            gpu_vendor=gl.glGetString(GL10.GL_VENDOR);
            gpu_version=gl.glGetString(GL10.GL_VERSION);

            mVendor = gl.glGetString(GL10.GL_VENDOR);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tvVendor.setText(mVendor);
                    rlRoot.removeView(mGlSurfaceView);

                }
            });}

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            // TODO Auto-generated method stub

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rlRoot = (RelativeLayout)findViewById(R.id.rlRoot);
        tvVendor = (TextView)findViewById(R.id.tvVendor);

        mEditAssetNum = (EditText) findViewById(R.id.edit_asset_num);

//        mGlSurfaceView = new GLSurfaceView(this);
//        mGlSurfaceView.setRenderer(mGlRenderer);

//        rlRoot.addView(mGlSurfaceView);
//


//        CollectLogic.getInstance();
    }

    public void test(View v){

        asset_num = mEditAssetNum.getText().toString();

        if(TextUtils.isEmpty(asset_num)){

            Toast.makeText(this,"请填写资产编号",Toast.LENGTH_SHORT).show();
            return ;
        }

//        CollectLogic.getInstance().isExistDevice(asset_num);

        if(isNetworkConnected(this)){
            autoCollect(asset_num);
        } else {
            Toast.makeText(this,"请连接网络",Toast.LENGTH_SHORT).show();
        }
    }


    public void autoCollect(final String asset_num){

        final String mac_address = getMac();

        final String ScreenSize = getScreenSizeOfDevice2()+"";

        final String Memary = getTotalMemory();
//        final String memory2 = getDeviceMemory();

//        Log.i(TAG,"memory1 = "+memory1+" , memory2="+memory2);

        String totalSDMemory = String.valueOf(getSDCardMemory()[0] / (1024.0 * 1024.0 * 1024.0));
//        String totalSDMemory = String.valueOf(getSDCardMemory()[0] / (1000.0 * 1000.0 * 1000.0));
        final String Size = totalSDMemory.substring(0, totalSDMemory.indexOf(".") + 3)+" G";

        String cpu_abi = Build.CPU_ABI;

        String freq = getMinCpuFreq() + "-"+ getMaxCpuFreq() +" MHZ ";

        final String CPU = cpu_abi+" "+freq;

        final String Density = getResources().getDisplayMetrics().densityDpi+" DPI";

        final String GPU = gpu_renderer+","+gpu_vendor+","+gpu_version;
        final String System = Build.VERSION.RELEASE;


        new Thread(new Runnable() {
            @Override
            public void run() {
                if(true){
                    Connection conn;
                    Statement stmt;

                    String sql = "select count(*) from devices where AssetNumber = '"+asset_num+"'";

//                    String sql = "select count(*) from user where AssetNumber＝auto_pub0013";

                    Log.i(TAG,"isExistDevice()  sql = "+sql);
                    try {

                        Class.forName("com.mysql.jdbc.Driver");
                        String url = "jdbc:mysql://30.85.194.129:3307/devicemanger";
                        conn = (Connection) DriverManager.getConnection(url, "root", "mysql123");
                        stmt = conn.createStatement();


                        ResultSet rs = (ResultSet) stmt.executeQuery(sql);

//                        Log.i(TAG,"isExistDevice()   before  rs.getRow() = "+rs.getRow());

                        int size = 0;

                        if(rs.next()){
                            Log.i(TAG,"isExistDevice()  rs.getRow() = "+rs.getRow());

                            Log.i(TAG,"id = "+rs.getString(1));

                            size  = rs.getInt(1);
                        }

                        if(size>0){

                            // 服务器存在该资产编号


//                            a.	屏幕尺寸   ScreenSize  [8英寸]
//                            b.	分辨率    DPI   ［1024*600］
//                            c.	内存   Memary ［1G］
//                            d.	容量（物理内存） Size ［16G］
//                            e.	处理器（记录：CPU芯片商、型号、大小）  CPU  ［］
//                            f.	密度（dpi）     ［Density］  [160dpi]
//                            g.	MAC 地址    [MACAddress]  [00:17:53:7e:7d:7b]
//                            h.	图形处理（记录：显卡、芯片类型）  [GPU]  []
//                            i.	系统版本  System   []

//                            String update_sql = "select count(*) from devices where AssetNumber = '"+asset_num+"'";

                            String update_sql = "update devices set" +

                                    " ScreenSize = '"+ScreenSize+"' ," +
                                    " Memary = '"+Memary+"' ," +
                                    " Size = '"+Size+"' ," +
                                    " CPU = '"+CPU+"' ," +
                                    " Density = '"+Density+"' ," +
                                    " MACAddress = '"+mac_address+"', " +
                                    " GPU = '"+GPU+"' ," +
                                    " System = '"+System+"' " +
                                    "where AssetNumber = '"+asset_num+"'";

                            Log.i(TAG,"update_sql = "+update_sql);
//                            stmt.executeQuery(update_sql);
                            stmt.executeUpdate(update_sql);
//                            UPDATE authors
//                            SET state = 'PC', city = 'Bay City'
//                            WHERE state = 'CA' AND city = 'Oakland'
//                            mHandler.obtainMessage(MSG_COLLECT_OK);
                            mHandler.sendEmptyMessage(MSG_COLLECT_OK);

                        } else {
                            mHandler.sendEmptyMessage(MSG_COLLECT_UN_EXIST);
                        }
                        rs.close();
                        stmt.close();
                        conn.close();

                    } catch (SQLException e) {
                        e.printStackTrace();

                        mHandler.obtainMessage(MSG_COLLECT_CANCEL,e.toString()).sendToTarget();


                        Log.e(TAG,e.toString());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG,e.toString());

                        mHandler.obtainMessage(MSG_COLLECT_CANCEL,e.toString()).sendToTarget();
                    }
                }
            }
        }).start();
    }


    public static String getMac() {
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                macSerial += line.trim();
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macSerial;
    }

    private String getScreenSizeOfDevice2() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        double x = Math.pow(point.x / dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        String inches = String.valueOf(screenInches);
        return inches.substring(0,inches.indexOf(".")+3);
    }


    /** 获取android总内存大小 */
    private String getTotalMemory() {
        ActivityManager am = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        // 将获取的内存大小规格化
        return Formatter.formatFileSize(getBaseContext(), mi.totalMem);
    }

    /**
     * 获取手机内存大小（物理内存）
     *
     * @return
     */
    private String getDeviceMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString = null;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
//        return String.valueOf(Integer.valueOf(arrayOfString[1]).intValue() / 1024) + " M ";
    }


    public long[] getSDCardMemory() {
        long[] sdCardInfo=new long[2];
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSize();
            long bCount = sf.getBlockCount();
            long availBlocks = sf.getAvailableBlocks();

            sdCardInfo[0] = bSize * bCount;//总大小
            sdCardInfo[1] = bSize * availBlocks;//可用大小
        }
        return sdCardInfo;
    }

    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        int maxFreq = Integer.parseInt(result.trim())/(1024);
        return String.valueOf(maxFreq);
    }

    // 获取CPU最小频率（单位KHZ）
    public static String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        int minFreq = Integer.parseInt(result.trim())/(1024);
        return String.valueOf(minFreq);
    }


    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        CollectLogic.getInstance().destroy();
    }
}

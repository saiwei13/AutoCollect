package demo.speech.shenma.autocollect;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by saiwei on 10/25/16.
 */
public class CollectLogic {

    private final String TAG = "chenwei.CollectLogic";

    private static CollectLogic instance;

    public static CollectLogic  getInstance(){
        if(instance == null){
            instance = new CollectLogic();
        }

        return instance;
    }

    private CollectLogic(){

//        init();
    }

    Connection conn;
    Statement stmt;

    /**
     * 初始化
     */
    private void init(){


        new Thread(new Runnable() {
            @Override
            public void run() {
                //注册驱动
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    String url = "jdbc:mysql://30.85.194.129:3307/devicemanger";
                    conn = (Connection) DriverManager.getConnection(url, "root", "mysql123");
                    stmt = conn.createStatement();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.toString());

                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.toString());
                }
            }
        }).start();
    }


    public void isExistDevice(final String asset_num){

        Log.i(TAG,"isExistDevice()  (conn!=null)="+(conn!=null));

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(true){

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








                        }


                        rs.close();
                        stmt.close();
                        conn.close();

                    } catch (SQLException e) {
                        e.printStackTrace();

                        Log.e(TAG,e.toString());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG,e.toString());
                    }

                }
            }
        }).start();
    }












    public void destroy(){
//        rs.close();
        if(stmt !=null){
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn != null){
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

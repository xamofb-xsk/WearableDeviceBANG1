package co.example.administrator.wearabledevicebang.tools;

import android.util.Log;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * author：LSen 
 * created at 2017/4/24　13:36
 */

public class HttpSend {

    public static Object SendObject(String urlStr, Object object) {
        URL url;
        Object result =null;//要返回的结果
        try {
            url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);//设置连接超时时间，单位ms
            httpURLConnection.setReadTimeout(10000);//设置读取超时时间，单位ms
            //设置是否向httpURLConnection输出，因为post请求参数要放在http正文内，所以要设置为true
            httpURLConnection.setDoOutput(true);
            //设置是否从httpURLConnection读入，默认是false
            httpURLConnection.setDoInput(true);
            //POST请求不能用缓存，设置为false
            httpURLConnection.setUseCaches(false);
            //传送的内容是可序列化的
            //如果不设置此项，传送序列化对象时，当WEB服务默认的不是这种类型时，会抛出java.io.EOFException错误
            httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            //设置请求方法是POST
            httpURLConnection.setRequestMethod("GET");
            //连接服务器
            httpURLConnection.connect();
            //getOutputStream会隐含调用connect()，所以不用写上述的httpURLConnection.connect()也行。
            //得到httpURLConnection的输出流
            OutputStream os = httpURLConnection.getOutputStream();
            //构建输出流对象，以实现输出序列化的对象
            ObjectOutputStream objOut = new ObjectOutputStream(os);
            //向对象输出流写出数据，这些数据将存到内存缓冲区中
            objOut.writeObject(object);
            //刷新对象输出流，将字节全部写入输出流中
            objOut.flush();
            //关闭流对象
            objOut.close();
            os.close();
            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                //得到httpURLConnection的输入流，这里面包含服务器返回来的java对象
                InputStream in = httpURLConnection.getInputStream();
                //构建对象输入流，使用readObject()方法取出输入流中的java对象
                ObjectInputStream inObj = new ObjectInputStream(in);
                object =  inObj.readObject();
                //取出对象里面的数据
                result = object;
                //输出日志，在控制台可以看到接收到的数据
                Log.w("HTTP", result + "  :by post");
                //关闭创建的流
                in.close();
                inObj.close();
            } else {
                result = new Object();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}

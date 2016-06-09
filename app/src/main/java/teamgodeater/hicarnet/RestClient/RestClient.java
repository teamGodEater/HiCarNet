package teamgodeater.hicarnet.RestClient;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teamgodeater.hicarnet.Help.RestClientHelp;

import static teamgodeater.hicarnet.Help.RestClientHelp.Session;

public class RestClient implements Runnable {

    public static final int DID_SUCCEED = 1;

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";

    private String url;
    private String method;
    private OnServiceResultListener listener;
    public boolean isFirst = true;

    private Map<String, String> urlParams = null;
    private Map<String, String> headerParams = null;

    // public RestClient() {
    // this(new Handler());
    // }

    public void create(String method, String url, OnServiceResultListener listener) {
        this.method = method;
        this.url = url;
        this.listener = listener;
        ConnectionManager.getInstance().push(this);
    }

    public RestClient addUrlParams(String key, String value) {
        if (urlParams == null) {
            urlParams = new HashMap<>();
        }
        if (value != null && !value.equals("") && !value.equals("0"))
            urlParams.put(key, value);
        return this;
    }

    public RestClient addHeaderParams(String key, String value) {
        if (headerParams == null) {
            headerParams = new HashMap<>();
        }
        if (value != null && !value.equals("") && !value.equals("0"))
            headerParams.put(key, value);
        return this;
    }


    public void get(String u, OnServiceResultListener l) {
        create(GET, u, l);
    }

    public void put(String u, OnServiceResultListener l) {
        create(PUT, u, l);
    }

    public void post(String u, OnServiceResultListener l) {
        create(POST, u, l);
    }

    public void delete(String u, OnServiceResultListener l) {
        create(DELETE, u, l);
    }

    public interface OnServiceResultListener {
        void resultListener(byte[] result, int code, Map<String, List<String>> header);
    }

    @Override
    public void run() {
        HttpURLConnection httpClient = null;
        InputStream result = null;
        Map<String, List<String>> headerFields = null;
        int responseCode = -1;
        try {
            httpClient = getHttpClient();
            responseCode = httpClient.getResponseCode();
            headerFields = httpClient.getHeaderFields();
            if (responseCode != 200) {
                result = httpClient.getErrorStream();
            } else {
                result = httpClient.getInputStream();
            }
        } catch (Exception e) {
            this.sendMessage(null, -1, headerFields);
            if (httpClient != null)
                httpClient.disconnect();
            return;
        }


        final int BUFFER_SIZE = 1024;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count;
        if (result != null) {
            try {
                while ((count = result.read(data, 0, BUFFER_SIZE)) != -1)
                    outStream.write(data, 0, count);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sendMessage(outStream.toByteArray(), responseCode, headerFields);
        ConnectionManager.getInstance().didComplete(this);

        try {
            outStream.close();
            if (result != null)
                result.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (httpClient != null)
            httpClient.disconnect();
    }

    private static final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == DID_SUCCEED) {
                HandleData handleData = (HandleData) message.obj;
                if (handleData != null) {
                    handleData.listener.resultListener(
                            handleData.result
                            , handleData.code
                            , handleData.header);
                }
            }
        }
    };

    private class HandleData {
        public OnServiceResultListener listener;
        public Map<String, List<String>> header;
        public int code;
        public byte[] result;
    }

    private void sendMessage(byte[] result, int code, Map<String, List<String>> header) {
        HandleData handleData = new HandleData();
        handleData.code = code;
        handleData.result = result;
        handleData.listener = listener;
        handleData.header = header;

        Message message = Message.obtain(handler, DID_SUCCEED, handleData);
        handler.sendMessage(message);
    }

    private HttpURLConnection getHttpClient() throws IOException {
        HttpURLConnection httpURLConnection = null;
        //除去结尾的/
        if (url.lastIndexOf("/") == url.length() - 1) {
            url = url.substring(0, url.lastIndexOf("/"));
        }
        //设置url参数
        if (urlParams != null && urlParams.size() > 0) {
            int urlParamsSize = urlParams.size();
            StringBuilder params = new StringBuilder();
            params.append("?");
            int count = 0;
            for (Map.Entry<String, String> entry : urlParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                params.append(key);
                params.append("=");
                params.append(value);
                if (count < urlParamsSize - 1) {
                    params.append("&");
                }
                count++;
            }
            url += params.toString();
        }

        httpURLConnection = (HttpURLConnection) new URL(this.url).openConnection();
        //设置header参数
        if (headerParams != null && headerParams.size() > 0) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setReadTimeout(10000);


        return httpURLConnection;
    }

    public static String byteToString(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static <T> void loginOperation(final String method, final String sendUrl, final RestClient rest, final OnResultListener<T> listener) {
        if (Session != null && !Session.equals(""))
            rest.addHeaderParams("Session", Session);

        rest.create(method, sendUrl, new OnServiceResultListener() {
            @Override
            public void resultListener(byte[] result, int code, Map<String, List<String>> header) {
                if (listener == null) {
                    return;
                }

                if (code == 200) {
                    Type type = getInterfacetype(listener.getClass());
                    if (type != null) {

                        if (type.equals(String.class)) {
                            listener.succeed((T) byteToString(result));
                            return;
                        }

                        T userInfoData;
                        try {
                            userInfoData = (T) new Gson().fromJson(byteToString(result), type);

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            listener.error(-2);
                            return;
                        }

                        listener.succeed(userInfoData);

                    } else {
                        //类型错误
                        listener.error(-2);
                    }
                } else {
                    if (code == 401
                            && rest.isFirst
                            && !RestClientHelp.username.equals("")
                            && !RestClientHelp.password.equals("")) {
                        rest.isFirst = false;
                        new RestClientHelp().login(new OnResultListener<String>() {
                            @Override
                            public void succeed(String bean) {
                                loginOperation(method, sendUrl, rest, listener);
                            }

                            @Override
                            public void error(int code) {
                                listener.error(401);
                            }
                        });
                        return;
                    }
                    listener.error(code);
                }
            }
        });
    }

    private static Type getInterfacetype(Class<?> x) {
        Type[] genericInterfaces = x.getGenericInterfaces();
        if (genericInterfaces != null && genericInterfaces.length > 0) {
            if (genericInterfaces[0] instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                    return actualTypeArguments[0];
                }
            }
        }
        return null;
    }


    public interface OnResultListener<T> {
        void succeed(T bean);

        void error(int code);
    }

}
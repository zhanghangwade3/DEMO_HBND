package com.computer.mazhihuapp.utils.net;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class HttpClientFactory {
    /** http����????��������??*/
    private static final int MAX_CONNECTIONS = 10;
    /** ��ʱʱ�� */
    private static final int TIMEOUT = 10 * 1000;
    /** �����С */
    private static final int SOCKET_BUFFER_SIZE = 8 * 1024; // 8KB

    public static DefaultHttpClient create(boolean isHttps) {
        HttpParams params = createHttpParams();
        DefaultHttpClient httpClient = null;
        if (isHttps) {
            // ֧��http��https
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));
            // ThreadSafeClientConnManager�̰߳�ȫ����??
            ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
                    params, schemeRegistry);
            httpClient = new DefaultHttpClient(cm, params);
        } else {
            httpClient = new DefaultHttpClient(params);
        }
        return httpClient;
    }

    private static HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();
        // �����Ƿ����þ����Ӽ�飬Ĭ���ǿ����ġ��ر����������????�������????��??�ܣ�����������I/O����ķ��գ�������˹ر�����ʱ��??
        // ????���ѡ������ÿ��ʹ���ϵ�����֮ǰ����????�����Ƿ���ã����??ʱ�����15-30ms֮��
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);// �������ӳ�ʱʱ��
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);// ����socket��ʱʱ��
        HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);// ���û����С
        HttpConnectionParams.setTcpNoDelay(params, true);// �Ƿ�ʹ���ӳٷ�??trueΪ���ӳ�)
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); // ����Э��汾
        HttpProtocolParams.setUseExpectContinue(params, true);// �����쳣�������
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);// ���ñ���
        HttpClientParams.setRedirecting(params, false);// �����Ƿ�����ض�??

        ConnManagerParams.setTimeout(params, TIMEOUT);// ���ó�ʱ
        ConnManagerParams.setMaxConnectionsPerRoute(params,
                new ConnPerRouteBean(MAX_CONNECTIONS));// ���߳����������
        ConnManagerParams.setMaxTotalConnections(params, 10); // ���߳�??����??
        return params;
    }

}

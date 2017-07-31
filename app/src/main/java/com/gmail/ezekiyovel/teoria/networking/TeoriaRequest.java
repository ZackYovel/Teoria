package com.gmail.ezekiyovel.teoria.networking;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.gmail.ezekiyovel.teoria.entity.QuestionItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class TeoriaRequest extends Request<List<QuestionItem>> {

    private final Response.Listener<List<QuestionItem>> listener;
    private final Context context;

    public TeoriaRequest(Context context, int method, String url,
                         Response.Listener<List<QuestionItem>> listener,
                         Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Response<List<QuestionItem>> parseNetworkResponse(NetworkResponse response) {
        Response<List<QuestionItem>> result;
        try {
            File theoryXmlFile = saveTheoryXmlFile(response.data);
            if (theoryXmlFile == null){
                throw new NullPointerException("TeoriaRequest couldn't get a temp file");
            }
            InputStream inputStream = new FileInputStream(theoryXmlFile);
            result = Response.success(new RssParser().parse(inputStream),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception ex) {
            result = Response.error(new VolleyError(ex));
        }
        return result;
    }

    private File saveTheoryXmlFile(byte[] response) {
        try {
            File file = getTempFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(response);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private File getTempFile() throws IOException {
        return File.createTempFile("TheoryExamHE.xml", null, context.getCacheDir());
    }

    @Override
    protected void deliverResponse(List<QuestionItem> response) {
        listener.onResponse(response);
    }
}

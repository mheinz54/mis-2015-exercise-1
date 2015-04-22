package mmbuw.com.brokenproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AnotherBrokenActivity extends Activity
{
    private Handler mHandler = new Handler();
    private TextView mTextView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_broken);

        mTextView = (TextView)findViewById(R.id.httpResponse);
        mContext = this;

        Intent intent = getIntent();
        String message = intent.getStringExtra(BrokenActivity.EXTRA_MESSAGE);
        new Fetch().execute(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.another_broken, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    private class Fetch extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... params)
        {
            String url = "";
            if(params.length > 0)
                url = params[0];

            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(new HttpGet(url));
                StatusLine status = response.getStatusLine();
                if (status.getStatusCode() == HttpStatus.SC_OK)
                {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    response.getEntity().writeTo(outStream);
                    final String responseAsString = outStream.toString();
                    System.out.println("Response string: " + responseAsString);

                    mHandler.post(new Runnable()
                    {
                        public void run()
                        {
                            mTextView.setText(responseAsString);
                        }
                    });
                }
                else
                {
                    response.getEntity().getContent().close();
                    throw new IOException(status.getReasonPhrase());
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                final String message = ex.getMessage();
                mHandler.post(new Runnable()
                {
                    public void run()
                    {
                        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
            return null;
        }
    }
}

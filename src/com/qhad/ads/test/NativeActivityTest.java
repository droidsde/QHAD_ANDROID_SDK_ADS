package com.qhad.ads.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qhad.ads.R;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAd;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdLoader;
import com.qhad.ads.sdk.core.BridgeMiddleware;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class NativeActivityTest extends Activity {

    private IQhNativeAdLoader ad;

    private AMyAdapter adapter;

    private IQhNativeAdLoader nativeAdLoader;

    private ArrayList<IQhNativeAd> allNativeAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        QhAdModel.getInstance().setUserLandingPage(new CustomLandingPageView());
        allNativeAds = NativeTest.allNativeAds;

        LinearLayout rl = new LinearLayout(this);
        rl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        rl.setOrientation(LinearLayout.VERTICAL);

        setContentView(rl);

        setTitle("Another Activity");

        CheckBox dialogcb = new CheckBox(this);
        dialogcb.setText("AlertDialog in this page when checked");
        dialogcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AMyAdapter.isthispage = true;
                } else {
                    AMyAdapter.isthispage = false;
                }
            }
        });

        Button btn = new Button(this);
        btn.setText("返回上一页");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        LinearLayout.LayoutParams btnlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rl.addView(dialogcb, btnlp);
        rl.addView(btn, btnlp);

        final RelativeLayout adContainer = new RelativeLayout(this);
        RelativeLayout.LayoutParams adlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl.addView(adContainer, adlp);

        ListView listView = new ListView(this);
        adapter = new AMyAdapter(this.getApplicationContext(),
                this);
        listView.setAdapter(adapter);

        adContainer.addView(listView);

        NativeTest.nativeAdLoader.loadAds(5);

        FeedsLoader.getInstance().load(new FeedsLoaderCallback() {

            @Override
            public void onLoaded(final ArrayList<MyListItem> items) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        int index = 0;
                        for (MyListItem item : items) {
                            adapter.addItem(item);
                            index++;

                            if (index % 3 == 0 && allNativeAds.size() > 0) {
                                IQhNativeAd nativeAd = allNativeAds.remove(0);
                                JSONObject adjson = nativeAd.getContent();

                                try {

                                    MyListItem aditem = new MyListItem();
                                    aditem.title = adjson.getString("title");
                                    aditem.desc = adjson.getString("desc");
                                    aditem.logo = adjson.getString("logo");
                                    aditem.adimg = adjson.getString("contentimg");
                                    aditem.isAd = true;
                                    aditem.ad = nativeAd;
                                    adapter.addItem(aditem);

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void whenLoaded(ArrayList<MListItem> items) {

            }

            @Override
            public void duringLoaded(ArrayList<AMyListItem> items) {

            }
        });

    }

    private void jump() {
//		Intent intent = new Intent(this, BannerTest.class);
//		startActivity(intent);
//		finish();
        nativeAdLoader.loadAds();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BridgeMiddleware.activityDestroy(this);
    }

}

class AMyListItem {
    public String logo;
    public String title;
    public String desc;
    public String link;
    public String adimg;
    public boolean isAd = false;
    public IQhNativeAd ad = null;
}

class AMyAdapter extends BaseAdapter {
    public static boolean isthispage;
    private ArrayList<MyListItem> list = new ArrayList<MyListItem>();
    private Context context;
    private Activity activity;

    public AMyAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.isthispage = false;
    }

    public void addItem(MyListItem item) {
        list.add(item);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.feedsitem, null);

            holder.nameTxt = (TextView) convertView.findViewById(R.id.name);
            holder.contentTxt = (TextView) convertView.findViewById(R.id.feedscontent);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.adimg = (ImageView) convertView.findViewById(R.id.adimg);

            convertView.setTag(holder);
        }

        // TODO Auto-generated method stub
        final MyListItem item = (MyListItem) this.getItem(position);


        holder.nameTxt.setText(item.title);

        holder.contentTxt.setText(item.desc);

        holder.imageView.setTag(item.logo);
        new ADownloadImageTask(holder.imageView).execute(item.logo);

        if (item.isAd) {
            holder.adimg.setVisibility(View.VISIBLE);
            holder.adimg.setTag(item.adimg);
            new ADownloadImageTask(holder.adimg).execute(item.adimg);
            item.ad.onAdShowed();
        } else {
            holder.adimg.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (item.isAd) {
                    if (isthispage) {
                        item.ad.onAdClicked(activity);
                    } else {
                        item.ad.onAdClicked();
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(item.link);
                    intent.setData(content_url);
                    activity.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public TextView nameTxt;
        public TextView contentTxt;
        public ImageView imageView;
        public ImageView adimg;
    }

}


class ADownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    String url;

    public ADownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        url = urldisplay;
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (url == bmImage.getTag()) {
            bmImage.setImageBitmap(result);
        }

    }

}

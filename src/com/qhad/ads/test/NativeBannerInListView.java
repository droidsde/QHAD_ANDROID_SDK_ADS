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
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qhad.ads.R;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAd;
import com.qhad.ads.sdk.core.BridgeMiddleware;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.SwitchConfig;

import java.io.InputStream;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class NativeBannerInListView extends Activity {

    int i, y, z = 1;
    int height, width = 0;
    private LinearLayout.LayoutParams nativebanneradlp;
    private LinearLayout nativebannerAdContainer;
    private RelativeLayout rlContainer;
    private RelativeLayout lvContainer;
    private boolean isGot = false;
    private boolean isNotyfied = false;
    private EditText etAspW;
    private EditText etAspH;
    private Button btnSet;
    private Button btnLoad;
    private int containerWidth;
    private MAdapter adapter;

    private ListView.LayoutParams listviewlp;
    private RelativeLayout nbitem;
    private RelativeLayout nbItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SwitchConfig.LOG = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nativebanner_listview);

        setTitle("单品 in ListView");

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });

        nbitem = new RelativeLayout(this);
        nbItem = new RelativeLayout(this);

        btnSet = (Button) findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWH();
            }
        });

        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });

        lvContainer = (RelativeLayout) findViewById(R.id.lvContainer);
        final ListView listView = new ListView(this);
        RelativeLayout.LayoutParams lvlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        adapter = new MAdapter(this.getApplicationContext(), this);
        listView.setAdapter(adapter);
        lvContainer.addView(listView, lvlp);

//        nbitem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                TextView tvInfo = (TextView) findViewById(R.id.tvViewInfo);
//                tvInfo.setText(String.format("W: %s px,H: %s px", nbitem.getMeasuredWidth(), nbitem.getMeasuredHeight()));
//            }
//        });

        listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

//                QHADLog.e(Utils.getCurrentTime() + "      listView onGlobalLayout");
                if (i == 1) {
                    containerWidth = listView.getMeasuredWidth();

                    etAspW = (EditText) findViewById(R.id.etAspRateW);
                    etAspH = (EditText) findViewById(R.id.etAspRateH);
                    double dW = Double.parseDouble(etAspW.getText().toString());
                    double dH = Double.parseDouble(etAspH.getText().toString());
                    width = containerWidth;
                    height = (int) Math.floor(containerWidth * (dH / dW));

                    listviewlp = new ListView.LayoutParams(width, height);
                    nbitem.setLayoutParams(listviewlp);
                    nbItem.setLayoutParams(listviewlp);

                    getdata();
                }
                i++;
            }
        });

//        nbitem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                QHADLog.e(Utils.getCurrentTime() + "      nbitme onGlobalLayout");
//            }
//        });

        FeedsLoader.getInstance().load(new FeedsLoaderCallback() {

            @Override
            public void onLoaded(ArrayList<MyListItem> items) {

            }

            @Override
            public void whenLoaded(final ArrayList<MListItem> items) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        if (!isNotyfied) {
                            int index = 0;
                            for (MListItem item : items) {
                                item.nbitem = nbitem;
                                item.nbItem = nbItem;
                                adapter.addItem(item);
                                QHADLog.e("index:" + index);
                                index++;
                            }

                            adapter.notifyDataSetChanged();
                            isNotyfied = true;
                        }
                    }
                });

            }

            @Override
            public void duringLoaded(ArrayList<AMyListItem> items) {

            }
        });
    }

    private void getdata() {
        BridgeMiddleware.initSimpleNativeBanner(nbitem, this, "5uavuInDAl", false);
        BridgeMiddleware.initSimpleNativeBanner(nbItem, this, "5uavuInDAl", false);
    }

    private void setWH() {
        double dW = Double.parseDouble(etAspW.getText().toString());
        double dH = Double.parseDouble(etAspH.getText().toString());
        width = containerWidth;
        height = (int) Math.floor(containerWidth * (dH / dW));

        listviewlp = new ListView.LayoutParams(width, height);
        nbitem.setLayoutParams(listviewlp);
        nbItem.setLayoutParams(listviewlp);
    }

    private void jump() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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

class MListItem {
    public String logo;
    public String title;
    public String desc;
    public String link;
    public String adimg;
    public View nbitem;
    public View nbItem;
    public boolean isAd = false;
    public IQhNativeAd ad = null;
}

class MAdapter extends BaseAdapter {
    private View nbitem;
    private View nbItem;
    private ArrayList<MListItem> list = new ArrayList<MListItem>();
    private Context context;
    private Activity activity;

    public MAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void addItem(MListItem item) {
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

        MViewHolder holder = new MViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
//        if (convertView != null) {
//            holder = (MViewHolder) convertView.getTag();
//        } else {
        convertView = inflater.inflate(R.layout.feedsitem, null);

        holder.nameTxt = (TextView) convertView.findViewById(R.id.name);
        holder.contentTxt = (TextView) convertView.findViewById(R.id.feedscontent);
        holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
        holder.adimg = (ImageView) convertView.findViewById(R.id.adimg);

//            convertView.setTag(holder);
//        }

        // TODO Auto-generated method stub
        final MListItem item = (MListItem) this.getItem(position);

        QHADLog.e("position is:" + position);

        holder.nameTxt.setText(item.title);

        holder.contentTxt.setText(item.desc);

        holder.imageView.setTag(item.logo);

        new MDownloadImageTask(holder.imageView).execute(item.logo);

        if (item.isAd) {
            holder.adimg.setVisibility(View.VISIBLE);
            holder.adimg.setTag(item.adimg);
            new MDownloadImageTask(holder.adimg).execute(item.adimg);
            item.ad.onAdShowed();
        } else {
            holder.adimg.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (item.isAd) {
                    item.ad.onAdClicked();
                } else {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(item.link);
                    intent.setData(content_url);
                    activity.startActivity(intent);
                }
            }
        });

        if (position == 2) {
            nbitem = item.nbitem;
            return nbitem;
        }

        if (position == 4) {
            nbItem = item.nbItem;
            return nbItem;
        }

        return convertView;
    }

    public static class MViewHolder {
        public TextView nameTxt;
        public TextView contentTxt;
        public ImageView imageView;
        public ImageView adimg;
    }

}

class MDownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    String url;

    public MDownloadImageTask(ImageView bmImage) {
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

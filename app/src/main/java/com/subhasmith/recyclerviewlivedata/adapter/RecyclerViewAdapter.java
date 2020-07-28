package com.subhasmith.recyclerviewlivedata.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhasmith.recyclerviewlivedata.R;
import com.subhasmith.recyclerviewlivedata.model.Data;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity context;
    ArrayList<Data> testDataArrayList;

    public RecyclerViewAdapter(Activity context, ArrayList<Data> testDataArrayList) {
        this.context = context;
        this.testDataArrayList = testDataArrayList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new RecyclerViewViewHolder(rootView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int reqWidth = 100, reqHeight = 100;
        Data testData = testDataArrayList.get(position);
        RecyclerViewViewHolder viewHolder = (RecyclerViewViewHolder) holder;


        if (!testData.getName().isEmpty()){
            viewHolder.txtView_title.setText(testData.getName());
        }else{
            viewHolder.txtView_title.setText("Not Available");
        }
        if (!testData.getDescription().isEmpty()){
            viewHolder.txtView_description.setText(testData.getDescription());
        }else{
            viewHolder.txtView_description.setText("Not Available");
        }

        if (!testData.getVideo_duration().isEmpty()){
            viewHolder.txtView_duration.setText(testData.getVideo_duration());
        }else{
            viewHolder.txtView_duration.setText("Not Available");
        }

        if (!testData.getPayment_plan().isEmpty()){
            viewHolder.txtView_plan.setText(testData.getPayment_plan());
        }else{
            viewHolder.txtView_plan.setText("Not Available");
        }


        viewHolder.txtView_release_year.setText(testData.getRelease_year() + "");

        if (!testData.getType().isEmpty()){
            viewHolder.txtView_type.setText(testData.getType());
        }else{
            viewHolder.txtView_type.setText("Not Available");
        }

        if (!testData.getCreated_on().isEmpty()) {
            java.util.Date date = Date.from(Instant.parse(testData.getCreated_on()));
            String createdOn = new SimpleDateFormat("dd.MM.yyyy").format(date);
            viewHolder.txtView_created_on.setText(createdOn);


        }
        if (!testData.getUpdated_on().isEmpty()) {

            java.util.Date date = Date.from(Instant.parse(testData.getUpdated_on()));
            String updatedOn = new SimpleDateFormat("dd.MM.yyyy").format(date);
            viewHolder.txtView_updated_on.setText(updatedOn);


        }

        if (!testData.getShortDescription().isEmpty()){
            viewHolder.txtView_short_desc.setText(testData.getShortDescription());
        }else{
            viewHolder.txtView_short_desc.setText("Not Available");
        }

        File file = null;
        try {
            file = (getCacheFile(context, "TestImage.tif"));
        } catch (IOException e) {
            e.printStackTrace();
        }

//Read data about image to Options object
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        TiffBitmapFactory.decodeFile(file, options);

        int dirCount = options.outDirectoryCount;
        String encoded = null;
//Read and process all images in file
        for (int i = 0; i < dirCount; i++) {
            options.inDirectoryNumber = i;
            TiffBitmapFactory.decodeFile(file, options);
            int curDir = options.outCurDirectoryNumber;
            int width = options.outWidth;
            int height = options.outHeight;
            //Change sample size if width or height bigger than required width or height
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;

            // Specify the amount of memory available for the final bitmap and temporary storage.
            options.inAvailableMemory = 20000000; // bytes

            Bitmap bmp = TiffBitmapFactory.decodeFile(file, options);
            viewHolder.imgView_icon.setImageBitmap(bmp);
        }


    }

    public static File getCacheFile(Context context, String name) throws IOException {
        File cacheFile = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getAssets().open(name);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new IOException("Could not open tiff", e);
        }
        return cacheFile;
    }

    @Override
    public int getItemCount() {
        return testDataArrayList.size();
    }

    class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView_icon;
        TextView txtView_title;
        TextView txtView_description;
        TextView txtView_plan;
        TextView txtView_release_year;
        TextView txtView_duration;
        TextView txtView_type;
        TextView txtView_created_on;
        TextView txtView_updated_on;
        TextView txtView_short_desc;

        public RecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView_icon = itemView.findViewById(R.id.imgView_icon);
            txtView_title = itemView.findViewById(R.id.txtView_title);
            txtView_description = itemView.findViewById(R.id.txtView_description);
            txtView_plan = itemView.findViewById(R.id.plan);
            txtView_duration = itemView.findViewById(R.id.duration);
            txtView_release_year = itemView.findViewById(R.id.year);
            txtView_type = itemView.findViewById(R.id.type);
            txtView_created_on = itemView.findViewById(R.id.created_on);
            txtView_updated_on = itemView.findViewById(R.id.updated_on);
            txtView_short_desc = itemView.findViewById(R.id.short_desc);


        }
    }
}

package com.growthskyinfotech.vidostatus.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.downloader.PRDownloader;
import com.growthskyinfotech.vidostatus.MyUtils;
import com.growthskyinfotech.vidostatus.R;
import com.growthskyinfotech.vidostatus.databinding.InboxItemBinding;
import com.growthskyinfotech.vidostatus.favorite.FavoriteDatabase;
import com.growthskyinfotech.vidostatus.model.VideoModel;

import java.io.File;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.MyViewHolder> {

    private final FavoriteDatabase database;
    Activity activity;
    List<VideoModel> videoModelList;
    OnItemClickListener onItemClickListener;

    public DownloadAdapter(Activity activity, List<VideoModel> videoModelList) {
        this.activity = activity;
        this.videoModelList = videoModelList;
        database = Room.databaseBuilder(activity, FavoriteDatabase.class, activity.getString(R.string.app_name)).allowMainThreadQueries().fallbackToDestructiveMigration().build();

    }

    public void onItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DownloadAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        InboxItemBinding downloadBinding = InboxItemBinding.inflate(LayoutInflater.from(activity), parent, false);

        return new MyViewHolder(downloadBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadAdapter.MyViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        VideoModel videoModel = videoModelList.get(position);

        holder.binding.fileNameDownloadItem.setText(videoModel.getVideo_title());
        holder.binding.soFarDownloadItem.setText(videoModel.getVideo_id());
        holder.binding.progressDownloadItem.setProgress(videoModel.getProgress());
        holder.binding.speedDownloadItem.setText(videoModel.getProgress() + "/" + "100");

        if (videoModel.getProgress() == 100) {

            holder.binding.progressDownloadItem.setVisibility(View.GONE);

        } else {

            holder.binding.progressDownloadItem.setVisibility(View.VISIBLE);
        }

        //Glide.with(activity).load(videoModel.getThumbnail()).into(holder.binding.imageViewDownloadItem);

      /*  holder.binding.playPause.setOnClickListener(view -> {

            Log.d("venky", "onBindViewHolder: play pause... ");
            PRDownloader.resume(videoModel.download_id);
            Toast.makeText(activity, "cancelled" + videoModel.download_id, Toast.LENGTH_SHORT).show();

        });
*/

        File file = new File(MyUtils.getStoreVideoExternalStorage(activity), videoModel.getVideo_title() + ".mp4");

        if (file.exists()) {

            holder.binding.progressDownloadItem.setProgress(100);
            holder.binding.progressDownloadItem.setVisibility(View.GONE);
            holder.binding.speedDownloadItem.setVisibility(View.GONE);
            holder.binding.speedDownloadItem.setText("100%");
            holder.binding.ivcancel.setImageResource(R.drawable.baseline_check_box_24);

            holder.binding.statusicon.setVisibility(View.VISIBLE);
        } else {


            holder.binding.statusicon.setVisibility(View.GONE);
            holder.binding.progressDownloadItem.setVisibility(View.VISIBLE);
            holder.binding.ivcancel.setImageResource(R.drawable.ic_download);
        }

        holder.binding.playPause.setOnClickListener(view -> {
            Log.d("venky", "onBindViewHolder: checks....");
            File file11 = new File(MyUtils.getStoreVideoExternalStorage(activity), videoModel.getVideo_title() + ".mp4");

            Log.d("venky", "onBindViewHolder: " + file11.exists());
            if (file11.exists()) {
                activity.startActivity(new Intent(activity, SaveVideoActivity.class).putExtra("finalVideo", file11.getAbsolutePath()));

            } else {
                Toast.makeText(activity, "file not downloaded. Downloading...", Toast.LENGTH_SHORT).show();
            }


        });


        holder.binding.ivcancel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("venky", "onBindViewHolder: play pause... ");
                PRDownloader.resume(videoModel.download_id);
                Toast.makeText(activity, "cancelled" + videoModel.download_id, Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        holder.binding.ivcancel.setOnClickListener(view -> {
            File file11 = new File(MyUtils.getStoreVideoExternalStorage(activity), videoModel.getVideo_title() + ".mp4");

            if (!file11.exists()) {

                holder.binding.progressDownloadItem.setVisibility(View.VISIBLE);
                onItemClickListener.onItemClick(position, videoModel, holder);

            } else {
                Toast.makeText(activity, "Already Downloaded", Toast.LENGTH_SHORT).show();
            }


        });

    }

    public void updateProgress(int position, int progress, int downloadId) {

        VideoModel videoModel = videoModelList.get(position);
        videoModel.setProgress(progress);

        videoModel.setDownload_id(downloadId);

        notifyItemChanged(position);
        database.favoriteDao().updateProgress(videoModel.getVideo_id(), progress);

    }

    @Override
    public int getItemCount() {
        return videoModelList.size();
    }


    public interface OnItemClickListener {

        void onItemClick(int position, VideoModel videoModel, MyViewHolder myViewHolder);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public InboxItemBinding binding;


        public MyViewHolder(@NonNull InboxItemBinding itemView) {
            super(itemView.getRoot());

            this.binding = itemView;
        }
    }
}

package com.theanhdev.rshare.adapters;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theanhdev.rshare.databinding.RvidContainerBinding;
import com.theanhdev.rshare.listeners.VideoListener;
import com.theanhdev.rshare.models.Video;

import java.util.List;

public class RvidAdapter extends RecyclerView.Adapter<RvidAdapter.RvidViewHolder> {

    private final List<Video> videoList;
    private final VideoListener videoListener;

    public RvidAdapter(List<Video> videoList, VideoListener videoListener) {
        this.videoList = videoList;
        this.videoListener = videoListener;
    }

    @NonNull
    @Override
    public RvidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvidContainerBinding rvidContainerBinding = RvidContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new RvidViewHolder(rvidContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RvidViewHolder holder, int position) {
        holder.setData(videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class RvidViewHolder extends RecyclerView.ViewHolder {

        RvidContainerBinding binding;
        RvidViewHolder(RvidContainerBinding rvidContainerBinding) {
            super(rvidContainerBinding.getRoot());
            binding = rvidContainerBinding;
        }

        void setData(Video video) {
            binding.videoPlayer.setVideoPath(video.UrlVideo);
            binding.videoPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                float screenRatio = binding.videoPlayer.getWidth() / (float) binding.videoPlayer.getHeight();

                float scale = videoRatio / screenRatio;
                if (scale >= 1f) {
                    binding.videoPlayer.setScaleX(scale);
                } else {
                    binding.videoPlayer.setScaleY(1f / scale);
                }
            });
            binding.videoPlayer.setOnCompletionListener(MediaPlayer::start);
        }
    }
}

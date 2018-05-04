package com.ashomok.lullabies.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashomok.lullabies.AlbumArtCache;
import com.ashomok.lullabies.R;
import com.ashomok.lullabies.model.MusicProvider;
import com.ashomok.lullabies.rate_app.RateAppAsker;
import com.ashomok.lullabies.utils.LogHelper;

import javax.inject.Inject;
import dagger.android.support.DaggerFragment;

/**
 * Created by iuliia on 03.05.16.
 */

//see FullScreenPlayerActivity if you want to add more components
public class MusicFragment extends DaggerFragment {

    private static final String TAG = LogHelper.makeLogTag(MusicFragment.class);
    private static final String ARGUMENT_MEDIA_ITEM = "media_item";
    private MediaBrowserCompat.MediaItem mediaItem;
    private ImageView mBackgroundImage;
    private String mCurrentArtUrl;

    @Inject
    MusicProvider musicProvider;

    @Inject
    public MusicFragment() {
        // Required empty public constructor
    }

    public MediaBrowserCompat.MediaItem getMediaItem() {
        return mediaItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaItem = getArguments().getParcelable(ARGUMENT_MEDIA_ITEM);

        RateAppAsker.init(getActivity());
    }

    static MusicFragment newInstance(MediaBrowserCompat.MediaItem mediaItem) {
        MusicFragment pageFragment = new MusicFragment();
        Bundle arguments = new Bundle();

        arguments.putParcelable(ARGUMENT_MEDIA_ITEM, mediaItem);

        pageFragment.setArguments(arguments);
        return pageFragment;
    }


    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_fragment, null);
        mBackgroundImage = view.findViewById(R.id.image);
        fetchImageAsync(mediaItem.getDescription());
        return view;
    }

    //todo what if image drawable come
    private void fetchImageAsync(@NonNull MediaDescriptionCompat description) {
        if (musicProvider != null)
        {

        }
        if (description.getIconUri() == null && description.getIconBitmap() == null) {
            return; //cals every time - todo fix it
        }

        Uri iconUri = description.getIconUri();

        if (iconUri != null) {
            String artUrl = iconUri.toString();
            mCurrentArtUrl = artUrl;
            AlbumArtCache cache = AlbumArtCache.getInstance();
            Bitmap art = cache.getBigImage(artUrl);
            if (art == null) {
                art = description.getIconBitmap();
            }
            if (art != null) {
                // if we have the art cached or from the MediaDescription, use it:
                mBackgroundImage.setImageBitmap(art);
            } else {
                // otherwise, fetch a high res version and update:
                cache.fetch(artUrl, new AlbumArtCache.FetchUrlListener() {
                    @Override
                    public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
                        // sanity check, in case a new fetch request has been done while
                        // the previous hasn't yet returned:
                        if (artUrl.equals(mCurrentArtUrl)) {
                            mBackgroundImage.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        } else {
            Bitmap art = description.getIconBitmap();
            mBackgroundImage.setImageBitmap(art);
        }
    }
}

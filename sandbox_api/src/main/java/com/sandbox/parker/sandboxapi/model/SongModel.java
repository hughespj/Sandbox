package com.sandbox.parker.sandboxapi.model;

import com.sandbox.parker.sandboxapi.dto.SongCollection;

/**
 * Created by parker on 10/6/17.
 */

public class SongModel {

    private SongModel.Presenter mPresenter;

    public SongModel(SongModel.Presenter presenter) {
        mPresenter = presenter;
    }

    public void getSongCollection(int pageNumber) {

    }

    interface Presenter {
        void onSongSuccess(SongCollection songs);
        void onSongFailure();
    }
}

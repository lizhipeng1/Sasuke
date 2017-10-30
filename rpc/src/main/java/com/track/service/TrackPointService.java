package com.track.service;

import com.track.model.TrackPointInfo;

/**
 * Created by hzlizhipeng on 2017/10/26.
 */
public interface TrackPointService {

    void persistenceRpcInvoke(TrackPointInfo trackPointInfo );
}

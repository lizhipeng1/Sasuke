package com.rpc.monitor.remote;

import com.rpc.annotation.provider.ServiceProvider;
import com.rpc.enums.RpcTypeEnum;
import com.track.model.TrackPointInfo;
import com.track.service.TrackPointService;

/**
 * Created by hzlizhipeng on 2017/10/26.
 */
@ServiceProvider(rpcTypeEnum = RpcTypeEnum.Hessian )
public class TrackPointServiceImpl  implements TrackPointService {
    @Override
    public void persistenceRpcInvoke(TrackPointInfo trackPointInfo) {
        System.out.println( "method：" + trackPointInfo.getMethod() );
        System.out.println( "params：" + trackPointInfo.getParam() );
        System.out.println( "userInfo：" + trackPointInfo.getUserInfo() );
        System.out.println( "responseString：" + trackPointInfo.getResponse() );
        System.out.println( "runTime：" + trackPointInfo.getRunTime() );
        System.out.println("==============================================");
    }
}

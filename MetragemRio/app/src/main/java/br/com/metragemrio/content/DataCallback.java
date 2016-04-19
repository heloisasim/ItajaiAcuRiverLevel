package br.com.metragemrio.content;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import br.com.metragemrio.AppApplication;
import br.com.metragemrio.MainActivity;
import br.com.metragemrio.database.DamDataSource;
import br.com.metragemrio.model.Dam;
import br.com.metragemrio.model.Dams;
import br.com.metragemrio.model.Meterage;
import br.com.metragemrio.database.MeterageDataSource;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DataCallback implements Callback<List<Meterage>> {

    @Override
    public void success(List<Meterage> meterageList, Response response) {
        if (meterageList != null) {
            MeterageDataSource dataSource = new MeterageDataSource();
            DamDataSource damDataSource = new DamDataSource();
            for (int i = 0; i < meterageList.size(); i++) {
                Meterage m = meterageList.get(i);
                dataSource.create(meterageList.get(i));
                Dams dams = m.getDams();
                if (dams != null) {
                    if (dams.getItuporanga() != null)
                        damDataSource.create(dams.getItuporanga(), Dam.TYPE_ITUPORANGA, m.getTimestamp());

                    if (dams.getTaio() != null)
                        damDataSource.create(dams.getTaio(), Dam.TYPE_TAIO, m.getTimestamp());
                }
            }
            sendToBroadcastReceiver(true);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        sendToBroadcastReceiver(false);

    }

    private void sendToBroadcastReceiver(boolean success) {
        Intent localIntent = new Intent(MainActivity.BROADCAST_DATA_RECEIVED);
        localIntent.putExtra("success", success);
        LocalBroadcastManager.getInstance(AppApplication.getContext()).sendBroadcast(localIntent);
    }

}

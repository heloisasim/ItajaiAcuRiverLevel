package br.com.metragemrio;

import android.app.Dialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import br.com.metragemrio.database.DamDataSource;
import br.com.metragemrio.model.Dam;
import br.com.metragemrio.model.Dams;
import br.com.metragemrio.model.Meterage;

public class DetailDialog {

    public static void show(Context context, Meterage meterage) {

        // custom dialog
        final Dialog dialog = new Dialog(context);

        AppApplication.getTracker().setScreenName("MainActivity");
        AppApplication.getTracker().send(new HitBuilders.EventBuilder().setCategory("Click")
                .setAction("Detail").setLabel("Detail: " + meterage.getLevel()).build());

        DamDataSource damsDataSource = new DamDataSource();
        Dam ituporanga = damsDataSource.get(meterage.getTimestamp(), Dam.TYPE_ITUPORANGA);
        Dam taio = damsDataSource.get(meterage.getTimestamp(), Dam.TYPE_TAIO);

        Dams dams = new Dams();
        dams.setTaio(taio);
        dams.setItuporanga(ituporanga);
        meterage.setDams(dams);

        String datetime = DateFormat.format("kk:mm", meterage.getTimestamp() * 1000).toString();
        dialog.setTitle(datetime + ": " + String.valueOf(meterage.getLevel() + "m"));

        dialog.setContentView(R.layout.detail_dialog);

        if (meterage.getDams() != null) {

            TextView ituOpen = (TextView) dialog.findViewById(R.id.ituporanga_comportas_abertas);
            TextView ituClosed = (TextView) dialog.findViewById(R.id.ituporanga_comportas_fechadas);
            TextView ituCapacity = (TextView) dialog.findViewById(R.id.ituporanga_capacidade);
            if (meterage.getDams().getItuporanga() != null) {
                ituOpen.setText("Comportas abertas: " + String.valueOf(meterage.getDams().getItuporanga().getOpen()));
                ituClosed.setText("Comportas fechadas: " + String.valueOf(meterage.getDams().getItuporanga().getClosed()));
                ituCapacity.setText("Capacidade: " + String.valueOf(meterage.getDams().getItuporanga().getCapacity()));
            } else {
                ituOpen.setText("Informações não disponíveis");
                ituClosed.setVisibility(View.GONE);
                ituCapacity.setVisibility(View.GONE);
            }


            TextView taioOpen = (TextView) dialog.findViewById(R.id.taio_comportas_abertas);
            TextView taioClosed = (TextView) dialog.findViewById(R.id.taio_comportas_fechadas);
            TextView taioCapacity = (TextView) dialog.findViewById(R.id.taio_capacidade);

            if (meterage.getDams().getTaio() != null) {
                taioOpen.setText("Comportas abertas: " + String.valueOf(meterage.getDams().getTaio().getOpen()));
                taioClosed.setText("Comportas fechadas: " + String.valueOf(meterage.getDams().getTaio().getClosed()));
                taioCapacity.setText("Capacidade: " + String.valueOf(meterage.getDams().getTaio().getCapacity()));
            } else {
                taioOpen.setText("Informações não disponíveis");
                taioCapacity.setVisibility(View.GONE);
                taioClosed.setVisibility(View.GONE);
            }
        }
        dialog.show();
    }
}

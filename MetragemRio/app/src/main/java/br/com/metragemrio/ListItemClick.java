package br.com.metragemrio;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import br.com.metragemrio.model.Meterage;

public class ListItemClick implements AdapterView.OnItemClickListener {

    private List<Meterage> all;

    public ListItemClick(List<Meterage> meterage) {
        this.all = meterage;
    }

    public void updateContent(List<Meterage> meterage) {
        if (all != null) {
            all.clear();
        } else {
            all = new ArrayList<>();
        }
        all.addAll(meterage);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (i > 1 && all.size() > 0) {
            Meterage meterage = all.get(i - 2);
            DetailDialog.show(view.getContext(), meterage);
        }
    }
}

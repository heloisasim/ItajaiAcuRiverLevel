package br.com.metragemrio;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.metragemrio.model.Meterage;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MeterageAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    List<Meterage> mMeterageList;
    Context mContext;

    public MeterageAdapter(Context context) {
        mContext = context;
    }

    public void setContent(List<Meterage> payments) {

        mMeterageList = payments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mMeterageList != null && mMeterageList.size() > 0) ? mMeterageList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return (mMeterageList != null && mMeterageList.size() > i) ? mMeterageList.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(mContext).inflate(R.layout.cell_meterage, viewGroup, false);

        TextView hour = (TextView) view.findViewById(R.id.hour);
        TextView level = (TextView) view.findViewById(R.id.level);
        ImageView image = (ImageView) view.findViewById(R.id.imageView);
        hour.setText(DateFormat.format("kk:mm", mMeterageList.get(i).getTimestamp() * 1000));

        level.setText(String.valueOf(mMeterageList.get(i).getLevel())+"m");
        String statusText = mMeterageList.get(i).getStatus();

        int color = MainActivity.getColor(statusText);
        applyColor(image, color);
        return view;
    }

    public void applyColor(ImageView imageView, int color){
        Drawable image = imageView.getDrawable();
        image.mutate().setColorFilter(mContext.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        imageView.setImageDrawable(image);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.section_divider, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.textViewHeaderName);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.text.setText(DateFormat.format("dd/MM/yyyy | EEEE", mMeterageList.get(position).getTimestamp() * 1000));
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        String date = DateFormat.format("dd/MM/yyyy | EEEE", mMeterageList.get(position).getTimestamp() * 1000).toString();
        return date.hashCode();
    }

    class HeaderViewHolder {
        TextView text;
    }
}

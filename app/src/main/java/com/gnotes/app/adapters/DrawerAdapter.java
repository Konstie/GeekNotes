package com.gnotes.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gnotes.app.R;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mMenuTitles[];
    private int mIcons[];

    private String name;
    private String slogan;
    Context context;

    DrawerAdapter(String titles[], int icons[],
                  String name, String slogan, Context passedCtx) {
        mMenuTitles = titles;
        mIcons = icons;
        this.name = name;
        this.slogan = slogan;
        this.context = passedCtx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType, context);
            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType, context);
            return vhHeader;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
        if (holder.holderID == 1) {
            holder.textView.setText(mMenuTitles[position-1]);
            holder.imageView.setImageResource(mIcons[position-1]);
        } else {
            holder.name.setText(this.name);
            holder.slogan.setText(slogan);
        }
    }

    @Override
    public int getItemCount() {
        return mMenuTitles.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        int holderID;

        TextView textView;
        ImageView imageView;
        TextView name;
        TextView slogan;
        Context ctx;

        public ViewHolder(View itemView, int ViewType, Context ctx) {
            super(itemView);
            this.ctx = ctx;
            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderID = 1;
            } else {
                name = (TextView) itemView.findViewById(R.id.name);
                slogan = (TextView) itemView.findViewById(R.id.email);
                holderID = 0;
            }
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(ctx, "The clicked item is: " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
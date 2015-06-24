package cy.com.morefan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cy.com.morefan.R;
import cy.com.morefan.util.ViewHolderUtil;

public class MenuAdapter extends BaseAdapter{

	private Context mContext;
	private int[] imgs;
	private String[] names;
	public MenuAdapter(Context mContext,int[] imgs, String[] names){
		this.mContext = mContext;
		this.imgs = imgs;
		this.names = names;
	}
	@Override
	public int getCount() {
		if(null == imgs || null == names)
			return 0;
		return imgs.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(null == convertView)
			convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_item, null);
		TextView txt  = ViewHolderUtil.get(convertView, R.id.txt);
		ImageView img = ViewHolderUtil.get(convertView, R.id.img);
		txt.setText(names[position]);
		img.setBackgroundResource(imgs[position]);
		return convertView;
	}

}

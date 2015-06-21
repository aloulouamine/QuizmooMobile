package com.disycs.quizmo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.disycs.quizmo.design.Color;
import com.disycs.quizmo.design.FontChangeCrawler;
import com.disycs.quizmo.model.Questionnaire;

import java.util.List;

public class QuestionnaireAdapter extends ArrayAdapter<Questionnaire>{

	private final List<Questionnaire> mList;
	private final Activity mContext;
	private final boolean mSync;

	public QuestionnaireAdapter(Activity mContext, 
			List<Questionnaire> mList) {
		super(mContext,R.layout.questionnaire_item, mList);
		this.mContext=mContext;
		this.mList= mList;
		SharedPreferences settings = mContext.getSharedPreferences("Quizmoo", 0);
		this.mSync=settings.getBoolean("SYNC", true);		
	}

	static class ViewHolder{
		protected TextView title,description, date,label;
		protected RelativeLayout label_layout;		
		protected ImageView show_more,attach;
	}
	@Override 
	public View getView(int position , View convertView , ViewGroup parent){
		View view = null;
		if (convertView == null){
			LayoutInflater inflator = mContext.getLayoutInflater();
			view = inflator.inflate(R.layout.questionnaire_item, null);
			FontChangeCrawler fontChanger = new FontChangeCrawler(mContext.getAssets(), "font/montserratregular.ttf");
			fontChanger.replaceFonts((ViewGroup)view);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView)view.findViewById(R.id.txtapptitle);
			viewHolder.description= (TextView)view.findViewById(R.id.txt_descriptioin_quest_item);
			viewHolder.date = (TextView)view.findViewById(R.id.txt_date_quest_item);
			viewHolder.label=(TextView)view.findViewById(R.id.txt_item_label);
			viewHolder.label_layout=(RelativeLayout)view.findViewById(R.id.label_layout);
			viewHolder.show_more=(ImageView)view.findViewById(R.id.img_showmore_listitem);
			viewHolder.attach= (ImageView)view.findViewById(R.id.imgattach);
			view.setTag(viewHolder);
		}else{
				view=convertView;
			}
		ViewHolder holder= (ViewHolder)view.getTag();
		updateViewHolder(holder,mSync,position);	
		return view;
	}
	private void updateViewHolder(ViewHolder holder,  boolean mSync, int position){
		holder.title.setText(mList.get(position).getTitle());
		holder.description.setText(mList.get(position).getDescription());
		holder.date.setText(mList.get(position).getDateOfCreation());
		holder.label_layout.setBackgroundColor(mContext.getResources().getColor(Color.color
				[mList.get(position).getCategory().ordinal()]));
		holder.label.setText(mContext.getResources().getStringArray(R.array.category)
				[mList.get(position).getCategory().ordinal()]);
		holder.attach.setVisibility(View.GONE);
	}

}
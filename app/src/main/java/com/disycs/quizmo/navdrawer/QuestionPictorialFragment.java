package com.disycs.quizmo.navdrawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.disycs.quizmo.model.questions.QuestionPictorial;
import com.disycs.quizmo.model.responses.Response;
import com.disycs.quizmo.model.responses.ResponsePictorial;
import com.disycs.quizmo.model.responses.ResponseProvider;
import com.disycs.quizmo.model.responses.ResponseSingleTextBox;
import com.disycs.quizmo.R;
import com.disycs.quizmo.design.FontChangeCrawler;

public class QuestionPictorialFragment extends Fragment implements ResponseProvider{
	EditText commentTxt;
	WebView image;
	QuestionPictorial q;
	int index,total;
	EditText singleTextBox;
	ResponseSingleTextBox response ;
	public void setFont(View rootView)
	{	    
	    FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "font/montserratregular.ttf");
	    fontChanger.replaceFonts((ViewGroup) rootView);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		index = getArguments().getInt("INDEX");
		index--;
		total=getArguments().getInt("TOTAL");
		View rootView=null;
		q =  (QuestionPictorial) getArguments().get("QUESTION");
		rootView= inflater.inflate(R.layout.fragment_pictorial_quest, container, false);
		commentTxt = (EditText)rootView.findViewById(R.id.txt_image_comment);
		image= (WebView)rootView.findViewById(R.id.webView);
		Log.i("pictureURL ",q.getPictureUrl());
		image.loadUrl(q.getPictureUrl());
		LinearLayout outer = (LinearLayout)rootView.findViewById(R.id.question_outer_layout);
        outer.setBackgroundColor(getActivity().getResources().getColor(getArguments().getInt("COLOR")));
		TextView title = (TextView)rootView.findViewById(R.id.txt_ques_title);
        title.setText(index+"/"+total+") "+q.getText());
        setFont(rootView);
        return rootView;
	}
	@Override
	public Response getResponse() {
		ResponsePictorial r = new ResponsePictorial(q);
		r.setComment(commentTxt.getText().toString());
		Log.i("Response Pictorial",r.getJsonString());
		return r;
	}

}

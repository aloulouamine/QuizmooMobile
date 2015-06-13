package com.disycs.quizmo.model.responses;

import android.os.Parcel;
import com.disycs.quizmo.model.questions.Question;
import com.disycs.quizmo.model.questions.QuestionSingleTextBox;

public class ResponseSingleTextBox extends Response {
	QuestionSingleTextBox q;
	String text;
	public ResponseSingleTextBox(Question q,String text) {
		super(q);
		// TODO Auto-generated constructor stub
		this.q=(QuestionSingleTextBox)q;
		this.text=text;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getJsonString() {
		String json = "invalid";
		if(isValid()){
			json="{\"id\":"+q.getId()+",\"answer\":\""+text+"\"}";
		}
		return json;
	}

	@Override
	public boolean isValid() {
		if(text.length()>0){
			return true;
		}
		return false;
	}

}

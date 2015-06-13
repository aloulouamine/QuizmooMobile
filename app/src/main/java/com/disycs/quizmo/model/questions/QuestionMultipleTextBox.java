package com.disycs.quizmo.model.questions;


import android.os.Parcel;
import com.disycs.quizmo.model.Option;
import com.disycs.quizmo.R;

public class QuestionMultipleTextBox extends Question {
	/**
	 * 
	 */
	
	Option[] options;

	public QuestionMultipleTextBox(int id, String text, Option[] options) {
		super(id, text);
		this.options = options;
	}

	public Option[] getOptions() {
		return options;
	}

	@Override
	public
	int getIcon() {
		
		return R.drawable.ic_txts;
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
	
	
}

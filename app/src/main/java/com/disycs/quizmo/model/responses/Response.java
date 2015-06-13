package com.disycs.quizmo.model.responses;

import android.os.Parcelable;
import com.disycs.quizmo.model.questions.Question;

public abstract class Response implements Parcelable {
	/**
	 * 
	 */
	
	Question q;

	public Response(Question q) {
		this.q = q;
	}
	
	abstract public String getJsonString();
	abstract public boolean isValid();

}
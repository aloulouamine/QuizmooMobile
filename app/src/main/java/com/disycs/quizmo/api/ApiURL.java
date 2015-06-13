package com.disycs.quizmo.api;

import com.disycs.quizmo.model.Questionnaire.CATEGORY;
import com.disycs.quizmo.model.Questionnaire.STATE;

public class ApiURL {

	static String BaseURL = "http://www.disycs.com/quizmoo-new-staging/web";
	public static String AuthAPI=BaseURL+"/apiauth";
	
	public static String questAPI(STATE state, CATEGORY category, int page){
			return BaseURL+"/api/questionnaire/"+
					state.getCode()+"/"+
					page+"/"+
					category.getCode();
	}
	
	public static String QuestDetailAPI(int id){
		return BaseURL+"/api/questionnaire/details/"+id;
	}
}

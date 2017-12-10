package com.rzico.weex.json;

import java.util.ArrayList;
import java.util.List;


public class StringsResponse extends BaseResponse {
	private List<String> data;

	
	public List<String> getData() {
		if(data == null){
			return new ArrayList<String>();
		}
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}


}

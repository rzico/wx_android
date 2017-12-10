package com.rzico.weex.json;

import java.util.HashMap;
import java.util.Map;


public class MapResponse extends BaseResponse {
	private Map<String, Object> data;

	public Map<String, Object> getData() {
		if (data == null) {
			return new HashMap<String, Object>();
		}
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}

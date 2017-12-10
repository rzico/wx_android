package com.rzico.weex.json;

import java.util.ArrayList;
import java.util.List;

public class ListResponse<T> extends BaseResponse {
	private List<T> data;

	public List<T> getData() {
		if (data == null) {
			return new ArrayList<T>();
		}
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}

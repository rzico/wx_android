package com.rzico.weex.json;


public class ObjectResponse<T> extends BaseResponse {
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}

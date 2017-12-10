package com.rzico.weex.json;


public class BaseResponse {
	private MessageEntity message;

	public BaseResponse(){

	}

	public BaseResponse(MessageEntity message){
		this.message = message;
	}

	public MessageEntity getMessage() {
		return message;
	}

	public void setMessage(MessageEntity message) {
		this.message = message;
	}

}

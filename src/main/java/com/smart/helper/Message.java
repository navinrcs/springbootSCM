package com.smart.helper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Message {
	
	private String content;
	private String type;
	public Message(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	
	
	
	

}

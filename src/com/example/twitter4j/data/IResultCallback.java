package com.example.twitter4j.data;

public interface IResultCallback<T> {
	public void perform(T result);
}
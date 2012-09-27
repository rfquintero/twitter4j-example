package com.example.twitter4j.ops;

public interface IResultCallback<T> {
	public void perform(T result);
}
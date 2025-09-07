package com.ibn.arrp.util;

public interface CallableFunction<A, B> {
	B get(A a) throws Exception;
}

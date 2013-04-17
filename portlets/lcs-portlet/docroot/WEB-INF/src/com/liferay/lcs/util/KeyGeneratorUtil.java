package com.liferay.lcs.util;

/**
 * @author Ivica Cardic
 */
public class KeyGeneratorUtil {

	public static String getKey() throws Exception {
		return _keyGenerator.getKey();
	}

	public void setKeyGenerator(KeyGenerator keyGenerator) {
		_keyGenerator = keyGenerator;
	}

	private static KeyGenerator _keyGenerator;
}
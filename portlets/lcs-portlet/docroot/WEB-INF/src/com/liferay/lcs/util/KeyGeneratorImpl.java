package com.liferay.lcs.util;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PortalClassInvoker;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.license.util.LicenseManager;
import com.liferay.util.Encryptor;

import java.io.FileInputStream;
import java.io.InputStream;

import java.security.Key;
import java.security.KeyStore;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/**
 * @author Ivica Cardic
 */
public class KeyGeneratorImpl implements KeyGenerator {

	public String getKey() {
		if (_clusterNodeKey == null) {
			_clusterNodeKey = _digester.digestHex(Digester.MD5, getServerId());
		}

		return _clusterNodeKey;
	}

	public void setDigester(Digester digester) {
		_digester = digester;
	}

	public void setKeyAlias(String keyAlias) {
		_keyAlias = keyAlias;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		_keyStorePassword = keyStorePassword;
	}

	public void setKeyStorePath(String keyStorePath) {
		_keyStorePath = keyStorePath;
	}

	public void setKeyStoreType(String keyStoreType) {
		_keyStoreType = keyStoreType;
	}

	public void setLicenseManager(LicenseManager licenseManager) {
		_licenseManager = licenseManager;
	}

	protected byte[] generateServerId() throws SystemException {
		try {
			String hostName = _licenseManager.getHostName();
			Set<String> ipAddresses = _licenseManager.getIpAddresses();
			Set<String> macAddresses = _licenseManager.getMacAddresses();

			Properties serverIdProperties = new Properties();

			serverIdProperties.put("hostName", hostName);
			serverIdProperties.put(
				"ipAddresses", StringUtil.merge(ipAddresses));
			serverIdProperties.put(
				"macAddresses", StringUtil.merge(macAddresses));
			serverIdProperties.put("salt", UUID.randomUUID().toString());

			String propertiesString = PropertiesUtil.toString(
				serverIdProperties);

			byte[] bytes = propertiesString.getBytes(StringPool.UTF8);

			Key key = getGeneratorKey();

			bytes = Encryptor.encryptUnencoded(key, bytes);

			return bytes;
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}

	protected String getServerId() {
		Class<?> licenseUtilClass;
		try {
			licenseUtilClass = PortalClassLoaderUtil.getClassLoader()
				.loadClass("com.liferay.portal.license.util.LicenseUtil");

			MethodKey getServerIdBytes = new MethodKey(
				licenseUtilClass, "getServerIdBytes");

			byte[] serverIdBytes = (byte[])PortalClassInvoker.invoke(
				true, getServerIdBytes);

			if (serverIdBytes.length == 0) {
				serverIdBytes = generateServerId();

				writeServerProperties(serverIdBytes);
			}

			return Arrays.toString(serverIdBytes);

		} catch (Exception e) {
			_log.error(e);
		}

		return StringPool.BLANK;
	}

	protected void writeServerProperties(byte[] serverIdBytes)
		throws Exception {

		Class<?> licenseUtilClass = PortalClassLoaderUtil.getClassLoader()
			.loadClass("com.liferay.portal.license.util.LicenseUtil");

		MethodKey writeServerProperties = new MethodKey(
			licenseUtilClass, "writeServerProperties", byte[].class);

		PortalClassInvoker.invoke(true, writeServerProperties, serverIdBytes);
	}

	private Key getGeneratorKey() throws Exception {
		KeyStore.ProtectionParameter protectionParameter =
			new KeyStore.PasswordProtection(_keyStorePassword.toCharArray());

		KeyStore keyStore = getKeyStore();

		KeyStore.SecretKeyEntry secretKeyEntry =
			(KeyStore.SecretKeyEntry)keyStore.getEntry(
				_keyAlias, protectionParameter);

		return secretKeyEntry.getSecretKey();
	}

	private KeyStore getKeyStore() throws Exception {
		if (_keyStore != null) {
			return _keyStore;
		}

		KeyStore keyStore = KeyStore.getInstance(_keyStoreType);

		InputStream inputStream = null;

		int index = _keyStorePath.indexOf("classpath:");

		if (index != -1) {
			String classPath = _keyStorePath.substring(index + 10);

			Class<?> clazz = getClass();

			inputStream = clazz.getResourceAsStream(classPath);
		}
		else {
			inputStream = new FileInputStream(_keyStorePath);
		}

		keyStore.load(inputStream, _keyStorePassword.toCharArray());

		_keyStore = keyStore;

		return _keyStore;
	}

	private static Log _log = LogFactoryUtil.getLog(KeyGeneratorImpl.class);

	private String _clusterNodeKey;
	private Digester _digester;
	private String _keyAlias;
	private KeyStore _keyStore;
	private String _keyStorePassword;
	private String _keyStorePath;
	private String _keyStoreType;
	private LicenseManager _licenseManager;

}
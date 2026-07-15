/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.BearerHTTPAuthorization;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HTTPAuthorization;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Peter Yoo
 */
public abstract class SecretsUtil {

	public static String getSecret(String key) {
		Matcher matcher = _secretReferencePattern.matcher(key);

		if (matcher.matches()) {
			String secret = getSecret(
				matcher.group("vaultName"), matcher.group("itemTitle"),
				matcher.group("fieldLabel"));

			if (!JenkinsResultsParserUtil.isNullOrEmpty(secret)) {
				return secret;
			}
		}

		return key;
	}

	public static String getSecret(
		String vaultName, String itemTitle, String fieldLabel) {

		String secretReference = _getSecretReference(
			vaultName, itemTitle, fieldLabel);

		if (_secrets.containsKey(secretReference)) {
			return _secrets.get(secretReference);
		}

		String cachedSecret = _getCachedSecret(secretReference);

		if (cachedSecret != null) {
			return cachedSecret;
		}

		return _getSecretFromConnect(vaultName, itemTitle, fieldLabel);
	}

	public static boolean isSecretProperty(String value) {
		if (value == null) {
			return false;
		}

		Matcher matcher = _secretReferencePattern.matcher(value);

		return matcher.matches();
	}

	public static void writeCachedSecrets(File cachedSecretsFile)
		throws IOException {

		if (!_isSecretsConfigured()) {
			System.out.println(
				"Secrets are not configured, unable to write 1Password cache");

			return;
		}

		PublicKey cachedSecretsPublicKey = _getCachedSecretsPublicKey();

		if (cachedSecretsPublicKey == null) {
			System.out.println(
				"Unable to write encrypted 1Password cache, cached secrets " +
					"public key is not configured");

			return;
		}

		JSONObject jsonObject = new JSONObject();

		for (Vault vault : Vault.getInstances()) {
			String vaultName = vault.getName();

			for (Item item : vault.getItems()) {
				String itemId = item.getId();
				String itemTitle = item.getTitle();

				for (ItemField itemField : item.getItemFields()) {
					String itemFieldValue = itemField.getValue();

					if (JenkinsResultsParserUtil.isNullOrEmpty(
							itemFieldValue)) {

						continue;
					}

					String itemFieldLabel = itemField.getLabel();
					String itemFieldId = itemField.getId();

					jsonObject.put(
						_getSecretReference(vaultName, itemId, itemFieldId),
						itemFieldValue
					).put(
						_getSecretReference(vaultName, itemId, itemFieldLabel),
						itemFieldValue
					).put(
						_getSecretReference(vaultName, itemTitle, itemFieldId),
						itemFieldValue
					).put(
						_getSecretReference(
							vaultName, itemTitle, itemFieldLabel),
						itemFieldValue
					);
				}

				for (ItemFile itemFile : item.getItemFiles()) {
					String itemFileValue = itemFile.getValue();

					if (JenkinsResultsParserUtil.isNullOrEmpty(itemFileValue)) {
						continue;
					}

					String itemFileName = itemFile.getName();

					jsonObject.put(
						_getSecretReference(vaultName, itemId, itemFileName),
						itemFileValue
					).put(
						_getSecretReference(vaultName, itemTitle, itemFileName),
						itemFileValue
					);
				}
			}
		}

		String encryptedCache = null;

		try {
			encryptedCache = _encrypt(
				jsonObject.toString(), cachedSecretsPublicKey);
		}
		catch (GeneralSecurityException generalSecurityException) {
			throw new IOException(
				"Unable to encrypt 1Password cache", generalSecurityException);
		}

		JenkinsResultsParserUtil.write(cachedSecretsFile, encryptedCache);

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Wrote ", String.valueOf(jsonObject.length()),
				" encrypted secrets to ", cachedSecretsFile.toString()));
	}

	private static byte[] _convertPEMToDER(String pem) {
		pem = pem.replaceAll("-----BEGIN [^-]+-----", "");
		pem = pem.replaceAll("-----END [^-]+-----", "");

		pem = pem.replaceAll("\\s", "");

		Base64.Decoder decoder = Base64.getDecoder();

		return decoder.decode(pem);
	}

	private static String _decrypt(String content, PrivateKey privateKey)
		throws GeneralSecurityException {

		JSONObject envelopeJSONObject;

		try {
			envelopeJSONObject = new JSONObject(content);
		}
		catch (JSONException jsonException) {
			return null;
		}

		if (!envelopeJSONObject.has("cipherText") ||
			!envelopeJSONObject.has("encryptedKey") ||
			!envelopeJSONObject.has("iv")) {

			return null;
		}

		Base64.Decoder decoder = Base64.getDecoder();

		Cipher rsaCipher = Cipher.getInstance(
			"RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

		rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

		SecretKey secretKey = new SecretKeySpec(
			rsaCipher.doFinal(
				decoder.decode(envelopeJSONObject.getString("encryptedKey"))),
			"AES");

		byte[] iv = decoder.decode(envelopeJSONObject.getString("iv"));

		Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");

		aesCipher.init(
			Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

		byte[] plainText = aesCipher.doFinal(
			decoder.decode(envelopeJSONObject.getString("cipherText")));

		return new String(plainText, StandardCharsets.UTF_8);
	}

	private static String _encrypt(String plainText, PublicKey publicKey)
		throws GeneralSecurityException {

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		byte[] iv = new byte[12];

		SecureRandom secureRandom = new SecureRandom();

		secureRandom.nextBytes(iv);

		Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");

		aesCipher.init(
			Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

		byte[] cipherText = aesCipher.doFinal(
			plainText.getBytes(StandardCharsets.UTF_8));

		Cipher rsaCipher = Cipher.getInstance(
			"RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

		rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] encryptedKey = rsaCipher.doFinal(secretKey.getEncoded());

		Base64.Encoder encoder = Base64.getEncoder();

		JSONObject envelopeJSONObject = new JSONObject();

		envelopeJSONObject.put(
			"cipher", _CACHE_CIPHER
		).put(
			"cipherText", encoder.encodeToString(cipherText)
		).put(
			"encryptedKey", encoder.encodeToString(encryptedKey)
		).put(
			"iv", encoder.encodeToString(iv)
		);

		return envelopeJSONObject.toString();
	}

	private static synchronized String _getAccessToken() {
		if (_accessToken != null) {
			return _accessToken;
		}

		String accessToken;

		try {
			String accessTokenKey = JenkinsResultsParserUtil.getBuildProperty(
				"one.password.access.token.key");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(accessTokenKey)) {
				accessToken = _getSSMParameterValue(accessTokenKey);
			}
			else {
				accessToken = "";
			}
		}
		catch (IOException | TimeoutException exception) {
			accessToken = "";
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(accessToken)) {
			JenkinsResultsParserUtil.addRedactToken(accessToken);
		}

		_accessToken = accessToken;

		return _accessToken;
	}

	private static synchronized String _getCachedSecret(String key) {
		if (!_cachedSecretsLoaded) {
			_loadCachedSecrets();
		}

		if (_cachedSecrets == null) {
			return null;
		}

		return _cachedSecrets.get(key);
	}

	private static synchronized String _getCachedSecretsContent()
		throws IOException {

		if (_cachedSecretsContent != null) {
			return _cachedSecretsContent;
		}

		String cachedSecretsURL = _getCachedSecretsURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(cachedSecretsURL)) {
			_cachedSecretsContent = "";

			return _cachedSecretsContent;
		}

		if (JenkinsResultsParserUtil.isURL(cachedSecretsURL)) {
			_cachedSecretsContent = JenkinsResultsParserUtil.toString(
				cachedSecretsURL, false);

			return _cachedSecretsContent;
		}

		String filePrefix = "file://";

		if (!cachedSecretsURL.startsWith(filePrefix)) {
			_cachedSecretsContent = "";

			return _cachedSecretsContent;
		}

		File file = new File(cachedSecretsURL.substring(filePrefix.length()));

		if (!file.exists()) {
			_cachedSecretsContent = "";

			return _cachedSecretsContent;
		}

		_cachedSecretsContent = JenkinsResultsParserUtil.read(file);

		return _cachedSecretsContent;
	}

	private static synchronized PrivateKey _getCachedSecretsPrivateKey() {
		if (_cachedSecretsPrivateKeyInitialized) {
			return _cachedSecretsPrivateKey;
		}

		_cachedSecretsPrivateKeyInitialized = true;

		String cachedSecretsPrivateKeyPEM = _getCachedSecretsPrivateKeyPEM();

		if (JenkinsResultsParserUtil.isNullOrEmpty(
				cachedSecretsPrivateKeyPEM)) {

			return null;
		}

		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			_cachedSecretsPrivateKey = keyFactory.generatePrivate(
				new PKCS8EncodedKeySpec(
					_convertPEMToDER(cachedSecretsPrivateKeyPEM)));
		}
		catch (GeneralSecurityException generalSecurityException) {
			System.out.println(
				"Unable to parse cache private key " +
					generalSecurityException.getMessage());

			_cachedSecretsPrivateKey = null;
		}

		return _cachedSecretsPrivateKey;
	}

	private static synchronized String _getCachedSecretsPrivateKeyPEM() {
		if (_cachedSecretsPrivateKeyPEM != null) {
			return _cachedSecretsPrivateKeyPEM;
		}

		String cachedSecretsPrivateKeyPEM;

		try {
			String cachedSecretsPrivateKeyPEMKey =
				JenkinsResultsParserUtil.getBuildProperty(
					"one.password.cached.secrets.private.key.pem.key");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					cachedSecretsPrivateKeyPEMKey)) {

				cachedSecretsPrivateKeyPEM = _getSSMParameterValue(
					cachedSecretsPrivateKeyPEMKey);
			}
			else {
				cachedSecretsPrivateKeyPEM = "";
			}
		}
		catch (IOException | TimeoutException exception) {
			cachedSecretsPrivateKeyPEM = "";
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				cachedSecretsPrivateKeyPEM)) {

			JenkinsResultsParserUtil.addRedactToken(cachedSecretsPrivateKeyPEM);
		}

		_cachedSecretsPrivateKeyPEM = cachedSecretsPrivateKeyPEM;

		return _cachedSecretsPrivateKeyPEM;
	}

	private static synchronized PublicKey _getCachedSecretsPublicKey() {
		if (_cachedSecretsPublicKeyInitialized) {
			return _cachedSecretsPublicKey;
		}

		_cachedSecretsPublicKeyInitialized = true;

		String cachedSecretsPublicKeyPEM = _getCachedSecretsPublicKeyPEM();

		if (JenkinsResultsParserUtil.isNullOrEmpty(cachedSecretsPublicKeyPEM)) {
			return null;
		}

		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			_cachedSecretsPublicKey = keyFactory.generatePublic(
				new X509EncodedKeySpec(
					_convertPEMToDER(cachedSecretsPublicKeyPEM)));
		}
		catch (GeneralSecurityException generalSecurityException) {
			System.out.println(
				"Unable to parse cache public key " +
					generalSecurityException.getMessage());

			_cachedSecretsPublicKey = null;
		}

		return _cachedSecretsPublicKey;
	}

	private static synchronized String _getCachedSecretsPublicKeyPEM() {
		if (_cachedSecretsPublicKeyPEM != null) {
			return _cachedSecretsPublicKeyPEM;
		}

		String cachedSecretsPublicKeyPEM;

		try {
			String cachedSecretsPublicKeyPEMKey =
				JenkinsResultsParserUtil.getBuildProperty(
					"one.password.cached.secrets.public.key.pem.key");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					cachedSecretsPublicKeyPEMKey)) {

				cachedSecretsPublicKeyPEM = _getSSMParameterValue(
					cachedSecretsPublicKeyPEMKey);
			}
			else {
				cachedSecretsPublicKeyPEM = "";
			}
		}
		catch (IOException | TimeoutException exception) {
			cachedSecretsPublicKeyPEM = "";
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				cachedSecretsPublicKeyPEM)) {

			JenkinsResultsParserUtil.addRedactToken(cachedSecretsPublicKeyPEM);
		}

		_cachedSecretsPublicKeyPEM = cachedSecretsPublicKeyPEM;

		return _cachedSecretsPublicKeyPEM;
	}

	private static synchronized String _getCachedSecretsURL() {
		if (_cachedSecretsURL != null) {
			return _cachedSecretsURL;
		}

		String cachedSecretsURL;

		try {
			cachedSecretsURL = JenkinsResultsParserUtil.getBuildProperty(
				"one.password.cached.secrets.url");

			if (JenkinsResultsParserUtil.isNullOrEmpty(cachedSecretsURL)) {
				cachedSecretsURL = "";
			}
		}
		catch (IOException ioException) {
			cachedSecretsURL = "";
		}

		_cachedSecretsURL = cachedSecretsURL;

		return _cachedSecretsURL;
	}

	private static synchronized String _getConnectURL() {
		if (_connectURL != null) {
			return _connectURL;
		}

		String connectURL;

		try {
			String connectURLKey = JenkinsResultsParserUtil.getBuildProperty(
				"one.password.connect.url.key");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(connectURLKey)) {
				connectURL = _getSSMParameterValue(connectURLKey);
			}
			else {
				connectURL = "";
			}
		}
		catch (IOException | TimeoutException exception) {
			connectURL = "";
		}

		if (JenkinsResultsParserUtil.isURL(connectURL)) {
			JenkinsResultsParserUtil.addRedactToken(connectURL);

			_connectURL = connectURL;

			return _connectURL;
		}

		try {
			connectURL = JenkinsResultsParserUtil.getBuildProperty(
				"one.password.connect.url");

			if (!JenkinsResultsParserUtil.isURL(connectURL)) {
				connectURL = "";
			}
		}
		catch (IOException ioException) {
			connectURL = "";
		}

		_connectURL = connectURL;

		return _connectURL;
	}

	private static synchronized HTTPAuthorization _getHTTPAuthorization() {
		if (_httpAuthorization != null) {
			return _httpAuthorization;
		}

		String accessToken = _getAccessToken();

		if (JenkinsResultsParserUtil.isNullOrEmpty(accessToken)) {
			return null;
		}

		_httpAuthorization = new BearerHTTPAuthorization(accessToken);

		return _httpAuthorization;
	}

	private static String _getSecretFromConnect(
		String vaultName, String itemTitle, String fieldLabel) {

		if (!_isSecretsConfigured()) {
			return null;
		}

		Vault vault = Vault.getInstance(vaultName);

		if (vault == null) {
			System.out.println("Unable to find vault " + vaultName);

			return null;
		}

		Item item = vault.getItem(itemTitle);

		if (item == null) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Unable to find item ", vaultName, "/", itemTitle));

			return null;
		}

		String itemId = item.getId();

		int secretRetriesMax = _getSecretRetriesMax();

		try {
			for (int i = 0; i <= secretRetriesMax; i++) {
				if (i > 0) {
					JenkinsResultsParserUtil.sleep(
						_getSecretRetryPeriodSeconds() * 1000L);

					item.refresh();
				}

				try {
					ItemField itemField = item.getItemField(fieldLabel);

					if (itemField != null) {
						String itemFieldValue = itemField.getValue();

						if (!JenkinsResultsParserUtil.isNullOrEmpty(
								itemFieldValue)) {

							String itemFieldId = itemField.getId();
							String itemFieldLabel = itemField.getLabel();

							_secrets.put(
								_getSecretReference(
									vaultName, itemId, itemFieldId),
								itemFieldValue);
							_secrets.put(
								_getSecretReference(
									vaultName, itemId, itemFieldLabel),
								itemFieldValue);
							_secrets.put(
								_getSecretReference(
									vaultName, itemTitle, itemFieldId),
								itemFieldValue);
							_secrets.put(
								_getSecretReference(
									vaultName, itemTitle, itemFieldLabel),
								itemFieldValue);

							return itemFieldValue;
						}
					}

					ItemFile itemFile = item.getItemFile(fieldLabel);

					if (itemFile != null) {
						String itemFileValue = itemFile.getValue();

						if (!JenkinsResultsParserUtil.isNullOrEmpty(
								itemFileValue)) {

							String itemFileName = itemFile.getName();

							_secrets.put(
								_getSecretReference(
									vaultName, itemId, itemFileName),
								itemFileValue);
							_secrets.put(
								_getSecretReference(
									vaultName, itemTitle, itemFileName),
								itemFileValue);

							return itemFileValue;
						}
					}
				}
				catch (Exception exception) {
				}
			}

			return null;
		}
		finally {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Loaded ", String.valueOf(_secrets.size()),
					" secrets from ", _getConnectURL()));
		}
	}

	private static String _getSecretReference(
		String vaultName, String itemTitle, String fieldLabel) {

		return JenkinsResultsParserUtil.combine(
			"op://", vaultName, "/", itemTitle, "/", fieldLabel);
	}

	private static synchronized int _getSecretRetriesMax() {
		if (_secretRetriesMax != null) {
			return _secretRetriesMax;
		}

		int secretRetriesMax;

		try {
			String value = JenkinsResultsParserUtil.getBuildProperty(
				"one.password.secret.retries.max");

			if (JenkinsResultsParserUtil.isInteger(value)) {
				secretRetriesMax = Integer.parseInt(value);
			}
			else {
				secretRetriesMax = _SECRET_RETRIES_MAX_DEFAULT;
			}
		}
		catch (IOException | NumberFormatException exception) {
			secretRetriesMax = _SECRET_RETRIES_MAX_DEFAULT;
		}

		_secretRetriesMax = secretRetriesMax;

		return _secretRetriesMax;
	}

	private static synchronized long _getSecretRetryPeriodSeconds() {
		if (_secretRetryPeriodSeconds != null) {
			return _secretRetryPeriodSeconds;
		}

		long secretRetryPeriodSeconds;

		try {
			String value = JenkinsResultsParserUtil.getBuildProperty(
				"one.password.secret.retry.period.seconds");

			if (JenkinsResultsParserUtil.isInteger(value)) {
				secretRetryPeriodSeconds = Long.parseLong(value);
			}
			else {
				secretRetryPeriodSeconds = _SECRET_RETRY_PERIOD_SECONDS_DEFAULT;
			}
		}
		catch (IOException | NumberFormatException exception) {
			secretRetryPeriodSeconds = _SECRET_RETRY_PERIOD_SECONDS_DEFAULT;
		}

		_secretRetryPeriodSeconds = secretRetryPeriodSeconds;

		return _secretRetryPeriodSeconds;
	}

	private static String _getSSMParameterValue(String parameterName)
		throws IOException, TimeoutException {

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			new File("."), true, false, 60000,
			JenkinsResultsParserUtil.combine(
				"aws ssm get-parameter --name \"", parameterName,
				"\" --with-decryption | jq -r .Parameter.Value"));

		String value = JenkinsResultsParserUtil.readInputStream(
			process.getInputStream());

		value = value.replace("Finished executing Bash commands.", "");

		return value.trim();
	}

	private static boolean _isSecretsConfigured() {
		if (JenkinsResultsParserUtil.isNullOrEmpty(_getAccessToken()) ||
			JenkinsResultsParserUtil.isNullOrEmpty(_getConnectURL())) {

			return false;
		}

		return true;
	}

	private static synchronized void _loadCachedSecrets() {
		_cachedSecretsLoaded = true;

		try {
			String cachedSecretsContent = _getCachedSecretsContent();

			if (JenkinsResultsParserUtil.isNullOrEmpty(cachedSecretsContent)) {
				return;
			}

			PrivateKey cachedSecretsPrivateKey = _getCachedSecretsPrivateKey();

			if (cachedSecretsPrivateKey == null) {
				return;
			}

			cachedSecretsContent = _decrypt(
				cachedSecretsContent, cachedSecretsPrivateKey);

			if (JenkinsResultsParserUtil.isNullOrEmpty(cachedSecretsContent)) {
				return;
			}

			JSONObject jsonObject = new JSONObject(cachedSecretsContent);

			_cachedSecrets = new HashMap<>();

			for (String key : jsonObject.keySet()) {
				String value = jsonObject.getString(key);

				if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
					continue;
				}

				_cachedSecrets.put(key, value);

				JenkinsResultsParserUtil.addRedactToken(value);
			}

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Loaded ", String.valueOf(_cachedSecrets.size()),
					" secrets from ", _getCachedSecretsURL()));
		}
		catch (Exception exception) {
			_cachedSecrets = null;
		}
	}

	private static JSONArray _toJSONArray(String path) {
		if (!_isSecretsConfigured()) {
			return new JSONArray();
		}

		try {
			return JenkinsResultsParserUtil.toJSONArray(
				_getConnectURL() + path, null, _getHTTPAuthorization());
		}
		catch (IOException ioException) {
			System.out.println(ioException.getMessage());

			ioException.printStackTrace();

			return new JSONArray();
		}
	}

	private static JSONObject _toJSONObject(String path) {
		if (!_isSecretsConfigured()) {
			return new JSONObject();
		}

		try {
			return JenkinsResultsParserUtil.toJSONObject(
				_getConnectURL() + path, null, _getHTTPAuthorization());
		}
		catch (IOException ioException) {
			System.out.println(ioException.getMessage());

			ioException.printStackTrace();

			return null;
		}
	}

	private static String _toString(String path) {
		if (!_isSecretsConfigured()) {
			return "";
		}

		try {
			return JenkinsResultsParserUtil.toString(
				_getConnectURL() + path, null, _getHTTPAuthorization());
		}
		catch (IOException ioException) {
			System.out.println(ioException.getMessage());

			ioException.printStackTrace();

			return "";
		}
	}

	private static final String _CACHE_CIPHER = "RSA-OAEP+AES-GCM";

	private static final int _SECRET_RETRIES_MAX_DEFAULT = 3;

	private static final long _SECRET_RETRY_PERIOD_SECONDS_DEFAULT = 5;

	private static String _accessToken;
	private static Map<String, String> _cachedSecrets;
	private static String _cachedSecretsContent;
	private static boolean _cachedSecretsLoaded;
	private static PrivateKey _cachedSecretsPrivateKey;
	private static boolean _cachedSecretsPrivateKeyInitialized;
	private static String _cachedSecretsPrivateKeyPEM;
	private static PublicKey _cachedSecretsPublicKey;
	private static boolean _cachedSecretsPublicKeyInitialized;
	private static String _cachedSecretsPublicKeyPEM;
	private static String _cachedSecretsURL;
	private static String _connectURL;
	private static BearerHTTPAuthorization _httpAuthorization;
	private static final Pattern _secretReferencePattern = Pattern.compile(
		"op://(?<vaultName>[^/]*)/(?<itemTitle>[^/]*)/(?<fieldLabel>.*)");
	private static Integer _secretRetriesMax;
	private static Long _secretRetryPeriodSeconds;
	private static final Map<String, String> _secrets =
		new ConcurrentHashMap<>();

	private static class Item {

		public String getId() {
			return _id;
		}

		public ItemField getItemField(String label) {
			List<ItemField> itemFields;

			synchronized (_vault) {
				if (_itemFields == null) {
					_init();
				}

				itemFields = _itemFields;
			}

			for (ItemField itemField : itemFields) {
				if (Objects.equals(itemField.getId(), label) ||
					Objects.equals(itemField.getLabel(), label)) {

					return itemField;
				}
			}

			if (_linkedItem != null) {
				return _linkedItem.getItemField(label);
			}

			return null;
		}

		public List<ItemField> getItemFields() {
			synchronized (_vault) {
				if (_itemFields == null) {
					_init();
				}

				return _itemFields;
			}
		}

		public ItemFile getItemFile(String fileName) {
			List<ItemFile> itemFiles;

			synchronized (_vault) {
				if (_itemFiles == null) {
					_init();
				}

				itemFiles = _itemFiles;
			}

			for (ItemFile itemFile : itemFiles) {
				if (Objects.equals(itemFile.getName(), fileName)) {
					return itemFile;
				}
			}

			if (_linkedItem != null) {
				return _linkedItem.getItemFile(fileName);
			}

			return null;
		}

		public List<ItemFile> getItemFiles() {
			synchronized (_vault) {
				if (_itemFiles == null) {
					_init();
				}

				return _itemFiles;
			}
		}

		public String getTitle() {
			return _title;
		}

		public void load() {
			synchronized (_vault) {
				if (_itemFields == null) {
					_init();
				}
			}
		}

		public void refresh() {
			synchronized (_vault) {
				_itemFields = null;
				_itemFiles = null;
				_linkedItem = null;
			}
		}

		private Item(String id, String title, Vault vault) {
			_id = id;
			_title = title;
			_vault = vault;
		}

		private void _init() {
			JSONObject itemJSONObject = _toJSONObject(
				JenkinsResultsParserUtil.combine(
					"/v1/vaults/", _vault.getId(), "/items/", getId()));

			JSONArray fieldsJSONArray = itemJSONObject.getJSONArray("fields");

			_itemFields = new ArrayList<>(fieldsJSONArray.length());

			for (int i = 0; i < fieldsJSONArray.length(); i++) {
				JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(i);

				try {
					JSONObject sectionJSONObject =
						fieldJSONObject.optJSONObject("section");

					if (sectionJSONObject != null) {
						if (Objects.equals(
								sectionJSONObject.optString("label"),
								"Related Items")) {

							_linkedItem = _vault.getItem(
								fieldJSONObject.getString("label"));
						}

						if (_linkedItem != null) {
							continue;
						}
					}

					if (!fieldJSONObject.has("value")) {
						continue;
					}

					_itemFields.add(
						new ItemField(
							fieldJSONObject.getString("id"),
							fieldJSONObject.getString("label"),
							fieldJSONObject.getString("value")));
				}
				catch (JSONException jsonException) {
					System.err.println(jsonException.toString());
					System.out.println(fieldJSONObject.toString(2));
				}
			}

			JSONArray filesJSONArray = itemJSONObject.optJSONArray(
				"files", new JSONArray());

			_itemFiles = new ArrayList<>(filesJSONArray.length());

			for (int i = 0; i < filesJSONArray.length(); i++) {
				JSONObject fileJSONObject = filesJSONArray.getJSONObject(i);

				try {
					JSONObject sectionJSONObject = fileJSONObject.optJSONObject(
						"section");

					if (sectionJSONObject != null) {
						if (Objects.equals(
								sectionJSONObject.optString("label"),
								"Related Items")) {

							_linkedItem = _vault.getItem(
								fileJSONObject.getString("label"));
						}

						if (_linkedItem != null) {
							continue;
						}
					}

					if (!fileJSONObject.has("content_path")) {
						continue;
					}

					_itemFiles.add(
						new ItemFile(
							fileJSONObject.getString("content_path"),
							fileJSONObject.getString("name")));
				}
				catch (JSONException jsonException) {
					System.err.println(jsonException.toString());
					System.out.println(fileJSONObject.toString(2));
				}
			}
		}

		private final String _id;
		private List<ItemField> _itemFields;
		private List<ItemFile> _itemFiles;
		private Item _linkedItem;
		private final String _title;
		private final Vault _vault;

	}

	private static class ItemField {

		public String getId() {
			return _id;
		}

		public String getLabel() {
			return _label;
		}

		public String getValue() {
			return _value;
		}

		private ItemField(String id, String label, String value) {
			_id = id;
			_label = label;
			_value = value;

			if (!JenkinsResultsParserUtil.isNullOrEmpty(value)) {
				JenkinsResultsParserUtil.addRedactToken(value);
			}
		}

		private final String _id;
		private final String _label;
		private final String _value;

	}

	private static class ItemFile {

		public ItemFile(String contentPath, String name) {
			_contentPath = contentPath;
			_name = name;
		}

		public String getName() {
			return _name;
		}

		public String getValue() {
			if (_value != null) {
				return _value;
			}

			String value = _toString(_contentPath);

			value = value.trim();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(value)) {
				JenkinsResultsParserUtil.addRedactToken(value);
			}

			_value = value;

			return _value;
		}

		private final String _contentPath;
		private final String _name;
		private String _value;

	}

	private static class Vault {

		public static Vault getInstance(String name) {
			Map<String, Vault> vaultsMap = _getVaultsMap();

			return vaultsMap.get(name);
		}

		public static List<Vault> getInstances() {
			Map<String, Vault> vaultsMap = _getVaultsMap();

			List<Vault> vaults = new ArrayList<>();

			for (Vault vault : vaultsMap.values()) {
				if (!vaults.contains(vault)) {
					vaults.add(vault);
				}
			}

			return vaults;
		}

		public String getId() {
			return _id;
		}

		public Item getItem(String title) {
			synchronized (_vaultsMap) {
				if (_items == null) {
					_init();
				}
			}

			for (Item item : _items) {
				if (Objects.equals(item.getId(), title) ||
					Objects.equals(item.getTitle(), title)) {

					return item;
				}
			}

			return null;
		}

		public List<Item> getItems() {
			synchronized (_vaultsMap) {
				if (_items == null) {
					_init();
				}
			}

			return _items;
		}

		public String getName() {
			return _name;
		}

		public void loadAllItems() {
			synchronized (_vaultsMap) {
				if (_allItemsLoaded) {
					return;
				}

				_allItemsLoaded = true;

				if (_items == null) {
					_init();
				}
			}

			for (Item item : _items) {
				item.load();
			}
		}

		private static Map<String, Vault> _getVaultsMap() {
			synchronized (_vaultsMap) {
				if (!_vaultsMap.isEmpty()) {
					return _vaultsMap;
				}

				JSONArray vaultsJSONArray = _toJSONArray("/v1/vaults");

				for (int i = 0; i < vaultsJSONArray.length(); i++) {
					JSONObject vaultJSONObject = vaultsJSONArray.getJSONObject(
						i);

					Vault vault = new Vault(
						vaultJSONObject.getString("id"),
						vaultJSONObject.getString("name"));

					_vaultsMap.put(vault.getId(), vault);
					_vaultsMap.put(vault.getName(), vault);

					vault.loadAllItems();
				}
			}

			return _vaultsMap;
		}

		private Vault(String id, String name) {
			_id = id;
			_name = name;
		}

		private synchronized void _init() {
			JSONArray itemsJSONArray = _toJSONArray(
				JenkinsResultsParserUtil.combine(
					"/v1/vaults/", getId(), "/items"));

			_items = new ArrayList<>(itemsJSONArray.length());

			for (int i = 0; i < itemsJSONArray.length(); i++) {
				JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

				_items.add(
					new Item(
						itemJSONObject.getString("id"),
						itemJSONObject.getString("title"), this));
			}
		}

		private static final Map<String, Vault> _vaultsMap = new HashMap<>();

		private boolean _allItemsLoaded;
		private final String _id;
		private List<Item> _items;
		private final String _name;

	}

}
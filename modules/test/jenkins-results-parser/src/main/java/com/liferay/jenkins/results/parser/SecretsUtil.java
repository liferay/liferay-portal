/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.BearerHTTPAuthorization;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HTTPAuthorization;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HttpRequestMethod;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Peter Yoo
 */
public abstract class SecretsUtil {

	public static void generateJenkinsAPITokenSecret(String key) {
		Matcher matcher = _itemReferencePattern.matcher(key);

		if (!matcher.matches()) {
			throw new RuntimeException("Invalid item reference " + key);
		}

		String vaultName = matcher.group("vaultName");

		Vault vault = Vault.getInstance(vaultName);

		if (vault == null) {
			throw new RuntimeException("Unable to find vault " + vaultName);
		}

		String itemTitle = matcher.group("itemTitle");

		if (vault.getItem(itemTitle) != null) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Item ", itemTitle, " already exists in vault ",
					vaultName));
		}

		_createItem(vault, itemTitle, _generateJenkinsAPITokenMap());
	}

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

		String secret = _connectSecrets.get(secretReference);

		if (secret != null) {
			return secret;
		}

		_loadConnectSecrets();

		secret = _connectSecrets.get(secretReference);

		if (secret == null) {
			System.out.println("Unable to find secret " + secretReference);
		}

		return secret;
	}

	public static boolean isSecretProperty(String value) {
		if (value == null) {
			return false;
		}

		Matcher matcher = _secretReferencePattern.matcher(value);

		return matcher.matches();
	}

	private static void _createItem(
		Vault vault, String itemTitle, Map<String, String> itemFieldsMap) {

		JSONArray fieldsJSONArray = new JSONArray();

		for (Map.Entry<String, String> itemFieldEntry :
				itemFieldsMap.entrySet()) {

			fieldsJSONArray.put(
				_getItemFieldJSONObject(
					itemFieldEntry.getKey(), itemFieldEntry.getValue()));
		}

		JSONObject itemJSONObject = _toJSONObject(
			JenkinsResultsParserUtil.combine(
				"/v1/vaults/", vault.getId(), "/items"),
			HttpRequestMethod.POST,
			String.valueOf(
				new JSONObject(
				).put(
					"category", Item.Category.SECURE_NOTE
				).put(
					"fields", fieldsJSONArray
				).put(
					"title", itemTitle
				).put(
					"vault",
					new JSONObject(
					).put(
						"id", vault.getId()
					)
				)));

		if ((itemJSONObject == null) || !itemJSONObject.has("id")) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to create item ", itemTitle, " in vault ",
					vault.getSecretReference()));
		}

		Item item = new Item(itemJSONObject.getString("id"), itemTitle, vault);

		vault.addItem(item);

		for (Map.Entry<String, String> entry : itemFieldsMap.entrySet()) {
			ItemField itemField = new ItemField(
				"", entry.getKey(), entry.getValue());

			item.addItemField(itemField);

			_loadItemField(vault, item, itemField);
		}
	}

	private static Map<String, String> _generateJenkinsAPITokenMap() {
		byte[] randomBytes = new byte[16];

		SecureRandom secureRandom = new SecureRandom();

		secureRandom.nextBytes(randomBytes);

		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance(_API_TOKEN_ALGORITHM);
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RuntimeException(
				"Unable to generate API token hash", noSuchAlgorithmException);
		}

		String secretValue = _toHexString(randomBytes);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS z");

		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		UUID uuid = UUID.randomUUID();

		Map<String, String> apiTokenMap = new LinkedHashMap<>();

		apiTokenMap.put("creationDate", simpleDateFormat.format(new Date()));
		apiTokenMap.put(
			"hash",
			_toHexString(
				messageDigest.digest(
					secretValue.getBytes(StandardCharsets.US_ASCII))));
		apiTokenMap.put("plainValue", _API_TOKEN_VERSION + secretValue);
		apiTokenMap.put("uuid", uuid.toString());
		apiTokenMap.put("version", _API_TOKEN_VERSION);

		return apiTokenMap;
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

	private static JSONObject _getItemFieldJSONObject(
		String fieldLabel, String fieldValue) {

		return new JSONObject(
		).put(
			"label", fieldLabel
		).put(
			"type", ItemField.Type.CONCEALED
		).put(
			"value", fieldValue
		);
	}

	private static String _getSecretReference(
		String vaultName, String itemTitle, String fieldLabel) {

		return JenkinsResultsParserUtil.combine(
			"op://", vaultName, "/", itemTitle, "/", fieldLabel);
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

	private static synchronized void _loadConnectSecrets() {
		if (_connectSecretsLoaded) {
			return;
		}

		if (!_isSecretsConfigured()) {
			_connectSecretsLoaded = true;

			return;
		}

		try {
			for (Vault vault : Vault.getInstances()) {
				for (Item item : vault.getItems()) {
					for (ItemField itemField : item.getItemFields()) {
						_loadItemField(vault, item, itemField);
					}

					for (ItemFile itemFile : item.getItemFiles()) {
						_loadItemFile(vault, item, itemFile);
					}
				}
			}

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Loaded ", String.valueOf(_connectSecrets.size()),
					" connect secrets from ", _getConnectURL()));

			_connectSecretsLoaded = true;
		}
		catch (Exception exception) {
			exception.printStackTrace();

			_connectSecrets.clear();
		}
	}

	private static void _loadItemField(
		Vault vault, Item item, ItemField itemField) {

		String itemFieldValue = itemField.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(itemFieldValue)) {
			return;
		}

		String vaultName = vault.getName();

		String itemId = item.getId();
		String itemTitle = item.getTitle();

		String itemFieldId = itemField.getId();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(itemFieldId)) {
			_connectSecrets.put(
				_getSecretReference(vaultName, itemId, itemFieldId),
				itemFieldValue);
			_connectSecrets.put(
				_getSecretReference(vaultName, itemTitle, itemFieldId),
				itemFieldValue);
		}

		String itemFieldLabel = itemField.getLabel();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(itemFieldLabel)) {
			_connectSecrets.put(
				_getSecretReference(vaultName, itemId, itemFieldLabel),
				itemFieldValue);
			_connectSecrets.put(
				_getSecretReference(vaultName, itemTitle, itemFieldLabel),
				itemFieldValue);
		}
	}

	private static void _loadItemFile(
		Vault vault, Item item, ItemFile itemFile) {

		String itemFieldValue = itemFile.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(itemFieldValue)) {
			return;
		}

		String vaultName = vault.getName();

		String itemId = item.getId();
		String itemTitle = item.getTitle();

		String itemFileName = itemFile.getName();

		_connectSecrets.put(
			_getSecretReference(vaultName, itemId, itemFileName),
			itemFieldValue);
		_connectSecrets.put(
			_getSecretReference(vaultName, itemTitle, itemFileName),
			itemFieldValue);
	}

	private static String _toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (byte b : bytes) {
			sb.append(String.format("%02x", b & 0xff));
		}

		return sb.toString();
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

	private static JSONObject _toJSONObject(
		String path, HttpRequestMethod httpRequestMethod, String postContent) {

		if (!_isSecretsConfigured()) {
			throw new RuntimeException("Secrets are not configured");
		}

		try {
			return JenkinsResultsParserUtil.toJSONObject(
				_getConnectURL() + path, false, 0, httpRequestMethod,
				postContent, 0, 0, _getHTTPAuthorization());
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to send " + httpRequestMethod + " to " + path,
				ioException);
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

	private static final String _API_TOKEN_ALGORITHM = "SHA-256";

	private static final String _API_TOKEN_VERSION = "11";

	private static String _accessToken;
	private static final Map<String, String> _connectSecrets =
		new ConcurrentHashMap<>();
	private static boolean _connectSecretsLoaded;
	private static String _connectURL;
	private static BearerHTTPAuthorization _httpAuthorization;
	private static final Pattern _itemReferencePattern = Pattern.compile(
		"op://(?<vaultName>[^/]*)/(?<itemTitle>[^/]*)");
	private static final Pattern _secretReferencePattern = Pattern.compile(
		"op://(?<vaultName>[^/]*)/(?<itemTitle>[^/]*)/(?<fieldLabel>.*)");

	private static class Item {

		public void addItemField(ItemField itemField) {
			synchronized (_vault) {
				if (_itemFields == null) {
					return;
				}

				_itemFields.add(itemField);
			}
		}

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

		private static enum Category {

			API_CREDENTIAL, BANK_ACCOUNT, CREDIT_CARD, CUSTOM, DATABASE,
			DOCUMENT, DRIVER_LICENSE, EMAIL_ACCOUNT, IDENTITY, LOGIN,
			MEDICAL_RECORD, MEMBERSHIP, OUTDOOR_LICENSE, PASSPORT, PASSWORD,
			REWARD_PROGRAM, SECURE_NOTE, SERVER, SOCIAL_SECURITY_NUMBER,
			SOFTWARE_LICENSE, SSH_KEY, WIRELESS_ROUTER

		}

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

		private static enum Type {

			ADDRESS, CONCEALED, CREDIT_CARD_NUMBER, CREDIT_CARD_TYPE, DATE,
			EMAIL, GENDER, MENU, MONTH_YEAR, OTP, PHONE, REFERENCE, STRING, URL

		}

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

		public void addItem(Item item) {
			synchronized (_vaultsMap) {
				if (_items == null) {
					return;
				}

				_items.add(item);
			}
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

		public String getSecretReference() {
			return "op://" + getName();
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
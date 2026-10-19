/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HttpRequestMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.lang.reflect.Method;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

/**
 * @author Michael Hashimoto
 */
public class SecretsUtilTest extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_resetSecretsUtil();

		_printStream = System.out;

		System.setOut(new PrintStream(_byteArrayOutputStream));

		mockURLReaders();
	}

	@After
	@Override
	public void tearDown() {
		System.setOut(_printStream);

		super.tearDown();

		_resetSecretsUtil();
	}

	@Test
	public void testGenerateJenkinsAPITokenSecret() throws Exception {
		String connectURL = _setUpConnect();

		String fieldLabel = RandomTestUtil.randomString();
		String itemId = RandomTestUtil.randomString();
		String itemTitle = RandomTestUtil.randomString();

		JSONObject itemJSONObject = _newItemJSONObject(
			new JSONArray(
			).put(
				_newFieldJSONObject(
					RandomTestUtil.randomString(), fieldLabel,
					RandomTestUtil.randomString())
			),
			new JSONArray(), itemId, itemTitle);

		String vaultId = RandomTestUtil.randomString();
		String vaultName = RandomTestUtil.randomString();

		_setVaultsOutput(
			connectURL,
			_newVaultJSONObject(vaultId, vaultName, itemJSONObject));

		List<String> postContents = new ArrayList<>();

		for (BaseURLReader<?> baseURLReader : getBaseURLReaders()) {
			Mockito.doAnswer(
				invocation -> {
					if (invocation.getArgument(2) == HttpRequestMethod.PUT) {
						postContents.add(invocation.getArgument(3));
					}

					return mockURLConnection(itemJSONObject.toString(), 200);
				}
			).when(
				baseURLReader
			).openURLConnection(
				Mockito.any(), Mockito.anyBoolean(), Mockito.any(),
				Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.argThat(
					readURL ->
						(readURL != null) &&
						readURL.endsWith("/items/" + itemId))
			);
		}

		TimeZone timeZone = TimeZone.getDefault();

		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));

		try {
			SecretsUtil.generateJenkinsAPITokenSecret(
				JenkinsResultsParserUtil.combine(
					"op://", vaultName, "/", itemTitle));
		}
		finally {
			TimeZone.setDefault(timeZone);
		}

		testEquals(1, postContents.size());

		JSONObject postJSONObject = new JSONObject(postContents.get(0));

		JSONArray fieldsJSONArray = postJSONObject.getJSONArray("fields");

		testEquals(2, fieldsJSONArray.length());

		JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(0);

		testEquals(fieldLabel, fieldJSONObject.getString("label"));

		JSONObject apiTokenFieldJSONObject = fieldsJSONArray.getJSONObject(1);

		String apiTokenFieldLabel = apiTokenFieldJSONObject.getString("label");

		Assert.assertTrue(
			apiTokenFieldLabel,
			apiTokenFieldLabel.matches(
				"api\\.token\\.json\\.\\d{4}-\\d{2}-\\d{2}_\\d{2}:\\d{2}:" +
					"\\d{2}\\.\\d{3}"));

		testEquals("CONCEALED", apiTokenFieldJSONObject.getString("type"));

		JSONObject apiTokenJSONObject = new JSONObject(
			apiTokenFieldJSONObject.getString("value"));

		String apiToken = apiTokenJSONObject.getString("api.token");

		Assert.assertTrue(apiToken, apiToken.matches("11[0-9a-f]{32}"));

		String apiTokenCreationDate = apiTokenJSONObject.getString(
			"api.token.creation.date");

		SimpleDateFormat apiTokenFieldLabelSimpleDateFormat =
			new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");

		apiTokenFieldLabelSimpleDateFormat.setTimeZone(
			TimeZone.getTimeZone("America/Los_Angeles"));

		SimpleDateFormat apiTokenCreationDateSimpleDateFormat =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");

		apiTokenCreationDateSimpleDateFormat.setTimeZone(
			TimeZone.getTimeZone("UTC"));

		testEquals(
			apiTokenFieldLabelSimpleDateFormat.parse(
				apiTokenFieldLabel.substring("api.token.json.".length())),
			apiTokenCreationDateSimpleDateFormat.parse(apiTokenCreationDate));

		Assert.assertTrue(
			apiTokenCreationDate, apiTokenCreationDate.endsWith(" UTC"));

		StringBuilder sb = new StringBuilder();

		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		String apiTokenHash = apiToken.substring(2);

		byte[] apiTokenHashBytes = apiTokenHash.getBytes(
			StandardCharsets.US_ASCII);

		for (byte b : messageDigest.digest(apiTokenHashBytes)) {
			sb.append(String.format("%02x", b & 0xff));
		}

		testEquals(
			"API Token - " + apiTokenCreationDate,
			apiTokenJSONObject.getString("api.token.name"));
		testEquals(
			sb.toString(), apiTokenJSONObject.getString("api.token.hash"));

		String apiTokenUUID = apiTokenJSONObject.getString("api.token.uuid");

		testEquals("11", apiTokenJSONObject.getString("api.token.version"));
		testEquals(apiTokenUUID, String.valueOf(UUID.fromString(apiTokenUUID)));
	}

	@Test
	public void testGenerateJenkinsAPITokenSecretFailure() throws Exception {
		String connectURL = _setUpConnect();

		String vaultName = RandomTestUtil.randomString();

		_setVaultsOutput(
			connectURL,
			_newVaultJSONObject(
				RandomTestUtil.randomString(), vaultName,
				_newItemJSONObject(
					new JSONArray(), new JSONArray(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())));

		String invalidReference = RandomTestUtil.randomString();

		_testGenerateJenkinsAPITokenSecretFailure(
			"Invalid item reference " + invalidReference, invalidReference);

		String itemTitle = RandomTestUtil.randomString();
		String unknownVaultName = RandomTestUtil.randomString();

		_testGenerateJenkinsAPITokenSecretFailure(
			"Unable to find vault " + unknownVaultName,
			JenkinsResultsParserUtil.combine(
				"op://", unknownVaultName, "/", itemTitle));

		_testGenerateJenkinsAPITokenSecretFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to find item ", itemTitle, " in vault ", vaultName),
			JenkinsResultsParserUtil.combine(
				"op://", vaultName, "/", itemTitle));
	}

	@Test
	public void testGetConnectURL() throws Exception {
		_testGetConnectURL("http://" + RandomTestUtil.randomString(), false);
		_testGetConnectURL(RandomTestUtil.randomString(), true);
		_testGetConnectURL(null, true);
	}

	@Test
	public void testGetItem() throws Exception {
		String connectURL = _setUpConnect();

		String itemTitle = RandomTestUtil.randomString();
		String vaultName = RandomTestUtil.randomString();

		_setVaultsOutput(
			connectURL,
			_newVaultJSONObject(
				RandomTestUtil.randomString(), vaultName,
				_newItemJSONObject(
					new JSONArray(), new JSONArray(),
					RandomTestUtil.randomString(), itemTitle)));

		SecretsUtil.Item item = SecretsUtil.getItem(
			JenkinsResultsParserUtil.combine(
				"op://", vaultName, "/", itemTitle, "//"));

		testEquals(itemTitle, item.getTitle());

		String invalidReference = RandomTestUtil.randomString();

		_testGetItemFailure(
			"Invalid item reference " + invalidReference, invalidReference);

		String unknownItemTitle = RandomTestUtil.randomString();

		_testGetItemFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to find item ", unknownItemTitle, " in vault ",
				vaultName),
			JenkinsResultsParserUtil.combine(
				"op://", vaultName, "/", unknownItemTitle));

		String unknownVaultName = RandomTestUtil.randomString();

		_testGetItemFailure(
			"Unable to find vault " + unknownVaultName,
			JenkinsResultsParserUtil.combine(
				"op://", unknownVaultName, "/", itemTitle));
	}

	@Test
	public void testGetSecret() throws Exception {
		String accessToken = RandomTestUtil.randomString();

		String connectURL = _setUpConnect(accessToken);

		String fieldId = RandomTestUtil.randomString();
		String fieldLabel = RandomTestUtil.randomString();
		String fieldValue = RandomTestUtil.randomString();
		String fileName = RandomTestUtil.randomString();
		String fileValue = RandomTestUtil.randomString();
		String itemId = RandomTestUtil.randomString();
		String itemTitle = RandomTestUtil.randomString();
		String vaultId = RandomTestUtil.randomString();
		String vaultName = RandomTestUtil.randomString();

		_setVaultsOutput(
			connectURL,
			_newVaultJSONObject(
				vaultId, vaultName,
				_newItemJSONObject(
					new JSONArray(
					).put(
						_newFieldJSONObject(fieldId, fieldLabel, fieldValue)
					),
					new JSONArray(
					).put(
						_newFileJSONObject(fileValue, fileName)
					),
					itemId, itemTitle)));

		for (String itemReference : new String[] {itemId, itemTitle}) {
			for (String fieldReference : new String[] {fieldId, fieldLabel}) {
				testEquals(
					fieldValue,
					SecretsUtil.getSecret(
						JenkinsResultsParserUtil.combine(
							"op://", vaultName, "/", itemReference, "/",
							fieldReference)));
			}

			testEquals(
				fileValue,
				SecretsUtil.getSecret(
					JenkinsResultsParserUtil.combine(
						"op://", vaultName, "/", itemReference, "/",
						fileName)));
		}

		String absentReference = JenkinsResultsParserUtil.combine(
			"op://", vaultName, "/", itemTitle, "/",
			RandomTestUtil.randomString());

		testEquals(absentReference, SecretsUtil.getSecret(absentReference));

		JSONObject itemJSONObject = _newItemJSONObject(
			new JSONArray(
			).put(
				_newFieldJSONObject(
					fieldId, fieldLabel, RandomTestUtil.randomString())
			),
			new JSONArray(), itemId, itemTitle);

		setURLReaderOutput(
			itemJSONObject.toString(),
			connectURL + "/v1/vaults/" + vaultId + "/items/" + itemId);

		testEquals(
			fieldValue,
			SecretsUtil.getSecret(
				JenkinsResultsParserUtil.combine(
					"op://", vaultName, "/", itemTitle, "/", fieldLabel)));

		verifyURLReaderAttemptsCount(4, connectURL);

		List<String> authorizations = new ArrayList<>();

		for (BaseURLReader<?> baseURLReader : getBaseURLReaders()) {
			MockingDetails mockingDetails = Mockito.mockingDetails(
				baseURLReader);

			for (Invocation invocation : mockingDetails.getInvocations()) {
				Method method = invocation.getMethod();

				if (!Objects.equals(method.getName(), "openURLConnection")) {
					continue;
				}

				authorizations.add(invocation.getArgument(0));
			}
		}

		testEquals(
			Collections.nCopies(4, "Bearer " + accessToken), authorizations);

		String output = _byteArrayOutputStream.toString();

		Assert.assertFalse(output, output.contains(fieldValue));
		Assert.assertFalse(output, output.contains(fileValue));
		Assert.assertTrue(
			output,
			output.contains("Unable to find secret " + absentReference));

		String redactedContent = JenkinsResultsParserUtil.redact(
			JenkinsResultsParserUtil.combine(
				accessToken, " ", fieldValue, " ", fileValue));

		Assert.assertFalse(
			redactedContent, redactedContent.contains(accessToken));
		Assert.assertFalse(
			redactedContent, redactedContent.contains(fieldValue));
		Assert.assertFalse(
			redactedContent, redactedContent.contains(fileValue));
	}

	@Test
	public void testGetSecretLinkedItem() throws Exception {
		String connectURL = _setUpConnect();

		String fieldLabel = RandomTestUtil.randomString();
		String fieldValue = RandomTestUtil.randomString();
		String itemTitle = RandomTestUtil.randomString();
		String linkedFieldLabel = RandomTestUtil.randomString();
		String linkedFieldValue = RandomTestUtil.randomString();
		String linkedItemTitle = RandomTestUtil.randomString();
		String vaultName = RandomTestUtil.randomString();

		_setVaultsOutput(
			connectURL,
			_newVaultJSONObject(
				RandomTestUtil.randomString(), vaultName,
				_newItemJSONObject(
					new JSONArray(
					).put(
						_newFieldJSONObject(
							RandomTestUtil.randomString(), fieldLabel,
							fieldValue)
					).put(
						_newRelatedItemFieldJSONObject(linkedItemTitle)
					),
					new JSONArray(), RandomTestUtil.randomString(), itemTitle),
				_newItemJSONObject(
					new JSONArray(
					).put(
						_newFieldJSONObject(
							RandomTestUtil.randomString(), fieldLabel,
							RandomTestUtil.randomString())
					).put(
						_newFieldJSONObject(
							RandomTestUtil.randomString(), linkedFieldLabel,
							linkedFieldValue)
					).put(
						_newRelatedItemFieldJSONObject(itemTitle)
					),
					new JSONArray(), RandomTestUtil.randomString(),
					linkedItemTitle)));

		testEquals(
			fieldValue,
			SecretsUtil.getSecret(
				JenkinsResultsParserUtil.combine(
					"op://", vaultName, "/", itemTitle, "/", fieldLabel)));
		testEquals(
			linkedFieldValue,
			SecretsUtil.getSecret(
				JenkinsResultsParserUtil.combine(
					"op://", vaultName, "/", itemTitle, "/",
					linkedFieldLabel)));
	}

	@Test
	public void testGetSecretNoAccessToken() throws Exception {
		_testGetSecretNoAccessToken("");
		_testGetSecretNoAccessToken(null);
	}

	@Test
	public void testGetSecretPassThrough() throws Exception {
		_testGetSecretPassThrough("");
		_testGetSecretPassThrough("http://" + RandomTestUtil.randomString());
		_testGetSecretPassThrough("op:/" + RandomTestUtil.randomString());
		_testGetSecretPassThrough(RandomTestUtil.randomString());
		_testGetSecretPassThrough(null);

		testEquals("", _byteArrayOutputStream.toString());
	}

	@Test
	public void testGetSecretVaults() throws Exception {
		String connectURL = _setUpConnect();

		String fieldLabel = RandomTestUtil.randomString();
		String fieldValue1 = RandomTestUtil.randomString();
		String fieldValue2 = RandomTestUtil.randomString();
		String itemTitle = RandomTestUtil.randomString();
		String vaultName1 = RandomTestUtil.randomString();
		String vaultName2 = RandomTestUtil.randomString();

		_setVaultsOutput(
			connectURL,
			_newVaultJSONObject(
				RandomTestUtil.randomString(), vaultName1,
				_newItemJSONObject(
					new JSONArray(
					).put(
						_newFieldJSONObject(
							RandomTestUtil.randomString(), fieldLabel,
							fieldValue1)
					),
					new JSONArray(), RandomTestUtil.randomString(), itemTitle)),
			_newVaultJSONObject(
				RandomTestUtil.randomString(), vaultName2,
				_newItemJSONObject(
					new JSONArray(
					).put(
						_newFieldJSONObject(
							RandomTestUtil.randomString(), fieldLabel,
							fieldValue2)
					),
					new JSONArray(), RandomTestUtil.randomString(),
					itemTitle)));

		testEquals(
			fieldValue1,
			SecretsUtil.getSecret(
				JenkinsResultsParserUtil.combine(
					"op://", vaultName1, "/", itemTitle, "/", fieldLabel)));
		testEquals(
			fieldValue2,
			SecretsUtil.getSecret(
				JenkinsResultsParserUtil.combine(
					"op://", vaultName2, "/", itemTitle, "/", fieldLabel)));
	}

	@Test
	public void testIsSecretProperty() {
		_testIsSecretProperty(false, "http://" + RandomTestUtil.randomString());
		_testIsSecretProperty(false, "op");
		_testIsSecretProperty(
			false, "op://CI Secrets - Shared/" + RandomTestUtil.randomString());
		_testIsSecretProperty(false, RandomTestUtil.randomString() + "op");
		_testIsSecretProperty(false, null);
		_testIsSecretProperty(
			true,
			"op://CI Secrets - Shared/" + RandomTestUtil.randomString() +
				"/credential");
		_testIsSecretProperty(
			true,
			"op://CI Secrets/" + RandomTestUtil.randomString() +
				"/secret.access.key");
	}

	@Test
	public void testLoadConnectSecrets() throws Exception {
		String connectURL = _setUpConnect();

		IOException ioException = new IOException(
			"Server returned HTTP response code: 401");

		setURLReaderException(ioException, connectURL + "/v1/vaults");

		String fieldLabel = RandomTestUtil.randomString();
		String itemTitle = RandomTestUtil.randomString();
		String vaultName = RandomTestUtil.randomString();

		String reference = JenkinsResultsParserUtil.combine(
			"op://", vaultName, "/", itemTitle, "/", fieldLabel);

		testEquals(reference, SecretsUtil.getSecret(reference));

		String fieldValue = RandomTestUtil.randomString();
		JSONObject fileJSONObject = _newFileJSONObject(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
		String itemId = RandomTestUtil.randomString();
		String otherVaultId = RandomTestUtil.randomString();
		String vaultId = RandomTestUtil.randomString();

		_setVaultsOutput(
			connectURL,
			_newVaultJSONObject(otherVaultId, RandomTestUtil.randomString()),
			_newVaultJSONObject(
				vaultId, vaultName,
				_newItemJSONObject(
					new JSONArray(
					).put(
						_newFieldJSONObject(
							RandomTestUtil.randomString(), fieldLabel,
							fieldValue)
					),
					new JSONArray(
					).put(
						fileJSONObject
					),
					itemId, itemTitle)));

		String otherItemsURL = JenkinsResultsParserUtil.combine(
			connectURL, "/v1/vaults/", otherVaultId, "/items");

		setURLReaderException(ioException, otherItemsURL);

		testEquals(reference, SecretsUtil.getSecret(reference));

		setURLReaderOutput(String.valueOf(new JSONArray()), otherItemsURL);

		String fileURL = connectURL + fileJSONObject.getString("content_path");

		setURLReaderException(ioException, fileURL);

		testEquals(reference, SecretsUtil.getSecret(reference));

		setURLReaderOutput(fileJSONObject.getString("content"), fileURL);

		testEquals(fieldValue, SecretsUtil.getSecret(reference));
	}

	@Test
	public void testReplaceSecrets() throws Exception {
		String connectURL = _setUpConnect();

		String fieldLabel = RandomTestUtil.randomString();
		String fieldValue = RandomTestUtil.randomString() + "$1\\";
		String itemTitle = RandomTestUtil.randomString();
		String vaultName = RandomTestUtil.randomString();

		_setVaultsOutput(
			connectURL,
			_newVaultJSONObject(
				RandomTestUtil.randomString(), vaultName,
				_newItemJSONObject(
					new JSONArray(
					).put(
						_newFieldJSONObject(
							RandomTestUtil.randomString(), fieldLabel,
							fieldValue)
					),
					new JSONArray(), RandomTestUtil.randomString(),
					itemTitle)));

		String absentReference = JenkinsResultsParserUtil.combine(
			"op://", vaultName, "/", itemTitle, "/",
			RandomTestUtil.randomString());
		String reference = JenkinsResultsParserUtil.combine(
			"op://", vaultName, "/", itemTitle, "/", fieldLabel);

		String text = RandomTestUtil.randomString() + " $1 \\";

		testEquals(
			JenkinsResultsParserUtil.combine(
				"<a>", fieldValue, "</a>\n<b>", absentReference, "</b>\n", text,
				"\n<c>", fieldValue, "</c>"),
			SecretsUtil.replaceSecrets(
				JenkinsResultsParserUtil.combine(
					"<a>", reference, "</a>\n<b>", absentReference, "</b>\n",
					text, "\n<c>", reference, "</c>")));

		String output = _byteArrayOutputStream.toString();

		Assert.assertTrue(
			output,
			output.contains("Unable to find secret " + absentReference));

		testEquals(null, SecretsUtil.replaceSecrets(null));
		testEquals(text, SecretsUtil.replaceSecrets(text));
	}

	private JSONObject _newFieldJSONObject(
		String id, String label, String value) {

		return new JSONObject(
		).put(
			"id", id
		).put(
			"label", label
		).put(
			"value", value
		);
	}

	private JSONObject _newFileJSONObject(String content, String name) {
		return new JSONObject(
		).put(
			"content", content
		).put(
			"content_path", "/" + RandomTestUtil.randomString()
		).put(
			"name", name
		);
	}

	private JSONObject _newItemJSONObject(
		JSONArray fieldsJSONArray, JSONArray filesJSONArray, String id,
		String title) {

		return new JSONObject(
		).put(
			"fields", fieldsJSONArray
		).put(
			"files", filesJSONArray
		).put(
			"id", id
		).put(
			"title", title
		);
	}

	private JSONObject _newRelatedItemFieldJSONObject(String itemTitle) {
		return new JSONObject(
		).put(
			"id", RandomTestUtil.randomString()
		).put(
			"label", itemTitle
		).put(
			"section",
			new JSONObject(
			).put(
				"label", "Related Items"
			)
		);
	}

	private JSONObject _newVaultJSONObject(
		String id, String name, JSONObject... itemJSONObjects) {

		JSONArray itemsJSONArray = new JSONArray();

		for (JSONObject itemJSONObject : itemJSONObjects) {
			itemsJSONArray.put(itemJSONObject);
		}

		return new JSONObject(
		).put(
			"id", id
		).put(
			"items", itemsJSONArray
		).put(
			"name", name
		);
	}

	private void _resetSecretsUtil() {
		Map<String, String> connectSecrets = ReflectionTestUtil.getFieldValue(
			SecretsUtil.class, "_connectSecrets");

		connectSecrets.clear();

		ReflectionTestUtil.setFieldValue(
			SecretsUtil.class, "_accessToken", null);
		ReflectionTestUtil.setFieldValue(
			SecretsUtil.class, "_connectSecretsLoaded", false);
		ReflectionTestUtil.setFieldValue(
			SecretsUtil.class, "_connectURL", null);
		ReflectionTestUtil.setFieldValue(
			SecretsUtil.class, "_httpAuthorization", null);

		try {
			Map<String, ?> vaultsMap = ReflectionTestUtil.getFieldValue(
				Class.forName(SecretsUtil.class.getName() + "$Vault"),
				"_vaultsMap");

			vaultsMap.clear();
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(classNotFoundException);
		}
	}

	private void _setBuildProperties(String... keyValues) {
		Properties properties = new Properties();

		for (int i = 0; i < keyValues.length; i += 2) {
			if (keyValues[i + 1] == null) {
				continue;
			}

			properties.setProperty(keyValues[i], keyValues[i + 1]);
		}

		JenkinsResultsParserUtil.setBuildProperties(properties);
	}

	private String _setUpConnect() throws Exception {
		return _setUpConnect(RandomTestUtil.randomString());
	}

	private String _setUpConnect(String accessToken) throws Exception {
		String accessTokenKey = "/" + RandomTestUtil.randomString();
		String connectURLKey = "/" + RandomTestUtil.randomString();

		_setBuildProperties(
			"one.password.access.token.key", accessTokenKey,
			"one.password.connect.url.key", connectURLKey);

		String connectURL = "http://" + RandomTestUtil.randomString();

		Shell shell = mockShell();

		setShellCommandOutput(accessTokenKey, shell, accessToken);
		setShellCommandOutput(connectURLKey, shell, connectURL);

		return connectURL;
	}

	private void _setVaultsOutput(
			String connectURL, JSONObject... vaultJSONObjects)
		throws Exception {

		JSONArray vaultsJSONArray = new JSONArray();

		for (JSONObject vaultJSONObject : vaultJSONObjects) {
			vaultsJSONArray.put(
				new JSONObject(
				).put(
					"id", vaultJSONObject.getString("id")
				).put(
					"name", vaultJSONObject.getString("name")
				));
		}

		setURLReaderOutput(
			vaultsJSONArray.toString(), connectURL + "/v1/vaults");

		for (JSONObject vaultJSONObject : vaultJSONObjects) {
			JSONArray itemSummariesJSONArray = new JSONArray();

			JSONArray itemsJSONArray = vaultJSONObject.getJSONArray("items");

			for (int i = 0; i < itemsJSONArray.length(); i++) {
				JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

				itemSummariesJSONArray.put(
					new JSONObject(
					).put(
						"id", itemJSONObject.getString("id")
					).put(
						"title", itemJSONObject.getString("title")
					));
			}

			String itemsURL = JenkinsResultsParserUtil.combine(
				connectURL, "/v1/vaults/", vaultJSONObject.getString("id"),
				"/items");

			setURLReaderOutput(itemSummariesJSONArray.toString(), itemsURL);

			for (int i = 0; i < itemsJSONArray.length(); i++) {
				JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

				setURLReaderOutput(
					itemJSONObject.toString(),
					itemsURL + "/" + itemJSONObject.getString("id"));

				JSONArray filesJSONArray = itemJSONObject.getJSONArray("files");

				for (int j = 0; j < filesJSONArray.length(); j++) {
					JSONObject fileJSONObject = filesJSONArray.getJSONObject(j);

					setURLReaderOutput(
						fileJSONObject.getString("content"),
						connectURL + fileJSONObject.getString("content_path"));
				}
			}
		}
	}

	private void _testGenerateJenkinsAPITokenSecretFailure(
		String expectedMessage, String key) {

		try {
			SecretsUtil.generateJenkinsAPITokenSecret(key);

			Assert.fail("Generated an API token for " + key);
		}
		catch (RuntimeException runtimeException) {
			testEquals(expectedMessage, runtimeException.getMessage());
		}
	}

	private void _testGetConnectURL(
			String connectURLKeyOutput, boolean fallback)
		throws Exception {

		_resetSecretsUtil();

		_byteArrayOutputStream.reset();

		mockURLReaders();

		String accessTokenKey = "/" + RandomTestUtil.randomString();
		String connectURL = "http://" + RandomTestUtil.randomString();
		String connectURLKey = null;

		Shell shell = mockShell();

		if (connectURLKeyOutput != null) {
			connectURLKey = "/" + RandomTestUtil.randomString();

			setShellCommandOutput(connectURLKey, shell, connectURLKeyOutput);
		}

		_setBuildProperties(
			"one.password.access.token.key", accessTokenKey,
			"one.password.connect.url", connectURL,
			"one.password.connect.url.key", connectURLKey);

		setShellCommandOutput(
			accessTokenKey, shell, RandomTestUtil.randomString());

		String expectedConnectURL = connectURL;

		if (!fallback) {
			expectedConnectURL = connectURLKeyOutput;
		}

		setURLReaderOutput(
			String.valueOf(new JSONArray()), expectedConnectURL + "/v1/vaults");

		SecretsUtil.getSecret(
			JenkinsResultsParserUtil.combine(
				"op://", RandomTestUtil.randomString(), "/",
				RandomTestUtil.randomString(), "/",
				RandomTestUtil.randomString()));

		verifyURLReaderAttemptsCount(1, expectedConnectURL + "/v1/vaults");

		String output = _byteArrayOutputStream.toString();

		testEquals(fallback, output.contains("one.password.connect.url.key"));
	}

	private void _testGetItemFailure(String expectedMessage, String key) {
		try {
			SecretsUtil.getItem(key);

			Assert.fail("Found an item for " + key);
		}
		catch (RuntimeException runtimeException) {
			testEquals(expectedMessage, runtimeException.getMessage());
		}
	}

	private void _testGetSecretNoAccessToken(String accessTokenKeyOutput)
		throws Exception {

		_resetSecretsUtil();

		_byteArrayOutputStream.reset();

		String accessTokenKey = null;

		Shell shell = mockShell();

		if (accessTokenKeyOutput != null) {
			accessTokenKey = "/" + RandomTestUtil.randomString();

			setShellCommandOutput(accessTokenKey, shell, accessTokenKeyOutput);
		}

		String connectURLKey = "/" + RandomTestUtil.randomString();

		_setBuildProperties(
			"one.password.access.token.key", accessTokenKey,
			"one.password.connect.url.key", connectURLKey);

		setShellCommandOutput(
			connectURLKey, shell, "http://" + RandomTestUtil.randomString());

		String itemTitle = RandomTestUtil.randomString();
		String vaultName = RandomTestUtil.randomString();

		String reference = JenkinsResultsParserUtil.combine(
			"op://", vaultName, "/", itemTitle, "/",
			RandomTestUtil.randomString());

		testEquals(reference, SecretsUtil.getSecret(reference));

		String output = _byteArrayOutputStream.toString();

		Assert.assertTrue(
			output, output.contains("one.password.access.token.key"));

		_testGetItemFailure(
			"Unable to find vault " + vaultName,
			JenkinsResultsParserUtil.combine(
				"op://", vaultName, "/", itemTitle));
	}

	private void _testGetSecretPassThrough(String key) {
		testEquals(key, SecretsUtil.getSecret(key));
	}

	private void _testIsSecretProperty(boolean expected, String value) {
		testEquals(expected, SecretsUtil.isSecretProperty(value));
	}

	private final ByteArrayOutputStream _byteArrayOutputStream =
		new ByteArrayOutputStream();
	private PrintStream _printStream;

}
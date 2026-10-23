/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.client;

import com.liferay.ai.creator.openai.web.internal.exception.AICreatorOpenAIClientException;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

/**
 * @author Lourdes Fernández Besada
 */
public class AICreatorOpenAIClientTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws IOException {
		_aiCreatorOpenAIClient = new AICreatorOpenAIClientImpl();

		_http = Mockito.mock(Http.class);

		ReflectionTestUtil.setFieldValue(
			_aiCreatorOpenAIClient, "_http", _http);

		_jsonFactory = Mockito.mock(JSONFactory.class);

		ReflectionTestUtil.setFieldValue(
			_aiCreatorOpenAIClient, "_jsonFactory", _jsonFactory);

		_originalLanguage = LanguageUtil.getLanguage();
	}

	@After
	public void tearDown() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_originalLanguage);
	}

	@Test
	public void testGetCompletion() throws Exception {
		String messageContent = RandomTestUtil.randomString();

		JSONObject responseJSONObject = JSONUtil.put(
			"choices",
			JSONUtil.put(
				JSONUtil.put(
					"message", JSONUtil.put("content", messageContent))));

		Http.Response response = _getMockResponse(
			HttpURLConnection.HTTP_OK, responseJSONObject);

		_mockLanguage();

		String apiKey = RandomTestUtil.randomString();
		String content = RandomTestUtil.randomString();
		String tone = RandomTestUtil.randomString();
		int words = RandomTestUtil.randomInt();

		Assert.assertEquals(
			messageContent,
			_aiCreatorOpenAIClient.getCompletion(
				apiKey, content, LocaleUtil.getDefault(), tone, words));
		_assertMessageRoleSystemContent(LocaleUtil.getDefault(), tone, words);

		_assertOptions(
			apiKey, content, ContentTypes.APPLICATION_JSON,
			AICreatorOpenAIClientImpl.ENDPOINT_COMPLETION);
		_assertResponse(response);
	}

	@Test
	public void testGetCompletionIOException() throws Exception {
		_mockLanguage();

		String content = RandomTestUtil.randomString();

		_testIOException(
			content, ContentTypes.APPLICATION_JSON,
			AICreatorOpenAIClientImpl.ENDPOINT_COMPLETION,
			apiKey -> _aiCreatorOpenAIClient.getCompletion(
				apiKey, content, LocaleUtil.getDefault(),
				RandomTestUtil.randomString(), RandomTestUtil.randomInt()));
	}

	@Test
	public void testGetCompletionResponseWithErrorKey() throws Exception {
		_mockLanguage();

		String content = RandomTestUtil.randomString();

		_testResponseWithErrorKey(
			content, ContentTypes.APPLICATION_JSON,
			AICreatorOpenAIClientImpl.ENDPOINT_COMPLETION,
			apiKey -> _aiCreatorOpenAIClient.getCompletion(
				apiKey, content, LocaleUtil.getDefault(),
				RandomTestUtil.randomString(), RandomTestUtil.randomInt()));
	}

	@Test
	public void testGetCompletionUnauthorizedResponseCode() throws Exception {
		_mockLanguage();

		String content = RandomTestUtil.randomString();

		_testUnauthorizedResponseCode(
			content, ContentTypes.APPLICATION_JSON,
			AICreatorOpenAIClientImpl.ENDPOINT_COMPLETION,
			apiKey -> _aiCreatorOpenAIClient.getCompletion(
				apiKey, content, LocaleUtil.getDefault(),
				RandomTestUtil.randomString(), RandomTestUtil.randomInt()));
	}

	@Test
	public void testGetGenerations() throws Exception {
		String b64JSON1 = RandomTestUtil.randomString();
		String b64JSON2 = RandomTestUtil.randomString();

		_getMockResponse(
			HttpURLConnection.HTTP_OK,
			JSONUtil.put(
				"data",
				JSONUtil.putAll(
					JSONUtil.put("b64_json", b64JSON1),
					JSONUtil.put(
						"b64_json", b64JSON2
					).put(
						"url", RandomTestUtil.randomString()
					))));

		String apiKey = RandomTestUtil.randomString();
		String model = RandomTestUtil.randomString();
		String prompt = RandomTestUtil.randomString();
		String size = RandomTestUtil.randomString();

		Assert.assertArrayEquals(
			new String[] {
				"data:image/png;base64," + b64JSON1,
				"data:image/png;base64," + b64JSON2
			},
			_aiCreatorOpenAIClient.getGenerations(
				apiKey, model, 2, prompt, size));

		ArgumentCaptor<Http.Options> argumentCaptor = ArgumentCaptor.forClass(
			Http.Options.class);

		Mockito.verify(
			_http
		).URLtoInputStream(
			argumentCaptor.capture()
		);

		Http.Options options = argumentCaptor.getValue();

		Http.Body body = options.getBody();

		JSONObject bodyJSONObject = JSONFactoryUtil.createJSONObject(
			body.getContent());

		Assert.assertEquals(
			AICreatorOpenAIClientImpl.ENDPOINT_GENERATIONS,
			options.getLocation());
		Assert.assertEquals(model, bodyJSONObject.getString("model"));
		Assert.assertEquals(2, bodyJSONObject.getInt("n"));
		Assert.assertEquals(prompt, bodyJSONObject.getString("prompt"));
		Assert.assertFalse(bodyJSONObject.has("response_format"));
		Assert.assertEquals(size, bodyJSONObject.getString("size"));
	}

	@Test
	public void testValidateAPIKey() throws Exception {
		JSONObject responseJSONObject = Mockito.mock(JSONObject.class);

		Http.Response response = _getMockResponse(
			HttpURLConnection.HTTP_OK, responseJSONObject);

		String apiKey = RandomTestUtil.randomString();
		String model = RandomTestUtil.randomString();

		_aiCreatorOpenAIClient.validateAPIKey(apiKey, model);

		_assertOptions(
			apiKey, AICreatorOpenAIClientImpl.ENDPOINT_MODELS + "/" + model);

		_assertResponse(response);
		_assertResponseJSONObject(responseJSONObject);
	}

	@Test
	public void testValidateAPIKeyIOException() throws Exception {
		String model = RandomTestUtil.randomString();

		_testIOException(
			null, null, AICreatorOpenAIClientImpl.ENDPOINT_MODELS + "/" + model,
			apiKey -> _aiCreatorOpenAIClient.validateAPIKey(apiKey, model));
	}

	@Test
	public void testValidateAPIKeyResponseWithErrorKey() throws Exception {
		String model = RandomTestUtil.randomString();

		_testResponseWithErrorKey(
			null, null, AICreatorOpenAIClientImpl.ENDPOINT_MODELS + "/" + model,
			apiKey -> _aiCreatorOpenAIClient.validateAPIKey(apiKey, model));
	}

	@Test
	public void testValidateAPIKeyUnauthorizedResponseCode() throws Exception {
		String model = RandomTestUtil.randomString();

		_testUnauthorizedResponseCode(
			null, null, AICreatorOpenAIClientImpl.ENDPOINT_MODELS + "/" + model,
			apiKey -> _aiCreatorOpenAIClient.validateAPIKey(apiKey, model));
	}

	@Test
	public void testValidateAPIKeyWithoutModel() throws Exception {
		JSONObject responseJSONObject = Mockito.mock(JSONObject.class);

		Http.Response response = _getMockResponse(
			HttpURLConnection.HTTP_OK, responseJSONObject);

		String apiKey = RandomTestUtil.randomString();

		_aiCreatorOpenAIClient.validateAPIKey(apiKey, null);

		_assertOptions(apiKey, AICreatorOpenAIClientImpl.ENDPOINT_MODELS);

		_assertResponse(response);
		_assertResponseJSONObject(responseJSONObject);
	}

	private static void _assertResponse(Http.Response response) {
		_assertResponse(response, Mockito.times(1));
	}

	private static void _assertResponse(
		Http.Response response, VerificationMode verificationMode) {

		Mockito.verify(
			response, verificationMode
		).getResponseCode();
	}

	private void _assertBody(String content, Http.Body body) throws Exception {
		Assert.assertNotNull(body);
		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON, body.getContentType());
		Assert.assertEquals(StringPool.UTF8, body.getCharset());
		Assert.assertTrue(
			body.getContent(), JSONUtil.isJSONObject(body.getContent()));

		JSONObject contentJSONObject = new JSONObjectImpl(body.getContent());

		Assert.assertEquals(
			"gpt-3.5-turbo", contentJSONObject.getString("model"));

		JSONArray messagesJSONArray = contentJSONObject.getJSONArray(
			"messages");

		Assert.assertNotNull(contentJSONObject.toString(), messagesJSONArray);
		Assert.assertEquals(
			messagesJSONArray.toString(), 2, messagesJSONArray.length());

		JSONObject messageJSONObject1 = messagesJSONArray.getJSONObject(0);

		Assert.assertEquals("system", messageJSONObject1.getString("role"));

		JSONObject messageJSONObject2 = messagesJSONArray.getJSONObject(1);

		Assert.assertEquals(content, messageJSONObject2.getString("content"));
		Assert.assertEquals("user", messageJSONObject2.getString("role"));
	}

	private void _assertMessageRoleSystemContent(
		Locale locale, String tone, int words) {

		ArgumentCaptor<String[]> argumentCaptor = ArgumentCaptor.forClass(
			String[].class);

		Mockito.verify(
			_language
		).format(
			Mockito.eq(locale),
			Mockito.eq(
				"i-want-you-to-create-a-text-of-approximately-x-words,-and-" +
					"using-a-x-tone"),
			argumentCaptor.capture()
		);

		String[] arguments = argumentCaptor.getValue();

		Assert.assertEquals(arguments.toString(), 2, arguments.length);
		Assert.assertEquals(String.valueOf(words), arguments[0]);
		Assert.assertEquals(tone, arguments[1]);
	}

	private void _assertOptions(String apiKey, String location)
		throws Exception {

		_assertOptions(apiKey, null, null, location);
	}

	private void _assertOptions(
			String apiKey, String content, String contentType, String location)
		throws Exception {

		ArgumentCaptor<Http.Options> argumentCaptor = ArgumentCaptor.forClass(
			Http.Options.class);

		Mockito.verify(
			_http
		).URLtoInputStream(
			argumentCaptor.capture()
		);

		Http.Options options = argumentCaptor.getValue();

		if (Validator.isNull(content)) {
			Assert.assertNull(options.getBody());
		}
		else {
			_assertBody(content, options.getBody());
		}

		Assert.assertEquals(
			"Bearer " + apiKey, options.getHeader("Authorization"));
		Assert.assertEquals(contentType, options.getHeader("Content-Type"));
		Assert.assertEquals(location, options.getLocation());
	}

	private void _assertResponseJSONObject(JSONObject responseJSONObject) {
		Mockito.verify(
			responseJSONObject
		).has(
			"error"
		);
	}

	private Http.Response _getMockResponse(
			int responseCode, JSONObject responseJSONObject)
		throws Exception {

		Http.Response response = Mockito.mock(Http.Response.class);

		Mockito.when(
			response.getResponseCode()
		).thenReturn(
			responseCode
		);

		Mockito.when(
			_http.URLtoInputStream(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(
					0, Http.Options.class);

				options.setResponse(response);

				InputStream inputStream = Mockito.mock(InputStream.class);

				Mockito.when(
					inputStream.read(
						Mockito.any(), Mockito.anyInt(), Mockito.anyInt())
				).thenReturn(
					-1
				);

				return inputStream;
			}
		);

		Mockito.when(
			_jsonFactory.createJSONObject(Mockito.anyMap())
		).thenReturn(
			responseJSONObject
		);

		Mockito.when(
			_jsonFactory.createJSONObject(Mockito.anyString())
		).thenReturn(
			responseJSONObject
		);

		return response;
	}

	private void _mockLanguage() {
		_language = Mockito.mock(Language.class);

		Mockito.when(
			_language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1, String.class)
		);

		ReflectionTestUtil.setFieldValue(
			_aiCreatorOpenAIClient, "_language", _language);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);
	}

	private void _testIOException(
			String content, String contentType, String location,
			UnsafeConsumer<String, Exception> unsafeConsumer)
		throws Exception {

		IOException ioException = new IOException();

		Mockito.when(
			_http.URLtoInputStream(Mockito.any(Http.Options.class))
		).thenThrow(
			ioException
		);

		String apiKey = RandomTestUtil.randomString();

		try {
			unsafeConsumer.accept(apiKey);

			Assert.fail();
		}
		catch (AICreatorOpenAIClientException aiCreatorOpenAIClientException) {
			Assert.assertEquals(
				ioException, aiCreatorOpenAIClientException.getCause());
		}

		_assertOptions(apiKey, content, contentType, location);
	}

	private void _testResponseWithErrorKey(
			String content, String contentType, String location,
			UnsafeConsumer<String, Exception> unsafeConsumer)
		throws Exception {

		JSONObject errorJSONObject = JSONUtil.put(
			"code", RandomTestUtil.randomString()
		).put(
			"message", RandomTestUtil.randomString()
		);

		Http.Response response = _getMockResponse(
			HttpURLConnection.HTTP_OK, JSONUtil.put("error", errorJSONObject));

		String apiKey = RandomTestUtil.randomString();

		try {
			unsafeConsumer.accept(apiKey);

			Assert.fail();
		}
		catch (AICreatorOpenAIClientException aiCreatorOpenAIClientException) {
			Assert.assertEquals(
				errorJSONObject.getString("code"),
				aiCreatorOpenAIClientException.getCode());
			Assert.assertEquals(
				errorJSONObject.getString("message"),
				aiCreatorOpenAIClientException.getMessage());

			Assert.assertEquals(
				HttpURLConnection.HTTP_OK,
				aiCreatorOpenAIClientException.getResponseCode());
		}

		_assertOptions(apiKey, content, contentType, location);

		_assertResponse(response);
	}

	private void _testUnauthorizedResponseCode(
			String content, String contentType, String location,
			UnsafeConsumer<String, Exception> unsafeConsumer)
		throws Exception {

		JSONObject responseJSONObject = Mockito.mock(JSONObject.class);

		Http.Response response = _getMockResponse(
			HttpURLConnection.HTTP_UNAUTHORIZED, responseJSONObject);

		String apiKey = RandomTestUtil.randomString();

		try {
			unsafeConsumer.accept(apiKey);

			Assert.fail();
		}
		catch (AICreatorOpenAIClientException aiCreatorOpenAIClientException) {
			Assert.assertEquals(
				HttpURLConnection.HTTP_UNAUTHORIZED,
				aiCreatorOpenAIClientException.getResponseCode());
		}

		_assertOptions(apiKey, content, contentType, location);
		_assertResponse(response, Mockito.times(2));
		_assertResponseJSONObject(responseJSONObject);
	}

	private AICreatorOpenAIClient _aiCreatorOpenAIClient;
	private Http _http;
	private JSONFactory _jsonFactory;
	private Language _language;
	private Language _originalLanguage;

}
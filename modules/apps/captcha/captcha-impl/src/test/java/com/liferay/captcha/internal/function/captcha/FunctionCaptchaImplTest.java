/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.internal.function.captcha;

import com.liferay.captcha.internal.configuration.FunctionCaptchaImplConfiguration;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Manuele Castro
 */
public class FunctionCaptchaImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpFunctionCaptchaImplConfiguration();
		_setUpPortalCatapult();
		_setUpUserLocalService();

		_setUpFunctionCaptchaImpl();
	}

	@Test
	public void testValidateChallenge() throws Exception {
		_testValidateChallenge(false);
		_testValidateChallenge(true);
	}

	private static void _setUpFunctionCaptchaImpl() {
		_functionCaptchaImpl = new FunctionCaptchaImpl();

		ReflectionTestUtil.setFieldValue(
			_functionCaptchaImpl, "_functionCaptchaImplConfiguration",
			_functionCaptchaImplConfiguration);
		ReflectionTestUtil.setFieldValue(
			_functionCaptchaImpl, "_portalCatapult", _portalCatapult);
		ReflectionTestUtil.setFieldValue(
			_functionCaptchaImpl, "_userLocalService", _userLocalService);
	}

	private static void _setUpFunctionCaptchaImplConfiguration() {
		_functionCaptchaImplConfiguration = Mockito.mock(
			FunctionCaptchaImplConfiguration.class);

		Mockito.when(
			_functionCaptchaImplConfiguration.captchaName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_functionCaptchaImplConfiguration.captchaResponseParameterName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_functionCaptchaImplConfiguration.
				oAuth2ApplicationExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_functionCaptchaImplConfiguration.resourcePath()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private static void _setUpPortalCatapult() throws Exception {
		Future<byte[]> future = Mockito.mock(Future.class);

		Mockito.when(
			future.get()
		).thenReturn(
			RandomTestUtil.randomBytes()
		);

		_portalCatapult = Mockito.mock(PortalCatapult.class);

		Mockito.when(
			_portalCatapult.launch(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			future
		);
	}

	private static void _setUpUserLocalService() throws Exception {
		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getUserId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_userLocalService = Mockito.mock(UserLocalService.class);

		Mockito.when(
			_userLocalService.getUserByScreenName(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			user
		);
	}

	private HttpServletRequest _mockHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getRemoteAddr()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			ParamUtil.getString(httpServletRequest, Mockito.anyString())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return httpServletRequest;
	}

	private JSONFactory _mockJSONFactory(boolean success) throws Exception {
		JSONFactory jsonFactory = Mockito.mock(JSONFactory.class);

		Mockito.when(
			jsonFactory.createJSONObject()
		).thenReturn(
			new JSONObjectImpl()
		);

		JSONObject jsonObject = new JSONObjectImpl();

		jsonObject.put("success", success);

		if (!success) {
			JSONArray jsonArray = new JSONArrayImpl();

			jsonArray.put(RandomTestUtil.randomString());

			jsonObject.put("error-codes", jsonArray);
		}

		Mockito.when(
			jsonFactory.createJSONObject(Mockito.anyString())
		).thenReturn(
			jsonObject
		);

		return jsonFactory;
	}

	private void _testValidateChallenge(boolean success) throws Exception {
		ReflectionTestUtil.setFieldValue(
			_functionCaptchaImpl, "_jsonFactory", _mockJSONFactory(success));

		if (success) {
			_functionCaptchaImpl.validateChallenge(_mockHttpServletRequest());

			ArgumentCaptor<JSONObject> argumentCaptor = ArgumentCaptor.forClass(
				JSONObject.class);

			Mockito.verify(
				_portalCatapult, Mockito.atLeastOnce()
			).launch(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				argumentCaptor.capture(), Mockito.anyString(), Mockito.anyLong()
			);

			JSONObject payloadJSONObject = argumentCaptor.getValue();

			Assert.assertTrue(payloadJSONObject.has("remoteip"));
			Assert.assertTrue(payloadJSONObject.has("response"));

			return;
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				FunctionCaptchaImpl.class.getName(), LoggerTestUtil.ERROR)) {

			List<LogEntry> logEntries = logCapture.getLogEntries();

			try {
				_functionCaptchaImpl.validateChallenge(
					_mockHttpServletRequest());

				Assert.fail();
			}
			catch (CaptchaException captchaException) {
				Assert.assertTrue(
					captchaException instanceof CaptchaConfigurationException);

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertTrue(
					StringUtil.startsWith(
						logEntry.getMessage(),
						_functionCaptchaImplConfiguration.captchaName() +
							" encountered an error: "));
			}
		}
	}

	private static FunctionCaptchaImpl _functionCaptchaImpl;
	private static FunctionCaptchaImplConfiguration
		_functionCaptchaImplConfiguration;
	private static PortalCatapult _portalCatapult;
	private static UserLocalService _userLocalService;

}
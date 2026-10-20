/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class AutoTranslateServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-109773")
	public void testDoPost() throws Exception {
		TestTranslator testTranslator = new TestTranslator();

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<Translator> serviceRegistration =
			bundleContext.registerService(
				Translator.class, testTranslator,
				MapUtil.singletonDictionary(
					"service.ranking", Integer.MAX_VALUE));

		try {
			MockHttpServletResponse mockHttpServletResponse = _post(null);

			Assert.assertEquals(
				HttpServletResponse.SC_FORBIDDEN,
				mockHttpServletResponse.getStatus());

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				mockHttpServletResponse.getContentAsString());

			Assert.assertTrue(jsonObject.has("error"));

			Assert.assertEquals(0, testTranslator.getCount());

			mockHttpServletResponse = _post(
				_userLocalService.getUser(TestPropsValues.getUserId()));

			Assert.assertEquals(
				HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

			jsonObject = _jsonFactory.createJSONObject(
				mockHttpServletResponse.getContentAsString());

			JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");

			Assert.assertEquals(
				"translated", fieldsJSONObject.getString("title"));

			Assert.assertEquals(1, testTranslator.getCount());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private MockHttpServletResponse _post(User user) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		if (user != null) {
			mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		}

		String content = JSONUtil.put(
			"fields", JSONUtil.put("title", "hello")
		).put(
			"sourceLanguageId", LocaleUtil.toLanguageId(LocaleUtil.US)
		).put(
			"targetLanguageId", LocaleUtil.toLanguageId(LocaleUtil.SPAIN)
		).toString();

		mockHttpServletRequest.setContent(content.getBytes());

		mockHttpServletRequest.setMethod("POST");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		ReflectionTestUtil.invoke(
			_servlet, "doPost",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class
			},
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.pattern=/translation/auto_translate"
	)
	private Servlet _servlet;

	@Inject
	private UserLocalService _userLocalService;

	private static class TestTranslator implements Translator {

		public int getCount() {
			return _count;
		}

		@Override
		public boolean isAIAssisted() {
			return false;
		}

		@Override
		public boolean isEnabled(long companyId) {
			return true;
		}

		@Override
		public TranslatorPacket translate(TranslatorPacket translatorPacket) {
			_count++;

			return new TranslatorPacket() {

				@Override
				public long getCompanyId() {
					return translatorPacket.getCompanyId();
				}

				@Override
				public Map<String, String> getFieldsMap() {
					return HashMapBuilder.put(
						"title", "translated"
					).build();
				}

				@Override
				public Map<String, Boolean> getHTMLMap() {
					return Collections.emptyMap();
				}

				@Override
				public String getSourceLanguageId() {
					return translatorPacket.getSourceLanguageId();
				}

				@Override
				public String getTargetLanguageId() {
					return translatorPacket.getTargetLanguageId();
				}

			};
		}

		private int _count;

	}

}
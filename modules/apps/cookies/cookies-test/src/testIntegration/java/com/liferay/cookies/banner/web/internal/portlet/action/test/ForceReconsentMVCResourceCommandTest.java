/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Alvaro Saugar
 */
@RunWith(Arquillian.class)
public class ForceReconsentMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_configurationTemporarySwapper = new ConfigurationTemporarySwapper(
			CookiesPreferenceHandlingConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).put(
				"modifiedDate", _MODIFIED_DATE
			).build());
	}

	@After
	public void tearDown() throws Exception {
		_configurationTemporarySwapper.close();
	}

	@Test
	public void testServeResource() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(
				HttpMethods.POST, _SYSTEM_SCOPE_NAME);

		mockLiferayResourceRequest.setParameter(
			"p_auth", _getSessionCSRFToken(mockLiferayResourceRequest));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			_serveResource(mockLiferayResourceRequest);

		Assert.assertNull(
			mockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));

		Assert.assertTrue(_getModifiedDate() > _MODIFIED_DATE);
	}

	@Test
	public void testServeResourceWithBlockedRequest() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(HttpMethods.GET, _SYSTEM_SCOPE_NAME);

		mockLiferayResourceRequest.setParameter(
			"p_auth", _getSessionCSRFToken(mockLiferayResourceRequest));

		_testServeResourceWithBlockedRequest(
			mockLiferayResourceRequest,
			HttpServletResponse.SC_METHOD_NOT_ALLOWED);

		mockLiferayResourceRequest = _getMockLiferayResourceRequest(
			HttpMethods.POST, _SYSTEM_SCOPE_NAME);

		mockLiferayResourceRequest.setParameter(
			"p_auth", RandomTestUtil.randomString());

		_testServeResourceWithBlockedRequest(
			mockLiferayResourceRequest, HttpServletResponse.SC_FORBIDDEN);

		_testServeResourceWithBlockedRequest(
			_getMockLiferayResourceRequest(
				HttpMethods.POST, _SYSTEM_SCOPE_NAME),
			HttpServletResponse.SC_FORBIDDEN);

		mockLiferayResourceRequest = _getMockLiferayResourceRequest(
			HttpMethods.POST, RandomTestUtil.randomString());

		mockLiferayResourceRequest.setParameter(
			"p_auth", _getSessionCSRFToken(mockLiferayResourceRequest));

		_testServeResourceWithBlockedRequest(
			mockLiferayResourceRequest, HttpServletResponse.SC_FORBIDDEN);
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			String method, String scopeName)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayResourceRequest.setMethod(method);
		mockLiferayResourceRequest.setParameter("scope", scopeName);

		_getSessionCSRFToken(mockLiferayResourceRequest);

		return mockLiferayResourceRequest;
	}

	private long _getModifiedDate() throws Exception {
		Configuration configuration = _configurationAdmin.getConfiguration(
			CookiesPreferenceHandlingConfiguration.class.getName(),
			StringPool.QUESTION);

		Dictionary<String, Object> properties = configuration.getProperties();

		if (properties == null) {
			return 0;
		}

		return GetterUtil.getLong(properties.get("modifiedDate"));
	}

	private String _getSessionCSRFToken(
		MockLiferayResourceRequest mockLiferayResourceRequest) {

		return AuthTokenUtil.getToken(
			mockLiferayResourceRequest.getHttpServletRequest());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private MockLiferayResourceResponse _serveResource(
			MockLiferayResourceRequest mockLiferayResourceRequest)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new PropertyRecordingMockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		return mockLiferayResourceResponse;
	}

	private void _testServeResourceWithBlockedRequest(
			MockLiferayResourceRequest mockLiferayResourceRequest,
			int statusCode)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			_serveResource(mockLiferayResourceRequest);

		Assert.assertEquals(
			String.valueOf(statusCode),
			mockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));

		Assert.assertEquals(_MODIFIED_DATE, _getModifiedDate());
	}

	private static final long _MODIFIED_DATE = 1000;

	private static final String _SYSTEM_SCOPE_NAME =
		ExtendedObjectClassDefinition.Scope.SYSTEM.getValue();

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private ConfigurationTemporarySwapper _configurationTemporarySwapper;

	@Inject(filter = "mvc.command.name=/cookies_banner/force_reconsent")
	private MVCResourceCommand _mvcResourceCommand;

	private static class PropertyRecordingMockLiferayResourceResponse
		extends MockLiferayResourceResponse {

		@Override
		public String getProperty(String name) {
			return _properties.get(name);
		}

		@Override
		public void setProperty(String property, String value) {
			_properties.put(property, value);
		}

		private final Map<String, String> _properties = new HashMap<>();

	}

}
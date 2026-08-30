/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

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
public class ProductionReadinessMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ruleKey = RandomTestUtil.randomString();
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_PID);
	}

	@Test
	public void testServeResource() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(HttpMethods.POST);

		mockLiferayResourceRequest.setParameter(
			"p_auth", _getSessionCSRFToken(mockLiferayResourceRequest));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			_serveResource(
				mockLiferayResourceRequest, _ignoreRuleMVCResourceCommand);

		Assert.assertNull(
			mockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));

		Assert.assertTrue(ArrayUtil.contains(_getIgnoredRules(), _ruleKey));
	}

	@Test
	public void testServeResourceWithBlockedRequest() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(HttpMethods.GET);

		mockLiferayResourceRequest.setParameter(
			"p_auth", _getSessionCSRFToken(mockLiferayResourceRequest));

		_testServeResourceWithBlockedRequest(
			mockLiferayResourceRequest,
			HttpServletResponse.SC_METHOD_NOT_ALLOWED);

		mockLiferayResourceRequest = _getMockLiferayResourceRequest(
			HttpMethods.POST);

		mockLiferayResourceRequest.setParameter(
			"p_auth", RandomTestUtil.randomString());

		_testServeResourceWithBlockedRequest(
			mockLiferayResourceRequest, HttpServletResponse.SC_FORBIDDEN);

		_testServeResourceWithBlockedRequest(
			_getMockLiferayResourceRequest(HttpMethods.POST),
			HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void testServeResourceWithoutCSRFProtection() throws Exception {
		MockLiferayResourceResponse mockLiferayResourceResponse =
			_serveResource(
				_getMockLiferayResourceRequest(HttpMethods.GET),
				_getResultsMVCResourceCommand);

		Assert.assertNull(
			mockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));
	}

	private String[] _getIgnoredRules() throws Exception {
		Configuration configuration = _configurationAdmin.getConfiguration(
			_PID, StringPool.QUESTION);

		Dictionary<String, Object> properties = configuration.getProperties();

		if (properties == null) {
			return new String[0];
		}

		return GetterUtil.getStringValues(properties.get("ignoredRules"));
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			String method)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayResourceRequest.setMethod(method);
		mockLiferayResourceRequest.setParameter("ruleKey", _ruleKey);

		_getSessionCSRFToken(mockLiferayResourceRequest);

		return mockLiferayResourceRequest;
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
			MockLiferayResourceRequest mockLiferayResourceRequest,
			MVCResourceCommand mvcResourceCommand)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new PropertyRecordingMockLiferayResourceResponse();

		mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		return mockLiferayResourceResponse;
	}

	private void _testServeResourceWithBlockedRequest(
			MockLiferayResourceRequest mockLiferayResourceRequest,
			int statusCode)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			_serveResource(
				mockLiferayResourceRequest, _ignoreRuleMVCResourceCommand);

		Assert.assertEquals(
			String.valueOf(statusCode),
			mockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));

		Assert.assertFalse(ArrayUtil.contains(_getIgnoredRules(), _ruleKey));
	}

	private static final String _PID =
		"com.liferay.server.admin.web.internal.configuration." +
			"ProductionReadinessConfiguration";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject(
		filter = "mvc.command.name=/server_admin/get_production_readiness_results"
	)
	private MVCResourceCommand _getResultsMVCResourceCommand;

	@Inject(
		filter = "mvc.command.name=/server_admin/ignore_production_readiness_rule"
	)
	private MVCResourceCommand _ignoreRuleMVCResourceCommand;

	private String _ruleKey;

	private static class PropertyRecordingMockLiferayResourceResponse
		extends MockLiferayResourceResponse {

		@Override
		public String getProperty(String name) {
			return _properties.get(name);
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			if (_printWriter == null) {
				_printWriter = new PrintWriter(getPortletOutputStream());
			}

			return _printWriter;
		}

		@Override
		public void setProperty(String property, String value) {
			_properties.put(property, value);
		}

		private PrintWriter _printWriter;
		private final Map<String, String> _properties = new HashMap<>();

	}

}
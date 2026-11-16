/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Álvaro Saugar
 */
@RunWith(Arquillian.class)
public class AuditConfigurationFormRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-6417")
	@Test
	public void testGetRequestParameters() throws Exception {
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"pseudonymizationEnabled", false
			).build(),
			_getRequestParameters(
				RandomTestUtil.randomInt(),
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS));
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"enabled", false
			).put(
				"pseudonymizationEnabled", false
			).build(),
			_getRequestParameters(
				RandomTestUtil.randomInt(),
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS));

		_testGetRequestParametersWithOverriddenProperties();
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testGetRequestParametersWhenFeatureFlagIsDisabled()
		throws Exception {

		try {
			_getRequestParameters(
				RandomTestUtil.randomInt(),
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS);

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertEquals(
				"Feature flag LPD-6417 is disabled for company " +
					TestPropsValues.getCompanyId(),
				unsupportedOperationException.getMessage());
		}

		int auditMessageMaxQueueSize = RandomTestUtil.randomInt();

		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"auditMessageMaxQueueSize", auditMessageMaxQueueSize
			).put(
				"enabled", false
			).put(
				"pseudonymizationEnabled", false
			).build(),
			_getRequestParameters(
				auditMessageMaxQueueSize,
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS));
	}

	private Map<String, Object> _getRequestParameters(
			int auditMessageMaxQueueSize, String portletId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			ProxyUtil.newProxyInstance(
				LiferayPortletConfig.class.getClassLoader(),
				new Class<?>[] {LiferayPortletConfig.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getPortletId")) {
						return portletId;
					}

					return null;
				}));
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletActionRequest);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			"auditMessageMaxQueueSize",
			String.valueOf(auditMessageMaxQueueSize));
		mockLiferayPortletActionRequest.setParameter("enabled", "false");
		mockLiferayPortletActionRequest.setParameter(
			"pseudonymizationEnabled", "false");

		return _configurationFormRenderer.getRequestParameters(
			mockLiferayPortletActionRequest.getHttpServletRequest());
	}

	private void _testGetRequestParametersWithOverriddenProperties()
		throws Exception {

		Map<String, Map<String, Object>> overridePropertiesMap =
			ReflectionTestUtil.getFieldValue(
				ConfigurationOverridePropertiesUtil.getOverridePropertiesMap(),
				"m");

		overridePropertiesMap.put(
			AuditConfiguration.class.getName(),
			HashMapBuilder.<String, Object>put(
				"enabled", RandomTestUtil.randomBoolean()
			).put(
				"pseudonymizationEnabled", RandomTestUtil.randomBoolean()
			).build());

		try {
			Assert.assertEquals(
				Collections.emptyMap(),
				_getRequestParameters(
					RandomTestUtil.randomInt(),
					ConfigurationAdminPortletKeys.SYSTEM_SETTINGS));
		}
		finally {
			overridePropertiesMap.remove(AuditConfiguration.class.getName());
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display.AuditConfigurationFormRenderer",
		type = ConfigurationFormRenderer.class
	)
	private ConfigurationFormRenderer _configurationFormRenderer;

}
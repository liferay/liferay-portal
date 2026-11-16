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
import com.liferay.portal.security.audit.router.configuration.FileSystemAuditMessageProcessorConfiguration;
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
public class FileSystemAuditMessageProcessorConfigurationFormRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-6417")
	@Test
	public void testGetRequestParameters() throws Exception {
		_testGetRequestParameters(
			ConfigurationAdminPortletKeys.INSTANCE_SETTINGS);
		_testGetRequestParameters(
			ConfigurationAdminPortletKeys.SYSTEM_SETTINGS);
		_testGetRequestParametersWithOverriddenProperties();
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testGetRequestParametersWhenFeatureFlagIsDisabled()
		throws Exception {

		try {
			_getRequestParameters(
				RandomTestUtil.randomString(),
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS);

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertEquals(
				"Feature flag LPD-6417 is disabled for company " +
					TestPropsValues.getCompanyId(),
				unsupportedOperationException.getMessage());
		}

		_testGetRequestParameters(
			ConfigurationAdminPortletKeys.SYSTEM_SETTINGS);
	}

	private Map<String, Object> _getRequestParameters(
			String outputDirectory, String portletId)
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

		mockLiferayPortletActionRequest.setParameter("enabled", "true");
		mockLiferayPortletActionRequest.setParameter(
			"generateChecksum", "true");
		mockLiferayPortletActionRequest.setParameter(
			"outputDirectory", outputDirectory);
		mockLiferayPortletActionRequest.setParameter("outputFormat", "CSV");

		return _configurationFormRenderer.getRequestParameters(
			mockLiferayPortletActionRequest.getHttpServletRequest());
	}

	private void _testGetRequestParameters(String portletId) throws Exception {
		String outputDirectory = RandomTestUtil.randomString();

		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"enabled", true
			).put(
				"generateChecksum", true
			).put(
				"outputDirectory", outputDirectory
			).put(
				"outputFormat", "CSV"
			).build(),
			_getRequestParameters(outputDirectory, portletId));
	}

	private void _testGetRequestParametersWithOverriddenProperties()
		throws Exception {

		Map<String, Map<String, Object>> overridePropertiesMap =
			ReflectionTestUtil.getFieldValue(
				ConfigurationOverridePropertiesUtil.getOverridePropertiesMap(),
				"m");

		overridePropertiesMap.put(
			FileSystemAuditMessageProcessorConfiguration.class.getName(),
			HashMapBuilder.<String, Object>put(
				"enabled", RandomTestUtil.randomBoolean()
			).put(
				"generateChecksum", RandomTestUtil.randomBoolean()
			).put(
				"outputDirectory", RandomTestUtil.randomString()
			).put(
				"outputFormat", RandomTestUtil.randomString()
			).build());

		try {
			Assert.assertEquals(
				Collections.emptyMap(),
				_getRequestParameters(
					RandomTestUtil.randomString(),
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS));
		}
		finally {
			overridePropertiesMap.remove(
				FileSystemAuditMessageProcessorConfiguration.class.getName());
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display.FileSystemAuditMessageProcessorConfigurationFormRenderer",
		type = ConfigurationFormRenderer.class
	)
	private ConfigurationFormRenderer _configurationFormRenderer;

}
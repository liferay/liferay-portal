/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorCompanyConfiguration;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class
	PersistentAuditMessageProcessorCompanyConfigurationFormRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-114718")
	@Test
	public void testGetRequestParameters() throws Exception {
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"enabled", false
			).build(),
			_getRequestParameters("false"));

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PersistentAuditMessageProcessorCompanyConfiguration.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", false
					).build())) {

			Assert.assertEquals(
				HashMapBuilder.<String, Object>put(
					"enabled", true
				).build(),
				_getRequestParameters(null));
		}

		Map<String, Map<String, Object>> overridePropertiesMap =
			ReflectionTestUtil.getFieldValue(
				ConfigurationOverridePropertiesUtil.getOverridePropertiesMap(),
				"m");

		overridePropertiesMap.put(
			PersistentAuditMessageProcessorCompanyConfiguration.class.getName(),
			HashMapBuilder.<String, Object>put(
				"enabled", RandomTestUtil.randomBoolean()
			).build());

		try {
			Assert.assertEquals(
				Collections.emptyMap(), _getRequestParameters("false"));
		}
		finally {
			overridePropertiesMap.remove(
				PersistentAuditMessageProcessorCompanyConfiguration.class.
					getName());
		}

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-114718")) {

			AssertUtils.assertFailure(
				UnsupportedOperationException.class,
				"Feature flag LPD-114718 is disabled for company " +
					TestPropsValues.getCompanyId(),
				() -> _getRequestParameters("false"));
		}
	}

	private Map<String, Object> _getRequestParameters(String enabled)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		if (enabled != null) {
			mockLiferayPortletActionRequest.setParameter("enabled", enabled);
		}

		return _configurationFormRenderer.getRequestParameters(
			mockLiferayPortletActionRequest.getHttpServletRequest());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display.PersistentAuditMessageProcessorCompanyConfigurationFormRenderer",
		type = ConfigurationFormRenderer.class
	)
	private ConfigurationFormRenderer _configurationFormRenderer;

}
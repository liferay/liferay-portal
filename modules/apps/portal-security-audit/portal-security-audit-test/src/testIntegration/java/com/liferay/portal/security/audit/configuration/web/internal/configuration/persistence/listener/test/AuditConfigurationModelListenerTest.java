/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Álvaro Saugar
 */
@RunWith(Arquillian.class)
public class AuditConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		String classPK = String.valueOf(TestPropsValues.getCompanyId());

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> {
				if (Objects.equals(auditMessage.getClassPK(), classPK) &&
					Objects.equals(
						auditMessage.getEventType(),
						EventTypes.AUDIT_CONFIG_CHANGE)) {

					_auditMessages.add(auditMessage);
				}
			},
			HashMapDictionaryBuilder.<String, Object>put(
				"eventTypes", "*"
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnAfterSave() throws Exception {
		_testOnAfterSaveWithCompanyScope();
		_testOnAfterSaveWithSystemScope();
		_testOnAfterSaveWithUnchangedPseudonymizationEnabled();
		_testOnAfterSaveWithoutConfigurationFormRenderer();
	}

	private void _assertAuditMessage(
			AuditMessage auditMessage, boolean newValue, boolean oldValue)
		throws Exception {

		Assert.assertEquals(
			AuditConfiguration.class.getName(), auditMessage.getClassName());
		Assert.assertEquals(
			TestPropsValues.getCompanyId(), auditMessage.getCompanyId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), auditMessage.getUserId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"name", "pseudonymizationEnabled"
				).put(
					"newValue", newValue
				).put(
					"oldValue", oldValue
				)
			).toString(),
			String.valueOf(additionalInfoJSONObject.getJSONArray("attributes")),
			true);
	}

	private Dictionary<String, Object> _getRequestParameters(
			String portletId, boolean pseudonymizationEnabled)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setUser(TestPropsValues.getUser());

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
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockLiferayPortletActionRequest.setParameter(
			"pseudonymizationEnabled", String.valueOf(pseudonymizationEnabled));

		return new HashMapDictionary<>(
			_configurationFormRenderer.getRequestParameters(
				mockLiferayPortletActionRequest.getHttpServletRequest()));
	}

	private void _testOnAfterSaveWithCompanyScope() throws Exception {
		_auditMessages.clear();

		try {
			AuditConfiguration auditConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AuditConfiguration.class, TestPropsValues.getCompanyId());

			boolean pseudonymizationEnabled =
				auditConfiguration.pseudonymizationEnabled();

			_configurationProvider.saveCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId(),
				_getRequestParameters(
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
					!pseudonymizationEnabled));

			Assert.assertEquals(
				_auditMessages.toString(), 1, _auditMessages.size());

			_assertAuditMessage(
				_auditMessages.get(0), !pseudonymizationEnabled,
				pseudonymizationEnabled);
		}
		finally {
			_configurationProvider.deleteCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId());
		}
	}

	private void _testOnAfterSaveWithSystemScope() throws Exception {
		_auditMessages.clear();

		AuditConfiguration auditConfiguration =
			_configurationProvider.getSystemConfiguration(
				AuditConfiguration.class);

		boolean pseudonymizationEnabled =
			auditConfiguration.pseudonymizationEnabled();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					AuditConfiguration.class.getName(),
					_getRequestParameters(
						ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
						!pseudonymizationEnabled))) {

			Assert.assertEquals(
				_auditMessages.toString(), 1, _auditMessages.size());

			_assertAuditMessage(
				_auditMessages.get(0), !pseudonymizationEnabled,
				pseudonymizationEnabled);
		}
	}

	private void _testOnAfterSaveWithUnchangedPseudonymizationEnabled()
		throws Exception {

		_auditMessages.clear();

		try {
			AuditConfiguration auditConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AuditConfiguration.class, TestPropsValues.getCompanyId());

			_configurationProvider.saveCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId(),
				_getRequestParameters(
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
					auditConfiguration.pseudonymizationEnabled()));

			Assert.assertEquals(
				_auditMessages.toString(), 0, _auditMessages.size());
		}
		finally {
			_configurationProvider.deleteCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId());
		}
	}

	private void _testOnAfterSaveWithoutConfigurationFormRenderer()
		throws Exception {

		_auditMessages.clear();

		try {
			AuditConfiguration auditConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AuditConfiguration.class, TestPropsValues.getCompanyId());

			_configurationProvider.saveCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId(),
				HashMapDictionaryBuilder.<String, Object>put(
					"pseudonymizationEnabled",
					!auditConfiguration.pseudonymizationEnabled()
				).build());

			Assert.assertEquals(
				_auditMessages.toString(), 0, _auditMessages.size());
		}
		finally {
			_configurationProvider.deleteCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId());
		}
	}

	private final List<AuditMessage> _auditMessages =
		Collections.synchronizedList(new ArrayList<>());

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display.AuditConfigurationFormRenderer",
		type = ConfigurationFormRenderer.class
	)
	private ConfigurationFormRenderer _configurationFormRenderer;

	@Inject
	private ConfigurationProvider _configurationProvider;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test.BaseModelListenerTestCase;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class AuditPortalPreferencesLocalServiceWrapperTest
	extends BaseModelListenerTestCase {

	@FeatureFlag("LPD-6417")
	@Test
	public void testUpdatePreferences() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		boolean strangers = company.isStrangers();

		String key = RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();
		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				TestPropsValues.getUserId(), true);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					LoggingAuditMessageProcessorConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"logMessageFormat", "JSON"
					).build());
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.security.audit.router.internal." +
					"LoggingAuditMessageProcessor",
				LoggerTestUtil.INFO)) {

			_companyLocalService.updateSecurity(
				company.getCompanyId(), company.getAuthType(),
				company.isAutoLogin(), company.isSendPasswordResetLink(),
				!strangers, company.isStrangersWithMx(),
				company.isStrangersVerify(), company.isSiteLogo());

			Assert.assertEquals(
				auditMessages.toString(), 1, auditMessages.size());

			AuditMessage auditMessage = fetchAuditMessage(
				PortalPreferences.class.getName(), EventTypes.UPDATE);

			Assert.assertEquals(
				TestPropsValues.getCompanyId(), auditMessage.getCompanyId());
			Assert.assertEquals(
				"instance.portalpreferences.update",
				auditMessage.getResourceAction());

			JSONAssert.assertEquals(
				JSONUtil.put(
					"attributes",
					JSONUtil.putAll(
						JSONUtil.put(
							"name", PropsKeys.COMPANY_SECURITY_STRANGERS
						).put(
							"newValue", ""
						).put(
							"oldValue", ""
						))
				).toString(),
				String.valueOf(auditMessage.getAdditionalInfo()),
				JSONCompareMode.NON_EXTENSIBLE);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			JSONAssert.assertEquals(
				String.valueOf(auditMessage.toJSONObject()),
				logEntry.getMessage(), JSONCompareMode.NON_EXTENSIBLE);

			portalPreferences.setValue(
				namespace, key, RandomTestUtil.randomString());

			Assert.assertEquals(
				auditMessages.toString(), 1, auditMessages.size());
		}
		finally {
			portalPreferences.setValue(namespace, key, null);

			_companyLocalService.updateSecurity(
				company.getCompanyId(), company.getAuthType(),
				company.isAutoLogin(), company.isSendPasswordResetLink(),
				strangers, company.isStrangersWithMx(),
				company.isStrangersVerify(), company.isSiteLogo());
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portlet.passwordpoliciesadmin.util.test.PasswordPolicyTestUtil;

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
public class PasswordPolicyModelListenerTest extends BaseModelListenerTestCase {

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnBeforeCreate() throws Exception {
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

			_passwordPolicy = PasswordPolicyTestUtil.addPasswordPolicy(
				ServiceContextTestUtil.getServiceContext());

			AuditMessage auditMessage = fetchAuditMessage(
				PasswordPolicy.class.getName(), EventTypes.ADD);

			_assertAuditMessage(auditMessage, _passwordPolicy);

			Assert.assertEquals(
				"instance.passwordpolicy.add",
				auditMessage.getResourceAction());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			JSONAssert.assertEquals(
				String.valueOf(auditMessage.toJSONObject()),
				logEntry.getMessage(), JSONCompareMode.NON_EXTENSIBLE);
		}
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		PasswordPolicy passwordPolicy =
			PasswordPolicyTestUtil.addPasswordPolicy(
				ServiceContextTestUtil.getServiceContext());

		_passwordPolicyLocalService.deletePasswordPolicy(passwordPolicy);

		_assertAuditMessage(
			fetchAuditMessage(
				PasswordPolicy.class.getName(), EventTypes.DELETE),
			passwordPolicy);
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_passwordPolicy = PasswordPolicyTestUtil.addPasswordPolicy(
			ServiceContextTestUtil.getServiceContext());

		auditMessages.clear();

		_passwordPolicy = _passwordPolicyLocalService.updatePasswordPolicy(
			_passwordPolicy);

		Assert.assertNull(
			fetchAuditMessage(
				PasswordPolicy.class.getName(), EventTypes.UPDATE));

		int minLength = _passwordPolicy.getMinLength();

		_passwordPolicy.setMinLength(minLength + 1);

		_company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_passwordPolicy = _passwordPolicyLocalService.updatePasswordPolicy(
				_passwordPolicy);
		}

		AuditMessage auditMessage = fetchAuditMessage(
			PasswordPolicy.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(
			_passwordPolicy.getCompanyId(), auditMessage.getCompanyId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		JSONAssert.assertEquals(
			JSONUtil.put(
				JSONUtil.put(
					"name", "minLength"
				).put(
					"newValue", String.valueOf(minLength + 1)
				).put(
					"oldValue", String.valueOf(minLength)
				)
			).toString(),
			String.valueOf(additionalInfoJSONObject.getJSONArray("attributes")),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private void _assertAuditMessage(
			AuditMessage auditMessage, PasswordPolicy passwordPolicy)
		throws Exception {

		Assert.assertEquals(
			passwordPolicy.getCompanyId(), auditMessage.getCompanyId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"name", passwordPolicy.getName()
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private PasswordPolicy _passwordPolicy;

	@Inject
	private PasswordPolicyLocalService _passwordPolicyLocalService;

}
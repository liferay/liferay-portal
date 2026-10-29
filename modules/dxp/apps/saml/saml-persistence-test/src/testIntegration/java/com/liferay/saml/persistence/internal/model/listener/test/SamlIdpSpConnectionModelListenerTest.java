/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;

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
public class SamlIdpSpConnectionModelListenerTest
	extends BaseModelListenerTestCase {

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnAfterCreate() throws Exception {
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

			_samlIdpSpConnection = _addSamlIdpSpConnection();

			AuditMessage auditMessage = fetchAuditMessage(
				SamlIdpSpConnection.class.getName(), EventTypes.ADD);

			_assertAuditMessage(auditMessage, _samlIdpSpConnection);

			Assert.assertEquals(
				"instance.samlidpspconnection.add",
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
		SamlIdpSpConnection samlIdpSpConnection = _addSamlIdpSpConnection();

		_samlIdpSpConnectionLocalService.deleteSamlIdpSpConnection(
			samlIdpSpConnection.getSamlIdpSpConnectionId());

		_assertAuditMessage(
			fetchAuditMessage(
				SamlIdpSpConnection.class.getName(), EventTypes.DELETE),
			samlIdpSpConnection);
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_samlIdpSpConnection = _addSamlIdpSpConnection();

		_samlIdpSpConnection =
			_samlIdpSpConnectionLocalService.updateSamlIdpSpConnection(
				_samlIdpSpConnection.getSamlIdpSpConnectionId(),
				_samlIdpSpConnection.getSamlSpEntityId(),
				_samlIdpSpConnection.getAssertionLifetime(),
				_samlIdpSpConnection.getAttributeNames(),
				_samlIdpSpConnection.isAttributesEnabled(),
				_samlIdpSpConnection.isAttributesNamespaceEnabled(),
				_samlIdpSpConnection.isEnabled(),
				_samlIdpSpConnection.isEncryptionForced(),
				_samlIdpSpConnection.getMetadataUrl(), null,
				_samlIdpSpConnection.getName(),
				_samlIdpSpConnection.getNameIdAttribute(),
				_samlIdpSpConnection.getNameIdFormat(),
				ServiceContextTestUtil.getServiceContext());

		Assert.assertNull(
			fetchAuditMessage(
				SamlIdpSpConnection.class.getName(), EventTypes.UPDATE));

		_samlIdpSpConnection.setMetadataXml(RandomTestUtil.randomString());

		String name = _samlIdpSpConnection.getName();

		String newName = RandomTestUtil.randomString();

		_samlIdpSpConnection.setName(newName);

		_company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_samlIdpSpConnection =
				_samlIdpSpConnectionLocalService.updateSamlIdpSpConnection(
					_samlIdpSpConnection);
		}

		AuditMessage auditMessage = fetchAuditMessage(
			SamlIdpSpConnection.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(
			_samlIdpSpConnection.getCompanyId(), auditMessage.getCompanyId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"name", "metadataXml"
				).put(
					"newValue", ""
				).put(
					"oldValue", ""
				),
				JSONUtil.put(
					"name", "name"
				).put(
					"newValue", newName
				).put(
					"oldValue", name
				)
			).toString(),
			String.valueOf(additionalInfoJSONObject.getJSONArray("attributes")),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private SamlIdpSpConnection _addSamlIdpSpConnection() throws Exception {
		SamlIdpSpConnection samlIdpSpConnection =
			_samlIdpSpConnectionLocalService.createSamlIdpSpConnection(
				RandomTestUtil.nextLong());

		samlIdpSpConnection.setCompanyId(TestPropsValues.getCompanyId());
		samlIdpSpConnection.setSamlSpEntityId(RandomTestUtil.randomString());
		samlIdpSpConnection.setMetadataXml(RandomTestUtil.randomString());
		samlIdpSpConnection.setName(RandomTestUtil.randomString());

		return _samlIdpSpConnectionLocalService.addSamlIdpSpConnection(
			samlIdpSpConnection);
	}

	private void _assertAuditMessage(
			AuditMessage auditMessage, SamlIdpSpConnection samlIdpSpConnection)
		throws Exception {

		Assert.assertEquals(
			samlIdpSpConnection.getCompanyId(), auditMessage.getCompanyId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"name", samlIdpSpConnection.getName()
			).put(
				"samlSpEntityId", samlIdpSpConnection.getSamlSpEntityId()
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private SamlIdpSpConnection _samlIdpSpConnection;

	@Inject
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

}
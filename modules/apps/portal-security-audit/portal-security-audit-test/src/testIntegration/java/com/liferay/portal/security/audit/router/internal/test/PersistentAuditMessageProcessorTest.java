/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class PersistentAuditMessageProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testProcessCorrelationIdPersistsAfterClear() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PersistentAuditMessageProcessorConfiguration.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"bufferSize", 1
					).build())) {

			_route(TestPropsValues.getCompanyId(), _createEventType());
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PersistentAuditMessageProcessorConfiguration.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"bufferSize", 2
					).put(
						"flushInterval", _FLUSH_INTERVAL
					).build())) {

			String correlationId = RandomTestUtil.randomString();

			AuditRequestContext auditRequestContext =
				AuditRequestContextThreadLocal.getAuditRequestContext();

			auditRequestContext.setCorrelationId(correlationId);

			String eventType1 = _createEventType();

			_route(TestPropsValues.getCompanyId(), eventType1);

			auditRequestContext.setCorrelationId(null);

			Assert.assertEquals(
				0,
				_getAuditEventsCount(
					TestPropsValues.getCompanyId(), eventType1));

			String eventType2 = _createEventType();

			_route(TestPropsValues.getCompanyId(), eventType2);

			AuditEvent auditEvent1 = _getAuditEvent(
				TestPropsValues.getCompanyId(), eventType1);
			AuditEvent auditEvent2 = _getAuditEvent(
				TestPropsValues.getCompanyId(), eventType2);

			Assert.assertEquals(correlationId, auditEvent1.getCorrelationId());
			Assert.assertEquals(
				StringPool.BLANK, auditEvent2.getCorrelationId());

			AuditRequestContextThreadLocal.removeAuditRequestContext();
		}
	}

	private String _createEventType() {
		return StringUtil.toUpperCase(RandomTestUtil.randomString());
	}

	private AuditEvent _getAuditEvent(long companyId, String eventType) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			List<AuditEvent> auditEvents =
				_auditEventLocalService.getAuditEvents(
					companyId, 0, 0, null, null, null, null, null, null, null,
					null, null, eventType, null, 0, null, false, 0, 1);

			return auditEvents.get(0);
		}
	}

	private int _getAuditEventsCount(long companyId, String eventType) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			return _auditEventLocalService.getAuditEventsCount(
				companyId, 0, 0, null, null, null, null, null, null, null, null,
				null, eventType, null, 0, null, false);
		}
	}

	private void _route(long companyId, String eventType) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			_auditRouter.route(
				new AuditMessage(
					companyId, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), eventType));
		}
	}

	private static final long _FLUSH_INTERVAL = 86400000;

	@Inject
	private AuditEventLocalService _auditEventLocalService;

	@Inject
	private AuditRouter _auditRouter;

}
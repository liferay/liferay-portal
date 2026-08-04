/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company1 = CompanyTestUtil.addCompany();
		_company2 = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		CompanyLocalServiceUtil.deleteCompany(_company1.getCompanyId());
		CompanyLocalServiceUtil.deleteCompany(_company2.getCompanyId());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessDoesNotDropBufferedEventsOnDisable()
		throws Exception {

		String eventType = _createEventType();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company1.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"bufferSize", 2000
						).put(
							"enabled", true
						).put(
							"flushInterval", _FLUSH_INTERVAL
						).build())) {

			_route(_company1.getCompanyId(), eventType);

			Assert.assertEquals(
				0, _getAuditEventsCount(_company1.getCompanyId(), eventType));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company1.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			_route(_company1.getCompanyId(), _createEventType());

			Assert.assertEquals(
				0, _getAuditEventsCount(_company1.getCompanyId(), eventType));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company1.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"bufferSize", 1
						).put(
							"enabled", true
						).put(
							"flushInterval", _FLUSH_INTERVAL
						).build())) {

			_route(_company1.getCompanyId(), _createEventType());

			Assert.assertEquals(
				1, _getAuditEventsCount(_company1.getCompanyId(), eventType));
		}
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessDoesNotDropBufferedEventsOnReconfigure()
		throws Exception {

		String eventType = _createEventType();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company1.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"bufferSize", 2000
						).put(
							"flushInterval", _FLUSH_INTERVAL
						).build())) {

			_route(_company1.getCompanyId(), eventType);

			Assert.assertEquals(
				0, _getAuditEventsCount(_company1.getCompanyId(), eventType));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company1.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"bufferSize", 1
						).put(
							"flushInterval", _FLUSH_INTERVAL
						).build())) {

			_route(_company1.getCompanyId(), _createEventType());

			Assert.assertEquals(
				1, _getAuditEventsCount(_company1.getCompanyId(), eventType));
		}
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessWhenBufferSizesAreDifferent() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper1 =
					new CompanyConfigurationTemporarySwapper(
						_company1.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"bufferSize", 1
						).put(
							"flushInterval", _FLUSH_INTERVAL
						).build());
			CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper2 =
					new CompanyConfigurationTemporarySwapper(
						_company2.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"bufferSize", 2000
						).put(
							"flushInterval", _FLUSH_INTERVAL
						).build())) {

			String eventType1 = _createEventType();
			String eventType2 = _createEventType();

			_route(_company1.getCompanyId(), eventType1);
			_route(_company2.getCompanyId(), eventType2);

			Assert.assertEquals(
				1, _getAuditEventsCount(_company1.getCompanyId(), eventType1));
			Assert.assertEquals(
				0, _getAuditEventsCount(_company2.getCompanyId(), eventType2));
		}
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessWhenEnabledValuesAreDifferent() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper1 =
					new CompanyConfigurationTemporarySwapper(
						_company1.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"bufferSize", 1
						).put(
							"enabled", true
						).build());
			CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper2 =
					new CompanyConfigurationTemporarySwapper(
						_company2.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"bufferSize", 1
						).put(
							"enabled", false
						).build())) {

			String eventType1 = _createEventType();
			String eventType2 = _createEventType();

			_route(_company1.getCompanyId(), eventType1);
			_route(_company2.getCompanyId(), eventType2);

			Assert.assertEquals(
				1, _getAuditEventsCount(_company1.getCompanyId(), eventType1));
			Assert.assertEquals(
				0, _getAuditEventsCount(_company2.getCompanyId(), eventType2));
		}
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testProcessWhenFeatureFlagIsDisabled() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company2.getCompanyId(),
						PersistentAuditMessageProcessorConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build());
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PersistentAuditMessageProcessorConfiguration.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"bufferSize", 1
					).build())) {

			String eventType = _createEventType();

			_route(_company2.getCompanyId(), eventType);

			Assert.assertEquals(
				1, _getAuditEventsCount(_company2.getCompanyId(), eventType));
		}
	}

	private String _createEventType() {
		return StringUtil.toUpperCase(RandomTestUtil.randomString());
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

	private static Company _company1;
	private static Company _company2;

	@Inject
	private AuditEventLocalService _auditEventLocalService;

	@Inject
	private AuditRouter _auditRouter;

}
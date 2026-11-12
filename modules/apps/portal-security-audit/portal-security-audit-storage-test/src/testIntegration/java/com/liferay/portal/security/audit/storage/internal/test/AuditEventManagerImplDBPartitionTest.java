/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.AuditEventManager;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class AuditEventManagerImplDBPartitionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);
	}

	@Test
	public void testAddAuditEvents() throws Exception {
		String eventType = StringUtil.toUpperCase(
			RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				PortalInstancePool.getDefaultCompanyId())) {

			_auditEventManager.addAuditEvents(
				Arrays.asList(
					new AuditMessage(
						RandomTestUtil.nextLong(), RandomTestUtil.randomLong(),
						RandomTestUtil.randomString(), eventType),
					new AuditMessage(
						TestPropsValues.getCompanyId(),
						RandomTestUtil.randomLong(),
						RandomTestUtil.randomString(), eventType)));
		}

		Assert.assertEquals(
			1,
			_auditEventLocalService.getAuditEventsCount(
				TestPropsValues.getCompanyId(), 0, 0, null, null, null, null,
				null, null, null, null, null, eventType, null, 0, null, false));
	}

	@Inject
	private AuditEventLocalService _auditEventLocalService;

	@Inject
	private AuditEventManager _auditEventManager;

}
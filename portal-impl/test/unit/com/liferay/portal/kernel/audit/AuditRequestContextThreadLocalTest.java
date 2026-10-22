/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.audit;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christian Moura
 */
public class AuditRequestContextThreadLocalTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAuditRequestContext() {
		AuditRequestContext auditRequestContext1 =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		Assert.assertNotNull(auditRequestContext1.getRequestId());
		Assert.assertTrue(auditRequestContext1.isRequestIdGenerated());

		Assert.assertSame(
			auditRequestContext1,
			AuditRequestContextThreadLocal.getAuditRequestContext());

		AuditRequestContextThreadLocal.removeAuditRequestContext();

		AuditRequestContext auditRequestContext2 =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		Assert.assertNotSame(auditRequestContext1, auditRequestContext2);
		Assert.assertNotEquals(
			auditRequestContext1.getRequestId(),
			auditRequestContext2.getRequestId());

		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

	@Test
	public void testSetAuditRequestContextWithSafeCloseable() {
		AuditRequestContextThreadLocal.removeAuditRequestContext();

		AuditRequestContext auditRequestContext1 = new AuditRequestContext();

		try (SafeCloseable safeCloseable =
				AuditRequestContextThreadLocal.
					setAuditRequestContextWithSafeCloseable(
						auditRequestContext1)) {

			Assert.assertSame(
				auditRequestContext1,
				AuditRequestContextThreadLocal.getAuditRequestContext());
		}

		Assert.assertNotSame(
			auditRequestContext1,
			AuditRequestContextThreadLocal.getAuditRequestContext());

		AuditRequestContext auditRequestContext2 =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		try (SafeCloseable safeCloseable =
				AuditRequestContextThreadLocal.
					setAuditRequestContextWithSafeCloseable(
						auditRequestContext1)) {

			Assert.assertSame(
				auditRequestContext1,
				AuditRequestContextThreadLocal.getAuditRequestContext());
		}

		Assert.assertSame(
			auditRequestContext2,
			AuditRequestContextThreadLocal.getAuditRequestContext());

		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

}
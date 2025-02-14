/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.db.partition.CompanyThreadLocalRunnable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class CompanyThreadLocalRunnableTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testInheritCompanyThreadLocal() throws Exception {
		Long companyId = CompanyThreadLocal.getCompanyId();

		AtomicLong capturedCompanyId = new AtomicLong();

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Future<?> future = executorService.submit(
			new CompanyThreadLocalRunnable(
				() -> capturedCompanyId.set(
					CompanyThreadLocal.getCompanyId())));

		future.get();

		executorService.shutdown();

		Assert.assertEquals(companyId, Long.valueOf(capturedCompanyId.get()));
	}

}
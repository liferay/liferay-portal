/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author István András Dézsi
 */
public class CompanyInheritableThreadLocalCallableTest {

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(ProxyFactory.newDummyInstance(Props.class));
	}

	@Test
	public void testInheritCompanyThreadLocal() throws Exception {
		CompanyThreadLocal.setCompanyId(1L);

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Future<Long> future = executorService.submit(
			new CompanyInheritableThreadLocalCallable<>(
				CompanyThreadLocal::getCompanyId));

		executorService.shutdown();

		Assert.assertEquals(Long.valueOf(1), future.get());
	}

}
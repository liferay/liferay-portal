/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author István András Dézsi
 * @author Alberto Chaparro
 */
public class CompanyInheritableThreadLocalCallableTest {

	@Before
	public void setUp() {
		MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
			Mockito.mockStatic(PortalInstancePool.class);

		portalInstancePoolMockedStatic.when(
			PortalInstancePool::getDefaultCompanyId
		).thenReturn(
			1L
		);

		PropsUtil.setProps(ProxyFactory.newDummyInstance(Props.class));
	}

	@Test
	public void test() throws Exception {
		_test(1L, false);
		_test(2L, true);
		_test(CompanyConstants.SYSTEM, false);
	}

	private void _test(Long companyId, boolean locked)
		throws Exception {

		CompanyThreadLocal.setCompanyId(companyId);

		Assert.assertEquals(
			companyId,
			new CompanyInheritableThreadLocalCallable<Long>(
				CompanyThreadLocal::getCompanyId
			).call());

		Assert.assertEquals(
			locked,
			new CompanyInheritableThreadLocalCallable<>(
				CompanyThreadLocal::isLocked
			).call());
	}

}
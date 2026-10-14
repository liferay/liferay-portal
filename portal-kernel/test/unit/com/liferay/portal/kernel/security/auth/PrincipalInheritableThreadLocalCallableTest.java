/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Feliphe Marinho
 */
public class PrincipalInheritableThreadLocalCallableTest {

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(null);
		PrincipalThreadLocal.setName(null);
	}

	@Test
	public void testCall() throws Exception {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		PrincipalThreadLocal.setName("1");

		PrincipalInheritableThreadLocalCallable<Object[]>
			principalInheritableThreadLocalCallable =
				new PrincipalInheritableThreadLocalCallable<>(
					() -> new Object[] {
						PrincipalThreadLocal.getName(),
						PermissionThreadLocal.getPermissionChecker()
					});

		PermissionChecker originalPermissionChecker = Mockito.mock(
			PermissionChecker.class);

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);

		PrincipalThreadLocal.setName("2");

		Assert.assertArrayEquals(
			new Object[] {"1", permissionChecker},
			principalInheritableThreadLocalCallable.call());

		Assert.assertEquals("2", PrincipalThreadLocal.getName());
		Assert.assertSame(
			originalPermissionChecker,
			PermissionThreadLocal.getPermissionChecker());
	}

}
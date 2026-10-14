/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import java.util.concurrent.Callable;

/**
 * @author Feliphe Marinho
 */
public class PrincipalInheritableThreadLocalCallable<T> implements Callable<T> {

	public PrincipalInheritableThreadLocalCallable(Callable<T> callable) {
		_callable = new CompanyInheritableThreadLocalCallable<>(callable);

		_name = PrincipalThreadLocal.getName();
		_permissionChecker = PermissionThreadLocal.getPermissionChecker();
	}

	@Override
	public T call() throws Exception {
		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PrincipalThreadLocal.setName(_name);
			PermissionThreadLocal.setPermissionChecker(_permissionChecker);

			return _callable.call();
		}
		finally {
			PrincipalThreadLocal.setName(originalName);
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private final Callable<T> _callable;
	private final String _name;
	private final PermissionChecker _permissionChecker;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.contributor;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Sergio González
 */
@ProviderType
public interface PermissionSQLContributor {

	public void collectPermittedClassPKs(
		String className, long userId, long[] groupIds,
		Set<Long> permittedClassPKs);

	public Predicate getPermissionPredicate(
		PermissionChecker permissionChecker, String className,
		Column<?, Long> classPKColumn, long[] groupIds);

	public String getPermissionSQL(
		String className, String classPKField, String groupIdField,
		long[] groupIds);

}
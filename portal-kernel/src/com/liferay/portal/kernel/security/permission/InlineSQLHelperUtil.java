/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.permission;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * @author Raymond Augé
 * @see    InlineSQLHelper
 */
public class InlineSQLHelperUtil {

	public static <T extends BaseModel<T>> List<T> filter(
		List<T> list, long... groupIds) {

		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.filter(list, groupIds);
	}

	public static <T extends Table<T>> Predicate getPermissionWherePredicate(
		Class<?> modelClass, Column<T, Long> classPKColumn, long... groupIds) {

		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.getPermissionWherePredicate(
			modelClass, classPKColumn, groupIds);
	}

	public static boolean isEnabled() {
		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.isEnabled();
	}

	public static boolean isEnabled(long groupId) {
		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.isEnabled(groupId);
	}

	public static boolean isEnabled(long companyId, long groupId) {
		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.isEnabled(companyId, groupId);
	}

	public static boolean isEnabled(long[] groupIds) {
		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.isEnabled(groupIds);
	}

	public static <T extends Table<T>> DSLQuery replacePermissionCheck(
		DSLQuery dslQuery, Class<?> modelClass, Column<T, Long> classPKColumn,
		long... groupIds) {

		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.replacePermissionCheck(
			dslQuery, modelClass, classPKColumn, groupIds);
	}

	public static String replacePermissionCheck(
		String sql, String className, String classPKField) {

		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.replacePermissionCheck(
			sql, className, classPKField);
	}

	public static String replacePermissionCheck(
		String sql, String className, String classPKField, long groupId) {

		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.replacePermissionCheck(
			sql, className, classPKField, groupId);
	}

	public static String replacePermissionCheck(
		String sql, String className, String classPKField, long[] groupIds) {

		InlineSQLHelper inlineSQLPermission =
			_inlineSQLPermissionSnapshot.get();

		return inlineSQLPermission.replacePermissionCheck(
			sql, className, classPKField, groupIds);
	}

	private static final Snapshot<InlineSQLHelper>
		_inlineSQLPermissionSnapshot = new Snapshot<>(
			InlineSQLHelperUtil.class, InlineSQLHelper.class);

}
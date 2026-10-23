/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionFinder;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.ResourcePermissionImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class ResourcePermissionFinderImpl
	extends ResourcePermissionFinderBaseImpl
	implements ResourcePermissionFinder {

	public static final String COUNT_BY_C_N_S_P_R_A =
		ResourcePermissionFinder.class.getName() + ".countByC_N_S_P_R_A";

	public static final String FIND_BY_R_S =
		ResourcePermissionFinder.class.getName() + ".findByR_S";

	public static final FinderPath FINDER_PATH_COUNT_BY_C_N_S_P_R_A =
		new FinderPath(
			ResourcePermissionPersistenceImpl.
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"countByC_N_S_P_R_A",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"companyId", "name", "scope", "primKey", "roleId", "actionIds"
			},
			false);

	@Override
	public int countByC_N_S_P_R_A(
		long companyId, String name, int scope, String primKey, long[] roleIds,
		long actionId) {

		Object[] finderArgs = {
			companyId, name, scope, primKey, StringUtil.merge(roleIds), actionId
		};

		Long count = (Long)FinderCacheUtil.getResult(
			FINDER_PATH_COUNT_BY_C_N_S_P_R_A, finderArgs,
			ResourcePermissionUtil.getPersistence());

		if (count != null) {
			return count.intValue();
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_C_N_S_P_R_A);

			if (roleIds.length > 1) {
				StringBundler sb = new StringBundler((roleIds.length * 2) - 1);

				for (int i = 0; i < roleIds.length; i++) {
					if (i > 0) {
						sb.append(" OR ");
					}

					sb.append("ResourcePermission.roleId = ?");
				}

				sql = StringUtil.replace(
					sql, "ResourcePermission.roleId = ?", sb.toString());
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(name);
			queryPos.add(scope);
			queryPos.add(primKey);
			queryPos.add(roleIds);
			queryPos.add(actionId);
			queryPos.add(actionId);

			count = (Long)sqlQuery.uniqueResult();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			if (count == null) {
				count = Long.valueOf(0);
			}

			FinderCacheUtil.putResult(
				FINDER_PATH_COUNT_BY_C_N_S_P_R_A, finderArgs, count);

			closeSession(session);
		}

		return count.intValue();
	}

	@Override
	public List<ResourcePermission> findByR_S(
		long roleId, int[] scopes, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_R_S);

			sql = StringUtil.replace(sql, "[$SCOPE$]", getScopes(scopes));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"ResourcePermission", ResourcePermissionImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(roleId);
			queryPos.add(scopes);

			return (List<ResourcePermission>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String getScopes(int[] scopes) {
		if (scopes.length == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((scopes.length * 2) + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < scopes.length; i++) {
			sb.append("ResourcePermission.scope = ? ");

			if ((i + 1) == scopes.length) {
				continue;
			}

			sb.append("OR ");
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

}
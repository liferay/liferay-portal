/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.persistence.PasswordPolicyFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.model.impl.PasswordPolicyImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.Iterator;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class PasswordPolicyFinderImpl
	extends PasswordPolicyFinderBaseImpl implements PasswordPolicyFinder {

	public static final String COUNT_BY_C_N =
		PasswordPolicyFinder.class.getName() + ".countByC_N";

	public static final String FIND_BY_C_N =
		PasswordPolicyFinder.class.getName() + ".findByC_N";

	@Override
	public int countByC_N(long companyId, String name) {
		return doCountByC_N(companyId, name, false);
	}

	@Override
	public int filterCountByC_N(long companyId, String name) {
		return doCountByC_N(companyId, name, true);
	}

	@Override
	public List<PasswordPolicy> filterFindByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator) {

		return doFindByC_N(
			companyId, name, start, end, orderByComparator, true);
	}

	@Override
	public List<PasswordPolicy> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator) {

		return doFindByC_N(
			companyId, name, start, end, orderByComparator, false);
	}

	protected int doCountByC_N(
		long companyId, String name, boolean inlineSQLHelper) {

		name = CustomSQLUtil.keywords(name)[0];

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_C_N);

			if (inlineSQLHelper &&
				InlineSQLHelperUtil.isEnabled(companyId, 0)) {

				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, PasswordPolicy.class.getName(),
					"PasswordPolicy.passwordPolicyId");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(name);
			queryPos.add(name);

			Iterator<Long> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<PasswordPolicy> doFindByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator,
		boolean inlineSQLHelper) {

		name = CustomSQLUtil.keywords(name)[0];

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_N);

			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			if (inlineSQLHelper &&
				InlineSQLHelperUtil.isEnabled(companyId, 0)) {

				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, PasswordPolicy.class.getName(),
					"PasswordPolicy.passwordPolicyId");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("PasswordPolicy", PasswordPolicyImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(name);
			queryPos.add(name);

			return (List<PasswordPolicy>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

}
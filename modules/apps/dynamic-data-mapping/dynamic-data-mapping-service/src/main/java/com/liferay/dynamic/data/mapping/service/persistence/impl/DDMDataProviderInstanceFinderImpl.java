/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(service = DDMDataProviderInstanceFinder.class)
public class DDMDataProviderInstanceFinderImpl
	extends DDMDataProviderInstanceFinderBaseImpl
	implements DDMDataProviderInstanceFinder {

	public static final String COUNT_BY_C_G_N_D =
		DDMDataProviderInstanceFinder.class.getName() + ".countByC_G_N_D";

	public static final String FIND_BY_C_G_N_D =
		DDMDataProviderInstanceFinder.class.getName() + ".findByC_G_N_D";

	@Override
	public List<DDMDataProviderInstance> filterByC_G(
		long companyId, long[] groupIds, int start, int end) {

		return filterByKeywords(companyId, groupIds, null, start, end, null);
	}

	@Override
	public List<DDMDataProviderInstance> filterByKeywords(
		long companyId, long[] groupIds, String keywords, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return _findByC_G_N_D(
			companyId, groupIds, names, descriptions, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, long[] groupIds, String keywords) {

		return _countByKeywords(companyId, groupIds, keywords);
	}

	@Override
	public int filterCountByC_G(long companyId, long[] groupIds) {
		return _countByKeywords(companyId, groupIds, null);
	}

	@Override
	public int filterCountByC_G_N_D(
		long companyId, long[] groupIds, String name, String description,
		boolean andOperator) {

		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description, false);

		return _countByC_G_N_D(
			companyId, groupIds, names, descriptions, andOperator);
	}

	@Override
	public List<DDMDataProviderInstance> filterFindByC_G_N_D(
		long companyId, long[] groupIds, String name, String description,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description, false);

		return _findByC_G_N_D(
			companyId, groupIds, names, descriptions, andOperator, start, end,
			orderByComparator);
	}

	private int _countByKeywords(
		long companyId, long[] groupIds, String keywords) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return _countByC_G_N_D(
			companyId, groupIds, names, descriptions, andOperator);
	}

	private int _countByC_G_N_D(
		long companyId, long[] groupIds, String[] names, String[] descriptions,
		boolean andOperator) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_C_G_N_D);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, DDMDataProviderInstance.class.getName(),
				"DDMDataProviderInstance.dataProviderInstanceId", groupIds);
			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", _getGroupIds(groupIds));
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(DDMDataProviderInstance.name)", StringPool.LIKE,
				false, names);
			sql = _customSQL.replaceKeywords(
				sql, "DDMDataProviderInstance.description", StringPool.LIKE,
				true, descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (groupIds != null) {
				queryPos.add(groupIds);
			}

			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

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

	private List<DDMDataProviderInstance> _findByC_G_N_D(
		long companyId, long[] groupIds, String[] names, String[] descriptions,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_G_N_D);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, DDMDataProviderInstance.class.getName(),
				"DDMDataProviderInstance.dataProviderInstanceId", groupIds);
			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", _getGroupIds(groupIds));
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(DDMDataProviderInstance.name)", StringPool.LIKE,
				false, names);
			sql = _customSQL.replaceKeywords(
				sql, "DDMDataProviderInstance.description", StringPool.LIKE,
				true, descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);
			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"DDMDataProviderInstance", DDMDataProviderInstanceImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (groupIds != null) {
				queryPos.add(groupIds);
			}

			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			return (List<DDMDataProviderInstance>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private String _getGroupIds(long[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(groupIds.length + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (groupIds.length - 1); i++) {
			sb.append("DDMDataProviderInstance.groupId = ? OR ");
		}

		sb.append("DDMDataProviderInstance.groupId = ?) AND");

		return sb.toString();
	}

	@Reference
	private CustomSQL _customSQL;

}
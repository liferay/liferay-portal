/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkImpl;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkFinder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = DDMStructureLinkFinder.class)
public class DDMStructureLinkFinderImpl
	extends DDMStructureLinkFinderBaseImpl implements DDMStructureLinkFinder {

	public static final String COUNT_BY_C_C_N_D =
		DDMStructureLinkFinder.class.getName() + ".countByC_C_N_D";

	public static final String FIND_BY_C_C_N_D =
		DDMStructureLinkFinder.class.getName() + ".findByC_C_N_D";

	@Override
	public int filterCountByKeywords(
		long classNameId, long classPK, long[] groupIds, String keywords,
		String resourceClassName) {

		return _countByKeywords(
			classNameId, classPK, groupIds, keywords, resourceClassName);
	}

	@Override
	public List<DDMStructureLink> filterFindByKeywords(
		long classNameId, long classPK, long[] groupIds, String keywords,
		String resourceClassName, int start, int end,
		OrderByComparator<DDMStructureLink> orderByComparator) {

		return _findByKeywords(
			classNameId, classPK, groupIds, keywords, resourceClassName, start,
			end, orderByComparator);
	}

	private int _countByKeywords(
		long classNameId, long classPK, long[] groupIds, String keywords,
		String resourceClassName) {

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

		return _countByC_C_N_D(
			classNameId, classPK, groupIds, names, descriptions,
			resourceClassName, andOperator);
	}

	private int _countByC_C_N_D(
		long classNameId, long classPK, long[] groupIds, String[] names,
		String[] descriptions, String resourceClassName, boolean andOperator) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_C_C_N_D);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql,
				_ddmPermissionSupport.getStructureModelResourceName(
					resourceClassName),
				"DDMStructure.structureId", groupIds);

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(CAST_TEXT(DDMStructure.name))", StringPool.LIKE,
				false, names);

			sql = _customSQL.replaceKeywords(
				sql, "DDMStructure.description", StringPool.LIKE, true,
				descriptions);

			sql = _customSQL.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classNameId);
			queryPos.add(classPK);
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

	private List<DDMStructureLink> _findByKeywords(
		long classNameId, long classPK, long[] groupIds, String keywords,
		String resourceClassName, int start, int end,
		OrderByComparator<DDMStructureLink> orderByComparator) {

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

		return _findByC_C_N_D(
			classNameId, classPK, groupIds, names, descriptions,
			resourceClassName, andOperator, start, end, orderByComparator);
	}

	private List<DDMStructureLink> _findByC_C_N_D(
		long classNameId, long classPK, long[] groupIds, String[] names,
		String[] descriptions, String resourceClassName, boolean andOperator,
		int start, int end,
		OrderByComparator<DDMStructureLink> orderByComparator) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_C_N_D);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql,
				_ddmPermissionSupport.getStructureModelResourceName(
					resourceClassName),
				"DDMStructure.structureId", groupIds);

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(CAST_TEXT(DDMStructure.name))", StringPool.LIKE,
				false, names);
			sql = _customSQL.replaceKeywords(
				sql, "DDMStructure.description", StringPool.LIKE, true,
				descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			if (orderByComparator != null) {
				sql = _customSQL.replaceOrderBy(sql, orderByComparator);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("DDMStructureLink", DDMStructureLinkImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classNameId);
			queryPos.add(classPK);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			return (List<DDMStructureLink>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DDMPermissionSupport _ddmPermissionSupport;

}
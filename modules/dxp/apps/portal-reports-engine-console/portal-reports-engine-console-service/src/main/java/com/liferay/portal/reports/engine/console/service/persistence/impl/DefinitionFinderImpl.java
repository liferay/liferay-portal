/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.persistence.impl;

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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.model.impl.DefinitionImpl;
import com.liferay.portal.reports.engine.console.service.persistence.DefinitionFinder;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = DefinitionFinder.class)
public class DefinitionFinderImpl
	extends DefinitionFinderBaseImpl implements DefinitionFinder {

	public static final String COUNT_BY_G_S_N_D_RN =
		DefinitionFinder.class.getName() + ".countByG_S_N_D_RN";

	public static final String FIND_BY_G_S_N_D_RN =
		DefinitionFinder.class.getName() + ".findByG_S_N_D_RN";

	@Override
	public int filterCountByG_S_N_D_RN(
		long groupId, String name, String description, long sourceId,
		String reportName, boolean andOperator) {

		return _countByG_S_N_D_RN(
			groupId, name, description, sourceId, reportName, andOperator);
	}

	@Override
	public List<Definition> filterFindByG_S_N_D_RN(
		long groupId, String name, String description, long sourceId,
		String reportName, boolean andOperator, int start, int end,
		OrderByComparator<Definition> orderByComparator) {

		return _findByG_S_N_D_RN(
			groupId, name, description, sourceId, reportName, andOperator,
			start, end, orderByComparator);
	}

	private int _countByG_S_N_D_RN(
		long groupId, String name, String description, long sourceId,
		String reportName, boolean andOperator) {

		if (Validator.isNull(name) || Validator.isNull(description) ||
			Validator.isNull(reportName)) {

			andOperator = true;
		}

		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description);
		String[] reportNames = _customSQL.keywords(reportName);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_S_N_D_RN);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, Definition.class.getName(),
				"Reports_Definition.definitionId", groupId);

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(Reports_Definition.groupId = ?) AND");
			}

			if (sourceId <= 0) {
				sql = StringUtil.removeSubstring(
					sql,
					"(Reports_Definition.sourceId = ?) [$AND_OR_CONNECTOR$]");
			}

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(CAST_TEXT(Reports_Definition.name))",
				StringPool.LIKE, false, names);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(CAST_TEXT(Reports_Definition.description))",
				StringPool.LIKE, false, descriptions);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(Reports_Definition.reportName)", StringPool.LIKE,
				true, reportNames);

			sql = _customSQL.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			if (sourceId > 0) {
				queryPos.add(sourceId);
			}

			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(reportNames, 2);

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

	private List<Definition> _findByG_S_N_D_RN(
		long groupId, String name, String description, long sourceId,
		String reportName, boolean andOperator, int start, int end,
		OrderByComparator<Definition> orderByComparator) {

		if (Validator.isNull(name) || Validator.isNull(description) ||
			Validator.isNull(reportName)) {

			andOperator = true;
		}

		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description);
		String[] reportNames = _customSQL.keywords(reportName);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_S_N_D_RN);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, Definition.class.getName(),
				"Reports_Definition.definitionId", groupId);

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(Reports_Definition.groupId = ?) AND");
			}

			if (sourceId <= 0) {
				sql = StringUtil.removeSubstring(
					sql,
					"(Reports_Definition.sourceId = ?) [$AND_OR_CONNECTOR$]");
			}

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(CAST_TEXT(Reports_Definition.name))",
				StringPool.LIKE, false, names);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(CAST_TEXT(Reports_Definition.description))",
				StringPool.LIKE, false, descriptions);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(Reports_Definition.reportName)", StringPool.LIKE,
				true, reportNames);

			sql = _customSQL.replaceAndOperator(sql, andOperator);

			if (orderByComparator != null) {
				sql = _customSQL.replaceOrderBy(sql, orderByComparator);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Reports_Definition", DefinitionImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			if (sourceId > 0) {
				queryPos.add(sourceId);
			}

			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(reportNames, 2);

			return (List<Definition>)QueryUtil.list(
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

}
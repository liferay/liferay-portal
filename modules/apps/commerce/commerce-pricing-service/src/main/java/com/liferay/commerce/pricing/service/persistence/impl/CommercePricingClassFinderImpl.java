/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence.impl;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.impl.CommercePricingClassImpl;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassFinder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommercePricingClassFinder.class)
public class CommercePricingClassFinderImpl
	extends CommercePricingClassFinderBaseImpl
	implements CommercePricingClassFinder {

	public static final String COUNT_BY_CPDEFINITION_ID =
		CommercePricingClassFinder.class.getName() + ".countByCPDefinitionId";

	public static final String FIND_BY_CPDEFINITION_ID =
		CommercePricingClassFinder.class.getName() + ".findByCPDefinitionId";

	@Override
	public int countByCPDefinitionId(long cpDefinitionId, String title) {
		return countByCPDefinitionId(cpDefinitionId, title, false);
	}

	@Override
	public int countByCPDefinitionId(
		long cpDefinitionId, String title, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_CPDEFINITION_ID);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, CommercePricingClass.class.getName(),
					"CommercePricingClass.commercePricingClassId");
			}

			String[] keywords = _customSQL.keywords(title, true);

			if (Validator.isNotNull(title)) {
				sql = _customSQL.replaceKeywords(
					sql, "(LOWER(CommercePricingClass.title)", StringPool.LIKE,
					true, keywords);
				sql = _customSQL.replaceAndOperator(sql, false);
			}
			else {
				sql = StringUtil.removeSubstring(
					sql,
					" AND (LOWER(CommercePricingClass.title) LIKE ? " +
						"[$AND_OR_NULL_CHECK$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(cpDefinitionId);

			if (Validator.isNotNull(title)) {
				queryPos.add(keywords, 2);
			}

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

	@Override
	public List<CommercePricingClass> findByCPDefinitionId(
		long cpDefinitionId, String title, int start, int end) {

		return findByCPDefinitionId(cpDefinitionId, title, start, end, false);
	}

	@Override
	public List<CommercePricingClass> findByCPDefinitionId(
		long cpDefinitionId, String title, int start, int end,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String[] keywords = _customSQL.keywords(title, true);

			String sql = _customSQL.get(getClass(), FIND_BY_CPDEFINITION_ID);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, CommercePricingClass.class.getName(),
					"CommercePricingClass.commercePricingClassId");
			}

			if (Validator.isNotNull(title)) {
				sql = _customSQL.replaceKeywords(
					sql, "(LOWER(CommercePricingClass.title)", StringPool.LIKE,
					true, keywords);
				sql = _customSQL.replaceAndOperator(sql, false);
			}
			else {
				sql = StringUtil.removeSubstring(
					sql,
					" AND (LOWER(CommercePricingClass.title) LIKE ? " +
						"[$AND_OR_NULL_CHECK$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommercePricingClassImpl.TABLE_NAME,
				CommercePricingClassImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(cpDefinitionId);

			if (Validator.isNotNull(title)) {
				queryPos.add(keywords, 2);
			}

			return (List<CommercePricingClass>)QueryUtil.list(
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
	private InlineSQLHelper _inlineSQLHelper;

}
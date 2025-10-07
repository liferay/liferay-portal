/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence.impl;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.model.impl.CommercePricingClassCPDefinitionRelImpl;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassCPDefinitionRelFinder;
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
@Component(service = CommercePricingClassCPDefinitionRelFinder.class)
public class CommercePricingClassCPDefinitionRelFinderImpl
	extends CommercePricingClassCPDefinitionRelFinderBaseImpl
	implements CommercePricingClassCPDefinitionRelFinder {

	public static final String COUNT_BY_COMMERCE_PRICING_CLASS_ID =
		CommercePricingClassCPDefinitionRelFinder.class.getName() +
			".countByCommercePricingClassId";

	public static final String FIND_BY_COMMERCE_PRICING_CLASS_ID =
		CommercePricingClassCPDefinitionRelFinder.class.getName() +
			".findByCommercePricingClassId";

	@Override
	public int countByCommercePricingClassId(
		long commercePricingClassId, String name, String languageId) {

		return countByCommercePricingClassId(
			commercePricingClassId, name, languageId, false);
	}

	@Override
	public int countByCommercePricingClassId(
		long commercePricingClassId, String name, String languageId,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_COMMERCE_PRICING_CLASS_ID);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, CommercePricingClass.class.getName(),
					"CommercePricingClass.commercePricingClassId");
			}

			String[] keywords = _customSQL.keywords(name, true);

			if (Validator.isNotNull(name)) {
				sql = _customSQL.replaceKeywords(
					sql, "(LOWER(CPDefinitionLocalization.name)",
					StringPool.LIKE, true, keywords);
				sql = _customSQL.replaceAndOperator(sql, false);
			}
			else {
				sql = StringUtil.removeSubstring(
					sql,
					" AND (LOWER(CPDefinitionLocalization.name) LIKE ? " +
						"[$AND_OR_NULL_CHECK$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(languageId);
			queryPos.add(commercePricingClassId);

			if (Validator.isNotNull(name)) {
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
	public List<CommercePricingClassCPDefinitionRel>
		findByCommercePricingClassId(
			long commercePricingClassId, String name, String languageId,
			int start, int end) {

		return findByCommercePricingClassId(
			commercePricingClassId, name, languageId, start, end, false);
	}

	@Override
	public List<CommercePricingClassCPDefinitionRel>
		findByCommercePricingClassId(
			long commercePricingClassId, String name, String languageId,
			int start, int end, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String[] keywords = _customSQL.keywords(name, true);

			String sql = _customSQL.get(
				getClass(), FIND_BY_COMMERCE_PRICING_CLASS_ID);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, CommercePricingClass.class.getName(),
					"CommercePricingClass.commercePricingClassId");
			}

			if (Validator.isNotNull(name)) {
				sql = _customSQL.replaceKeywords(
					sql, "(LOWER(CPDefinitionLocalization.name)",
					StringPool.LIKE, true, keywords);
				sql = _customSQL.replaceAndOperator(sql, false);
			}
			else {
				sql = StringUtil.removeSubstring(
					sql,
					" AND (LOWER(CPDefinitionLocalization.name) LIKE ? " +
						"[$AND_OR_NULL_CHECK$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommercePricingClassCPDefinitionRelImpl.TABLE_NAME,
				CommercePricingClassCPDefinitionRelImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(languageId);
			queryPos.add(commercePricingClassId);

			if (Validator.isNotNull(name)) {
				queryPos.add(keywords, 2);
			}

			return (List<CommercePricingClassCPDefinitionRel>)QueryUtil.list(
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
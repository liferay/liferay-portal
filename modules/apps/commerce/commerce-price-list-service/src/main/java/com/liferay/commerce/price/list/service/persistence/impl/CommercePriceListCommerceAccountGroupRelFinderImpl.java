/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRel;
import com.liferay.commerce.price.list.model.impl.CommercePriceListCommerceAccountGroupRelImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListCommerceAccountGroupRelFinder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommercePriceListCommerceAccountGroupRelFinder.class)
public class CommercePriceListCommerceAccountGroupRelFinderImpl
	extends CommercePriceListCommerceAccountGroupRelFinderBaseImpl
	implements CommercePriceListCommerceAccountGroupRelFinder {

	public static final String COUNT_BY_COMMERCE_PRICE_LIST_ID =
		CommercePriceListCommerceAccountGroupRelFinder.class.getName() +
			".countByCommercePriceListId";

	public static final String FIND_BY_COMMERCE_PRICE_LIST_ID =
		CommercePriceListCommerceAccountGroupRelFinder.class.getName() +
			".findByCommercePriceListId";

	@Override
	public int countByCommercePriceListId(
		long commercePriceListId, String name) {

		return countByCommercePriceListId(commercePriceListId, name, false);
	}

	@Override
	public int countByCommercePriceListId(
		long commercePriceListId, String name, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_COMMERCE_PRICE_LIST_ID);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, CommercePriceList.class.getName(),
					"CommercePriceList.commercePriceListId");
			}

			String[] keywords = _customSQL.keywords(name, true);

			if (Validator.isNotNull(name)) {
				sql = _customSQL.replaceKeywords(
					sql, "(LOWER(AccountGroup.name)", StringPool.LIKE, true,
					keywords);
				sql = _customSQL.replaceAndOperator(sql, false);
			}
			else {
				sql = StringUtil.removeSubstring(
					sql,
					" AND (LOWER(AccountGroup.name) LIKE ? " +
						"[$AND_OR_NULL_CHECK$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commercePriceListId);

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
	public List<CommercePriceListCommerceAccountGroupRel>
		findByCommercePriceListId(
			long commercePriceListId, String name, int start, int end) {

		return findByCommercePriceListId(
			commercePriceListId, name, start, end, false);
	}

	@Override
	public List<CommercePriceListCommerceAccountGroupRel>
		findByCommercePriceListId(
			long commercePriceListId, String name, int start, int end,
			boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String[] keywords = _customSQL.keywords(name, true);

			String sql = _customSQL.get(
				getClass(), FIND_BY_COMMERCE_PRICE_LIST_ID);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, CommercePriceList.class.getName(),
					"CommercePriceList.commercePriceListId");
			}

			if (Validator.isNotNull(name)) {
				sql = _customSQL.replaceKeywords(
					sql, "(LOWER(AccountGroup.name)", StringPool.LIKE, true,
					keywords);
				sql = _customSQL.replaceAndOperator(sql, false);
			}
			else {
				sql = StringUtil.removeSubstring(
					sql,
					" AND (LOWER(AccountGroup.name) LIKE ? " +
						"[$AND_OR_NULL_CHECK$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommercePriceListCommerceAccountGroupRelImpl.TABLE_NAME,
				CommercePriceListCommerceAccountGroupRelImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commercePriceListId);

			if (Validator.isNotNull(name)) {
				queryPos.add(keywords, 2);
			}

			return (List<CommercePriceListCommerceAccountGroupRel>)
				QueryUtil.list(sqlQuery, getDialect(), start, end);
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
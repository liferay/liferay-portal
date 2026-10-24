/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.model.impl.CommerceSubscriptionEntryImpl;
import com.liferay.commerce.service.persistence.CommerceSubscriptionEntryFinder;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(service = CommerceSubscriptionEntryFinder.class)
public class CommerceSubscriptionEntryFinderImpl
	extends CommerceSubscriptionEntryFinderBaseImpl
	implements CommerceSubscriptionEntryFinder {

	public static final String FIND_BY_DELIVERY_NEXT_ITERATION_DATE =
		CommerceSubscriptionEntryFinder.class.getName() +
			".findByDeliveryNextIterationDate";

	public static final String FIND_BY_NEXT_ITERATION_DATE =
		CommerceSubscriptionEntryFinder.class.getName() +
			".findByNextIterationDate";

	@Override
	public List<CommerceSubscriptionEntry> findByDeliveryNextIterationDate(
		Date nextIterationDate) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_DELIVERY_NEXT_ITERATION_DATE);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommerceSubscriptionEntryImpl.TABLE_NAME,
				CommerceSubscriptionEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (nextIterationDate != null) {
				queryPos.add(nextIterationDate);
			}

			return (List<CommerceSubscriptionEntry>)QueryUtil.list(
				sqlQuery, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<CommerceSubscriptionEntry> findByNextIterationDate(
		Date nextIterationDate) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_NEXT_ITERATION_DATE);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CommerceSubscriptionEntryImpl.TABLE_NAME,
				CommerceSubscriptionEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (nextIterationDate != null) {
				queryPos.add(nextIterationDate);
			}

			return (List<CommerceSubscriptionEntry>)QueryUtil.list(
				sqlQuery, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
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
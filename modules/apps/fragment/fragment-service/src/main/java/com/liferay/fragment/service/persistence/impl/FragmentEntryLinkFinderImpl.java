/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence.impl;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.model.impl.FragmentEntryLinkImpl;
import com.liferay.fragment.service.persistence.FragmentEntryLinkFinder;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = FragmentEntryLinkFinder.class)
public class FragmentEntryLinkFinderImpl
	extends FragmentEntryLinkFinderBaseImpl implements FragmentEntryLinkFinder {

	public static final String FIND_BY_G_FEERC =
		FragmentEntryLinkFinder.class.getName() + ".findByG_FEERC";

	public static final String FIND_BY_G_FEERC_FESERC =
		FragmentEntryLinkFinder.class.getName() + ".findByG_FEERC_FESERC";

	public static final String FIND_BY_G_FEERC_P =
		FragmentEntryLinkFinder.class.getName() + ".findByG_FEERC_P";

	public static final String FIND_BY_G_FEERC_FESERC_P =
		FragmentEntryLinkFinder.class.getName() + ".findByG_FEERC_FESERC_P";

	public static final String FIND_BY_G_FEERC_P_L =
		FragmentEntryLinkFinder.class.getName() + ".findByG_FEERC_P_L";

	public static final String FIND_BY_G_FEERC_FESERC_P_L =
		FragmentEntryLinkFinder.class.getName() + ".findByG_FEERC_FESERC_P_L";

	@Override
	public List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		Session session = null;

		try {
			session = openSession();

			String sql;

			if (Validator.isNull(fragmentEntryScopeERC)) {
				sql = _customSQL.get(getClass(), FIND_BY_G_FEERC);
			}
			else {
				sql = _customSQL.get(getClass(), FIND_BY_G_FEERC_FESERC);
			}

			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"FragmentEntryLink", FragmentEntryLinkImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(fragmentEntryERC);

			if (Validator.isNotNull(fragmentEntryScopeERC)) {
				queryPos.add(fragmentEntryScopeERC);
			}

			return (List<FragmentEntryLink>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<FragmentEntryLink> findByG_FEERC_FESERC_P_L(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int layoutPageTemplateEntryType, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		Session session = null;

		try {
			session = openSession();

			String sql;

			if (layoutPageTemplateEntryType >= 0) {
				if (Validator.isNull(fragmentEntryScopeERC)) {
					sql = _customSQL.get(getClass(), FIND_BY_G_FEERC_P_L);
				}
				else {
					sql = _customSQL.get(
						getClass(), FIND_BY_G_FEERC_FESERC_P_L);
				}
			}
			else {
				if (Validator.isNull(fragmentEntryScopeERC)) {
					sql = _customSQL.get(getClass(), FIND_BY_G_FEERC_P);
				}
				else {
					sql = _customSQL.get(getClass(), FIND_BY_G_FEERC_FESERC_P);
				}
			}

			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"FragmentEntryLink", FragmentEntryLinkImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (layoutPageTemplateEntryType >= 0) {
				queryPos.add(layoutPageTemplateEntryType);
			}

			queryPos.add(groupId);
			queryPos.add(fragmentEntryERC);

			if (Validator.isNotNull(fragmentEntryScopeERC)) {
				queryPos.add(fragmentEntryScopeERC);
			}

			return (List<FragmentEntryLink>)QueryUtil.list(
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
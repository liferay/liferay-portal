/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.impl.CPAttachmentFileEntryImpl;
import com.liferay.commerce.product.service.persistence.CPAttachmentFileEntryFinder;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
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
 * @author Alessio Antonio Rendina
 */
@Component(service = CPAttachmentFileEntryFinder.class)
public class CPAttachmentFileEntryFinderImpl
	extends CPAttachmentFileEntryFinderBaseImpl
	implements CPAttachmentFileEntryFinder {

	public static final String FIND_BY_EXPIRATION_DATE =
		CPAttachmentFileEntryFinder.class.getName() + ".findByExpirationDate";

	@Override
	public List<CPAttachmentFileEntry> findByExpirationDate(
		Date expirationDate,
		QueryDefinition<CPAttachmentFileEntry> queryDefinition) {

		return doFindByExpirationDate(expirationDate, queryDefinition);
	}

	protected List<CPAttachmentFileEntry> doFindByExpirationDate(
		Date expirationDate,
		QueryDefinition<CPAttachmentFileEntry> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_EXPIRATION_DATE, queryDefinition,
				CPAttachmentFileEntryImpl.TABLE_NAME);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				CPAttachmentFileEntryImpl.TABLE_NAME,
				CPAttachmentFileEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (expirationDate != null) {
				queryPos.add(expirationDate);
			}

			queryPos.add(queryDefinition.getStatus());

			return (List<CPAttachmentFileEntry>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
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
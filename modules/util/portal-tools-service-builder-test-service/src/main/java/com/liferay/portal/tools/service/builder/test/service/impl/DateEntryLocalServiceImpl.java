/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.impl;

import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;
import com.liferay.portal.tools.service.builder.test.service.base.DateEntryLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DateEntryLocalServiceImpl extends DateEntryLocalServiceBaseImpl {

	@Override
	public DateEntry fetchDateEntry(long companyId, Date snapshotDate) {
		return dateEntryPersistence.fetchByC_S(companyId, snapshotDate);
	}

	@Override
	public List<DateEntry> getDateEntries(Date snapshotDate) {
		return dateEntryPersistence.findBySnapshotDate(snapshotDate);
	}

	@Override
	public List<Object[]> getDateEntriesBySQLQuery(long companyId, Type type) {
		Session session = dateEntryPersistence.openSession();

		try {
			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				"select dateEntryId, snapshotDate from DateEntry where " +
					"companyId = ? order by snapshotDate");

			if (type != null) {
				sqlQuery.addScalar("dateEntryId", Type.LONG);
				sqlQuery.addScalar("snapshotDate", type);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<Object[]>)sqlQuery.list();
		}
		finally {
			dateEntryPersistence.closeSession(session);
		}
	}

	@Override
	public List<Object> getMaxSnapshotDatesBySQLQuery(
		long companyId, Type type) {

		Session session = dateEntryPersistence.openSession();

		try {
			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				"select max(snapshotDate) as maxSnapshotDate from DateEntry " +
					"where companyId = ?");

			if (type != null) {
				sqlQuery.addScalar("maxSnapshotDate", type);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return sqlQuery.list();
		}
		finally {
			dateEntryPersistence.closeSession(session);
		}
	}

}
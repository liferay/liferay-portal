/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.impl;

import com.liferay.portal.tools.service.builder.test.model.DateEntry;
import com.liferay.portal.tools.service.builder.test.service.base.DateEntryLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DateEntryLocalServiceImpl extends DateEntryLocalServiceBaseImpl {

	public DateEntry fetchDateEntry(long companyId, Date snapshotDate) {
		return dateEntryPersistence.fetchByC_S(companyId, snapshotDate);
	}

	public List<DateEntry> getDateEntries(Date snapshotDate) {
		return dateEntryPersistence.findBySnapshotDate(snapshotDate);
	}

}
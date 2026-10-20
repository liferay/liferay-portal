/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal;

import com.liferay.change.tracking.model.CTEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Preston Crary
 */
public class CTServiceCopier<T extends CTModel<T>> {

	public CTServiceCopier(
		CTService<T> ctService, long sourceCTCollectionId,
		long targetCTCollectionId) {

		_ctService = ctService;
		_sourceCTCollectionId = sourceCTCollectionId;
		_targetCTCollectionId = targetCTCollectionId;
	}

	public void addCTEntry(CTEntry ctEntry) {
		if (_ctEntries == null) {
			_ctEntries = new ArrayList<>();
		}

		_ctEntries.add(ctEntry);
	}

	public void copy() throws Exception {
		_ctService.updateWithUnsafeFunction(this::_copy);
	}

	private Void _copy(CTPersistence<T> ctPersistence) throws Exception {
		String tableName = ctPersistence.getTableName();

		Set<String> primaryKeyNames = ctPersistence.getCTColumnNames(
			CTColumnResolutionType.PK);

		if (primaryKeyNames.size() != 1) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"{primaryKeyNames=", primaryKeyNames, ", tableName=",
					tableName, "}"));
		}

		Iterator<String> iterator = primaryKeyNames.iterator();

		String primaryKeyName = iterator.next();

		Connection connection = CurrentConnectionUtil.getConnection(
			ctPersistence.getDataSource());

		Map<String, Integer> tableColumnsMap =
			ctPersistence.getTableColumnsMap();

		StringBundler sb = new StringBundler();

		sb.append("select ");

		for (String name : tableColumnsMap.keySet()) {
			if (name.equals("ctCollectionId")) {
				sb.append(_targetCTCollectionId);
				sb.append(" as ");
			}
			else {
				sb.append("t1.");
			}

			sb.append(name);
			sb.append(", ");
		}

		sb.setStringAt(" from ", sb.index() - 1);

		sb.append(ctPersistence.getTableName());
		sb.append(" t1 where t1.ctCollectionId = ");
		sb.append(_sourceCTCollectionId);
		sb.append(" and (t1.");
		sb.append(primaryKeyName);
		sb.append(" in (");

		int i = 0;

		for (CTEntry ctEntry : _ctEntries) {
			if (i == _BATCH_SIZE) {
				sb.setStringAt(")", sb.index() - 1);

				sb.append(" or t1.");
				sb.append(primaryKeyName);
				sb.append(" in (");

				i = 0;
			}

			sb.append(ctEntry.getModelClassPK());
			sb.append(", ");

			i++;
		}

		sb.setStringAt(")", sb.index() - 1);

		sb.append(")");

		CTRowUtil.copyCTRows(connection, ctPersistence, sb.toString());

		return null;
	}

	private static final int _BATCH_SIZE = 1000;

	private List<CTEntry> _ctEntries;
	private final CTService<T> _ctService;
	private final long _sourceCTCollectionId;
	private final long _targetCTCollectionId;

}
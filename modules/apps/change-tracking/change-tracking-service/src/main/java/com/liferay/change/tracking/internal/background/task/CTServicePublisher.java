/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.background.task;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.internal.CTRowUtil;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Preston Crary
 */
public class CTServicePublisher<T extends CTModel<T>> {

	public CTServicePublisher(
		CTEntryLocalService ctEntryLocalService, CTService<T> ctService,
		long modelClassNameId, long sourceCTCollectionId,
		long targetCTCollectionId) {

		_ctEntryLocalService = ctEntryLocalService;
		_ctService = ctService;
		_modelClassNameId = modelClassNameId;
		_sourceCTCollectionId = sourceCTCollectionId;
		_targetCTCollectionId = targetCTCollectionId;
	}

	public void addCTEntry(CTEntry ctEntry) {
		long modelClassPK = ctEntry.getModelClassPK();
		int changeType = ctEntry.getChangeType();

		if (changeType == CTConstants.CT_CHANGE_TYPE_ADDITION) {
			if (_additionCTEntries == null) {
				_additionCTEntries = new HashMap<>();
			}

			_additionCTEntries.put(modelClassPK, ctEntry);
		}
		else if (changeType == CTConstants.CT_CHANGE_TYPE_DELETION) {
			if (_deletionCTEntries == null) {
				_deletionCTEntries = new HashMap<>();
			}

			_deletionCTEntries.put(modelClassPK, ctEntry);
		}
		else {
			if (_modificationCTEntries == null) {
				_modificationCTEntries = new HashMap<>();
			}

			_modificationCTEntries.put(modelClassPK, ctEntry);
		}
	}

	public void publish() throws Exception {
		_ctService.updateWithUnsafeFunction(this::_publish);
	}

	private void _copyCTRow(
			Connection connection, CTPersistence<?> ctPersistence,
			String tableName, String primaryKeyName, Serializable primaryKey,
			long tempCTCollectionId)
		throws Exception {

		StringBundler sb = new StringBundler();

		Map<String, Integer> tableColumnsMap =
			ctPersistence.getTableColumnsMap();

		sb.append("select ");

		for (String name : tableColumnsMap.keySet()) {
			if (name.equals("ctCollectionId")) {
				sb.append(_targetCTCollectionId);
				sb.append(" as ");
			}
			else if (name.equals("mvccVersion")) {
				sb.append("(mvccVersion + 1) ");
			}

			sb.append(name);
			sb.append(", ");
		}

		sb.setStringAt(" from ", sb.index() - 1);

		sb.append(tableName);
		sb.append(" where ");
		sb.append(primaryKeyName);
		sb.append(" = ");
		sb.append(primaryKey);
		sb.append(" and ctCollectionId = ");
		sb.append(tempCTCollectionId);

		CTRowUtil.copyCTRows(ctPersistence, connection, sb.toString());
	}

	private int _getPredeletedRowCount(
			Connection connection, String tableName, String primaryKeyName)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) from CTEntry left join ", tableName,
					" on CTEntry.modelClassPK = ", tableName, ".",
					primaryKeyName, " and ", tableName, ".ctCollectionId = ",
					_targetCTCollectionId, " where CTEntry.changeType = ",
					CTConstants.CT_CHANGE_TYPE_DELETION,
					" and CTEntry.ctCollectionId = ", _sourceCTCollectionId,
					" and CTEntry.modelClassNameId = ", _modelClassNameId,
					" and ", tableName, ".", primaryKeyName, " is null"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		}

		return 0;
	}

	private Void _publish(CTPersistence<T> ctPersistence) throws Exception {
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

		// Order matters to avoid causing constraint violations

		long tempCTCollectionId = -_sourceCTCollectionId;

		Connection connection = CurrentConnectionUtil.getConnection(
			ctPersistence.getDataSource());

		if (_additionCTEntries != null) {
			_updateCTCollectionId(
				connection, tableName, primaryKeyName,
				_additionCTEntries.values(), _sourceCTCollectionId,
				tempCTCollectionId, false, false);
		}

		if (_modificationCTEntries != null) {
			_updateCTCollectionId(
				connection, tableName, primaryKeyName,
				_modificationCTEntries.values(), _sourceCTCollectionId,
				tempCTCollectionId, false, true);
		}

		if (_deletionCTEntries != null) {
			int predeletedRowCount = _getPredeletedRowCount(
				connection, tableName, primaryKeyName);

			if (predeletedRowCount != _deletionCTEntries.size()) {
				int updatedRowCount = _updateCTCollectionId(
					connection, tableName, primaryKeyName,
					_deletionCTEntries.values(), _targetCTCollectionId,
					_sourceCTCollectionId, false, false);

				if ((predeletedRowCount + updatedRowCount) !=
						_deletionCTEntries.size()) {

					throw new SystemException(
						StringBundler.concat(
							"Size mismatch expected ",
							_deletionCTEntries.size(), " but was ",
							updatedRowCount));
				}
			}

			_updateModelMvccVersion(
				connection, tableName, primaryKeyName, _deletionCTEntries,
				_sourceCTCollectionId);
		}

		if (_modificationCTEntries != null) {
			_updateCTCollectionId(
				connection, tableName, primaryKeyName,
				_modificationCTEntries.values(), _targetCTCollectionId,
				_sourceCTCollectionId, true, true);
		}

		if (_additionCTEntries != null) {
			_updateCTCollectionId(
				connection, tableName, primaryKeyName,
				_additionCTEntries.values(), tempCTCollectionId,
				_targetCTCollectionId, false, false);

			_updateModelMvccVersion(
				connection, tableName, primaryKeyName, _additionCTEntries,
				_targetCTCollectionId);
		}

		if (_modificationCTEntries != null) {
			for (Serializable primaryKey : _modificationCTEntries.keySet()) {
				_copyCTRow(
					connection, ctPersistence, tableName, primaryKeyName,
					primaryKey, tempCTCollectionId);
			}

			StringBundler sb = new StringBundler(7);

			sb.append("delete from ");
			sb.append(tableName);
			sb.append(" where ctCollectionId = ");
			sb.append(tempCTCollectionId);
			sb.append(" and ");
			sb.append(primaryKeyName);
			sb.append(" = ?");

			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection, sb.toString())) {

				for (Serializable primaryKey :
						_modificationCTEntries.keySet()) {

					preparedStatement.setLong(1, (Long)primaryKey);

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}

			_updateModelMvccVersion(
				connection, tableName, primaryKeyName, _modificationCTEntries,
				_targetCTCollectionId);
		}

		if (_additionCTEntries != null) {
			ctPersistence.clearCache(_additionCTEntries.keySet());
		}

		if (_deletionCTEntries != null) {
			ctPersistence.clearCache(_deletionCTEntries.keySet());
		}

		if (_modificationCTEntries != null) {
			ctPersistence.clearCache(_modificationCTEntries.keySet());
		}

		return null;
	}

	private int _updateCTCollectionId(
			Connection connection, String tableName, String primaryKeyName,
			Collection<CTEntry> ctEntries, long fromCTCollectionId,
			long toCTCollectionId, boolean includeMvccVersion,
			boolean checkRowCount)
		throws Exception {

		StringBundler sb = new StringBundler(16);

		sb.append("update ");
		sb.append(tableName);
		sb.append(" set ctCollectionId = ");
		sb.append(toCTCollectionId);
		sb.append(" where ");
		sb.append(tableName);
		sb.append(".ctCollectionId = ");
		sb.append(fromCTCollectionId);
		sb.append(" and ");
		sb.append(tableName);
		sb.append(".");
		sb.append(primaryKeyName);
		sb.append(" = ?");

		if (includeMvccVersion) {
			sb.append(" and ");
			sb.append(tableName);
			sb.append(".mvccVersion = ?");
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sb.toString())) {

			int batchCount = 0;
			int totalRowCount = 0;

			for (CTEntry ctEntry : ctEntries) {
				preparedStatement.setLong(1, ctEntry.getModelClassPK());

				if (includeMvccVersion) {
					preparedStatement.setLong(2, ctEntry.getModelMvccVersion());
				}

				preparedStatement.addBatch();

				if (++batchCount >= PropsValues.HIBERNATE_JDBC_BATCH_SIZE) {
					batchCount = 0;

					for (int rowCount : preparedStatement.executeBatch()) {
						totalRowCount += rowCount;
					}
				}
			}

			for (int rowCount : preparedStatement.executeBatch()) {
				totalRowCount += rowCount;
			}

			if (checkRowCount && (totalRowCount != ctEntries.size())) {
				throw new SystemException(
					StringBundler.concat(
						"Size mismatch expected ", ctEntries.size(),
						" but was ", totalRowCount));
			}

			return totalRowCount;
		}
	}

	private void _updateModelMvccVersion(
			Connection connection, String tableName, String primaryKeyName,
			Map<Serializable, CTEntry> ctEntries, long ctCollectionId)
		throws Exception {

		int count = 0;

		List<Serializable> primaryKeys = new ArrayList<>(ctEntries.keySet());

		while (count < primaryKeys.size()) {
			int batchCount = count;

			count += _BATCH_SIZE;

			if (count > primaryKeys.size()) {
				count = primaryKeys.size();
			}

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						CTRowUtil.getUpdateMVCCVersionSQL(
							ctCollectionId,
							ListUtil.subList(primaryKeys, batchCount, count),
							primaryKeyName, tableName));
				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					long primaryKey = resultSet.getLong(1);
					long mvccVersion = resultSet.getLong(2);

					CTEntry ctEntry = ctEntries.get(primaryKey);

					_ctEntryLocalService.updateModelMvccVersion(
						ctEntry.getCtEntryId(), mvccVersion);
				}
			}
		}
	}

	private static final int _BATCH_SIZE = 50000;

	private Map<Serializable, CTEntry> _additionCTEntries;
	private final CTEntryLocalService _ctEntryLocalService;
	private final CTService<T> _ctService;
	private Map<Serializable, CTEntry> _deletionCTEntries;
	private final long _modelClassNameId;
	private Map<Serializable, CTEntry> _modificationCTEntries;
	private final long _sourceCTCollectionId;
	private final long _targetCTCollectionId;

}
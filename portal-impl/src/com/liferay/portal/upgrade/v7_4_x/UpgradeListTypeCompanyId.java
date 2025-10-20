/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Luis Ortiz
 */
public class UpgradeListTypeCompanyId extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();

		_resetCounter(defaultCompanyId);

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			_upgradeDBPartition(defaultCompanyId);

			return;
		}

		_upgrade(defaultCompanyId);
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns("ListType", "companyId LONG")
		};
	}

	private List<ListTypeEntry> _getListTypes() throws Exception {
		List<ListTypeEntry> listTypeEntries = new ArrayList<>();

		try (Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				"select listTypeId, name, type_ from ListType")) {

			while (resultSet.next()) {
				listTypeEntries.add(
					new ListTypeEntry(
						resultSet.getLong(1), resultSet.getString(2),
						resultSet.getString(3)));
			}
		}

		return listTypeEntries;
	}

	private HashMap<Long, Long> _insertListTypes(
			List<ListTypeEntry> listTypeEntries, long companyId)
		throws Exception {

		HashMap<Long, Long> listTypeIds = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into ListType (mvccVersion, listTypeId, companyId, " +
					"name, type_) values (?, ?, ?, ?, ?)")) {

			for (ListTypeEntry listTypeEntry : listTypeEntries) {
				long newListTypeId = increment(ListType.class.getName());

				preparedStatement.setLong(1, 0);
				preparedStatement.setLong(2, newListTypeId);
				preparedStatement.setLong(3, companyId);
				preparedStatement.setString(4, listTypeEntry.getName());
				preparedStatement.setString(5, listTypeEntry.getType());

				listTypeIds.put(listTypeEntry.getListTypeId(), newListTypeId);

				preparedStatement.execute();
			}
		}

		return listTypeIds;
	}

	private void _resetCounter(long defaultCompanyId) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED ||
			(CompanyThreadLocal.getCompanyId() == defaultCompanyId)) {

			try (Statement statement = connection.createStatement();
				ResultSet resultSet1 = statement.executeQuery(
					StringBundler.concat(
						"select currentId from Counter where name = '",
						ListType.class.getName(), "'"))) {

				long counter = 0;

				if (resultSet1.next()) {
					counter = resultSet1.getLong("currentId");
				}

				try (ResultSet resultSet2 = statement.executeQuery(
						"select max(listTypeId) from ListType")) {

					if (resultSet2.next()) {
						long increment = Math.max(
							0, resultSet2.getLong(1) - counter);

						if (increment > 0) {
							increment(ListType.class.getName(), (int)increment);
						}
					}
				}
			}
		}
	}

	private void _updateListTypeReferences(
			HashMap<Long, Long> listTypeIds, long companyId)
		throws Exception {

		for (Map.Entry<String, List<String>> listTypeReference :
				_listTypeReferences.entrySet()) {

			String tableName = listTypeReference.getKey();
			List<String> columnNames = listTypeReference.getValue();

			for (String columnName : columnNames) {
				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							StringBundler.concat(
								"update ", tableName, " set ", columnName,
								" = ? where ", columnName,
								" = ? and companyId = ?"))) {

					for (Map.Entry<Long, Long> entry : listTypeIds.entrySet()) {
						preparedStatement.setLong(1, entry.getValue());
						preparedStatement.setLong(2, entry.getKey());
						preparedStatement.setLong(3, companyId);

						preparedStatement.executeUpdate();
					}
				}
			}
		}
	}

	private void _upgrade(long defaultCompanyId) throws Exception {
		dropIndexes("ListType", "name");

		runSQL("update ListType set companyId = " + defaultCompanyId);

		long[] companyIds = PortalInstancePool.getCompanyIds();

		List<ListTypeEntry> listTypeEntries = _getListTypes();

		for (long companyId : companyIds) {
			if (companyId != defaultCompanyId) {
				HashMap<Long, Long> listTypeIds = _insertListTypes(
					listTypeEntries, companyId);

				_updateListTypeReferences(listTypeIds, companyId);
			}
		}
	}

	private void _upgradeDBPartition(long defaultCompanyId) throws Exception {
		if (CompanyThreadLocal.getCompanyId() == defaultCompanyId) {
			runSQL("update ListType set companyId = " + defaultCompanyId);

			for (long companyId : PortalInstancePool.getCompanyIds()) {
				DBPartitionUtil.replaceByTable(
					connection, companyId, "ListType", true);
			}
		}
		else {
			runSQL(
				"update ListType set companyId = " +
					CompanyThreadLocal.getCompanyId());
		}
	}

	private static final Map<String, List<String>> _listTypeReferences =
		HashMapBuilder.<String, List<String>>put(
			"Address", Collections.singletonList("listTypeId")
		).put(
			"Contact_", Arrays.asList("prefixListTypeId", "suffixListTypeId")
		).put(
			"EmailAddress", Collections.singletonList("listTypeId")
		).put(
			"Organization_", Collections.singletonList("statusListTypeId")
		).put(
			"OrgLabor", Collections.singletonList("listTypeId")
		).put(
			"Phone", Collections.singletonList("listTypeId")
		).put(
			"Website", Collections.singletonList("listTypeId")
		).build();

	private static class ListTypeEntry {

		public ListTypeEntry(long listTypeId, String name, String type) {
			_listTypeId = listTypeId;
			_name = name;
			_type = type;
		}

		public long getListTypeId() {
			return _listTypeId;
		}

		public String getName() {
			return _name;
		}

		public String getType() {
			return _type;
		}

		private final long _listTypeId;
		private final String _name;
		private final String _type;

	}

}
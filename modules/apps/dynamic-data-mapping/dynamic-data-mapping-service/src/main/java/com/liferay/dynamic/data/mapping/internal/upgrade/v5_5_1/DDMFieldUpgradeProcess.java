/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_5_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.DBTypeToSQLMap;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Alicia García
 */
public class DDMFieldUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			_upgradeByCompanyId(CompanyThreadLocal.getCompanyId());

			return;
		}

		long[] companyIds = PortalInstancePool.getCompanyIds();

		if (companyIds.length == 1) {
			_upgradeByCompanyId(companyIds[0]);

			return;
		}

		_upgrade();
	}

	private boolean _hasDDMFieldCompanyId0() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) from DDMField where companyId = 0")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int count = resultSet.getInt(1);

					if (count > 0) {
						return true;
					}
				}

				return false;
			}
		}
	}

	private void _upgrade() throws Exception {
		if (!_hasDDMFieldCompanyId0()) {
			return;
		}

		DBTypeToSQLMap dbTypeToSQLMap = new DBTypeToSQLMap(
			StringBundler.concat(
				"update DDMField set companyId = ",
				"DDMStructureVersion.companyId from DDMStructureVersion where ",
				"DDMField.structureVersionId = ",
				"DDMStructureVersion.structureVersionId and ",
				"DDMField.ctCollectionId = DDMStructureVersion.ctCollectionId ",
				"and DDMField.companyId = 0"));

		String sql = StringBundler.concat(
			"update DDMField inner join DDMStructureVersion on ",
			"DDMField.structureVersionId = ",
			"DDMStructureVersion.structureVersionId and ",
			"DDMField.ctCollectionId = DDMStructureVersion.ctCollectionId set ",
			"DDMField.companyId = DDMStructureVersion.companyId where ",
			"DDMField.companyId = 0");

		dbTypeToSQLMap.add(DBType.MARIADB, sql);
		dbTypeToSQLMap.add(DBType.MYSQL, sql);

		dbTypeToSQLMap.add(
			DBType.ORACLE,
			StringBundler.concat(
				"update DDMField set companyId = COALESCE((select ",
				"DDMStructureVersion.companyId from DDMStructureVersion where ",
				"DDMField.structureVersionId = ",
				"DDMStructureVersion.structureVersionId and ",
				"DDMField.ctCollectionId = ",
				"DDMStructureVersion.ctCollectionId), 0) where ",
				"DDMField.companyId = 0"));

		runSQL(dbTypeToSQLMap);
	}

	private void _upgradeByCompanyId(long companyId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update DDMField set companyId = ? where companyId = 0")) {

			preparedStatement.setLong(1, companyId);

			preparedStatement.executeUpdate();
		}
	}

}
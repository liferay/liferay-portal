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
public class DDMFieldAttributeUpgradeProcess extends UpgradeProcess {

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

	private boolean _hasDDMFieldAttributeCompanyId0() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) from DDMFieldAttribute where companyId = 0")) {

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
		if (!_hasDDMFieldAttributeCompanyId0()) {
			return;
		}

		DBTypeToSQLMap dbTypeToSQLMap = new DBTypeToSQLMap(
			StringBundler.concat(
				"update DDMFieldAttribute set companyId = ",
				"DDMStructureVersion.companyId from DDMField inner join ",
				"DDMStructureVersion on DDMField.structureVersionId = ",
				"DDMStructureVersion.structureVersionId and ",
				"DDMField.ctCollectionId = DDMStructureVersion.ctCollectionId ",
				"where DDMFieldAttribute.companyId = 0 and DDMField.fieldId = ",
				"DDMFieldAttribute.fieldId"));

		String sql = StringBundler.concat(
			"update DDMFieldAttribute inner join DDMField on DDMField.fieldId ",
			"= DDMFieldAttribute.fieldId inner join DDMStructureVersion on ",
			"DDMField.structureVersionId = ",
			"DDMStructureVersion.structureVersionId and ",
			"DDMField.ctCollectionId = DDMStructureVersion.ctCollectionId set ",
			"DDMFieldAttribute.companyId = DDMStructureVersion.companyId ",
			"where DDMFieldAttribute.companyId = 0");

		dbTypeToSQLMap.add(DBType.MARIADB, sql);
		dbTypeToSQLMap.add(DBType.MYSQL, sql);

		dbTypeToSQLMap.add(
			DBType.ORACLE,
			StringBundler.concat(
				"update DDMFieldAttribute set companyId = COALESCE((select ",
				"DDMStructureVersion.companyId from DDMField inner join ",
				"DDMStructureVersion on DDMField.structureVersionId = ",
				"DDMStructureVersion.structureVersionId and ",
				"DDMField.ctCollectionId = DDMStructureVersion.ctCollectionId ",
				"and DDMField.fieldId = DDMFieldAttribute.fieldId), 0) where ",
				"DDMFieldAttribute.companyId = 0"));

		runSQL(dbTypeToSQLMap);
	}

	private void _upgradeByCompanyId(long companyId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update DDMFieldAttribute set companyId = ? where companyId " +
					"= 0")) {

			preparedStatement.setLong(1, companyId);

			preparedStatement.executeUpdate();
		}
	}

}
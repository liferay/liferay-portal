/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ClassResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Leon Chi
 */
public abstract class BaseLocalizedColumnUpgradeProcess extends UpgradeProcess {

	protected void upgradeLocalizedColumn(
			ResourceBundleLoader resourceBundleLoader, String tableName,
			String columnName, String originalContent,
			String localizationMapKey, String localizationXMLKey)
		throws SQLException {

		try {
			if (!hasColumnType(tableName, columnName, "TEXT null") &&
				!_alteredTableNameColumnNames.contains(
					tableName + StringPool.POUND + columnName)) {

				alterColumnType(tableName, columnName, "TEXT null");

				_alteredTableNameColumnNames.add(
					tableName + StringPool.POUND + columnName);
			}

			Class<?> clazz = getClass();

			CompanyLocalServiceUtil.forEachCompanyId(
				companyId -> _upgrade(
					new AggregateResourceBundleLoader(
						new ClassResourceBundleLoader(
							"content.Language", clazz.getClassLoader()),
						resourceBundleLoader),
					tableName, columnName, originalContent, localizationMapKey,
					localizationXMLKey, companyId));
		}
		catch (Exception exception) {
			throw new SQLException(exception);
		}
	}

	private String _getLocalizationXML(
		String localizationMapKey, String localizationXMLKey, long companyId,
		ResourceBundleLoader resourceBundleLoader) {

		return LocalizationUtil.updateLocalization(
			ResourceBundleUtil.getLocalizationMap(
				resourceBundleLoader, localizationMapKey),
			"", localizationXMLKey,
			UpgradeProcessUtil.getDefaultLanguageId(companyId));
	}

	private void _upgrade(
		ResourceBundleLoader resourceBundleLoader, String tableName,
		String columnName, String originalContent, String localizationMapKey,
		String localizationXMLKey, long companyId) {

		String localizationXML = _getLocalizationXML(
			localizationMapKey, localizationXMLKey, companyId,
			resourceBundleLoader);

		String sql = StringBundler.concat(
			"update ", tableName, " set ", columnName, " = ? where ",
			columnName, " like ? and companyId = ?");

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, localizationXML);
			preparedStatement.setString(2, originalContent);
			preparedStatement.setLong(3, companyId);

			preparedStatement.executeUpdate();
		}
		catch (SQLException sqlException) {
			throw new SystemException(sqlException);
		}
	}

	private static final Set<String> _alteredTableNameColumnNames =
		new HashSet<>();

}
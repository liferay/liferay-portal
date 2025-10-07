/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_20_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Locale;

/**
 * @author Jhosseph Gonzalez
 */
public class ObjectFieldUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
			SQLTransformer.transform(
				StringBundler.concat(
					"select ObjectDefinition.objectDefinitionId, ",
					"ObjectDefinition.companyId, ObjectDefinition.userId, ",
					"ObjectDefinition.userName from ObjectDefinition where ",
					"ObjectDefinition.objectDefinitionId not in (select ",
					"distinct ObjectField.objectDefinitionId from ObjectField ",
					"where ObjectField.name in ('displaydate', ",
					"'expirationDate','reviewdate')) and ObjectDefinition.",
					"modifiable = [$TRUE$]")));
			 PreparedStatement preparedStatement2 =
				 AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					 connection,
					 StringBundler.concat(
						 "insert into ObjectField (mvccVersion, uuid_, ",
						 "externalReferenceCode, objectFieldId, companyId, ",
						 "userId, userName, createDate, modifiedDate, ",
						 "listTypeDefinitionId, objectDefinitionId, ",
						 "businessType, dbColumnName, dbTableName, dbType, ",
						 "indexed, indexedAsKeyword, indexedLanguageId, ",
						 "label, localized, name, readOnly, ",
						 "readOnlyConditionExpression, relationshipType,",
						 "required, state_, system_) values (",
						 "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
						 "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
				 ));
			 PreparedStatement preparedStatement3 =
				 AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					 connection,
					 StringBundler.concat(
						 "insert into ObjectFieldSetting (mvccVersion, uuid_, ",
						 "objectFieldSettingId, companyId, userId, userName, ",
						 "createDate, modifiedDate, objectFieldId, name, ",
						 "value) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"));

			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long companyId = resultSet.getLong("companyId");

				Locale defaultLocale = LocaleUtil.fromLanguageId(
					UpgradeProcessUtil.getDefaultLanguageId(companyId));

				long objectDefinitionId = resultSet.getLong(
					"objectDefinitionId");
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");

				_insertObjectField(
					companyId, "display-date", defaultLocale, "displayDate",
					objectDefinitionId, preparedStatement2, preparedStatement3,
					timestamp, userId, userName);
				_insertObjectField(
					companyId, "expiration-date", defaultLocale,
					"expirationDate", objectDefinitionId, preparedStatement2,
					preparedStatement3, timestamp, userId, userName);
				_insertObjectField(
					companyId, "review-date", defaultLocale, "reviewDate",
					objectDefinitionId, preparedStatement2, preparedStatement3,
					timestamp, userId, userName);

				preparedStatement2.executeBatch();

				preparedStatement3.executeBatch();
			}
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"ObjectDefinition", "enableObjectEntrySchedule BOOLEAN")
		};
	}

	private void _insertObjectField(
			long companyId, String key, Locale locale, String name,
			long objectDefinitionId, PreparedStatement preparedStatement2,
			PreparedStatement preparedStatement3, Timestamp timestamp,
			long userId, String userName)
		throws SQLException {

		preparedStatement2.setLong(1, 0);

		String uuid = PortalUUIDUtil.generate();

		preparedStatement2.setString(2, uuid);
		preparedStatement2.setString(3, uuid);

		long objectFieldId = increment();

		preparedStatement2.setLong(4, objectFieldId);

		preparedStatement2.setLong(5, companyId);
		preparedStatement2.setLong(6, userId);
		preparedStatement2.setString(7, userName);
		preparedStatement2.setTimestamp(8, timestamp);
		preparedStatement2.setTimestamp(9, timestamp);
		preparedStatement2.setLong(10, 0);
		preparedStatement2.setLong(11, objectDefinitionId);
		preparedStatement2.setString(
			12, ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME);
		preparedStatement2.setString(13, name);
		preparedStatement2.setString(
			14, ObjectEntryTable.INSTANCE.getTableName());
		preparedStatement2.setString(
			15, ObjectFieldConstants.DB_TYPE_DATE_TIME);
		preparedStatement2.setBoolean(16, false);
		preparedStatement2.setBoolean(17, false);
		preparedStatement2.setString(18, null);
		preparedStatement2.setString(
			19,
			LocalizationUtil.getXml(
				new LocalizedValuesMap() {
					{
						put(locale, LanguageUtil.get(locale, key));
					}
				},
				"Label"));
		preparedStatement2.setBoolean(20, false);
		preparedStatement2.setString(21, name);
		preparedStatement2.setBoolean(22, true);
		preparedStatement2.setString(23, null);
		preparedStatement2.setString(24, null);
		preparedStatement2.setBoolean(25, false);
		preparedStatement2.setBoolean(26, false);
		preparedStatement2.setBoolean(27, true);

		preparedStatement2.addBatch();

		preparedStatement3.setLong(1, 0);
		preparedStatement3.setString(2, PortalUUIDUtil.generate());
		preparedStatement3.setLong(3, increment());
		preparedStatement3.setLong(4, companyId);
		preparedStatement3.setLong(5, userId);
		preparedStatement3.setString(6, userName);
		preparedStatement3.setTimestamp(7, timestamp);
		preparedStatement3.setTimestamp(8, timestamp);
		preparedStatement3.setLong(9, objectFieldId);
		preparedStatement3.setString(
			10, ObjectFieldSettingConstants.NAME_TIME_STORAGE);
		preparedStatement3.setString(
			11, ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC);

		preparedStatement3.addBatch();
	}

}
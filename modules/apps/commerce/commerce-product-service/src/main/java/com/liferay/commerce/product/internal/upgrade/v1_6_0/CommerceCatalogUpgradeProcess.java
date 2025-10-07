/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v1_6_0;

import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.internal.upgrade.v1_6_0.util.CommerceCatalogTable;
import com.liferay.commerce.product.internal.upgrade.v1_6_0.util.CommerceChannelRelTable;
import com.liferay.commerce.product.internal.upgrade.v1_6_0.util.CommerceChannelTable;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Alec Sloan
 */
public class CommerceCatalogUpgradeProcess extends UpgradeProcess {

	public CommerceCatalogUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		GroupLocalService groupLocalService) {

		_classNameLocalService = classNameLocalService;
		_groupLocalService = groupLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		String insertCommerceCatalogSQL = StringBundler.concat(
			"insert into CommerceCatalog (commerceCatalogId, companyId, ",
			"userId, userName, createDate, modifiedDate, name, ",
			"catalogDefaultLanguageId) values (?, ?, ?, ?, ?, ?, ?, ?)");

		String insertCommerceChannelSQL = StringBundler.concat(
			"insert into CommerceChannel (commerceChannelId, companyId, ",
			"userId, userName, createDate, modifiedDate, name, siteGroupId, ",
			"type_) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

		String insertCommerceChannelRelSQL = StringBundler.concat(
			"insert into CommerceChannelRel (commerceChannelRelId, companyId, ",
			"userId, userName, createDate, modifiedDate, classNameId, ",
			"classPK, commerceChannelId) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

		try (PreparedStatement preparedStatement1 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, insertCommerceCatalogSQL);
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, insertCommerceChannelSQL);
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, insertCommerceChannelRelSQL);
			Statement s1 = connection.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = s1.executeQuery(
				"select distinct groupId, companyId, userId, userName, " +
					"defaultLanguageId from CPDefinition")) {

			long cpDefinitionClassNameId =
				_classNameLocalService.getClassNameId(
					CPDefinition.class.getName());

			while (resultSet.next()) {
				long commerceCatalogId = increment();
				long commerceChannelId = increment();
				long groupId = resultSet.getLong("groupId");
				long companyId = resultSet.getLong("companyId");
				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");
				Date date = new Date(System.currentTimeMillis());
				String defaultLanguageId = resultSet.getString(
					"defaultLanguageId");

				Group siteGroup = _groupLocalService.getGroup(groupId);

				preparedStatement1.setLong(1, commerceCatalogId);
				preparedStatement1.setLong(2, companyId);
				preparedStatement1.setLong(3, userId);
				preparedStatement1.setString(4, userName);
				preparedStatement1.setDate(5, date);
				preparedStatement1.setDate(6, date);
				preparedStatement1.setString(
					7, siteGroup.getName(defaultLanguageId));
				preparedStatement1.setString(8, defaultLanguageId);

				preparedStatement1.addBatch();

				preparedStatement2.setLong(1, commerceChannelId);
				preparedStatement2.setLong(2, companyId);
				preparedStatement2.setLong(3, userId);
				preparedStatement2.setString(4, userName);
				preparedStatement2.setDate(5, date);
				preparedStatement2.setDate(6, date);
				preparedStatement2.setString(
					7, siteGroup.getName(defaultLanguageId));
				preparedStatement2.setLong(8, siteGroup.getGroupId());
				preparedStatement2.setString(
					9, CommerceChannelConstants.CHANNEL_TYPE_SITE);

				preparedStatement2.addBatch();

				Group catalogGroup = _groupLocalService.addGroup(
					userId, GroupConstants.DEFAULT_PARENT_GROUP_ID,
					CommerceCatalog.class.getName(), commerceCatalogId,
					GroupConstants.DEFAULT_LIVE_GROUP_ID,
					siteGroup.getNameMap(), null,
					GroupConstants.TYPE_SITE_PRIVATE, false,
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false,
					true, null);

				Group channelGroup = _groupLocalService.addGroup(
					userId, GroupConstants.DEFAULT_PARENT_GROUP_ID,
					CommerceChannel.class.getName(), commerceChannelId,
					GroupConstants.DEFAULT_LIVE_GROUP_ID,
					siteGroup.getNameMap(), null,
					GroupConstants.TYPE_SITE_PRIVATE, false,
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false,
					true, null);

				String updateTableGroupIdSQL =
					"update %s set groupId = %s where groupId = %s";

				runSQL(
					String.format(
						updateTableGroupIdSQL, "CPDefinition",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));

				Statement s2 = connection.createStatement(
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

				ResultSet cpDefinitionsResultSet = s2.executeQuery(
					"select distinct cpDefinitionId from CPDefinition where " +
						"groupId = " + catalogGroup.getGroupId());

				while (cpDefinitionsResultSet.next()) {
					long commerceChannelRelId = increment();

					preparedStatement3.setLong(1, commerceChannelRelId);

					preparedStatement3.setLong(2, companyId);
					preparedStatement3.setLong(3, userId);
					preparedStatement3.setString(4, userName);
					preparedStatement3.setDate(5, date);
					preparedStatement3.setDate(6, date);
					preparedStatement3.setLong(7, cpDefinitionClassNameId);
					preparedStatement3.setLong(
						8, cpDefinitionsResultSet.getLong("cpDefinitionId"));
					preparedStatement3.setLong(9, commerceChannelId);

					preparedStatement3.addBatch();
				}

				runSQL(
					String.format(
						updateTableGroupIdSQL, "AssetEntry",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "AssetCategory",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "CPAttachmentFileEntry",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "CPDefinitionLink",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "CPDefinitionOptionRel",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "CPDefinitionOptionValueRel",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "CPDSpecificationOptionValue",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "CPDisplayLayout",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "CPInstance",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
				runSQL(
					String.format(
						updateTableGroupIdSQL, "CProduct",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));

				if (hasTable("CPFriendlyURLEntry")) {
					runSQL(
						String.format(
							updateTableGroupIdSQL, "CPFriendlyURLEntry",
							GroupConstants.DEFAULT_LIVE_GROUP_ID,
							siteGroup.getGroupId()));
				}

				runSQL(
					String.format(
						updateTableGroupIdSQL, "CommerceOrder",
						channelGroup.getGroupId(), siteGroup.getGroupId()));

				runSQL(
					String.format(
						updateTableGroupIdSQL, "CommerceShipment",
						channelGroup.getGroupId(), siteGroup.getGroupId()));

				runSQL(
					String.format(
						updateTableGroupIdSQL, "CommercePriceList",
						catalogGroup.getGroupId(), siteGroup.getGroupId()));
			}

			preparedStatement1.executeBatch();

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			CommerceCatalogTable.create(), CommerceChannelRelTable.create(),
			CommerceChannelTable.create()
		};
	}

	private final ClassNameLocalService _classNameLocalService;
	private final GroupLocalService _groupLocalService;

}
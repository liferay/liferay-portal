/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_5;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Balazs Breier
 */
public class CommerceOrderAttachmentUpgradeProcess extends UpgradeProcess {

	public CommerceOrderAttachmentUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		ResourceLocalService resourceLocalService) {

		_classNameLocalService = classNameLocalService;
		_resourceLocalService = resourceLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select DLFileEntry.fileEntryId, ",
						"DLFileEntry.companyId, DLFileEntry.userId, ",
						"DLFileEntry.userName, DLFileEntry.createDate, ",
						"DLFileEntry.modifiedDate, DLFileEntry.title, ",
						"DLFileEntry.classPK, CommerceOrder.groupId from ",
						"DLFileEntry inner join CommerceOrder on ",
						"CommerceOrder.commerceOrderId = DLFileEntry.classPK ",
						"where DLFileEntry.classNameId = ? and not exists ",
						"(select 1 from CommerceOrderAttachment where ",
						"CommerceOrderAttachment.fileEntryId = ",
						"DLFileEntry.fileEntryId)"));
			PreparedStatement insertPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"insert into CommerceOrderAttachment (mvccVersion, ",
						"uuid_, externalReferenceCode, ",
						"commerceOrderAttachmentId, groupId, companyId, ",
						"userId, userName, createDate, modifiedDate, ",
						"commerceOrderId, fileEntryId, priority, restricted, ",
						"title, type_) values (0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
						"?, ?, 0, ?, ?, ?)"))) {

			selectPreparedStatement.setLong(
				1,
				_classNameLocalService.getClassNameId(
					CommerceOrder.class.getName()));

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					_addCommerceOrderAttachment(
						insertPreparedStatement, resultSet);
				}
			}
		}
	}

	private void _addCommerceOrderAttachment(
			PreparedStatement insertPreparedStatement, ResultSet resultSet)
		throws Exception {

		String uuid = PortalUUIDUtil.generate();

		insertPreparedStatement.setString(1, uuid);
		insertPreparedStatement.setString(2, uuid);

		long commerceOrderAttachmentId = increment();

		insertPreparedStatement.setLong(3, commerceOrderAttachmentId);

		insertPreparedStatement.setLong(4, resultSet.getLong("groupId"));
		insertPreparedStatement.setLong(5, resultSet.getLong("companyId"));
		insertPreparedStatement.setLong(6, resultSet.getLong("userId"));
		insertPreparedStatement.setString(7, resultSet.getString("userName"));
		insertPreparedStatement.setTimestamp(
			8, resultSet.getTimestamp("createDate"));
		insertPreparedStatement.setTimestamp(
			9, resultSet.getTimestamp("modifiedDate"));
		insertPreparedStatement.setLong(10, resultSet.getLong("classPK"));
		insertPreparedStatement.setLong(11, resultSet.getLong("fileEntryId"));
		insertPreparedStatement.setBoolean(12, false);
		insertPreparedStatement.setString(
			13, StringUtil.shorten(resultSet.getString("title"), 75));
		insertPreparedStatement.setString(14, "purchaseOrderDocument");

		insertPreparedStatement.executeUpdate();

		_resourceLocalService.addModelResources(
			resultSet.getLong("companyId"), resultSet.getLong("groupId"),
			resultSet.getLong("userId"),
			CommerceOrderAttachment.class.getName(), commerceOrderAttachmentId,
			null, null);
	}

	private final ClassNameLocalService _classNameLocalService;
	private final ResourceLocalService _resourceLocalService;

}
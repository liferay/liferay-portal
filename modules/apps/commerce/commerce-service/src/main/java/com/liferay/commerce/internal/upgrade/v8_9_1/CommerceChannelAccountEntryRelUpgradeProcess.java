/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v8_9_1;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Crescenzo Rega
 */
public class CommerceChannelAccountEntryRelUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select CChannelAccountEntryRel.ctCollectionId, ",
					"CChannelAccountEntryRel.CChannelAccountEntryRelId from ",
					"CChannelAccountEntryRel inner join CommercePriceList on ",
					"CChannelAccountEntryRel.classPK = CommercePriceList.",
					"commercePriceListId inner join ClassName_ on ",
					"CChannelAccountEntryRel.classNameId = ClassName_.",
					"classNameId where ClassName_.classNameId = ?"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CChannelAccountEntryRel set classNameId = ? " +
						"where ctCollectionId = ? and " +
							"CChannelAccountEntryRelId = ?")) {

			preparedStatement1.setLong(
				1,
				ClassNameLocalServiceUtil.getClassNameId(
					CommerceDiscount.class));

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					preparedStatement2.setLong(
						1,
						ClassNameLocalServiceUtil.getClassNameId(
							CommercePriceList.class));
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						3, resultSet.getLong("CChannelAccountEntryRelId"));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

}
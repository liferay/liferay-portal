/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v5_0_0;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPageTemplateStructureUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement deletePreparedStatement =
				connection.prepareStatement(
					"delete from LayoutPageTemplateStructure where " +
						"ctCollectionId = ? and " +
							"layoutPageTemplateStructureId = ?");
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, layoutPageTemplateStructureId, " +
					"classPK from LayoutPageTemplateStructure where " +
						"classNameId = ?");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update LayoutPageTemplateStructure set classNameId = ?, " +
						"classPK = ? where ctCollectionId = ? and " +
							"layoutPageTemplateStructureId = ?")) {

			preparedStatement1.setLong(
				1, PortalUtil.getClassNameId(LayoutPageTemplateEntry.class));

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long ctCollectionId = resultSet.getLong("ctCollectionId");
					long layoutPageTemplateStructureId = resultSet.getLong(
						"layoutPageTemplateStructureId");
					long classPK = resultSet.getLong("classPK");

					long plid = _getPlidFromLayoutPageTemplateEntry(
						ctCollectionId, classPK);

					if (_hasLayoutPageTemplateStructure(ctCollectionId, plid)) {
						deletePreparedStatement.setLong(1, ctCollectionId);
						deletePreparedStatement.setLong(
							2, layoutPageTemplateStructureId);

						deletePreparedStatement.executeUpdate();

						continue;
					}

					preparedStatement2.setLong(
						1, PortalUtil.getClassNameId(Layout.class));
					preparedStatement2.setLong(2, plid);
					preparedStatement2.setLong(3, ctCollectionId);
					preparedStatement2.setLong(
						4, layoutPageTemplateStructureId);

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private long _getPlidFromLayoutPageTemplateEntry(
			long ctCollectionId, long layoutPageTemplateEntryId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select plid from LayoutPageTemplateEntry where " +
					"ctCollectionId = ? and layoutPageTemplateEntryId = ?")) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, layoutPageTemplateEntryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("plid");
				}
			}
		}

		return 0;
	}

	private boolean _hasLayoutPageTemplateStructure(
			long ctCollectionId, long classPK)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select 1 from LayoutPageTemplateStructure where " +
					"ctCollectionId = ? and classNameId = ? and classPK = ?")) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(
				2, PortalUtil.getClassNameId(Layout.class));
			preparedStatement.setLong(3, classPK);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

}
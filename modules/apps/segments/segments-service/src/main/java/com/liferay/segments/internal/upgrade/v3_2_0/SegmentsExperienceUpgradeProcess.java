/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v3_2_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eudaldo Alonso
 */
public class SegmentsExperienceUpgradeProcess extends UpgradeProcess {

	public SegmentsExperienceUpgradeProcess(
		LayoutLocalService layoutLocalService) {

		_layoutLocalService = layoutLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		String fragmentEntryLinkColumnName = "plid";

		if (!hasColumn("FragmentEntryLink", "plid")) {
			fragmentEntryLinkColumnName = "classPK";
		}

		String layoutPageTemplateStructureColumnName = "plid";

		if (!hasColumn("LayoutPageTemplateStructure", "plid")) {
			layoutPageTemplateStructureColumnName = "classPK";
		}

		boolean hasLayoutPageTemplateStructureRelCTCollectionId = hasColumn(
			"LayoutPageTemplateStructureRel", "ctCollectionId");

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
			"select * from SegmentsExperience");
			 PreparedStatement preparedStatement2 =
				 AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					 connection,
					 StringBundler.concat(
						 "insert into SegmentsExperience (mvccVersion, ",
						 "ctCollectionId, uuid_, externalReferenceCode, ",
						 "segmentsExperienceId, groupId, companyId, userId, ",
						 "userName, createDate, modifiedDate, segmentsEntryId, ",
						 "segmentsExperienceKey, plid, name, priority, ",
						 "active_, typeSettings, lastPublishDate) values (?, ",
						 "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
						 "?)"));
			 PreparedStatement preparedStatement3 =
				 AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					 connection,
					 StringBundler.concat(
						 "update FragmentEntryLink set segmentsExperienceId = ",
						 "? where ctCollectionId = ? and segmentsExperienceId ",
						 "= ? and ",
						 fragmentEntryLinkColumnName, " = ?"));
			 PreparedStatement preparedStatement4 =
				 AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					 connection,
					 _buildUpdateLayoutPageTemplateStructureRelSQL(
						 layoutPageTemplateStructureColumnName,
						 hasLayoutPageTemplateStructureRelCTCollectionId));

			 ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				Layout layout = _layoutLocalService.fetchLayout(
					resultSet.getLong("plid"));

				if (layout == null) {
					continue;
				}

				Layout draftLayout = layout.fetchDraftLayout();

				long ctCollectionId = resultSet.getLong("ctCollectionId");
				long groupId = resultSet.getLong("groupId");
				String segmentsExperienceKey = resultSet.getString(
					"segmentsExperienceKey");

				if ((draftLayout == null) ||
					_existDraftLayoutSegmentsExperience(
						ctCollectionId, groupId, segmentsExperienceKey,
						draftLayout.getPlid())) {

					continue;
				}

				preparedStatement2.setLong(1, 0);
				preparedStatement2.setLong(2, ctCollectionId);

				String uuid = PortalUUIDUtil.generate();

				preparedStatement2.setString(3, uuid);
				preparedStatement2.setString(4, uuid);

				long draftLayoutSegmentsExperienceId = increment();

				preparedStatement2.setLong(5, draftLayoutSegmentsExperienceId);

				preparedStatement2.setLong(6, groupId);
				preparedStatement2.setLong(7, resultSet.getLong("companyId"));
				preparedStatement2.setLong(8, resultSet.getLong("userId"));
				preparedStatement2.setString(
					9, resultSet.getString("userName"));
				preparedStatement2.setDate(10, resultSet.getDate("createDate"));
				preparedStatement2.setDate(
					11, resultSet.getDate("modifiedDate"));
				preparedStatement2.setLong(
					12, resultSet.getLong("segmentsEntryId"));
				preparedStatement2.setString(
					13, segmentsExperienceKey + "_" + draftLayout.getPlid());
				preparedStatement2.setLong(14, draftLayout.getPlid());
				preparedStatement2.setString(15, resultSet.getString("name"));
				preparedStatement2.setLong(16, resultSet.getInt("priority"));
				preparedStatement2.setBoolean(
					17, resultSet.getBoolean("active_"));
				preparedStatement2.setString(
					18, resultSet.getString("typeSettings"));
				preparedStatement2.setDate(
					19, resultSet.getDate("lastPublishDate"));

				preparedStatement2.addBatch();

				long segmentsExperienceId = resultSet.getLong(
					"segmentsExperienceId");

				if (hasColumn(
					"FragmentEntryLink", "segmentsExperienceId")) {
					preparedStatement3.setLong(
						1, draftLayoutSegmentsExperienceId);
					preparedStatement3.setLong(2, ctCollectionId);
					preparedStatement3.setLong(3, segmentsExperienceId);
					preparedStatement3.setLong(4, draftLayout.getPlid());

					preparedStatement3.addBatch();

					preparedStatement3.executeBatch();
				}

				int index = 1;

				preparedStatement4.setLong(
					index++, draftLayoutSegmentsExperienceId);

				if (hasLayoutPageTemplateStructureRelCTCollectionId) {
					preparedStatement4.setLong(index++, ctCollectionId);
				}

				preparedStatement4.setLong(index++, segmentsExperienceId);
				preparedStatement4.setLong(index, draftLayout.getPlid());

				preparedStatement4.addBatch();
			}

			preparedStatement2.executeBatch();

			preparedStatement4.executeBatch();
		}
	}

	private String _buildUpdateLayoutPageTemplateStructureRelSQL(
		String layoutPageTemplateStructureColumnName,
		boolean hasLayoutPageTemplateStructureRelCTCollectionId)
		throws Exception {

		StringBundler sb = new StringBundler(9);

		sb.append("update LayoutPageTemplateStructureRel set ");
		sb.append("segmentsExperienceId = ? where ");

		if (hasLayoutPageTemplateStructureRelCTCollectionId) {
			sb.append("ctCollectionId = ? and ");
		}

		sb.append("segmentsExperienceId = ? and ");
		sb.append("LayoutPageTemplateStructureId in (select ");
		sb.append("LayoutPageTemplateStructureId from ");
		sb.append("LayoutPageTemplateStructure where ");
		sb.append(layoutPageTemplateStructureColumnName);
		sb.append(" = ?)");

		return sb.toString();
	}

	private boolean _existDraftLayoutSegmentsExperience(
		long ctCollectionId, long groupId, String segmentsExperienceKey,
		long plid)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
			StringBundler.concat(
				"select 1 from SegmentsExperience where ",
				"ctCollectionId = ? and groupId = ? and ",
				"segmentsExperienceKey = ? and plid = ?"))) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, groupId);
			preparedStatement.setString(3, segmentsExperienceKey);
			preparedStatement.setLong(4, plid);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private final LayoutLocalService _layoutLocalService;

}
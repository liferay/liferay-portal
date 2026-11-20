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
		_addDraftLayoutSegmentsExperiences();
		_updateSegmentsExperienceIds();
	}

	private void _addDraftLayoutSegmentsExperiences() throws Exception {
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
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				Layout draftLayout = _fetchDraftLayout(
					resultSet.getLong("plid"));

				if (draftLayout == null) {
					continue;
				}

				long ctCollectionId = resultSet.getLong("ctCollectionId");
				long groupId = resultSet.getLong("groupId");
				String segmentsExperienceKey = resultSet.getString(
					"segmentsExperienceKey");

				long draftLayoutSegmentsExperienceId =
					_getDraftLayoutSegmentsExperienceId(
						ctCollectionId, groupId, segmentsExperienceKey,
						draftLayout.getPlid());

				if (draftLayoutSegmentsExperienceId > 0) {
					continue;
				}

				preparedStatement2.setLong(1, 0);
				preparedStatement2.setLong(2, ctCollectionId);

				String uuid = PortalUUIDUtil.generate();

				preparedStatement2.setString(3, uuid);
				preparedStatement2.setString(4, uuid);

				preparedStatement2.setLong(5, increment());
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
				preparedStatement2.setString(13, segmentsExperienceKey);
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
			}

			preparedStatement2.executeBatch();
		}
	}

	private Layout _fetchDraftLayout(long plid) {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return null;
		}

		return layout.fetchDraftLayout();
	}

	private long _getDraftLayoutSegmentsExperienceId(
			long ctCollectionId, long groupId, String segmentsExperienceKey,
			long plid)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select segmentsExperienceId from SegmentsExperience ",
					"where ctCollectionId = ? and groupId = ? and ",
					"segmentsExperienceKey = ? and plid = ?"))) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, groupId);
			preparedStatement.setString(3, segmentsExperienceKey);
			preparedStatement.setLong(4, plid);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("segmentsExperienceId");
				}

				return 0;
			}
		}
	}

	private String _getUpdateFragmentEntryLinkSQL(
		String columnName, boolean hasCTCollectionId) {

		StringBundler sb = new StringBundler(6);

		sb.append("update FragmentEntryLink set segmentsExperienceId = ? ");
		sb.append("where groupId = ? and ");

		if (hasCTCollectionId) {
			sb.append("ctCollectionId = ? and ");
		}

		sb.append("segmentsExperienceId = ? and ");
		sb.append(columnName);
		sb.append(" = ?");

		return sb.toString();
	}

	private String _getUpdateLayoutPageTemplateStructureRelSQL(
		String columnName, boolean hasCTCollectionId) {

		StringBundler sb = new StringBundler(9);

		sb.append("update LayoutPageTemplateStructureRel set ");
		sb.append("segmentsExperienceId = ? where ");

		if (hasCTCollectionId) {
			sb.append("ctCollectionId = ? and ");
		}

		sb.append("segmentsExperienceId = ? and ");
		sb.append("layoutPageTemplateStructureId in (select ");
		sb.append("layoutPageTemplateStructureId from ");
		sb.append("LayoutPageTemplateStructure where groupId = ? and ");
		sb.append(columnName);
		sb.append(" = ?)");

		return sb.toString();
	}

	private void _updateSegmentsExperienceIds() throws Exception {
		String fragmentEntryLinkColumnName = "plid";

		if (!hasColumn("FragmentEntryLink", "plid")) {
			fragmentEntryLinkColumnName = "classPK";
		}

		boolean hasFragmentEntryLinkCTCollectionId = hasColumn(
			"FragmentEntryLink", "ctCollectionId");
		boolean hasFragmentEntryLinkSegmentsExperienceId = hasColumn(
			"FragmentEntryLink", "segmentsExperienceId");
		boolean hasLayoutPageTemplateStructureRelCTCollectionId = hasColumn(
			"LayoutPageTemplateStructureRel", "ctCollectionId");

		String layoutPageTemplateStructureColumnName = "plid";

		if (!hasColumn("LayoutPageTemplateStructure", "plid")) {
			layoutPageTemplateStructureColumnName = "classPK";
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select * from SegmentsExperience");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					_getUpdateFragmentEntryLinkSQL(
						fragmentEntryLinkColumnName,
						hasFragmentEntryLinkCTCollectionId));
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					_getUpdateLayoutPageTemplateStructureRelSQL(
						layoutPageTemplateStructureColumnName,
						hasLayoutPageTemplateStructureRelCTCollectionId));

			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				Layout draftLayout = _fetchDraftLayout(
					resultSet.getLong("plid"));

				if (draftLayout == null) {
					continue;
				}

				long ctCollectionId = resultSet.getLong("ctCollectionId");
				long groupId = resultSet.getLong("groupId");

				long draftLayoutSegmentsExperienceId =
					_getDraftLayoutSegmentsExperienceId(
						ctCollectionId, groupId,
						resultSet.getString("segmentsExperienceKey"),
						draftLayout.getPlid());

				if (draftLayoutSegmentsExperienceId <= 0) {
					continue;
				}

				long segmentsExperienceId = resultSet.getLong(
					"segmentsExperienceId");

				int index = 1;

				if (hasFragmentEntryLinkSegmentsExperienceId) {
					preparedStatement2.setLong(
						index++, draftLayoutSegmentsExperienceId);
					preparedStatement2.setLong(index++, groupId);

					if (hasFragmentEntryLinkCTCollectionId) {
						preparedStatement2.setLong(index++, ctCollectionId);
					}

					preparedStatement2.setLong(index++, segmentsExperienceId);
					preparedStatement2.setLong(index, draftLayout.getPlid());

					preparedStatement2.addBatch();

					index = 1;
				}

				preparedStatement3.setLong(
					index++, draftLayoutSegmentsExperienceId);

				if (hasLayoutPageTemplateStructureRelCTCollectionId) {
					preparedStatement3.setLong(index++, ctCollectionId);
				}

				preparedStatement3.setLong(index++, segmentsExperienceId);
				preparedStatement3.setLong(index++, groupId);
				preparedStatement3.setLong(index, draftLayout.getPlid());

				preparedStatement3.addBatch();
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}
	}

	private final LayoutLocalService _layoutLocalService;

}
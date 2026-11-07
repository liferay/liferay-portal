/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v6_1_1;

import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alberto Chaparro
 * @author Adolfo Pérez
 */
public class LayoutUpgradeProcess extends UpgradeProcess {

	public LayoutUpgradeProcess(ClassNameLocalService classNameLocalService) {
		_layoutSetPrototypeClassNameId = classNameLocalService.getClassNameId(
			LayoutSetPrototype.class);
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (!_hasLayoutSetPrototypeLinks()) {
			return;
		}

		// Order is important

		_upgradeLayoutLayoutSetPrototypeLayoutERCs();

		boolean upgraded = true;

		while (upgraded) {
			upgraded = _upgradeParentLayoutLayoutSetPrototypeLayoutERCs();
		}

		_upgradeLayoutExternalReferenceCodes();

		_upgradeSegmentsExperiences(true);

		_upgradeSegmentsExperiences(false);

		_upgradeFragmentEntryLinks(true);

		_upgradeFragmentEntryLinks(false);
	}

	private Map<String, FragmentEntryLinkInfo>
			_getLayoutSetPrototypeFragmentEntryLinkInfos(
				long ctCollectionId, long plid, long segmentsExperienceId)
		throws Exception {

		Map<String, FragmentEntryLinkInfo> fragmentEntryLinkInfos =
			new HashMap<>();

		Map<Long, String> layoutStructureItemIds = _getLayoutStructureItemIds(
			ctCollectionId, plid, segmentsExperienceId);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select externalReferenceCode, fragmentEntryLinkId, ",
					"originalFragmentEntryLinkERC, uuid_ from ",
					"FragmentEntryLink where (ctCollectionId = 0 or ",
					"ctCollectionId = ?) and plid = ? and ",
					"segmentsExperienceId = ? order by ctCollectionId"))) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, plid);
			preparedStatement.setLong(3, segmentsExperienceId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String layoutStructureItemId = layoutStructureItemIds.get(
						resultSet.getLong("fragmentEntryLinkId"));

					if (layoutStructureItemId == null) {
						continue;
					}

					fragmentEntryLinkInfos.put(
						layoutStructureItemId,
						new FragmentEntryLinkInfo(
							resultSet.getString("externalReferenceCode"),
							resultSet.getString("originalFragmentEntryLinkERC"),
							resultSet.getString("uuid_")));
				}
			}
		}

		return fragmentEntryLinkInfos;
	}

	private String _getLayoutSetPrototypeLayoutJoinSQL(
		String layoutAlias, String layoutSetPrototypeLayoutAlias,
		String layoutSetPrototypeLayoutERCColumnName) {

		return StringBundler.concat(
			" left join Layout ", layoutSetPrototypeLayoutAlias, " on (",
			layoutSetPrototypeLayoutAlias, ".ctCollectionId = 0 or ",
			layoutAlias, ".ctCollectionId = ", layoutSetPrototypeLayoutAlias,
			".ctCollectionId) and ", layoutAlias, ".companyId = ",
			layoutSetPrototypeLayoutAlias, ".companyId and ",
			layoutSetPrototypeLayoutERCColumnName, " = ",
			layoutSetPrototypeLayoutAlias, ".externalReferenceCode and ",
			layoutSetPrototypeLayoutAlias, ".groupId in (select groupId from ",
			"Group_ where classNameId = ", _layoutSetPrototypeClassNameId, ")");
	}

	private Map<Long, String> _getLayoutStructureItemIds(
			long ctCollectionId, long plid, long segmentsExperienceId)
		throws Exception {

		Map<Long, String> layoutStructureItemIds = new HashMap<>();

		String data = null;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select LayoutPageTemplateStructureRel.data_ from ",
					"LayoutPageTemplateStructureRel inner join ",
					"LayoutPageTemplateStructure on ",
					"(LayoutPageTemplateStructure.ctCollectionId = 0 or ",
					"LayoutPageTemplateStructure.ctCollectionId = ?) and ",
					"LayoutPageTemplateStructure.",
					"layoutPageTemplateStructureId = ",
					"LayoutPageTemplateStructureRel.",
					"layoutPageTemplateStructureId where ",
					"LayoutPageTemplateStructure.plid = ? and ",
					"(LayoutPageTemplateStructureRel.ctCollectionId = 0 or ",
					"LayoutPageTemplateStructureRel.ctCollectionId = ?) and ",
					"LayoutPageTemplateStructureRel.segmentsExperienceId = ? ",
					"order by LayoutPageTemplateStructureRel.ctCollectionId ",
					"desc"))) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, plid);
			preparedStatement.setLong(3, ctCollectionId);
			preparedStatement.setLong(4, segmentsExperienceId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					data = resultSet.getString("data_");
				}
			}
		}

		if (Validator.isNull(data)) {
			return layoutStructureItemIds;
		}

		LayoutStructure layoutStructure = LayoutStructure.of(data);

		Map<Long, LayoutStructureItem> layoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		for (Map.Entry<Long, LayoutStructureItem> entry :
				layoutStructureItems.entrySet()) {

			LayoutStructureItem layoutStructureItem = entry.getValue();

			layoutStructureItemIds.put(
				entry.getKey(), layoutStructureItem.getItemId());
		}

		return layoutStructureItemIds;
	}

	private boolean _hasLayoutSetPrototypeLinks() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select 1 from LayoutSet where layoutSetPrototypeUuid is not " +
					"null and layoutSetPrototypeUuid != ''");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			return resultSet.next();
		}
	}

	private void _upgradeFragmentEntryLinks(boolean privateLayout)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct FragmentEntryLink.ctCollectionId, ",
					"FragmentEntryLink.externalReferenceCode, ",
					"FragmentEntryLink.fragmentEntryLinkId, ",
					"FragmentEntryLink.originalFragmentEntryLinkERC, ",
					"FragmentEntryLink.plid, ",
					"FragmentEntryLink.segmentsExperienceId, ",
					"FragmentEntryLink.uuid_, Layout2.plid as ",
					"layoutSetPrototypePlid, ",
					"SegmentsExperience2.segmentsExperienceId as ",
					"layoutSetPrototypeSegmentsExperienceId from ",
					"FragmentEntryLink inner join Layout Layout1 on ",
					"(Layout1.ctCollectionId = 0 or ",
					"FragmentEntryLink.ctCollectionId = ",
					"Layout1.ctCollectionId) and FragmentEntryLink.plid = ",
					"Layout1.plid",
					_getLayoutSetPrototypeLayoutJoinSQL(
						"Layout1", "Layout2",
						"Layout1.layoutSetPrototypeLayoutERC"),
					" inner join SegmentsExperience SegmentsExperience1 on ",
					"(SegmentsExperience1.ctCollectionId = 0 or ",
					"FragmentEntryLink.ctCollectionId = ",
					"SegmentsExperience1.ctCollectionId) and ",
					"FragmentEntryLink.segmentsExperienceId = ",
					"SegmentsExperience1.segmentsExperienceId inner join ",
					"SegmentsExperience SegmentsExperience2 on ",
					"(SegmentsExperience2.ctCollectionId = 0 or ",
					"FragmentEntryLink.ctCollectionId = ",
					"SegmentsExperience2.ctCollectionId) and Layout2.groupId ",
					"= SegmentsExperience2.groupId and Layout2.plid = ",
					"SegmentsExperience2.plid and ",
					"SegmentsExperience1.segmentsExperienceKey = ",
					"SegmentsExperience2.segmentsExperienceKey where ",
					"Layout1.privateLayout = ? and ",
					"Layout1.layoutSetPrototypeLayoutERC is not null and ",
					"Layout1.layoutSetPrototypeLayoutERC != ''"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update FragmentEntryLink set externalReferenceCode = ",
						"?, originalFragmentEntryLinkERC = ?, uuid_ = ? where ",
						"ctCollectionId = ? and fragmentEntryLinkId = ?"))) {

			preparedStatement1.setBoolean(1, privateLayout);

			Map<String, Map<String, FragmentEntryLinkInfo>>
				fragmentEntryLinkInfosMap = new HashMap<>();
			Map<String, Map<Long, String>> layoutStructureItemIdsMap =
				new HashMap<>();

			String suffix = StringPool.BLANK;

			if (privateLayout) {
				suffix = LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_PRIVATE;
			}

			Map<Long, String> uuids = new HashMap<>();

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long ctCollectionId = resultSet.getLong("ctCollectionId");
					long plid = resultSet.getLong("plid");
					long segmentsExperienceId = resultSet.getLong(
						"segmentsExperienceId");

					String layoutStructureItemIdsKey = StringBundler.concat(
						ctCollectionId, StringPool.POUND, plid,
						StringPool.POUND, segmentsExperienceId);

					Map<Long, String> layoutStructureItemIds =
						layoutStructureItemIdsMap.get(
							layoutStructureItemIdsKey);

					if (layoutStructureItemIds == null) {
						layoutStructureItemIds = _getLayoutStructureItemIds(
							ctCollectionId, plid, segmentsExperienceId);

						layoutStructureItemIdsMap.put(
							layoutStructureItemIdsKey, layoutStructureItemIds);
					}

					long fragmentEntryLinkId = resultSet.getLong(
						"fragmentEntryLinkId");

					String layoutStructureItemId = layoutStructureItemIds.get(
						fragmentEntryLinkId);

					if (layoutStructureItemId == null) {
						continue;
					}

					long layoutSetPrototypePlid = resultSet.getLong(
						"layoutSetPrototypePlid");
					long layoutSetPrototypeSegmentsExperienceId =
						resultSet.getLong(
							"layoutSetPrototypeSegmentsExperienceId");

					String fragmentEntryLinkInfosKey = StringBundler.concat(
						ctCollectionId, StringPool.POUND,
						layoutSetPrototypePlid, StringPool.POUND,
						layoutSetPrototypeSegmentsExperienceId);

					Map<String, FragmentEntryLinkInfo> fragmentEntryLinkInfos =
						fragmentEntryLinkInfosMap.get(
							fragmentEntryLinkInfosKey);

					if (fragmentEntryLinkInfos == null) {
						fragmentEntryLinkInfos =
							_getLayoutSetPrototypeFragmentEntryLinkInfos(
								ctCollectionId, layoutSetPrototypePlid,
								layoutSetPrototypeSegmentsExperienceId);

						fragmentEntryLinkInfosMap.put(
							fragmentEntryLinkInfosKey, fragmentEntryLinkInfos);
					}

					FragmentEntryLinkInfo fragmentEntryLinkInfo =
						fragmentEntryLinkInfos.get(layoutStructureItemId);

					if (fragmentEntryLinkInfo == null) {
						continue;
					}

					String externalReferenceCode = resultSet.getString(
						"externalReferenceCode");

					String newExternalReferenceCode =
						fragmentEntryLinkInfo._externalReferenceCode + suffix;

					String originalExternalReferenceCode = resultSet.getString(
						"originalFragmentEntryLinkERC");

					String newOriginalExternalReferenceCode =
						originalExternalReferenceCode;

					if (Validator.isNotNull(
							fragmentEntryLinkInfo.
								_originalExternalReferenceCode)) {

						newOriginalExternalReferenceCode =
							fragmentEntryLinkInfo.
								_originalExternalReferenceCode + suffix;
					}

					String uuid = resultSet.getString("uuid_");

					String newUUID = uuid;

					if (privateLayout &&
						Objects.equals(uuid, fragmentEntryLinkInfo._uuid)) {

						newUUID = uuids.computeIfAbsent(
							fragmentEntryLinkId,
							curFragmentEntryLinkId ->
								PortalUUIDUtil.generate());
					}

					if (Objects.equals(
							externalReferenceCode, newExternalReferenceCode) &&
						Objects.equals(
							originalExternalReferenceCode,
							newOriginalExternalReferenceCode) &&
						Objects.equals(uuid, newUUID)) {

						continue;
					}

					preparedStatement2.setString(1, newExternalReferenceCode);
					preparedStatement2.setString(
						2, newOriginalExternalReferenceCode);
					preparedStatement2.setString(3, newUUID);
					preparedStatement2.setLong(4, ctCollectionId);
					preparedStatement2.setLong(5, fragmentEntryLinkId);

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private void _upgradeLayoutExternalReferenceCodes() throws Exception {
		runSQL(
			StringBundler.concat(
				"update Layout set externalReferenceCode = CONCAT(",
				"layoutSetPrototypeLayoutERC, '",
				LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_PRIVATE,
				"') where privateLayout = [$TRUE$] and ",
				"layoutSetPrototypeLayoutERC is not null and ",
				"layoutSetPrototypeLayoutERC != ''"));

		runSQL(
			StringBundler.concat(
				"update Layout set externalReferenceCode = ",
				"layoutSetPrototypeLayoutERC where privateLayout = [$FALSE$] ",
				"and layoutSetPrototypeLayoutERC is not null and ",
				"layoutSetPrototypeLayoutERC != '' and externalReferenceCode ",
				"!= layoutSetPrototypeLayoutERC"));
	}

	private void _upgradeLayoutLayoutSetPrototypeLayoutERCs() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct Layout1.externalReferenceCode, ",
					"Layout1.plid from Layout Layout1",
					_getLayoutSetPrototypeLayoutJoinSQL(
						"Layout1", "Layout2", "Layout1.externalReferenceCode"),
					" where (Layout1.layoutSetPrototypeLayoutERC is null or ",
					"Layout1.layoutSetPrototypeLayoutERC = '') and ",
					"Layout1.plid != Layout2.plid"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update Layout set layoutSetPrototypeLayoutERC = ? where " +
						"plid = ?");

			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				preparedStatement2.setString(
					1, resultSet.getString("externalReferenceCode"));
				preparedStatement2.setLong(2, resultSet.getLong("plid"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private boolean _upgradeParentLayoutLayoutSetPrototypeLayoutERCs()
		throws Exception {

		boolean upgraded = false;

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct Layout1.plid, ",
					"Layout1.layoutSetPrototypeLayoutERC, ",
					"Layout4.externalReferenceCode as ",
					"parentLayoutSetPrototypeLayoutERC from Layout Layout1 ",
					"inner join Layout Layout2 on Layout1.groupId = ",
					"Layout2.groupId and Layout1.privateLayout = ",
					"Layout2.privateLayout and Layout1.layoutId = ",
					"Layout2.parentLayoutId and (Layout2.ctCollectionId = 0 ",
					"or Layout1.ctCollectionId = Layout2.ctCollectionId) and ",
					"Layout1.layoutSetPrototypeLayoutERC = ",
					"Layout2.layoutSetPrototypeLayoutERC",
					_getLayoutSetPrototypeLayoutJoinSQL(
						"Layout1", "Layout3",
						"Layout1.layoutSetPrototypeLayoutERC"),
					" left join Layout Layout4 on (Layout4.ctCollectionId = 0 ",
					"or Layout3.ctCollectionId = Layout4.ctCollectionId) and ",
					"Layout3.groupId = Layout4.groupId and ",
					"Layout3.privateLayout = Layout4.privateLayout and ",
					"Layout3.parentLayoutId = Layout4.layoutId where ",
					"Layout1.layoutSetPrototypeLayoutERC is not null and ",
					"Layout1.layoutSetPrototypeLayoutERC != ''"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update Layout set layoutSetPrototypeLayoutERC = ? where " +
						"plid = ? and layoutSetPrototypeLayoutERC = ?");

			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				preparedStatement2.setString(
					1,
					GetterUtil.getString(
						resultSet.getString(
							"parentLayoutSetPrototypeLayoutERC")));
				preparedStatement2.setLong(2, resultSet.getLong("plid"));
				preparedStatement2.setString(
					3, resultSet.getString("layoutSetPrototypeLayoutERC"));

				preparedStatement2.addBatch();

				upgraded = true;
			}

			preparedStatement2.executeBatch();
		}

		return upgraded;
	}

	private void _upgradeSegmentsExperiences(boolean privateLayout)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct SegmentsExperience1.ctCollectionId, ",
					"SegmentsExperience1.externalReferenceCode, ",
					"SegmentsExperience1.segmentsExperienceId, ",
					"SegmentsExperience1.uuid_, ",
					"SegmentsExperience2.externalReferenceCode as ",
					"layoutSetPrototypeERC, SegmentsExperience2.uuid_ as ",
					"layoutSetPrototypeUUID from SegmentsExperience ",
					"SegmentsExperience1 inner join Layout Layout1 on ",
					"(Layout1.ctCollectionId = 0 or Layout1.ctCollectionId = ",
					"SegmentsExperience1.ctCollectionId) and Layout1.plid = ",
					"SegmentsExperience1.plid",
					_getLayoutSetPrototypeLayoutJoinSQL(
						"Layout1", "Layout2",
						"Layout1.layoutSetPrototypeLayoutERC"),
					" inner join SegmentsExperience SegmentsExperience2 on ",
					"(SegmentsExperience2.ctCollectionId = 0 or ",
					"SegmentsExperience1.ctCollectionId = ",
					"SegmentsExperience2.ctCollectionId) and Layout2.groupId ",
					"= SegmentsExperience2.groupId and Layout2.plid = ",
					"SegmentsExperience2.plid and ",
					"SegmentsExperience1.segmentsExperienceKey = ",
					"SegmentsExperience2.segmentsExperienceKey where ",
					"Layout1.privateLayout = ? and ",
					"Layout1.layoutSetPrototypeLayoutERC is not null and ",
					"Layout1.layoutSetPrototypeLayoutERC != ''"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update SegmentsExperience set externalReferenceCode ",
						"= ?, uuid_ = ? where ctCollectionId = ? and ",
						"segmentsExperienceId = ?"))) {

			preparedStatement1.setBoolean(1, privateLayout);

			String suffix = StringPool.BLANK;

			if (privateLayout) {
				suffix = LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_PRIVATE;
			}

			Map<Long, String> uuids = new HashMap<>();

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					String externalReferenceCode = resultSet.getString(
						"externalReferenceCode");
					long segmentsExperienceId = resultSet.getLong(
						"segmentsExperienceId");
					String uuid = resultSet.getString("uuid_");

					String newExternalReferenceCode =
						resultSet.getString("layoutSetPrototypeERC") + suffix;

					String newUUID = uuid;

					if (privateLayout &&
						Objects.equals(
							uuid,
							resultSet.getString("layoutSetPrototypeUUID"))) {

						newUUID = uuids.computeIfAbsent(
							segmentsExperienceId,
							curSegmentsExperienceId ->
								PortalUUIDUtil.generate());
					}

					if (Objects.equals(
							externalReferenceCode, newExternalReferenceCode) &&
						Objects.equals(uuid, newUUID)) {

						continue;
					}

					preparedStatement2.setString(1, newExternalReferenceCode);
					preparedStatement2.setString(2, newUUID);
					preparedStatement2.setLong(
						3, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(4, segmentsExperienceId);

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private final long _layoutSetPrototypeClassNameId;

	private static class FragmentEntryLinkInfo {

		private FragmentEntryLinkInfo(
			String externalReferenceCode, String originalExternalReferenceCode,
			String uuid) {

			_externalReferenceCode = externalReferenceCode;
			_originalExternalReferenceCode = originalExternalReferenceCode;
			_uuid = uuid;
		}

		private final String _externalReferenceCode;
		private final String _originalExternalReferenceCode;
		private final String _uuid;

	}

}
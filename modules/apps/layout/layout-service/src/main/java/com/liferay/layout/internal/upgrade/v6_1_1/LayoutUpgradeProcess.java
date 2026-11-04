/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v6_1_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
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

		// Order is important.

		_upgradeLayoutLayoutSetPrototypeLayoutERCs();

		boolean upgraded = true;

		while (upgraded) {
			upgraded = _upgradeParentLayoutLayoutSetPrototypeLayoutERCs();
		}

		_upgradeLayoutExternalReferenceCodes();

		_upgradeSegmentsExperiences(true);
		_upgradeSegmentsExperiences(false);
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
							key -> PortalUUIDUtil.generate());
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

}
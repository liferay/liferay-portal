/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v6_2_2;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutPageTemplateStructureRelUpgradeProcess
	extends UpgradeProcess {

	public LayoutPageTemplateStructureRelUpgradeProcess(
		DepotEntryLocalService depotEntryLocalService,
		FragmentEntryLocalService fragmentEntryLocalService,
		GroupLocalService groupLocalService) {

		_depotEntryLocalService = depotEntryLocalService;
		_fragmentEntryLocalService = fragmentEntryLocalService;
		_groupLocalService = groupLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select companyId, ctCollectionId, data_, groupId, ",
						"lPageTemplateStructureRelId from ",
						"LayoutPageTemplateStructureRel where data_ like ",
						"'%\"drop-zone\"%'"));
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update LayoutPageTemplateStructureRel set data_ = ? " +
						"where ctCollectionId = ? and " +
							"lPageTemplateStructureRelId = ?");

			ResultSet resultSet = selectPreparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String dataJSON = resultSet.getString("data_");

				if (Validator.isNull(dataJSON)) {
					continue;
				}

				JSONObject dataJSONObject = JSONFactoryUtil.createJSONObject(
					dataJSON);

				JSONObject itemsJSONObject = dataJSONObject.getJSONObject(
					"items");

				if (itemsJSONObject == null) {
					continue;
				}

				JSONObject dropZoneItemConfigJSONObject = null;

				for (String itemId : itemsJSONObject.keySet()) {
					JSONObject itemJSONObject = itemsJSONObject.getJSONObject(
						itemId);

					if (LayoutDataItemTypeConstants.TYPE_DROP_ZONE.equals(
							itemJSONObject.getString("type"))) {

						dropZoneItemConfigJSONObject =
							itemJSONObject.getJSONObject("config");

						break;
					}
				}

				if (dropZoneItemConfigJSONObject == null) {
					continue;
				}

				long companyId = resultSet.getLong("companyId");
				long scopeGroupId = resultSet.getLong("groupId");

				if (!_isNormalized(
						companyId, dropZoneItemConfigJSONObject,
						scopeGroupId)) {

					continue;
				}

				updatePreparedStatement.setString(1, dataJSONObject.toString());
				updatePreparedStatement.setLong(
					2, resultSet.getLong("ctCollectionId"));
				updatePreparedStatement.setLong(
					3, resultSet.getLong("lPageTemplateStructureRelId"));
				updatePreparedStatement.addBatch();
			}

			updatePreparedStatement.executeBatch();
		}
	}

	private FragmentEntry _fetchFragmentEntry(
		String fragmentEntryKey, long[] groupIds) {

		for (long groupId : groupIds) {
			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.fetchFragmentEntry(
					groupId, fragmentEntryKey);

			if (fragmentEntry == null) {
				continue;
			}

			return fragmentEntry;
		}

		return null;
	}

	private Set<String> _getExistingKeys(
		long companyId, JSONArray fragmentEntriesJSONArray, long scopeGroupId) {

		Set<String> existingKeys = new HashSet<>();

		for (int i = 0; i < fragmentEntriesJSONArray.length(); i++) {
			JSONObject jsonObject = fragmentEntriesJSONArray.getJSONObject(i);

			String fragmentEntryRendererKey = jsonObject.getString(
				"fragmentEntryRendererKey");

			if (Validator.isNotNull(fragmentEntryRendererKey)) {
				existingKeys.add(fragmentEntryRendererKey);

				continue;
			}

			String fragmentEntryERC = jsonObject.getString("fragmentEntryERC");

			if (Validator.isNull(fragmentEntryERC)) {
				continue;
			}

			Long fragmentEntryScopeGroupId = ScopeUtil.getItemGroupId(
				companyId, jsonObject.getString("fragmentEntryScopeERC"),
				scopeGroupId);

			if (fragmentEntryScopeGroupId == null) {
				continue;
			}

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.
					fetchFragmentEntryByExternalReferenceCode(
						fragmentEntryERC, fragmentEntryScopeGroupId);

			if (fragmentEntry == null) {
				continue;
			}

			existingKeys.add(fragmentEntry.getFragmentEntryKey());
		}

		return existingKeys;
	}

	private long[] _getGroupIds(long companyId, long scopeGroupId)
		throws PortalException {

		Group companyGroup = _groupLocalService.fetchCompanyGroup(companyId);

		long companyGroupId = 0;

		if (companyGroup != null) {
			companyGroupId = companyGroup.getGroupId();
		}

		return ArrayUtil.append(
			new long[] {scopeGroupId, companyGroupId},
			ListUtil.toLongArray(
				_depotEntryLocalService.getGroupConnectedDepotEntries(
					scopeGroupId, DepotConstants.TYPE_DESIGN_LIBRARY,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				DepotEntry::getGroupId));
	}

	private boolean _isNormalized(
			long companyId, JSONObject itemConfigJSONObject, long scopeGroupId)
		throws PortalException {

		if (!itemConfigJSONObject.has("fragmentEntryKeys")) {
			return false;
		}

		JSONArray fragmentEntryKeysJSONArray =
			itemConfigJSONObject.getJSONArray("fragmentEntryKeys");

		itemConfigJSONObject.remove("fragmentEntryKeys");

		List<String> fragmentEntryKeys = JSONUtil.toStringList(
			fragmentEntryKeysJSONArray);

		if (ListUtil.isEmpty(fragmentEntryKeys)) {
			return true;
		}

		JSONArray fragmentEntriesJSONArray = itemConfigJSONObject.getJSONArray(
			"fragmentEntries");

		if (fragmentEntriesJSONArray == null) {
			fragmentEntriesJSONArray = JSONUtil.putAll();
		}

		Set<String> existingKeys = _getExistingKeys(
			companyId, fragmentEntriesJSONArray, scopeGroupId);
		long[] groupIds = _getGroupIds(companyId, scopeGroupId);

		itemConfigJSONObject.put(
			"fragmentEntries",
			JSONUtil.concat(
				fragmentEntriesJSONArray,
				JSONUtil.toJSONArray(
					ListUtil.unique(fragmentEntryKeys),
					fragmentEntryKey -> {
						if (existingKeys.contains(fragmentEntryKey)) {
							return null;
						}

						FragmentEntry fragmentEntry = _fetchFragmentEntry(
							fragmentEntryKey, groupIds);

						if (fragmentEntry == null) {
							return JSONUtil.put(
								"fragmentEntryRendererKey", fragmentEntryKey);
						}

						return JSONUtil.put(
							"fragmentEntryERC",
							fragmentEntry.getExternalReferenceCode()
						).put(
							"fragmentEntryScopeERC",
							ScopeUtil.getItemScopeExternalReferenceCode(
								fragmentEntry.getGroupId(), scopeGroupId)
						);
					},
					_log)));

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureRelUpgradeProcess.class);

	private final DepotEntryLocalService _depotEntryLocalService;
	private final FragmentEntryLocalService _fragmentEntryLocalService;
	private final GroupLocalService _groupLocalService;

}
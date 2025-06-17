/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.model.listener;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Joshua Cords
 * @author Jürgen Kappler
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onBeforeRemove(Group group) throws ModelListenerException {
		_deleteAssetListEntries(group.getGroupId());
		_updateTypeSettings(group);
	}

	private void _deleteAssetListEntries(long groupId) {
		try {
			List<AssetListEntry> assetListEntries =
				_assetListEntryLocalService.getAssetListEntries(groupId);

			for (AssetListEntry assetListEntry : assetListEntries) {
				_assetListEntryLocalService.deleteAssetListEntry(
					assetListEntry);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _updateTypeSettings(Group group) {
		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchDynamicAssetListEntrySegmentsEntryRels(
					group.getCompanyId());

		for (AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel :
				assetListEntrySegmentsEntryRels) {

			String groupId = String.valueOf(group.getGroupId());

			UnicodeProperties unicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).fastLoad(
					assetListEntrySegmentsEntryRel.getTypeSettings()
				).build();

			String[] groupIds = StringUtil.split(
				unicodeProperties.getProperty("groupIds", StringPool.BLANK));

			if (ArrayUtil.contains(groupIds, groupId)) {
				unicodeProperties.setProperty(
					"groupIds",
					StringUtil.merge(ArrayUtil.remove(groupIds, groupId)));

				_assetListEntrySegmentsEntryRelLocalService.
					updateAssetListEntrySegmentsEntryRelTypeSettings(
						assetListEntrySegmentsEntryRel.getAssetListEntryId(),
						assetListEntrySegmentsEntryRel.getSegmentsEntryId(),
						unicodeProperties.toString());
			}
		}
	}

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.item.selector.web.internal.layout.list.retriever;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.layout.list.retriever.ClassedModelListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ListObjectReferenceFactory.class)
public class AssetEntryListListObjectReferenceFactory
	implements ListObjectReferenceFactory<InfoListItemSelectorReturnType> {

	@Override
	public ListObjectReference getListObjectReference(JSONObject jsonObject) {
		String classPK = jsonObject.getString("classPK");

		return new ClassedModelListObjectReference(
			JSONUtil.put(
				"className", jsonObject.getLong("className")
			).put(
				"classPK", classPK
			).put(
				"itemType",
				() -> {
					AssetListEntry assetListEntry =
						_assetListEntryLocalService.fetchAssetListEntry(
							GetterUtil.getLong(classPK));

					if (assetListEntry == null) {
						return jsonObject.getString("itemType");
					}

					return assetListEntry.getAssetEntryType();
				}
			).put(
				"title", jsonObject.getString("title")
			));
	}

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

}
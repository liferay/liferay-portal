/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.layout.list.retriever;

import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.layout.list.retriever.KeyListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ListObjectReferenceFactory.class)
public class InfoCollectionProviderListObjectReferenceFactory
	implements ListObjectReferenceFactory
		<InfoListProviderItemSelectorReturnType> {

	@Override
	public ListObjectReference getListObjectReference(JSONObject jsonObject) {
		String key = jsonObject.getString("key");

		return new KeyListObjectReference(
			JSONUtil.put(
				"itemType",
				() -> {
					InfoCollectionProvider<?> infoCollectionProvider =
						_infoItemServiceRegistry.getInfoItemService(
							InfoCollectionProvider.class, key);

					if (infoCollectionProvider == null) {
						infoCollectionProvider =
							_infoItemServiceRegistry.getInfoItemService(
								RelatedInfoItemCollectionProvider.class, key);
					}

					if (infoCollectionProvider == null) {
						return jsonObject.getString("itemType");
					}

					return infoCollectionProvider.getCollectionItemClassName();
				}
			).put(
				"key", key
			).put(
				"title", jsonObject.getString("title")
			));
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}
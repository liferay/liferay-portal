/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.headless.admin.site.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.dto.v1_0.EmptyCollectionConfig;
import com.liferay.headless.admin.site.dto.v1_0.PageCollectionDefinition;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.collection.EmptyCollectionOptions;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "dto.class.name=com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem",
	service = DTOConverter.class
)
public class PageCollectionDefinitionDTOConverter
	implements DTOConverter
		<CollectionStyledLayoutStructureItem, PageCollectionDefinition> {

	@Override
	public String getContentType() {
		return PageCollectionDefinition.class.getSimpleName();
	}

	@Override
	public PageCollectionDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem)
		throws Exception {

		return new PageCollectionDefinition() {
			{
				setCollectionReference(
					() -> _toCollectionReference(
						collectionStyledLayoutStructureItem));
				setDisplayAllItems(
					collectionStyledLayoutStructureItem::isDisplayAllItems);
				setDisplayAllPages(
					collectionStyledLayoutStructureItem::isDisplayAllPages);
				setEmptyCollectionConfig(
					() -> _toEmptyCollectionOption(
						collectionStyledLayoutStructureItem));
				setListItemStyle(
					collectionStyledLayoutStructureItem::getListItemStyle);
				setListStyle(collectionStyledLayoutStructureItem::getListStyle);
				setName(collectionStyledLayoutStructureItem::getName);
				setNumberOfColumns(
					collectionStyledLayoutStructureItem::getNumberOfColumns);
				setNumberOfItems(
					collectionStyledLayoutStructureItem::getNumberOfItems);
				setNumberOfItemsPerPage(
					collectionStyledLayoutStructureItem::
						getNumberOfItemsPerPage);
				setNumberOfPages(
					collectionStyledLayoutStructureItem::getNumberOfPages);
				setPaginationType(
					() -> _internalToExternalValuesMap.get(
						collectionStyledLayoutStructureItem.
							getPaginationType()));
				setTemplateKey(
					collectionStyledLayoutStructureItem::getTemplateKey);
			}
		};
	}

	private CollectionReference _toCollectionReference(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		JSONObject jsonObject =
			collectionStyledLayoutStructureItem.getCollectionJSONObject();

		if (jsonObject == null) {
			return null;
		}

		String type = jsonObject.getString("type");

		if (Validator.isNull(type)) {
			return null;
		}

		if (Objects.equals(
				type, InfoListItemSelectorReturnType.class.getName())) {

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.fetchAssetListEntry(
					jsonObject.getLong("classPK"));

			if (assetListEntry == null) {
				return null;
			}

			return new CollectionItemExternalReference() {
				{
					setCollectionType(CollectionType.COLLECTION);
					setExternalReferenceCode(
						assetListEntry::getExternalReferenceCode);
				}
			};
		}
		else if (Objects.equals(
					type,
					InfoListProviderItemSelectorReturnType.class.getName())) {

			return new ClassNameReference() {
				{
					setClassName(() -> jsonObject.getString("key"));
					setCollectionType(CollectionType.COLLECTION_PROVIDER);
				}
			};
		}

		return null;
	}

	private EmptyCollectionConfig _toEmptyCollectionOption(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		EmptyCollectionOptions emptyCollectionOptions =
			collectionStyledLayoutStructureItem.getEmptyCollectionOptions();

		if (emptyCollectionOptions == null) {
			return null;
		}

		return new EmptyCollectionConfig() {
			{
				setDisplayMessage(emptyCollectionOptions::isDisplayMessage);
				setMessage_i18n(emptyCollectionOptions::getMessage);
			}
		};
	}

	private static final Map<String, PageCollectionDefinition.PaginationType>
		_internalToExternalValuesMap = HashMapBuilder.put(
			CollectionPaginationUtil.PAGINATION_TYPE_NONE,
			PageCollectionDefinition.PaginationType.NONE
		).put(
			CollectionPaginationUtil.PAGINATION_TYPE_NUMERIC,
			PageCollectionDefinition.PaginationType.NUMERIC
		).put(
			CollectionPaginationUtil.PAGINATION_TYPE_REGULAR,
			PageCollectionDefinition.PaginationType.REGULAR
		).put(
			CollectionPaginationUtil.PAGINATION_TYPE_SIMPLE,
			PageCollectionDefinition.PaginationType.SIMPLE
		).build();

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

}
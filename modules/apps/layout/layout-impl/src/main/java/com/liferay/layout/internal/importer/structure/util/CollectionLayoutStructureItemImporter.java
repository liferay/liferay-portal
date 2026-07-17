/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.headless.delivery.dto.v1_0.CollectionConfig;
import com.liferay.headless.delivery.dto.v1_0.PageCollectionDefinition;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.layout.converter.AlignConverter;
import com.liferay.layout.converter.FlexWrapConverter;
import com.liferay.layout.converter.JustifyConverter;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.collection.EmptyCollectionOptions;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Jürgen Kappler
 */
public class CollectionLayoutStructureItemImporter
	extends BaseLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	public CollectionLayoutStructureItemImporter(
		AssetListEntryLocalService assetListEntryLocalService) {

		_assetListEntryLocalService = assetListEntryLocalService;
	}

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement, Set<String> warningMessages)
		throws Exception {

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				(CollectionStyledLayoutStructureItem)
					layoutStructure.addCollectionStyledLayoutStructureItem(
						_getCollectionItemId(
							layoutStructureItemImporterContext, pageElement),
						layoutStructureItemImporterContext.getItemId(
							pageElement),
						layoutStructureItemImporterContext.getParentItemId(),
						layoutStructureItemImporterContext.getPosition());

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return collectionStyledLayoutStructureItem;
		}

		Map<String, Object> collectionConfig =
			(Map<String, Object>)definitionMap.get("collectionConfig");

		if (collectionConfig != null) {
			collectionStyledLayoutStructureItem.setCollectionJSONObject(
				_getCollectionConfigAsJSONObject(
					collectionConfig, layoutStructureItemImporterContext));
		}

		if (definitionMap.get("collectionViewports") instanceof
				List<?> collectionViewports) {

			for (Map<String, Object> collectionViewport :
					(List<Map<String, Object>>)collectionViewports) {

				_processCollectionViewportDefinition(
					collectionStyledLayoutStructureItem,
					(Map<String, Object>)collectionViewport.get(
						"collectionViewportDefinition"),
					(String)collectionViewport.get("id"));
			}
		}

		collectionStyledLayoutStructureItem.setDisplayAllItems(
			(Boolean)definitionMap.get("displayAllItems"));

		Boolean displayAllPages = (Boolean)definitionMap.get("displayAllPages");

		Map<String, Object> emptyCollectionConfig =
			(Map<String, Object>)definitionMap.get("emptyCollectionConfig");

		collectionStyledLayoutStructureItem.setEmptyCollectionOptions(
			_getEmptyCollectionOptions(emptyCollectionConfig));

		Boolean showAllItems = (Boolean)definitionMap.get("showAllItems");

		if (displayAllPages == null) {
			displayAllPages = showAllItems;
		}

		collectionStyledLayoutStructureItem.setDisplayAllPages(displayAllPages);

		collectionStyledLayoutStructureItem.setListItemStyle(
			(String)definitionMap.get("listItemStyle"));
		collectionStyledLayoutStructureItem.setListStyle(
			(String)definitionMap.get("listStyle"));
		collectionStyledLayoutStructureItem.setNumberOfColumns(
			(Integer)definitionMap.get("numberOfColumns"));

		Integer numberOfItems = (Integer)definitionMap.get("numberOfItems");

		collectionStyledLayoutStructureItem.setNumberOfItems(numberOfItems);

		Integer numberOfItemsPerPage = (Integer)definitionMap.get(
			"numberOfItemsPerPage");

		if (numberOfItemsPerPage != null) {
			collectionStyledLayoutStructureItem.setNumberOfItemsPerPage(
				numberOfItemsPerPage);
		}

		Integer numberOfPages = (Integer)definitionMap.get("numberOfPages");

		if (numberOfPages == null) {
			if ((numberOfItemsPerPage != null) && (numberOfItemsPerPage > 0)) {
				collectionStyledLayoutStructureItem.setNumberOfPages(
					(int)Math.ceil(
						numberOfItems / (double)numberOfItemsPerPage));
			}
		}
		else {
			collectionStyledLayoutStructureItem.setNumberOfPages(numberOfPages);
		}

		collectionStyledLayoutStructureItem.setPaginationType(
			_toPaginationType((String)definitionMap.get("paginationType")));
		collectionStyledLayoutStructureItem.setShowAllItems(showAllItems);
		collectionStyledLayoutStructureItem.setTemplateKey(
			(String)definitionMap.get("templateKey"));

		if (definitionMap.get("fragmentViewports") instanceof
				List<?> fragmentViewports) {

			for (Map<String, Object> fragmentViewport :
					(List<Map<String, Object>>)fragmentViewports) {

				JSONObject jsonObject = JSONUtil.put(
					(String)fragmentViewport.get("id"),
					toFragmentViewportStylesJSONObject(fragmentViewport));

				collectionStyledLayoutStructureItem.updateItemConfig(
					jsonObject);
			}
		}

		Map<String, Object> formLayout = (Map<String, Object>)definitionMap.get(
			"layout");

		if (formLayout != null) {
			String align = String.valueOf(
				formLayout.getOrDefault("align", StringPool.BLANK));

			if (Validator.isNotNull(align)) {
				collectionStyledLayoutStructureItem.setAlign(
					AlignConverter.convertToInternalValue(align));
			}

			String flexWrap = String.valueOf(
				formLayout.getOrDefault("flexWrap", StringPool.BLANK));

			if (Validator.isNotNull(flexWrap)) {
				collectionStyledLayoutStructureItem.setFlexWrap(
					FlexWrapConverter.convertToInternalValue(flexWrap));
			}

			String justify = String.valueOf(
				formLayout.getOrDefault("justify", StringPool.BLANK));

			if (Validator.isNotNull(justify)) {
				collectionStyledLayoutStructureItem.setJustify(
					JustifyConverter.convertToInternalValue(justify));
			}
		}

		String name = GetterUtil.getString(definitionMap.get("name"), null);

		if (name != null) {
			collectionStyledLayoutStructureItem.setName(name);
		}

		return collectionStyledLayoutStructureItem;
	}

	@Override
	public PageElement.Type getPageElementType() {
		return PageElement.Type.COLLECTION;
	}

	private JSONObject _getCollectionConfigAsJSONObject(
		Map<String, Object> collectionConfig,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		String type = (String)collectionConfig.get("collectionType");

		if (Validator.isNull(type)) {
			return null;
		}

		Map<String, Object> collectionReference =
			(Map<String, Object>)collectionConfig.get("collectionReference");

		if (MapUtil.isEmpty(collectionConfig)) {
			return null;
		}

		if (Objects.equals(
				type, CollectionConfig.CollectionType.COLLECTION.getValue())) {

			return _getCollectionJSONObject(collectionReference);
		}
		else if (Objects.equals(
					type,
					CollectionConfig.CollectionType.COLLECTION_PROVIDER.
						getValue())) {

			return _getCollectionProviderJSONObject(
				collectionReference, layoutStructureItemImporterContext);
		}

		return null;
	}

	private String _getCollectionItemId(
		LayoutStructureItemImporterContext layoutStructureItemImporterContext,
		PageElement pageElement) {

		PageElement[] pageElements = pageElement.getPageElements();

		if (ArrayUtil.isEmpty(pageElements)) {
			return PortalUUIDUtil.generate();
		}

		return layoutStructureItemImporterContext.getItemId(pageElements[0]);
	}

	private JSONObject _getCollectionJSONObject(
		Map<String, Object> collectionReference) {

		Long classPK = _toClassPK(
			String.valueOf(collectionReference.get("classPK")));

		if (classPK == null) {
			return null;
		}

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.fetchAssetListEntry(classPK);

		if (assetListEntry == null) {
			return null;
		}

		return JSONUtil.put(
			"classNameId",
			PortalUtil.getClassNameId(AssetListEntry.class.getName())
		).put(
			"classPK", String.valueOf(classPK)
		).put(
			"itemSubtype", assetListEntry.getAssetEntrySubtype()
		).put(
			"itemType", assetListEntry.getAssetEntryType()
		).put(
			"title", assetListEntry.getTitle()
		).put(
			"type", InfoListItemSelectorReturnType.class.getName()
		);
	}

	private JSONObject _getCollectionProviderJSONObject(
		Map<String, Object> collectionReference,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		InfoItemServiceRegistry infoItemServiceRegistry =
			layoutStructureItemImporterContext.getInfoItemServiceRegistry();

		String className = (String)collectionReference.get("className");

		InfoCollectionProvider<?> infoCollectionProvider =
			infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class, className);

		if (infoCollectionProvider == null) {
			infoCollectionProvider = infoItemServiceRegistry.getInfoItemService(
				RelatedInfoItemCollectionProvider.class, className);
		}

		if (infoCollectionProvider == null) {
			return null;
		}

		return JSONUtil.put(
			"itemSubtype", _getItemSubtype(infoCollectionProvider)
		).put(
			"itemType", infoCollectionProvider.getCollectionItemClassName()
		).put(
			"key", className
		).put(
			"title", infoCollectionProvider.getLabel(LocaleUtil.getDefault())
		).put(
			"type", InfoListProviderItemSelectorReturnType.class.getName()
		);
	}

	private EmptyCollectionOptions _getEmptyCollectionOptions(
		Map<String, Object> emptyCollectionConfig) {

		if ((emptyCollectionConfig == null) ||
			emptyCollectionConfig.isEmpty()) {

			return null;
		}

		return new EmptyCollectionOptions() {
			{
				setDisplayMessage(
					() -> {
						Map.Entry<String, Object> displayMessageEntry =
							MapUtil.getEntry(
								emptyCollectionConfig, "displayMessage");

						if (displayMessageEntry == null) {
							return null;
						}

						return GetterUtil.getBoolean(
							displayMessageEntry.getValue(), true);
					});
				setMessage(
					() -> (Map<String, String>)emptyCollectionConfig.get(
						"message_i18n"));
			}
		};
	}

	private String _getItemSubtype(
		InfoCollectionProvider<?> infoCollectionProvider) {

		if (!(infoCollectionProvider instanceof
				SingleFormVariationInfoCollectionProvider)) {

			return null;
		}

		SingleFormVariationInfoCollectionProvider<?>
			singleFormVariationInfoCollectionProvider =
				(SingleFormVariationInfoCollectionProvider<?>)
					infoCollectionProvider;

		return singleFormVariationInfoCollectionProvider.getFormVariationKey();
	}

	private void _processCollectionViewportDefinition(
		CollectionStyledLayoutStructureItem collectionStyledLayoutStructureItem,
		Map<String, Object> collectionViewportDefinitionMap,
		String collectionViewportId) {

		collectionStyledLayoutStructureItem.setViewportConfiguration(
			collectionViewportId,
			JSONUtil.put(
				"numberOfColumns",
				() -> {
					Map.Entry<String, Object> numberOfColumnsEntry =
						MapUtil.getEntry(
							collectionViewportDefinitionMap, "numberOfColumns");

					if (numberOfColumnsEntry == null) {
						return null;
					}

					return GetterUtil.getInteger(
						numberOfColumnsEntry.getValue());
				}));
	}

	private Long _toClassPK(String classPKString) {
		if (Validator.isNull(classPKString)) {
			return null;
		}

		Long classPK = null;

		try {
			classPK = Long.parseLong(classPKString);
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					String.format(
						"Unable to parse class PK %s to a long", classPKString),
					numberFormatException);
			}

			return null;
		}

		return classPK;
	}

	private String _toPaginationType(String paginationType) {
		if (Validator.isNull(paginationType) ||
			Objects.equals(
				paginationType,
				PageCollectionDefinition.PaginationType.NONE.getValue())) {

			return "none";
		}

		if (Objects.equals(
				paginationType,
				PageCollectionDefinition.PaginationType.NUMERIC.getValue()) ||
			Objects.equals(
				paginationType,
				PageCollectionDefinition.PaginationType.REGULAR.getValue())) {

			return "numeric";
		}

		if (Objects.equals(
				paginationType,
				PageCollectionDefinition.PaginationType.SIMPLE.getValue())) {

			return "simple";
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CollectionLayoutStructureItemImporter.class);

	private final AssetListEntryLocalService _assetListEntryLocalService;

}
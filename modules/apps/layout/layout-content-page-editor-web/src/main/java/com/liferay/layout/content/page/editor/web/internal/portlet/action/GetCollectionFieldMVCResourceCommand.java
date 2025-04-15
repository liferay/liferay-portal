/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.info.collection.provider.RepeatableFieldInfoItemCollectionProvider;
import com.liferay.info.collection.provider.item.selector.InfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.collection.provider.item.selector.RelatedInfoItemCollectionProviderItemSelectorCriterion;
import com.liferay.info.collection.provider.item.selector.RepeatableFieldInfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.RepeatableInfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.filter.InfoItemServiceFilter;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.list.renderer.DefaultInfoListRendererContext;
import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.list.renderer.InfoListRendererRegistry;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.LayoutObjectReferenceUtil;
import com.liferay.layout.list.permission.provider.LayoutListPermissionProvider;
import com.liferay.layout.list.permission.provider.LayoutListPermissionProviderRegistry;
import com.liferay.layout.list.retriever.ClassedModelListObjectReference;
import com.liferay.layout.list.retriever.DefaultLayoutListRetrieverContext;
import com.liferay.layout.list.retriever.KeyListObjectReference;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.layout.list.retriever.ListObjectReferenceFactoryRegistry;
import com.liferay.layout.list.retriever.SegmentsEntryLayoutListRetriever;
import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_collection_field"
	},
	service = MVCResourceCommand.class
)
public class GetCollectionFieldMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String languageId = ParamUtil.getString(
			resourceRequest, "languageId", themeDisplay.getLanguageId());

		int activePage = ParamUtil.getInteger(resourceRequest, "activePage");
		boolean displayAllItems = ParamUtil.getBoolean(
			resourceRequest, "displayAllItems");
		boolean displayAllPages = ParamUtil.getBoolean(
			resourceRequest, "displayAllPages");
		String layoutObjectReference = ParamUtil.getString(
			resourceRequest, "layoutObjectReference");
		String listStyle = ParamUtil.getString(resourceRequest, "listStyle");
		String listItemStyle = ParamUtil.getString(
			resourceRequest, "listItemStyle");
		int numberOfItems = ParamUtil.getInteger(
			resourceRequest, "numberOfItems");
		int numberOfItemsPerPage = ParamUtil.getInteger(
			resourceRequest, "numberOfItemsPerPage");
		int numberOfPages = ParamUtil.getInteger(
			resourceRequest, "numberOfPages");
		String paginationType = ParamUtil.getString(
			resourceRequest, "paginationType");
		long segmentsExperienceId = ParamUtil.getLong(
			resourceRequest, "segmentsExperienceId");
		String templateKey = ParamUtil.getString(
			resourceRequest, "templateKey");

		try {
			jsonObject = _getCollectionFieldsJSONObject(
				_portal.getHttpServletRequest(resourceRequest),
				_portal.getHttpServletResponse(resourceResponse), activePage,
				displayAllItems, displayAllPages, languageId,
				layoutObjectReference, listStyle, listItemStyle,
				resourceResponse.getNamespace(), numberOfItems,
				numberOfItemsPerPage, numberOfPages, paginationType,
				segmentsExperienceId, templateKey);
		}
		catch (Exception exception) {
			_log.error("Unable to get collection field", exception);

			jsonObject.put(
				"error",
				_language.get(
					themeDisplay.getRequest(), "an-unexpected-error-occurred"));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonObject);
	}

	private long[] _filterSegmentsEntryIds(
		LayoutListRetriever<?, ListObjectReference> layoutListRetriever,
		ListObjectReference listObjectReference, long segmentsExperienceId) {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if (!(layoutListRetriever instanceof
				SegmentsEntryLayoutListRetriever)) {

			return new long[] {segmentsExperience.getSegmentsEntryId()};
		}

		SegmentsEntryLayoutListRetriever<ListObjectReference>
			segmentsEntryLayoutListRetriever =
				(SegmentsEntryLayoutListRetriever<ListObjectReference>)
					layoutListRetriever;

		if (segmentsEntryLayoutListRetriever.hasSegmentsEntryVariation(
				listObjectReference, segmentsExperience.getSegmentsEntryId())) {

			return new long[] {segmentsExperience.getSegmentsEntryId()};
		}

		return new long[] {
			segmentsEntryLayoutListRetriever.getDefaultVariationSegmentsEntryId(
				listObjectReference)
		};
	}

	private AssetListEntry _getAssetListEntry(
		ListObjectReference listObjectReference) {

		// LPS-133832

		if (!(listObjectReference instanceof ClassedModelListObjectReference)) {
			return null;
		}

		ClassedModelListObjectReference classedModelListObjectReference =
			(ClassedModelListObjectReference)listObjectReference;

		return _assetListEntryLocalService.fetchAssetListEntry(
			classedModelListObjectReference.getClassPK());
	}

	private JSONObject _getCollectionFieldsJSONObject(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, int activePage,
			boolean displayAllItems, boolean displayAllPages, String languageId,
			String layoutObjectReference, String listStyle,
			String listItemStyle, String namespace, int numberOfItems,
			int numberOfItemsPerPage, int numberOfPages, String paginationType,
			long segmentsExperienceId, String templateKey)
		throws PortalException {

		JSONObject layoutObjectReferenceJSONObject =
			_jsonFactory.createJSONObject(layoutObjectReference);

		String type = layoutObjectReferenceJSONObject.getString("type");

		LayoutListRetriever<?, ListObjectReference> layoutListRetriever =
			(LayoutListRetriever<?, ListObjectReference>)
				_layoutListRetrieverRegistry.getLayoutListRetriever(type);

		if (layoutListRetriever == null) {
			return _jsonFactory.createJSONObject();
		}

		ListObjectReferenceFactory<?> listObjectReferenceFactory =
			_listObjectReferenceFactoryRegistry.getListObjectReference(type);

		if (listObjectReferenceFactory == null) {
			return _jsonFactory.createJSONObject();
		}

		ListObjectReference listObjectReference =
			listObjectReferenceFactory.getListObjectReference(
				layoutObjectReferenceJSONObject);

		String originalItemType = null;

		AssetListEntry assetListEntry = _getAssetListEntry(listObjectReference);

		if (assetListEntry != null) {
			originalItemType = assetListEntry.getAssetEntryType();
		}
		else {
			originalItemType = listObjectReference.getItemType();
		}

		if (!_hasViewPermission(httpServletRequest, listObjectReference)) {
			return JSONUtil.put(
				"customCollectionSelectorURL", StringPool.BLANK
			).put(
				"isRestricted", true
			).put(
				"items", _jsonFactory.createJSONArray()
			).put(
				"itemSubtype",
				() -> {
					if (assetListEntry == null) {
						return null;
					}

					return assetListEntry.getAssetEntrySubtype();
				}
			).put(
				"itemType", originalItemType
			).put(
				"length", 0
			).put(
				"totalNumberOfItems", 0
			);
		}

		String itemType = _infoSearchClassMapperRegistry.getClassName(
			originalItemType);

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_getInfoFieldValuesProvider(itemType, listObjectReference);

		if (infoItemFieldValuesProvider == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get info item form provider for class " +
						itemType);
			}

			return _jsonFactory.createJSONObject();
		}

		DefaultLayoutListRetrieverContext defaultLayoutListRetrieverContext =
			new DefaultLayoutListRetrieverContext();

		defaultLayoutListRetrieverContext.setConfiguration(
			LayoutObjectReferenceUtil.getConfiguration(
				layoutObjectReferenceJSONObject));
		defaultLayoutListRetrieverContext.setContextObject(
			_getInfoItem(httpServletRequest));
		defaultLayoutListRetrieverContext.setPagination(
			CollectionPaginationUtil.getPagination(
				activePage, displayAllItems, numberOfItems,
				numberOfItemsPerPage, paginationType));
		defaultLayoutListRetrieverContext.setSegmentsEntryIds(
			_filterSegmentsEntryIds(
				layoutListRetriever, listObjectReference,
				segmentsExperienceId));

		InfoPage<?> infoPage = layoutListRetriever.getInfoPage(
			listObjectReference, defaultLayoutListRetrieverContext);

		return JSONUtil.put(
			"content",
			() -> {
				InfoListRenderer<Object> infoListRenderer =
					(InfoListRenderer<Object>)
						_infoListRendererRegistry.getInfoListRenderer(
							listStyle);

				if (infoListRenderer == null) {
					return null;
				}

				UnsyncStringWriter unsyncStringWriter =
					new UnsyncStringWriter();

				HttpServletResponse pipingHttpServletResponse =
					new PipingServletResponse(
						httpServletResponse, unsyncStringWriter);

				DefaultInfoListRendererContext defaultInfoListRendererContext =
					new DefaultInfoListRendererContext(
						httpServletRequest, pipingHttpServletResponse);

				defaultInfoListRendererContext.setListItemRendererKey(
					listItemStyle);
				defaultInfoListRendererContext.setTemplateKey(templateKey);

				infoListRenderer.render(
					(List<Object>)infoPage.getPageItems(),
					defaultInfoListRendererContext);

				return unsyncStringWriter.toString();
			}
		).put(
			"customCollectionSelectorURL",
			_getCustomCollectionSelectorURL(
				httpServletRequest, itemType, layoutObjectReferenceJSONObject,
				namespace)
		).put(
			"isRestricted", false
		).put(
			"items",
			() -> {
				JSONArray jsonArray = _jsonFactory.createJSONArray();

				for (Object object : infoPage.getPageItems()) {
					jsonArray.put(
						_getDisplayObjectJSONObject(
							httpServletRequest, httpServletResponse,
							infoItemFieldValuesProvider, object,
							LocaleUtil.fromLanguageId(languageId)));
				}

				return jsonArray;
			}
		).put(
			"itemSubtype",
			() -> {
				if (assetListEntry == null) {
					return null;
				}

				return assetListEntry.getAssetEntrySubtype();
			}
		).put(
			"itemType", originalItemType
		).put(
			"length", infoPage.getTotalCount()
		).put(
			"totalNumberOfItems",
			CollectionPaginationUtil.getTotalNumberOfItems(
				infoPage.getTotalCount(), displayAllPages, displayAllItems,
				numberOfItems, numberOfItemsPerPage, numberOfPages,
				paginationType)
		);
	}

	private String _getCustomCollectionSelectorURL(
		HttpServletRequest httpServletRequest, String itemType,
		JSONObject layoutObjectReferenceJSONObject, String namespace) {

		List<ItemSelectorCriterion> itemSelectorCriterions = new ArrayList<>();

		InfoCollectionProviderItemSelectorCriterion
			infoCollectionProviderItemSelectorCriterion =
				new InfoCollectionProviderItemSelectorCriterion();

		infoCollectionProviderItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				new InfoListItemSelectorReturnType(),
				new InfoListProviderItemSelectorReturnType());
		infoCollectionProviderItemSelectorCriterion.setType(
			InfoCollectionProviderItemSelectorCriterion.Type.
				SUPPORTED_INFO_FRAMEWORK_COLLECTIONS);

		itemSelectorCriterions.add(infoCollectionProviderItemSelectorCriterion);

		if (!Objects.equals(
				layoutObjectReferenceJSONObject.getString("key"),
				RepeatableFieldInfoItemCollectionProvider.class.getName())) {

			RelatedInfoItemCollectionProviderItemSelectorCriterion
				relatedInfoItemCollectionProviderItemSelectorCriterion =
					new RelatedInfoItemCollectionProviderItemSelectorCriterion();

			relatedInfoItemCollectionProviderItemSelectorCriterion.
				setDesiredItemSelectorReturnTypes(
					new InfoListProviderItemSelectorReturnType());

			List<String> sourceItemTypes = new ArrayList<>();

			sourceItemTypes.add(itemType);

			String className =
				_infoSearchClassMapperRegistry.getSearchClassName(itemType);

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className);

			if (assetRendererFactory != null) {
				sourceItemTypes.add(AssetEntry.class.getName());
			}

			relatedInfoItemCollectionProviderItemSelectorCriterion.
				setSourceItemTypes(sourceItemTypes);

			itemSelectorCriterions.add(
				relatedInfoItemCollectionProviderItemSelectorCriterion);

			RepeatableFieldInfoCollectionProviderItemSelectorCriterion
				repeatableFieldInfoCollectionProviderItemSelectorCriterion =
					new RepeatableFieldInfoCollectionProviderItemSelectorCriterion();

			repeatableFieldInfoCollectionProviderItemSelectorCriterion.
				setDesiredItemSelectorReturnTypes(
					new InfoListProviderItemSelectorReturnType());
			repeatableFieldInfoCollectionProviderItemSelectorCriterion.
				setItemType(itemType);
			repeatableFieldInfoCollectionProviderItemSelectorCriterion.
				setItemSubtype(
					layoutObjectReferenceJSONObject.getString("itemSubtype"));

			itemSelectorCriterions.add(
				repeatableFieldInfoCollectionProviderItemSelectorCriterion);
		}

		PortletURL infoListSelectorURL = _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
			namespace + "selectInfoList",
			itemSelectorCriterions.toArray(new ItemSelectorCriterion[0]));

		if (infoListSelectorURL == null) {
			return StringPool.BLANK;
		}

		return infoListSelectorURL.toString();
	}

	private JSONObject _getDisplayObjectJSONObject(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider,
		Object object, Locale locale) {

		InfoItemFieldValues infoItemFieldValues =
			infoItemFieldValuesProvider.getInfoItemFieldValues(object);

		InfoItemReference infoItemReference =
			infoItemFieldValues.getInfoItemReference();

		if (infoItemReference == null) {
			return _jsonFactory.createJSONObject();
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		JSONObject displayObjectJSONObject = JSONUtil.put(
			"className", infoItemReference.getClassName()
		).put(
			"classNameId",
			_portal.getClassNameId(infoItemReference.getClassName())
		).put(
			"classPK",
			() -> {
				if (!(infoItemIdentifier instanceof
						ClassPKInfoItemIdentifier)) {

					return null;
				}

				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)infoItemIdentifier;

				return classPKInfoItemIdentifier.getClassPK();
			}
		).put(
			"externalReferenceCode",
			() -> {
				if (!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {
					return null;
				}

				ERCInfoItemIdentifier ercInfoItemIdentifier =
					(ERCInfoItemIdentifier)infoItemIdentifier;

				return ercInfoItemIdentifier.getExternalReferenceCode();
			}
		);

		FragmentEntryProcessorContext fragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				httpServletRequest, httpServletResponse,
				FragmentEntryLinkConstants.EDIT, locale);

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField<?> infoField = infoFieldValue.getInfoField();

			displayObjectJSONObject.put("fieldId", infoField.getUniqueId());

			Object value =
				_fragmentEntryProcessorHelper.getMappedInfoItemFieldValue(
					displayObjectJSONObject, infoField.getUniqueId(),
					fragmentEntryProcessorContext, infoItemFieldValues);

			displayObjectJSONObject.put(
				infoField.getName(), value
			).put(
				infoField.getUniqueId(), value
			);
		}

		return displayObjectJSONObject;
	}

	private InfoItemFieldValuesProvider<Object> _getInfoFieldValuesProvider(
		String itemType, ListObjectReference listObjectReference) {

		if (listObjectReference instanceof KeyListObjectReference) {
			KeyListObjectReference keyListObjectReference =
				(KeyListObjectReference)listObjectReference;

			if (Objects.equals(
					keyListObjectReference.getKey(),
					RepeatableFieldInfoItemCollectionProvider.class.
						getName())) {

				itemType = RepeatableInfoFieldValue.class.getName();
			}
		}

		return (InfoItemFieldValuesProvider<Object>)
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, itemType);
	}

	private Object _getInfoItem(HttpServletRequest httpServletRequest) {
		String className = _portal.fetchClassName(
			ParamUtil.getLong(httpServletRequest, "classNameId"));

		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");
		String externalReferenceCode = ParamUtil.getString(
			httpServletRequest, "externalReferenceCode");

		if (Validator.isNull(className) ||
			((classPK <= 0) && Validator.isNull(externalReferenceCode))) {

			return null;
		}

		InfoItemServiceFilter infoItemServiceFilter =
			ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER;

		if (Validator.isNotNull(externalReferenceCode)) {
			infoItemServiceFilter =
				ERCInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER;
		}

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			(InfoItemObjectProvider<Object>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, className,
					infoItemServiceFilter);

		if (infoItemObjectProvider == null) {
			return null;
		}

		try {
			InfoItemIdentifier infoItemIdentifier =
				new ClassPKInfoItemIdentifier(classPK);

			if (Validator.isNotNull(externalReferenceCode)) {
				infoItemIdentifier = new ERCInfoItemIdentifier(
					externalReferenceCode);
			}

			return infoItemObjectProvider.getInfoItem(infoItemIdentifier);
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchInfoItemException);
			}
		}

		return null;
	}

	private boolean _hasViewPermission(
		HttpServletRequest httpServletRequest,
		ListObjectReference listObjectReference) {

		Class<? extends ListObjectReference> listObjectReferenceClass =
			listObjectReference.getClass();

		LayoutListPermissionProvider<ListObjectReference>
			layoutListPermissionProvider =
				(LayoutListPermissionProvider<ListObjectReference>)
					_layoutListPermissionProviderRegistry.
						getLayoutListPermissionProvider(
							listObjectReferenceClass.getName());

		if (layoutListPermissionProvider == null) {
			return true;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return layoutListPermissionProvider.hasPermission(
			themeDisplay.getPermissionChecker(), listObjectReference,
			ActionKeys.VIEW);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetCollectionFieldMVCResourceCommand.class);

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoListRendererRegistry _infoListRendererRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutListPermissionProviderRegistry
		_layoutListPermissionProviderRegistry;

	@Reference
	private LayoutListRetrieverRegistry _layoutListRetrieverRegistry;

	@Reference
	private ListObjectReferenceFactoryRegistry
		_listObjectReferenceFactoryRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}
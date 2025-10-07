/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.info.collection.provider.item.selector.RelatedInfoItemCollectionProviderItemSelectorCriterion;
import com.liferay.info.collection.provider.item.selector.RepeatableFieldInfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.content.page.editor.sidebar.panel.ContentPageEditorSidebarPanel;
import com.liferay.layout.content.page.editor.web.internal.configuration.PageEditorConfiguration;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentCollectionManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.MappingContentUtil;
import com.liferay.layout.manager.ContentManager;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.service.SegmentsEntryService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Jürgen Kappler
 */
public class ContentPageEditorLayoutPageTemplateDisplayContext
	extends ContentPageEditorDisplayContext {

	public ContentPageEditorLayoutPageTemplateDisplayContext(
		List<ContentPageEditorSidebarPanel> contentPageEditorSidebarPanels,
		ContentManager contentManager,
		FragmentCollectionManager fragmentCollectionManager,
		FragmentEntryLinkManager fragmentEntryLinkManager,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		HttpServletRequest httpServletRequest,
		InfoItemServiceRegistry infoItemServiceRegistry,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		ItemSelector itemSelector, JSONFactory jsonFactory, Language language,
		LayoutLocalService layoutLocalService,
		LayoutLockManager layoutLockManager,
		LayoutSetLocalService layoutSetLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateEntryService layoutPageTemplateEntryService,
		LayoutPermission layoutPermission,
		PageEditorConfiguration pageEditorConfiguration,
		boolean pageIsDisplayPage, Portal portal, PortletRequest portletRequest,
		PortletResourcePermission portletResourcePermission,
		PortletURLFactory portletURLFactory, RenderResponse renderResponse,
		SegmentsConfigurationProvider segmentsConfigurationProvider,
		SegmentsExperienceManager segmentsExperienceManager,
		SegmentsExperienceLocalService segmentsExperienceLocalService,
		SegmentsExperimentRelLocalService segmentsExperimentRelLocalService,
		SegmentsEntryService segmentsEntryService, Staging staging,
		StagingGroupHelper stagingGroupHelper,
		StyleBookEntryLocalService styleBookEntryLocalService,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		super(
			contentPageEditorSidebarPanels, contentManager,
			fragmentCollectionManager, fragmentEntryLinkManager,
			fragmentEntryLinkLocalService, frontendTokenDefinitionRegistry,
			httpServletRequest, infoItemServiceRegistry,
			infoSearchClassMapperRegistry, itemSelector, jsonFactory, language,
			layoutLocalService, layoutLockManager,
			layoutPageTemplateEntryLocalService, layoutPageTemplateEntryService,
			layoutPermission, layoutSetLocalService, pageEditorConfiguration,
			portal, portletRequest, portletResourcePermission,
			portletURLFactory, renderResponse, segmentsConfigurationProvider,
			segmentsExperienceManager, segmentsExperienceLocalService,
			segmentsExperimentRelLocalService, segmentsEntryService, staging,
			stagingGroupHelper, styleBookEntryLocalService,
			workflowDefinitionLinkLocalService);

		_itemSelector = itemSelector;
		_pageIsDisplayPage = pageIsDisplayPage;
	}

	@Override
	public Map<String, Object> getEditorContext() throws Exception {
		Map<String, Object> editorContext = super.getEditorContext();

		if (!_pageIsDisplayPage) {
			return editorContext;
		}

		Map<String, Object> configContext =
			(Map<String, Object>)editorContext.get("config");

		configContext.put(
			"infoItemPreviewSelectorURL", _getInfoItemPreviewSelectorURL());

		Map<String, Object> stateContext =
			(Map<String, Object>)editorContext.get("state");

		stateContext.put(
			"mappingFields",
			_addDisplayPageMappingFields(
				(JSONObject)stateContext.get("mappingFields")));

		configContext.put("selectedMappingTypes", _getSelectedMappingTypes());

		return editorContext;
	}

	@Override
	public String getPublishURL() {
		return getFragmentEntryActionURL(
			"/layout_content_page_editor/publish_layout_page_template_entry");
	}

	@Override
	public boolean isWorkflowEnabled() {
		return false;
	}

	@Override
	protected List<ItemSelectorCriterion>
		getCollectionItemSelectorCriterions() {

		List<ItemSelectorCriterion> collectionItemSelectorCriterions =
			super.getCollectionItemSelectorCriterions();

		if (!_pageIsDisplayPage) {
			return collectionItemSelectorCriterions;
		}

		RelatedInfoItemCollectionProviderItemSelectorCriterion
			relatedInfoItemCollectionProviderItemSelectorCriterion =
				new RelatedInfoItemCollectionProviderItemSelectorCriterion();

		relatedInfoItemCollectionProviderItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				new InfoListProviderItemSelectorReturnType());

		List<String> sourceItemTypes = new ArrayList<>();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry();

		String className = layoutPageTemplateEntry.getClassName();

		sourceItemTypes.add(className);

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				infoSearchClassMapperRegistry.getSearchClassName(className));

		if (assetRendererFactory != null) {
			sourceItemTypes.add(AssetEntry.class.getName());
		}

		relatedInfoItemCollectionProviderItemSelectorCriterion.
			setSourceItemTypes(sourceItemTypes);

		RepeatableFieldInfoCollectionProviderItemSelectorCriterion
			repeatableFieldInfoCollectionProviderItemSelectorCriterion =
				new RepeatableFieldInfoCollectionProviderItemSelectorCriterion();

		repeatableFieldInfoCollectionProviderItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				new InfoListProviderItemSelectorReturnType());
		repeatableFieldInfoCollectionProviderItemSelectorCriterion.setItemType(
			layoutPageTemplateEntry.getClassName());
		repeatableFieldInfoCollectionProviderItemSelectorCriterion.
			setItemSubtype(
				String.valueOf(layoutPageTemplateEntry.getClassTypeId()));

		return ListUtil.concat(
			collectionItemSelectorCriterions,
			Arrays.asList(
				relatedInfoItemCollectionProviderItemSelectorCriterion,
				repeatableFieldInfoCollectionProviderItemSelectorCriterion));
	}

	private JSONObject _addDisplayPageMappingFields(
			JSONObject mappingFieldsJSONObject)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry();

		String key =
			layoutPageTemplateEntry.getClassNameId() + StringPool.DASH +
				layoutPageTemplateEntry.getClassTypeId();

		if (!mappingFieldsJSONObject.has(key)) {
			mappingFieldsJSONObject.put(
				key,
				MappingContentUtil.getMappingFieldsJSONArray(
					String.valueOf(layoutPageTemplateEntry.getClassTypeId()),
					themeDisplay.getScopeGroupId(), infoItemServiceRegistry,
					layoutPageTemplateEntry.getClassName(),
					themeDisplay.getLocale()));
		}

		return mappingFieldsJSONObject;
	}

	private String _getInfoItemPreviewSelectorURL() {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry();

		if ((layoutPageTemplateEntry == null) ||
			(layoutPageTemplateEntry.getClassNameId() <= 0)) {

			return StringPool.BLANK;
		}

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(
			layoutPageTemplateEntry.getClassName());
		itemSelectorCriterion.setItemSubtype(
			_getItemSubtype(layoutPageTemplateEntry.getClassTypeId()));

		PortletURL infoItemSelectorURL = _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
			renderResponse.getNamespace() + "selectInfoItem",
			itemSelectorCriterion);

		if (infoItemSelectorURL == null) {
			return StringPool.BLANK;
		}

		return infoItemSelectorURL.toString();
	}

	private String _getItemSubtype(long classTypeId) {
		if (classTypeId <= 0) {
			return StringPool.BLANK;
		}

		return String.valueOf(classTypeId);
	}

	private LayoutPageTemplateEntry _getLayoutPageTemplateEntry() {
		if (_layoutPageTemplateEntry != null) {
			return _layoutPageTemplateEntry;
		}

		Layout draftLayout = themeDisplay.getLayout();

		_layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(draftLayout.getClassPK());

		return _layoutPageTemplateEntry;
	}

	private String _getMappingSubtypeLabel() {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry();

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				layoutPageTemplateEntry.getClassName());

		if (infoItemFormVariationsProvider == null) {
			return null;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				layoutPageTemplateEntry.getGroupId(),
				String.valueOf(layoutPageTemplateEntry.getClassTypeId()));

		if (infoItemFormVariation != null) {
			return infoItemFormVariation.getLabel(themeDisplay.getLocale());
		}

		return null;
	}

	private String _getMappingTypeLabel() {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry();

		InfoItemFormProvider<?> infoItemFormProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class,
				layoutPageTemplateEntry.getClassName());

		if (infoItemFormProvider == null) {
			return null;
		}

		InfoItemDetailsProvider<?> infoItemDetailsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				layoutPageTemplateEntry.getClassName());

		if (infoItemDetailsProvider == null) {
			return null;
		}

		InfoItemClassDetails infoItemClassDetails =
			infoItemDetailsProvider.getInfoItemClassDetails();

		return infoItemClassDetails.getLabel(themeDisplay.getLocale());
	}

	private Map<String, Object> _getSelectedMappingTypes() {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry();

		if ((layoutPageTemplateEntry == null) ||
			(layoutPageTemplateEntry.getClassNameId() <= 0)) {

			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"mappingDescription",
			language.get(
				httpServletRequest,
				"content-source-selected-for-this-display-page-template")
		).put(
			"subtype",
			() -> {
				String subtypeLabel = _getMappingSubtypeLabel();

				if (Validator.isNull(subtypeLabel)) {
					return StringPool.BLANK;
				}

				return HashMapBuilder.<String, Object>put(
					"groupSubtypeTitle",
					language.get(httpServletRequest, "subtype")
				).put(
					"id", layoutPageTemplateEntry.getClassTypeId()
				).put(
					"label", subtypeLabel
				).build();
			}
		).put(
			"type",
			HashMapBuilder.<String, Object>put(
				"groupTypeTitle",
				language.get(httpServletRequest, "content-type")
			).put(
				"id", layoutPageTemplateEntry.getClassNameId()
			).put(
				"label", _getMappingTypeLabel()
			).build()
		).build();
	}

	private final ItemSelector _itemSelector;
	private LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final boolean _pageIsDisplayPage;

}
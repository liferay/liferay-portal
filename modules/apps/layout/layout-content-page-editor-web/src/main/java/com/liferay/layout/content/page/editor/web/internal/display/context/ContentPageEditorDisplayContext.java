/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.display.context;

import com.liferay.asset.categories.item.selector.AssetCategoryTreeNodeItemSelectorCriterion;
import com.liferay.asset.categories.item.selector.AssetCategoryTreeNodeItemSelectorReturnType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.info.collection.provider.item.selector.InfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.field.item.selector.InfoFieldItemSelectorCriterion;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.ActionableInfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.VideoEmbeddableHTMLItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.item.selector.criteria.url.criterion.URLItemSelectorCriterion;
import com.liferay.item.selector.criteria.video.criterion.VideoItemSelectorCriterion;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.constants.LayoutScreenNavigationEntryConstants;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.sidebar.panel.ContentPageEditorSidebarPanel;
import com.liferay.layout.content.page.editor.web.internal.configuration.PageEditorConfiguration;
import com.liferay.layout.content.page.editor.web.internal.constants.ContentPageEditorActionKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.ContentManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentCollectionManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.MappingContentUtil;
import com.liferay.layout.content.page.editor.web.internal.util.MappingTypesUtil;
import com.liferay.layout.content.page.editor.web.internal.util.StyleBookEntryUtil;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.converter.PaddingConverter;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.item.selector.LayoutItemSelectorCriterion;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.info.item.capability.EditPageInfoItemCapability;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateEntryNameComparator;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.util.structure.CommonStylesUtil;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.marketplace.constants.MarketplaceActionKeys;
import com.liferay.marketplace.constants.MarketplacePortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsEntryService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.site.navigation.item.selector.SiteNavigationMenuItemSelectorCriterion;
import com.liferay.site.navigation.item.selector.SiteNavigationMenuItemSelectorReturnType;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;
import com.liferay.style.book.util.StyleBookUtil;
import com.liferay.style.book.util.comparator.StyleBookEntryNameComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class ContentPageEditorDisplayContext {

	public ContentPageEditorDisplayContext(
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
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateEntryService layoutPageTemplateEntryService,
		LayoutPermission layoutPermission,
		LayoutSetLocalService layoutSetLocalService,
		PageEditorConfiguration pageEditorConfiguration, Portal portal,
		PortletRequest portletRequest,
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

		_contentPageEditorSidebarPanels = contentPageEditorSidebarPanels;
		_contentManager = contentManager;
		_fragmentCollectionManager = fragmentCollectionManager;
		_fragmentEntryLinkManager = fragmentEntryLinkManager;
		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;
		_itemSelector = itemSelector;
		_jsonFactory = jsonFactory;
		this.language = language;
		_layoutLocalService = layoutLocalService;
		this.layoutLockManager = layoutLockManager;
		this.layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutPageTemplateEntryService = layoutPageTemplateEntryService;
		_layoutPermission = layoutPermission;
		_layoutSetLocalService = layoutSetLocalService;
		_pageEditorConfiguration = pageEditorConfiguration;
		this.portal = portal;
		_portletResourcePermission = portletResourcePermission;
		_portletURLFactory = portletURLFactory;
		this.renderResponse = renderResponse;
		_segmentsConfigurationProvider = segmentsConfigurationProvider;
		_segmentsExperienceManager = segmentsExperienceManager;
		this.segmentsExperienceLocalService = segmentsExperienceLocalService;
		_segmentsExperimentRelLocalService = segmentsExperimentRelLocalService;
		_segmentsEntryService = segmentsEntryService;
		_staging = staging;
		_styleBookEntryLocalService = styleBookEntryLocalService;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;

		this.httpServletRequest = httpServletRequest;
		this.infoItemServiceRegistry = infoItemServiceRegistry;
		this.infoSearchClassMapperRegistry = infoSearchClassMapperRegistry;
		this.portletRequest = portletRequest;
		this.stagingGroupHelper = stagingGroupHelper;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getEditorContext() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"config",
			HashMapBuilder.<String, Object>put(
				"actionableInfoItemSelectorURL",
				_getActionableInfoItemSelectorURL()
			).put(
				"addFragmentCompositionURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/add_fragment_composition")
			).put(
				"addFragmentEntryLinkCommentURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/add_fragment_entry_link_comment")
			).put(
				"addFragmentEntryLinksURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/add_fragment_entry_links")
			).put(
				"addFragmentEntryLinkURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/add_fragment_entry_link")
			).put(
				"addItemURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/add_item")
			).put(
				"addPortletURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/add_portlet")
			).put(
				"addRuleURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/add_rule")
			).put(
				"addStepperFragmentEntryLinkURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/add_stepper_fragment_entry_link")
			).put(
				"assetCategoryTreeNodeItemSelectorURL",
				_getAssetCategoryTreeNodeItemSelectorURL()
			).put(
				"autoExtendSessionEnabled",
				_pageEditorConfiguration.autoExtendSessionEnabled()
			).put(
				"availableLanguages", _getAvailableLanguages()
			).put(
				"availableSegmentsEntries", _getAvailableSegmentsEntries()
			).put(
				"availableViewportSizes", _getAvailableViewportSizes()
			).put(
				"changeMasterLayoutURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/change_master_layout")
			).put(
				"changeStyleBookEntryURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/change_style_book_entry")
			).put(
				"collectionSelectorURL", _getCollectionSelectorURL()
			).put(
				"commonStyles",
				CommonStylesUtil.getCommonStylesJSONArray(
					LanguageResources.getResourceBundle(
						themeDisplay.getLocale()))
			).put(
				"copyItemsURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/copy_items")
			).put(
				"createLayoutPageTemplateEntryURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/create_layout_page_template_entry")
			).put(
				"defaultEditorConfigurations", _getDefaultConfigurations()
			).put(
				"defaultLanguageId",
				LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale())
			).put(
				"defaultSegmentsExperienceId",
				segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
					themeDisplay.getPlid())
			).put(
				"deleteFormStepURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/delete_form_step")
			).put(
				"deleteFragmentEntryLinkCommentURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/delete_fragment_entry_link_comment")
			).put(
				"deleteRuleURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/delete_rule")
			).put(
				"discardDraftURL", _getDiscardDraftURL()
			).put(
				"duplicateItemURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/duplicate_item")
			).put(
				"duplicateSegmentsExperienceURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/duplicate_segments_experience")
			).put(
				"editFragmentEntryLinkCommentURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/edit_fragment_entry_link_comment",
					Constants.UPDATE)
			).put(
				"editFragmentEntryLinkURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/edit_fragment_entry_link")
			).put(
				"formTypes",
				MappingTypesUtil.getMappingTypesJSONArray(
					infoItemServiceRegistry, EditPageInfoItemCapability.KEY,
					themeDisplay)
			).put(
				"fragmentCompositionDescriptionMaxLength",
				() -> ModelHintsUtil.getMaxLength(
					FragmentComposition.class.getName(), "description")
			).put(
				"fragmentCompositionNameMaxLength",
				() -> ModelHintsUtil.getMaxLength(
					FragmentComposition.class.getName(), "name")
			).put(
				"fragmentPortletNamespace",
				portal.getPortletNamespace(FragmentPortletKeys.FRAGMENT)
			).put(
				"fragmentsImportURL",
				() -> ResourceURLBuilder.createResourceURL(
					PortletURLFactoryUtil.create(
						httpServletRequest, FragmentPortletKeys.FRAGMENT,
						PortletRequest.RESOURCE_PHASE)
				).setParameter(
					"fragmentCollectionId",
					ParamUtil.getString(
						httpServletRequest, "fragmentCollectionId")
				).setResourceID(
					"/fragment/import"
				).buildString()
			).put(
				"frontendTokens",
				() -> {
					FrontendTokenDefinition frontendTokenDefinition = null;

					if (FeatureFlagManagerUtil.isEnabled(
							themeDisplay.getCompanyId(), "LPD-30204")) {

						frontendTokenDefinition =
							_frontendTokenDefinitionRegistry.
								getFrontendTokenDefinition(
									themeDisplay.getLayout());
					}
					else {
						Group group = themeDisplay.getScopeGroup();

						frontendTokenDefinition =
							_frontendTokenDefinitionRegistry.
								getFrontendTokenDefinition(
									_layoutSetLocalService.fetchLayoutSet(
										themeDisplay.getSiteGroupId(),
										group.isLayoutSetPrototype()));
					}

					if (frontendTokenDefinition == null) {
						return _jsonFactory.createJSONObject();
					}

					return StyleBookEntryUtil.getFrontendTokensValues(
						frontendTokenDefinition, themeDisplay.getLocale(),
						_getDefaultStyleBookEntry());
				}
			).put(
				"getAvailableImageConfigurationsURL",
				_getResourceURL(
					"/layout_content_page_editor" +
						"/get_available_image_configurations")
			).put(
				"getAvailableListItemRenderersURL",
				_getResourceURL(
					"/layout_content_page_editor" +
						"/get_available_list_item_renderers")
			).put(
				"getAvailableListRenderersURL",
				_getResourceURL(
					"/layout_content_page_editor/get_available_list_renderers")
			).put(
				"getAvailableTemplatesURL",
				_getResourceURL(
					"/layout_content_page_editor/get_available_templates")
			).put(
				"getCollectionFieldURL",
				_getResourceURL(
					"/layout_content_page_editor/get_collection_field")
			).put(
				"getCollectionFiltersURL",
				_getResourceURL(
					"/layout_content_page_editor/get_collection_filters")
			).put(
				"getCollectionItemCountURL",
				_getResourceURL(
					"/layout_content_page_editor/get_collection_item_count")
			).put(
				"getCollectionMappingFieldsURL",
				_getResourceURL(
					"/layout_content_page_editor/get_collection_mapping_fields")
			).put(
				"getCollectionSupportedFiltersURL",
				_getResourceURL(
					"/layout_content_page_editor" +
						"/get_collection_supported_filters")
			).put(
				"getCollectionVariationsURL",
				_getResourceURL(
					"/layout_content_page_editor/get_collection_variations")
			).put(
				"getCollectionWarningMessageURL",
				_getResourceURL(
					"/layout_content_page_editor" +
						"/get_collection_warning_message")
			).put(
				"getEditCollectionConfigurationURL",
				ResourceURLBuilder.createResourceURL(
					renderResponse
				).setRedirect(
					themeDisplay.getURLCurrent()
				).setResourceID(
					"/layout_content_page_editor" +
						"/get_edit_collection_configuration_url"
				).buildString()
			).put(
				"getExperienceDataURL",
				_getResourceURL(
					"/layout_content_page_editor/get_experience_data")
			).put(
				"getFileEntryURL",
				_getResourceURL(
					"/layout_content_page_editor/get_file_entry_url")
			).put(
				"getFormConfigURL",
				_getResourceURL("/layout_content_page_editor/get_form_config")
			).put(
				"getFormFieldsURL",
				_getResourceURL("/layout_content_page_editor/get_form_fields")
			).put(
				"getFragmentEntryInputFieldTypesURL",
				_getResourceURL(
					"/layout_content_page_editor" +
						"/get_fragment_entry_input_field_types")
			).put(
				"getIframeContentCssURL",
				portal.getStaticResourceURL(
					httpServletRequest,
					portal.getPathModule() +
						"/layout-content-page-editor-web/page_editor/app" +
							"/components/App.css")
			).put(
				"getIframeContentURL",
				() -> {
					String layoutURL = portal.getLayoutFriendlyURL(
						themeDisplay.getLayout(), themeDisplay);

					layoutURL = HttpComponentsUtil.addParameter(
						layoutURL, "p_l_mode", Constants.PREVIEW);

					return HttpComponentsUtil.addParameter(
						layoutURL, "disableCommonStyles", Boolean.TRUE);
				}
			).put(
				"getInfoItemActionErrorMessageURL",
				_getResourceURL(
					"/layout_content_page_editor" +
						"/get_info_item_action_error_message")
			).put(
				"getInfoItemFieldValueURL",
				_getResourceURL(
					"/layout_content_page_editor/get_info_item_field_value")
			).put(
				"getInfoItemOneToManyRelationshipsURL",
				_getResourceURL(
					"/layout_content_page_editor" +
						"/get_info_item_one_to_many_relationships")
			).put(
				"getLayoutFriendlyURL",
				_getResourceURL(
					"/layout_content_page_editor/get_layout_friendly_url")
			).put(
				"getLayoutPageTemplateCollectionsURL",
				_getResourceURL(
					"/layout_content_page_editor" +
						"/get_layout_page_template_collections")
			).put(
				"getPageContentsURL",
				_getResourceURL("/layout_content_page_editor/get_page_content")
			).put(
				"getPortletsURL",
				_getResourceURL("/layout_content_page_editor/get_portlets")
			).put(
				"getRolesURL",
				_getResourceURL("/layout_content_page_editor/get_roles")
			).put(
				"getUsersURL",
				_getResourceURL("/layout_content_page_editor/get_users")
			).put(
				"imageSelectorURL", _getItemSelectorURL()
			).put(
				"imagesPath",
				portal.getPathContext(httpServletRequest) + "/images"
			).put(
				"infoFieldItemSelectorURL", _getInfoFieldItemSelectorURL()
			).put(
				"infoItemSelectorURL", _getInfoItemSelectorURL()
			).put(
				"infoListSelectorURL", _getInfoListSelectorURL()
			).put(
				"isCMS",
				() -> {
					Group scopeGroup = themeDisplay.getScopeGroup();

					return scopeGroup.isCMS();
				}
			).put(
				"isConversionDraft", _isConversionDraft()
			).put(
				"isPrivateLayoutsEnabled",
				() -> {
					Group group = themeDisplay.getScopeGroup();

					return group.isPrivateLayoutsEnabled();
				}
			).put(
				"isSegmentationEnabled", _isSegmentationEnabled()
			).put(
				"layoutConversionWarningMessages",
				MultiSessionMessages.get(
					portletRequest, "layoutConversionWarningMessages")
			).put(
				"layoutItemSelectorURL", _getLayoutItemSelectorURL()
			).put(
				"layoutType", String.valueOf(_getLayoutType())
			).put(
				"lookAndFeelURL", getLookAndFeelURL()
			).put(
				"mappingFieldsURL",
				_getResourceURL(
					"/layout_content_page_editor/get_mapping_fields")
			).put(
				"markItemForDeletionURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/mark_item_for_deletion")
			).put(
				"masterLayouts", _getMasterLayouts()
			).put(
				"masterUsed", _isMasterUsed()
			).put(
				"maxNumberOfItemsInEditMode",
				_pageEditorConfiguration.maxNumberOfItemsInEditMode()
			).put(
				"moveItemsURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/move_fragment_entry_link")
			).put(
				"moveStepperFragmentEntryLinkURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/move_stepper_fragment_entry_link")
			).put(
				"paddingOptions",
				() -> {
					Set<Map.Entry<String, String>> entrySet =
						PaddingConverter.externalToInternalValuesMap.entrySet();

					List<Map<String, String>> list = new ArrayList<>();

					for (Map.Entry<String, String> entry : entrySet) {
						list.add(
							HashMapBuilder.put(
								"label", entry.getKey()
							).put(
								"value", entry.getValue()
							).build());
					}

					return list;
				}
			).put(
				"pending",
				() -> {
					Layout draftLayout = themeDisplay.getLayout();

					if (draftLayout.isDenied() || draftLayout.isPending()) {
						return true;
					}

					Layout publishedLayout = _getPublishedLayout();

					if ((publishedLayout != null) &&
						(publishedLayout.isDenied() ||
						 publishedLayout.isPending())) {

						return true;
					}

					return false;
				}
			).put(
				"portletNamespace", getPortletNamespace()
			).put(
				"publishURL", getPublishURL()
			).put(
				"redirectURL", _getRedirect()
			).put(
				"regenerateDisplayPageURL",
				() -> {
					Layout draftLayout = themeDisplay.getLayout();

					return StringBundler.concat(
						themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
						"/cms/regenerate_structure_display_page?plid=",
						draftLayout.getPlid());
				}
			).put(
				"renderFragmentEntriesURL",
				_getResourceURL(
					"/layout_content_page_editor/get_fragment_entry_links")
			).put(
				"restoreCollectionDisplayConfigURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/restore_collection_display_config")
			).put(
				"saveVariantSegmentsExperienceURL",
				getSaveVariantSegmentsExperienceURL()
			).put(
				"searchContainerPageMaxDelta",
				PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA
			).put(
				"segmentsConfigurationURL",
				_getSegmentsCompanyConfigurationURL()
			).put(
				"sidebarPanels", getSidebarPanels()
			).put(
				"siteNavigationMenuItemSelectorURL",
				_getSiteNavigationMenuItemSelectorURL()
			).put(
				"styleBookEnabled",
				() -> {
					Layout layout = themeDisplay.getLayout();

					Theme theme = layout.getTheme();

					LayoutSet layoutSet = _layoutSetLocalService.fetchLayoutSet(
						themeDisplay.getSiteGroupId(), false);

					return Objects.equals(
						theme.getThemeId(), layoutSet.getThemeId());
				}
			).put(
				"styleBookEntryId",
				() -> {
					Layout layout = themeDisplay.getLayout();

					if (!FeatureFlagManagerUtil.isEnabled(
							layout.getCompanyId(), "LPD-30204")) {

						return layout.getStyleBookEntryId();
					}

					if (layout.getStyleBookEntryId() > 0) {
						StyleBookEntry defaultStyleBookEntry =
							DefaultStyleBookEntryUtil.getDefaultStyleBookEntry(
								layout);

						if (defaultStyleBookEntry != null) {
							return defaultStyleBookEntry.getStyleBookEntryId();
						}
					}

					return "0";
				}
			).put(
				"styleBooks", _getStyleBooks()
			).put(
				"themeColorsCssClasses", _getThemeColorsCssClasses()
			).put(
				"themeName",
				StyleBookUtil.getThemeName(
					themeDisplay.getLayout(), themeDisplay.getLocale())
			).put(
				"undoUpdateFormConfigURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/undo_form_item_config")
			).put(
				"unmarkItemsForDeletionURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/unmark_items_for_deletion")
			).put(
				"updateCollectionDisplayConfigURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/update_collection_display_config")
			).put(
				"updateConfigurationValuesURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/update_configuration_values")
			).put(
				"updateFormItemConfigURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/update_form_item_config")
			).put(
				"updateFragmentPortletSetsSortURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/update_fragment_portlet_sets_sort_configuration")
			).put(
				"updateFragmentsHighlightedConfigurationURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/update_fragments_highlighted_configuration")
			).put(
				"updateItemConfigURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/update_item_config")
			).put(
				"updateLayoutPageTemplateDataURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/update_layout_page_template_data")
			).put(
				"updatePortletsHighlightedConfigurationURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/update_portlets_highlighted_configuration")
			).put(
				"updateRowColumnsURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/update_row_columns")
			).put(
				"updateRuleURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/update_rule")
			).put(
				"updateSegmentsExperiencePriorityURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor" +
						"/update_segments_experience_priority")
			).put(
				"updateSegmentsExperienceURL",
				getFragmentEntryActionURL(
					"/layout_content_page_editor/update_segments_experience")
			).put(
				"videoItemSelectorURL", _getVideoItemSelectorURL()
			).put(
				"workflowEnabled", isWorkflowEnabled()
			).build()
		).put(
			"state",
			HashMapBuilder.<String, Object>put(
				"collections",
				_fragmentCollectionManager.getFragmentCollectionMapsList(
					getGroupId(), httpServletRequest, true, false,
					_getMasterDropZoneLayoutStructureItem(), themeDisplay)
			).put(
				"draft",
				() -> {
					Layout layout = themeDisplay.getLayout();

					return layout.isDraft();
				}
			).put(
				"fragmentEntryLinks", _getFragmentEntryLinks()
			).put(
				"fragments",
				_fragmentCollectionManager.getFragmentCollectionMapsList(
					getGroupId(), httpServletRequest, false, true,
					_getMasterDropZoneLayoutStructureItem(), themeDisplay)
			).put(
				"languageId",
				LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale())
			).put(
				"layoutData",
				() -> {
					LayoutStructure layoutStructure = _getLayoutStructure();

					return layoutStructure.toJSONObject();
				}
			).put(
				"mappingFields", _getMappingFieldsJSONObject()
			).put(
				"masterLayout", _getMasterLayoutJSONObject()
			).put(
				"permissions",
				() -> {
					boolean hasUpdatePermission = _hasPermissions(
						ActionKeys.UPDATE);

					return HashMapBuilder.<String, Object>put(
						ContentPageEditorActionKeys.
							INSTALL_FREE_BUNDLED_APPS_MARKETPLACE,
						() -> PortletPermissionUtil.contains(
							themeDisplay.getPermissionChecker(),
							MarketplacePortletKeys.FRAGMENTS,
							MarketplaceActionKeys.INSTALL_FREE_BUNDLED_APPS)
					).put(
						ContentPageEditorActionKeys.
							PURCHASE_AND_INSTALL_PAID_APPS_MARKETPLACE,
						() -> PortletPermissionUtil.contains(
							themeDisplay.getPermissionChecker(),
							MarketplacePortletKeys.FRAGMENTS,
							MarketplaceActionKeys.
								PURCHASE_AND_INSTALL_PAID_APPS)
					).put(
						ContentPageEditorActionKeys.UPDATE, hasUpdatePermission
					).put(
						ContentPageEditorActionKeys.
							UPDATE_LAYOUT_ADVANCED_OPTIONS,
						() -> {
							if (!hasUpdatePermission) {
								return _hasPermissions(
									ContentPageEditorActionKeys.
										UPDATE_LAYOUT_ADVANCED_OPTIONS);
							}

							return false;
						}
					).put(
						ContentPageEditorActionKeys.UPDATE_LAYOUT_BASIC,
						() -> {
							if (!hasUpdatePermission) {
								return _hasPermissions(
									ContentPageEditorActionKeys.
										UPDATE_LAYOUT_BASIC);
							}

							return false;
						}
					).put(
						ContentPageEditorActionKeys.UPDATE_LAYOUT_CONTENT,
						() -> _hasPermissions(
							ContentPageEditorActionKeys.UPDATE_LAYOUT_CONTENT)
					).put(
						ContentPageEditorActionKeys.UPDATE_LAYOUT_LIMITED,
						() -> {
							if (!hasUpdatePermission) {
								return _hasPermissions(
									ContentPageEditorActionKeys.
										UPDATE_LAYOUT_LIMITED);
							}

							return false;
						}
					).put(
						ContentPageEditorActionKeys.VIEW_MARKETPLACE,
						() -> {
							if (PortletPermissionUtil.contains(
									themeDisplay.getPermissionChecker(),
									MarketplacePortletKeys.FRAGMENTS,
									MarketplaceActionKeys.
										INSTALL_FREE_BUNDLED_APPS) ||
								PortletPermissionUtil.contains(
									themeDisplay.getPermissionChecker(),
									MarketplacePortletKeys.FRAGMENTS,
									MarketplaceActionKeys.
										PURCHASE_AND_INSTALL_PAID_APPS)) {

								return true;
							}

							return PortletPermissionUtil.contains(
								themeDisplay.getPermissionChecker(),
								MarketplacePortletKeys.FRAGMENTS,
								MarketplaceActionKeys.VIEW_APPS);
						}
					).put(
						FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES,
						() -> _portletResourcePermission.contains(
							themeDisplay.getPermissionChecker(), getGroupId(),
							FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)
					).build();
				}
			).put(
				"restrictedItemIds", _getRestrictedItemIds()
			).put(
				"segmentsExperienceId", getSegmentsExperienceId()
			).build()
		).build();
	}

	public String getPortletNamespace() {
		return renderResponse.getNamespace();
	}

	public String getPublishURL() {
		return getFragmentEntryActionURL(
			"/layout_content_page_editor/publish_layout");
	}

	public String getSaveVariantSegmentsExperienceURL() {
		String portletId = _getPortletId(httpServletRequest);

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createActionURL(portletId)
		).setActionName(
			"/layout_content_page_editor/save_variant_segments_experience"
		).setParameter(
			"segmentsExperienceId", getSegmentsExperienceId()
		).buildString();
	}

	public List<Map<String, Object>> getSidebarPanels() {
		return getSidebarPanels(_getLayoutType());
	}

	public boolean isContentLayout() {
		if (_getLayoutType() == -1) {
			return true;
		}

		return false;
	}

	public boolean isMasterLayout() {
		if (_getLayoutType() ==
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

			return true;
		}

		return false;
	}

	public boolean isSingleSegmentsExperienceMode() {
		return _isSegmentsExperimentVariant();
	}

	public boolean isWorkflowEnabled() {
		return _workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
			themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
			Layout.class.getName());
	}

	protected List<ItemSelectorCriterion>
		getCollectionItemSelectorCriterions() {

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

		return Collections.singletonList(
			infoCollectionProviderItemSelectorCriterion);
	}

	protected long getDraftSegmentsExperienceId(
		long plid, long segmentsExperienceId) {

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if (segmentsExperience == null) {
			return segmentsExperienceId;
		}

		segmentsExperience =
			segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience.getGroupId(),
				segmentsExperience.getSegmentsExperienceKey(), plid);

		if (segmentsExperience == null) {
			return segmentsExperienceId;
		}

		return segmentsExperience.getSegmentsExperienceId();
	}

	protected String getFragmentEntryActionURL(String action) {
		return getFragmentEntryActionURL(action, null);
	}

	protected String getFragmentEntryActionURL(String action, String command) {
		return HttpComponentsUtil.addParameter(
			PortletURLBuilder.createActionURL(
				renderResponse
			).setActionName(
				action
			).setCMD(
				() -> {
					if (Validator.isNotNull(command)) {
						return command;
					}

					return null;
				}
			).setBackURL(
				ParamUtil.getString(
					portal.getOriginalServletRequest(httpServletRequest),
					"p_l_back_url", themeDisplay.getURLCurrent())
			).buildString(),
			"p_l_mode", Constants.EDIT);
	}

	protected long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(
			httpServletRequest, "groupId", themeDisplay.getScopeGroupId());

		return _groupId;
	}

	protected String getLookAndFeelURL() throws Exception {
		return layoutLockManager.getUnlockDraftLayoutURL(
			portal.getLiferayPortletResponse(renderResponse),
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				Layout layout = themeDisplay.getLayout();

				return PortletURLBuilder.create(
					portal.getControlPanelPortletURL(
						httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/layout_admin/edit_layout"
				).setRedirect(
					themeDisplay.getURLCurrent()
				).setBackURL(
					themeDisplay.getURLCurrent()
				).setParameter(
					"backURLTitle", layout.getName(themeDisplay.getLocale())
				).setParameter(
					"groupId", layout.getGroupId()
				).setParameter(
					"privateLayout", layout.isPrivateLayout()
				).setParameter(
					"screenNavigationEntryKey",
					LayoutScreenNavigationEntryConstants.ENTRY_KEY_DESIGN
				).setParameter(
					"selPlid",
					() -> {
						if (layout.isDraftLayout()) {
							return layout.getClassPK();
						}

						return layout.getPlid();
					}
				).buildString();
			});
	}

	protected long getSegmentsExperienceId() {
		if (_segmentsExperienceId != null) {
			return _segmentsExperienceId;
		}

		Layout layout = themeDisplay.getLayout();

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		// LPS-131416

		_segmentsExperienceId = GetterUtil.getLong(
			unicodeProperties.getProperty("segmentsExperienceId"), -1);

		if (_segmentsExperienceId != -1) {
			SegmentsExperience segmentsExperience =
				segmentsExperienceLocalService.fetchSegmentsExperience(
					_segmentsExperienceId);

			if (segmentsExperience != null) {
				_segmentsExperienceId =
					segmentsExperience.getSegmentsExperienceId();
			}
			else {
				_segmentsExperienceId = -1L;
			}
		}

		if (_segmentsExperienceId == -1) {
			_segmentsExperienceId =
				_segmentsExperienceManager.getSegmentsExperienceId(
					httpServletRequest);
		}

		_segmentsExperienceId = getDraftSegmentsExperienceId(
			themeDisplay.getPlid(), _segmentsExperienceId);

		return _segmentsExperienceId;
	}

	protected List<Map<String, Object>> getSidebarPanels(int layoutType) {
		if (_sidebarPanels != null) {
			return _sidebarPanels;
		}

		List<Map<String, Object>> sidebarPanels = new ArrayList<>();

		for (ContentPageEditorSidebarPanel contentPageEditorSidebarPanel :
				_contentPageEditorSidebarPanels) {

			if (!contentPageEditorSidebarPanel.isVisible(
					themeDisplay.getPermissionChecker(), themeDisplay.getPlid(),
					layoutType)) {

				continue;
			}

			sidebarPanels.add(
				HashMapBuilder.<String, Object>put(
					"icon", contentPageEditorSidebarPanel.getIcon()
				).put(
					"label",
					contentPageEditorSidebarPanel.getLabel(
						themeDisplay.getLocale())
				).put(
					"sidebarPanelId", contentPageEditorSidebarPanel.getId()
				).build());
		}

		_sidebarPanels = sidebarPanels;

		return _sidebarPanels;
	}

	protected final HttpServletRequest httpServletRequest;
	protected final InfoItemServiceRegistry infoItemServiceRegistry;
	protected final InfoSearchClassMapperRegistry infoSearchClassMapperRegistry;
	protected final Language language;
	protected final LayoutLockManager layoutLockManager;
	protected final LayoutPageTemplateEntryLocalService
		layoutPageTemplateEntryLocalService;
	protected final Portal portal;
	protected final PortletRequest portletRequest;
	protected final RenderResponse renderResponse;
	protected final SegmentsExperienceLocalService
		segmentsExperienceLocalService;
	protected final StagingGroupHelper stagingGroupHelper;
	protected final ThemeDisplay themeDisplay;

	private String _getActionableInfoItemSelectorURL() {
		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new ActionableInfoItemItemSelectorReturnType());

		PortletURL infoItemSelectorURL = _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
			renderResponse.getNamespace() + "selectInfoItem",
			itemSelectorCriterion);

		if (infoItemSelectorURL == null) {
			return StringPool.BLANK;
		}

		return infoItemSelectorURL.toString();
	}

	private String _getAssetCategoryTreeNodeItemSelectorURL() {
		ItemSelectorCriterion itemSelectorCriterion =
			new AssetCategoryTreeNodeItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetCategoryTreeNodeItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				renderResponse.getNamespace() + "selectAssetCategoryTreeNode",
				itemSelectorCriterion));
	}

	private Map<String, Object> _getAvailableLanguages() {
		Map<String, Object> availableLanguages = new LinkedHashMap<>();

		for (Locale locale :
				language.getAvailableLocales(themeDisplay.getSiteGroupId())) {

			availableLanguages.put(
				LocaleUtil.toLanguageId(locale),
				HashMapBuilder.<String, Object>put(
					"languageIcon",
					StringUtil.toLowerCase(LocaleUtil.toW3cLanguageId(locale))
				).put(
					"w3cLanguageId", LocaleUtil.toW3cLanguageId(locale)
				).build());
		}

		return availableLanguages;
	}

	private Map<String, Object> _getAvailableSegmentsEntries() {
		Map<String, Object> availableSegmentsEntries = new HashMap<>();

		List<SegmentsEntry> segmentsEntries =
			_segmentsEntryService.getSegmentsEntries(
				stagingGroupHelper.getStagedPortletGroupId(
					getGroupId(), SegmentsPortletKeys.SEGMENTS));

		for (SegmentsEntry segmentsEntry : segmentsEntries) {
			availableSegmentsEntries.put(
				String.valueOf(segmentsEntry.getSegmentsEntryId()),
				HashMapBuilder.<String, Object>put(
					"name", segmentsEntry.getName(themeDisplay.getLocale())
				).put(
					"segmentsEntryId",
					String.valueOf(segmentsEntry.getSegmentsEntryId())
				).build());
		}

		availableSegmentsEntries.put(
			String.valueOf(SegmentsEntryConstants.ID_DEFAULT),
			HashMapBuilder.<String, Object>put(
				"name",
				SegmentsEntryConstants.getDefaultSegmentsEntryName(
					themeDisplay.getLocale())
			).put(
				"segmentsEntryId", SegmentsEntryConstants.ID_DEFAULT
			).build());

		return availableSegmentsEntries;
	}

	private Map<String, Map<String, Object>> _getAvailableViewportSizes() {
		Map<String, Map<String, Object>> availableViewportSizesMap =
			new LinkedHashMap<>();

		for (ViewportSize viewportSize :
				ListUtil.sort(
					Arrays.asList(ViewportSize.values()),
					Comparator.comparingInt(ViewportSize::getOrder))) {

			availableViewportSizesMap.put(
				viewportSize.getViewportSizeId(),
				HashMapBuilder.<String, Object>put(
					"icon", viewportSize.getIcon()
				).put(
					"label",
					language.get(httpServletRequest, viewportSize.getLabel())
				).put(
					"maxWidth", viewportSize.getMaxWidth()
				).put(
					"minWidth", viewportSize.getMinWidth()
				).put(
					"sizeId", viewportSize.getViewportSizeId()
				).build());
		}

		return availableViewportSizesMap;
	}

	private String _getCollectionSelectorURL() {
		List<ItemSelectorCriterion> collectionItemSelectorCriterions =
			getCollectionItemSelectorCriterions();

		PortletURL infoListSelectorURL = _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
			renderResponse.getNamespace() + "selectInfoList",
			collectionItemSelectorCriterions.toArray(
				new ItemSelectorCriterion[0]));

		if (infoListSelectorURL == null) {
			return StringPool.BLANK;
		}

		return HttpComponentsUtil.addParameter(
			infoListSelectorURL.toString(), "refererPlid",
			themeDisplay.getPlid());
	}

	private Map<String, Object> _getDefaultConfigurations() {
		if (_defaultConfigurations != null) {
			return _defaultConfigurations;
		}

		_defaultConfigurations = HashMapBuilder.<String, Object>put(
			"comment",
			() -> {
				EditorConfiguration commentEditorConfiguration =
					EditorConfigurationFactoryUtil.getEditorConfiguration(
						ContentPageEditorPortletKeys.
							CONTENT_PAGE_EDITOR_PORTLET,
						"pageEditorCommentEditor", StringPool.BLANK,
						Collections.emptyMap(), themeDisplay,
						RequestBackedPortletURLFactoryUtil.create(
							httpServletRequest));

				return commentEditorConfiguration.getData();
			}
		).put(
			"rich-text",
			() -> {
				EditorConfiguration richTextEditorConfiguration =
					EditorConfigurationFactoryUtil.getEditorConfiguration(
						ContentPageEditorPortletKeys.
							CONTENT_PAGE_EDITOR_PORTLET,
						"fragmentEntryLinkRichTextEditor", StringPool.BLANK,
						Collections.emptyMap(), themeDisplay,
						RequestBackedPortletURLFactoryUtil.create(
							httpServletRequest));

				return richTextEditorConfiguration.getData();
			}
		).put(
			"text",
			() -> {
				EditorConfiguration editorConfiguration =
					EditorConfigurationFactoryUtil.getEditorConfiguration(
						ContentPageEditorPortletKeys.
							CONTENT_PAGE_EDITOR_PORTLET,
						"fragmentEntryLinkEditor", StringPool.BLANK,
						Collections.emptyMap(), themeDisplay,
						RequestBackedPortletURLFactoryUtil.create(
							httpServletRequest));

				return editorConfiguration.getData();
			}
		).build();

		return _defaultConfigurations;
	}

	private StyleBookEntry _getDefaultMasterStyleBookEntry() {
		if (_defaultMasterStyleBookEntry != null) {
			return _defaultMasterStyleBookEntry;
		}

		_defaultMasterStyleBookEntry =
			DefaultStyleBookEntryUtil.getDefaultMasterStyleBookEntry(
				themeDisplay.getLayout());

		return _defaultMasterStyleBookEntry;
	}

	private StyleBookEntry _getDefaultStyleBookEntry() {
		if (_defaultStyleBookEntry != null) {
			return _defaultStyleBookEntry;
		}

		_defaultStyleBookEntry =
			DefaultStyleBookEntryUtil.getDefaultStyleBookEntry(
				themeDisplay.getLayout());

		return _defaultStyleBookEntry;
	}

	private String _getDiscardDraftURL() {
		Layout publishedLayout = _getPublishedLayout();

		if ((publishedLayout != null) &&
			!Objects.equals(
				publishedLayout.getType(), LayoutConstants.TYPE_PORTLET)) {

			return PortletURLBuilder.create(
				_portletURLFactory.create(
					httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"/layout_admin/discard_draft_layout"
			).setRedirect(
				_getRedirect()
			).setParameter(
				"selPlid", themeDisplay.getPlid()
			).buildString();
		}

		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/layout_admin/delete_layout"
		).setRedirect(
			PortletURLBuilder.create(
				portal.getControlPanelPortletURL(
					httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"selPlid",
				() -> {
					if (publishedLayout != null) {
						return publishedLayout.getPlid();
					}

					Layout draftLayout = themeDisplay.getLayout();

					return draftLayout.getClassPK();
				}
			).buildString()
		).setParameter(
			"selPlid", themeDisplay.getPlid()
		).buildString();
	}

	private Map<String, Object> _getFragmentEntryLinks() throws Exception {
		if (_fragmentEntryLinks != null) {
			return _fragmentEntryLinks;
		}

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					getGroupId(), getSegmentsExperienceId(),
					themeDisplay.getPlid(), false);

		LayoutStructure layoutStructure = _getLayoutStructure();

		Map<String, Object> fragmentEntryLinksMap = new HashMap<>(
			_getFragmentEntryLinksMap(
				fragmentEntryLinks, false, layoutStructure));

		Layout layout = themeDisplay.getLayout();

		if (layout.getMasterLayoutPlid() > 0) {
			LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
				layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByPlid(
						layout.getMasterLayoutPlid());

			if (masterLayoutPageTemplateEntry != null) {
				fragmentEntryLinks =
					_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
						getGroupId(), masterLayoutPageTemplateEntry.getPlid());

				fragmentEntryLinksMap.putAll(
					_getFragmentEntryLinksMap(
						fragmentEntryLinks, true, _getMasterLayoutStructure()));
			}
		}

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		for (Map.Entry<Long, LayoutStructureItem> fragmentLayoutStructureItem :
				fragmentLayoutStructureItems.entrySet()) {

			if (fragmentEntryLinksMap.containsKey(
					String.valueOf(fragmentLayoutStructureItem.getKey()))) {

				continue;
			}

			LayoutStructureItem layoutStructureItem =
				fragmentLayoutStructureItem.getValue();

			if (layoutStructure.isItemMarkedForDeletion(
					layoutStructureItem.getItemId())) {

				continue;
			}

			fragmentEntryLinksMap.put(
				String.valueOf(fragmentLayoutStructureItem.getKey()),
				JSONUtil.put(
					"configuration", _jsonFactory.createJSONObject()
				).put(
					"content", StringPool.BLANK
				).put(
					"defaultConfigurationValues",
					_jsonFactory.createJSONObject()
				).put(
					"editableValues", _jsonFactory.createJSONObject()
				).put(
					"error", Boolean.TRUE
				).put(
					"fragmentEntryLinkId",
					String.valueOf(fragmentLayoutStructureItem.getKey())
				));
		}

		_fragmentEntryLinks = fragmentEntryLinksMap;

		return _fragmentEntryLinks;
	}

	private Map<String, Object> _getFragmentEntryLinksMap(
			List<FragmentEntryLink> fragmentEntryLinks, boolean masterLayout,
			LayoutStructure layoutStructure)
		throws Exception {

		Map<String, Object> fragmentEntryLinksMap = new HashMap<>();

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			DefaultFragmentRendererContext defaultFragmentRendererContext =
				new DefaultFragmentRendererContext(fragmentEntryLink);

			JSONObject jsonObject =
				_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					defaultFragmentRendererContext, fragmentEntryLink,
					httpServletRequest,
					portal.getHttpServletResponse(renderResponse),
					layoutStructure);

			jsonObject.put(
				"error",
				() -> {
					if (SessionErrors.contains(
							httpServletRequest,
							"fragmentEntryContentInvalid")) {

						SessionErrors.clear(httpServletRequest);

						return true;
					}

					return false;
				}
			).put(
				"masterLayout", masterLayout
			);

			fragmentEntryLinksMap.put(
				String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
				jsonObject);
		}

		return fragmentEntryLinksMap;
	}

	private ItemSelectorCriterion _getImageItemSelectorCriterion() {
		if (_imageItemSelectorCriterion != null) {
			return _imageItemSelectorCriterion;
		}

		ItemSelectorCriterion itemSelectorCriterion =
			new ImageItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType());

		_imageItemSelectorCriterion = itemSelectorCriterion;

		return _imageItemSelectorCriterion;
	}

	private String _getInfoFieldItemSelectorURL() {
		InfoFieldItemSelectorCriterion infoFieldItemSelectorCriterion =
			new InfoFieldItemSelectorCriterion();

		infoFieldItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				renderResponse.getNamespace() + "selectInfoField",
				infoFieldItemSelectorCriterion));
	}

	private String _getInfoItemSelectorURL() {
		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());

		PortletURL infoItemSelectorURL = _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
			renderResponse.getNamespace() + "selectInfoItem",
			itemSelectorCriterion);

		if (infoItemSelectorURL == null) {
			return StringPool.BLANK;
		}

		return infoItemSelectorURL.toString();
	}

	private String _getInfoListSelectorURL() {
		InfoCollectionProviderItemSelectorCriterion
			infoCollectionProviderItemSelectorCriterion =
				new InfoCollectionProviderItemSelectorCriterion();

		infoCollectionProviderItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				new InfoListItemSelectorReturnType(),
				new InfoListProviderItemSelectorReturnType());

		PortletURL infoListSelectorURL = _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
			renderResponse.getNamespace() + "selectInfoList",
			infoCollectionProviderItemSelectorCriterion);

		if (infoListSelectorURL == null) {
			return StringPool.BLANK;
		}

		return HttpComponentsUtil.addParameter(
			infoListSelectorURL.toString(), "refererPlid",
			themeDisplay.getPlid());
	}

	private String _getItemSelectorURL() {
		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				renderResponse.getNamespace() + "selectImage",
				_getImageItemSelectorCriterion(),
				_getURLItemSelectorCriterion()));
	}

	private String _getLayoutItemSelectorURL() {
		LayoutItemSelectorCriterion layoutItemSelectorCriterion =
			new LayoutItemSelectorCriterion();

		layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());
		layoutItemSelectorCriterion.setMultiSelection(false);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				renderResponse.getNamespace() + "selectLayout",
				layoutItemSelectorCriterion));
	}

	private LayoutStructure _getLayoutStructure() throws Exception {
		if (_layoutStructure != null) {
			return _layoutStructure;
		}

		_layoutStructure = LayoutStructureUtil.getLayoutStructure(
			themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
			getSegmentsExperienceId());

		return _layoutStructure;
	}

	private int _getLayoutType() {
		if (_layoutType != null) {
			return _layoutType;
		}

		Layout layout = themeDisplay.getLayout();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByPlid(layout.getClassPK());
		}

		if (layoutPageTemplateEntry == null) {
			_layoutType = -1;
		}
		else {
			_layoutType = layoutPageTemplateEntry.getType();
		}

		return _layoutType;
	}

	private JSONObject _getMappingFieldsJSONObject() throws Exception {
		JSONObject mappingFieldsJSONObject = _jsonFactory.createJSONObject();

		Set<LayoutDisplayPageObjectProvider<?>>
			layoutDisplayPageObjectProviders =
				_contentManager.getMappedLayoutDisplayPageObjectProviders(
					getGroupId(), themeDisplay.getPlid());

		Layout layout = themeDisplay.getLayout();

		if (layout.getMasterLayoutPlid() > 0) {
			layoutDisplayPageObjectProviders.addAll(
				_contentManager.getMappedLayoutDisplayPageObjectProviders(
					getGroupId(), layout.getMasterLayoutPlid()));
		}

		for (LayoutDisplayPageObjectProvider<?>
				layoutDisplayPageObjectProvider :
					layoutDisplayPageObjectProviders) {

			String uniqueMappingFieldKey =
				layoutDisplayPageObjectProvider.getClassNameId() +
					StringPool.DASH +
						layoutDisplayPageObjectProvider.getClassTypeId();

			if (mappingFieldsJSONObject.has(uniqueMappingFieldKey)) {
				continue;
			}

			mappingFieldsJSONObject.put(
				uniqueMappingFieldKey,
				MappingContentUtil.getMappingFieldsJSONArray(
					String.valueOf(
						layoutDisplayPageObjectProvider.getClassTypeId()),
					themeDisplay.getScopeGroupId(), infoItemServiceRegistry,
					layoutDisplayPageObjectProvider.getClassName(),
					themeDisplay.getLocale()));
		}

		return mappingFieldsJSONObject;
	}

	private DropZoneLayoutStructureItem
		_getMasterDropZoneLayoutStructureItem() {

		LayoutStructure masterLayoutStructure = _getMasterLayoutStructure();

		if (masterLayoutStructure == null) {
			return null;
		}

		LayoutStructureItem layoutStructureItem =
			masterLayoutStructure.getDropZoneLayoutStructureItem();

		if (layoutStructureItem == null) {
			return null;
		}

		return (DropZoneLayoutStructureItem)layoutStructureItem;
	}

	private JSONObject _getMasterLayoutJSONObject() {
		return JSONUtil.put(
			"masterLayoutData",
			() -> {
				LayoutStructure layoutStructure = _getMasterLayoutStructure();

				if (layoutStructure != null) {
					return layoutStructure.toJSONObject();
				}

				return null;
			}
		).put(
			"masterLayoutPlid",
			() -> {
				Layout layout = themeDisplay.getLayout();

				return String.valueOf(layout.getMasterLayoutPlid());
			}
		);
	}

	private List<Map<String, Object>> _getMasterLayouts() {
		ArrayList<Map<String, Object>> masterLayouts = new ArrayList<>();

		masterLayouts.add(
			HashMapBuilder.<String, Object>put(
				"imagePreviewURL", StringPool.BLANK
			).put(
				"masterLayoutPlid", "0"
			).put(
				"name", language.get(httpServletRequest, "blank")
			).build());

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				themeDisplay.getScopeGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				LayoutPageTemplateEntryNameComparator.getInstance(true));

		for (LayoutPageTemplateEntry layoutPageTemplateEntry :
				layoutPageTemplateEntries) {

			masterLayouts.add(
				HashMapBuilder.<String, Object>put(
					"imagePreviewURL",
					layoutPageTemplateEntry.getImagePreviewURL(themeDisplay)
				).put(
					"masterLayoutPlid",
					String.valueOf(layoutPageTemplateEntry.getPlid())
				).put(
					"name", layoutPageTemplateEntry.getName()
				).build());
		}

		return masterLayouts;
	}

	private LayoutStructure _getMasterLayoutStructure() {
		if (_masterLayoutStructure != null) {
			return _masterLayoutStructure;
		}

		Layout layout = themeDisplay.getLayout();

		if (layout.getMasterLayoutPlid() <= 0) {
			return _masterLayoutStructure;
		}

		try {
			_masterLayoutStructure = LayoutStructureUtil.getLayoutStructure(
				layout.getGroupId(), layout.getMasterLayoutPlid(),
				SegmentsExperienceConstants.KEY_DEFAULT);

			return _masterLayoutStructure;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get master layout structure", exception);
			}
		}

		return _masterLayoutStructure;
	}

	private String _getPortletId(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getId();
	}

	private Layout _getPublishedLayout() {
		if (_publishedLayout != null) {
			return _publishedLayout;
		}

		Layout draftLayout = themeDisplay.getLayout();

		_publishedLayout = _layoutLocalService.fetchLayout(
			draftLayout.getClassPK());

		return _publishedLayout;
	}

	private String _getRedirect() {
		if (Validator.isNotNull(_redirect)) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNull(_redirect)) {
			_redirect = portal.escapeRedirect(
				ParamUtil.getString(
					portal.getOriginalServletRequest(httpServletRequest),
					"p_l_back_url", themeDisplay.getURLCurrent()));
		}

		return _redirect;
	}

	private String _getResourceURL(String resourceID) {
		return ResourceURLBuilder.createResourceURL(
			renderResponse.createResourceURL()
		).setBackURL(
			ParamUtil.getString(
				portal.getOriginalServletRequest(httpServletRequest),
				"p_l_back_url", themeDisplay.getURLCurrent())
		).setParameter(
			"backURLTitle",
			ParamUtil.getString(
				portal.getOriginalServletRequest(httpServletRequest),
				"p_l_back_url_title")
		).setParameter(
			"p_l_mode", Constants.EDIT
		).setResourceID(
			resourceID
		).buildString();
	}

	private List<String> _getRestrictedItemIds() throws Exception {
		if (_restrictedItemIds != null) {
			return _restrictedItemIds;
		}

		_restrictedItemIds = _contentManager.getRestrictedItemIds(
			httpServletRequest, _getLayoutStructure(), themeDisplay);

		return _restrictedItemIds;
	}

	private String _getSegmentsCompanyConfigurationURL() {
		try {
			return _segmentsConfigurationProvider.getCompanyConfigurationURL(
				httpServletRequest);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	private String _getSiteNavigationMenuItemSelectorURL() {
		ItemSelectorCriterion itemSelectorCriterion =
			new SiteNavigationMenuItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new SiteNavigationMenuItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				renderResponse.getNamespace() + "selectSiteNavigationMenu",
				itemSelectorCriterion));
	}

	private List<Map<String, Object>> _getStyleBooks() throws Exception {
		ArrayList<Map<String, Object>> styleBooks = new ArrayList<>();

		List<StyleBookEntry> styleBookEntries = new ArrayList<>();

		FrontendTokenDefinition frontendTokenDefinition = null;

		if (FeatureFlagManagerUtil.isEnabled("LPD-30204")) {
			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					themeDisplay.getLayout());

			if (frontendTokenDefinition != null) {
				styleBookEntries =
					_styleBookEntryLocalService.getStyleBookEntries(
						_staging.getLiveGroupId(themeDisplay.getScopeGroupId()),
						frontendTokenDefinition.getThemeId());
			}
		}
		else {
			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					themeDisplay.getLayoutSet());

			styleBookEntries = _styleBookEntryLocalService.getStyleBookEntries(
				_staging.getLiveGroupId(themeDisplay.getScopeGroupId()),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				StyleBookEntryNameComparator.getInstance(true));
		}

		if (frontendTokenDefinition != null) {
			styleBooks.add(
				HashMapBuilder.<String, Object>put(
					"imagePreviewURL",
					() -> {
						StyleBookEntry defaultStyleBookEntry =
							_getDefaultMasterStyleBookEntry();

						if (defaultStyleBookEntry != null) {
							return defaultStyleBookEntry.getImagePreviewURL(
								themeDisplay);
						}

						return StringPool.BLANK;
					}
				).put(
					"name",
					DefaultStyleBookEntryUtil.getStyleBookEntryName(
						themeDisplay.getLayout(), themeDisplay.getLocale(),
						StyleBookUtil.getStyleFromThemeStyleBookEntry(
							themeDisplay.getLayout(), themeDisplay.getLocale()))
				).put(
					"styleBookEntryId", "0"
				).put(
					"subtitle",
					() -> {
						StyleBookEntry defaultStyleBookEntry =
							_getDefaultMasterStyleBookEntry();

						if (defaultStyleBookEntry != null) {
							return defaultStyleBookEntry.getName();
						}

						return null;
					}
				).build());
		}

		for (StyleBookEntry styleBookEntry : styleBookEntries) {
			styleBooks.add(
				HashMapBuilder.<String, Object>put(
					"imagePreviewURL",
					styleBookEntry.getImagePreviewURL(themeDisplay)
				).put(
					"name", styleBookEntry.getName()
				).put(
					"styleBookEntryId", styleBookEntry.getStyleBookEntryId()
				).build());
		}

		return styleBooks;
	}

	private String[] _getThemeColorsCssClasses() {
		Theme theme = themeDisplay.getTheme();

		String colorPalette = theme.getSetting("color-palette");

		if (Validator.isNotNull(colorPalette)) {
			return StringUtil.split(colorPalette);
		}

		return new String[] {
			"primary", "success", "danger", "warning", "info", "dark",
			"gray-dark", "secondary", "light", "lighter", "white"
		};
	}

	private ItemSelectorCriterion _getURLItemSelectorCriterion() {
		if (_urlItemSelectorCriterion != null) {
			return _urlItemSelectorCriterion;
		}

		ItemSelectorCriterion itemSelectorCriterion =
			new URLItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		_urlItemSelectorCriterion = itemSelectorCriterion;

		return _urlItemSelectorCriterion;
	}

	private String _getVideoItemSelectorURL() {
		VideoItemSelectorCriterion videoItemSelectorCriterion =
			new VideoItemSelectorCriterion();

		videoItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new VideoEmbeddableHTMLItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				renderResponse.getNamespace() + "selectVideo",
				videoItemSelectorCriterion));
	}

	private boolean _hasPermissions(String actionId) {
		try {
			if (_layoutPermission.contains(
					themeDisplay.getPermissionChecker(), themeDisplay.getPlid(),
					actionId)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private boolean _isConversionDraft() {
		Layout publishedLayout = _getPublishedLayout();

		if ((publishedLayout != null) &&
			Objects.equals(
				publishedLayout.getType(), LayoutConstants.TYPE_PORTLET)) {

			return true;
		}

		return false;
	}

	private boolean _isMasterUsed() {
		if (_getLayoutType() !=
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

			return false;
		}

		Layout draftLayout = themeDisplay.getLayout();

		int masterUsagesCount = _layoutLocalService.getMasterLayoutsCount(
			themeDisplay.getScopeGroupId(), draftLayout.getClassPK());

		if (masterUsagesCount > 0) {
			return true;
		}

		return false;
	}

	private boolean _isSegmentationEnabled() {
		try {
			return _segmentsConfigurationProvider.isSegmentationEnabled(
				themeDisplay.getCompanyId());
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}

			return false;
		}
	}

	private boolean _isSegmentsExperimentVariant() {
		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.fetchSegmentsExperience(
				getSegmentsExperienceId());

		if ((segmentsExperience != null) && !segmentsExperience.isActive()) {
			List<SegmentsExperimentRel> segmentsExperimentRels =
				_segmentsExperimentRelLocalService.
					getSegmentsExperimentRelsBySegmentsExperienceKey(
						segmentsExperience.getSegmentsExperienceKey(),
						themeDisplay.getPlid());

			if (ListUtil.isNotEmpty(segmentsExperimentRels)) {
				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentPageEditorDisplayContext.class);

	private final ContentManager _contentManager;
	private final List<ContentPageEditorSidebarPanel>
		_contentPageEditorSidebarPanels;
	private Map<String, Object> _defaultConfigurations;
	private StyleBookEntry _defaultMasterStyleBookEntry;
	private StyleBookEntry _defaultStyleBookEntry;
	private final FragmentCollectionManager _fragmentCollectionManager;
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final FragmentEntryLinkManager _fragmentEntryLinkManager;
	private Map<String, Object> _fragmentEntryLinks;
	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;
	private Long _groupId;
	private ItemSelectorCriterion _imageItemSelectorCriterion;
	private final ItemSelector _itemSelector;
	private final JSONFactory _jsonFactory;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryService
		_layoutPageTemplateEntryService;
	private final LayoutPermission _layoutPermission;
	private final LayoutSetLocalService _layoutSetLocalService;
	private LayoutStructure _layoutStructure;
	private Integer _layoutType;
	private LayoutStructure _masterLayoutStructure;
	private final PageEditorConfiguration _pageEditorConfiguration;
	private final PortletResourcePermission _portletResourcePermission;
	private final PortletURLFactory _portletURLFactory;
	private Layout _publishedLayout;
	private String _redirect;
	private List<String> _restrictedItemIds;
	private final SegmentsConfigurationProvider _segmentsConfigurationProvider;
	private final SegmentsEntryService _segmentsEntryService;
	private Long _segmentsExperienceId;
	private final SegmentsExperienceManager _segmentsExperienceManager;
	private final SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;
	private List<Map<String, Object>> _sidebarPanels;
	private final Staging _staging;
	private final StyleBookEntryLocalService _styleBookEntryLocalService;
	private ItemSelectorCriterion _urlItemSelectorCriterion;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context;

import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorCriterion;
import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorReturnType;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.fragment.util.comparator.FragmentCollectionContributorNameComparator;
import com.liferay.fragment.util.comparator.FragmentCollectionCreateDateComparator;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.item.selector.LayoutItemSelectorCriterion;
import com.liferay.layout.item.selector.LayoutItemSelectorReturnType;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateEntryItemSelectorCriterion;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateEntryItemSelectorReturnType;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateEntryModifiedDateComparator;
import com.liferay.layout.util.comparator.LayoutModifiedDateComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;
import com.liferay.style.book.util.StyleBookUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class EditStyleBookEntryDisplayContext {

	public EditStyleBookEntryDisplayContext(
		FragmentCollectionContributorRegistry
			fragmentCollectionContributorRegistry,
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		HttpServletRequest httpServletRequest, ItemSelector itemSelector,
		RenderResponse renderResponse) {

		_fragmentCollectionContributorRegistry =
			fragmentCollectionContributorRegistry;
		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;
		_httpServletRequest = httpServletRequest;
		_itemSelector = itemSelector;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_setViewAttributes();
	}

	public Map<String, Object> getStyleBookEditorData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"fragmentCollectionPreviewURL",
			ResourceURLBuilder.createResourceURL(
				_renderResponse
			).setParameter(
				"styleBookEntryThemeId", _styleBookEntry.getThemeId()
			).setResourceID(
				"/style_book/preview_fragment_collection"
			).buildString()
		).put(
			"frontendTokenDefinition", _getFrontendTokenDefinitionJSONObject()
		).put(
			"frontendTokensValues",
			() -> {
				StyleBookEntry styleBookEntry = _getStyleBookEntry();

				return JSONFactoryUtil.createJSONObject(
					styleBookEntry.getFrontendTokensValues());
			}
		).put(
			"isPrivateLayoutsEnabled",
			() -> {
				Group group = _themeDisplay.getScopeGroup();

				return group.isPrivateLayoutsEnabled();
			}
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"previewOptions",
			JSONUtil.putAll(
				JSONUtil.put(
					"data",
					_getOptionJSONObject(
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE)
				).put(
					"type", "displayPageTemplate"
				),
				JSONUtil.put(
					"data", _getFragmentCollectionOptionJSONObject()
				).put(
					"type", "fragmentCollection"
				),
				JSONUtil.put(
					"data",
					_getOptionJSONObject(
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)
				).put(
					"type", "master"
				),
				JSONUtil.put(
					"data", _getPageOptionJSONObject()
				).put(
					"type", "page"
				),
				JSONUtil.put(
					"data",
					_getOptionJSONObject(
						LayoutPageTemplateEntryTypeConstants.BASIC,
						LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)
				).put(
					"type", "pageTemplate"
				))
		).put(
			"publishURL", _getActionURL("/style_book/publish_style_book_entry")
		).put(
			"redirectURL", _getRedirect()
		).put(
			"saveDraftURL", _getActionURL("/style_book/edit_style_book_entry")
		).put(
			"styleBookEntryId", _getStyleBookEntryId()
		).put(
			"themeName", _getThemeName()
		).build();
	}

	private String _getActionURL(String actionName) {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			actionName
		).buildString();
	}

	private String _getFragmentCollectionItemSelectorURL() {
		FragmentCollectionItemSelectorCriterion
			fragmentCollectionItemSelectorCriterion =
				new FragmentCollectionItemSelectorCriterion();

		fragmentCollectionItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				new FragmentCollectionItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "selectPreviewItem",
				fragmentCollectionItemSelectorCriterion));
	}

	private JSONObject _getFragmentCollectionOptionJSONObject() {
		int fragmentCollectionsCount = _getFragmentCollectionsCount();

		return JSONUtil.put(
			"itemSelectorURL", _getFragmentCollectionItemSelectorURL()
		).put(
			"recentLayouts",
			() -> {
				List<FragmentCollection> fragmentCollections =
					FragmentCollectionServiceUtil.getFragmentCollections(
						new long[] {
							_themeDisplay.getSiteGroupId(),
							_themeDisplay.getCompanyGroupId()
						},
						0, Math.min(fragmentCollectionsCount, 4),
						FragmentCollectionCreateDateComparator.getInstance(
							false));

				JSONObject[] fragmentCollectionContributorJSONObjects =
					new JSONObject[0];

				if (fragmentCollections.size() < 4) {
					List<FragmentCollectionContributor>
						fragmentCollectionContributors =
							_fragmentCollectionContributorRegistry.
								getFragmentCollectionContributors();

					Collections.sort(
						fragmentCollectionContributors,
						new FragmentCollectionContributorNameComparator(
							_themeDisplay.getLocale()));

					List<FragmentCollectionContributor>
						filteredFragmentCollectionContributors =
							ListUtil.subList(
								fragmentCollectionContributors, 0,
								4 - fragmentCollections.size());

					fragmentCollectionContributorJSONObjects =
						TransformUtil.transformToArray(
							filteredFragmentCollectionContributors,
							fragmentCollectionContributor -> JSONUtil.put(
								"name", fragmentCollectionContributor.getName()
							).put(
								"url",
								_getFragmentCollectionPreviewURL(
									fragmentCollectionContributor.
										getFragmentCollectionKey(),
									CompanyConstants.SYSTEM)
							),
							JSONObject.class);
				}

				return JSONUtil.putAll(
					ArrayUtil.append(
						(JSONObject[])TransformUtil.transformToArray(
							fragmentCollections,
							fragmentCollection -> JSONUtil.put(
								"name", fragmentCollection.getName()
							).put(
								"url",
								_getFragmentCollectionPreviewURL(
									fragmentCollection.
										getFragmentCollectionKey(),
									fragmentCollection.getGroupId())
							),
							JSONObject.class),
						fragmentCollectionContributorJSONObjects));
			}
		).put(
			"totalLayouts", fragmentCollectionsCount
		);
	}

	private String _getFragmentCollectionPreviewURL(
		String fragmentCollectionKey, long groupId) {

		return ResourceURLBuilder.createResourceURL(
			_renderResponse
		).setParameter(
			"fragmentCollectionKey", fragmentCollectionKey
		).setParameter(
			"groupId", groupId
		).setParameter(
			"styleBookEntryThemeId", _styleBookEntry.getThemeId()
		).setResourceID(
			"/style_book/preview_fragment_collection"
		).buildString();
	}

	private int _getFragmentCollectionsCount() {
		int fragmentCollectionsCount =
			FragmentCollectionServiceUtil.getFragmentCollectionsCount(
				new long[] {
					_themeDisplay.getSiteGroupId(),
					_themeDisplay.getCompanyGroupId()
				});

		if (_fragmentCollectionContributorRegistry == null) {
			return fragmentCollectionsCount;
		}

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			_fragmentCollectionContributorRegistry.
				getFragmentCollectionContributors();

		return fragmentCollectionsCount + fragmentCollectionContributors.size();
	}

	private JSONObject _getFrontendTokenDefinitionJSONObject()
		throws Exception {

		FrontendTokenDefinition frontendTokenDefinition = null;

		if (FeatureFlagManagerUtil.isEnabled("LPD-30204")) {
			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					_themeDisplay.getCompanyId(), _styleBookEntry.getThemeId());
		}
		else {
			Group group = _themeDisplay.getScopeGroup();

			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					LayoutSetLocalServiceUtil.fetchLayoutSet(
						_themeDisplay.getSiteGroupId(),
						group.isLayoutSetPrototype()));
		}

		if (frontendTokenDefinition != null) {
			return frontendTokenDefinition.getJSONObject(
				_themeDisplay.getLocale());
		}

		return JSONFactoryUtil.createJSONObject();
	}

	private JSONObject _getOptionJSONObject(int... layoutTypes) {
		int total =
			LayoutPageTemplateEntryServiceUtil.
				getLayoutPageTemplateEntriesCount(
					_getPreviewItemsGroupId(), layoutTypes,
					WorkflowConstants.STATUS_APPROVED);

		return JSONUtil.put(
			"itemSelectorURL",
			() -> {
				LayoutPageTemplateEntryItemSelectorCriterion
					layoutPageTemplateEntryItemSelectorCriterion =
						new LayoutPageTemplateEntryItemSelectorCriterion();

				layoutPageTemplateEntryItemSelectorCriterion.
					setDesiredItemSelectorReturnTypes(
						new LayoutPageTemplateEntryItemSelectorReturnType());
				layoutPageTemplateEntryItemSelectorCriterion.setGroupId(
					_getPreviewItemsGroupId());
				layoutPageTemplateEntryItemSelectorCriterion.setLayoutTypes(
					layoutTypes);

				PortletURL entryItemSelectorURL =
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						_renderResponse.getNamespace() + "selectPreviewItem",
						layoutPageTemplateEntryItemSelectorCriterion);

				return entryItemSelectorURL.toString();
			}
		).put(
			"recentLayouts",
			() -> JSONUtil.putAll(
				(JSONObject[])TransformUtil.transformToArray(
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntries(
							_getPreviewItemsGroupId(), layoutTypes,
							WorkflowConstants.STATUS_APPROVED, 0,
							Math.min(total, 4),
							LayoutPageTemplateEntryModifiedDateComparator.
								getInstance(false)),
					layoutPageTemplateEntry -> JSONUtil.put(
						"name", layoutPageTemplateEntry.getName()
					).put(
						"private", false
					).put(
						"url", _getPreviewURL(layoutPageTemplateEntry)
					),
					JSONObject.class))
		).put(
			"totalLayouts", total
		);
	}

	private JSONObject _getPageOptionJSONObject() {
		int total = LayoutLocalServiceUtil.getPublishedLayoutsCount(
			_getPreviewItemsGroupId());

		return JSONUtil.put(
			"itemSelectorURL",
			() -> {
				LayoutItemSelectorCriterion layoutItemSelectorCriterion =
					new LayoutItemSelectorCriterion();

				layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new LayoutItemSelectorReturnType());

				Group group = _themeDisplay.getScopeGroup();

				layoutItemSelectorCriterion.setShowPrivatePages(
					group.isPrivateLayoutsEnabled());

				return String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						_renderResponse.getNamespace() + "selectPreviewItem",
						layoutItemSelectorCriterion));
			}
		).put(
			"recentLayouts",
			() -> {
				List<Layout> layouts =
					LayoutLocalServiceUtil.getPublishedLayouts(
						_getPreviewItemsGroupId(), 0, Math.min(total, 4),
						LayoutModifiedDateComparator.getInstance(false));

				return JSONUtil.putAll(
					(JSONObject[])TransformUtil.transformToArray(
						layouts,
						layout -> JSONUtil.put(
							"hasGuestViewPermission",
							() -> {
								Role role = RoleLocalServiceUtil.getRole(
									layout.getCompanyId(), RoleConstants.GUEST);

								return ResourcePermissionLocalServiceUtil.
									hasResourcePermission(
										layout.getCompanyId(),
										Layout.class.getName(),
										ResourceConstants.SCOPE_INDIVIDUAL,
										String.valueOf(layout.getPlid()),
										role.getRoleId(), ActionKeys.VIEW);
							}
						).put(
							"name", layout.getName(_themeDisplay.getLocale())
						).put(
							"private", layout.isPrivateLayout()
						).put(
							"url", _getPreviewURL(layout)
						),
						JSONObject.class));
			}
		).put(
			"totalLayouts", total
		);
	}

	private long _getPreviewItemsGroupId() {
		if (_previewItemsGroupId != null) {
			return _previewItemsGroupId;
		}

		Layout layout = _themeDisplay.getLayout();

		_previewItemsGroupId = layout.getGroupId();

		return _previewItemsGroupId;
	}

	private String _getPreviewURL(Layout layout) {
		try {
			String layoutURL = HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(layout, _themeDisplay), "p_l_mode",
				Constants.PREVIEW, "p_p_auth",
				AuthTokenUtil.getToken(_httpServletRequest),
				"styleBookEntryPreview", true);

			if (Validator.isNotNull(_themeDisplay.getDoAsUserId())) {
				layoutURL = PortalUtil.addPreservedParameters(
					_themeDisplay, layoutURL, false, true);
			}

			return layoutURL;
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private String _getPreviewURL(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		try {
			if (layoutPageTemplateEntry.getType() ==
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

				String previewURL = HttpComponentsUtil.addParameters(
					_themeDisplay.getPortalURL() + _themeDisplay.getPathMain() +
						"/portal/get_page_preview",
					"p_l_mode", Constants.PREVIEW, "segmentsExperienceId",
					SegmentsExperienceLocalServiceUtil.
						fetchDefaultSegmentsExperienceId(
							layoutPageTemplateEntry.getPlid()),
					"selPlid", layoutPageTemplateEntry.getPlid(),
					"styleBookEntryPreview", true);

				if (Validator.isNotNull(_themeDisplay.getDoAsUserId())) {
					previewURL = PortalUtil.addPreservedParameters(
						_themeDisplay, previewURL, false, true);
				}

				return previewURL;
			}

			return _getPreviewURL(
				LayoutLocalServiceUtil.getLayout(
					layoutPageTemplateEntry.getPlid()));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private String _getRedirect() {
		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).buildString();
	}

	private StyleBookEntry _getStyleBookEntry() {
		if (_styleBookEntry != null) {
			return _styleBookEntry;
		}

		_styleBookEntry = StyleBookEntryLocalServiceUtil.fetchStyleBookEntry(
			_getStyleBookEntryId());

		if (_styleBookEntry.isHead()) {
			StyleBookEntry draftStyleBookEntry =
				StyleBookEntryLocalServiceUtil.fetchDraft(_styleBookEntry);

			if (draftStyleBookEntry != null) {
				_styleBookEntry = draftStyleBookEntry;
			}
		}

		return _styleBookEntry;
	}

	private long _getStyleBookEntryId() {
		if (_styleBookEntryId != null) {
			return _styleBookEntryId;
		}

		_styleBookEntryId = ParamUtil.getLong(
			_httpServletRequest, "styleBookEntryId");

		return _styleBookEntryId;
	}

	private String _getStyleBookEntryTitle() {
		StyleBookEntry styleBookEntry = _getStyleBookEntry();

		return styleBookEntry.getName();
	}

	private String _getThemeName() {
		if (FeatureFlagManagerUtil.isEnabled("LPD-30204")) {
			return StyleBookUtil.getThemeName(
				_styleBookEntry.getCompanyId(), _themeDisplay.getLocale(),
				_styleBookEntry.getThemeId());
		}

		Group group = _themeDisplay.getScopeGroup();

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.fetchLayoutSet(
			_themeDisplay.getSiteGroupId(), group.isLayoutSetPrototype());

		Theme theme = layoutSet.getTheme();

		return theme.getName();
	}

	private void _setViewAttributes() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(_getRedirect());
		portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

		_renderResponse.setTitle(_getStyleBookEntryTitle());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditStyleBookEntryDisplayContext.class.getName());

	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;
	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private Long _previewItemsGroupId;
	private final RenderResponse _renderResponse;
	private StyleBookEntry _styleBookEntry;
	private Long _styleBookEntryId;
	private final ThemeDisplay _themeDisplay;

}
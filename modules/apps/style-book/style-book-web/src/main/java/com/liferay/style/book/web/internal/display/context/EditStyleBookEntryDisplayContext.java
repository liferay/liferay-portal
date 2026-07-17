/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalServiceUtil;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
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
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

		_updatePortletDisplay();
	}

	public Map<String, Object> getStyleBookEditorData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"defaultTokenDefinitionPriority",
			FrontendTokenDefinitionConstants.PRIORITY_LEGACY
		).put(
			"fragmentCollectionPreviewURL",
			ResourceURLBuilder.createResourceURL(
				_renderResponse
			).setParameter(
				"styleBookEntryThemeId", _styleBookEntry.getThemeId()
			).setResourceID(
				"/style_book/preview_fragment_collection"
			).buildString()
		).put(
			"frontendTokenDefinitions",
			_getFrontendTokenDefinitionsJSONObjects()
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
			"themeFrontendTokenDefinitionId", _styleBookEntry.getThemeId()
		).put(
			"themeName",
			StyleBookUtil.getThemeName(
				_styleBookEntry.getCompanyId(), _themeDisplay.getLocale(),
				_styleBookEntry.getThemeId())
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

	private List<JSONObject> _getFrontendTokenDefinitionsJSONObjects()
		throws Exception {

		List<FrontendTokenDefinition> frontendTokenDefinitions = ListUtil.sort(
			ListUtil.filter(
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinitions(
					_themeDisplay.getCompanyId()),
				frontendTokenDefinition ->
					Objects.equals(
						frontendTokenDefinition.getThemeId(),
						_styleBookEntry.getThemeId()) ||
					Objects.equals(
						frontendTokenDefinition.getThemeType(),
						FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL)),
			(frontendTokenDefinition1, frontendTokenDefinition2) ->
				Integer.compare(
					frontendTokenDefinition2.getPriority(),
					frontendTokenDefinition1.getPriority()));

		return TransformUtil.transform(
			frontendTokenDefinitions,
			frontendTokenDefinition -> {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				JSONObject frontendTokenDefinitionJSONObject =
					frontendTokenDefinition.getJSONObject(
						_themeDisplay.getLocale());

				for (String key : frontendTokenDefinitionJSONObject.keySet()) {
					jsonObject.put(
						key, frontendTokenDefinitionJSONObject.get(key));
				}

				jsonObject.put(
					"id", frontendTokenDefinition.getThemeId()
				).put(
					"name",
					frontendTokenDefinition.getThemeName(
						_themeDisplay.getLocale())
				).put(
					"priority", frontendTokenDefinition.getPriority()
				);

				return jsonObject;
			});
	}

	private String _getName(Group entryGroup, Layout layout) {
		String layoutName = layout.getName(_themeDisplay.getLocale());

		if ((entryGroup == null) || !entryGroup.isDepot()) {
			return layoutName;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(layout.getGroupId());

		if (group == null) {
			return layoutName;
		}

		try {
			return StringBundler.concat(
				"[", group.getDescriptiveName(_themeDisplay.getLocale()), "] ",
				layoutName);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return layoutName;
		}
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
		long[] previewLayoutGroupIds = _getPreviewLayoutGroupIds();

		int total = 0;

		for (long groupId : previewLayoutGroupIds) {
			total += LayoutLocalServiceUtil.getPublishedLayoutsCount(groupId);
		}

		Group entryGroup = _getStyleBookEntryGroup();
		int finalTotal = total;

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
				List<Layout> layouts = new ArrayList<>();

				for (long groupId : previewLayoutGroupIds) {
					layouts.addAll(
						LayoutLocalServiceUtil.getPublishedLayouts(
							groupId, 0,
							LayoutLocalServiceUtil.getPublishedLayoutsCount(
								groupId),
							LayoutModifiedDateComparator.getInstance(false)));
				}

				layouts.sort(LayoutModifiedDateComparator.getInstance(false));

				return JSONUtil.putAll(
					(JSONObject[])TransformUtil.transformToArray(
						layouts.subList(0, Math.min(finalTotal, 4)),
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
							"name", _getName(entryGroup, layout)
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

	private long[] _getPreviewLayoutGroupIds() {
		Group entryGroup = _getStyleBookEntryGroup();

		if ((entryGroup == null) || !entryGroup.isDepot()) {
			return new long[] {_getPreviewItemsGroupId()};
		}

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
			entryGroup.getGroupId());

		if (depotEntry == null) {
			return new long[] {entryGroup.getGroupId()};
		}

		List<DepotEntryGroupRel> depotEntryGroupRels =
			DepotEntryGroupRelLocalServiceUtil.getDepotEntryGroupRels(
				depotEntry);

		if (depotEntryGroupRels.isEmpty()) {
			return new long[] {entryGroup.getGroupId()};
		}

		long[] groupIds = new long[depotEntryGroupRels.size()];

		for (int i = 0; i < depotEntryGroupRels.size(); i++) {
			DepotEntryGroupRel depotEntryGroupRel = depotEntryGroupRels.get(i);

			groupIds[i] = depotEntryGroupRel.getToGroupId();
		}

		return groupIds;
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

	private Group _getStyleBookEntryGroup() {
		return GroupLocalServiceUtil.fetchGroup(
			_getStyleBookEntry().getGroupId());
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

	private void _updatePortletDisplay() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(_getRedirect());

		String backURLTitle = ParamUtil.getString(
			_httpServletRequest, "backURLTitle");

		portletDisplay.setURLBackTitle(
			Validator.isNotNull(backURLTitle) ? backURLTitle :
				portletDisplay.getPortletDisplayName());

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
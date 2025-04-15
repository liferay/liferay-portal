/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.util;

import com.liferay.application.list.GroupProvider;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.layout.admin.web.internal.action.provider.LayoutActionProvider;
import com.liferay.layout.admin.web.internal.helper.LayoutActionsHelper;
import com.liferay.layout.security.permission.resource.LayoutContentModelResourcePermission;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.layout.util.LayoutsTree;
import com.liferay.layout.util.template.LayoutConverterRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.portal.util.PropsValues;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.translation.security.permission.TranslationPermission;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Marcellus Tavares
 * @author Zsolt Szabó
 * @author Tibor Lipusz
 */
@Component(service = LayoutsTree.class)
public class LayoutsTreeImpl implements LayoutsTree {

	@Override
	public JSONArray getLayoutsJSONArray(
			Set<Long> expandedLayoutIds, long groupId,
			HttpServletRequest httpServletRequest, boolean includeActions,
			boolean incomplete, boolean loadMore, long parentLayoutId,
			boolean privateLayout, String treeId)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String key = StringBundler.concat(
			treeId, StringPool.COLON, groupId, StringPool.COLON, privateLayout,
			":Pagination");

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			_layoutConverterRegistry, themeDisplay, _translationPermission);

		String paginationJSON = SessionClicks.get(
			httpServletRequest.getSession(), key, _jsonFactory.getNullJSON());

		JSONObject paginationJSONObject = _jsonFactory.createJSONObject(
			paginationJSON);

		JSONArray jsonArray = _getLayoutsJSONArray(
			_getAncestorLayouts(httpServletRequest), false,
			_getDuplicatedFriendlyURLPlids(groupId, privateLayout),
			expandedLayoutIds, groupId, httpServletRequest, includeActions,
			incomplete, layoutActionsHelper, loadMore,
			_isPaginationEnabled(httpServletRequest), paginationJSONObject,
			parentLayoutId, privateLayout, themeDisplay);

		if (loadMore) {
			SessionClicks.put(
				httpServletRequest.getSession(), key,
				paginationJSONObject.toString());
		}

		return jsonArray;
	}

	private Layout _fetchCurrentLayout(HttpServletRequest httpServletRequest) {
		long selPlid = ParamUtil.getLong(httpServletRequest, "selPlid");

		if (selPlid > 0) {
			return _layoutLocalService.fetchLayout(selPlid);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypeControlPanel()) {
			return layout;
		}

		return null;
	}

	private List<Layout> _getAncestorLayouts(
			HttpServletRequest httpServletRequest)
		throws Exception {

		Layout layout = _fetchCurrentLayout(httpServletRequest);

		if (layout == null) {
			return Collections.emptyList();
		}

		List<Layout> ancestorLayouts = _layoutService.getAncestorLayouts(
			layout.getPlid());

		ancestorLayouts.add(layout);

		return ancestorLayouts;
	}

	private Layout _getDraftLayout(Layout layout) {
		if (!layout.isTypeContent()) {
			return null;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return null;
		}

		if (draftLayout.isDraft() || !layout.isPublished()) {
			return draftLayout;
		}

		return null;
	}

	private List<Long> _getDuplicatedFriendlyURLPlids(
			long groupId, boolean privateLayout)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-174417")) {
			return Collections.emptyList();
		}

		LayoutSet layoutSet = _layoutSetLocalService.fetchLayoutSet(
			groupId, privateLayout);

		if (layoutSet.isLayoutSetPrototypeLinkEnabled()) {
			return _layoutSetPrototypeHelper.getDuplicatedFriendlyURLPlids(
				layoutSet);
		}

		Group group = layoutSet.getGroup();

		if (group.isLayoutSetPrototype()) {
			return _layoutSetPrototypeHelper.getDuplicatedFriendlyURLPlids(
				_layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
					group.getClassPK()));
		}

		return Collections.emptyList();
	}

	private JSONArray _getLayoutsJSONArray(
			List<Layout> ancestorLayouts, boolean childLayout,
			List<Long> duplicatedFriendlyURLPlids, Set<Long> expandedLayoutIds,
			long groupId, HttpServletRequest httpServletRequest,
			boolean includeActions, boolean incomplete,
			LayoutActionsHelper layoutActionsHelper, boolean loadMore,
			boolean paginationEnabled, JSONObject paginationJSONObject,
			long parentLayoutId, boolean privateLayout,
			ThemeDisplay themeDisplay)
		throws Exception {

		int count = _layoutService.getLayoutsCount(
			groupId, privateLayout, parentLayoutId);

		if (count <= 0) {
			return _jsonFactory.createJSONArray();
		}

		JSONArray layoutsJSONArray = _jsonFactory.createJSONArray();

		List<Layout> layouts = _getPaginatedLayouts(
			httpServletRequest, groupId, paginationEnabled,
			paginationJSONObject, privateLayout, parentLayoutId, loadMore,
			incomplete, childLayout, count,
			_layoutLocalService.getLayoutsCount(
				groupId, privateLayout, parentLayoutId));

		Layout afterDeleteSelectedLayout = null;
		Layout secondLayout = null;

		int index = 0;

		for (Layout layout : layouts) {
			if (index == 1) {
				secondLayout = layout;

				break;
			}

			index++;
		}

		for (Layout layout : layouts) {
			JSONArray childLayoutsJSONArray = null;

			if (ancestorLayouts.contains(layout) ||
				(SetUtil.isNotEmpty(expandedLayoutIds) &&
				 expandedLayoutIds.contains(layout.getLayoutId()))) {

				if (layout instanceof VirtualLayout) {
					VirtualLayout virtualLayout = (VirtualLayout)layout;

					childLayoutsJSONArray = _getLayoutsJSONArray(
						ancestorLayouts, true, duplicatedFriendlyURLPlids,
						expandedLayoutIds, virtualLayout.getSourceGroupId(),
						httpServletRequest, includeActions, incomplete,
						layoutActionsHelper, loadMore, paginationEnabled,
						paginationJSONObject, virtualLayout.getLayoutId(),
						virtualLayout.isPrivateLayout(), themeDisplay);
				}
				else {
					childLayoutsJSONArray = _getLayoutsJSONArray(
						ancestorLayouts, true, duplicatedFriendlyURLPlids,
						expandedLayoutIds, groupId, httpServletRequest,
						includeActions, incomplete, layoutActionsHelper,
						loadMore, paginationEnabled, paginationJSONObject,
						layout.getLayoutId(), layout.isPrivateLayout(),
						themeDisplay);
				}
			}
			else {
				childLayoutsJSONArray = _jsonFactory.createJSONArray();
			}

			if (includeActions) {
				if ((afterDeleteSelectedLayout == null) &&
					(layout.getParentLayoutId() !=
						LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)) {

					afterDeleteSelectedLayout = _layoutLocalService.fetchLayout(
						layout.getParentPlid());
				}

				if (afterDeleteSelectedLayout == null) {
					afterDeleteSelectedLayout = secondLayout;
				}
			}

			layoutsJSONArray.put(
				_toJSONObject(
					afterDeleteSelectedLayout,
					_layoutService.getLayoutsCount(
						groupId, privateLayout, layout.getLayoutId()),
					childLayoutsJSONArray, duplicatedFriendlyURLPlids,
					httpServletRequest, includeActions, layout,
					layoutActionsHelper, themeDisplay));

			if (includeActions) {
				afterDeleteSelectedLayout = layout;
			}
		}

		return layoutsJSONArray;
	}

	private List<Layout> _getPaginatedLayouts(
			HttpServletRequest httpServletRequest, long groupId,
			boolean paginationEnabled, JSONObject paginationJSONObject,
			boolean privateLayout, long parentLayoutId, boolean loadMore,
			boolean incomplete, boolean childLayout, int count, int totalCount)
		throws Exception {

		if (!paginationEnabled) {
			return _layoutService.getLayouts(
				groupId, privateLayout, parentLayoutId, incomplete,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		int loadedLayoutsCount = paginationJSONObject.getInt(
			String.valueOf(parentLayoutId), 0);

		int start = ParamUtil.getInteger(httpServletRequest, "start");

		start = Math.max(0, Math.min(start, count));

		int end = ParamUtil.getInteger(
			httpServletRequest, "end",
			start + PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN);

		if (loadedLayoutsCount > end) {
			end = loadedLayoutsCount;
		}

		if (loadMore) {
			paginationJSONObject.put(String.valueOf(parentLayoutId), end);
		}

		end = Math.max(start, Math.min(end, count));

		if (childLayout &&
			(count > PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN) &&
			(start == PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN)) {

			start = end;
		}

		if (count != totalCount) {
			List<Layout> layouts = _layoutService.getLayouts(
				groupId, privateLayout, parentLayoutId, incomplete,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			return layouts.subList(start, end);
		}

		return _layoutService.getLayouts(
			groupId, privateLayout, parentLayoutId, incomplete, start, end);
	}

	private boolean _isPaginationEnabled(
		HttpServletRequest httpServletRequest) {

		boolean paginate = ParamUtil.getBoolean(
			httpServletRequest, "paginate", true);

		if (paginate &&
			(PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN > 0)) {

			return true;
		}

		return false;
	}

	private JSONObject _toJSONObject(
			Layout afterDeleteSelectedLayout, long childLayoutsCount,
			JSONArray childLayoutsJSONArray,
			List<Long> duplicatedFriendlyURLPlids,
			HttpServletRequest httpServletRequest, boolean includeActions,
			Layout layout, LayoutActionsHelper layoutActionsHelper,
			ThemeDisplay themeDisplay)
		throws Exception {

		boolean hasUpdatePermission = true;

		if (includeActions) {
			hasUpdatePermission =
				_layoutPermission.containsLayoutUpdatePermission(
					themeDisplay.getPermissionChecker(), layout);
		}

		boolean finalHasUpdatePermission = hasUpdatePermission;

		String layoutName = layout.getName(themeDisplay.getLocale());

		if (includeActions && (_getDraftLayout(layout) != null) &&
			(finalHasUpdatePermission || !layout.isPublished() ||
			 _layoutContentModelResourcePermission.contains(
				 themeDisplay.getPermissionChecker(), layout.getPlid(),
				 ActionKeys.UPDATE))) {

			layoutName += StringPool.STAR;
		}

		LayoutType layoutType = layout.getLayoutType();

		JSONObject jsonObject = JSONUtil.put(
			"actions",
			() -> {
				if (!includeActions) {
					return null;
				}

				LayoutActionProvider layoutActionProvider =
					new LayoutActionProvider(
						_groupProvider, httpServletRequest, _language,
						layoutActionsHelper, _siteNavigationMenuLocalService);

				return layoutActionProvider.getActionsJSONArray(
					layout, afterDeleteSelectedLayout);
			}
		).put(
			"children",
			() -> {
				if (childLayoutsJSONArray.length() > 0) {
					return childLayoutsJSONArray;
				}

				return null;
			}
		).put(
			"firstPageable", layoutType.isFirstPageable()
		).put(
			"groupId",
			() -> {
				if (layout instanceof VirtualLayout) {
					VirtualLayout virtualLayout = (VirtualLayout)layout;

					return virtualLayout.getSourceGroupId();
				}

				return layout.getGroupId();
			}
		).put(
			"hasChildren", layout.hasChildren()
		).put(
			"hasDuplicatedFriendlyURL",
			duplicatedFriendlyURLPlids.contains(layout.getPlid())
		).put(
			"hasGuestViewPermission",
			() -> {
				Role role = _roleLocalService.getRole(
					layout.getCompanyId(), RoleConstants.GUEST);

				return _resourcePermissionLocalService.hasResourcePermission(
					layout.getCompanyId(), Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(layout.getPlid()), role.getRoleId(),
					ActionKeys.VIEW);
			}
		).put(
			"icon", layout.getIcon()
		).put(
			"id", layout.getLayoutId()
		).put(
			"layoutId", layout.getLayoutId()
		).put(
			"name", layoutName
		).put(
			"paginated",
			() -> {
				if (childLayoutsCount != childLayoutsJSONArray.length()) {
					return true;
				}

				return null;
			}
		).put(
			"parentable", layoutType.isParentable()
		).put(
			"plid", layout.getPlid()
		).put(
			"priority", layout.getPriority()
		).put(
			"privateLayout", layout.isPrivateLayout()
		).put(
			"regularURL",
			() -> {
				if (includeActions &&
					(finalHasUpdatePermission || layout.isPublished())) {

					return layout.getRegularURL(httpServletRequest);
				}

				return StringPool.BLANK;
			}
		).put(
			"target",
			() -> {
				if (includeActions &&
					(finalHasUpdatePermission || layout.isPublished())) {

					return GetterUtil.getString(
						HtmlUtil.escape(
							layout.getTypeSettingsProperty("target")),
						"_self");
				}

				return StringPool.BLANK;
			}
		).put(
			"title", HtmlUtil.escapeAttribute(layoutName)
		).put(
			"type", layout.getType()
		).put(
			"typeName",
			() -> {
				LayoutTypeController layoutTypeController =
					LayoutTypeControllerTracker.getLayoutTypeController(
						layout.getType());

				ResourceBundle layoutTypeResourceBundle =
					ResourceBundleUtil.getBundle(
						"content.Language", themeDisplay.getLocale(),
						layoutTypeController.getClass());

				return _language.get(
					layoutTypeResourceBundle,
					"layout.types." + layout.getType());
			}
		);

		LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
			layout);

		if (layoutRevision != null) {
			if (_staging.isIncomplete(
					layout, layoutRevision.getLayoutSetBranchId())) {

				jsonObject.put("incomplete", true);
			}

			LayoutBranch layoutBranch = layoutRevision.getLayoutBranch();

			if (!layoutBranch.isMaster()) {
				jsonObject.put("layoutBranchName", layoutBranch.getName());
			}

			if (layoutRevision.isHead()) {
				jsonObject.put("layoutRevisionHead", true);
			}

			jsonObject.put(
				"layoutRevisionId", layoutRevision.getLayoutRevisionId());
		}

		return jsonObject;
	}

	@Reference
	private GroupProvider _groupProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutContentModelResourcePermission
		_layoutContentModelResourcePermission;

	@Reference
	private LayoutConverterRegistry _layoutConverterRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Reference
	private Staging _staging;

	@Reference
	private TranslationPermission _translationPermission;

}
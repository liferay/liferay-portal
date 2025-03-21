/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.action.provider;

import com.liferay.application.list.GroupProvider;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.helper.LayoutActionsHelper;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.ActionURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuWebKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.ActionURL;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Rubén Pulido
 */
public class LayoutActionProvider {

	public LayoutActionProvider(
		GroupProvider groupProvider, HttpServletRequest httpServletRequest,
		Language language, LayoutActionsHelper layoutActionsHelper,
		SiteNavigationMenuLocalService siteNavigationMenuLocalService) {

		_groupProvider = groupProvider;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_layoutActionsHelper = layoutActionsHelper;
		_siteNavigationMenuLocalService = siteNavigationMenuLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public JSONArray getActionsJSONArray(
			Layout layout, Layout afterDeleteSelectedLayout)
		throws Exception {

		JSONArray itemsJSONArray = JSONFactoryUtil.createJSONArray();

		if (_layoutActionsHelper.isShowPreviewDraftActions(layout)) {
			itemsJSONArray.put(() -> _getPreviewDraftActionJSONObject(layout));
		}

		boolean showAddChildPageAction =
			_layoutActionsHelper.isShowAddChildPageAction(layout);

		if (showAddChildPageAction &&
			Validator.isNotNull(_getAddLayoutURL(layout.getPlid()))) {

			itemsJSONArray.put(() -> _getAddChildPageJSONObject(layout));
		}

		Group group = layout.getGroup();

		if (_layoutActionsHelper.isShowCopyLayoutAction(layout, group)) {
			itemsJSONArray.put(
				JSONUtil.put("type", "divider")
			).put(
				() -> _getCopyPageJSONObject(layout)
			);
		}

		if (_layoutActionsHelper.isShowConfigureAction(layout)) {
			itemsJSONArray.put(
				JSONUtil.put("type", "divider")
			).put(
				() -> _getConfigureJSONObject(layout)
			);
		}

		if (_layoutActionsHelper.isShowPermissionsAction(layout, group)) {
			itemsJSONArray.put(() -> _getPermissionsJSONObject(layout));
		}

		if (_layoutActionsHelper.isShowDeleteAction(layout)) {
			itemsJSONArray.put(
				JSONUtil.put("type", "divider")
			).put(
				() -> _getDeleteJSONObject(afterDeleteSelectedLayout, layout)
			);
		}

		if (itemsJSONArray.length() <= 0) {
			return null;
		}

		return JSONUtil.putAll(
			JSONUtil.put(
				"items", itemsJSONArray
			).put(
				"type", "group"
			));
	}

	private JSONObject _getAddChildPageJSONObject(Layout layout) {
		return JSONUtil.put(
			"href", _getAddLayoutURL(layout.getPlid())
		).put(
			"id", "add-child-page"
		).put(
			"label", _language.get(_themeDisplay.getLocale(), "add-child-page")
		).put(
			"type", "item"
		);
	}

	private String _getAddLayoutURL(long plid) {
		Group scopeGroup = _themeDisplay.getScopeGroup();

		if (scopeGroup.isStaged() && !scopeGroup.isStagingGroup()) {
			return null;
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/select_layout_page_template_entry"
		).setRedirect(
			_getRedirect()
		).setBackURL(
			_getBackURL()
		).setParameter(
			"groupId", _themeDisplay.getSiteGroupId()
		).setParameter(
			"privateLayout", _isPrivateLayout()
		).setParameter(
			"selPlid", plid
		).buildString();
	}

	private String _getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		String backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		if (Validator.isNull(backURL)) {
			backURL = ParamUtil.getString(
				PortalUtil.getOriginalServletRequest(_httpServletRequest),
				"backURL", _themeDisplay.getURLCurrent());
		}

		_backURL = backURL;

		return backURL;
	}

	private String _getBackURLTitle() {
		Layout layout = _themeDisplay.getLayout();

		if (!layout.isTypeAssetDisplay()) {
			return layout.getName(_themeDisplay.getLocale());
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)
				_httpServletRequest.getAttribute(
					LayoutDisplayPageWebKeys.
						LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if (layoutDisplayPageObjectProvider != null) {
			return layoutDisplayPageObjectProvider.getTitle(
				_themeDisplay.getLocale());
		}

		AssetEntry assetEntry = (AssetEntry)_httpServletRequest.getAttribute(
			WebKeys.LAYOUT_ASSET_ENTRY);

		if (assetEntry != null) {
			return assetEntry.getTitle(_themeDisplay.getLocale());
		}

		return layout.getName(_themeDisplay.getLocale());
	}

	private JSONObject _getConfigureJSONObject(Layout layout) {
		return JSONUtil.put(
			"href", _getConfigureLayoutURL(layout.getPlid())
		).put(
			"id", "configure"
		).put(
			"label", _language.get(_themeDisplay.getLocale(), "configure")
		).put(
			"symbolLeft", "cog"
		).put(
			"type", "item"
		);
	}

	private String _getConfigureLayoutURL(long plid) {
		Layout layout = _themeDisplay.getLayout();

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).setRedirect(
			() -> {
				if (layout.isTypeAssetDisplay() ||
					layout.isTypeControlPanel()) {

					return ParamUtil.getString(
						_httpServletRequest, "redirect",
						_themeDisplay.getURLCurrent());
				}

				return PortalUtil.getLayoutFullURL(layout, _themeDisplay);
			}
		).setBackURL(
			() -> {
				if (layout.isTypeAssetDisplay() ||
					layout.isTypeControlPanel()) {

					return ParamUtil.getString(
						_httpServletRequest, "redirect",
						_themeDisplay.getURLCurrent());
				}

				return PortalUtil.getLayoutFullURL(layout, _themeDisplay);
			}
		).setParameter(
			"backURLTitle", _getBackURLTitle()
		).setParameter(
			"groupId", _themeDisplay.getScopeGroupId()
		).setParameter(
			"privateLayout", _isPrivateLayout()
		).setParameter(
			"selPlid", plid
		).buildString();
	}

	private String _getCopyLayoutRenderURL(Layout layout) {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/add_layout"
		).setRedirect(
			ParamUtil.getString(
				_httpServletRequest, "redirect", _themeDisplay.getURLCurrent())
		).setParameter(
			"privateLayout", layout.isPrivateLayout()
		).setParameter(
			"sourcePlid", layout.getPlid()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private JSONObject _getCopyPageJSONObject(Layout layout) {
		return JSONUtil.put(
			"data",
			JSONUtil.put(
				"id", "copy-page"
			).put(
				"modalTitle",
				_language.get(_themeDisplay.getLocale(), "copy-page")
			).put(
				"url", _getCopyLayoutRenderURL(layout)
			)
		).put(
			"href", StringPool.POUND
		).put(
			"id", "copy-page"
		).put(
			"label", _language.get(_themeDisplay.getLocale(), "copy-page")
		).put(
			"symbolLeft", "copy"
		).put(
			"type", "item"
		);
	}

	private JSONObject _getDeleteJSONObject(
			Layout afterDeleteSelectedLayout, Layout layout)
		throws Exception {

		return JSONUtil.put(
			"data",
			HashMapBuilder.put(
				"message",
				() -> {
					String messageKey =
						"are-you-sure-you-want-to-delete-the-page-x.-it-will-" +
							"be-removed-immediately";

					if (layout.hasChildren() && _hasScopeGroup(layout)) {
						messageKey = StringBundler.concat(
							"are-you-sure-you-want-to-delete-the-page-x.-this-",
							"page-serves-as-a-scope-for-content-and-also-",
							"contains-child-pages");
					}
					else if (layout.hasChildren()) {
						messageKey = StringBundler.concat(
							"are-you-sure-you-want-to-delete-the-page-x.-this-",
							"page-contains-child-pages-that-will-also-be-",
							"removed");
					}
					else if (_hasScopeGroup(layout)) {
						messageKey =
							"are-you-sure-you-want-to-delete-the-page-x.-" +
								"this-page-serves-as-a-scope-for-content";
					}

					return _language.format(
						_httpServletRequest, messageKey,
						HtmlUtil.escape(
							layout.getName(_themeDisplay.getLocale())));
				}
			).put(
				"modalTitle",
				_language.get(_themeDisplay.getLocale(), "delete-page")
			).put(
				"url", _getDeleteLayoutURL(layout, afterDeleteSelectedLayout)
			).build()
		).put(
			"id", "delete"
		).put(
			"label", _language.get(_themeDisplay.getLocale(), "delete")
		).put(
			"symbolLeft", "trash"
		).put(
			"type", "item"
		);
	}

	private String _getDeleteLayoutURL(
			Layout layout, Layout afterDeleteSelectedLayout)
		throws Exception {

		Group scopeGroup = _themeDisplay.getScopeGroup();

		if (scopeGroup.isStaged() && !scopeGroup.isStagingGroup()) {
			return null;
		}

		String redirect = ParamUtil.getString(
			_httpServletRequest, "redirect", _themeDisplay.getURLCurrent());

		Layout curLayout = _themeDisplay.getLayout();

		Layout draftLayout = layout.fetchDraftLayout();

		if (Objects.equals(curLayout.getPlid(), layout.getPlid()) ||
			((draftLayout != null) &&
			 Objects.equals(curLayout.getPlid(), draftLayout.getPlid()))) {

			if (afterDeleteSelectedLayout != null) {
				redirect = PortalUtil.getLayoutRelativeURL(
					afterDeleteSelectedLayout, _themeDisplay);
			}
			else {
				redirect = String.valueOf(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
						PortletRequest.RENDER_PHASE));
			}
		}

		return ActionURLBuilder.createActionURL(
			(ActionURL)PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/layout_admin/delete_layout"
		).setRedirect(
			redirect
		).setParameter(
			"hideDefaultSuccessMessage", true
		).setParameter(
			"selPlid", String.valueOf(layout.getPlid())
		).buildString();
	}

	private long _getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		Group group = _groupProvider.getGroup(_httpServletRequest);

		if (group != null) {
			_groupId = group.getGroupId();
		}
		else {
			_groupId = _themeDisplay.getSiteGroupId();
		}

		return _groupId;
	}

	private String _getPageTypeSelectedOption() {
		if (_pageTypeSelectedOption != null) {
			return _pageTypeSelectedOption;
		}

		String pageTypeSelectedOption =
			ProductNavigationProductMenuWebKeys.PUBLIC_LAYOUT;

		String namespace = PortalUtil.getPortletNamespace(
			ProductNavigationProductMenuPortletKeys.
				PRODUCT_NAVIGATION_PRODUCT_MENU);

		String pageTypeSelectedOptionSessionValue = SessionClicks.get(
			_httpServletRequest,
			namespace +
				ProductNavigationProductMenuWebKeys.PAGE_TYPE_SELECTED_OPTION,
			ProductNavigationProductMenuWebKeys.PUBLIC_LAYOUT);

		if (_isValidPageTypeSelectedOption(
				pageTypeSelectedOptionSessionValue)) {

			pageTypeSelectedOption = pageTypeSelectedOptionSessionValue;
		}

		_pageTypeSelectedOption = pageTypeSelectedOption;

		return _pageTypeSelectedOption;
	}

	private JSONObject _getPermissionsJSONObject(Layout layout)
		throws Exception {

		return JSONUtil.put(
			"data",
			JSONUtil.put(
				"id", "permissions"
			).put(
				"modalTitle",
				_language.get(_themeDisplay.getLocale(), "permissions")
			).put(
				"url", _getPermissionsURL(layout)
			)
		).put(
			"href", StringPool.POUND
		).put(
			"id", "permissions"
		).put(
			"label", _language.get(_themeDisplay.getLocale(), "permissions")
		).put(
			"symbolLeft", "password-policies"
		).put(
			"type", "item"
		);
	}

	private String _getPermissionsURL(Layout layout) throws Exception {
		return PermissionsURLTag.doTag(
			StringPool.BLANK, Layout.class.getName(),
			HtmlUtil.escape(layout.getName(_themeDisplay.getLocale())), null,
			String.valueOf(layout.getPlid()),
			LiferayWindowState.POP_UP.toString(), null,
			_themeDisplay.getRequest());
	}

	private JSONObject _getPreviewDraftActionJSONObject(Layout layout)
		throws Exception {

		return JSONUtil.put(
			"href",
			PortalUtil.getLayoutFriendlyURL(
				layout.fetchDraftLayout(), _themeDisplay)
		).put(
			"id", "preview-draft"
		).put(
			"label", _language.get(_themeDisplay.getLocale(), "preview-draft")
		).put(
			"symbolRight", "shortcut"
		).put(
			"target", "_blank"
		).put(
			"type", "item"
		);
	}

	private String _getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNull(redirect)) {
			redirect = PortalUtil.escapeRedirect(_getBackURL());
		}

		_redirect = redirect;

		return _redirect;
	}

	private boolean _hasScopeGroup(Layout layout) throws Exception {
		if (layout.hasScopeGroup()) {
			return true;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return false;
		}

		return draftLayout.hasScopeGroup();
	}

	private boolean _isPageHierarchyOption(String pageTypeOption) {
		if (Objects.equals(
				pageTypeOption,
				ProductNavigationProductMenuWebKeys.PUBLIC_LAYOUT) ||
			Objects.equals(
				pageTypeOption,
				ProductNavigationProductMenuWebKeys.PRIVATE_LAYOUT)) {

			return true;
		}

		return false;
	}

	private boolean _isPrivateLayout() {
		return Objects.equals(
			ProductNavigationProductMenuWebKeys.PRIVATE_LAYOUT,
			_getPageTypeSelectedOption());
	}

	private boolean _isValidPageTypeSelectedOption(
		String pageTypeSelectedOption) {

		if (_isPageHierarchyOption(pageTypeSelectedOption)) {
			return true;
		}

		long siteNavigationMenuId = GetterUtil.getLong(pageTypeSelectedOption);

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.fetchSiteNavigationMenu(
				siteNavigationMenuId);

		if ((siteNavigationMenu != null) &&
			(siteNavigationMenu.getGroupId() == _getGroupId())) {

			return true;
		}

		return false;
	}

	private String _backURL;
	private Long _groupId;
	private final GroupProvider _groupProvider;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final LayoutActionsHelper _layoutActionsHelper;
	private String _pageTypeSelectedOption;
	private String _redirect;
	private final SiteNavigationMenuLocalService
		_siteNavigationMenuLocalService;
	private final ThemeDisplay _themeDisplay;

}
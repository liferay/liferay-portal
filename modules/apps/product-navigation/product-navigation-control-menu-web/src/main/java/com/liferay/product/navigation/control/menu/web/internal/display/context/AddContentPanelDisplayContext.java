/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.web.internal.display.context;

import com.liferay.asset.constants.AssetWebKeys;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.util.AssetHelper;
import com.liferay.layout.portlet.category.PortletCategoryManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class AddContentPanelDisplayContext {

	public AddContentPanelDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_assetHelper = (AssetHelper)httpServletRequest.getAttribute(
			AssetWebKeys.ASSET_HELPER);
		_portletCategoryManager =
			(PortletCategoryManager)httpServletRequest.getAttribute(
				PortletCategoryManager.class.getName());
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getAddContentPanelData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"addContentsURLs",
			() -> {
				if (hasAddContentPermission()) {
					return _getAddContentsURLs();
				}

				return Collections.emptyList();
			}
		).put(
			"contents",
			() -> {
				if (hasAddContentPermission()) {
					return getContents();
				}

				return Collections.emptyList();
			}
		).put(
			"getContentsURL",
			() -> {
				ResourceURL resourceURL =
					_liferayPortletResponse.createResourceURL();

				resourceURL.setParameter(
					"status", String.valueOf(WorkflowConstants.STATUS_ANY));
				resourceURL.setResourceID(
					"/product_navigation_control_menu/get_contents");

				return resourceURL.toString();
			}
		).put(
			"hasAddContentPermission", hasAddContentPermission()
		).put(
			"languageDirection", _getLanguageDirection()
		).put(
			"languageId", _themeDisplay.getLanguageId()
		).put(
			"namespace", _liferayPortletResponse.getNamespace()
		).put(
			"plid", _themeDisplay.getPlid()
		).put(
			"widgets",
			() -> {
				if (hasAddApplicationsPermission()) {
					return _getWidgetsJSONArray();
				}

				return Collections.emptyList();
			}
		).build();
	}

	public List<Map<String, Object>> getContents() throws Exception {
		List<Map<String, Object>> contents = new ArrayList<>();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAttribute("showNonindexable", Boolean.TRUE);
		assetEntryQuery.setClassNameIds(_getAvailableClassNameIds());
		assetEntryQuery.setEnd(_getDelta());
		assetEntryQuery.setGroupIds(
			new long[] {_themeDisplay.getScopeGroupId()});
		assetEntryQuery.setKeywords(_getKeywords());
		assetEntryQuery.setOrderByCol1("modifiedDate");
		assetEntryQuery.setOrderByCol2("title");
		assetEntryQuery.setOrderByType1("DESC");
		assetEntryQuery.setOrderByType2("ASC");
		assetEntryQuery.setStart(0);

		BaseModelSearchResult<AssetEntry> baseModelSearchResult =
			_assetHelper.searchAssetEntries(
				_httpServletRequest, assetEntryQuery, 0, _getDelta());

		for (AssetEntry assetEntry : baseModelSearchResult.getBaseModels()) {
			AssetRendererFactory<?> assetRendererFactory =
				assetEntry.getAssetRendererFactory();

			if (assetRendererFactory == null) {
				continue;
			}

			AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

			if (assetRenderer == null) {
				continue;
			}

			String portletId = PortletProviderUtil.getPortletId(
				assetEntry.getClassName(), PortletProvider.Action.ADD);

			contents.add(
				HashMapBuilder.<String, Object>put(
					"className", assetEntry.getClassName()
				).put(
					"classPK", assetEntry.getClassPK()
				).put(
					"draggable",
					PortletPermissionUtil.contains(
						_themeDisplay.getPermissionChecker(),
						_themeDisplay.getLayout(), portletId,
						ActionKeys.ADD_TO_PAGE)
				).put(
					"icon", assetRenderer.getIconCssClass()
				).put(
					"portletId", portletId
				).put(
					"title",
					HtmlUtil.escape(
						assetRenderer.getTitle(_themeDisplay.getLocale()))
				).put(
					"type",
					_getAssetEntryTypeLabel(
						assetEntry.getClassName(), assetEntry.getClassTypeId())
				).build());
		}

		return contents;
	}

	public boolean hasAddApplicationsPermission() throws Exception {
		if (_hasAddApplicationsPermission != null) {
			return _hasAddApplicationsPermission;
		}

		_hasAddApplicationsPermission = false;

		boolean stateMaximized = ParamUtil.getBoolean(
			_httpServletRequest, "stateMaximized");

		LayoutTypePortlet layoutTypePortlet =
			_themeDisplay.getLayoutTypePortlet();

		LayoutTypeController layoutTypeController =
			layoutTypePortlet.getLayoutTypeController();

		Layout layout = _themeDisplay.getLayout();

		if (!stateMaximized && layout.isTypePortlet() &&
			!layout.isLayoutPrototypeLinkActive() &&
			!layoutTypeController.isFullPageDisplayable() &&
			(hasLayoutUpdatePermission() ||
			 (layoutTypePortlet.isCustomizable() &&
			  layoutTypePortlet.isCustomizedView() &&
			  hasLayoutCustomizePermission()))) {

			_hasAddApplicationsPermission = true;
		}

		return _hasAddApplicationsPermission;
	}

	public boolean hasAddContentPermission() throws Exception {
		if (_hasAddContentPermission != null) {
			return _hasAddContentPermission;
		}

		_hasAddContentPermission = false;

		Group group = _themeDisplay.getScopeGroup();

		if (hasAddApplicationsPermission() && !group.isLayoutPrototype()) {
			_hasAddContentPermission = true;
		}

		return _hasAddContentPermission;
	}

	public boolean hasLayoutCustomizePermission() throws Exception {
		if (_hasLayoutCustomizePermission != null) {
			return _hasLayoutCustomizePermission;
		}

		_hasLayoutCustomizePermission = false;

		if (LayoutPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), _themeDisplay.getLayout(),
				ActionKeys.CUSTOMIZE)) {

			_hasLayoutCustomizePermission = true;
		}

		return _hasLayoutCustomizePermission;
	}

	public boolean hasLayoutUpdatePermission() throws Exception {
		if (_hasLayoutUpdatePermission != null) {
			return _hasLayoutUpdatePermission;
		}

		_hasLayoutUpdatePermission = false;

		if (LayoutPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), _themeDisplay.getLayout(),
				ActionKeys.UPDATE)) {

			_hasLayoutUpdatePermission = true;
		}

		return _hasLayoutUpdatePermission;
	}

	public boolean showAddPanel() throws Exception {
		Group group = _themeDisplay.getScopeGroup();

		LayoutTypePortlet layoutTypePortlet =
			_themeDisplay.getLayoutTypePortlet();

		if (!group.isControlPanel() &&
			(hasLayoutUpdatePermission() ||
			 (layoutTypePortlet.isCustomizable() &&
			  layoutTypePortlet.isCustomizedView() &&
			  hasLayoutCustomizePermission()))) {

			return true;
		}

		return false;
	}

	private List<Map<String, Object>> _getAddContentsURLs() throws Exception {
		return TransformUtil.transform(
			_assetHelper.getAssetPublisherAddItemHolders(
				_liferayPortletRequest, _liferayPortletResponse,
				_themeDisplay.getScopeGroupId(), _getClassNameIds(),
				new long[0], null, null, _getRedirectURL()),
			assetPublisherAddItemHolder -> HashMapBuilder.<String, Object>put(
				"label", assetPublisherAddItemHolder.getModelResource()
			).put(
				"url",
				() -> {
					long curGroupId = _themeDisplay.getScopeGroupId();

					Group group = _themeDisplay.getScopeGroup();

					if (!group.isStagedPortlet(
							assetPublisherAddItemHolder.getPortletId()) &&
						!group.isStagedRemotely()) {

						curGroupId = group.getLiveGroupId();
					}

					return _assetHelper.getAddURLPopUp(
						curGroupId, _themeDisplay.getPlid(),
						assetPublisherAddItemHolder.getPortletURL(), false,
						_themeDisplay.getLayout());
				}
			).build());
	}

	private String _getAssetEntryTypeLabel(String className, long classTypeId) {
		if (classTypeId <= 0) {
			return ResourceActionsUtil.getModelResource(
				_themeDisplay.getLocale(), className);
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if ((assetRendererFactory == null) ||
			!assetRendererFactory.isSupportsClassTypes()) {

			return ResourceActionsUtil.getModelResource(
				_themeDisplay.getLocale(), className);
		}

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			ClassType classType = classTypeReader.getClassType(
				classTypeId, themeDisplay.getLocale());

			return classType.getName();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return ResourceActionsUtil.getModelResource(
			_themeDisplay.getLocale(), className);
	}

	private long[] _getAvailableClassNameIds() {
		return AssetRendererFactoryRegistryUtil.getClassNameIds(
			_themeDisplay.getCompanyId(), true);
	}

	private long[] _getClassNameIds() {
		return AssetRendererFactoryRegistryUtil.getClassNameIds(
			_themeDisplay.getCompanyId());
	}

	private int _getDelta() {
		if (_delta != null) {
			return _delta;
		}

		int deltaDefault = GetterUtil.getInteger(
			SessionClicks.get(
				_httpServletRequest,
				"com.liferay.product.navigation.control.menu." +
					"web_addPanelNumItems",
				"10"));

		_delta = ParamUtil.getInteger(
			_httpServletRequest, "delta", deltaDefault);

		return _delta;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private Map<String, String> _getLanguageDirection() {
		Map<String, String> map = new HashMap<>();

		for (Locale locale :
				LanguageUtil.getAvailableLocales(
					_themeDisplay.getScopeGroupId())) {

			map.put(
				LocaleUtil.toLanguageId(locale),
				LanguageUtil.get(locale, "lang.dir"));
		}

		return map;
	}

	private String _getRedirectURL() throws Exception {
		return PortalUtil.getLayoutFullURL(
			_themeDisplay.getLayout(), _themeDisplay);
	}

	private JSONArray _getWidgetsJSONArray() throws Exception {
		return _portletCategoryManager.getPortletsJSONArray(
			_httpServletRequest, _themeDisplay);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddContentPanelDisplayContext.class);

	private final AssetHelper _assetHelper;
	private Integer _delta;
	private Boolean _hasAddApplicationsPermission;
	private Boolean _hasAddContentPermission;
	private Boolean _hasLayoutCustomizePermission;
	private Boolean _hasLayoutUpdatePermission;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PortletCategoryManager _portletCategoryManager;
	private final ThemeDisplay _themeDisplay;

}
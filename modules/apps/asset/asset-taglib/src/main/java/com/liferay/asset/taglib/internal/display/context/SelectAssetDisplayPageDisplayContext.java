/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.internal.display.context;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.item.selector.AssetDisplayPageSelectorCriterion;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalServiceUtil;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.taglib.internal.info.display.contributor.LayoutDisplayPageProviderRegistryUtil;
import com.liferay.asset.taglib.internal.item.selector.ItemSelectorUtil;
import com.liferay.info.item.InfoItemReference;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pavel Savinov
 */
public class SelectAssetDisplayPageDisplayContext {

	public SelectAssetDisplayPageDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_classNameId = GetterUtil.getLong(
			httpServletRequest.getAttribute(
				"liferay-asset:select-asset-display-page:classNameId"));
		_classPK = GetterUtil.getLong(
			httpServletRequest.getAttribute(
				"liferay-asset:select-asset-display-page:classPK"));
		_classTypeId = GetterUtil.getLong(
			httpServletRequest.getAttribute(
				"liferay-asset:select-asset-display-page:classTypeId"));
		_eventName = GetterUtil.getString(
			httpServletRequest.getAttribute(
				"liferay-asset:select-asset-display-page:eventName"),
			liferayPortletResponse.getNamespace() + "selectDisplayPage");
		_groupId = GetterUtil.getLong(
			httpServletRequest.getAttribute(
				"liferay-asset:select-asset-display-page:groupId"));
		_parentClassPK = GetterUtil.getLong(
			httpServletRequest.getAttribute(
				"liferay-asset:select-asset-display-page:parentClassPK"));
		_showPortletLayouts = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(
				"liferay-asset:select-asset-display-page:showPortletLayouts"));
		_showViewInContextLink = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(
				"liferay-asset:select-asset-display-page:" +
					"showViewInContextLink"));
	}

	public long getAssetDisplayPageId() {
		if (_assetDisplayPageId != null) {
			return _assetDisplayPageId;
		}

		_assetDisplayPageId = ParamUtil.getLong(
			_httpServletRequest, "assetDisplayPageId");

		AssetDisplayPageEntry assetDisplayPageEntry =
			_getAssetDisplayPageEntry();

		if (assetDisplayPageEntry != null) {
			_assetDisplayPageId =
				assetDisplayPageEntry.getLayoutPageTemplateEntryId();
		}

		return _assetDisplayPageId;
	}

	public String getAssetDisplayPageItemSelectorURL() {
		ItemSelector itemSelector = ItemSelectorUtil.getItemSelector();

		List<ItemSelectorCriterion> itemSelectorCriteria = new ArrayList<>();

		AssetDisplayPageSelectorCriterion assetDisplayPageSelectorCriterion =
			new AssetDisplayPageSelectorCriterion();

		assetDisplayPageSelectorCriterion.setClassNameId(_classNameId);
		assetDisplayPageSelectorCriterion.setClassTypeId(_classTypeId);
		assetDisplayPageSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		itemSelectorCriteria.add(assetDisplayPageSelectorCriterion);

		if (_showPortletLayouts) {
			LayoutItemSelectorCriterion layoutItemSelectorCriterion =
				new LayoutItemSelectorCriterion();

			layoutItemSelectorCriterion.setCheckDisplayPage(true);
			layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new UUIDItemSelectorReturnType());
			layoutItemSelectorCriterion.setShowBreadcrumb(false);

			itemSelectorCriteria.add(layoutItemSelectorCriterion);
		}

		return PortletURLBuilder.create(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					_liferayPortletRequest),
				_eventName,
				itemSelectorCriteria.toArray(new ItemSelectorCriterion[0]))
		).buildString();
	}

	public int getAssetDisplayPageType() {
		if (_displayPageType != null) {
			return _displayPageType;
		}

		if ((_classPK == 0) && (_parentClassPK > 0) &&
			inheritableDisplayPageTemplate()) {

			_displayPageType = AssetDisplayPageConstants.TYPE_INHERITED;

			return _displayPageType;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getDefaultLayoutPageTemplateEntry();

		if ((_classPK == 0) && (layoutPageTemplateEntry != null)) {
			_displayPageType = AssetDisplayPageConstants.TYPE_DEFAULT;

			return _displayPageType;
		}

		AssetDisplayPageEntry assetDisplayPageEntry =
			_getAssetDisplayPageEntry();

		if (assetDisplayPageEntry == null) {
			if (Validator.isNull(getLayoutUuid())) {
				_displayPageType = AssetDisplayPageConstants.TYPE_DEFAULT;
			}
			else {
				_displayPageType = AssetDisplayPageConstants.TYPE_SPECIFIC;
			}
		}
		else {
			_displayPageType = assetDisplayPageEntry.getType();
		}

		return _displayPageType;
	}

	public String getDefaultAssetDisplayPageName() {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getDefaultLayoutPageTemplateEntry();

		if (layoutPageTemplateEntry != null) {
			return layoutPageTemplateEntry.getName();
		}

		return null;
	}

	public String getEventName() {
		return _eventName;
	}

	public String getLayoutUuid() {
		return BeanParamUtil.getString(
			AssetEntryLocalServiceUtil.fetchEntry(_classNameId, _classPK),
			_httpServletRequest, "layoutUuid", null);
	}

	public String getSpecificAssetDisplayPageName() throws Exception {
		String assetDisplayPageName = _getAssetDisplayPageName();

		if (Validator.isNotNull(assetDisplayPageName)) {
			return assetDisplayPageName;
		}

		String layoutUuid = getLayoutUuid();

		if (Validator.isNull(layoutUuid)) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout selLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
			layoutUuid, themeDisplay.getSiteGroupId(), false);

		if (selLayout == null) {
			selLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layoutUuid, themeDisplay.getSiteGroupId(), true);
		}

		if (selLayout != null) {
			return selLayout.getBreadcrumb(themeDisplay.getLocale());
		}

		return null;
	}

	public String getURLViewInContext() {
		if (_classPK <= 0) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.
				getAssetRendererFactoryByClassNameId(_classNameId);

		try {
			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(
					_classPK, AssetRendererFactory.TYPE_LATEST);

			String viewInContextURL = assetRenderer.getURLViewInContext(
				_liferayPortletRequest, _liferayPortletResponse,
				themeDisplay.getURLCurrent());

			return HttpComponentsUtil.addParameter(
				viewInContextURL, "p_l_mode", Constants.PREVIEW);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	public boolean inheritableDisplayPageTemplate() {
		if (_inheritableDisplayPageTemplate != null) {
			return _inheritableDisplayPageTemplate;
		}

		if (_parentClassPK <= 0) {
			_inheritableDisplayPageTemplate = false;

			return _inheritableDisplayPageTemplate;
		}

		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry =
			LayoutDisplayPageProviderRegistryUtil.
				getLayoutDisplayPageProviderRegistry();

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					PortalUtil.getClassName(_classNameId));

		boolean inheritableDisplayPageTemplate = false;

		if (layoutDisplayPageProvider != null) {
			inheritableDisplayPageTemplate =
				layoutDisplayPageProvider.inheritable();
		}

		_inheritableDisplayPageTemplate = inheritableDisplayPageTemplate;

		return _inheritableDisplayPageTemplate;
	}

	public boolean isAssetDisplayPageTypeDefault() {
		if (getAssetDisplayPageType() ==
				AssetDisplayPageConstants.TYPE_DEFAULT) {

			return true;
		}

		return false;
	}

	public boolean isAssetDisplayPageTypeSpecific() {
		if (getAssetDisplayPageType() ==
				AssetDisplayPageConstants.TYPE_SPECIFIC) {

			return true;
		}

		return false;
	}

	public boolean isShowViewInContextLink() {
		if (!_showViewInContextLink) {
			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			LayoutDisplayPageProviderRegistry
				layoutDisplayPageProviderRegistry =
					LayoutDisplayPageProviderRegistryUtil.
						getLayoutDisplayPageProviderRegistry();

			LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
				layoutDisplayPageProviderRegistry.
					getLayoutDisplayPageProviderByClassName(
						PortalUtil.getClassName(_classNameId));

			if (layoutDisplayPageProvider == null) {
				return false;
			}

			InfoItemReference infoItemReference = new InfoItemReference(
				PortalUtil.getClassName(_classNameId), _classPK);

			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					infoItemReference);

			if ((layoutDisplayPageObjectProvider == null) ||
				!AssetDisplayPageUtil.hasAssetDisplayPage(
					themeDisplay.getScopeGroupId(),
					layoutDisplayPageObjectProvider.getClassNameId(),
					layoutDisplayPageObjectProvider.getClassPK(),
					layoutDisplayPageObjectProvider.getClassTypeId())) {

				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return true;
	}

	public boolean isURLViewInContext() throws Exception {
		if ((_classPK == 0) ||
			(Validator.isNull(getLayoutUuid()) &&
			 Validator.isNull(getURLViewInContext()))) {

			return false;
		}

		return true;
	}

	private AssetDisplayPageEntry _getAssetDisplayPageEntry() {
		if (_assetDisplayPageEntry != null) {
			return _assetDisplayPageEntry;
		}

		AssetDisplayPageEntry assetDisplayPageEntry =
			AssetDisplayPageEntryLocalServiceUtil.fetchAssetDisplayPageEntry(
				_groupId, _classNameId, _classPK);

		if (assetDisplayPageEntry != null) {
			_assetDisplayPageEntry = assetDisplayPageEntry;
		}

		return _assetDisplayPageEntry;
	}

	private String _getAssetDisplayPageName() {
		long assetDisplayPageId = getAssetDisplayPageId();

		if (assetDisplayPageId == 0) {
			return StringPool.BLANK;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntry(assetDisplayPageId);

		if (layoutPageTemplateEntry == null) {
			return StringPool.BLANK;
		}

		return layoutPageTemplateEntry.getName();
	}

	private LayoutPageTemplateEntry _getDefaultLayoutPageTemplateEntry() {
		if (_defaultLayoutPageTemplateEntry != null) {
			return _defaultLayoutPageTemplateEntry;
		}

		_defaultLayoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					_groupId, _classNameId, _classTypeId);

		return _defaultLayoutPageTemplateEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectAssetDisplayPageDisplayContext.class);

	private AssetDisplayPageEntry _assetDisplayPageEntry;
	private Long _assetDisplayPageId;
	private final Long _classNameId;
	private Long _classPK;
	private final Long _classTypeId;
	private LayoutPageTemplateEntry _defaultLayoutPageTemplateEntry;
	private Integer _displayPageType;
	private final String _eventName;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private Boolean _inheritableDisplayPageTemplate;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final Long _parentClassPK;
	private final boolean _showPortletLayouts;
	private final boolean _showViewInContextLink;

}
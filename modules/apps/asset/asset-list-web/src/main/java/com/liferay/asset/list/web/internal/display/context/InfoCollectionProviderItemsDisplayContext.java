/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class InfoCollectionProviderItemsDisplayContext {

	public InfoCollectionProviderItemsDisplayContext(
		InfoItemServiceRegistry infoItemServiceRegistry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_infoItemServiceRegistry = infoItemServiceRegistry;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getInfoCollectionItemsType(Object result) {
		String className = StringPool.BLANK;

		if (result instanceof AssetEntry) {
			AssetEntry assetEntry = (AssetEntry)result;

			className = PortalUtil.getClassName(assetEntry.getClassNameId());
		}
		else {
			InfoCollectionProvider infoCollectionProvider =
				_getInfoCollectionProvider();

			className = infoCollectionProvider.getCollectionItemClassName();
		}

		return ResourceActionsUtil.getModelResource(
			_themeDisplay.getLocale(), className);
	}

	public String getInfoCollectionProviderClassName() {
		if (Validator.isNotNull(_infoCollectionProviderClassName)) {
			return _infoCollectionProviderClassName;
		}

		InfoCollectionProvider<?> infoCollectionProvider =
			_getInfoCollectionProvider();

		_infoCollectionProviderClassName =
			infoCollectionProvider.getCollectionItemClassName();

		return _infoCollectionProviderClassName;
	}

	public InfoItemFieldValuesProvider<Object>
		getInfoItemFieldValuesProvider() {

		if (_infoItemFieldValuesProvider != null) {
			return _infoItemFieldValuesProvider;
		}

		InfoCollectionProvider<?> infoCollectionProvider =
			_getInfoCollectionProvider();

		if (infoCollectionProvider == null) {
			return _infoItemFieldValuesProvider;
		}

		_infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class,
				infoCollectionProvider.getCollectionItemClassName());

		return _infoItemFieldValuesProvider;
	}

	public SearchContainer<Object> getSearchContainer() {
		SearchContainer<Object> searchContainer = new SearchContainer<>(
			_renderRequest, _getPortletURL(), null,
			LanguageUtil.get(
				_httpServletRequest,
				"there-are-no-items-in-the-collection-provider"));

		InfoCollectionProvider<?> infoCollectionProvider =
			_getInfoCollectionProvider();

		if (infoCollectionProvider == null) {
			return searchContainer;
		}

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setPagination(
			Pagination.of(
				searchContainer.getEnd(), searchContainer.getStart()));

		InfoPage infoPage = infoCollectionProvider.getCollectionInfoPage(
			collectionQuery);

		searchContainer.setResultsAndTotal(
			infoPage::getPageItems, infoPage.getTotalCount());

		return searchContainer;
	}

	public String getTitle(Object object) {
		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			getInfoItemFieldValuesProvider();

		InfoItemFieldValues infoItemFieldValues =
			infoItemFieldValuesProvider.getInfoItemFieldValues(object);

		InfoFieldValue<Object> titleInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("title");

		if (titleInfoFieldValue != null) {
			return String.valueOf(
				titleInfoFieldValue.getValue(_themeDisplay.getLocale()));
		}

		InfoFieldValue<Object> nameInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("name");

		if (nameInfoFieldValue != null) {
			return String.valueOf(
				nameInfoFieldValue.getValue(_themeDisplay.getLocale()));
		}

		if (object instanceof ClassedModel) {
			ClassedModel classedModel = (ClassedModel)object;

			return getInfoCollectionItemsType(object) +
				StringPool.COMMA_AND_SPACE + classedModel.getPrimaryKeyObj();
		}

		return getInfoCollectionItemsType(object);
	}

	public boolean isShowActions() {
		if (_showActions != null) {
			return _showActions;
		}

		_showActions = ParamUtil.getBoolean(_renderRequest, "showActions");

		return _showActions;
	}

	private InfoCollectionProvider<?> _getInfoCollectionProvider() {
		if (_infoCollectionProvider != null) {
			return _infoCollectionProvider;
		}

		_infoCollectionProvider = _infoItemServiceRegistry.getInfoItemService(
			InfoCollectionProvider.class, _getInfoCollectionProviderKey());

		return _infoCollectionProvider;
	}

	private String _getInfoCollectionProviderKey() {
		if (_infoCollectionProviderKey != null) {
			return _infoCollectionProviderKey;
		}

		String infoCollectionProviderKey = ParamUtil.getString(
			_renderRequest, "infoCollectionProviderKey");

		if (Validator.isNull(infoCollectionProviderKey)) {
			infoCollectionProviderKey = ParamUtil.getString(
				_renderRequest, "collectionPK");
		}

		_infoCollectionProviderKey = infoCollectionProviderKey;

		return _infoCollectionProviderKey;
	}

	private PortletURL _getPortletURL() {
		PortletURL currentURLObj = PortletURLUtil.getCurrent(
			_renderRequest, _renderResponse);

		try {
			return PortletURLUtil.clone(currentURLObj, _renderResponse);
		}
		catch (PortletException portletException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portletException);
			}

			return PortletURLBuilder.createRenderURL(
				_renderResponse
			).setParameters(
				currentURLObj.getParameterMap()
			).buildPortletURL();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InfoCollectionProviderItemsDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private InfoCollectionProvider<?> _infoCollectionProvider;
	private String _infoCollectionProviderClassName;
	private String _infoCollectionProviderKey;
	private InfoItemFieldValuesProvider<Object> _infoItemFieldValuesProvider;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Boolean _showActions;
	private final ThemeDisplay _themeDisplay;

}
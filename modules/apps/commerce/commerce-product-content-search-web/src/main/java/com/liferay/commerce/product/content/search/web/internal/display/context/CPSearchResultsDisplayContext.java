/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.display.context;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.render.list.CPContentListRenderer;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRenderer;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPSearchResultsPortletInstanceConfiguration;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPSortPortletInstanceConfiguration;
import com.liferay.commerce.product.content.search.web.internal.constants.CPSearchResultsConstants;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CPSearchResultsDisplayContext {

	public CPSearchResultsDisplayContext(
			ConfigurationProvider configurationProvider,
			CPContentListEntryRendererRegistry
				cpContentListEntryRendererRegistry,
			CPContentListRendererRegistry cpContentListRendererRegistry,
			CPDefinitionHelper cpDefinitionHelper,
			CPTypeRegistry cpTypeRegistry,
			HttpServletRequest httpServletRequest,
			PortletSharedSearchResponse portletSharedSearchResponse)
		throws ConfigurationException {

		_configurationProvider = configurationProvider;
		_cpContentListEntryRendererRegistry =
			cpContentListEntryRendererRegistry;
		_cpContentListRendererRegistry = cpContentListRendererRegistry;
		_cpDefinitionHelper = cpDefinitionHelper;
		_cpTypeRegistry = cpTypeRegistry;
		_httpServletRequest = httpServletRequest;
		_portletSharedSearchResponse = portletSharedSearchResponse;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);

		_cpSearchResultsPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CPSearchResultsPortletInstanceConfiguration.class,
				_cpRequestHelper.getThemeDisplay());
		_cpSortPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				CPSortPortletInstanceConfiguration.class,
				_cpRequestHelper.getThemeDisplay());
	}

	public Map<String, String> getCPContentListEntryRendererKeys() {
		Map<String, String> cpContentListEntryRendererKeys = new HashMap<>();

		for (CPType cpType : getCPTypes()) {
			String cpTypeName = cpType.getName();

			cpContentListEntryRendererKeys.put(
				cpTypeName, getCPTypeListEntryRendererKey(cpTypeName));
		}

		return cpContentListEntryRendererKeys;
	}

	public List<CPContentListEntryRenderer> getCPContentListEntryRenderers(
		String cpType) {

		return _cpContentListEntryRendererRegistry.
			getCPContentListEntryRenderers(
				CPPortletKeys.CP_SEARCH_RESULTS, cpType);
	}

	public String getCPContentListRendererKey() {
		RenderRequest renderRequest = _cpRequestHelper.getRenderRequest();

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		String value = portletPreferences.getValue(
			"cpContentListRendererKey", null);

		if (Validator.isNotNull(value)) {
			return value;
		}

		List<CPContentListRenderer> cpContentListRenderers =
			getCPContentListRenderers();

		if (cpContentListRenderers.isEmpty()) {
			return StringPool.BLANK;
		}

		CPContentListRenderer cpContentListRenderer =
			cpContentListRenderers.get(0);

		if (cpContentListRenderer == null) {
			return StringPool.BLANK;
		}

		return cpContentListRenderer.getKey();
	}

	public List<CPContentListRenderer> getCPContentListRenderers() {
		return _cpContentListRendererRegistry.getCPContentListRenderers(
			CPPortletKeys.CP_SEARCH_RESULTS);
	}

	public CPDataSourceResult getCPDataSourceResult() {
		List<CPCatalogEntry> cpCatalogEntries = _getCPCatalogEntries(
			_portletSharedSearchResponse.getDocuments());

		return new CPDataSourceResult(
			cpCatalogEntries, _portletSharedSearchResponse.getTotalHits());
	}

	public String getCPTypeListEntryRendererKey(String cpType) {
		RenderRequest renderRequest = _cpRequestHelper.getRenderRequest();

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		String value = portletPreferences.getValue(
			cpType + "--cpTypeListEntryRendererKey", null);

		if (Validator.isNotNull(value)) {
			return value;
		}

		List<CPContentListEntryRenderer> cpContentListEntryRenderers =
			getCPContentListEntryRenderers(cpType);

		if (cpContentListEntryRenderers.isEmpty()) {
			return StringPool.BLANK;
		}

		CPContentListEntryRenderer cpContentListEntryRenderer =
			cpContentListEntryRenderers.get(0);

		if (cpContentListEntryRenderer == null) {
			return StringPool.BLANK;
		}

		return cpContentListEntryRenderer.getKey();
	}

	public List<CPType> getCPTypes() {
		return _cpTypeRegistry.getCPTypes();
	}

	public String getDisplayStyle() {
		return _cpSearchResultsPortletInstanceConfiguration.displayStyle();
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId > 0) {
			return _displayStyleGroupId;
		}

		_displayStyleGroupId =
			_cpSearchResultsPortletInstanceConfiguration.displayStyleGroupId();

		if (_displayStyleGroupId <= 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public String getNames() {
		StringBundler sb = new StringBundler();

		List<CPType> cpTypes = getCPTypes();

		for (int i = 0; i < cpTypes.size(); i++) {
			CPType cpType = cpTypes.get(i);

			sb.append(cpType.getLabel(_cpRequestHelper.getLocale()));

			if ((i + 1) < cpTypes.size()) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	public String getOrderByCol() throws PortalException {
		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(_httpServletRequest);

		String portletId = ParamUtil.getString(
			originalHttpServletRequest, "p_p_id");

		String sortOptionDefault = CPSearchResultsConstants.SORT_OPTION_DEFAULT;

		if (!Validator.isBlank(
				_cpSortPortletInstanceConfiguration.defaultSort())) {

			sortOptionDefault =
				_cpSortPortletInstanceConfiguration.defaultSort();
		}

		String orderByCol = ParamUtil.getString(
			originalHttpServletRequest,
			StringBundler.concat(
				StringPool.UNDERLINE, portletId, StringPool.UNDERLINE,
				SearchContainer.DEFAULT_ORDER_BY_COL_PARAM),
			sortOptionDefault);

		if (ArrayUtil.contains(
				CPSearchResultsConstants.SORT_OPTIONS, orderByCol)) {

			return orderByCol;
		}

		return CPSearchResultsConstants.SORT_OPTION_DEFAULT;
	}

	public String getOrderByColMessage() throws PortalException {
		return LanguageUtil.format(
			_httpServletRequest, "sort-by-colon-x", getOrderByCol(), true);
	}

	public int getPaginationDelta() {
		return _cpSearchResultsPortletInstanceConfiguration.paginationDelta();
	}

	public SearchContainer<CPCatalogEntry> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = _buildSearchContainer(
			getCPDataSourceResult(),
			_portletSharedSearchResponse.getPaginationStart(), "start",
			_portletSharedSearchResponse.getPaginationDelta(), "delta");

		return _searchContainer;
	}

	public String getSelectionStyle() {
		return _cpSearchResultsPortletInstanceConfiguration.selectionStyle();
	}

	public boolean hasCommerceChannel() throws PortalException {
		CommerceContext commerceContext =
			(CommerceContext)_httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return false;
		}

		long commerceChannelId = commerceContext.getCommerceChannelId();

		if (commerceChannelId > 0) {
			return true;
		}

		return false;
	}

	public boolean isPaginate() {
		return _cpSearchResultsPortletInstanceConfiguration.paginate();
	}

	public boolean isSelectionStyleADT() {
		String selectionStyle = getSelectionStyle();

		return selectionStyle.equals("adt");
	}

	public boolean isSelectionStyleCustomRenderer() {
		String selectionStyle = getSelectionStyle();

		return selectionStyle.equals("custom");
	}

	private SearchContainer<CPCatalogEntry> _buildSearchContainer(
		CPDataSourceResult cpDataSourceResult, int paginationStart,
		String paginationStartParameterName, int paginationDelta,
		String paginationDeltaParameterName) {

		SearchContainer<CPCatalogEntry> searchContainer = new SearchContainer<>(
			_cpRequestHelper.getLiferayPortletRequest(), null, null,
			paginationStartParameterName, paginationStart, paginationDelta,
			_getPortletURL(), null, null, null);

		searchContainer.setDeltaParam(paginationDeltaParameterName);
		searchContainer.setResultsAndTotal(
			cpDataSourceResult::getCPCatalogEntries,
			cpDataSourceResult.getLength());

		return searchContainer;
	}

	private List<CPCatalogEntry> _getCPCatalogEntries(
		List<Document> documents) {

		return TransformUtil.transform(
			documents,
			document -> _cpDefinitionHelper.getCPCatalogEntry(
				document, _cpRequestHelper.getLocale()));
	}

	private PortletURL _getPortletURL() {
		final String urlString = _getURLString();

		return new NullPortletURL() {

			@Override
			public String toString() {
				return urlString;
			}

		};
	}

	private String _getURLString() {
		return HttpComponentsUtil.removeParameter(
			PortalUtil.getCurrentURL(_cpRequestHelper.getRequest()), "start");
	}

	private final ConfigurationProvider _configurationProvider;
	private final CPContentListEntryRendererRegistry
		_cpContentListEntryRendererRegistry;
	private final CPContentListRendererRegistry _cpContentListRendererRegistry;
	private final CPDefinitionHelper _cpDefinitionHelper;
	private final CPRequestHelper _cpRequestHelper;
	private final CPSearchResultsPortletInstanceConfiguration
		_cpSearchResultsPortletInstanceConfiguration;
	private final CPSortPortletInstanceConfiguration
		_cpSortPortletInstanceConfiguration;
	private final CPTypeRegistry _cpTypeRegistry;
	private long _displayStyleGroupId;
	private final HttpServletRequest _httpServletRequest;
	private final PortletSharedSearchResponse _portletSharedSearchResponse;
	private SearchContainer<CPCatalogEntry> _searchContainer;

}
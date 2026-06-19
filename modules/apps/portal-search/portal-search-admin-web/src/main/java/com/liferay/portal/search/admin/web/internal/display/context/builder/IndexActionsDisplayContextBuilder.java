/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.display.context.builder;

import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.admin.web.internal.display.context.IndexActionsDisplayContext;
import com.liferay.portal.search.admin.web.internal.reindexer.IndexReindexerCategoryRegistry;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.cluster.StatsInformation;
import com.liferay.portal.search.cluster.StatsInformationFactory;
import com.liferay.portal.search.configuration.ReindexConfiguration;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivia Yu
 */
public class IndexActionsDisplayContextBuilder {

	public IndexActionsDisplayContextBuilder(
		Language language, Portal portal,
		ReindexConfiguration reindexConfiguration, RenderRequest renderRequest,
		SearchCapabilities searchCapabilities) {

		_language = language;
		_reindexConfiguration = reindexConfiguration;
		_renderRequest = renderRequest;
		_searchCapabilities = searchCapabilities;

		_httpServletRequest = portal.getHttpServletRequest(renderRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_permissionChecker = themeDisplay.getPermissionChecker();
	}

	public IndexActionsDisplayContext build() {
		IndexActionsDisplayContext indexActionsDisplayContext =
			new IndexActionsDisplayContext();

		indexActionsDisplayContext.setData(getData());

		return indexActionsDisplayContext;
	}

	public void setIndexReindexerCategoryRegistry(
		IndexReindexerCategoryRegistry indexReindexerCategoryRegistry) {

		_indexReindexerCategoryRegistry = indexReindexerCategoryRegistry;
	}

	public void setIndexReindexerClassNames(
		List<String> indexReindexerClassNames) {

		_indexReindexerClassNames = indexReindexerClassNames;
	}

	public void setStatsInformationFactory(
		StatsInformationFactory statsInformationFactory) {

		_statsInformationFactory = statsInformationFactory;
	}

	protected Map<String, Object> getData() {
		return HashMapBuilder.<String, Object>put(
			"concurrentModeSupported",
			_searchCapabilities.isConcurrentModeSupported()
		).put(
			"controlMenuCategoryKey",
			ProductNavigationControlMenuCategoryKeys.TOOLS
		).put(
			"indexersMap", _getIndexersMap()
		).put(
			"indexReindexerNames", _getIndexReindexerNames()
		).put(
			"initialCompanyIds", _getInitialCompanyIds()
		).put(
			"initialExecutionMode", _getInitialExecutionMode()
		).put(
			"initialScope", _getInitialScope()
		).put(
			"omniadmin", _permissionChecker.isOmniadmin()
		).put(
			"searchEngineDiskSpace", _getSearchEngineDiskSpace()
		).put(
			"virtualInstances", _getVirtualInstancesJSONArray()
		).build();
	}

	private Map<String, List<Object>> _getIndexersMap() {
		Set<Indexer<?>> indexersSet = IndexerRegistryUtil.getIndexers();

		if (SetUtil.isEmpty(indexersSet)) {
			return Collections.emptyMap();
		}

		Locale locale = _renderRequest.getLocale();

		Collator collator = CollatorUtil.getInstance(locale);

		Map<String, Map<String, Indexer<?>>> indexersKeyMap = new HashMap<>();

		for (Indexer<?> indexer : indexersSet) {
			String key = "com.liferay.custom";

			String className = indexer.getClassName();

			int index = className.indexOf(".model.");

			if (index != -1) {
				key = className.substring(0, index);
			}

			indexersKeyMap.compute(
				key,
				(_key, value) -> {
					if (value == null) {
						value = new TreeMap<>(collator);
					}

					value.put(
						ResourceActionsUtil.getModelResource(
							locale, indexer.getClassName()),
						indexer);

					return value;
				});
		}

		Map<String, List<Object>> indexersMap = new HashMap<>();

		for (Map.Entry<String, Map<String, Indexer<?>>> entry :
				indexersKeyMap.entrySet()) {

			Map<String, Indexer<?>> indexers = entry.getValue();
			List<Object> indexersData = new ArrayList<>();

			for (Indexer<?> indexer : indexers.values()) {
				String className = indexer.getClassName();

				if (className.contains(
						"com.liferay.object.model.ObjectDefinition") &&
					!_permissionChecker.isOmniadmin() &&
					(indexer.getCompanyId() !=
						CompanyThreadLocal.getCompanyId())) {

					continue;
				}

				indexersData.add(
					HashMapBuilder.<String, Object>put(
						"className", className
					).put(
						"displayName",
						_language.get(
							_httpServletRequest, "model.resource." + className)
					).put(
						"enabled", indexer.isIndexerEnabled()
					).build());
			}

			indexersMap.put(
				_language.get(
					_httpServletRequest, "model.resource." + entry.getKey()),
				indexersData);
		}

		return indexersMap;
	}

	private Map<String, List<Object>> _getIndexReindexerNames() {
		Map<String, List<Object>> indexReindexerNamesMap = new TreeMap<>();

		if (ListUtil.isNotNull(_indexReindexerClassNames)) {
			for (String indexReindexerClassName : _indexReindexerClassNames) {
				String categoryDisplayKey = _language.get(
					_httpServletRequest,
					_indexReindexerCategoryRegistry.getCategory(
						indexReindexerClassName));

				List<Object> indexReindexerNames =
					indexReindexerNamesMap.computeIfAbsent(
						categoryDisplayKey, key -> new ArrayList<>());

				indexReindexerNames.add(
					HashMapBuilder.put(
						"className", indexReindexerClassName
					).put(
						"displayName",
						_language.get(
							_httpServletRequest,
							"model.resource." + indexReindexerClassName)
					).build());
			}
		}

		return indexReindexerNamesMap;
	}

	private long[] _getInitialCompanyIds() {
		return StringUtil.split(
			ParamUtil.getString(_httpServletRequest, "companyIds"), 0L);
	}

	private String _getInitialExecutionMode() {
		return ParamUtil.getString(
			_httpServletRequest, "executionMode",
			_reindexConfiguration.defaultReindexExecutionMode());
	}

	private String _getInitialScope() {
		return ParamUtil.getString(_httpServletRequest, "scope");
	}

	private Map<String, Object> _getSearchEngineDiskSpace() {
		Map<String, Object> searchEngineDiskSpace = new HashMap<>();

		if (_isStatsInformationAvailable()) {
			StatsInformation statsInformation =
				_statsInformationFactory.getStatsInformation();

			searchEngineDiskSpace.put(
				"availableDiskSpace", statsInformation.getAvailableDiskSpace());
			searchEngineDiskSpace.put(
				"isLowOnDiskSpace",
				_isLowOnDiskSpace(
					statsInformation.getAvailableDiskSpace(),
					statsInformation.getSizeOfLargestIndex()));
			searchEngineDiskSpace.put(
				"usedDiskSpace", statsInformation.getUsedDiskSpace());
		}

		return searchEngineDiskSpace;
	}

	private JSONArray _getVirtualInstancesJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		long[] companyIds = PortalInstancePool.getCompanyIds();

		if (!ArrayUtil.contains(companyIds, CompanyConstants.SYSTEM)) {
			jsonArray.put(
				JSONUtil.put(
					"id", String.valueOf(CompanyConstants.SYSTEM)
				).put(
					"name", _language.get(_httpServletRequest, "system")
				));
		}

		for (long companyId : companyIds) {
			try {
				Company company = CompanyLocalServiceUtil.getCompany(companyId);

				jsonArray.put(
					JSONUtil.put(
						"id", String.valueOf(companyId)
					).put(
						"name", company.getWebId()
					));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to get company with company ID " + companyId,
						exception);
				}
			}
		}

		return jsonArray;
	}

	private boolean _isLowOnDiskSpace(
		double availableDiskSpace, double largestIndexSize) {

		if (availableDiskSpace < (largestIndexSize * 1.5)) {
			return true;
		}

		return false;
	}

	private boolean _isStatsInformationAvailable() {
		if (_statsInformationFactory != null) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexActionsDisplayContextBuilder.class);

	private final HttpServletRequest _httpServletRequest;
	private IndexReindexerCategoryRegistry _indexReindexerCategoryRegistry;
	private List<String> _indexReindexerClassNames;
	private final Language _language;
	private final PermissionChecker _permissionChecker;
	private final ReindexConfiguration _reindexConfiguration;
	private final RenderRequest _renderRequest;
	private final SearchCapabilities _searchCapabilities;
	private StatsInformationFactory _statsInformationFactory;

}
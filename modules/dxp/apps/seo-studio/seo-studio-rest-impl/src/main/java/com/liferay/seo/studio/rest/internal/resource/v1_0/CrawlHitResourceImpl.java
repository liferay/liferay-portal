/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.internal.resource.v1_0;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.pagination.provider.PaginationProvider;
import com.liferay.seo.studio.rest.dto.v1_0.CrawlHit;
import com.liferay.seo.studio.rest.resource.v1_0.CrawlHitResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Brooke Dalton
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/crawl-hit.properties",
	scope = ServiceScope.PROTOTYPE, service = CrawlHitResource.class
)
public class CrawlHitResourceImpl extends BaseCrawlHitResourceImpl {

	@Override
	public Page<CrawlHit> getSeoStudioDomainCrawlHitsPage(
			Long seoStudioDomainId, String lastURL, Integer pageSize)
		throws Exception {

		if (!_objectEntryService.hasModelResourcePermission(
				contextUser, seoStudioDomainId, ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				contextUser.getUserId(), ObjectEntry.class.getName(),
				seoStudioDomainId, ActionKeys.VIEW);
		}

		String indexName = "seo_studio_" + seoStudioDomainId;

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(
				new IndicesExistsIndexRequest(indexName));

		if (!indicesExistsIndexResponse.isExists()) {
			return Page.of(Collections.emptyList());
		}

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.addSorts(
			_sorts.field("url.keyword", SortOrder.ASC));
		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(indexName);
		searchSearchRequest.setQuery(
			QueriesUtil.rangeTerm(
				"url.keyword", false, false, GetterUtil.getString(lastURL),
				null));

		Pagination pagination = _paginationProvider.getPagination(
			contextCompany.getCompanyId(), 1, pageSize);

		searchSearchRequest.setSize(pagination.getPageSize());

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		return Page.of(
			transform(searchHits.getSearchHits(), this::_toCrawlHit));
	}

	private String[] _getLinks(Map<String, Object> sourcesMap) {
		Object linksObject = sourcesMap.get("links");

		if (!(linksObject instanceof List<?>)) {
			return null;
		}

		return ArrayUtil.toStringArray(
			transform(
				(List<?>)linksObject,
				link -> {
					if (link instanceof String) {
						return _toCanonicalURL((String)link);
					}

					return null;
				}));
	}

	private String _toCanonicalURL(String url) {
		String queryString = HttpComponentsUtil.getQueryString(url);

		if (Validator.isNotNull(queryString)) {
			for (String name :
					HttpComponentsUtil.getParameterNames(queryString)) {

				if (_excludedParameterNames.contains(name) ||
					name.startsWith("filter_category_") ||
					_portal.isReservedParameter(name)) {

					url = HttpComponentsUtil.removeParameter(url, name);
				}
			}
		}

		String canonicalURL = StringUtil.replace(url, CharPool.SPACE, "%20");

		if ((canonicalURL.length() > 1) &&
			(canonicalURL.indexOf(CharPool.QUESTION) < 0) &&
			canonicalURL.endsWith(StringPool.SLASH)) {

			return canonicalURL.substring(0, canonicalURL.length() - 1);
		}

		return canonicalURL;
	}

	private CrawlHit _toCrawlHit(SearchHit searchHit) {
		Map<String, Object> sourcesMap = searchHit.getSourcesMap();

		if (sourcesMap == null) {
			return null;
		}

		String url = GetterUtil.getString(sourcesMap.get("url"));

		if (Validator.isNull(url)) {
			return null;
		}

		CrawlHit crawlHit = new CrawlHit();

		crawlHit.setCanonicalUrl(() -> _toCanonicalURL(url));
		crawlHit.setLinks(() -> _getLinks(sourcesMap));
		crawlHit.setTitle(() -> GetterUtil.getString(sourcesMap.get("title")));
		crawlHit.setUrl(() -> url);

		return crawlHit;
	}

	private static final Set<String> _excludedParameterNames =
		SetUtil.fromArray(
			"category", "cur", "delta", "facet", "filter", "highlight", "order",
			"orderby", "sort", "start", "tag", "utm_campaign", "utm_cid",
			"utm_content", "utm_medium", "utm_source", "utm_term");

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private PaginationProvider _paginationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private Sorts _sorts;

}
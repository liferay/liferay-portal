/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.internal.resource.v1_0;

import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.seo.studio.rest.dto.v1_0.CrawledPage;
import com.liferay.seo.studio.rest.resource.v1_0.CrawledPageResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Brooke Dalton
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/crawled-page.properties",
	scope = ServiceScope.PROTOTYPE, service = CrawledPageResource.class
)
public class CrawledPageResourceImpl extends BaseCrawledPageResourceImpl {

	@Override
	public Page<CrawledPage> getCrawlHitsPage(
			Long seoStudioDomainId, Integer maxDocs)
		throws Exception {

		String indexName = _resolveIndexName(seoStudioDomainId);

		int size = (maxDocs == null) ? _DEFAULT_MAX_DOCS : maxDocs;

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.addSorts(_sorts.field("_doc"));
		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(indexName);
		searchSearchRequest.setSize(size);
		searchSearchRequest.setStart(0);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		List<CrawledPage> crawledPages = new ArrayList<>();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Map<String, Object> sourcesMap = searchHit.getSourcesMap();

			if (sourcesMap == null) {
				continue;
			}

			String url = (String)sourcesMap.get("url");

			if (Validator.isNull(url)) {
				continue;
			}

			CrawledPage crawledPage = new CrawledPage();

			crawledPage.setUrl(() -> url);
			crawledPage.setTitle(() -> (String)sourcesMap.get("title"));

			Object linksObject = sourcesMap.get("links");

			if (linksObject instanceof List<?>) {
				List<?> linksList = (List<?>)linksObject;

				List<String> stringLinks = new ArrayList<>(linksList.size());

				for (Object link : linksList) {
					if (link instanceof String) {
						stringLinks.add((String)link);
					}
				}

				crawledPage.setLinks(() -> stringLinks.toArray(new String[0]));
			}

			crawledPages.add(crawledPage);
		}

		return Page.of(crawledPages);
	}

	private String _resolveIndexName(Long seoStudioDomainId) throws Exception {
		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
			seoStudioDomainId);

		try {
			_objectEntryService.getObjectEntry(
				GetterUtil.getLong(
					objectEntry.getValues(
					).get(
						_OBJECT_FIELD_SEO_STUDIO_INSTANCE_ID
					)));
		}
		catch (PortalException portalException) {
			throw new NoSuchObjectEntryException(
				"No object entry found for " + seoStudioDomainId,
				portalException);
		}

		return _INDEX_NAME_PREFIX + seoStudioDomainId;
	}

	private static final int _DEFAULT_MAX_DOCS = 10000;

	private static final String _INDEX_NAME_PREFIX = "seo_studio_";

	private static final String _OBJECT_FIELD_SEO_STUDIO_INSTANCE_ID =
		"r_seoStudioInstanceToSEOStudioDomains_seoStudioInstanceId";

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private Sorts _sorts;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.internal.resource.v1_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.seo.studio.rest.dto.v1_0.PageContent;
import com.liferay.seo.studio.rest.resource.v1_0.PageContentResource;

import jakarta.ws.rs.NotFoundException;

import java.io.Serializable;

import java.net.URI;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author David Truong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-content.properties",
	scope = ServiceScope.PROTOTYPE, service = PageContentResource.class
)
public class PageContentResourceImpl extends BasePageContentResourceImpl {

	@Override
	public PageContent getPageContent(String pageURL) throws Exception {
		URI uri = new URI(pageURL);

		ObjectEntry objectEntry = _getObjectEntry(
			contextCompany.getCompanyId(), "L_SEO_STUDIO_DOMAIN",
			uri.getAuthority());

		if (objectEntry == null) {
			throw new NotFoundException(
				"No SEO Studio domain is registered for the page host");
		}

		long seoStudioDomainId = objectEntry.getObjectEntryId();

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
			throw new NotFoundException(
				"No crawl data exists for the page host");
		}

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(indexName);
		searchSearchRequest.setQuery(QueriesUtil.term("url.keyword", pageURL));
		searchSearchRequest.setSize(1);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		List<SearchHit> hits = searchHits.getSearchHits();

		if (hits.isEmpty()) {
			throw new NotFoundException(
				"No crawl data exists for the page URL");
		}

		SearchHit searchHit = hits.get(0);

		Map<String, Object> sourcesMap = searchHit.getSourcesMap();

		PageContent pageContent = new PageContent();

		pageContent.setContent(
			() -> GetterUtil.getString(sourcesMap.get("full_html")));

		return pageContent;
	}

	private ObjectEntry _getObjectEntry(
			long companyId, String externalReferenceCode, String hostname)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (ObjectEntry objectEntry : objectEntries) {
			Map<String, Serializable> values = objectEntry.getValues();

			if (Objects.equals(
					GetterUtil.getString(values.get("hostname")), hostname)) {

				return objectEntry;
			}
		}

		return null;
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}
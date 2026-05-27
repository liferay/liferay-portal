/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.internal.graphql.query.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.seo.studio.rest.dto.v1_0.CrawledPage;
import com.liferay.seo.studio.rest.resource.v1_0.CrawledPageResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Brooke Dalton
 * @generated
 */
@Generated("")
public class Query {

	public static void setCrawledPageResourceComponentServiceObjects(
		ComponentServiceObjects<CrawledPageResource>
			crawledPageResourceComponentServiceObjects) {

		_crawledPageResourceComponentServiceObjects =
			crawledPageResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {crawlHits(maxDocs: ___, seoStudioDomainId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Returns crawl hits from the Elasticsearch index for the SEO Studio Domain identified by seoStudioDomainId. The index is resolved server-side from the Domain itself, so each Domain has its own index; callers cannot address indexes belonging to Domains they do not own."
	)
	public CrawledPagePage crawlHits(
			@GraphQLName("seoStudioDomainId") Long seoStudioDomainId,
			@GraphQLName("maxDocs") Integer maxDocs)
		throws Exception {

		return _applyComponentServiceObjects(
			_crawledPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			crawledPageResource -> new CrawledPagePage(
				crawledPageResource.getCrawlHitsPage(
					seoStudioDomainId, maxDocs)));
	}

	@GraphQLName("CrawledPagePage")
	public class CrawledPagePage {

		public CrawledPagePage(Page crawledPagePage) {
			actions = crawledPagePage.getActions();

			items = crawledPagePage.getItems();
			lastPage = crawledPagePage.getLastPage();
			page = crawledPagePage.getPage();
			pageSize = crawledPagePage.getPageSize();
			totalCount = crawledPagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CrawledPage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			CrawledPageResource crawledPageResource)
		throws Exception {

		crawledPageResource.setContextAcceptLanguage(_acceptLanguage);
		crawledPageResource.setContextCompany(_company);
		crawledPageResource.setContextHttpServletRequest(_httpServletRequest);
		crawledPageResource.setContextHttpServletResponse(_httpServletResponse);
		crawledPageResource.setContextUriInfo(_uriInfo);
		crawledPageResource.setContextUser(_user);
		crawledPageResource.setGroupLocalService(_groupLocalService);
		crawledPageResource.setResourceActionLocalService(
			_resourceActionLocalService);
		crawledPageResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		crawledPageResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<CrawledPageResource>
		_crawledPageResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}
// LIFERAY-REST-BUILDER-HASH:391836287
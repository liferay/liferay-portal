/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.graphql.query.v1_0;

import com.liferay.headless.cms.dto.v1_0.AssetUsage;
import com.liferay.headless.cms.resource.v1_0.AssetUsageResource;
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
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public class Query {

	public static void setAssetUsageResourceComponentServiceObjects(
		ComponentServiceObjects<AssetUsageResource>
			assetUsageResourceComponentServiceObjects) {

		_assetUsageResourceComponentServiceObjects =
			assetUsageResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetUsagesAsset(assetId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AssetUsagePage assetUsagesAsset(
			@GraphQLName("assetId") Long assetId,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_assetUsageResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetUsageResource -> new AssetUsagePage(
				assetUsageResource.getAssetUsagesAssetPage(
					assetId, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(assetUsageResource, sortsString))));
	}

	@GraphQLName("AssetUsagePage")
	public class AssetUsagePage {

		public AssetUsagePage(Page assetUsagePage) {
			actions = assetUsagePage.getActions();

			items = assetUsagePage.getItems();
			lastPage = assetUsagePage.getLastPage();
			page = assetUsagePage.getPage();
			pageSize = assetUsagePage.getPageSize();
			totalCount = assetUsagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AssetUsage> items;

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

	private void _populateResourceContext(AssetUsageResource assetUsageResource)
		throws Exception {

		assetUsageResource.setContextAcceptLanguage(_acceptLanguage);
		assetUsageResource.setContextCompany(_company);
		assetUsageResource.setContextHttpServletRequest(_httpServletRequest);
		assetUsageResource.setContextHttpServletResponse(_httpServletResponse);
		assetUsageResource.setContextUriInfo(_uriInfo);
		assetUsageResource.setContextUser(_user);
		assetUsageResource.setGroupLocalService(_groupLocalService);
		assetUsageResource.setResourceActionLocalService(
			_resourceActionLocalService);
		assetUsageResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		assetUsageResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AssetUsageResource>
		_assetUsageResourceComponentServiceObjects;

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
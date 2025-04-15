/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.internal.graphql.query.v1_0;

import com.liferay.analytics.reports.rest.dto.v1_0.AssetAppearsOnHistogramMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetDeviceMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetHistogramMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetMetric;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetAppearsOnHistogramMetricResource;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetDeviceMetricResource;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetHistogramMetricResource;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetMetricResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Map;
import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class Query {

	public static void
		setAssetAppearsOnHistogramMetricResourceComponentServiceObjects(
			ComponentServiceObjects<AssetAppearsOnHistogramMetricResource>
				assetAppearsOnHistogramMetricResourceComponentServiceObjects) {

		_assetAppearsOnHistogramMetricResourceComponentServiceObjects =
			assetAppearsOnHistogramMetricResourceComponentServiceObjects;
	}

	public static void setAssetDeviceMetricResourceComponentServiceObjects(
		ComponentServiceObjects<AssetDeviceMetricResource>
			assetDeviceMetricResourceComponentServiceObjects) {

		_assetDeviceMetricResourceComponentServiceObjects =
			assetDeviceMetricResourceComponentServiceObjects;
	}

	public static void setAssetHistogramMetricResourceComponentServiceObjects(
		ComponentServiceObjects<AssetHistogramMetricResource>
			assetHistogramMetricResourceComponentServiceObjects) {

		_assetHistogramMetricResourceComponentServiceObjects =
			assetHistogramMetricResourceComponentServiceObjects;
	}

	public static void setAssetMetricResourceComponentServiceObjects(
		ComponentServiceObjects<AssetMetricResource>
			assetMetricResourceComponentServiceObjects) {

		_assetMetricResourceComponentServiceObjects =
			assetMetricResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {groupAssetMetricAssetTypeAppearsOnHistogram(assetId: ___, assetType: ___, groupId: ___, identityType: ___, rangeKey: ___){assetAppearsOnHistograms}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AssetAppearsOnHistogramMetric
			groupAssetMetricAssetTypeAppearsOnHistogram(
				@GraphQLName("groupId") Long groupId,
				@GraphQLName("assetType") String assetType,
				@GraphQLName("assetId") String assetId,
				@GraphQLName("identityType") String identityType,
				@GraphQLName("rangeKey") Integer rangeKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_assetAppearsOnHistogramMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetAppearsOnHistogramMetricResource ->
				assetAppearsOnHistogramMetricResource.
					getGroupAssetMetricAssetTypeAppearsOnHistogram(
						groupId, assetType, assetId, identityType, rangeKey));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {groupAssetMetricAssetTypeDevice(assetId: ___, assetType: ___, groupId: ___, identityType: ___, rangeKey: ___){deviceMetrics}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AssetDeviceMetric groupAssetMetricAssetTypeDevice(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("assetType") String assetType,
			@GraphQLName("assetId") String assetId,
			@GraphQLName("identityType") String identityType,
			@GraphQLName("rangeKey") Integer rangeKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_assetDeviceMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetDeviceMetricResource ->
				assetDeviceMetricResource.getGroupAssetMetricAssetTypeDevice(
					groupId, assetType, assetId, identityType, rangeKey));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {groupAssetMetricAssetTypeHistogram(assetId: ___, assetType: ___, groupId: ___, identityType: ___, rangeKey: ___){histograms}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AssetHistogramMetric groupAssetMetricAssetTypeHistogram(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("assetType") String assetType,
			@GraphQLName("assetId") String assetId,
			@GraphQLName("identityType") String identityType,
			@GraphQLName("rangeKey") Integer rangeKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_assetHistogramMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetHistogramMetricResource ->
				assetHistogramMetricResource.
					getGroupAssetMetricAssetTypeHistogram(
						groupId, assetType, assetId, identityType, rangeKey));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {groupAssetMetric(assetId: ___, assetType: ___, groupId: ___, identityType: ___, rangeKey: ___, selectedMetrics: ___){assetId, assetTitle, assetType, dataSourceId, defaultMetric, selectedMetrics}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AssetMetric groupAssetMetric(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("assetType") String assetType,
			@GraphQLName("assetId") String assetId,
			@GraphQLName("identityType") String identityType,
			@GraphQLName("rangeKey") Integer rangeKey,
			@GraphQLName("selectedMetrics") String[] selectedMetrics)
		throws Exception {

		return _applyComponentServiceObjects(
			_assetMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetMetricResource -> assetMetricResource.getGroupAssetMetric(
				groupId, assetType, assetId, identityType, rangeKey,
				selectedMetrics));
	}

	@GraphQLName("AssetAppearsOnHistogramMetricPage")
	public class AssetAppearsOnHistogramMetricPage {

		public AssetAppearsOnHistogramMetricPage(
			Page assetAppearsOnHistogramMetricPage) {

			actions = assetAppearsOnHistogramMetricPage.getActions();

			items = assetAppearsOnHistogramMetricPage.getItems();
			lastPage = assetAppearsOnHistogramMetricPage.getLastPage();
			page = assetAppearsOnHistogramMetricPage.getPage();
			pageSize = assetAppearsOnHistogramMetricPage.getPageSize();
			totalCount = assetAppearsOnHistogramMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AssetAppearsOnHistogramMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AssetDeviceMetricPage")
	public class AssetDeviceMetricPage {

		public AssetDeviceMetricPage(Page assetDeviceMetricPage) {
			actions = assetDeviceMetricPage.getActions();

			items = assetDeviceMetricPage.getItems();
			lastPage = assetDeviceMetricPage.getLastPage();
			page = assetDeviceMetricPage.getPage();
			pageSize = assetDeviceMetricPage.getPageSize();
			totalCount = assetDeviceMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AssetDeviceMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AssetHistogramMetricPage")
	public class AssetHistogramMetricPage {

		public AssetHistogramMetricPage(Page assetHistogramMetricPage) {
			actions = assetHistogramMetricPage.getActions();

			items = assetHistogramMetricPage.getItems();
			lastPage = assetHistogramMetricPage.getLastPage();
			page = assetHistogramMetricPage.getPage();
			pageSize = assetHistogramMetricPage.getPageSize();
			totalCount = assetHistogramMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AssetHistogramMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AssetMetricPage")
	public class AssetMetricPage {

		public AssetMetricPage(Page assetMetricPage) {
			actions = assetMetricPage.getActions();

			items = assetMetricPage.getItems();
			lastPage = assetMetricPage.getLastPage();
			page = assetMetricPage.getPage();
			pageSize = assetMetricPage.getPageSize();
			totalCount = assetMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AssetMetric> items;

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
			AssetAppearsOnHistogramMetricResource
				assetAppearsOnHistogramMetricResource)
		throws Exception {

		assetAppearsOnHistogramMetricResource.setContextAcceptLanguage(
			_acceptLanguage);
		assetAppearsOnHistogramMetricResource.setContextCompany(_company);
		assetAppearsOnHistogramMetricResource.setContextHttpServletRequest(
			_httpServletRequest);
		assetAppearsOnHistogramMetricResource.setContextHttpServletResponse(
			_httpServletResponse);
		assetAppearsOnHistogramMetricResource.setContextUriInfo(_uriInfo);
		assetAppearsOnHistogramMetricResource.setContextUser(_user);
		assetAppearsOnHistogramMetricResource.setGroupLocalService(
			_groupLocalService);
		assetAppearsOnHistogramMetricResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			AssetDeviceMetricResource assetDeviceMetricResource)
		throws Exception {

		assetDeviceMetricResource.setContextAcceptLanguage(_acceptLanguage);
		assetDeviceMetricResource.setContextCompany(_company);
		assetDeviceMetricResource.setContextHttpServletRequest(
			_httpServletRequest);
		assetDeviceMetricResource.setContextHttpServletResponse(
			_httpServletResponse);
		assetDeviceMetricResource.setContextUriInfo(_uriInfo);
		assetDeviceMetricResource.setContextUser(_user);
		assetDeviceMetricResource.setGroupLocalService(_groupLocalService);
		assetDeviceMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			AssetHistogramMetricResource assetHistogramMetricResource)
		throws Exception {

		assetHistogramMetricResource.setContextAcceptLanguage(_acceptLanguage);
		assetHistogramMetricResource.setContextCompany(_company);
		assetHistogramMetricResource.setContextHttpServletRequest(
			_httpServletRequest);
		assetHistogramMetricResource.setContextHttpServletResponse(
			_httpServletResponse);
		assetHistogramMetricResource.setContextUriInfo(_uriInfo);
		assetHistogramMetricResource.setContextUser(_user);
		assetHistogramMetricResource.setGroupLocalService(_groupLocalService);
		assetHistogramMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			AssetMetricResource assetMetricResource)
		throws Exception {

		assetMetricResource.setContextAcceptLanguage(_acceptLanguage);
		assetMetricResource.setContextCompany(_company);
		assetMetricResource.setContextHttpServletRequest(_httpServletRequest);
		assetMetricResource.setContextHttpServletResponse(_httpServletResponse);
		assetMetricResource.setContextUriInfo(_uriInfo);
		assetMetricResource.setContextUser(_user);
		assetMetricResource.setGroupLocalService(_groupLocalService);
		assetMetricResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects
		<AssetAppearsOnHistogramMetricResource>
			_assetAppearsOnHistogramMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<AssetDeviceMetricResource>
		_assetDeviceMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<AssetHistogramMetricResource>
		_assetHistogramMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<AssetMetricResource>
		_assetMetricResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}
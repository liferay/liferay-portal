/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.graphql.query.v1_0;

import com.liferay.headless.commerce.machine.learning.dto.v1_0.AccountCategoryForecast;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.AccountForecast;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.SkuForecast;
import com.liferay.headless.commerce.machine.learning.resource.v1_0.AccountCategoryForecastResource;
import com.liferay.headless.commerce.machine.learning.resource.v1_0.AccountForecastResource;
import com.liferay.headless.commerce.machine.learning.resource.v1_0.SkuForecastResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
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

import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class Query {

	public static void
		setAccountCategoryForecastResourceComponentServiceObjects(
			ComponentServiceObjects<AccountCategoryForecastResource>
				accountCategoryForecastResourceComponentServiceObjects) {

		_accountCategoryForecastResourceComponentServiceObjects =
			accountCategoryForecastResourceComponentServiceObjects;
	}

	public static void setAccountForecastResourceComponentServiceObjects(
		ComponentServiceObjects<AccountForecastResource>
			accountForecastResourceComponentServiceObjects) {

		_accountForecastResourceComponentServiceObjects =
			accountForecastResourceComponentServiceObjects;
	}

	public static void setSkuForecastResourceComponentServiceObjects(
		ComponentServiceObjects<SkuForecastResource>
			skuForecastResourceComponentServiceObjects) {

		_skuForecastResourceComponentServiceObjects =
			skuForecastResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountCategoryForecastsByMonthlyRevenue(accountIds: ___, categoryIds: ___, forecastLength: ___, forecastStartDate: ___, historyLength: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Get the forecast points")
	public AccountCategoryForecastPage accountCategoryForecastsByMonthlyRevenue(
			@GraphQLName("accountIds") Long[] accountIds,
			@GraphQLName("categoryIds") Long[] categoryIds,
			@GraphQLName("forecastLength") Integer forecastLength,
			@GraphQLName("forecastStartDate") Date forecastStartDate,
			@GraphQLName("historyLength") Integer historyLength,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountCategoryForecastResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountCategoryForecastResource -> new AccountCategoryForecastPage(
				accountCategoryForecastResource.
					getAccountCategoryForecastsByMonthlyRevenuePage(
						accountIds, categoryIds, forecastLength,
						forecastStartDate, historyLength,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountForecastsByMonthlyRevenue(accountIds: ___, forecastLength: ___, forecastStartDate: ___, historyLength: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Get the forecast points")
	public AccountForecastPage accountForecastsByMonthlyRevenue(
			@GraphQLName("accountIds") Long[] accountIds,
			@GraphQLName("forecastLength") Integer forecastLength,
			@GraphQLName("forecastStartDate") Date forecastStartDate,
			@GraphQLName("historyLength") Integer historyLength,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountForecastResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountForecastResource -> new AccountForecastPage(
				accountForecastResource.getAccountForecastsByMonthlyRevenuePage(
					accountIds, forecastLength, forecastStartDate,
					historyLength, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuForecastsByMonthlyRevenue(forecastLength: ___, forecastStartDate: ___, historyLength: ___, page: ___, pageSize: ___, skus: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Get the forecast points")
	public SkuForecastPage skuForecastsByMonthlyRevenue(
			@GraphQLName("forecastLength") Integer forecastLength,
			@GraphQLName("forecastStartDate") Date forecastStartDate,
			@GraphQLName("historyLength") Integer historyLength,
			@GraphQLName("skus") String[] skus,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuForecastResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuForecastResource -> new SkuForecastPage(
				skuForecastResource.getSkuForecastsByMonthlyRevenuePage(
					forecastLength, forecastStartDate, historyLength, skus,
					Pagination.of(page, pageSize))));
	}

	@GraphQLName("AccountCategoryForecastPage")
	public class AccountCategoryForecastPage {

		public AccountCategoryForecastPage(Page accountCategoryForecastPage) {
			actions = accountCategoryForecastPage.getActions();

			items = accountCategoryForecastPage.getItems();
			lastPage = accountCategoryForecastPage.getLastPage();
			page = accountCategoryForecastPage.getPage();
			pageSize = accountCategoryForecastPage.getPageSize();
			totalCount = accountCategoryForecastPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountCategoryForecast> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AccountForecastPage")
	public class AccountForecastPage {

		public AccountForecastPage(Page accountForecastPage) {
			actions = accountForecastPage.getActions();

			items = accountForecastPage.getItems();
			lastPage = accountForecastPage.getLastPage();
			page = accountForecastPage.getPage();
			pageSize = accountForecastPage.getPageSize();
			totalCount = accountForecastPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountForecast> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SkuForecastPage")
	public class SkuForecastPage {

		public SkuForecastPage(Page skuForecastPage) {
			actions = skuForecastPage.getActions();

			items = skuForecastPage.getItems();
			lastPage = skuForecastPage.getLastPage();
			page = skuForecastPage.getPage();
			pageSize = skuForecastPage.getPageSize();
			totalCount = skuForecastPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SkuForecast> items;

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
			AccountCategoryForecastResource accountCategoryForecastResource)
		throws Exception {

		accountCategoryForecastResource.setContextAcceptLanguage(
			_acceptLanguage);
		accountCategoryForecastResource.setContextCompany(_company);
		accountCategoryForecastResource.setContextHttpServletRequest(
			_httpServletRequest);
		accountCategoryForecastResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountCategoryForecastResource.setContextUriInfo(_uriInfo);
		accountCategoryForecastResource.setContextUser(_user);
		accountCategoryForecastResource.setGroupLocalService(
			_groupLocalService);
		accountCategoryForecastResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			AccountForecastResource accountForecastResource)
		throws Exception {

		accountForecastResource.setContextAcceptLanguage(_acceptLanguage);
		accountForecastResource.setContextCompany(_company);
		accountForecastResource.setContextHttpServletRequest(
			_httpServletRequest);
		accountForecastResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountForecastResource.setContextUriInfo(_uriInfo);
		accountForecastResource.setContextUser(_user);
		accountForecastResource.setGroupLocalService(_groupLocalService);
		accountForecastResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SkuForecastResource skuForecastResource)
		throws Exception {

		skuForecastResource.setContextAcceptLanguage(_acceptLanguage);
		skuForecastResource.setContextCompany(_company);
		skuForecastResource.setContextHttpServletRequest(_httpServletRequest);
		skuForecastResource.setContextHttpServletResponse(_httpServletResponse);
		skuForecastResource.setContextUriInfo(_uriInfo);
		skuForecastResource.setContextUser(_user);
		skuForecastResource.setGroupLocalService(_groupLocalService);
		skuForecastResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AccountCategoryForecastResource>
		_accountCategoryForecastResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountForecastResource>
		_accountForecastResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuForecastResource>
		_skuForecastResourceComponentServiceObjects;

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
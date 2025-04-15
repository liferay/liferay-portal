/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.graphql.query.v1_0;

import com.liferay.analytics.settings.rest.dto.v1_0.Channel;
import com.liferay.analytics.settings.rest.dto.v1_0.CommerceChannel;
import com.liferay.analytics.settings.rest.dto.v1_0.ContactAccountGroup;
import com.liferay.analytics.settings.rest.dto.v1_0.ContactConfiguration;
import com.liferay.analytics.settings.rest.dto.v1_0.ContactOrganization;
import com.liferay.analytics.settings.rest.dto.v1_0.ContactUserGroup;
import com.liferay.analytics.settings.rest.dto.v1_0.Field;
import com.liferay.analytics.settings.rest.dto.v1_0.FieldSummary;
import com.liferay.analytics.settings.rest.dto.v1_0.RecommendationConfiguration;
import com.liferay.analytics.settings.rest.dto.v1_0.Site;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
import com.liferay.analytics.settings.rest.resource.v1_0.CommerceChannelResource;
import com.liferay.analytics.settings.rest.resource.v1_0.ContactAccountGroupResource;
import com.liferay.analytics.settings.rest.resource.v1_0.ContactConfigurationResource;
import com.liferay.analytics.settings.rest.resource.v1_0.ContactOrganizationResource;
import com.liferay.analytics.settings.rest.resource.v1_0.ContactUserGroupResource;
import com.liferay.analytics.settings.rest.resource.v1_0.FieldResource;
import com.liferay.analytics.settings.rest.resource.v1_0.FieldSummaryResource;
import com.liferay.analytics.settings.rest.resource.v1_0.RecommendationConfigurationResource;
import com.liferay.analytics.settings.rest.resource.v1_0.SiteResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Map;
import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class Query {

	public static void setChannelResourceComponentServiceObjects(
		ComponentServiceObjects<ChannelResource>
			channelResourceComponentServiceObjects) {

		_channelResourceComponentServiceObjects =
			channelResourceComponentServiceObjects;
	}

	public static void setCommerceChannelResourceComponentServiceObjects(
		ComponentServiceObjects<CommerceChannelResource>
			commerceChannelResourceComponentServiceObjects) {

		_commerceChannelResourceComponentServiceObjects =
			commerceChannelResourceComponentServiceObjects;
	}

	public static void setContactAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<ContactAccountGroupResource>
			contactAccountGroupResourceComponentServiceObjects) {

		_contactAccountGroupResourceComponentServiceObjects =
			contactAccountGroupResourceComponentServiceObjects;
	}

	public static void setContactConfigurationResourceComponentServiceObjects(
		ComponentServiceObjects<ContactConfigurationResource>
			contactConfigurationResourceComponentServiceObjects) {

		_contactConfigurationResourceComponentServiceObjects =
			contactConfigurationResourceComponentServiceObjects;
	}

	public static void setContactOrganizationResourceComponentServiceObjects(
		ComponentServiceObjects<ContactOrganizationResource>
			contactOrganizationResourceComponentServiceObjects) {

		_contactOrganizationResourceComponentServiceObjects =
			contactOrganizationResourceComponentServiceObjects;
	}

	public static void setContactUserGroupResourceComponentServiceObjects(
		ComponentServiceObjects<ContactUserGroupResource>
			contactUserGroupResourceComponentServiceObjects) {

		_contactUserGroupResourceComponentServiceObjects =
			contactUserGroupResourceComponentServiceObjects;
	}

	public static void setFieldResourceComponentServiceObjects(
		ComponentServiceObjects<FieldResource>
			fieldResourceComponentServiceObjects) {

		_fieldResourceComponentServiceObjects =
			fieldResourceComponentServiceObjects;
	}

	public static void setFieldSummaryResourceComponentServiceObjects(
		ComponentServiceObjects<FieldSummaryResource>
			fieldSummaryResourceComponentServiceObjects) {

		_fieldSummaryResourceComponentServiceObjects =
			fieldSummaryResourceComponentServiceObjects;
	}

	public static void
		setRecommendationConfigurationResourceComponentServiceObjects(
			ComponentServiceObjects<RecommendationConfigurationResource>
				recommendationConfigurationResourceComponentServiceObjects) {

		_recommendationConfigurationResourceComponentServiceObjects =
			recommendationConfigurationResourceComponentServiceObjects;
	}

	public static void setSiteResourceComponentServiceObjects(
		ComponentServiceObjects<SiteResource>
			siteResourceComponentServiceObjects) {

		_siteResourceComponentServiceObjects =
			siteResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channels(keywords: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ChannelPage channels(
			@GraphQLName("keywords") String keywords,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> new ChannelPage(
				channelResource.getChannelsPage(
					keywords, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(channelResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {commerceChannels(keywords: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CommerceChannelPage commerceChannels(
			@GraphQLName("keywords") String keywords,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_commerceChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			commerceChannelResource -> new CommerceChannelPage(
				commerceChannelResource.getCommerceChannelsPage(
					keywords, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						commerceChannelResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contactAccountGroups(keywords: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContactAccountGroupPage contactAccountGroups(
			@GraphQLName("keywords") String keywords,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contactAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			contactAccountGroupResource -> new ContactAccountGroupPage(
				contactAccountGroupResource.getContactAccountGroupsPage(
					keywords, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contactAccountGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contactConfiguration{syncAllAccounts, syncAllContacts, syncedAccountGroupIds, syncedOrganizationIds, syncedUserGroupIds}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContactConfiguration contactConfiguration() throws Exception {
		return _applyComponentServiceObjects(
			_contactConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			contactConfigurationResource ->
				contactConfigurationResource.getContactConfiguration());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contactOrganizations(keywords: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContactOrganizationPage contactOrganizations(
			@GraphQLName("keywords") String keywords,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contactOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			contactOrganizationResource -> new ContactOrganizationPage(
				contactOrganizationResource.getContactOrganizationsPage(
					keywords, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contactOrganizationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contactUserGroups(keywords: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContactUserGroupPage contactUserGroups(
			@GraphQLName("keywords") String keywords,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contactUserGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			contactUserGroupResource -> new ContactUserGroupPage(
				contactUserGroupResource.getContactUserGroupsPage(
					keywords, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contactUserGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {fieldsAccounts(keyword: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public FieldPage fieldsAccounts(
			@GraphQLName("keyword") String keyword,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_fieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			fieldResource -> new FieldPage(
				fieldResource.getFieldsAccountsPage(
					keyword, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(fieldResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {fieldsOrders(keyword: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public FieldPage fieldsOrders(
			@GraphQLName("keyword") String keyword,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_fieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			fieldResource -> new FieldPage(
				fieldResource.getFieldsOrdersPage(
					keyword, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(fieldResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {fieldsPeople(keyword: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public FieldPage fieldsPeople(
			@GraphQLName("keyword") String keyword,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_fieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			fieldResource -> new FieldPage(
				fieldResource.getFieldsPeoplePage(
					keyword, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(fieldResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {fieldsProducts(keyword: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public FieldPage fieldsProducts(
			@GraphQLName("keyword") String keyword,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_fieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			fieldResource -> new FieldPage(
				fieldResource.getFieldsProductsPage(
					keyword, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(fieldResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {field{account, order, people, product}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public FieldSummary field() throws Exception {
		return _applyComponentServiceObjects(
			_fieldSummaryResourceComponentServiceObjects,
			this::_populateResourceContext,
			fieldSummaryResource -> fieldSummaryResource.getField());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {recommendationConfiguration{contentRecommenderMostPopularItems, contentRecommenderUserPersonalization}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public RecommendationConfiguration recommendationConfiguration()
		throws Exception {

		return _applyComponentServiceObjects(
			_recommendationConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			recommendationConfigurationResource ->
				recommendationConfigurationResource.
					getRecommendationConfiguration());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sites(keywords: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SitePage sites(
			@GraphQLName("keywords") String keywords,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteResource -> new SitePage(
				siteResource.getSitesPage(
					keywords, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(siteResource, sortsString))));
	}

	@GraphQLName("ChannelPage")
	public class ChannelPage {

		public ChannelPage(Page channelPage) {
			actions = channelPage.getActions();

			items = channelPage.getItems();
			lastPage = channelPage.getLastPage();
			page = channelPage.getPage();
			pageSize = channelPage.getPageSize();
			totalCount = channelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Channel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CommerceChannelPage")
	public class CommerceChannelPage {

		public CommerceChannelPage(Page commerceChannelPage) {
			actions = commerceChannelPage.getActions();

			items = commerceChannelPage.getItems();
			lastPage = commerceChannelPage.getLastPage();
			page = commerceChannelPage.getPage();
			pageSize = commerceChannelPage.getPageSize();
			totalCount = commerceChannelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CommerceChannel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ContactAccountGroupPage")
	public class ContactAccountGroupPage {

		public ContactAccountGroupPage(Page contactAccountGroupPage) {
			actions = contactAccountGroupPage.getActions();

			items = contactAccountGroupPage.getItems();
			lastPage = contactAccountGroupPage.getLastPage();
			page = contactAccountGroupPage.getPage();
			pageSize = contactAccountGroupPage.getPageSize();
			totalCount = contactAccountGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ContactAccountGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ContactConfigurationPage")
	public class ContactConfigurationPage {

		public ContactConfigurationPage(Page contactConfigurationPage) {
			actions = contactConfigurationPage.getActions();

			items = contactConfigurationPage.getItems();
			lastPage = contactConfigurationPage.getLastPage();
			page = contactConfigurationPage.getPage();
			pageSize = contactConfigurationPage.getPageSize();
			totalCount = contactConfigurationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ContactConfiguration> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ContactOrganizationPage")
	public class ContactOrganizationPage {

		public ContactOrganizationPage(Page contactOrganizationPage) {
			actions = contactOrganizationPage.getActions();

			items = contactOrganizationPage.getItems();
			lastPage = contactOrganizationPage.getLastPage();
			page = contactOrganizationPage.getPage();
			pageSize = contactOrganizationPage.getPageSize();
			totalCount = contactOrganizationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ContactOrganization> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ContactUserGroupPage")
	public class ContactUserGroupPage {

		public ContactUserGroupPage(Page contactUserGroupPage) {
			actions = contactUserGroupPage.getActions();

			items = contactUserGroupPage.getItems();
			lastPage = contactUserGroupPage.getLastPage();
			page = contactUserGroupPage.getPage();
			pageSize = contactUserGroupPage.getPageSize();
			totalCount = contactUserGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ContactUserGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("FieldPage")
	public class FieldPage {

		public FieldPage(Page fieldPage) {
			actions = fieldPage.getActions();

			items = fieldPage.getItems();
			lastPage = fieldPage.getLastPage();
			page = fieldPage.getPage();
			pageSize = fieldPage.getPageSize();
			totalCount = fieldPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Field> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("FieldSummaryPage")
	public class FieldSummaryPage {

		public FieldSummaryPage(Page fieldSummaryPage) {
			actions = fieldSummaryPage.getActions();

			items = fieldSummaryPage.getItems();
			lastPage = fieldSummaryPage.getLastPage();
			page = fieldSummaryPage.getPage();
			pageSize = fieldSummaryPage.getPageSize();
			totalCount = fieldSummaryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<FieldSummary> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("RecommendationConfigurationPage")
	public class RecommendationConfigurationPage {

		public RecommendationConfigurationPage(
			Page recommendationConfigurationPage) {

			actions = recommendationConfigurationPage.getActions();

			items = recommendationConfigurationPage.getItems();
			lastPage = recommendationConfigurationPage.getLastPage();
			page = recommendationConfigurationPage.getPage();
			pageSize = recommendationConfigurationPage.getPageSize();
			totalCount = recommendationConfigurationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<RecommendationConfiguration> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SitePage")
	public class SitePage {

		public SitePage(Page sitePage) {
			actions = sitePage.getActions();

			items = sitePage.getItems();
			lastPage = sitePage.getLastPage();
			page = sitePage.getPage();
			pageSize = sitePage.getPageSize();
			totalCount = sitePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Site> items;

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

	private void _populateResourceContext(ChannelResource channelResource)
		throws Exception {

		channelResource.setContextAcceptLanguage(_acceptLanguage);
		channelResource.setContextCompany(_company);
		channelResource.setContextHttpServletRequest(_httpServletRequest);
		channelResource.setContextHttpServletResponse(_httpServletResponse);
		channelResource.setContextUriInfo(_uriInfo);
		channelResource.setContextUser(_user);
		channelResource.setGroupLocalService(_groupLocalService);
		channelResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			CommerceChannelResource commerceChannelResource)
		throws Exception {

		commerceChannelResource.setContextAcceptLanguage(_acceptLanguage);
		commerceChannelResource.setContextCompany(_company);
		commerceChannelResource.setContextHttpServletRequest(
			_httpServletRequest);
		commerceChannelResource.setContextHttpServletResponse(
			_httpServletResponse);
		commerceChannelResource.setContextUriInfo(_uriInfo);
		commerceChannelResource.setContextUser(_user);
		commerceChannelResource.setGroupLocalService(_groupLocalService);
		commerceChannelResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ContactAccountGroupResource contactAccountGroupResource)
		throws Exception {

		contactAccountGroupResource.setContextAcceptLanguage(_acceptLanguage);
		contactAccountGroupResource.setContextCompany(_company);
		contactAccountGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		contactAccountGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		contactAccountGroupResource.setContextUriInfo(_uriInfo);
		contactAccountGroupResource.setContextUser(_user);
		contactAccountGroupResource.setGroupLocalService(_groupLocalService);
		contactAccountGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ContactConfigurationResource contactConfigurationResource)
		throws Exception {

		contactConfigurationResource.setContextAcceptLanguage(_acceptLanguage);
		contactConfigurationResource.setContextCompany(_company);
		contactConfigurationResource.setContextHttpServletRequest(
			_httpServletRequest);
		contactConfigurationResource.setContextHttpServletResponse(
			_httpServletResponse);
		contactConfigurationResource.setContextUriInfo(_uriInfo);
		contactConfigurationResource.setContextUser(_user);
		contactConfigurationResource.setGroupLocalService(_groupLocalService);
		contactConfigurationResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ContactOrganizationResource contactOrganizationResource)
		throws Exception {

		contactOrganizationResource.setContextAcceptLanguage(_acceptLanguage);
		contactOrganizationResource.setContextCompany(_company);
		contactOrganizationResource.setContextHttpServletRequest(
			_httpServletRequest);
		contactOrganizationResource.setContextHttpServletResponse(
			_httpServletResponse);
		contactOrganizationResource.setContextUriInfo(_uriInfo);
		contactOrganizationResource.setContextUser(_user);
		contactOrganizationResource.setGroupLocalService(_groupLocalService);
		contactOrganizationResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ContactUserGroupResource contactUserGroupResource)
		throws Exception {

		contactUserGroupResource.setContextAcceptLanguage(_acceptLanguage);
		contactUserGroupResource.setContextCompany(_company);
		contactUserGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		contactUserGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		contactUserGroupResource.setContextUriInfo(_uriInfo);
		contactUserGroupResource.setContextUser(_user);
		contactUserGroupResource.setGroupLocalService(_groupLocalService);
		contactUserGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(FieldResource fieldResource)
		throws Exception {

		fieldResource.setContextAcceptLanguage(_acceptLanguage);
		fieldResource.setContextCompany(_company);
		fieldResource.setContextHttpServletRequest(_httpServletRequest);
		fieldResource.setContextHttpServletResponse(_httpServletResponse);
		fieldResource.setContextUriInfo(_uriInfo);
		fieldResource.setContextUser(_user);
		fieldResource.setGroupLocalService(_groupLocalService);
		fieldResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			FieldSummaryResource fieldSummaryResource)
		throws Exception {

		fieldSummaryResource.setContextAcceptLanguage(_acceptLanguage);
		fieldSummaryResource.setContextCompany(_company);
		fieldSummaryResource.setContextHttpServletRequest(_httpServletRequest);
		fieldSummaryResource.setContextHttpServletResponse(
			_httpServletResponse);
		fieldSummaryResource.setContextUriInfo(_uriInfo);
		fieldSummaryResource.setContextUser(_user);
		fieldSummaryResource.setGroupLocalService(_groupLocalService);
		fieldSummaryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			RecommendationConfigurationResource
				recommendationConfigurationResource)
		throws Exception {

		recommendationConfigurationResource.setContextAcceptLanguage(
			_acceptLanguage);
		recommendationConfigurationResource.setContextCompany(_company);
		recommendationConfigurationResource.setContextHttpServletRequest(
			_httpServletRequest);
		recommendationConfigurationResource.setContextHttpServletResponse(
			_httpServletResponse);
		recommendationConfigurationResource.setContextUriInfo(_uriInfo);
		recommendationConfigurationResource.setContextUser(_user);
		recommendationConfigurationResource.setGroupLocalService(
			_groupLocalService);
		recommendationConfigurationResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(SiteResource siteResource)
		throws Exception {

		siteResource.setContextAcceptLanguage(_acceptLanguage);
		siteResource.setContextCompany(_company);
		siteResource.setContextHttpServletRequest(_httpServletRequest);
		siteResource.setContextHttpServletResponse(_httpServletResponse);
		siteResource.setContextUriInfo(_uriInfo);
		siteResource.setContextUser(_user);
		siteResource.setGroupLocalService(_groupLocalService);
		siteResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;
	private static ComponentServiceObjects<CommerceChannelResource>
		_commerceChannelResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContactAccountGroupResource>
		_contactAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContactConfigurationResource>
		_contactConfigurationResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContactOrganizationResource>
		_contactOrganizationResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContactUserGroupResource>
		_contactUserGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<FieldResource>
		_fieldResourceComponentServiceObjects;
	private static ComponentServiceObjects<FieldSummaryResource>
		_fieldSummaryResourceComponentServiceObjects;
	private static ComponentServiceObjects<RecommendationConfigurationResource>
		_recommendationConfigurationResourceComponentServiceObjects;
	private static ComponentServiceObjects<SiteResource>
		_siteResourceComponentServiceObjects;

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
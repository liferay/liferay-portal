/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.user.notification.internal.graphql.query.v1_0;

import com.liferay.headless.user.notification.dto.v1_0.UserNotification;
import com.liferay.headless.user.notification.resource.v1_0.UserNotificationResource;
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

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Carlos Correa
 * @generated
 */
@Generated("")
public class Query {

	public static void setUserNotificationResourceComponentServiceObjects(
		ComponentServiceObjects<UserNotificationResource>
			userNotificationResourceComponentServiceObjects) {

		_userNotificationResourceComponentServiceObjects =
			userNotificationResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {myUserNotifications(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the current user's notifications. Results can be paginated, filtered, searched and sorted."
	)
	public UserNotificationPage myUserNotifications(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userNotificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			userNotificationResource -> new UserNotificationPage(
				userNotificationResource.getMyUserNotificationsPage(
					search,
					_filterBiFunction.apply(
						userNotificationResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						userNotificationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountUserNotifications(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___, userAccountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the user account's notifications. Results can be paginated, filtered, searched and sorted."
	)
	public UserNotificationPage userAccountUserNotifications(
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userNotificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			userNotificationResource -> new UserNotificationPage(
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, search,
					_filterBiFunction.apply(
						userNotificationResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						userNotificationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userNotification(userNotificationId: ___){actions, dateCreated, id, message, read, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user notification.")
	public UserNotification userNotification(
			@GraphQLName("userNotificationId") Long userNotificationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_userNotificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			userNotificationResource ->
				userNotificationResource.getUserNotification(
					userNotificationId));
	}

	@GraphQLName("UserNotificationPage")
	public class UserNotificationPage {

		public UserNotificationPage(Page userNotificationPage) {
			actions = userNotificationPage.getActions();

			items = userNotificationPage.getItems();
			lastPage = userNotificationPage.getLastPage();
			page = userNotificationPage.getPage();
			pageSize = userNotificationPage.getPageSize();
			totalCount = userNotificationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<UserNotification> items;

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
			UserNotificationResource userNotificationResource)
		throws Exception {

		userNotificationResource.setContextAcceptLanguage(_acceptLanguage);
		userNotificationResource.setContextCompany(_company);
		userNotificationResource.setContextHttpServletRequest(
			_httpServletRequest);
		userNotificationResource.setContextHttpServletResponse(
			_httpServletResponse);
		userNotificationResource.setContextUriInfo(_uriInfo);
		userNotificationResource.setContextUser(_user);
		userNotificationResource.setGroupLocalService(_groupLocalService);
		userNotificationResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<UserNotificationResource>
		_userNotificationResourceComponentServiceObjects;

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
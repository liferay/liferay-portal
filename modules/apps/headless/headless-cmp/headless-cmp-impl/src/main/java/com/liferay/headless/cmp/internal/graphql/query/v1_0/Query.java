/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.graphql.query.v1_0;

import com.liferay.headless.cmp.dto.v1_0.ContentCoverage;
import com.liferay.headless.cmp.dto.v1_0.TaskAssignee;
import com.liferay.headless.cmp.dto.v1_0.TaskStatistics;
import com.liferay.headless.cmp.dto.v1_0.UserAccount;
import com.liferay.headless.cmp.dto.v1_0.UserGroup;
import com.liferay.headless.cmp.resource.v1_0.ContentCoverageResource;
import com.liferay.headless.cmp.resource.v1_0.TaskAssigneeResource;
import com.liferay.headless.cmp.resource.v1_0.TaskStatisticsResource;
import com.liferay.headless.cmp.resource.v1_0.UserAccountResource;
import com.liferay.headless.cmp.resource.v1_0.UserGroupResource;
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
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class Query {

	public static void setContentCoverageResourceComponentServiceObjects(
		ComponentServiceObjects<ContentCoverageResource>
			contentCoverageResourceComponentServiceObjects) {

		_contentCoverageResourceComponentServiceObjects =
			contentCoverageResourceComponentServiceObjects;
	}

	public static void setTaskAssigneeResourceComponentServiceObjects(
		ComponentServiceObjects<TaskAssigneeResource>
			taskAssigneeResourceComponentServiceObjects) {

		_taskAssigneeResourceComponentServiceObjects =
			taskAssigneeResourceComponentServiceObjects;
	}

	public static void setTaskStatisticsResourceComponentServiceObjects(
		ComponentServiceObjects<TaskStatisticsResource>
			taskStatisticsResourceComponentServiceObjects) {

		_taskStatisticsResourceComponentServiceObjects =
			taskStatisticsResourceComponentServiceObjects;
	}

	public static void setUserAccountResourceComponentServiceObjects(
		ComponentServiceObjects<UserAccountResource>
			userAccountResourceComponentServiceObjects) {

		_userAccountResourceComponentServiceObjects =
			userAccountResourceComponentServiceObjects;
	}

	public static void setUserGroupResourceComponentServiceObjects(
		ComponentServiceObjects<UserGroupResource>
			userGroupResourceComponentServiceObjects) {

		_userGroupResourceComponentServiceObjects =
			userGroupResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {projectContentCoverage(projectId: ___){assetCount, contentCoverageEntries, funnelStages, personas}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentCoverage projectContentCoverage(
			@GraphQLName("projectId") Long projectId)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentCoverageResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentCoverageResource ->
				contentCoverageResource.getProjectContentCoverage(projectId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taskAssignees(search: ___, type: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaskAssigneePage taskAssignees(
			@GraphQLName("search") String search,
			@GraphQLName("type") String type)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskAssigneeResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskAssigneeResource -> new TaskAssigneePage(
				taskAssigneeResource.getTaskAssigneesPage(search, type)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {projectTaskStatistics(filter: ___, projectId: ___){blockedCount, inProgressCount, overdueCount, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaskStatistics projectTaskStatistics(
			@GraphQLName("projectId") Long projectId,
			@GraphQLName("filter") String filterString)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskStatisticsResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskStatisticsResource ->
				taskStatisticsResource.getProjectTaskStatistics(
					projectId,
					_filterBiFunction.apply(
						taskStatisticsResource, filterString)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taskStatistics(filter: ___){blockedCount, inProgressCount, overdueCount, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaskStatistics taskStatistics(
			@GraphQLName("filter") String filterString)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskStatisticsResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskStatisticsResource -> taskStatisticsResource.getTaskStatistics(
				_filterBiFunction.apply(taskStatisticsResource, filterString)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {projectUserAccounts(page: ___, pageSize: ___, projectId: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public UserAccountPage projectUserAccounts(
			@GraphQLName("projectId") Long projectId,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.getProjectUserAccountsPage(
					projectId, search, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {projectUserGroups(page: ___, pageSize: ___, projectId: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public UserGroupPage projectUserGroups(
			@GraphQLName("projectId") Long projectId,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> new UserGroupPage(
				userGroupResource.getProjectUserGroupsPage(
					projectId, search, Pagination.of(page, pageSize))));
	}

	@GraphQLName("ContentCoveragePage")
	public class ContentCoveragePage {

		public ContentCoveragePage(Page contentCoveragePage) {
			actions = contentCoveragePage.getActions();

			items = contentCoveragePage.getItems();
			lastPage = contentCoveragePage.getLastPage();
			page = contentCoveragePage.getPage();
			pageSize = contentCoveragePage.getPageSize();
			totalCount = contentCoveragePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ContentCoverage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TaskAssigneePage")
	public class TaskAssigneePage {

		public TaskAssigneePage(Page taskAssigneePage) {
			actions = taskAssigneePage.getActions();

			items = taskAssigneePage.getItems();
			lastPage = taskAssigneePage.getLastPage();
			page = taskAssigneePage.getPage();
			pageSize = taskAssigneePage.getPageSize();
			totalCount = taskAssigneePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TaskAssignee> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TaskStatisticsPage")
	public class TaskStatisticsPage {

		public TaskStatisticsPage(Page taskStatisticsPage) {
			actions = taskStatisticsPage.getActions();

			items = taskStatisticsPage.getItems();
			lastPage = taskStatisticsPage.getLastPage();
			page = taskStatisticsPage.getPage();
			pageSize = taskStatisticsPage.getPageSize();
			totalCount = taskStatisticsPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TaskStatistics> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("UserAccountPage")
	public class UserAccountPage {

		public UserAccountPage(Page userAccountPage) {
			actions = userAccountPage.getActions();

			items = userAccountPage.getItems();
			lastPage = userAccountPage.getLastPage();
			page = userAccountPage.getPage();
			pageSize = userAccountPage.getPageSize();
			totalCount = userAccountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<UserAccount> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("UserGroupPage")
	public class UserGroupPage {

		public UserGroupPage(Page userGroupPage) {
			actions = userGroupPage.getActions();

			items = userGroupPage.getItems();
			lastPage = userGroupPage.getLastPage();
			page = userGroupPage.getPage();
			pageSize = userGroupPage.getPageSize();
			totalCount = userGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<UserGroup> items;

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
			ContentCoverageResource contentCoverageResource)
		throws Exception {

		contentCoverageResource.setContextAcceptLanguage(_acceptLanguage);
		contentCoverageResource.setContextCompany(_company);
		contentCoverageResource.setContextHttpServletRequest(
			_httpServletRequest);
		contentCoverageResource.setContextHttpServletResponse(
			_httpServletResponse);
		contentCoverageResource.setContextUriInfo(_uriInfo);
		contentCoverageResource.setContextUser(_user);
		contentCoverageResource.setGroupLocalService(_groupLocalService);
		contentCoverageResource.setResourceActionLocalService(
			_resourceActionLocalService);
		contentCoverageResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		contentCoverageResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TaskAssigneeResource taskAssigneeResource)
		throws Exception {

		taskAssigneeResource.setContextAcceptLanguage(_acceptLanguage);
		taskAssigneeResource.setContextCompany(_company);
		taskAssigneeResource.setContextHttpServletRequest(_httpServletRequest);
		taskAssigneeResource.setContextHttpServletResponse(
			_httpServletResponse);
		taskAssigneeResource.setContextUriInfo(_uriInfo);
		taskAssigneeResource.setContextUser(_user);
		taskAssigneeResource.setGroupLocalService(_groupLocalService);
		taskAssigneeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		taskAssigneeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		taskAssigneeResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TaskStatisticsResource taskStatisticsResource)
		throws Exception {

		taskStatisticsResource.setContextAcceptLanguage(_acceptLanguage);
		taskStatisticsResource.setContextCompany(_company);
		taskStatisticsResource.setContextHttpServletRequest(
			_httpServletRequest);
		taskStatisticsResource.setContextHttpServletResponse(
			_httpServletResponse);
		taskStatisticsResource.setContextUriInfo(_uriInfo);
		taskStatisticsResource.setContextUser(_user);
		taskStatisticsResource.setGroupLocalService(_groupLocalService);
		taskStatisticsResource.setResourceActionLocalService(
			_resourceActionLocalService);
		taskStatisticsResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		taskStatisticsResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			UserAccountResource userAccountResource)
		throws Exception {

		userAccountResource.setContextAcceptLanguage(_acceptLanguage);
		userAccountResource.setContextCompany(_company);
		userAccountResource.setContextHttpServletRequest(_httpServletRequest);
		userAccountResource.setContextHttpServletResponse(_httpServletResponse);
		userAccountResource.setContextUriInfo(_uriInfo);
		userAccountResource.setContextUser(_user);
		userAccountResource.setGroupLocalService(_groupLocalService);
		userAccountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		userAccountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		userAccountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(UserGroupResource userGroupResource)
		throws Exception {

		userGroupResource.setContextAcceptLanguage(_acceptLanguage);
		userGroupResource.setContextCompany(_company);
		userGroupResource.setContextHttpServletRequest(_httpServletRequest);
		userGroupResource.setContextHttpServletResponse(_httpServletResponse);
		userGroupResource.setContextUriInfo(_uriInfo);
		userGroupResource.setContextUser(_user);
		userGroupResource.setGroupLocalService(_groupLocalService);
		userGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		userGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		userGroupResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<ContentCoverageResource>
		_contentCoverageResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaskAssigneeResource>
		_taskAssigneeResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaskStatisticsResource>
		_taskStatisticsResourceComponentServiceObjects;
	private static ComponentServiceObjects<UserAccountResource>
		_userAccountResourceComponentServiceObjects;
	private static ComponentServiceObjects<UserGroupResource>
		_userGroupResourceComponentServiceObjects;

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
// LIFERAY-REST-BUILDER-HASH:339825133
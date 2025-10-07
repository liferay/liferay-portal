/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.internal.graphql.query.v1_0;

import com.liferay.notification.rest.dto.v1_0.NotificationQueueEntry;
import com.liferay.notification.rest.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.resource.v1_0.NotificationQueueEntryResource;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public class Query {

	public static void setNotificationQueueEntryResourceComponentServiceObjects(
		ComponentServiceObjects<NotificationQueueEntryResource>
			notificationQueueEntryResourceComponentServiceObjects) {

		_notificationQueueEntryResourceComponentServiceObjects =
			notificationQueueEntryResourceComponentServiceObjects;
	}

	public static void setNotificationTemplateResourceComponentServiceObjects(
		ComponentServiceObjects<NotificationTemplateResource>
			notificationTemplateResourceComponentServiceObjects) {

		_notificationTemplateResourceComponentServiceObjects =
			notificationTemplateResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {notificationQueueEntries(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NotificationQueueEntryPage notificationQueueEntries(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationQueueEntryResource -> new NotificationQueueEntryPage(
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					search,
					_filterBiFunction.apply(
						notificationQueueEntryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						notificationQueueEntryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {notificationQueueEntry(notificationQueueEntryId: ___){actions, body, fromName, id, recipients, recipientsSummary, sentDate, status, subject, triggerBy, type, typeLabel}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NotificationQueueEntry notificationQueueEntry(
			@GraphQLName("notificationQueueEntryId") Long
				notificationQueueEntryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationQueueEntryResource ->
				notificationQueueEntryResource.getNotificationQueueEntry(
					notificationQueueEntryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {notificationTemplate(notificationTemplateId: ___){actions, attachmentObjectFieldExternalReferenceCodes, attachmentObjectFieldIds, body, dateCreated, dateModified, description, editorType, externalReferenceCode, id, name, name_i18n, objectDefinitionExternalReferenceCode, objectDefinitionId, recipientType, recipients, subject, system, type, typeLabel}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NotificationTemplate notificationTemplate(
			@GraphQLName("notificationTemplateId") Long notificationTemplateId)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.getNotificationTemplate(
					notificationTemplateId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {notificationTemplateByExternalReferenceCode(externalReferenceCode: ___){actions, attachmentObjectFieldExternalReferenceCodes, attachmentObjectFieldIds, body, dateCreated, dateModified, description, editorType, externalReferenceCode, id, name, name_i18n, objectDefinitionExternalReferenceCode, objectDefinitionId, recipientType, recipients, subject, system, type, typeLabel}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NotificationTemplate notificationTemplateByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.
					getNotificationTemplateByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {notificationTemplates(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NotificationTemplatePage notificationTemplates(
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource -> new NotificationTemplatePage(
				notificationTemplateResource.getNotificationTemplatesPage(
					search,
					_aggregationBiFunction.apply(
						notificationTemplateResource, aggregations),
					_filterBiFunction.apply(
						notificationTemplateResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						notificationTemplateResource, sortsString))));
	}

	@GraphQLName("NotificationQueueEntryPage")
	public class NotificationQueueEntryPage {

		public NotificationQueueEntryPage(Page notificationQueueEntryPage) {
			actions = notificationQueueEntryPage.getActions();

			facets = notificationQueueEntryPage.getFacets();

			items = notificationQueueEntryPage.getItems();
			lastPage = notificationQueueEntryPage.getLastPage();
			page = notificationQueueEntryPage.getPage();
			pageSize = notificationQueueEntryPage.getPageSize();
			totalCount = notificationQueueEntryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<NotificationQueueEntry> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("NotificationTemplatePage")
	public class NotificationTemplatePage {

		public NotificationTemplatePage(Page notificationTemplatePage) {
			actions = notificationTemplatePage.getActions();

			facets = notificationTemplatePage.getFacets();

			items = notificationTemplatePage.getItems();
			lastPage = notificationTemplatePage.getLastPage();
			page = notificationTemplatePage.getPage();
			pageSize = notificationTemplatePage.getPageSize();
			totalCount = notificationTemplatePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<NotificationTemplate> items;

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
			NotificationQueueEntryResource notificationQueueEntryResource)
		throws Exception {

		notificationQueueEntryResource.setContextAcceptLanguage(
			_acceptLanguage);
		notificationQueueEntryResource.setContextCompany(_company);
		notificationQueueEntryResource.setContextHttpServletRequest(
			_httpServletRequest);
		notificationQueueEntryResource.setContextHttpServletResponse(
			_httpServletResponse);
		notificationQueueEntryResource.setContextUriInfo(_uriInfo);
		notificationQueueEntryResource.setContextUser(_user);
		notificationQueueEntryResource.setGroupLocalService(_groupLocalService);
		notificationQueueEntryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		notificationQueueEntryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		notificationQueueEntryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			NotificationTemplateResource notificationTemplateResource)
		throws Exception {

		notificationTemplateResource.setContextAcceptLanguage(_acceptLanguage);
		notificationTemplateResource.setContextCompany(_company);
		notificationTemplateResource.setContextHttpServletRequest(
			_httpServletRequest);
		notificationTemplateResource.setContextHttpServletResponse(
			_httpServletResponse);
		notificationTemplateResource.setContextUriInfo(_uriInfo);
		notificationTemplateResource.setContextUser(_user);
		notificationTemplateResource.setGroupLocalService(_groupLocalService);
		notificationTemplateResource.setResourceActionLocalService(
			_resourceActionLocalService);
		notificationTemplateResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		notificationTemplateResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<NotificationQueueEntryResource>
		_notificationQueueEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<NotificationTemplateResource>
		_notificationTemplateResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
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
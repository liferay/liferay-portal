/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.internal.graphql.mutation.v1_0;

import com.liferay.notification.rest.dto.v1_0.NotificationQueueEntry;
import com.liferay.notification.rest.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.resource.v1_0.NotificationQueueEntryResource;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public class Mutation {

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

	@GraphQLField
	public Response createNotificationQueueEntriesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationQueueEntryResource ->
				notificationQueueEntryResource.
					postNotificationQueueEntriesPageExportBatch(
						search,
						_filterBiFunction.apply(
							notificationQueueEntryResource, filterString),
						_sortsBiFunction.apply(
							notificationQueueEntryResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public NotificationQueueEntry createNotificationQueueEntry(
			@GraphQLName("notificationQueueEntry") NotificationQueueEntry
				notificationQueueEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationQueueEntryResource ->
				notificationQueueEntryResource.postNotificationQueueEntry(
					notificationQueueEntry));
	}

	@GraphQLField
	public Response createNotificationQueueEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationQueueEntryResource ->
				notificationQueueEntryResource.postNotificationQueueEntryBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteNotificationQueueEntry(
			@GraphQLName("notificationQueueEntryId") Long
				notificationQueueEntryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationQueueEntryResource ->
				notificationQueueEntryResource.deleteNotificationQueueEntry(
					notificationQueueEntryId));

		return true;
	}

	@GraphQLField
	public Response deleteNotificationQueueEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationQueueEntryResource ->
				notificationQueueEntryResource.
					deleteNotificationQueueEntryBatch(callbackURL, object));
	}

	@GraphQLField
	public boolean updateNotificationQueueEntryResend(
			@GraphQLName("notificationQueueEntryId") Long
				notificationQueueEntryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationQueueEntryResource ->
				notificationQueueEntryResource.putNotificationQueueEntryResend(
					notificationQueueEntryId));

		return true;
	}

	@GraphQLField
	public Response createNotificationTemplatesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.
					postNotificationTemplatesPageExportBatch(
						search,
						_filterBiFunction.apply(
							notificationTemplateResource, filterString),
						_sortsBiFunction.apply(
							notificationTemplateResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public NotificationTemplate createNotificationTemplate(
			@GraphQLName("notificationTemplate") NotificationTemplate
				notificationTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.postNotificationTemplate(
					notificationTemplate));
	}

	@GraphQLField
	public Response createNotificationTemplateBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.postNotificationTemplateBatch(
					callbackURL, object));
	}

	@GraphQLField
	public NotificationTemplate
			updateNotificationTemplateByExternalReferenceCode(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("notificationTemplate") NotificationTemplate
					notificationTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.
					putNotificationTemplateByExternalReferenceCode(
						externalReferenceCode, notificationTemplate));
	}

	@GraphQLField
	public boolean deleteNotificationTemplate(
			@GraphQLName("notificationTemplateId") Long notificationTemplateId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.deleteNotificationTemplate(
					notificationTemplateId));

		return true;
	}

	@GraphQLField
	public Response deleteNotificationTemplateBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.deleteNotificationTemplateBatch(
					callbackURL, object));
	}

	@GraphQLField
	public NotificationTemplate patchNotificationTemplate(
			@GraphQLName("notificationTemplateId") Long notificationTemplateId,
			@GraphQLName("notificationTemplate") NotificationTemplate
				notificationTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.patchNotificationTemplate(
					notificationTemplateId, notificationTemplate));
	}

	@GraphQLField
	public NotificationTemplate updateNotificationTemplate(
			@GraphQLName("notificationTemplateId") Long notificationTemplateId,
			@GraphQLName("notificationTemplate") NotificationTemplate
				notificationTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.putNotificationTemplate(
					notificationTemplateId, notificationTemplate));
	}

	@GraphQLField
	public Response updateNotificationTemplateBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.putNotificationTemplateBatch(
					callbackURL, object));
	}

	@GraphQLField
	public NotificationTemplate createNotificationTemplateCopy(
			@GraphQLName("notificationTemplateId") Long notificationTemplateId)
		throws Exception {

		return _applyComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			notificationTemplateResource ->
				notificationTemplateResource.postNotificationTemplateCopy(
					notificationTemplateId));
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

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
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
		notificationQueueEntryResource.setRoleLocalService(_roleLocalService);

		notificationQueueEntryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		notificationQueueEntryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		notificationTemplateResource.setRoleLocalService(_roleLocalService);

		notificationTemplateResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		notificationTemplateResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<NotificationQueueEntryResource>
		_notificationQueueEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<NotificationTemplateResource>
		_notificationTemplateResourceComponentServiceObjects;

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
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}
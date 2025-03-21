/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.internal.graphql.servlet.v1_0;

import com.liferay.notification.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.notification.rest.internal.graphql.query.v1_0.Query;
import com.liferay.notification.rest.internal.resource.v1_0.NotificationQueueEntryResourceImpl;
import com.liferay.notification.rest.internal.resource.v1_0.NotificationTemplateResourceImpl;
import com.liferay.notification.rest.resource.v1_0.NotificationQueueEntryResource;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setNotificationQueueEntryResourceComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects);
		Mutation.setNotificationTemplateResourceComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects);

		Query.setNotificationQueueEntryResourceComponentServiceObjects(
			_notificationQueueEntryResourceComponentServiceObjects);
		Query.setNotificationTemplateResourceComponentServiceObjects(
			_notificationTemplateResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Notification.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/notification-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#createNotificationQueueEntriesPageExportBatch",
						new ObjectValuePair<>(
							NotificationQueueEntryResourceImpl.class,
							"postNotificationQueueEntriesPageExportBatch"));
					put(
						"mutation#createNotificationQueueEntry",
						new ObjectValuePair<>(
							NotificationQueueEntryResourceImpl.class,
							"postNotificationQueueEntry"));
					put(
						"mutation#createNotificationQueueEntryBatch",
						new ObjectValuePair<>(
							NotificationQueueEntryResourceImpl.class,
							"postNotificationQueueEntryBatch"));
					put(
						"mutation#deleteNotificationQueueEntry",
						new ObjectValuePair<>(
							NotificationQueueEntryResourceImpl.class,
							"deleteNotificationQueueEntry"));
					put(
						"mutation#deleteNotificationQueueEntryBatch",
						new ObjectValuePair<>(
							NotificationQueueEntryResourceImpl.class,
							"deleteNotificationQueueEntryBatch"));
					put(
						"mutation#updateNotificationQueueEntryResend",
						new ObjectValuePair<>(
							NotificationQueueEntryResourceImpl.class,
							"putNotificationQueueEntryResend"));
					put(
						"mutation#createNotificationTemplatesPageExportBatch",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"postNotificationTemplatesPageExportBatch"));
					put(
						"mutation#createNotificationTemplate",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"postNotificationTemplate"));
					put(
						"mutation#createNotificationTemplateBatch",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"postNotificationTemplateBatch"));
					put(
						"mutation#updateNotificationTemplateByExternalReferenceCode",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"putNotificationTemplateByExternalReferenceCode"));
					put(
						"mutation#deleteNotificationTemplate",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"deleteNotificationTemplate"));
					put(
						"mutation#deleteNotificationTemplateBatch",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"deleteNotificationTemplateBatch"));
					put(
						"mutation#patchNotificationTemplate",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"patchNotificationTemplate"));
					put(
						"mutation#updateNotificationTemplate",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"putNotificationTemplate"));
					put(
						"mutation#updateNotificationTemplateBatch",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"putNotificationTemplateBatch"));
					put(
						"mutation#createNotificationTemplateCopy",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"postNotificationTemplateCopy"));

					put(
						"query#notificationQueueEntries",
						new ObjectValuePair<>(
							NotificationQueueEntryResourceImpl.class,
							"getNotificationQueueEntriesPage"));
					put(
						"query#notificationQueueEntry",
						new ObjectValuePair<>(
							NotificationQueueEntryResourceImpl.class,
							"getNotificationQueueEntry"));
					put(
						"query#notificationTemplates",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"getNotificationTemplatesPage"));
					put(
						"query#notificationTemplateByExternalReferenceCode",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"getNotificationTemplateByExternalReferenceCode"));
					put(
						"query#notificationTemplate",
						new ObjectValuePair<>(
							NotificationTemplateResourceImpl.class,
							"getNotificationTemplate"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<NotificationQueueEntryResource>
		_notificationQueueEntryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<NotificationTemplateResource>
		_notificationTemplateResourceComponentServiceObjects;

}
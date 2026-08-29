/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.internal.resource.v1_0;

import com.liferay.notification.constants.NotificationActionKeys;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.exception.NotificationQueueEntryTypeException;
import com.liferay.notification.handler.NotificationHandler;
import com.liferay.notification.handler.NotificationHandlerTracker;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.rest.dto.v1_0.NotificationQueueEntry;
import com.liferay.notification.rest.dto.v1_0.util.NotificationUtil;
import com.liferay.notification.rest.internal.odata.entity.v1_0.NotificationQueueEntryEntityModel;
import com.liferay.notification.rest.resource.v1_0.NotificationQueueEntryResource;
import com.liferay.notification.service.NotificationQueueEntryService;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.type.NotificationTypeServiceTracker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Paulo Albuquerque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/notification-queue-entry.properties",
	scope = ServiceScope.PROTOTYPE,
	service = NotificationQueueEntryResource.class
)
public class NotificationQueueEntryResourceImpl
	extends BaseNotificationQueueEntryResourceImpl {

	@Override
	public void deleteNotificationQueueEntry(Long notificationQueueEntryId)
		throws Exception {

		_notificationQueueEntryService.deleteNotificationQueueEntry(
			notificationQueueEntryId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<NotificationQueueEntry> getNotificationQueueEntriesPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					NotificationActionKeys.ADD_NOTIFICATION_QUEUE_ENTRY,
					"postNotificationQueueEntry",
					NotificationConstants.RESOURCE_NAME_NOTIFICATION_QUEUE,
					contextCompany.getCompanyId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getNotificationQueueEntriesPage",
					NotificationConstants.RESOURCE_NAME_NOTIFICATION_QUEUE,
					contextCompany.getCompanyId())
			).build(),
			booleanQuery -> {
			},
			filter,
			com.liferay.notification.model.NotificationQueueEntry.class.
				getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toNotificationQueueEntry(
				_notificationQueueEntryService.getNotificationQueueEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public NotificationQueueEntry getNotificationQueueEntry(
			Long notificationQueueEntryId)
		throws Exception {

		return _toNotificationQueueEntry(
			_notificationQueueEntryService.getNotificationQueueEntry(
				notificationQueueEntryId));
	}

	@Override
	public NotificationQueueEntry postNotificationQueueEntry(
			NotificationQueueEntry notificationQueueEntry)
		throws Exception {

		if (!StringUtil.equals(
				notificationQueueEntry.getType(),
				NotificationConstants.TYPE_EMAIL)) {

			throw new NotificationQueueEntryTypeException(
				"Type can only be email");
		}

		NotificationContext notificationContext = new NotificationContext();

		notificationContext.setCompanyId(contextCompany.getCompanyId());

		NotificationType notificationType =
			_notificationTypeServiceTracker.getNotificationType(
				NotificationConstants.TYPE_EMAIL);

		notificationContext.setNotificationQueueEntry(
			notificationType.createNotificationQueueEntry(
				contextUser, notificationQueueEntry.getBody(),
				notificationContext, notificationQueueEntry.getSubject()));

		notificationContext.setNotificationRecipient(
			NotificationUtil.toNotificationRecipient(contextUser, 0L));

		for (Object recipient : notificationQueueEntry.getRecipients()) {
			Map<String, Object> recipientMap = (Map<String, Object>)recipient;

			recipientMap.putAll(
				notificationType.evaluateNotificationRecipientSettings(
					contextCompany.getCompanyId(), notificationContext,
					recipientMap));
		}

		notificationContext.setNotificationRecipientSettings(
			NotificationUtil.toNotificationRecipientSetting(
				0L, notificationType, notificationQueueEntry.getRecipients(),
				contextUser));

		notificationContext.setType(NotificationConstants.TYPE_EMAIL);

		com.liferay.notification.model.NotificationQueueEntry
			serviceBuilderNotificationQueueEntry =
				_notificationQueueEntryService.addNotificationQueueEntry(
					notificationContext);

		notificationType.sendNotification(serviceBuilderNotificationQueueEntry);

		return _toNotificationQueueEntry(serviceBuilderNotificationQueueEntry);
	}

	@Override
	public void putNotificationQueueEntryResend(Long notificationQueueEntryId)
		throws Exception {

		_notificationQueueEntryService.resendNotificationQueueEntry(
			notificationQueueEntryId);
	}

	private Locale _getLocale() {
		if (contextUser != null) {
			return contextUser.getLocale();
		}

		return contextAcceptLanguage.getPreferredLocale();
	}

	private NotificationQueueEntry _toNotificationQueueEntry(
			com.liferay.notification.model.NotificationQueueEntry
				serviceBuilderNotificationQueueEntry)
		throws PortalException {

		NotificationType notificationType =
			_notificationTypeServiceTracker.getNotificationType(
				serviceBuilderNotificationQueueEntry.getType());

		return new NotificationQueueEntry() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"delete",
						addAction(
							ActionKeys.DELETE, "deleteNotificationQueueEntry",
							com.liferay.notification.model.
								NotificationQueueEntry.class.getName(),
							serviceBuilderNotificationQueueEntry.
								getNotificationQueueEntryId())
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, "getNotificationQueueEntry",
							com.liferay.notification.model.
								NotificationQueueEntry.class.getName(),
							serviceBuilderNotificationQueueEntry.
								getNotificationQueueEntryId())
					).put(
						"update",
						() -> {
							int status =
								serviceBuilderNotificationQueueEntry.
									getStatus();

							if (status ==
									NotificationQueueEntryConstants.
										STATUS_SENT) {

								return null;
							}

							return addAction(
								ActionKeys.UPDATE,
								"putNotificationQueueEntryResend",
								com.liferay.notification.model.
									NotificationQueueEntry.class.getName(),
								serviceBuilderNotificationQueueEntry.
									getNotificationQueueEntryId());
						}
					).build());
				setBody(serviceBuilderNotificationQueueEntry::getBody);
				setFromName(
					() -> notificationType.getFromName(
						serviceBuilderNotificationQueueEntry));
				setId(
					() ->
						serviceBuilderNotificationQueueEntry.
							getNotificationQueueEntryId());
				setRecipients(
					() -> {
						NotificationRecipient notificationRecipient =
							serviceBuilderNotificationQueueEntry.
								getNotificationRecipient();

						return notificationType.toRecipients(
							notificationRecipient.
								getNotificationRecipientSettings());
					});
				setRecipientsSummary(
					() -> notificationType.getRecipientSummary(
						serviceBuilderNotificationQueueEntry));
				setSentDate(serviceBuilderNotificationQueueEntry::getSentDate);
				setStatus(serviceBuilderNotificationQueueEntry::getStatus);
				setSubject(serviceBuilderNotificationQueueEntry::getSubject);
				setTriggerBy(
					() -> {
						long classNameId =
							serviceBuilderNotificationQueueEntry.
								getClassNameId();

						if (classNameId == 0) {
							return _language.get(
								contextAcceptLanguage.getPreferredLocale(),
								"added-via-api");
						}

						NotificationHandler notificationHandler =
							_notificationHandlerTracker.getNotificationHandler(
								_portal.fetchClassName(classNameId));

						if (notificationHandler != null) {
							return notificationHandler.getTriggerBy(
								contextAcceptLanguage.getPreferredLocale());
						}

						return _language.get(
							contextAcceptLanguage.getPreferredLocale(),
							"missing-object-definition");
					});
				setType(serviceBuilderNotificationQueueEntry::getType);
				setTypeLabel(
					() -> _language.get(
						_getLocale(), notificationType.getTypeLanguageKey()));
			}
		};
	}

	private static final EntityModel _entityModel =
		new NotificationQueueEntryEntityModel();

	@Reference
	private Language _language;

	@Reference
	private NotificationHandlerTracker _notificationHandlerTracker;

	@Reference
	private NotificationQueueEntryService _notificationQueueEntryService;

	@Reference
	private NotificationTypeServiceTracker _notificationTypeServiceTracker;

	@Reference
	private Portal _portal;

}
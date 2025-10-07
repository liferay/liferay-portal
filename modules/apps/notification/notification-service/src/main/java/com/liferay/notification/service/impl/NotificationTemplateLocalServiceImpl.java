/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.impl;

import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.internal.template.util.NotificationTemplateUtil;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.model.NotificationTemplateAttachment;
import com.liferay.notification.service.NotificationRecipientLocalService;
import com.liferay.notification.service.NotificationRecipientSettingLocalService;
import com.liferay.notification.service.NotificationTemplateAttachmentLocalService;
import com.liferay.notification.service.base.NotificationTemplateLocalServiceBaseImpl;
import com.liferay.notification.service.persistence.NotificationQueueEntryPersistence;
import com.liferay.notification.service.persistence.NotificationTemplateAttachmentPersistence;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.type.NotificationTypeServiceTracker;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 * @author Gustavo Lima
 */
@Component(
	property = "model.class.name=com.liferay.notification.model.NotificationTemplate",
	service = AopService.class
)
public class NotificationTemplateLocalServiceImpl
	extends NotificationTemplateLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationTemplate addNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		NotificationTemplateUtil.validateInvokerBundle(
			"Only allowed bundles can add system notification templates",
			notificationTemplate.isSystem());

		_validate(notificationContext);

		notificationTemplate.setNotificationTemplateId(
			counterLocalService.increment());

		notificationTemplate = notificationTemplatePersistence.update(
			notificationTemplate);

		_resourceLocalService.addResources(
			notificationTemplate.getCompanyId(), 0,
			notificationTemplate.getUserId(),
			NotificationTemplate.class.getName(),
			notificationTemplate.getNotificationTemplateId(), false, true,
			true);

		NotificationRecipient notificationRecipient =
			notificationContext.getNotificationRecipient();

		notificationRecipient.setNotificationRecipientId(
			counterLocalService.increment());
		notificationRecipient.setClassNameId(
			_portal.getClassNameId(NotificationTemplate.class));
		notificationRecipient.setClassPK(
			notificationTemplate.getNotificationTemplateId());

		notificationRecipient =
			_notificationRecipientLocalService.updateNotificationRecipient(
				notificationRecipient);

		for (NotificationRecipientSetting notificationRecipientSetting :
				notificationContext.getNotificationRecipientSettings()) {

			notificationRecipientSetting.setNotificationRecipientSettingId(
				counterLocalService.increment());
			notificationRecipientSetting.setNotificationRecipientId(
				notificationRecipient.getNotificationRecipientId());

			_notificationRecipientSettingLocalService.
				updateNotificationRecipientSetting(
					notificationRecipientSetting);
		}

		for (long attachmentObjectFieldId :
				notificationContext.getAttachmentObjectFieldIds()) {

			_notificationTemplateAttachmentLocalService.
				addNotificationTemplateAttachment(
					notificationTemplate.getCompanyId(),
					notificationTemplate.getNotificationTemplateId(),
					attachmentObjectFieldId);
		}

		return notificationTemplate;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationTemplate addNotificationTemplate(
			String externalReferenceCode, long userId, String type)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			externalReferenceCode, user, null, externalReferenceCode, null,
			null, false, type);

		NotificationRecipient notificationRecipient =
			_notificationRecipientLocalService.addNotificationRecipient(
				user.getUserId(),
				_portal.getClassNameId(NotificationTemplate.class),
				notificationTemplate.getNotificationTemplateId());

		_notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(
				user.getUserId(),
				notificationRecipient.getNotificationRecipientId(),
				NotificationRecipientSettingConstants.NAME_FROM,
				externalReferenceCode);
		_notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(
				user.getUserId(),
				notificationRecipient.getNotificationRecipientId(),
				NotificationRecipientSettingConstants.NAME_FROM_NAME,
				LocalizedMapUtil.getLocalizedMap(externalReferenceCode));
		_notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(
				user.getUserId(),
				notificationRecipient.getNotificationRecipientId(),
				NotificationRecipientSettingConstants.NAME_TO,
				LocalizedMapUtil.getLocalizedMap(externalReferenceCode));

		return notificationTemplate;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationTemplate addSubscriptionNotificationTemplate(
			String externalReferenceCode, long userId)
		throws PortalException {

		Map<String, Map<String, String>> subscriptionNotificationTemplates =
			NotificationTemplateConstants.
				getSubscriptionNotificationTemplates();
		User user = _userLocalService.getUser(userId);

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			externalReferenceCode, user,
			MapUtil.getString(
				subscriptionNotificationTemplates.get(externalReferenceCode),
				"body"),
			MapUtil.getString(
				subscriptionNotificationTemplates.get(externalReferenceCode),
				"name"),
			NotificationRecipientConstants.TYPE_EMAIL,
			MapUtil.getString(
				subscriptionNotificationTemplates.get(externalReferenceCode),
				"subject"),
			true, NotificationConstants.TYPE_EMAIL);

		NotificationRecipient notificationRecipient =
			_notificationRecipientLocalService.addNotificationRecipient(
				user.getUserId(),
				_portal.getClassNameId(NotificationTemplate.class),
				notificationTemplate.getNotificationTemplateId());

		_notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(
				user.getUserId(),
				notificationRecipient.getNotificationRecipientId(),
				NotificationRecipientSettingConstants.NAME_FROM,
				"team@liferay.com");
		_notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(
				user.getUserId(),
				notificationRecipient.getNotificationRecipientId(),
				NotificationRecipientSettingConstants.NAME_FROM_NAME,
				LocalizedMapUtil.getLocalizedMap("Liferay Team"));
		_notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(
				user.getUserId(),
				notificationRecipient.getNotificationRecipientId(),
				NotificationRecipientSettingConstants.NAME_SINGLE_RECIPIENT,
				Boolean.TRUE.toString());
		_notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(
				user.getUserId(),
				notificationRecipient.getNotificationRecipientId(),
				NotificationRecipientSettingConstants.NAME_TO_TYPE,
				NotificationRecipientConstants.TYPE_SUBSCRIBERS);

		return notificationTemplate;
	}

	@Override
	public void deleteCompanyNotificationTemplates(long companyId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq("companyId", companyId)));
		actionableDynamicQuery.setPerformActionMethod(
			(NotificationTemplate notificationTemplate) ->
				deleteNotificationTemplate(notificationTemplate));

		actionableDynamicQuery.performActions();
	}

	@Override
	public NotificationTemplate deleteNotificationTemplate(
			long notificationTemplateId)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationTemplatePersistence.findByPrimaryKey(
				notificationTemplateId);

		return notificationTemplateLocalService.deleteNotificationTemplate(
			notificationTemplate);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public NotificationTemplate deleteNotificationTemplate(
			NotificationTemplate notificationTemplate)
		throws PortalException {

		NotificationTemplateUtil.validateInvokerBundle(
			"Only allowed bundles can delete system notification templates",
			notificationTemplate.isSystem());

		notificationTemplate = notificationTemplatePersistence.remove(
			notificationTemplate);

		_resourceLocalService.deleteResource(
			notificationTemplate, ResourceConstants.SCOPE_INDIVIDUAL);

		NotificationRecipient notificationRecipient =
			notificationTemplate.getNotificationRecipient();

		_notificationRecipientLocalService.deleteNotificationRecipient(
			notificationRecipient);

		for (NotificationRecipientSetting notificationRecipientSetting :
				notificationRecipient.getNotificationRecipientSettings()) {

			_notificationRecipientSettingLocalService.
				deleteNotificationRecipientSetting(
					notificationRecipientSetting);
		}

		List<NotificationQueueEntry> notificationQueueEntries =
			_notificationQueueEntryPersistence.findByNotificationTemplateId(
				notificationTemplate.getNotificationTemplateId());

		for (NotificationQueueEntry notificationQueueEntry :
				notificationQueueEntries) {

			notificationQueueEntry.setNotificationTemplateId(0);

			_notificationQueueEntryPersistence.update(notificationQueueEntry);
		}

		_notificationTemplateAttachmentPersistence.
			removeByNotificationTemplateId(
				notificationTemplate.getNotificationTemplateId());

		return notificationTemplate;
	}

	@Override
	public NotificationTemplate getNotificationTemplate(
			long notificationTemplateId)
		throws PortalException {

		return notificationTemplatePersistence.findByPrimaryKey(
			notificationTemplateId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationTemplate updateNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		String name = notificationTemplate.getName();

		notificationTemplate = notificationTemplatePersistence.findByPrimaryKey(
			notificationTemplate.getNotificationTemplateId());

		if (!FeatureFlagManagerUtil.isEnabled(
				notificationTemplate.getCompanyId(), "LPD-17564")) {

			NotificationTemplateUtil.validateInvokerBundle(
				"Only allowed bundles can update system notification templates",
				notificationTemplate.isSystem());
		}

		_validate(notificationContext);

		NotificationRecipient notificationRecipient =
			_notificationRecipientLocalService.updateNotificationRecipient(
				notificationContext.getNotificationRecipient());

		if (FeatureFlagManagerUtil.isEnabled(
				notificationTemplate.getCompanyId(), "LPD-17564") &&
			notificationTemplate.isSystem() &&
			!ObjectDefinitionUtil.isInvokerBundleAllowed()) {

			notificationTemplate.setName(name);

			Map<String, Object> notificationRecipientSettings =
				NotificationRecipientSettingUtil.toMap(
					notificationContext.getNotificationRecipientSettings());

			_notificationRecipientSettingLocalService.
				updateNotificationRecipientSetting(
					notificationRecipient.getNotificationRecipientId(),
					NotificationRecipientSettingConstants.NAME_FROM,
					notificationRecipientSettings.get(
						NotificationRecipientSettingConstants.NAME_FROM));
			_notificationRecipientSettingLocalService.
				updateNotificationRecipientSetting(
					notificationRecipient.getNotificationRecipientId(),
					NotificationRecipientSettingConstants.NAME_FROM_NAME,
					notificationRecipientSettings.get(
						NotificationRecipientSettingConstants.NAME_FROM_NAME));

			return notificationTemplatePersistence.update(notificationTemplate);
		}

		for (NotificationRecipientSetting notificationRecipientSetting :
				notificationRecipient.getNotificationRecipientSettings()) {

			_notificationRecipientSettingLocalService.
				deleteNotificationRecipientSetting(
					notificationRecipientSetting.
						getNotificationRecipientSettingId());
		}

		for (NotificationRecipientSetting notificationRecipientSetting :
				notificationContext.getNotificationRecipientSettings()) {

			notificationRecipientSetting.setNotificationRecipientSettingId(
				counterLocalService.increment());

			_notificationRecipientSettingLocalService.
				updateNotificationRecipientSetting(
					notificationRecipientSetting);
		}

		notificationTemplate = notificationTemplatePersistence.update(
			notificationContext.getNotificationTemplate());

		List<Long> oldAttachmentObjectFieldIds = new ArrayList<>();

		for (NotificationTemplateAttachment notificationTemplateAttachment :
				_notificationTemplateAttachmentPersistence.
					findByNotificationTemplateId(
						notificationTemplate.getNotificationTemplateId())) {

			if (ListUtil.exists(
					notificationContext.getAttachmentObjectFieldIds(),
					attachmentObjectFieldId -> Objects.equals(
						attachmentObjectFieldId,
						notificationTemplateAttachment.getObjectFieldId()))) {

				oldAttachmentObjectFieldIds.add(
					notificationTemplateAttachment.getObjectFieldId());

				continue;
			}

			_notificationTemplateAttachmentPersistence.remove(
				notificationTemplateAttachment);
		}

		for (long attachmentObjectFieldId :
				ListUtil.remove(
					notificationContext.getAttachmentObjectFieldIds(),
					oldAttachmentObjectFieldIds)) {

			_notificationTemplateAttachmentLocalService.
				addNotificationTemplateAttachment(
					notificationTemplate.getCompanyId(),
					notificationTemplate.getNotificationTemplateId(),
					attachmentObjectFieldId);
		}

		return notificationTemplate;
	}

	private NotificationTemplate _addNotificationTemplate(
			String externalReferenceCode, User user, String body, String name,
			String recipientType, String subject, boolean system, String type)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationTemplatePersistence.create(
				counterLocalService.increment());

		notificationTemplate.setExternalReferenceCode(externalReferenceCode);
		notificationTemplate.setCompanyId(user.getCompanyId());
		notificationTemplate.setUserId(user.getUserId());
		notificationTemplate.setUserName(user.getFullName());

		if (body != null) {
			notificationTemplate.setBodyMap(
				LocalizedMapUtil.getLocalizedMap(body));
		}

		notificationTemplate.setEditorType(
			NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT);
		notificationTemplate.setName(name);

		if (recipientType != null) {
			notificationTemplate.setRecipientType(recipientType);
		}

		if (subject != null) {
			notificationTemplate.setSubjectMap(
				LocalizedMapUtil.getLocalizedMap(subject));
		}

		notificationTemplate.setSystem(system);
		notificationTemplate.setType(type);

		notificationTemplate = notificationTemplatePersistence.update(
			notificationTemplate);

		_resourceLocalService.addResources(
			notificationTemplate.getCompanyId(), 0,
			notificationTemplate.getUserId(),
			NotificationTemplate.class.getName(),
			notificationTemplate.getNotificationTemplateId(), false, true,
			true);

		return notificationTemplate;
	}

	private void _validate(NotificationContext notificationContext)
		throws PortalException {

		NotificationType notificationType =
			_notificationTypeServiceTracker.getNotificationType(
				notificationContext.getType());

		notificationType.validateNotificationTemplate(notificationContext);
	}

	@Reference
	private NotificationQueueEntryPersistence
		_notificationQueueEntryPersistence;

	@Reference
	private NotificationRecipientLocalService
		_notificationRecipientLocalService;

	@Reference
	private NotificationRecipientSettingLocalService
		_notificationRecipientSettingLocalService;

	@Reference
	private NotificationTemplateAttachmentLocalService
		_notificationTemplateAttachmentLocalService;

	@Reference
	private NotificationTemplateAttachmentPersistence
		_notificationTemplateAttachmentPersistence;

	@Reference
	private NotificationTypeServiceTracker _notificationTypeServiceTracker;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
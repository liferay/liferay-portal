/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.impl;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.exception.NotificationQueueEntryStatusException;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.model.NotificationTemplateAttachment;
import com.liferay.notification.service.NotificationQueueEntryAttachmentLocalService;
import com.liferay.notification.service.NotificationRecipientLocalService;
import com.liferay.notification.service.NotificationRecipientSettingLocalService;
import com.liferay.notification.service.NotificationTemplateAttachmentLocalService;
import com.liferay.notification.service.base.NotificationQueueEntryLocalServiceBaseImpl;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.type.NotificationTypeServiceTracker;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 * @author Gustavo Lima
 */
@Component(
	property = "model.class.name=com.liferay.notification.model.NotificationQueueEntry",
	service = AopService.class
)
public class NotificationQueueEntryLocalServiceImpl
	extends NotificationQueueEntryLocalServiceBaseImpl {

	@Override
	public NotificationQueueEntry addNotificationQueueEntry(
			NotificationContext notificationContext)
		throws PortalException {

		NotificationQueueEntry notificationQueueEntry =
			notificationContext.getNotificationQueueEntry();

		NotificationType notificationType =
			_notificationTypeServiceTracker.getNotificationType(
				notificationQueueEntry.getType());

		notificationType.validateNotificationQueueEntry(notificationContext);

		notificationQueueEntry.setNotificationQueueEntryId(
			counterLocalService.increment());

		notificationQueueEntry = notificationQueueEntryPersistence.update(
			notificationQueueEntry);

		_resourceLocalService.addResources(
			notificationQueueEntry.getCompanyId(), 0,
			notificationQueueEntry.getUserId(),
			NotificationQueueEntry.class.getName(),
			notificationQueueEntry.getNotificationQueueEntryId(), false, true,
			true);

		_addNotificationQueueEntryAttachments(
			notificationContext, notificationQueueEntry);

		NotificationRecipient notificationRecipient =
			notificationContext.getNotificationRecipient();

		notificationRecipient.setNotificationRecipientId(
			counterLocalService.increment());
		notificationRecipient.setClassNameId(
			_portal.getClassNameId(NotificationQueueEntry.class));
		notificationRecipient.setClassPK(
			notificationQueueEntry.getNotificationQueueEntryId());

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

		return notificationQueueEntry;
	}

	@Override
	public void deleteCompanyNotificationQueueEntries(long companyId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq("companyId", companyId)));
		actionableDynamicQuery.setPerformActionMethod(
			(NotificationQueueEntry notificationQueueEntry) ->
				deleteNotificationQueueEntry(notificationQueueEntry));

		actionableDynamicQuery.performActions();
	}

	@Override
	public void deleteNotificationQueueEntries(Date sentDate)
		throws PortalException {

		for (NotificationQueueEntry notificationQueueEntry :
				notificationQueueEntryPersistence.findByLtSentDate(sentDate)) {

			notificationQueueEntryLocalService.deleteNotificationQueueEntry(
				notificationQueueEntry);
		}
	}

	@Override
	public NotificationQueueEntry deleteNotificationQueueEntry(
			long notificationQueueEntryId)
		throws PortalException {

		NotificationQueueEntry notificationQueueEntry =
			notificationQueueEntryPersistence.findByPrimaryKey(
				notificationQueueEntryId);

		return notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntry);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public NotificationQueueEntry deleteNotificationQueueEntry(
			NotificationQueueEntry notificationQueueEntry)
		throws PortalException {

		notificationQueueEntry = notificationQueueEntryPersistence.remove(
			notificationQueueEntry);

		_resourceLocalService.deleteResource(
			notificationQueueEntry, ResourceConstants.SCOPE_INDIVIDUAL);

		_notificationQueueEntryAttachmentLocalService.
			deleteNotificationQueueEntryAttachments(
				notificationQueueEntry.getNotificationQueueEntryId());

		NotificationRecipient notificationRecipient =
			notificationQueueEntry.fetchNotificationRecipient();

		if (notificationRecipient != null) {
			_notificationRecipientLocalService.deleteNotificationRecipient(
				notificationRecipient);

			for (NotificationRecipientSetting notificationRecipientSetting :
					notificationRecipient.getNotificationRecipientSettings()) {

				_notificationRecipientSettingLocalService.
					deleteNotificationRecipientSetting(
						notificationRecipientSetting);
			}
		}

		Repository repository = _getRepository(notificationQueueEntry);

		if (repository != null) {
			try {
				Folder folder = _portletFileRepository.getPortletFolder(
					repository.getRepositoryId(),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					String.valueOf(
						notificationQueueEntry.getNotificationQueueEntryId()));

				_portletFileRepository.deletePortletFolder(
					folder.getFolderId());
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return notificationQueueEntry;
	}

	@Override
	public List<NotificationQueueEntry> getNotificationEntries(
		String type, int status) {

		return notificationQueueEntryPersistence.findByT_S(type, status);
	}

	@Override
	public NotificationQueueEntry resendNotificationQueueEntry(
			long notificationQueueEntryId)
		throws PortalException {

		NotificationQueueEntry notificationQueueEntry =
			getNotificationQueueEntry(notificationQueueEntryId);

		if (notificationQueueEntry.getStatus() ==
				NotificationQueueEntryConstants.STATUS_SENT) {

			throw new NotificationQueueEntryStatusException(
				"Notification queue entry " +
					notificationQueueEntry.getNotificationQueueEntryId() +
						" was already sent");
		}

		NotificationType notificationType =
			_notificationTypeServiceTracker.getNotificationType(
				notificationQueueEntry.getType());

		notificationType.resendNotification(notificationQueueEntry);

		return getNotificationQueueEntry(notificationQueueEntryId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationQueueEntry updateNotificationQueueEntry(
		NotificationQueueEntry notificationQueueEntry) {

		return notificationQueueEntryPersistence.update(notificationQueueEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationQueueEntry updateStatus(
			long notificationQueueEntryId, int status)
		throws PortalException {

		NotificationQueueEntry notificationQueueEntry =
			notificationQueueEntryPersistence.findByPrimaryKey(
				notificationQueueEntryId);

		if (status == NotificationQueueEntryConstants.STATUS_SENT) {
			notificationQueueEntry.setSentDate(new Date());
		}
		else {
			notificationQueueEntry.setSentDate(null);
		}

		notificationQueueEntry.setStatus(status);

		return notificationQueueEntryPersistence.update(notificationQueueEntry);
	}

	private void _addNotificationQueueEntryAttachments(
			NotificationContext notificationContext,
			NotificationQueueEntry notificationQueueEntry)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		if (notificationTemplate == null) {
			return;
		}

		List<NotificationTemplateAttachment> notificationTemplateAttachments =
			_notificationTemplateAttachmentLocalService.
				getNotificationTemplateAttachments(
					notificationTemplate.getNotificationTemplateId());

		if (ListUtil.isEmpty(notificationTemplateAttachments)) {
			return;
		}

		Repository repository = _getRepository(notificationQueueEntry);

		if (repository == null) {
			return;
		}

		_resourceLocalService.addResources(
			repository.getCompanyId(), repository.getGroupId(),
			repository.getUserId(), DLFolder.class.getName(),
			repository.getDlFolderId(), false, true, true);

		Folder folder;

		try {
			folder = _portletFileRepository.addPortletFolder(
				_userLocalService.getGuestUserId(
					notificationQueueEntry.getCompanyId()),
				repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				String.valueOf(
					notificationQueueEntry.getNotificationQueueEntryId()),
				new ServiceContext());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return;
		}

		for (NotificationTemplateAttachment notificationTemplateAttachment :
				notificationTemplateAttachments) {

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				notificationTemplateAttachment.getObjectFieldId());

			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				MapUtil.getLong(
					notificationContext.getTermValues(),
					objectField.getName()));

			if (dlFileEntry == null) {
				continue;
			}

			FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
				null, repository.getGroupId(),
				_userLocalService.getGuestUserId(
					notificationQueueEntry.getCompanyId()),
				NotificationTemplate.class.getName(), 0,
				NotificationPortletKeys.NOTIFICATION_TEMPLATES,
				folder.getFolderId(), dlFileEntry.getContentStream(),
				_portletFileRepository.getUniqueFileName(
					repository.getGroupId(), folder.getFolderId(),
					dlFileEntry.getFileName()),
				dlFileEntry.getMimeType(), false);

			_notificationQueueEntryAttachmentLocalService.
				addNotificationQueueEntryAttachment(
					notificationQueueEntry.getCompanyId(),
					fileEntry.getFileEntryId(),
					notificationQueueEntry.getNotificationQueueEntryId());
		}
	}

	private Repository _getRepository(
		NotificationQueueEntry notificationQueueEntry) {

		try {
			Group group = _groupLocalService.getCompanyGroup(
				notificationQueueEntry.getCompanyId());

			return _portletFileRepository.addPortletRepository(
				group.getGroupId(),
				NotificationPortletKeys.NOTIFICATION_TEMPLATES,
				new ServiceContext());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationQueueEntryLocalServiceImpl.class);

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private NotificationQueueEntryAttachmentLocalService
		_notificationQueueEntryAttachmentLocalService;

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
	private NotificationTypeServiceTracker _notificationTypeServiceTracker;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
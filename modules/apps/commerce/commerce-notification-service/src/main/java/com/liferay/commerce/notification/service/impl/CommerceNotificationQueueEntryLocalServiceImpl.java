/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.impl;

import com.liferay.commerce.notification.model.CommerceNotificationAttachment;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.service.CommerceNotificationAttachmentLocalService;
import com.liferay.commerce.notification.service.base.CommerceNotificationQueueEntryLocalServiceBaseImpl;
import com.liferay.commerce.notification.util.comparator.CommerceNotificationAttachmentCreateDateComparator;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.mail.internet.InternetAddress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = "model.class.name=com.liferay.commerce.notification.model.CommerceNotificationQueueEntry",
	service = AopService.class
)
@Deprecated
public class CommerceNotificationQueueEntryLocalServiceImpl
	extends CommerceNotificationQueueEntryLocalServiceBaseImpl {

	/**
	 * @deprecated As of Mueller (7.2.x), this method will be replaced
	 */
	@Deprecated
	@Override
	public CommerceNotificationQueueEntry addCommerceNotificationQueueEntry(
			long userId, long groupId, long commerceNotificationTemplateId,
			String from, String fromName, String to, String toName, String cc,
			String bcc, String subject, String body, double priority)
		throws PortalException {

		return commerceNotificationQueueEntryLocalService.
			addCommerceNotificationQueueEntry(
				userId, groupId, StringPool.BLANK, 0L,
				commerceNotificationTemplateId, from, fromName, to, toName, cc,
				bcc, subject, body, priority);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceNotificationQueueEntry addCommerceNotificationQueueEntry(
			long userId, long groupId, String className, long classPK,
			long commerceNotificationTemplateId, String from, String fromName,
			String to, String toName, String cc, String bcc, String subject,
			String body, double priority)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long commerceNotificationQueueEntryId = counterLocalService.increment();

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntryPersistence.create(
				commerceNotificationQueueEntryId);

		commerceNotificationQueueEntry.setGroupId(groupId);
		commerceNotificationQueueEntry.setCompanyId(user.getCompanyId());
		commerceNotificationQueueEntry.setUserId(user.getUserId());
		commerceNotificationQueueEntry.setUserName(user.getFullName());
		commerceNotificationQueueEntry.setClassName(className);
		commerceNotificationQueueEntry.setClassPK(classPK);
		commerceNotificationQueueEntry.setCommerceNotificationTemplateId(
			commerceNotificationTemplateId);
		commerceNotificationQueueEntry.setFrom(from);
		commerceNotificationQueueEntry.setFromName(fromName);
		commerceNotificationQueueEntry.setTo(to);
		commerceNotificationQueueEntry.setToName(toName);
		commerceNotificationQueueEntry.setCc(cc);
		commerceNotificationQueueEntry.setBcc(bcc);
		commerceNotificationQueueEntry.setSubject(subject);
		commerceNotificationQueueEntry.setBody(body);
		commerceNotificationQueueEntry.setPriority(priority);

		return commerceNotificationQueueEntryPersistence.update(
			commerceNotificationQueueEntry);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceNotificationQueueEntry deleteCommerceNotificationQueue(
			CommerceNotificationQueueEntry commerceNotificationQueueEntry)
		throws PortalException {

		// Commerce notification attachments

		_commerceNotificationAttachmentLocalService.
			deleteCommerceNotificationAttachments(
				commerceNotificationQueueEntry.
					getCommerceNotificationQueueEntryId());

		// Commerce notification queue entry

		commerceNotificationQueueEntryPersistence.remove(
			commerceNotificationQueueEntry);

		return commerceNotificationQueueEntry;
	}

	@Override
	public void deleteCommerceNotificationQueueEntries(Date sentDate) {
		commerceNotificationQueueEntryPersistence.removeByLtSentDate(sentDate);
	}

	@Override
	public void deleteCommerceNotificationQueueEntries(long groupId)
		throws PortalException {

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			commerceNotificationQueueEntryPersistence.findByGroupId(groupId);

		for (CommerceNotificationQueueEntry commerceNotificationQueueEntry :
				commerceNotificationQueueEntries) {

			commerceNotificationQueueEntryLocalService.
				deleteCommerceNotificationQueue(commerceNotificationQueueEntry);
		}
	}

	@Override
	public CommerceNotificationQueueEntry deleteCommerceNotificationQueueEntry(
			long commerceNotificationQueueEntryId)
		throws PortalException {

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntryPersistence.findByPrimaryKey(
				commerceNotificationQueueEntryId);

		return commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueue(commerceNotificationQueueEntry);
	}

	@Override
	public List<CommerceNotificationQueueEntry>
		getCommerceNotificationQueueEntries(boolean sent) {

		return commerceNotificationQueueEntryPersistence.findBySent(sent);
	}

	@Override
	public List<CommerceNotificationQueueEntry>
		getCommerceNotificationQueueEntries(
			long groupId, int start, int end,
			OrderByComparator<CommerceNotificationQueueEntry>
				orderByComparator) {

		return commerceNotificationQueueEntryPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceNotificationQueueEntry>
		getCommerceNotificationQueueEntries(
			long groupId, String className, long classPK, boolean sent,
			int start, int end,
			OrderByComparator<CommerceNotificationQueueEntry>
				orderByComparator) {

		return commerceNotificationQueueEntryPersistence.findByG_C_C_S(
			groupId, _classNameLocalService.getClassNameId(className), classPK,
			sent, start, end, orderByComparator);
	}

	@Override
	public int getCommerceNotificationQueueEntriesCount(long groupId) {
		return commerceNotificationQueueEntryPersistence.countByGroupId(
			groupId);
	}

	@Override
	public int getCommerceNotificationQueueEntriesCount(
		long groupId, String className, long classPK, boolean sent) {

		return commerceNotificationQueueEntryPersistence.countByG_C_C_S(
			groupId, _classNameLocalService.getClassNameId(className), classPK,
			sent);
	}

	@Override
	public CommerceNotificationQueueEntry resendCommerceNotificationQueueEntry(
			long commerceNotificationQueueEntryId)
		throws PortalException {

		return commerceNotificationQueueEntryLocalService.updateSent(
			commerceNotificationQueueEntryId, false);
	}

	@Override
	public void sendCommerceNotificationQueueEntries() throws Exception {
		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			commerceNotificationQueueEntryPersistence.findBySent(false);

		for (CommerceNotificationQueueEntry commerceNotificationQueueEntry :
				commerceNotificationQueueEntries) {

			InternetAddress from = new InternetAddress(
				commerceNotificationQueueEntry.getFrom(),
				commerceNotificationQueueEntry.getFromName());
			InternetAddress to = new InternetAddress(
				commerceNotificationQueueEntry.getTo(),
				commerceNotificationQueueEntry.getToName());

			MailMessage mailMessage = new MailMessage(
				from, to, commerceNotificationQueueEntry.getSubject(),
				commerceNotificationQueueEntry.getBody(), true);

			List<CommerceNotificationAttachment>
				commerceNotificationAttachments =
					_commerceNotificationAttachmentLocalService.
						getCommerceNotificationAttachments(
							commerceNotificationQueueEntry.
								getCommerceNotificationQueueEntryId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS,
							CommerceNotificationAttachmentCreateDateComparator.
								getInstance(false));

			for (CommerceNotificationAttachment commerceNotificationAttachment :
					commerceNotificationAttachments) {

				FileEntry fileEntry =
					commerceNotificationAttachment.getFileEntry();

				mailMessage.addFileAttachment(
					_file.createTempFile(fileEntry.getContentStream()),
					fileEntry.getFileName());
			}

			List<InternetAddress> bccInternetAddresses = new ArrayList<>();
			List<InternetAddress> ccInternetAddresses = new ArrayList<>();

			String[] bccAddresses = StringUtil.split(
				commerceNotificationQueueEntry.getBcc());
			String[] ccAddresses = StringUtil.split(
				commerceNotificationQueueEntry.getCc());

			for (String bccAddress : bccAddresses) {
				bccInternetAddresses.add(new InternetAddress(bccAddress));
			}

			for (String ccAddress : ccAddresses) {
				ccInternetAddresses.add(new InternetAddress(ccAddress));
			}

			mailMessage.setBCC(
				bccInternetAddresses.toArray(new InternetAddress[0]));
			mailMessage.setCC(
				ccInternetAddresses.toArray(new InternetAddress[0]));

			try {
				_mailService.sendEmail(mailMessage);

				commerceNotificationQueueEntryLocalService.updateSent(
					commerceNotificationQueueEntry.
						getCommerceNotificationQueueEntryId(),
					true);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}
	}

	@Override
	public void updateCommerceNotificationQueueEntriesTemplateIds(
		long commerceNotificationTemplateId) {

		List<CommerceNotificationQueueEntry> commerceNotificationQueueEntries =
			commerceNotificationQueueEntryPersistence.
				findByCommerceNotificationTemplateId(
					commerceNotificationTemplateId);

		for (CommerceNotificationQueueEntry commerceNotificationQueueEntry :
				commerceNotificationQueueEntries) {

			updateCommerceNotificationQueueEntry(
				commerceNotificationQueueEntry, 0);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceNotificationQueueEntry updateSent(
			long commerceNotificationQueueEntryId, boolean sent)
		throws PortalException {

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntryPersistence.findByPrimaryKey(
				commerceNotificationQueueEntryId);

		commerceNotificationQueueEntry.setSent(sent);

		if (sent) {
			commerceNotificationQueueEntry.setSentDate(new Date());
		}
		else {
			commerceNotificationQueueEntry.setSentDate(null);
		}

		return commerceNotificationQueueEntryPersistence.update(
			commerceNotificationQueueEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	protected CommerceNotificationQueueEntry
		updateCommerceNotificationQueueEntry(
			CommerceNotificationQueueEntry commerceNotificationQueueEntry,
			long commerceNotificationTemplateId) {

		commerceNotificationQueueEntry.setCommerceNotificationTemplateId(
			commerceNotificationTemplateId);

		return commerceNotificationQueueEntryPersistence.update(
			commerceNotificationQueueEntry);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceNotificationQueueEntryLocalServiceImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceNotificationAttachmentLocalService
		_commerceNotificationAttachmentLocalService;

	@Reference
	private File _file;

	@Reference
	private MailService _mailService;

	@Reference
	private UserLocalService _userLocalService;

}
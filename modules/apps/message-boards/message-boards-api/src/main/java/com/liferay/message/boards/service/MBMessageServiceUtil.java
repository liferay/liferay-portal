/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.io.InputStream;

import java.util.List;

/**
 * Provides the remote service utility for MBMessage. This utility wraps
 * <code>com.liferay.message.boards.service.impl.MBMessageServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see MBMessageService
 * @generated
 */
public class MBMessageServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBMessageServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static MBMessage addDiscussionMessage(
			long groupId, String className, long classPK, long threadId,
			long parentMessageId, String subject, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDiscussionMessage(
			groupId, className, classPK, threadId, parentMessageId, subject,
			body, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, String, String, List,
	 boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	public static MBMessage addMessage(
			long groupId, long categoryId, String subject, String body,
			String format,
			List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMessage(
			groupId, categoryId, subject, body, format, inputStreamOVPs,
			anonymous, priority, allowPingbacks, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, String, String, List,
	 boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	public static MBMessage addMessage(
			long groupId, long categoryId, String subject, String body,
			String format, String fileName, java.io.File file,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws java.io.FileNotFoundException, PortalException {

		return getService().addMessage(
			groupId, categoryId, subject, body, format, fileName, file,
			anonymous, priority, allowPingbacks, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, String, String, List,
	 boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	public static MBMessage addMessage(
			long categoryId, String subject, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMessage(
			categoryId, subject, body, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, String, String, List,
	 boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	public static MBMessage addMessage(
			long parentMessageId, String subject, String body, String format,
			List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMessage(
			parentMessageId, subject, body, format, inputStreamOVPs, anonymous,
			priority, allowPingbacks, serviceContext);
	}

	public static MBMessage addMessage(
			String externalReferenceCode, long parentMessageId, String subject,
			String body, String format,
			List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMessage(
			externalReferenceCode, parentMessageId, subject, body, format,
			inputStreamOVPs, anonymous, priority, allowPingbacks,
			serviceContext);
	}

	public static void addMessageAttachment(
			long messageId, String fileName, java.io.File file, String mimeType)
		throws PortalException {

		getService().addMessageAttachment(messageId, fileName, file, mimeType);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempAttachment(
				long groupId, long categoryId, String folderName,
				String fileName, InputStream inputStream, String mimeType)
		throws PortalException {

		return getService().addTempAttachment(
			groupId, categoryId, folderName, fileName, inputStream, mimeType);
	}

	public static void deleteDiscussionMessage(long messageId)
		throws PortalException {

		getService().deleteDiscussionMessage(messageId);
	}

	public static void deleteMessage(long messageId) throws PortalException {
		getService().deleteMessage(messageId);
	}

	public static void deleteMessageAttachment(long messageId, String fileName)
		throws PortalException {

		getService().deleteMessageAttachment(messageId, fileName);
	}

	public static void deleteMessageAttachments(long messageId)
		throws PortalException {

		getService().deleteMessageAttachments(messageId);
	}

	public static void deleteTempAttachment(
			long groupId, long categoryId, String folderName, String fileName)
		throws PortalException {

		getService().deleteTempAttachment(
			groupId, categoryId, folderName, fileName);
	}

	public static void emptyMessageAttachments(long messageId)
		throws PortalException {

		getService().emptyMessageAttachments(messageId);
	}

	public static MBMessage fetchMBMessageByUrlSubject(
			long groupId, String urlSubject)
		throws PortalException {

		return getService().fetchMBMessageByUrlSubject(groupId, urlSubject);
	}

	public static List<MBMessage> getCategoryMessages(
			long groupId, long categoryId, int status, int start, int end)
		throws PortalException {

		return getService().getCategoryMessages(
			groupId, categoryId, status, start, end);
	}

	public static int getCategoryMessagesCount(
		long groupId, long categoryId, int status) {

		return getService().getCategoryMessagesCount(
			groupId, categoryId, status);
	}

	public static String getCategoryMessagesRSS(
			long groupId, long categoryId, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getCategoryMessagesRSS(
			groupId, categoryId, status, max, type, version, displayStyle,
			feedURL, entryURL, themeDisplay);
	}

	public static List<MBMessage> getChildMessages(
			long parentMessageId, boolean flatten,
			com.liferay.portal.kernel.dao.orm.QueryDefinition<MBMessage>
				queryDefinition)
		throws PortalException {

		return getService().getChildMessages(
			parentMessageId, flatten, queryDefinition);
	}

	public static int getChildMessagesCount(
			long parentMessageId, boolean flatten,
			com.liferay.portal.kernel.dao.orm.QueryDefinition<MBMessage>
				queryDefinition)
		throws PortalException {

		return getService().getChildMessagesCount(
			parentMessageId, flatten, queryDefinition);
	}

	public static String getCompanyMessagesRSS(
			long companyId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getCompanyMessagesRSS(
			companyId, status, max, type, version, displayStyle, feedURL,
			entryURL, themeDisplay);
	}

	public static int getGroupMessagesCount(long groupId, int status) {
		return getService().getGroupMessagesCount(groupId, status);
	}

	public static String getGroupMessagesRSS(
			long groupId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getGroupMessagesRSS(
			groupId, status, max, type, version, displayStyle, feedURL,
			entryURL, themeDisplay);
	}

	public static String getGroupMessagesRSS(
			long groupId, long userId, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getGroupMessagesRSS(
			groupId, userId, status, max, type, version, displayStyle, feedURL,
			entryURL, themeDisplay);
	}

	public static List<MBMessage> getGroupUserMessageBoardMessagesActivity(
			long groupId, long userId, int start, int end)
		throws PortalException {

		return getService().getGroupUserMessageBoardMessagesActivity(
			groupId, userId, start, end);
	}

	public static int getGroupUserMessageBoardMessagesActivityCount(
			long groupId, long userId)
		throws PortalException {

		return getService().getGroupUserMessageBoardMessagesActivityCount(
			groupId, userId);
	}

	public static MBMessage getMBMessageByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getMBMessageByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static MBMessage getMessage(long messageId) throws PortalException {
		return getService().getMessage(messageId);
	}

	public static com.liferay.message.boards.model.MBMessageDisplay
			getMessageDisplay(long messageId, int status)
		throws PortalException {

		return getService().getMessageDisplay(messageId, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static String[] getTempAttachmentNames(
			long groupId, String folderName)
		throws PortalException {

		return getService().getTempAttachmentNames(groupId, folderName);
	}

	public static int getThreadAnswersCount(
		long groupId, long categoryId, long threadId) {

		return getService().getThreadAnswersCount(
			groupId, categoryId, threadId);
	}

	public static List<MBMessage> getThreadMessages(
			long groupId, long categoryId, long threadId, int status, int start,
			int end)
		throws PortalException {

		return getService().getThreadMessages(
			groupId, categoryId, threadId, status, start, end);
	}

	public static int getThreadMessagesCount(
			long groupId, long categoryId, long threadId, int status)
		throws PortalException {

		return getService().getThreadMessagesCount(
			groupId, categoryId, threadId, status);
	}

	public static String getThreadMessagesRSS(
			long threadId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getThreadMessagesRSS(
			threadId, status, max, type, version, displayStyle, feedURL,
			entryURL, themeDisplay);
	}

	public static void moveMessageAttachmentToTrash(
			long messageId, String fileName)
		throws PortalException {

		getService().moveMessageAttachmentToTrash(messageId, fileName);
	}

	public static void restoreMessageAttachmentFromTrash(
			long messageId, String fileName)
		throws PortalException {

		getService().restoreMessageAttachmentFromTrash(messageId, fileName);
	}

	public static void subscribeMessage(long messageId) throws PortalException {
		getService().subscribeMessage(messageId);
	}

	public static void unsubscribeMessage(long messageId)
		throws PortalException {

		getService().unsubscribeMessage(messageId);
	}

	public static MBMessage updateAnswer(
			long messageId, boolean answer, boolean cascade)
		throws PortalException {

		return getService().updateAnswer(messageId, answer, cascade);
	}

	public static MBMessage updateDiscussionMessage(
			String className, long classPK, long messageId, String subject,
			String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateDiscussionMessage(
			className, classPK, messageId, subject, body, serviceContext);
	}

	public static MBMessage updateMessage(
			long messageId, String subject, String body,
			List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, InputStream>> inputStreamOVPs,
			double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateMessage(
			messageId, subject, body, inputStreamOVPs, priority, allowPingbacks,
			serviceContext);
	}

	public static MBMessageService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MBMessageService> _serviceSnapshot =
		new Snapshot<>(MBMessageServiceUtil.class, MBMessageService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1079107350
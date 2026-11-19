/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ObjectValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for MBMessage. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see MBMessageServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface MBMessageService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBMessageServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the message-boards message remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link MBMessageServiceUtil} if injection and service tracking are not available.
	 */
	public MBMessage addDiscussionMessage(
			long groupId, String className, long classPK, long threadId,
			long parentMessageId, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, String, String, List,
	 boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	public MBMessage addMessage(
			long groupId, long categoryId, String subject, String body,
			String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, String, String, List,
	 boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	public MBMessage addMessage(
			long groupId, long categoryId, String subject, String body,
			String format, String fileName, File file, boolean anonymous,
			double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws FileNotFoundException, PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, String, String, List,
	 boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	public MBMessage addMessage(
			long categoryId, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, String, String, List,
	 boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	public MBMessage addMessage(
			long parentMessageId, String subject, String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException;

	public MBMessage addMessage(
			String externalReferenceCode, long parentMessageId, String subject,
			String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException;

	public void addMessageAttachment(
			long messageId, String fileName, File file, String mimeType)
		throws PortalException;

	public FileEntry addTempAttachment(
			long groupId, long categoryId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException;

	public void deleteDiscussionMessage(long messageId) throws PortalException;

	public void deleteMessage(long messageId) throws PortalException;

	public void deleteMessageAttachment(long messageId, String fileName)
		throws PortalException;

	public void deleteMessageAttachments(long messageId) throws PortalException;

	public void deleteTempAttachment(
			long groupId, long categoryId, String folderName, String fileName)
		throws PortalException;

	public void emptyMessageAttachments(long messageId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage fetchMBMessageByUrlSubject(long groupId, String urlSubject)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getCategoryMessages(
			long groupId, long categoryId, int status, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoryMessagesCount(
		long groupId, long categoryId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getCategoryMessagesRSS(
			long groupId, long categoryId, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getChildMessages(
			long parentMessageId, boolean flatten,
			QueryDefinition<MBMessage> queryDefinition)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getChildMessagesCount(
			long parentMessageId, boolean flatten,
			QueryDefinition<MBMessage> queryDefinition)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getCompanyMessagesRSS(
			long companyId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			ThemeDisplay themeDisplay)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupMessagesCount(long groupId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getGroupMessagesRSS(
			long groupId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			ThemeDisplay themeDisplay)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getGroupMessagesRSS(
			long groupId, long userId, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getGroupUserMessageBoardMessagesActivity(
			long groupId, long userId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupUserMessageBoardMessagesActivityCount(
			long groupId, long userId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getMBMessageByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getMessage(long messageId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessageDisplay getMessageDisplay(long messageId, int status)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTempAttachmentNames(long groupId, String folderName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getThreadAnswersCount(
		long groupId, long categoryId, long threadId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getThreadMessages(
			long groupId, long categoryId, long threadId, int status, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getThreadMessagesCount(
			long groupId, long categoryId, long threadId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getThreadMessagesRSS(
			long threadId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			ThemeDisplay themeDisplay)
		throws PortalException;

	public void moveMessageAttachmentToTrash(long messageId, String fileName)
		throws PortalException;

	public void restoreMessageAttachmentFromTrash(
			long messageId, String fileName)
		throws PortalException;

	public void subscribeMessage(long messageId) throws PortalException;

	public void unsubscribeMessage(long messageId) throws PortalException;

	public MBMessage updateAnswer(
			long messageId, boolean answer, boolean cascade)
		throws PortalException;

	public MBMessage updateDiscussionMessage(
			String className, long classPK, long messageId, String subject,
			String body, ServiceContext serviceContext)
		throws PortalException;

	public MBMessage updateMessage(
			long messageId, String subject, String body,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-977784306
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for MBMessage. This utility wraps
 * <code>com.liferay.message.boards.service.impl.MBMessageLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MBMessageLocalService
 * @generated
 */
public class MBMessageLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBMessageLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static MBMessage addDiscussionMessage(
			long userId, String userName, long groupId, String className,
			long classPK, int workflowAction)
		throws PortalException {

		return getService().addDiscussionMessage(
			userId, userName, groupId, className, classPK, workflowAction);
	}

	public static MBMessage addDiscussionMessage(
			String externalReferenceCode, long userId, String userName,
			long groupId, String className, long classPK, long threadId,
			long parentMessageId, String subject, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDiscussionMessage(
			externalReferenceCode, userId, userName, groupId, className,
			classPK, threadId, parentMessageId, subject, body, serviceContext);
	}

	/**
	 * Adds the message-boards message to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbMessage the message-boards message
	 * @return the message-boards message that was added
	 */
	public static MBMessage addMBMessage(MBMessage mbMessage) {
		return getService().addMBMessage(mbMessage);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public static MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			long threadId, long parentMessageId, String subject, String body,
			String format,
			List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMessage(
			userId, userName, groupId, categoryId, threadId, parentMessageId,
			subject, body, format, inputStreamOVPs, anonymous, priority,
			allowPingbacks, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public static MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMessage(
			userId, userName, groupId, categoryId, subject, body,
			serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public static MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format,
			List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMessage(
			userId, userName, groupId, categoryId, subject, body, format,
			inputStreamOVPs, anonymous, priority, allowPingbacks,
			serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public static MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format, String fileName,
			java.io.File file, boolean anonymous, double priority,
			boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws java.io.FileNotFoundException, PortalException {

		return getService().addMessage(
			userId, userName, groupId, categoryId, subject, body, format,
			fileName, file, anonymous, priority, allowPingbacks,
			serviceContext);
	}

	public static MBMessage addMessage(
			String externalReferenceCode, long userId, String userName,
			long groupId, long categoryId, long threadId, long parentMessageId,
			String subject, String body, String format,
			List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMessage(
			externalReferenceCode, userId, userName, groupId, categoryId,
			threadId, parentMessageId, subject, body, format, inputStreamOVPs,
			anonymous, priority, allowPingbacks, serviceContext);
	}

	public static void addMessageAttachment(
			long userId, long messageId, String fileName, java.io.File file,
			String mimeType)
		throws PortalException {

		getService().addMessageAttachment(
			userId, messageId, fileName, file, mimeType);
	}

	public static void addMessageResources(
			long messageId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addMessageResources(
			messageId, addGroupPermissions, addGuestPermissions);
	}

	public static void addMessageResources(
			long messageId,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addMessageResources(messageId, modelPermissions);
	}

	public static void addMessageResources(
			MBMessage message, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addMessageResources(
			message, addGroupPermissions, addGuestPermissions);
	}

	public static void addMessageResources(
			MBMessage message,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addMessageResources(message, modelPermissions);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempAttachment(
				long groupId, long userId, String folderName, String fileName,
				InputStream inputStream, String mimeType)
		throws PortalException {

		return getService().addTempAttachment(
			groupId, userId, folderName, fileName, inputStream, mimeType);
	}

	/**
	 * Creates a new message-boards message with the primary key. Does not add the message-boards message to the database.
	 *
	 * @param messageId the primary key for the new message-boards message
	 * @return the new message-boards message
	 */
	public static MBMessage createMBMessage(long messageId) {
		return getService().createMBMessage(messageId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static MBMessage deleteDiscussionMessage(long messageId)
		throws PortalException {

		return getService().deleteDiscussionMessage(messageId);
	}

	public static void deleteDiscussionMessages(String className, long classPK)
		throws PortalException {

		getService().deleteDiscussionMessages(className, classPK);
	}

	/**
	 * Deletes the message-boards message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message that was removed
	 * @throws PortalException if a message-boards message with the primary key could not be found
	 */
	public static MBMessage deleteMBMessage(long messageId)
		throws PortalException {

		return getService().deleteMBMessage(messageId);
	}

	/**
	 * Deletes the message-boards message from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbMessage the message-boards message
	 * @return the message-boards message that was removed
	 */
	public static MBMessage deleteMBMessage(MBMessage mbMessage) {
		return getService().deleteMBMessage(mbMessage);
	}

	public static MBMessage deleteMessage(long messageId)
		throws PortalException {

		return getService().deleteMessage(messageId);
	}

	public static MBMessage deleteMessage(MBMessage message)
		throws PortalException {

		return getService().deleteMessage(message);
	}

	public static void deleteMessageAttachment(long messageId, String fileName)
		throws PortalException {

		getService().deleteMessageAttachment(messageId, fileName);
	}

	public static void deleteMessageAttachments(long messageId)
		throws PortalException {

		getService().deleteMessageAttachments(messageId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteTempAttachment(
			long groupId, long userId, String folderName, String fileName)
		throws PortalException {

		getService().deleteTempAttachment(
			groupId, userId, folderName, fileName);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static void emptyMessageAttachments(long messageId)
		throws PortalException {

		getService().emptyMessageAttachments(messageId);
	}

	public static MBMessage fetchFileEntryMessage(long fileEntryId)
		throws PortalException {

		return getService().fetchFileEntryMessage(fileEntryId);
	}

	public static MBMessage fetchFirstMessage(
			long threadId, long parentMessageId)
		throws PortalException {

		return getService().fetchFirstMessage(threadId, parentMessageId);
	}

	public static MBMessage fetchMBMessage(long messageId) {
		return getService().fetchMBMessage(messageId);
	}

	public static MBMessage fetchMBMessageByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchMBMessageByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static MBMessage fetchMBMessageByUrlSubject(
		long groupId, String urlSubject) {

		return getService().fetchMBMessageByUrlSubject(groupId, urlSubject);
	}

	/**
	 * Returns the message-boards message matching the UUID and group.
	 *
	 * @param uuid the message-boards message's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchMBMessageByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchMBMessageByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end) {

		return getService().getCategoryMessages(
			groupId, categoryId, status, start, end);
	}

	public static List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getService().getCategoryMessages(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	public static List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, long threadId) {

		return getService().getCategoryMessages(groupId, categoryId, threadId);
	}

	public static int getCategoryMessagesCount(
		long groupId, long categoryId, int status) {

		return getService().getCategoryMessagesCount(
			groupId, categoryId, status);
	}

	public static List<MBMessage> getChildMessages(
		long parentMessageId, int status) {

		return getService().getChildMessages(parentMessageId, status);
	}

	public static List<MBMessage> getChildMessages(
		long parentMessageId, int status, int start, int end) {

		return getService().getChildMessages(
			parentMessageId, status, start, end);
	}

	public static int getChildMessagesCount(long parentMessageId, int status) {
		return getService().getChildMessagesCount(parentMessageId, status);
	}

	public static List<MBMessage> getCompanyMessages(
		long companyId, int status, int start, int end) {

		return getService().getCompanyMessages(companyId, status, start, end);
	}

	public static List<MBMessage> getCompanyMessages(
		long companyId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getService().getCompanyMessages(
			companyId, status, start, end, orderByComparator);
	}

	public static int getCompanyMessagesCount(long companyId, int status) {
		return getService().getCompanyMessagesCount(companyId, status);
	}

	public static com.liferay.message.boards.model.MBMessageDisplay
			getDiscussionMessageDisplay(
				long userId, long groupId, String className, long classPK,
				int status)
		throws PortalException {

		return getService().getDiscussionMessageDisplay(
			userId, groupId, className, classPK, status);
	}

	public static com.liferay.message.boards.model.MBMessageDisplay
			getDiscussionMessageDisplay(
				long userId, long groupId, String className, long classPK,
				int status, java.util.Comparator<MBMessage> comparator)
		throws PortalException {

		return getService().getDiscussionMessageDisplay(
			userId, groupId, className, classPK, status, comparator);
	}

	public static int getDiscussionMessagesCount(
		long classNameId, long classPK, int status) {

		return getService().getDiscussionMessagesCount(
			classNameId, classPK, status);
	}

	public static int getDiscussionMessagesCount(
		String className, long classPK, int status) {

		return getService().getDiscussionMessagesCount(
			className, classPK, status);
	}

	public static List<com.liferay.message.boards.model.MBDiscussion>
		getDiscussions(String className) {

		return getService().getDiscussions(className);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static MBMessage getFileEntryMessage(long fileEntryId)
		throws PortalException {

		return getService().getFileEntryMessage(fileEntryId);
	}

	public static MBMessage getFirstMessage(long threadId, long parentMessageId)
		throws PortalException {

		return getService().getFirstMessage(threadId, parentMessageId);
	}

	public static List<MBMessage> getGroupMessages(
		long groupId, int status, int start, int end) {

		return getService().getGroupMessages(groupId, status, start, end);
	}

	public static List<MBMessage> getGroupMessages(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getService().getGroupMessages(
			groupId, status, start, end, orderByComparator);
	}

	public static List<MBMessage> getGroupMessages(
		long groupId, long userId, int status, int start, int end) {

		return getService().getGroupMessages(
			groupId, userId, status, start, end);
	}

	public static List<MBMessage> getGroupMessages(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getService().getGroupMessages(
			groupId, userId, status, start, end, orderByComparator);
	}

	public static int getGroupMessagesCount(long groupId, int status) {
		return getService().getGroupMessagesCount(groupId, status);
	}

	public static int getGroupMessagesCount(
		long groupId, long userId, int status) {

		return getService().getGroupMessagesCount(groupId, userId, status);
	}

	public static List<MBMessage> getGroupUserMessageBoardMessagesActivity(
		long groupId, long userId, int start, int end) {

		return getService().getGroupUserMessageBoardMessagesActivity(
			groupId, userId, start, end);
	}

	public static int getGroupUserMessageBoardMessagesActivityCount(
		long groupId, long userId) {

		return getService().getGroupUserMessageBoardMessagesActivityCount(
			groupId, userId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static MBMessage getLastThreadMessage(long threadId, int status)
		throws PortalException {

		return getService().getLastThreadMessage(threadId, status);
	}

	/**
	 * Returns the message-boards message with the primary key.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message
	 * @throws PortalException if a message-boards message with the primary key could not be found
	 */
	public static MBMessage getMBMessage(long messageId)
		throws PortalException {

		return getService().getMBMessage(messageId);
	}

	public static MBMessage getMBMessageByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getMBMessageByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the message-boards message matching the UUID and group.
	 *
	 * @param uuid the message-boards message's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message-boards message
	 * @throws PortalException if a matching message-boards message could not be found
	 */
	public static MBMessage getMBMessageByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getMBMessageByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the message-boards messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of message-boards messages
	 */
	public static List<MBMessage> getMBMessages(int start, int end) {
		return getService().getMBMessages(start, end);
	}

	/**
	 * Returns all the message-boards messages matching the UUID and company.
	 *
	 * @param uuid the UUID of the message-boards messages
	 * @param companyId the primary key of the company
	 * @return the matching message-boards messages, or an empty list if no matches were found
	 */
	public static List<MBMessage> getMBMessagesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getMBMessagesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of message-boards messages matching the UUID and company.
	 *
	 * @param uuid the UUID of the message-boards messages
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message-boards messages, or an empty list if no matches were found
	 */
	public static List<MBMessage> getMBMessagesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getService().getMBMessagesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message-boards messages.
	 *
	 * @return the number of message-boards messages
	 */
	public static int getMBMessagesCount() {
		return getService().getMBMessagesCount();
	}

	public static MBMessage getMessage(long messageId) throws PortalException {
		return getService().getMessage(messageId);
	}

	public static com.liferay.message.boards.model.MBMessageDisplay
			getMessageDisplay(long userId, long messageId, int status)
		throws PortalException {

		return getService().getMessageDisplay(userId, messageId, status);
	}

	public static com.liferay.message.boards.model.MBMessageDisplay
			getMessageDisplay(long userId, MBMessage message, int status)
		throws PortalException {

		return getService().getMessageDisplay(userId, message, status);
	}

	public static com.liferay.message.boards.model.MBMessageDisplay
			getMessageDisplay(
				long userId, MBMessage message, int status,
				java.util.Comparator<MBMessage> comparator)
		throws PortalException {

		return getService().getMessageDisplay(
			userId, message, status, comparator);
	}

	public static List<MBMessage> getMessages(
		String className, long classPK, int status) {

		return getService().getMessages(className, classPK, status);
	}

	public static MBMessage getOrAddEmptyDiscussionMessage(
			String externalReferenceCode, long userId, long groupId,
			String className, long classPK)
		throws PortalException {

		return getService().getOrAddEmptyDiscussionMessage(
			externalReferenceCode, userId, groupId, className, classPK);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<MBMessage> getRootDiscussionMessages(
			String className, long classPK, int status)
		throws PortalException {

		return getService().getRootDiscussionMessages(
			className, classPK, status);
	}

	public static List<MBMessage> getRootDiscussionMessages(
			String className, long classPK, int status, int start, int end)
		throws PortalException {

		return getService().getRootDiscussionMessages(
			className, classPK, status, start, end);
	}

	public static int getRootDiscussionMessagesCount(
		String className, long classPK, int status) {

		return getService().getRootDiscussionMessagesCount(
			className, classPK, status);
	}

	public static String[] getTempAttachmentNames(
			long groupId, long userId, String folderName)
		throws PortalException {

		return getService().getTempAttachmentNames(groupId, userId, folderName);
	}

	public static List<MBMessage> getThreadMessages(long threadId, int status) {
		return getService().getThreadMessages(threadId, status);
	}

	public static List<MBMessage> getThreadMessages(
		long threadId, int status, java.util.Comparator<MBMessage> comparator) {

		return getService().getThreadMessages(threadId, status, comparator);
	}

	public static List<MBMessage> getThreadMessages(
		long threadId, int status, int start, int end) {

		return getService().getThreadMessages(threadId, status, start, end);
	}

	public static List<MBMessage> getThreadMessages(
		long threadId, long parentMessageId) {

		return getService().getThreadMessages(threadId, parentMessageId);
	}

	public static List<MBMessage> getThreadMessages(
		long userId, long threadId, int status, int start, int end,
		java.util.Comparator<MBMessage> comparator) {

		return getService().getThreadMessages(
			userId, threadId, status, start, end, comparator);
	}

	public static int getThreadMessagesCount(long threadId, int status) {
		return getService().getThreadMessagesCount(threadId, status);
	}

	public static List<MBMessage> getUserDiscussionMessages(
		long userId, long classNameId, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return getService().getUserDiscussionMessages(
			userId, classNameId, classPK, status, start, end,
			orderByComparator);
	}

	public static List<MBMessage> getUserDiscussionMessages(
		long userId, String className, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return getService().getUserDiscussionMessages(
			userId, className, classPK, status, start, end, orderByComparator);
	}

	public static int getUserDiscussionMessagesCount(
		long userId, long classNameId, long classPK, int status) {

		return getService().getUserDiscussionMessagesCount(
			userId, classNameId, classPK, status);
	}

	public static int getUserDiscussionMessagesCount(
		long userId, String className, long classPK, int status) {

		return getService().getUserDiscussionMessagesCount(
			userId, className, classPK, status);
	}

	public static long moveMessageAttachmentToTrash(
			long userId, long messageId, String fileName)
		throws PortalException {

		return getService().moveMessageAttachmentToTrash(
			userId, messageId, fileName);
	}

	public static void restoreMessageAttachmentFromTrash(
			long userId, long messageId, String deletedFileName)
		throws PortalException {

		getService().restoreMessageAttachmentFromTrash(
			userId, messageId, deletedFileName);
	}

	public static void subscribeMessage(long userId, long messageId)
		throws PortalException {

		getService().subscribeMessage(userId, messageId);
	}

	public static void unsubscribeMessage(long userId, long messageId)
		throws PortalException {

		getService().unsubscribeMessage(userId, messageId);
	}

	public static MBMessage updateAnswer(
			long messageId, boolean answer, boolean cascade)
		throws PortalException {

		return getService().updateAnswer(messageId, answer, cascade);
	}

	public static MBMessage updateAnswer(
			MBMessage message, boolean answer, boolean cascade)
		throws PortalException {

		return getService().updateAnswer(message, answer, cascade);
	}

	public static void updateAsset(
			long userId, MBMessage message, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException {

		getService().updateAsset(
			userId, message, assetCategoryIds, assetTagNames,
			assetLinkEntryIds);
	}

	public static MBMessage updateDiscussionMessage(
			long userId, long messageId, String className, long classPK,
			String subject, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateDiscussionMessage(
			userId, messageId, className, classPK, subject, body,
			serviceContext);
	}

	/**
	 * Updates the message-boards message in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbMessage the message-boards message
	 * @return the message-boards message that was updated
	 */
	public static MBMessage updateMBMessage(MBMessage mbMessage) {
		return getService().updateMBMessage(mbMessage);
	}

	public static MBMessage updateMessage(
			long userId, long messageId, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateMessage(
			userId, messageId, body, serviceContext);
	}

	public static MBMessage updateMessage(
			long userId, long messageId, String subject, String body,
			List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, InputStream>> inputStreamOVPs,
			double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateMessage(
			userId, messageId, subject, body, inputStreamOVPs, priority,
			allowPingbacks, serviceContext);
	}

	public static MBMessage updateStatus(
			long userId, long messageId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, messageId, status, serviceContext, workflowContext);
	}

	public static void updateUserName(long userId, String userName) {
		getService().updateUserName(userId, userName);
	}

	public static MBMessageLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MBMessageLocalService> _serviceSnapshot =
		new Snapshot<>(
			MBMessageLocalServiceUtil.class, MBMessageLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:500851615
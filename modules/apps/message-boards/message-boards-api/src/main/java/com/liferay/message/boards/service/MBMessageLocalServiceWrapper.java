/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link MBMessageLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see MBMessageLocalService
 * @generated
 */
public class MBMessageLocalServiceWrapper
	implements MBMessageLocalService, ServiceWrapper<MBMessageLocalService> {

	public MBMessageLocalServiceWrapper() {
		this(null);
	}

	public MBMessageLocalServiceWrapper(
		MBMessageLocalService mbMessageLocalService) {

		_mbMessageLocalService = mbMessageLocalService;
	}

	@Override
	public MBMessage addDiscussionMessage(
			long userId, String userName, long groupId, String className,
			long classPK, int workflowAction)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.addDiscussionMessage(
			userId, userName, groupId, className, classPK, workflowAction);
	}

	@Override
	public MBMessage addDiscussionMessage(
			String externalReferenceCode, long userId, String userName,
			long groupId, String className, long classPK, long threadId,
			long parentMessageId, String subject, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.addDiscussionMessage(
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
	@Override
	public MBMessage addMBMessage(MBMessage mbMessage) {
		return _mbMessageLocalService.addMBMessage(mbMessage);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			long threadId, long parentMessageId, String subject, String body,
			String format,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, java.io.InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.addMessage(
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
	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.addMessage(
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
	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, java.io.InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.addMessage(
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
	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format, String fileName,
			java.io.File file, boolean anonymous, double priority,
			boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			   java.io.FileNotFoundException {

		return _mbMessageLocalService.addMessage(
			userId, userName, groupId, categoryId, subject, body, format,
			fileName, file, anonymous, priority, allowPingbacks,
			serviceContext);
	}

	@Override
	public MBMessage addMessage(
			String externalReferenceCode, long userId, String userName,
			long groupId, long categoryId, long threadId, long parentMessageId,
			String subject, String body, String format,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, java.io.InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.addMessage(
			externalReferenceCode, userId, userName, groupId, categoryId,
			threadId, parentMessageId, subject, body, format, inputStreamOVPs,
			anonymous, priority, allowPingbacks, serviceContext);
	}

	@Override
	public void addMessageAttachment(
			long userId, long messageId, String fileName, java.io.File file,
			String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.addMessageAttachment(
			userId, messageId, fileName, file, mimeType);
	}

	@Override
	public void addMessageResources(
			long messageId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.addMessageResources(
			messageId, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addMessageResources(
			long messageId,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.addMessageResources(messageId, modelPermissions);
	}

	@Override
	public void addMessageResources(
			MBMessage message, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.addMessageResources(
			message, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addMessageResources(
			MBMessage message,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.addMessageResources(message, modelPermissions);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			addTempAttachment(
				long groupId, long userId, String folderName, String fileName,
				java.io.InputStream inputStream, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.addTempAttachment(
			groupId, userId, folderName, fileName, inputStream, mimeType);
	}

	/**
	 * Creates a new message-boards message with the primary key. Does not add the message-boards message to the database.
	 *
	 * @param messageId the primary key for the new message-boards message
	 * @return the new message-boards message
	 */
	@Override
	public MBMessage createMBMessage(long messageId) {
		return _mbMessageLocalService.createMBMessage(messageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public MBMessage deleteDiscussionMessage(long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.deleteDiscussionMessage(messageId);
	}

	@Override
	public void deleteDiscussionMessages(String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.deleteDiscussionMessages(className, classPK);
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
	@Override
	public MBMessage deleteMBMessage(long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.deleteMBMessage(messageId);
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
	@Override
	public MBMessage deleteMBMessage(MBMessage mbMessage) {
		return _mbMessageLocalService.deleteMBMessage(mbMessage);
	}

	@Override
	public MBMessage deleteMessage(long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.deleteMessage(messageId);
	}

	@Override
	public MBMessage deleteMessage(MBMessage message)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.deleteMessage(message);
	}

	@Override
	public void deleteMessageAttachment(long messageId, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.deleteMessageAttachment(messageId, fileName);
	}

	@Override
	public void deleteMessageAttachments(long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.deleteMessageAttachments(messageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteTempAttachment(
			long groupId, long userId, String folderName, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.deleteTempAttachment(
			groupId, userId, folderName, fileName);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _mbMessageLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _mbMessageLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _mbMessageLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _mbMessageLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _mbMessageLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _mbMessageLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _mbMessageLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _mbMessageLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public void emptyMessageAttachments(long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.emptyMessageAttachments(messageId);
	}

	@Override
	public MBMessage fetchFileEntryMessage(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.fetchFileEntryMessage(fileEntryId);
	}

	@Override
	public MBMessage fetchFirstMessage(long threadId, long parentMessageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.fetchFirstMessage(
			threadId, parentMessageId);
	}

	@Override
	public MBMessage fetchMBMessage(long messageId) {
		return _mbMessageLocalService.fetchMBMessage(messageId);
	}

	@Override
	public MBMessage fetchMBMessageByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _mbMessageLocalService.fetchMBMessageByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public MBMessage fetchMBMessageByUrlSubject(
		long groupId, String urlSubject) {

		return _mbMessageLocalService.fetchMBMessageByUrlSubject(
			groupId, urlSubject);
	}

	/**
	 * Returns the message-boards message matching the UUID and group.
	 *
	 * @param uuid the message-boards message's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Override
	public MBMessage fetchMBMessageByUuidAndGroupId(String uuid, long groupId) {
		return _mbMessageLocalService.fetchMBMessageByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _mbMessageLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end) {

		return _mbMessageLocalService.getCategoryMessages(
			groupId, categoryId, status, start, end);
	}

	@Override
	public java.util.List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBMessage>
			orderByComparator) {

		return _mbMessageLocalService.getCategoryMessages(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, long threadId) {

		return _mbMessageLocalService.getCategoryMessages(
			groupId, categoryId, threadId);
	}

	@Override
	public int getCategoryMessagesCount(
		long groupId, long categoryId, int status) {

		return _mbMessageLocalService.getCategoryMessagesCount(
			groupId, categoryId, status);
	}

	@Override
	public java.util.List<MBMessage> getChildMessages(
		long parentMessageId, int status) {

		return _mbMessageLocalService.getChildMessages(parentMessageId, status);
	}

	@Override
	public java.util.List<MBMessage> getChildMessages(
		long parentMessageId, int status, int start, int end) {

		return _mbMessageLocalService.getChildMessages(
			parentMessageId, status, start, end);
	}

	@Override
	public int getChildMessagesCount(long parentMessageId, int status) {
		return _mbMessageLocalService.getChildMessagesCount(
			parentMessageId, status);
	}

	@Override
	public java.util.List<MBMessage> getCompanyMessages(
		long companyId, int status, int start, int end) {

		return _mbMessageLocalService.getCompanyMessages(
			companyId, status, start, end);
	}

	@Override
	public java.util.List<MBMessage> getCompanyMessages(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBMessage>
			orderByComparator) {

		return _mbMessageLocalService.getCompanyMessages(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public int getCompanyMessagesCount(long companyId, int status) {
		return _mbMessageLocalService.getCompanyMessagesCount(
			companyId, status);
	}

	@Override
	public com.liferay.message.boards.model.MBMessageDisplay
			getDiscussionMessageDisplay(
				long userId, long groupId, String className, long classPK,
				int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getDiscussionMessageDisplay(
			userId, groupId, className, classPK, status);
	}

	@Override
	public com.liferay.message.boards.model.MBMessageDisplay
			getDiscussionMessageDisplay(
				long userId, long groupId, String className, long classPK,
				int status, java.util.Comparator<MBMessage> comparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getDiscussionMessageDisplay(
			userId, groupId, className, classPK, status, comparator);
	}

	@Override
	public int getDiscussionMessagesCount(
		long classNameId, long classPK, int status) {

		return _mbMessageLocalService.getDiscussionMessagesCount(
			classNameId, classPK, status);
	}

	@Override
	public int getDiscussionMessagesCount(
		String className, long classPK, int status) {

		return _mbMessageLocalService.getDiscussionMessagesCount(
			className, classPK, status);
	}

	@Override
	public java.util.List<com.liferay.message.boards.model.MBDiscussion>
		getDiscussions(String className) {

		return _mbMessageLocalService.getDiscussions(className);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _mbMessageLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public MBMessage getFileEntryMessage(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getFileEntryMessage(fileEntryId);
	}

	@Override
	public MBMessage getFirstMessage(long threadId, long parentMessageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getFirstMessage(
			threadId, parentMessageId);
	}

	@Override
	public java.util.List<MBMessage> getGroupMessages(
		long groupId, int status, int start, int end) {

		return _mbMessageLocalService.getGroupMessages(
			groupId, status, start, end);
	}

	@Override
	public java.util.List<MBMessage> getGroupMessages(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBMessage>
			orderByComparator) {

		return _mbMessageLocalService.getGroupMessages(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<MBMessage> getGroupMessages(
		long groupId, long userId, int status, int start, int end) {

		return _mbMessageLocalService.getGroupMessages(
			groupId, userId, status, start, end);
	}

	@Override
	public java.util.List<MBMessage> getGroupMessages(
		long groupId, long userId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBMessage>
			orderByComparator) {

		return _mbMessageLocalService.getGroupMessages(
			groupId, userId, status, start, end, orderByComparator);
	}

	@Override
	public int getGroupMessagesCount(long groupId, int status) {
		return _mbMessageLocalService.getGroupMessagesCount(groupId, status);
	}

	@Override
	public int getGroupMessagesCount(long groupId, long userId, int status) {
		return _mbMessageLocalService.getGroupMessagesCount(
			groupId, userId, status);
	}

	@Override
	public java.util.List<MBMessage> getGroupUserMessageBoardMessagesActivity(
		long groupId, long userId, int start, int end) {

		return _mbMessageLocalService.getGroupUserMessageBoardMessagesActivity(
			groupId, userId, start, end);
	}

	@Override
	public int getGroupUserMessageBoardMessagesActivityCount(
		long groupId, long userId) {

		return _mbMessageLocalService.
			getGroupUserMessageBoardMessagesActivityCount(groupId, userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _mbMessageLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public MBMessage getLastThreadMessage(long threadId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getLastThreadMessage(threadId, status);
	}

	/**
	 * Returns the message-boards message with the primary key.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message
	 * @throws PortalException if a message-boards message with the primary key could not be found
	 */
	@Override
	public MBMessage getMBMessage(long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getMBMessage(messageId);
	}

	@Override
	public MBMessage getMBMessageByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getMBMessageByExternalReferenceCode(
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
	@Override
	public MBMessage getMBMessageByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getMBMessageByUuidAndGroupId(
			uuid, groupId);
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
	@Override
	public java.util.List<MBMessage> getMBMessages(int start, int end) {
		return _mbMessageLocalService.getMBMessages(start, end);
	}

	/**
	 * Returns all the message-boards messages matching the UUID and company.
	 *
	 * @param uuid the UUID of the message-boards messages
	 * @param companyId the primary key of the company
	 * @return the matching message-boards messages, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<MBMessage> getMBMessagesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _mbMessageLocalService.getMBMessagesByUuidAndCompanyId(
			uuid, companyId);
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
	@Override
	public java.util.List<MBMessage> getMBMessagesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBMessage>
			orderByComparator) {

		return _mbMessageLocalService.getMBMessagesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message-boards messages.
	 *
	 * @return the number of message-boards messages
	 */
	@Override
	public int getMBMessagesCount() {
		return _mbMessageLocalService.getMBMessagesCount();
	}

	@Override
	public MBMessage getMessage(long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getMessage(messageId);
	}

	@Override
	public com.liferay.message.boards.model.MBMessageDisplay getMessageDisplay(
			long userId, long messageId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getMessageDisplay(
			userId, messageId, status);
	}

	@Override
	public com.liferay.message.boards.model.MBMessageDisplay getMessageDisplay(
			long userId, MBMessage message, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getMessageDisplay(
			userId, message, status);
	}

	@Override
	public com.liferay.message.boards.model.MBMessageDisplay getMessageDisplay(
			long userId, MBMessage message, int status,
			java.util.Comparator<MBMessage> comparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getMessageDisplay(
			userId, message, status, comparator);
	}

	@Override
	public java.util.List<MBMessage> getMessages(
		String className, long classPK, int status) {

		return _mbMessageLocalService.getMessages(className, classPK, status);
	}

	@Override
	public MBMessage getOrAddEmptyDiscussionMessage(
			String externalReferenceCode, long userId, long groupId,
			String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getOrAddEmptyDiscussionMessage(
			externalReferenceCode, userId, groupId, className, classPK);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _mbMessageLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<MBMessage> getRootDiscussionMessages(
			String className, long classPK, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getRootDiscussionMessages(
			className, classPK, status);
	}

	@Override
	public java.util.List<MBMessage> getRootDiscussionMessages(
			String className, long classPK, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getRootDiscussionMessages(
			className, classPK, status, start, end);
	}

	@Override
	public int getRootDiscussionMessagesCount(
		String className, long classPK, int status) {

		return _mbMessageLocalService.getRootDiscussionMessagesCount(
			className, classPK, status);
	}

	@Override
	public String[] getTempAttachmentNames(
			long groupId, long userId, String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.getTempAttachmentNames(
			groupId, userId, folderName);
	}

	@Override
	public java.util.List<MBMessage> getThreadMessages(
		long threadId, int status) {

		return _mbMessageLocalService.getThreadMessages(threadId, status);
	}

	@Override
	public java.util.List<MBMessage> getThreadMessages(
		long threadId, int status, java.util.Comparator<MBMessage> comparator) {

		return _mbMessageLocalService.getThreadMessages(
			threadId, status, comparator);
	}

	@Override
	public java.util.List<MBMessage> getThreadMessages(
		long threadId, int status, int start, int end) {

		return _mbMessageLocalService.getThreadMessages(
			threadId, status, start, end);
	}

	@Override
	public java.util.List<MBMessage> getThreadMessages(
		long threadId, long parentMessageId) {

		return _mbMessageLocalService.getThreadMessages(
			threadId, parentMessageId);
	}

	@Override
	public java.util.List<MBMessage> getThreadMessages(
		long userId, long threadId, int status, int start, int end,
		java.util.Comparator<MBMessage> comparator) {

		return _mbMessageLocalService.getThreadMessages(
			userId, threadId, status, start, end, comparator);
	}

	@Override
	public int getThreadMessagesCount(long threadId, int status) {
		return _mbMessageLocalService.getThreadMessagesCount(threadId, status);
	}

	@Override
	public java.util.List<MBMessage> getUserDiscussionMessages(
		long userId, long classNameId, long classPK, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBMessage>
			orderByComparator) {

		return _mbMessageLocalService.getUserDiscussionMessages(
			userId, classNameId, classPK, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<MBMessage> getUserDiscussionMessages(
		long userId, String className, long classPK, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBMessage>
			orderByComparator) {

		return _mbMessageLocalService.getUserDiscussionMessages(
			userId, className, classPK, status, start, end, orderByComparator);
	}

	@Override
	public int getUserDiscussionMessagesCount(
		long userId, long classNameId, long classPK, int status) {

		return _mbMessageLocalService.getUserDiscussionMessagesCount(
			userId, classNameId, classPK, status);
	}

	@Override
	public int getUserDiscussionMessagesCount(
		long userId, String className, long classPK, int status) {

		return _mbMessageLocalService.getUserDiscussionMessagesCount(
			userId, className, classPK, status);
	}

	@Override
	public long moveMessageAttachmentToTrash(
			long userId, long messageId, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.moveMessageAttachmentToTrash(
			userId, messageId, fileName);
	}

	@Override
	public void restoreMessageAttachmentFromTrash(
			long userId, long messageId, String deletedFileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.restoreMessageAttachmentFromTrash(
			userId, messageId, deletedFileName);
	}

	@Override
	public void subscribeMessage(long userId, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.subscribeMessage(userId, messageId);
	}

	@Override
	public void unsubscribeMessage(long userId, long messageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.unsubscribeMessage(userId, messageId);
	}

	@Override
	public MBMessage updateAnswer(
			long messageId, boolean answer, boolean cascade)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.updateAnswer(messageId, answer, cascade);
	}

	@Override
	public MBMessage updateAnswer(
			MBMessage message, boolean answer, boolean cascade)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.updateAnswer(message, answer, cascade);
	}

	@Override
	public void updateAsset(
			long userId, MBMessage message, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMessageLocalService.updateAsset(
			userId, message, assetCategoryIds, assetTagNames,
			assetLinkEntryIds);
	}

	@Override
	public MBMessage updateDiscussionMessage(
			long userId, long messageId, String className, long classPK,
			String subject, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.updateDiscussionMessage(
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
	@Override
	public MBMessage updateMBMessage(MBMessage mbMessage) {
		return _mbMessageLocalService.updateMBMessage(mbMessage);
	}

	@Override
	public MBMessage updateMessage(
			long userId, long messageId, String body,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.updateMessage(
			userId, messageId, body, serviceContext);
	}

	@Override
	public MBMessage updateMessage(
			long userId, long messageId, String subject, String body,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair
					<String, java.io.InputStream>> inputStreamOVPs,
			double priority, boolean allowPingbacks,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.updateMessage(
			userId, messageId, subject, body, inputStreamOVPs, priority,
			allowPingbacks, serviceContext);
	}

	@Override
	public MBMessage updateStatus(
			long userId, long messageId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMessageLocalService.updateStatus(
			userId, messageId, status, serviceContext, workflowContext);
	}

	@Override
	public void updateUserName(long userId, String userName) {
		_mbMessageLocalService.updateUserName(userId, userName);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _mbMessageLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<MBMessage> getCTPersistence() {
		return _mbMessageLocalService.getCTPersistence();
	}

	@Override
	public Class<MBMessage> getModelClass() {
		return _mbMessageLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<MBMessage>, R, E> updateUnsafeFunction)
		throws E {

		return _mbMessageLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public MBMessageLocalService getWrappedService() {
		return _mbMessageLocalService;
	}

	@Override
	public void setWrappedService(MBMessageLocalService mbMessageLocalService) {
		_mbMessageLocalService = mbMessageLocalService;
	}

	private MBMessageLocalService _mbMessageLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1164699470
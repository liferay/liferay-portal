/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for MBMessage. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see MBMessageLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface MBMessageLocalService
	extends BaseLocalService, CTService<MBMessage>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBMessageLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the message-boards message local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link MBMessageLocalServiceUtil} if injection and service tracking are not available.
	 */
	public MBMessage addDiscussionMessage(
			long userId, String userName, long groupId, String className,
			long classPK, int workflowAction)
		throws PortalException;

	public MBMessage addDiscussionMessage(
			String externalReferenceCode, long userId, String userName,
			long groupId, String className, long classPK, long threadId,
			long parentMessageId, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public MBMessage addMBMessage(MBMessage mbMessage);

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			long threadId, long parentMessageId, String subject, String body,
			String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addMessage(String, long, String, long, long, long, long,
	 String, String, String, List, boolean, double, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format, String fileName,
			File file, boolean anonymous, double priority,
			boolean allowPingbacks, ServiceContext serviceContext)
		throws FileNotFoundException, PortalException;

	public MBMessage addMessage(
			String externalReferenceCode, long userId, String userName,
			long groupId, long categoryId, long threadId, long parentMessageId,
			String subject, String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException;

	public void addMessageAttachment(
			long userId, long messageId, String fileName, File file,
			String mimeType)
		throws PortalException;

	public void addMessageResources(
			long messageId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addMessageResources(
			long messageId, ModelPermissions modelPermissions)
		throws PortalException;

	public void addMessageResources(
			MBMessage message, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addMessageResources(
			MBMessage message, ModelPermissions modelPermissions)
		throws PortalException;

	public FileEntry addTempAttachment(
			long groupId, long userId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException;

	/**
	 * Creates a new message-boards message with the primary key. Does not add the message-boards message to the database.
	 *
	 * @param messageId the primary key for the new message-boards message
	 * @return the new message-boards message
	 */
	@Transactional(enabled = false)
	public MBMessage createMBMessage(long messageId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public MBMessage deleteDiscussionMessage(long messageId)
		throws PortalException;

	public void deleteDiscussionMessages(String className, long classPK)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public MBMessage deleteMBMessage(long messageId) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public MBMessage deleteMBMessage(MBMessage mbMessage);

	@Indexable(type = IndexableType.DELETE)
	public MBMessage deleteMessage(long messageId) throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public MBMessage deleteMessage(MBMessage message) throws PortalException;

	public void deleteMessageAttachment(long messageId, String fileName)
		throws PortalException;

	public void deleteMessageAttachments(long messageId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteTempAttachment(
			long groupId, long userId, String folderName, String fileName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	public void emptyMessageAttachments(long messageId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage fetchFileEntryMessage(long fileEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage fetchFirstMessage(long threadId, long parentMessageId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage fetchMBMessage(long messageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage fetchMBMessageByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage fetchMBMessageByUrlSubject(
		long groupId, String urlSubject);

	/**
	 * Returns the message-boards message matching the UUID and group.
	 *
	 * @param uuid the message-boards message's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage fetchMBMessageByUuidAndGroupId(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, long threadId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoryMessagesCount(
		long groupId, long categoryId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getChildMessages(long parentMessageId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getChildMessages(
		long parentMessageId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getChildMessagesCount(long parentMessageId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getCompanyMessages(
		long companyId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getCompanyMessages(
		long companyId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyMessagesCount(long companyId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessageDisplay getDiscussionMessageDisplay(
			long userId, long groupId, String className, long classPK,
			int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessageDisplay getDiscussionMessageDisplay(
			long userId, long groupId, String className, long classPK,
			int status, Comparator<MBMessage> comparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDiscussionMessagesCount(
		long classNameId, long classPK, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDiscussionMessagesCount(
		String className, long classPK, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBDiscussion> getDiscussions(String className);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getFileEntryMessage(long fileEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getFirstMessage(long threadId, long parentMessageId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getGroupMessages(
		long groupId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getGroupMessages(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getGroupMessages(
		long groupId, long userId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getGroupMessages(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupMessagesCount(long groupId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupMessagesCount(long groupId, long userId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getGroupUserMessageBoardMessagesActivity(
		long groupId, long userId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupUserMessageBoardMessagesActivityCount(
		long groupId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getLastThreadMessage(long threadId, int status)
		throws PortalException;

	/**
	 * Returns the message-boards message with the primary key.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message
	 * @throws PortalException if a message-boards message with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getMBMessage(long messageId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getMBMessageByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the message-boards message matching the UUID and group.
	 *
	 * @param uuid the message-boards message's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message-boards message
	 * @throws PortalException if a matching message-boards message could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getMBMessageByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getMBMessages(int start, int end);

	/**
	 * Returns all the message-boards messages matching the UUID and company.
	 *
	 * @param uuid the UUID of the message-boards messages
	 * @param companyId the primary key of the company
	 * @return the matching message-boards messages, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getMBMessagesByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getMBMessagesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator);

	/**
	 * Returns the number of message-boards messages.
	 *
	 * @return the number of message-boards messages
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getMBMessagesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getMessage(long messageId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessageDisplay getMessageDisplay(
			long userId, long messageId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessageDisplay getMessageDisplay(
			long userId, MBMessage message, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessageDisplay getMessageDisplay(
			long userId, MBMessage message, int status,
			Comparator<MBMessage> comparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getMessages(
		String className, long classPK, int status);

	@Indexable(type = IndexableType.REINDEX)
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBMessage getOrAddEmptyDiscussionMessage(
			String externalReferenceCode, long userId, long groupId,
			String className, long classPK)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getRootDiscussionMessages(
			String className, long classPK, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getRootDiscussionMessages(
			String className, long classPK, int status, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRootDiscussionMessagesCount(
		String className, long classPK, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTempAttachmentNames(
			long groupId, long userId, String folderName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getThreadMessages(long threadId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getThreadMessages(
		long threadId, int status, Comparator<MBMessage> comparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getThreadMessages(
		long threadId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getThreadMessages(
		long threadId, long parentMessageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getThreadMessages(
		long userId, long threadId, int status, int start, int end,
		Comparator<MBMessage> comparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getThreadMessagesCount(long threadId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getUserDiscussionMessages(
		long userId, long classNameId, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<MBMessage> getUserDiscussionMessages(
		long userId, String className, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserDiscussionMessagesCount(
		long userId, long classNameId, long classPK, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserDiscussionMessagesCount(
		long userId, String className, long classPK, int status);

	public long moveMessageAttachmentToTrash(
			long userId, long messageId, String fileName)
		throws PortalException;

	public void restoreMessageAttachmentFromTrash(
			long userId, long messageId, String deletedFileName)
		throws PortalException;

	public void subscribeMessage(long userId, long messageId)
		throws PortalException;

	public void unsubscribeMessage(long userId, long messageId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public MBMessage updateAnswer(
			long messageId, boolean answer, boolean cascade)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public MBMessage updateAnswer(
			MBMessage message, boolean answer, boolean cascade)
		throws PortalException;

	public void updateAsset(
			long userId, MBMessage message, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException;

	public MBMessage updateDiscussionMessage(
			long userId, long messageId, String className, long classPK,
			String subject, String body, ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public MBMessage updateMBMessage(MBMessage mbMessage);

	public MBMessage updateMessage(
			long userId, long messageId, String body,
			ServiceContext serviceContext)
		throws PortalException;

	public MBMessage updateMessage(
			long userId, long messageId, String subject, String body,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException;

	public MBMessage updateStatus(
			long userId, long messageId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException;

	public void updateUserName(long userId, String userName);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<MBMessage> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<MBMessage> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<MBMessage>, R, E> updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-228462142
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.comment.configuration.CommentGroupServiceConfiguration;
import com.liferay.comment.constants.CommentConstants;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.constants.MBThreadConstants;
import com.liferay.message.boards.exception.DiscussionMaxCommentsException;
import com.liferay.message.boards.exception.MessageBodyException;
import com.liferay.message.boards.exception.MessageSubjectException;
import com.liferay.message.boards.exception.NoSuchThreadException;
import com.liferay.message.boards.exception.RequiredMessageException;
import com.liferay.message.boards.internal.helper.MBMessageNotificationTemplateHelper;
import com.liferay.message.boards.internal.util.MBDiscussionSubscriptionSender;
import com.liferay.message.boards.internal.util.MBMailUtil;
import com.liferay.message.boards.internal.util.MBMessageUtil;
import com.liferay.message.boards.internal.util.MBSubscriptionSender;
import com.liferay.message.boards.internal.util.MBUtil;
import com.liferay.message.boards.internal.util.MailingListThreadLocal;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBMessageTable;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.impl.MBCategoryImpl;
import com.liferay.message.boards.model.impl.MBMessageDisplayImpl;
import com.liferay.message.boards.service.MBDiscussionLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.message.boards.service.base.MBMessageLocalServiceBaseImpl;
import com.liferay.message.boards.service.persistence.MBCategoryPersistence;
import com.liferay.message.boards.service.persistence.MBThreadPersistence;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.message.boards.social.MBActivityKeys;
import com.liferay.message.boards.util.comparator.MessageCreateDateComparator;
import com.liferay.message.boards.util.comparator.MessageThreadComparator;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.json.jabsorb.serializer.LiferayJSONDeserializationWhitelist;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.CopyLayoutThreadLocal;
import com.liferay.portal.kernel.util.EscapableLocalizableFunction;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.linkback.LinkbackProducerUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.subscription.service.SubscriptionLocalService;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Mika Koivisto
 * @author Jorge Ferrer
 * @author Juan Fernández
 * @author Shuyang Zhou
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBMessage",
	service = AopService.class
)
public class MBMessageLocalServiceImpl extends MBMessageLocalServiceBaseImpl {

	@Override
	public MBMessage addDiscussionMessage(
			long userId, String userName, long groupId, String className,
			long classPK, int workflowAction)
		throws PortalException {

		long threadId = 0;
		long parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;

		String subject = String.valueOf(classPK);

		String body = subject;

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setWorkflowAction(workflowAction);

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		WorkflowThreadLocal.setEnabled(false);

		try {
			return addDiscussionMessage(
				null, userId, userName, groupId, className, classPK, threadId,
				parentMessageId, subject, body, serviceContext);
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	@Override
	public MBMessage addDiscussionMessage(
			String externalReferenceCode, long userId, String userName,
			long groupId, String className, long classPK, long threadId,
			long parentMessageId, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException {

		// Message

		_validateDiscussionMaxComments(className, classPK);

		long categoryId = MBCategoryConstants.DISCUSSION_CATEGORY_ID;
		subject = _getDiscussionMessageSubject(subject, body);
		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			Collections.emptyList();
		boolean anonymous = false;
		double priority = 0.0;
		boolean allowPingbacks = false;

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAttribute("className", className);
		serviceContext.setAttribute("classPK", String.valueOf(classPK));

		Date date = new Date();

		if (serviceContext.getCreateDate() == null) {
			serviceContext.setCreateDate(date);
		}

		if (serviceContext.getModifiedDate() == null) {
			serviceContext.setModifiedDate(date);
		}

		MBMessage message = addMessage(
			externalReferenceCode, userId, userName, groupId, categoryId,
			threadId, parentMessageId, subject, body,
			PropsValues.DISCUSSION_COMMENTS_FORMAT, inputStreamOVPs, anonymous,
			priority, allowPingbacks, serviceContext);

		// Discussion

		if (parentMessageId == MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID) {
			long classNameId = _classNameLocalService.getClassNameId(className);

			MBDiscussion discussion = _mbDiscussionLocalService.fetchDiscussion(
				classNameId, classPK);

			if (discussion == null) {
				_mbDiscussionLocalService.addDiscussion(
					userId, groupId, classNameId, classPK,
					message.getThreadId(), serviceContext);
			}
		}

		return message;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addMessage(String, long, String, long, long, long, long,
	 *             String, String, String, List, boolean, double, boolean,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			long threadId, long parentMessageId, String subject, String body,
			String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		return addMessage(
			null, userId, userName, groupId, categoryId, threadId,
			parentMessageId, subject, body, format, inputStreamOVPs, anonymous,
			priority, allowPingbacks, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addMessage(String, long, String, long, long, long, long,
	 *             String, String, String, List, boolean, double, boolean,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, ServiceContext serviceContext)
		throws PortalException {

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			Collections.emptyList();

		return addMessage(
			userId, userName, groupId, categoryId, 0, 0, subject, body,
			MBMessageConstants.DEFAULT_FORMAT, inputStreamOVPs, false, 0.0,
			false, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addMessage(String, long, String, long, long, long, long,
	 *             String, String, String, List, boolean, double, boolean,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		long threadId = 0;
		long parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;

		return addMessage(
			userId, userName, groupId, categoryId, threadId, parentMessageId,
			subject, body, format, inputStreamOVPs, anonymous, priority,
			allowPingbacks, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addMessage(String, long, String, long, long, long, long,
	 *             String, String, String, List, boolean, double, boolean,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format, String fileName,
			File file, boolean anonymous, double priority,
			boolean allowPingbacks, ServiceContext serviceContext)
		throws FileNotFoundException, PortalException {

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			new ArrayList<>(1);

		InputStream inputStream = new FileInputStream(file);

		ObjectValuePair<String, InputStream> inputStreamOVP =
			new ObjectValuePair<>(fileName, inputStream);

		inputStreamOVPs.add(inputStreamOVP);

		return addMessage(
			userId, userName, groupId, categoryId, 0, 0, subject, body, format,
			inputStreamOVPs, anonymous, priority, allowPingbacks,
			serviceContext);
	}

	@Override
	public MBMessage addMessage(
			String externalReferenceCode, long userId, String userName,
			long groupId, long categoryId, long threadId, long parentMessageId,
			String subject, String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		MBMessage parentMBMessage = fetchMBMessage(parentMessageId);

		if ((parentMBMessage != null) && !parentMBMessage.isApproved()) {
			throw new PortalException("Parent message is not approved");
		}

		// Message

		Group group = _groupLocalService.getGroup(groupId);

		User user = _userLocalService.fetchUser(
			_portal.getValidUserId(group.getCompanyId(), userId));

		userName = user.isGuestUser() ? userName : user.getFullName();

		subject = ModelHintsUtil.trimString(
			MBMessage.class.getName(), "subject", subject);

		if (!com.liferay.message.boards.util.MBUtil.isValidMessageFormat(
				format)) {

			format = "html";
		}

		if (anonymous || user.isGuestUser()) {
			MBGroupServiceSettings mbGroupServiceSettings =
				MBGroupServiceSettings.getInstance(groupId);

			if ((mbGroupServiceSettings != null) &&
				!mbGroupServiceSettings.isAllowAnonymousPosting()) {

				throw new PrincipalException.MustHavePermission(
					userId, ActionKeys.ADD_MESSAGE);
			}
		}

		if (user.isGuestUser()) {
			anonymous = true;
		}

		Date date = new Date();

		Date modifiedDate = serviceContext.getModifiedDate(date);

		long messageId = counterLocalService.increment();

		subject = _getSubject(subject, body);

		body = _getBody(subject, body, format);

		body = SanitizerUtil.sanitize(
			user.getCompanyId(), groupId, userId, MBMessage.class.getName(),
			messageId, "text/" + format, Sanitizer.MODE_ALL, body,
			HashMapBuilder.<String, Object>put(
				"discussion",
				() -> {
					if (categoryId ==
							MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

						return true;
					}

					return false;
				}
			).build());

		_validate(subject, body);

		MBMessage message = mbMessagePersistence.create(messageId);

		message.setUuid(serviceContext.getUuid());
		message.setExternalReferenceCode(externalReferenceCode);
		message.setGroupId(groupId);
		message.setCompanyId(user.getCompanyId());
		message.setUserId(user.getUserId());
		message.setUserName(userName);
		message.setCreateDate(serviceContext.getCreateDate(date));
		message.setModifiedDate(modifiedDate);

		if (threadId > 0) {
			message.setThreadId(threadId);
		}

		if (priority != MBThreadConstants.PRIORITY_NOT_GIVEN) {
			message.setPriority(priority);
		}

		message.setSubject(subject);
		message.setUrlSubject(
			_getUniqueUrlSubject(groupId, messageId, subject));
		message.setAllowPingbacks(allowPingbacks);
		message.setStatus(WorkflowConstants.STATUS_DRAFT);
		message.setStatusByUserId(user.getUserId());
		message.setStatusByUserName(userName);
		message.setStatusDate(modifiedDate);

		// Thread

		if (parentMessageId != MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID) {
			MBMessage parentMessage = mbMessagePersistence.fetchByPrimaryKey(
				parentMessageId);

			if (parentMessage == null) {
				parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
			}
		}

		MBThread thread = null;

		if (threadId > 0) {
			thread = _mbThreadPersistence.fetchByPrimaryKey(threadId);
		}

		if (thread == null) {
			if (parentMessageId ==
					MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID) {

				thread = _mbThreadLocalService.addThread(
					categoryId, message, serviceContext);
			}
			else {
				throw new NoSuchThreadException("{threadId=" + threadId + "}");
			}
		}

		if ((priority != MBThreadConstants.PRIORITY_NOT_GIVEN) &&
			(thread.getPriority() != priority)) {

			thread.setPriority(priority);

			thread = _mbThreadLocalService.updateMBThread(thread);

			_updatePriorities(thread.getThreadId(), priority);
		}

		// Message

		message.setCategoryId(categoryId);
		message.setThreadId(thread.getThreadId());
		message.setRootMessageId(thread.getRootMessageId());
		message.setParentMessageId(parentMessageId);
		message.setTreePath(message.buildTreePath());
		message.setBody(body);
		message.setFormat(format);
		message.setAnonymous(anonymous);

		if (message.isDiscussion()) {
			message.setClassNameId(
				_classNameLocalService.getClassNameId(
					(String)serviceContext.getAttribute("className")));
			message.setClassPK(ParamUtil.getLong(serviceContext, "classPK"));
		}

		message.setExpandoBridgeAttributes(serviceContext);

		message = mbMessagePersistence.update(message);

		// Resources

		if ((parentMessageId != MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID) &&
			GetterUtil.getBoolean(
				serviceContext.getAttribute("propagatePermissions"))) {

			MBUtil.propagatePermissions(
				message.getCompanyId(), groupId, parentMessageId,
				serviceContext);
		}

		if (!message.isDiscussion()) {
			if (user.isGuestUser()) {
				addMessageResources(message, true, true);
			}
			else if (serviceContext.isAddGroupPermissions() ||
					 serviceContext.isAddGuestPermissions()) {

				addMessageResources(
					message, serviceContext.isAddGroupPermissions(),
					serviceContext.isAddGuestPermissions());
			}
			else {
				addMessageResources(
					message, serviceContext.getModelPermissions());
			}
		}

		// Attachments

		if (ListUtil.isNotEmpty(inputStreamOVPs)) {
			Folder folder = message.addAttachmentsFolder();

			_portletFileRepository.addPortletFileEntries(
				message.getGroupId(), userId, MBMessage.class.getName(),
				message.getMessageId(), MBConstants.SERVICE_NAME,
				folder.getFolderId(), inputStreamOVPs);
		}

		// Asset

		_updateAsset(
			userId, message, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.isAssetEntryVisible());

		// Workflow

		return _startWorkflowInstance(userId, message, serviceContext);
	}

	@Override
	public void addMessageAttachment(
			long userId, long messageId, String fileName, File file,
			String mimeType)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		Folder folder = message.addAttachmentsFolder();

		_portletFileRepository.addPortletFileEntry(
			null, message.getGroupId(), userId, MBMessage.class.getName(),
			message.getMessageId(), MBConstants.SERVICE_NAME,
			folder.getFolderId(), file, fileName, mimeType, true);
	}

	@Override
	public void addMessageResources(
			long messageId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		addMessageResources(message, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addMessageResources(
			long messageId, ModelPermissions modelPermissions)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		addMessageResources(message, modelPermissions);
	}

	@Override
	public void addMessageResources(
			MBMessage message, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			message.getCompanyId(), message.getGroupId(), message.getUserId(),
			MBMessage.class.getName(), message.getMessageId(), false,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addMessageResources(
			MBMessage message, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			message.getCompanyId(), message.getGroupId(), message.getUserId(),
			MBMessage.class.getName(), message.getMessageId(),
			modelPermissions);
	}

	@Override
	public FileEntry addTempAttachment(
			long groupId, long userId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		return TempFileEntryUtil.addTempFileEntry(
			groupId, userId, folderName, fileName, inputStream, mimeType);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public MBMessage deleteDiscussionMessage(long messageId)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		SocialActivityManagerUtil.deleteActivities(message);

		return mbMessageLocalService.deleteMessage(messageId);
	}

	@Override
	public void deleteDiscussionMessages(String className, long classPK)
		throws PortalException {

		MBDiscussion discussion = _mbDiscussionLocalService.fetchDiscussion(
			_classNameLocalService.getClassNameId(className), classPK);

		if (discussion == null) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Unable to delete discussion message for class name ",
						className, " and class PK ", classPK,
						" because it does not exist"));
			}

			return;
		}

		List<MBMessage> messages = mbMessagePersistence.findByT_P(
			discussion.getThreadId(),
			MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, 0, 1);

		if (!messages.isEmpty()) {
			MBMessage message = messages.get(0);

			SocialActivityManagerUtil.deleteActivities(message);

			MBThread thread = _mbThreadPersistence.findByPrimaryKey(
				message.getThreadId());

			_mbThreadLocalService.deleteThread(thread);
		}

		_mbDiscussionLocalService.deleteMBDiscussion(discussion);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public MBMessage deleteMessage(long messageId) throws PortalException {
		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		return mbMessageLocalService.deleteMessage(message);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public MBMessage deleteMessage(MBMessage message) throws PortalException {

		// Attachments

		long folderId = message.getAttachmentsFolderId();

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_portletFileRepository.deletePortletFolder(folderId);
		}

		// Thread

		int count = mbMessagePersistence.countByThreadId(message.getThreadId());

		if (count == 1) {

			// Attachments

			long threadAttachmentsFolderId =
				message.getThreadAttachmentsFolderId();

			if (threadAttachmentsFolderId !=
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				_portletFileRepository.deletePortletFolder(
					threadAttachmentsFolderId);
			}

			// Subscriptions

			_subscriptionLocalService.deleteSubscriptions(
				message.getCompanyId(), MBThread.class.getName(),
				message.getThreadId());

			// Thread

			MBThread thread = _mbThreadPersistence.findByPrimaryKey(
				message.getThreadId());

			_mbThreadLocalService.deleteMBThread(thread);

			// Discussion

			if (message.isDiscussion()) {
				MBDiscussion discussion =
					_mbDiscussionLocalService.getThreadDiscussion(
						message.getThreadId());

				_mbDiscussionLocalService.deleteMBDiscussion(
					discussion.getDiscussionId());
			}

			// Indexer

			Indexer<MBThread> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				MBThread.class);

			indexer.delete(thread);
		}
		else {
			MBThread thread = _mbThreadPersistence.findByPrimaryKey(
				message.getThreadId());

			// Message is a root message

			if (thread.getRootMessageId() == message.getMessageId()) {
				List<MBMessage> childrenMessages =
					mbMessagePersistence.findByT_P(
						message.getThreadId(), message.getMessageId());

				if (childrenMessages.size() > 1) {
					throw new RequiredMessageException(
						String.valueOf(message.getMessageId()));
				}
				else if (childrenMessages.size() == 1) {
					MBMessage childMessage = childrenMessages.get(0);

					long defaultParentMessageId =
						MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;

					childMessage.setRootMessageId(childMessage.getMessageId());
					childMessage.setParentMessageId(defaultParentMessageId);
					childMessage.setTreePath(childMessage.buildTreePath());

					childMessage = mbMessagePersistence.update(childMessage);

					List<MBMessage> repliesMessages =
						mbMessagePersistence.findByThreadIdReplies(
							message.getThreadId());

					for (MBMessage repliesMessage : repliesMessages) {
						repliesMessage.setRootMessageId(
							childMessage.getMessageId());

						mbMessagePersistence.update(repliesMessage);
					}

					thread.setRootMessageId(childMessage.getMessageId());
					thread.setRootMessageUserId(childMessage.getUserId());

					thread = _mbThreadLocalService.updateMBThread(thread);
				}
			}
			else {

				// Message is a child message

				List<MBMessage> childrenMessages =
					mbMessagePersistence.findByT_P(
						message.getThreadId(), message.getMessageId());

				// Message has children messages

				if (!childrenMessages.isEmpty()) {
					for (MBMessage childMessage : childrenMessages) {
						childMessage.setParentMessageId(
							message.getParentMessageId());
						childMessage.setTreePath(childMessage.buildTreePath());

						mbMessagePersistence.update(childMessage);
					}

					Indexer<MBMessage> indexer =
						IndexerRegistryUtil.nullSafeGetIndexer(MBMessage.class);

					indexer.reindex(childrenMessages);
				}
				else if (message.getStatus() ==
							WorkflowConstants.STATUS_APPROVED) {

					MessageCreateDateComparator comparator =
						MessageCreateDateComparator.getInstance(true);

					MBMessage[] prevAndNextMessages =
						mbMessagePersistence.findByT_S_PrevAndNext(
							message.getMessageId(), thread.getThreadId(),
							WorkflowConstants.STATUS_APPROVED, comparator);

					if (prevAndNextMessages[2] == null) {
						_mbThreadLocalService.updateLastPostDate(
							thread.getThreadId(),
							prevAndNextMessages[0].getModifiedDate());
					}
				}
			}

			// Indexer

			Indexer<MBThread> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				MBThread.class);

			indexer.reindex(thread);

			Indexer<MBMessage> mbMessageIndexer =
				IndexerRegistryUtil.nullSafeGetIndexer(MBMessage.class);

			mbMessageIndexer.reindex(
				mbMessageLocalService.getMBMessage(thread.getRootMessageId()));
		}

		// Asset

		_assetEntryLocalService.deleteEntry(
			message.getWorkflowClassName(), message.getMessageId());

		// Expando

		_expandoRowLocalService.deleteRows(message.getMessageId());

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			message.getWorkflowClassName(), message.getMessageId());

		// Resources

		if (!message.isDiscussion()) {
			_resourceLocalService.deleteResource(
				message.getCompanyId(), message.getWorkflowClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL, message.getMessageId());
		}

		// Message

		mbMessagePersistence.remove(message);

		// Workflow

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			message.getCompanyId(), message.getGroupId(),
			message.getWorkflowClassName(), message.getMessageId());

		return message;
	}

	@Override
	public void deleteMessageAttachment(long messageId, String fileName)
		throws PortalException {

		MBMessage message = getMessage(messageId);

		long folderId = message.getAttachmentsFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		_portletFileRepository.deletePortletFileEntry(
			message.getGroupId(), folderId, fileName);
	}

	@Override
	public void deleteMessageAttachments(long messageId)
		throws PortalException {

		MBMessage message = getMessage(messageId);

		long folderId = message.getAttachmentsFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		_portletFileRepository.deletePortletFileEntries(
			message.getGroupId(), folderId);
	}

	@Override
	public void deleteTempAttachment(
			long groupId, long userId, String folderName, String fileName)
		throws PortalException {

		TempFileEntryUtil.deleteTempFileEntry(
			groupId, userId, folderName, fileName);
	}

	@Override
	public void emptyMessageAttachments(long messageId) throws PortalException {
		MBMessage message = getMessage(messageId);

		long folderId = message.getAttachmentsFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		_portletFileRepository.deletePortletFileEntries(
			message.getGroupId(), folderId, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public MBMessage fetchFileEntryMessage(long fileEntryId)
		throws PortalException {

		return mbMessagePersistence.fetchByPrimaryKey(
			_getFileEntryMessageId(fileEntryId));
	}

	@Override
	public MBMessage fetchFirstMessage(long threadId, long parentMessageId)
		throws PortalException {

		return mbMessagePersistence.fetchByT_P_First(
			threadId, parentMessageId, null);
	}

	@Override
	public MBMessage fetchMBMessageByUrlSubject(
		long groupId, String urlSubject) {

		return mbMessagePersistence.fetchByG_US(
			groupId,
			_friendlyURLNormalizer.normalizeWithEncodingPeriodsAndSlashes(
				urlSubject));
	}

	@Override
	public List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByG_C(
				groupId, categoryId, start, end);
		}

		return mbMessagePersistence.findByG_C_S(
			groupId, categoryId, status, start, end);
	}

	@Override
	public List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByG_C(
				groupId, categoryId, start, end, orderByComparator);
		}

		return mbMessagePersistence.findByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	@Override
	public List<MBMessage> getCategoryMessages(
		long groupId, long categoryId, long threadId) {

		return mbMessagePersistence.findByG_C_T(groupId, categoryId, threadId);
	}

	@Override
	public int getCategoryMessagesCount(
		long groupId, long categoryId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.countByG_C(groupId, categoryId);
		}

		return mbMessagePersistence.countByG_C_S(groupId, categoryId, status);
	}

	@Override
	public List<MBMessage> getChildMessages(long parentMessageId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByParentMessageId(parentMessageId);
		}

		return mbMessagePersistence.findByP_S(parentMessageId, status);
	}

	@Override
	public List<MBMessage> getChildMessages(
		long parentMessageId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByParentMessageId(
				parentMessageId, start, end);
		}

		return mbMessagePersistence.findByP_S(
			parentMessageId, status, start, end);
	}

	@Override
	public int getChildMessagesCount(long parentMessageId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.countByParentMessageId(parentMessageId);
		}

		return mbMessagePersistence.countByP_S(parentMessageId, status);
	}

	@Override
	public List<MBMessage> getCompanyMessages(
		long companyId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByCompanyId(companyId, start, end);
		}

		return mbMessagePersistence.findByC_S(companyId, status, start, end);
	}

	@Override
	public List<MBMessage> getCompanyMessages(
		long companyId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByCompanyId(
				companyId, start, end, orderByComparator);
		}

		return mbMessagePersistence.findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public int getCompanyMessagesCount(long companyId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.countByCompanyId(companyId);
		}

		return mbMessagePersistence.countByC_S(companyId, status);
	}

	@Override
	public MBMessageDisplay getDiscussionMessageDisplay(
			long userId, long groupId, String className, long classPK,
			int status)
		throws PortalException {

		return getDiscussionMessageDisplay(
			userId, groupId, className, classPK, status,
			new MessageThreadComparator());
	}

	@Override
	public MBMessageDisplay getDiscussionMessageDisplay(
			long userId, long groupId, String className, long classPK,
			int status, Comparator<MBMessage> comparator)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		MBMessage message = null;

		MBDiscussion discussion = _mbDiscussionLocalService.fetchDiscussion(
			classNameId, classPK);

		if (discussion != null) {
			MBThread mbThread = _mbThreadPersistence.findByPrimaryKey(
				discussion.getThreadId());

			message = mbMessagePersistence.findByPrimaryKey(
				mbThread.getRootMessageId());
		}
		else {
			boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

			WorkflowThreadLocal.setEnabled(false);

			try {
				String subject = String.valueOf(classPK);

				//String body = subject;

				if (CTCollectionThreadLocal.isProductionMode() ||
					_ctEntryLocalService.hasCTEntry(
						CTCollectionThreadLocal.getCTCollectionId(),
						classNameId, classPK)) {

					message = mbMessageLocalService.addDiscussionMessage(
						null, userId, null, groupId, className, classPK, 0,
						MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, subject,
						subject, new ServiceContext());
				}
				else {
					Group group = _groupLocalService.fetchGroup(groupId);

					try (SafeCloseable safeCloseable =
							CTCollectionThreadLocal.
								setCTCollectionIdWithSafeCloseable(
									group.getCtCollectionId())) {

						message = mbMessageLocalService.addDiscussionMessage(
							null, userId, null, groupId, className, classPK, 0,
							MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID,
							subject, subject, new ServiceContext());
					}
				}
			}
			catch (SystemException systemException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Add failed, fetch {threadId=0, parentMessageId=" +
							MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID + "}");
				}

				message = mbMessagePersistence.fetchByT_P_First(
					0, MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, null);

				if (message == null) {
					throw systemException;
				}
			}
			finally {
				WorkflowThreadLocal.setEnabled(workflowEnabled);
			}
		}

		return getMessageDisplay(userId, message, status, comparator);
	}

	@Override
	public int getDiscussionMessagesCount(
		long classNameId, long classPK, int status) {

		MBDiscussion discussion = _mbDiscussionLocalService.fetchDiscussion(
			classNameId, classPK);

		if (discussion == null) {
			return 0;
		}

		int count = 0;

		if (status == WorkflowConstants.STATUS_ANY) {
			count = mbMessagePersistence.countByThreadId(
				discussion.getThreadId());
		}
		else {
			count = mbMessagePersistence.countByT_S(
				discussion.getThreadId(), status);
		}

		if (count >= 1) {
			return count - 1;
		}

		return 0;
	}

	@Override
	public int getDiscussionMessagesCount(
		String className, long classPK, int status) {

		return getDiscussionMessagesCount(
			_classNameLocalService.getClassNameId(className), classPK, status);
	}

	@Override
	public List<MBDiscussion> getDiscussions(String className) {
		return _mbDiscussionLocalService.getDiscussions(className);
	}

	@Override
	public MBMessage getFileEntryMessage(long fileEntryId)
		throws PortalException {

		return mbMessagePersistence.findByPrimaryKey(
			_getFileEntryMessageId(fileEntryId));
	}

	@Override
	public MBMessage getFirstMessage(long threadId, long parentMessageId)
		throws PortalException {

		return mbMessagePersistence.findByT_P_First(
			threadId, parentMessageId, null);
	}

	@Override
	public List<MBMessage> getGroupMessages(
		long groupId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByGroupId(groupId, start, end);
		}

		return mbMessagePersistence.findByG_S(groupId, status, start, end);
	}

	@Override
	public List<MBMessage> getGroupMessages(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByGroupId(
				groupId, start, end, orderByComparator);
		}

		return mbMessagePersistence.findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public List<MBMessage> getGroupMessages(
		long groupId, long userId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByG_U(groupId, userId, start, end);
		}

		return mbMessagePersistence.findByG_U_S(
			groupId, userId, status, start, end);
	}

	@Override
	public List<MBMessage> getGroupMessages(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByG_U(
				groupId, userId, start, end, orderByComparator);
		}

		return mbMessagePersistence.findByG_U_S(
			groupId, userId, status, start, end, orderByComparator);
	}

	@Override
	public int getGroupMessagesCount(long groupId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.countByGroupId(groupId);
		}

		return mbMessagePersistence.countByG_S(groupId, status);
	}

	@Override
	public int getGroupMessagesCount(long groupId, long userId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.countByG_U(groupId, userId);
		}

		return mbMessagePersistence.countByG_U_S(groupId, userId, status);
	}

	@Override
	public List<MBMessage> getGroupUserMessageBoardMessagesActivity(
		long groupId, long userId, int start, int end) {

		MBMessageTable aliasMBMessageTable = MBMessageTable.INSTANCE.as(
			"aliasMBMessageTable");

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			MBMessageTable.INSTANCE
		).from(
			MBMessageTable.INSTANCE
		).where(
			MBMessageTable.INSTANCE.modifiedDate.in(
				DSLQueryFactoryUtil.selectDistinct(
					MBMessageTable.INSTANCE.modifiedDate
				).from(
					aliasMBMessageTable
				).where(
					MBMessageTable.INSTANCE.rootMessageId.eq(
						MBMessageTable.INSTANCE.parentMessageId
					).and(
						MBMessageTable.INSTANCE.categoryId.eq(
							aliasMBMessageTable.categoryId)
					).and(
						MBMessageTable.INSTANCE.groupId.eq(groupId)
					).and(
						MBMessageTable.INSTANCE.userId.eq(userId)
					)
				)
			).or(
				MBMessageTable.INSTANCE.parentMessageId.eq(
					0L
				).and(
					MBMessageTable.INSTANCE.rootMessageId.notIn(
						DSLQueryFactoryUtil.select(
							MBMessageTable.INSTANCE.parentMessageId
						).from(
							MBMessageTable.INSTANCE
						).where(
							MBMessageTable.INSTANCE.rootMessageId.eq(
								MBMessageTable.INSTANCE.parentMessageId)
						))
				).and(
					MBMessageTable.INSTANCE.groupId.eq(groupId)
				).and(
					MBMessageTable.INSTANCE.userId.eq(userId)
				)
			)
		).orderBy(
			MBMessageTable.INSTANCE.modifiedDate.descending()
		).limit(
			start, end
		);

		return mbMessagePersistence.dslQuery(dslQuery);
	}

	@Override
	public int getGroupUserMessageBoardMessagesActivityCount(
		long groupId, long userId) {

		MBMessageTable aliasMBMessageTable = MBMessageTable.INSTANCE.as(
			"aliasMBMessageTable");

		return mbMessagePersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				MBMessageTable.INSTANCE
			).where(
				MBMessageTable.INSTANCE.modifiedDate.in(
					DSLQueryFactoryUtil.selectDistinct(
						MBMessageTable.INSTANCE.modifiedDate
					).from(
						aliasMBMessageTable
					).where(
						MBMessageTable.INSTANCE.rootMessageId.eq(
							MBMessageTable.INSTANCE.parentMessageId
						).and(
							MBMessageTable.INSTANCE.categoryId.eq(
								aliasMBMessageTable.categoryId)
						).and(
							MBMessageTable.INSTANCE.groupId.eq(groupId)
						).and(
							MBMessageTable.INSTANCE.userId.eq(userId)
						)
					)
				).or(
					MBMessageTable.INSTANCE.parentMessageId.eq(
						0L
					).and(
						MBMessageTable.INSTANCE.groupId.eq(groupId)
					).and(
						MBMessageTable.INSTANCE.rootMessageId.notIn(
							DSLQueryFactoryUtil.select(
								MBMessageTable.INSTANCE.parentMessageId
							).from(
								MBMessageTable.INSTANCE
							).where(
								MBMessageTable.INSTANCE.rootMessageId.eq(
									MBMessageTable.INSTANCE.parentMessageId)
							))
					).and(
						MBMessageTable.INSTANCE.userId.eq(userId)
					)
				)
			));
	}

	@Override
	public MBMessage getLastThreadMessage(long threadId, int status)
		throws PortalException {

		return mbMessagePersistence.findByT_S_Last(threadId, status, null);
	}

	@Override
	public MBMessage getMessage(long messageId) throws PortalException {
		return mbMessagePersistence.findByPrimaryKey(messageId);
	}

	@Override
	public MBMessageDisplay getMessageDisplay(
			long userId, long messageId, int status)
		throws PortalException {

		return getMessageDisplay(userId, getMessage(messageId), status);
	}

	@Override
	public MBMessageDisplay getMessageDisplay(
			long userId, MBMessage message, int status)
		throws PortalException {

		return getMessageDisplay(
			userId, message, status, new MessageThreadComparator());
	}

	@Override
	public MBMessageDisplay getMessageDisplay(
			long userId, MBMessage message, int status,
			Comparator<MBMessage> comparator)
		throws PortalException {

		MBCategory category = null;

		if ((message.getCategoryId() !=
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) &&
			(message.getCategoryId() !=
				MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			category = _mbCategoryPersistence.findByPrimaryKey(
				message.getCategoryId());
		}
		else {
			category = new MBCategoryImpl();

			category.setCategoryId(message.getCategoryId());
			category.setDisplayStyle(MBCategoryConstants.DEFAULT_DISPLAY_STYLE);
		}

		MBMessage parentMessage = null;

		if (message.isReply()) {
			parentMessage = mbMessagePersistence.findByPrimaryKey(
				message.getParentMessageId());
		}

		MBThread thread = _mbThreadPersistence.findByPrimaryKey(
			message.getThreadId());

		if (message.isApproved() && !message.isDiscussion()) {
			_mbThreadLocalService.incrementViewCounter(thread.getThreadId(), 1);

			SocialActivityManagerUtil.addActivity(
				userId, thread, SocialActivityConstants.TYPE_VIEW,
				StringPool.BLANK, 0);
		}

		List<MBMessage> messages = null;

		if (userId > 0) {
			messages = getThreadMessages(
				userId, message.getThreadId(), status, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, comparator);
		}
		else {
			messages = getThreadMessages(
				message.getThreadId(), status, comparator);
		}

		int discussionMessagesCount = 0;

		if (message.isDiscussion() &&
			(PropsValues.DISCUSSION_MAX_COMMENTS > 0)) {

			discussionMessagesCount = getDiscussionMessagesCount(
				message.getClassName(), message.getClassPK(),
				WorkflowConstants.STATUS_APPROVED);
		}

		return new MBMessageDisplayImpl(
			message, parentMessage, messages, category, thread,
			discussionMessagesCount);
	}

	@Override
	public List<MBMessage> getMessages(
		String className, long classPK, int status) {

		long classNameId = _classNameLocalService.getClassNameId(className);

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByC_C(classNameId, classPK);
		}

		return mbMessagePersistence.findByC_C_S(classNameId, classPK, status);
	}

	@Override
	public int getPositionInThread(long messageId) throws PortalException {
		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		return mbMessageFinder.countByC_T(
			message.getCreateDate(), message.getThreadId());
	}

	@Override
	public List<MBMessage> getRootDiscussionMessages(
			String className, long classPK, int status)
		throws PortalException {

		return getRootDiscussionMessages(
			className, classPK, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<MBMessage> getRootDiscussionMessages(
			String className, long classPK, int status, int start, int end)
		throws PortalException {

		return getChildMessages(
			_getRootDiscussionMessageId(className, classPK), status, start,
			end);
	}

	@Override
	public int getRootDiscussionMessagesCount(
		String className, long classPK, int status) {

		int count = 0;

		try {
			count = getChildMessagesCount(
				_getRootDiscussionMessageId(className, classPK), status);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to obtain root discussion message id for ",
						"class name ", className, " and class PK ", classPK),
					portalException);
			}
		}

		return count;
	}

	@Override
	public String[] getTempAttachmentNames(
			long groupId, long userId, String folderName)
		throws PortalException {

		return TempFileEntryUtil.getTempFileNames(groupId, userId, folderName);
	}

	@Override
	public List<MBMessage> getThreadMessages(long threadId, int status) {
		return getThreadMessages(
			threadId, status, new MessageThreadComparator());
	}

	@Override
	public List<MBMessage> getThreadMessages(
		long threadId, int status, Comparator<MBMessage> comparator) {

		List<MBMessage> messages = null;

		if (status == WorkflowConstants.STATUS_ANY) {
			messages = mbMessagePersistence.findByThreadId(threadId);
		}
		else {
			messages = mbMessagePersistence.findByT_S(threadId, status);
		}

		return ListUtil.sort(messages, comparator);
	}

	@Override
	public List<MBMessage> getThreadMessages(
		long threadId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByThreadId(threadId, start, end);
		}

		return mbMessagePersistence.findByT_S(threadId, status, start, end);
	}

	@Override
	public List<MBMessage> getThreadMessages(
		long threadId, long parentMessageId) {

		return mbMessagePersistence.findByT_P(threadId, parentMessageId);
	}

	@Override
	public List<MBMessage> getThreadMessages(
		long userId, long threadId, int status, int start, int end,
		Comparator<MBMessage> comparator) {

		return MBMessageUtil.getThreadMessages(
			mbMessagePersistence, mbMessageFinder, userId, threadId, status,
			start, end, comparator);
	}

	@Override
	public int getThreadMessagesCount(long threadId, boolean answer) {
		return mbMessagePersistence.countByT_A(threadId, answer);
	}

	@Override
	public int getThreadMessagesCount(long threadId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.countByThreadId(threadId);
		}

		return mbMessagePersistence.countByT_S(threadId, status);
	}

	@Override
	public List<MBMessage> getThreadRepliesMessages(
		long threadId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByThreadIdReplies(
				threadId, start, end);
		}

		return mbMessagePersistence.findByTR_S(threadId, status, start, end);
	}

	@Override
	public List<MBMessage> getUserDiscussionMessages(
		long userId, long classNameId, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByU_C_C(
				userId, classNameId, classPK, start, end, orderByComparator);
		}

		return mbMessagePersistence.findByU_C_C_S(
			userId, classNameId, classPK, status, start, end,
			orderByComparator);
	}

	@Override
	public List<MBMessage> getUserDiscussionMessages(
		long userId, long[] classNameIds, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.findByU_C(
				userId, classNameIds, start, end, orderByComparator);
		}

		return mbMessagePersistence.findByU_C_S(
			userId, classNameIds, status, start, end, orderByComparator);
	}

	@Override
	public List<MBMessage> getUserDiscussionMessages(
		long userId, String className, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return getUserDiscussionMessages(
			userId, _classNameLocalService.getClassNameId(className), classPK,
			status, start, end, orderByComparator);
	}

	@Override
	public int getUserDiscussionMessagesCount(
		long userId, long classNameId, long classPK, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.countByU_C_C(
				userId, classNameId, classPK);
		}

		return mbMessagePersistence.countByU_C_C_S(
			userId, classNameId, classPK, status);
	}

	@Override
	public int getUserDiscussionMessagesCount(
		long userId, long[] classNameIds, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.countByU_C(userId, classNameIds);
		}

		return mbMessagePersistence.countByU_C_S(userId, classNameIds, status);
	}

	@Override
	public int getUserDiscussionMessagesCount(
		long userId, String className, long classPK, int status) {

		return getUserDiscussionMessagesCount(
			userId, _classNameLocalService.getClassNameId(className), classPK,
			status);
	}

	@Override
	public long moveMessageAttachmentToTrash(
			long userId, long messageId, String fileName)
		throws PortalException {

		MBMessage message = getMessage(messageId);

		long folderId = message.getAttachmentsFolderId();

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			message.getGroupId(), folderId, fileName);

		_portletFileRepository.movePortletFileEntryToTrash(
			userId, fileEntry.getFileEntryId());

		return fileEntry.getFileEntryId();
	}

	@Override
	public void restoreMessageAttachmentFromTrash(
			long userId, long messageId, String deletedFileName)
		throws PortalException {

		MBMessage message = getMessage(messageId);

		Folder folder = message.addAttachmentsFolder();

		_portletFileRepository.restorePortletFileEntryFromTrash(
			message.getGroupId(), userId, folder.getFolderId(),
			deletedFileName);
	}

	@Override
	public void subscribeMessage(long userId, long messageId)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		_subscriptionLocalService.addSubscription(
			userId, message.getGroupId(), MBThread.class.getName(),
			message.getThreadId());
	}

	@Override
	public void unsubscribeMessage(long userId, long messageId)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		_subscriptionLocalService.deleteSubscription(
			userId, MBThread.class.getName(), message.getThreadId());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public MBMessage updateAnswer(
			long messageId, boolean answer, boolean cascade)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		return updateAnswer(message, answer, cascade);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public MBMessage updateAnswer(
			MBMessage message, boolean answer, boolean cascade)
		throws PortalException {

		return MBMessageUtil.updateAnswer(
			mbMessagePersistence, message, answer, cascade);
	}

	@Override
	public void updateAsset(
			long userId, MBMessage message, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException {

		_updateAsset(
			userId, message, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			true);
	}

	@Override
	public MBMessage updateDiscussionMessage(
			long userId, long messageId, String className, long classPK,
			String subject, String body, ServiceContext serviceContext)
		throws PortalException {

		subject = _getDiscussionMessageSubject(subject, body);
		List<ObjectValuePair<String, InputStream>> inputStreamOVPs = null;
		double priority = 0.0;
		boolean allowPingbacks = false;

		serviceContext.setAttribute("className", className);
		serviceContext.setAttribute("classPK", String.valueOf(classPK));

		return mbMessageLocalService.updateMessage(
			userId, messageId, subject, body, inputStreamOVPs, priority,
			allowPingbacks, serviceContext);
	}

	@Override
	public MBMessage updateMessage(
			long userId, long messageId, String body,
			ServiceContext serviceContext)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		return mbMessageLocalService.updateMessage(
			userId, messageId, message.getSubject(), body, null,
			message.getPriority(), message.isAllowPingbacks(), serviceContext);
	}

	@Override
	public MBMessage updateMessage(
			long userId, long messageId, String subject, String body,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		// Message

		MBMessage message = _updateMessage(
			userId, messageId, subject, body, priority, allowPingbacks,
			serviceContext);

		// Attachments

		if (ListUtil.isNotEmpty(inputStreamOVPs)) {
			Folder folder = message.addAttachmentsFolder();

			_portletFileRepository.addPortletFileEntries(
				message.getGroupId(), userId, MBMessage.class.getName(),
				message.getMessageId(), MBConstants.SERVICE_NAME,
				folder.getFolderId(), inputStreamOVPs);
		}

		return message;
	}

	@Override
	public MBMessage updateStatus(
			long userId, long messageId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		// Message

		MBMessage message = getMessage(messageId);

		int oldStatus = message.getStatus();

		User user = _userLocalService.getUser(userId);

		Date modifiedDate = serviceContext.getModifiedDate(new Date());

		message.setStatus(status);
		message.setStatusByUserId(userId);
		message.setStatusByUserName(user.getFullName());
		message.setStatusDate(modifiedDate);

		message = mbMessagePersistence.update(message);

		// Thread

		MBThread thread = _mbThreadPersistence.findByPrimaryKey(
			message.getThreadId());

		_updateThreadStatus(thread, message, user, oldStatus, modifiedDate);

		Indexer<MBMessage> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			MBMessage.class);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			long notifySubscribersUserId = userId;

			if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
				notifySubscribersUserId = message.getUserId();

				// Asset

				if (serviceContext.isAssetEntryVisible() &&
					((message.getClassNameId() == 0) ||
					 (message.getParentMessageId() != 0))) {

					Date publishDate = null;

					AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
						message.getWorkflowClassName(), message.getMessageId());

					if ((assetEntry != null) &&
						(assetEntry.getPublishDate() != null)) {

						publishDate = assetEntry.getPublishDate();
					}
					else {
						publishDate = modifiedDate;

						serviceContext.setCommand(Constants.ADD);
					}

					_assetEntryLocalService.updateEntry(
						message.getWorkflowClassName(), message.getMessageId(),
						publishDate, null, true, true);
				}
			}

			// Social

			_updateSocialActivity(user, message, serviceContext);

			// Subscriptions

			_notifySubscribers(
				notifySubscribersUserId, (MBMessage)message.clone(),
				(String)workflowContext.get(WorkflowConstants.CONTEXT_URL),
				serviceContext);

			// Indexer

			indexer.reindex(message);

			// Ping

			_pingPingback(message, serviceContext);
		}
		else if (oldStatus == WorkflowConstants.STATUS_APPROVED) {

			// Asset

			_assetEntryLocalService.updateVisible(
				message.getWorkflowClassName(), message.getMessageId(), false);

			// Indexer

			indexer.delete(message);
		}

		return message;
	}

	@Override
	public void updateUserName(long userId, String userName) {
		List<MBMessage> messages = mbMessagePersistence.findByUserId(userId);

		for (MBMessage message : messages) {
			message.setUserName(userName);

			mbMessagePersistence.update(message);
		}
	}

	@Activate
	protected void activate() {
		_closeable = _liferayJSONDeserializationWhitelist.register(
			MBDiscussionSubscriptionSender.class.getName(),
			MBSubscriptionSender.class.getName());
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		try {
			_closeable.close();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getBody(String subject, String body, String format) {
		if (!Validator.isBlank(body)) {
			return body;
		}

		if (StringUtil.equals(format, "html")) {
			return HtmlUtil.escape(subject);
		}

		return subject;
	}

	private CommentGroupServiceConfiguration
			_getCommentGroupServiceConfiguration(long groupId)
		throws ConfigurationException {

		return _configurationProvider.getConfiguration(
			CommentGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId, CommentConstants.SERVICE_NAME,
				CommentGroupServiceConfiguration.class.getName()));
	}

	private String _getDiscussionMessageSubject(String subject, String body)
		throws MessageBodyException {

		if (Validator.isNotNull(subject)) {
			return subject;
		}

		if (Validator.isNull(body)) {
			throw new MessageBodyException("Body is null");
		}

		subject = _htmlParser.extractText(body);

		if (subject.length() <= MBMessageConstants.MESSAGE_SUBJECT_MAX_LENGTH) {
			return subject;
		}

		String subjectSubstring = subject.substring(
			0, MBMessageConstants.MESSAGE_SUBJECT_MAX_LENGTH);

		return subjectSubstring + StringPool.TRIPLE_PERIOD;
	}

	private long _getFileEntryMessageId(long fileEntryId)
		throws PortalException {

		// See LPS-110554

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry != null) {
			DLFolder dlFolder = dlFileEntry.getFolder();

			return GetterUtil.getLong(dlFolder.getName());
		}

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			fileEntryId);

		Folder folder = _portletFileRepository.getPortletFolder(
			fileEntry.getFolderId());

		return GetterUtil.getLong(folder.getName());
	}

	private String _getGroupDescriptiveName(Group group, Locale locale) {
		try {
			return group.getDescriptiveName(locale);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get descriptive name for group " +
					group.getGroupId(),
				portalException);
		}

		return StringPool.BLANK;
	}

	private String _getLayoutFullURL(
			MBMessage message, String portletId, ServiceContext serviceContext)
		throws PortalException {

		List<Layout> layouts = _layoutLocalService.getPublishedLayouts(
			message.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (Layout curLayout : layouts) {
			PortletPreferences portletPreferences =
				_portletPreferencesLocalService.fetchPortletPreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, curLayout.getPlid(),
					portletId);

			if (portletPreferences != null) {
				return _portal.getLayoutFullURL(
					curLayout, serviceContext.getThemeDisplay(), false);
			}
		}

		String layoutFullURL = StringPool.BLANK;

		Layout layout = _layoutLocalService.fetchLayout(
			_portal.getPlidFromPortletId(
				message.getGroupId(), false, portletId));
		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if ((layout != null) && (themeDisplay != null)) {
			layoutFullURL = _portal.getLayoutFullURL(layout, themeDisplay);
		}
		else {
			layoutFullURL = _portal.getLayoutFullURL(
				message.getGroupId(), portletId);
		}

		if (Validator.isNotNull(layoutFullURL)) {
			return layoutFullURL;
		}

		return null;
	}

	private String _getLocalizedRootCategoryName(Group group, Locale locale) {
		try {
			return _language.get(locale, "home") + " - " +
				group.getDescriptiveName(locale);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get descriptive name for group " +
					group.getGroupId(),
				portalException);

			return _language.get(locale, "home");
		}
	}

	private String _getMessageURL(
			MBMessage message, ServiceContext serviceContext)
		throws PortalException {

		String entryURL = GetterUtil.getString(
			serviceContext.getAttribute("entryURL"));

		if (Validator.isNotNull(entryURL)) {
			return entryURL;
		}

		String link = GetterUtil.getString(serviceContext.getAttribute("link"));

		if (Validator.isNotNull(link)) {
			return link + message.getUrlSubject();
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if ((httpServletRequest == null) || message.isDiscussion()) {
			if (Validator.isNull(serviceContext.getLayoutFullURL())) {
				return StringPool.BLANK;
			}

			return StringBundler.concat(
				serviceContext.getLayoutFullURL(),
				Portal.FRIENDLY_URL_SEPARATOR, "message_boards/view_message/",
				message.getMessageId());
		}

		String layoutURL = _getLayoutFullURL(
			message,
			PortletProviderUtil.getPortletId(
				MBMessage.class.getName(), PortletProvider.Action.VIEW),
			serviceContext);

		if (Validator.isNotNull(layoutURL)) {
			return StringBundler.concat(
				layoutURL, Portal.FRIENDLY_URL_SEPARATOR,
				"message_boards/view_message/", message.getMessageId());
		}

		Group group = _groupLocalService.fetchGroup(message.getGroupId());

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group,
				PortletProviderUtil.getPortletId(
					MBMessage.class.getName(), PortletProvider.Action.MANAGE),
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/message_boards/view_message"
		).setParameter(
			"messageId", message.getMessageId()
		).buildString();
	}

	private long _getRootDiscussionMessageId(String className, long classPK)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByC_C_First(
			_classNameLocalService.getClassNameId(className), classPK,
			MessageCreateDateComparator.getInstance(true));

		return message.getMessageId();
	}

	private String _getSubject(String subject, String body) {
		if (Validator.isNull(subject)) {
			return StringUtil.shorten(body);
		}

		return subject;
	}

	private MBSubscriptionSender _getSubscriptionSender(
			long userId, MBCategory category, MBMessage message,
			String messageURL, String entryTitle, boolean htmlFormat,
			String messageBody, String messageSubject,
			String messageSubjectPrefix, String inReplyTo, String fromName,
			String fromAddress, String replyToAddress, String emailAddress,
			String fullName, String messageParentMessageContent,
			String messageSiblingMessagesContent, String rootMessageBody,
			LocalizedValuesMap subjectLocalizedValuesMap,
			LocalizedValuesMap bodyLocalizedValuesMap,
			ServiceContext serviceContext)
		throws PortalException {

		MBSubscriptionSender subscriptionSender = new MBSubscriptionSender(
			MBConstants.RESOURCE_NAME);

		subscriptionSender.setAnonymous(message.isAnonymous());
		subscriptionSender.setBulk(PropsValues.MESSAGE_BOARDS_EMAIL_BULK);
		subscriptionSender.setClassName(message.getModelClassName());
		subscriptionSender.setClassPK(message.getMessageId());
		subscriptionSender.setContextAttribute(
			"[$MESSAGE_BODY$]", messageBody, false);
		subscriptionSender.setContextAttribute(
			"[$MESSAGE_PARENT$]", messageParentMessageContent, false);
		subscriptionSender.setContextAttribute(
			"[$MESSAGE_SIBLINGS$]", messageSiblingMessagesContent, false);
		subscriptionSender.setContextAttribute(
			"[$ROOT_MESSAGE_BODY$]", rootMessageBody, false);

		long groupId = message.getGroupId();

		Group group = _groupLocalService.getGroup(groupId);

		if (category.getCategoryId() !=
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			subscriptionSender.setContextAttribute(
				"[$CATEGORY_NAME$]", category.getName(), true);
		}
		else {
			subscriptionSender.setLocalizedContextAttribute(
				"[$CATEGORY_NAME$]",
				new EscapableLocalizableFunction(
					locale -> _getLocalizedRootCategoryName(group, locale)));
		}

		subscriptionSender.setContextAttributes(
			"[$MAILING_LIST_ADDRESS$]", replyToAddress, "[$MESSAGE_ID$]",
			message.getMessageId(), "[$MESSAGE_SUBJECT$]", messageSubject,
			"[$MESSAGE_SUBJECT_PREFIX$]", messageSubjectPrefix,
			"[$MESSAGE_URL$]", messageURL, "[$MESSAGE_USER_ADDRESS$]",
			emailAddress, "[$MESSAGE_USER_NAME$]", fullName);
		subscriptionSender.setCreatorUserId(message.getUserId());
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(entryTitle);
		subscriptionSender.setEntryURL(messageURL);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setFullName(fullName);
		subscriptionSender.setHtmlFormat(htmlFormat);
		subscriptionSender.setInReplyTo(inReplyTo);
		subscriptionSender.setLocalizedContextAttribute(
			"[$SITE_NAME$]",
			new EscapableLocalizableFunction(
				locale -> _getGroupDescriptiveName(group, locale)));

		if (bodyLocalizedValuesMap != null) {
			subscriptionSender.setLocalizedBodyMap(
				_localization.getMap(bodyLocalizedValuesMap));
		}

		if (subjectLocalizedValuesMap != null) {
			subscriptionSender.setLocalizedSubjectMap(
				_localization.getMap(subjectLocalizedValuesMap));
		}

		Date modifiedDate = message.getModifiedDate();

		subscriptionSender.setMailId(
			MBMailUtil.MESSAGE_POP_PORTLET_PREFIX, message.getCategoryId(),
			message.getMessageId(), modifiedDate.getTime());

		int notificationType =
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY;

		if (serviceContext.isCommandUpdate()) {
			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY;
		}

		subscriptionSender.setNotificationType(notificationType);
		subscriptionSender.setPortletId(
			PortletProviderUtil.getPortletId(
				MBMessage.class.getName(), PortletProvider.Action.VIEW));
		subscriptionSender.setReplyToAddress(replyToAddress);
		subscriptionSender.setScopeGroupId(groupId);
		subscriptionSender.setServiceContext(serviceContext);
		subscriptionSender.setUniqueMailId(false);

		return subscriptionSender;
	}

	private String _getUniqueUrlSubject(
		long groupId, long mbMessageId, String subject) {

		String urlSubject = _getUrlSubject(mbMessageId, subject);

		String uniqueUrlSubject = urlSubject;

		if (Objects.equals(StringPool.DASH, urlSubject)) {
			uniqueUrlSubject = urlSubject + mbMessageId;
		}

		MBMessage mbMessage = mbMessagePersistence.fetchByG_US(
			groupId, uniqueUrlSubject);

		if (mbMessage == null) {
			return uniqueUrlSubject;
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			MBMessage.class.getName(), "urlSubject");

		for (int i = 1; mbMessage != null; i++) {
			String suffix = StringPool.DASH + i;

			if (urlSubject.length() > (maxLength - suffix.length())) {
				urlSubject = urlSubject.substring(
					0, maxLength - suffix.length());
			}

			if (urlSubject.endsWith(StringPool.DASH)) {
				uniqueUrlSubject = urlSubject + i;
			}
			else {
				uniqueUrlSubject = urlSubject + suffix;
			}

			mbMessage = mbMessagePersistence.fetchByG_US(
				groupId, uniqueUrlSubject);
		}

		return uniqueUrlSubject;
	}

	private String _getUrlSubject(long id, String subject) {
		if (subject == null) {
			return String.valueOf(id);
		}

		subject = StringUtil.toLowerCase(subject.trim());

		if (Validator.isNull(subject) || Validator.isNumber(subject) ||
			subject.equals("rss")) {

			subject = String.valueOf(id);
		}
		else {
			subject =
				_friendlyURLNormalizer.normalizeWithEncodingPeriodsAndSlashes(
					subject);
		}

		return ModelHintsUtil.trimString(
			MBMessage.class.getName(), "urlSubject", subject);
	}

	private void _notifyDiscussionSubscribers(
			long userId, MBMessage message, ServiceContext serviceContext)
		throws PortalException {

		CommentGroupServiceConfiguration commentGroupServiceConfiguration =
			_getCommentGroupServiceConfiguration(message.getGroupId());

		MBDiscussion mbDiscussion =
			_mbDiscussionLocalService.getThreadDiscussion(
				message.getThreadId());

		String contentURL = (String)serviceContext.getAttribute("contentURL");

		contentURL = HttpComponentsUtil.addParameter(
			contentURL, serviceContext.getAttribute("namespace") + "messageId",
			message.getMessageId());

		String userAddress = StringPool.BLANK;
		String userName = (String)serviceContext.getAttribute(
			"pingbackUserName");

		if (Validator.isNull(userName)) {
			userAddress = _portal.getUserEmailAddress(message.getUserId());
			userName = _portal.getUserName(
				message.getUserId(), StringPool.BLANK);
		}

		SubscriptionSender subscriptionSender =
			new MBDiscussionSubscriptionSender(
				commentGroupServiceConfiguration);

		subscriptionSender.setClassName(MBDiscussion.class.getName());
		subscriptionSender.setClassPK(mbDiscussion.getDiscussionId());
		subscriptionSender.setContextAttribute(
			"[$COMMENTS_BODY$]", message.getBody(message.isFormatBBCode()),
			false);
		subscriptionSender.setContextAttributes(
			"[$COMMENTS_USER_ADDRESS$]", userAddress, "[$COMMENTS_USER_NAME$]",
			userName, "[$CONTENT_URL$]", contentURL);
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(message.getBody());
		subscriptionSender.setEntryURL(contentURL);
		subscriptionSender.setFrom(
			commentGroupServiceConfiguration.emailFromAddress(),
			commentGroupServiceConfiguration.emailFromName());
		subscriptionSender.setHtmlFormat(true);

		Map<Locale, String> localizedBodyMap = null;
		Map<Locale, String> localizedSubjectMap = null;

		if (serviceContext.isCommandUpdate()) {
			localizedBodyMap = _localization.getMap(
				commentGroupServiceConfiguration.discussionEmailUpdatedBody());
			localizedSubjectMap = _localization.getMap(
				commentGroupServiceConfiguration.
					discussionEmailUpdatedSubject());
		}
		else {
			localizedBodyMap = _localization.getMap(
				commentGroupServiceConfiguration.discussionEmailBody());
			localizedSubjectMap = _localization.getMap(
				commentGroupServiceConfiguration.discussionEmailSubject());
		}

		if (localizedBodyMap != null) {
			subscriptionSender.setLocalizedBodyMap(localizedBodyMap);
		}

		if (localizedSubjectMap != null) {
			subscriptionSender.setLocalizedSubjectMap(localizedSubjectMap);
		}

		Date modifiedDate = message.getModifiedDate();

		subscriptionSender.setMailId(
			"mb_discussion", message.getCategoryId(), message.getMessageId(),
			modifiedDate.getTime());

		int notificationType =
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY;

		if (serviceContext.isCommandUpdate()) {
			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY;
		}

		subscriptionSender.setNotificationType(notificationType);
		subscriptionSender.setPortletId(
			PortletProviderUtil.getPortletId(
				Comment.class.getName(), PortletProvider.Action.VIEW));
		subscriptionSender.setScopeGroupId(message.getGroupId());
		subscriptionSender.setServiceContext(serviceContext);
		subscriptionSender.setUniqueMailId(false);

		String className = (String)serviceContext.getAttribute("className");
		long classPK = ParamUtil.getLong(serviceContext, "classPK");

		subscriptionSender.addPersistedSubscribers(
			com.liferay.message.boards.util.MBUtil.getSubscriptionClassName(
				className),
			classPK);

		subscriptionSender.flushNotificationsAsync();
	}

	private void _notifySubscribers(
			long userId, MBMessage message, String messageURL,
			ServiceContext serviceContext)
		throws PortalException {

		if (!message.isApproved() || Validator.isNull(messageURL)) {
			return;
		}

		if (message.isDiscussion()) {
			if (CopyLayoutThreadLocal.isCopyLayout()) {
				return;
			}

			try {
				_notifyDiscussionSubscribers(userId, message, serviceContext);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return;
		}

		MBGroupServiceSettings mbGroupServiceSettings =
			MBGroupServiceSettings.getInstance(message.getGroupId());

		if (serviceContext.isCommandAdd() &&
			mbGroupServiceSettings.isEmailMessageAddedEnabled()) {
		}
		else if (serviceContext.isCommandUpdate() &&
				 mbGroupServiceSettings.isEmailMessageUpdatedEnabled()) {
		}
		else {
			return;
		}

		Company company = _companyLocalService.getCompany(
			message.getCompanyId());

		User user = _userLocalService.getUser(userId);

		String emailAddress = user.getEmailAddress();
		String fullName = user.getFullName();

		if (message.isAnonymous()) {
			emailAddress = StringPool.BLANK;
			fullName = serviceContext.translate("anonymous");
		}

		MBCategory category = message.getCategory();

		List<Long> categoryIds = new ArrayList<>();

		categoryIds.add(message.getCategoryId());

		if (message.getCategoryId() !=
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			categoryIds.addAll(category.getAncestorCategoryIds());
		}

		String entryTitle = message.getSubject();

		String fromName = mbGroupServiceSettings.getEmailFromName();
		String fromAddress = mbGroupServiceSettings.getEmailFromAddress();

		String replyToAddress = StringPool.BLANK;

		if (PrefsPropsUtil.getBoolean(
				company.getCompanyId(),
				PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED,
				PropsValues.POP_SERVER_NOTIFICATIONS_ENABLED)) {

			replyToAddress = MBMailUtil.getReplyToAddress(
				message.getCategoryId(), message.getMessageId(),
				company.getMx(), fromAddress);
		}

		LocalizedValuesMap subjectLocalizedValuesMap = null;
		LocalizedValuesMap bodyLocalizedValuesMap = null;

		if (serviceContext.isCommandUpdate()) {
			subjectLocalizedValuesMap =
				mbGroupServiceSettings.getEmailMessageUpdatedSubject();
			bodyLocalizedValuesMap =
				mbGroupServiceSettings.getEmailMessageUpdatedBody();
		}
		else {
			subjectLocalizedValuesMap =
				mbGroupServiceSettings.getEmailMessageAddedSubject();
			bodyLocalizedValuesMap =
				mbGroupServiceSettings.getEmailMessageAddedBody();
		}

		boolean htmlFormat = mbGroupServiceSettings.isEmailHtmlFormat();
		int maxNumberOfMessages = 3;
		int maxNumberOfParentMessages = 1;

		MBMessageNotificationTemplateHelper
			mbMessageNotificationTemplateHelper =
				new MBMessageNotificationTemplateHelper(
					htmlFormat, maxNumberOfMessages, maxNumberOfParentMessages,
					mbMessageLocalService, serviceContext);

		String messageBody = mbMessageNotificationTemplateHelper.getMessageBody(
			message, StringPool.BLANK);

		String inReplyTo = null;
		String messageSubject = message.getSubject();
		String messageSubjectPrefix = StringPool.BLANK;
		String messageParentMessageContent = StringPool.BLANK;
		String messageSiblingMessagesContent = StringPool.BLANK;

		if (message.getParentMessageId() !=
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID) {

			MBMessage parentMessage = mbMessageLocalService.getMessage(
				message.getParentMessageId());

			Date modifiedDate = parentMessage.getModifiedDate();

			inReplyTo = _portal.getMailId(
				company.getMx(), MBMailUtil.MESSAGE_POP_PORTLET_PREFIX,
				message.getCategoryId(), parentMessage.getMessageId(),
				modifiedDate.getTime());

			if (messageSubject.startsWith(
					MBMessageConstants.MESSAGE_SUBJECT_PREFIX_RE)) {

				messageSubjectPrefix =
					MBMessageConstants.MESSAGE_SUBJECT_PREFIX_RE;

				messageSubject = messageSubject.substring(
					messageSubjectPrefix.length());
			}

			messageParentMessageContent =
				mbMessageNotificationTemplateHelper.
					renderMessageParentMessageContent(parentMessage);
			messageSiblingMessagesContent =
				mbMessageNotificationTemplateHelper.
					renderMessageSiblingMessagesContent(message);
		}

		String rootMessageBody =
			mbMessageNotificationTemplateHelper.renderRootMessage(message);

		SubscriptionSender subscriptionSender = _getSubscriptionSender(
			userId, category, message, messageURL, entryTitle, htmlFormat,
			messageBody, messageSubject, messageSubjectPrefix, inReplyTo,
			fromName, fromAddress, replyToAddress, emailAddress, fullName,
			messageParentMessageContent, messageSiblingMessagesContent,
			rootMessageBody, subjectLocalizedValuesMap, bodyLocalizedValuesMap,
			serviceContext);

		subscriptionSender.addAssetEntryPersistedSubscribers(
			MBMessage.class.getName(), message.getMessageId());
		subscriptionSender.addPersistedSubscribers(
			MBCategory.class.getName(), message.getGroupId());

		for (long categoryId : categoryIds) {
			if (categoryId != MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
				subscriptionSender.addPersistedSubscribers(
					MBCategory.class.getName(), categoryId);
			}
		}

		subscriptionSender.addPersistedSubscribers(
			MBThread.class.getName(), message.getThreadId());

		subscriptionSender.flushNotificationsAsync();

		if (!MailingListThreadLocal.isSourceMailingList()) {
			for (long categoryId : categoryIds) {
				MBSubscriptionSender sourceMailingListSubscriptionSender =
					_getSubscriptionSender(
						userId, category, message, messageURL, entryTitle,
						htmlFormat, messageBody, messageSubject,
						messageSubjectPrefix, inReplyTo, fromName, fromAddress,
						replyToAddress, emailAddress, fullName,
						messageParentMessageContent,
						messageSiblingMessagesContent, rootMessageBody,
						subjectLocalizedValuesMap, bodyLocalizedValuesMap,
						serviceContext);

				sourceMailingListSubscriptionSender.setBulk(false);

				sourceMailingListSubscriptionSender.addMailingListSubscriber(
					message.getGroupId(), categoryId);

				sourceMailingListSubscriptionSender.flushNotificationsAsync();
			}
		}
	}

	private void _pingPingback(
		MBMessage message, ServiceContext serviceContext) {

		if (!PropsValues.BLOGS_PINGBACK_ENABLED ||
			!message.isAllowPingbacks() || !message.isApproved()) {

			return;
		}

		String layoutFullURL = serviceContext.getLayoutFullURL();

		if (Validator.isNull(layoutFullURL)) {
			return;
		}

		String sourceUri = StringBundler.concat(
			layoutFullURL, Portal.FRIENDLY_URL_SEPARATOR,
			"message_boards/view_message/", message.getMessageId());

		Source source = new Source(message.getBody(message.isFormatBBCode()));

		List<StartTag> startTags = source.getAllStartTags("a");

		for (StartTag startTag : startTags) {
			String targetUri = startTag.getAttributeValue("href");

			if (Validator.isNotNull(targetUri)) {
				try {
					LinkbackProducerUtil.sendPingback(sourceUri, targetUri);
				}
				catch (Exception exception) {
					_log.error(
						"Error while sending pingback " + targetUri, exception);
				}
			}
		}
	}

	private MBMessage _startWorkflowInstance(
			long userId, MBMessage message, ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext =
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_URL,
				_getMessageURL(message, serviceContext)
			).build();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			message.getCompanyId(), message.getGroupId(), userId,
			message.getWorkflowClassName(), message.getMessageId(), message,
			serviceContext, workflowContext);
	}

	private void _updateAsset(
			long userId, MBMessage message, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds,
			boolean assetEntryVisible)
		throws PortalException {

		boolean visible = false;
		Date publishDate = null;

		if (assetEntryVisible && message.isApproved() &&
			((message.getClassNameId() == 0) ||
			 (message.getParentMessageId() != 0))) {

			visible = true;
			publishDate = message.getModifiedDate();
		}

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, message.getGroupId(), message.getCreateDate(),
			message.getModifiedDate(), message.getWorkflowClassName(),
			message.getMessageId(), message.getUuid(), 0, assetCategoryIds,
			assetTagNames, true, visible, null, null, publishDate, null,
			ContentTypes.TEXT_HTML, message.getSubject(), null, null, null,
			null, 0, 0, message.getPriority());

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	private MBMessage _updateMessage(
			long userId, long messageId, String subject, String body,
			double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		int oldStatus = message.getStatus();
		String oldSubject = message.getSubject();

		Date modifiedDate = serviceContext.getModifiedDate(null);

		subject = ModelHintsUtil.trimString(
			MBMessage.class.getName(), "subject", subject);

		subject = _getSubject(subject, body);

		body = _getBody(subject, body, message.getFormat());

		body = SanitizerUtil.sanitize(
			message.getCompanyId(), message.getGroupId(), userId,
			MBMessage.class.getName(), messageId, "text/" + message.getFormat(),
			Sanitizer.MODE_ALL, body,
			HashMapBuilder.<String, Object>put(
				"discussion", message.isDiscussion()
			).build());

		_validate(subject, body);

		message.setModifiedDate(modifiedDate);
		message.setSubject(subject);
		message.setBody(body);
		message.setAllowPingbacks(allowPingbacks);

		if (priority != MBThreadConstants.PRIORITY_NOT_GIVEN) {
			message.setPriority(priority);
		}

		MBThread thread = _mbThreadPersistence.findByPrimaryKey(
			message.getThreadId());

		if ((serviceContext.getWorkflowAction() ==
				WorkflowConstants.ACTION_SAVE_DRAFT) &&
			!message.isDraft() && !message.isPending()) {

			message.setStatus(WorkflowConstants.STATUS_DRAFT);

			// Thread

			thread = _updateThreadStatus(
				thread, message, _userLocalService.getUser(userId), oldStatus,
				modifiedDate);

			// Asset

			_assetEntryLocalService.updateVisible(
				message.getWorkflowClassName(), message.getMessageId(), false);

			if (!message.isDiscussion()) {

				// Indexer

				Indexer<MBMessage> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(MBMessage.class);

				indexer.delete(message);
			}
		}

		message.setExpandoBridgeAttributes(serviceContext);

		message = mbMessagePersistence.update(message);

		// Thread

		if ((priority != MBThreadConstants.PRIORITY_NOT_GIVEN) &&
			(thread.getPriority() != priority)) {

			thread.setPriority(priority);

			thread = _mbThreadLocalService.updateMBThread(thread);

			_updatePriorities(thread.getThreadId(), priority);
		}

		if (message.isRoot()) {
			if (!Objects.equals(subject, oldSubject)) {
				thread.setTitle(subject);
			}

			_mbThreadLocalService.updateMBThread(thread);
		}

		// Asset

		updateAsset(
			userId, message, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());

		// Workflow

		return _startWorkflowInstance(userId, message, serviceContext);
	}

	private void _updatePriorities(long threadId, double priority) {
		List<MBMessage> messages = mbMessagePersistence.findByThreadId(
			threadId);

		for (MBMessage message : messages) {
			if (message.getPriority() != priority) {
				message.setPriority(priority);

				mbMessagePersistence.update(message);
			}
		}
	}

	private void _updateSocialActivity(
			User user, MBMessage message, ServiceContext serviceContext)
		throws PortalException {

		String title = message.getSubject();

		if (message.isDiscussion()) {
			title = HtmlUtil.stripHtml(title);
		}

		JSONObject extraDataJSONObject = JSONUtil.put("title", title);

		if (!message.isDiscussion()) {
			if (!message.isAnonymous() && !user.isGuestUser()) {
				long receiverUserId = 0;

				MBMessage parentMessage =
					mbMessagePersistence.fetchByPrimaryKey(
						message.getParentMessageId());

				if (parentMessage != null) {
					receiverUserId = parentMessage.getUserId();
				}

				int activityKey = MBActivityKeys.UPDATE_MESSAGE;

				if (serviceContext.isCommandAdd()) {
					activityKey = MBActivityKeys.ADD_MESSAGE;
				}

				SocialActivityManagerUtil.addActivity(
					message.getUserId(), message, activityKey,
					extraDataJSONObject.toString(), receiverUserId);

				if ((parentMessage != null) &&
					(receiverUserId != message.getUserId())) {

					SocialActivityManagerUtil.addActivity(
						message.getUserId(), parentMessage,
						MBActivityKeys.REPLY_MESSAGE,
						extraDataJSONObject.toString(), 0);
				}
			}
		}
		else if (serviceContext.isCommandAdd()) {
			long parentMessageId = message.getParentMessageId();

			if (parentMessageId !=
					MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID) {

				String className = (String)serviceContext.getAttribute(
					"className");
				long classPK = ParamUtil.getLong(serviceContext, "classPK");

				AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
					className, classPK);

				if (assetEntry != null) {
					extraDataJSONObject.put(
						"messageId", message.getMessageId());

					SocialActivityManagerUtil.addActivity(
						message.getUserId(), assetEntry,
						SocialActivityConstants.TYPE_ADD_COMMENT,
						extraDataJSONObject.toString(), assetEntry.getUserId());
				}
			}
		}
	}

	private MBThread _updateThreadStatus(
			MBThread thread, MBMessage message, User user, int oldStatus,
			Date modifiedDate)
		throws PortalException {

		int status = message.getStatus();

		if (status == oldStatus) {
			return thread;
		}

		if (thread.getRootMessageId() == message.getMessageId()) {
			thread.setModifiedDate(modifiedDate);
			thread.setStatus(status);
			thread.setStatusByUserId(user.getUserId());
			thread.setStatusByUserName(user.getFullName());
			thread.setStatusDate(modifiedDate);

			if (status == WorkflowConstants.STATUS_APPROVED) {
				if (message.isAnonymous()) {
					thread.setLastPostByUserId(0);
				}
				else {
					thread.setLastPostByUserId(message.getUserId());
				}

				thread.setLastPostDate(modifiedDate);
			}

			thread = _mbThreadPersistence.update(thread);

			Indexer<MBThread> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				MBThread.class);

			indexer.reindex(thread);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			_mbThreadLocalService.updateLastPostDate(
				thread.getThreadId(), modifiedDate);
		}

		return thread;
	}

	private void _validate(String subject, String body) throws PortalException {
		if (Validator.isNull(subject) && Validator.isNull(body)) {
			throw new MessageSubjectException("Subject and body are null");
		}
	}

	private void _validateDiscussionMaxComments(String className, long classPK)
		throws PortalException {

		if (PropsValues.DISCUSSION_MAX_COMMENTS <= 0) {
			return;
		}

		int count = mbMessageLocalService.getDiscussionMessagesCount(
			className, classPK, WorkflowConstants.STATUS_APPROVED);

		if (count >= PropsValues.DISCUSSION_MAX_COMMENTS) {
			int max = PropsValues.DISCUSSION_MAX_COMMENTS - 1;

			throw new DiscussionMaxCommentsException(count + " exceeds " + max);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBMessageLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private Closeable _closeable;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LiferayJSONDeserializationWhitelist
		_liferayJSONDeserializationWhitelist;

	@Reference
	private Localization _localization;

	@Reference
	private MBCategoryPersistence _mbCategoryPersistence;

	@Reference
	private MBDiscussionLocalService _mbDiscussionLocalService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference
	private MBThreadPersistence _mbThreadPersistence;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}
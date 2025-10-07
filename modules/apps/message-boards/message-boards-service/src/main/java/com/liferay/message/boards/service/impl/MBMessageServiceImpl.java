/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.constants.MBThreadConstants;
import com.liferay.message.boards.exception.LockedThreadException;
import com.liferay.message.boards.internal.util.MBUtil;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.message.boards.service.base.MBMessageServiceBaseImpl;
import com.liferay.message.boards.util.comparator.MessageCreateDateComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.parsers.bbcode.BBCodeTranslatorUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.rss.export.RSSExporter;
import com.liferay.rss.model.SyndContent;
import com.liferay.rss.model.SyndEntry;
import com.liferay.rss.model.SyndFeed;
import com.liferay.rss.model.SyndLink;
import com.liferay.rss.model.SyndModelFactory;
import com.liferay.rss.util.RSSUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Mika Koivisto
 * @author Shuyang Zhou
 */
@Component(
	property = {
		"json.web.service.context.name=mb",
		"json.web.service.context.path=MBMessage"
	},
	service = AopService.class
)
public class MBMessageServiceImpl extends MBMessageServiceBaseImpl {

	@Override
	public MBMessage addDiscussionMessage(
			long groupId, String className, long classPK, long threadId,
			long parentMessageId, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException {

		User user = getGuestOrUser();

		_discussionPermission.checkAddPermission(
			getPermissionChecker(), user.getCompanyId(),
			serviceContext.getScopeGroupId(), className, classPK);

		return mbMessageLocalService.addDiscussionMessage(
			null, user.getUserId(), null, groupId, className, classPK, threadId,
			parentMessageId, subject, body, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addMessage(String, long, String, String, String, List,
	 *             boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long groupId, long categoryId, String subject, String body,
			String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(), groupId,
			categoryId, ActionKeys.ADD_MESSAGE);

		if (!ModelResourcePermissionUtil.contains(
				_categoryModelResourcePermission, getPermissionChecker(),
				groupId, categoryId, ActionKeys.ADD_FILE)) {

			inputStreamOVPs = Collections.emptyList();
		}

		if (!ModelResourcePermissionUtil.contains(
				_categoryModelResourcePermission, getPermissionChecker(),
				groupId, categoryId, ActionKeys.UPDATE_THREAD_PRIORITY)) {

			priority = MBThreadConstants.PRIORITY_NOT_GIVEN;
		}

		return mbMessageLocalService.addMessage(
			getGuestOrUserId(), null, groupId, categoryId, subject, body,
			format, inputStreamOVPs, anonymous, priority, allowPingbacks,
			serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addMessage(String, long, String, String, String, List,
	 *             boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long groupId, long categoryId, String subject, String body,
			String format, String fileName, File file, boolean anonymous,
			double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws FileNotFoundException, PortalException {

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			new ArrayList<>();

		InputStream inputStream = new FileInputStream(file);

		ObjectValuePair<String, InputStream> inputStreamOVP =
			new ObjectValuePair<>(fileName, inputStream);

		inputStreamOVPs.add(inputStreamOVP);

		return addMessage(
			groupId, categoryId, subject, body, format, inputStreamOVPs,
			anonymous, priority, allowPingbacks, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addMessage(String, long, String, String, String, List,
	 *             boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long categoryId, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getCategory(categoryId);

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			Collections.emptyList();

		return addMessage(
			category.getGroupId(), categoryId, subject, body,
			MBMessageConstants.DEFAULT_FORMAT, inputStreamOVPs, false, 0.0,
			false, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addMessage(String, long, String, String, String, List,
	 *             boolean, double, boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public MBMessage addMessage(
			long parentMessageId, String subject, String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		return addMessage(
			null, parentMessageId, subject, body, format, inputStreamOVPs,
			anonymous, priority, allowPingbacks, serviceContext);
	}

	@Override
	public MBMessage addMessage(
			String externalReferenceCode, long parentMessageId, String subject,
			String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		MBMessage parentMessage = mbMessagePersistence.findByPrimaryKey(
			parentMessageId);

		_checkReplyToPermission(
			parentMessage.getGroupId(), parentMessage.getCategoryId(),
			parentMessageId);

		boolean preview = ParamUtil.getBoolean(serviceContext, "preview");

		int workflowAction = serviceContext.getWorkflowAction();

		if ((workflowAction == WorkflowConstants.STATUS_DRAFT) && !preview &&
			!serviceContext.isSignedIn()) {

			_messageModelResourcePermission.check(
				getPermissionChecker(), parentMessageId, ActionKeys.UPDATE);
		}

		if (_lockManager.isLocked(
				MBThread.class.getName(), parentMessage.getThreadId())) {

			throw new LockedThreadException(
				StringBundler.concat(
					"Thread is locked for class name ",
					MBThread.class.getName(), " and class PK ",
					parentMessage.getThreadId()));
		}

		if (!ModelResourcePermissionUtil.contains(
				_categoryModelResourcePermission, getPermissionChecker(),
				parentMessage.getGroupId(), parentMessage.getCategoryId(),
				ActionKeys.ADD_FILE)) {

			inputStreamOVPs = Collections.emptyList();
		}

		if (!ModelResourcePermissionUtil.contains(
				_categoryModelResourcePermission, getPermissionChecker(),
				parentMessage.getGroupId(), parentMessage.getCategoryId(),
				ActionKeys.UPDATE_THREAD_PRIORITY)) {

			priority = MBThreadConstants.PRIORITY_NOT_GIVEN;
		}

		return mbMessageLocalService.addMessage(
			externalReferenceCode, getGuestOrUserId(), null,
			parentMessage.getGroupId(), parentMessage.getCategoryId(),
			parentMessage.getThreadId(), parentMessageId, subject, body, format,
			inputStreamOVPs, anonymous, priority, allowPingbacks,
			serviceContext);
	}

	@Override
	public void addMessageAttachment(
			long messageId, String fileName, File file, String mimeType)
		throws PortalException {

		MBMessage message = mbMessageLocalService.getMBMessage(messageId);

		if (_lockManager.isLocked(
				MBThread.class.getName(), message.getThreadId())) {

			throw new LockedThreadException(
				StringBundler.concat(
					"Thread is locked for class name ",
					MBThread.class.getName(), " and class PK ",
					message.getThreadId()));
		}

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			message.getGroupId(), message.getCategoryId(), ActionKeys.ADD_FILE);

		mbMessageLocalService.addMessageAttachment(
			getUserId(), messageId, fileName, file, mimeType);
	}

	@Override
	public FileEntry addTempAttachment(
			long groupId, long categoryId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(), groupId,
			categoryId, ActionKeys.ADD_FILE);

		return mbMessageLocalService.addTempAttachment(
			groupId, getUserId(), folderName, fileName, inputStream, mimeType);
	}

	@Override
	public void deleteDiscussionMessage(long messageId) throws PortalException {
		_discussionPermission.checkDeletePermission(
			getPermissionChecker(), messageId);

		mbMessageLocalService.deleteDiscussionMessage(messageId);
	}

	@Override
	public void deleteMessage(long messageId) throws PortalException {
		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.DELETE);

		mbMessageLocalService.deleteMessage(messageId);
	}

	@Override
	public void deleteMessageAttachment(long messageId, String fileName)
		throws PortalException {

		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.UPDATE);

		mbMessageLocalService.deleteMessageAttachment(messageId, fileName);
	}

	@Override
	public void deleteMessageAttachments(long messageId)
		throws PortalException {

		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.DELETE);

		mbMessageLocalService.deleteMessageAttachments(messageId);
	}

	@Override
	public void deleteTempAttachment(
			long groupId, long categoryId, String folderName, String fileName)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(), groupId,
			categoryId, ActionKeys.ADD_FILE);

		mbMessageLocalService.deleteTempAttachment(
			groupId, getUserId(), folderName, fileName);
	}

	@Override
	public void emptyMessageAttachments(long messageId) throws PortalException {
		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.DELETE);

		mbMessageLocalService.emptyMessageAttachments(messageId);
	}

	@Override
	public MBMessage fetchMBMessageByUrlSubject(long groupId, String urlSubject)
		throws PortalException {

		MBMessage mbMessage = mbMessageLocalService.fetchMBMessageByUrlSubject(
			groupId, urlSubject);

		if (mbMessage == null) {
			return null;
		}

		_messageModelResourcePermission.check(
			getPermissionChecker(), mbMessage, ActionKeys.VIEW);

		return mbMessage;
	}

	@Override
	public List<MBMessage> getCategoryMessages(
			long groupId, long categoryId, int status, int start, int end)
		throws PortalException {

		return TransformUtil.transform(
			mbMessageLocalService.getCategoryMessages(
				groupId, categoryId, status, start, end),
			message -> {
				if (_messageModelResourcePermission.contains(
						getPermissionChecker(), message, ActionKeys.VIEW)) {

					return message;
				}

				return null;
			});
	}

	@Override
	public int getCategoryMessagesCount(
		long groupId, long categoryId, int status) {

		return mbMessageLocalService.getCategoryMessagesCount(
			groupId, categoryId, status);
	}

	@Override
	public String getCategoryMessagesRSS(
			long groupId, long categoryId, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException {

		String name = StringPool.BLANK;
		String description = StringPool.BLANK;

		MBCategory category = _mbCategoryLocalService.fetchMBCategory(
			categoryId);

		if (category == null) {
			Group group = _groupLocalService.getGroup(categoryId);

			groupId = group.getGroupId();

			categoryId = MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;
			name = group.getDescriptiveName();
			description = group.getDescription(
				LocaleUtil.getMostRelevantLocale());
		}
		else {
			groupId = category.getGroupId();
			name = category.getName();
			description = category.getDescription();
		}

		List<MBMessage> messages = new ArrayList<>();

		int lastIntervalStart = 0;
		boolean listNotExhausted = true;
		MessageCreateDateComparator comparator =
			MessageCreateDateComparator.getInstance(false);

		while ((messages.size() < max) && listNotExhausted) {
			List<MBMessage> messageList =
				mbMessageLocalService.getCategoryMessages(
					groupId, categoryId, status, lastIntervalStart,
					lastIntervalStart + max, comparator);

			lastIntervalStart += max;
			listNotExhausted = messageList.size() == max;

			for (MBMessage message : messageList) {
				if (messages.size() >= max) {
					break;
				}

				if (_messageModelResourcePermission.contains(
						getPermissionChecker(), message, ActionKeys.VIEW)) {

					messages.add(message);
				}
			}
		}

		return _exportToRSS(
			name, description, type, version, displayStyle, feedURL, entryURL,
			messages, themeDisplay);
	}

	@Override
	public List<MBMessage> getChildMessages(
			long parentMessageId, boolean flatten,
			QueryDefinition<MBMessage> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return mbMessageFinder.findByParentMessageId(
			parentMessageId, flatten, queryDefinition);
	}

	@Override
	public int getChildMessagesCount(
			long parentMessageId, boolean flatten,
			QueryDefinition<MBMessage> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return mbMessageFinder.countByParentMessageId(
			parentMessageId, flatten, queryDefinition);
	}

	@Override
	public String getCompanyMessagesRSS(
			long companyId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Company company = _companyLocalService.getCompany(companyId);

		String name = company.getName();
		String description = company.getName();

		List<MBMessage> messages = new ArrayList<>();

		int lastIntervalStart = 0;
		boolean listNotExhausted = true;
		MessageCreateDateComparator comparator =
			MessageCreateDateComparator.getInstance(false);

		while ((messages.size() < max) && listNotExhausted) {
			List<MBMessage> messageList =
				mbMessageLocalService.getCompanyMessages(
					companyId, status, lastIntervalStart,
					lastIntervalStart + max, comparator);

			lastIntervalStart += max;
			listNotExhausted = messageList.size() == max;

			for (MBMessage message : messageList) {
				if (messages.size() >= max) {
					break;
				}

				if (_messageModelResourcePermission.contains(
						getPermissionChecker(), message, ActionKeys.VIEW)) {

					messages.add(message);
				}
			}
		}

		return _exportToRSS(
			name, description, type, version, displayStyle, feedURL, entryURL,
			messages, themeDisplay);
	}

	@Override
	public int getGroupMessagesCount(long groupId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.filterCountByGroupId(groupId);
		}

		return mbMessagePersistence.filterCountByG_S(groupId, status);
	}

	@Override
	public String getGroupMessagesRSS(
			long groupId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		String name = group.getDescriptiveName();
		String description = group.getDescription(
			LocaleUtil.getMostRelevantLocale());

		List<MBMessage> messages = new ArrayList<>();

		int lastIntervalStart = 0;
		boolean listNotExhausted = true;
		MessageCreateDateComparator comparator =
			MessageCreateDateComparator.getInstance(false);

		while ((messages.size() < max) && listNotExhausted) {
			List<MBMessage> messageList =
				mbMessageLocalService.getGroupMessages(
					groupId, status, lastIntervalStart, lastIntervalStart + max,
					comparator);

			lastIntervalStart += max;
			listNotExhausted = messageList.size() == max;

			for (MBMessage message : messageList) {
				if (messages.size() >= max) {
					break;
				}

				if (_messageModelResourcePermission.contains(
						getPermissionChecker(), message, ActionKeys.VIEW)) {

					messages.add(message);
				}
			}
		}

		return _exportToRSS(
			name, description, type, version, displayStyle, feedURL, entryURL,
			messages, themeDisplay);
	}

	@Override
	public String getGroupMessagesRSS(
			long groupId, long userId, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		String name = group.getDescriptiveName();
		String description = group.getDescription(
			LocaleUtil.getMostRelevantLocale());

		List<MBMessage> messages = new ArrayList<>();

		int lastIntervalStart = 0;
		boolean listNotExhausted = true;
		MessageCreateDateComparator comparator =
			MessageCreateDateComparator.getInstance(false);

		while ((messages.size() < max) && listNotExhausted) {
			List<MBMessage> messageList =
				mbMessageLocalService.getGroupMessages(
					groupId, userId, status, lastIntervalStart,
					lastIntervalStart + max, comparator);

			lastIntervalStart += max;
			listNotExhausted = messageList.size() == max;

			for (MBMessage message : messageList) {
				if (messages.size() >= max) {
					break;
				}

				if (_messageModelResourcePermission.contains(
						getPermissionChecker(), message, ActionKeys.VIEW)) {

					messages.add(message);
				}
			}
		}

		return _exportToRSS(
			name, description, type, version, displayStyle, feedURL, entryURL,
			messages, themeDisplay);
	}

	@Override
	public List<MBMessage> getGroupUserMessageBoardMessagesActivity(
			long groupId, long userId, int start, int end)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.VIEW);

		return mbMessageLocalService.getGroupUserMessageBoardMessagesActivity(
			groupId, userId, start, end);
	}

	@Override
	public int getGroupUserMessageBoardMessagesActivityCount(
			long groupId, long userId)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.VIEW);

		return mbMessageLocalService.
			getGroupUserMessageBoardMessagesActivityCount(groupId, userId);
	}

	@Override
	public MBMessage getMBMessageByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		MBMessage mbMessage =
			mbMessageLocalService.getMBMessageByExternalReferenceCode(
				externalReferenceCode, groupId);

		_messageModelResourcePermission.check(
			getPermissionChecker(), mbMessage, ActionKeys.VIEW);

		return mbMessage;
	}

	@Override
	public MBMessage getMessage(long messageId) throws PortalException {
		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.VIEW);

		return mbMessageLocalService.getMessage(messageId);
	}

	@Override
	public MBMessageDisplay getMessageDisplay(long messageId, int status)
		throws PortalException {

		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.VIEW);

		return mbMessageLocalService.getMessageDisplay(
			getGuestOrUserId(), messageId, status);
	}

	@Override
	public String[] getTempAttachmentNames(long groupId, String folderName)
		throws PortalException {

		return mbMessageLocalService.getTempAttachmentNames(
			groupId, getUserId(), folderName);
	}

	@Override
	public int getThreadAnswersCount(
		long groupId, long categoryId, long threadId) {

		return mbMessagePersistence.filterCountByG_C_T_A(
			groupId, categoryId, threadId, true);
	}

	@Override
	public List<MBMessage> getThreadMessages(
		long groupId, long categoryId, long threadId, int status, int start,
		int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.filterFindByG_C_T(
				groupId, categoryId, threadId, start, end);
		}

		return mbMessagePersistence.filterFindByG_C_T_S(
			groupId, categoryId, threadId, status, start, end);
	}

	@Override
	public int getThreadMessagesCount(
		long groupId, long categoryId, long threadId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbMessagePersistence.filterCountByG_C_T(
				groupId, categoryId, threadId);
		}

		return mbMessagePersistence.filterCountByG_C_T_S(
			groupId, categoryId, threadId, status);
	}

	@Override
	public String getThreadMessagesRSS(
			long threadId, int status, int max, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			ThemeDisplay themeDisplay)
		throws PortalException {

		String name = StringPool.BLANK;
		String description = StringPool.BLANK;

		List<MBMessage> messages = new ArrayList<>();

		MBThread thread = _mbThreadLocalService.getThread(threadId);

		if (_messageModelResourcePermission.contains(
				getPermissionChecker(), thread.getRootMessageId(),
				ActionKeys.VIEW)) {

			MessageCreateDateComparator comparator =
				MessageCreateDateComparator.getInstance(false);

			List<MBMessage> threadMessages =
				mbMessageLocalService.getThreadMessages(
					threadId, status, comparator);

			for (MBMessage message : threadMessages) {
				if (messages.size() >= max) {
					break;
				}

				if (_messageModelResourcePermission.contains(
						getPermissionChecker(), message, ActionKeys.VIEW)) {

					messages.add(message);
				}
			}

			if (!messages.isEmpty()) {
				MBMessage message = messages.get(messages.size() - 1);

				name = message.getSubject();
				description = message.getSubject();
			}
		}

		return _exportToRSS(
			name, description, type, version, displayStyle, feedURL, entryURL,
			messages, themeDisplay);
	}

	@Override
	public void moveMessageAttachmentToTrash(long messageId, String fileName)
		throws PortalException {

		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.UPDATE);

		mbMessageLocalService.moveMessageAttachmentToTrash(
			getUserId(), messageId, fileName);
	}

	@Override
	public void restoreMessageAttachmentFromTrash(
			long messageId, String fileName)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			message.getGroupId(), message.getCategoryId(), ActionKeys.ADD_FILE);

		mbMessageLocalService.restoreMessageAttachmentFromTrash(
			getUserId(), messageId, fileName);
	}

	@Override
	public void subscribeMessage(long messageId) throws PortalException {
		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.SUBSCRIBE);

		mbMessageLocalService.subscribeMessage(getUserId(), messageId);
	}

	@Override
	public void unsubscribeMessage(long messageId) throws PortalException {
		_messageModelResourcePermission.check(
			getPermissionChecker(), messageId, ActionKeys.SUBSCRIBE);

		mbMessageLocalService.unsubscribeMessage(getUserId(), messageId);
	}

	@Override
	public MBMessage updateAnswer(
			long messageId, boolean answer, boolean cascade)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		_messageModelResourcePermission.check(
			getPermissionChecker(), message.getRootMessageId(),
			ActionKeys.UPDATE);

		return mbMessageLocalService.updateAnswer(messageId, answer, cascade);
	}

	@Override
	public MBMessage updateDiscussionMessage(
			String className, long classPK, long messageId, String subject,
			String body, ServiceContext serviceContext)
		throws PortalException {

		_discussionPermission.checkUpdatePermission(
			getPermissionChecker(), messageId);

		return mbMessageLocalService.updateDiscussionMessage(
			getUserId(), messageId, className, classPK, subject, body,
			serviceContext);
	}

	@Override
	public MBMessage updateMessage(
			long messageId, String subject, String body,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException {

		MBMessage message = mbMessagePersistence.findByPrimaryKey(messageId);

		boolean preview = ParamUtil.getBoolean(serviceContext, "preview");

		if (preview &&
			_messageModelResourcePermission.contains(
				getPermissionChecker(), message, ActionKeys.UPDATE)) {

			_checkReplyToPermission(
				message.getGroupId(), message.getCategoryId(),
				message.getParentMessageId());
		}
		else {
			_messageModelResourcePermission.check(
				getPermissionChecker(), messageId, ActionKeys.UPDATE);
		}

		if (_lockManager.isLocked(
				MBThread.class.getName(), message.getThreadId())) {

			throw new LockedThreadException(
				StringBundler.concat(
					"Thread is locked for class name ",
					MBThread.class.getName(), " and class PK ",
					message.getThreadId()));
		}

		if (!ModelResourcePermissionUtil.contains(
				_categoryModelResourcePermission, getPermissionChecker(),
				message.getGroupId(), message.getCategoryId(),
				ActionKeys.ADD_FILE)) {

			inputStreamOVPs = Collections.emptyList();
		}

		if (!ModelResourcePermissionUtil.contains(
				_categoryModelResourcePermission, getPermissionChecker(),
				message.getGroupId(), message.getCategoryId(),
				ActionKeys.UPDATE_THREAD_PRIORITY)) {

			MBThread thread = _mbThreadLocalService.getThread(
				message.getThreadId());

			priority = thread.getPriority();
		}

		return mbMessageLocalService.updateMessage(
			getGuestOrUserId(), messageId, subject, body, inputStreamOVPs,
			priority, allowPingbacks, serviceContext);
	}

	private void _checkReplyToPermission(
			long groupId, long categoryId, long parentMessageId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (parentMessageId > 0) {
			if (ModelResourcePermissionUtil.contains(
					_categoryModelResourcePermission, permissionChecker,
					groupId, categoryId, ActionKeys.ADD_MESSAGE)) {

				return;
			}

			if (!ModelResourcePermissionUtil.contains(
					_categoryModelResourcePermission, permissionChecker,
					groupId, categoryId, ActionKeys.REPLY_TO_MESSAGE)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, MBCategory.class.getName(), categoryId,
					ActionKeys.REPLY_TO_MESSAGE);
			}
		}
		else {
			ModelResourcePermissionUtil.check(
				_categoryModelResourcePermission, permissionChecker, groupId,
				categoryId, ActionKeys.ADD_MESSAGE);
		}
	}

	private String _exportToRSS(
		String name, String description, String type, double version,
		String displayStyle, String feedURL, String entryURL,
		List<MBMessage> messages, ThemeDisplay themeDisplay) {

		SyndFeed syndFeed = _syndModelFactory.createSyndFeed();

		syndFeed.setDescription(description);

		List<SyndEntry> syndEntries = new ArrayList<>();

		syndFeed.setEntries(syndEntries);

		for (MBMessage message : messages) {
			SyndEntry syndEntry = _syndModelFactory.createSyndEntry();

			if (message.isAnonymous()) {
				syndEntry.setAuthor(
					_language.get(themeDisplay.getLocale(), "anonymous"));
			}
			else {
				syndEntry.setAuthor(_portal.getUserName(message));
			}

			SyndContent syndContent = _syndModelFactory.createSyndContent();

			syndContent.setType(RSSUtil.ENTRY_TYPE_DEFAULT);

			String value = null;

			if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_ABSTRACT)) {
				value = StringUtil.shorten(
					_htmlParser.extractText(message.getBody()),
					PropsValues.MESSAGE_BOARDS_RSS_ABSTRACT_LENGTH,
					StringPool.BLANK);
			}
			else if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_TITLE)) {
				value = StringPool.BLANK;
			}
			else if (message.isFormatBBCode()) {
				value = BBCodeTranslatorUtil.getHTML(message.getBody());

				value = MBUtil.replaceMessageBodyPaths(themeDisplay, value);
			}
			else {
				value = message.getBody();
			}

			syndContent.setValue(value);

			syndEntry.setDescription(syndContent);

			String link = entryURL + "&messageId=" + message.getMessageId();

			syndEntry.setLink(link);

			syndEntry.setPublishedDate(message.getCreateDate());
			syndEntry.setTitle(message.getSubject());
			syndEntry.setUpdatedDate(message.getModifiedDate());
			syndEntry.setUri(link);

			syndEntries.add(syndEntry);
		}

		syndFeed.setFeedType(RSSUtil.getFeedType(type, version));

		List<SyndLink> syndLinks = new ArrayList<>();

		syndFeed.setLinks(syndLinks);

		SyndLink selfSyndLink = _syndModelFactory.createSyndLink();

		syndLinks.add(selfSyndLink);

		selfSyndLink.setHref(feedURL);
		selfSyndLink.setRel("self");

		syndFeed.setPublishedDate(new Date());
		syndFeed.setTitle(name);
		syndFeed.setUri(feedURL);

		return _rssExporter.export(syndFeed);
	}

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Language _language;

	@Reference
	private LockManager _lockManager;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBMessage)"
	)
	private ModelResourcePermission<MBMessage> _messageModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference
	private RSSExporter _rssExporter;

	@Reference
	private SyndModelFactory _syndModelFactory;

}
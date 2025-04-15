/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.helper;

import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.internal.util.MBUtil;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.parsers.bbcode.BBCodeTranslatorUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author Alicia García
 */
public class MBMessageNotificationTemplateHelper {

	public MBMessageNotificationTemplateHelper(
		boolean htmlFormat, int maxNumberOfMessages,
		int maxNumberOfParentMessages,
		MBMessageLocalService mbMessageLocalService,
		ServiceContext serviceContext) {

		_htmlFormat = htmlFormat;
		_maxNumberOfMessages = maxNumberOfMessages;
		_maxNumberOfParentMessages = maxNumberOfParentMessages;
		_mbMessageLocalService = mbMessageLocalService;
		_serviceContext = serviceContext;
	}

	public String getMessageBody(MBMessage message, String quoteMark) {
		if (!_htmlFormat) {
			return _getQuotedMessage(true, message.getBody(), quoteMark);
		}

		if (!message.isFormatBBCode()) {
			return message.getBody();
		}

		try {
			String messageBody = BBCodeTranslatorUtil.getHTML(
				message.getBody());

			HttpServletRequest httpServletRequest =
				_serviceContext.getRequest();

			if (httpServletRequest == null) {
				return messageBody;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return MBUtil.replaceMessageBodyPaths(themeDisplay, messageBody);
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to parse message ", message.getMessageId(), ": ",
					exception.getMessage()));
		}

		return message.getBody();
	}

	public String renderMessageParentMessageContent(MBMessage parentMessage) {
		if ((_maxNumberOfParentMessages == 0) ||
			(parentMessage.getParentMessageId() ==
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID)) {

			return StringPool.BLANK;
		}

		List<MBMessage> messages = new LinkedList<>();

		int numberOfMessages = _maxNumberOfParentMessages;

		while ((numberOfMessages > 0) && (parentMessage != null) &&
			   (parentMessage.getParentMessageId() !=
				   MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID)) {

			messages.add(0, parentMessage);

			parentMessage = _mbMessageLocalService.fetchMBMessage(
				parentMessage.getParentMessageId());
			numberOfMessages--;
		}

		_numberOfMessagesByParentMessageIds.put(
			parentMessage.getMessageId(), messages.size());

		if (ListUtil.isEmpty(messages)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(messages.size());

		sb.append(_getMarkupElement(MarkupElement.START_MESSAGE_THREAD));

		int elementCount = 0;

		for (MBMessage message : messages) {
			sb.append(
				StringBundler.concat(
					_getMarkupElement(MarkupElement.START_MESSAGE),
					_getMarkupElement(MarkupElement.START_USER_MESSAGE),
					_getUserName(message, _getQuote(elementCount)),
					_getMarkupElement(MarkupElement.END),
					_getMarkupElement(MarkupElement.START_MESSAGE_BODY),
					getMessageBody(message, _getQuote(elementCount)),
					_getMarkupElement(MarkupElement.END),
					_getMarkupElement(MarkupElement.END_MESSAGE)));

			elementCount++;
		}

		for (int i = 0; i < elementCount; i++) {
			sb.append(_getMarkupElement(MarkupElement.END_ELEMENT));
		}

		sb.append(_getMarkupElement(MarkupElement.END));

		return sb.toString();
	}

	public String renderMessageSiblingMessagesContent(MBMessage message) {
		int numberOfMessagesByParentMessageId =
			_numberOfMessagesByParentMessageIds.getOrDefault(
				message.getParentMessageId(), 0);

		int numberOfMessages =
			_maxNumberOfMessages - numberOfMessagesByParentMessageId;

		if (numberOfMessages == 0) {
			return StringPool.BLANK;
		}

		int childMessagesCount = _mbMessageLocalService.getChildMessagesCount(
			message.getParentMessageId(), WorkflowConstants.STATUS_APPROVED);

		if (childMessagesCount == 1) {
			return StringPool.BLANK;
		}

		List<MBMessage> childMessages = _mbMessageLocalService.getChildMessages(
			message.getParentMessageId(), WorkflowConstants.STATUS_APPROVED,
			childMessagesCount - numberOfMessages - 1, childMessagesCount - 1);

		if (ListUtil.isEmpty(childMessages)) {
			return StringPool.BLANK;
		}

		String quoteMark = _getQuote(numberOfMessagesByParentMessageId + 1);

		StringBundler sb = new StringBundler(childMessages.size() + 2);

		sb.append(_getMarkupElement(MarkupElement.START_SIBLING));

		for (MBMessage childMessage : childMessages) {
			sb.append(
				StringBundler.concat(
					_getMarkupElement(MarkupElement.START_MESSAGE_SIBLING),
					_getMarkupElement(MarkupElement.START_USER_SIBLING),
					_getUserName(childMessage, quoteMark),
					_getMarkupElement(MarkupElement.END),
					_getMarkupElement(MarkupElement.START_BODY_SIBLING),
					getMessageBody(childMessage, quoteMark),
					_getMarkupElement(MarkupElement.END),
					_getMarkupElement(MarkupElement.END)));
		}

		sb.append(_getMarkupElement(MarkupElement.END));

		return sb.toString();
	}

	public String renderRootMessage(MBMessage message) throws PortalException {
		if (message.getParentMessageId() ==
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID) {

			return StringPool.BLANK;
		}

		MBMessage rootMessage = _mbMessageLocalService.getMessage(
			message.getRootMessageId());

		return StringBundler.concat(
			_getMarkupElement(MarkupElement.START_ROOT),
			_getMarkupElement(MarkupElement.START_USER_ROOT),
			_getUserName(rootMessage, _getQuoteMark()),
			_getMarkupElement(MarkupElement.END),
			_getMarkupElement(MarkupElement.START_BODY_ROOT),
			getMessageBody(rootMessage, _getQuoteMark()),
			_getMarkupElement(MarkupElement.END),
			_getMarkupElement(MarkupElement.END));
	}

	private String _getMarkupElement(MarkupElement element) {
		if (!_htmlFormat) {
			return StringPool.BLANK;
		}

		return _markupElements.getOrDefault(element, StringPool.BLANK);
	}

	private String _getQuote(int depth) {
		if (Validator.isBlank(_getQuoteMark())) {
			return StringPool.BLANK;
		}

		return StringUtils.repeat(_QUOTE_MARK, depth) + _getQuoteMark();
	}

	private String _getQuotedMessage(
		boolean lastPosition, String messageBody, String quoteMark) {

		if (Validator.isBlank(quoteMark)) {
			return messageBody;
		}

		StringBundler sb = new StringBundler();

		for (String line : messageBody.split(StringPool.NEW_LINE)) {
			sb.append(StringPool.NEW_LINE);
			sb.append(quoteMark);
			sb.append(line);
		}

		sb.append(StringPool.NEW_LINE);

		if (!lastPosition) {
			sb.append(quoteMark);
		}

		return sb.toString();
	}

	private String _getQuoteMark() {
		if (_htmlFormat) {
			return StringPool.BLANK;
		}

		return _QUOTE_MARK + StringPool.SPACE;
	}

	private String _getUserName(MBMessage message, String quoteMark) {
		if (!_htmlFormat) {
			return _getQuotedMessage(false, message.getUserName(), quoteMark);
		}

		return message.getUserName() + "<br />";
	}

	private static final String _QUOTE_MARK = StringPool.GREATER_THAN;

	private static final Log _log = LogFactoryUtil.getLog(
		MBMessageNotificationTemplateHelper.class);

	private final boolean _htmlFormat;
	private final Map<MarkupElement, String> _markupElements =
		HashMapBuilder.put(
			MarkupElement.END, "</div>"
		).put(
			MarkupElement.END_ELEMENT, "</ul>"
		).put(
			MarkupElement.END_MESSAGE, "</li>"
		).put(
			MarkupElement.START_BODY_ROOT,
			"<div class=\"mb-root-message-body\">"
		).put(
			MarkupElement.START_BODY_SIBLING,
			"<div class=\"mb-sibling-message-body\">"
		).put(
			MarkupElement.START_MESSAGE, "<ul><li class=\"mb-parent-message\">"
		).put(
			MarkupElement.START_MESSAGE_BODY,
			"<div class=\"mb-parent-message-body\">"
		).put(
			MarkupElement.START_MESSAGE_SIBLING,
			"<div class=\"mb-sibling-message\">"
		).put(
			MarkupElement.START_MESSAGE_THREAD,
			"<div class=\"mb-parent-message-thread\">"
		).put(
			MarkupElement.START_ROOT, "<div class=\"mb-root-message\">"
		).put(
			MarkupElement.START_SIBLING,
			"<div class=\"mb-sibling-message-thread\">"
		).put(
			MarkupElement.START_USER_MESSAGE,
			"<div class=\"mb-parent-message-user\">"
		).put(
			MarkupElement.START_USER_ROOT,
			"<div class=\"mb-root-message-user\">"
		).put(
			MarkupElement.START_USER_SIBLING,
			"<div class=\"mb-sibling-message-user\">"
		).build();
	private final int _maxNumberOfMessages;
	private final int _maxNumberOfParentMessages;
	private final MBMessageLocalService _mbMessageLocalService;
	private final Map<Long, Integer> _numberOfMessagesByParentMessageIds =
		new HashMap<>();
	private final ServiceContext _serviceContext;

	private enum MarkupElement {

		END, END_ELEMENT, END_MESSAGE, START_BODY_ROOT, START_BODY_SIBLING,
		START_MESSAGE, START_MESSAGE_BODY, START_MESSAGE_SIBLING,
		START_MESSAGE_THREAD, START_ROOT, START_SIBLING, START_USER_MESSAGE,
		START_USER_ROOT, START_USER_SIBLING

	}

}
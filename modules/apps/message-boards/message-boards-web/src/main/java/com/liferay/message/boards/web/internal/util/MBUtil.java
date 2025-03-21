/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.util;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBThreadConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.message.boards.web.internal.security.permission.MBMessagePermission;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sergio González
 */
public class MBUtil {

	public static String getBBCodeQuoteBody(
		HttpServletRequest httpServletRequest, MBMessage parentMessage) {

		return StringBundler.concat(
			"[quote=",
			StringUtil.replace(
				_getParentAuthor(parentMessage, httpServletRequest),
				new String[] {"[", "]"}, new String[] {"&#91;", "&#93;"}),
			"]\n", parentMessage.getBody(false), "[/quote]\n\n\n");
	}

	public static String getBBCodeSplitThreadBody(
		HttpServletRequest httpServletRequest) {

		return LanguageUtil.format(
			httpServletRequest, "the-new-thread-can-be-found-at-x",
			StringBundler.concat(
				"[url=", MBThreadConstants.NEW_THREAD_URL, "]",
				MBThreadConstants.NEW_THREAD_URL, "[/url]"),
			false);
	}

	public static long getCategoryId(
		HttpServletRequest httpServletRequest, MBCategory category) {

		long categoryId = MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;

		if (category != null) {
			categoryId = category.getCategoryId();
		}

		return ParamUtil.getLong(
			httpServletRequest, "mbCategoryId", categoryId);
	}

	public static long getCategoryId(
		HttpServletRequest httpServletRequest, MBMessage message) {

		long categoryId = MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;

		if (message != null) {
			categoryId = message.getCategoryId();
		}

		return ParamUtil.getLong(
			httpServletRequest, "mbCategoryId", categoryId);
	}

	public static long getCategoryId(
		PortletRequest portletRequest, MBCategory category) {

		long categoryId = MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;

		if (category != null) {
			categoryId = category.getCategoryId();
		}

		return ParamUtil.getLong(portletRequest, "mbCategoryId", categoryId);
	}

	public static String getEditorName(String messageFormat) {
		if (messageFormat.equals("bbcode")) {
			return "ckeditor_bbcode";
		}

		return "ckeditor_classic";
	}

	public static String getHtmlQuoteBody(
		HttpServletRequest httpServletRequest, MBMessage parentMessage) {

		return StringBundler.concat(
			"<blockquote><div class=\"quote-title\">",
			_getParentAuthor(parentMessage, httpServletRequest),
			": </div><div class=\"quote\"><div class=\"quote-content\">",
			parentMessage.getBody(false),
			"</div></blockquote><br /><br /><br />");
	}

	public static String getHtmlSplitThreadBody(
		HttpServletRequest httpServletRequest) {

		return LanguageUtil.format(
			httpServletRequest, "the-new-thread-can-be-found-at-x",
			StringBundler.concat(
				"<a href=", MBThreadConstants.NEW_THREAD_URL, ">",
				MBThreadConstants.NEW_THREAD_URL, "</a>"),
			false);
	}

	public static String getMBMessageURL(
		long messageId, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return StringBundler.concat(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, portletDisplay.getId(),
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/message_boards/view_message"
			).setParameter(
				"messageId", messageId
			).buildString(),
			StringPool.POUND, portletDisplay.getNamespace(), "message_",
			messageId);
	}

	public static String getMBMessageURL(
		long messageId, RenderResponse renderResponse) {

		return StringBundler.concat(
			PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"/message_boards/view_message"
			).setParameter(
				"messageId", messageId
			).buildString(),
			StringPool.POUND, renderResponse.getNamespace(), "message_",
			messageId);
	}

	public static String getMBMessageURL(
		long messageId, String layoutURL, RenderResponse renderResponse) {

		return StringBundler.concat(
			layoutURL, Portal.FRIENDLY_URL_SEPARATOR,
			"message_boards/view_message/", messageId, StringPool.POUND,
			renderResponse.getNamespace(), "message_", messageId);
	}

	public static String[] getThreadPriority(
		MBGroupServiceSettings mbGroupServiceSettings, String languageId,
		double value) {

		String[] priorityPair = _findThreadPriority(
			value, mbGroupServiceSettings.getPriorities(languageId));

		if (priorityPair != null) {
			return priorityPair;
		}

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		return _findThreadPriority(
			value, mbGroupServiceSettings.getPriorities(defaultLanguageId));
	}

	public static boolean isViewableMessage(
			ThemeDisplay themeDisplay, MBMessage message)
		throws Exception {

		return isViewableMessage(themeDisplay, message, message);
	}

	public static boolean isViewableMessage(
			ThemeDisplay themeDisplay, MBMessage message,
			MBMessage parentMessage)
		throws Exception {

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!MBMessagePermission.contains(
				permissionChecker, parentMessage, ActionKeys.VIEW) ||
			((message.getMessageId() != parentMessage.getMessageId()) &&
			 !MBMessagePermission.contains(
				 permissionChecker, message, ActionKeys.VIEW))) {

			return false;
		}

		if (!message.isApproved() &&
			(message.getUserId() != themeDisplay.getUserId()) &&
			!permissionChecker.isContentReviewer(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId())) {

			return false;
		}

		return true;
	}

	private static String[] _findThreadPriority(
		double value, String[] priorities) {

		for (String priority : priorities) {
			String[] priorityArray = StringUtil.split(
				priority, StringPool.PIPE);

			if ((priorityArray == null) || (priorityArray.length < 3)) {
				continue;
			}

			double priorityValue = GetterUtil.getDouble(priorityArray[2]);

			if (value == priorityValue) {
				String priorityName = priorityArray[0];
				String priorityImage = priorityArray[1];

				return new String[] {priorityName, priorityImage};
			}
		}

		return null;
	}

	private static String _getParentAuthor(
		MBMessage parentMessage, HttpServletRequest httpServletRequest) {

		if (parentMessage.isAnonymous()) {
			return LanguageUtil.get(httpServletRequest, "anonymous");
		}

		return HtmlUtil.escape(PortalUtil.getUserName(parentMessage));
	}

}
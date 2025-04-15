/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Eudaldo Alonso
 */
public class JournalArticleCommentsVerticalCard implements VerticalCard {

	public JournalArticleCommentsVerticalCard(
		BaseModel<?> baseModel, RenderRequest renderRequest) {

		_mbMessage = (MBMessage)baseModel;
		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public String getImageSrc() {
		User user = UserLocalServiceUtil.fetchUserById(_mbMessage.getUserId());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (user != null) {
			try {
				return user.getPortraitURL(themeDisplay);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return UserConstants.getPortraitURL(
			themeDisplay.getPathImage(), true, 0, null);
	}

	@Override
	public String getSubtitle() {
		Date createDate = _mbMessage.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - createDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "modified-x-ago", modifiedDateDescription);
	}

	@Override
	public String getTitle() {
		boolean translate = _mbMessage.isFormatBBCode();

		String content = _mbMessage.getBody(translate);

		return HtmlParserUtil.extractText(content);
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleCommentsVerticalCard.class);

	private final HttpServletRequest _httpServletRequest;
	private final MBMessage _mbMessage;

}
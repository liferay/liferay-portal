/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.taglib.servlet.taglib;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;
import com.liferay.user.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Eudaldo Alonso
 */
public class UserPortraitTag extends IncludeTag {

	public static String getUserPortraitHTML(
		String size, User user, ThemeDisplay themeDisplay) {

		String portraitURL = _getPortraitURL(user, themeDisplay);

		if (Validator.isNull(portraitURL)) {
			StringBundler sb = new StringBundler(11);

			sb.append("<span class=\"sticker sticker-circle sticker-light ");

			if (Validator.isNotNull(size)) {
				sb.append(_getSizeCssClass(size));
				sb.append(CharPool.SPACE);
			}

			sb.append("user-icon-color-");
			sb.append((user == null) ? 0 : (user.getUserId() % 10));
			sb.append("\"><span class=\"inline-item\">");
			sb.append("<svg class=\"lexicon-icon\">");
			sb.append("<use href=\"");
			sb.append(themeDisplay.getPathThemeSpritemap());
			sb.append("#user\" /></svg>");
			sb.append("</span></span>");

			return sb.toString();
		}

		StringBundler sb = new StringBundler(6);

		sb.append("<span class=\"rounded-circle sticker sticker-primary");

		if (Validator.isNotNull(size)) {
			sb.append(_getSizeCssClass(size));
		}

		sb.append("\"><span class=\"sticker-overlay\">");
		sb.append("<img alt=\"thumbnail\" class=\"img-fluid\" src=\"");
		sb.append(portraitURL);
		sb.append("\" /></span></span>");

		return sb.toString();
	}

	public String getSize() {
		return _size;
	}

	public User getUser() {
		return _user;
	}

	public long getUserId() {
		if (_user == null) {
			return 0;
		}

		return _user.getUserId();
	}

	@Override
	public int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		jspWriter.write(getUserPortraitHTML(_size, getUser(), themeDisplay));

		return EVAL_PAGE;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setSize(String size) {
		_size = size;
	}

	public void setUser(User user) {
		_user = user;
	}

	public void setUserId(long userId) {
		_user = UserLocalServiceUtil.fetchUser(userId);
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_size = StringPool.BLANK;
		_user = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return false;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
	}

	private static String _getPortraitURL(
		User user, ThemeDisplay themeDisplay) {

		try {
			if ((user == null) || (user.getPortraitId() == 0)) {
				return null;
			}

			return user.getPortraitURL(themeDisplay);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private static String _getSizeCssClass(String size) {
		return "sticker-" + size;
	}

	private static final String _PAGE = "/user_portrait/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		UserPortraitTag.class);

	private String _size = StringPool.BLANK;
	private User _user;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspWriter;

import java.util.function.Supplier;

/**
 * @author Eudaldo Alonso
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.user.taglib.servlet.taglib.UserPortraitTag}
 */
@Deprecated
public class UserPortraitTag extends IncludeTag {

	public static String getUserPortraitHTML(
		String cssClass, String size, User user, ThemeDisplay themeDisplay) {

		String portraitURL = _getPortraitURL(user, themeDisplay);

		if (Validator.isNull(portraitURL)) {
			StringBundler sb = new StringBundler(13);

			sb.append("<span class=\"sticker sticker-circle sticker-light ");

			if (Validator.isNotNull(size)) {
				sb.append(_getSizeCssClass(size));
				sb.append(CharPool.SPACE);
			}

			sb.append("user-icon-color-");
			sb.append((user == null) ? 0 : (user.getUserId() % 10));
			sb.append(CharPool.SPACE);
			sb.append(cssClass);
			sb.append("\"><span class=\"inline-item\">");
			sb.append("<svg class=\"lexicon-icon\">");
			sb.append("<use href=\"");
			sb.append(themeDisplay.getPathThemeSpritemap());
			sb.append("#user\" /></svg>");
			sb.append("</span></span>");

			return sb.toString();
		}

		StringBundler sb = new StringBundler(8);

		sb.append("<span class=\"rounded-circle sticker sticker-primary ");

		if (Validator.isNotNull(size)) {
			sb.append(_getSizeCssClass(size));
			sb.append(CharPool.SPACE);
		}

		sb.append(cssClass);
		sb.append("\"><span class=\"sticker-overlay\">");
		sb.append("<img alt=\"thumbnail\" class=\"img-fluid\" src=\"");
		sb.append(portraitURL);
		sb.append("\" /></span></span>");

		return sb.toString();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replace by {@link
	 *             #getUserPortraitHTML(String, String, User, ThemeDisplay)}
	 */
	@Deprecated
	public static String getUserPortraitHTML(
		String cssClass, Supplier<String> userPortraitURLSupplier) {

		StringBundler sb = new StringBundler(7);

		sb.append("<span class=\"sticker sticker-circle sticker-light ");
		sb.append(cssClass);
		sb.append("\">");
		sb.append("<span class=\"sticker-overlay\">");
		sb.append("<img alt=\"\" class=\"sticker-img\" src=\"");
		sb.append(HtmlUtil.escape(userPortraitURLSupplier.get()));
		sb.append("\"></span></span>");

		return sb.toString();
	}

	public static String getUserPortraitHTML(
		String cssClass, User user, ThemeDisplay themeDisplay) {

		return getUserPortraitHTML(cssClass, null, user, themeDisplay);
	}

	public String getCssClass() {
		return _cssClass;
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

		jspWriter.write(
			getUserPortraitHTML(_cssClass, _size, getUser(), themeDisplay));

		return EVAL_PAGE;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@SuppressWarnings("unused")
	public void setImageCssClass(String imageCssClass) {
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

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	@SuppressWarnings("unused")
	public void setUserName(String userName) {
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = StringPool.BLANK;
		_size = StringPool.BLANK;
		_user = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	protected String getPortraitURL(User user) {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getPortraitURL(user, themeDisplay);
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

	private static final String _PAGE =
		"/html/taglib/ui/user_portrait/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		UserPortraitTag.class);

	private String _cssClass = StringPool.BLANK;
	private String _size = StringPool.BLANK;
	private User _user;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspWriter;

/**
 * @author Balázs Sáfrány-Kovalik
 */
public class DescriptiveNameTag extends IncludeTag {

	@Override
	public void cleanUp() {
		super.cleanUp();

		_group = null;
	}

	public Group getGroup() {
		return _group;
	}

	public void setGroup(Group group) {
		_group = group;
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write(_getDescriptiveName());

		return EVAL_PAGE;
	}

	private String _getDescriptiveName() {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		StringBundler sb = new StringBundler(5);

		try {
			String descriptiveName = HtmlUtil.escape(
				_group.getDescriptiveName(themeDisplay.getLocale()));

			sb.append(descriptiveName);

			if (_group.isStaged() && !_group.isStagedRemotely() &&
				_group.isStagingGroup()) {

				sb.append(StringPool.SPACE);
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(LanguageUtil.get(httpServletRequest, "staging"));
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DescriptiveNameTag.class);

	private Group _group;

}
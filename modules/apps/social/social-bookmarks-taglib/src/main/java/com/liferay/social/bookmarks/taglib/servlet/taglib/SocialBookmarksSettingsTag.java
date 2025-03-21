/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.bookmarks.taglib.servlet.taglib;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.social.bookmarks.taglib.internal.servlet.ServletContextUtil;
import com.liferay.social.bookmarks.taglib.internal.util.SocialBookmarksRegistryUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class SocialBookmarksSettingsTag extends IncludeTag {

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public String getTypes() {
		return StringUtil.merge(_types);
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setTypes(String types) {
		if (types != null) {
			_types = StringUtil.split(types);
		}
		else {
			List<String> allTypes =
				SocialBookmarksRegistryUtil.getSocialBookmarksTypes();

			_types = allTypes.toArray(new String[0]);
		}
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_displayStyle = null;
		_types = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks-settings:displayStyle",
			_displayStyle);
		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks-settings:types", _types);
	}

	private static final String _PAGE = "/bookmarks_settings/page.jsp";

	private String _displayStyle;
	private String[] _types;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.bookmarks.taglib.servlet.taglib;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.social.bookmarks.taglib.internal.servlet.ServletContextUtil;
import com.liferay.social.bookmarks.taglib.internal.util.SocialBookmarksRegistryUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class SocialBookmarksTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		if ((_types != null) && (_types.length == 0)) {
			return EVAL_PAGE;
		}

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		if ((_types != null) && (_types.length == 0)) {
			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public int getMaxInlineItems() {
		return _maxInlineItems;
	}

	public String getTarget() {
		return _target;
	}

	public String getTitle() {
		return _title;
	}

	public String getTypes() {
		return StringUtil.merge(_types);
	}

	public String getUrl() {
		return _url;
	}

	public PortletURL getUrlImpl() {
		return _urlImpl;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setDisplayStyle(String displayStyle) {
		if (Validator.isNull(displayStyle)) {
			_displayStyle = "inline";
		}
		else {
			_displayStyle = displayStyle;
		}
	}

	public void setMaxInlineItems(int maxInlineItems) {
		_maxInlineItems = maxInlineItems;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setTarget(String target) {
		_target = target;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setTypes(String types) {
		if (types != null) {
			_types = StringUtil.split(types);
		}
	}

	public void setUrl(String url) {
		_url = url;
	}

	public void setUrlImpl(PortletURL urlImpl) {
		_urlImpl = urlImpl;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = null;
		_classPK = 0;
		_displayStyle = null;
		_maxInlineItems = 3;
		_target = null;
		_title = null;
		_types = null;
		_url = null;
		_urlImpl = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks:className", _className);
		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks:classPK", _classPK);
		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks:displayStyle", _displayStyle);
		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks:maxInlineItems",
			_maxInlineItems);
		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks:target", _target);
		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks:title", _title);

		String[] types = _types;

		if (types == null) {
			List<String> allTypes =
				SocialBookmarksRegistryUtil.getSocialBookmarksTypes();

			types = allTypes.toArray(new String[0]);
		}

		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks:types", types);

		if ((_url == null) && (_urlImpl != null)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			try {
				Layout layout = themeDisplay.getLayout();

				_url = PortalUtil.getCanonicalURL(
					_urlImpl.toString(), themeDisplay, layout, true, false);

				try {
					_url = PortalUtil.getAlternateURL(
						_url, themeDisplay, themeDisplay.getLocale(), layout);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to get alternate URL " + _urlImpl,
							portalException);
					}
				}
			}
			catch (PortalException portalException) {
				_log.error(
					"Unable to get canonical URL " + _urlImpl, portalException);
			}
		}

		if (_url == null) {
			throw new IllegalArgumentException();
		}

		httpServletRequest.setAttribute(
			"liferay-social-bookmarks:bookmarks:url", _url);
	}

	private static final String _PAGE = "/bookmarks/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		SocialBookmarksTag.class);

	private String _className;
	private long _classPK;
	private String _displayStyle;
	private int _maxInlineItems = 3;
	private String _target;
	private String _title;
	private String[] _types;
	private String _url;
	private PortletURL _urlImpl;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.taglib.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.comment.Discussion;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Charles May
 */
public class DiscussionTag extends IncludeTag {

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public Discussion getDiscussion() {
		return _discussion;
	}

	public String getFormName() {
		return _formName;
	}

	public String getRedirect() {
		return _redirect;
	}

	public long getUserId() {
		return _userId;
	}

	public boolean isAssetEntryVisible() {
		return _assetEntryVisible;
	}

	public boolean isHideControls() {
		return _hideControls;
	}

	public boolean isRatingsEnabled() {
		return _ratingsEnabled;
	}

	public boolean isRefreshPageOnReply() {
		return _refreshPageOnReply;
	}

	public void setAssetEntryVisible(boolean assetEntryVisible) {
		_assetEntryVisible = assetEntryVisible;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setDiscussion(Discussion discussion) {
		_discussion = discussion;
	}

	public void setFormAction(String formAction) {
		_formAction = formAction;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setHideControls(boolean hideControls) {
		_hideControls = hideControls;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(_servletContextSnapshot.get());
	}

	public void setRatingsEnabled(boolean ratingsEnabled) {
		_ratingsEnabled = ratingsEnabled;
	}

	public void setRedirect(String redirect) {
		_redirect = redirect;
	}

	public void setRefreshPageOnReply(boolean refreshPageOnReply) {
		_refreshPageOnReply = refreshPageOnReply;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_assetEntryVisible = true;
		_className = null;
		_classPK = 0;
		_discussion = null;
		_formAction = null;
		_formName = "fm";
		_hideControls = false;
		_ratingsEnabled = true;
		_redirect = null;
		_refreshPageOnReply = false;
		_userId = 0;
	}

	protected String getEditorURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletId = portletDisplay.getId();

		return StringBundler.concat(
			themeDisplay.getPathMain(),
			"/portal/comment/discussion/get_editor?p_l_id=",
			themeDisplay.getPlid(), "&p_p_id=", portletId, "&p_p_isolated=1&",
			"doAsUserId=", URLCodec.encodeURL(themeDisplay.getDoAsUserId()));
	}

	protected String getFormAction(HttpServletRequest httpServletRequest) {
		if (_formAction != null) {
			return _formAction;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletId = portletDisplay.getId();

		return StringBundler.concat(
			themeDisplay.getPathMain(),
			"/portal/comment/discussion/edit?doAsUserId=",
			URLCodec.encodeURL(themeDisplay.getDoAsUserId()), "&p_p_id=",
			portletId, "&p_l_id=", themeDisplay.getPlid());
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	protected String getPaginationURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletId = portletDisplay.getId();

		return StringBundler.concat(
			themeDisplay.getPathMain(),
			"/portal/comment/discussion/get_comments?p_p_isolated=1&",
			"doAsUserId=", URLCodec.encodeURL(themeDisplay.getDoAsUserId()),
			"&p_p_id=", portletId, "&p_l_id=", themeDisplay.getPlid());
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return true;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:assetEntryVisible",
			String.valueOf(_assetEntryVisible));
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:className", _className);
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:classPK", String.valueOf(_classPK));
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:discussion", _discussion);
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:editorURL",
			getEditorURL(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:formAction",
			getFormAction(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:formName", _formName);
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:hideControls",
			String.valueOf(_hideControls));
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:paginationURL",
			getPaginationURL(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:ratingsEnabled",
			String.valueOf(_ratingsEnabled));
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:redirect", _redirect);
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:refreshPageOnReply",
			_refreshPageOnReply);
		httpServletRequest.setAttribute(
			"liferay-comment:discussion:userId", String.valueOf(_userId));
	}

	private static final String _PAGE = "/discussion/page.jsp";

	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			DiscussionTag.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.comment.taglib)");

	private boolean _assetEntryVisible = true;
	private String _className;
	private long _classPK;
	private Discussion _discussion;
	private String _formAction;
	private String _formName = "fm";
	private boolean _hideControls;
	private boolean _ratingsEnabled = true;
	private String _redirect;
	private boolean _refreshPageOnReply;
	private long _userId;

}
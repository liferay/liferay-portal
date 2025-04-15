/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Pavel Savinov
 */
public class SelectAssetDisplayPageTag extends IncludeTag {

	public long getClassNameId() {
		return _classNameId;
	}

	public long getClassPK() {
		return _classPK;
	}

	public long getClassTypeId() {
		return _classTypeId;
	}

	public String getEventName() {
		return _eventName;
	}

	public long getGroupId() {
		return _groupId;
	}

	public long getParentClassPK() {
		return _parentClassPK;
	}

	public boolean isShowPortletLayouts() {
		return _showPortletLayouts;
	}

	public boolean isShowViewInContextLink() {
		return _showViewInContextLink;
	}

	public void setClassNameId(long classNameId) {
		_classNameId = classNameId;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setClassTypeId(long classTypeId) {
		_classTypeId = classTypeId;
	}

	public void setEventName(String eventName) {
		_eventName = eventName;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setParentClassPK(long parentClassPK) {
		_parentClassPK = parentClassPK;
	}

	public void setShowPortletLayouts(boolean showPortletLayouts) {
		_showPortletLayouts = showPortletLayouts;
	}

	public void setShowViewInContextLink(boolean showViewInContextLink) {
		_showViewInContextLink = showViewInContextLink;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_classNameId = 0;
		_classPK = 0;
		_classTypeId = 0;
		_eventName = null;
		_groupId = 0;
		_parentClassPK = 0;
		_showPortletLayouts = false;
		_showViewInContextLink = true;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:classNameId",
			String.valueOf(_classNameId));
		httpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:classPK",
			String.valueOf(_classPK));
		httpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:classTypeId",
			String.valueOf(_classTypeId));
		httpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:eventName", _eventName);
		httpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:groupId",
			String.valueOf(_groupId));
		httpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:parentClassPK",
			String.valueOf(_parentClassPK));
		httpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:showPortletLayouts",
			String.valueOf(_showPortletLayouts));
		httpServletRequest.setAttribute(
			"liferay-asset:select-asset-display-page:showViewInContextLink",
			String.valueOf(_showViewInContextLink));
	}

	private static final String _PAGE = "/select_asset_display_page/page.jsp";

	private long _classNameId;
	private long _classPK;
	private long _classTypeId;
	private String _eventName;
	private long _groupId;
	private long _parentClassPK;
	private boolean _showPortletLayouts;
	private boolean _showViewInContextLink = true;

}
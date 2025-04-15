/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Levente Hudák
 */
public class DefineObjectsTag extends IncludeTag {

	@Override
	public int doStartTag() {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			group = (Group)httpServletRequest.getAttribute(WebKeys.GROUP);
		}

		if (group == null) {
			group = themeDisplay.getScopeGroup();
		}

		if (group == null) {
			return SKIP_BODY;
		}

		pageContext.setAttribute("group", group);
		pageContext.setAttribute("groupId", group.getGroupId());
		pageContext.setAttribute("liveGroup", null);
		pageContext.setAttribute("liveGroupId", 0L);

		Layout layout = themeDisplay.getLayout();

		String privateLayoutString = httpServletRequest.getParameter(
			"privateLayout");

		if (Validator.isNull(privateLayoutString)) {
			privateLayoutString = GetterUtil.getString(
				httpServletRequest.getAttribute(WebKeys.PRIVATE_LAYOUT), null);
		}

		boolean privateLayout = GetterUtil.getBoolean(
			privateLayoutString, layout.isPrivateLayout());

		pageContext.setAttribute("privateLayout", privateLayout);

		pageContext.setAttribute("stagingGroup", null);
		pageContext.setAttribute("stagingGroupId", 0L);

		if (!group.isStaged() && !group.isStagedRemotely() &&
			!group.hasLocalOrRemoteStagingGroup()) {

			return SKIP_BODY;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group.getGroupId());

		pageContext.setAttribute("liveGroup", liveGroup);
		pageContext.setAttribute("liveGroupId", liveGroup.getGroupId());

		Group stagingGroup = null;

		if (!group.hasRemoteStagingGroup() || group.isStagedRemotely()) {
			stagingGroup = StagingUtil.getStagingGroup(group.getGroupId());
		}

		if (stagingGroup != null) {
			pageContext.setAttribute("stagingGroup", stagingGroup);
			pageContext.setAttribute(
				"stagingGroupId", stagingGroup.getGroupId());
		}

		if (Validator.isNotNull(_portletId)) {
			boolean stagedPortlet = liveGroup.isStagedPortlet(_portletId);

			if (group.isStagedRemotely()) {
				stagedPortlet = stagingGroup.isStagedPortlet(_portletId);
			}

			if (stagedPortlet) {
				pageContext.setAttribute("group", stagingGroup);
				pageContext.setAttribute("groupId", stagingGroup.getGroupId());
				pageContext.setAttribute("scopeGroup", stagingGroup);
				pageContext.setAttribute(
					"scopeGroupId", stagingGroup.getGroupId());
			}
		}

		return SKIP_BODY;
	}

	public String getPortletId() {
		return _portletId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	public void setPortletId(String portletId) {
		_portletId = portletId;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_portletId = null;
	}

	private String _portletId;

}
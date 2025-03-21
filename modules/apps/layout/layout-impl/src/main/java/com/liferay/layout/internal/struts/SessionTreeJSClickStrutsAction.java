/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.struts;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionTreeJSClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "path=/portal/session_tree_js_click",
	service = StrutsAction.class
)
public class SessionTreeJSClickStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

			String treeId = ParamUtil.getString(httpServletRequest, "treeId");

			if (cmd.equals("collapse")) {
				SessionTreeJSClicks.closeNodes(httpServletRequest, treeId);
			}
			else if (cmd.equals("expand")) {
				String[] nodeIds = StringUtil.split(
					ParamUtil.getString(httpServletRequest, "nodeIds"));

				SessionTreeJSClicks.openNodes(
					httpServletRequest, treeId, nodeIds);
			}
			else if (cmd.equals("layoutCheck")) {
				long plid = ParamUtil.getLong(httpServletRequest, "plid");
				boolean privateLayout = ParamUtil.getBoolean(
					httpServletRequest, "privateLayout");

				if (plid == LayoutConstants.DEFAULT_PLID) {
					SessionTreeJSClicks.openLayoutNodes(
						httpServletRequest, treeId, privateLayout,
						LayoutConstants.DEFAULT_PLID, true);
				}
				else {
					long layoutId = ParamUtil.getLong(
						httpServletRequest, "layoutId");
					boolean recursive = ParamUtil.getBoolean(
						httpServletRequest, "recursive");

					SessionTreeJSClicks.openLayoutNodes(
						httpServletRequest, treeId, privateLayout, layoutId,
						recursive);
				}
			}
			else if (cmd.equals("layoutCollapse")) {
			}
			else if (cmd.equals("layoutUncheck")) {
				long plid = ParamUtil.getLong(httpServletRequest, "plid");

				if (plid == LayoutConstants.DEFAULT_PLID) {
					SessionTreeJSClicks.closeNodes(httpServletRequest, treeId);
				}
				else {
					boolean privateLayout = ParamUtil.getBoolean(
						httpServletRequest, "privateLayout");
					long layoutId = ParamUtil.getLong(
						httpServletRequest, "layoutId");
					boolean recursive = ParamUtil.getBoolean(
						httpServletRequest, "recursive");

					SessionTreeJSClicks.closeLayoutNodes(
						httpServletRequest, treeId, privateLayout, layoutId,
						recursive);
				}
			}
			else if (cmd.equals("layoutUncollapse")) {
			}
			else {
				String nodeId = ParamUtil.getString(
					httpServletRequest, "nodeId");
				boolean openNode = ParamUtil.getBoolean(
					httpServletRequest, "openNode");

				if (openNode) {
					SessionTreeJSClicks.openNode(
						httpServletRequest, treeId, nodeId);
				}
				else {
					SessionTreeJSClicks.closeNode(
						httpServletRequest, treeId, nodeId);
				}
			}

			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			String json = _getCheckedLayoutIds(httpServletRequest, treeId);

			if (Validator.isNotNull(json)) {
				ServletResponseUtil.write(httpServletResponse, json);
			}
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);
		}

		return null;
	}

	private String _getCheckedLayoutIds(
		HttpServletRequest httpServletRequest, String treeId) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		long[] checkedLayoutIds = StringUtil.split(
			portalPreferences.getValue(
				SessionTreeJSClicks.class.getName(), treeId),
			0L);

		for (long checkedLayoutId : checkedLayoutIds) {
			if (checkedLayoutId == LayoutConstants.DEFAULT_PLID) {
				jsonArray.put(String.valueOf(LayoutConstants.DEFAULT_PLID));
			}

			jsonArray.put(String.valueOf(checkedLayoutId));
		}

		return jsonArray.toString();
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}
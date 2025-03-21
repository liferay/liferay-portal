/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class InputPermissionsParamsTag extends TagSupport {

	public static String doTag(
			String modelName, HttpServletRequest httpServletRequest)
		throws Exception {

		try {
			RenderResponse renderResponse =
				(RenderResponse)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = themeDisplay.getLayout();

			Group layoutGroup = layout.getGroup();

			Group group = themeDisplay.getScopeGroup();

			List<String> supportedActions =
				ResourceActionsUtil.getModelResourceActions(modelName);
			List<String> groupDefaultActions =
				ResourceActionsUtil.getModelResourceGroupDefaultActions(
					modelName);
			List<String> guestDefaultActions =
				ResourceActionsUtil.getModelResourceGuestDefaultActions(
					modelName);
			List<String> guestUnsupportedActions =
				ResourceActionsUtil.getModelResourceGuestUnsupportedActions(
					modelName);

			StringBundler sb = new StringBundler();

			for (String action : supportedActions) {
				boolean groupChecked = groupDefaultActions.contains(action);

				boolean guestChecked = false;

				if (layoutGroup.isControlPanel()) {
					if (!group.hasPrivateLayouts() &&
						guestDefaultActions.contains(action)) {

						guestChecked = true;
					}
				}
				else if (layout.isPublicLayout() &&
						 guestDefaultActions.contains(action)) {

					guestChecked = true;
				}

				boolean guestDisabled = guestUnsupportedActions.contains(
					action);

				if (guestDisabled) {
					guestChecked = false;
				}

				if ((group.isOrganization() || group.isRegularSite()) &&
					groupChecked) {

					sb.append(StringPool.AMPERSAND);
					sb.append(renderResponse.getNamespace());
					sb.append("groupPermissions=");
					sb.append(URLCodec.encodeURL(action));
				}

				if (guestChecked) {
					sb.append(StringPool.AMPERSAND);
					sb.append(renderResponse.getNamespace());
					sb.append("guestPermissions=");
					sb.append(URLCodec.encodeURL(action));
				}
			}

			String inputPermissionsViewRole = getDefaultViewRole(
				modelName, themeDisplay);

			sb.append(StringPool.AMPERSAND);
			sb.append(renderResponse.getNamespace());
			sb.append("inputPermissionsViewRole=");
			sb.append(URLCodec.encodeURL(inputPermissionsViewRole));

			return sb.toString();
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public static String getDefaultViewRole(
			String modelName, ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		List<String> guestDefaultActions =
			ResourceActionsUtil.getModelResourceGuestDefaultActions(modelName);

		if (layout.isTypeControlPanel()) {
			long refererPlid = themeDisplay.getRefererPlid();

			if (refererPlid > 0) {
				Layout refererLayout = LayoutLocalServiceUtil.getLayout(
					refererPlid);

				if (refererLayout.isPublicLayout() &&
					guestDefaultActions.contains(ActionKeys.VIEW)) {

					return RoleConstants.GUEST;
				}
			}
			else {
				Group group = themeDisplay.getScopeGroup();

				if (!group.hasPrivateLayouts() &&
					guestDefaultActions.contains(ActionKeys.VIEW)) {

					return RoleConstants.GUEST;
				}
			}
		}
		else if (layout.isPublicLayout() &&
				 guestDefaultActions.contains(ActionKeys.VIEW)) {

			return RoleConstants.GUEST;
		}

		List<String> groupDefaultActions =
			ResourceActionsUtil.getModelResourceGroupDefaultActions(modelName);

		if (groupDefaultActions.contains(ActionKeys.VIEW)) {
			Group siteGroup = GroupLocalServiceUtil.getGroup(
				themeDisplay.getSiteGroupId());

			Role defaultGroupRole = RoleLocalServiceUtil.getDefaultGroupRole(
				siteGroup.getGroupId());

			return defaultGroupRole.getName();
		}

		return RoleConstants.OWNER;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(_modelName);

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public void setModelName(String modelName) {
		_modelName = modelName;
	}

	private String _modelName;

}
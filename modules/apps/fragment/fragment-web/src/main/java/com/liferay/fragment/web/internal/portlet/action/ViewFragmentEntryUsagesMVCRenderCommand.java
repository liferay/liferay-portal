/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/view_fragment_entry_usages"
	},
	service = MVCRenderCommand.class
)
public class ViewFragmentEntryUsagesMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				ParamUtil.getLong(renderRequest, "fragmentEntryId"));
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if ((fragmentEntry == null) ||
			!_portletResourcePermission.contains(
				themeDisplay.getPermissionChecker(), fragmentEntry.getGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES) ||
			!_isValidGroupId(
				fragmentEntry, ParamUtil.getLong(renderRequest, "groupId"))) {

			SessionErrors.add(renderRequest, NoSuchEntryException.class);

			SessionMessages.add(
				renderRequest,
				_portal.getPortletId(renderRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return "/error.jsp";
		}

		return "/view_fragment_entry_usages.jsp";
	}

	private boolean _isValidGroupId(FragmentEntry fragmentEntry, long groupId) {
		Group fragmentEntryGroup = _groupLocalService.fetchGroup(
			fragmentEntry.getGroupId());

		if (fragmentEntryGroup.isCompany()) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if ((group != null) &&
				(group.getCompanyId() == fragmentEntry.getCompanyId())) {

				return true;
			}

			return false;
		}

		if (DesignLibraryUtil.isDesignLibraryScope(fragmentEntryGroup)) {
			return DesignLibraryUtil.isConnectedDesignLibraryGroupId(
				fragmentEntry.getGroupId(), groupId);
		}

		if ((groupId <= 0) || (groupId == fragmentEntry.getGroupId())) {
			return true;
		}

		return false;
	}

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}
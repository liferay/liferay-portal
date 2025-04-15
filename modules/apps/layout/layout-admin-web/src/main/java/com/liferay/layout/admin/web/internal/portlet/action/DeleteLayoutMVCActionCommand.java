/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.handler.LayoutExceptionRequestHandlerUtil;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.exception.GroupInheritContentException;
import com.liferay.portal.kernel.exception.RequiredLayoutException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.exception.RequiredSegmentsExperienceException;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/delete_layout"
	},
	service = MVCActionCommand.class
)
public class DeleteLayoutMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long selPlid = ParamUtil.getLong(actionRequest, "selPlid");

		long[] selPlids = ParamUtil.getLongValues(actionRequest, "rowIds");

		if ((selPlid > 0) && ArrayUtil.isEmpty(selPlids)) {
			selPlids = new long[] {selPlid};
		}

		for (long curSelPlid : selPlids) {
			_deleteLayout(curSelPlid, actionRequest, actionResponse);
		}

		if (ParamUtil.getBoolean(actionRequest, "hideDefaultSuccessMessage")) {
			hideDefaultSuccessMessage(actionRequest);
		}
	}

	private void _deleteLayout(
			long selPlid, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(selPlid);

		if (layout == null) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if ((layout instanceof VirtualLayout) || !layout.isLayoutDeleteable()) {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			SessionMessages.add(
				actionRequest,
				portletDisplay.getId() +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

			throw new GroupInheritContentException();
		}

		Group group = layout.getGroup();

		if (group.isStagingGroup() &&
			!GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), group,
				ActionKeys.MANAGE_STAGING) &&
			!GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), group,
				ActionKeys.PUBLISH_STAGING)) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getPermissionChecker(), Group.class.getName(),
				group.getGroupId(), ActionKeys.MANAGE_STAGING,
				ActionKeys.PUBLISH_STAGING);
		}

		if (LayoutPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), layout,
				ActionKeys.DELETE)) {

			LayoutType layoutType = layout.getLayoutType();

			EventsProcessorUtil.process(
				PropsKeys.LAYOUT_CONFIGURATION_ACTION_DELETE,
				layoutType.getConfigurationActionDelete(),
				_portal.getHttpServletRequest(actionRequest),
				_portal.getHttpServletResponse(actionResponse));
		}

		if (group.isGuest() && !layout.isPrivateLayout() &&
			layout.isRootLayout()) {

			int count = _layoutLocalService.getLayoutsCount(
				group, false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

			if (count == 1) {
				throw new RequiredLayoutException(
					RequiredLayoutException.AT_LEAST_ONE);
			}
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		long layoutSetBranchId = ParamUtil.getLong(
			actionRequest, "layoutSetBranchId");

		serviceContext.setAttribute("layoutSetBranchId", layoutSetBranchId);

		try {
			_layoutService.deleteLayout(selPlid, serviceContext);

			if (!layout.isDraftLayout()) {
				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"redirectURL",
						() -> {
							String redirect = ParamUtil.getString(
								actionRequest, "redirect");

							if (redirect != null) {
								return redirect;
							}

							return _portal.getControlPanelPortletURL(
								actionRequest,
								LayoutAdminPortletKeys.GROUP_PAGES,
								PortletRequest.RENDER_PHASE);
						}));
			}
		}
		catch (Exception exception) {
			Throwable throwable = exception.getCause();

			if (throwable instanceof
					RequiredSegmentsExperienceException.
						MustNotDeleteSegmentsExperienceReferencedBySegmentsExperiments) {

				SessionErrors.add(actionRequest, throwable.getClass());
			}
			else {
				hideDefaultErrorMessage(actionRequest);

				LayoutExceptionRequestHandlerUtil.handleException(
					actionRequest, actionResponse, exception);
			}
		}
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

}
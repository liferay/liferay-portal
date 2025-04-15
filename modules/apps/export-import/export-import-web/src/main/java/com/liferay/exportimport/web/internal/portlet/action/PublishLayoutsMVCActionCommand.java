/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.exception.LayoutPrototypeException;
import com.liferay.portal.kernel.exception.RemoteOptionsException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.RemoteAuthException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionTreeJSClicks;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT_IMPORT,
		"mvc.command.name=/export_import/publish_layouts"
	},
	service = MVCActionCommand.class
)
public class PublishLayoutsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_FORCE_SEND_REDIRECT);

			hideDefaultSuccessMessage(actionRequest);

			return;
		}

		try {
			if (cmd.equals("copy_from_live")) {
				setLayoutIdMap(actionRequest);

				_staging.copyFromLive(actionRequest);
			}
			else if (cmd.equals(Constants.PUBLISH_TO_LIVE)) {
				hideDefaultSuccessMessage(actionRequest);

				setLayoutIdMap(actionRequest);

				setRedirect(
					actionRequest, actionResponse,
					_staging.publishToLive(actionRequest));
			}
			else if (cmd.equals(Constants.PUBLISH_TO_REMOTE)) {
				hideDefaultSuccessMessage(actionRequest);

				setLayoutIdMap(actionRequest);

				setRedirect(
					actionRequest, actionResponse,
					_staging.publishToRemote(actionRequest));
			}
			else if (cmd.equals("schedule_copy_from_live")) {
				setLayoutIdMap(actionRequest);

				_staging.scheduleCopyFromLive(actionRequest);
			}
			else if (cmd.equals("schedule_publish_to_live")) {
				setLayoutIdMap(actionRequest);

				_staging.schedulePublishToLive(actionRequest);
			}
			else if (cmd.equals("schedule_publish_to_remote")) {
				setLayoutIdMap(actionRequest);

				_staging.schedulePublishToRemote(actionRequest);
			}
			else if (cmd.equals("unschedule_copy_from_live")) {
				_staging.unscheduleCopyFromLive(actionRequest);
			}
			else if (cmd.equals("unschedule_publish_to_live")) {
				_staging.unschedulePublishToLive(actionRequest);
			}
			else if (cmd.equals("unschedule_publish_to_remote")) {
				_staging.unschedulePublishToRemote(actionRequest);
			}

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof AuthException ||
					 exception instanceof DuplicateLockException ||
					 exception instanceof LayoutPrototypeException ||
					 exception instanceof RemoteAuthException ||
					 exception instanceof RemoteExportException ||
					 exception instanceof RemoteOptionsException ||
					 exception instanceof SystemException) {

				if (exception instanceof RemoteAuthException) {
					SessionErrors.add(
						actionRequest, AuthException.class, exception);
				}
				else {
					SessionErrors.add(
						actionRequest, exception.getClass(), exception);
				}

				sendRedirect(actionRequest, actionResponse);
			}
			else {
				throw exception;
			}
		}
	}

	protected void setLayoutIdMap(ActionRequest actionRequest) {
		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");

		String treeId = ParamUtil.getString(actionRequest, "treeId");

		actionRequest.setAttribute(
			"layoutIdMap",
			_exportImportHelper.getSelectedLayoutsJSON(
				groupId, privateLayout,
				SessionTreeJSClicks.getOpenNodes(
					httpServletRequest, treeId + "SelectedNode")));
	}

	protected void setRedirect(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long backgroundTaskId) {

		actionRequest.setAttribute(
			WebKeys.REDIRECT,
			PortletURLBuilder.createRenderURL(
				_portal.getLiferayPortletResponse(actionResponse)
			).setMVCPath(
				"/view_export_import.jsp"
			).setParameter(
				"backgroundTaskId", backgroundTaskId
			).buildString());
	}

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.monitoring.web.internal.portlet.action;

import com.liferay.monitoring.web.internal.constants.MonitoringPortletKeys;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Philip Jones
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MonitoringPortletKeys.MONITORING,
		"mvc.command.name=/monitoring/edit_session"
	},
	service = MVCActionCommand.class
)
public class EditSessionMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin()) {
			SessionErrors.add(
				actionRequest,
				PrincipalException.MustBeCompanyAdmin.class.getName());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		_invalidateSession(actionRequest);

		sendRedirect(actionRequest, actionResponse);
	}

	private static void _invalidateSession(String sessionId) {
		HttpSession userHttpSession = PortalSessionContext.get(sessionId);

		if (userHttpSession != null) {
			boolean eanbled = ClusterInvokeThreadLocal.isEnabled();

			ClusterInvokeThreadLocal.setEnabled(true);

			try {
				userHttpSession.invalidate();
			}
			finally {
				ClusterInvokeThreadLocal.setEnabled(eanbled);
			}
		}
	}

	private void _invalidateSession(ActionRequest actionRequest)
		throws Exception {

		String sessionId = ParamUtil.getString(actionRequest, "sessionId");

		try {
			PortletSession portletSession = actionRequest.getPortletSession();

			String portletSessionId = portletSession.getId();

			if (!portletSessionId.equals(sessionId)) {
				HttpSession userHttpSession = PortalSessionContext.get(
					sessionId);

				if (userHttpSession != null) {
					userHttpSession.invalidate();

					return;
				}

				if (!_clusterExecutor.isEnabled()) {
					return;
				}

				try {
					MethodHandler methodHandler = new MethodHandler(
						_invalidateSessionMethodKey, sessionId);

					ClusterRequest clusterRequest =
						ClusterRequest.createMulticastRequest(
							methodHandler, true);

					clusterRequest.setFireAndForget(true);

					_clusterExecutor.execute(clusterRequest);
				}
				catch (Throwable throwable) {
					_log.error("Unable to notify cluster ", throwable);
				}
			}
		}
		catch (Exception exception) {
			_log.error("Unable to invalidate session", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditSessionMVCActionCommand.class);

	private static final MethodKey _invalidateSessionMethodKey = new MethodKey(
		EditSessionMVCActionCommand.class, "_invalidateSession", String.class);

	@Reference
	private ClusterExecutor _clusterExecutor;

}
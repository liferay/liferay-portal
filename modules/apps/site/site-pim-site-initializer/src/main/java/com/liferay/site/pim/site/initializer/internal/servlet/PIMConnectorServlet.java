/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.servlet;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.constants.DispatchScreenNavigationConstants;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorDispatchTriggerUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.site.pim.site.initializer.internal.servlet.PIMConnectorServlet",
		"osgi.http.whiteboard.servlet.pattern=/pim/connector/*",
		"servlet.init.httpMethods=GET,POST"
	},
	service = Servlet.class
)
public class PIMConnectorServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (PortalSessionThreadLocal.getHttpSession() == null) {
			PortalSessionThreadLocal.setHttpSession(
				httpServletRequest.getSession());
		}

		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			User user = _portal.getUser(httpServletRequest);

			if ((user == null) || user.isGuestUser()) {
				_sendError(
					_language.get(
						httpServletRequest,
						"you-do-not-have-permission-to-access-the-requested-" +
							"resource"),
					httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED);

				return;
			}

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
			PrincipalThreadLocal.setName(user.getUserId());

			ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
				ParamUtil.getLong(httpServletRequest, "objectEntryId"));

			DispatchTrigger dispatchTrigger =
				PIMConnectorDispatchTriggerUtil.getDispatchTrigger(objectEntry);

			String method = httpServletRequest.getMethod();
			String pathInfo = httpServletRequest.getPathInfo();

			if (Objects.equals(method, HttpMethods.POST) &&
				Objects.equals(pathInfo, "/execute")) {

				AuthTokenUtil.checkCSRFToken(
					httpServletRequest, PIMConnectorServlet.class.getName());

				if (!PIMConnectorFieldMappingsUtil.isMappingComplete(
						objectEntry,
						_pimConnectorRegistry.getPIMConnector(
							MapUtil.getString(
								objectEntry.getValues(), "key")))) {

					_sendError(
						_language.get(
							httpServletRequest,
							"a-required-channel-field-is-not-mapped"),
						httpServletResponse,
						HttpServletResponse.SC_BAD_REQUEST);

					return;
				}

				Message message = new Message();

				message.put("companyId", dispatchTrigger.getCompanyId());
				message.setPayload(
					JSONUtil.put(
						"dispatchTriggerId",
						dispatchTrigger.getDispatchTriggerId()
					).toString());

				_messageBus.sendMessage(
					DispatchConstants.EXECUTOR_DESTINATION_NAME, message);

				httpServletResponse.setStatus(
					HttpServletResponse.SC_NO_CONTENT);
			}
			else if (Objects.equals(method, HttpMethods.GET) &&
					 Objects.equals(pathInfo, "/schedule")) {

				Group group = _groupLocalService.getGroup(
					dispatchTrigger.getCompanyId(),
					GroupConstants.CONTROL_PANEL);

				String portletNamespace = _portal.getPortletNamespace(
					DispatchPortletKeys.DISPATCH);

				httpServletResponse.sendRedirect(
					_portal.getSiteAdminURL(
						_portal.getPortalURL(httpServletRequest), group,
						DispatchPortletKeys.DISPATCH,
						HashMapBuilder.put(
							portletNamespace.concat("backURL"),
							new String[] {
								ParamUtil.getString(
									httpServletRequest, "backURL")
							}
						).put(
							portletNamespace.concat("dispatchTriggerId"),
							new String[] {
								String.valueOf(
									dispatchTrigger.getDispatchTriggerId())
							}
						).put(
							portletNamespace.concat("mvcRenderCommandName"),
							new String[] {"/dispatch/edit_dispatch_trigger"}
						).put(
							portletNamespace.concat(
								"screenNavigationCategoryKey"),
							new String[] {
								DispatchScreenNavigationConstants.
									CATEGORY_KEY_DISPATCH_TRIGGER
							}
						).build()));
			}
			else {
				_sendError(
					_language.get(
						httpServletRequest,
						"the-requested-resource-could-not-be-found"),
					httpServletResponse, HttpServletResponse.SC_NOT_FOUND);
			}
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			_sendError(
				_language.get(
					httpServletRequest,
					"you-do-not-have-permission-to-access-the-requested-" +
						"resource"),
				httpServletResponse, HttpServletResponse.SC_FORBIDDEN);
		}
		catch (Exception exception) {
			_log.error(exception);

			_sendError(
				_language.get(
					httpServletRequest, "an-unexpected-error-occurred"),
				httpServletResponse,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	private void _sendError(
			String errorMessage, HttpServletResponse httpServletResponse,
			int status)
		throws IOException {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(status);

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.put(
				"error", errorMessage
			).toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PIMConnectorServlet.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private PIMConnectorRegistry _pimConnectorRegistry;

	@Reference
	private Portal _portal;

}
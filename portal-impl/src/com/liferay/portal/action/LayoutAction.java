/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.SSOUtil;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.constants.ActionConstants;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.LiferayPortletUtil;
import com.liferay.portlet.RenderParametersPool;
import com.liferay.portlet.internal.RenderData;
import com.liferay.portlet.internal.RenderStateUtil;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
public class LayoutAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Boolean layoutDefault = (Boolean)httpServletRequest.getAttribute(
			WebKeys.LAYOUT_DEFAULT);

		if (Boolean.TRUE.equals(layoutDefault)) {
			Layout requestedLayout = (Layout)httpServletRequest.getAttribute(
				WebKeys.REQUESTED_LAYOUT);

			if (requestedLayout != null) {
				String redirectParam = "redirect";

				if (Validator.isNotNull(PropsValues.AUTH_LOGIN_PORTLET_NAME)) {
					String portletNamespace = PortalUtil.getPortletNamespace(
						PropsValues.AUTH_LOGIN_PORTLET_NAME);

					redirectParam = portletNamespace + redirectParam;
				}

				String authLoginURL = SSOUtil.getSignInURL(
					themeDisplay.getCompanyId(), themeDisplay.getURLSignIn());

				if (Validator.isNull(authLoginURL)) {
					authLoginURL = PortalUtil.getSiteLoginURL(themeDisplay);
				}

				if (Validator.isNull(authLoginURL)) {
					authLoginURL = PropsValues.AUTH_LOGIN_URL;
				}

				if (Validator.isNull(authLoginURL)) {
					authLoginURL = PortletURLBuilder.create(
						PortletURLFactoryUtil.create(
							httpServletRequest, PortletKeys.LOGIN,
							PortletRequest.RENDER_PHASE)
					).setMVCRenderCommandName(
						"/login/login"
					).setParameter(
						"saveLastPath", false
					).setPortletMode(
						PortletMode.VIEW
					).setWindowState(
						WindowState.MAXIMIZED
					).buildString();
				}

				authLoginURL = HttpComponentsUtil.setParameter(
					authLoginURL, "p_p_id",
					PropsValues.AUTH_LOGIN_PORTLET_NAME);

				authLoginURL = HttpComponentsUtil.setParameter(
					authLoginURL, redirectParam,
					PortalUtil.getCurrentURL(httpServletRequest));

				if (_log.isDebugEnabled()) {
					_log.debug("Redirect requested layout to " + authLoginURL);
				}

				httpServletResponse.sendRedirect(authLoginURL);
			}
			else {
				String redirect = PortalUtil.getLayoutURL(
					themeDisplay.getLayout(), themeDisplay);

				if (_log.isDebugEnabled()) {
					_log.debug("Redirect default layout to " + redirect);
				}

				httpServletResponse.sendRedirect(redirect);
			}

			return null;
		}

		long plid = ParamUtil.getLong(httpServletRequest, "p_l_id");

		if (_log.isDebugEnabled()) {
			_log.debug("p_l_id is " + plid);
		}

		if (plid > 0) {
			Layout layout = themeDisplay.getLayout();

			if (layout != null) {
				plid = layout.getPlid();
			}

			if (!layout.isTypeLinkToLayout()) {
				return processLayout(
					actionMapping, httpServletRequest, httpServletResponse,
					plid);
			}
		}

		try {
			forwardLayout(httpServletRequest);

			return actionMapping.getActionForward(
				ActionConstants.COMMON_FORWARD_JSP);
		}
		catch (Exception exception) {
			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	protected void forwardLayout(HttpServletRequest httpServletRequest)
		throws Exception {

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		long plid = LayoutConstants.DEFAULT_PLID;

		String layoutFriendlyURL = null;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (layout != null) {
			plid = layout.getPlid();

			layoutFriendlyURL = PortalUtil.getLayoutFriendlyURL(
				layout, themeDisplay);
		}

		String forwardURL = layoutFriendlyURL;

		if (Validator.isNull(forwardURL)) {
			forwardURL =
				themeDisplay.getPathMain() + "/portal/layout?p_l_id=" + plid;
		}

		if (Validator.isNotNull(themeDisplay.getDoAsUserId())) {
			forwardURL = HttpComponentsUtil.addParameter(
				forwardURL, "doAsUserId", themeDisplay.getDoAsUserId());
		}

		if (Validator.isNotNull(themeDisplay.getDoAsUserLanguageId())) {
			forwardURL = HttpComponentsUtil.addParameter(
				forwardURL, "doAsUserLanguageId",
				themeDisplay.getDoAsUserLanguageId());
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Forward layout to " + forwardURL);
		}

		httpServletRequest.setAttribute(WebKeys.FORWARD_URL, forwardURL);
	}

	protected String getRenderStateJSON(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, ThemeDisplay themeDisplay,
			String portletId, LayoutTypePortlet layoutTypePortlet)
		throws Exception {

		Map<String, RenderData> renderDataMap = new HashMap<>();

		List<Portlet> allPortlets = layoutTypePortlet.getAllPortlets();

		for (Portlet curPortlet : allPortlets) {
			String curPortletId = curPortlet.getPortletId();

			if (curPortletId.equals(portletId) ||
				curPortlet.isPartialActionServeResource()) {

				BufferCacheServletResponse bufferCacheServletResponse =
					new BufferCacheServletResponse(httpServletResponse);

				PortletContainerUtil.preparePortlet(
					httpServletRequest, curPortlet);

				PortletContainerUtil.serveResource(
					httpServletRequest, bufferCacheServletResponse, curPortlet);

				RenderData renderData = new RenderData(
					bufferCacheServletResponse.getContentType(),
					bufferCacheServletResponse.getString());

				renderDataMap.put(curPortletId, renderData);
			}
		}

		return RenderStateUtil.generateJSON(
			httpServletRequest, themeDisplay, renderDataMap);
	}

	protected ActionForward processLayout(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long plid)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			Layout layout = themeDisplay.getLayout();

			if ((layout != null) && layout.isTypeURL()) {
				String redirect = PortalUtil.getLayoutActualURL(layout);

				httpServletResponse.sendRedirect(redirect);

				return null;
			}

			Long previousLayoutPlid = (Long)httpSession.getAttribute(
				WebKeys.PREVIOUS_LAYOUT_PLID);

			if ((previousLayoutPlid == null) ||
				(layout.getPlid() != previousLayoutPlid.longValue())) {

				httpSession.setAttribute(
					WebKeys.PREVIOUS_LAYOUT_PLID, layout.getPlid());

				if (themeDisplay.isSignedIn() &&
					PropsValues.
						AUDIT_MESSAGE_COM_LIFERAY_PORTAL_MODEL_LAYOUT_VIEW &&
					AuditRouterUtil.isDeployed()) {

					User realUser = themeDisplay.getRealUser();
					User user = themeDisplay.getUser();

					JSONObject additionalInfoJSONObject = null;

					if (Validator.isNotNull(themeDisplay.getDoAsUserId()) &&
						(realUser.getUserId() != user.getUserId())) {

						additionalInfoJSONObject = JSONUtil.put(
							"doAsUserEmailAddress", user.getEmailAddress()
						).put(
							"doAsUserId", user.getUserId()
						).put(
							"doAsUserName", user.getFullName()
						);
					}

					AuditMessage auditMessage = new AuditMessage(
						ActionKeys.VIEW, realUser.getCompanyId(),
						layout.getGroupId(), realUser.getUserId(),
						realUser.getFullName(), Layout.class.getName(),
						String.valueOf(layout.getPlid()), null, null,
						additionalInfoJSONObject);

					AuditRouterUtil.route(auditMessage);
				}
			}

			boolean resetLayout = ParamUtil.getBoolean(
				httpServletRequest, "p_l_reset",
				PropsValues.LAYOUT_DEFAULT_P_L_RESET);

			String portletId = ParamUtil.getString(
				httpServletRequest, "p_p_id");

			if (resetLayout &&
				(Validator.isNull(portletId) ||
				 ((previousLayoutPlid != null) &&
				  (layout.getPlid() != previousLayoutPlid.longValue())))) {

				// Always clear render parameters on a layout url, but do not
				// clear on portlet urls invoked on the same layout

				RenderParametersPool.clear(httpServletRequest, plid);
			}

			Portlet portlet = null;

			if (Validator.isNotNull(portletId)) {
				portlet = PortletLocalServiceUtil.getPortletById(
					PortalUtil.getCompanyId(httpServletRequest), portletId);
			}

			if (portlet != null) {
				PortletContainerUtil.preparePortlet(
					httpServletRequest, portlet);

				if (themeDisplay.isLifecycleAction()) {
					PortletContainerUtil.processAction(
						httpServletRequest, httpServletResponse, portlet);

					if (httpServletResponse.isCommitted()) {
						return null;
					}

					String renderStateJSON = StringPool.BLANK;

					if (themeDisplay.isHubAction()) {
						renderStateJSON = RenderStateUtil.generateJSON(
							httpServletRequest, themeDisplay);
					}
					else if (themeDisplay.isHubPartialAction()) {
						LayoutTypePortlet layoutTypePortlet =
							themeDisplay.getLayoutTypePortlet();

						if (layoutTypePortlet != null) {
							renderStateJSON = getRenderStateJSON(
								httpServletRequest, httpServletResponse,
								themeDisplay, portlet.getPortletId(),
								layoutTypePortlet);
						}
					}

					if (themeDisplay.isHubAction() ||
						themeDisplay.isHubPartialAction()) {

						httpServletResponse.setContentLength(
							renderStateJSON.length());
						httpServletResponse.setContentType(
							ContentTypes.APPLICATION_JSON);

						PrintWriter printWriter =
							httpServletResponse.getWriter();

						printWriter.write(renderStateJSON);

						return null;
					}
				}
				else if (themeDisplay.isLifecycleResource()) {
					PortletContainerUtil.serveResource(
						httpServletRequest, httpServletResponse, portlet);

					return null;
				}
			}

			if (layout != null) {
				if (themeDisplay.isStateExclusive()) {
					PortletContainerUtil.renderHeaders(
						httpServletRequest, httpServletResponse, portlet);

					PortletContainerUtil.render(
						httpServletRequest, httpServletResponse, portlet);

					return null;
				}

				// Include layout content before the page loads because portlets
				// on the page can set the page title and page subtitle

				PortletContainerUtil.processPublicRenderParameters(
					httpServletRequest, layout, portlet);

				if (layout.includeLayoutContent(
						httpServletRequest, httpServletResponse)) {

					return null;
				}
			}

			return actionMapping.getActionForward("portal.layout");
		}
		catch (Exception exception) {
			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
		finally {
			PortletRequest portletRequest =
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST);

			if (portletRequest != null) {
				LiferayPortletRequest liferayPortletRequest =
					LiferayPortletUtil.getLiferayPortletRequest(portletRequest);

				if (liferayPortletRequest instanceof ResourceRequest) {
					ResourceRequest resourceRequest =
						(ResourceRequest)liferayPortletRequest;

					if (!resourceRequest.isAsyncStarted()) {
						liferayPortletRequest.cleanUp();
					}
				}
				else {
					liferayPortletRequest.cleanUp();
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(LayoutAction.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.change.tracking.CTRequiredModelException;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.GenericPortlet;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class LiferayPortlet extends GenericPortlet {

	@Override
	public void init() throws PortletException {
		super.init();

		addProcessActionSuccessMessage = GetterUtil.getBoolean(
			getInitParameter("add-process-action-success-action"), true);
		alwaysSendRedirect = GetterUtil.getBoolean(
			getInitParameter("always-send-redirect"));
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		try {
			if (!callActionMethod(actionRequest, actionResponse) ||
				!SessionErrors.isEmpty(actionRequest)) {

				return;
			}

			String portletId = PortalUtil.getPortletId(actionRequest);

			if (!SessionMessages.contains(
					actionRequest,
					portletId.concat(
						SessionMessages.KEY_SUFFIX_FORCE_SEND_REDIRECT)) &&
				(isEmptySessionMessages(actionRequest) ||
				 isAlwaysSendRedirect())) {

				sendRedirect(actionRequest, actionResponse);
			}

			if (isAddSuccessMessage(actionRequest)) {
				addSuccessMessage(actionRequest, actionResponse);
			}
		}
		catch (PortletException portletException) {
			Throwable throwable = portletException.getCause();

			if (throwable.getCause() instanceof CTRequiredModelException) {
				throwable = throwable.getCause();

				SessionErrors.add(
					PortalUtil.getHttpServletRequest(actionRequest),
					throwable.getClass(), throwable);
			}
			else if (throwable instanceof CTTransactionException) {
				_log.error(throwable, throwable);

				SessionErrors.add(
					PortalUtil.getHttpServletRequest(actionRequest),
					throwable.getClass(), throwable);
			}
			else if (isSessionErrorException(throwable)) {
				if (_log.isDebugEnabled()) {
					_log.debug(throwable, throwable);
				}

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug(portletException);
				}

				throw portletException;
			}
		}
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		if (!callResourceMethod(resourceRequest, resourceResponse) ||
			!SessionErrors.isEmpty(resourceRequest) ||
			!SessionMessages.isEmpty(resourceRequest)) {

			return;
		}

		super.serveResource(resourceRequest, resourceResponse);
	}

	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		if (!addProcessActionSuccessMessage) {
			return;
		}

		String successMessage = ParamUtil.getString(
			actionRequest, "successMessage");

		SessionMessages.add(actionRequest, "requestProcessed", successMessage);
	}

	protected boolean callActionMethod(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		String actionName = ParamUtil.getString(
			actionRequest, ActionRequest.ACTION_NAME);

		if (Validator.isNull(actionName) ||
			actionName.equals("callActionMethod") ||
			actionName.equals("processAction")) {

			return false;
		}

		try {
			Method method = getActionMethod(actionName);

			method.invoke(this, actionRequest, actionResponse);

			return true;
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchMethodException);
			}

			try {
				super.processAction(actionRequest, actionResponse);

				return true;
			}
			catch (Exception exception) {
				throw new PortletException(exception);
			}
		}
		catch (InvocationTargetException invocationTargetException) {
			Throwable throwable = invocationTargetException.getCause();

			if (throwable != null) {
				throw new PortletException(throwable);
			}

			throw new PortletException(invocationTargetException);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	protected boolean callResourceMethod(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		String actionName = ParamUtil.getString(
			resourceRequest, ActionRequest.ACTION_NAME);

		if (Validator.isNull(actionName) ||
			actionName.equals("callResourceMethod") ||
			actionName.equals("serveResource")) {

			return false;
		}

		try {
			Method method = getResourceMethod(actionName);

			method.invoke(this, resourceRequest, resourceResponse);

			return true;
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchMethodException);
			}

			try {
				super.serveResource(resourceRequest, resourceResponse);

				return true;
			}
			catch (Exception exception) {
				throw new PortletException(exception);
			}
		}
		catch (InvocationTargetException invocationTargetException) {
			Throwable throwable = invocationTargetException.getCause();

			if (throwable != null) {
				throw new PortletException(throwable);
			}

			throw new PortletException(invocationTargetException);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	protected void doAbout(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doAbout method not implemented");
	}

	protected void doConfig(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doConfig method not implemented");
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		WindowState windowState = renderRequest.getWindowState();

		if (windowState.equals(WindowState.MINIMIZED)) {
			return;
		}

		PortletMode portletMode = renderRequest.getPortletMode();

		if (portletMode.equals(PortletMode.VIEW)) {
			doView(renderRequest, renderResponse);
		}
		else if (portletMode.equals(LiferayPortletMode.ABOUT)) {
			doAbout(renderRequest, renderResponse);
		}
		else if (portletMode.equals(LiferayPortletMode.CONFIG)) {
			doConfig(renderRequest, renderResponse);
		}
		else if (portletMode.equals(PortletMode.EDIT)) {
			doEdit(renderRequest, renderResponse);
		}
		else if (portletMode.equals(LiferayPortletMode.EDIT_DEFAULTS)) {
			doEditDefaults(renderRequest, renderResponse);
		}
		else if (portletMode.equals(LiferayPortletMode.EDIT_GUEST)) {
			doEditGuest(renderRequest, renderResponse);
		}
		else if (portletMode.equals(PortletMode.HELP)) {
			doHelp(renderRequest, renderResponse);
		}
		else if (portletMode.equals(LiferayPortletMode.PREVIEW)) {
			doPreview(renderRequest, renderResponse);
		}
		else if (portletMode.equals(LiferayPortletMode.PRINT)) {
			doPrint(renderRequest, renderResponse);
		}
		else {
			throw new PortletException(portletMode.toString());
		}
	}

	protected void doEditDefaults(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doEditDefaults method not implemented");
	}

	protected void doEditGuest(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doEditGuest method not implemented");
	}

	protected void doPreview(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doPreview method not implemented");
	}

	protected void doPrint(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doPrint method not implemented");
	}

	protected Method getActionMethod(String actionName)
		throws NoSuchMethodException {

		Method method = _actionMethods.get(actionName);

		if (method != null) {
			return method;
		}

		Class<?> clazz = getClass();

		method = clazz.getMethod(
			actionName, ActionRequest.class, ActionResponse.class);

		_actionMethods.put(actionName, method);

		return method;
	}

	protected String getRedirect(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String redirect = (String)actionRequest.getAttribute(WebKeys.REDIRECT);

		if (Validator.isBlank(redirect)) {
			redirect = ParamUtil.getString(actionRequest, "redirect");

			if (!Validator.isBlank(redirect)) {
				redirect = PortalUtil.escapeRedirect(redirect);
			}
		}

		return redirect;
	}

	protected Method getResourceMethod(String actionName)
		throws NoSuchMethodException {

		Method method = _resourceMethods.get(actionName);

		if (method != null) {
			return method;
		}

		Class<?> clazz = getClass();

		method = clazz.getMethod(
			actionName, ResourceRequest.class, ResourceResponse.class);

		_resourceMethods.put(actionName, method);

		return method;
	}

	protected ServletContext getServletContext() {
		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)getPortletConfig();

		Portlet portlet = liferayPortletConfig.getPortlet();

		PortletApp portletApp = portlet.getPortletApp();

		return portletApp.getServletContext();
	}

	@Override
	protected String getTitle(RenderRequest renderRequest) {
		try {
			return PortalUtil.getPortletTitle(renderRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return super.getTitle(renderRequest);
		}
	}

	protected boolean isAddSuccessMessage(ActionRequest actionRequest) {
		if (!addProcessActionSuccessMessage) {
			return false;
		}

		String portletId = PortalUtil.getPortletId(actionRequest);

		if (SessionMessages.contains(
				actionRequest,
				portletId.concat(
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE))) {

			return false;
		}

		if (SessionMessages.isEmpty(actionRequest)) {
			return true;
		}

		int sessionMessagesSize = SessionMessages.size(actionRequest);

		for (String suffix : _IGNORED_SESSION_MESSAGE_SUFFIXES) {
			if (SessionMessages.contains(
					actionRequest, portletId.concat(suffix))) {

				sessionMessagesSize--;
			}
		}

		if (sessionMessagesSize == 0) {
			return true;
		}

		return false;
	}

	protected boolean isAlwaysSendRedirect() {
		return alwaysSendRedirect;
	}

	protected boolean isEmptySessionMessages(ActionRequest actionRequest) {
		if (SessionMessages.isEmpty(actionRequest)) {
			return true;
		}

		int sessionMessagesSize = SessionMessages.size(actionRequest);

		String portletId = PortalUtil.getPortletId(actionRequest);

		for (String suffix : _IGNORED_SESSION_MESSAGE_SUFFIXES) {
			if (SessionMessages.contains(
					actionRequest, portletId.concat(suffix))) {

				sessionMessagesSize--;
			}
		}

		if (sessionMessagesSize == 0) {
			return true;
		}

		return false;
	}

	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof DuplicateExternalReferenceCodeException ||
			throwable instanceof PortalException) {

			return true;
		}

		return false;
	}

	protected void sendRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		String redirect = getRedirect(actionRequest, actionResponse);

		if (Validator.isNotNull(redirect)) {
			actionResponse.sendRedirect(redirect);
		}
	}

	protected String translate(PortletRequest portletRequest, String key) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LanguageUtil.get(
			getResourceBundle(themeDisplay.getLocale()), key);
	}

	protected String translate(
		PortletRequest portletRequest, String key, Object... arguments) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LanguageUtil.format(
			getResourceBundle(themeDisplay.getLocale()), key, arguments);
	}

	protected void writeJSON(
			PortletRequest portletRequest, ActionResponse actionResponse,
			Object object)
		throws IOException {

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(actionResponse);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(
			httpServletResponse, _toXSSSafeJSON(object.toString()));

		httpServletResponse.flushBuffer();
	}

	protected void writeJSON(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			Object object)
		throws IOException {

		mimeResponse.setContentType(ContentTypes.APPLICATION_JSON);

		PortletResponseUtil.write(
			mimeResponse, _toXSSSafeJSON(object.toString()));

		mimeResponse.flushBuffer();
	}

	protected boolean addProcessActionSuccessMessage;
	protected boolean alwaysSendRedirect;

	private String _toXSSSafeJSON(String json) {
		return StringUtil.replace(json, CharPool.LESS_THAN, "\\u003c");
	}

	private static final String[] _IGNORED_SESSION_MESSAGE_SUFFIXES = {
		SessionMessages.KEY_SUFFIX_DELETE_SUCCESS_DATA,
		SessionMessages.KEY_SUFFIX_FORCE_SEND_REDIRECT,
		SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE,
		SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE,
		SessionMessages.KEY_SUFFIX_REFRESH_PORTLET
	};

	private static final Log _log = LogFactoryUtil.getLog(LiferayPortlet.class);

	private final Map<String, Method> _actionMethods =
		new ConcurrentHashMap<>();
	private final Map<String, Method> _resourceMethods =
		new ConcurrentHashMap<>();

}
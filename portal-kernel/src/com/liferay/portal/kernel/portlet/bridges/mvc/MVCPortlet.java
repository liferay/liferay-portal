/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.internal.util.ContextResourcePathsUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.portlet.LiferayPortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class MVCPortlet extends LiferayPortlet {

	@Override
	public void destroy() {
		PortletContext portletContext = getPortletContext();

		_validPathsMaps.remove(portletContext.getPortletContextName());

		super.destroy();

		_actionMVCCommandCache.close();
		_headerMVCCommandCache.close();
		_renderMVCCommandCache.close();
		_resourceMVCCommandCache.close();
	}

	@Override
	public void doAbout(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		include(aboutTemplate, renderRequest, renderResponse);
	}

	@Override
	public void doConfig(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		include(configTemplate, renderRequest, renderResponse);
	}

	@Override
	public void doEdit(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		if (portletPreferences == null) {
			super.doEdit(renderRequest, renderResponse);
		}
		else {
			include(editTemplate, renderRequest, renderResponse);
		}
	}

	@Override
	public void doEditDefaults(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		if (portletPreferences == null) {
			super.doEdit(renderRequest, renderResponse);
		}
		else {
			include(editDefaultsTemplate, renderRequest, renderResponse);
		}
	}

	@Override
	public void doEditGuest(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		if (portletPreferences == null) {
			super.doEdit(renderRequest, renderResponse);
		}
		else {
			include(editGuestTemplate, renderRequest, renderResponse);
		}
	}

	@Override
	public void doHelp(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		include(helpTemplate, renderRequest, renderResponse);
	}

	@Override
	public void doPreview(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		include(previewTemplate, renderRequest, renderResponse);
	}

	@Override
	public void doPrint(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		include(printTemplate, renderRequest, renderResponse);
	}

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		include(viewTemplate, renderRequest, renderResponse);
	}

	@Override
	public void init() throws PortletException {
		super.init();

		templatePath = _getInitParameter("template-path");

		if (Validator.isNull(templatePath)) {
			templatePath = StringPool.SLASH;
		}
		else if (templatePath.contains(StringPool.BACK_SLASH) ||
				 templatePath.contains(StringPool.DOUBLE_SLASH) ||
				 templatePath.contains(StringPool.PERIOD) ||
				 templatePath.contains(StringPool.SPACE)) {

			throw new PortletException(
				"template-path " + templatePath + " has invalid characters");
		}
		else if (!templatePath.startsWith(StringPool.SLASH) ||
				 !templatePath.endsWith(StringPool.SLASH)) {

			throw new PortletException(
				"template-path " + templatePath +
					" must start and end with a /");
		}

		aboutTemplate = _getInitParameter("about-template");
		configTemplate = _getInitParameter("config-template");
		editTemplate = _getInitParameter("edit-template");
		editDefaultsTemplate = _getInitParameter("edit-defaults-template");
		editGuestTemplate = _getInitParameter("edit-guest-template");
		helpTemplate = _getInitParameter("help-template");
		previewTemplate = _getInitParameter("preview-template");
		printTemplate = _getInitParameter("print-template");
		viewTemplate = _getInitParameter("view-template");

		clearRequestParameters = GetterUtil.getBoolean(
			getInitParameter("clear-request-parameters"));
		copyRequestParameters = GetterUtil.getBoolean(
			getInitParameter("copy-request-parameters"), true);

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)getPortletConfig();

		String portletId = liferayPortletConfig.getPortletId();

		_actionMVCCommandCache = new MVCCommandCache<>(
			MVCActionCommand.EMPTY,
			getInitParameter("mvc-action-command-package-prefix"),
			getPortletName(), portletId, MVCActionCommand.class,
			"ActionCommand");
		_headerMVCCommandCache = new MVCCommandCache<>(
			MVCHeaderCommand.EMPTY,
			getInitParameter("mvc-header-command-package-prefix"),
			getPortletName(), portletId, MVCHeaderCommand.class,
			"HeaderCommand");
		_renderMVCCommandCache = new MVCCommandCache<>(
			MVCRenderCommand.EMPTY,
			getInitParameter("mvc-render-command-package-prefix"),
			getPortletName(), portletId, MVCRenderCommand.class,
			"RenderCommand");
		_resourceMVCCommandCache = new MVCCommandCache<>(
			MVCResourceCommand.EMPTY,
			getInitParameter("mvc-resource-command-package-prefix"),
			getPortletName(), portletId, MVCResourceCommand.class,
			"ResourceCommand");
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		super.processAction(actionRequest, actionResponse);

		if (copyRequestParameters) {
			PortalUtil.copyRequestParameters(actionRequest, actionResponse);
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		invokeHideDefaultSuccessMessage(renderRequest);

		String mvcRenderCommandName = ParamUtil.getString(
			renderRequest, "mvcRenderCommandName", "/");

		String mvcPath = ParamUtil.getString(renderRequest, "mvcPath");

		if (!mvcRenderCommandName.equals("/") || Validator.isNull(mvcPath)) {
			MVCRenderCommand mvcRenderCommand =
				_renderMVCCommandCache.getMVCCommand(mvcRenderCommandName);

			mvcPath = null;

			if (mvcRenderCommand != MVCRenderCommand.EMPTY) {
				mvcPath = mvcRenderCommand.render(
					renderRequest, renderResponse);
			}

			if (MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH.equals(
					mvcPath)) {

				return;
			}

			if (Validator.isNotNull(mvcPath)) {
				renderRequest.setAttribute(
					getMVCPathAttributeName(renderResponse.getNamespace()),
					mvcPath);
			}
			else if (!mvcRenderCommandName.equals("/")) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"No render mappings found for MVC render command ",
							"name \"", HtmlUtil.escape(mvcRenderCommandName),
							"\" for portlet ",
							renderRequest.getAttribute(WebKeys.PORTLET_ID)));
				}
			}
		}

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void renderHeaders(
			HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws IOException, PortletException {

		PortletConfig portletConfig = getPortletConfig();

		PortletContext portletContext = portletConfig.getPortletContext();

		if (portletContext.getEffectiveMajorVersion() < 3) {
			return;
		}

		String mvcPath = ParamUtil.getString(headerRequest, "mvcPath");
		String mvcRenderCommandName = ParamUtil.getString(
			headerRequest, "mvcRenderCommandName", "/");

		if (mvcRenderCommandName.equals("/") && Validator.isNotNull(mvcPath)) {
			return;
		}

		MVCHeaderCommand mvcHeaderCommand =
			_headerMVCCommandCache.getMVCCommand(mvcRenderCommandName);

		if (mvcHeaderCommand == MVCHeaderCommand.EMPTY) {
			return;
		}

		mvcPath = mvcHeaderCommand.renderHeaders(headerRequest, headerResponse);

		if (Validator.isNotNull(mvcPath) &&
			!MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH.equals(mvcPath)) {

			headerRequest.setAttribute(
				getMVCPathAttributeName(headerResponse.getNamespace()),
				mvcPath);
		}
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		invokeHideDefaultSuccessMessage(resourceRequest);

		String path = getPath(resourceRequest, resourceResponse);

		if (path != null) {
			include(
				path, resourceRequest, resourceResponse,
				PortletRequest.RESOURCE_PHASE);
		}

		super.serveResource(resourceRequest, resourceResponse);
	}

	@Override
	protected boolean callActionMethod(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			checkPermissions(actionRequest);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		String[] actionNames = ParamUtil.getParameterValues(
			actionRequest, ActionRequest.ACTION_NAME);

		String actionName = StringUtil.merge(actionNames);

		if (!actionName.contains(StringPool.COMMA)) {
			MVCActionCommand mvcActionCommand =
				_actionMVCCommandCache.getMVCCommand(actionName);

			if (mvcActionCommand != MVCActionCommand.EMPTY) {
				if (mvcActionCommand instanceof FormMVCActionCommand) {
					FormMVCActionCommand formMVCActionCommand =
						(FormMVCActionCommand)mvcActionCommand;

					if (!formMVCActionCommand.validateForm(
							actionRequest, actionResponse)) {

						return false;
					}
				}

				return mvcActionCommand.processAction(
					actionRequest, actionResponse);
			}
		}
		else {
			List<MVCActionCommand> mvcActionCommands =
				_actionMVCCommandCache.getMVCCommands(actionName);

			if (!mvcActionCommands.isEmpty()) {
				boolean valid = true;

				for (MVCActionCommand mvcActionCommand : mvcActionCommands) {
					if (mvcActionCommand instanceof FormMVCActionCommand) {
						FormMVCActionCommand formMVCActionCommand =
							(FormMVCActionCommand)mvcActionCommand;

						valid &= formMVCActionCommand.validateForm(
							actionRequest, actionResponse);
					}
				}

				if (!valid) {
					return false;
				}

				for (MVCActionCommand mvcActionCommand : mvcActionCommands) {
					if (!mvcActionCommand.processAction(
							actionRequest, actionResponse)) {

						return false;
					}
				}

				return true;
			}
		}

		return super.callActionMethod(actionRequest, actionResponse);
	}

	@Override
	protected boolean callResourceMethod(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			checkPermissions(resourceRequest);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		String resourceID = GetterUtil.getString(
			resourceRequest.getResourceID());

		if (!resourceID.contains(StringPool.COMMA)) {
			MVCResourceCommand mvcResourceCommand =
				_resourceMVCCommandCache.getMVCCommand(resourceID);

			if (mvcResourceCommand != MVCResourceCommand.EMPTY) {
				return mvcResourceCommand.serveResource(
					resourceRequest, resourceResponse);
			}
		}
		else {
			List<MVCResourceCommand> mvcResourceCommands =
				_resourceMVCCommandCache.getMVCCommands(resourceID);

			if (!mvcResourceCommands.isEmpty()) {
				for (MVCResourceCommand mvcResourceCommand :
						mvcResourceCommands) {

					if (!mvcResourceCommand.serveResource(
							resourceRequest, resourceResponse)) {

						return false;
					}
				}

				return true;
			}
		}

		return super.callResourceMethod(resourceRequest, resourceResponse);
	}

	protected void checkPermissions(PortletRequest portletRequest)
		throws Exception {
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		String path = getPath(renderRequest, renderResponse);

		if (path != null) {
			WindowState windowState = renderRequest.getWindowState();

			if (windowState.equals(WindowState.MINIMIZED)) {
				return;
			}

			include(path, renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	protected MVCCommandCache<MVCActionCommand> getActionMVCCommandCache() {
		return _actionMVCCommandCache;
	}

	protected MVCCommandCache<MVCHeaderCommand> getHeaderMVCCommandCache() {
		return _headerMVCCommandCache;
	}

	protected String getMVCPathAttributeName(String namespace) {
		return StringBundler.concat(
			namespace, StringPool.PERIOD,
			MVCRenderConstants.MVC_PATH_REQUEST_ATTRIBUTE_NAME);
	}

	protected String getPath(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String mvcPath = portletRequest.getParameter("mvcPath");

		if (mvcPath == null) {
			mvcPath = (String)portletRequest.getAttribute(
				getMVCPathAttributeName(portletResponse.getNamespace()));
		}

		// Check deprecated parameter

		if (mvcPath == null) {
			mvcPath = portletRequest.getParameter("jspPage");
		}

		return mvcPath;
	}

	protected MVCCommandCache<MVCRenderCommand> getRenderMVCCommandCache() {
		return _renderMVCCommandCache;
	}

	protected MVCCommandCache<MVCResourceCommand> getResourceMVCCommandCache() {
		return _resourceMVCCommandCache;
	}

	protected void hideDefaultErrorMessage(PortletRequest portletRequest) {
		SessionMessages.add(
			portletRequest,
			PortalUtil.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
	}

	protected void hideDefaultSuccessMessage(PortletRequest portletRequest) {
		SessionMessages.add(
			portletRequest,
			PortalUtil.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
	}

	protected void include(
			String path, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws IOException, PortletException {

		include(
			path, actionRequest, actionResponse, PortletRequest.ACTION_PHASE);
	}

	protected void include(
			String path, EventRequest eventRequest, EventResponse eventResponse)
		throws IOException, PortletException {

		include(path, eventRequest, eventResponse, PortletRequest.EVENT_PHASE);
	}

	protected void include(
			String path, PortletRequest portletRequest,
			PortletResponse portletResponse, String lifecycle)
		throws IOException, PortletException {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(portletRequest);

		PortletContext portletContext =
			(PortletContext)httpServletRequest.getAttribute(
				MVCRenderConstants.
					PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
						path);

		if (portletContext == null) {
			portletContext = getPortletContext();
		}

		PortletRequestDispatcher portletRequestDispatcher =
			portletContext.getRequestDispatcher(path);

		if (portletRequestDispatcher == null) {
			_log.error(path + " is not a valid include");
		}
		else {
			Set<String> validPaths = _getValidPaths();

			if (Validator.isNotNull(path) && !validPaths.contains(path) &&
				!validPaths.contains(_PATH_META_INF_RESOURCES.concat(path))) {

				throw new PortletException(
					StringBundler.concat(
						"Path ", path, " is not accessible by portlet ",
						getPortletName()));
			}

			portletRequestDispatcher.include(portletRequest, portletResponse);
		}

		if (clearRequestParameters &&
			lifecycle.equals(PortletRequest.RENDER_PHASE)) {

			portletResponse.setProperty(
				"clear-request-parameters", Boolean.TRUE.toString());
		}
	}

	protected void include(
			String path, RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws IOException, PortletException {

		include(
			path, renderRequest, renderResponse, PortletRequest.RENDER_PHASE);
	}

	protected void include(
			String path, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse)
		throws IOException, PortletException {

		include(
			path, resourceRequest, resourceResponse,
			PortletRequest.RESOURCE_PHASE);
	}

	protected void invokeHideDefaultSuccessMessage(
		PortletRequest portletRequest) {

		boolean hideDefaultSuccessMessage = ParamUtil.getBoolean(
			portletRequest, "hideDefaultSuccessMessage");

		if (hideDefaultSuccessMessage) {
			hideDefaultSuccessMessage(portletRequest);
		}
	}

	protected String aboutTemplate;
	protected boolean clearRequestParameters;
	protected String configTemplate;
	protected boolean copyRequestParameters;
	protected String editDefaultsTemplate;
	protected String editGuestTemplate;
	protected String editTemplate;
	protected String helpTemplate;
	protected String previewTemplate;
	protected String printTemplate;
	protected String templatePath;
	protected String viewTemplate;

	private String _getInitParameter(String name) {
		String value = getInitParameter(name);

		if (value != null) {
			return value;
		}

		// Check deprecated parameter

		if (name.equals("template-path")) {
			return getInitParameter("jsp-path");
		}
		else if (name.endsWith("-template")) {
			name = name.substring(0, name.length() - 9) + "-jsp";

			return getInitParameter(name);
		}

		return null;
	}

	private Set<String> _getJspPaths(String path) {
		PortletContext portletContext = getPortletContext();

		Set<String> paths = _visitResources(portletContext, path, "*.jsp");

		if (paths == null) {
			paths = _visitResources(portletContext, path, "*.jspx");
		}
		else {
			paths.addAll(_visitResources(portletContext, path, "*.jspx"));
		}

		if (paths != null) {
			return paths;
		}

		paths = new HashSet<>();

		Queue<String> queue = new ArrayDeque<>();

		queue.add(path);

		while ((path = queue.poll()) != null) {
			Set<String> childPaths = portletContext.getResourcePaths(path);

			if (childPaths != null) {
				for (String childPath : childPaths) {
					if (childPath.charAt(childPath.length() - 1) ==
							CharPool.SLASH) {

						queue.add(childPath);
					}
					else if (childPath.endsWith(".jsp") ||
							 childPath.endsWith(".jspx")) {

						paths.add(childPath);
					}
				}
			}
		}

		return paths;
	}

	private Set<String> _getValidPaths() {
		if (_validPaths == null) {
			_validPaths = _initValidPaths(templatePath);
		}

		return _validPaths;
	}

	private Set<String> _initValidPaths(String rootPath) {
		PortletContext portletContext = getPortletContext();

		String portletContextName = portletContext.getPortletContextName();

		Map<String, Set<String>> validPathsMap = _validPathsMaps.get(
			portletContextName);

		if (validPathsMap != null) {
			Set<String> validPaths = validPathsMap.get(rootPath);

			if (validPaths != null) {
				return validPaths;
			}
		}
		else {
			validPathsMap = _validPathsMaps.computeIfAbsent(
				portletContextName, key -> new ConcurrentHashMap<>());
		}

		if (rootPath.equals(StringPool.SLASH)) {
			PortletApp portletApp = PortletLocalServiceUtil.getPortletApp(
				portletContextName);

			if (!portletApp.isWARFile()) {
				_log.error(
					StringBundler.concat(
						"Disabling paths for portlet ", getPortletName(),
						" because root path is configured to have access to ",
						"all portal paths"));

				return validPathsMap.computeIfAbsent(
					rootPath, key -> Collections.emptySet());
			}
		}

		return validPathsMap.computeIfAbsent(
			rootPath,
			key -> {
				Set<String> validPaths = _getJspPaths(key);

				if (!key.equals(StringPool.SLASH) &&
					!key.equals("/META-INF/") &&
					!key.equals("/META-INF/resources/")) {

					validPaths.addAll(
						_getJspPaths(_PATH_META_INF_RESOURCES.concat(key)));
				}

				Collections.addAll(
					validPaths,
					StringUtil.split(getInitParameter("valid-paths")));

				return validPaths;
			});
	}

	private Set<String> _visitResources(
		PortletContext portletContext, String path, String pattern) {

		return ContextResourcePathsUtil.visitResources(
			portletContext, path, pattern,
			enumeration -> {
				Set<String> paths = new HashSet<>();

				if (enumeration == null) {
					return paths;
				}

				while (enumeration.hasMoreElements()) {
					URL url = enumeration.nextElement();

					paths.add(url.getPath());
				}

				return paths;
			});
	}

	private static final String _PATH_META_INF_RESOURCES =
		"/META-INF/resources";

	private static final Log _log = LogFactoryUtil.getLog(MVCPortlet.class);

	private static final Map<String, Map<String, Set<String>>> _validPathsMaps =
		new ConcurrentHashMap<>();

	private MVCCommandCache<MVCActionCommand> _actionMVCCommandCache;
	private MVCCommandCache<MVCHeaderCommand> _headerMVCCommandCache;
	private MVCCommandCache<MVCRenderCommand> _renderMVCCommandCache;
	private MVCCommandCache<MVCResourceCommand> _resourceMVCCommandCache;
	private Set<String> _validPaths;

}
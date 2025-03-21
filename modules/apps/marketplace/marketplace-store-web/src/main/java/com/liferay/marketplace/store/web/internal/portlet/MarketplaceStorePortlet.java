/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.store.web.internal.portlet;

import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.marketplace.constants.MarketplaceStorePortletKeys;
import com.liferay.marketplace.model.App;
import com.liferay.marketplace.service.AppLocalService;
import com.liferay.marketplace.service.AppService;
import com.liferay.marketplace.store.web.internal.configuration.MarketplaceStoreWebConfigurationValues;
import com.liferay.marketplace.store.web.internal.constants.MarketplaceConstants;
import com.liferay.marketplace.store.web.internal.constants.MarketplaceStoreWebKeys;
import com.liferay.marketplace.store.web.internal.oauth.util.OAuthManager;
import com.liferay.marketplace.store.web.internal.util.MarketplaceLicenseUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.patcher.PatcherValues;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 * @author Ryan Park
 * @author Joan Kim
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=marketplace-portlet",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/legacy/main.js",
		"com.liferay.portlet.icon=/icons/store.png",
		"com.liferay.portlet.preferences-owned-by-group=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.description=", "jakarta.portlet.display-name=Store",
		"jakarta.portlet.init-param.add-process-action-success-action=false",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + MarketplaceStorePortletKeys.MARKETPLACE_STORE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class MarketplaceStorePortlet extends MVCPortlet {

	public void authorize(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		actionResponse.addProperty(
			LiferayActionResponse.SKIP_ESCAPE_REDIRECT, "true");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OAuthService oAuthService = oAuthManager.getOAuthService();

		Token requestToken = oAuthService.getRequestToken();

		oAuthManager.updateRequestToken(themeDisplay.getUser(), requestToken);

		String redirect = oAuthService.getAuthorizationUrl(requestToken);

		String callbackURL = ParamUtil.getString(actionRequest, "callbackURL");

		redirect = HttpComponentsUtil.addParameter(
			redirect, OAuthConstants.CALLBACK, callbackURL);

		actionResponse.sendRedirect(redirect);
	}

	public void deauthorize(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		oAuthManager.deleteAccessToken(themeDisplay.getUser());

		actionResponse.sendRedirect(
			PortletURLBuilder.createRenderURL(
				portal.getLiferayPortletResponse(actionResponse)
			).setMVCPath(
				"/view.jsp"
			).buildString());
	}

	public void downloadApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long appPackageId = ParamUtil.getLong(actionRequest, "appPackageId");
		boolean unlicensed = ParamUtil.getBoolean(actionRequest, "unlicensed");

		File file = null;

		try {
			file = FileUtil.createTempFile();

			downloadApp(actionRequest, appPackageId, unlicensed, file);

			App app = appService.updateApp(file);

			JSONObject jsonObject = _getAppJSONObject(app.getRemoteAppId());

			jsonObject.put(
				"cmd", "downloadApp"
			).put(
				"message", "success"
			);

			writeJSON(actionRequest, actionResponse, jsonObject);
		}
		finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	public void getApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long remoteAppId = ParamUtil.getLong(actionRequest, "appId");

		JSONObject jsonObject = _getAppJSONObject(remoteAppId);

		jsonObject.put(
			"cmd", "getApp"
		).put(
			"message", "success"
		);

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	public void getInstalledApps(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		writeJSON(
			actionRequest, actionResponse,
			JSONUtil.put(
				"apps", _getInstalledAppsJSONArray()
			).put(
				"cmd", "getInstalledApps"
			).put(
				"message", "success"
			));
	}

	public void getPrepackagedApps(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OAuthRequest oAuthRequest = new OAuthRequest(
			Verb.POST, getServerPortletURL());

		setBaseRequestParameters(actionRequest, oAuthRequest);

		addOAuthParameter(oAuthRequest, "p_p_lifecycle", "1");
		addOAuthParameter(
			oAuthRequest, "p_p_state", WindowState.NORMAL.toString());

		String serverNamespace = getServerNamespace();

		addOAuthParameter(
			oAuthRequest, serverNamespace.concat("compatibility"),
			String.valueOf(ReleaseInfo.getBuildNumber()));
		addOAuthParameter(
			oAuthRequest, serverNamespace.concat("jakarta.portlet.action"),
			"getPrepackagedApps");

		Map<String, String> prepackagedApps =
			appLocalService.getPrepackagedApps();

		JSONObject jsonObject = jsonFactory.createJSONObject();

		Set<String> keys = prepackagedApps.keySet();

		for (String key : keys) {
			jsonObject.put(key, prepackagedApps.get(key));
		}

		addOAuthParameter(
			oAuthRequest, serverNamespace.concat("prepackagedApps"),
			jsonObject.toString());

		Response response = getResponse(themeDisplay.getUser(), oAuthRequest);

		JSONObject responseJSONObject = jsonFactory.createJSONObject(
			response.getBody());

		writeJSON(actionRequest, actionResponse, responseJSONObject);
	}

	public void installApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long remoteAppId = ParamUtil.getLong(actionRequest, "appId");

		appService.installApp(remoteAppId);

		JSONObject jsonObject = _getAppJSONObject(remoteAppId);

		jsonObject.put(
			"cmd", "installApp"
		).put(
			"message", "success"
		);

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		_checkOmniadmin();

		try {
			String actionName = ParamUtil.getString(
				actionRequest, ActionRequest.ACTION_NAME);

			getActionMethod(actionName);

			super.processAction(actionRequest, actionResponse);

			return;
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchMethodException);
			}
		}

		try {
			_remoteProcessAction(actionRequest, actionResponse);
		}
		catch (IOException ioException) {
			throw ioException;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			_checkOmniadmin();

			HttpServletRequest httpServletRequest =
				portal.getHttpServletRequest(renderRequest);

			httpServletRequest = portal.getOriginalServletRequest(
				httpServletRequest);

			String oAuthVerifier = httpServletRequest.getParameter(
				OAuthConstants.VERIFIER);

			if (oAuthVerifier != null) {
				_updateAccessToken(renderRequest, oAuthVerifier);
			}

			String remoteMVCPath = renderRequest.getParameter("remoteMVCPath");

			if (remoteMVCPath != null) {
				_remoteRender(renderRequest, renderResponse);

				return;
			}
		}
		catch (IOException ioException) {
			throw ioException;
		}
		catch (PortletException portletException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portletException);
			}

			include("/error.jsp", renderRequest, renderResponse);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		_checkOmniadmin();

		try {
			_remoteServeResource(resourceRequest, resourceResponse);
		}
		catch (IOException ioException) {
			throw ioException;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	public void uninstallApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long remoteAppId = ParamUtil.getLong(actionRequest, "appId");

		appService.uninstallApp(remoteAppId);

		JSONObject jsonObject = _getAppJSONObject(remoteAppId);

		jsonObject.put(
			"cmd", "uninstallApp"
		).put(
			"message", "success"
		);

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	public void updateApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long appPackageId = ParamUtil.getLong(actionRequest, "appPackageId");
		boolean unlicensed = ParamUtil.getBoolean(actionRequest, "unlicensed");
		String orderUuid = ParamUtil.getString(actionRequest, "orderUuid");
		String productEntryName = ParamUtil.getString(
			actionRequest, "productEntryName");

		File file = null;

		try {
			file = FileUtil.createTempFile();

			downloadApp(actionRequest, appPackageId, unlicensed, file);

			App app = appService.updateApp(file);

			if (Validator.isNull(orderUuid) &&
				Validator.isNotNull(productEntryName)) {

				orderUuid = MarketplaceLicenseUtil.getOrder(productEntryName);
			}

			if (Validator.isNotNull(orderUuid)) {
				MarketplaceLicenseUtil.registerOrder(
					orderUuid, productEntryName);
			}

			appService.installApp(app.getRemoteAppId());

			JSONObject jsonObject = _getAppJSONObject(app.getRemoteAppId());

			jsonObject.put(
				"cmd", "updateApp"
			).put(
				"message", "success"
			);

			writeJSON(actionRequest, actionResponse, jsonObject);
		}
		finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	public void updateAppLicense(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String orderUuid = ParamUtil.getString(actionRequest, "orderUuid");
		String productEntryName = ParamUtil.getString(
			actionRequest, "productEntryName");

		JSONObject jsonObject = JSONUtil.put("cmd", "updateAppLicense");

		if (Validator.isNull(orderUuid) &&
			Validator.isNotNull(productEntryName)) {

			orderUuid = MarketplaceLicenseUtil.getOrder(productEntryName);
		}

		if (Validator.isNotNull(orderUuid)) {
			MarketplaceLicenseUtil.registerOrder(orderUuid, productEntryName);

			jsonObject.put("message", "success");
		}
		else {
			jsonObject.put("message", "failed");
		}

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	public void updateApps(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			"cmd", "updateApps"
		).put(
			"message", "success"
		);

		if (_reentrantLock.tryLock()) {
			try {
				long[] appPackageIds = ParamUtil.getLongValues(
					actionRequest, "appPackageIds");

				JSONArray jsonArray = jsonFactory.createJSONArray();

				for (long appPackageId : appPackageIds) {
					File file = null;

					try {
						file = FileUtil.createTempFile();

						downloadApp(actionRequest, appPackageId, false, file);

						App app = appService.updateApp(file);

						appService.installApp(app.getRemoteAppId());

						jsonArray.put(_getAppJSONObject(app));
					}
					catch (Exception exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}

						jsonObject.put("message", "failed");
					}
					finally {
						if (file != null) {
							file.delete();
						}
					}
				}

				jsonObject.put("updatedApps", jsonArray);
			}
			finally {
				_reentrantLock.unlock();
			}
		}
		else {
			jsonObject.put("message", "failed");
		}

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	@Activate
	protected void activate() {
		oAuthManager = new OAuthManager(expandoValueLocalService);
	}

	protected void addOAuthParameter(
		OAuthRequest oAuthRequest, String key, String value) {

		if (oAuthRequest.getVerb() == Verb.GET) {
			oAuthRequest.addQuerystringParameter(key, value);
		}
		else if (oAuthRequest.getVerb() == Verb.POST) {
			oAuthRequest.addBodyParameter(key, value);
		}
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			Token accessToken = oAuthManager.getAccessToken(
				themeDisplay.getUser());

			if (accessToken == null) {
				include("/login.jsp", renderRequest, renderResponse);

				return;
			}
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		renderRequest.setAttribute(
			MarketplaceStoreWebKeys.OAUTH_AUTHORIZED, Boolean.TRUE);

		super.doDispatch(renderRequest, renderResponse);
	}

	protected void downloadApp(
			PortletRequest portletRequest, long appPackageId,
			boolean unlicensed, File file)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OAuthRequest oAuthRequest = new OAuthRequest(
			Verb.GET, getServerPortletURL());

		setBaseRequestParameters(portletRequest, oAuthRequest);

		String serverNamespace = getServerNamespace();

		addOAuthParameter(
			oAuthRequest, serverNamespace.concat("appPackageId"),
			String.valueOf(appPackageId));

		addOAuthParameter(oAuthRequest, "p_p_lifecycle", "2");

		if (unlicensed) {
			addOAuthParameter(
				oAuthRequest, "p_p_resource_id", "serveUnlicensedApp");
		}
		else {
			addOAuthParameter(oAuthRequest, "p_p_resource_id", "serveApp");
		}

		Response response = getResponse(themeDisplay.getUser(), oAuthRequest);

		FileUtil.write(file, response.getStream());
	}

	protected String getClientPortletId() {
		return MarketplaceStorePortletKeys.MARKETPLACE_STORE;
	}

	protected Response getResponse(User user, OAuthRequest oAuthRequest)
		throws Exception {

		Token token = oAuthManager.getAccessToken(user);

		if (token != null) {
			OAuthService oAuthService = oAuthManager.getOAuthService();

			oAuthService.signRequest(token, oAuthRequest);
		}

		oAuthRequest.setFollowRedirects(false);

		return oAuthRequest.send();
	}

	protected String getServerNamespace() {
		return portal.getPortletNamespace(getServerPortletId());
	}

	protected String getServerPortletId() {
		return MarketplaceStoreWebConfigurationValues.MARKETPLACE_PORTLET_ID;
	}

	protected String getServerPortletURL() {
		return MarketplaceStoreWebConfigurationValues.MARKETPLACE_URL +
			"/osb-portlet/mp_server";
	}

	protected void processPortletParameterMap(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Map<String, String[]> parameterMap) {

		parameterMap.put(
			"clientBuild",
			new String[] {String.valueOf(MarketplaceConstants.CLIENT_BUILD)});

		if (!parameterMap.containsKey("compatibility")) {
			parameterMap.put(
				"compatibility",
				new String[] {String.valueOf(ReleaseInfo.getBuildNumber())});
		}

		parameterMap.put(
			"installedPatches", PatcherValues.INSTALLED_PATCH_NAMES);
		parameterMap.put(
			"supportsHotDeploy", new String[] {Boolean.TRUE.toString()});
	}

	protected void setBaseRequestParameters(
		PortletRequest portletRequest, OAuthRequest oAuthRequest) {

		HttpServletRequest httpServletRequest = portal.getHttpServletRequest(
			portletRequest);

		String clientAuthToken = AuthTokenUtil.getToken(httpServletRequest);

		addOAuthParameter(oAuthRequest, "clientAuthToken", clientAuthToken);

		addOAuthParameter(
			oAuthRequest, "clientPortletId", getClientPortletId());
		addOAuthParameter(
			oAuthRequest, "clientURL",
			portal.getCurrentCompleteURL(httpServletRequest));
		addOAuthParameter(oAuthRequest, "p_p_id", getServerPortletId());
	}

	@Reference
	protected AppLocalService appLocalService;

	@Reference
	protected AppService appService;

	@Reference
	protected ExpandoValueLocalService expandoValueLocalService;

	@Reference
	protected JSONFactory jsonFactory;

	protected OAuthManager oAuthManager;

	@Reference
	protected Portal portal;

	private void _checkOmniadmin() throws PortletException {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			PrincipalException principalException =
				new PrincipalException.MustBeCompanyAdmin(
					permissionChecker.getUserId());

			throw new PortletException(principalException);
		}
	}

	private JSONObject _getAppJSONObject(App app) throws Exception {
		return JSONUtil.put(
			"appId", app.getRemoteAppId()
		).put(
			"downloaded", app.isDownloaded()
		).put(
			"installed", app.isInstalled()
		).put(
			"version", app.getVersion()
		);
	}

	private JSONObject _getAppJSONObject(long remoteAppId) throws Exception {
		App app = appLocalService.fetchRemoteApp(remoteAppId);

		if (app != null) {
			return _getAppJSONObject(app);
		}

		return JSONUtil.put(
			"appId", remoteAppId
		).put(
			"downloaded", false
		).put(
			"installed", false
		).put(
			"version", StringPool.BLANK
		);
	}

	private String _getFileName(String contentDisposition) {
		int pos = contentDisposition.indexOf("filename=\"");

		if (pos == -1) {
			return StringPool.BLANK;
		}

		return contentDisposition.substring(
			pos + 10, contentDisposition.length() - 1);
	}

	private JSONArray _getInstalledAppsJSONArray() throws Exception {
		JSONArray jsonArray = jsonFactory.createJSONArray();

		List<App> apps = appLocalService.getInstalledApps();

		for (App app : apps) {
			if (app.getRemoteAppId() > 0) {
				jsonArray.put(_getAppJSONObject(app));
			}
		}

		return jsonArray;
	}

	private void _remoteProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OAuthRequest oAuthRequest = new OAuthRequest(
			Verb.POST, getServerPortletURL());

		_setRequestParameters(actionRequest, actionResponse, oAuthRequest);

		addOAuthParameter(oAuthRequest, "p_p_lifecycle", "1");
		addOAuthParameter(
			oAuthRequest, "p_p_state", WindowState.NORMAL.toString());

		Response response = getResponse(themeDisplay.getUser(), oAuthRequest);

		if (response.getCode() == HttpServletResponse.SC_FOUND) {
			String redirectLocation = response.getHeader(HttpHeaders.LOCATION);

			actionResponse.sendRedirect(redirectLocation);
		}
		else {
			HttpServletResponse httpServletResponse =
				portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setContentType(
				response.getHeader(HttpHeaders.CONTENT_TYPE));

			ServletResponseUtil.write(
				httpServletResponse, response.getStream());
		}
	}

	private void _remoteRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OAuthRequest oAuthRequest = new OAuthRequest(
			Verb.GET, getServerPortletURL());

		_setRequestParameters(renderRequest, renderResponse, oAuthRequest);

		Response response = getResponse(themeDisplay.getUser(), oAuthRequest);

		renderResponse.setContentType(ContentTypes.TEXT_HTML);

		PrintWriter printWriter = renderResponse.getWriter();

		printWriter.write(response.getBody());
	}

	private void _remoteServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OAuthRequest oAuthRequest = new OAuthRequest(
			Verb.GET, getServerPortletURL());

		_setRequestParameters(resourceRequest, resourceResponse, oAuthRequest);

		addOAuthParameter(oAuthRequest, "p_p_lifecycle", "2");
		addOAuthParameter(
			oAuthRequest, "p_p_resource_id", resourceRequest.getResourceID());

		Response response = getResponse(themeDisplay.getUser(), oAuthRequest);

		String contentType = response.getHeader(HttpHeaders.CONTENT_TYPE);

		if (contentType.startsWith(ContentTypes.APPLICATION_OCTET_STREAM)) {
			String contentDisposition = response.getHeader(
				HttpHeaders.CONTENT_DISPOSITION);
			int contentLength = GetterUtil.getInteger(
				response.getHeader(HttpHeaders.CONTENT_LENGTH));

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				_getFileName(contentDisposition), response.getStream(),
				contentLength, contentType,
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		else {
			resourceResponse.setContentType(contentType);

			PortletResponseUtil.write(resourceResponse, response.getStream());
		}
	}

	private void _setRequestParameters(
		PortletRequest portletRequest, PortletResponse portletResponse,
		OAuthRequest oAuthRequest) {

		setBaseRequestParameters(portletRequest, oAuthRequest);

		Map<String, String[]> parameterMap = new HashMap<>();

		MapUtil.copy(portletRequest.getParameterMap(), parameterMap);

		processPortletParameterMap(
			portletRequest, portletResponse, parameterMap);

		String serverNamespace = getServerNamespace();

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String key = entry.getKey();
			String[] values = entry.getValue();

			if (key.equals("remoteWindowState")) {
				key = "p_p_state";
			}
			else {
				key = serverNamespace.concat(key);
			}

			if (ArrayUtil.isEmpty(values) || Validator.isNull(values[0])) {
				continue;
			}

			addOAuthParameter(oAuthRequest, key, values[0]);
		}
	}

	private void _updateAccessToken(
			RenderRequest renderRequest, String oAuthVerifier)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Token requestToken = oAuthManager.getRequestToken(
			themeDisplay.getUser());

		OAuthService oAuthService = oAuthManager.getOAuthService();

		oAuthManager.updateAccessToken(
			themeDisplay.getUser(),
			oAuthService.getAccessToken(
				requestToken, new Verifier(oAuthVerifier)));

		oAuthManager.deleteRequestToken(themeDisplay.getUser());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MarketplaceStorePortlet.class);

	private final ReentrantLock _reentrantLock = new ReentrantLock();

}
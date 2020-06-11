/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.marketplace.store.web.internal.portlet;

import com.liferay.marketplace.constants.MarketplaceStorePortletKeys;
import com.liferay.marketplace.model.App;
import com.liferay.marketplace.service.AppLocalService;
import com.liferay.marketplace.service.AppService;
import com.liferay.marketplace.store.web.internal.configuration.MarketplaceStoreWebConfigurationValues;
import com.liferay.marketplace.store.web.internal.constants.MarketplaceConstants;
import com.liferay.marketplace.store.web.internal.constants.MarketplaceStoreWebKeys;
import com.liferay.marketplace.store.web.internal.oauth.util.OAuthManager;
import com.liferay.marketplace.store.web.internal.util.MarketplaceLicenseUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.patcher.PatcherUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;

/**
 * @author Ryan Park
 * @author Joan Kim
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.css-class-wrapper=marketplace-portlet",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/main.js",
		"com.liferay.portlet.icon=/icons/store.png",
		"com.liferay.portlet.preferences-owned-by-group=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.description=", "javax.portlet.display-name=Store",
		"javax.portlet.init-param.add-process-action-success-action=false",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + MarketplaceStorePortletKeys.MARKETPLACE_STORE,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=administrator",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class MarketplaceStorePortlet extends RemoteMVCPortlet {

	public void downloadApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long appPackageId = ParamUtil.getLong(actionRequest, "appPackageId");
		boolean unlicensed = ParamUtil.getBoolean(actionRequest, "unlicensed");

		File file = null;

		try {
			file = FileUtil.createTempFile();

			downloadApp(
				actionRequest, actionResponse, appPackageId, unlicensed, file);

			App app = _appService.updateApp(file);

			JSONObject jsonObject = getAppJSONObject(app.getRemoteAppId());

			jsonObject.put("cmd", "downloadApp");
			jsonObject.put("message", "success");

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

		JSONObject jsonObject = getAppJSONObject(remoteAppId);

		jsonObject.put("cmd", "getApp");
		jsonObject.put("message", "success");

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	public void getInstalledApps(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("apps", getInstalledAppsJSONArray());
		jsonObject.put("cmd", "getInstalledApps");
		jsonObject.put("message", "success");

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	public void getPrepackagedApps(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OAuthRequest oAuthRequest = new OAuthRequest(
			Verb.POST, getServerPortletURL());

		setBaseRequestParameters(actionRequest, actionResponse, oAuthRequest);

		addOAuthParameter(oAuthRequest, "p_p_lifecycle", "1");
		addOAuthParameter(
			oAuthRequest, "p_p_state", WindowState.NORMAL.toString());

		String serverNamespace = getServerNamespace();

		addOAuthParameter(
			oAuthRequest, serverNamespace.concat("compatibility"),
			String.valueOf(ReleaseInfo.getBuildNumber()));
		addOAuthParameter(
			oAuthRequest, serverNamespace.concat("javax.portlet.action"),
			"getPrepackagedApps");

		Map<String, String> prepackagedApps =
			_appLocalService.getPrepackagedApps();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Set<String> keys = prepackagedApps.keySet();

		for (String key : keys) {
			jsonObject.put(key, prepackagedApps.get(key));
		}

		addOAuthParameter(
			oAuthRequest, serverNamespace.concat("prepackagedApps"),
			jsonObject.toString());

		Response response = getResponse(themeDisplay.getUser(), oAuthRequest);

		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			response.getBody());

		writeJSON(actionRequest, actionResponse, responseJSONObject);
	}

	public void installApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long remoteAppId = ParamUtil.getLong(actionRequest, "appId");

		_appService.installApp(remoteAppId);

		JSONObject jsonObject = getAppJSONObject(remoteAppId);

		jsonObject.put("cmd", "installApp");
		jsonObject.put("message", "success");

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			super.render(renderRequest, renderResponse);
		}
		catch (PortletException pe) {
			include("/error.jsp", renderRequest, renderResponse);
		}
	}

	public void uninstallApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long remoteAppId = ParamUtil.getLong(actionRequest, "appId");

		_appService.uninstallApp(remoteAppId);

		JSONObject jsonObject = getAppJSONObject(remoteAppId);

		jsonObject.put("cmd", "uninstallApp");
		jsonObject.put("message", "success");

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

			downloadApp(
				actionRequest, actionResponse, appPackageId, unlicensed, file);

			App app = _appService.updateApp(file);

			if (Validator.isNull(orderUuid) &&
				Validator.isNotNull(productEntryName)) {

				orderUuid = MarketplaceLicenseUtil.getOrder(productEntryName);
			}

			if (Validator.isNotNull(orderUuid)) {
				MarketplaceLicenseUtil.registerOrder(
					orderUuid, productEntryName);
			}

			_appService.installApp(app.getRemoteAppId());

			JSONObject jsonObject = getAppJSONObject(app.getRemoteAppId());

			jsonObject.put("cmd", "updateApp");
			jsonObject.put("message", "success");

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

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("cmd", "updateAppLicense");

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

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("cmd", "updateApps");
		jsonObject.put("message", "success");

		if (_reentrantLock.tryLock()) {
			try {
				long[] appPackageIds = ParamUtil.getLongValues(
					actionRequest, "appPackageIds");

				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				for (long appPackageId : appPackageIds) {
					File file = null;

					try {
						file = FileUtil.createTempFile();

						downloadApp(
							actionRequest, actionResponse, appPackageId, false,
							file);

						App app = _appService.updateApp(file);

						_appService.installApp(app.getRemoteAppId());

						jsonArray.put(getAppJSONObject(app));
					}
					catch (Exception e) {
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
		catch (PortalException pe) {
			throw new PortletException(pe);
		}

		renderRequest.setAttribute(
			MarketplaceStoreWebKeys.OAUTH_AUTHORIZED, Boolean.TRUE);

		super.doDispatch(renderRequest, renderResponse);
	}

	protected void downloadApp(
			PortletRequest portletRequest, PortletResponse portletResponse,
			long appPackageId, boolean unlicensed, File file)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		OAuthRequest oAuthRequest = new OAuthRequest(
			Verb.GET, getServerPortletURL());

		setBaseRequestParameters(portletRequest, portletResponse, oAuthRequest);

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

	protected JSONObject getAppJSONObject(App app) throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("appId", app.getRemoteAppId());
		jsonObject.put("downloaded", app.isDownloaded());
		jsonObject.put("installed", app.isInstalled());
		jsonObject.put("version", app.getVersion());

		return jsonObject;
	}

	protected JSONObject getAppJSONObject(long remoteAppId) throws Exception {
		App app = _appLocalService.fetchRemoteApp(remoteAppId);

		if (app != null) {
			return getAppJSONObject(app);
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("appId", remoteAppId);
		jsonObject.put("downloaded", false);
		jsonObject.put("installed", false);
		jsonObject.put("version", StringPool.BLANK);

		return jsonObject;
	}

	@Override
	protected String getClientPortletId() {
		return MarketplaceStorePortletKeys.MARKETPLACE_STORE;
	}

	protected JSONArray getInstalledAppsJSONArray() throws Exception {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<App> apps = _appLocalService.getInstalledApps();

		for (App app : apps) {
			if (app.getRemoteAppId() > 0) {
				jsonArray.put(getAppJSONObject(app));
			}
		}

		return jsonArray;
	}

	@Override
	protected String getServerPortletId() {
		return MarketplaceStoreWebConfigurationValues.MARKETPLACE_PORTLET_ID;
	}

	@Override
	protected String getServerPortletURL() {
		return MarketplaceStoreWebConfigurationValues.MARKETPLACE_URL +
			"/osb-portlet/mp_server";
	}

	@Override
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

		parameterMap.put("installedPatches", PatcherUtil.getInstalledPatches());
		parameterMap.put(
			"supportsHotDeploy",
			new String[] {
				String.valueOf(ServerDetector.isSupportsHotDeploy())
			});
	}

	@Reference(unbind = "-")
	protected void setAppLocalService(AppLocalService appLocalService) {
		_appLocalService = appLocalService;
	}

	@Reference(unbind = "-")
	protected void setAppService(AppService appService) {
		_appService = appService;
	}

	@Override
	@Reference(unbind = "-")
	protected void setOAuthManager(OAuthManager oAuthManager) {
		super.setOAuthManager(oAuthManager);
	}

	private AppLocalService _appLocalService;
	private AppService _appService;
	private final ReentrantLock _reentrantLock = new ReentrantLock();

}
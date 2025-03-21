/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.portlet;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.marketplace.app.manager.web.internal.constants.MarketplaceAppManagerPortletKeys;
import com.liferay.marketplace.app.manager.web.internal.util.BundleUtil;
import com.liferay.marketplace.exception.FileExtensionException;
import com.liferay.marketplace.service.AppService;
import com.liferay.marketplace.util.BundleManagerUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.Plugin;
import com.liferay.portal.kernel.model.PluginSetting;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.PluginSettingLocalService;
import com.liferay.portal.kernel.service.PluginSettingService;
import com.liferay.portal.kernel.service.PortletService;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 * @author Joan Kim
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=marketplace-app-manager-portlet",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/marketplace_app_manager.png",
		"com.liferay.portlet.preferences-owned-by-group=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.description=",
		"jakarta.portlet.display-name=App Manager",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + MarketplaceAppManagerPortletKeys.MARKETPLACE_APP_MANAGER,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = jakarta.portlet.Portlet.class
)
public class MarketplaceAppManagerPortlet extends MVCPortlet {

	public void activateBundles(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] bundleIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "bundleIds"), 0L);

		List<Bundle> bundles = BundleManagerUtil.getInstalledBundles();

		for (Bundle bundle : bundles) {
			if (BundleUtil.isFragment(bundle)) {
				continue;
			}

			if (ArrayUtil.contains(bundleIds, bundle.getBundleId())) {
				bundle.start();
			}
		}
	}

	public void deactivateBundles(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] bundleIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "bundleIds"), 0L);

		List<Bundle> bundles = BundleManagerUtil.getInstalledBundles();

		for (Bundle bundle : bundles) {
			if (BundleUtil.isFragment(bundle)) {
				continue;
			}

			if (ArrayUtil.contains(bundleIds, bundle.getBundleId())) {
				bundle.stop();
			}
		}
	}

	public void installLocalApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		String fileName = GetterUtil.getString(
			uploadPortletRequest.getFileName("file"));

		File file = uploadPortletRequest.getFile("file");

		if (ArrayUtil.isEmpty(FileUtil.getBytes(file))) {
			SessionErrors.add(actionRequest, UploadException.class.getName());
		}
		else if (!fileName.endsWith(".jar") && !fileName.endsWith(".lpkg") &&
				 !fileName.endsWith(".war")) {

			throw new FileExtensionException();
		}
		else {
			String deployDir = PropsUtil.get(PropsKeys.AUTO_DEPLOY_DEPLOY_DIR);

			FileUtil.copyFile(
				file.toString(), deployDir + StringPool.SLASH + fileName);

			SessionMessages.add(actionRequest, "pluginUploaded");
		}

		sendRedirect(actionRequest, actionResponse);
	}

	public void installRemoteApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			String url = ParamUtil.getString(actionRequest, "url");

			URL urlObj = new URL(url);

			String host = urlObj.getHost();

			if (host.endsWith("sf.net") || host.endsWith("sourceforge.net")) {
				_installSourceForgeApp(urlObj.getPath(), actionRequest);
			}
			else {
				_installRemoteApp(url, actionRequest, true);
			}
		}
		catch (MalformedURLException malformedURLException) {
			SessionErrors.add(
				actionRequest, "invalidURL", malformedURLException);
		}
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		_checkOmniadmin();

		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_checkOmniadmin();

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		_checkOmniadmin();

		super.serveResource(resourceRequest, resourceResponse);
	}

	public void uninstallApp(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long remoteAppId = ParamUtil.getLong(actionRequest, "remoteAppId");

		if (remoteAppId > 0) {
			_appService.uninstallApp(remoteAppId);
		}

		SessionMessages.add(actionRequest, "triggeredPortletUndeploy");
	}

	public void uninstallBundles(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] bundleIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "bundleIds"), 0L);

		List<Bundle> bundles = BundleManagerUtil.getInstalledBundles();

		List<String> symbolicNames = new ArrayList<>(bundleIds.length);

		for (Bundle bundle : bundles) {
			if (ArrayUtil.contains(bundleIds, bundle.getBundleId())) {
				symbolicNames.add(bundle.getSymbolicName());
			}
		}

		_bundleBlacklistManager.addToBlacklistAndUninstall(
			symbolicNames.toArray(new String[0]));
	}

	public void updatePluginSetting(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String pluginId = ParamUtil.getString(actionRequest, "pluginId");
		String pluginType = ParamUtil.getString(actionRequest, "pluginType");

		String[] roles = StringUtil.split(
			ParamUtil.getString(actionRequest, "roles"), CharPool.NEW_LINE);

		Arrays.sort(roles);

		boolean active = ParamUtil.getBoolean(actionRequest, "active");

		if (pluginType.equals(Plugin.TYPE_PORTLET)) {
			_portletService.updatePortlet(
				themeDisplay.getCompanyId(), pluginId, StringPool.BLANK,
				active);
		}
		else {
			if (roles.length == 0) {
				PluginSetting pluginSetting =
					_pluginSettingLocalService.getPluginSetting(
						themeDisplay.getCompanyId(), pluginId, pluginType);

				roles = StringUtil.split(pluginSetting.getRoles());
			}

			_pluginSettingService.updatePluginSetting(
				themeDisplay.getCompanyId(), pluginId, pluginType,
				StringUtil.merge(roles), active);
		}
	}

	public void updatePluginSettings(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] contextNames = StringUtil.split(
			ParamUtil.getString(actionRequest, "contextNames"));

		boolean active = ParamUtil.getBoolean(actionRequest, "active");

		for (String contextName : contextNames) {
			ServletContext servletContext = ServletContextPool.get(contextName);

			List<LayoutTemplate> layoutTemplates =
				(List<LayoutTemplate>)servletContext.getAttribute(
					WebKeys.PLUGIN_LAYOUT_TEMPLATES);

			if (layoutTemplates != null) {
				for (LayoutTemplate layoutTemplate : layoutTemplates) {
					PluginSetting pluginSetting =
						_pluginSettingLocalService.getPluginSetting(
							themeDisplay.getCompanyId(),
							layoutTemplate.getLayoutTemplateId(),
							Plugin.TYPE_LAYOUT_TEMPLATE);

					_pluginSettingService.updatePluginSetting(
						themeDisplay.getCompanyId(),
						layoutTemplate.getLayoutTemplateId(),
						Plugin.TYPE_LAYOUT_TEMPLATE, pluginSetting.getRoles(),
						active);
				}
			}

			List<Portlet> portlets = (List<Portlet>)servletContext.getAttribute(
				WebKeys.PLUGIN_PORTLETS);

			if (portlets != null) {
				for (Portlet portlet : portlets) {
					_portletService.updatePortlet(
						themeDisplay.getCompanyId(), portlet.getPortletId(),
						StringPool.BLANK, active);
				}
			}

			List<Theme> themes = (List<Theme>)servletContext.getAttribute(
				WebKeys.PLUGIN_THEMES);

			if (themes != null) {
				for (Theme theme : themes) {
					PluginSetting pluginSetting =
						_pluginSettingLocalService.getPluginSetting(
							themeDisplay.getCompanyId(), theme.getThemeId(),
							Plugin.TYPE_THEME);

					_pluginSettingService.updatePluginSetting(
						themeDisplay.getCompanyId(), theme.getThemeId(),
						Plugin.TYPE_THEME, pluginSetting.getRoles(), active);
				}
			}
		}
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY, _panelAppRegistry);

		PanelCategoryHelper panelCategoryHelper = new PanelCategoryHelper(
			_panelAppRegistry);

		renderRequest.setAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY_HELPER, panelCategoryHelper);

		super.doDispatch(renderRequest, renderResponse);
	}

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

	private int _installRemoteApp(
			String url, ActionRequest actionRequest, boolean failOnError)
		throws Exception {

		int responseCode = HttpServletResponse.SC_OK;

		try {
			Http.Options options = new Http.Options();

			options.setFollowRedirects(false);
			options.setLocation(url);
			options.setPost(false);

			byte[] bytes = _http.URLtoByteArray(options);

			Http.Response response = options.getResponse();

			responseCode = response.getResponseCode();

			if ((responseCode == HttpServletResponse.SC_OK) &&
				(bytes.length > 0)) {

				String deployDir = PropsUtil.get(
					PropsKeys.AUTO_DEPLOY_DEPLOY_DIR);

				String destination =
					deployDir + StringPool.SLASH +
						url.substring(url.lastIndexOf(CharPool.SLASH) + 1);

				File destinationFile = new File(destination);

				FileUtil.write(destinationFile, bytes);

				SessionMessages.add(actionRequest, "pluginDownloaded");
			}
			else {
				if (failOnError) {
					SessionErrors.add(
						actionRequest, UploadException.class.getName());
				}

				responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			}
		}
		catch (MalformedURLException malformedURLException) {
			SessionErrors.add(
				actionRequest, "invalidUrl", malformedURLException);
		}
		catch (IOException ioException) {
			SessionErrors.add(
				actionRequest, "errorConnectingToUrl", ioException);
		}

		return responseCode;
	}

	private void _installSourceForgeApp(
			String path, ActionRequest actionRequest)
		throws Exception {

		String[] sourceForgeMirrors = PropsUtil.getArray(
			PropsKeys.SOURCE_FORGE_MIRRORS);

		for (int i = 0; i < sourceForgeMirrors.length; i++) {
			try {
				String url = sourceForgeMirrors[i] + path;

				boolean failOnError = false;

				if ((i + 1) == sourceForgeMirrors.length) {
					failOnError = true;
				}

				int responseCode = _installRemoteApp(
					url, actionRequest, failOnError);

				if (responseCode == HttpServletResponse.SC_OK) {
					return;
				}
			}
			catch (MalformedURLException malformedURLException) {
				SessionErrors.add(
					actionRequest, "invalidUrl", malformedURLException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MarketplaceAppManagerPortlet.class);

	@Reference
	private AppService _appService;

	private final BundleBlacklistManager _bundleBlacklistManager =
		new BundleBlacklistManager();

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private Http _http;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private PluginSettingLocalService _pluginSettingLocalService;

	@Reference
	private PluginSettingService _pluginSettingService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletService _portletService;

	private class BundleBlacklistManager {

		public void addToBlacklistAndUninstall(String... bundleSymbolicNames)
			throws IOException {

			_updateProperties(
				blacklistBundleSymbolicNames -> {
					if (blacklistBundleSymbolicNames == null) {
						return bundleSymbolicNames;
					}

					Set<String> blacklistBundleSymbolicNamesSet =
						SetUtil.fromArray(blacklistBundleSymbolicNames);

					Collections.addAll(
						blacklistBundleSymbolicNamesSet, bundleSymbolicNames);

					return blacklistBundleSymbolicNamesSet.toArray(
						new String[0]);
				});
		}

		private void _updateConfiguration(
				Configuration configuration,
				Dictionary<String, Object> properties)
			throws IOException {

			Bundle bundle = FrameworkUtil.getBundle(
				BundleBlacklistManager.class);

			BundleContext bundleContext = bundle.getBundleContext();

			CountDownLatch countDownLatch = new CountDownLatch(1);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					ConfigurationListener.class,
					configurationEvent -> {
						if (Objects.equals(
								_BUNDLE_BLACKLIST_CONFIGURATION_PID,
								configurationEvent.getPid())) {

							countDownLatch.countDown();
						}
					},
					null);

			try {
				configuration.update(properties);

				countDownLatch.await();
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException);
				}
			}
			finally {
				serviceRegistration.unregister();
			}
		}

		private void _updateProperties(
				Function<String[], String[]> updateFunction)
			throws IOException {

			Configuration configuration = _configurationAdmin.getConfiguration(
				_BUNDLE_BLACKLIST_CONFIGURATION_PID, StringPool.QUESTION);

			Dictionary<String, Object> properties =
				configuration.getProperties();

			String[] blacklistBundleSymbolicNames = null;

			if (properties == null) {
				properties = new HashMapDictionary<>();
			}
			else {

				// LPS-114840

				Object value = properties.get("blacklistBundleSymbolicNames");

				if (value instanceof String) {
					blacklistBundleSymbolicNames = StringUtil.split(
						(String)value);
				}
				else {
					blacklistBundleSymbolicNames = (String[])properties.get(
						"blacklistBundleSymbolicNames");
				}
			}

			blacklistBundleSymbolicNames = updateFunction.apply(
				blacklistBundleSymbolicNames);

			if (blacklistBundleSymbolicNames == null) {
				return;
			}

			if (blacklistBundleSymbolicNames.length == 0) {
				properties.remove("blacklistBundleSymbolicNames");
			}
			else {
				properties.put(
					"blacklistBundleSymbolicNames",
					blacklistBundleSymbolicNames);
			}

			_updateConfiguration(configuration, properties);
		}

		private static final String _BUNDLE_BLACKLIST_CONFIGURATION_PID =
			"com.liferay.portal.bundle.blacklist.internal.configuration." +
				"BundleBlacklistConfiguration";

	}

}
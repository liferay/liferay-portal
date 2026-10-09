/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.service.impl;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.marketplace.exception.AppPropertiesException;
import com.liferay.marketplace.exception.AppTitleException;
import com.liferay.marketplace.exception.AppVersionException;
import com.liferay.marketplace.model.App;
import com.liferay.marketplace.model.Module;
import com.liferay.marketplace.service.ModuleLocalService;
import com.liferay.marketplace.service.base.AppLocalServiceBaseImpl;
import com.liferay.marketplace.service.persistence.ModulePersistence;
import com.liferay.marketplace.util.BundleManagerUtil;
import com.liferay.marketplace.util.comparator.AppTitleComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.plugin.PluginPackageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 * @author Joan Kim
 */
@Component(
	property = "model.class.name=com.liferay.marketplace.model.App",
	service = AopService.class
)
public class AppLocalServiceImpl extends AppLocalServiceBaseImpl {

	@Override
	public void clearInstalledAppsCache() {
		_installedApps = null;
		_prepackagedApps = null;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public App deleteApp(App app) {

		// App

		clearInstalledAppsCache();

		appPersistence.remove(app);

		// Module

		List<Module> modules = _modulePersistence.findByAppId(app.getAppId());

		for (Module module : modules) {
			_moduleLocalService.deleteModule(module);
		}

		// File

		try {
			_store.deleteDirectory(
				app.getCompanyId(), CompanyConstants.SYSTEM, app.getFilePath());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return app;
	}

	@Override
	public App deleteApp(long appId) throws PortalException {
		App app = appPersistence.findByPrimaryKey(appId);

		return deleteApp(app);
	}

	@Override
	public App fetchRemoteApp(long remoteAppId) {
		return appPersistence.fetchByRemoteAppId(remoteAppId);
	}

	@Override
	public List<App> getApps(String category) {
		return appPersistence.findByCategory(category);
	}

	@Override
	public List<App> getInstalledApps() {
		if (_installedApps != null) {
			return _installedApps;
		}

		List<App> installedApps = new ArrayList<>();

		// Core app

		App coreApp = appPersistence.create(0);

		coreApp.setTitle("Liferay Core");
		coreApp.setDescription("Plugins bundled with Liferay Portal.");
		coreApp.setVersion(ReleaseInfo.getVersion());

		coreApp.addContextName(_portal.getServletContextName());

		installedApps.add(coreApp);

		// Deployed apps

		for (PluginPackage pluginPackage :
				PluginPackageUtil.getInstalledPluginPackages()) {

			List<Module> modules = _modulePersistence.findByContextName(
				pluginPackage.getContext());

			boolean installedApp = false;

			for (Module module : modules) {
				App app = appPersistence.fetchByPrimaryKey(module.getAppId());

				if ((app != null) && app.isInstalled()) {
					installedApp = true;

					break;
				}
			}

			if (installedApp) {
				continue;
			}

			App app = appPersistence.create(0);

			app.setTitle(pluginPackage.getName());
			app.setDescription(pluginPackage.getLongDescription());
			app.setVersion(pluginPackage.getVersion());
			app.setRequired(true);

			app.addContextName(pluginPackage.getContext());

			installedApps.add(app);
		}

		// Marketplace apps

		List<App> apps = appPersistence.findAll();

		for (App app : apps) {
			if (!app.isInstalled()) {
				continue;
			}

			installedApps.add(app);
		}

		installedApps = ListUtil.sort(
			installedApps, AppTitleComparator.getInstance(true));

		_installedApps = installedApps;

		return _installedApps;
	}

	@Override
	public Map<String, String> getPrepackagedApps() {
		if (_prepackagedApps != null) {
			return _prepackagedApps;
		}

		Map<String, String> prepackagedApps = new HashMap<>();

		List<Bundle> bundles = BundleManagerUtil.getInstalledBundles();

		for (Bundle bundle : bundles) {
			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			boolean liferayRelengBundle = GetterUtil.getBoolean(
				headers.get("Liferay-Releng-Bundle"));

			if (!liferayRelengBundle) {
				continue;
			}

			prepackagedApps.put(
				bundle.getSymbolicName(), String.valueOf(bundle.getVersion()));
		}

		_prepackagedApps = prepackagedApps;

		return _prepackagedApps;
	}

	@Override
	public void installApp(long remoteAppId) throws PortalException {
		App app = appPersistence.findByRemoteAppId(remoteAppId);

		if (!_store.hasFile(
				app.getCompanyId(), CompanyConstants.SYSTEM, app.getFilePath(),
				Store.VERSION_DEFAULT)) {

			throw new NoSuchFileException();
		}

		try (InputStream inputStream = _store.getFileAsStream(
				app.getCompanyId(), CompanyConstants.SYSTEM, app.getFilePath(),
				StringPool.BLANK)) {

			if (inputStream == null) {
				throw new IOException(
					"Unable to open file at " + app.getFilePath());
			}

			File file = new File(
				StringBundler.concat(
					SystemProperties.get(SystemProperties.TMP_DIR),
					StringPool.SLASH, _encodeSafeFileName(app.getTitle()),
					StringPool.PERIOD,
					FileUtil.getExtension(app.getFileName())));

			FileUtil.write(file, inputStream);

			BundleManagerUtil.installLPKG(file);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			clearInstalledAppsCache();
		}
	}

	@Override
	public boolean isDownloaded(App app) {
		return _store.hasFile(
			app.getCompanyId(), CompanyConstants.SYSTEM, app.getFilePath(),
			Store.VERSION_DEFAULT);
	}

	@Override
	public void uninstallApp(long remoteAppId) throws PortalException {
		clearInstalledAppsCache();

		App app = appPersistence.findByRemoteAppId(remoteAppId);

		List<Module> modules = _modulePersistence.findByAppId(app.getAppId());

		for (Module module : modules) {
			_moduleLocalService.deleteModule(module.getModuleId());

			if (!module.isBundle()) {
				continue;
			}

			BundleManagerUtil.uninstallBundle(
				module.getBundleSymbolicName(), module.getBundleVersion());
		}
	}

	@Override
	public App updateApp(long userId, File file) throws PortalException {
		Properties properties = _getMarketplaceProperties(file);

		if (properties == null) {
			throw new AppPropertiesException(
				"Unable to read liferay-marketplace.properties");
		}

		long remoteAppId = GetterUtil.getLong(
			properties.getProperty("remote-app-id"));
		String title = properties.getProperty("title");
		String description = properties.getProperty("description");
		String category = properties.getProperty("category");
		String iconURL = properties.getProperty("icon-url");
		String version = properties.getProperty("version");
		boolean required = GetterUtil.getBoolean(
			properties.getProperty("required"));

		return updateApp(
			userId, remoteAppId, title, description, category, iconURL, version,
			required, file);
	}

	@Override
	public App updateApp(
			long userId, long remoteAppId, String title, String description,
			String category, String iconURL, String version, boolean required,
			File file)
		throws PortalException {

		// App

		User user = _userLocalService.fetchUser(userId);
		Date date = new Date();

		_validate(title, version);

		App app = appPersistence.fetchByRemoteAppId(remoteAppId);

		if (app == null) {
			long appId = counterLocalService.increment();

			app = appPersistence.create(appId);
		}

		if (user != null) {
			app.setCompanyId(user.getCompanyId());
			app.setUserId(user.getUserId());
			app.setUserName(user.getFullName());
		}

		app.setCreateDate(date);
		app.setModifiedDate(date);
		app.setRemoteAppId(remoteAppId);
		app.setTitle(title);
		app.setDescription(description);
		app.setCategory(category);
		app.setIconURL(iconURL);
		app.setVersion(version);
		app.setRequired(required);

		app = appPersistence.update(app);

		// File

		if (file != null) {
			try (InputStream inputStream = new FileInputStream(file)) {
				_store.deleteDirectory(
					app.getCompanyId(), CompanyConstants.SYSTEM,
					app.getFilePath());

				_store.addFile(
					app.getCompanyId(), CompanyConstants.SYSTEM,
					app.getFilePath(), Store.VERSION_DEFAULT, inputStream);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		clearInstalledAppsCache();

		return app;
	}

	private String _encodeSafeFileName(String fileName) {
		if (fileName == null) {
			return StringPool.BLANK;
		}

		fileName = FileUtil.encodeSafeFileName(fileName);

		return StringUtil.replace(
			fileName, _SAFE_FILE_NAME_1, _SAFE_FILE_NAME_2);
	}

	private Properties _getMarketplaceProperties(File liferayPackageFile) {
		try (ZipFile zipFile = new ZipFile(liferayPackageFile)) {
			ZipEntry zipEntry = zipFile.getEntry(
				"liferay-marketplace.properties");

			if (zipEntry == null) {
				Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

				ZipEntry subsystemZipEntry = enumeration.nextElement();

				if (!StringUtil.endsWith(
						subsystemZipEntry.getName(), ".lpkg")) {

					return null;
				}

				File file = null;

				try (InputStream subsystemInputStream = zipFile.getInputStream(
						subsystemZipEntry)) {

					file = FileUtil.createTempFile(subsystemInputStream);

					return _getMarketplaceProperties(file);
				}
				finally {
					FileUtil.delete(file);
				}
			}

			try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
				String propertiesString = StringUtil.read(inputStream);

				return PropertiesUtil.load(propertiesString);
			}
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return null;
		}
	}

	private void _validate(String title, String version)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new AppTitleException();
		}

		if (Validator.isNull(version)) {
			throw new AppVersionException();
		}
	}

	/**
	 * @see com.liferay.portal.util.FileImpl#_SAFE_FILE_NAME_1
	 */
	private static final String[] _SAFE_FILE_NAME_1 = {
		StringPool.BACK_SLASH, StringPool.COLON, StringPool.GREATER_THAN,
		StringPool.LESS_THAN, StringPool.PIPE, StringPool.QUESTION,
		StringPool.QUOTE, StringPool.SLASH, StringPool.STAR
	};

	/**
	 * @see com.liferay.portal.util.FileImpl#_SAFE_FILE_NAME_2
	 */
	private static final String[] _SAFE_FILE_NAME_2 = {
		"_BSL_", "_COL_", "_GT_", "_LT_", "_PIP_", "_QUE_", "_QUO_", "_SL_",
		"_ST_"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		AppLocalServiceImpl.class);

	private List<App> _installedApps;

	@Reference
	private ModuleLocalService _moduleLocalService;

	@Reference
	private ModulePersistence _modulePersistence;

	@Reference
	private Portal _portal;

	private Map<String, String> _prepackagedApps;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference
	private UserLocalService _userLocalService;

}
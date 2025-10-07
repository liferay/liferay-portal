/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.ResourceActionsException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogContext;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.patcher.PatcherValues;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.log.UpgradeLogContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Raymond Augé
 */
public class StartupHelperUtil {

	public static void initResourceActions() {
		try {
			ResourceActionLocalServiceUtil.checkResourceActions();
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}

		try {
			ResourceActionsUtil.populateModelResources(
				StartupHelperUtil.class.getClassLoader(),
				PropsValues.RESOURCE_ACTIONS_CONFIGS);
		}
		catch (ResourceActionsException resourceActionsException) {
			ReflectionUtil.throwException(resourceActionsException);
		}
	}

	public static boolean isDBNew() {
		return _dbNew;
	}

	public static boolean isDBWarmed() {
		return _dbWarmedSCLSingleton.getSingleton(
			StartupHelperUtil::_isDBWarmed);
	}

	public static boolean isNewRelease() {
		return _newRelease;
	}

	public static boolean isUpgrading() {
		return _upgrading;
	}

	public static void printPatchLevel() {
		if (_log.isInfoEnabled()) {
			String installedPatches = StringUtil.merge(
				PatcherValues.INSTALLED_PATCH_NAMES,
				StringPool.COMMA_AND_SPACE);

			if (Validator.isNull(installedPatches)) {
				_log.info("There are no patches installed");
			}
			else {
				_log.info(
					"The following patches are installed: " + installedPatches);
			}
		}
	}

	public static void setDBNew(boolean dbNew) {
		if (dbNew != _dbNew) {
			_dbWarmedSCLSingleton.destroy(null);

			_dbNew = dbNew;
		}
	}

	public static void setNewRelease(boolean newRelease) {
		_newRelease = newRelease;
	}

	public static void setUpgrading(boolean upgrading) {
		if (upgrading == _upgrading) {
			return;
		}

		_dbWarmedSCLSingleton.destroy(null);

		_upgrading = upgrading;

		if (upgrading) {
			ThreadLocalCacheManager.disable();

			if (PropsValues.UPGRADE_LOG_CONTEXT_ENABLED) {
				BundleContext bundleContext =
					SystemBundleUtil.getBundleContext();

				_serviceRegistration = bundleContext.registerService(
					LogContext.class, UpgradeLogContext.getInstance(), null);
			}

			DBUpgrader.startUpgradeLogAppender();
		}
		else {
			DBUpgrader.stopUpgradeLogAppender();

			ServiceRegistration<?> serviceRegistration = _serviceRegistration;

			if (serviceRegistration != null) {
				serviceRegistration.unregister();

				_serviceRegistration = null;
			}

			ThreadLocalCacheManager.enable();
		}
	}

	public static void upgradeProcess(int buildNumber) throws UpgradeException {
		List<String> upgradeProcessClassNames = new ArrayList<>();

		if (FeatureFlagManagerUtil.isEnabled("LPS-157670")) {
			Collections.addAll(
				upgradeProcessClassNames,
				"com.liferay.portal.upgrade.UpgradeProcess_6_1_1",
				"com.liferay.portal.upgrade.UpgradeProcess_6_2_0");
		}

		Collections.addAll(
			upgradeProcessClassNames,
			"com.liferay.portal.upgrade.UpgradeProcess_7_0_0",
			"com.liferay.portal.upgrade.UpgradeProcess_7_0_1",
			"com.liferay.portal.upgrade.UpgradeProcess_7_0_3",
			"com.liferay.portal.upgrade.UpgradeProcess_7_0_5",
			"com.liferay.portal.upgrade.UpgradeProcess_7_0_6",
			"com.liferay.portal.upgrade.PortalUpgradeProcess");

		List<UpgradeProcess> upgradeProcesses =
			UpgradeProcessUtil.initUpgradeProcesses(
				PortalClassLoaderUtil.getClassLoader(),
				upgradeProcessClassNames.toArray(new String[0]));

		UpgradeProcessUtil.upgradeProcess(buildNumber, upgradeProcesses);
	}

	public static void verifyRequiredSchemaVersion() throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Check the portal's required schema version");
		}

		try (Connection connection = DataAccess.getConnection()) {
			if (PortalUpgradeProcess.isInRequiredSchemaVersion(connection)) {
				return;
			}

			Version currentSchemaVersion =
				PortalUpgradeProcess.getCurrentSchemaVersion(
					DataAccess.getConnection());

			Version requiredSchemaVersion =
				PortalUpgradeProcess.getRequiredSchemaVersion();

			String msg;

			if (currentSchemaVersion.compareTo(requiredSchemaVersion) < 0) {
				msg =
					"You must first upgrade the portal to the required " +
						"schema version " + requiredSchemaVersion;
			}
			else {
				msg =
					"Current portal schema version " + currentSchemaVersion +
						" requires a newer version of Liferay";
			}

			System.out.println(msg);

			throw new RuntimeException(msg);
		}
	}

	private static boolean _isDBWarmed() {
		boolean dbWarmed = true;

		if (_dbNew || DBUpgrader.isUpgradeDatabaseAutoRunEnabled()) {
			dbWarmed = false;
		}

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		File dataFile = bundleContext.getDataFile("dbWarmed.data");

		if (dbWarmed && dataFile.exists()) {
			try {
				Deserializer deserializer = new Deserializer(
					ByteBuffer.wrap(FileUtil.getBytes(dataFile)));

				if (deserializer.readBoolean()) {
					dbWarmed = false;
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to read DB warmed state", exception);
				}
			}
		}

		Serializer serializer = new Serializer();

		serializer.writeBoolean(_upgrading);

		try (OutputStream outputStream = new FileOutputStream(dataFile)) {
			serializer.writeTo(outputStream);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to write DB warmed state", exception);
			}
		}

		return dbWarmed;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StartupHelperUtil.class);

	private static volatile boolean _dbNew;
	private static final DCLSingleton<Boolean> _dbWarmedSCLSingleton =
		new DCLSingleton<>();
	private static boolean _newRelease;
	private static volatile ServiceRegistration<?> _serviceRegistration;
	private static volatile boolean _upgrading;

}
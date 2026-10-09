/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.deploy.auto;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.deploy.auto.context.AutoDeploymentContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ivica Cardic
 * @author Brian Wing Shun Chan
 */
public class AutoDeployDir {

	public static void deploy(AutoDeploymentContext autoDeploymentContext)
		throws AutoDeployException {

		String[] dirNames = PropsUtil.getArray(
			PropsKeys.MODULE_FRAMEWORK_AUTO_DEPLOY_DIRS);

		if (ArrayUtil.isEmpty(dirNames)) {
			throw new AutoDeployException(
				"The portal property \"" +
					PropsKeys.MODULE_FRAMEWORK_AUTO_DEPLOY_DIRS +
						"\" is not set");
		}

		String dirName = dirNames[0];

		File file = autoDeploymentContext.getFile();

		String fileName = file.getName();

		if (StringUtil.endsWith(fileName, ".cfg")) {
			for (String curDirName : dirNames) {
				if (curDirName.endsWith("/configs")) {
					dirName = curDirName;

					break;
				}
			}
		}
		else if (StringUtil.endsWith(fileName, ".jar") && !_isModule(file)) {
			throw new AutoDeployException(fileName + " is an invalid module");
		}
		else if (StringUtil.endsWith(fileName, ".lpkg")) {
			for (String curDirName : dirNames) {
				if (curDirName.endsWith("/marketplace")) {
					dirName = curDirName;

					break;
				}
			}
		}
		else if (StringUtil.endsWith(fileName, ".war")) {
			for (String curDirName : dirNames) {
				if (curDirName.endsWith("/war")) {
					dirName = curDirName;

					break;
				}
			}

			Matcher matcher = _versionPattern.matcher(fileName);

			if (matcher.find()) {
				fileName = matcher.replaceFirst(".war");
			}
		}
		else {
			for (String curDirName : dirNames) {
				if (curDirName.endsWith("/modules")) {
					dirName = curDirName;

					break;
				}
			}
		}

		FileUtil.move(file, new File(dirName, fileName));
	}

	public static void scanDirectory() {
		File deployDir = new File(PropsValues.AUTO_DEPLOY_DEPLOY_DIR);

		if (!deployDir.exists()) {
			if (_log.isInfoEnabled()) {
				_log.info("Creating missing directory " + deployDir);
			}

			boolean created = deployDir.mkdirs();

			if (!created) {
				_log.error("Directory " + deployDir + " could not be created");
			}

			return;
		}

		File[] files = deployDir.listFiles();

		if (files == null) {
			return;
		}

		Set<String> blacklistedFileNames = _blacklistFileTimestamps.keySet();

		Iterator<String> iterator = blacklistedFileNames.iterator();

		while (iterator.hasNext()) {
			String blacklistedFileName = iterator.next();

			boolean blacklistedFileExists = false;

			for (File file : files) {
				if (!StringUtil.equalsIgnoreCase(
						blacklistedFileName, file.getName())) {

					continue;
				}

				blacklistedFileExists = true;
			}

			if (blacklistedFileExists) {
				continue;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Remove blacklisted file " + blacklistedFileName +
						" because it was deleted");
			}

			iterator.remove();
		}

		for (File file : files) {
			String fileName = file.getName();

			fileName = StringUtil.toLowerCase(fileName);

			if (!file.isFile() ||
				(!fileName.endsWith(".jar") && !fileName.endsWith(".lpkg") &&
				 !fileName.endsWith(".war") && !fileName.endsWith(".xml") &&
				 !fileName.endsWith(".zip"))) {

				continue;
			}

			_processFile(file);
		}
	}

	public static void start() {
		if ((PropsValues.AUTO_DEPLOY_INTERVAL > 0) &&
			((_autoDeployScanner == null) || !_autoDeployScanner.isAlive())) {

			try {
				Thread currentThread = Thread.currentThread();

				_autoDeployScanner = new AutoDeployScanner(
					currentThread.getThreadGroup(),
					AutoDeployScanner.class.getName(),
					PropsValues.AUTO_DEPLOY_INTERVAL);

				_autoDeployScanner.start();

				if (_log.isInfoEnabled()) {
					_log.info(
						"Auto deploy scanner started for " +
							PropsValues.AUTO_DEPLOY_DEPLOY_DIR);
				}
			}
			catch (Exception exception) {
				_log.error(exception);

				stop();
			}
		}
		else {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Auto deploy scanning is disabled for " +
						PropsValues.AUTO_DEPLOY_DEPLOY_DIR);
			}
		}
	}

	public static void stop() {
		if (_autoDeployScanner != null) {
			_autoDeployScanner.pause();
		}
	}

	private static boolean _isModule(File file) throws AutoDeployException {
		Manifest manifest = null;

		try (JarFile jarFile = new JarFile(file)) {
			manifest = jarFile.getManifest();
		}
		catch (IOException ioException) {
			throw new AutoDeployException(ioException);
		}

		if (manifest == null) {
			return false;
		}

		Attributes attributes = manifest.getMainAttributes();

		String bundleSymbolicName = attributes.getValue("Bundle-SymbolicName");

		if (bundleSymbolicName == null) {
			return false;
		}

		int index = bundleSymbolicName.indexOf(CharPool.SEMICOLON);

		if (index != -1) {
			bundleSymbolicName = bundleSymbolicName.substring(0, index);
		}

		return !bundleSymbolicName.isEmpty();
	}

	private static void _processFile(File file) {
		String fileName = file.getName();

		if (!file.canRead()) {
			_log.error("Unable to read " + fileName);

			return;
		}

		if (!file.canWrite()) {
			_log.error("Unable to write " + fileName);

			return;
		}

		if (_blacklistFileTimestamps.containsKey(fileName) &&
			(_blacklistFileTimestamps.get(fileName) == file.lastModified())) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skip processing of ", fileName, " because it is ",
						"blacklisted"));
			}

			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Processing " + fileName);
		}

		try {
			AutoDeploymentContext autoDeploymentContext =
				new AutoDeploymentContext();

			autoDeploymentContext.setFile(file);

			deploy(autoDeploymentContext);

			return;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (_log.isInfoEnabled()) {
			_log.info("Add " + fileName + " to the blacklist");
		}

		_blacklistFileTimestamps.put(fileName, file.lastModified());
	}

	private static final Log _log = LogFactoryUtil.getLog(AutoDeployDir.class);

	private static AutoDeployScanner _autoDeployScanner;
	private static final Map<String, Long> _blacklistFileTimestamps =
		new ConcurrentHashMap<>();
	private static final Pattern _versionPattern = Pattern.compile(
		"-[\\d]+((\\.[\\d]+)+(-.+)*)\\.war$");

}
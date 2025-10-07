/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.tools.DBUpgrader;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Raymond Augé
 */
public class StartupAction extends SimpleAction {

	@Override
	public void run(String[] ids) throws ActionException {
		try {
			doRun(ids);
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	protected void doRun(String[] ids) throws Exception {

		// Check Tomcat's lib/ext directory

		if (ServerDetector.isTomcat()) {
			Path libPath = Paths.get(
				System.getProperty("catalina.base"), "lib");

			Path extPath = libPath.resolve("ext");

			File extDir = extPath.toFile();

			if (extDir.exists()) {
				File[] extJarFiles = extDir.listFiles();

				if (extJarFiles.length != 0) {
					_log.error(
						StringBundler.concat(
							"Files ", Arrays.toString(extJarFiles), " in ",
							extDir, " are no longer read. Move them to ",
							libPath, " or ",
							PropsValues.
								LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR,
							"."));
				}
			}
		}

		// Print release information

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"com/liferay/portal/events/dependencies/startup.txt")) {

			System.out.println(StreamUtil.toString(inputStream));
		}

		System.out.println("Starting " + ReleaseInfo.getReleaseInfo() + "\n");

		StartupHelperUtil.printPatchLevel();

		// MySQL version

		DB db = DBManagerUtil.getDB();

		if ((db.getDBType() == DBType.MYSQL) &&
			(GetterUtil.getFloat(db.getVersionString()) < 5.6F)) {

			_log.error(
				"Please upgrade to at least MySQL 5.6.4. The portal no " +
					"longer supports older versions of MySQL.");

			System.exit(1);
		}

		// Check required schema version

		StartupHelperUtil.verifyRequiredSchemaVersion();

		DBUpgrader.checkReleaseState();

		// Check resource actions

		if (_log.isDebugEnabled()) {
			_log.debug("Check resource actions");
		}

		StartupHelperUtil.initResourceActions();

		if (StartupHelperUtil.isDBNew()) {
			DBUpgrader.updatePortalServiceComponent();

			DLFileEntryTypeLocalServiceUtil.getBasicDocumentDLFileEntryType();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(StartupAction.class);

}
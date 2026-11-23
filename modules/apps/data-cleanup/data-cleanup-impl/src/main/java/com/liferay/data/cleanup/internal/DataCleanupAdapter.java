/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal;

import com.liferay.data.cleanup.DataCleanup;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.verify.VerifyProcess;

/**
 * @author Maríano Álvaro Sáiz
 */
public class DataCleanupAdapter {

	public static DataCleanup create(
		String helpLabel, String label, String servletContextName, String type,
		UpgradeProcess upgradeProcess) {

		return _create(
			helpLabel, label, servletContextName, type,
			upgradeProcess::upgrade);
	}

	public static DataCleanup create(
		String label, String servletContextName, String type,
		UpgradeProcess upgradeProcess) {

		return _create(
			label + "-help", label, servletContextName, type,
			upgradeProcess::upgrade);
	}

	public static DataCleanup create(
		String label, String servletContextName, String type,
		VerifyProcess verifyProcess) {

		return _create(
			label + "-help", label, servletContextName, type,
			verifyProcess::verify);
	}

	private static DataCleanup _create(
		String helpLabel, String label, String servletContextName, String type,
		UnsafeRunnable<Exception> unsafeRunnable) {

		return new DataCleanup() {

			@Override
			public String getHelpLabel() {
				return helpLabel;
			}

			@Override
			public String getLabel() {
				return label;
			}

			@Override
			public String getServletContextName() {
				return servletContextName;
			}

			@Override
			public String getType() {
				return type;
			}

			@Override
			protected void doCleanup() throws Exception {
				unsafeRunnable.run();
			}

		};
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.util;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.function.Predicate;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleRevision;

/**
 * @author Luis Ortiz
 */
public class PostUpgradeDataCleanupProcessUtil {

	public static boolean isEveryLiferayBundleActive() {
		return _isEveryLiferayBundle(
			PostUpgradeDataCleanupProcessUtil::_isActive);
	}

	public static boolean isEveryLiferayBundleResolved() {
		return _isEveryLiferayBundle(
			PostUpgradeDataCleanupProcessUtil::_isResolved);
	}

	private static boolean _isActive(Bundle bundle) {
		int state = bundle.getState();

		if ((state == Bundle.ACTIVE) ||
			((state == Bundle.RESOLVED) && _isFragment(bundle))) {

			return true;
		}

		return false;
	}

	private static boolean _isEveryLiferayBundle(Predicate<Bundle> predicate) {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			String bundleSymbolicName = bundle.getSymbolicName();

			if (bundleSymbolicName.startsWith("com.liferay.") &&
				!predicate.test(bundle)) {

				return false;
			}
		}

		return true;
	}

	private static boolean _isFragment(Bundle bundle) {
		BundleRevision bundleRevision = bundle.adapt(BundleRevision.class);

		if ((bundleRevision != null) &&
			((bundleRevision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0)) {

			return true;
		}

		return false;
	}

	private static boolean _isResolved(Bundle bundle) {
		if (bundle.getState() != Bundle.INSTALLED) {
			return true;
		}

		return false;
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.util;

import com.liferay.marketplace.app.manager.web.internal.constants.BundleConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Bundle;

/**
 * @author Ryan Park
 */
public abstract class BaseAppDisplay implements AppDisplay {

	@Override
	public void addBundle(Bundle bundle) {
		_bundles.add(bundle);
	}

	@Override
	public int compareTo(AppDisplay appDisplay) {
		if (appDisplay == null) {
			return -1;
		}

		String title = getTitle();

		return title.compareToIgnoreCase(appDisplay.getTitle());
	}

	@Override
	public List<Bundle> getBundles() {
		return _bundles;
	}

	@Override
	public String getDisplaySuiteTitle() {
		if (_suiteTitle != null) {
			return _suiteTitle;
		}

		List<Bundle> bundles = getBundles();

		if (bundles.isEmpty()) {
			return StringPool.BLANK;
		}

		Bundle bundle = bundles.get(0);

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		_suiteTitle = getDisplayTitle(
			headers.get(BundleConstants.LIFERAY_RELENG_SUITE_TITLE));

		return _suiteTitle;
	}

	@Override
	public String getDisplayTitle() {
		return getDisplayTitle(getTitle());
	}

	@Override
	public int getState() {
		List<Bundle> bundles = getBundles();

		if (bundles.isEmpty()) {
			return Bundle.UNINSTALLED;
		}

		int state = Bundle.ACTIVE;

		for (Bundle bundle : bundles) {
			if (BundleUtil.isFragment(bundle)) {
				continue;
			}

			int bundleState = bundle.getState();

			if (state <= bundleState) {
				continue;
			}

			state = bundleState;
		}

		return state;
	}

	protected static String getDisplayTitle(String title) {
		if (title == null) {
			return StringPool.BLANK;
		}

		if (!StringUtil.equals(
				ReleaseInfo.getName(), "Liferay Community Edition Portal")) {

			// See SubsystemLPKGPackagerImpl#_getSuiteTitle

			return title.replaceFirst("^Liferay CE ", "Liferay ");
		}

		return title;
	}

	private final List<Bundle> _bundles = new ArrayList<>();
	private String _suiteTitle;

}
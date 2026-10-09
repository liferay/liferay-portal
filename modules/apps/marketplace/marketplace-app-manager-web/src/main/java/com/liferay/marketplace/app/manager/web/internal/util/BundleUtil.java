/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Ryan Park
 */
public class BundleUtil {

	public static void filterBundles(List<Bundle> bundles, int state) {
		Iterator<Bundle> iterator = bundles.iterator();

		while (iterator.hasNext()) {
			Bundle bundle = iterator.next();

			if ((state > 0) && (bundle.getState() != state)) {
				iterator.remove();

				continue;
			}

			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			String bundleType = GetterUtil.getString(
				headers.get("Liferay-Releng-Bundle-Type"));

			if (!bundleType.equals("lpkg")) {
				continue;
			}

			iterator.remove();
		}
	}

	public static boolean isFragment(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String fragmentHost = headers.get(Constants.FRAGMENT_HOST);

		return Validator.isNotNull(fragmentHost);
	}

}
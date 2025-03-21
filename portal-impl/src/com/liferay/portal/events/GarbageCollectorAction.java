/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.events.SessionAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.HttpSession;

import java.text.NumberFormat;

/**
 * @author Brian Wing Shun Chan
 */
public class GarbageCollectorAction extends SessionAction {

	@Override
	public void run(HttpSession httpSession) {
		Runtime runtime = Runtime.getRuntime();

		NumberFormat nf = NumberFormat.getInstance();

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Before:\t\t", nf.format(runtime.freeMemory()), "\t",
					nf.format(runtime.totalMemory()), "\t",
					nf.format(runtime.maxMemory())));
		}

		System.gc();

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"After:\t\t", nf.format(runtime.freeMemory()), "\t",
					nf.format(runtime.totalMemory()), "\t",
					nf.format(runtime.maxMemory())));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GarbageCollectorAction.class);

}
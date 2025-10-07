/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.process.ConsumerOutputProcessor;
import com.liferay.petra.process.ProcessUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.OSDetector;
import com.liferay.portal.kernel.util.PropsValues;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Brian Wing Shun Chan
 */
public class BrowserLauncher implements Runnable {

	@Override
	public void run() {
		try {
			URL url = new URL(PropsValues.BROWSER_LAUNCHER_URL);

			HttpURLConnection urlc = (HttpURLConnection)url.openConnection();

			urlc.setConnectTimeout(0);
			urlc.setReadTimeout(0);
			urlc.setRequestMethod("HEAD");

			int responseCode = urlc.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				try {
					launchBrowser();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	protected void launchBrowser() throws Exception {
		if (OSDetector.isApple()) {
			launchBrowserApple(null);
		}
		else if (OSDetector.isWindows()) {
			launchBrowserWindows(null);
		}
		else {
			launchBrowserUnix(null);
		}
	}

	protected void launchBrowserApple(Runtime runtime) throws Exception {
		ProcessUtil.execute(
			ConsumerOutputProcessor.INSTANCE, "open",
			PropsValues.BROWSER_LAUNCHER_URL);
	}

	protected void launchBrowserUnix(Runtime runtime) throws Exception {
		StringBundler sb = new StringBundler((_BROWSERS.length * 5) - 1);

		for (int i = 0; i < _BROWSERS.length; i++) {
			if (i != 0) {
				sb.append(" || ");
			}

			sb.append(_BROWSERS[i]);
			sb.append(" \"");
			sb.append(PropsValues.BROWSER_LAUNCHER_URL);
			sb.append("\" ");
		}

		ProcessUtil.execute(
			ConsumerOutputProcessor.INSTANCE, "sh", "-c", sb.toString());
	}

	protected void launchBrowserWindows(Runtime runtime) throws Exception {
		ProcessUtil.execute(
			ConsumerOutputProcessor.INSTANCE, "cmd.exe", "/c", "start",
			PropsValues.BROWSER_LAUNCHER_URL);
	}

	/**
	 * Order matters. See LPS-48525.
	 */
	private static final String[] _BROWSERS = {
		"xdg-open", "firefox", "mozilla", "konqueror", "opera"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		BrowserLauncher.class);

}
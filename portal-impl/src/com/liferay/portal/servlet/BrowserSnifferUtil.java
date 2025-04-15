/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.BrowserMetadata;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * See http://www.zytrax.com/tech/web/browser_ids.htm for examples.
 *
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 */
public class BrowserSnifferUtil {

	public static final String BROWSER_ID_CHROME = "chrome";

	public static final String BROWSER_ID_EDGE = "edge";

	public static final String BROWSER_ID_FIREFOX = "firefox";

	public static final String BROWSER_ID_IE = "ie";

	public static final String BROWSER_ID_OTHER = "other";

	public static boolean acceptsGzip(HttpServletRequest httpServletRequest) {
		String acceptEncoding = httpServletRequest.getHeader(
			HttpHeaders.ACCEPT_ENCODING);

		if ((acceptEncoding != null) && acceptEncoding.contains("gzip")) {
			return true;
		}

		return false;
	}

	public static String getAccept(HttpServletRequest httpServletRequest) {
		String accept = StringPool.BLANK;

		if (httpServletRequest == null) {
			return accept;
		}

		accept = String.valueOf(
			httpServletRequest.getAttribute(HttpHeaders.ACCEPT));

		if (Validator.isNotNull(accept)) {
			return accept;
		}

		accept = httpServletRequest.getHeader(HttpHeaders.ACCEPT);

		if (accept != null) {
			accept = StringUtil.toLowerCase(accept);
		}
		else {
			accept = StringPool.BLANK;
		}

		httpServletRequest.setAttribute(HttpHeaders.ACCEPT, accept);

		return accept;
	}

	public static String getBrowserId(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		if (browserMetadata.isChrome()) {
			return BROWSER_ID_CHROME;
		}
		else if (browserMetadata.isEdge()) {
			return BROWSER_ID_EDGE;
		}
		else if (browserMetadata.isIe()) {
			return BROWSER_ID_IE;
		}
		else if (browserMetadata.isFirefox()) {
			return BROWSER_ID_FIREFOX;
		}

		return BROWSER_ID_OTHER;
	}

	public static BrowserMetadata getBrowserMetadata(
		HttpServletRequest httpServletRequest) {

		return new BrowserMetadata(_getUserAgent(httpServletRequest));
	}

	/**
	 * Returns the browser's version number as a float. This differs from {@link
	 * BrowserSniffer#getVersion(HttpServletRequest)}, which returns the version
	 * number as a String.
	 *
	 * <p>
	 * Note that the version returned is defined as the real version of the
	 * browser software, not the one used to render the page. For example, the
	 * browser can be IE10 but it may be using a compatibility view emulating
	 * IE8 to render the page. In such a case, this method would return
	 * <code>10.0</code>, not <code>8.0</code>.
	 * </p>
	 *
	 * @param  httpServletRequest the servlet request
	 * @return a float representing the version number
	 */
	public static float getMajorVersion(HttpServletRequest httpServletRequest) {
		return GetterUtil.getFloat(getVersion(httpServletRequest));
	}

	/**
	 * Returns the browser's revision.
	 *
	 * <p>
	 * Note that the revision returned is defined as the real revision of the
	 * browser software, not the one used to render the page. For example, the
	 * browser can be IE10 but it may be using a compatibility view emulating
	 * IE8 to render the page. In such a case, this method would return
	 * <code>10.0</code>, not <code>8.0</code>.
	 * </p>
	 *
	 * @param  httpServletRequest the servlet request
	 * @return a String containing the revision number
	 */
	public static String getRevision(HttpServletRequest httpServletRequest) {
		String revision = (String)httpServletRequest.getAttribute(
			WebKeys.BROWSER_SNIFFER_REVISION);

		if (revision != null) {
			return revision;
		}

		revision = parseVersion(
			_getUserAgent(httpServletRequest), _REVISION_LEADINGS,
			_REVISION_SEPARATORS);

		httpServletRequest.setAttribute(
			WebKeys.BROWSER_SNIFFER_REVISION, revision);

		return revision;
	}

	/**
	 * Returns the browser's version.
	 *
	 * <p>
	 * Note that the version returned is defined as the real version of the
	 * browser software, not the one used to render the page. For example, the
	 * browser can be an IE10 but it may be using a compatibility view emulating
	 * IE8 to render the page. In such a case, this method would return
	 * <code>10.0</code>, not <code>8.0</code>.
	 * </p>
	 *
	 * @param  httpServletRequest the servlet request
	 * @return a String containing the version number
	 */
	public static String getVersion(HttpServletRequest httpServletRequest) {
		String version = (String)httpServletRequest.getAttribute(
			WebKeys.BROWSER_SNIFFER_VERSION);

		if (version != null) {
			return version;
		}

		String userAgent = _getUserAgent(httpServletRequest);

		version = parseVersion(
			userAgent, _VERSION_LEADINGS, _VERSION_SEPARATORS);

		if (version.isEmpty()) {
			version = parseVersion(
				userAgent, _REVISION_LEADINGS, _REVISION_SEPARATORS);
		}

		httpServletRequest.setAttribute(
			WebKeys.BROWSER_SNIFFER_VERSION, version);

		return version;
	}

	public static boolean isAir(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isAir();
	}

	public static boolean isAndroid(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isAndroid();
	}

	public static boolean isChrome(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isChrome();
	}

	public static boolean isEdge(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isEdge();
	}

	public static boolean isFirefox(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isFirefox();
	}

	public static boolean isGecko(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isGecko();
	}

	public static boolean isIe(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isIe();
	}

	public static boolean isIeOnWin32(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isIeOnWin32();
	}

	public static boolean isIeOnWin64(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isIeOnWin64();
	}

	public static boolean isIphone(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isIphone();
	}

	public static boolean isLinux(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isLinux();
	}

	public static boolean isMac(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isMac();
	}

	public static boolean isMobile(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isMobile();
	}

	public static boolean isMozilla(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isMozilla();
	}

	public static boolean isOpera(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isOpera();
	}

	public static boolean isRtf(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isRtf(getVersion(httpServletRequest));
	}

	public static boolean isSafari(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isSafari();
	}

	public static boolean isSun(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isSun();
	}

	public static boolean isWebKit(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isWebKit();
	}

	public static boolean isWindows(HttpServletRequest httpServletRequest) {
		BrowserMetadata browserMetadata = getBrowserMetadata(
			httpServletRequest);

		return browserMetadata.isWindows();
	}

	public static String parseVersion(
		String userAgent, String[] leadings, char[] separators) {

		leading:
		for (String leading : leadings) {
			int index = 0;

			version:
			while (true) {
				index = userAgent.indexOf(leading, index);

				if ((index < 0) ||
					(((index += leading.length()) + 2) > userAgent.length())) {

					continue leading;
				}

				char c1 = userAgent.charAt(index);
				char c2 = userAgent.charAt(++index);

				if (((c2 >= '0') && (c2 <= '9')) || (c2 == '.')) {
					for (char separator : separators) {
						if (c1 == separator) {
							break version;
						}
					}
				}
			}

			// Major

			int majorStart = index;
			int majorEnd = index + 1;

			for (int i = majorStart; i < userAgent.length(); i++) {
				char c = userAgent.charAt(i);

				if ((c < '0') || (c > '9')) {
					majorEnd = i;

					break;
				}
			}

			String major = userAgent.substring(majorStart, majorEnd);

			if ((majorEnd >= userAgent.length()) ||
				(userAgent.charAt(majorEnd) != '.')) {

				return major;
			}

			// Minor

			int minorStart = majorEnd + 1;
			int minorEnd = userAgent.length();

			for (int i = minorStart; i < userAgent.length(); i++) {
				char c = userAgent.charAt(i);

				if ((c < '0') || (c > '9')) {
					minorEnd = i;

					break;
				}
			}

			String minor = userAgent.substring(minorStart, minorEnd);

			String version = StringBundler.concat(
				major, StringPool.PERIOD, minor);

			if (leading.equals("trident")) {
				if (version.equals("7.0")) {
					version = "11.0";
				}
				else if (version.equals("6.0")) {
					version = "10.0";
				}
				else if (version.equals("5.0")) {
					version = "9.0";
				}
				else if (version.equals("4.0")) {
					version = "8.0";
				}
			}

			return version;
		}

		return StringPool.BLANK;
	}

	private static String _getUserAgent(HttpServletRequest httpServletRequest) {
		if (httpServletRequest == null) {
			return StringPool.BLANK;
		}

		Object userAgentObject = httpServletRequest.getAttribute(
			HttpHeaders.USER_AGENT);

		if (userAgentObject != null) {
			return userAgentObject.toString();
		}

		String userAgent = httpServletRequest.getHeader(HttpHeaders.USER_AGENT);

		if (userAgent != null) {
			userAgent = StringUtil.toLowerCase(userAgent);
		}
		else {
			userAgent = StringPool.BLANK;
		}

		httpServletRequest.setAttribute(HttpHeaders.USER_AGENT, userAgent);

		return userAgent;
	}

	private static final String[] _REVISION_LEADINGS = {
		"rv", "it", "ra", "trident", "ie"
	};

	private static final char[] _REVISION_SEPARATORS = {
		CharPool.BACK_SLASH, CharPool.COLON, CharPool.SLASH, CharPool.SPACE
	};

	private static final String[] _VERSION_LEADINGS = {
		"edge", "chrome", "firefox", "version", "minefield", "trident"
	};

	private static final char[] _VERSION_SEPARATORS = {
		CharPool.BACK_SLASH, CharPool.SLASH
	};

}
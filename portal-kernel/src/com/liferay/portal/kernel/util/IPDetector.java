/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Shuyang Zhou
 */
public class IPDetector {

	public static boolean isPrefersV4() {
		if (_prefersV4 != null) {
			return _prefersV4.booleanValue();
		}

		_prefersV4 = Boolean.valueOf(
			System.getProperty("java.net.preferIPv4Stack"));

		return _prefersV4.booleanValue();
	}

	public static boolean isPrefersV6() {
		if (_prefersV6 != null) {
			return _prefersV6.booleanValue();
		}

		_prefersV6 = Boolean.valueOf(
			System.getProperty("java.net.preferIPv6Stack"));

		return _prefersV6.booleanValue();
	}

	public static boolean isSupportsV6() {
		if (_supportsV6 != null) {
			return _supportsV6.booleanValue();
		}

		try {
			InetAddress[] inetAddresses = InetAddress.getAllByName("localhost");

			for (InetAddress inetAddress : inetAddresses) {
				String hostAddress = inetAddress.getHostAddress();

				if (hostAddress.contains(":")) {
					_supportsV6 = Boolean.TRUE;

					break;
				}
			}
		}
		catch (UnknownHostException unknownHostException) {
			_log.error(unknownHostException);
		}

		if (_supportsV6 == null) {
			_supportsV6 = Boolean.FALSE;
		}

		return _supportsV6.booleanValue();
	}

	private static final Log _log = LogFactoryUtil.getLog(IPDetector.class);

	private static Boolean _prefersV4;
	private static Boolean _prefersV6;
	private static Boolean _supportsV6;

}
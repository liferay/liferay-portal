/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.exception.SystemException;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import java.util.Enumeration;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 */
public class InetAddressUtil {

	public static String getLocalHostName() throws Exception {
		return LocalHostNameHolder._LOCAL_HOST_NAME;
	}

	public static InetAddress getLocalInetAddress() throws Exception {
		Enumeration<NetworkInterface> enu1 =
			NetworkInterface.getNetworkInterfaces();

		while (enu1.hasMoreElements()) {
			NetworkInterface networkInterface = enu1.nextElement();

			Enumeration<InetAddress> enu2 = networkInterface.getInetAddresses();

			while (enu2.hasMoreElements()) {
				InetAddress inetAddress = enu2.nextElement();

				if (!inetAddress.isLoopbackAddress() &&
					(inetAddress instanceof Inet4Address)) {

					return inetAddress;
				}
			}
		}

		throw new SystemException("No local internet address");
	}

	public static InetAddress getLoopbackInetAddress()
		throws UnknownHostException {

		return InetAddress.getByName("127.0.0.1");
	}

	public static boolean isLocalInetAddress(InetAddress inetAddress) {
		if (inetAddress.isAnyLocalAddress() ||
			inetAddress.isLinkLocalAddress() ||
			inetAddress.isLoopbackAddress() ||
			inetAddress.isSiteLocalAddress()) {

			return true;
		}

		return false;
	}

	private static class LocalHostNameHolder {

		private static final String _LOCAL_HOST_NAME;

		static {
			try {
				InetAddress inetAddress = getLocalInetAddress();

				_LOCAL_HOST_NAME = inetAddress.getHostName();
			}
			catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
		}

	}

}
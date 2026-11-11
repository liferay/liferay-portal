/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.concurrent.NoticeableThreadPoolExecutor;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 * @author Marta Medio
 */
public class InetAddressUtil {

	public static InetAddress getInetAddressByName(String domain)
		throws UnknownHostException {

		InetAddress inetAddress = _fastResolveAddress(domain);

		if (inetAddress != null) {
			return inetAddress;
		}

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Get internet address for domain ", domain,
						" has active count ",
						_noticeableThreadPoolExecutor.getActiveCount(),
						" and pending tasking count ",
						_noticeableThreadPoolExecutor.getPendingTaskCount()));
			}

			Future<InetAddress> future = _noticeableThreadPoolExecutor.submit(
				() -> InetAddress.getByName(domain));

			return future.get(
				_DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		catch (RejectedExecutionException rejectedExecutionException) {
			_log.error(
				"Thread limit exceeded to resolve domain: " + domain,
				rejectedExecutionException);

			return null;
		}
		catch (ExecutionException | InterruptedException | TimeoutException
					exception) {

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new UnknownHostException(
				"Unable to resolve domain: " + domain);
		}
	}

	public static String getLocalHostName() {
		return LocalHostNameHolder._LOCAL_HOST_NAME;
	}

	public static InetAddress getLocalInetAddress() throws Exception {
		Enumeration<NetworkInterface> enumeration1 =
			NetworkInterface.getNetworkInterfaces();

		while (enumeration1.hasMoreElements()) {
			NetworkInterface networkInterface = enumeration1.nextElement();

			Enumeration<InetAddress> enumeration2 =
				networkInterface.getInetAddresses();

			while (enumeration2.hasMoreElements()) {
				InetAddress inetAddress = enumeration2.nextElement();

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

	private static InetAddress _fastResolveAddress(String domain)
		throws UnknownHostException {

		if ((domain == null) || (domain.length() == 0)) {
			return null;
		}

		if (domain.charAt(0) == '[') {
			if (domain.charAt(domain.length() - 1) == ']') {
				if (domain.length() == 2) {
					return null;
				}

				domain = domain.substring(1, domain.length() - 1);
			}
			else {
				throw new UnknownHostException(
					domain + " is an invalid IPv6 address");
			}
		}

		if ((Character.digit(domain.charAt(0), 16) == -1) &&
			(domain.charAt(0) != ':')) {

			return null;
		}

		try {
			if (domain.indexOf(':') >= 0) {
				return _fastResolveIPv6Address(domain);
			}
			else if (domain.indexOf('.') >= 0) {
				return _fastResolveIPv4Address(domain);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to fast resolve " + domain);
			}
		}

		return null;
	}

	private static InetAddress _fastResolveIPv4Address(String domain)
		throws Exception {

		byte[] bytes = new byte[4];
		int section = 0;
		int value = 0;

		for (int x = 0; x < domain.length(); x++) {
			char c = domain.charAt(x);

			if (c == '.') {
				bytes[section] = (byte)(value & 0xFF);
				value = 0;

				if (section++ == 3) {
					return null;
				}
			}
			else {
				int d = Character.digit(c, 10);

				if (d == -1) {
					return null;
				}

				value = (value * 10) + d;

				if (value > 255) {
					return null;
				}
			}
		}

		if (section == 3) {
			bytes[section] = (byte)(value & 0xFF);

			return InetAddress.getByAddress(bytes);
		}

		return null;
	}

	private static InetAddress _fastResolveIPv6Address(String domain)
		throws Exception {

		byte[] bytes = new byte[16];
		int pos = 0;
		int value = 0;

		for (int x = 0; x < domain.length(); x++) {
			char c = domain.charAt(x);

			if (c == ':') {
				bytes[pos++] = (byte)((value >> 8) & 0xFF);
				bytes[pos++] = (byte)(value & 0xFF);
				value = 0;

				if (pos == 16) {
					return null;
				}
			}
			else {
				int d = Character.digit(c, 16);

				if (d == -1) {
					return null;
				}

				value = (value << 4) + d;

				if (value > 0xFFFF) {
					return null;
				}
			}
		}

		if (pos == 14) {
			bytes[pos++] = (byte)((value >> 8) & 0xFF);
			bytes[pos++] = (byte)(value & 0xFF);

			return InetAddress.getByAddress(bytes);
		}

		return null;
	}

	private static final int _DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS =
		GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS));

	private static final int _DNS_SECURITY_THREAD_LIMIT = GetterUtil.getInteger(
		PropsUtil.get(PropsKeys.DNS_SECURITY_THREAD_LIMIT));

	private static final int _DNS_SECURITY_THREAD_QUEUE_LIMIT =
		GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.DNS_SECURITY_THREAD_QUEUE_LIMIT));

	private static final Log _log = LogFactoryUtil.getLog(
		InetAddressUtil.class);

	private static final NoticeableThreadPoolExecutor
		_noticeableThreadPoolExecutor = new NoticeableThreadPoolExecutor(
			1, _DNS_SECURITY_THREAD_LIMIT, 300, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>(_DNS_SECURITY_THREAD_QUEUE_LIMIT),
			new NamedThreadFactory(
				InetAddressUtil.class.getName(), Thread.NORM_PRIORITY,
				InetAddressUtil.class.getClassLoader()),
			new ThreadPoolExecutor.AbortPolicy(),
			new ThreadPoolHandlerAdapter());

	private static class LocalHostNameHolder {

		private static final String _LOCAL_HOST_NAME;

		static {
			try {
				InetAddress inetAddress = getLocalInetAddress();

				_LOCAL_HOST_NAME = inetAddress.getHostName();
			}
			catch (Exception exception) {
				throw new ExceptionInInitializerError(exception);
			}
		}

	}

}
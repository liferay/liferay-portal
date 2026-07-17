/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.transport;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A client that listens for multicast messages at a designated port. You may
 * use this to for potential multicast issues when tuning distributed caches.
 * </p>
 *
 * @author Michael C. Han
 * @author Raymond Augé
 */
public class MulticastClientTool {

	public static void main(String[] args) {
		try {
			new MulticastClientTool(args);
		}
		catch (Exception exception) {
			_log.error(exception);

			System.err.println(
				StringBundler.concat(
					"Usage: java -classpath util-java.jar ",
					MulticastClientTool.class.getName(),
					"[-g] [-s] -h [multicastAddress] -p [port] [-b] ",
					"[bindAddress]"));

			System.exit(1);
		}
	}

	private MulticastClientTool(String[] args) throws Exception {
		Map<String, Object> argsMap = _getArgsMap(args);

		Integer port = (Integer)argsMap.get("port");
		String multicastAddress = (String)argsMap.get("multicastAddress");
		String bindAddress = (String)argsMap.get("bindAddress");

		Boolean gzipData = (Boolean)argsMap.get("gzip");
		Boolean shortData = (Boolean)argsMap.get("short");

		DatagramHandler datagramHandler = new MulticastDatagramHandler(
			gzipData.booleanValue(), shortData.booleanValue());

		MulticastTransport multicastTransport = new MulticastTransport(
			datagramHandler, multicastAddress, port, bindAddress);

		if (shortData.booleanValue()) {
			System.out.println("Truncating to 96 bytes.");
		}

		System.out.println("Started up and waiting...");

		multicastTransport.connect();

		synchronized (multicastTransport) {
			multicastTransport.wait();
		}
	}

	private Map<String, Object> _getArgsMap(String[] args) throws Exception {
		Map<String, Object> argsMap = new HashMap<>();

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-g")) {
				argsMap.put("gzip", Boolean.TRUE);
			}
			else if (args[i].equals("-s")) {
				argsMap.put("short", Boolean.TRUE);
			}
			else if (args[i].equals("-h")) {
				argsMap.put("multicastAddress", args[i + 1]);

				i++;
			}
			else if (args[i].equals("-p")) {
				argsMap.put("port", Integer.valueOf(args[i + 1]));

				i++;
			}
			else if (args[i].equals("-b")) {
				argsMap.put("bindAddress", args[i + 1]);

				i++;
			}
		}

		argsMap.putIfAbsent("gzip", Boolean.FALSE);
		argsMap.putIfAbsent("short", Boolean.FALSE);

		return argsMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MulticastClientTool.class);

}
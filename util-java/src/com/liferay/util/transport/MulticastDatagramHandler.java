/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.transport;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;

import java.io.InputStream;

import java.net.DatagramPacket;

import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Michael C. Han
 * @author Raymond Augé
 */
public class MulticastDatagramHandler implements DatagramHandler {

	public MulticastDatagramHandler(boolean gzipData, boolean shortData) {
		_gzipData = gzipData;
		_shortData = shortData;
	}

	@Override
	public void errorReceived(Throwable throwable) {
		_log.error(throwable, throwable);
	}

	@Override
	public void process(DatagramPacket packet) {
		byte[] bytes = packet.getData();

		if (_gzipData) {
			try {
				bytes = getUnzippedBytes(bytes);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		if (_shortData) {
			byte[] temp = new byte[96];

			System.arraycopy(bytes, 0, temp, 0, 96);

			bytes = temp;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"[", packet.getSocketAddress(), "] ", new String(bytes)));
		}
	}

	protected byte[] getUnzippedBytes(byte[] bytes) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream(bytes.length);

		try (InputStream inputStream = new GZIPInputStream(
				new UnsyncByteArrayInputStream(bytes))) {

			byte[] buffer = new byte[1500];
			int c = 0;

			while (true) {
				if (c == -1) {
					break;
				}

				c = inputStream.read(buffer, 0, 1500);

				if (c == -1) {
					continue;
				}

				unsyncByteArrayOutputStream.write(buffer, 0, c);
			}
		}

		unsyncByteArrayOutputStream.flush();
		unsyncByteArrayOutputStream.close();

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private static final Log _log = LogFactory.getLog(
		MulticastDatagramHandler.class);

	private final boolean _gzipData;
	private final boolean _shortData;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.nio.ByteBuffer;

import java.util.Arrays;

/**
 * @author Christopher Kian
 */
public class AWSByteBufferUtil {

	public static byte[] getBytes(ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			throw new IllegalArgumentException("AWS response buffer is null");
		}

		byte[] bytes = new byte[byteBuffer.remaining()];

		byteBuffer.get(bytes);

		if (byteBuffer.hasArray() && !byteBuffer.isReadOnly()) {
			int arrayOffset = byteBuffer.arrayOffset();

			Arrays.fill(
				byteBuffer.array(), arrayOffset,
				arrayOffset + byteBuffer.limit(), (byte)0);
		}
		else if (_log.isWarnEnabled()) {
			_log.warn(
				"Sensitive bytes remain until the JVM reclaims them because " +
					"the AWS response buffer is direct or read only");
		}

		return bytes;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AWSByteBufferUtil.class);

}
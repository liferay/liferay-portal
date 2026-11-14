/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.buffer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.internal.buffer.util.IndexerRequestBufferExecutorUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = IndexerRequestBufferOverflowHandler.class)
public class IndexerRequestBufferOverflowHandler {

	public void bufferOverflowed(
		IndexerRequestBuffer indexerRequestBuffer, int maxBufferSize) {

		int currentBufferSize = indexerRequestBuffer.size();

		if (currentBufferSize < maxBufferSize) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Buffer size is less than maximum: " + maxBufferSize);
			}

			return;
		}

		try {
			BufferOverflowThreadLocal.setOverflowMode(true);

			IndexerRequestBufferExecutorUtil.execute(
				indexerRequestBuffer, currentBufferSize);
		}
		finally {
			BufferOverflowThreadLocal.setOverflowMode(false);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerRequestBufferOverflowHandler.class);

}
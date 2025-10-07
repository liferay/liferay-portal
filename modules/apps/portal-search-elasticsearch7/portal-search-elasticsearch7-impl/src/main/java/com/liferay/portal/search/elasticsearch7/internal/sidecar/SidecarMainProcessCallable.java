/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.local.LocalProcessLauncher;

import java.io.Serializable;

/**
 * @author Tina Tian
 */
public class SidecarMainProcessCallable
	implements ProcessCallable<Serializable> {

	@Override
	public Serializable call() throws ProcessException {
		LocalProcessLauncher.ProcessContext.attach(
			"SidecarMainProcessCallable",
			Long.valueOf(System.getProperty("sidecar.heart.beat.interval")),
			(shutdownCode, shutdownThrowable) -> {
				ElasticsearchServerUtil.shutdown();

				return true;
			});

		ElasticsearchServerUtil.waitForShutdown();

		return null;
	}

	private static final long serialVersionUID = 1L;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.sidecar.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author Dante Wang
 */
public class SidecarAgent {

	public static void premain(
		String argument, Instrumentation instrumentation) {

		instrumentation.addTransformer(new SidecarClassFileTransformer());
	}

}
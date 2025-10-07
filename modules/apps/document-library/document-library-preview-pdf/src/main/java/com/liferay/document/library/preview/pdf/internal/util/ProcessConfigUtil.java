/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.pdf.internal.util;

import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalClassPathUtil;

import java.util.Collections;

/**
 * @author Shuyang Zhou
 */
public class ProcessConfigUtil {

	public static ProcessConfig getProcessConfig() {
		return _processConfig;
	}

	private static final ProcessConfig _processConfig;

	static {
		ProcessConfig processConfig =
			PortalClassPathUtil.createBundleProcessConfig(
				ProcessConfigUtil.class);

		if (PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED) {
			String jvmOptions = StringUtil.trim(
				PropsValues.DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_JVM_OPTIONS);

			if (!jvmOptions.isEmpty()) {
				ProcessConfig.Builder builder = new ProcessConfig.Builder(
					processConfig);

				Collections.addAll(
					builder.getArguments(),
					StringUtil.split(jvmOptions, CharPool.SPACE));

				processConfig = builder.build();
			}
		}

		_processConfig = processConfig;
	}

}
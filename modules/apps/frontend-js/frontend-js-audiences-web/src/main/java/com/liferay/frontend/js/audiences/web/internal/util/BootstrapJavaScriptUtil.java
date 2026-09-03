/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

/**
 * @author Iván Zaera Avellón
 */
public class BootstrapJavaScriptUtil {

	public static String getContent(
		String audiencesDefinitionHash, int detectionTimeout,
		boolean enableLog) {

		return StringUtil.replace(
			_TPL_BOOTSTRAP_JS,
			new String[] {
				"[$AUDIENCES_DEFINITION_HASH$]", "[$DETECTION_TIMEOUT$]",
				"[$ENABLE_LOG$]"
			},
			new Object[] {
				HtmlUtil.escapeJS(audiencesDefinitionHash), detectionTimeout,
				enableLog
			});
	}

	public static String getHash() {
		return _TPL_BOOTSTRAP_JS_HASH;
	}

	private static String _read(String name) {
		try (InputStream inputStream =
				BootstrapJavaScriptUtil.class.getResourceAsStream(
					"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			_log.error("Unable to read template " + name, exception);
		}

		return StringPool.BLANK;
	}

	private static final String _TPL_BOOTSTRAP_JS;

	private static final String _TPL_BOOTSTRAP_JS_HASH;

	private static final Log _log = LogFactoryUtil.getLog(
		BootstrapJavaScriptUtil.class);

	static {
		_TPL_BOOTSTRAP_JS = _read("bootstrap.js.tpl");

		_TPL_BOOTSTRAP_JS_HASH = HashedFilesUtil.computeHash(_TPL_BOOTSTRAP_JS);
	}

}
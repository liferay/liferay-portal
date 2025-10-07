/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Alberto Chaparro
 */
public class LogUtil {

	public static void logOptionalReference(
		Class<?> modelClass, String modelExternalReferenceCode, long scopeId) {

		if (_log.isWarnEnabled()) {
			StringBundler sb = new StringBundler(6);

			sb.append("Optional reference generated for missing ");
			sb.append(modelClass.getSimpleName());
			sb.append(" with external reference code ");
			sb.append(modelExternalReferenceCode);
			sb.append(" and scope ID ");
			sb.append(scopeId);

			_log.warn(sb.toString());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(LogUtil.class);

}
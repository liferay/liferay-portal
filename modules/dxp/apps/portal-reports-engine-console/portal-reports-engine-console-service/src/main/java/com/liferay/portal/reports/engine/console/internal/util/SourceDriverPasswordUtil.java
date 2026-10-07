/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

/**
 * @author Caio Farias
 */
public class SourceDriverPasswordUtil {

	public static String getDriverPassword(
		long companyId, String driverPassword, long sourceId) {

		return SecretResolverUtil.resolve(
			companyId,
			StringBundler.concat(
				Source.class.getSimpleName(), StringPool.SLASH, companyId,
				StringPool.SLASH, sourceId),
			driverPassword);
	}

}
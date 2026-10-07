/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.util;

import com.liferay.message.boards.model.MBMailingList;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

/**
 * @author Caio Farias
 */
public class MBMailingListPasswordUtil {

	public static String getPassword(
		long categoryId, long companyId, String name, String password) {

		return SecretResolverUtil.resolve(
			companyId,
			StringBundler.concat(
				MBMailingList.class.getSimpleName(), StringPool.SLASH,
				companyId, StringPool.SLASH, categoryId, StringPool.SLASH,
				name),
			password);
	}

}
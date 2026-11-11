/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class CustomAttributesUtil {

	public static boolean hasCustomAttributes(
		long companyId, String className, long classPK,
		String ignoreAttributeNames) {

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			companyId, className, classPK);

		List<String> attributeNames = ListUtil.remove(
			Collections.list(expandoBridge.getAttributeNames()),
			ListUtil.fromString(ignoreAttributeNames, StringPool.COMMA));

		return ListUtil.isNotEmpty(attributeNames);
	}

}
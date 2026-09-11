/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.util;

import com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.SharepointExtRepository;

/**
 * @author Jürgen Kappler
 */
public class SharepointRepositoryClassNameUtil {

	public static String getClassName(String name) {
		return SharepointExtRepository.class.getName() + name;
	}

}
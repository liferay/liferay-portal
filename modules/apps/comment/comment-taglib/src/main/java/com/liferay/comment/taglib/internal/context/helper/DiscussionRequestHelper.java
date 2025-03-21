/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.taglib.internal.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class DiscussionRequestHelper extends BaseRequestHelper {

	public DiscussionRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

}
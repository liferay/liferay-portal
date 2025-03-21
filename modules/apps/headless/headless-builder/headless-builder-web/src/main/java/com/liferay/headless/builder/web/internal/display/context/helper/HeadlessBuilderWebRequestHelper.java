/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.web.internal.display.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Carlos Montenegro
 */
public class HeadlessBuilderWebRequestHelper extends BaseRequestHelper {

	public HeadlessBuilderWebRequestHelper(
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);
	}

}
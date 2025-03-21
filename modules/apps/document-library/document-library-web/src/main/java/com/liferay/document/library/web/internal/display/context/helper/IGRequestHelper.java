/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.helper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera
 */
public class IGRequestHelper extends DLRequestHelper {

	public IGRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

}
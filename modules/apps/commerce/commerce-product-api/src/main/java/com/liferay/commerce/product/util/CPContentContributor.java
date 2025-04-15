/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.util;

import com.liferay.commerce.product.model.CPInstance;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public interface CPContentContributor {

	public String getName();

	public JSONObject getValue(
			CPInstance cpInstance, HttpServletRequest httpServletRequest)
		throws PortalException;

}
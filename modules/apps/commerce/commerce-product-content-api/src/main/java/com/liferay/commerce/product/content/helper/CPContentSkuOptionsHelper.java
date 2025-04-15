/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.helper;

import aQute.bnd.annotation.ProviderType;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
@ProviderType
public interface CPContentSkuOptionsHelper {

	public String getDefaultCPInstanceSkuOptions(
			long cpDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception;

}
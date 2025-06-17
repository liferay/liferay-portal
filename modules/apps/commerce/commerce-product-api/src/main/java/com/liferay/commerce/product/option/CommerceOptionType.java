/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.option;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public interface CommerceOptionType {

	public String getKey();

	public String getLabel(Locale locale);

	public boolean hasValues();

	public default boolean isActive() {
		return true;
	}

	public default boolean isValid(
			CPDefinitionOptionRel cpDefinitionOptionRel, String[] values)
		throws PortalException {

		return true;
	}

	public void render(
			CPDefinitionOptionRel cpDefinitionOptionRel,
			long defaultCPInstanceId, boolean forceRequired, String json,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.data.source;

import aQute.bnd.annotation.ProviderType;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Marco Leo
 */
@ProviderType
public interface CPDataSource {

	public String getLabel(Locale locale);

	public String getName();

	public CPDataSourceResult getResult(
			HttpServletRequest httpServletRequest, int start, int end)
		throws Exception;

}
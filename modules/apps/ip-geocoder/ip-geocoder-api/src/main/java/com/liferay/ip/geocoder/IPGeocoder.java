/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ip.geocoder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Julio Camarero
 */
public interface IPGeocoder {

	public IPInfo getIPInfo(HttpServletRequest httpServletRequest);

}
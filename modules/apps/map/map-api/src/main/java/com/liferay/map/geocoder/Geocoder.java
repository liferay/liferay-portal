/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.geocoder;

/**
 * @author Carolina Barbosa
 */
public interface Geocoder {

	public GeocoderResult geocode(String address, long companyId, long groupId)
		throws Exception;

	public GeocoderResult reverseGeocode(
			long companyId, long groupId, double latitude, double longitude)
		throws Exception;

}
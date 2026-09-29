/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.geocoder;

/**
 * @author Carolina Barbosa
 */
public class GeocoderResult {

	public GeocoderResult(String address, double latitude, double longitude) {
		_address = address;
		_latitude = latitude;
		_longitude = longitude;
	}

	public String getAddress() {
		return _address;
	}

	public double getLatitude() {
		return _latitude;
	}

	public double getLongitude() {
		return _longitude;
	}

	private final String _address;
	private final double _latitude;
	private final double _longitude;

}
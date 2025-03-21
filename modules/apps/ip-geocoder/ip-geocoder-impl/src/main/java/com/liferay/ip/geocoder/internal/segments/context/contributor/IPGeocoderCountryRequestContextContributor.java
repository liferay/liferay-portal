/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ip.geocoder.internal.segments.context.contributor;

import com.liferay.ip.geocoder.IPGeocoder;
import com.liferay.ip.geocoder.IPInfo;
import com.liferay.segments.context.Context;
import com.liferay.segments.context.contributor.RequestContextContributor;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Allen Ziegenfus
 */
@Component(
	property = {
		"request.context.contributor.key=" + IPGeocoderCountryRequestContextContributor.KEY,
		"request.context.contributor.type=string"
	},
	service = RequestContextContributor.class
)
public class IPGeocoderCountryRequestContextContributor
	implements RequestContextContributor {

	public static final String KEY = "ipGeocoderCountry";

	@Override
	public void contribute(
		Context context, HttpServletRequest httpServletRequest) {

		context.put(
			KEY,
			new Serializable() {

				@Override
				public String toString() {
					IPInfo ipInfo = _ipGeocoder.getIPInfo(httpServletRequest);

					return ipInfo.getCountryCode();
				}

			});
	}

	@Reference
	private IPGeocoder _ipGeocoder;

}
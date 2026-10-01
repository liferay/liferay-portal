/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.test.util;

import com.liferay.map.geocoder.Geocoder;
import com.liferay.map.geocoder.GeocoderRegistry;
import com.liferay.map.geocoder.GeocoderResult;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class GeocoderTestUtil {

	public static SafeCloseable swapWithSafeCloseable(
		GeocoderResult geocoderResult, Object instance) {

		Geocoder geocoder = (Geocoder)ProxyUtil.newProxyInstance(
			Geocoder.class.getClassLoader(), new Class<?>[] {Geocoder.class},
			(proxy, method, arguments) -> {
				if (Objects.equals(method.getName(), "geocode")) {
					return new GeocoderResult(
						(String)arguments[0], geocoderResult.getLatitude(),
						geocoderResult.getLongitude());
				}
				else if (Objects.equals(method.getName(), "reverseGeocode")) {
					return new GeocoderResult(
						geocoderResult.getAddress(), (double)arguments[2],
						(double)arguments[3]);
				}

				return method.invoke(arguments);
			});

		Object geocoderRegistry = ReflectionTestUtil.getAndSetFieldValue(
			instance, "_geocoderRegistry",
			(GeocoderRegistry)ProxyUtil.newProxyInstance(
				GeocoderRegistry.class.getClassLoader(),
				new Class<?>[] {GeocoderRegistry.class},
				(proxy, method, arguments) -> geocoder));

		return () -> ReflectionTestUtil.setFieldValue(
			instance, "_geocoderRegistry", geocoderRegistry);
	}

}
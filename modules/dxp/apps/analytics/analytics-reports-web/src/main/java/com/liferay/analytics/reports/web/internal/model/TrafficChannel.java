/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.model;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author David Arques
 */
public class TrafficChannel {

	public static TrafficChannel newInstance(
		AcquisitionChannel acquisitionChannel) {

		Type type = Type.valueOf(
			StringUtil.toUpperCase(acquisitionChannel.getName()));

		if (type == null) {
			throw new IllegalArgumentException(
				"Invalid traffic channel name " + acquisitionChannel.getName());
		}

		return newInstance(
			acquisitionChannel.getTrafficAmount(),
			acquisitionChannel.getTrafficShare(), type);
	}

	public static TrafficChannel newInstance(Exception exception, Type type) {
		return new TrafficChannel(true, type);
	}

	public static TrafficChannel newInstance(
		long trafficAmount, double trafficShare, Type type) {

		if (type == null) {
			throw new IllegalArgumentException("Type is null");
		}

		return new TrafficChannel(trafficAmount, trafficShare, type);
	}

	public TrafficChannel(boolean error, Type type) {
		_error = error;
		_type = type;

		_trafficAmount = 0;
		_trafficShare = 0;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TrafficChannel)) {
			return false;
		}

		TrafficChannel trafficChannel = (TrafficChannel)object;

		if (Objects.equals(
				getHelpMessageKey(), trafficChannel.getHelpMessageKey()) &&
			Objects.equals(getType(), trafficChannel.getType()) &&
			Objects.equals(_trafficAmount, trafficChannel._trafficAmount) &&
			Objects.equals(_trafficShare, trafficChannel._trafficShare)) {

			return true;
		}

		return false;
	}

	public String getHelpMessageKey() {
		return _helpMessageKeys.get(_type);
	}

	public long getTrafficAmount() {
		return _trafficAmount;
	}

	public double getTrafficShare() {
		return _trafficShare;
	}

	public Tuple getTuple() {
		return _tuples.get(_type);
	}

	public Type getType() {
		return _type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_type, _trafficAmount, _trafficShare);
	}

	public JSONObject toJSONObject(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		ResourceBundle resourceBundle) {

		return JSONUtil.put(
			"endpointURL",
			() -> {
				Tuple tuple = _tuples.get(_type);

				if (tuple == null) {
					return null;
				}

				ResourceURL resourceURL =
					liferayPortletResponse.createResourceURL();

				resourceURL.setResourceID(String.valueOf(tuple.getObject(0)));

				HttpServletRequest httpServletRequest =
					liferayPortletRequest.getHttpServletRequest();

				resourceURL.setParameters(
					HashMapBuilder.putAll(
						httpServletRequest.getParameterMap()
					).putAll(
						(Map<String, String[]>)tuple.getObject(1)
					).build());

				return String.valueOf(resourceURL);
			}
		).put(
			"helpMessage",
			ResourceBundleUtil.getString(resourceBundle, getHelpMessageKey())
		).put(
			"name", _type.toString()
		).put(
			"share",
			() -> {
				if (!_error) {
					return String.format("%.1f", _trafficShare);
				}

				return null;
			}
		).put(
			"title",
			ResourceBundleUtil.getString(resourceBundle, _type.toString())
		).put(
			"value",
			() -> {
				if (!_error) {
					return Math.toIntExact(_trafficAmount);
				}

				return null;
			}
		);
	}

	@Override
	public String toString() {
		return JSONUtil.put(
			"endpointURL", _tuples.get(_type)
		).put(
			"helpMessageKey", getHelpMessageKey()
		).put(
			"name", _type
		).put(
			"share",
			() -> {
				if (!_error) {
					return String.format("%.1f", _trafficShare);
				}

				return null;
			}
		).put(
			"title", _type
		).put(
			"value",
			() -> {
				if (!_error) {
					return Math.toIntExact(_trafficAmount);
				}

				return null;
			}
		).toString();
	}

	public enum Type {

		DIRECT {

			@Override
			public String toString() {
				return "direct";
			}

		},
		ORGANIC {

			@Override
			public String toString() {
				return "organic";
			}

		},
		PAID {

			@Override
			public String toString() {
				return "paid";
			}

		},
		REFERRAL {

			@Override
			public String toString() {
				return "referral";
			}

		},
		SOCIAL {

			@Override
			public String toString() {
				return "social";
			}

		}

	}

	private TrafficChannel(long trafficAmount, double trafficShare, Type type) {
		_trafficAmount = trafficAmount;
		_trafficShare = trafficShare;
		_type = type;

		_error = false;
	}

	private static final Map<Type, String> _helpMessageKeys =
		HashMapBuilder.put(
			Type.DIRECT,
			"this-is-the-number-of-page-views-generated-by-people-arriving-" +
				"directly-to-your-page"
		).put(
			Type.ORGANIC,
			"this-is-the-number-of-page-views-generated-by-people-coming-" +
				"from-a-search-engine"
		).put(
			Type.PAID,
			"this-is-the-number-of-page-views-generated-by-people-that-find-" +
				"your-page-through-google-adwords"
		).put(
			Type.REFERRAL,
			"this-is-the-number-of-page-views-generated-by-people-coming-to-" +
				"your-page-from-other-sites-which-are-not-search-engine-" +
					"pages-or-social-sites"
		).put(
			Type.SOCIAL,
			"this-is-the-number-of-page-views-generated-by-people-coming-to-" +
				"your-page-from-social-sites"
		).build();
	private static final Map<Type, Tuple> _tuples =
		HashMapBuilder.<Type, Tuple>put(
			Type.REFERRAL,
			new Tuple(
				"/analytics_reports/get_referral_traffic_sources",
				Collections.emptyMap())
		).put(
			Type.SOCIAL,
			new Tuple(
				"/analytics_reports/get_social_traffic_sources",
				Collections.emptyMap())
		).build();

	private final boolean _error;
	private final long _trafficAmount;
	private final double _trafficShare;
	private final Type _type;

}
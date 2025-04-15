/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.util;

import com.liferay.expando.kernel.exception.ValueDataException;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class ExpandoUtil {

	public static Map<String, Serializable> getExpandoBridgeAttributes(
			ExpandoBridge expandoBridge, HttpServletRequest httpServletRequest)
		throws PortalException {

		Map<String, Serializable> attributes = new HashMap<>();

		List<String> names = new ArrayList<>();

		Enumeration<String> enumeration =
			httpServletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String param = enumeration.nextElement();

			if (param.contains("ExpandoAttributeName--")) {
				String name = ParamUtil.getString(httpServletRequest, param);

				names.add(name);
			}
		}

		for (String name : names) {
			int type = expandoBridge.getAttributeType(name);

			UnicodeProperties unicodeProperties =
				expandoBridge.getAttributeProperties(name);

			String displayType = GetterUtil.getString(
				unicodeProperties.getProperty(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE),
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX);

			Serializable value = _getExpandoValue(
				httpServletRequest, "ExpandoAttribute--" + name + "--", type,
				displayType);

			attributes.put(name, value);
		}

		return attributes;
	}

	public static Map<String, Serializable> getExpandoBridgeAttributes(
			ExpandoBridge expandoBridge, PortletRequest portletRequest)
		throws PortalException {

		return getExpandoBridgeAttributes(
			expandoBridge, PortalUtil.getHttpServletRequest(portletRequest));
	}

	private static Serializable _getExpandoValue(
			HttpServletRequest httpServletRequest, String name, int type,
			String displayType)
		throws PortalException {

		Serializable value = null;

		if (type == ExpandoColumnConstants.BOOLEAN) {
			value = ParamUtil.getBoolean(httpServletRequest, name);
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
		}
		else if (type == ExpandoColumnConstants.DATE) {
			int valueDateMonth = ParamUtil.getInteger(
				httpServletRequest, name + "Month");
			int valueDateDay = ParamUtil.getInteger(
				httpServletRequest, name + "Day");
			int valueDateYear = ParamUtil.getInteger(
				httpServletRequest, name + "Year");
			int valueDateHour = ParamUtil.getInteger(
				httpServletRequest, name + "Hour");
			int valueDateMinute = ParamUtil.getInteger(
				httpServletRequest, name + "Minute");
			int valueDateAmPm = ParamUtil.getInteger(
				httpServletRequest, name + "AmPm");

			if (valueDateAmPm == Calendar.PM) {
				valueDateHour += 12;
			}

			TimeZone timeZone = null;

			User user = PortalUtil.getUser(httpServletRequest);

			if (user != null) {
				timeZone = user.getTimeZone();
			}

			value = PortalUtil.getDate(
				valueDateMonth, valueDateDay, valueDateYear, valueDateHour,
				valueDateMinute, timeZone, ValueDataException.class);
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			value = ParamUtil.getDouble(httpServletRequest, name);
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			String[] values = httpServletRequest.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				ArrayUtil.isNotEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getDoubleValues(values);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			value = ParamUtil.getFloat(httpServletRequest, name);
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			String[] values = httpServletRequest.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				ArrayUtil.isNotEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getFloatValues(values);
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			value = ParamUtil.getInteger(httpServletRequest, name);
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			String[] values = httpServletRequest.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				ArrayUtil.isNotEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getIntegerValues(values);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			value = ParamUtil.getLong(httpServletRequest, name);
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			String[] values = httpServletRequest.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				ArrayUtil.isNotEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getLongValues(values);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			value = ParamUtil.getNumber(httpServletRequest, name);
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			String[] values = httpServletRequest.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				ArrayUtil.isNotEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getNumberValues(values);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			value = ParamUtil.getShort(httpServletRequest, name);
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			String[] values = httpServletRequest.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				ArrayUtil.isNotEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getShortValues(values);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			String[] values = httpServletRequest.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				ArrayUtil.isNotEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = values;
		}
		else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
			value = (Serializable)LocalizationUtil.getLocalizationMap(
				httpServletRequest, name);
		}
		else {
			value = ParamUtil.getString(httpServletRequest, name);
		}

		return value;
	}

}
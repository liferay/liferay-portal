/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.Normalizer;

import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Provides utility methods for reading request parameters.
 *
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class ParamUtil {

	/**
	 * Returns the request parameter value as a boolean. If the parameter is
	 * missing, the default value is returned.
	 *
	 * <p>
	 * If the value is not convertible to a boolean, <code>false</code> is
	 * returned.
	 * </p>
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a boolean
	 */
	public static boolean get(
		HttpServletRequest httpServletRequest, String param,
		boolean defaultValue) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a Date. If the parameter is
	 * missing or not convertible to a Date, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the request parameter value as a Date
	 */
	public static Date get(
		HttpServletRequest httpServletRequest, String param,
		DateFormat dateFormat, Date defaultValue) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), dateFormat, defaultValue);
	}

	/**
	 * Returns the request parameter value as a double. If the parameter is
	 * missing or not convertible to a double, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a double
	 */
	public static double get(
		HttpServletRequest httpServletRequest, String param,
		double defaultValue) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a float. If the parameter is
	 * missing or not convertible to a float, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a float
	 */
	public static float get(
		HttpServletRequest httpServletRequest, String param,
		float defaultValue) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as an integer. If the parameter is
	 * missing or not convertible to an integer, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as an integer
	 */
	public static int get(
		HttpServletRequest httpServletRequest, String param, int defaultValue) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a long. If the parameter is
	 * missing or not convertible to a long, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a long
	 */
	public static long get(
		HttpServletRequest httpServletRequest, String param,
		long defaultValue) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a Number. If the parameter is
	 * missing or not convertible to a Number, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a Number
	 */
	public static Number get(
		HttpServletRequest httpServletRequest, String param,
		Number defaultValue) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a short. If the parameter is
	 * missing or not convertible to a short, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a short
	 */
	public static short get(
		HttpServletRequest httpServletRequest, String param,
		short defaultValue) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a String. If the parameter is
	 * missing or not convertible to a String, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a String
	 */
	public static String get(
		HttpServletRequest httpServletRequest, String param,
		String defaultValue) {

		String returnValue = GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue);

		if (returnValue != null) {
			return _normalize(StringUtil.trim(returnValue));
		}

		return null;
	}

	/**
	 * Returns the portlet request parameter value as a boolean. If the
	 * parameter is missing, the default value is returned.
	 *
	 * <p>
	 * If the value is not convertible to a boolean, <code>false</code> is
	 * returned.
	 * </p>
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a boolean
	 */
	public static boolean get(
		PortletRequest portletRequest, String param, boolean defaultValue) {

		return GetterUtil.get(portletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a Date. If the parameter
	 * is missing or not convertible to a Date, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a Date
	 */
	public static Date get(
		PortletRequest portletRequest, String param, DateFormat dateFormat,
		Date defaultValue) {

		return GetterUtil.get(
			portletRequest.getParameter(param), dateFormat, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a double. If the parameter
	 * is missing or not convertible to a double, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a double
	 */
	public static double get(
		PortletRequest portletRequest, String param, double defaultValue) {

		return GetterUtil.get(portletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a float. If the parameter
	 * is missing or not convertible to a float, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a float
	 */
	public static float get(
		PortletRequest portletRequest, String param, float defaultValue) {

		return GetterUtil.get(portletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as an integer. If the
	 * parameter is missing or not convertible to an integer, the default value
	 * is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as an integer
	 */
	public static int get(
		PortletRequest portletRequest, String param, int defaultValue) {

		return GetterUtil.get(portletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a long. If the parameter
	 * is missing or not convertible to a long, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a long
	 */
	public static long get(
		PortletRequest portletRequest, String param, long defaultValue) {

		return GetterUtil.get(portletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a Number. If the parameter
	 * is missing or not convertible to a Number, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a Number
	 */
	public static Number get(
		PortletRequest portletRequest, String param, Number defaultValue) {

		return GetterUtil.get(portletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a short. If the parameter
	 * is missing or not convertible to a short, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a short
	 */
	public static short get(
		PortletRequest portletRequest, String param, short defaultValue) {

		return GetterUtil.get(portletRequest.getParameter(param), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a String. If the parameter
	 * is missing or not convertible to a String, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a String
	 */
	public static String get(
		PortletRequest portletRequest, String param, String defaultValue) {

		String returnValue = GetterUtil.get(
			portletRequest.getParameter(param), defaultValue);

		if (returnValue != null) {
			return _normalize(StringUtil.trim(returnValue));
		}

		return null;
	}

	/**
	 * Returns the service context parameter value as a boolean. If the
	 * parameter is missing, the default value is returned.
	 *
	 * <p>
	 * If the value is not convertible to a boolean, <code>false</code> is
	 * returned.
	 * </p>
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a boolean
	 */
	public static boolean get(
		ServiceContext serviceContext, String param, boolean defaultValue) {

		return GetterUtil.get(serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a Date. If the parameter
	 * is missing or not convertible to a Date, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a Date
	 */
	public static Date get(
		ServiceContext serviceContext, String param, DateFormat dateFormat,
		Date defaultValue) {

		return GetterUtil.get(
			serviceContext.getAttribute(param), dateFormat, defaultValue);
	}

	/**
	 * Returns the service context parameter value as a double. If the parameter
	 * is missing or not convertible to a double, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a double
	 */
	public static double get(
		ServiceContext serviceContext, String param, double defaultValue) {

		return GetterUtil.get(serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a float. If the parameter
	 * is missing or not convertible to a float, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a float
	 */
	public static float get(
		ServiceContext serviceContext, String param, float defaultValue) {

		return GetterUtil.get(serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the service context parameter value as an integer. If the
	 * parameter is missing or not convertible to an integer, the default value
	 * is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as an integer
	 */
	public static int get(
		ServiceContext serviceContext, String param, int defaultValue) {

		return GetterUtil.get(serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a long. If the parameter
	 * is missing or not convertible to a long, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a long
	 */
	public static long get(
		ServiceContext serviceContext, String param, long defaultValue) {

		return GetterUtil.get(serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a Number. If the parameter
	 * is missing or not convertible to a Number, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a Number
	 */
	public static Number get(
		ServiceContext serviceContext, String param, Number defaultValue) {

		return GetterUtil.get(serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a short. If the parameter
	 * is missing or not convertible to a short, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a short
	 */
	public static short get(
		ServiceContext serviceContext, String param, short defaultValue) {

		return GetterUtil.get(serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a String. If the parameter
	 * is missing or not convertible to a String, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a String
	 */
	public static String get(
		ServiceContext serviceContext, String param, String defaultValue) {

		String returnValue = GetterUtil.get(
			serviceContext.getAttribute(param), defaultValue);

		if (returnValue != null) {
			return _normalize(StringUtil.trim(returnValue));
		}

		return null;
	}

	/**
	 * Returns the request parameter value as a boolean. If the parameter is
	 * missing or not convertible to a boolean, <code>false</code> is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a boolean
	 */
	public static boolean getBoolean(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getBoolean(httpServletRequest.getParameter(param));
	}

	/**
	 * Returns the request parameter value as a boolean. If the parameter is
	 * missing, the default value is returned.
	 *
	 * <p>
	 * If the value is not convertible to a boolean, <code>false</code> is
	 * returned.
	 * </p>
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a boolean
	 */
	public static boolean getBoolean(
		HttpServletRequest httpServletRequest, String param,
		boolean defaultValue) {

		return get(httpServletRequest, param, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a boolean. If the
	 * parameter is missing or not convertible to a boolean, <code>false</code>
	 * is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a boolean
	 */
	public static boolean getBoolean(
		PortletRequest portletRequest, String param) {

		return GetterUtil.getBoolean(portletRequest.getParameter(param));
	}

	/**
	 * Returns the portlet request parameter value as a boolean. If the
	 * parameter is missing, the default value is returned.
	 *
	 * <p>
	 * If the value is not convertible to a boolean, <code>false</code> is
	 * returned.
	 * </p>
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a boolean
	 */
	public static boolean getBoolean(
		PortletRequest portletRequest, String param, boolean defaultValue) {

		return get(portletRequest, param, defaultValue);
	}

	/**
	 * Returns the service context parameter value as a boolean. If the
	 * parameter is missing or not convertible to a boolean, <code>false</code>
	 * is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a boolean
	 */
	public static boolean getBoolean(
		ServiceContext serviceContext, String param) {

		return GetterUtil.getBoolean(serviceContext.getAttribute(param));
	}

	/**
	 * Returns the service context parameter value as a boolean. If the
	 * parameter is missing, the default value is returned.
	 *
	 * <p>
	 * If the value is not convertible to a boolean, <code>false</code> is
	 * returned.
	 * </p>
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a boolean
	 */
	public static boolean getBoolean(
		ServiceContext serviceContext, String param, boolean defaultValue) {

		return get(serviceContext, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as a boolean array. In the returned
	 * array, each parameter value not convertible to a boolean is replaced by
	 * <code>false</code>.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a boolean array
	 */
	public static boolean[] getBooleanValues(
		HttpServletRequest httpServletRequest, String param) {

		return getBooleanValues(httpServletRequest, param, new boolean[0]);
	}

	/**
	 * Returns the request parameter value as a boolean array. In the returned
	 * array, each parameter value not convertible to a boolean is replaced by
	 * the default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a boolean array
	 */
	public static boolean[] getBooleanValues(
		HttpServletRequest httpServletRequest, String param,
		boolean[] defaultValue) {

		return GetterUtil.getBooleanValues(
			getParameterValues(httpServletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a boolean array. In the
	 * returned array, each parameter value not convertible to a boolean is
	 * replaced by <code>false</code>.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a boolean array
	 */
	public static boolean[] getBooleanValues(
		PortletRequest portletRequest, String param) {

		return getBooleanValues(portletRequest, param, new boolean[0]);
	}

	/**
	 * Returns the portlet request parameter value as a boolean array. In the
	 * returned array, each parameter value not convertible to a boolean is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a boolean array
	 */
	public static boolean[] getBooleanValues(
		PortletRequest portletRequest, String param, boolean[] defaultValue) {

		return GetterUtil.getBooleanValues(
			getParameterValues(portletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a boolean array. In the
	 * returned array, each parameter value not convertible to a boolean is
	 * replaced by <code>false</code>.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a boolean array
	 */
	public static boolean[] getBooleanValues(
		ServiceContext serviceContext, String param) {

		return getBooleanValues(serviceContext, param, new boolean[0]);
	}

	/**
	 * Returns the service context parameter value as a boolean array. In the
	 * returned array, each parameter value not convertible to a boolean is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a boolean array
	 */
	public static boolean[] getBooleanValues(
		ServiceContext serviceContext, String param, boolean[] defaultValue) {

		return GetterUtil.getBooleanValues(
			serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a Date. If the parameter is
	 * missing or not convertible to a Date, the current date is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @return the request parameter value as a Date
	 */
	public static Date getDate(
		HttpServletRequest httpServletRequest, String param,
		DateFormat dateFormat) {

		return GetterUtil.getDate(
			httpServletRequest.getParameter(param), dateFormat);
	}

	/**
	 * Returns the request parameter value as a Date. If the parameter is
	 * missing or not convertible to a Date, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the request parameter value as a Date
	 */
	public static Date getDate(
		HttpServletRequest httpServletRequest, String param,
		DateFormat dateFormat, Date defaultValue) {

		return get(httpServletRequest, param, dateFormat, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a Date. If the parameter
	 * is missing or not convertible to a Date, the current date is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @return the portlet request parameter value as a Date
	 */
	public static Date getDate(
		PortletRequest portletRequest, String param, DateFormat dateFormat) {

		return GetterUtil.getDate(
			portletRequest.getParameter(param), dateFormat);
	}

	/**
	 * Returns the portlet request parameter value as a Date. If the parameter
	 * is missing or not convertible to a Date, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a Date
	 */
	public static Date getDate(
		PortletRequest portletRequest, String param, DateFormat dateFormat,
		Date defaultValue) {

		return get(portletRequest, param, dateFormat, defaultValue);
	}

	/**
	 * Returns the service context parameter value as a Date. If the parameter
	 * is missing or not convertible to a Date, the current date is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @return the service context parameter value as a Date
	 */
	public static Date getDate(
		ServiceContext serviceContext, String param, DateFormat dateFormat) {

		return GetterUtil.getDate(
			serviceContext.getAttribute(param), dateFormat);
	}

	/**
	 * Returns the service context parameter value as a Date. If the parameter
	 * is missing or not convertible to a Date, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a Date
	 */
	public static Date getDate(
		ServiceContext serviceContext, String param, DateFormat dateFormat,
		Date defaultValue) {

		return get(serviceContext, param, dateFormat, defaultValue);
	}

	/**
	 * Returns the request parameter value as a Date array. In the returned
	 * array, each parameter value not convertible to a Date is replaced by the
	 * current date.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @return the request parameter value as a Date array
	 */
	public static Date[] getDateValues(
		HttpServletRequest httpServletRequest, String param,
		DateFormat dateFormat) {

		return getDateValues(
			httpServletRequest, param, dateFormat, new Date[0]);
	}

	/**
	 * Returns the request parameter value as a Date array. In the returned
	 * array, each parameter value not convertible to a Date is replaced by the
	 * default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the request parameter value as a Date array
	 */
	public static Date[] getDateValues(
		HttpServletRequest httpServletRequest, String param,
		DateFormat dateFormat, Date[] defaultValue) {

		return GetterUtil.getDateValues(
			getParameterValues(httpServletRequest, param, null), dateFormat,
			defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a Date array. In the
	 * returned array, each parameter value not convertible to a Date is
	 * replaced by the current date.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @return the portlet request parameter value as a Date array
	 */
	public static Date[] getDateValues(
		PortletRequest portletRequest, String param, DateFormat dateFormat) {

		return getDateValues(portletRequest, param, dateFormat, new Date[0]);
	}

	/**
	 * Returns the portlet request parameter value as a Date array. In the
	 * returned array, each parameter value not convertible to a Date is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a Date array
	 */
	public static Date[] getDateValues(
		PortletRequest portletRequest, String param, DateFormat dateFormat,
		Date[] defaultValue) {

		return GetterUtil.getDateValues(
			getParameterValues(portletRequest, param, null), dateFormat,
			defaultValue);
	}

	/**
	 * Returns the service context parameter value as a Date array. In the
	 * returned array, each parameter value not convertible to a Date is
	 * replaced by the current date.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @return the service context parameter value as a Date array
	 */
	public static Date[] getDateValues(
		ServiceContext serviceContext, String param, DateFormat dateFormat) {

		return getDateValues(serviceContext, param, dateFormat, new Date[0]);
	}

	/**
	 * Returns the service context parameter value as a Date array. In the
	 * returned array, each parameter value not convertible to a Date is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  dateFormat the format used to parse the date
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a Date array
	 */
	public static Date[] getDateValues(
		ServiceContext serviceContext, String param, DateFormat dateFormat,
		Date[] defaultValue) {

		return GetterUtil.getDateValues(
			serviceContext.getAttribute(param), dateFormat, defaultValue);
	}

	/**
	 * Returns the request parameter value as a double. If the parameter is
	 * missing or not convertible to a double, <code>0</code> is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a double
	 */
	public static double getDouble(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getDouble(httpServletRequest.getParameter(param));
	}

	/**
	 * Returns the request parameter value as a double. If the parameter is
	 * missing or not convertible to a double, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a double
	 */
	public static double getDouble(
		HttpServletRequest httpServletRequest, String param,
		double defaultValue) {

		return get(httpServletRequest, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as a double. If the parameter is
	 * missing or not convertible to a double, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @param  locale the locale used to parse the double value
	 * @return the request parameter value as a double
	 */
	public static double getDouble(
		HttpServletRequest httpServletRequest, String param,
		double defaultValue, Locale locale) {

		return GetterUtil.get(
			httpServletRequest.getParameter(param), defaultValue, locale);
	}

	/**
	 * Returns the request parameter value as a double. If the parameter is
	 * missing or not convertible to a double, <code>0</code> is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  locale the locale used to parse the double value
	 * @return the request parameter value as a double
	 */
	public static double getDouble(
		HttpServletRequest httpServletRequest, String param, Locale locale) {

		return GetterUtil.getDouble(
			httpServletRequest.getParameter(param), locale);
	}

	/**
	 * Returns the portlet request parameter value as a double. If the parameter
	 * is missing or not convertible to a double, <code>0</code> is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a double
	 */
	public static double getDouble(
		PortletRequest portletRequest, String param) {

		return GetterUtil.getDouble(portletRequest.getParameter(param));
	}

	/**
	 * Returns the portlet request parameter value as a double. If the parameter
	 * is missing or not convertible to a double, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a double
	 */
	public static double getDouble(
		PortletRequest portletRequest, String param, double defaultValue) {

		return get(portletRequest, param, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a double. If the parameter
	 * is missing or not convertible to a double, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @param  locale the locale used to parse the double value
	 * @return the portlet request parameter value as a double
	 */
	public static double getDouble(
		PortletRequest portletRequest, String param, double defaultValue,
		Locale locale) {

		return GetterUtil.get(
			portletRequest.getParameter(param), defaultValue, locale);
	}

	/**
	 * Returns the portlet request parameter value as a double. If the parameter
	 * is missing or not convertible to a double, <code>0</code> is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  locale the locale used to parse the double value
	 * @return the portlet request parameter value as a double
	 */
	public static double getDouble(
		PortletRequest portletRequest, String param, Locale locale) {

		return GetterUtil.getDouble(portletRequest.getParameter(param), locale);
	}

	/**
	 * Returns the service context parameter value as a double. If the parameter
	 * is missing or not convertible to a double, <code>0</code> is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a double
	 */
	public static double getDouble(
		ServiceContext serviceContext, String param) {

		return GetterUtil.getDouble(serviceContext.getAttribute(param));
	}

	/**
	 * Returns the service context parameter value as a double. If the parameter
	 * is missing or not convertible to a double, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a double
	 */
	public static double getDouble(
		ServiceContext serviceContext, String param, double defaultValue) {

		return get(serviceContext, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as a double array. In the returned
	 * array, each parameter value not convertible to a double is replaced by
	 * <code>0</code>.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a double array
	 */
	public static double[] getDoubleValues(
		HttpServletRequest httpServletRequest, String param) {

		return getDoubleValues(httpServletRequest, param, new double[0]);
	}

	/**
	 * Returns the request parameter value as a double array. In the returned
	 * array, each parameter value not convertible to a double is replaced by
	 * the default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a double array
	 */
	public static double[] getDoubleValues(
		HttpServletRequest httpServletRequest, String param,
		double[] defaultValue) {

		return GetterUtil.getDoubleValues(
			getParameterValues(httpServletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a double array. In the
	 * returned array, each parameter value not convertible to a double is
	 * replaced by <code>0</code>.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a double array
	 */
	public static double[] getDoubleValues(
		PortletRequest portletRequest, String param) {

		return getDoubleValues(portletRequest, param, new double[0]);
	}

	/**
	 * Returns the portlet request parameter value as a double array. In the
	 * returned array, each parameter value not convertible to a double is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a double array
	 */
	public static double[] getDoubleValues(
		PortletRequest portletRequest, String param, double[] defaultValue) {

		return GetterUtil.getDoubleValues(
			getParameterValues(portletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a double array. In the
	 * returned array, each parameter value not convertible to a double is
	 * replaced by <code>0</code>.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a double array
	 */
	public static double[] getDoubleValues(
		ServiceContext serviceContext, String param) {

		return getDoubleValues(serviceContext, param, new double[0]);
	}

	/**
	 * Returns the service context parameter value as a double array. In the
	 * returned array, each parameter value not convertible to a double is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a double array
	 */
	public static double[] getDoubleValues(
		ServiceContext serviceContext, String param, double[] defaultValue) {

		return GetterUtil.getDoubleValues(
			serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a float. If the parameter is
	 * missing or not convertible to a float, <code>0</code> is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a float
	 */
	public static float getFloat(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getFloat(httpServletRequest.getParameter(param));
	}

	/**
	 * Returns the request parameter value as a float. If the parameter is
	 * missing or not convertible to a float, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a float
	 */
	public static float getFloat(
		HttpServletRequest httpServletRequest, String param,
		float defaultValue) {

		return get(httpServletRequest, param, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a float. If the parameter
	 * is missing or not convertible to a float, <code>0</code> is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a float
	 */
	public static float getFloat(PortletRequest portletRequest, String param) {
		return GetterUtil.getFloat(portletRequest.getParameter(param));
	}

	/**
	 * Returns the portlet request parameter value as a float. If the parameter
	 * is missing or not convertible to a float, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a float
	 */
	public static float getFloat(
		PortletRequest portletRequest, String param, float defaultValue) {

		return get(portletRequest, param, defaultValue);
	}

	/**
	 * Returns the service context parameter value as a float. If the parameter
	 * is missing or not convertible to a float, <code>0</code> is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a float
	 */
	public static float getFloat(ServiceContext serviceContext, String param) {
		return GetterUtil.getFloat(serviceContext.getAttribute(param));
	}

	/**
	 * Returns the service context parameter value as a float. If the parameter
	 * is missing or not convertible to a float, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a float
	 */
	public static float getFloat(
		ServiceContext serviceContext, String param, float defaultValue) {

		return get(serviceContext, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as a float array. In the returned
	 * array, each parameter value not convertible to a float is replaced by
	 * <code>0</code>.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a float array
	 */
	public static float[] getFloatValues(
		HttpServletRequest httpServletRequest, String param) {

		return getFloatValues(httpServletRequest, param, new float[0]);
	}

	/**
	 * Returns the request parameter value as a float array. In the returned
	 * array, each parameter value not convertible to a float is replaced by the
	 * default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a float array
	 */
	public static float[] getFloatValues(
		HttpServletRequest httpServletRequest, String param,
		float[] defaultValue) {

		return GetterUtil.getFloatValues(
			getParameterValues(httpServletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a float array. In the
	 * returned array, each parameter value not convertible to a float is
	 * replaced by <code>0</code>.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a float array
	 */
	public static float[] getFloatValues(
		PortletRequest portletRequest, String param) {

		return getFloatValues(portletRequest, param, new float[0]);
	}

	/**
	 * Returns the portlet request parameter value as a float array. In the
	 * returned array, each parameter value not convertible to a float is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a float array
	 */
	public static float[] getFloatValues(
		PortletRequest portletRequest, String param, float[] defaultValue) {

		return GetterUtil.getFloatValues(
			getParameterValues(portletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a float array. In the
	 * returned array, each parameter value not convertible to a float is
	 * replaced by <code>0</code>.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a float array
	 */
	public static float[] getFloatValues(
		ServiceContext serviceContext, String param) {

		return getFloatValues(serviceContext, param, new float[0]);
	}

	/**
	 * Returns the service context parameter value as a float array. In the
	 * returned array, each parameter value not convertible to a float is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a float array
	 */
	public static float[] getFloatValues(
		ServiceContext serviceContext, String param, float[] defaultValue) {

		return GetterUtil.getFloatValues(
			serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as an integer. If the parameter is
	 * missing or not convertible to an integer, <code>0</code> is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as an integer
	 */
	public static int getInteger(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getInteger(httpServletRequest.getParameter(param));
	}

	/**
	 * Returns the request parameter value as an integer. If the parameter is
	 * missing or not convertible to an integer, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as an integer
	 */
	public static int getInteger(
		HttpServletRequest httpServletRequest, String param, int defaultValue) {

		return get(httpServletRequest, param, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as an integer. If the
	 * parameter is missing or not convertible to an integer, <code>0</code> is
	 * returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as an integer
	 */
	public static int getInteger(PortletRequest portletRequest, String param) {
		return GetterUtil.getInteger(portletRequest.getParameter(param));
	}

	/**
	 * Returns the portlet request parameter value as an integer. If the
	 * parameter is missing or not convertible to an integer, the default value
	 * is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as an integer
	 */
	public static int getInteger(
		PortletRequest portletRequest, String param, int defaultValue) {

		return get(portletRequest, param, defaultValue);
	}

	/**
	 * Returns the service context parameter value as an integer. If the
	 * parameter is missing or not convertible to an integer, <code>0</code> is
	 * returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as an integer
	 */
	public static int getInteger(ServiceContext serviceContext, String param) {
		return GetterUtil.getInteger(serviceContext.getAttribute(param));
	}

	/**
	 * Returns the service context parameter value as an integer. If the
	 * parameter is missing or not convertible to an integer, the default value
	 * is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as an integer
	 */
	public static int getInteger(
		ServiceContext serviceContext, String param, int defaultValue) {

		return get(serviceContext, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as an integer array. In the returned
	 * array, each parameter value not convertible to an integer is replaced by
	 * <code>0</code>.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as an integer
	 */
	public static int[] getIntegerValues(
		HttpServletRequest httpServletRequest, String param) {

		return getIntegerValues(httpServletRequest, param, new int[0]);
	}

	/**
	 * Returns the request parameter value as an integer array. In the returned
	 * array, each parameter value not convertible to an integer is replaced by
	 * the default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as an integer
	 */
	public static int[] getIntegerValues(
		HttpServletRequest httpServletRequest, String param,
		int[] defaultValue) {

		return GetterUtil.getIntegerValues(
			getParameterValues(httpServletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as an integer array. In the
	 * returned array, each parameter value not convertible to an integer is
	 * replaced by <code>0</code>.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as an integer
	 */
	public static int[] getIntegerValues(
		PortletRequest portletRequest, String param) {

		return getIntegerValues(portletRequest, param, new int[0]);
	}

	/**
	 * Returns the portlet request parameter value as an integer array. In the
	 * returned array, each parameter value not convertible to an integer is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as an integer
	 */
	public static int[] getIntegerValues(
		PortletRequest portletRequest, String param, int[] defaultValue) {

		return GetterUtil.getIntegerValues(
			getParameterValues(portletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the service context parameter value as an integer array. In the
	 * returned array, each parameter value not convertible to an integer is
	 * replaced by <code>0</code>.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as an integer
	 */
	public static int[] getIntegerValues(
		ServiceContext serviceContext, String param) {

		return getIntegerValues(serviceContext, param, new int[0]);
	}

	/**
	 * Returns the service context parameter value as an integer array. In the
	 * returned array, each parameter value not convertible to an integer is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as an integer
	 */
	public static int[] getIntegerValues(
		ServiceContext serviceContext, String param, int[] defaultValue) {

		return GetterUtil.getIntegerValues(
			serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a long. If the parameter is
	 * missing or not convertible to a long, <code>0</code> is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a long
	 */
	public static long getLong(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getLong(httpServletRequest.getParameter(param));
	}

	/**
	 * Returns the request parameter value as a long. If the parameter is
	 * missing or not convertible to a long, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a long
	 */
	public static long getLong(
		HttpServletRequest httpServletRequest, String param,
		long defaultValue) {

		return get(httpServletRequest, param, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a long. If the parameter
	 * is missing or not convertible to a long, <code>0</code> is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a long
	 */
	public static long getLong(PortletRequest portletRequest, String param) {
		return GetterUtil.getLong(portletRequest.getParameter(param));
	}

	/**
	 * Returns the portlet request parameter value as a long. If the parameter
	 * is missing or not convertible to a long, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a long
	 */
	public static long getLong(
		PortletRequest portletRequest, String param, long defaultValue) {

		return get(portletRequest, param, defaultValue);
	}

	/**
	 * Returns the service context parameter value as a long. If the parameter
	 * is missing or not convertible to a long, <code>0</code> is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a long
	 */
	public static long getLong(ServiceContext serviceContext, String param) {
		return GetterUtil.getLong(serviceContext.getAttribute(param));
	}

	/**
	 * Returns the service context parameter value as a long. If the parameter
	 * is missing or not convertible to a long, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a long
	 */
	public static long getLong(
		ServiceContext serviceContext, String param, long defaultValue) {

		return get(serviceContext, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as a long array. In the returned
	 * array, each parameter value not convertible to a long is replaced by
	 * <code>0</code>.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a long array
	 */
	public static long[] getLongValues(
		HttpServletRequest httpServletRequest, String param) {

		return getLongValues(httpServletRequest, param, new long[0]);
	}

	/**
	 * Returns the request parameter value as a long array. In the returned
	 * array, each parameter value not convertible to a long is replaced by the
	 * default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a long array
	 */
	public static long[] getLongValues(
		HttpServletRequest httpServletRequest, String param,
		long[] defaultValue) {

		return GetterUtil.getLongValues(
			getParameterValues(httpServletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a long array. In the
	 * returned array, each parameter value not convertible to a long is
	 * replaced by <code>0</code>.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a long array
	 */
	public static long[] getLongValues(
		PortletRequest portletRequest, String param) {

		return getLongValues(portletRequest, param, new long[0]);
	}

	/**
	 * Returns the portlet request parameter value as a long array. In the
	 * returned array, each parameter value not convertible to a long is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a long array
	 */
	public static long[] getLongValues(
		PortletRequest portletRequest, String param, long[] defaultValue) {

		return GetterUtil.getLongValues(
			getParameterValues(portletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a long array. In the
	 * returned array, each parameter value not convertible to a long is
	 * replaced by <code>0</code>.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a long array
	 */
	public static long[] getLongValues(
		ServiceContext serviceContext, String param) {

		return getLongValues(serviceContext, param, new long[0]);
	}

	/**
	 * Returns the service context parameter value as a long array. In the
	 * returned array, each parameter value not convertible to a long is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a long array
	 */
	public static long[] getLongValues(
		ServiceContext serviceContext, String param, long[] defaultValue) {

		return GetterUtil.getLongValues(
			serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a Number. If the parameter is
	 * missing or not convertible to a Number, <code>0</code> is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a Number
	 */
	public static Number getNumber(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getNumber(httpServletRequest.getParameter(param));
	}

	/**
	 * Returns the request parameter value as a Number. If the parameter is
	 * missing or not convertible to a Number, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a Number
	 */
	public static Number getNumber(
		HttpServletRequest httpServletRequest, String param,
		Number defaultValue) {

		return get(httpServletRequest, param, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a Number. If the parameter
	 * is missing or not convertible to a Number, <code>0</code> is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a Number
	 */
	public static Number getNumber(
		PortletRequest portletRequest, String param) {

		return GetterUtil.getNumber(portletRequest.getParameter(param));
	}

	/**
	 * Returns the portlet request parameter value as a Number. If the parameter
	 * is missing or not convertible to a Number, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a Number
	 */
	public static Number getNumber(
		PortletRequest portletRequest, String param, Number defaultValue) {

		return get(portletRequest, param, defaultValue);
	}

	/**
	 * Returns the service context parameter value as a Number. If the parameter
	 * is missing or not convertible to a Number, <code>0</code> is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a Number
	 */
	public static Number getNumber(
		ServiceContext serviceContext, String param) {

		return GetterUtil.getNumber(serviceContext.getAttribute(param));
	}

	/**
	 * Returns the service context parameter value as a Number. If the parameter
	 * is missing or not convertible to a Number, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a Number
	 */
	public static Number getNumber(
		ServiceContext serviceContext, String param, Number defaultValue) {

		return get(serviceContext, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as a Number array. In the returned
	 * array, each parameter value not convertible to a Number is replaced by
	 * <code>0</code>.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a Number array
	 */
	public static Number[] getNumberValues(
		HttpServletRequest httpServletRequest, String param) {

		return getNumberValues(httpServletRequest, param, new Number[0]);
	}

	/**
	 * Returns the request parameter value as a Number array. In the returned
	 * array, each parameter value not convertible to a Number is replaced by
	 * the default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a Number array
	 */
	public static Number[] getNumberValues(
		HttpServletRequest httpServletRequest, String param,
		Number[] defaultValue) {

		return GetterUtil.getNumberValues(
			getParameterValues(httpServletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a Number array. In the
	 * returned array, each parameter value not convertible to a Number is
	 * replaced by <code>0</code>.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a Number array
	 */
	public static Number[] getNumberValues(
		PortletRequest portletRequest, String param) {

		return getNumberValues(portletRequest, param, new Number[0]);
	}

	/**
	 * Returns the portlet request parameter value as a Number array. In the
	 * returned array, each parameter value not convertible to a Number is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a Number array
	 */
	public static Number[] getNumberValues(
		PortletRequest portletRequest, String param, Number[] defaultValue) {

		return GetterUtil.getNumberValues(
			getParameterValues(portletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a Number array. In the
	 * returned array, each parameter value not convertible to a Number is
	 * replaced by <code>0</code>.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service request parameter value as a Number array
	 */
	public static Number[] getNumberValues(
		ServiceContext serviceContext, String param) {

		return getNumberValues(serviceContext, param, new Number[0]);
	}

	/**
	 * Returns the service context parameter value as a Number array. In the
	 * returned array, each parameter value not convertible to a Number is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service request parameter value as a Number array
	 */
	public static Number[] getNumberValues(
		ServiceContext serviceContext, String param, Number[] defaultValue) {

		return GetterUtil.getNumberValues(
			serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a String array. In the returned
	 * array, each parameter value not convertible to a String is replaced by a
	 * blank string.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a String array
	 */
	public static String[] getParameterValues(
		HttpServletRequest httpServletRequest, String param) {

		return getParameterValues(httpServletRequest, param, new String[0]);
	}

	/**
	 * Returns the request parameter value as a String array. In the returned
	 * array, each parameter value not convertible to a String is replaced by
	 * the default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a String array
	 */
	public static String[] getParameterValues(
		HttpServletRequest httpServletRequest, String param,
		String[] defaultValue) {

		return getParameterValues(
			httpServletRequest, param, defaultValue, true);
	}

	/**
	 * Returns the request parameter value as a String array. In the returned
	 * array, each parameter value not convertible to a String is replaced by
	 * the default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @param  split whether to split the single parameter value using comma
	 *         separators to get multiple values
	 * @return the request parameter value as a String array
	 */
	public static String[] getParameterValues(
		HttpServletRequest httpServletRequest, String param,
		String[] defaultValue, boolean split) {

		String[] values = httpServletRequest.getParameterValues(param);

		if (values == null) {
			return _normalize(defaultValue);
		}

		if (split && (values.length == 1)) {
			return _normalize(StringUtil.split(values[0]));
		}

		return _normalize(values);
	}

	/**
	 * Returns the portlet request parameter value as a String array. In the
	 * returned array, each parameter value not convertible to a String is
	 * replaced by a blank string.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a String array
	 */
	public static String[] getParameterValues(
		PortletRequest portletRequest, String param) {

		return getParameterValues(portletRequest, param, new String[0]);
	}

	/**
	 * Returns the portlet request parameter value as a String array. In the
	 * returned array, each parameter value not convertible to a String is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a String array
	 */
	public static String[] getParameterValues(
		PortletRequest portletRequest, String param, String[] defaultValue) {

		return getParameterValues(portletRequest, param, defaultValue, true);
	}

	/**
	 * Returns the portlet request parameter value as a String array. In the
	 * returned array, each parameter value not convertible to a String is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @param  split whether to split the single parameter value using comma
	 *         separators to get multiple values
	 * @return the portlet request parameter value as a String array
	 */
	public static String[] getParameterValues(
		PortletRequest portletRequest, String param, String[] defaultValue,
		boolean split) {

		return getParameterValues(
			PortalUtil.getHttpServletRequest(portletRequest), param,
			defaultValue, split);
	}

	/**
	 * Returns the request parameter value as a short. If the parameter is
	 * missing or not convertible to a short, <code>0</code> is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a short
	 */
	public static short getShort(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getShort(httpServletRequest.getParameter(param));
	}

	/**
	 * Returns the request parameter value as a short. If the parameter is
	 * missing or not convertible to a short, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a short
	 */
	public static short getShort(
		HttpServletRequest httpServletRequest, String param,
		short defaultValue) {

		return get(httpServletRequest, param, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a short. If the parameter
	 * is missing or not convertible to a short, <code>0</code> is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a short
	 */
	public static short getShort(PortletRequest portletRequest, String param) {
		return GetterUtil.getShort(portletRequest.getParameter(param));
	}

	/**
	 * Returns the portlet request parameter value as a short. If the parameter
	 * is missing or not convertible to a short, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a short
	 */
	public static short getShort(
		PortletRequest portletRequest, String param, short defaultValue) {

		return get(portletRequest, param, defaultValue);
	}

	/**
	 * Returns the service context parameter value as a short. If the parameter
	 * is missing or not convertible to a short, <code>0</code> is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a short
	 */
	public static short getShort(ServiceContext serviceContext, String param) {
		return GetterUtil.getShort(serviceContext.getAttribute(param));
	}

	/**
	 * Returns the service context parameter value as a short. If the parameter
	 * is missing or not convertible to a short, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a short
	 */
	public static short getShort(
		ServiceContext serviceContext, String param, short defaultValue) {

		return get(serviceContext, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as a short array. In the returned
	 * array, each parameter value not convertible to a short is replaced by
	 * <code>0</code>.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a short array
	 */
	public static short[] getShortValues(
		HttpServletRequest httpServletRequest, String param) {

		return getShortValues(httpServletRequest, param, new short[0]);
	}

	/**
	 * Returns the request parameter value as a short array. In the returned
	 * array, each parameter value not convertible to a short is replaced by the
	 * default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a short array
	 */
	public static short[] getShortValues(
		HttpServletRequest httpServletRequest, String param,
		short[] defaultValue) {

		return GetterUtil.getShortValues(
			getParameterValues(httpServletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a short array. In the
	 * returned array, each parameter value not convertible to a short is
	 * replaced by <code>0</code>.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a short array
	 */
	public static short[] getShortValues(
		PortletRequest portletRequest, String param) {

		return getShortValues(portletRequest, param, new short[0]);
	}

	/**
	 * Returns the portlet request parameter value as a short array. In the
	 * returned array, each parameter value not convertible to a short is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a short array
	 */
	public static short[] getShortValues(
		PortletRequest portletRequest, String param, short[] defaultValue) {

		return GetterUtil.getShortValues(
			getParameterValues(portletRequest, param, null), defaultValue);
	}

	/**
	 * Returns the service context parameter value as a short array. In the
	 * returned array, each parameter value not convertible to a short is
	 * replaced by <code>0</code>.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a short array
	 */
	public static short[] getShortValues(
		ServiceContext serviceContext, String param) {

		return getShortValues(serviceContext, param, new short[0]);
	}

	/**
	 * Returns the service context parameter value as a short array. In the
	 * returned array, each parameter value not convertible to a short is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a short array
	 */
	public static short[] getShortValues(
		ServiceContext serviceContext, String param, short[] defaultValue) {

		return GetterUtil.getShortValues(
			serviceContext.getAttribute(param), defaultValue);
	}

	/**
	 * Returns the request parameter value as a String. If the parameter is
	 * missing or not convertible to a String, a blank string is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a String
	 */
	public static String getString(
		HttpServletRequest httpServletRequest, String param) {

		return _normalize(
			GetterUtil.getString(httpServletRequest.getParameter(param)));
	}

	/**
	 * Returns the request parameter value as a String. If the parameter is
	 * missing or not convertible to a String, the default value is returned.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a String
	 */
	public static String getString(
		HttpServletRequest httpServletRequest, String param,
		String defaultValue) {

		return get(httpServletRequest, param, defaultValue);
	}

	/**
	 * Returns the portlet request parameter value as a String. If the parameter
	 * is missing or not convertible to a String, a blank string is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a String
	 */
	public static String getString(
		PortletRequest portletRequest, String param) {

		return _normalize(
			GetterUtil.getString(portletRequest.getParameter(param)));
	}

	/**
	 * Returns the portlet request parameter value as a String. If the parameter
	 * is missing or not convertible to a String, the default value is returned.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a String
	 */
	public static String getString(
		PortletRequest portletRequest, String param, String defaultValue) {

		return get(portletRequest, param, defaultValue);
	}

	/**
	 * Returns the service context parameter value as a String. If the parameter
	 * is missing or not convertible to a String, a blank string is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a String
	 */
	public static String getString(
		ServiceContext serviceContext, String param) {

		return _normalize(
			GetterUtil.getString(serviceContext.getAttribute(param)));
	}

	/**
	 * Returns the service context parameter value as a String. If the parameter
	 * is missing or not convertible to a String, the default value is returned.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a String
	 */
	public static String getString(
		ServiceContext serviceContext, String param, String defaultValue) {

		return get(serviceContext, param, defaultValue);
	}

	/**
	 * Returns the request parameter value as a String array. In the returned
	 * array, each parameter value not convertible to a String is replaced by a
	 * blank string.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the request parameter value as a String array
	 */
	public static String[] getStringValues(
		HttpServletRequest httpServletRequest, String param) {

		return getStringValues(httpServletRequest, param, new String[0]);
	}

	/**
	 * Returns the request parameter value as a String array. In the returned
	 * array, each parameter value not convertible to a String is replaced by
	 * the default value.
	 *
	 * @param  httpServletRequest the servlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the request parameter value as a String array
	 */
	public static String[] getStringValues(
		HttpServletRequest httpServletRequest, String param,
		String[] defaultValue) {

		return GetterUtil.getStringValues(
			getParameterValues(httpServletRequest, param, null),
			() -> _normalize(defaultValue));
	}

	/**
	 * Returns the portlet request parameter value as a String array. In the
	 * returned array, each parameter value not convertible to a String is
	 * replaced by a blank string.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the portlet request parameter value as a String array
	 */
	public static String[] getStringValues(
		PortletRequest portletRequest, String param) {

		return getStringValues(portletRequest, param, new String[0]);
	}

	/**
	 * Returns the portlet request parameter value as a String array. In the
	 * returned array, each parameter value not convertible to a String is
	 * replaced by the default value.
	 *
	 * @param  portletRequest the portlet request from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the portlet request parameter value as a String array
	 */
	public static String[] getStringValues(
		PortletRequest portletRequest, String param, String[] defaultValue) {

		return GetterUtil.getStringValues(
			getParameterValues(portletRequest, param, null),
			() -> _normalize(defaultValue));
	}

	/**
	 * Returns the service context parameter value as a String array. In the
	 * returned array, each parameter value not convertible to a String is
	 * replaced by a blank string.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @return the service context parameter value as a String array
	 */
	public static String[] getStringValues(
		ServiceContext serviceContext, String param) {

		return getStringValues(serviceContext, param, new String[0]);
	}

	/**
	 * Returns the service context parameter value as a String array. In the
	 * returned array, each parameter value not convertible to a String is
	 * replaced by the default value.
	 *
	 * @param  serviceContext the service context from which to read the
	 *         parameter
	 * @param  param the name of the parameter
	 * @param  defaultValue a default value
	 * @return the service context parameter value as a String array
	 */
	public static String[] getStringValues(
		ServiceContext serviceContext, String param, String[] defaultValue) {

		return GetterUtil.getStringValues(
			serviceContext.getAttribute(param), () -> _normalize(defaultValue));
	}

	/**
	 * Prints all the request parameters as standard output.
	 *
	 * @param httpServletRequest the servlet request from which to read the
	 *        parameters
	 */
	public static void print(HttpServletRequest httpServletRequest) {
		Map<String, String[]> parameters = httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String name = entry.getKey();
			String[] values = entry.getValue();

			for (int i = 0; i < values.length; i++) {
				System.out.println(
					StringBundler.concat(name, "[", i, "] = ", values[i]));
			}
		}
	}

	/**
	 * Prints all the portlet request parameters as standard output.
	 *
	 * @param portletRequest the portlet request from which to read the
	 *        parameters
	 */
	public static void print(PortletRequest portletRequest) {
		Enumeration<String> enumeration = portletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String param = enumeration.nextElement();

			String[] values = portletRequest.getParameterValues(param);

			for (int i = 0; i < values.length; i++) {
				System.out.println(
					StringBundler.concat(param, "[", i, "] = ", values[i]));
			}
		}
	}

	/**
	 * Prints all the service context parameters as standard output.
	 *
	 * @param serviceContext the service context from which to read the
	 *        parameters
	 */
	public static void print(ServiceContext serviceContext) {
		Map<String, Serializable> attributes = serviceContext.getAttributes();

		for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
			System.out.println(
				entry.getKey() + " = " + String.valueOf(entry.getValue()));
		}
	}

	private static String _normalize(String input) {
		if ((_FORM == null) || Validator.isNull(input)) {
			return input;
		}

		return Normalizer.normalize(input, _FORM);
	}

	private static String[] _normalize(String[] input) {
		if ((_FORM == null) || ArrayUtil.isEmpty(input)) {
			return input;
		}

		for (int i = 0; i < input.length; i++) {
			input[i] = Normalizer.normalize(input[i], _FORM);
		}

		return input;
	}

	private static final Normalizer.Form _FORM;

	private static final Log _log = LogFactoryUtil.getLog(ParamUtil.class);

	static {
		String formString = PropsUtil.get(
			PropsKeys.UNICODE_TEXT_NORMALIZER_FORM);

		if ((formString == null) || formString.isEmpty()) {
			_FORM = null;
		}
		else {
			Normalizer.Form form = null;

			try {
				form = Normalizer.Form.valueOf(formString);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalArgumentException);
				}

				form = null;
			}

			_FORM = form;
		}
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class SessionParamUtil {

	public static boolean getBoolean(
		HttpServletRequest httpServletRequest, String param) {

		return getBoolean(
			httpServletRequest, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		HttpServletRequest httpServletRequest, String param,
		boolean defaultValue) {

		HttpSession httpSession = httpServletRequest.getSession(false);

		String requestValue = httpServletRequest.getParameter(param);

		if (requestValue != null) {
			boolean value = GetterUtil.getBoolean(requestValue);

			if (httpSession != null) {
				httpSession.setAttribute(param, value);
			}

			return value;
		}

		if (httpSession != null) {
			Boolean sessionValue = (Boolean)httpSession.getAttribute(param);

			if (sessionValue != null) {
				return sessionValue;
			}
		}

		return defaultValue;
	}

	public static boolean getBoolean(
		PortletRequest portletRequest, String param) {

		return getBoolean(portletRequest, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		PortletRequest portletRequest, String param, boolean defaultValue) {

		PortletSession portletSession = portletRequest.getPortletSession(false);

		String portletRequestValue = portletRequest.getParameter(param);

		if (portletRequestValue != null) {
			boolean value = GetterUtil.getBoolean(portletRequestValue);

			portletSession.setAttribute(param, value);

			return value;
		}

		if (portletSession != null) {
			Boolean portletSessionValue = (Boolean)portletSession.getAttribute(
				param);

			if (portletSessionValue != null) {
				return portletSessionValue;
			}
		}

		return defaultValue;
	}

	public static double getDouble(
		HttpServletRequest httpServletRequest, String param) {

		return getDouble(httpServletRequest, param, GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		HttpServletRequest httpServletRequest, String param,
		double defaultValue) {

		HttpSession httpSession = httpServletRequest.getSession(false);

		String requestValue = httpServletRequest.getParameter(param);

		if (requestValue != null) {
			double value = GetterUtil.getDouble(requestValue);

			if (httpSession != null) {
				httpSession.setAttribute(param, value);
			}

			return value;
		}

		if (httpSession != null) {
			Double sessionValue = (Double)httpSession.getAttribute(param);

			if (sessionValue != null) {
				return sessionValue;
			}
		}

		return defaultValue;
	}

	public static double getDouble(
		HttpServletRequest httpServletRequest, String param,
		double defaultValue, Locale locale) {

		HttpSession httpSession = httpServletRequest.getSession(false);

		String requestValue = httpServletRequest.getParameter(param);

		if (requestValue != null) {
			double value = GetterUtil.getDouble(requestValue, locale);

			if (httpSession != null) {
				httpSession.setAttribute(param, value);
			}

			return value;
		}

		if (httpSession != null) {
			Double sessionValue = (Double)httpSession.getAttribute(param);

			if (sessionValue != null) {
				return sessionValue;
			}
		}

		return defaultValue;
	}

	public static double getDouble(
		HttpServletRequest httpServletRequest, String param, Locale locale) {

		return getDouble(
			httpServletRequest, param, GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static double getDouble(
		PortletRequest portletRequest, String param) {

		return getDouble(portletRequest, param, GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		PortletRequest portletRequest, String param, double defaultValue) {

		PortletSession portletSession = portletRequest.getPortletSession(false);

		String portletRequestValue = portletRequest.getParameter(param);

		if (portletRequestValue != null) {
			double value = GetterUtil.getDouble(portletRequestValue);

			portletSession.setAttribute(param, value);

			return value;
		}

		if (portletSession != null) {
			Double portletSessionValue = (Double)portletSession.getAttribute(
				param);

			if (portletSessionValue != null) {
				return portletSessionValue;
			}
		}

		return defaultValue;
	}

	public static double getDouble(
		PortletRequest portletRequest, String param, double defaultValue,
		Locale locale) {

		PortletSession portletSession = portletRequest.getPortletSession(false);

		String portletRequestValue = portletRequest.getParameter(param);

		if (portletRequestValue != null) {
			double value = GetterUtil.getDouble(portletRequestValue, locale);

			portletSession.setAttribute(param, value);

			return value;
		}

		if (portletSession != null) {
			Double portletSessionValue = (Double)portletSession.getAttribute(
				param);

			if (portletSessionValue != null) {
				return portletSessionValue;
			}
		}

		return defaultValue;
	}

	public static double getDouble(
		PortletRequest portletRequest, String param, Locale locale) {

		return getDouble(
			portletRequest, param, GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static int getInteger(
		HttpServletRequest httpServletRequest, String param) {

		return getInteger(
			httpServletRequest, param, GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		HttpServletRequest httpServletRequest, String param, int defaultValue) {

		HttpSession httpSession = httpServletRequest.getSession(false);

		String requestValue = httpServletRequest.getParameter(param);

		if (requestValue != null) {
			int value = GetterUtil.getInteger(requestValue);

			if (httpSession != null) {
				httpSession.setAttribute(param, value);
			}

			return value;
		}

		if (httpSession != null) {
			Integer sessionValue = (Integer)httpSession.getAttribute(param);

			if (sessionValue != null) {
				return sessionValue;
			}
		}

		return defaultValue;
	}

	public static int getInteger(PortletRequest portletRequest, String param) {
		return getInteger(portletRequest, param, GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		PortletRequest portletRequest, String param, int defaultValue) {

		PortletSession portletSession = portletRequest.getPortletSession(false);

		String portletRequestValue = portletRequest.getParameter(param);

		if (portletRequestValue != null) {
			int value = GetterUtil.getInteger(portletRequestValue);

			portletSession.setAttribute(param, value);

			return value;
		}

		if (portletSession != null) {
			Integer portletSessionValue = (Integer)portletSession.getAttribute(
				param);

			if (portletSessionValue != null) {
				return portletSessionValue;
			}
		}

		return defaultValue;
	}

	public static long getLong(
		HttpServletRequest httpServletRequest, String param) {

		return getLong(httpServletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		HttpServletRequest httpServletRequest, String param,
		long defaultValue) {

		HttpSession httpSession = httpServletRequest.getSession(false);

		String requestValue = httpServletRequest.getParameter(param);

		if (requestValue != null) {
			long value = GetterUtil.getLong(requestValue);

			if (httpSession != null) {
				httpSession.setAttribute(param, value);
			}

			return value;
		}

		if (httpSession != null) {
			Long sessionValue = (Long)httpSession.getAttribute(param);

			if (sessionValue != null) {
				return sessionValue;
			}
		}

		return defaultValue;
	}

	public static long getLong(PortletRequest portletRequest, String param) {
		return getLong(portletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		PortletRequest portletRequest, String param, long defaultValue) {

		PortletSession portletSession = portletRequest.getPortletSession(false);

		String portletRequestValue = portletRequest.getParameter(param);

		if (portletRequestValue != null) {
			long value = GetterUtil.getLong(portletRequestValue);

			portletSession.setAttribute(param, value);

			return value;
		}

		if (portletSession != null) {
			Long portletSessionValue = (Long)portletSession.getAttribute(param);

			if (portletSessionValue != null) {
				return portletSessionValue;
			}
		}

		return defaultValue;
	}

	public static short getShort(
		HttpServletRequest httpServletRequest, String param) {

		return getShort(httpServletRequest, param, GetterUtil.DEFAULT_SHORT);
	}

	public static short getShort(
		HttpServletRequest httpServletRequest, String param,
		short defaultValue) {

		HttpSession httpSession = httpServletRequest.getSession(false);

		String requestValue = httpServletRequest.getParameter(param);

		if (requestValue != null) {
			short value = GetterUtil.getShort(requestValue);

			if (httpSession != null) {
				httpSession.setAttribute(param, value);
			}

			return value;
		}

		if (httpSession != null) {
			Short sessionValue = (Short)httpSession.getAttribute(param);

			if (sessionValue != null) {
				return sessionValue;
			}
		}

		return defaultValue;
	}

	public static short getShort(PortletRequest portletRequest, String param) {
		return getShort(portletRequest, param, GetterUtil.DEFAULT_SHORT);
	}

	public static short getShort(
		PortletRequest portletRequest, String param, short defaultValue) {

		PortletSession portletSession = portletRequest.getPortletSession(false);

		String portletRequestValue = portletRequest.getParameter(param);

		if (portletRequestValue != null) {
			short value = GetterUtil.getShort(portletRequestValue);

			portletSession.setAttribute(param, value);

			return value;
		}

		if (portletSession != null) {
			Short portletSessionValue = (Short)portletSession.getAttribute(
				param);

			if (portletSessionValue != null) {
				return portletSessionValue;
			}
		}

		return defaultValue;
	}

	public static String getString(
		HttpServletRequest httpServletRequest, String param) {

		return getString(httpServletRequest, param, GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		HttpServletRequest httpServletRequest, String param,
		String defaultValue) {

		HttpSession httpSession = httpServletRequest.getSession(false);

		String requestValue = httpServletRequest.getParameter(param);

		if (requestValue != null) {
			String value = GetterUtil.getString(requestValue);

			if (httpSession != null) {
				httpSession.setAttribute(param, value);
			}

			return value;
		}

		if (httpSession != null) {
			String sessionValue = (String)httpSession.getAttribute(param);

			if (sessionValue != null) {
				return sessionValue;
			}
		}

		return defaultValue;
	}

	public static String getString(
		PortletRequest portletRequest, String param) {

		return getString(portletRequest, param, GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		PortletRequest portletRequest, String param, String defaultValue) {

		PortletSession portletSession = portletRequest.getPortletSession(false);

		String portletRequestValue = portletRequest.getParameter(param);

		if (portletRequestValue != null) {
			String value = GetterUtil.getString(portletRequestValue);

			portletSession.setAttribute(param, value);

			return value;
		}

		if (portletSession != null) {
			String portletSessionValue = (String)portletSession.getAttribute(
				param);

			if (portletSessionValue != null) {
				return portletSessionValue;
			}
		}

		return defaultValue;
	}

}
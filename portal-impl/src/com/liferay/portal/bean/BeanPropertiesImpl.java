/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.bean;

import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import jodd.bean.BeanCopy;
import jodd.bean.BeanUtil;

import jodd.typeconverter.Converter;

/**
 * @author Brian Wing Shun Chan
 */
public class BeanPropertiesImpl implements BeanProperties {

	@Override
	public void copyProperties(Object source, Object target) {
		try {
			BeanCopy beanCopy = new BeanCopy(source, target);

			beanCopy.copy();
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public <T> T deepCopyProperties(Object source) throws Exception {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				unsyncByteArrayOutputStream)) {

			objectOutputStream.writeObject(source);

			objectOutputStream.flush();

			try (UnsyncByteArrayInputStream unsyncByteArrayInputStream =
					new UnsyncByteArrayInputStream(
						unsyncByteArrayOutputStream.toByteArray());
				ObjectInputStream objectInputStream = new ObjectInputStream(
					unsyncByteArrayInputStream)) {

				return (T)objectInputStream.readObject();
			}
		}
	}

	@Override
	public boolean getBoolean(Object bean, String param) {
		return getBoolean(bean, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	@Override
	public boolean getBoolean(Object bean, String param, boolean defaultValue) {
		boolean beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toBooleanValue(value, defaultValue);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return beanValue;
	}

	@Override
	public boolean getBooleanSilent(Object bean, String param) {
		return getBooleanSilent(bean, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	@Override
	public boolean getBooleanSilent(
		Object bean, String param, boolean defaultValue) {

		boolean beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toBooleanValue(value, defaultValue);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return beanValue;
	}

	@Override
	public byte getByte(Object bean, String param) {
		return getByte(bean, param, GetterUtil.DEFAULT_BYTE);
	}

	@Override
	public byte getByte(Object bean, String param, byte defaultValue) {
		byte beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toByteValue(value, defaultValue);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return beanValue;
	}

	@Override
	public byte getByteSilent(Object bean, String param) {
		return getByteSilent(bean, param, GetterUtil.DEFAULT_BYTE);
	}

	@Override
	public byte getByteSilent(Object bean, String param, byte defaultValue) {
		byte beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toByteValue(value, defaultValue);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return beanValue;
	}

	@Override
	public double getDouble(Object bean, String param) {
		return getDouble(bean, param, GetterUtil.DEFAULT_DOUBLE);
	}

	@Override
	public double getDouble(Object bean, String param, double defaultValue) {
		double beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toDoubleValue(value, defaultValue);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return beanValue;
	}

	@Override
	public double getDoubleSilent(Object bean, String param) {
		return getDoubleSilent(bean, param, GetterUtil.DEFAULT_DOUBLE);
	}

	@Override
	public double getDoubleSilent(
		Object bean, String param, double defaultValue) {

		double beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toDoubleValue(value, defaultValue);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return beanValue;
	}

	@Override
	public float getFloat(Object bean, String param) {
		return getFloat(bean, param, GetterUtil.DEFAULT_FLOAT);
	}

	@Override
	public float getFloat(Object bean, String param, float defaultValue) {
		float beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toFloatValue(value, defaultValue);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return beanValue;
	}

	@Override
	public float getFloatSilent(Object bean, String param) {
		return getFloatSilent(bean, param, GetterUtil.DEFAULT_FLOAT);
	}

	@Override
	public float getFloatSilent(Object bean, String param, float defaultValue) {
		float beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toFloatValue(value, defaultValue);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return beanValue;
	}

	@Override
	public int getInteger(Object bean, String param) {
		return getInteger(bean, param, GetterUtil.DEFAULT_INTEGER);
	}

	@Override
	public int getInteger(Object bean, String param, int defaultValue) {
		int beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toIntValue(value, defaultValue);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return beanValue;
	}

	@Override
	public int getIntegerSilent(Object bean, String param) {
		return getIntegerSilent(bean, param, GetterUtil.DEFAULT_INTEGER);
	}

	@Override
	public int getIntegerSilent(Object bean, String param, int defaultValue) {
		int beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toIntValue(value, defaultValue);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return beanValue;
	}

	@Override
	public long getLong(Object bean, String param) {
		return getLong(bean, param, GetterUtil.DEFAULT_LONG);
	}

	@Override
	public long getLong(Object bean, String param, long defaultValue) {
		long beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toLongValue(value, defaultValue);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return beanValue;
	}

	@Override
	public long getLongSilent(Object bean, String param) {
		return getLongSilent(bean, param, GetterUtil.DEFAULT_LONG);
	}

	@Override
	public long getLongSilent(Object bean, String param, long defaultValue) {
		long beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toLongValue(value, defaultValue);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return beanValue;
	}

	@Override
	public Object getObject(Object bean, String param) {
		return getObject(bean, param, null);
	}

	@Override
	public Object getObject(Object bean, String param, Object defaultValue) {
		Object beanValue = null;

		if (bean != null) {
			try {
				beanValue = BeanUtil.pojo.getProperty(bean, param);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		if (beanValue == null) {
			return defaultValue;
		}

		return beanValue;
	}

	@Override
	public Object getObjectSilent(Object bean, String param) {
		return getObjectSilent(bean, param, null);
	}

	@Override
	public Object getObjectSilent(
		Object bean, String param, Object defaultValue) {

		Object beanValue = null;

		if (bean != null) {
			try {
				beanValue = BeanUtil.pojo.getProperty(bean, param);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		if (beanValue == null) {
			return defaultValue;
		}

		return beanValue;
	}

	@Override
	public Class<?> getObjectType(Object bean, String param) {
		return getObjectType(bean, param, null);
	}

	@Override
	public Class<?> getObjectType(
		Object bean, String param, Class<?> defaultValue) {

		Class<?> beanType = null;

		if (bean != null) {
			try {
				beanType = BeanUtil.pojo.getPropertyType(bean, param);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		if (beanType == null) {
			return defaultValue;
		}

		return beanType;
	}

	@Override
	public Class<?> getObjectTypeSilent(Object bean, String param) {
		return getObjectTypeSilent(bean, param, null);
	}

	@Override
	public Class<?> getObjectTypeSilent(
		Object bean, String param, Class<?> defaultValue) {

		Class<?> beanType = null;

		if (bean != null) {
			try {
				beanType = BeanUtil.pojo.getPropertyType(bean, param);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		if (beanType == null) {
			return defaultValue;
		}

		return beanType;
	}

	@Override
	public short getShort(Object bean, String param) {
		return getShort(bean, param, GetterUtil.DEFAULT_SHORT);
	}

	@Override
	public short getShort(Object bean, String param, short defaultValue) {
		short beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toShortValue(value, defaultValue);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return beanValue;
	}

	@Override
	public short getShortSilent(Object bean, String param) {
		return getShortSilent(bean, param, GetterUtil.DEFAULT_SHORT);
	}

	@Override
	public short getShortSilent(Object bean, String param, short defaultValue) {
		short beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toShortValue(value, defaultValue);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return beanValue;
	}

	@Override
	public String getString(Object bean, String param) {
		return getString(bean, param, GetterUtil.DEFAULT_STRING);
	}

	@Override
	public String getString(Object bean, String param, String defaultValue) {
		String beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toString(value, defaultValue);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return beanValue;
	}

	@Override
	public String getStringSilent(Object bean, String param) {
		return getStringSilent(bean, param, GetterUtil.DEFAULT_STRING);
	}

	@Override
	public String getStringSilent(
		Object bean, String param, String defaultValue) {

		String beanValue = defaultValue;

		if (bean != null) {
			try {
				Object value = BeanUtil.pojo.getProperty(bean, param);

				beanValue = _converter.toString(value, defaultValue);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return beanValue;
	}

	@Override
	public void setProperties(
		Object bean, HttpServletRequest httpServletRequest) {

		setProperties(bean, httpServletRequest, new String[0]);
	}

	@Override
	public void setProperties(
		Object bean, HttpServletRequest httpServletRequest,
		String[] ignoreProperties) {

		Enumeration<String> enumeration =
			httpServletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (ArrayUtil.contains(ignoreProperties, name)) {
				continue;
			}

			String value = httpServletRequest.getParameter(name);

			if (Validator.isNull(value) &&
				(getObjectSilent(bean, name) instanceof Number)) {

				value = String.valueOf(0);
			}

			BeanUtil.forcedSilent.setProperty(bean, name, value);

			if (name.endsWith("Month")) {
				String dateParam = name.substring(0, name.lastIndexOf("Month"));

				if (httpServletRequest.getParameter(dateParam) != null) {
					continue;
				}

				Class<?> propertyTypeClass = BeanUtil.pojo.getPropertyType(
					bean, dateParam);

				if ((propertyTypeClass == null) ||
					!propertyTypeClass.equals(Date.class)) {

					continue;
				}

				Date date = getDate(dateParam, httpServletRequest);

				if (date != null) {
					BeanUtil.forcedSilent.setProperty(bean, dateParam, date);
				}
			}
		}
	}

	@Override
	public void setProperty(Object bean, String param, Object value) {
		try {
			BeanUtil.pojo.setProperty(bean, param, value);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public void setPropertySilent(Object bean, String param, Object value) {
		BeanUtil.forcedSilent.setProperty(bean, param, value);
	}

	protected Date getDate(
		String param, HttpServletRequest httpServletRequest) {

		int month = ParamUtil.getInteger(httpServletRequest, param + "Month");
		int day = ParamUtil.getInteger(httpServletRequest, param + "Day");
		int year = ParamUtil.getInteger(httpServletRequest, param + "Year");
		int hour = ParamUtil.getInteger(httpServletRequest, param + "Hour", -1);

		int amPm = ParamUtil.getInteger(httpServletRequest, param + "AmPm");

		if (amPm == Calendar.PM) {
			hour += 12;
		}

		if (hour == -1) {
			return PortalUtil.getDate(month, day, year);
		}

		int minute = ParamUtil.getInteger(httpServletRequest, param + "Minute");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		try {
			return PortalUtil.getDate(
				month, day, year, hour, minute, user.getTimeZone(), null);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BeanPropertiesImpl.class);

	private final Converter _converter = Converter.get();

}
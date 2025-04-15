/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.bean;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface BeanProperties {

	public void copyProperties(Object source, Object target);

	public <T> T deepCopyProperties(Object source) throws Exception;

	public boolean getBoolean(Object bean, String param);

	public boolean getBoolean(Object bean, String param, boolean defaultValue);

	public boolean getBooleanSilent(Object bean, String param);

	public boolean getBooleanSilent(
		Object bean, String param, boolean defaultValue);

	public byte getByte(Object bean, String param);

	public byte getByte(Object bean, String param, byte defaultValue);

	public byte getByteSilent(Object bean, String param);

	public byte getByteSilent(Object bean, String param, byte defaultValue);

	public double getDouble(Object bean, String param);

	public double getDouble(Object bean, String param, double defaultValue);

	public double getDoubleSilent(Object bean, String param);

	public double getDoubleSilent(
		Object bean, String param, double defaultValue);

	public float getFloat(Object bean, String param);

	public float getFloat(Object bean, String param, float defaultValue);

	public float getFloatSilent(Object bean, String param);

	public float getFloatSilent(Object bean, String param, float defaultValue);

	public int getInteger(Object bean, String param);

	public int getInteger(Object bean, String param, int defaultValue);

	public int getIntegerSilent(Object bean, String param);

	public int getIntegerSilent(Object bean, String param, int defaultValue);

	public long getLong(Object bean, String param);

	public long getLong(Object bean, String param, long defaultValue);

	public long getLongSilent(Object bean, String param);

	public long getLongSilent(Object bean, String param, long defaultValue);

	public Object getObject(Object bean, String param);

	public Object getObject(Object bean, String param, Object defaultValue);

	public Object getObjectSilent(Object bean, String param);

	public Object getObjectSilent(
		Object bean, String param, Object defaultValue);

	public Class<?> getObjectType(Object bean, String param);

	public Class<?> getObjectType(
		Object bean, String param, Class<?> defaultValue);

	public Class<?> getObjectTypeSilent(Object bean, String param);

	public Class<?> getObjectTypeSilent(
		Object bean, String param, Class<?> defaultValue);

	public short getShort(Object bean, String param);

	public short getShort(Object bean, String param, short defaultValue);

	public short getShortSilent(Object bean, String param);

	public short getShortSilent(Object bean, String param, short defaultValue);

	public String getString(Object bean, String param);

	public String getString(Object bean, String param, String defaultValue);

	public String getStringSilent(Object bean, String param);

	public String getStringSilent(
		Object bean, String param, String defaultValue);

	public void setProperties(
		Object bean, HttpServletRequest httpServletRequest);

	public void setProperties(
		Object bean, HttpServletRequest httpServletRequest,
		String[] ignoreProperties);

	public void setProperty(Object bean, String param, Object value);

	public void setPropertySilent(Object bean, String param, Object value);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

/**
 * @author Brian Wing Shun Chan
 */
public class PropsUtil_IW {
	public static PropsUtil_IW getInstance() {
		return _instance;
	}

	public boolean contains(java.lang.String key) {
		return PropsUtil.contains(key);
	}

	public java.lang.String get(java.lang.String key) {
		return PropsUtil.get(key);
	}

	public java.lang.String get(java.lang.String key,
		com.liferay.portal.kernel.configuration.Filter filter) {
		return PropsUtil.get(key, filter);
	}

	public java.lang.String[] getArray(java.lang.String key) {
		return PropsUtil.getArray(key);
	}

	public java.lang.String[] getArray(java.lang.String key,
		com.liferay.portal.kernel.configuration.Filter filter) {
		return PropsUtil.getArray(key, filter);
	}

	public java.util.List<java.lang.String> getLoadedSources() {
		return PropsUtil.getLoadedSources();
	}

	public java.util.Properties getProperties() {
		return PropsUtil.getProperties();
	}

	public java.util.Properties getProperties(boolean includeSystem) {
		return PropsUtil.getProperties(includeSystem);
	}

	public java.util.Properties getProperties(java.lang.String prefix,
		boolean removePrefix) {
		return PropsUtil.getProperties(prefix, removePrefix);
	}

	public void set(java.lang.String key, java.lang.String value) {
		PropsUtil.set(key, value);
	}

	private PropsUtil_IW() {
	}

	private static PropsUtil_IW _instance = new PropsUtil_IW();
}
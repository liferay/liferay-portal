/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;

import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 */
public class JasperVersionDetector {

	public static String getJasperVersion() {
		return _jasperVersionDetector._jasperVersion;
	}

	public static boolean hasJspServletDependantsMap() {
		return _jasperVersionDetector._jspServletDependantsMap;
	}

	private JasperVersionDetector() {
		_initializeJasperVersion();
		_initializeJspServletDependantsMap();
	}

	private void _initializeJasperVersion() {
		Class<?> clazz = getClass();

		URL url = clazz.getResource("/org/apache/jasper/JasperException.class");

		if (url == null) {
			return;
		}

		String path = url.getPath();

		int pos = path.indexOf(CharPool.EXCLAMATION);

		if (pos == -1) {
			return;
		}

		try (JarFile jarFile = new JarFile(
				new File(new URI(path.substring(0, pos))))) {

			Manifest manifest = jarFile.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			if (attributes.containsKey(Attributes.Name.SPECIFICATION_VERSION)) {
				_jasperVersion = GetterUtil.getString(
					attributes.getValue(Attributes.Name.SPECIFICATION_VERSION));

				if (_isValidJasperVersion(_jasperVersion)) {
					return;
				}
			}

			Object implementationVersion = attributes.get(
				Attributes.Name.IMPLEMENTATION_VERSION);

			if (implementationVersion != null) {
				_jasperVersion = GetterUtil.getString(implementationVersion);

				if (_isValidJasperVersion(_jasperVersion)) {
					return;
				}
			}

			Attributes.Name bundleVersionAttributesName = new Attributes.Name(
				"Bundle-Version");

			Object bundleVersion = attributes.get(bundleVersionAttributesName);

			if (bundleVersion != null) {
				_jasperVersion = GetterUtil.getString(bundleVersion);

				if (_isValidJasperVersion(_jasperVersion)) {
					return;
				}

				_jasperVersion = StringPool.BLANK;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _initializeJspServletDependantsMap() {
		try {
			Class<?> clazz = Class.forName(
				"org.apache.jasper.servlet.JspServletWrapper");

			Method method = ReflectionUtil.getDeclaredMethod(
				clazz, "getDependants");

			_jspServletDependantsMap = Map.class.isAssignableFrom(
				method.getReturnType());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private boolean _isValidJasperVersion(String jasperVersion) {
		if (Validator.isNull(jasperVersion) ||
			!Validator.isDigit(jasperVersion.charAt(0))) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JasperVersionDetector.class);

	private static final JasperVersionDetector _jasperVersionDetector =
		new JasperVersionDetector();

	private String _jasperVersion = StringPool.BLANK;
	private boolean _jspServletDependantsMap;

}
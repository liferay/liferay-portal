/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogUtil;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 */
public class JavaDetector {

	public static String getJavaClassPath() {
		return _javaDetector._javaClassPath;
	}

	public static double getJavaClassVersion() {
		return _javaDetector._javaClassVersion;
	}

	public static String getJavaRuntimeName() {
		return _javaDetector._javaRuntimeName;
	}

	public static String getJavaRuntimeVersion() {
		return _javaDetector._javaRuntimeVersion;
	}

	public static double getJavaSpecificationVersion() {
		return _javaDetector._javaSpecificationVersion;
	}

	public static String getJavaVendor() {
		return _javaDetector._javaVendor;
	}

	public static String getJavaVersion() {
		return _javaDetector._javaVersion;
	}

	public static String getJavaVmVersion() {
		return _javaDetector._javaVmVersion;
	}

	public static boolean is64bit() {
		return _javaDetector._64bit;
	}

	public static boolean isIBM() {
		return _javaDetector._ibm;
	}

	public static boolean isOpenJDK() {
		return _javaDetector._openJDK;
	}

	public static boolean isOracle() {
		return _javaDetector._oracle;
	}

	protected JavaDetector() {
		_javaClassPath = System.getProperty("java.class.path");
		_javaClassVersion = GetterUtil.getDouble(
			System.getProperty("java.class.version"));
		_javaRuntimeName = System.getProperty("java.runtime.name");
		_javaRuntimeVersion = System.getProperty("java.runtime.version");
		_javaSpecificationVersion = GetterUtil.getDouble(
			System.getProperty("java.specification.version"));
		_javaVendor = System.getProperty("java.vendor");
		_javaVersion = System.getProperty("java.version");
		_javaVmVersion = System.getProperty("java.vm.version");

		_64bit = Objects.equals(
			System.getProperty("sun.arch.data.model"), "64");

		boolean oracle = false;

		if (_javaVendor != null) {
			_ibm = _javaVendor.startsWith("IBM");

			if (_javaVendor.startsWith("Oracle") ||
				_javaVendor.startsWith("Sun")) {

				oracle = true;
			}
		}
		else {
			_ibm = false;
		}

		_oracle = oracle;

		if (_javaRuntimeName != null) {
			_openJDK = _javaRuntimeName.contains("OpenJDK");
		}
		else {
			_openJDK = false;
		}

		if (_log.isDebugEnabled()) {
			LogUtil.debug(_log, System.getProperties());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(JavaDetector.class);

	private static final JavaDetector _javaDetector = new JavaDetector();

	private final boolean _64bit;
	private final boolean _ibm;
	private final String _javaClassPath;
	private final double _javaClassVersion;
	private final String _javaRuntimeName;
	private final String _javaRuntimeVersion;
	private final double _javaSpecificationVersion;
	private final String _javaVendor;
	private final String _javaVersion;
	private final String _javaVmVersion;
	private final boolean _openJDK;
	private final boolean _oracle;

}
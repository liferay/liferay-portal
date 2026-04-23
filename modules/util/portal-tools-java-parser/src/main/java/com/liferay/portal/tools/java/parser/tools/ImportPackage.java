/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser.tools;

import com.liferay.petra.string.StringPool;

/**
 * @author Carlos Sierra Andrés
 * @author Hugo Huijser
 */
public class ImportPackage implements Comparable<ImportPackage> {

	public ImportPackage(String importString, boolean isStatic, String line) {
		_importString = importString;
		_isStatic = isStatic;
		_line = line;
	}

	@Override
	public int compareTo(ImportPackage importPackage) {
		if (_isStatic != importPackage.isStatic()) {
			if (_isStatic) {
				return -1;
			}

			return 1;
		}

		String importPackageImportString = importPackage.getImportString();

		int value = _importString.compareTo(importPackageImportString);

		if (_importString.startsWith(StringPool.EXCLAMATION) ||
			importPackageImportString.startsWith(StringPool.EXCLAMATION)) {

			return value;
		}

		return value;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ImportPackage)) {
			return false;
		}

		ImportPackage importPackage = (ImportPackage)object;

		if ((_isStatic == importPackage.isStatic()) &&
			_importString.equals(importPackage.getImportString())) {

			return true;
		}

		return false;
	}

	public String getImportString() {
		return _importString;
	}

	public String getLine() {
		return _line;
	}

	public String getPackageLevel() {
		int pos = _importString.indexOf(StringPool.SLASH);

		if (pos != -1) {
			pos = _importString.indexOf(StringPool.SLASH, pos + 1);

			if (pos == -1) {
				return _importString;
			}

			return _importString.substring(0, pos);
		}

		pos = _importString.indexOf(StringPool.PERIOD);

		pos = _importString.indexOf(StringPool.PERIOD, pos + 1);

		if (pos == -1) {
			pos = _importString.indexOf(StringPool.PERIOD);
		}

		if (pos == -1) {
			return _importString;
		}

		return _importString.substring(0, pos);
	}

	@Override
	public int hashCode() {
		return _importString.hashCode();
	}

	public boolean isGroupedWith(ImportPackage importPackage) {
		if (_importString.equals(StringPool.STAR)) {
			return false;
		}

		String importPackageImportString = importPackage.getImportString();

		if (importPackageImportString.equals(StringPool.STAR) ||
			(_isStatic != importPackage.isStatic())) {

			return false;
		}

		String packageLevel = getPackageLevel();

		return packageLevel.equals(importPackage.getPackageLevel());
	}

	public boolean isStatic() {
		return _isStatic;
	}

	private final String _importString;
	private boolean _isStatic;
	private final String _line;

}
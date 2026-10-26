/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

/**
 * @author Brian Wing Shun Chan
 */
public class UnicodeFormatter_IW {
	public static UnicodeFormatter_IW getInstance() {
		return _instance;
	}

	public java.lang.String byteToHex(byte b) {
		return UnicodeFormatter.byteToHex(b);
	}

	public char[] byteToHex(byte b, char[] hexes) {
		return UnicodeFormatter.byteToHex(b, hexes);
	}

	public char[] byteToHex(byte b, char[] hexes, boolean upperCase) {
		return UnicodeFormatter.byteToHex(b, hexes, upperCase);
	}

	public java.lang.String bytesToHex(byte[] bytes) {
		return UnicodeFormatter.bytesToHex(bytes);
	}

	public java.lang.String charToHex(char c) {
		return UnicodeFormatter.charToHex(c);
	}

	public java.lang.String charToHex(char c, boolean upperCase) {
		return UnicodeFormatter.charToHex(c, upperCase);
	}

	public byte[] hexToBytes(java.lang.String hexString) {
		return UnicodeFormatter.hexToBytes(hexString);
	}

	public java.lang.String parseString(java.lang.String hexString) {
		return UnicodeFormatter.parseString(hexString);
	}

	public java.lang.String toString(char[] array) {
		return UnicodeFormatter.toString(array);
	}

	public java.lang.String toString(java.lang.String s) {
		return UnicodeFormatter.toString(s);
	}

	private UnicodeFormatter_IW() {
	}

	private static UnicodeFormatter_IW _instance = new UnicodeFormatter_IW();
}
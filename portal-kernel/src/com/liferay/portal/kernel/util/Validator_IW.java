/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

/**
 * @author Brian Wing Shun Chan
 */
public class Validator_IW {
	public static Validator_IW getInstance() {
		return _instance;
	}

	public boolean equalsSorted(boolean[] booleanArray1, boolean[] booleanArray2) {
		return Validator.equalsSorted(booleanArray1, booleanArray2);
	}

	public boolean equalsSorted(byte[] byteArray1, byte[] byteArray2) {
		return Validator.equalsSorted(byteArray1, byteArray2);
	}

	public boolean equalsSorted(char[] charArray1, char[] charArray2) {
		return Validator.equalsSorted(charArray1, charArray2);
	}

	public boolean equalsSorted(double[] doubleArray1, double[] doubleArray2) {
		return Validator.equalsSorted(doubleArray1, doubleArray2);
	}

	public boolean equalsSorted(float[] floatArray1, float[] floatArray2) {
		return Validator.equalsSorted(floatArray1, floatArray2);
	}

	public boolean equalsSorted(int[] intArray1, int[] intArray2) {
		return Validator.equalsSorted(intArray1, intArray2);
	}

	public boolean equalsSorted(long[] longArray1, long[] longArray2) {
		return Validator.equalsSorted(longArray1, longArray2);
	}

	public boolean equalsSorted(java.lang.Object[] objArray1,
		java.lang.Object[] objArray2) {
		return Validator.equalsSorted(objArray1, objArray2);
	}

	public boolean equalsSorted(short[] shortArray1, short[] shortArray2) {
		return Validator.equalsSorted(shortArray1, shortArray2);
	}

	public boolean isAddress(java.lang.String address) {
		return Validator.isAddress(address);
	}

	public boolean isAlphanumericName(java.lang.String name) {
		return Validator.isAlphanumericName(name);
	}

	public boolean isAscii(char c) {
		return Validator.isAscii(c);
	}

	public boolean isBlank(java.lang.String s) {
		return Validator.isBlank(s);
	}

	public boolean isBoolean(java.lang.String value) {
		return Validator.isBoolean(value);
	}

	public boolean isChar(char c) {
		return Validator.isChar(c);
	}

	public boolean isChar(java.lang.String s) {
		return Validator.isChar(s);
	}

	public boolean isContent(java.lang.String s) {
		return Validator.isContent(s);
	}

	public boolean isDate(int month, int day, int year) {
		return Validator.isDate(month, day, year);
	}

	public boolean isDigit(char c) {
		return Validator.isDigit(c);
	}

	public boolean isDigit(java.lang.String s) {
		return Validator.isDigit(s);
	}

	public boolean isDomain(java.lang.String domainName) {
		return Validator.isDomain(domainName);
	}

	public boolean isEmailAddress(java.lang.String emailAddress) {
		return Validator.isEmailAddress(emailAddress);
	}

	public boolean isEmailAddressSpecialChar(char c) {
		return Validator.isEmailAddressSpecialChar(c);
	}

	public boolean isFileExtension(java.lang.String fileExtension) {
		return Validator.isFileExtension(fileExtension);
	}

	public boolean isFileName(java.lang.String name) {
		return Validator.isFileName(name);
	}

	public boolean isFilePath(java.lang.String path, boolean parentDirAllowed) {
		return Validator.isFilePath(path, parentDirAllowed);
	}

	public boolean isGregorianDate(int month, int day, int year) {
		return Validator.isGregorianDate(month, day, year);
	}

	public boolean isHTML(java.lang.String s) {
		return Validator.isHTML(s);
	}

	public boolean isHex(java.lang.String hexString) {
		return Validator.isHex(hexString);
	}

	public boolean isHostName(java.lang.String name) {
		return Validator.isHostName(name);
	}

	public boolean isIPAddress(java.lang.String ipAddress) {
		return Validator.isIPAddress(ipAddress);
	}

	public boolean isIPv4Address(java.lang.String ipAddress) {
		return Validator.isIPv4Address(ipAddress);
	}

	public boolean isIPv6Address(java.lang.String ipAddress) {
		return Validator.isIPv6Address(ipAddress);
	}

	public boolean isJulianDate(int month, int day, int year) {
		return Validator.isJulianDate(month, day, year);
	}

	public boolean isLUHN(java.lang.String number) {
		return Validator.isLUHN(number);
	}

	public boolean isName(java.lang.String name) {
		return Validator.isName(name);
	}

	public boolean isNotNull(java.lang.Long l) {
		return Validator.isNotNull(l);
	}

	public boolean isNotNull(java.lang.Object object) {
		return Validator.isNotNull(object);
	}

	public boolean isNotNull(java.lang.String s) {
		return Validator.isNotNull(s);
	}

	public boolean isNull(java.lang.Long l) {
		return Validator.isNull(l);
	}

	public boolean isNull(java.lang.Object object) {
		return Validator.isNull(object);
	}

	public boolean isNull(java.lang.String s) {
		return Validator.isNull(s);
	}

	public boolean isNumber(java.lang.String number) {
		return Validator.isNumber(number);
	}

	public boolean isPassword(java.lang.String password) {
		return Validator.isPassword(password);
	}

	public boolean isPhoneNumber(java.lang.String phoneNumber) {
		return Validator.isPhoneNumber(phoneNumber);
	}

	public boolean isUri(java.lang.String uri) {
		return Validator.isUri(uri);
	}

	public boolean isUrl(java.lang.String url) {
		return Validator.isUrl(url);
	}

	public boolean isUrl(java.lang.String url, boolean acceptRootRelative) {
		return Validator.isUrl(url, acceptRootRelative);
	}

	public boolean isVariableName(java.lang.String variableName) {
		return Validator.isVariableName(variableName);
	}

	public boolean isVariableTerm(java.lang.String s) {
		return Validator.isVariableTerm(s);
	}

	public boolean isWhitespace(char c) {
		return Validator.isWhitespace(c);
	}

	public boolean isXml(java.lang.String s) {
		return Validator.isXml(s);
	}

	private Validator_IW() {
	}

	private static Validator_IW _instance = new Validator_IW();
}
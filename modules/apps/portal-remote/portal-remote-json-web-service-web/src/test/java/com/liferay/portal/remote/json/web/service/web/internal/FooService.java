/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Igor Spasic
 */
public class FooService {

	public static void addFile(String fileName) {
	}

	public static BarData bar() {
		return new BarData();
	}

	public static String camel(String goodName, String badNAME) {
		return goodName + '*' + badNAME;
	}

	public static int complex(
		List<Long> longs, int[] ints, Map<String, Long> map) {

		return longs.size() + ints.length + map.size();
	}

	public static String complexWithArrays(
		List<Long[]> longArrays, Map<String, String[]> mapNames) {

		StringBundler sb = new StringBundler();

		for (Long[] longArray : longArrays) {
			sb.append(Arrays.toString(longArray));
			sb.append('|');
		}

		sb.append('*');

		for (Map.Entry<String, String[]> entry : mapNames.entrySet()) {
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(Arrays.toString(entry.getValue()));
			sb.append('|');
		}

		return sb.toString();
	}

	public static String date(Date date) {
		return date.toString();
	}

	public static FooData getFooData(int id) {
		FooData fooData = new FooDataImpl();

		fooData.setId(id);

		if (id == 7) {
			FooDataImpl fooDataImpl = (FooDataImpl)fooData;

			fooDataImpl.setName("James Bond");
			fooDataImpl.setHeight(173);
			fooDataImpl.setValue("licensed");
		}
		else if (id == -13) {
			FooDataImpl fooDataImpl = (FooDataImpl)fooData;

			fooDataImpl.setName("Dr. Evil");
			fooDataImpl.setHeight(59);
			fooDataImpl.setValue("fun");
		}

		return fooData;
	}

	public static FooDataPage getFooDataPage() {
		FooDataAltImpl fooDataAltImpl = new FooDataAltImpl();

		fooDataAltImpl.setArray(9, 5, 7);
		fooDataAltImpl.setHeight(8);
		fooDataAltImpl.setId(2);
		fooDataAltImpl.setName("life");

		return new FooDataPage(fooDataAltImpl, getFooDatas(), 3);
	}

	public static List<FooData> getFooDatas() {
		return ListUtil.fromArray(getFooData(1), getFooData(2), getFooData(3));
	}

	public static FooData[] getFooDatas2() {
		FooData[] fooDataArray = new FooData[3];

		fooDataArray[0] = getFooData(1);
		fooDataArray[1] = getFooData(2);
		fooDataArray[2] = getFooData(3);

		return fooDataArray;
	}

	public static int[] getFooDatas3() {
		int[] fooDataArray = new int[3];

		fooDataArray[0] = 1;
		fooDataArray[1] = 2;
		fooDataArray[2] = 3;

		return fooDataArray;
	}

	public static String hello() {
		return "world";
	}

	public static String hello(int i1) {
		return "hello:" + i1;
	}

	public static String hello(int i1, int i2, int i3) {
		return StringBundler.concat("hello:", i1, ":", i2, ":", i3);
	}

	public static String hello(int i1, int i2, String s) {
		return StringBundler.concat("hello:", i1, ":", i2, ">", s);
	}

	public static String helloWorld(Integer userId, String worldName) {
		return StringBundler.concat("Welcome ", userId, " to ", worldName);
	}

	public static String hey(
		Calendar calendar, long[] userIds, List<Locale> locales, Long[] ids) {

		return StringBundler.concat(
			calendar.get(Calendar.YEAR), ", ", userIds[0], "/", userIds.length,
			", ", locales.get(0), "/", locales.size(), ", ", ids[0], "/",
			ids.length);
	}

	public static String locales(List<Locale> locales) {
		return locales.toString();
	}

	public static String methodOne(long id, long nameId) {
		return "m-2";
	}

	public static String methodOne(long id, long nameId, String subname) {
		return "m-3";
	}

	public static String methodOne(long id, String name) {
		return "m-1";
	}

	public static String nullLover(String name, int number) {
		if (name == null) {
			return "null!";
		}

		return StringBundler.concat("[", name, "|", number, "]");
	}

	public static String nullReturn() {
		return null;
	}

	public static String search(String name, String... params) {
		return StringBundler.concat(
			"search ", name, ">", StringUtil.merge(params));
	}

	public static String srvcctx(ServiceContext serviceContext) {
		Class<?> clazz = serviceContext.getClass();

		return clazz.getName();
	}

	public static ServiceContext srvcctx2(ServiceContext serviceContext) {
		return serviceContext;
	}

	public static String uploadFiles(File firstFile, File[] otherFiles)
		throws IOException {

		StringBundler sb = new StringBundler(otherFiles.length + 1);

		sb.append(FileUtil.read(firstFile));

		for (File otherFile : otherFiles) {
			sb.append(FileUtil.read(otherFile));
		}

		return sb.toString();
	}

	public static String use1(FooDataImpl fooData) {
		return "using #1: " + fooData.toString();
	}

	public static String use2(FooData fooData) {
		return "using #2: " + fooData.toString();
	}

}
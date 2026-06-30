/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.InputStream;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Máté Thurzó
 */
public class ExportImportTestUtil {

	public static String getBatchFileNameWithPath(
		String fileName, long groupId) {

		return StringBundler.concat(
			"group/", groupId, StringPool.FORWARD_SLASH, fileName);
	}

	public static JSONArray getExportedJSONArray(
			String fileNamePrefix, long groupId, InputStream inputStream)
		throws Exception {

		String batchFileNameWithPath = getBatchFileNameWithPath(
			fileNamePrefix + ".json", groupId);

		try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while (zipEntry != null) {
				if (Objects.equals(zipEntry.getName(), batchFileNameWithPath)) {
					return JSONFactoryUtil.createJSONArray(
						StringUtil.read(zipInputStream));
				}

				zipEntry = zipInputStream.getNextEntry();
			}
		}

		return null;
	}

	public static PortletDataContext getExportPortletDataContext()
		throws Exception {

		return getExportPortletDataContext(TestPropsValues.getGroupId());
	}

	public static PortletDataContext getExportPortletDataContext(long groupId)
		throws Exception {

		return getExportPortletDataContext(
			TestPropsValues.getCompanyId(), groupId);
	}

	public static PortletDataContext getExportPortletDataContext(
			long companyId, long groupId)
		throws Exception {

		return getExportPortletDataContext(
			companyId, groupId, new HashMap<String, String[]>());
	}

	public static PortletDataContext getExportPortletDataContext(
			long companyId, long groupId, Map<String, String[]> parameterMap)
		throws Exception {

		return getExportPortletDataContext(
			companyId, groupId, parameterMap, null, null);
	}

	public static PortletDataContext getExportPortletDataContext(
			long companyId, long groupId, Map<String, String[]> parameterMap,
			Date startDate, Date endDate)
		throws Exception {

		TestReaderWriter testReaderWriter = new TestReaderWriter();

		Document document = SAXReaderUtil.createDocument();

		Element manifestRootElement = document.addElement("root");

		manifestRootElement.addElement("header");

		testReaderWriter.addEntry("/manifest.xml", document.asXML());

		PortletDataContext portletDataContext =
			PortletDataContextFactoryUtil.createExportPortletDataContext(
				companyId, groupId, parameterMap, startDate, endDate,
				testReaderWriter);

		Element rootElement = SAXReaderUtil.createElement("root");

		portletDataContext.setExportDataRootElement(rootElement);
		portletDataContext.setMissingReferencesElement(
			rootElement.addElement("missing-references"));

		return portletDataContext;
	}

	public static PortletDataContext getImportPortletDataContext()
		throws Exception {

		return getImportPortletDataContext(TestPropsValues.getGroupId());
	}

	public static PortletDataContext getImportPortletDataContext(long groupId)
		throws Exception {

		return getImportPortletPreferences(
			TestPropsValues.getCompanyId(), groupId);
	}

	public static PortletDataContext getImportPortletDataContext(
			long companyId, long groupId, Map<String, String[]> parameterMap)
		throws Exception {

		TestReaderWriter testReaderWriter = new TestReaderWriter();

		Document document = SAXReaderUtil.createDocument();

		Element manifestRootElement = document.addElement("root");

		manifestRootElement.addElement("header");

		testReaderWriter.addEntry("/manifest.xml", document.asXML());

		PortletDataContext portletDataContext =
			PortletDataContextFactoryUtil.createImportPortletDataContext(
				companyId, groupId, parameterMap, new TestUserIdStrategy(),
				testReaderWriter);

		Element rootElement = SAXReaderUtil.createElement("root");

		portletDataContext.setImportDataRootElement(rootElement);
		portletDataContext.setMissingReferencesElement(
			rootElement.addElement("missing-references"));

		return portletDataContext;
	}

	public static PortletDataContext getImportPortletPreferences(
			long companyId, long groupId)
		throws Exception {

		return getImportPortletDataContext(
			companyId, groupId, new HashMap<String, String[]>());
	}

	public static void retryAssert(
			long pause, TimeUnit pauseTimeUnit, long timeout,
			TimeUnit timeoutTimeUnit, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		long deadline =
			System.currentTimeMillis() + timeoutTimeUnit.toMillis(timeout);

		while (true) {
			try {
				unsafeRunnable.run();

				return;
			}
			catch (AssertionError assertionError) {
				if (System.currentTimeMillis() > deadline) {
					throw assertionError;
				}
			}

			Thread.sleep(pauseTimeUnit.toMillis(pause));
		}
	}

}
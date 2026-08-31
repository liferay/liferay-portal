/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalServiceUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;

/**
 * @author Máté Thurzó
 */
public class ExportImportTestUtil {

	public static void assertBackgroundTaskSuccessful(long backgroundTaskId)
		throws Exception {

		assertBackgroundTaskSuccessful(backgroundTaskId, 30, TimeUnit.SECONDS);
	}

	public static void assertBackgroundTaskSuccessful(
			long backgroundTaskId, long timeout, TimeUnit timeoutTimeUnit)
		throws Exception {

		retryAssert(
			1, TimeUnit.SECONDS, timeout, timeoutTimeUnit,
			() -> {
				BackgroundTask backgroundTask =
					BackgroundTaskLocalServiceUtil.getBackgroundTask(
						backgroundTaskId);

				if (backgroundTask.getStatus() ==
						BackgroundTaskConstants.STATUS_FAILED) {

					throw new IllegalStateException(
						backgroundTask.getStatusMessage());
				}

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_SUCCESSFUL,
					backgroundTask.getStatus());
			});
	}

	public static File exportLayoutsAsFile(Group group, Layout layout)
		throws Exception {

		return ExportImportLocalServiceUtil.exportLayoutsAsFile(
			_addExportImportConfiguration(
				group.getGroupId(),
				_buildExportLayoutSettingsMap(group, layout),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT));
	}

	public static long exportLayoutsInBackground(Group group, Layout layout)
		throws Exception {

		return ExportImportLocalServiceUtil.exportLayoutsAsFileInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				group.getGroupId(),
				_buildExportLayoutSettingsMap(group, layout),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT));
	}

	public static File exportPortletInfoAsFile(
			Group group, Layout layout, String portletId)
		throws Exception {

		return ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			_addExportImportConfiguration(
				group.getGroupId(),
				_buildExportPortletSettingsMap(group, layout, portletId),
				ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET));
	}

	public static long exportPortletInfoInBackground(
			Group group, Layout layout, String portletId)
		throws Exception {

		return ExportImportLocalServiceUtil.exportPortletInfoAsFileInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				group.getGroupId(),
				_buildExportPortletSettingsMap(group, layout, portletId),
				ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET));
	}

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

	public static long importLayoutsInBackground(Group group, File larFile)
		throws Exception {

		return ExportImportLocalServiceUtil.importLayoutsInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				group.getGroupId(),
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						TestPropsValues.getUserId(), group.getGroupId(), false,
						null,
						ExportImportConfigurationParameterMapFactoryUtil.
							buildParameterMap(),
						LocaleUtil.US, TimeZoneUtil.GMT),
				ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT),
			larFile);
	}

	public static long importPortletInfoInBackground(
			Group group, Layout layout, String portletId, File larFile)
		throws Exception {

		return ExportImportLocalServiceUtil.importPortletInfoInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				group.getGroupId(),
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportPortletSettingsMap(
						TestPropsValues.getUserId(), layout.getPlid(),
						group.getGroupId(), portletId,
						ExportImportConfigurationParameterMapFactoryUtil.
							buildParameterMap(),
						LocaleUtil.US, TimeZoneUtil.GMT),
				ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET),
			larFile);
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

	private static ExportImportConfiguration _addExportImportConfiguration(
			long groupId, Map<String, Serializable> settingsMap, int type)
		throws Exception {

		return ExportImportConfigurationLocalServiceUtil.
			addExportImportConfiguration(
				TestPropsValues.getUserId(), groupId,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				type, settingsMap, WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext());
	}

	private static Map<String, Serializable> _buildExportLayoutSettingsMap(
			Group group, Layout layout)
		throws Exception {

		return ExportImportConfigurationSettingsMapFactoryUtil.
			buildExportLayoutSettingsMap(
				TestPropsValues.getUserId(), group.getGroupId(), false,
				new long[] {layout.getLayoutId()},
				ExportImportConfigurationParameterMapFactoryUtil.
					buildParameterMap(),
				LocaleUtil.US, TimeZoneUtil.GMT);
	}

	private static Map<String, Serializable> _buildExportPortletSettingsMap(
			Group group, Layout layout, String portletId)
		throws Exception {

		return ExportImportConfigurationSettingsMapFactoryUtil.
			buildExportPortletSettingsMap(
				TestPropsValues.getUserId(), layout.getPlid(),
				group.getGroupId(), portletId,
				ExportImportConfigurationParameterMapFactoryUtil.
					buildParameterMap(),
				LocaleUtil.US, TimeZoneUtil.GMT,
				RandomTestUtil.randomString() + ".lar");
	}

}
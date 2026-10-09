/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.file.install.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class FragmentFileInstallerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(getClass());

		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Test Site Name");
	}

	@Test
	public void testDeployFragmentsToSpecificSite() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.fragment.internal.file.install." +
					"FragmentFileInstaller",
				LoggerTestUtil.INFO)) {

			File file = _generateZipFile();

			_fileInstaller.transformURL(file);

			_assertLogEntries(
				logCapture.getLogEntries(), "Deploying " + file.getName(),
				"Deployed " + file.getName() + " successfully");
		}

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.fetchFragmentCollection(
				_group.getGroupId(), "Imported");

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			fragmentEntries.toString(), 1, fragmentEntries.size());

		FragmentEntry fragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals("Card", fragmentEntry.getName());
	}

	private void _assertLogEntries(
		List<LogEntry> logEntries, String... messages) {

		Assert.assertEquals(
			logEntries.toString(), messages.length, logEntries.size());

		for (int i = 0; i < logEntries.size(); i++) {
			LogEntry logEntry = logEntries.get(i);

			Assert.assertEquals(messages[i], logEntry.getMessage());
		}
	}

	private File _generateZipFile() throws Exception {
		Enumeration<URL> enumeration = _bundle.findEntries(
			_RESOURCES_PATH, "*", true);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String path = url.getPath();

			if (path.endsWith(StringPool.SLASH)) {
				continue;
			}

			try (InputStream inputStream = url.openStream()) {
				zipWriter.addEntry(
					StringUtil.removeSubstring(url.getPath(), _RESOURCES_PATH),
					inputStream);
			}
		}

		return zipWriter.getFile();
	}

	private static final String _RESOURCES_PATH =
		"com/liferay/fragment/file/install/test/dependencies/fragments";

	private Bundle _bundle;

	@Inject(
		filter = "component.name=com.liferay.fragment.internal.file.install.FragmentFileInstaller"
	)
	private FileInstaller _fileInstaller;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}
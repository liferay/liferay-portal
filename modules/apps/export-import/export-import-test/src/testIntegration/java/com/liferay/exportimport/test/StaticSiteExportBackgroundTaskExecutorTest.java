/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.staticexport.background.task.StaticSiteExportBackgroundTaskExecutorNames;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class StaticSiteExportBackgroundTaskExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Sync
	@Test
	public void testExecute() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		List<String> names = _getNames(
			HashMapBuilder.<String, Serializable>put(
				"groupId", _group.getGroupId()
			).build());

		Assert.assertTrue(
			names.toString(),
			names.contains("en" + layout.getFriendlyURL() + ".html"));
	}

	@Sync
	@Test
	public void testExecuteWithLanguageIds() throws Exception {
		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.US);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		List<String> names = _getNames(
			HashMapBuilder.<String, Serializable>put(
				"groupId", _group.getGroupId()
			).put(
				"languageIds", new String[] {"en_US", "es_ES"}
			).build());

		Assert.assertFalse(
			names.toString(),
			names.contains("de" + layout.getFriendlyURL() + ".html"));
		Assert.assertTrue(
			names.toString(),
			names.contains("en" + layout.getFriendlyURL() + ".html"));
		Assert.assertTrue(
			names.toString(),
			names.contains("es" + layout.getFriendlyURL() + ".html"));
	}

	private List<String> _getNames(Map<String, Serializable> taskContextMap)
		throws Exception {

		List<String> names = new ArrayList<>();

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_group.getDescriptiveName(),
				StaticSiteExportBackgroundTaskExecutorNames.
					STATIC_SITE_EXPORT_BACKGROUND_TASK_EXECUTOR,
				taskContextMap, new ServiceContext());

		backgroundTask = _backgroundTaskManager.fetchBackgroundTask(
			backgroundTask.getBackgroundTaskId());

		List<FileEntry> fileEntries =
			backgroundTask.getAttachmentsFileEntries();

		Assert.assertEquals(fileEntries.toString(), 1, fileEntries.size());

		FileEntry fileEntry = fileEntries.get(0);

		Assert.assertTrue(
			fileEntry.getFileName(),
			StringUtil.endsWith(fileEntry.getFileName(), ".zip"));

		try (ZipFile zipFile = new ZipFile(
				FileUtil.createTempFile(fileEntry.getContentStream()))) {

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				names.add(zipEntry.getName());
			}
		}

		return names;
	}

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	private Group _group;

}
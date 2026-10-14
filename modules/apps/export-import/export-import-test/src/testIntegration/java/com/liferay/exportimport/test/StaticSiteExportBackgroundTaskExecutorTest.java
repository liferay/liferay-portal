/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.staticexport.background.task.StaticSiteExportBackgroundTaskExecutorNames;

import java.io.Serializable;

import java.time.Instant;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
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

		BackgroundTask backgroundTask = _addBackgroundTask(
			HashMapBuilder.<String, Serializable>put(
				"groupId", _group.getGroupId()
			).build());

		Map<String, String> zipEntries = _getZipEntries(backgroundTask);

		String path = "en" + layout.getFriendlyURL() + ".html";

		Assert.assertTrue(
			String.valueOf(zipEntries.keySet()), zipEntries.containsKey(path));

		JSONObject manifestJSONObject = _jsonFactory.createJSONObject(
			zipEntries.get("manifest.json"));

		Assert.assertTrue(manifestJSONObject.getBoolean("allLayoutsExported"));
		Assert.assertEquals(
			"[]",
			String.valueOf(manifestJSONObject.getJSONArray("displayPages")));

		Date createDate = backgroundTask.getCreateDate();

		Instant instant = createDate.toInstant();

		Assert.assertEquals(
			instant.toString(), manifestJSONObject.getString("exportDate"));

		Assert.assertEquals(
			backgroundTask.getBackgroundTaskId(),
			manifestJSONObject.getLong("exportProcessId"));
		Assert.assertEquals(
			"[\"en_US\"]",
			String.valueOf(manifestJSONObject.getJSONArray("languageIds")));
		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"externalReferenceCode", layout.getExternalReferenceCode()
				).put(
					"paths", JSONUtil.put("en_US", path)
				)
			).toString(),
			String.valueOf(manifestJSONObject.getJSONArray("layouts")));
		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			manifestJSONObject.getString("siteExternalReferenceCode"));
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

		Map<String, String> zipEntries = _getZipEntries(
			_addBackgroundTask(
				HashMapBuilder.<String, Serializable>put(
					"groupId", _group.getGroupId()
				).put(
					"languageIds", new String[] {"en_US", "es_ES"}
				).build()));

		String englishPath = "en" + layout.getFriendlyURL() + ".html";
		String spanishPath = "es" + layout.getFriendlyURL() + ".html";

		Assert.assertFalse(
			String.valueOf(zipEntries.keySet()),
			zipEntries.containsKey("de" + layout.getFriendlyURL() + ".html"));
		Assert.assertTrue(
			String.valueOf(zipEntries.keySet()),
			zipEntries.containsKey(englishPath));
		Assert.assertTrue(
			String.valueOf(zipEntries.keySet()),
			zipEntries.containsKey(spanishPath));

		JSONObject manifestJSONObject = _jsonFactory.createJSONObject(
			zipEntries.get("manifest.json"));

		Assert.assertEquals(
			"[\"en_US\",\"es_ES\"]",
			String.valueOf(manifestJSONObject.getJSONArray("languageIds")));
		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"externalReferenceCode", layout.getExternalReferenceCode()
				).put(
					"paths",
					JSONUtil.put(
						"en_US", englishPath
					).put(
						"es_ES", spanishPath
					)
				)
			).toString(),
			String.valueOf(manifestJSONObject.getJSONArray("layouts")));
	}

	@Sync
	@Test
	public void testExecuteWithLayoutIds() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(
			layout1.fetchDraftLayout(), layout1);

		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(
			layout2.fetchDraftLayout(), layout2);

		Map<String, String> zipEntries = _getZipEntries(
			_addBackgroundTask(
				HashMapBuilder.<String, Serializable>put(
					"groupId", _group.getGroupId()
				).put(
					"layoutIds", new long[] {layout2.getLayoutId()}
				).build()));

		JSONObject manifestJSONObject = _jsonFactory.createJSONObject(
			zipEntries.get("manifest.json"));

		Assert.assertFalse(manifestJSONObject.getBoolean("allLayoutsExported"));
		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"externalReferenceCode", layout2.getExternalReferenceCode()
				).put(
					"paths",
					JSONUtil.put(
						"en_US", "en" + layout2.getFriendlyURL() + ".html")
				)
			).toString(),
			String.valueOf(manifestJSONObject.getJSONArray("layouts")));
	}

	@Sync
	@Test
	public void testExecuteWithLayoutPageTemplateEntry() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				PortalUtil.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		Layout contentLayout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = contentLayout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			StringBundler.concat(
				"<a href=\"", PortalUtil.getPathFriendlyURLPublic(),
				_group.getFriendlyURL(), "/w/", journalArticle.getUrlTitle(),
				"\">", RandomTestUtil.randomString(), "</a>"),
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, contentLayout);

		Map<String, String> zipEntries = _getZipEntries(
			_addBackgroundTask(
				HashMapBuilder.<String, Serializable>put(
					"groupId", _group.getGroupId()
				).build()));

		JSONObject manifestJSONObject = _jsonFactory.createJSONObject(
			zipEntries.get("manifest.json"));

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"externalReferenceCode",
					journalArticle.getExternalReferenceCode()
				).put(
					"layoutExternalReferenceCode",
					layout.getExternalReferenceCode()
				).put(
					"paths",
					JSONUtil.put(
						"en_US",
						"en/w/" + journalArticle.getUrlTitle() + ".html")
				)
			).toString(),
			String.valueOf(manifestJSONObject.getJSONArray("displayPages")));
	}

	private BackgroundTask _addBackgroundTask(
			Map<String, Serializable> taskContextMap)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_group.getDescriptiveName(),
				StaticSiteExportBackgroundTaskExecutorNames.
					STATIC_SITE_EXPORT_BACKGROUND_TASK_EXECUTOR,
				taskContextMap, new ServiceContext());

		return _backgroundTaskManager.fetchBackgroundTask(
			backgroundTask.getBackgroundTaskId());
	}

	private Map<String, String> _getZipEntries(BackgroundTask backgroundTask)
		throws Exception {

		Map<String, String> zipEntries = new HashMap<>();

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

				zipEntries.put(
					zipEntry.getName(),
					StringUtil.read(zipFile.getInputStream(zipEntry)));
			}
		}

		return zipEntries;
	}

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-72278")
@RunWith(Arquillian.class)
public class JournalArticleLaunchEntryManagerTest
	extends BaseLaunchEntryManagerTestCase<String> {

	@Override
	@Test
	public void testAddDraft() throws Exception {
		super.testAddDraft();

		_testAddDraftFromHistoricalVersion();
	}

	@Override
	@Test
	public void testGetPreviewBaseModel() throws Exception {
		super.testGetPreviewBaseModel();

		_testGetPreviewBaseModelForHistoricalVersion();
	}

	@Override
	@Test
	public void testPublishDraft() throws Exception {
		super.testPublishDraft();

		_testPublishDraftProducesDistinctVersionKey();
	}

	@Override
	protected long addEntry() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return journalArticle.getResourcePrimKey();
	}

	@Override
	protected String createValue() {
		return RandomTestUtil.randomString();
	}

	@Override
	protected String getBackURLParameterName() {
		return PortalUtil.getPortletNamespace(JournalPortletKeys.JOURNAL) +
			"backURL";
	}

	@Override
	protected String getDraftValue(LaunchEntryManager.Draft draft)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				draft.getDraftId(), WorkflowConstants.STATUS_ANY, false);

		return journalArticle.getTitle(LocaleUtil.getSiteDefault());
	}

	@Override
	protected String getEntryValue(long classPK) {
		JournalArticle journalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				classPK, WorkflowConstants.STATUS_APPROVED);

		return journalArticle.getTitle(LocaleUtil.getSiteDefault());
	}

	@Override
	protected List<Serializable> getListedPrimaryKeys(long classPK) {
		JournalArticle journalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				classPK,
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_IN_TRASH
				});

		return ListUtil.fromArray(journalArticle.getId());
	}

	@Override
	protected Class<?> getModelClass() {
		return JournalArticle.class;
	}

	@Override
	protected Map<String, String> getSelectedItemData(long classPK) {
		return TreeMapBuilder.<String, String>create(
			String.CASE_INSENSITIVE_ORDER
		).put(
			"classPK", String.valueOf(classPK)
		).build();
	}

	@Override
	protected void updateDraft(LaunchEntryManager.Draft draft, String value)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				draft.getDraftId(), WorkflowConstants.STATUS_ANY, false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_journalArticleLocalService.updateArticle(
			TestPropsValues.getUserId(), journalArticle.getGroupId(),
			journalArticle.getFolderId(), journalArticle.getArticleId(),
			journalArticle.getVersion(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), value
			).build(),
			journalArticle.getDescriptionMap(), journalArticle.getContent(),
			journalArticle.getLayoutUuid(), serviceContext);
	}

	@Override
	protected void updateLiveEntry(long classPK, String value)
		throws Exception {

		JournalTestUtil.updateArticle(
			_journalArticleLocalService.getLatestArticle(
				classPK, WorkflowConstants.STATUS_APPROVED),
			value);
	}

	private void _testAddDraftFromHistoricalVersion() throws Exception {
		long classPK = addEntry();

		String entryValue = getEntryValue(classPK);

		LaunchEntryManager.Version version = getPublishedVersion(classPK);

		updateLiveEntry(classPK, createValue());

		Assert.assertNotEquals(entryValue, getEntryValue(classPK));

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		Assert.assertEquals(
			entryValue,
			getDraftValue(
				launchEntryManager.addDraft(
					classPK, RandomTestUtil.randomLong(),
					version.getVersionKey())));
	}

	private void _testGetPreviewBaseModelForHistoricalVersion()
		throws Exception {

		long classPK = addEntry();

		String value1 = createValue();

		LaunchEntryManager.Draft draft1 = addDraft(classPK);

		updateDraft(draft1, value1);

		String value2 = createValue();

		LaunchEntryManager.Draft draft2 = addDraft(classPK);

		updateDraft(draft2, value2);

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version publishedVersion1 =
			launchEntryManager.publishDraft(classPK, draft1.getDraftId());

		launchEntryManager.publishDraft(classPK, draft2.getDraftId());

		LaunchEntryManager.Version version1 = getVersion(
			classPK, publishedVersion1.getVersionKey());

		try (SafeCloseable safeCloseable = setPreviewIdWithSafeCloseable(
				classPK, version1::getPreviewBaseModel)) {

			Assert.assertEquals(value1, getEntryValue(classPK));
		}

		Assert.assertEquals(value2, getEntryValue(classPK));
	}

	private void _testPublishDraftProducesDistinctVersionKey()
		throws Exception {

		long classPK = addEntry();

		LaunchEntryManager.Draft draft1 = addDraft(classPK);

		updateDraft(draft1, createValue());

		LaunchEntryManager.Draft draft2 = addDraft(classPK);

		updateDraft(draft2, createValue());

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		LaunchEntryManager.Version publishedVersion2 =
			launchEntryManager.publishDraft(classPK, draft2.getDraftId());

		LaunchEntryManager.Version publishedVersion1 =
			launchEntryManager.publishDraft(classPK, draft1.getDraftId());

		Assert.assertNotEquals(
			publishedVersion2.getVersionKey(),
			publishedVersion1.getVersionKey());
	}

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

}
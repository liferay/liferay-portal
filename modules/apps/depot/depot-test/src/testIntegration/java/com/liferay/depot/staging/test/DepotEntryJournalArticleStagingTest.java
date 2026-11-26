/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotStagingTestUtil;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Veronica Gonzalez
 */
@RunWith(Arquillian.class)
public class DepotEntryJournalArticleStagingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_liveDepotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	@TestInfo("LPD-117655")
	public void testStagedDepotEntryJournalArticleUpdatingFriendlyURL()
		throws Exception {

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> {
				JournalArticle journalArticle = JournalTestUtil.addArticle(
					stagingDepotEntry.getGroupId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString());

				Group liveGroup = _liveDepotEntry.getGroup();

				DepotStagingTestUtil.publishLayouts(
					stagingDepotEntry.getGroup(), liveGroup);

				Assert.assertEquals(
					journalArticle.getUrlTitle(LocaleUtil.getDefault()),
					_getLiveURLTitle(journalArticle, liveGroup));

				String urlTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());

				_journalArticleLocalService.updateArticle(
					journalArticle.getUserId(), journalArticle.getGroupId(),
					journalArticle.getFolderId(), journalArticle.getArticleId(),
					journalArticle.getVersion(), journalArticle.getTitleMap(),
					journalArticle.getDescriptionMap(),
					HashMapBuilder.put(
						LocaleUtil.getDefault(), urlTitle
					).build(),
					journalArticle.getContent(),
					journalArticle.getDDMTemplateKey(),
					journalArticle.getLayoutUuid(), 1, 1, 1965, 0, 0, 0, 0, 0,
					0, 0, true, 0, 0, 0, 0, 0, true, true, false, 0, 0, null,
					null, null, null,
					ServiceContextTestUtil.getServiceContext(
						journalArticle.getGroupId()));

				DepotStagingTestUtil.publishLayouts(
					stagingDepotEntry.getGroup(), liveGroup);

				Assert.assertEquals(
					urlTitle, _getLiveURLTitle(journalArticle, liveGroup));
			});
	}

	@Test
	@TestInfo("LPD-117655")
	public void testStagedGroupJournalArticleWithDepotEntryDDMStructure()
		throws Exception {

		_liveGroup = GroupTestUtil.addGroup();

		DepotTestUtil.withLocalStagingEnabled(
			_liveDepotEntry,
			stagingDepotEntry -> DepotTestUtil.withLocalStagingEnabled(
				_liveGroup,
				stagingGroup -> {
					_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
						true, stagingDepotEntry.getDepotEntryId(),
						stagingGroup.getGroupId(), true);

					DDMStructure ddmStructure =
						DDMStructureTestUtil.addStructure(
							stagingDepotEntry.getGroupId(),
							JournalArticle.class.getName());

					DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
						stagingDepotEntry.getGroupId(),
						ddmStructure.getStructureId(),
						_portal.getClassNameId(JournalArticle.class));

					DepotStagingTestUtil.publishLayouts(
						stagingDepotEntry.getGroup(),
						_liveDepotEntry.getGroup());

					JournalArticle journalArticle =
						_journalArticleLocalService.addArticle(
							null, TestPropsValues.getUserId(),
							stagingGroup.getGroupId(), 0,
							HashMapBuilder.put(
								LocaleUtil.getDefault(),
								RandomTestUtil.randomString()
							).build(),
							null,
							DDMStructureTestUtil.getSampleStructuredContent(),
							ddmStructure.getStructureId(),
							ddmTemplate.getTemplateKey(),
							ServiceContextTestUtil.getServiceContext(
								stagingGroup.getGroupId()));

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					JournalArticle liveJournalArticle =
						_journalArticleLocalService.fetchLatestArticle(
							_liveGroup.getGroupId(),
							journalArticle.getArticleId(),
							WorkflowConstants.STATUS_APPROVED);

					Assert.assertEquals(
						journalArticle.getTitle(),
						liveJournalArticle.getTitle());

					DDMStructure liveDDMStructure =
						liveJournalArticle.getDDMStructure();

					Assert.assertEquals(
						_liveDepotEntry.getGroupId(),
						liveDDMStructure.getGroupId());
					Assert.assertEquals(
						ddmStructure.getUuid(), liveDDMStructure.getUuid());

					journalArticle = JournalTestUtil.updateArticle(
						journalArticle, RandomTestUtil.randomString());

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					liveJournalArticle =
						_journalArticleLocalService.fetchLatestArticle(
							_liveGroup.getGroupId(),
							journalArticle.getArticleId(),
							WorkflowConstants.STATUS_APPROVED);

					Assert.assertEquals(
						journalArticle.getTitle(),
						liveJournalArticle.getTitle());

					_journalArticleLocalService.deleteArticle(
						stagingGroup.getGroupId(),
						journalArticle.getArticleId(),
						ServiceContextTestUtil.getServiceContext(
							stagingGroup.getGroupId()));

					DepotStagingTestUtil.publishLayouts(
						stagingGroup, _liveGroup);

					Assert.assertNull(
						_journalArticleLocalService.fetchLatestArticle(
							_liveGroup.getGroupId(),
							journalArticle.getArticleId(),
							WorkflowConstants.STATUS_ANY));
				}));
	}

	private String _getLiveURLTitle(
			JournalArticle journalArticle, Group liveGroup)
		throws Exception {

		JournalArticle liveJournalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				liveGroup.getGroupId(), journalArticle.getArticleId(),
				WorkflowConstants.STATUS_APPROVED);

		return liveJournalArticle.getUrlTitle(LocaleUtil.getDefault());
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@DeleteAfterTestRun
	private DepotEntry _liveDepotEntry;

	@DeleteAfterTestRun
	private Group _liveGroup;

	@Inject
	private Portal _portal;

}
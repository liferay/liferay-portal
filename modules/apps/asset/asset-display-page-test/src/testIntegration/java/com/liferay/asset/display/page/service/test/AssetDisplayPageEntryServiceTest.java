/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryService;
import com.liferay.asset.display.page.test.util.AssetDisplayPageEntryTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class AssetDisplayPageEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_classNameId = _portal.getClassNameId(JournalArticle.class.getName());
		_group = GroupTestUtil.addGroup();
		_journalArticle1 = _addJournalArticle(false);
		_journalArticle2 = _addJournalArticle(true);
		_layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED);
		_user = UserTestUtil.addUser();

		AssetDisplayPageEntryTestUtil.addAssetDisplayPageEntry(
			_group.getGroupId(), _classNameId,
			_journalArticle1.getResourcePrimKey(),
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC);
		AssetDisplayPageEntryTestUtil.addAssetDisplayPageEntry(
			_group.getGroupId(), _classNameId,
			_journalArticle2.getResourcePrimKey(),
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC);
	}

	@Test
	@TestInfo("LPD-111649")
	public void testGetAssetDisplayPageEntries() throws Exception {
		Assert.assertEquals(
			SetUtil.fromArray(
				_journalArticle1.getResourcePrimKey(),
				_journalArticle2.getResourcePrimKey()),
			_getClassPKs(
				_assetDisplayPageEntryService.getAssetDisplayPageEntries(
					_classNameId, 0,
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)));
		Assert.assertEquals(
			2,
			_assetDisplayPageEntryService.getAssetDisplayPageEntriesCount(
				_classNameId, 0,
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				false));
		Assert.assertEquals(
			Collections.emptyList(),
			_assetDisplayPageEntryService.getAssetDisplayPageEntries(
				_classNameId, 0, 0, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));
		Assert.assertEquals(
			0,
			_assetDisplayPageEntryService.getAssetDisplayPageEntriesCount(
				_classNameId, 0, 0, false));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertEquals(
				SetUtil.fromArray(_journalArticle2.getResourcePrimKey()),
				_getClassPKs(
					_assetDisplayPageEntryService.getAssetDisplayPageEntries(
						_classNameId, 0,
						_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
						false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)));
		}
	}

	@Test
	@TestInfo("LPD-111649")
	public void testGetAssetDisplayPageEntriesByLayoutPageTemplateEntryId()
		throws Exception {

		Assert.assertEquals(
			SetUtil.fromArray(
				_journalArticle1.getResourcePrimKey(),
				_journalArticle2.getResourcePrimKey()),
			_getClassPKs(
				_assetDisplayPageEntryService.
					getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
						_layoutPageTemplateEntry.
							getLayoutPageTemplateEntryId())));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertEquals(
				SetUtil.fromArray(_journalArticle2.getResourcePrimKey()),
				_getClassPKs(
					_assetDisplayPageEntryService.
						getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
							_layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId())));
			Assert.assertEquals(
				SetUtil.fromArray(_journalArticle2.getResourcePrimKey()),
				_getClassPKs(
					_assetDisplayPageEntryService.
						getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
							_layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)));
		}
	}

	private JournalArticle _addJournalArticle(boolean addGuestPermissions)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(addGuestPermissions);

		return JournalTestUtil.addArticle(
			_group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), LocaleUtil.getSiteDefault(), false,
			true, serviceContext);
	}

	private Set<Long> _getClassPKs(
		List<AssetDisplayPageEntry> assetDisplayPageEntries) {

		return SetUtil.fromCollection(
			ListUtil.toList(
				assetDisplayPageEntries, AssetDisplayPageEntry::getClassPK));
	}

	@Inject
	private AssetDisplayPageEntryService _assetDisplayPageEntryService;

	private long _classNameId;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle1;
	private JournalArticle _journalArticle2;
	private LayoutPageTemplateEntry _layoutPageTemplateEntry;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private User _user;

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.change.tracking.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseTableReferenceDefinitionTestCase;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalContentSearch;
import com.liferay.journal.service.JournalContentSearchLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Gislayne Vitorino
 */
@RunWith(Arquillian.class)
public class JournalContentSearchTableReferenceDefinitionTest
	extends BaseTableReferenceDefinitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		_layout = _layoutLocalService.getLayout(TestPropsValues.getPlid());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		JournalContentSearch journalContentSearch =
			_journalContentSearchLocalService.createJournalContentSearch(
				_counterLocalService.increment());

		journalContentSearch.setGroupId(_journalArticle.getGroupId());
		journalContentSearch.setCompanyId(_journalArticle.getCompanyId());
		journalContentSearch.setPrivateLayout(_layout.isPrivateLayout());
		journalContentSearch.setLayoutId(_layout.getLayoutId());
		journalContentSearch.setPortletId(JournalPortletKeys.JOURNAL);
		journalContentSearch.setArticleId(_journalArticle.getArticleId());

		return _journalContentSearchLocalService.addJournalContentSearch(
			journalContentSearch);
	}

	@Inject
	private CounterLocalService _counterLocalService;

	private JournalArticle _journalArticle;

	@Inject
	private JournalContentSearchLocalService _journalContentSearchLocalService;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

}
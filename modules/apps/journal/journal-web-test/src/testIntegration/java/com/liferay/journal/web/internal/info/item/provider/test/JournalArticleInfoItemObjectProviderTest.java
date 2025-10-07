/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupKeyInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class JournalArticleInfoItemObjectProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalArticle = JournalTestUtil.addArticle(_group.getGroupId(), 0);
	}

	@Test
	public void testGetInfoItem() throws Exception {
		InfoItemObjectProvider<JournalArticle> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, JournalArticle.class.getName());

		Assert.assertEquals(
			_journalArticle,
			infoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new ClassPKInfoItemIdentifier(
					_journalArticle.getResourcePrimKey())));
		Assert.assertEquals(
			_journalArticle,
			infoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new ERCInfoItemIdentifier(
					_journalArticle.getExternalReferenceCode())));
		Assert.assertEquals(
			_journalArticle,
			infoItemObjectProvider.getInfoItem(
				RandomTestUtil.randomLong(),
				new ERCInfoItemIdentifier(
					_journalArticle.getExternalReferenceCode(),
					_group.getExternalReferenceCode())));
		Assert.assertEquals(
			_journalArticle,
			infoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new GroupKeyInfoItemIdentifier(
					_group.getGroupId(), _journalArticle.getArticleId())));
		Assert.assertEquals(
			_journalArticle,
			infoItemObjectProvider.getInfoItem(
				_group.getGroupId(),
				new GroupUrlTitleInfoItemIdentifier(
					_group.getGroupId(), _journalArticle.getUrlTitle())));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

}
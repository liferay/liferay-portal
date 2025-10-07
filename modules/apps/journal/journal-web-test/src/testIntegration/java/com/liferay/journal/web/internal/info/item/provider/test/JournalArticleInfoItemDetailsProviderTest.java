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
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
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
public class JournalArticleInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	@Test
	public void testGetInfoItemDetails() throws Exception {
		InfoItemDetailsProvider<JournalArticle> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, JournalArticle.class.getName());

		InfoItemDetails classPKInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), ClassPKInfoItemIdentifier.class,
				_journalArticle);

		Assert.assertEquals(
			JournalArticle.class.getName(),
			classPKInfoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				JournalArticle.class.getName(),
				_journalArticle.getResourcePrimKey()),
			classPKInfoItemDetails.getInfoItemReference());

		InfoItemDetails ercInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), ERCInfoItemIdentifier.class,
				_journalArticle);

		Assert.assertEquals(
			new InfoItemReference(
				JournalArticle.class.getName(),
				new ERCInfoItemIdentifier(
					_journalArticle.getExternalReferenceCode())),
			ercInfoItemDetails.getInfoItemReference());

		InfoItemDetails randomGroupERCInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
				_journalArticle);

		Assert.assertEquals(
			new InfoItemReference(
				JournalArticle.class.getName(),
				new ERCInfoItemIdentifier(
					_journalArticle.getExternalReferenceCode(),
					_group.getExternalReferenceCode())),
			randomGroupERCInfoItemDetails.getInfoItemReference());

		InfoItemDetails groupKeyInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), GroupKeyInfoItemIdentifier.class,
				_journalArticle);

		Assert.assertEquals(
			new InfoItemReference(
				JournalArticle.class.getName(),
				new GroupKeyInfoItemIdentifier(
					_group.getGroupId(), _journalArticle.getArticleId())),
			groupKeyInfoItemDetails.getInfoItemReference());

		InfoItemDetails groupUrlTitleInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), GroupUrlTitleInfoItemIdentifier.class,
				_journalArticle);

		Assert.assertEquals(
			new InfoItemReference(
				JournalArticle.class.getName(),
				new GroupUrlTitleInfoItemIdentifier(
					_group.getGroupId(), _journalArticle.getUrlTitle())),
			groupUrlTitleInfoItemDetails.getInfoItemReference());
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private JournalArticle _journalArticle;

}
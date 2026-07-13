/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class KBArticleInfoItemProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = TestPropsValues.getUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, _user.getUserId());
	}

	@Test
	public void testGetInfoItemFromKBArticleInfoItemProvider()
		throws Exception {

		_kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(),
			ClassNameLocalServiceUtil.getClassNameId(
				KBFolderConstants.getClassName()),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			StringPool.BLANK, RandomTestUtil.nextDate(), null, null, null,
			_serviceContext);

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		KBArticle updatedKBArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), _kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, _kbArticle.getDisplayDate(),
			null, null, null, null, _serviceContext);

		InfoItemIdentifier infoItemIdentifier = new ClassPKInfoItemIdentifier(
			_kbArticle.getResourcePrimKey());

		InfoItemObjectProvider<KBArticle> kbArticleArticleInfoItemProvider =
			(InfoItemObjectProvider<KBArticle>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, KBArticle.class.getName(),
					infoItemIdentifier.getInfoItemServiceFilter());

		KBArticle publishedKBArticle =
			kbArticleArticleInfoItemProvider.getInfoItem(infoItemIdentifier);

		Assert.assertEquals(
			_kbArticle.getTitle(), publishedKBArticle.getTitle());

		KBArticle draftKBArticle = kbArticleArticleInfoItemProvider.getInfoItem(
			new ClassPKInfoItemIdentifier(
				_kbArticle.getResourcePrimKey(),
				InfoItemIdentifier.VERSION_LATEST));

		Assert.assertEquals(
			updatedKBArticle.getTitle(), draftKBArticle.getTitle());

		_kbArticleLocalService.deleteKBArticles(
			updatedKBArticle.getGroupId(),
			updatedKBArticle.getResourcePrimKey());
	}

	@Test(expected = NoSuchInfoItemException.class)
	public void testGetInvalidInfoItemFromKBArticleInfoItemProvider()
		throws Exception {

		InfoItemIdentifier infoItemIdentifier = new ClassPKInfoItemIdentifier(
			RandomTestUtil.randomLong());

		InfoItemObjectProvider<KBArticle> kbArticleArticleInfoItemProvider =
			(InfoItemObjectProvider<KBArticle>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, KBArticle.class.getName(),
					infoItemIdentifier.getInfoItemServiceFilter());

		kbArticleArticleInfoItemProvider.getInfoItem(infoItemIdentifier);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@DeleteAfterTestRun
	private KBArticle _kbArticle;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}
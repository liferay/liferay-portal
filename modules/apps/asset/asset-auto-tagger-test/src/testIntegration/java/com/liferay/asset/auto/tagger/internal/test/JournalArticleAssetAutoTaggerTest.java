/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
@Sync
public class JournalArticleAssetAutoTaggerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			AssetAutoTagProvider.class, model -> Arrays.asList(_ASSET_TAG_NAME),
			MapUtil.singletonDictionary(
				"model.class.name", JournalArticle.class.getName()));
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testAutoTagsAnArticleOnPublishAfterDraft() throws Exception {
		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, titleMap, null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.US, true, false, serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, article.getStatus());
		Assert.assertEquals(
			Collections.emptyList(), _getAssetTagNames(article));

		article = JournalTestUtil.updateArticle(
			article, titleMap, article.getContent(), true, true,
			serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());
		Assert.assertEquals(
			Arrays.asList(_ASSET_TAG_NAME), _getAssetTagNames(article));
	}

	private List<String> _getAssetTagNames(JournalArticle article) {
		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(), article.getResourcePrimKey());

		return Arrays.asList(assetEntry.getTagNames());
	}

	private static final String _ASSET_TAG_NAME = "auto tag";

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceRegistration<?> _serviceRegistration;

}
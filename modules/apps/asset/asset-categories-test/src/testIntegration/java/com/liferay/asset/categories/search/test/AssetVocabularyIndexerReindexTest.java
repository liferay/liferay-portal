/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 * @author Lucas Marques
 */
@RunWith(Arquillian.class)
public class AssetVocabularyIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		AssetVocabularyFixture assetVocabularyFixture =
			new AssetVocabularyFixture(assetVocabularyService, group);

		_assetVocabularies = assetVocabularyFixture.getAssetVocabularies();
		_assetVocabularyFixture = assetVocabularyFixture;

		_group = group;

		_groups = groupSearchFixture.getGroups();
	}

	@Test
	public void testReindex() throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyFixture.createAssetVocabulary();

		String searchTerm = assetVocabulary.getName();

		assertFieldValue(Field.NAME, searchTerm, searchTerm);

		deleteDocument(
			assetVocabulary.getCompanyId(), uidFactory.getUID(assetVocabulary));

		assertNoHits(searchTerm);

		reindexAllIndexerModels();

		assertFieldValue(Field.NAME, searchTerm, searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValue(
		String fieldName, String fieldValue, String searchTerm) {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, search(searchTerm));
	}

	protected void assertNoHits(String searchTerm) {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), search(searchTerm));
	}

	protected void deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	protected void reindexAllIndexerModels() throws Exception {
		indexer.reindexCompany(_group.getCompanyId());
	}

	protected SearchResponse search(String searchTerm) {
		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				AssetVocabulary.class
			).queryString(
				searchTerm
			).build());
	}

	@Inject
	protected AssetVocabularyService assetVocabularyService;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject(
		filter = "indexer.class.name=com.liferay.asset.kernel.model.AssetVocabulary"
	)
	protected Indexer<AssetVocabulary> indexer;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected UIDFactory uidFactory;

	@DeleteAfterTestRun
	private List<AssetVocabulary> _assetVocabularies;

	private AssetVocabularyFixture _assetVocabularyFixture;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

}
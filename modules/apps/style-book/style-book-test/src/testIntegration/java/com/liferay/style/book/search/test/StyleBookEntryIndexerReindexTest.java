/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.Collections;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class StyleBookEntryIndexerReindexTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test
	public void testReindex() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry();

		long styleBookEntryId = styleBookEntry.getStyleBookEntryId();

		String styleBookEntryKey = styleBookEntry.getStyleBookEntryKey();

		_assertFieldValue(
			"styleBookEntryKey", styleBookEntryKey, styleBookEntryKey);

		String originalName = styleBookEntry.getName();

		String newName = RandomTestUtil.randomString();

		_styleBookEntryLocalService.updateName(styleBookEntryId, newName);

		_assertFieldValue(Field.NAME, newName, styleBookEntryKey);

		_assertNoFieldValues(originalName);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntryId, true);

		_assertFieldValue(
			"defaultStyleBookEntry", Boolean.TRUE.toString(),
			styleBookEntryKey);

		String frontendTokensValues = "{}";

		_assertReindexAfterUnsafeRunnable(
			styleBookEntry,
			() -> _styleBookEntryLocalService.updateFrontendTokensValues(
				styleBookEntryId, frontendTokensValues));

		_assertReindexAfterUnsafeRunnable(
			styleBookEntry,
			() -> _styleBookEntryLocalService.updatePreviewFileEntryId(
				styleBookEntryId, 0L, _serviceContext));

		newName = RandomTestUtil.randomString();

		_styleBookEntryLocalService.updateStyleBookEntry(
			styleBookEntryId, frontendTokensValues, newName, _serviceContext);

		_assertFieldValue(Field.NAME, newName, styleBookEntryKey);

		newName = RandomTestUtil.randomString();

		_styleBookEntryLocalService.updateStyleBookEntry(
			TestPropsValues.getUserId(), styleBookEntryId, true,
			frontendTokensValues, newName, styleBookEntryKey, 0L,
			_serviceContext);

		_assertFieldValue(Field.NAME, newName, styleBookEntryKey);

		StyleBookEntry copyStyleBookEntry =
			_styleBookEntryLocalService.copyStyleBookEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				styleBookEntryId, _serviceContext);

		String copyStyleBookEntryId = String.valueOf(
			copyStyleBookEntry.getStyleBookEntryId());

		_assertFieldValue(
			Field.ENTRY_CLASS_PK, copyStyleBookEntryId, copyStyleBookEntryId);

		_styleBookEntryLocalService.deleteStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			styleBookEntry.getGroupId());

		_assertNoFieldValues(styleBookEntryKey);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private StyleBookEntry _addStyleBookEntry() throws Exception {
		return _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertFieldValue(
			String fieldName, String fieldValue, String queryString)
		throws Exception {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, _search(queryString));
	}

	private void _assertNoFieldValues(String queryString) throws Exception {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), _search(queryString));
	}

	private void _assertReindexAfterUnsafeRunnable(
			StyleBookEntry styleBookEntry,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		String styleBookEntryKey = styleBookEntry.getStyleBookEntryKey();

		_indexWriterHelper.deleteDocument(
			styleBookEntry.getCompanyId(), _uidFactory.getUID(styleBookEntry),
			true);

		_assertNoFieldValues(styleBookEntryKey);

		unsafeRunnable.run();

		_assertFieldValue(
			"styleBookEntryKey", styleBookEntryKey, styleBookEntryKey);
	}

	private SearchResponse _search(String searchTerm) throws Exception {
		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				StyleBookEntry.class
			).queryString(
				searchTerm
			).build());
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private IndexWriterHelper _indexWriterHelper;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private UIDFactory _uidFactory;

}
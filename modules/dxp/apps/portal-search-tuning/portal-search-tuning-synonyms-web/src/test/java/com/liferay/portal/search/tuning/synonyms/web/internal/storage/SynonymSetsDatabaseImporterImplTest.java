/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.storage;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReindexer;
import com.liferay.portal.search.tuning.synonyms.web.internal.storage.helper.SynonymSetJSONStorageHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class SynonymSetsDatabaseImporterImplTest
	extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_synonymSetsDatabaseImporterImpl =
			new SynonymSetsDatabaseImporterImpl();

		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "searchEngineAdapter",
			searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "synonymSetIndexNameBuilder",
			synonymSetIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "synonymSetIndexReindexer",
			_synonymSetIndexReindexer);
		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "synonymSetJSONStorageHelper",
			_synonymSetJSONStorageHelper);
	}

	@Test
	public void testPopulateDatabase() throws Exception {
		setUpSynonymSetIndexNameBuilder();
		setUpSearchEngineAdapter(setUpSearchHits("car,automobile"));

		_synonymSetsDatabaseImporterImpl.populateDatabase(111L);

		Mockito.verify(
			_synonymSetIndexReindexer, Mockito.times(1)
		).reindex(
			Mockito.anyLong(), Mockito.any(IndexReindexer.ExecutionMode.class)
		);
	}

	@Test
	public void testPopulateDatabaseExceptionBeforeReindex() throws Exception {
		_synonymSetsDatabaseImporterImpl.populateDatabase(111L);

		Mockito.verify(
			_synonymSetIndexReindexer, Mockito.never()
		).reindex(
			Mockito.anyLong(), Mockito.any(IndexReindexer.ExecutionMode.class)
		);
	}

	private final SynonymSetIndexReindexer _synonymSetIndexReindexer =
		Mockito.mock(SynonymSetIndexReindexer.class);
	private final SynonymSetJSONStorageHelper _synonymSetJSONStorageHelper =
		Mockito.mock(SynonymSetJSONStorageHelper.class);
	private SynonymSetsDatabaseImporterImpl _synonymSetsDatabaseImporterImpl;

}
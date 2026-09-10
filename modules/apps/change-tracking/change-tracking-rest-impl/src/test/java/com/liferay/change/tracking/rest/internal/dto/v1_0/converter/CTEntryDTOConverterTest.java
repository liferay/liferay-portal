/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.dto.v1_0.converter;

import com.liferay.change.tracking.rest.dto.v1_0.CTEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kiana Suetani
 */
public class CTEntryDTOConverterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_ctEntryDTOConverter, "_indexerRegistry", _indexerRegistry);
		ReflectionTestUtil.setFieldValue(
			_ctEntryDTOConverter, "_language", Mockito.mock(Language.class));

		Mockito.when(
			_serviceBuilderCTEntry.getCtEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);
	}

	@Test
	public void testToDTOFallsBackToIndexerDocument() throws Exception {
		Document document = new DocumentImpl();

		String ctCollectionName = RandomTestUtil.randomString();
		String groupName = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();
		String typeName = RandomTestUtil.randomString();

		document.addKeyword(_CT_COLLECTION_NAME, ctCollectionName);
		document.addLocalizedKeyword(
			_GROUP_NAME, _getLocalizedValues(groupName), false, true);
		document.addLocalizedText(
			_TYPE_NAME, _getLocalizedValues(typeName), true);
		document.addLocalizedText(
			Field.TITLE, _getLocalizedValues(title), true);

		Mockito.when(
			_indexer.getDocument(_serviceBuilderCTEntry)
		).thenReturn(
			document
		);

		Mockito.when(
			_indexerRegistry.getIndexer(
				com.liferay.change.tracking.model.CTEntry.class)
		).thenReturn(
			_indexer
		);

		CTEntry ctEntry = _ctEntryDTOConverter.toDTO(
			_getDTOConverterContext(LocaleUtil.US), _serviceBuilderCTEntry);

		Assert.assertEquals(ctCollectionName, ctEntry.getCtCollectionName());
		Assert.assertEquals(groupName, ctEntry.getSiteName());
		Assert.assertEquals(title, ctEntry.getTitle());
		Assert.assertEquals(typeName, ctEntry.getTypeName());
	}

	@Test
	public void testToDTOUsesContextDocument() throws Exception {
		Document document = new DocumentImpl();

		String ctCollectionName = RandomTestUtil.randomString();
		String groupName = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();

		document.addKeyword(_CT_COLLECTION_NAME, ctCollectionName);
		document.addKeyword(
			_getLocalizedName(LocaleUtil.SPAIN, Field.TITLE), title);
		document.addKeyword(
			_getLocalizedName(LocaleUtil.US, _GROUP_NAME), groupName);

		DefaultDTOConverterContext dtoConverterContext =
			_getDTOConverterContext(LocaleUtil.SPAIN);

		dtoConverterContext.setAttribute("document", document);

		CTEntry ctEntry = _ctEntryDTOConverter.toDTO(
			dtoConverterContext, _serviceBuilderCTEntry);

		Assert.assertEquals(ctCollectionName, ctEntry.getCtCollectionName());
		Assert.assertEquals(groupName, ctEntry.getSiteName());
		Assert.assertEquals(title, ctEntry.getTitle());
		Assert.assertEquals(StringPool.BLANK, ctEntry.getTypeName());

		Mockito.verify(
			_indexerRegistry, Mockito.never()
		).getIndexer(
			Mockito.any(Class.class)
		);
	}

	private DefaultDTOConverterContext _getDTOConverterContext(Locale locale) {
		return new DefaultDTOConverterContext(
			RandomTestUtil.randomBoolean(), new HashMap<>(), new HashMap<>(),
			null, null, _serviceBuilderCTEntry.getCtEntryId(), locale, null,
			null);
	}

	private String _getLocalizedName(Locale locale, String name) {
		return StringBundler.concat(
			name, StringPool.UNDERLINE, LocaleUtil.toLanguageId(locale));
	}

	private Map<Locale, String> _getLocalizedValues(String value) {
		return HashMapBuilder.put(
			LocaleUtil.US, value
		).build();
	}

	private static final String _CT_COLLECTION_NAME = "ctCollectionName";

	private static final String _GROUP_NAME = "groupName";

	private static final String _TYPE_NAME = "typeName";

	private final CTEntryDTOConverter _ctEntryDTOConverter =
		new CTEntryDTOConverter();
	private final Indexer<com.liferay.change.tracking.model.CTEntry> _indexer =
		Mockito.mock(Indexer.class);
	private final IndexerRegistry _indexerRegistry = Mockito.mock(
		IndexerRegistry.class);
	private final com.liferay.change.tracking.model.CTEntry
		_serviceBuilderCTEntry = Mockito.mock(
			com.liferay.change.tracking.model.CTEntry.class);

}
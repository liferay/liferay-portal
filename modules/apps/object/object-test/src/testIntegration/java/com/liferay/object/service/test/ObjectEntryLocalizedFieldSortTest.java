/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Guilherme Camacho
 */
@RunWith(Arquillian.class)
public class ObjectEntryLocalizedFieldSortTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_availableLocales = LanguageUtil.getAvailableLocales(
			TestPropsValues.getCompanyId());
		_defaultLocale = LocaleUtil.getDefault();

		Set<Locale> locales = new HashSet<>(_availableLocales);

		locales.add(LocaleUtil.BRAZIL);
		locales.add(LocaleUtil.GERMANY);
		locales.add(LocaleUtil.US);

		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), locales, LocaleUtil.US);

		_objectFieldName = "a" + RandomTestUtil.randomString();

		ObjectField objectField = ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, _objectFieldName,
			_objectFieldName);

		objectField.setLocalized(true);

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Arrays.asList(objectField), ObjectDefinitionConstants.SCOPE_SITE);

		_objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"en_US", "alpha"
			).put(
				"pt_BR", "zulu"
			).build());
		_objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"en_US", "bravo"
			).build());
		_objectEntry3 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"pt_BR", "charlie"
			).build());
		_objectEntry4 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"de_DE", "delta"
			).build());
	}

	@After
	public void tearDown() throws Exception {
		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), _availableLocales, _defaultLocale);
	}

	@Test
	public void testGetPrimaryKeysSortsBySiteDefaultLanguageValue()
		throws Exception {

		_assertPrimaryKeys(
			LocaleUtil.BRAZIL, false,
			Arrays.asList(_objectEntry2, _objectEntry3, _objectEntry1),
			new HashSet<>(Arrays.asList(_objectEntry4)));
	}

	@Test
	public void testGetPrimaryKeysSortsUntranslatedEntriesLast()
		throws Exception {

		_assertPrimaryKeys(
			LocaleUtil.US, false, Arrays.asList(_objectEntry1, _objectEntry2),
			new HashSet<>(Arrays.asList(_objectEntry3, _objectEntry4)));
		_assertPrimaryKeys(
			LocaleUtil.US, true, Arrays.asList(_objectEntry2, _objectEntry1),
			new HashSet<>(Arrays.asList(_objectEntry3, _objectEntry4)));
	}

	private ObjectEntry _addObjectEntry(
			Map<String, Serializable> localizedValues)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, "en_US",
			HashMapBuilder.<String, Serializable>put(
				_objectFieldName + "_i18n", (Serializable)localizedValues
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertPrimaryKeys(
			Locale locale, boolean reverse,
			List<ObjectEntry> orderedObjectEntries,
			Set<ObjectEntry> unorderedObjectEntries)
		throws Exception {

		Locale siteDefaultLocale = LocaleThreadLocal.getSiteDefaultLocale();
		Locale themeDisplayLocale = LocaleThreadLocal.getThemeDisplayLocale();

		try {
			LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.US);
			LocaleThreadLocal.setThemeDisplayLocale(locale);

			List<Long> actualPrimaryKeys =
				_objectEntryLocalService.getPrimaryKeys(
					new Long[] {TestPropsValues.getGroupId()},
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(),
					ObjectEntryTable.INSTANCE.objectEntryId.isNotNull(), false,
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					new Sort[] {
						new Sort(_objectFieldName, Sort.STRING_TYPE, reverse)
					});

			List<Long> expectedOrderedPrimaryKeys = TransformUtil.transform(
				orderedObjectEntries, ObjectEntry::getObjectEntryId);

			Assert.assertEquals(
				actualPrimaryKeys.toString(), expectedOrderedPrimaryKeys,
				actualPrimaryKeys.subList(
					0, expectedOrderedPrimaryKeys.size()));

			Set<Long> expectedUnorderedPrimaryKeys = new HashSet<>(
				TransformUtil.transform(
					new ArrayList<>(unorderedObjectEntries),
					ObjectEntry::getObjectEntryId));

			Assert.assertEquals(
				actualPrimaryKeys.toString(), expectedUnorderedPrimaryKeys,
				new HashSet<>(
					actualPrimaryKeys.subList(
						expectedOrderedPrimaryKeys.size(),
						actualPrimaryKeys.size())));
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(themeDisplayLocale);
			LocaleThreadLocal.setSiteDefaultLocale(siteDefaultLocale);
		}
	}

	private Set<Locale> _availableLocales;
	private Locale _defaultLocale;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	private ObjectEntry _objectEntry1;
	private ObjectEntry _objectEntry2;
	private ObjectEntry _objectEntry3;
	private ObjectEntry _objectEntry4;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private String _objectFieldName;

}
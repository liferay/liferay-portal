/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.helper.DDLRecordSetTestHelper;
import com.liferay.dynamic.data.lists.helper.DDLRecordTestHelper;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcellus Tavares
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class DDLRecordSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		DDLRecordSet recordSet = addRecordSet();

		_recordTestHelper = new DDLRecordTestHelper(
			_group, recordSet, _user.getUserId());
		_searchContext = getSearchContext(_group, recordSet, _user);
	}

	@Test
	public void testBasicSearchWithGuestUser() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		User user = UserLocalServiceUtil.getGuestUser(companyId);

		Group group = GroupTestUtil.addGroup(
			companyId, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		DDLRecordSetTestHelper recordSetTestHelper = new DDLRecordSetTestHelper(
			group);

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(DDLRecordSet.class), group);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			createDDMForm(LocaleUtil.US), StorageType.DEFAULT.toString());

		DDLRecordSet recordSet = recordSetTestHelper.addRecordSet(ddmStructure);

		SearchContext searchContext = getSearchContext(group, recordSet, user);

		DDLRecordTestHelper recordTestHelper = new DDLRecordTestHelper(
			group, recordSet);

		DDMFormValues ddmFormValues = createDDMFormValues(LocaleUtil.US);

		DDMFormFieldValue nameDDMFormFieldValue =
			createLocalizedDDMFormFieldValue(
				"name",
				HashMapBuilder.put(
					LocaleUtil.US, "Joe Bloggs"
				).build());

		ddmFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);

		DDMFormFieldValue descriptionDDMFormFieldValue =
			createLocalizedDDMFormFieldValue(
				"description",
				HashMapBuilder.put(
					LocaleUtil.US, "Simple description"
				).build());

		ddmFormValues.addDDMFormFieldValue(descriptionDDMFormFieldValue);

		recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		searchContext.setKeywords("Simple description");

		Hits hits = DDLRecordLocalServiceUtil.search(searchContext);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	@Test
	public void testBasicSearchWithJustOneTerm() throws Exception {
		addRecord("Joe Bloggs", "Simple description");
		addRecord("Bloggs", "Another description example");
		addRecord(RandomTestUtil.randomString(), RandomTestUtil.randomString());

		assertSearch("example", 1);
		assertSearch("description", 2);
	}

	@Test
	public void testExactPhrase() throws Exception {
		addRecord("Joe Bloggs", "Simple description");
		addRecord("Bloggs", "Another description example");
		addRecord(RandomTestUtil.randomString(), RandomTestUtil.randomString());

		assertSearch("\"Joe Bloggs\"", 1);
		assertSearch("Bloggs", 2);
	}

	@Test
	public void testExactPhraseMixedWithWords() throws Exception {
		addRecord("One Two Three Four Five Six", RandomTestUtil.randomString());
		addRecord(RandomTestUtil.randomString(), RandomTestUtil.randomString());

		assertSearch("\"Two Three\" Five", 1);
		assertSearch("\"Two Three\" Nine", 0);
		assertSearch("\"Two  Four\" Five", 0);
		assertSearch("\"Two  Four\" Nine", 0);
		assertSearch("Three \"Five Six\"", 1);
		assertSearch("Zero  \"Five Six\"", 0);
		assertSearch("Three \"Four Six\"", 0);
		assertSearch("Zero  \"Four Six\"", 0);
		assertSearch("One  \"Three Four\" Six ", 1);
		assertSearch("Zero \"Three Four\" Nine", 0);
		assertSearch("One  \"Three Five\" Six ", 0);
		assertSearch("Zero \"Three Five\" Nine", 0);
	}

	@Test
	public void testLocales() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		User user = UserLocalServiceUtil.getGuestUser(companyId);

		Group group = GroupTestUtil.addGroup(
			companyId, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		DDLRecordSetTestHelper recordSetTestHelper = new DDLRecordSetTestHelper(
			group, user.getUserId());

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(DDLRecordSet.class), group,
				user.getUserId());

		Set<Locale> locales = DDMFormTestUtil.createAvailableLocales(
			new Locale[] {LocaleUtil.US, LocaleUtil.JAPAN});

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(locales, LocaleUtil.US);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", true, false, false);

		LocalizedValue label = nameDDMFormField.getLabel();

		label.addString(LocaleUtil.JAPAN, "名");

		nameDDMFormField.setIndexType("keyword");

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.toString());

		DDLRecordSet recordSet = recordSetTestHelper.addRecordSet(ddmStructure);

		SearchContext searchContext = getSearchContext(group, recordSet, user);

		searchContext.setLocale(LocaleUtil.JAPAN);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue nameDDMFormFieldValue =
			createLocalizedDDMFormFieldValue(
				"name",
				HashMapBuilder.put(
					LocaleUtil.JAPAN, "単純なテキスト"
				).put(
					LocaleUtil.US, "simple text"
				).build());

		ddmFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);

		_recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		DDLRecordTestHelper recordTestHelper = new DDLRecordTestHelper(
			group, recordSet, user.getUserId());

		recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		searchContext.setKeywords("単純なテキスト");

		Hits hits = DDLRecordLocalServiceUtil.search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	@Test
	public void testPunctuationInExactPhrase() throws Exception {
		addRecord("Joe? Bloggs!");
		addRecord("Joe! Bloggs?");
		addRecord("Joe Bloggs");
		addRecord("Bloggs");

		assertSearch("\"Joe? Bloggs!\"", 3);
		assertSearch("\"Joe! Bloggs?\"", 3);
	}

	@Test
	public void testQuestionMarksVersusStopwords1() throws Exception {
		addRecord(RandomTestUtil.randomString());
		addRecord("how ? create ? coupon");

		assertSearch("\"how ? create ? coupon\"", 1);
		assertSearch("\"how to create a coupon\"", 0);
		assertSearch("\"how with create the coupon\"", 0);
	}

	@Test
	public void testQuestionMarksVersusStopwords2() throws Exception {
		addRecord(RandomTestUtil.randomString());
		addRecord("how with create the coupon");

		assertSearch("\"how ? create ? coupon\"", 0);
		assertSearch("\"how to create a coupon\"", 1);
		assertSearch("\"how with create the coupon\"", 1);
	}

	@Test
	public void testQuestionMarksVersusStopwords3() throws Exception {
		addRecord(RandomTestUtil.randomString());
		addRecord("how to create a coupon");

		assertSearch("\"how ? create ? coupon\"", 0);
		assertSearch("\"how to create a coupon\"", 1);
		assertSearch("\"how with create the coupon\"", 1);
	}

	@Test
	public void testQuestionMarksVersusStopwords4() throws Exception {
		addRecord(RandomTestUtil.randomString());
		addRecord("how ! create ! coupon");

		assertSearch("\"how ? create ? coupon\"", 1);
		assertSearch("\"how to create a coupon\"", 0);
		assertSearch("\"how with create the coupon\"", 0);
	}

	@Test
	public void testStopwords() throws Exception {
		addRecord(RandomTestUtil.randomString());
		addRecord(RandomTestUtil.randomString(), "Another description example");

		assertSearch("Another The Example", 1);
	}

	@Test
	public void testStopwordsInExactPhrase() throws Exception {
		addRecord("how to create a coupon");
		addRecord("Joe Of Bloggs");
		addRecord("Joe Bloggs");
		addRecord("Bloggs");

		assertSearch("\"how to create a coupon\"", 1);
		assertSearch("\"how with create the coupon\"", 1);
		assertSearch("\"how Liferay create Liferay coupon\"", 0);
		assertSearch("\"how create coupon\"", 0);
		assertSearch("\"Joe Of Bloggs\"", 1);
		assertSearch("\"Joe The Bloggs\"", 1);
		assertSearch("\"Bloggs A\"", 3);
		assertSearch("\"Of Bloggs\"", 3);
		assertSearch("\"The Bloggs\"", 3);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected static SearchContext getSearchContext(
			Group group, DDLRecordSet recordSet, User user)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			group.getGroupId());

		searchContext.setAttribute("recordSetId", recordSet.getRecordSetId());
		searchContext.setAttribute("status", WorkflowConstants.STATUS_ANY);
		searchContext.setUserId(user.getUserId());

		return searchContext;
	}

	protected void addRecord(Map<Locale, String> name) throws Exception {
		Map<Locale, String> description = new HashMap<>();

		for (Map.Entry<Locale, String> entry : name.entrySet()) {
			description.put(entry.getKey(), RandomTestUtil.randomString());
		}

		addRecord(name, description);
	}

	protected void addRecord(
			Map<Locale, String> name, Map<Locale, String> description)
		throws Exception {

		Locale[] locales = new Locale[name.size()];

		Set<Locale> localesKeySet = name.keySet();

		localesKeySet.toArray(locales);

		DDMFormValues ddmFormValues = createDDMFormValues(locales);

		DDMFormFieldValue nameDDMFormFieldValue =
			createLocalizedDDMFormFieldValue("name", name);

		ddmFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);

		DDMFormFieldValue descriptionDDMFormFieldValue =
			createLocalizedDDMFormFieldValue("description", description);

		ddmFormValues.addDDMFormFieldValue(descriptionDDMFormFieldValue);

		_recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);
	}

	protected void addRecord(String name) throws Exception {
		addRecord(
			HashMapBuilder.put(
				LocaleUtil.US, name
			).build());
	}

	protected void addRecord(String name, String description) throws Exception {
		addRecord(
			HashMapBuilder.put(
				LocaleUtil.US, name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, description
			).build());
	}

	protected DDLRecordSet addRecordSet() throws Exception {
		DDLRecordSetTestHelper recordSetTestHelper = new DDLRecordSetTestHelper(
			_group, _user.getUserId());

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(DDLRecordSet.class), _group,
				_user.getUserId());

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			createDDMForm(LocaleUtil.US), StorageType.DEFAULT.toString());

		return recordSetTestHelper.addRecordSet(ddmStructure);
	}

	protected void assertSearch(String keywords, int length) throws Exception {
		_searchContext.setKeywords(keywords);

		Hits hits = DDLRecordLocalServiceUtil.search(_searchContext);

		Assert.assertEquals(hits.toString(), length, hits.getLength());
	}

	protected DDMForm createDDMForm(Locale... locales) {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(locales), locales[0]);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", true, false, false);

		nameDDMFormField.setIndexType("keyword");

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormField descriptionDDMFormField =
			DDMFormTestUtil.createTextDDMFormField(
				"description", true, false, false);

		descriptionDDMFormField.setIndexType("text");

		ddmForm.addDDMFormField(descriptionDDMFormField);

		return ddmForm;
	}

	protected DDMFormValues createDDMFormValues(Locale... locales)
		throws Exception {

		DDLRecordSet recordSet = _recordTestHelper.getRecordSet();

		DDMStructure ddmStructure = recordSet.getDDMStructure();

		return DDMFormValuesTestUtil.createDDMFormValues(
			ddmStructure.getDDMForm(),
			DDMFormValuesTestUtil.createAvailableLocales(locales), locales[0]);
	}

	protected DDMFormFieldValue createLocalizedDDMFormFieldValue(
		String name, Map<Locale, String> values) {

		Value localizedValue = new LocalizedValue(LocaleUtil.US);

		for (Map.Entry<Locale, String> value : values.entrySet()) {
			localizedValue.addString(value.getKey(), value.getValue());
		}

		return DDMFormValuesTestUtil.createDDMFormFieldValue(
			name, localizedValue);
	}

	@DeleteAfterTestRun
	private Group _group;

	private DDLRecordTestHelper _recordTestHelper;
	private SearchContext _searchContext;

	@DeleteAfterTestRun
	private User _user;

}
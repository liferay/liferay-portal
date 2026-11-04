/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategory;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.filter.InvalidFilterException;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceFilterTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testFilterByUnknownObjectField() throws Exception {
		String filterString =
			"x" + RandomTestUtil.randomString() + " eq 'value'";

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() + "?filter=" +
				escape(filterString),
			Http.Method.GET);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title",
				"A property used in the filter criteria is not supported: " +
					filterString
			).put(
				"type", InvalidFilterException.class.getSimpleName()
			).toString(),
			jsonObject.toString(), JSONCompareMode.STRICT);
	}

	@Test
	public void testGetObjectEntriesFilteredBySystemDate() throws Exception {
		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry1.setCreateDate(
			dateTimeDateFormat.parse("2023-09-20T10:00:00.150Z"));
		objectEntry1.setModifiedDate(
			dateTimeDateFormat.parse("2023-09-20T10:00:00.150Z"));

		objectEntry1 = objectEntryLocalService.updateObjectEntry(objectEntry1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2);

		objectEntry2.setCreateDate(
			dateTimeDateFormat.parse("2023-09-20T10:05:00.450Z"));
		objectEntry2.setModifiedDate(
			dateTimeDateFormat.parse("2023-09-20T10:05:00.450Z"));

		objectEntry2 = objectEntryLocalService.updateObjectEntry(objectEntry2);

		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_3);

		objectEntry3.setCreateDate(null);
		objectEntry3.setModifiedDate(null);

		objectEntry3 = objectEntryLocalService.updateObjectEntry(objectEntry3);

		_testGetObjectEntriesFilteredBySystemDate("dateCreated");
		_testGetObjectEntriesFilteredBySystemDate("dateModified");

		Locale locale = LocaleUtil.getDefault();

		try {
			LocaleUtil.setDefault(
				LocaleUtil.SPAIN.getLanguage(), LocaleUtil.SPAIN.getCountry(),
				LocaleUtil.SPAIN.getVariant());

			_testGetObjectEntriesFilteredBySystemDate("dateCreated");
			_testGetObjectEntriesFilteredBySystemDate("dateModified");
		}
		finally {
			LocaleUtil.setDefault(
				locale.getLanguage(), locale.getCountry(), locale.getVariant());
		}
	}

	@Test
	public void testGetObjectEntryFilteredByKeywords() throws Exception {
		_postObjectEntryWithKeywords();
		_postObjectEntryWithKeywords("tag1");
		_postObjectEntryWithKeywords("TAG1");
		_postObjectEntryWithKeywords("tag1", "tag2");
		_postObjectEntryWithKeywords("tag1", "tag2", "tag3");

		_assertFilteredObjectEntries(4, "keywords/any()");

		_assertFilteredObjectEntries(0, "keywords/any(k:contains(k,'1234'))");
		_assertFilteredObjectEntries(4, "keywords/any(k:contains(k,'AG1'))");
		_assertFilteredObjectEntries(4, "keywords/any(k:contains(k,'ag1'))");
		_assertFilteredObjectEntries(2, "keywords/any(k:contains(k,'ag2'))");
		_assertFilteredObjectEntries(1, "keywords/any(k:contains(k,'ag3'))");
		_assertFilteredObjectEntries(4, "keywords/any(k:contains(k,'tag'))");

		_assertFilteredObjectEntries(0, "keywords/any(k:k eq '1234')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k eq 'TAG1')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k eq 'tag1')");
		_assertFilteredObjectEntries(2, "keywords/any(k:k eq 'tag2')");
		_assertFilteredObjectEntries(1, "keywords/any(k:k eq 'tag3')");

		_assertFilteredObjectEntries(4, "keywords/any(k:k ge 'TAG1')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k ge 'tag1')");
		_assertFilteredObjectEntries(2, "keywords/any(k:k ge 'tag2')");
		_assertFilteredObjectEntries(1, "keywords/any(k:k ge 'tag3')");

		_assertFilteredObjectEntries(2, "keywords/any(k:k gt 'TAG1')");
		_assertFilteredObjectEntries(2, "keywords/any(k:k gt 'tag1')");
		_assertFilteredObjectEntries(1, "keywords/any(k:k gt 'tag2')");
		_assertFilteredObjectEntries(0, "keywords/any(k:k gt 'tag3')");

		_assertFilteredObjectEntries(
			0, "keywords/any(k:k in ('1234', '5678'))");
		_assertFilteredObjectEntries(
			4, "keywords/any(k:k in ('TAG1', 'tag2'))");
		_assertFilteredObjectEntries(
			4, "keywords/any(k:k in ('tag1', 'tag2'))");
		_assertFilteredObjectEntries(
			2, "keywords/any(k:k in ('tag2', 'tag3'))");

		_assertFilteredObjectEntries(4, "keywords/any(k:k le 'TAG1')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k le 'tag1')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k le 'tag2')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k le 'tag3')");

		_assertFilteredObjectEntries(0, "keywords/any(k:k lt 'TAG1')");
		_assertFilteredObjectEntries(0, "keywords/any(k:k lt 'tag1')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k lt 'tag2')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k lt 'tag3')");

		_assertFilteredObjectEntries(2, "keywords/any(k:k ne 'TAG1')");
		_assertFilteredObjectEntries(2, "keywords/any(k:k ne 'tag1')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k ne 'tag2')");
		_assertFilteredObjectEntries(4, "keywords/any(k:k ne 'tag3')");

		_assertFilteredObjectEntries(0, "keywords/any(k:startswith(k,'1234'))");
		_assertFilteredObjectEntries(4, "keywords/any(k:startswith(k,'TAG1'))");
		_assertFilteredObjectEntries(4, "keywords/any(k:startswith(k,'t'))");
		_assertFilteredObjectEntries(4, "keywords/any(k:startswith(k,'ta'))");
		_assertFilteredObjectEntries(4, "keywords/any(k:startswith(k,'tag'))");
		_assertFilteredObjectEntries(4, "keywords/any(k:startswith(k,'tag1'))");
		_assertFilteredObjectEntries(2, "keywords/any(k:startswith(k,'tag2'))");
		_assertFilteredObjectEntries(1, "keywords/any(k:startswith(k,'tag3'))");

		_assertFilteredObjectEntries(5, "not (keywords/any(k:k eq '1234'))");
		_assertFilteredObjectEntries(1, "not (keywords/any(k:k eq 'TAG1'))");
		_assertFilteredObjectEntries(1, "not (keywords/any(k:k eq 'tag1'))");
		_assertFilteredObjectEntries(3, "not (keywords/any(k:k eq 'tag2'))");
		_assertFilteredObjectEntries(4, "not (keywords/any(k:k eq 'tag3'))");
		_assertFilteredObjectEntries(
			3, "not (keywords/any(k:k in ('tag2', 'tag3')))");
		_assertFilteredObjectEntries(1, "not keywords/any()");
	}

	@Test
	public void testGetObjectEntryFilteredByMultiselectPicklistObjectField()
		throws Exception {

		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).build(),
			TAG_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1
			).build(),
			TAG_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				(Serializable)Arrays.asList(
					LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).build(),
			TAG_1);
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				(Serializable)Arrays.asList(
					LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2,
					LIST_TYPE_ENTRY_KEY_3)
			).build(),
			TAG_1);

		_assertFilteredObjectEntries(
			3,
			String.format("%s/any()", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
		_assertFilteredObjectEntries(
			0,
			String.format(
				"%s/any(k:contains(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				RandomTestUtil.randomString()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"%s/any(k:contains(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_1.substring(1)));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"%s/any(k:contains(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_2.substring(1)));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"%s/any(k:contains(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_3.substring(1)));
		_assertFilteredObjectEntries(
			0,
			String.format(
				"%s/any(k:k eq '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				RandomTestUtil.randomString()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"%s/any(k:k eq '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_1));
		_assertFilteredObjectEntries(
			0,
			String.format(
				"%s/any(k:k eq '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_1.substring(1)));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"%s/any(k:k eq '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_2));
		_assertFilteredObjectEntries(
			0,
			String.format(
				"%s/any(k:k eq '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_2.substring(1)));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"%s/any(k:k eq '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_3));
		_assertFilteredObjectEntries(
			0,
			String.format(
				"%s/any(k:k eq '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_3.substring(1)));
		_assertFilteredObjectEntries(
			0,
			String.format(
				"%s/any(k:k in ('%s', '%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"%s/any(k:k in ('%s', '%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1,
				LIST_TYPE_ENTRY_KEY_2));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"%s/any(k:k in ('%s', '%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_2,
				LIST_TYPE_ENTRY_KEY_3));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"%s/any(k:k ne '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				RandomTestUtil.randomString()));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"%s/any(k:k ne '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_1));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"%s/any(k:k ne '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_2));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"%s/any(k:k ne '%s')", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_3));
		_assertFilteredObjectEntries(
			0,
			String.format(
				"%s/any(k:startswith(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				RandomTestUtil.randomString()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"%s/any(k:startswith(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"%s/any(k:startswith(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_1.substring(0, 2)));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"%s/any(k:startswith(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_2));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"%s/any(k:startswith(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_2.substring(0, 2)));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"%s/any(k:startswith(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"%s/any(k:startswith(k,'%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				LIST_TYPE_ENTRY_KEY_3.substring(0, 2)));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"not (%s/any(k:(k eq '%s') or (k eq '%s')))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_2,
				LIST_TYPE_ENTRY_KEY_3));
		_assertFilteredObjectEntries(
			4,
			String.format(
				"not (%s/any(k:k eq '%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				RandomTestUtil.randomString()));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"not (%s/any(k:k eq '%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_1));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"not (%s/any(k:k eq '%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_2));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"not (%s/any(k:k eq '%s'))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_3));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"not (%s/any(k:k in ('%s', '%s')))",
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, LIST_TYPE_ENTRY_KEY_2,
				LIST_TYPE_ENTRY_KEY_3));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"not %s/any()", OBJECT_FIELD_NAME_MULTISELECT_PICKLIST));
	}

	@Test
	public void testGetObjectEntryFilteredByStatus() throws Exception {
		getObjectDefinition1().setEnableObjectEntryDraft(true);

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			getObjectDefinition1());

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"status", JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		_assertFilteredObjectEntries(2, "status eq 0");
		_assertFilteredObjectEntries(1, "status eq 2");
		_assertFilteredObjectEntries(2, "status/any(k:k eq 0)");
		_assertFilteredObjectEntries(1, "status/any(k:k eq 2)");
	}

	@Test
	public void testGetObjectEntryFilteredByTaxonomyCategories()
		throws Exception {

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), company.getGroupId(),
				RandomTestUtil.randomString(), new ServiceContext());

		TaxonomyCategory taxonomyCategory1 =
			postTaxonomyVocabularyTaxonomyCategory(
				company.getGroupId(), assetVocabulary.getVocabularyId());
		TaxonomyCategory taxonomyCategory2 =
			postTaxonomyVocabularyTaxonomyCategory(
				company.getGroupId(), assetVocabulary.getVocabularyId());
		TaxonomyCategory taxonomyCategory3 =
			postTaxonomyVocabularyTaxonomyCategory(
				company.getGroupId(), assetVocabulary.getVocabularyId());

		_postObjectEntryWithTaxonomyCategories();
		_postObjectEntryWithTaxonomyCategories(taxonomyCategory1);
		_postObjectEntryWithTaxonomyCategories(
			taxonomyCategory1, taxonomyCategory2);
		_postObjectEntryWithTaxonomyCategories(
			taxonomyCategory1, taxonomyCategory2, taxonomyCategory3);

		_assertFilteredObjectEntries(3, "taxonomyCategoryIds/any()");
		_assertFilteredObjectEntries(0, "taxonomyCategoryIds/any(k:k eq 1234)");
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k eq %s)",
				taxonomyCategory1.getId()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k eq %s)",
				taxonomyCategory1.getId()));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"taxonomyCategoryIds/any(k:k eq %s)",
				taxonomyCategory2.getId()));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"taxonomyCategoryIds/any(k:k eq %s)",
				taxonomyCategory3.getId()));

		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k ge %s)",
				taxonomyCategory1.getId()));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"taxonomyCategoryIds/any(k:k ge %s)",
				taxonomyCategory2.getId()));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"taxonomyCategoryIds/any(k:k ge %s)",
				taxonomyCategory3.getId()));

		_assertFilteredObjectEntries(
			2,
			String.format(
				"taxonomyCategoryIds/any(k:k gt %s)",
				taxonomyCategory1.getId()));
		_assertFilteredObjectEntries(
			1,
			String.format(
				"taxonomyCategoryIds/any(k:k gt %s)",
				taxonomyCategory2.getId()));
		_assertFilteredObjectEntries(
			0,
			String.format(
				"taxonomyCategoryIds/any(k:k gt %s)",
				taxonomyCategory3.getId()));

		_assertFilteredObjectEntries(
			0, "taxonomyCategoryIds/any(k:k in (1234, 5678))");
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k in (%s, %s))",
				taxonomyCategory1.getId(), taxonomyCategory2.getId()));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"taxonomyCategoryIds/any(k:k in (%s, %s))",
				taxonomyCategory2.getId(), taxonomyCategory3.getId()));

		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k le %s)",
				taxonomyCategory1.getId()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k le %s)",
				taxonomyCategory2.getId()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k le %s)",
				taxonomyCategory3.getId()));

		_assertFilteredObjectEntries(
			0,
			String.format(
				"taxonomyCategoryIds/any(k:k lt %s)",
				taxonomyCategory1.getId()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k lt %s)",
				taxonomyCategory2.getId()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k lt %s)",
				taxonomyCategory3.getId()));

		_assertFilteredObjectEntries(
			2,
			String.format(
				"taxonomyCategoryIds/any(k:k ne %s)",
				taxonomyCategory1.getId()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k ne %s)",
				taxonomyCategory2.getId()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"taxonomyCategoryIds/any(k:k ne %s)",
				taxonomyCategory3.getId()));

		_assertFilteredObjectEntries(
			4, "not (taxonomyCategoryIds/any(k:k eq 1234))");
		_assertFilteredObjectEntries(
			1,
			String.format(
				"not (taxonomyCategoryIds/any(k:k eq %s))",
				taxonomyCategory1.getId()));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"not (taxonomyCategoryIds/any(k:k eq %s))",
				taxonomyCategory2.getId()));
		_assertFilteredObjectEntries(
			3,
			String.format(
				"not (taxonomyCategoryIds/any(k:k eq %s))",
				taxonomyCategory3.getId()));
		_assertFilteredObjectEntries(
			2,
			String.format(
				"not (taxonomyCategoryIds/any(k:k in (%s, %s)))",
				taxonomyCategory2.getId(), taxonomyCategory3.getId()));
		_assertFilteredObjectEntries(1, "not taxonomyCategoryIds/any()");
	}

	private void _assertFilteredObjectEntries(
			int expectedObjectEntryCount, String filterString)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() + "?filter=" +
				URLCodec.encodeURL(filterString),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(expectedObjectEntryCount, itemsJSONArray.length());
	}

	private void _postObjectEntryWithKeywords(String... keywords)
		throws Exception {

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"keywords", JSONUtil.putAll(keywords)
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
	}

	private void _postObjectEntryWithTaxonomyCategories(
			TaxonomyCategory... taxonomyCategories)
		throws Exception {

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"taxonomyCategoryIds",
				TransformUtil.transform(
					taxonomyCategories, TaxonomyCategory::getId, String.class)
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
	}

	private void _testGetObjectEntriesFilteredBySystemDate(String fieldName)
		throws Exception {

		// eq

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			URLCodec.encodeURL(fieldName + " eq 2023-09-20T10:00:00Z"),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			URLCodec.encodeURL(fieldName + " eq 2023-09-20T10:00:00.999Z"),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2,
			URLCodec.encodeURL(fieldName + " eq 2023-09-20T10:05:00Z"),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2,
			URLCodec.encodeURL(fieldName + " eq 2023-09-20T10:05:00.999Z"),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_3,
			URLCodec.encodeURL(fieldName + " eq null"), getObjectDefinition1());

		// ge

		_assertFilteredObjectEntries(2, fieldName + " ge 2023-09-20T10:00:00Z");
		_assertFilteredObjectEntries(
			2, fieldName + " ge 2023-09-20T10:00:00.999Z");
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2,
			URLCodec.encodeURL(fieldName + " ge 2023-09-20T10:00:01Z"),
			getObjectDefinition1());

		// gt

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2,
			URLCodec.encodeURL(fieldName + " gt 2023-09-20T10:00:00Z"),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2,
			URLCodec.encodeURL(fieldName + " gt 2023-09-20T10:00:00.999Z"),
			getObjectDefinition1());

		// in

		_assertFilteredObjectEntries(
			2, fieldName + " in (2023-09-20T10:05:00Z, 2023-09-20T10:00:00Z)");

		// le

		_assertFilteredObjectEntries(2, fieldName + " le 2023-09-20T10:05:00Z");
		_assertFilteredObjectEntries(
			2, fieldName + " le 2023-09-20T10:05:00.999Z");
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			URLCodec.encodeURL(fieldName + " le 2023-09-20T10:04:00.999Z"),
			getObjectDefinition1());

		// lt

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			URLCodec.encodeURL(fieldName + " lt 2023-09-20T10:05:00Z"),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			URLCodec.encodeURL(fieldName + " lt 2023-09-20T10:05:00.999Z"),
			getObjectDefinition1());

		// ne

		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1,
			URLCodec.encodeURL(fieldName + " ne 2023-09-20T10:05:00Z"),
			getObjectDefinition1());
		assertFilterString(
			OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_2,
			URLCodec.encodeURL(fieldName + " ne 2023-09-20T10:00:00Z"),
			getObjectDefinition1());
		_assertFilteredObjectEntries(2, fieldName + " ne null");
	}

}
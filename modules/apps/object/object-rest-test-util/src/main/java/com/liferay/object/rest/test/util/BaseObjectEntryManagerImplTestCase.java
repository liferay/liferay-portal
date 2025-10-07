/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.test.util;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;

import java.text.DateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;

/**
 * @author Paulo Albuquerque
 */
public abstract class BaseObjectEntryManagerImplTestCase {

	@Before
	public void setUp() throws Exception {
		dtoConverterContext = new DefaultDTOConverterContext(
			false, Collections.emptyMap(), dtoConverterRegistry, null,
			LocaleUtil.getDefault(), null, adminUser);
	}

	protected void assertEquals(
			List<ObjectEntry> actualObjectEntries,
			List<ObjectEntry> expectedObjectEntries)
		throws Exception {

		Assert.assertEquals(
			actualObjectEntries.toString(), expectedObjectEntries.size(),
			actualObjectEntries.size());

		for (int i = 0; i < expectedObjectEntries.size(); i++) {
			assertEquals(
				actualObjectEntries.get(i), expectedObjectEntries.get(i));
		}
	}

	protected void assertEquals(
			ObjectEntry actualObjectEntry, ObjectEntry expectedObjectEntry)
		throws Exception {

		Map<String, Map<String, String>> actualOjectEntryActions =
			actualObjectEntry.getActions();

		Map<String, Map<String, String>> expectedObjectEntryActions =
			expectedObjectEntry.getActions();

		if (expectedObjectEntryActions != null) {
			for (Map.Entry<String, Map<String, String>> actualActions :
					actualOjectEntryActions.entrySet()) {

				Assert.assertEquals(
					actualActions.getKey(), actualActions.getValue(),
					expectedObjectEntryActions.get(actualActions.getKey()));
			}
		}

		Map<String, Object> actualObjectEntryProperties =
			actualObjectEntry.getProperties();

		Map<String, Object> expectedObjectEntryProperties =
			expectedObjectEntry.getProperties();

		for (Map.Entry<String, Object> expectedEntry :
				expectedObjectEntryProperties.entrySet()) {

			assertObjectEntryProperties(
				actualObjectEntry, actualObjectEntryProperties, expectedEntry);
		}

		if (expectedObjectEntry.getStatus() != null) {
			Assert.assertEquals(
				_getObjectEntryStatusCode(expectedObjectEntry),
				_getObjectEntryStatusCode(actualObjectEntry));
		}
	}

	protected void assertEquals(
			Page<ObjectEntry> actualPage, Page<ObjectEntry> expectedPage)
		throws Exception {

		if (expectedPage.getFacets() != null) {
			assertFacets(actualPage.getFacets(), expectedPage.getFacets());
		}

		assertEquals(
			(List<ObjectEntry>)actualPage.getItems(),
			(List<ObjectEntry>)expectedPage.getItems());

		Assert.assertEquals(
			expectedPage.getTotalCount(), actualPage.getTotalCount());
	}

	protected void assertFacets(
			List<Facet> actualFacets, List<Facet> expectedFacets)
		throws Exception {

		Assert.assertEquals(
			actualFacets.toString(), expectedFacets.size(),
			actualFacets.size());

		for (int i = 0; i < expectedFacets.size(); i++) {
			Facet actualFacet = actualFacets.get(i);
			Facet expectedFacet = expectedFacets.get(i);

			Assert.assertEquals(
				expectedFacet.getFacetCriteria(),
				actualFacet.getFacetCriteria());

			List<Facet.FacetValue> actualFacetFacetValues =
				actualFacet.getFacetValues();

			List<Facet.FacetValue> expectedFacetFacetValues =
				expectedFacet.getFacetValues();

			Assert.assertEquals(
				actualFacetFacetValues.toString(),
				expectedFacetFacetValues.size(), actualFacetFacetValues.size());

			for (int j = 0; j < expectedFacetFacetValues.size(); j++) {
				Facet.FacetValue actualFacetValue = actualFacetFacetValues.get(
					j);
				Facet.FacetValue expectedFacetValue =
					expectedFacetFacetValues.get(j);

				Assert.assertEquals(
					expectedFacetValue.getNumberOfOccurrences(),
					actualFacetValue.getNumberOfOccurrences());
				Assert.assertEquals(
					expectedFacetValue.getTerm(), actualFacetValue.getTerm());
			}
		}
	}

	protected void assertObjectEntryProperties(
			ObjectEntry actualObjectEntry,
			Map<String, Object> actualObjectEntryProperties,
			Map.Entry<String, Object> expectedEntry)
		throws Exception {

		Assert.assertEquals(
			expectedEntry.getKey(), expectedEntry.getValue(),
			actualObjectEntryProperties.get(expectedEntry.getKey()));
	}

	protected String buildEqualsExpressionFilterString(
		String fieldName, Object value) {

		return StringBundler.concat(fieldName, " eq ", getValue(value));
	}

	protected String buildRangeExpression(
		Date date1, Date date2, String fieldName, String pattern) {

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			pattern);

		return StringBundler.concat(
			"(( ", fieldName, " ge ", dateFormat.format(date1), ") and ( ",
			fieldName, " le ", dateFormat.format(date2), "))");
	}

	protected Page<ObjectEntry> getObjectEntries(
			Map<String, String> context, Sort[] sorts)
		throws Exception {

		return null;
	}

	protected Sort[] getSorts(String sortString) {
		if (sortString == null) {
			return new Sort[] {SortFactoryUtil.create("createDate", false)};
		}

		String[] parts = StringUtil.split(sortString, ":");

		return new Sort[] {
			SortFactoryUtil.create(parts[0], Objects.equals(parts[1], "desc"))
		};
	}

	protected String getValue(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyy-MM-dd");

			return dateFormat.format(value);
		}
		else if (value instanceof LocalDateTime) {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

			LocalDateTime localDateTime = (LocalDateTime)value;

			return dateTimeFormatter.format(localDateTime.withNano(0));
		}
		else if (value instanceof String) {
			return StringUtil.quote(String.valueOf(value));
		}

		return String.valueOf(value);
	}

	protected void testGetObjectEntries(
			Map<String, String> context, ObjectEntry... expectedObjectEntries)
		throws Exception {

		assertEquals(
			getObjectEntries(context, getSorts(context.get("sort"))),
			Page.of(
				ListUtil.fromArray(expectedObjectEntries), null,
				expectedObjectEntries.length));
	}

	protected static User adminUser;
	protected static long companyId;
	protected static DTOConverterContext dtoConverterContext;

	@Inject
	protected static DTOConverterRegistry dtoConverterRegistry;

	@Inject
	protected static ObjectDefinitionLocalService objectDefinitionLocalService;

	protected ListTypeDefinition listTypeDefinition;

	@Inject
	protected ListTypeDefinitionLocalService listTypeDefinitionLocalService;

	@Inject
	protected ObjectFieldLocalService objectFieldLocalService;

	private Integer _getObjectEntryStatusCode(ObjectEntry objectEntry) {
		Status objectEntryStatus = objectEntry.getStatus();

		return objectEntryStatus.getCode();
	}

}
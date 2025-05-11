/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.client.pagination.Page;
import com.liferay.headless.admin.list.type.client.pagination.Pagination;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ListTypeEntryResourceTest
	extends BaseListTypeEntryResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_listTypeDefinition =
			ListTypeDefinitionLocalServiceUtil.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(LocaleUtil.getDefault(), "test"),
				false, Collections.emptyList());
		_systemListTypeDefinition =
			ListTypeDefinitionLocalServiceUtil.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				true, Collections.emptyList());
	}

	@FeatureFlag("LPD-24055")
	@Override
	@Test
	public void testDeleteListTypeEntry() throws Exception {
		super.testDeleteListTypeEntry();

		ListTypeEntry listTypeEntry = _addListTypeEntry(
			_systemListTypeDefinition);

		assertHttpResponseStatusCode(
			204,
			listTypeEntryResource.deleteListTypeEntryHttpResponse(
				listTypeEntry.getId()));

		assertHttpResponseStatusCode(
			404,
			listTypeEntryResource.getListTypeEntryHttpResponse(
				listTypeEntry.getId()));
	}

	@Override
	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSortInteger()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				if (BeanTestUtil.hasProperty(
						listTypeEntry1, entityField.getName())) {

					BeanTestUtil.setProperty(
						listTypeEntry1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						listTypeEntry2, entityField.getName())) {

					BeanTestUtil.setProperty(
						listTypeEntry2, entityField.getName(), 1);
				}
			});
	}

	@FeatureFlag("LPD-24055")
	@Override
	@Test
	public void testGetListTypeDefinitionListTypeEntriesPage()
		throws Exception {

		super.testGetListTypeDefinitionListTypeEntriesPage();

		ListTypeEntry customListTypeEntry = _addListTypeEntry(
			_systemListTypeDefinition, false);
		ListTypeEntry systemListTypeEntry = _addListTypeEntry(
			_systemListTypeDefinition, true);

		Page<ListTypeEntry> page =
			listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
				_systemListTypeDefinition.getListTypeDefinitionId(), null, null,
				null, Pagination.of(1, 10), null);

		List<ListTypeEntry> listTypeEntries =
			(List<ListTypeEntry>)page.getItems();

		Map<String, Map<String, String>> customListTypeEntryActions =
			_getActions(listTypeEntries, customListTypeEntry.getId());

		Assert.assertEquals(
			customListTypeEntryActions.toString(), 3,
			customListTypeEntryActions.size());

		Assert.assertTrue(customListTypeEntryActions.containsKey("delete"));
		Assert.assertTrue(customListTypeEntryActions.containsKey("get"));
		Assert.assertTrue(customListTypeEntryActions.containsKey("update"));

		Map<String, Map<String, String>> systemListTypeEntryActions =
			_getActions(listTypeEntries, systemListTypeEntry.getId());

		Assert.assertEquals(
			systemListTypeEntryActions.toString(), 2,
			systemListTypeEntryActions.size());

		Assert.assertTrue(systemListTypeEntryActions.containsKey("get"));
		Assert.assertTrue(systemListTypeEntryActions.containsKey("update"));

		listTypeEntryResource.deleteListTypeEntry(customListTypeEntry.getId());
		listTypeEntryResource.deleteListTypeEntry(systemListTypeEntry.getId());
	}

	@Override
	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithSortInteger()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				if (BeanTestUtil.hasProperty(
						listTypeEntry1, entityField.getName())) {

					BeanTestUtil.setProperty(
						listTypeEntry1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						listTypeEntry2, entityField.getName())) {

					BeanTestUtil.setProperty(
						listTypeEntry2, entityField.getName(), 1);
				}
			});
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetListTypeEntry() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetListTypeEntryNotFound() throws Exception {
	}

	@FeatureFlag("LPD-24055")
	@Override
	@Test
	public void testPostListTypeDefinitionListTypeEntry() throws Exception {
		super.testPostListTypeDefinitionListTypeEntry();

		ListTypeEntry listTypeEntry = randomListTypeEntry();

		listTypeEntry.setName(RandomTestUtil.randomString());
		listTypeEntry.setName_i18n((Map<String, String>)null);

		_assertListTypeEntryNameLocalizedMap(
			testPostListTypeDefinitionListTypeEntry_addListTypeEntry(
				listTypeEntry));

		listTypeEntry = _addListTypeEntry(_systemListTypeDefinition);

		assertEquals(
			listTypeEntry,
			listTypeEntryResource.getListTypeEntry(listTypeEntry.getId()));
	}

	@FeatureFlag("LPD-24055")
	@Override
	@Test
	public void testPutListTypeEntry() throws Exception {
		super.testPutListTypeEntry();

		ListTypeEntry listTypeEntry = testPutListTypeEntry_addListTypeEntry();

		listTypeEntry.setName(RandomTestUtil.randomString());
		listTypeEntry.setName_i18n((Map<String, String>)null);

		_assertListTypeEntryNameLocalizedMap(
			listTypeEntryResource.putListTypeEntry(
				listTypeEntry.getId(), listTypeEntry));

		listTypeEntry = _addListTypeEntry(_systemListTypeDefinition);

		listTypeEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		assertEquals(
			listTypeEntry,
			listTypeEntryResource.putListTypeEntry(
				listTypeEntry.getId(), listTypeEntry));
	}

	@Override
	protected ListTypeEntry randomListTypeEntry() throws Exception {
		return _randomListTypeEntry(false);
	}

	@Override
	protected ListTypeEntry testDeleteListTypeEntry_addListTypeEntry()
		throws Exception {

		return _addListTypeEntry(_listTypeDefinition);
	}

	@Override
	protected ListTypeEntry
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				String externalReferenceCode, ListTypeEntry listTypeEntry)
		throws Exception {

		return listTypeEntryResource.
			postListTypeDefinitionByExternalReferenceCodeListTypeEntry(
				externalReferenceCode, listTypeEntry);
	}

	@Override
	protected String
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExternalReferenceCode()
		throws Exception {

		return _listTypeDefinition.getExternalReferenceCode();
	}

	@Override
	protected Long
		testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId() {

		return _listTypeDefinition.getListTypeDefinitionId();
	}

	@Override
	protected ListTypeEntry testGetListTypeEntry_addListTypeEntry()
		throws Exception {

		return _addListTypeEntry(_listTypeDefinition);
	}

	@Override
	protected ListTypeEntry testGraphQLListTypeEntry_addListTypeEntry()
		throws Exception {

		return _addListTypeEntry(_listTypeDefinition);
	}

	@Override
	protected ListTypeEntry
			testPostListTypeDefinitionByExternalReferenceCodeListTypeEntry_addListTypeEntry(
				ListTypeEntry listTypeEntry)
		throws Exception {

		return listTypeEntryResource.
			postListTypeDefinitionByExternalReferenceCodeListTypeEntry(
				_listTypeDefinition.getExternalReferenceCode(), listTypeEntry);
	}

	@Override
	protected ListTypeEntry testPutListTypeEntry_addListTypeEntry()
		throws Exception {

		return _addListTypeEntry(_listTypeDefinition);
	}

	private ListTypeEntry _addListTypeEntry(
			ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _addListTypeEntry(listTypeDefinition, false);
	}

	private ListTypeEntry _addListTypeEntry(
			ListTypeDefinition listTypeDefinition, boolean system)
		throws Exception {

		return listTypeEntryResource.postListTypeDefinitionListTypeEntry(
			listTypeDefinition.getListTypeDefinitionId(),
			_randomListTypeEntry(system));
	}

	private void _assertListTypeEntryNameLocalizedMap(
		ListTypeEntry listTypeEntry) {

		Map<Locale, String> nameLocalizedMap = LocalizedMapUtil.getLocalizedMap(
			listTypeEntry.getName_i18n());

		Assert.assertEquals(
			listTypeEntry.getName(),
			nameLocalizedMap.get(LocaleUtil.getSiteDefault()));
	}

	private Map<String, Map<String, String>> _getActions(
		List<ListTypeEntry> listTypeEntries, long listTypeEntryId) {

		for (ListTypeEntry listTypeEntry : listTypeEntries) {
			if (Objects.equals(listTypeEntry.getId(), listTypeEntryId)) {
				return listTypeEntry.getActions();
			}
		}

		return null;
	}

	private ListTypeEntry _randomListTypeEntry(boolean system)
		throws Exception {

		ListTypeEntry listTypeEntry = super.randomListTypeEntry();

		listTypeEntry.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));
		listTypeEntry.setSystem(system);

		return listTypeEntry;
	}

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@DeleteAfterTestRun
	private ListTypeDefinition _systemListTypeDefinition;

}
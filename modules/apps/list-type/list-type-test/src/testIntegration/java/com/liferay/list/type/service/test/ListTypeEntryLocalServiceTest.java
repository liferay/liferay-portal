/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.exception.DuplicateListTypeEntryException;
import com.liferay.list.type.exception.DuplicateListTypeEntryExternalReferenceCodeException;
import com.liferay.list.type.exception.ListTypeDefinitionSystemException;
import com.liferay.list.type.exception.ListTypeEntryKeyException;
import com.liferay.list.type.exception.ListTypeEntrySystemException;
import com.liferay.list.type.exception.NoSuchListTypeDefinitionException;
import com.liferay.list.type.exception.NoSuchListTypeEntryException;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ListTypeEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, Collections.emptyList());

		_listTypeEntry = _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			_listTypeDefinition.getListTypeDefinitionId(), "able",
			Collections.singletonMap(LocaleUtil.US, "Able"),
			_listTypeDefinition.isSystem());

		_systemListTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				true, Collections.emptyList());

		_systemListTypeEntry = _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			_systemListTypeDefinition.getListTypeDefinitionId(), "able",
			Collections.singletonMap(LocaleUtil.US, "Able"),
			_systemListTypeDefinition.isSystem());
	}

	@After
	public void tearDown() throws Exception {
		_listTypeDefinitionLocalService.deleteListTypeDefinition(
			_listTypeDefinition);
	}

	@FeatureFlag("LPD-24055")
	@Test
	public void testAddListTypeEntry() throws Exception {
		String externalReferenceCode =
			_listTypeEntry.getExternalReferenceCode();

		Assert.assertEquals(_listTypeEntry.getUuid(), externalReferenceCode);

		AssertUtils.assertFailure(
			DuplicateListTypeEntryException.class, "Duplicate key able",
			() -> _testAddListTypeEntry(
				_listTypeDefinition.getListTypeDefinitionId(), "able",
				_listTypeDefinition.isSystem()));
		AssertUtils.assertFailure(
			DuplicateListTypeEntryExternalReferenceCodeException.class,
			"Duplicate external reference code " + externalReferenceCode,
			() -> _listTypeEntryLocalService.addListTypeEntry(
				externalReferenceCode, TestPropsValues.getUserId(),
				_listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				_listTypeDefinition.isSystem()));
		AssertUtils.assertFailure(
			ListTypeDefinitionSystemException.class, false,
			"Only allowed bundles can add system list type entries",
			() -> _testAddListTypeEntry(
				_systemListTypeDefinition.getListTypeDefinitionId(), "baker",
				_systemListTypeDefinition.isSystem()));
		AssertUtils.assertFailure(
			ListTypeEntryKeyException.class, "Key is null",
			() -> _testAddListTypeEntry(
				_listTypeDefinition.getListTypeDefinitionId(), null,
				_listTypeDefinition.isSystem()));
		AssertUtils.assertFailure(
			ListTypeEntryKeyException.class,
			"Key must only contain letters and digits",
			() -> _testAddListTypeEntry(
				_listTypeDefinition.getListTypeDefinitionId(), " able ",
				_listTypeDefinition.isSystem()));
		AssertUtils.assertFailure(
			ListTypeEntrySystemException.class, true,
			"System list type entries cannot be added to custom list type " +
				"definitions",
			() -> _testAddListTypeEntry(
				_listTypeDefinition.getListTypeDefinitionId(), "baker", true));
		AssertUtils.assertFailure(
			NoSuchListTypeDefinitionException.class,
			"No ListTypeDefinition exists with the primary key 0",
			() -> _testAddListTypeEntry(0, "able", false));

		ListTypeEntry listTypeEntry = _addListTypeEntry(
			_listTypeDefinition.getListTypeDefinitionId());

		_assertListTypeEntry(
			"externalReferenceCode", "baker", "Baker", listTypeEntry);

		listTypeEntry = _addListTypeEntry(
			_systemListTypeDefinition.getListTypeDefinitionId());

		_assertListTypeEntry(
			"externalReferenceCode", "baker", "Baker", listTypeEntry);
	}

	@Test
	public void testDeleteListTypeEntry() throws Exception {
		AssertUtils.assertFailure(
			ListTypeDefinitionSystemException.class, false,
			"Only allowed bundles can delete system list type entries",
			() -> _listTypeEntryLocalService.deleteListTypeEntry(
				_systemListTypeEntry.getListTypeEntryId()));
	}

	@Test
	public void testFetchListTypeEntry() throws Exception {
		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				_listTypeEntry.getListTypeEntryId());

		Assert.assertNotNull(listTypeEntry);

		listTypeEntry = _listTypeEntryLocalService.fetchListTypeEntry(0);

		Assert.assertNull(listTypeEntry);

		listTypeEntry =
			_listTypeEntryLocalService.
				fetchListTypeEntryByExternalReferenceCode(
					_listTypeEntry.getExternalReferenceCode(),
					_listTypeEntry.getCompanyId(),
					_listTypeDefinition.getListTypeDefinitionId());

		Assert.assertNotNull(listTypeEntry);

		listTypeEntry =
			_listTypeEntryLocalService.
				fetchListTypeEntryByExternalReferenceCode(
					null, _listTypeEntry.getCompanyId(),
					_listTypeDefinition.getListTypeDefinitionId());

		Assert.assertNull(listTypeEntry);
	}

	@Test
	public void testGetListTypeEntry() throws Exception {
		try {
			_listTypeEntryLocalService.getListTypeEntry(0, "able");
		}
		catch (NoSuchListTypeEntryException noSuchListTypeEntryException) {
			Assert.assertNotNull(noSuchListTypeEntryException);
		}

		Assert.assertNotNull(
			_listTypeEntryLocalService.getListTypeEntry(
				_listTypeDefinition.getListTypeDefinitionId(),
				_listTypeEntry.getKey()));

		try {
			_listTypeEntryLocalService.getListTypeEntryByExternalReferenceCode(
				RandomTestUtil.randomString(), _listTypeEntry.getCompanyId(),
				_listTypeDefinition.getListTypeDefinitionId());
		}
		catch (NoSuchListTypeEntryException noSuchListTypeEntryException) {
			Assert.assertNotNull(noSuchListTypeEntryException);
		}

		Assert.assertNotNull(
			_listTypeEntryLocalService.getListTypeEntryByExternalReferenceCode(
				_listTypeEntry.getExternalReferenceCode(),
				_listTypeEntry.getCompanyId(),
				_listTypeDefinition.getListTypeDefinitionId()));
	}

	@Test
	@TestInfo("LPD-55656")
	public void testGetOrAddIncompleteListTypeEntry() throws Exception {

		// Lazy referencing disabled

		try {
			_listTypeEntryLocalService.getOrAddIncompleteListTypeEntry(
				TestPropsValues.getUserId(),
				_listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (NoSuchListTypeEntryException noSuchListTypeEntryException) {
			Assert.assertNotNull(noSuchListTypeEntryException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			_testAddIncompleteListTypeEntry(_listTypeDefinition);
			_testAddIncompleteListTypeEntry(_systemListTypeDefinition);
		}
	}

	@FeatureFlag("LPD-24055")
	@Test
	public void testUpdateListTypeEntry() throws Exception {
		String externalReferenceCode = "externalReferenceCode";

		Map<Locale, String> nameMap = Collections.singletonMap(
			LocaleUtil.US, "Updated Able");

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.updateListTypeEntry(
				externalReferenceCode, _listTypeEntry.getListTypeEntryId(),
				nameMap);

		Assert.assertEquals(
			externalReferenceCode, listTypeEntry.getExternalReferenceCode());

		Assert.assertEquals(nameMap, listTypeEntry.getNameMap());

		externalReferenceCode = "externalReferenceCode2";

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.clear("liferay.mode");

		try {
			_systemListTypeEntry =
				_listTypeEntryLocalService.updateListTypeEntry(
					externalReferenceCode,
					_systemListTypeEntry.getListTypeEntryId(), nameMap);
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);
		}

		Assert.assertNotEquals(
			externalReferenceCode,
			_systemListTypeEntry.getExternalReferenceCode());

		Assert.assertEquals(nameMap, _systemListTypeEntry.getNameMap());

		listTypeEntry = _addListTypeEntry(
			_systemListTypeDefinition.getListTypeDefinitionId());

		externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		_assertListTypeEntry(
			externalReferenceCode, "baker", name,
			_listTypeEntryLocalService.updateListTypeEntry(
				externalReferenceCode, listTypeEntry.getListTypeEntryId(),
				Collections.singletonMap(LocaleUtil.US, name)));
	}

	@Test
	@TestInfo("LPD-55656")
	public void testUpdateListTypeEntryWithLazyReferenceEnabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			_testUpdateIncompleteListTypeEntry(
				_listTypeDefinition.getListTypeDefinitionId());
			_testUpdateIncompleteListTypeEntry(
				_systemListTypeDefinition.getListTypeDefinitionId());
		}
	}

	private ListTypeEntry _addListTypeEntry(long listTypeDefinitionId)
		throws Exception {

		return _listTypeEntryLocalService.addListTypeEntry(
			"externalReferenceCode", TestPropsValues.getUserId(),
			listTypeDefinitionId, "baker",
			Collections.singletonMap(LocaleUtil.US, "Baker"), false);
	}

	private void _assertListTypeEntry(
			String expectedExternalReferenceCode, String expectedKey,
			String expectedName, ListTypeEntry listTypeEntry)
		throws Exception {

		Assert.assertEquals(
			expectedExternalReferenceCode,
			listTypeEntry.getExternalReferenceCode());
		Assert.assertEquals(expectedKey, listTypeEntry.getKey());
		Assert.assertEquals(
			Collections.singletonMap(LocaleUtil.US, expectedName),
			listTypeEntry.getNameMap());

		_listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
	}

	private void _testAddIncompleteListTypeEntry(
			ListTypeDefinition listTypeDefinition)
		throws Exception {

		String key = RandomTestUtil.randomString();

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.getOrAddIncompleteListTypeEntry(
				TestPropsValues.getUserId(),
				listTypeDefinition.getListTypeDefinitionId(), key);

		Assert.assertEquals(key, listTypeEntry.getKey());
		Assert.assertEquals(key, listTypeEntry.getName(LocaleUtil.US));

		Assert.assertEquals(
			WorkflowConstants.STATUS_INCOMPLETE, listTypeEntry.getStatus());
	}

	private void _testAddListTypeEntry(
			long listTypeDefinitionId, String key, boolean system)
		throws Exception {

		ListTypeEntry listTypeEntry = null;

		try {
			listTypeEntry = _listTypeEntryLocalService.addListTypeEntry(
				null, TestPropsValues.getUserId(), listTypeDefinitionId, key,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				system);
		}
		finally {
			if (listTypeEntry != null) {
				_listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
			}
		}
	}

	private void _testUpdateIncompleteListTypeEntry(long listTypeDefinitionId)
		throws Exception {

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.getOrAddIncompleteListTypeEntry(
				TestPropsValues.getUserId(), listTypeDefinitionId,
				RandomTestUtil.randomString());

		Assert.assertEquals(
			WorkflowConstants.STATUS_INCOMPLETE, listTypeEntry.getStatus());

		listTypeEntry = _listTypeEntryLocalService.updateListTypeEntry(
			listTypeEntry.getExternalReferenceCode(),
			listTypeEntry.getListTypeEntryId(),
			RandomTestUtil.randomLocaleStringMap());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, listTypeEntry.getStatus());
	}

	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	private ListTypeEntry _listTypeEntry;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	private ListTypeDefinition _systemListTypeDefinition;
	private ListTypeEntry _systemListTypeEntry;

}
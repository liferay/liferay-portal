/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.opener.exception.NoSuchFileEntryReferenceException;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.service.persistence.DLOpenerFileEntryReferencePersistence;
import com.liferay.document.library.opener.service.persistence.DLOpenerFileEntryReferenceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
 * @generated
 */
@RunWith(Arquillian.class)
public class DLOpenerFileEntryReferencePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.document.library.opener.service"));

	@Before
	public void setUp() {
		_persistence = DLOpenerFileEntryReferenceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLOpenerFileEntryReference> iterator =
			_dlOpenerFileEntryReferences.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			_persistence.create(pk);

		Assert.assertNotNull(dlOpenerFileEntryReference);

		Assert.assertEquals(dlOpenerFileEntryReference.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLOpenerFileEntryReference newDLOpenerFileEntryReference =
			addDLOpenerFileEntryReference();

		_persistence.remove(newDLOpenerFileEntryReference);

		DLOpenerFileEntryReference existingDLOpenerFileEntryReference =
			_persistence.fetchByPrimaryKey(
				newDLOpenerFileEntryReference.getPrimaryKey());

		Assert.assertNull(existingDLOpenerFileEntryReference);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLOpenerFileEntryReference();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLOpenerFileEntryReference newDLOpenerFileEntryReference =
			_persistence.create(pk);

		newDLOpenerFileEntryReference.setGroupId(RandomTestUtil.nextLong());

		newDLOpenerFileEntryReference.setCompanyId(RandomTestUtil.nextLong());

		newDLOpenerFileEntryReference.setUserId(RandomTestUtil.nextLong());

		newDLOpenerFileEntryReference.setUserName(
			RandomTestUtil.randomString());

		newDLOpenerFileEntryReference.setCreateDate(RandomTestUtil.nextDate());

		newDLOpenerFileEntryReference.setModifiedDate(
			RandomTestUtil.nextDate());

		newDLOpenerFileEntryReference.setReferenceKey(
			RandomTestUtil.randomString());

		newDLOpenerFileEntryReference.setReferenceType(
			RandomTestUtil.randomString());

		newDLOpenerFileEntryReference.setFileEntryId(RandomTestUtil.nextLong());

		newDLOpenerFileEntryReference.setType(RandomTestUtil.nextInt());

		_dlOpenerFileEntryReferences.add(
			_persistence.update(newDLOpenerFileEntryReference));

		DLOpenerFileEntryReference existingDLOpenerFileEntryReference =
			_persistence.findByPrimaryKey(
				newDLOpenerFileEntryReference.getPrimaryKey());

		Assert.assertEquals(
			existingDLOpenerFileEntryReference.
				getDlOpenerFileEntryReferenceId(),
			newDLOpenerFileEntryReference.getDlOpenerFileEntryReferenceId());
		Assert.assertEquals(
			existingDLOpenerFileEntryReference.getGroupId(),
			newDLOpenerFileEntryReference.getGroupId());
		Assert.assertEquals(
			existingDLOpenerFileEntryReference.getCompanyId(),
			newDLOpenerFileEntryReference.getCompanyId());
		Assert.assertEquals(
			existingDLOpenerFileEntryReference.getUserId(),
			newDLOpenerFileEntryReference.getUserId());
		Assert.assertEquals(
			existingDLOpenerFileEntryReference.getUserName(),
			newDLOpenerFileEntryReference.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDLOpenerFileEntryReference.getCreateDate()),
			Time.getShortTimestamp(
				newDLOpenerFileEntryReference.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDLOpenerFileEntryReference.getModifiedDate()),
			Time.getShortTimestamp(
				newDLOpenerFileEntryReference.getModifiedDate()));
		Assert.assertEquals(
			existingDLOpenerFileEntryReference.getReferenceKey(),
			newDLOpenerFileEntryReference.getReferenceKey());
		Assert.assertEquals(
			existingDLOpenerFileEntryReference.getReferenceType(),
			newDLOpenerFileEntryReference.getReferenceType());
		Assert.assertEquals(
			existingDLOpenerFileEntryReference.getFileEntryId(),
			newDLOpenerFileEntryReference.getFileEntryId());
		Assert.assertEquals(
			existingDLOpenerFileEntryReference.getType(),
			newDLOpenerFileEntryReference.getType());
	}

	@Test
	public void testCountByFileEntryId() throws Exception {
		_persistence.countByFileEntryId(RandomTestUtil.nextLong());

		_persistence.countByFileEntryId(0L);
	}

	@Test
	public void testCountByR_F() throws Exception {
		_persistence.countByR_F("", RandomTestUtil.nextLong());

		_persistence.countByR_F("null", 0L);

		_persistence.countByR_F((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLOpenerFileEntryReference newDLOpenerFileEntryReference =
			addDLOpenerFileEntryReference();

		DLOpenerFileEntryReference existingDLOpenerFileEntryReference =
			_persistence.findByPrimaryKey(
				newDLOpenerFileEntryReference.getPrimaryKey());

		Assert.assertEquals(
			existingDLOpenerFileEntryReference, newDLOpenerFileEntryReference);
	}

	@Test(expected = NoSuchFileEntryReferenceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DLOpenerFileEntryReference>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"DLOpenerFileEntryReference", "dlOpenerFileEntryReferenceId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "referenceKey",
			true, "referenceType", true, "fileEntryId", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLOpenerFileEntryReference newDLOpenerFileEntryReference =
			addDLOpenerFileEntryReference();

		DLOpenerFileEntryReference existingDLOpenerFileEntryReference =
			_persistence.fetchByPrimaryKey(
				newDLOpenerFileEntryReference.getPrimaryKey());

		Assert.assertEquals(
			existingDLOpenerFileEntryReference, newDLOpenerFileEntryReference);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLOpenerFileEntryReference missingDLOpenerFileEntryReference =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDLOpenerFileEntryReference);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLOpenerFileEntryReference newDLOpenerFileEntryReference1 =
			addDLOpenerFileEntryReference();
		DLOpenerFileEntryReference newDLOpenerFileEntryReference2 =
			addDLOpenerFileEntryReference();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLOpenerFileEntryReference1.getPrimaryKey());
		primaryKeys.add(newDLOpenerFileEntryReference2.getPrimaryKey());

		Map<Serializable, DLOpenerFileEntryReference>
			dlOpenerFileEntryReferences = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, dlOpenerFileEntryReferences.size());
		Assert.assertEquals(
			newDLOpenerFileEntryReference1,
			dlOpenerFileEntryReferences.get(
				newDLOpenerFileEntryReference1.getPrimaryKey()));
		Assert.assertEquals(
			newDLOpenerFileEntryReference2,
			dlOpenerFileEntryReferences.get(
				newDLOpenerFileEntryReference2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLOpenerFileEntryReference>
			dlOpenerFileEntryReferences = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(dlOpenerFileEntryReferences.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLOpenerFileEntryReference newDLOpenerFileEntryReference =
			addDLOpenerFileEntryReference();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLOpenerFileEntryReference.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLOpenerFileEntryReference>
			dlOpenerFileEntryReferences = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, dlOpenerFileEntryReferences.size());
		Assert.assertEquals(
			newDLOpenerFileEntryReference,
			dlOpenerFileEntryReferences.get(
				newDLOpenerFileEntryReference.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLOpenerFileEntryReference>
			dlOpenerFileEntryReferences = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(dlOpenerFileEntryReferences.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLOpenerFileEntryReference newDLOpenerFileEntryReference =
			addDLOpenerFileEntryReference();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLOpenerFileEntryReference.getPrimaryKey());

		Map<Serializable, DLOpenerFileEntryReference>
			dlOpenerFileEntryReferences = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, dlOpenerFileEntryReferences.size());
		Assert.assertEquals(
			newDLOpenerFileEntryReference,
			dlOpenerFileEntryReferences.get(
				newDLOpenerFileEntryReference.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLOpenerFileEntryReference newDLOpenerFileEntryReference =
			addDLOpenerFileEntryReference();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDLOpenerFileEntryReference.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DLOpenerFileEntryReference dlOpenerFileEntryReference) {

		Assert.assertEquals(
			Long.valueOf(dlOpenerFileEntryReference.getFileEntryId()),
			ReflectionTestUtil.<Long>invoke(
				dlOpenerFileEntryReference, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fileEntryId"));

		Assert.assertEquals(
			dlOpenerFileEntryReference.getReferenceType(),
			ReflectionTestUtil.invoke(
				dlOpenerFileEntryReference, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "referenceType"));
		Assert.assertEquals(
			Long.valueOf(dlOpenerFileEntryReference.getFileEntryId()),
			ReflectionTestUtil.<Long>invoke(
				dlOpenerFileEntryReference, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fileEntryId"));
	}

	protected DLOpenerFileEntryReference addDLOpenerFileEntryReference()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			_persistence.create(pk);

		dlOpenerFileEntryReference.setGroupId(RandomTestUtil.nextLong());

		dlOpenerFileEntryReference.setCompanyId(RandomTestUtil.nextLong());

		dlOpenerFileEntryReference.setUserId(RandomTestUtil.nextLong());

		dlOpenerFileEntryReference.setUserName(RandomTestUtil.randomString());

		dlOpenerFileEntryReference.setCreateDate(RandomTestUtil.nextDate());

		dlOpenerFileEntryReference.setModifiedDate(RandomTestUtil.nextDate());

		dlOpenerFileEntryReference.setReferenceKey(
			RandomTestUtil.randomString());

		dlOpenerFileEntryReference.setReferenceType(
			RandomTestUtil.randomString());

		dlOpenerFileEntryReference.setFileEntryId(RandomTestUtil.nextLong());

		dlOpenerFileEntryReference.setType(RandomTestUtil.nextInt());

		_dlOpenerFileEntryReferences.add(
			_persistence.update(dlOpenerFileEntryReference));

		return dlOpenerFileEntryReference;
	}

	private List<DLOpenerFileEntryReference> _dlOpenerFileEntryReferences =
		new ArrayList<DLOpenerFileEntryReference>();
	private DLOpenerFileEntryReferencePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
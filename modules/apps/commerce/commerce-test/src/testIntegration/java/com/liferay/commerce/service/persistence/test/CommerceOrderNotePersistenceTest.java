/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.DuplicateCommerceOrderNoteExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderNoteException;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.service.persistence.CommerceOrderNotePersistence;
import com.liferay.commerce.service.persistence.CommerceOrderNoteUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class CommerceOrderNotePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceOrderNoteUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceOrderNote> iterator = _commerceOrderNotes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderNote commerceOrderNote = _persistence.create(pk);

		Assert.assertNotNull(commerceOrderNote);

		Assert.assertEquals(commerceOrderNote.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceOrderNote newCommerceOrderNote = addCommerceOrderNote();

		_persistence.remove(newCommerceOrderNote);

		CommerceOrderNote existingCommerceOrderNote =
			_persistence.fetchByPrimaryKey(
				newCommerceOrderNote.getPrimaryKey());

		Assert.assertNull(existingCommerceOrderNote);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceOrderNote();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderNote newCommerceOrderNote = _persistence.create(pk);

		newCommerceOrderNote.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceOrderNote.setUuid(RandomTestUtil.randomString());

		newCommerceOrderNote.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceOrderNote.setGroupId(RandomTestUtil.nextLong());

		newCommerceOrderNote.setCompanyId(RandomTestUtil.nextLong());

		newCommerceOrderNote.setUserId(RandomTestUtil.nextLong());

		newCommerceOrderNote.setUserName(RandomTestUtil.randomString());

		newCommerceOrderNote.setCreateDate(RandomTestUtil.nextDate());

		newCommerceOrderNote.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceOrderNote.setCommerceOrderId(RandomTestUtil.nextLong());

		newCommerceOrderNote.setContent(RandomTestUtil.randomString());

		newCommerceOrderNote.setRestricted(RandomTestUtil.randomBoolean());

		_commerceOrderNotes.add(_persistence.update(newCommerceOrderNote));

		CommerceOrderNote existingCommerceOrderNote =
			_persistence.findByPrimaryKey(newCommerceOrderNote.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrderNote.getMvccVersion(),
			newCommerceOrderNote.getMvccVersion());
		Assert.assertEquals(
			existingCommerceOrderNote.getUuid(),
			newCommerceOrderNote.getUuid());
		Assert.assertEquals(
			existingCommerceOrderNote.getExternalReferenceCode(),
			newCommerceOrderNote.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceOrderNote.getCommerceOrderNoteId(),
			newCommerceOrderNote.getCommerceOrderNoteId());
		Assert.assertEquals(
			existingCommerceOrderNote.getGroupId(),
			newCommerceOrderNote.getGroupId());
		Assert.assertEquals(
			existingCommerceOrderNote.getCompanyId(),
			newCommerceOrderNote.getCompanyId());
		Assert.assertEquals(
			existingCommerceOrderNote.getUserId(),
			newCommerceOrderNote.getUserId());
		Assert.assertEquals(
			existingCommerceOrderNote.getUserName(),
			newCommerceOrderNote.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrderNote.getCreateDate()),
			Time.getShortTimestamp(newCommerceOrderNote.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrderNote.getModifiedDate()),
			Time.getShortTimestamp(newCommerceOrderNote.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceOrderNote.getCommerceOrderId(),
			newCommerceOrderNote.getCommerceOrderId());
		Assert.assertEquals(
			existingCommerceOrderNote.getContent(),
			newCommerceOrderNote.getContent());
		Assert.assertEquals(
			existingCommerceOrderNote.isRestricted(),
			newCommerceOrderNote.isRestricted());
	}

	@Test(
		expected = DuplicateCommerceOrderNoteExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceOrderNote commerceOrderNote = addCommerceOrderNote();

		CommerceOrderNote newCommerceOrderNote = addCommerceOrderNote();

		newCommerceOrderNote.setCompanyId(commerceOrderNote.getCompanyId());

		newCommerceOrderNote = _persistence.update(newCommerceOrderNote);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceOrderNote);

		newCommerceOrderNote.setExternalReferenceCode(
			commerceOrderNote.getExternalReferenceCode());

		_persistence.update(newCommerceOrderNote);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCommerceOrderId() throws Exception {
		_persistence.countByCommerceOrderId(RandomTestUtil.nextLong());

		_persistence.countByCommerceOrderId(0L);
	}

	@Test
	public void testCountByC_R() throws Exception {
		_persistence.countByC_R(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_R(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceOrderNote newCommerceOrderNote = addCommerceOrderNote();

		CommerceOrderNote existingCommerceOrderNote =
			_persistence.findByPrimaryKey(newCommerceOrderNote.getPrimaryKey());

		Assert.assertEquals(existingCommerceOrderNote, newCommerceOrderNote);
	}

	@Test(expected = NoSuchOrderNoteException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceOrderNote> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceOrderNote", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceOrderNoteId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "commerceOrderId",
			true, "content", true, "restricted", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceOrderNote newCommerceOrderNote = addCommerceOrderNote();

		CommerceOrderNote existingCommerceOrderNote =
			_persistence.fetchByPrimaryKey(
				newCommerceOrderNote.getPrimaryKey());

		Assert.assertEquals(existingCommerceOrderNote, newCommerceOrderNote);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderNote missingCommerceOrderNote =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceOrderNote);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceOrderNote newCommerceOrderNote1 = addCommerceOrderNote();
		CommerceOrderNote newCommerceOrderNote2 = addCommerceOrderNote();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderNote1.getPrimaryKey());
		primaryKeys.add(newCommerceOrderNote2.getPrimaryKey());

		Map<Serializable, CommerceOrderNote> commerceOrderNotes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceOrderNotes.size());
		Assert.assertEquals(
			newCommerceOrderNote1,
			commerceOrderNotes.get(newCommerceOrderNote1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceOrderNote2,
			commerceOrderNotes.get(newCommerceOrderNote2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceOrderNote> commerceOrderNotes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrderNotes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceOrderNote newCommerceOrderNote = addCommerceOrderNote();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderNote.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceOrderNote> commerceOrderNotes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrderNotes.size());
		Assert.assertEquals(
			newCommerceOrderNote,
			commerceOrderNotes.get(newCommerceOrderNote.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceOrderNote> commerceOrderNotes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrderNotes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceOrderNote newCommerceOrderNote = addCommerceOrderNote();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderNote.getPrimaryKey());

		Map<Serializable, CommerceOrderNote> commerceOrderNotes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrderNotes.size());
		Assert.assertEquals(
			newCommerceOrderNote,
			commerceOrderNotes.get(newCommerceOrderNote.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceOrderNote newCommerceOrderNote = addCommerceOrderNote();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceOrderNote.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceOrderNote commerceOrderNote) {
		Assert.assertEquals(
			commerceOrderNote.getUuid(),
			ReflectionTestUtil.invoke(
				commerceOrderNote, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(commerceOrderNote.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceOrderNote, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			commerceOrderNote.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceOrderNote, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceOrderNote.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceOrderNote, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceOrderNote addCommerceOrderNote() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderNote commerceOrderNote = _persistence.create(pk);

		commerceOrderNote.setMvccVersion(RandomTestUtil.nextLong());

		commerceOrderNote.setUuid(RandomTestUtil.randomString());

		commerceOrderNote.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceOrderNote.setGroupId(RandomTestUtil.nextLong());

		commerceOrderNote.setCompanyId(RandomTestUtil.nextLong());

		commerceOrderNote.setUserId(RandomTestUtil.nextLong());

		commerceOrderNote.setUserName(RandomTestUtil.randomString());

		commerceOrderNote.setCreateDate(RandomTestUtil.nextDate());

		commerceOrderNote.setModifiedDate(RandomTestUtil.nextDate());

		commerceOrderNote.setCommerceOrderId(RandomTestUtil.nextLong());

		commerceOrderNote.setContent(RandomTestUtil.randomString());

		commerceOrderNote.setRestricted(RandomTestUtil.randomBoolean());

		_commerceOrderNotes.add(_persistence.update(commerceOrderNote));

		return commerceOrderNote;
	}

	private List<CommerceOrderNote> _commerceOrderNotes =
		new ArrayList<CommerceOrderNote>();
	private CommerceOrderNotePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
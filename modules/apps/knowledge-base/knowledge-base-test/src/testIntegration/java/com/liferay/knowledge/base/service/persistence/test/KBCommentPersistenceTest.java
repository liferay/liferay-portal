/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.exception.NoSuchCommentException;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.service.persistence.KBCommentPersistence;
import com.liferay.knowledge.base.service.persistence.KBCommentUtil;
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
public class KBCommentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.knowledge.base.service"));

	@Before
	public void setUp() {
		_persistence = KBCommentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KBComment> iterator = _kbComments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBComment kbComment = _persistence.create(pk);

		Assert.assertNotNull(kbComment);

		Assert.assertEquals(kbComment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KBComment newKBComment = addKBComment();

		_persistence.remove(newKBComment);

		KBComment existingKBComment = _persistence.fetchByPrimaryKey(
			newKBComment.getPrimaryKey());

		Assert.assertNull(existingKBComment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKBComment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBComment newKBComment = _persistence.create(pk);

		newKBComment.setMvccVersion(RandomTestUtil.nextLong());

		newKBComment.setCtCollectionId(RandomTestUtil.nextLong());

		newKBComment.setUuid(RandomTestUtil.randomString());

		newKBComment.setGroupId(RandomTestUtil.nextLong());

		newKBComment.setCompanyId(RandomTestUtil.nextLong());

		newKBComment.setUserId(RandomTestUtil.nextLong());

		newKBComment.setUserName(RandomTestUtil.randomString());

		newKBComment.setCreateDate(RandomTestUtil.nextDate());

		newKBComment.setModifiedDate(RandomTestUtil.nextDate());

		newKBComment.setClassNameId(RandomTestUtil.nextLong());

		newKBComment.setClassPK(RandomTestUtil.nextLong());

		newKBComment.setContent(RandomTestUtil.randomString());

		newKBComment.setUserRating(RandomTestUtil.nextInt());

		newKBComment.setLastPublishDate(RandomTestUtil.nextDate());

		newKBComment.setStatus(RandomTestUtil.nextInt());

		_kbComments.add(_persistence.update(newKBComment));

		KBComment existingKBComment = _persistence.findByPrimaryKey(
			newKBComment.getPrimaryKey());

		Assert.assertEquals(
			existingKBComment.getMvccVersion(), newKBComment.getMvccVersion());
		Assert.assertEquals(
			existingKBComment.getCtCollectionId(),
			newKBComment.getCtCollectionId());
		Assert.assertEquals(
			existingKBComment.getUuid(), newKBComment.getUuid());
		Assert.assertEquals(
			existingKBComment.getKbCommentId(), newKBComment.getKbCommentId());
		Assert.assertEquals(
			existingKBComment.getGroupId(), newKBComment.getGroupId());
		Assert.assertEquals(
			existingKBComment.getCompanyId(), newKBComment.getCompanyId());
		Assert.assertEquals(
			existingKBComment.getUserId(), newKBComment.getUserId());
		Assert.assertEquals(
			existingKBComment.getUserName(), newKBComment.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBComment.getCreateDate()),
			Time.getShortTimestamp(newKBComment.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBComment.getModifiedDate()),
			Time.getShortTimestamp(newKBComment.getModifiedDate()));
		Assert.assertEquals(
			existingKBComment.getClassNameId(), newKBComment.getClassNameId());
		Assert.assertEquals(
			existingKBComment.getClassPK(), newKBComment.getClassPK());
		Assert.assertEquals(
			existingKBComment.getContent(), newKBComment.getContent());
		Assert.assertEquals(
			existingKBComment.getUserRating(), newKBComment.getUserRating());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBComment.getLastPublishDate()),
			Time.getShortTimestamp(newKBComment.getLastPublishDate()));
		Assert.assertEquals(
			existingKBComment.getStatus(), newKBComment.getStatus());
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByU_C_C() throws Exception {
		_persistence.countByU_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByU_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_S() throws Exception {
		_persistence.countByC_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByC_C_S(0L, 0L, 0);
	}

	@Test
	public void testCountByC_C_SArrayable() throws Exception {
		_persistence.countByC_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KBComment newKBComment = addKBComment();

		KBComment existingKBComment = _persistence.findByPrimaryKey(
			newKBComment.getPrimaryKey());

		Assert.assertEquals(existingKBComment, newKBComment);
	}

	@Test(expected = NoSuchCommentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KBComment> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KBComment", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "kbCommentId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"content", true, "userRating", true, "lastPublishDate", true,
			"status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KBComment newKBComment = addKBComment();

		KBComment existingKBComment = _persistence.fetchByPrimaryKey(
			newKBComment.getPrimaryKey());

		Assert.assertEquals(existingKBComment, newKBComment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBComment missingKBComment = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKBComment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KBComment newKBComment1 = addKBComment();
		KBComment newKBComment2 = addKBComment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBComment1.getPrimaryKey());
		primaryKeys.add(newKBComment2.getPrimaryKey());

		Map<Serializable, KBComment> kbComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kbComments.size());
		Assert.assertEquals(
			newKBComment1, kbComments.get(newKBComment1.getPrimaryKey()));
		Assert.assertEquals(
			newKBComment2, kbComments.get(newKBComment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KBComment> kbComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kbComments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KBComment newKBComment = addKBComment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBComment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KBComment> kbComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kbComments.size());
		Assert.assertEquals(
			newKBComment, kbComments.get(newKBComment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KBComment> kbComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kbComments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KBComment newKBComment = addKBComment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBComment.getPrimaryKey());

		Map<Serializable, KBComment> kbComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kbComments.size());
		Assert.assertEquals(
			newKBComment, kbComments.get(newKBComment.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KBComment newKBComment = addKBComment();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newKBComment.getPrimaryKey()));
	}

	private void _assertOriginalValues(KBComment kbComment) {
		Assert.assertEquals(
			kbComment.getUuid(),
			ReflectionTestUtil.invoke(
				kbComment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(kbComment.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				kbComment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected KBComment addKBComment() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBComment kbComment = _persistence.create(pk);

		kbComment.setMvccVersion(RandomTestUtil.nextLong());

		kbComment.setCtCollectionId(RandomTestUtil.nextLong());

		kbComment.setUuid(RandomTestUtil.randomString());

		kbComment.setGroupId(RandomTestUtil.nextLong());

		kbComment.setCompanyId(RandomTestUtil.nextLong());

		kbComment.setUserId(RandomTestUtil.nextLong());

		kbComment.setUserName(RandomTestUtil.randomString());

		kbComment.setCreateDate(RandomTestUtil.nextDate());

		kbComment.setModifiedDate(RandomTestUtil.nextDate());

		kbComment.setClassNameId(RandomTestUtil.nextLong());

		kbComment.setClassPK(RandomTestUtil.nextLong());

		kbComment.setContent(RandomTestUtil.randomString());

		kbComment.setUserRating(RandomTestUtil.nextInt());

		kbComment.setLastPublishDate(RandomTestUtil.nextDate());

		kbComment.setStatus(RandomTestUtil.nextInt());

		_kbComments.add(_persistence.update(kbComment));

		return kbComment;
	}

	private List<KBComment> _kbComments = new ArrayList<KBComment>();
	private KBCommentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
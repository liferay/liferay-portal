/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchCommentException;
import com.liferay.change.tracking.model.CTComment;
import com.liferay.change.tracking.service.persistence.CTCommentPersistence;
import com.liferay.change.tracking.service.persistence.CTCommentUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CTCommentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTCommentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTComment> iterator = _ctComments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTComment ctComment = _persistence.create(pk);

		Assert.assertNotNull(ctComment);

		Assert.assertEquals(ctComment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTComment newCTComment = addCTComment();

		_persistence.remove(newCTComment);

		CTComment existingCTComment = _persistence.fetchByPrimaryKey(
			newCTComment.getPrimaryKey());

		Assert.assertNull(existingCTComment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTComment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTComment newCTComment = _persistence.create(pk);

		newCTComment.setMvccVersion(RandomTestUtil.nextLong());

		newCTComment.setCompanyId(RandomTestUtil.nextLong());

		newCTComment.setUserId(RandomTestUtil.nextLong());

		newCTComment.setCreateDate(RandomTestUtil.nextDate());

		newCTComment.setModifiedDate(RandomTestUtil.nextDate());

		newCTComment.setCtCollectionId(RandomTestUtil.nextLong());

		newCTComment.setCtEntryId(RandomTestUtil.nextLong());

		newCTComment.setValue(RandomTestUtil.randomString());

		_ctComments.add(_persistence.update(newCTComment));

		CTComment existingCTComment = _persistence.findByPrimaryKey(
			newCTComment.getPrimaryKey());

		Assert.assertEquals(
			existingCTComment.getMvccVersion(), newCTComment.getMvccVersion());
		Assert.assertEquals(
			existingCTComment.getCtCommentId(), newCTComment.getCtCommentId());
		Assert.assertEquals(
			existingCTComment.getCompanyId(), newCTComment.getCompanyId());
		Assert.assertEquals(
			existingCTComment.getUserId(), newCTComment.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTComment.getCreateDate()),
			Time.getShortTimestamp(newCTComment.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTComment.getModifiedDate()),
			Time.getShortTimestamp(newCTComment.getModifiedDate()));
		Assert.assertEquals(
			existingCTComment.getCtCollectionId(),
			newCTComment.getCtCollectionId());
		Assert.assertEquals(
			existingCTComment.getCtEntryId(), newCTComment.getCtEntryId());
		Assert.assertEquals(
			existingCTComment.getValue(), newCTComment.getValue());
	}

	@Test
	public void testCountByCtCollectionId() throws Exception {
		_persistence.countByCtCollectionId(RandomTestUtil.nextLong());

		_persistence.countByCtCollectionId(0L);
	}

	@Test
	public void testCountByCtEntryId() throws Exception {
		_persistence.countByCtEntryId(RandomTestUtil.nextLong());

		_persistence.countByCtEntryId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTComment newCTComment = addCTComment();

		CTComment existingCTComment = _persistence.findByPrimaryKey(
			newCTComment.getPrimaryKey());

		Assert.assertEquals(existingCTComment, newCTComment);
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

	protected OrderByComparator<CTComment> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTComment", "mvccVersion", true, "ctCommentId", true, "companyId",
			true, "userId", true, "createDate", true, "modifiedDate", true,
			"ctCollectionId", true, "ctEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTComment newCTComment = addCTComment();

		CTComment existingCTComment = _persistence.fetchByPrimaryKey(
			newCTComment.getPrimaryKey());

		Assert.assertEquals(existingCTComment, newCTComment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTComment missingCTComment = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTComment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTComment newCTComment1 = addCTComment();
		CTComment newCTComment2 = addCTComment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTComment1.getPrimaryKey());
		primaryKeys.add(newCTComment2.getPrimaryKey());

		Map<Serializable, CTComment> ctComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctComments.size());
		Assert.assertEquals(
			newCTComment1, ctComments.get(newCTComment1.getPrimaryKey()));
		Assert.assertEquals(
			newCTComment2, ctComments.get(newCTComment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTComment> ctComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctComments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTComment newCTComment = addCTComment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTComment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTComment> ctComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctComments.size());
		Assert.assertEquals(
			newCTComment, ctComments.get(newCTComment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTComment> ctComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctComments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTComment newCTComment = addCTComment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTComment.getPrimaryKey());

		Map<Serializable, CTComment> ctComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctComments.size());
		Assert.assertEquals(
			newCTComment, ctComments.get(newCTComment.getPrimaryKey()));
	}

	protected CTComment addCTComment() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTComment ctComment = _persistence.create(pk);

		ctComment.setMvccVersion(RandomTestUtil.nextLong());

		ctComment.setCompanyId(RandomTestUtil.nextLong());

		ctComment.setUserId(RandomTestUtil.nextLong());

		ctComment.setCreateDate(RandomTestUtil.nextDate());

		ctComment.setModifiedDate(RandomTestUtil.nextDate());

		ctComment.setCtCollectionId(RandomTestUtil.nextLong());

		ctComment.setCtEntryId(RandomTestUtil.nextLong());

		ctComment.setValue(RandomTestUtil.randomString());

		_ctComments.add(_persistence.update(ctComment));

		return ctComment;
	}

	private List<CTComment> _ctComments = new ArrayList<CTComment>();
	private CTCommentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
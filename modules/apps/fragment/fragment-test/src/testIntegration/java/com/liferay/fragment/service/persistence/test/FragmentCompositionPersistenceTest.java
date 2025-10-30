/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.exception.DuplicateFragmentCompositionExternalReferenceCodeException;
import com.liferay.fragment.exception.NoSuchCompositionException;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.service.persistence.FragmentCompositionPersistence;
import com.liferay.fragment.service.persistence.FragmentCompositionUtil;
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
public class FragmentCompositionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.fragment.service"));

	@Before
	public void setUp() {
		_persistence = FragmentCompositionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FragmentComposition> iterator =
			_fragmentCompositions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentComposition fragmentComposition = _persistence.create(pk);

		Assert.assertNotNull(fragmentComposition);

		Assert.assertEquals(fragmentComposition.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FragmentComposition newFragmentComposition = addFragmentComposition();

		_persistence.remove(newFragmentComposition);

		FragmentComposition existingFragmentComposition =
			_persistence.fetchByPrimaryKey(
				newFragmentComposition.getPrimaryKey());

		Assert.assertNull(existingFragmentComposition);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFragmentComposition();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentComposition newFragmentComposition = _persistence.create(pk);

		newFragmentComposition.setMvccVersion(RandomTestUtil.nextLong());

		newFragmentComposition.setCtCollectionId(RandomTestUtil.nextLong());

		newFragmentComposition.setUuid(RandomTestUtil.randomString());

		newFragmentComposition.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newFragmentComposition.setGroupId(RandomTestUtil.nextLong());

		newFragmentComposition.setCompanyId(RandomTestUtil.nextLong());

		newFragmentComposition.setUserId(RandomTestUtil.nextLong());

		newFragmentComposition.setUserName(RandomTestUtil.randomString());

		newFragmentComposition.setCreateDate(RandomTestUtil.nextDate());

		newFragmentComposition.setModifiedDate(RandomTestUtil.nextDate());

		newFragmentComposition.setFragmentCollectionId(
			RandomTestUtil.nextLong());

		newFragmentComposition.setFragmentCompositionKey(
			RandomTestUtil.randomString());

		newFragmentComposition.setName(RandomTestUtil.randomString());

		newFragmentComposition.setDescription(RandomTestUtil.randomString());

		newFragmentComposition.setData(RandomTestUtil.randomString());

		newFragmentComposition.setPreviewFileEntryId(RandomTestUtil.nextLong());

		newFragmentComposition.setMarketplace(RandomTestUtil.randomBoolean());

		newFragmentComposition.setLastPublishDate(RandomTestUtil.nextDate());

		newFragmentComposition.setStatus(RandomTestUtil.nextInt());

		newFragmentComposition.setStatusByUserId(RandomTestUtil.nextLong());

		newFragmentComposition.setStatusByUserName(
			RandomTestUtil.randomString());

		newFragmentComposition.setStatusDate(RandomTestUtil.nextDate());

		_fragmentCompositions.add(_persistence.update(newFragmentComposition));

		FragmentComposition existingFragmentComposition =
			_persistence.findByPrimaryKey(
				newFragmentComposition.getPrimaryKey());

		Assert.assertEquals(
			existingFragmentComposition.getMvccVersion(),
			newFragmentComposition.getMvccVersion());
		Assert.assertEquals(
			existingFragmentComposition.getCtCollectionId(),
			newFragmentComposition.getCtCollectionId());
		Assert.assertEquals(
			existingFragmentComposition.getUuid(),
			newFragmentComposition.getUuid());
		Assert.assertEquals(
			existingFragmentComposition.getExternalReferenceCode(),
			newFragmentComposition.getExternalReferenceCode());
		Assert.assertEquals(
			existingFragmentComposition.getFragmentCompositionId(),
			newFragmentComposition.getFragmentCompositionId());
		Assert.assertEquals(
			existingFragmentComposition.getGroupId(),
			newFragmentComposition.getGroupId());
		Assert.assertEquals(
			existingFragmentComposition.getCompanyId(),
			newFragmentComposition.getCompanyId());
		Assert.assertEquals(
			existingFragmentComposition.getUserId(),
			newFragmentComposition.getUserId());
		Assert.assertEquals(
			existingFragmentComposition.getUserName(),
			newFragmentComposition.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingFragmentComposition.getCreateDate()),
			Time.getShortTimestamp(newFragmentComposition.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingFragmentComposition.getModifiedDate()),
			Time.getShortTimestamp(newFragmentComposition.getModifiedDate()));
		Assert.assertEquals(
			existingFragmentComposition.getFragmentCollectionId(),
			newFragmentComposition.getFragmentCollectionId());
		Assert.assertEquals(
			existingFragmentComposition.getFragmentCompositionKey(),
			newFragmentComposition.getFragmentCompositionKey());
		Assert.assertEquals(
			existingFragmentComposition.getName(),
			newFragmentComposition.getName());
		Assert.assertEquals(
			existingFragmentComposition.getDescription(),
			newFragmentComposition.getDescription());
		Assert.assertEquals(
			existingFragmentComposition.getData(),
			newFragmentComposition.getData());
		Assert.assertEquals(
			existingFragmentComposition.getPreviewFileEntryId(),
			newFragmentComposition.getPreviewFileEntryId());
		Assert.assertEquals(
			existingFragmentComposition.isMarketplace(),
			newFragmentComposition.isMarketplace());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingFragmentComposition.getLastPublishDate()),
			Time.getShortTimestamp(
				newFragmentComposition.getLastPublishDate()));
		Assert.assertEquals(
			existingFragmentComposition.getStatus(),
			newFragmentComposition.getStatus());
		Assert.assertEquals(
			existingFragmentComposition.getStatusByUserId(),
			newFragmentComposition.getStatusByUserId());
		Assert.assertEquals(
			existingFragmentComposition.getStatusByUserName(),
			newFragmentComposition.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingFragmentComposition.getStatusDate()),
			Time.getShortTimestamp(newFragmentComposition.getStatusDate()));
	}

	@Test(
		expected = DuplicateFragmentCompositionExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		FragmentComposition fragmentComposition = addFragmentComposition();

		FragmentComposition newFragmentComposition = addFragmentComposition();

		newFragmentComposition.setGroupId(fragmentComposition.getGroupId());

		newFragmentComposition = _persistence.update(newFragmentComposition);

		Session session = _persistence.getCurrentSession();

		session.evict(newFragmentComposition);

		newFragmentComposition.setExternalReferenceCode(
			fragmentComposition.getExternalReferenceCode());

		_persistence.update(newFragmentComposition);
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
	public void testCountByFragmentCollectionId() throws Exception {
		_persistence.countByFragmentCollectionId(RandomTestUtil.nextLong());

		_persistence.countByFragmentCollectionId(0L);
	}

	@Test
	public void testCountByG_FCI() throws Exception {
		_persistence.countByG_FCI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_FCI(0L, 0L);
	}

	@Test
	public void testCountByG_FCK() throws Exception {
		_persistence.countByG_FCK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_FCK(0L, "null");

		_persistence.countByG_FCK(0L, (String)null);
	}

	@Test
	public void testCountByG_FCI_LikeN() throws Exception {
		_persistence.countByG_FCI_LikeN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_FCI_LikeN(0L, 0L, "null");

		_persistence.countByG_FCI_LikeN(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_FCI_S() throws Exception {
		_persistence.countByG_FCI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_FCI_LikeN_S() throws Exception {
		_persistence.countByG_FCI_LikeN_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_LikeN_S(0L, 0L, "null", 0);

		_persistence.countByG_FCI_LikeN_S(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FragmentComposition newFragmentComposition = addFragmentComposition();

		FragmentComposition existingFragmentComposition =
			_persistence.findByPrimaryKey(
				newFragmentComposition.getPrimaryKey());

		Assert.assertEquals(
			existingFragmentComposition, newFragmentComposition);
	}

	@Test(expected = NoSuchCompositionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FragmentComposition> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"FragmentComposition", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true,
			"fragmentCompositionId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "fragmentCollectionId", true,
			"fragmentCompositionKey", true, "name", true, "description", true,
			"previewFileEntryId", true, "marketplace", true, "lastPublishDate",
			true, "status", true, "statusByUserId", true, "statusByUserName",
			true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FragmentComposition newFragmentComposition = addFragmentComposition();

		FragmentComposition existingFragmentComposition =
			_persistence.fetchByPrimaryKey(
				newFragmentComposition.getPrimaryKey());

		Assert.assertEquals(
			existingFragmentComposition, newFragmentComposition);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentComposition missingFragmentComposition =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFragmentComposition);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FragmentComposition newFragmentComposition1 = addFragmentComposition();
		FragmentComposition newFragmentComposition2 = addFragmentComposition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentComposition1.getPrimaryKey());
		primaryKeys.add(newFragmentComposition2.getPrimaryKey());

		Map<Serializable, FragmentComposition> fragmentCompositions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, fragmentCompositions.size());
		Assert.assertEquals(
			newFragmentComposition1,
			fragmentCompositions.get(newFragmentComposition1.getPrimaryKey()));
		Assert.assertEquals(
			newFragmentComposition2,
			fragmentCompositions.get(newFragmentComposition2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FragmentComposition> fragmentCompositions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(fragmentCompositions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FragmentComposition newFragmentComposition = addFragmentComposition();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentComposition.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FragmentComposition> fragmentCompositions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, fragmentCompositions.size());
		Assert.assertEquals(
			newFragmentComposition,
			fragmentCompositions.get(newFragmentComposition.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FragmentComposition> fragmentCompositions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(fragmentCompositions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FragmentComposition newFragmentComposition = addFragmentComposition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentComposition.getPrimaryKey());

		Map<Serializable, FragmentComposition> fragmentCompositions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, fragmentCompositions.size());
		Assert.assertEquals(
			newFragmentComposition,
			fragmentCompositions.get(newFragmentComposition.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		FragmentComposition newFragmentComposition = addFragmentComposition();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newFragmentComposition.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		FragmentComposition fragmentComposition) {

		Assert.assertEquals(
			fragmentComposition.getUuid(),
			ReflectionTestUtil.invoke(
				fragmentComposition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(fragmentComposition.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentComposition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(fragmentComposition.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentComposition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			fragmentComposition.getFragmentCompositionKey(),
			ReflectionTestUtil.invoke(
				fragmentComposition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fragmentCompositionKey"));

		Assert.assertEquals(
			fragmentComposition.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				fragmentComposition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(fragmentComposition.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentComposition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected FragmentComposition addFragmentComposition() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentComposition fragmentComposition = _persistence.create(pk);

		fragmentComposition.setMvccVersion(RandomTestUtil.nextLong());

		fragmentComposition.setCtCollectionId(RandomTestUtil.nextLong());

		fragmentComposition.setUuid(RandomTestUtil.randomString());

		fragmentComposition.setExternalReferenceCode(
			RandomTestUtil.randomString());

		fragmentComposition.setGroupId(RandomTestUtil.nextLong());

		fragmentComposition.setCompanyId(RandomTestUtil.nextLong());

		fragmentComposition.setUserId(RandomTestUtil.nextLong());

		fragmentComposition.setUserName(RandomTestUtil.randomString());

		fragmentComposition.setCreateDate(RandomTestUtil.nextDate());

		fragmentComposition.setModifiedDate(RandomTestUtil.nextDate());

		fragmentComposition.setFragmentCollectionId(RandomTestUtil.nextLong());

		fragmentComposition.setFragmentCompositionKey(
			RandomTestUtil.randomString());

		fragmentComposition.setName(RandomTestUtil.randomString());

		fragmentComposition.setDescription(RandomTestUtil.randomString());

		fragmentComposition.setData(RandomTestUtil.randomString());

		fragmentComposition.setPreviewFileEntryId(RandomTestUtil.nextLong());

		fragmentComposition.setMarketplace(RandomTestUtil.randomBoolean());

		fragmentComposition.setLastPublishDate(RandomTestUtil.nextDate());

		fragmentComposition.setStatus(RandomTestUtil.nextInt());

		fragmentComposition.setStatusByUserId(RandomTestUtil.nextLong());

		fragmentComposition.setStatusByUserName(RandomTestUtil.randomString());

		fragmentComposition.setStatusDate(RandomTestUtil.nextDate());

		_fragmentCompositions.add(_persistence.update(fragmentComposition));

		return fragmentComposition;
	}

	private List<FragmentComposition> _fragmentCompositions =
		new ArrayList<FragmentComposition>();
	private FragmentCompositionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
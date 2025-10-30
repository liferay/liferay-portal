/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.exception.NoSuchArticleResourceException;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.persistence.JournalArticleResourcePersistence;
import com.liferay.journal.service.persistence.JournalArticleResourceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class JournalArticleResourcePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.journal.service"));

	@Before
	public void setUp() {
		_persistence = JournalArticleResourceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<JournalArticleResource> iterator =
			_journalArticleResources.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticleResource journalArticleResource = _persistence.create(pk);

		Assert.assertNotNull(journalArticleResource);

		Assert.assertEquals(journalArticleResource.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		JournalArticleResource newJournalArticleResource =
			addJournalArticleResource();

		_persistence.remove(newJournalArticleResource);

		JournalArticleResource existingJournalArticleResource =
			_persistence.fetchByPrimaryKey(
				newJournalArticleResource.getPrimaryKey());

		Assert.assertNull(existingJournalArticleResource);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addJournalArticleResource();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticleResource newJournalArticleResource = _persistence.create(
			pk);

		newJournalArticleResource.setMvccVersion(RandomTestUtil.nextLong());

		newJournalArticleResource.setCtCollectionId(RandomTestUtil.nextLong());

		newJournalArticleResource.setUuid(RandomTestUtil.randomString());

		newJournalArticleResource.setGroupId(RandomTestUtil.nextLong());

		newJournalArticleResource.setCompanyId(RandomTestUtil.nextLong());

		newJournalArticleResource.setArticleId(RandomTestUtil.randomString());

		_journalArticleResources.add(
			_persistence.update(newJournalArticleResource));

		JournalArticleResource existingJournalArticleResource =
			_persistence.findByPrimaryKey(
				newJournalArticleResource.getPrimaryKey());

		Assert.assertEquals(
			existingJournalArticleResource.getMvccVersion(),
			newJournalArticleResource.getMvccVersion());
		Assert.assertEquals(
			existingJournalArticleResource.getCtCollectionId(),
			newJournalArticleResource.getCtCollectionId());
		Assert.assertEquals(
			existingJournalArticleResource.getUuid(),
			newJournalArticleResource.getUuid());
		Assert.assertEquals(
			existingJournalArticleResource.getResourcePrimKey(),
			newJournalArticleResource.getResourcePrimKey());
		Assert.assertEquals(
			existingJournalArticleResource.getGroupId(),
			newJournalArticleResource.getGroupId());
		Assert.assertEquals(
			existingJournalArticleResource.getCompanyId(),
			newJournalArticleResource.getCompanyId());
		Assert.assertEquals(
			existingJournalArticleResource.getArticleId(),
			newJournalArticleResource.getArticleId());
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
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(RandomTestUtil.nextLong(), "");

		_persistence.countByG_A(0L, "null");

		_persistence.countByG_A(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		JournalArticleResource newJournalArticleResource =
			addJournalArticleResource();

		JournalArticleResource existingJournalArticleResource =
			_persistence.findByPrimaryKey(
				newJournalArticleResource.getPrimaryKey());

		Assert.assertEquals(
			existingJournalArticleResource, newJournalArticleResource);
	}

	@Test(expected = NoSuchArticleResourceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<JournalArticleResource> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"JournalArticleResource", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "resourcePrimKey", true, "groupId", true,
			"companyId", true, "articleId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		JournalArticleResource newJournalArticleResource =
			addJournalArticleResource();

		JournalArticleResource existingJournalArticleResource =
			_persistence.fetchByPrimaryKey(
				newJournalArticleResource.getPrimaryKey());

		Assert.assertEquals(
			existingJournalArticleResource, newJournalArticleResource);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticleResource missingJournalArticleResource =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingJournalArticleResource);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		JournalArticleResource newJournalArticleResource1 =
			addJournalArticleResource();
		JournalArticleResource newJournalArticleResource2 =
			addJournalArticleResource();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticleResource1.getPrimaryKey());
		primaryKeys.add(newJournalArticleResource2.getPrimaryKey());

		Map<Serializable, JournalArticleResource> journalArticleResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, journalArticleResources.size());
		Assert.assertEquals(
			newJournalArticleResource1,
			journalArticleResources.get(
				newJournalArticleResource1.getPrimaryKey()));
		Assert.assertEquals(
			newJournalArticleResource2,
			journalArticleResources.get(
				newJournalArticleResource2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, JournalArticleResource> journalArticleResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalArticleResources.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		JournalArticleResource newJournalArticleResource =
			addJournalArticleResource();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticleResource.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, JournalArticleResource> journalArticleResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalArticleResources.size());
		Assert.assertEquals(
			newJournalArticleResource,
			journalArticleResources.get(
				newJournalArticleResource.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, JournalArticleResource> journalArticleResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalArticleResources.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		JournalArticleResource newJournalArticleResource =
			addJournalArticleResource();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticleResource.getPrimaryKey());

		Map<Serializable, JournalArticleResource> journalArticleResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalArticleResources.size());
		Assert.assertEquals(
			newJournalArticleResource,
			journalArticleResources.get(
				newJournalArticleResource.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		JournalArticleResource newJournalArticleResource =
			addJournalArticleResource();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newJournalArticleResource.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		JournalArticleResource journalArticleResource) {

		Assert.assertEquals(
			journalArticleResource.getUuid(),
			ReflectionTestUtil.invoke(
				journalArticleResource, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(journalArticleResource.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticleResource, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(journalArticleResource.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticleResource, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			journalArticleResource.getArticleId(),
			ReflectionTestUtil.invoke(
				journalArticleResource, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "articleId"));
	}

	protected JournalArticleResource addJournalArticleResource()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		JournalArticleResource journalArticleResource = _persistence.create(pk);

		journalArticleResource.setMvccVersion(RandomTestUtil.nextLong());

		journalArticleResource.setCtCollectionId(RandomTestUtil.nextLong());

		journalArticleResource.setUuid(RandomTestUtil.randomString());

		journalArticleResource.setGroupId(RandomTestUtil.nextLong());

		journalArticleResource.setCompanyId(RandomTestUtil.nextLong());

		journalArticleResource.setArticleId(RandomTestUtil.randomString());

		_journalArticleResources.add(
			_persistence.update(journalArticleResource));

		return journalArticleResource;
	}

	private List<JournalArticleResource> _journalArticleResources =
		new ArrayList<JournalArticleResource>();
	private JournalArticleResourcePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
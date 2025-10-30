/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.wiki.exception.NoSuchPageResourceException;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.service.persistence.WikiPageResourcePersistence;
import com.liferay.wiki.service.persistence.WikiPageResourceUtil;

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
public class WikiPageResourcePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.wiki.service"));

	@Before
	public void setUp() {
		_persistence = WikiPageResourceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<WikiPageResource> iterator = _wikiPageResources.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiPageResource wikiPageResource = _persistence.create(pk);

		Assert.assertNotNull(wikiPageResource);

		Assert.assertEquals(wikiPageResource.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		WikiPageResource newWikiPageResource = addWikiPageResource();

		_persistence.remove(newWikiPageResource);

		WikiPageResource existingWikiPageResource =
			_persistence.fetchByPrimaryKey(newWikiPageResource.getPrimaryKey());

		Assert.assertNull(existingWikiPageResource);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWikiPageResource();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiPageResource newWikiPageResource = _persistence.create(pk);

		newWikiPageResource.setMvccVersion(RandomTestUtil.nextLong());

		newWikiPageResource.setCtCollectionId(RandomTestUtil.nextLong());

		newWikiPageResource.setUuid(RandomTestUtil.randomString());

		newWikiPageResource.setGroupId(RandomTestUtil.nextLong());

		newWikiPageResource.setCompanyId(RandomTestUtil.nextLong());

		newWikiPageResource.setNodeId(RandomTestUtil.nextLong());

		newWikiPageResource.setTitle(RandomTestUtil.randomString());

		_wikiPageResources.add(_persistence.update(newWikiPageResource));

		WikiPageResource existingWikiPageResource =
			_persistence.findByPrimaryKey(newWikiPageResource.getPrimaryKey());

		Assert.assertEquals(
			existingWikiPageResource.getMvccVersion(),
			newWikiPageResource.getMvccVersion());
		Assert.assertEquals(
			existingWikiPageResource.getCtCollectionId(),
			newWikiPageResource.getCtCollectionId());
		Assert.assertEquals(
			existingWikiPageResource.getUuid(), newWikiPageResource.getUuid());
		Assert.assertEquals(
			existingWikiPageResource.getResourcePrimKey(),
			newWikiPageResource.getResourcePrimKey());
		Assert.assertEquals(
			existingWikiPageResource.getGroupId(),
			newWikiPageResource.getGroupId());
		Assert.assertEquals(
			existingWikiPageResource.getCompanyId(),
			newWikiPageResource.getCompanyId());
		Assert.assertEquals(
			existingWikiPageResource.getNodeId(),
			newWikiPageResource.getNodeId());
		Assert.assertEquals(
			existingWikiPageResource.getTitle(),
			newWikiPageResource.getTitle());
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
	public void testCountByN_T() throws Exception {
		_persistence.countByN_T(RandomTestUtil.nextLong(), "");

		_persistence.countByN_T(0L, "null");

		_persistence.countByN_T(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		WikiPageResource newWikiPageResource = addWikiPageResource();

		WikiPageResource existingWikiPageResource =
			_persistence.findByPrimaryKey(newWikiPageResource.getPrimaryKey());

		Assert.assertEquals(existingWikiPageResource, newWikiPageResource);
	}

	@Test(expected = NoSuchPageResourceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<WikiPageResource> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"WikiPageResource", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "resourcePrimKey", true, "groupId", true, "companyId",
			true, "nodeId", true, "title", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		WikiPageResource newWikiPageResource = addWikiPageResource();

		WikiPageResource existingWikiPageResource =
			_persistence.fetchByPrimaryKey(newWikiPageResource.getPrimaryKey());

		Assert.assertEquals(existingWikiPageResource, newWikiPageResource);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiPageResource missingWikiPageResource =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWikiPageResource);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		WikiPageResource newWikiPageResource1 = addWikiPageResource();
		WikiPageResource newWikiPageResource2 = addWikiPageResource();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiPageResource1.getPrimaryKey());
		primaryKeys.add(newWikiPageResource2.getPrimaryKey());

		Map<Serializable, WikiPageResource> wikiPageResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, wikiPageResources.size());
		Assert.assertEquals(
			newWikiPageResource1,
			wikiPageResources.get(newWikiPageResource1.getPrimaryKey()));
		Assert.assertEquals(
			newWikiPageResource2,
			wikiPageResources.get(newWikiPageResource2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, WikiPageResource> wikiPageResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(wikiPageResources.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		WikiPageResource newWikiPageResource = addWikiPageResource();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiPageResource.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, WikiPageResource> wikiPageResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, wikiPageResources.size());
		Assert.assertEquals(
			newWikiPageResource,
			wikiPageResources.get(newWikiPageResource.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, WikiPageResource> wikiPageResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(wikiPageResources.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		WikiPageResource newWikiPageResource = addWikiPageResource();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiPageResource.getPrimaryKey());

		Map<Serializable, WikiPageResource> wikiPageResources =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, wikiPageResources.size());
		Assert.assertEquals(
			newWikiPageResource,
			wikiPageResources.get(newWikiPageResource.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		WikiPageResource newWikiPageResource = addWikiPageResource();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newWikiPageResource.getPrimaryKey()));
	}

	private void _assertOriginalValues(WikiPageResource wikiPageResource) {
		Assert.assertEquals(
			wikiPageResource.getUuid(),
			ReflectionTestUtil.invoke(
				wikiPageResource, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(wikiPageResource.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				wikiPageResource, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(wikiPageResource.getNodeId()),
			ReflectionTestUtil.<Long>invoke(
				wikiPageResource, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "nodeId"));
		Assert.assertEquals(
			wikiPageResource.getTitle(),
			ReflectionTestUtil.invoke(
				wikiPageResource, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "title"));
	}

	protected WikiPageResource addWikiPageResource() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiPageResource wikiPageResource = _persistence.create(pk);

		wikiPageResource.setMvccVersion(RandomTestUtil.nextLong());

		wikiPageResource.setCtCollectionId(RandomTestUtil.nextLong());

		wikiPageResource.setUuid(RandomTestUtil.randomString());

		wikiPageResource.setGroupId(RandomTestUtil.nextLong());

		wikiPageResource.setCompanyId(RandomTestUtil.nextLong());

		wikiPageResource.setNodeId(RandomTestUtil.nextLong());

		wikiPageResource.setTitle(RandomTestUtil.randomString());

		_wikiPageResources.add(_persistence.update(wikiPageResource));

		return wikiPageResource;
	}

	private List<WikiPageResource> _wikiPageResources =
		new ArrayList<WikiPageResource>();
	private WikiPageResourcePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
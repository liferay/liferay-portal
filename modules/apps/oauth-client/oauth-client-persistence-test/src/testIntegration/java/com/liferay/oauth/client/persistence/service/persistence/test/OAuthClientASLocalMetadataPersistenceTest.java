/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientASLocalMetadataException;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientASLocalMetadataPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientASLocalMetadataUtil;
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
public class OAuthClientASLocalMetadataPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.oauth.client.persistence.service"));

	@Before
	public void setUp() {
		_persistence = OAuthClientASLocalMetadataUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<OAuthClientASLocalMetadata> iterator =
			_oAuthClientASLocalMetadatas.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_persistence.create(pk);

		Assert.assertNotNull(oAuthClientASLocalMetadata);

		Assert.assertEquals(oAuthClientASLocalMetadata.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		_persistence.remove(newOAuthClientASLocalMetadata);

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			_persistence.fetchByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey());

		Assert.assertNull(existingOAuthClientASLocalMetadata);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOAuthClientASLocalMetadata();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			_persistence.create(pk);

		newOAuthClientASLocalMetadata.setMvccVersion(RandomTestUtil.nextLong());

		newOAuthClientASLocalMetadata.setCompanyId(RandomTestUtil.nextLong());

		newOAuthClientASLocalMetadata.setUserId(RandomTestUtil.nextLong());

		newOAuthClientASLocalMetadata.setUserName(
			RandomTestUtil.randomString());

		newOAuthClientASLocalMetadata.setCreateDate(RandomTestUtil.nextDate());

		newOAuthClientASLocalMetadata.setModifiedDate(
			RandomTestUtil.nextDate());

		newOAuthClientASLocalMetadata.setLocalWellKnownURI(
			RandomTestUtil.randomString());

		newOAuthClientASLocalMetadata.setMetadataJSON(
			RandomTestUtil.randomString());

		_oAuthClientASLocalMetadatas.add(
			_persistence.update(newOAuthClientASLocalMetadata));

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			_persistence.findByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getMvccVersion(),
			newOAuthClientASLocalMetadata.getMvccVersion());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.
				getOAuthClientASLocalMetadataId(),
			newOAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getCompanyId(),
			newOAuthClientASLocalMetadata.getCompanyId());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getUserId(),
			newOAuthClientASLocalMetadata.getUserId());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getUserName(),
			newOAuthClientASLocalMetadata.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingOAuthClientASLocalMetadata.getCreateDate()),
			Time.getShortTimestamp(
				newOAuthClientASLocalMetadata.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingOAuthClientASLocalMetadata.getModifiedDate()),
			Time.getShortTimestamp(
				newOAuthClientASLocalMetadata.getModifiedDate()));
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getLocalWellKnownURI(),
			newOAuthClientASLocalMetadata.getLocalWellKnownURI());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getMetadataJSON(),
			newOAuthClientASLocalMetadata.getMetadataJSON());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByLocalWellKnownURI() throws Exception {
		_persistence.countByLocalWellKnownURI("");

		_persistence.countByLocalWellKnownURI("null");

		_persistence.countByLocalWellKnownURI((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			_persistence.findByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientASLocalMetadata, newOAuthClientASLocalMetadata);
	}

	@Test(expected = NoSuchOAuthClientASLocalMetadataException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<OAuthClientASLocalMetadata>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"OAuthClientASLocalMetadata", "mvccVersion", true,
			"oAuthClientASLocalMetadataId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"localWellKnownURI", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			_persistence.fetchByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientASLocalMetadata, newOAuthClientASLocalMetadata);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientASLocalMetadata missingOAuthClientASLocalMetadata =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOAuthClientASLocalMetadata);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata1 =
			addOAuthClientASLocalMetadata();
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata2 =
			addOAuthClientASLocalMetadata();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientASLocalMetadata1.getPrimaryKey());
		primaryKeys.add(newOAuthClientASLocalMetadata2.getPrimaryKey());

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, oAuthClientASLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientASLocalMetadata1,
			oAuthClientASLocalMetadatas.get(
				newOAuthClientASLocalMetadata1.getPrimaryKey()));
		Assert.assertEquals(
			newOAuthClientASLocalMetadata2,
			oAuthClientASLocalMetadatas.get(
				newOAuthClientASLocalMetadata2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(oAuthClientASLocalMetadatas.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientASLocalMetadata.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, oAuthClientASLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientASLocalMetadata,
			oAuthClientASLocalMetadatas.get(
				newOAuthClientASLocalMetadata.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(oAuthClientASLocalMetadatas.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientASLocalMetadata.getPrimaryKey());

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, oAuthClientASLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientASLocalMetadata,
			oAuthClientASLocalMetadatas.get(
				newOAuthClientASLocalMetadata.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		Assert.assertEquals(
			oAuthClientASLocalMetadata.getLocalWellKnownURI(),
			ReflectionTestUtil.invoke(
				oAuthClientASLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "localWellKnownURI"));
	}

	protected OAuthClientASLocalMetadata addOAuthClientASLocalMetadata()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_persistence.create(pk);

		oAuthClientASLocalMetadata.setMvccVersion(RandomTestUtil.nextLong());

		oAuthClientASLocalMetadata.setCompanyId(RandomTestUtil.nextLong());

		oAuthClientASLocalMetadata.setUserId(RandomTestUtil.nextLong());

		oAuthClientASLocalMetadata.setUserName(RandomTestUtil.randomString());

		oAuthClientASLocalMetadata.setCreateDate(RandomTestUtil.nextDate());

		oAuthClientASLocalMetadata.setModifiedDate(RandomTestUtil.nextDate());

		oAuthClientASLocalMetadata.setLocalWellKnownURI(
			RandomTestUtil.randomString());

		oAuthClientASLocalMetadata.setMetadataJSON(
			RandomTestUtil.randomString());

		_oAuthClientASLocalMetadatas.add(
			_persistence.update(oAuthClientASLocalMetadata));

		return oAuthClientASLocalMetadata;
	}

	private List<OAuthClientASLocalMetadata> _oAuthClientASLocalMetadatas =
		new ArrayList<OAuthClientASLocalMetadata>();
	private OAuthClientASLocalMetadataPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
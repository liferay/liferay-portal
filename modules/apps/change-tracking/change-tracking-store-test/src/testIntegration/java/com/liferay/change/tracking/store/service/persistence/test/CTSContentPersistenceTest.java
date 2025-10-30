/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.store.exception.NoSuchContentException;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.change.tracking.store.service.persistence.CTSContentPersistence;
import com.liferay.change.tracking.store.service.persistence.CTSContentUtil;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.sql.Blob;

import java.util.ArrayList;
import java.util.Arrays;
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
public class CTSContentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.change.tracking.store.service"));

	@Before
	public void setUp() {
		_persistence = CTSContentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTSContent> iterator = _ctsContents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSContent ctsContent = _persistence.create(pk);

		Assert.assertNotNull(ctsContent);

		Assert.assertEquals(ctsContent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTSContent newCTSContent = addCTSContent();

		_persistence.remove(newCTSContent);

		CTSContent existingCTSContent = _persistence.fetchByPrimaryKey(
			newCTSContent.getPrimaryKey());

		Assert.assertNull(existingCTSContent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTSContent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSContent newCTSContent = _persistence.create(pk);

		newCTSContent.setMvccVersion(RandomTestUtil.nextLong());

		newCTSContent.setCtCollectionId(RandomTestUtil.nextLong());

		newCTSContent.setCompanyId(RandomTestUtil.nextLong());

		newCTSContent.setRepositoryId(RandomTestUtil.nextLong());

		newCTSContent.setPath(RandomTestUtil.randomString());

		newCTSContent.setVersion(RandomTestUtil.randomString());
		String newDataString = RandomTestUtil.randomString();

		byte[] newDataBytes = newDataString.getBytes("UTF-8");

		Blob newDataBlob = new OutputBlob(
			new ByteArrayInputStream(newDataBytes), newDataBytes.length);

		newCTSContent.setData(newDataBlob);

		newCTSContent.setSize(RandomTestUtil.nextLong());

		newCTSContent.setStoreType(RandomTestUtil.randomString());

		_ctsContents.add(_persistence.update(newCTSContent));

		Session session = _persistence.openSession();

		session.flush();

		session.clear();

		CTSContent existingCTSContent = _persistence.findByPrimaryKey(
			newCTSContent.getPrimaryKey());

		Assert.assertEquals(
			existingCTSContent.getMvccVersion(),
			newCTSContent.getMvccVersion());
		Assert.assertEquals(
			existingCTSContent.getCtCollectionId(),
			newCTSContent.getCtCollectionId());
		Assert.assertEquals(
			existingCTSContent.getCtsContentId(),
			newCTSContent.getCtsContentId());
		Assert.assertEquals(
			existingCTSContent.getCompanyId(), newCTSContent.getCompanyId());
		Assert.assertEquals(
			existingCTSContent.getRepositoryId(),
			newCTSContent.getRepositoryId());
		Assert.assertEquals(
			existingCTSContent.getPath(), newCTSContent.getPath());
		Assert.assertEquals(
			existingCTSContent.getVersion(), newCTSContent.getVersion());
		Blob existingData = existingCTSContent.getData();

		Assert.assertTrue(
			Arrays.equals(
				existingData.getBytes(1, (int)existingData.length()),
				newDataBytes));
		Assert.assertEquals(
			existingCTSContent.getSize(), newCTSContent.getSize());
		Assert.assertEquals(
			existingCTSContent.getStoreType(), newCTSContent.getStoreType());
	}

	@Test
	public void testCountByC_R_S() throws Exception {
		_persistence.countByC_R_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_R_S(0L, 0L, "null");

		_persistence.countByC_R_S(0L, 0L, (String)null);
	}

	@Test
	public void testCountByC_R_P_S() throws Exception {
		_persistence.countByC_R_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_R_P_S(0L, 0L, "null", "null");

		_persistence.countByC_R_P_S(0L, 0L, (String)null, (String)null);
	}

	@Test
	public void testCountByC_R_LikeP_S() throws Exception {
		_persistence.countByC_R_LikeP_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_R_LikeP_S(0L, 0L, "null", "null");

		_persistence.countByC_R_LikeP_S(0L, 0L, (String)null, (String)null);
	}

	@Test
	public void testCountByC_R_P_V_S() throws Exception {
		_persistence.countByC_R_P_V_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "", "", "");

		_persistence.countByC_R_P_V_S(0L, 0L, "null", "null", "null");

		_persistence.countByC_R_P_V_S(
			0L, 0L, (String)null, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTSContent newCTSContent = addCTSContent();

		CTSContent existingCTSContent = _persistence.findByPrimaryKey(
			newCTSContent.getPrimaryKey());

		Assert.assertEquals(existingCTSContent, newCTSContent);
	}

	@Test(expected = NoSuchContentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTSContent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTSContent", "mvccVersion", true, "ctCollectionId", true,
			"ctsContentId", true, "companyId", true, "repositoryId", true,
			"path", true, "version", true, "size", true, "storeType", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTSContent newCTSContent = addCTSContent();

		CTSContent existingCTSContent = _persistence.fetchByPrimaryKey(
			newCTSContent.getPrimaryKey());

		Assert.assertEquals(existingCTSContent, newCTSContent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSContent missingCTSContent = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTSContent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTSContent newCTSContent1 = addCTSContent();
		CTSContent newCTSContent2 = addCTSContent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSContent1.getPrimaryKey());
		primaryKeys.add(newCTSContent2.getPrimaryKey());

		Map<Serializable, CTSContent> ctsContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctsContents.size());
		Assert.assertEquals(
			newCTSContent1, ctsContents.get(newCTSContent1.getPrimaryKey()));
		Assert.assertEquals(
			newCTSContent2, ctsContents.get(newCTSContent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTSContent> ctsContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctsContents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTSContent newCTSContent = addCTSContent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSContent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTSContent> ctsContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctsContents.size());
		Assert.assertEquals(
			newCTSContent, ctsContents.get(newCTSContent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTSContent> ctsContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctsContents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTSContent newCTSContent = addCTSContent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSContent.getPrimaryKey());

		Map<Serializable, CTSContent> ctsContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctsContents.size());
		Assert.assertEquals(
			newCTSContent, ctsContents.get(newCTSContent.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CTSContent newCTSContent = addCTSContent();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCTSContent.getPrimaryKey()));
	}

	private void _assertOriginalValues(CTSContent ctsContent) {
		Assert.assertEquals(
			Long.valueOf(ctsContent.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				ctsContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(ctsContent.getRepositoryId()),
			ReflectionTestUtil.<Long>invoke(
				ctsContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "repositoryId"));
		Assert.assertEquals(
			ctsContent.getPath(),
			ReflectionTestUtil.invoke(
				ctsContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "path_"));
		Assert.assertEquals(
			ctsContent.getVersion(),
			ReflectionTestUtil.invoke(
				ctsContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
		Assert.assertEquals(
			ctsContent.getStoreType(),
			ReflectionTestUtil.invoke(
				ctsContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "storeType"));
	}

	protected CTSContent addCTSContent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSContent ctsContent = _persistence.create(pk);

		ctsContent.setMvccVersion(RandomTestUtil.nextLong());

		ctsContent.setCtCollectionId(RandomTestUtil.nextLong());

		ctsContent.setCompanyId(RandomTestUtil.nextLong());

		ctsContent.setRepositoryId(RandomTestUtil.nextLong());

		ctsContent.setPath(RandomTestUtil.randomString());

		ctsContent.setVersion(RandomTestUtil.randomString());
		String dataString = RandomTestUtil.randomString();

		byte[] dataBytes = dataString.getBytes("UTF-8");

		Blob dataBlob = new OutputBlob(
			new ByteArrayInputStream(dataBytes), dataBytes.length);

		ctsContent.setData(dataBlob);

		ctsContent.setSize(RandomTestUtil.nextLong());

		ctsContent.setStoreType(RandomTestUtil.randomString());

		_ctsContents.add(_persistence.update(ctsContent));

		return ctsContent;
	}

	private List<CTSContent> _ctsContents = new ArrayList<CTSContent>();
	private CTSContentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
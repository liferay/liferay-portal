/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.content.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.content.exception.NoSuchContentException;
import com.liferay.document.library.content.model.DLContent;
import com.liferay.document.library.content.service.persistence.DLContentPersistence;
import com.liferay.document.library.content.service.persistence.DLContentUtil;
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
public class DLContentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.document.library.content.service"));

	@Before
	public void setUp() {
		_persistence = DLContentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLContent> iterator = _dlContents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLContent dlContent = _persistence.create(pk);

		Assert.assertNotNull(dlContent);

		Assert.assertEquals(dlContent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLContent newDLContent = addDLContent();

		_persistence.remove(newDLContent);

		DLContent existingDLContent = _persistence.fetchByPrimaryKey(
			newDLContent.getPrimaryKey());

		Assert.assertNull(existingDLContent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLContent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLContent newDLContent = _persistence.create(pk);

		newDLContent.setMvccVersion(RandomTestUtil.nextLong());

		newDLContent.setCtCollectionId(RandomTestUtil.nextLong());

		newDLContent.setGroupId(RandomTestUtil.nextLong());

		newDLContent.setCompanyId(RandomTestUtil.nextLong());

		newDLContent.setRepositoryId(RandomTestUtil.nextLong());

		newDLContent.setPath(RandomTestUtil.randomString());

		newDLContent.setVersion(RandomTestUtil.randomString());
		String newDataString = RandomTestUtil.randomString();

		byte[] newDataBytes = newDataString.getBytes("UTF-8");

		Blob newDataBlob = new OutputBlob(
			new ByteArrayInputStream(newDataBytes), newDataBytes.length);

		newDLContent.setData(newDataBlob);

		newDLContent.setSize(RandomTestUtil.nextLong());

		_dlContents.add(_persistence.update(newDLContent));

		Session session = _persistence.openSession();

		session.flush();

		session.clear();

		DLContent existingDLContent = _persistence.findByPrimaryKey(
			newDLContent.getPrimaryKey());

		Assert.assertEquals(
			existingDLContent.getMvccVersion(), newDLContent.getMvccVersion());
		Assert.assertEquals(
			existingDLContent.getCtCollectionId(),
			newDLContent.getCtCollectionId());
		Assert.assertEquals(
			existingDLContent.getContentId(), newDLContent.getContentId());
		Assert.assertEquals(
			existingDLContent.getGroupId(), newDLContent.getGroupId());
		Assert.assertEquals(
			existingDLContent.getCompanyId(), newDLContent.getCompanyId());
		Assert.assertEquals(
			existingDLContent.getRepositoryId(),
			newDLContent.getRepositoryId());
		Assert.assertEquals(
			existingDLContent.getPath(), newDLContent.getPath());
		Assert.assertEquals(
			existingDLContent.getVersion(), newDLContent.getVersion());
		Blob existingData = existingDLContent.getData();

		Assert.assertTrue(
			Arrays.equals(
				existingData.getBytes(1, (int)existingData.length()),
				newDataBytes));
		Assert.assertEquals(
			existingDLContent.getSize(), newDLContent.getSize());
	}

	@Test
	public void testCountByC_R() throws Exception {
		_persistence.countByC_R(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_R(0L, 0L);
	}

	@Test
	public void testCountByC_R_P() throws Exception {
		_persistence.countByC_R_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_R_P(0L, 0L, "null");

		_persistence.countByC_R_P(0L, 0L, (String)null);
	}

	@Test
	public void testCountByC_R_LikeP() throws Exception {
		_persistence.countByC_R_LikeP(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_R_LikeP(0L, 0L, "null");

		_persistence.countByC_R_LikeP(0L, 0L, (String)null);
	}

	@Test
	public void testCountByC_R_P_V() throws Exception {
		_persistence.countByC_R_P_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_R_P_V(0L, 0L, "null", "null");

		_persistence.countByC_R_P_V(0L, 0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLContent newDLContent = addDLContent();

		DLContent existingDLContent = _persistence.findByPrimaryKey(
			newDLContent.getPrimaryKey());

		Assert.assertEquals(existingDLContent, newDLContent);
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

	protected OrderByComparator<DLContent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DLContent", "mvccVersion", true, "ctCollectionId", true,
			"contentId", true, "groupId", true, "companyId", true,
			"repositoryId", true, "path", true, "version", true, "size", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLContent newDLContent = addDLContent();

		DLContent existingDLContent = _persistence.fetchByPrimaryKey(
			newDLContent.getPrimaryKey());

		Assert.assertEquals(existingDLContent, newDLContent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLContent missingDLContent = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDLContent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLContent newDLContent1 = addDLContent();
		DLContent newDLContent2 = addDLContent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLContent1.getPrimaryKey());
		primaryKeys.add(newDLContent2.getPrimaryKey());

		Map<Serializable, DLContent> dlContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dlContents.size());
		Assert.assertEquals(
			newDLContent1, dlContents.get(newDLContent1.getPrimaryKey()));
		Assert.assertEquals(
			newDLContent2, dlContents.get(newDLContent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLContent> dlContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlContents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLContent newDLContent = addDLContent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLContent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLContent> dlContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlContents.size());
		Assert.assertEquals(
			newDLContent, dlContents.get(newDLContent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLContent> dlContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlContents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLContent newDLContent = addDLContent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLContent.getPrimaryKey());

		Map<Serializable, DLContent> dlContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlContents.size());
		Assert.assertEquals(
			newDLContent, dlContents.get(newDLContent.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLContent newDLContent = addDLContent();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDLContent.getPrimaryKey()));
	}

	private void _assertOriginalValues(DLContent dlContent) {
		Assert.assertEquals(
			Long.valueOf(dlContent.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				dlContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(dlContent.getRepositoryId()),
			ReflectionTestUtil.<Long>invoke(
				dlContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "repositoryId"));
		Assert.assertEquals(
			dlContent.getPath(),
			ReflectionTestUtil.invoke(
				dlContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "path_"));
		Assert.assertEquals(
			dlContent.getVersion(),
			ReflectionTestUtil.invoke(
				dlContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected DLContent addDLContent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLContent dlContent = _persistence.create(pk);

		dlContent.setMvccVersion(RandomTestUtil.nextLong());

		dlContent.setCtCollectionId(RandomTestUtil.nextLong());

		dlContent.setGroupId(RandomTestUtil.nextLong());

		dlContent.setCompanyId(RandomTestUtil.nextLong());

		dlContent.setRepositoryId(RandomTestUtil.nextLong());

		dlContent.setPath(RandomTestUtil.randomString());

		dlContent.setVersion(RandomTestUtil.randomString());
		String dataString = RandomTestUtil.randomString();

		byte[] dataBytes = dataString.getBytes("UTF-8");

		Blob dataBlob = new OutputBlob(
			new ByteArrayInputStream(dataBytes), dataBytes.length);

		dlContent.setData(dataBlob);

		dlContent.setSize(RandomTestUtil.nextLong());

		_dlContents.add(_persistence.update(dlContent));

		return dlContent;
	}

	private List<DLContent> _dlContents = new ArrayList<DLContent>();
	private DLContentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
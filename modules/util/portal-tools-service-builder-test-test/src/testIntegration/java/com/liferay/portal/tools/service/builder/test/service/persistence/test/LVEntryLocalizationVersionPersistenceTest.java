/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryLocalizationVersionException;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalizationVersion;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationVersionPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationVersionUtil;

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
public class LVEntryLocalizationVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = LVEntryLocalizationVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LVEntryLocalizationVersion> iterator =
			_lvEntryLocalizationVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryLocalizationVersion lvEntryLocalizationVersion =
			_persistence.create(pk);

		Assert.assertNotNull(lvEntryLocalizationVersion);

		Assert.assertEquals(lvEntryLocalizationVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LVEntryLocalizationVersion newLVEntryLocalizationVersion =
			addLVEntryLocalizationVersion();

		_persistence.remove(newLVEntryLocalizationVersion);

		LVEntryLocalizationVersion existingLVEntryLocalizationVersion =
			_persistence.fetchByPrimaryKey(
				newLVEntryLocalizationVersion.getPrimaryKey());

		Assert.assertNull(existingLVEntryLocalizationVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLVEntryLocalizationVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryLocalizationVersion newLVEntryLocalizationVersion =
			_persistence.create(pk);

		newLVEntryLocalizationVersion.setVersion(RandomTestUtil.nextInt());

		newLVEntryLocalizationVersion.setLvEntryLocalizationId(
			RandomTestUtil.nextLong());

		newLVEntryLocalizationVersion.setCompanyId(RandomTestUtil.nextLong());

		newLVEntryLocalizationVersion.setLvEntryId(RandomTestUtil.nextLong());

		newLVEntryLocalizationVersion.setLanguageId(
			RandomTestUtil.randomString());

		newLVEntryLocalizationVersion.setTitle(RandomTestUtil.randomString());

		newLVEntryLocalizationVersion.setContent(RandomTestUtil.randomString());

		_lvEntryLocalizationVersions.add(
			_persistence.update(newLVEntryLocalizationVersion));

		LVEntryLocalizationVersion existingLVEntryLocalizationVersion =
			_persistence.findByPrimaryKey(
				newLVEntryLocalizationVersion.getPrimaryKey());

		Assert.assertEquals(
			existingLVEntryLocalizationVersion.
				getLvEntryLocalizationVersionId(),
			newLVEntryLocalizationVersion.getLvEntryLocalizationVersionId());
		Assert.assertEquals(
			existingLVEntryLocalizationVersion.getVersion(),
			newLVEntryLocalizationVersion.getVersion());
		Assert.assertEquals(
			existingLVEntryLocalizationVersion.getLvEntryLocalizationId(),
			newLVEntryLocalizationVersion.getLvEntryLocalizationId());
		Assert.assertEquals(
			existingLVEntryLocalizationVersion.getCompanyId(),
			newLVEntryLocalizationVersion.getCompanyId());
		Assert.assertEquals(
			existingLVEntryLocalizationVersion.getLvEntryId(),
			newLVEntryLocalizationVersion.getLvEntryId());
		Assert.assertEquals(
			existingLVEntryLocalizationVersion.getLanguageId(),
			newLVEntryLocalizationVersion.getLanguageId());
		Assert.assertEquals(
			existingLVEntryLocalizationVersion.getTitle(),
			newLVEntryLocalizationVersion.getTitle());
		Assert.assertEquals(
			existingLVEntryLocalizationVersion.getContent(),
			newLVEntryLocalizationVersion.getContent());
	}

	@Test
	public void testCountByLvEntryLocalizationId() throws Exception {
		_persistence.countByLvEntryLocalizationId(RandomTestUtil.nextLong());

		_persistence.countByLvEntryLocalizationId(0L);
	}

	@Test
	public void testCountByLvEntryLocalizationId_Version() throws Exception {
		_persistence.countByLvEntryLocalizationId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByLvEntryLocalizationId_Version(0L, 0);
	}

	@Test
	public void testCountByLvEntryId() throws Exception {
		_persistence.countByLvEntryId(RandomTestUtil.nextLong());

		_persistence.countByLvEntryId(0L);
	}

	@Test
	public void testCountByLvEntryId_Version() throws Exception {
		_persistence.countByLvEntryId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByLvEntryId_Version(0L, 0);
	}

	@Test
	public void testCountByLvEntryId_LanguageId() throws Exception {
		_persistence.countByLvEntryId_LanguageId(RandomTestUtil.nextLong(), "");

		_persistence.countByLvEntryId_LanguageId(0L, "null");

		_persistence.countByLvEntryId_LanguageId(0L, (String)null);
	}

	@Test
	public void testCountByLvEntryId_LanguageId_Version() throws Exception {
		_persistence.countByLvEntryId_LanguageId_Version(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByLvEntryId_LanguageId_Version(0L, "null", 0);

		_persistence.countByLvEntryId_LanguageId_Version(0L, (String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LVEntryLocalizationVersion newLVEntryLocalizationVersion =
			addLVEntryLocalizationVersion();

		LVEntryLocalizationVersion existingLVEntryLocalizationVersion =
			_persistence.findByPrimaryKey(
				newLVEntryLocalizationVersion.getPrimaryKey());

		Assert.assertEquals(
			existingLVEntryLocalizationVersion, newLVEntryLocalizationVersion);
	}

	@Test(expected = NoSuchLVEntryLocalizationVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LVEntryLocalizationVersion>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"LVEntryLocalizationVersion", "lvEntryLocalizationVersionId", true,
			"version", true, "lvEntryLocalizationId", true, "companyId", true,
			"lvEntryId", true, "languageId", true, "title", true, "content",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LVEntryLocalizationVersion newLVEntryLocalizationVersion =
			addLVEntryLocalizationVersion();

		LVEntryLocalizationVersion existingLVEntryLocalizationVersion =
			_persistence.fetchByPrimaryKey(
				newLVEntryLocalizationVersion.getPrimaryKey());

		Assert.assertEquals(
			existingLVEntryLocalizationVersion, newLVEntryLocalizationVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryLocalizationVersion missingLVEntryLocalizationVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLVEntryLocalizationVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LVEntryLocalizationVersion newLVEntryLocalizationVersion1 =
			addLVEntryLocalizationVersion();
		LVEntryLocalizationVersion newLVEntryLocalizationVersion2 =
			addLVEntryLocalizationVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryLocalizationVersion1.getPrimaryKey());
		primaryKeys.add(newLVEntryLocalizationVersion2.getPrimaryKey());

		Map<Serializable, LVEntryLocalizationVersion>
			lvEntryLocalizationVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, lvEntryLocalizationVersions.size());
		Assert.assertEquals(
			newLVEntryLocalizationVersion1,
			lvEntryLocalizationVersions.get(
				newLVEntryLocalizationVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newLVEntryLocalizationVersion2,
			lvEntryLocalizationVersions.get(
				newLVEntryLocalizationVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LVEntryLocalizationVersion>
			lvEntryLocalizationVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(lvEntryLocalizationVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LVEntryLocalizationVersion newLVEntryLocalizationVersion =
			addLVEntryLocalizationVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryLocalizationVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LVEntryLocalizationVersion>
			lvEntryLocalizationVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, lvEntryLocalizationVersions.size());
		Assert.assertEquals(
			newLVEntryLocalizationVersion,
			lvEntryLocalizationVersions.get(
				newLVEntryLocalizationVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LVEntryLocalizationVersion>
			lvEntryLocalizationVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(lvEntryLocalizationVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LVEntryLocalizationVersion newLVEntryLocalizationVersion =
			addLVEntryLocalizationVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryLocalizationVersion.getPrimaryKey());

		Map<Serializable, LVEntryLocalizationVersion>
			lvEntryLocalizationVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, lvEntryLocalizationVersions.size());
		Assert.assertEquals(
			newLVEntryLocalizationVersion,
			lvEntryLocalizationVersions.get(
				newLVEntryLocalizationVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LVEntryLocalizationVersion newLVEntryLocalizationVersion =
			addLVEntryLocalizationVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLVEntryLocalizationVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		LVEntryLocalizationVersion lvEntryLocalizationVersion) {

		Assert.assertEquals(
			Long.valueOf(lvEntryLocalizationVersion.getLvEntryLocalizationId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntryLocalizationVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "lvEntryLocalizationId"));
		Assert.assertEquals(
			Integer.valueOf(lvEntryLocalizationVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				lvEntryLocalizationVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(lvEntryLocalizationVersion.getLvEntryId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntryLocalizationVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "lvEntryId"));
		Assert.assertEquals(
			lvEntryLocalizationVersion.getLanguageId(),
			ReflectionTestUtil.invoke(
				lvEntryLocalizationVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
		Assert.assertEquals(
			Integer.valueOf(lvEntryLocalizationVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				lvEntryLocalizationVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected LVEntryLocalizationVersion addLVEntryLocalizationVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LVEntryLocalizationVersion lvEntryLocalizationVersion =
			_persistence.create(pk);

		lvEntryLocalizationVersion.setVersion(RandomTestUtil.nextInt());

		lvEntryLocalizationVersion.setLvEntryLocalizationId(
			RandomTestUtil.nextLong());

		lvEntryLocalizationVersion.setCompanyId(RandomTestUtil.nextLong());

		lvEntryLocalizationVersion.setLvEntryId(RandomTestUtil.nextLong());

		lvEntryLocalizationVersion.setLanguageId(RandomTestUtil.randomString());

		lvEntryLocalizationVersion.setTitle(RandomTestUtil.randomString());

		lvEntryLocalizationVersion.setContent(RandomTestUtil.randomString());

		_lvEntryLocalizationVersions.add(
			_persistence.update(lvEntryLocalizationVersion));

		return lvEntryLocalizationVersion;
	}

	private List<LVEntryLocalizationVersion> _lvEntryLocalizationVersions =
		new ArrayList<LVEntryLocalizationVersion>();
	private LVEntryLocalizationVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
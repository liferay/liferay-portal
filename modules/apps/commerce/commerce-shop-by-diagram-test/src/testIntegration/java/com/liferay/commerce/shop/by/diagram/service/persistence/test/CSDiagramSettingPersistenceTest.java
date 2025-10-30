/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramSettingException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramSettingPersistence;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramSettingUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CSDiagramSettingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.shop.by.diagram.service"));

	@Before
	public void setUp() {
		_persistence = CSDiagramSettingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CSDiagramSetting> iterator = _csDiagramSettings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramSetting csDiagramSetting = _persistence.create(pk);

		Assert.assertNotNull(csDiagramSetting);

		Assert.assertEquals(csDiagramSetting.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CSDiagramSetting newCSDiagramSetting = addCSDiagramSetting();

		_persistence.remove(newCSDiagramSetting);

		CSDiagramSetting existingCSDiagramSetting =
			_persistence.fetchByPrimaryKey(newCSDiagramSetting.getPrimaryKey());

		Assert.assertNull(existingCSDiagramSetting);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCSDiagramSetting();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramSetting newCSDiagramSetting = _persistence.create(pk);

		newCSDiagramSetting.setMvccVersion(RandomTestUtil.nextLong());

		newCSDiagramSetting.setCtCollectionId(RandomTestUtil.nextLong());

		newCSDiagramSetting.setUuid(RandomTestUtil.randomString());

		newCSDiagramSetting.setCompanyId(RandomTestUtil.nextLong());

		newCSDiagramSetting.setUserId(RandomTestUtil.nextLong());

		newCSDiagramSetting.setUserName(RandomTestUtil.randomString());

		newCSDiagramSetting.setCreateDate(RandomTestUtil.nextDate());

		newCSDiagramSetting.setModifiedDate(RandomTestUtil.nextDate());

		newCSDiagramSetting.setCPAttachmentFileEntryId(
			RandomTestUtil.nextLong());

		newCSDiagramSetting.setCPDefinitionId(RandomTestUtil.nextLong());

		newCSDiagramSetting.setColor(RandomTestUtil.randomString());

		newCSDiagramSetting.setRadius(RandomTestUtil.nextDouble());

		newCSDiagramSetting.setType(RandomTestUtil.randomString());

		_csDiagramSettings.add(_persistence.update(newCSDiagramSetting));

		CSDiagramSetting existingCSDiagramSetting =
			_persistence.findByPrimaryKey(newCSDiagramSetting.getPrimaryKey());

		Assert.assertEquals(
			existingCSDiagramSetting.getMvccVersion(),
			newCSDiagramSetting.getMvccVersion());
		Assert.assertEquals(
			existingCSDiagramSetting.getCtCollectionId(),
			newCSDiagramSetting.getCtCollectionId());
		Assert.assertEquals(
			existingCSDiagramSetting.getUuid(), newCSDiagramSetting.getUuid());
		Assert.assertEquals(
			existingCSDiagramSetting.getCSDiagramSettingId(),
			newCSDiagramSetting.getCSDiagramSettingId());
		Assert.assertEquals(
			existingCSDiagramSetting.getCompanyId(),
			newCSDiagramSetting.getCompanyId());
		Assert.assertEquals(
			existingCSDiagramSetting.getUserId(),
			newCSDiagramSetting.getUserId());
		Assert.assertEquals(
			existingCSDiagramSetting.getUserName(),
			newCSDiagramSetting.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCSDiagramSetting.getCreateDate()),
			Time.getShortTimestamp(newCSDiagramSetting.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCSDiagramSetting.getModifiedDate()),
			Time.getShortTimestamp(newCSDiagramSetting.getModifiedDate()));
		Assert.assertEquals(
			existingCSDiagramSetting.getCPAttachmentFileEntryId(),
			newCSDiagramSetting.getCPAttachmentFileEntryId());
		Assert.assertEquals(
			existingCSDiagramSetting.getCPDefinitionId(),
			newCSDiagramSetting.getCPDefinitionId());
		Assert.assertEquals(
			existingCSDiagramSetting.getColor(),
			newCSDiagramSetting.getColor());
		AssertUtils.assertEquals(
			existingCSDiagramSetting.getRadius(),
			newCSDiagramSetting.getRadius());
		Assert.assertEquals(
			existingCSDiagramSetting.getType(), newCSDiagramSetting.getType());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CSDiagramSetting newCSDiagramSetting = addCSDiagramSetting();

		CSDiagramSetting existingCSDiagramSetting =
			_persistence.findByPrimaryKey(newCSDiagramSetting.getPrimaryKey());

		Assert.assertEquals(existingCSDiagramSetting, newCSDiagramSetting);
	}

	@Test(expected = NoSuchCSDiagramSettingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CSDiagramSetting> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CSDiagramSetting", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "CSDiagramSettingId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "CPAttachmentFileEntryId", true,
			"CPDefinitionId", true, "color", true, "radius", true, "type",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CSDiagramSetting newCSDiagramSetting = addCSDiagramSetting();

		CSDiagramSetting existingCSDiagramSetting =
			_persistence.fetchByPrimaryKey(newCSDiagramSetting.getPrimaryKey());

		Assert.assertEquals(existingCSDiagramSetting, newCSDiagramSetting);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramSetting missingCSDiagramSetting =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCSDiagramSetting);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CSDiagramSetting newCSDiagramSetting1 = addCSDiagramSetting();
		CSDiagramSetting newCSDiagramSetting2 = addCSDiagramSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramSetting1.getPrimaryKey());
		primaryKeys.add(newCSDiagramSetting2.getPrimaryKey());

		Map<Serializable, CSDiagramSetting> csDiagramSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, csDiagramSettings.size());
		Assert.assertEquals(
			newCSDiagramSetting1,
			csDiagramSettings.get(newCSDiagramSetting1.getPrimaryKey()));
		Assert.assertEquals(
			newCSDiagramSetting2,
			csDiagramSettings.get(newCSDiagramSetting2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CSDiagramSetting> csDiagramSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(csDiagramSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CSDiagramSetting newCSDiagramSetting = addCSDiagramSetting();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramSetting.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CSDiagramSetting> csDiagramSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, csDiagramSettings.size());
		Assert.assertEquals(
			newCSDiagramSetting,
			csDiagramSettings.get(newCSDiagramSetting.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CSDiagramSetting> csDiagramSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(csDiagramSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CSDiagramSetting newCSDiagramSetting = addCSDiagramSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramSetting.getPrimaryKey());

		Map<Serializable, CSDiagramSetting> csDiagramSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, csDiagramSettings.size());
		Assert.assertEquals(
			newCSDiagramSetting,
			csDiagramSettings.get(newCSDiagramSetting.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CSDiagramSetting newCSDiagramSetting = addCSDiagramSetting();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCSDiagramSetting.getPrimaryKey()));
	}

	private void _assertOriginalValues(CSDiagramSetting csDiagramSetting) {
		Assert.assertEquals(
			Long.valueOf(csDiagramSetting.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				csDiagramSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
	}

	protected CSDiagramSetting addCSDiagramSetting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramSetting csDiagramSetting = _persistence.create(pk);

		csDiagramSetting.setMvccVersion(RandomTestUtil.nextLong());

		csDiagramSetting.setCtCollectionId(RandomTestUtil.nextLong());

		csDiagramSetting.setUuid(RandomTestUtil.randomString());

		csDiagramSetting.setCompanyId(RandomTestUtil.nextLong());

		csDiagramSetting.setUserId(RandomTestUtil.nextLong());

		csDiagramSetting.setUserName(RandomTestUtil.randomString());

		csDiagramSetting.setCreateDate(RandomTestUtil.nextDate());

		csDiagramSetting.setModifiedDate(RandomTestUtil.nextDate());

		csDiagramSetting.setCPAttachmentFileEntryId(RandomTestUtil.nextLong());

		csDiagramSetting.setCPDefinitionId(RandomTestUtil.nextLong());

		csDiagramSetting.setColor(RandomTestUtil.randomString());

		csDiagramSetting.setRadius(RandomTestUtil.nextDouble());

		csDiagramSetting.setType(RandomTestUtil.randomString());

		_csDiagramSettings.add(_persistence.update(csDiagramSetting));

		return csDiagramSetting;
	}

	private List<CSDiagramSetting> _csDiagramSettings =
		new ArrayList<CSDiagramSetting>();
	private CSDiagramSettingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
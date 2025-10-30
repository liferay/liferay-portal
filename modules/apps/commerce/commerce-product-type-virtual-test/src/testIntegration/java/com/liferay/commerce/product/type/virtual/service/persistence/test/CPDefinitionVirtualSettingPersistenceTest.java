/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.type.virtual.exception.NoSuchCPDefinitionVirtualSettingException;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDefinitionVirtualSettingPersistence;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDefinitionVirtualSettingUtil;
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
public class CPDefinitionVirtualSettingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.product.type.virtual.service"));

	@Before
	public void setUp() {
		_persistence = CPDefinitionVirtualSettingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDefinitionVirtualSetting> iterator =
			_cpDefinitionVirtualSettings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_persistence.create(pk);

		Assert.assertNotNull(cpDefinitionVirtualSetting);

		Assert.assertEquals(cpDefinitionVirtualSetting.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting =
			addCPDefinitionVirtualSetting();

		_persistence.remove(newCPDefinitionVirtualSetting);

		CPDefinitionVirtualSetting existingCPDefinitionVirtualSetting =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionVirtualSetting.getPrimaryKey());

		Assert.assertNull(existingCPDefinitionVirtualSetting);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDefinitionVirtualSetting();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting =
			_persistence.create(pk);

		newCPDefinitionVirtualSetting.setMvccVersion(RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setUuid(RandomTestUtil.randomString());

		newCPDefinitionVirtualSetting.setGroupId(RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setCompanyId(RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setUserId(RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setUserName(
			RandomTestUtil.randomString());

		newCPDefinitionVirtualSetting.setCreateDate(RandomTestUtil.nextDate());

		newCPDefinitionVirtualSetting.setModifiedDate(
			RandomTestUtil.nextDate());

		newCPDefinitionVirtualSetting.setClassNameId(RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setClassPK(RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setActivationStatus(
			RandomTestUtil.nextInt());

		newCPDefinitionVirtualSetting.setDuration(RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setMaxUsages(RandomTestUtil.nextInt());

		newCPDefinitionVirtualSetting.setUseSample(
			RandomTestUtil.randomBoolean());

		newCPDefinitionVirtualSetting.setSampleFileEntryId(
			RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setSampleURL(
			RandomTestUtil.randomString());

		newCPDefinitionVirtualSetting.setTermsOfUseRequired(
			RandomTestUtil.randomBoolean());

		newCPDefinitionVirtualSetting.setTermsOfUseContent(
			RandomTestUtil.randomString());

		newCPDefinitionVirtualSetting.
			setTermsOfUseJournalArticleResourcePrimKey(
				RandomTestUtil.nextLong());

		newCPDefinitionVirtualSetting.setOverride(
			RandomTestUtil.randomBoolean());

		newCPDefinitionVirtualSetting.setLastPublishDate(
			RandomTestUtil.nextDate());

		_cpDefinitionVirtualSettings.add(
			_persistence.update(newCPDefinitionVirtualSetting));

		CPDefinitionVirtualSetting existingCPDefinitionVirtualSetting =
			_persistence.findByPrimaryKey(
				newCPDefinitionVirtualSetting.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getMvccVersion(),
			newCPDefinitionVirtualSetting.getMvccVersion());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getUuid(),
			newCPDefinitionVirtualSetting.getUuid());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.
				getCPDefinitionVirtualSettingId(),
			newCPDefinitionVirtualSetting.getCPDefinitionVirtualSettingId());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getGroupId(),
			newCPDefinitionVirtualSetting.getGroupId());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getCompanyId(),
			newCPDefinitionVirtualSetting.getCompanyId());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getUserId(),
			newCPDefinitionVirtualSetting.getUserId());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getUserName(),
			newCPDefinitionVirtualSetting.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionVirtualSetting.getCreateDate()),
			Time.getShortTimestamp(
				newCPDefinitionVirtualSetting.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionVirtualSetting.getModifiedDate()),
			Time.getShortTimestamp(
				newCPDefinitionVirtualSetting.getModifiedDate()));
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getClassNameId(),
			newCPDefinitionVirtualSetting.getClassNameId());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getClassPK(),
			newCPDefinitionVirtualSetting.getClassPK());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getActivationStatus(),
			newCPDefinitionVirtualSetting.getActivationStatus());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getDuration(),
			newCPDefinitionVirtualSetting.getDuration());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getMaxUsages(),
			newCPDefinitionVirtualSetting.getMaxUsages());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.isUseSample(),
			newCPDefinitionVirtualSetting.isUseSample());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getSampleFileEntryId(),
			newCPDefinitionVirtualSetting.getSampleFileEntryId());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getSampleURL(),
			newCPDefinitionVirtualSetting.getSampleURL());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.isTermsOfUseRequired(),
			newCPDefinitionVirtualSetting.isTermsOfUseRequired());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.getTermsOfUseContent(),
			newCPDefinitionVirtualSetting.getTermsOfUseContent());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.
				getTermsOfUseJournalArticleResourcePrimKey(),
			newCPDefinitionVirtualSetting.
				getTermsOfUseJournalArticleResourcePrimKey());
		Assert.assertEquals(
			existingCPDefinitionVirtualSetting.isOverride(),
			newCPDefinitionVirtualSetting.isOverride());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionVirtualSetting.getLastPublishDate()),
			Time.getShortTimestamp(
				newCPDefinitionVirtualSetting.getLastPublishDate()));
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
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting =
			addCPDefinitionVirtualSetting();

		CPDefinitionVirtualSetting existingCPDefinitionVirtualSetting =
			_persistence.findByPrimaryKey(
				newCPDefinitionVirtualSetting.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionVirtualSetting, newCPDefinitionVirtualSetting);
	}

	@Test(expected = NoSuchCPDefinitionVirtualSettingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDefinitionVirtualSetting>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPDefinitionVirtualSetting", "mvccVersion", true, "uuid", true,
			"CPDefinitionVirtualSettingId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"activationStatus", true, "duration", true, "maxUsages", true,
			"useSample", true, "sampleFileEntryId", true, "sampleURL", true,
			"termsOfUseRequired", true, "termsOfUseContent", true,
			"termsOfUseJournalArticleResourcePrimKey", true, "override", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting =
			addCPDefinitionVirtualSetting();

		CPDefinitionVirtualSetting existingCPDefinitionVirtualSetting =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionVirtualSetting.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionVirtualSetting, newCPDefinitionVirtualSetting);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionVirtualSetting missingCPDefinitionVirtualSetting =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDefinitionVirtualSetting);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting1 =
			addCPDefinitionVirtualSetting();
		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting2 =
			addCPDefinitionVirtualSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionVirtualSetting1.getPrimaryKey());
		primaryKeys.add(newCPDefinitionVirtualSetting2.getPrimaryKey());

		Map<Serializable, CPDefinitionVirtualSetting>
			cpDefinitionVirtualSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, cpDefinitionVirtualSettings.size());
		Assert.assertEquals(
			newCPDefinitionVirtualSetting1,
			cpDefinitionVirtualSettings.get(
				newCPDefinitionVirtualSetting1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDefinitionVirtualSetting2,
			cpDefinitionVirtualSettings.get(
				newCPDefinitionVirtualSetting2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDefinitionVirtualSetting>
			cpDefinitionVirtualSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(cpDefinitionVirtualSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting =
			addCPDefinitionVirtualSetting();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionVirtualSetting.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDefinitionVirtualSetting>
			cpDefinitionVirtualSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, cpDefinitionVirtualSettings.size());
		Assert.assertEquals(
			newCPDefinitionVirtualSetting,
			cpDefinitionVirtualSettings.get(
				newCPDefinitionVirtualSetting.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDefinitionVirtualSetting>
			cpDefinitionVirtualSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(cpDefinitionVirtualSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting =
			addCPDefinitionVirtualSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionVirtualSetting.getPrimaryKey());

		Map<Serializable, CPDefinitionVirtualSetting>
			cpDefinitionVirtualSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, cpDefinitionVirtualSettings.size());
		Assert.assertEquals(
			newCPDefinitionVirtualSetting,
			cpDefinitionVirtualSettings.get(
				newCPDefinitionVirtualSetting.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDefinitionVirtualSetting newCPDefinitionVirtualSetting =
			addCPDefinitionVirtualSetting();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPDefinitionVirtualSetting.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		Assert.assertEquals(
			cpDefinitionVirtualSetting.getUuid(),
			ReflectionTestUtil.invoke(
				cpDefinitionVirtualSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpDefinitionVirtualSetting.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionVirtualSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpDefinitionVirtualSetting.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionVirtualSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(cpDefinitionVirtualSetting.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionVirtualSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected CPDefinitionVirtualSetting addCPDefinitionVirtualSetting()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_persistence.create(pk);

		cpDefinitionVirtualSetting.setMvccVersion(RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setUuid(RandomTestUtil.randomString());

		cpDefinitionVirtualSetting.setGroupId(RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setCompanyId(RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setUserId(RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setUserName(RandomTestUtil.randomString());

		cpDefinitionVirtualSetting.setCreateDate(RandomTestUtil.nextDate());

		cpDefinitionVirtualSetting.setModifiedDate(RandomTestUtil.nextDate());

		cpDefinitionVirtualSetting.setClassNameId(RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setClassPK(RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setActivationStatus(
			RandomTestUtil.nextInt());

		cpDefinitionVirtualSetting.setDuration(RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setMaxUsages(RandomTestUtil.nextInt());

		cpDefinitionVirtualSetting.setUseSample(RandomTestUtil.randomBoolean());

		cpDefinitionVirtualSetting.setSampleFileEntryId(
			RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setSampleURL(RandomTestUtil.randomString());

		cpDefinitionVirtualSetting.setTermsOfUseRequired(
			RandomTestUtil.randomBoolean());

		cpDefinitionVirtualSetting.setTermsOfUseContent(
			RandomTestUtil.randomString());

		cpDefinitionVirtualSetting.setTermsOfUseJournalArticleResourcePrimKey(
			RandomTestUtil.nextLong());

		cpDefinitionVirtualSetting.setOverride(RandomTestUtil.randomBoolean());

		cpDefinitionVirtualSetting.setLastPublishDate(
			RandomTestUtil.nextDate());

		_cpDefinitionVirtualSettings.add(
			_persistence.update(cpDefinitionVirtualSetting));

		return cpDefinitionVirtualSetting;
	}

	private List<CPDefinitionVirtualSetting> _cpDefinitionVirtualSettings =
		new ArrayList<CPDefinitionVirtualSetting>();
	private CPDefinitionVirtualSettingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
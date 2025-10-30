/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPAttachmentFileEntryExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPAttachmentFileEntryException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.persistence.CPAttachmentFileEntryPersistence;
import com.liferay.commerce.product.service.persistence.CPAttachmentFileEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class CPAttachmentFileEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPAttachmentFileEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPAttachmentFileEntry> iterator =
			_cpAttachmentFileEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPAttachmentFileEntry cpAttachmentFileEntry = _persistence.create(pk);

		Assert.assertNotNull(cpAttachmentFileEntry);

		Assert.assertEquals(cpAttachmentFileEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPAttachmentFileEntry newCPAttachmentFileEntry =
			addCPAttachmentFileEntry();

		_persistence.remove(newCPAttachmentFileEntry);

		CPAttachmentFileEntry existingCPAttachmentFileEntry =
			_persistence.fetchByPrimaryKey(
				newCPAttachmentFileEntry.getPrimaryKey());

		Assert.assertNull(existingCPAttachmentFileEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPAttachmentFileEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPAttachmentFileEntry newCPAttachmentFileEntry = _persistence.create(
			pk);

		newCPAttachmentFileEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setUuid(RandomTestUtil.randomString());

		newCPAttachmentFileEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCPAttachmentFileEntry.setGroupId(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setCompanyId(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setUserId(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setUserName(RandomTestUtil.randomString());

		newCPAttachmentFileEntry.setCreateDate(RandomTestUtil.nextDate());

		newCPAttachmentFileEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCPAttachmentFileEntry.setClassNameId(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setClassPK(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setFileEntryId(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setCDNEnabled(RandomTestUtil.randomBoolean());

		newCPAttachmentFileEntry.setCDNURL(RandomTestUtil.randomString());

		newCPAttachmentFileEntry.setDisplayDate(RandomTestUtil.nextDate());

		newCPAttachmentFileEntry.setExpirationDate(RandomTestUtil.nextDate());

		newCPAttachmentFileEntry.setGalleryEnabled(
			RandomTestUtil.randomBoolean());

		newCPAttachmentFileEntry.setTitle(RandomTestUtil.randomString());

		newCPAttachmentFileEntry.setJson(RandomTestUtil.randomString());

		newCPAttachmentFileEntry.setPriority(RandomTestUtil.nextDouble());

		newCPAttachmentFileEntry.setType(RandomTestUtil.nextInt());

		newCPAttachmentFileEntry.setLastPublishDate(RandomTestUtil.nextDate());

		newCPAttachmentFileEntry.setStatus(RandomTestUtil.nextInt());

		newCPAttachmentFileEntry.setStatusByUserId(RandomTestUtil.nextLong());

		newCPAttachmentFileEntry.setStatusByUserName(
			RandomTestUtil.randomString());

		newCPAttachmentFileEntry.setStatusDate(RandomTestUtil.nextDate());

		_cpAttachmentFileEntries.add(
			_persistence.update(newCPAttachmentFileEntry));

		CPAttachmentFileEntry existingCPAttachmentFileEntry =
			_persistence.findByPrimaryKey(
				newCPAttachmentFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPAttachmentFileEntry.getMvccVersion(),
			newCPAttachmentFileEntry.getMvccVersion());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getCtCollectionId(),
			newCPAttachmentFileEntry.getCtCollectionId());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getUuid(),
			newCPAttachmentFileEntry.getUuid());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getExternalReferenceCode(),
			newCPAttachmentFileEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getCPAttachmentFileEntryId(),
			newCPAttachmentFileEntry.getCPAttachmentFileEntryId());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getGroupId(),
			newCPAttachmentFileEntry.getGroupId());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getCompanyId(),
			newCPAttachmentFileEntry.getCompanyId());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getUserId(),
			newCPAttachmentFileEntry.getUserId());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getUserName(),
			newCPAttachmentFileEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPAttachmentFileEntry.getCreateDate()),
			Time.getShortTimestamp(newCPAttachmentFileEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPAttachmentFileEntry.getModifiedDate()),
			Time.getShortTimestamp(newCPAttachmentFileEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getClassNameId(),
			newCPAttachmentFileEntry.getClassNameId());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getClassPK(),
			newCPAttachmentFileEntry.getClassPK());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getFileEntryId(),
			newCPAttachmentFileEntry.getFileEntryId());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.isCDNEnabled(),
			newCPAttachmentFileEntry.isCDNEnabled());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getCDNURL(),
			newCPAttachmentFileEntry.getCDNURL());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPAttachmentFileEntry.getDisplayDate()),
			Time.getShortTimestamp(newCPAttachmentFileEntry.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPAttachmentFileEntry.getExpirationDate()),
			Time.getShortTimestamp(
				newCPAttachmentFileEntry.getExpirationDate()));
		Assert.assertEquals(
			existingCPAttachmentFileEntry.isGalleryEnabled(),
			newCPAttachmentFileEntry.isGalleryEnabled());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getTitle(),
			newCPAttachmentFileEntry.getTitle());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getJson(),
			newCPAttachmentFileEntry.getJson());
		AssertUtils.assertEquals(
			existingCPAttachmentFileEntry.getPriority(),
			newCPAttachmentFileEntry.getPriority());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getType(),
			newCPAttachmentFileEntry.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPAttachmentFileEntry.getLastPublishDate()),
			Time.getShortTimestamp(
				newCPAttachmentFileEntry.getLastPublishDate()));
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getStatus(),
			newCPAttachmentFileEntry.getStatus());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getStatusByUserId(),
			newCPAttachmentFileEntry.getStatusByUserId());
		Assert.assertEquals(
			existingCPAttachmentFileEntry.getStatusByUserName(),
			newCPAttachmentFileEntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPAttachmentFileEntry.getStatusDate()),
			Time.getShortTimestamp(newCPAttachmentFileEntry.getStatusDate()));
	}

	@Test(
		expected = DuplicateCPAttachmentFileEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPAttachmentFileEntry cpAttachmentFileEntry =
			addCPAttachmentFileEntry();

		CPAttachmentFileEntry newCPAttachmentFileEntry =
			addCPAttachmentFileEntry();

		newCPAttachmentFileEntry.setCompanyId(
			cpAttachmentFileEntry.getCompanyId());

		newCPAttachmentFileEntry = _persistence.update(
			newCPAttachmentFileEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPAttachmentFileEntry);

		newCPAttachmentFileEntry.setExternalReferenceCode(
			cpAttachmentFileEntry.getExternalReferenceCode());

		_persistence.update(newCPAttachmentFileEntry);
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
	public void testCountByFileEntryId() throws Exception {
		_persistence.countByFileEntryId(RandomTestUtil.nextLong());

		_persistence.countByFileEntryId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByG_C_F() throws Exception {
		_persistence.countByG_C_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_F(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_F() throws Exception {
		_persistence.countByC_C_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_F(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_C_C(0L, 0L, "null");

		_persistence.countByC_C_C(0L, 0L, (String)null);
	}

	@Test
	public void testCountByC_C_LtD_S() throws Exception {
		_persistence.countByC_C_LtD_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByC_C_LtD_S(0L, 0L, RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByC_C_T_ST() throws Exception {
		_persistence.countByC_C_T_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByC_C_T_ST(0L, 0L, 0, 0);
	}

	@Test
	public void testCountByC_C_T_NotST() throws Exception {
		_persistence.countByC_C_T_NotST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByC_C_T_NotST(0L, 0L, 0, 0);
	}

	@Test
	public void testCountByC_C_G_T_ST() throws Exception {
		_persistence.countByC_C_G_T_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt());

		_persistence.countByC_C_G_T_ST(
			0L, 0L, RandomTestUtil.randomBoolean(), 0, 0);
	}

	@Test
	public void testCountByC_C_G_T_NotST() throws Exception {
		_persistence.countByC_C_G_T_NotST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt());

		_persistence.countByC_C_G_T_NotST(
			0L, 0L, RandomTestUtil.randomBoolean(), 0, 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPAttachmentFileEntry newCPAttachmentFileEntry =
			addCPAttachmentFileEntry();

		CPAttachmentFileEntry existingCPAttachmentFileEntry =
			_persistence.findByPrimaryKey(
				newCPAttachmentFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPAttachmentFileEntry, newCPAttachmentFileEntry);
	}

	@Test(expected = NoSuchCPAttachmentFileEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPAttachmentFileEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPAttachmentFileEntry", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "externalReferenceCode", true,
			"CPAttachmentFileEntryId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"fileEntryId", true, "cdnEnabled", true, "cdnURL", true,
			"displayDate", true, "expirationDate", true, "galleryEnabled", true,
			"title", true, "priority", true, "type", true, "lastPublishDate",
			true, "status", true, "statusByUserId", true, "statusByUserName",
			true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPAttachmentFileEntry newCPAttachmentFileEntry =
			addCPAttachmentFileEntry();

		CPAttachmentFileEntry existingCPAttachmentFileEntry =
			_persistence.fetchByPrimaryKey(
				newCPAttachmentFileEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPAttachmentFileEntry, newCPAttachmentFileEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPAttachmentFileEntry missingCPAttachmentFileEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPAttachmentFileEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPAttachmentFileEntry newCPAttachmentFileEntry1 =
			addCPAttachmentFileEntry();
		CPAttachmentFileEntry newCPAttachmentFileEntry2 =
			addCPAttachmentFileEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPAttachmentFileEntry1.getPrimaryKey());
		primaryKeys.add(newCPAttachmentFileEntry2.getPrimaryKey());

		Map<Serializable, CPAttachmentFileEntry> cpAttachmentFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpAttachmentFileEntries.size());
		Assert.assertEquals(
			newCPAttachmentFileEntry1,
			cpAttachmentFileEntries.get(
				newCPAttachmentFileEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCPAttachmentFileEntry2,
			cpAttachmentFileEntries.get(
				newCPAttachmentFileEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPAttachmentFileEntry> cpAttachmentFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpAttachmentFileEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPAttachmentFileEntry newCPAttachmentFileEntry =
			addCPAttachmentFileEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPAttachmentFileEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPAttachmentFileEntry> cpAttachmentFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpAttachmentFileEntries.size());
		Assert.assertEquals(
			newCPAttachmentFileEntry,
			cpAttachmentFileEntries.get(
				newCPAttachmentFileEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPAttachmentFileEntry> cpAttachmentFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpAttachmentFileEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPAttachmentFileEntry newCPAttachmentFileEntry =
			addCPAttachmentFileEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPAttachmentFileEntry.getPrimaryKey());

		Map<Serializable, CPAttachmentFileEntry> cpAttachmentFileEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpAttachmentFileEntries.size());
		Assert.assertEquals(
			newCPAttachmentFileEntry,
			cpAttachmentFileEntries.get(
				newCPAttachmentFileEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPAttachmentFileEntry newCPAttachmentFileEntry =
			addCPAttachmentFileEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPAttachmentFileEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPAttachmentFileEntry cpAttachmentFileEntry) {

		Assert.assertEquals(
			cpAttachmentFileEntry.getUuid(),
			ReflectionTestUtil.invoke(
				cpAttachmentFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpAttachmentFileEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpAttachmentFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			cpAttachmentFileEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpAttachmentFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpAttachmentFileEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpAttachmentFileEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPAttachmentFileEntry addCPAttachmentFileEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPAttachmentFileEntry cpAttachmentFileEntry = _persistence.create(pk);

		cpAttachmentFileEntry.setMvccVersion(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setCtCollectionId(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setUuid(RandomTestUtil.randomString());

		cpAttachmentFileEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		cpAttachmentFileEntry.setGroupId(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setCompanyId(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setUserId(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setUserName(RandomTestUtil.randomString());

		cpAttachmentFileEntry.setCreateDate(RandomTestUtil.nextDate());

		cpAttachmentFileEntry.setModifiedDate(RandomTestUtil.nextDate());

		cpAttachmentFileEntry.setClassNameId(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setClassPK(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setFileEntryId(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setCDNEnabled(RandomTestUtil.randomBoolean());

		cpAttachmentFileEntry.setCDNURL(RandomTestUtil.randomString());

		cpAttachmentFileEntry.setDisplayDate(RandomTestUtil.nextDate());

		cpAttachmentFileEntry.setExpirationDate(RandomTestUtil.nextDate());

		cpAttachmentFileEntry.setGalleryEnabled(RandomTestUtil.randomBoolean());

		cpAttachmentFileEntry.setTitle(RandomTestUtil.randomString());

		cpAttachmentFileEntry.setJson(RandomTestUtil.randomString());

		cpAttachmentFileEntry.setPriority(RandomTestUtil.nextDouble());

		cpAttachmentFileEntry.setType(RandomTestUtil.nextInt());

		cpAttachmentFileEntry.setLastPublishDate(RandomTestUtil.nextDate());

		cpAttachmentFileEntry.setStatus(RandomTestUtil.nextInt());

		cpAttachmentFileEntry.setStatusByUserId(RandomTestUtil.nextLong());

		cpAttachmentFileEntry.setStatusByUserName(
			RandomTestUtil.randomString());

		cpAttachmentFileEntry.setStatusDate(RandomTestUtil.nextDate());

		_cpAttachmentFileEntries.add(
			_persistence.update(cpAttachmentFileEntry));

		return cpAttachmentFileEntry;
	}

	private List<CPAttachmentFileEntry> _cpAttachmentFileEntries =
		new ArrayList<CPAttachmentFileEntry>();
	private CPAttachmentFileEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
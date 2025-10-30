/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCommerceCatalogExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.persistence.CommerceCatalogPersistence;
import com.liferay.commerce.product.service.persistence.CommerceCatalogUtil;
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
public class CommerceCatalogPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CommerceCatalogUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceCatalog> iterator = _commerceCatalogs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceCatalog commerceCatalog = _persistence.create(pk);

		Assert.assertNotNull(commerceCatalog);

		Assert.assertEquals(commerceCatalog.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceCatalog newCommerceCatalog = addCommerceCatalog();

		_persistence.remove(newCommerceCatalog);

		CommerceCatalog existingCommerceCatalog =
			_persistence.fetchByPrimaryKey(newCommerceCatalog.getPrimaryKey());

		Assert.assertNull(existingCommerceCatalog);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceCatalog();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceCatalog newCommerceCatalog = _persistence.create(pk);

		newCommerceCatalog.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceCatalog.setCtCollectionId(RandomTestUtil.nextLong());

		newCommerceCatalog.setUuid(RandomTestUtil.randomString());

		newCommerceCatalog.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceCatalog.setCompanyId(RandomTestUtil.nextLong());

		newCommerceCatalog.setUserId(RandomTestUtil.nextLong());

		newCommerceCatalog.setUserName(RandomTestUtil.randomString());

		newCommerceCatalog.setCreateDate(RandomTestUtil.nextDate());

		newCommerceCatalog.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceCatalog.setAccountEntryId(RandomTestUtil.nextLong());

		newCommerceCatalog.setName(RandomTestUtil.randomString());

		newCommerceCatalog.setCommerceCurrencyCode(
			RandomTestUtil.randomString());

		newCommerceCatalog.setCatalogDefaultLanguageId(
			RandomTestUtil.randomString());

		newCommerceCatalog.setSystem(RandomTestUtil.randomBoolean());

		_commerceCatalogs.add(_persistence.update(newCommerceCatalog));

		CommerceCatalog existingCommerceCatalog = _persistence.findByPrimaryKey(
			newCommerceCatalog.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceCatalog.getMvccVersion(),
			newCommerceCatalog.getMvccVersion());
		Assert.assertEquals(
			existingCommerceCatalog.getCtCollectionId(),
			newCommerceCatalog.getCtCollectionId());
		Assert.assertEquals(
			existingCommerceCatalog.getUuid(), newCommerceCatalog.getUuid());
		Assert.assertEquals(
			existingCommerceCatalog.getExternalReferenceCode(),
			newCommerceCatalog.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceCatalog.getCommerceCatalogId(),
			newCommerceCatalog.getCommerceCatalogId());
		Assert.assertEquals(
			existingCommerceCatalog.getCompanyId(),
			newCommerceCatalog.getCompanyId());
		Assert.assertEquals(
			existingCommerceCatalog.getUserId(),
			newCommerceCatalog.getUserId());
		Assert.assertEquals(
			existingCommerceCatalog.getUserName(),
			newCommerceCatalog.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceCatalog.getCreateDate()),
			Time.getShortTimestamp(newCommerceCatalog.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceCatalog.getModifiedDate()),
			Time.getShortTimestamp(newCommerceCatalog.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceCatalog.getAccountEntryId(),
			newCommerceCatalog.getAccountEntryId());
		Assert.assertEquals(
			existingCommerceCatalog.getName(), newCommerceCatalog.getName());
		Assert.assertEquals(
			existingCommerceCatalog.getCommerceCurrencyCode(),
			newCommerceCatalog.getCommerceCurrencyCode());
		Assert.assertEquals(
			existingCommerceCatalog.getCatalogDefaultLanguageId(),
			newCommerceCatalog.getCatalogDefaultLanguageId());
		Assert.assertEquals(
			existingCommerceCatalog.isSystem(), newCommerceCatalog.isSystem());
	}

	@Test(
		expected = DuplicateCommerceCatalogExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceCatalog commerceCatalog = addCommerceCatalog();

		CommerceCatalog newCommerceCatalog = addCommerceCatalog();

		newCommerceCatalog.setCompanyId(commerceCatalog.getCompanyId());

		newCommerceCatalog = _persistence.update(newCommerceCatalog);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceCatalog);

		newCommerceCatalog.setExternalReferenceCode(
			commerceCatalog.getExternalReferenceCode());

		_persistence.update(newCommerceCatalog);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByAccountEntryId() throws Exception {
		_persistence.countByAccountEntryId(RandomTestUtil.nextLong());

		_persistence.countByAccountEntryId(0L);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_S(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceCatalog newCommerceCatalog = addCommerceCatalog();

		CommerceCatalog existingCommerceCatalog = _persistence.findByPrimaryKey(
			newCommerceCatalog.getPrimaryKey());

		Assert.assertEquals(existingCommerceCatalog, newCommerceCatalog);
	}

	@Test(expected = NoSuchCatalogException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceCatalog> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceCatalog", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "commerceCatalogId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "accountEntryId", true,
			"name", true, "commerceCurrencyCode", true,
			"catalogDefaultLanguageId", true, "system", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceCatalog newCommerceCatalog = addCommerceCatalog();

		CommerceCatalog existingCommerceCatalog =
			_persistence.fetchByPrimaryKey(newCommerceCatalog.getPrimaryKey());

		Assert.assertEquals(existingCommerceCatalog, newCommerceCatalog);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceCatalog missingCommerceCatalog = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCommerceCatalog);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceCatalog newCommerceCatalog1 = addCommerceCatalog();
		CommerceCatalog newCommerceCatalog2 = addCommerceCatalog();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceCatalog1.getPrimaryKey());
		primaryKeys.add(newCommerceCatalog2.getPrimaryKey());

		Map<Serializable, CommerceCatalog> commerceCatalogs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceCatalogs.size());
		Assert.assertEquals(
			newCommerceCatalog1,
			commerceCatalogs.get(newCommerceCatalog1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceCatalog2,
			commerceCatalogs.get(newCommerceCatalog2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceCatalog> commerceCatalogs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceCatalogs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceCatalog newCommerceCatalog = addCommerceCatalog();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceCatalog.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceCatalog> commerceCatalogs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceCatalogs.size());
		Assert.assertEquals(
			newCommerceCatalog,
			commerceCatalogs.get(newCommerceCatalog.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceCatalog> commerceCatalogs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceCatalogs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceCatalog newCommerceCatalog = addCommerceCatalog();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceCatalog.getPrimaryKey());

		Map<Serializable, CommerceCatalog> commerceCatalogs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceCatalogs.size());
		Assert.assertEquals(
			newCommerceCatalog,
			commerceCatalogs.get(newCommerceCatalog.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceCatalog newCommerceCatalog = addCommerceCatalog();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCommerceCatalog.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceCatalog commerceCatalog) {
		Assert.assertEquals(
			commerceCatalog.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceCatalog, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceCatalog.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceCatalog, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceCatalog addCommerceCatalog() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceCatalog commerceCatalog = _persistence.create(pk);

		commerceCatalog.setMvccVersion(RandomTestUtil.nextLong());

		commerceCatalog.setCtCollectionId(RandomTestUtil.nextLong());

		commerceCatalog.setUuid(RandomTestUtil.randomString());

		commerceCatalog.setExternalReferenceCode(RandomTestUtil.randomString());

		commerceCatalog.setCompanyId(RandomTestUtil.nextLong());

		commerceCatalog.setUserId(RandomTestUtil.nextLong());

		commerceCatalog.setUserName(RandomTestUtil.randomString());

		commerceCatalog.setCreateDate(RandomTestUtil.nextDate());

		commerceCatalog.setModifiedDate(RandomTestUtil.nextDate());

		commerceCatalog.setAccountEntryId(RandomTestUtil.nextLong());

		commerceCatalog.setName(RandomTestUtil.randomString());

		commerceCatalog.setCommerceCurrencyCode(RandomTestUtil.randomString());

		commerceCatalog.setCatalogDefaultLanguageId(
			RandomTestUtil.randomString());

		commerceCatalog.setSystem(RandomTestUtil.randomBoolean());

		_commerceCatalogs.add(_persistence.update(commerceCatalog));

		return commerceCatalog;
	}

	private List<CommerceCatalog> _commerceCatalogs =
		new ArrayList<CommerceCatalog>();
	private CommerceCatalogPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
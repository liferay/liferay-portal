/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateWebsiteExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchWebsiteException;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.persistence.WebsitePersistence;
import com.liferay.portal.kernel.service.persistence.WebsiteUtil;
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
public class WebsitePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = WebsiteUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Website> iterator = _websites.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Website website = _persistence.create(pk);

		Assert.assertNotNull(website);

		Assert.assertEquals(website.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Website newWebsite = addWebsite();

		_persistence.remove(newWebsite);

		Website existingWebsite = _persistence.fetchByPrimaryKey(
			newWebsite.getPrimaryKey());

		Assert.assertNull(existingWebsite);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWebsite();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Website newWebsite = _persistence.create(pk);

		newWebsite.setMvccVersion(RandomTestUtil.nextLong());

		newWebsite.setUuid(RandomTestUtil.randomString());

		newWebsite.setExternalReferenceCode(RandomTestUtil.randomString());

		newWebsite.setCompanyId(RandomTestUtil.nextLong());

		newWebsite.setUserId(RandomTestUtil.nextLong());

		newWebsite.setUserName(RandomTestUtil.randomString());

		newWebsite.setCreateDate(RandomTestUtil.nextDate());

		newWebsite.setModifiedDate(RandomTestUtil.nextDate());

		newWebsite.setClassNameId(RandomTestUtil.nextLong());

		newWebsite.setClassPK(RandomTestUtil.nextLong());

		newWebsite.setUrl(RandomTestUtil.randomString());

		newWebsite.setListTypeId(RandomTestUtil.nextLong());

		newWebsite.setPrimary(RandomTestUtil.randomBoolean());

		newWebsite.setLastPublishDate(RandomTestUtil.nextDate());

		_websites.add(_persistence.update(newWebsite));

		Website existingWebsite = _persistence.findByPrimaryKey(
			newWebsite.getPrimaryKey());

		Assert.assertEquals(
			existingWebsite.getMvccVersion(), newWebsite.getMvccVersion());
		Assert.assertEquals(existingWebsite.getUuid(), newWebsite.getUuid());
		Assert.assertEquals(
			existingWebsite.getExternalReferenceCode(),
			newWebsite.getExternalReferenceCode());
		Assert.assertEquals(
			existingWebsite.getWebsiteId(), newWebsite.getWebsiteId());
		Assert.assertEquals(
			existingWebsite.getCompanyId(), newWebsite.getCompanyId());
		Assert.assertEquals(
			existingWebsite.getUserId(), newWebsite.getUserId());
		Assert.assertEquals(
			existingWebsite.getUserName(), newWebsite.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWebsite.getCreateDate()),
			Time.getShortTimestamp(newWebsite.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingWebsite.getModifiedDate()),
			Time.getShortTimestamp(newWebsite.getModifiedDate()));
		Assert.assertEquals(
			existingWebsite.getClassNameId(), newWebsite.getClassNameId());
		Assert.assertEquals(
			existingWebsite.getClassPK(), newWebsite.getClassPK());
		Assert.assertEquals(existingWebsite.getUrl(), newWebsite.getUrl());
		Assert.assertEquals(
			existingWebsite.getListTypeId(), newWebsite.getListTypeId());
		Assert.assertEquals(
			existingWebsite.isPrimary(), newWebsite.isPrimary());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWebsite.getLastPublishDate()),
			Time.getShortTimestamp(newWebsite.getLastPublishDate()));
	}

	@Test(expected = DuplicateWebsiteExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		Website website = addWebsite();

		Website newWebsite = addWebsite();

		newWebsite.setCompanyId(website.getCompanyId());

		newWebsite = _persistence.update(newWebsite);

		Session session = _persistence.getCurrentSession();

		session.evict(newWebsite);

		newWebsite.setExternalReferenceCode(website.getExternalReferenceCode());

		_persistence.update(newWebsite);
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
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_C_P() throws Exception {
		_persistence.countByC_C_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_C_C_P(0L, 0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Website newWebsite = addWebsite();

		Website existingWebsite = _persistence.findByPrimaryKey(
			newWebsite.getPrimaryKey());

		Assert.assertEquals(existingWebsite, newWebsite);
	}

	@Test(expected = NoSuchWebsiteException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Website> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Website", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "websiteId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true, "url",
			true, "listTypeId", true, "primary", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Website newWebsite = addWebsite();

		Website existingWebsite = _persistence.fetchByPrimaryKey(
			newWebsite.getPrimaryKey());

		Assert.assertEquals(existingWebsite, newWebsite);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Website missingWebsite = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWebsite);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Website newWebsite1 = addWebsite();
		Website newWebsite2 = addWebsite();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWebsite1.getPrimaryKey());
		primaryKeys.add(newWebsite2.getPrimaryKey());

		Map<Serializable, Website> websites = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, websites.size());
		Assert.assertEquals(
			newWebsite1, websites.get(newWebsite1.getPrimaryKey()));
		Assert.assertEquals(
			newWebsite2, websites.get(newWebsite2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Website> websites = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(websites.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Website newWebsite = addWebsite();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWebsite.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Website> websites = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, websites.size());
		Assert.assertEquals(
			newWebsite, websites.get(newWebsite.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Website> websites = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(websites.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Website newWebsite = addWebsite();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWebsite.getPrimaryKey());

		Map<Serializable, Website> websites = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, websites.size());
		Assert.assertEquals(
			newWebsite, websites.get(newWebsite.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Website newWebsite = addWebsite();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newWebsite.getPrimaryKey()));
	}

	private void _assertOriginalValues(Website website) {
		Assert.assertEquals(
			website.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				website, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(website.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				website, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected Website addWebsite() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Website website = _persistence.create(pk);

		website.setMvccVersion(RandomTestUtil.nextLong());

		website.setUuid(RandomTestUtil.randomString());

		website.setExternalReferenceCode(RandomTestUtil.randomString());

		website.setCompanyId(RandomTestUtil.nextLong());

		website.setUserId(RandomTestUtil.nextLong());

		website.setUserName(RandomTestUtil.randomString());

		website.setCreateDate(RandomTestUtil.nextDate());

		website.setModifiedDate(RandomTestUtil.nextDate());

		website.setClassNameId(RandomTestUtil.nextLong());

		website.setClassPK(RandomTestUtil.nextLong());

		website.setUrl(RandomTestUtil.randomString());

		website.setListTypeId(RandomTestUtil.nextLong());

		website.setPrimary(RandomTestUtil.randomBoolean());

		website.setLastPublishDate(RandomTestUtil.nextDate());

		_websites.add(_persistence.update(website));

		return website;
	}

	private List<Website> _websites = new ArrayList<Website>();
	private WebsitePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
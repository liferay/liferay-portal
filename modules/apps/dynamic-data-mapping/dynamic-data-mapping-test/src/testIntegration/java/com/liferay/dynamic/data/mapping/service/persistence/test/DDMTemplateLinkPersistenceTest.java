/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateLinkException;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateLinkUtil;
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
public class DDMTemplateLinkPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMTemplateLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMTemplateLink> iterator = _ddmTemplateLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplateLink ddmTemplateLink = _persistence.create(pk);

		Assert.assertNotNull(ddmTemplateLink);

		Assert.assertEquals(ddmTemplateLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMTemplateLink newDDMTemplateLink = addDDMTemplateLink();

		_persistence.remove(newDDMTemplateLink);

		DDMTemplateLink existingDDMTemplateLink =
			_persistence.fetchByPrimaryKey(newDDMTemplateLink.getPrimaryKey());

		Assert.assertNull(existingDDMTemplateLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMTemplateLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplateLink newDDMTemplateLink = _persistence.create(pk);

		newDDMTemplateLink.setMvccVersion(RandomTestUtil.nextLong());

		newDDMTemplateLink.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMTemplateLink.setCompanyId(RandomTestUtil.nextLong());

		newDDMTemplateLink.setClassNameId(RandomTestUtil.nextLong());

		newDDMTemplateLink.setClassPK(RandomTestUtil.nextLong());

		newDDMTemplateLink.setTemplateId(RandomTestUtil.nextLong());

		_ddmTemplateLinks.add(_persistence.update(newDDMTemplateLink));

		DDMTemplateLink existingDDMTemplateLink = _persistence.findByPrimaryKey(
			newDDMTemplateLink.getPrimaryKey());

		Assert.assertEquals(
			existingDDMTemplateLink.getMvccVersion(),
			newDDMTemplateLink.getMvccVersion());
		Assert.assertEquals(
			existingDDMTemplateLink.getCtCollectionId(),
			newDDMTemplateLink.getCtCollectionId());
		Assert.assertEquals(
			existingDDMTemplateLink.getTemplateLinkId(),
			newDDMTemplateLink.getTemplateLinkId());
		Assert.assertEquals(
			existingDDMTemplateLink.getCompanyId(),
			newDDMTemplateLink.getCompanyId());
		Assert.assertEquals(
			existingDDMTemplateLink.getClassNameId(),
			newDDMTemplateLink.getClassNameId());
		Assert.assertEquals(
			existingDDMTemplateLink.getClassPK(),
			newDDMTemplateLink.getClassPK());
		Assert.assertEquals(
			existingDDMTemplateLink.getTemplateId(),
			newDDMTemplateLink.getTemplateId());
	}

	@Test
	public void testCountByTemplateId() throws Exception {
		_persistence.countByTemplateId(RandomTestUtil.nextLong());

		_persistence.countByTemplateId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMTemplateLink newDDMTemplateLink = addDDMTemplateLink();

		DDMTemplateLink existingDDMTemplateLink = _persistence.findByPrimaryKey(
			newDDMTemplateLink.getPrimaryKey());

		Assert.assertEquals(existingDDMTemplateLink, newDDMTemplateLink);
	}

	@Test(expected = NoSuchTemplateLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMTemplateLink> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMTemplateLink", "mvccVersion", true, "ctCollectionId", true,
			"templateLinkId", true, "companyId", true, "classNameId", true,
			"classPK", true, "templateId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMTemplateLink newDDMTemplateLink = addDDMTemplateLink();

		DDMTemplateLink existingDDMTemplateLink =
			_persistence.fetchByPrimaryKey(newDDMTemplateLink.getPrimaryKey());

		Assert.assertEquals(existingDDMTemplateLink, newDDMTemplateLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplateLink missingDDMTemplateLink = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingDDMTemplateLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMTemplateLink newDDMTemplateLink1 = addDDMTemplateLink();
		DDMTemplateLink newDDMTemplateLink2 = addDDMTemplateLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplateLink1.getPrimaryKey());
		primaryKeys.add(newDDMTemplateLink2.getPrimaryKey());

		Map<Serializable, DDMTemplateLink> ddmTemplateLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmTemplateLinks.size());
		Assert.assertEquals(
			newDDMTemplateLink1,
			ddmTemplateLinks.get(newDDMTemplateLink1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMTemplateLink2,
			ddmTemplateLinks.get(newDDMTemplateLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMTemplateLink> ddmTemplateLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmTemplateLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMTemplateLink newDDMTemplateLink = addDDMTemplateLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplateLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMTemplateLink> ddmTemplateLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmTemplateLinks.size());
		Assert.assertEquals(
			newDDMTemplateLink,
			ddmTemplateLinks.get(newDDMTemplateLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMTemplateLink> ddmTemplateLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmTemplateLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMTemplateLink newDDMTemplateLink = addDDMTemplateLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplateLink.getPrimaryKey());

		Map<Serializable, DDMTemplateLink> ddmTemplateLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmTemplateLinks.size());
		Assert.assertEquals(
			newDDMTemplateLink,
			ddmTemplateLinks.get(newDDMTemplateLink.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMTemplateLink newDDMTemplateLink = addDDMTemplateLink();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDMTemplateLink.getPrimaryKey()));
	}

	private void _assertOriginalValues(DDMTemplateLink ddmTemplateLink) {
		Assert.assertEquals(
			Long.valueOf(ddmTemplateLink.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ddmTemplateLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(ddmTemplateLink.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				ddmTemplateLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected DDMTemplateLink addDDMTemplateLink() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplateLink ddmTemplateLink = _persistence.create(pk);

		ddmTemplateLink.setMvccVersion(RandomTestUtil.nextLong());

		ddmTemplateLink.setCtCollectionId(RandomTestUtil.nextLong());

		ddmTemplateLink.setCompanyId(RandomTestUtil.nextLong());

		ddmTemplateLink.setClassNameId(RandomTestUtil.nextLong());

		ddmTemplateLink.setClassPK(RandomTestUtil.nextLong());

		ddmTemplateLink.setTemplateId(RandomTestUtil.nextLong());

		_ddmTemplateLinks.add(_persistence.update(ddmTemplateLink));

		return ddmTemplateLink;
	}

	private List<DDMTemplateLink> _ddmTemplateLinks =
		new ArrayList<DDMTemplateLink>();
	private DDMTemplateLinkPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
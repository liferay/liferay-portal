/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.search.experiences.exception.DuplicateSXPElementExternalReferenceCodeException;
import com.liferay.search.experiences.exception.NoSuchSXPElementException;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.service.persistence.SXPElementPersistence;
import com.liferay.search.experiences.service.persistence.SXPElementUtil;

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
public class SXPElementPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.search.experiences.service"));

	@Before
	public void setUp() {
		_persistence = SXPElementUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SXPElement> iterator = _sxpElements.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SXPElement sxpElement = _persistence.create(pk);

		Assert.assertNotNull(sxpElement);

		Assert.assertEquals(sxpElement.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SXPElement newSXPElement = addSXPElement();

		_persistence.remove(newSXPElement);

		SXPElement existingSXPElement = _persistence.fetchByPrimaryKey(
			newSXPElement.getPrimaryKey());

		Assert.assertNull(existingSXPElement);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSXPElement();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SXPElement newSXPElement = _persistence.create(pk);

		newSXPElement.setMvccVersion(RandomTestUtil.nextLong());

		newSXPElement.setUuid(RandomTestUtil.randomString());

		newSXPElement.setExternalReferenceCode(RandomTestUtil.randomString());

		newSXPElement.setCompanyId(RandomTestUtil.nextLong());

		newSXPElement.setUserId(RandomTestUtil.nextLong());

		newSXPElement.setUserName(RandomTestUtil.randomString());

		newSXPElement.setCreateDate(RandomTestUtil.nextDate());

		newSXPElement.setModifiedDate(RandomTestUtil.nextDate());

		newSXPElement.setDescription(RandomTestUtil.randomString());

		newSXPElement.setElementDefinitionJSON(RandomTestUtil.randomString());

		newSXPElement.setFallbackDescription(RandomTestUtil.randomString());

		newSXPElement.setFallbackTitle(RandomTestUtil.randomString());

		newSXPElement.setHidden(RandomTestUtil.randomBoolean());

		newSXPElement.setReadOnly(RandomTestUtil.randomBoolean());

		newSXPElement.setSchemaVersion(RandomTestUtil.randomString());

		newSXPElement.setTitle(RandomTestUtil.randomString());

		newSXPElement.setType(RandomTestUtil.nextInt());

		newSXPElement.setVersion(RandomTestUtil.randomString());

		newSXPElement.setStatus(RandomTestUtil.nextInt());

		_sxpElements.add(_persistence.update(newSXPElement));

		SXPElement existingSXPElement = _persistence.findByPrimaryKey(
			newSXPElement.getPrimaryKey());

		Assert.assertEquals(
			existingSXPElement.getMvccVersion(),
			newSXPElement.getMvccVersion());
		Assert.assertEquals(
			existingSXPElement.getUuid(), newSXPElement.getUuid());
		Assert.assertEquals(
			existingSXPElement.getExternalReferenceCode(),
			newSXPElement.getExternalReferenceCode());
		Assert.assertEquals(
			existingSXPElement.getSXPElementId(),
			newSXPElement.getSXPElementId());
		Assert.assertEquals(
			existingSXPElement.getCompanyId(), newSXPElement.getCompanyId());
		Assert.assertEquals(
			existingSXPElement.getUserId(), newSXPElement.getUserId());
		Assert.assertEquals(
			existingSXPElement.getUserName(), newSXPElement.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSXPElement.getCreateDate()),
			Time.getShortTimestamp(newSXPElement.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSXPElement.getModifiedDate()),
			Time.getShortTimestamp(newSXPElement.getModifiedDate()));
		Assert.assertEquals(
			existingSXPElement.getDescription(),
			newSXPElement.getDescription());
		Assert.assertEquals(
			existingSXPElement.getElementDefinitionJSON(),
			newSXPElement.getElementDefinitionJSON());
		Assert.assertEquals(
			existingSXPElement.getFallbackDescription(),
			newSXPElement.getFallbackDescription());
		Assert.assertEquals(
			existingSXPElement.getFallbackTitle(),
			newSXPElement.getFallbackTitle());
		Assert.assertEquals(
			existingSXPElement.isHidden(), newSXPElement.isHidden());
		Assert.assertEquals(
			existingSXPElement.isReadOnly(), newSXPElement.isReadOnly());
		Assert.assertEquals(
			existingSXPElement.getSchemaVersion(),
			newSXPElement.getSchemaVersion());
		Assert.assertEquals(
			existingSXPElement.getTitle(), newSXPElement.getTitle());
		Assert.assertEquals(
			existingSXPElement.getType(), newSXPElement.getType());
		Assert.assertEquals(
			existingSXPElement.getVersion(), newSXPElement.getVersion());
		Assert.assertEquals(
			existingSXPElement.getStatus(), newSXPElement.getStatus());
	}

	@Test(expected = DuplicateSXPElementExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		SXPElement sxpElement = addSXPElement();

		SXPElement newSXPElement = addSXPElement();

		newSXPElement.setCompanyId(sxpElement.getCompanyId());

		newSXPElement = _persistence.update(newSXPElement);

		Session session = _persistence.getCurrentSession();

		session.evict(newSXPElement);

		newSXPElement.setExternalReferenceCode(
			sxpElement.getExternalReferenceCode());

		_persistence.update(newSXPElement);
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
	public void testCountByC_R() throws Exception {
		_persistence.countByC_R(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_R(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_T() throws Exception {
		_persistence.countByC_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_T(0L, 0);
	}

	@Test
	public void testCountByC_T_S() throws Exception {
		_persistence.countByC_T_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt());

		_persistence.countByC_T_S(0L, 0, 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SXPElement newSXPElement = addSXPElement();

		SXPElement existingSXPElement = _persistence.findByPrimaryKey(
			newSXPElement.getPrimaryKey());

		Assert.assertEquals(existingSXPElement, newSXPElement);
	}

	@Test(expected = NoSuchSXPElementException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SXPElement> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SXPElement", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "sxpElementId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "description", true, "fallbackDescription",
			true, "fallbackTitle", true, "hidden", true, "readOnly", true,
			"schemaVersion", true, "title", true, "type", true, "version", true,
			"status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SXPElement newSXPElement = addSXPElement();

		SXPElement existingSXPElement = _persistence.fetchByPrimaryKey(
			newSXPElement.getPrimaryKey());

		Assert.assertEquals(existingSXPElement, newSXPElement);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SXPElement missingSXPElement = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSXPElement);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SXPElement newSXPElement1 = addSXPElement();
		SXPElement newSXPElement2 = addSXPElement();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSXPElement1.getPrimaryKey());
		primaryKeys.add(newSXPElement2.getPrimaryKey());

		Map<Serializable, SXPElement> sxpElements =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, sxpElements.size());
		Assert.assertEquals(
			newSXPElement1, sxpElements.get(newSXPElement1.getPrimaryKey()));
		Assert.assertEquals(
			newSXPElement2, sxpElements.get(newSXPElement2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SXPElement> sxpElements =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sxpElements.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SXPElement newSXPElement = addSXPElement();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSXPElement.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SXPElement> sxpElements =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sxpElements.size());
		Assert.assertEquals(
			newSXPElement, sxpElements.get(newSXPElement.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SXPElement> sxpElements =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sxpElements.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SXPElement newSXPElement = addSXPElement();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSXPElement.getPrimaryKey());

		Map<Serializable, SXPElement> sxpElements =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sxpElements.size());
		Assert.assertEquals(
			newSXPElement, sxpElements.get(newSXPElement.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SXPElement newSXPElement = addSXPElement();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSXPElement.getPrimaryKey()));
	}

	private void _assertOriginalValues(SXPElement sxpElement) {
		Assert.assertEquals(
			sxpElement.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				sxpElement, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(sxpElement.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				sxpElement, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected SXPElement addSXPElement() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SXPElement sxpElement = _persistence.create(pk);

		sxpElement.setMvccVersion(RandomTestUtil.nextLong());

		sxpElement.setUuid(RandomTestUtil.randomString());

		sxpElement.setExternalReferenceCode(RandomTestUtil.randomString());

		sxpElement.setCompanyId(RandomTestUtil.nextLong());

		sxpElement.setUserId(RandomTestUtil.nextLong());

		sxpElement.setUserName(RandomTestUtil.randomString());

		sxpElement.setCreateDate(RandomTestUtil.nextDate());

		sxpElement.setModifiedDate(RandomTestUtil.nextDate());

		sxpElement.setDescription(RandomTestUtil.randomString());

		sxpElement.setElementDefinitionJSON(RandomTestUtil.randomString());

		sxpElement.setFallbackDescription(RandomTestUtil.randomString());

		sxpElement.setFallbackTitle(RandomTestUtil.randomString());

		sxpElement.setHidden(RandomTestUtil.randomBoolean());

		sxpElement.setReadOnly(RandomTestUtil.randomBoolean());

		sxpElement.setSchemaVersion(RandomTestUtil.randomString());

		sxpElement.setTitle(RandomTestUtil.randomString());

		sxpElement.setType(RandomTestUtil.nextInt());

		sxpElement.setVersion(RandomTestUtil.randomString());

		sxpElement.setStatus(RandomTestUtil.nextInt());

		_sxpElements.add(_persistence.update(sxpElement));

		return sxpElement;
	}

	private List<SXPElement> _sxpElements = new ArrayList<SXPElement>();
	private SXPElementPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
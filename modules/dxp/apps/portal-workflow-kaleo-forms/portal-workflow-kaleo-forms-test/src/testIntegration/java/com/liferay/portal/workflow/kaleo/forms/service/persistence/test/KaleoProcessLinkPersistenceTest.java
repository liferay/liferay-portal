/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service.persistence.test;

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
import com.liferay.portal.workflow.kaleo.forms.exception.NoSuchKaleoProcessLinkException;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.KaleoProcessLinkPersistence;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.KaleoProcessLinkUtil;

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
public class KaleoProcessLinkPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.workflow.kaleo.forms.service"));

	@Before
	public void setUp() {
		_persistence = KaleoProcessLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoProcessLink> iterator = _kaleoProcessLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoProcessLink kaleoProcessLink = _persistence.create(pk);

		Assert.assertNotNull(kaleoProcessLink);

		Assert.assertEquals(kaleoProcessLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoProcessLink newKaleoProcessLink = addKaleoProcessLink();

		_persistence.remove(newKaleoProcessLink);

		KaleoProcessLink existingKaleoProcessLink =
			_persistence.fetchByPrimaryKey(newKaleoProcessLink.getPrimaryKey());

		Assert.assertNull(existingKaleoProcessLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoProcessLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoProcessLink newKaleoProcessLink = _persistence.create(pk);

		newKaleoProcessLink.setCompanyId(RandomTestUtil.nextLong());

		newKaleoProcessLink.setKaleoProcessId(RandomTestUtil.nextLong());

		newKaleoProcessLink.setWorkflowTaskName(RandomTestUtil.randomString());

		newKaleoProcessLink.setDDMTemplateId(RandomTestUtil.nextLong());

		_kaleoProcessLinks.add(_persistence.update(newKaleoProcessLink));

		KaleoProcessLink existingKaleoProcessLink =
			_persistence.findByPrimaryKey(newKaleoProcessLink.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoProcessLink.getKaleoProcessLinkId(),
			newKaleoProcessLink.getKaleoProcessLinkId());
		Assert.assertEquals(
			existingKaleoProcessLink.getCompanyId(),
			newKaleoProcessLink.getCompanyId());
		Assert.assertEquals(
			existingKaleoProcessLink.getKaleoProcessId(),
			newKaleoProcessLink.getKaleoProcessId());
		Assert.assertEquals(
			existingKaleoProcessLink.getWorkflowTaskName(),
			newKaleoProcessLink.getWorkflowTaskName());
		Assert.assertEquals(
			existingKaleoProcessLink.getDDMTemplateId(),
			newKaleoProcessLink.getDDMTemplateId());
	}

	@Test
	public void testCountByKaleoProcessId() throws Exception {
		_persistence.countByKaleoProcessId(RandomTestUtil.nextLong());

		_persistence.countByKaleoProcessId(0L);
	}

	@Test
	public void testCountByKPI_WTN() throws Exception {
		_persistence.countByKPI_WTN(RandomTestUtil.nextLong(), "");

		_persistence.countByKPI_WTN(0L, "null");

		_persistence.countByKPI_WTN(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoProcessLink newKaleoProcessLink = addKaleoProcessLink();

		KaleoProcessLink existingKaleoProcessLink =
			_persistence.findByPrimaryKey(newKaleoProcessLink.getPrimaryKey());

		Assert.assertEquals(existingKaleoProcessLink, newKaleoProcessLink);
	}

	@Test(expected = NoSuchKaleoProcessLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoProcessLink> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoProcessLink", "kaleoProcessLinkId", true, "companyId", true,
			"kaleoProcessId", true, "workflowTaskName", true, "DDMTemplateId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoProcessLink newKaleoProcessLink = addKaleoProcessLink();

		KaleoProcessLink existingKaleoProcessLink =
			_persistence.fetchByPrimaryKey(newKaleoProcessLink.getPrimaryKey());

		Assert.assertEquals(existingKaleoProcessLink, newKaleoProcessLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoProcessLink missingKaleoProcessLink =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoProcessLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoProcessLink newKaleoProcessLink1 = addKaleoProcessLink();
		KaleoProcessLink newKaleoProcessLink2 = addKaleoProcessLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoProcessLink1.getPrimaryKey());
		primaryKeys.add(newKaleoProcessLink2.getPrimaryKey());

		Map<Serializable, KaleoProcessLink> kaleoProcessLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoProcessLinks.size());
		Assert.assertEquals(
			newKaleoProcessLink1,
			kaleoProcessLinks.get(newKaleoProcessLink1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoProcessLink2,
			kaleoProcessLinks.get(newKaleoProcessLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoProcessLink> kaleoProcessLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoProcessLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoProcessLink newKaleoProcessLink = addKaleoProcessLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoProcessLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoProcessLink> kaleoProcessLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoProcessLinks.size());
		Assert.assertEquals(
			newKaleoProcessLink,
			kaleoProcessLinks.get(newKaleoProcessLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoProcessLink> kaleoProcessLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoProcessLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoProcessLink newKaleoProcessLink = addKaleoProcessLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoProcessLink.getPrimaryKey());

		Map<Serializable, KaleoProcessLink> kaleoProcessLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoProcessLinks.size());
		Assert.assertEquals(
			newKaleoProcessLink,
			kaleoProcessLinks.get(newKaleoProcessLink.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KaleoProcessLink newKaleoProcessLink = addKaleoProcessLink();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newKaleoProcessLink.getPrimaryKey()));
	}

	private void _assertOriginalValues(KaleoProcessLink kaleoProcessLink) {
		Assert.assertEquals(
			Long.valueOf(kaleoProcessLink.getKaleoProcessId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoProcessLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "kaleoProcessId"));
		Assert.assertEquals(
			kaleoProcessLink.getWorkflowTaskName(),
			ReflectionTestUtil.invoke(
				kaleoProcessLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "workflowTaskName"));
	}

	protected KaleoProcessLink addKaleoProcessLink() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoProcessLink kaleoProcessLink = _persistence.create(pk);

		kaleoProcessLink.setCompanyId(RandomTestUtil.nextLong());

		kaleoProcessLink.setKaleoProcessId(RandomTestUtil.nextLong());

		kaleoProcessLink.setWorkflowTaskName(RandomTestUtil.randomString());

		kaleoProcessLink.setDDMTemplateId(RandomTestUtil.nextLong());

		_kaleoProcessLinks.add(_persistence.update(kaleoProcessLink));

		return kaleoProcessLink;
	}

	private List<KaleoProcessLink> _kaleoProcessLinks =
		new ArrayList<KaleoProcessLink>();
	private KaleoProcessLinkPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
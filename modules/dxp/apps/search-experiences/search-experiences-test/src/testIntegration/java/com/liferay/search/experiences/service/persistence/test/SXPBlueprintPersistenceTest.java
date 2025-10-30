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
import com.liferay.search.experiences.exception.DuplicateSXPBlueprintExternalReferenceCodeException;
import com.liferay.search.experiences.exception.NoSuchSXPBlueprintException;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.persistence.SXPBlueprintPersistence;
import com.liferay.search.experiences.service.persistence.SXPBlueprintUtil;

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
public class SXPBlueprintPersistenceTest {

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
		_persistence = SXPBlueprintUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SXPBlueprint> iterator = _sxpBlueprints.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SXPBlueprint sxpBlueprint = _persistence.create(pk);

		Assert.assertNotNull(sxpBlueprint);

		Assert.assertEquals(sxpBlueprint.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SXPBlueprint newSXPBlueprint = addSXPBlueprint();

		_persistence.remove(newSXPBlueprint);

		SXPBlueprint existingSXPBlueprint = _persistence.fetchByPrimaryKey(
			newSXPBlueprint.getPrimaryKey());

		Assert.assertNull(existingSXPBlueprint);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSXPBlueprint();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SXPBlueprint newSXPBlueprint = _persistence.create(pk);

		newSXPBlueprint.setMvccVersion(RandomTestUtil.nextLong());

		newSXPBlueprint.setUuid(RandomTestUtil.randomString());

		newSXPBlueprint.setExternalReferenceCode(RandomTestUtil.randomString());

		newSXPBlueprint.setCompanyId(RandomTestUtil.nextLong());

		newSXPBlueprint.setUserId(RandomTestUtil.nextLong());

		newSXPBlueprint.setUserName(RandomTestUtil.randomString());

		newSXPBlueprint.setCreateDate(RandomTestUtil.nextDate());

		newSXPBlueprint.setModifiedDate(RandomTestUtil.nextDate());

		newSXPBlueprint.setConfigurationJSON(RandomTestUtil.randomString());

		newSXPBlueprint.setDescription(RandomTestUtil.randomString());

		newSXPBlueprint.setElementInstancesJSON(RandomTestUtil.randomString());

		newSXPBlueprint.setSchemaVersion(RandomTestUtil.randomString());

		newSXPBlueprint.setTitle(RandomTestUtil.randomString());

		newSXPBlueprint.setVersion(RandomTestUtil.randomString());

		newSXPBlueprint.setStatus(RandomTestUtil.nextInt());

		newSXPBlueprint.setStatusByUserId(RandomTestUtil.nextLong());

		newSXPBlueprint.setStatusByUserName(RandomTestUtil.randomString());

		newSXPBlueprint.setStatusDate(RandomTestUtil.nextDate());

		_sxpBlueprints.add(_persistence.update(newSXPBlueprint));

		SXPBlueprint existingSXPBlueprint = _persistence.findByPrimaryKey(
			newSXPBlueprint.getPrimaryKey());

		Assert.assertEquals(
			existingSXPBlueprint.getMvccVersion(),
			newSXPBlueprint.getMvccVersion());
		Assert.assertEquals(
			existingSXPBlueprint.getUuid(), newSXPBlueprint.getUuid());
		Assert.assertEquals(
			existingSXPBlueprint.getExternalReferenceCode(),
			newSXPBlueprint.getExternalReferenceCode());
		Assert.assertEquals(
			existingSXPBlueprint.getSXPBlueprintId(),
			newSXPBlueprint.getSXPBlueprintId());
		Assert.assertEquals(
			existingSXPBlueprint.getCompanyId(),
			newSXPBlueprint.getCompanyId());
		Assert.assertEquals(
			existingSXPBlueprint.getUserId(), newSXPBlueprint.getUserId());
		Assert.assertEquals(
			existingSXPBlueprint.getUserName(), newSXPBlueprint.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSXPBlueprint.getCreateDate()),
			Time.getShortTimestamp(newSXPBlueprint.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSXPBlueprint.getModifiedDate()),
			Time.getShortTimestamp(newSXPBlueprint.getModifiedDate()));
		Assert.assertEquals(
			existingSXPBlueprint.getConfigurationJSON(),
			newSXPBlueprint.getConfigurationJSON());
		Assert.assertEquals(
			existingSXPBlueprint.getDescription(),
			newSXPBlueprint.getDescription());
		Assert.assertEquals(
			existingSXPBlueprint.getElementInstancesJSON(),
			newSXPBlueprint.getElementInstancesJSON());
		Assert.assertEquals(
			existingSXPBlueprint.getSchemaVersion(),
			newSXPBlueprint.getSchemaVersion());
		Assert.assertEquals(
			existingSXPBlueprint.getTitle(), newSXPBlueprint.getTitle());
		Assert.assertEquals(
			existingSXPBlueprint.getVersion(), newSXPBlueprint.getVersion());
		Assert.assertEquals(
			existingSXPBlueprint.getStatus(), newSXPBlueprint.getStatus());
		Assert.assertEquals(
			existingSXPBlueprint.getStatusByUserId(),
			newSXPBlueprint.getStatusByUserId());
		Assert.assertEquals(
			existingSXPBlueprint.getStatusByUserName(),
			newSXPBlueprint.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSXPBlueprint.getStatusDate()),
			Time.getShortTimestamp(newSXPBlueprint.getStatusDate()));
	}

	@Test(expected = DuplicateSXPBlueprintExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		SXPBlueprint sxpBlueprint = addSXPBlueprint();

		SXPBlueprint newSXPBlueprint = addSXPBlueprint();

		newSXPBlueprint.setCompanyId(sxpBlueprint.getCompanyId());

		newSXPBlueprint = _persistence.update(newSXPBlueprint);

		Session session = _persistence.getCurrentSession();

		session.evict(newSXPBlueprint);

		newSXPBlueprint.setExternalReferenceCode(
			sxpBlueprint.getExternalReferenceCode());

		_persistence.update(newSXPBlueprint);
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
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SXPBlueprint newSXPBlueprint = addSXPBlueprint();

		SXPBlueprint existingSXPBlueprint = _persistence.findByPrimaryKey(
			newSXPBlueprint.getPrimaryKey());

		Assert.assertEquals(existingSXPBlueprint, newSXPBlueprint);
	}

	@Test(expected = NoSuchSXPBlueprintException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SXPBlueprint> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SXPBlueprint", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "sxpBlueprintId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "description", true, "schemaVersion", true,
			"title", true, "version", true, "status", true, "statusByUserId",
			true, "statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SXPBlueprint newSXPBlueprint = addSXPBlueprint();

		SXPBlueprint existingSXPBlueprint = _persistence.fetchByPrimaryKey(
			newSXPBlueprint.getPrimaryKey());

		Assert.assertEquals(existingSXPBlueprint, newSXPBlueprint);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SXPBlueprint missingSXPBlueprint = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSXPBlueprint);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SXPBlueprint newSXPBlueprint1 = addSXPBlueprint();
		SXPBlueprint newSXPBlueprint2 = addSXPBlueprint();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSXPBlueprint1.getPrimaryKey());
		primaryKeys.add(newSXPBlueprint2.getPrimaryKey());

		Map<Serializable, SXPBlueprint> sxpBlueprints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, sxpBlueprints.size());
		Assert.assertEquals(
			newSXPBlueprint1,
			sxpBlueprints.get(newSXPBlueprint1.getPrimaryKey()));
		Assert.assertEquals(
			newSXPBlueprint2,
			sxpBlueprints.get(newSXPBlueprint2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SXPBlueprint> sxpBlueprints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sxpBlueprints.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SXPBlueprint newSXPBlueprint = addSXPBlueprint();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSXPBlueprint.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SXPBlueprint> sxpBlueprints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sxpBlueprints.size());
		Assert.assertEquals(
			newSXPBlueprint,
			sxpBlueprints.get(newSXPBlueprint.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SXPBlueprint> sxpBlueprints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sxpBlueprints.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SXPBlueprint newSXPBlueprint = addSXPBlueprint();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSXPBlueprint.getPrimaryKey());

		Map<Serializable, SXPBlueprint> sxpBlueprints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sxpBlueprints.size());
		Assert.assertEquals(
			newSXPBlueprint,
			sxpBlueprints.get(newSXPBlueprint.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SXPBlueprint newSXPBlueprint = addSXPBlueprint();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSXPBlueprint.getPrimaryKey()));
	}

	private void _assertOriginalValues(SXPBlueprint sxpBlueprint) {
		Assert.assertEquals(
			sxpBlueprint.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				sxpBlueprint, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(sxpBlueprint.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				sxpBlueprint, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected SXPBlueprint addSXPBlueprint() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SXPBlueprint sxpBlueprint = _persistence.create(pk);

		sxpBlueprint.setMvccVersion(RandomTestUtil.nextLong());

		sxpBlueprint.setUuid(RandomTestUtil.randomString());

		sxpBlueprint.setExternalReferenceCode(RandomTestUtil.randomString());

		sxpBlueprint.setCompanyId(RandomTestUtil.nextLong());

		sxpBlueprint.setUserId(RandomTestUtil.nextLong());

		sxpBlueprint.setUserName(RandomTestUtil.randomString());

		sxpBlueprint.setCreateDate(RandomTestUtil.nextDate());

		sxpBlueprint.setModifiedDate(RandomTestUtil.nextDate());

		sxpBlueprint.setConfigurationJSON(RandomTestUtil.randomString());

		sxpBlueprint.setDescription(RandomTestUtil.randomString());

		sxpBlueprint.setElementInstancesJSON(RandomTestUtil.randomString());

		sxpBlueprint.setSchemaVersion(RandomTestUtil.randomString());

		sxpBlueprint.setTitle(RandomTestUtil.randomString());

		sxpBlueprint.setVersion(RandomTestUtil.randomString());

		sxpBlueprint.setStatus(RandomTestUtil.nextInt());

		sxpBlueprint.setStatusByUserId(RandomTestUtil.nextLong());

		sxpBlueprint.setStatusByUserName(RandomTestUtil.randomString());

		sxpBlueprint.setStatusDate(RandomTestUtil.nextDate());

		_sxpBlueprints.add(_persistence.update(sxpBlueprint));

		return sxpBlueprint;
	}

	private List<SXPBlueprint> _sxpBlueprints = new ArrayList<SXPBlueprint>();
	private SXPBlueprintPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
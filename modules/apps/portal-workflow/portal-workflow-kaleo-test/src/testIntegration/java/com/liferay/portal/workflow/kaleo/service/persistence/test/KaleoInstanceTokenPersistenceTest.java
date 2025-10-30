/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.workflow.kaleo.exception.NoSuchInstanceTokenException;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstanceTokenPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstanceTokenUtil;

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
public class KaleoInstanceTokenPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.workflow.kaleo.service"));

	@Before
	public void setUp() {
		_persistence = KaleoInstanceTokenUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoInstanceToken> iterator = _kaleoInstanceTokens.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoInstanceToken kaleoInstanceToken = _persistence.create(pk);

		Assert.assertNotNull(kaleoInstanceToken);

		Assert.assertEquals(kaleoInstanceToken.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoInstanceToken newKaleoInstanceToken = addKaleoInstanceToken();

		_persistence.remove(newKaleoInstanceToken);

		KaleoInstanceToken existingKaleoInstanceToken =
			_persistence.fetchByPrimaryKey(
				newKaleoInstanceToken.getPrimaryKey());

		Assert.assertNull(existingKaleoInstanceToken);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoInstanceToken();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoInstanceToken newKaleoInstanceToken = _persistence.create(pk);

		newKaleoInstanceToken.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setGroupId(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setCompanyId(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setUserId(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setUserName(RandomTestUtil.randomString());

		newKaleoInstanceToken.setCreateDate(RandomTestUtil.nextDate());

		newKaleoInstanceToken.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoInstanceToken.setKaleoDefinitionId(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		newKaleoInstanceToken.setKaleoInstanceId(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setParentKaleoInstanceTokenId(
			RandomTestUtil.nextLong());

		newKaleoInstanceToken.setCurrentKaleoNodeId(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setCurrentKaleoNodeName(
			RandomTestUtil.randomString());

		newKaleoInstanceToken.setClassName(RandomTestUtil.randomString());

		newKaleoInstanceToken.setClassPK(RandomTestUtil.nextLong());

		newKaleoInstanceToken.setCompleted(RandomTestUtil.randomBoolean());

		newKaleoInstanceToken.setCompletionDate(RandomTestUtil.nextDate());

		_kaleoInstanceTokens.add(_persistence.update(newKaleoInstanceToken));

		KaleoInstanceToken existingKaleoInstanceToken =
			_persistence.findByPrimaryKey(
				newKaleoInstanceToken.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoInstanceToken.getMvccVersion(),
			newKaleoInstanceToken.getMvccVersion());
		Assert.assertEquals(
			existingKaleoInstanceToken.getCtCollectionId(),
			newKaleoInstanceToken.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getKaleoInstanceTokenId(),
			newKaleoInstanceToken.getKaleoInstanceTokenId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getGroupId(),
			newKaleoInstanceToken.getGroupId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getCompanyId(),
			newKaleoInstanceToken.getCompanyId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getUserId(),
			newKaleoInstanceToken.getUserId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getUserName(),
			newKaleoInstanceToken.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoInstanceToken.getCreateDate()),
			Time.getShortTimestamp(newKaleoInstanceToken.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoInstanceToken.getModifiedDate()),
			Time.getShortTimestamp(newKaleoInstanceToken.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoInstanceToken.getKaleoDefinitionId(),
			newKaleoInstanceToken.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getKaleoDefinitionVersionId(),
			newKaleoInstanceToken.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getKaleoInstanceId(),
			newKaleoInstanceToken.getKaleoInstanceId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getParentKaleoInstanceTokenId(),
			newKaleoInstanceToken.getParentKaleoInstanceTokenId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getCurrentKaleoNodeId(),
			newKaleoInstanceToken.getCurrentKaleoNodeId());
		Assert.assertEquals(
			existingKaleoInstanceToken.getCurrentKaleoNodeName(),
			newKaleoInstanceToken.getCurrentKaleoNodeName());
		Assert.assertEquals(
			existingKaleoInstanceToken.getClassName(),
			newKaleoInstanceToken.getClassName());
		Assert.assertEquals(
			existingKaleoInstanceToken.getClassPK(),
			newKaleoInstanceToken.getClassPK());
		Assert.assertEquals(
			existingKaleoInstanceToken.isCompleted(),
			newKaleoInstanceToken.isCompleted());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoInstanceToken.getCompletionDate()),
			Time.getShortTimestamp(newKaleoInstanceToken.getCompletionDate()));
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByKaleoDefinitionVersionId() throws Exception {
		_persistence.countByKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		_persistence.countByKaleoDefinitionVersionId(0L);
	}

	@Test
	public void testCountByKaleoInstanceId() throws Exception {
		_persistence.countByKaleoInstanceId(RandomTestUtil.nextLong());

		_persistence.countByKaleoInstanceId(0L);
	}

	@Test
	public void testCountByC_PKITI() throws Exception {
		_persistence.countByC_PKITI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_PKITI(0L, 0L);
	}

	@Test
	public void testCountByC_PKITI_CD() throws Exception {
		_persistence.countByC_PKITI_CD(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextDate());

		_persistence.countByC_PKITI_CD(0L, 0L, RandomTestUtil.nextDate());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoInstanceToken newKaleoInstanceToken = addKaleoInstanceToken();

		KaleoInstanceToken existingKaleoInstanceToken =
			_persistence.findByPrimaryKey(
				newKaleoInstanceToken.getPrimaryKey());

		Assert.assertEquals(existingKaleoInstanceToken, newKaleoInstanceToken);
	}

	@Test(expected = NoSuchInstanceTokenException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoInstanceToken> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoInstanceToken", "mvccVersion", true, "ctCollectionId", true,
			"kaleoInstanceTokenId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "kaleoDefinitionId", true,
			"kaleoDefinitionVersionId", true, "kaleoInstanceId", true,
			"parentKaleoInstanceTokenId", true, "currentKaleoNodeId", true,
			"currentKaleoNodeName", true, "className", true, "classPK", true,
			"completed", true, "completionDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoInstanceToken newKaleoInstanceToken = addKaleoInstanceToken();

		KaleoInstanceToken existingKaleoInstanceToken =
			_persistence.fetchByPrimaryKey(
				newKaleoInstanceToken.getPrimaryKey());

		Assert.assertEquals(existingKaleoInstanceToken, newKaleoInstanceToken);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoInstanceToken missingKaleoInstanceToken =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoInstanceToken);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoInstanceToken newKaleoInstanceToken1 = addKaleoInstanceToken();
		KaleoInstanceToken newKaleoInstanceToken2 = addKaleoInstanceToken();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoInstanceToken1.getPrimaryKey());
		primaryKeys.add(newKaleoInstanceToken2.getPrimaryKey());

		Map<Serializable, KaleoInstanceToken> kaleoInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoInstanceTokens.size());
		Assert.assertEquals(
			newKaleoInstanceToken1,
			kaleoInstanceTokens.get(newKaleoInstanceToken1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoInstanceToken2,
			kaleoInstanceTokens.get(newKaleoInstanceToken2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoInstanceToken> kaleoInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoInstanceTokens.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoInstanceToken newKaleoInstanceToken = addKaleoInstanceToken();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoInstanceToken.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoInstanceToken> kaleoInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoInstanceTokens.size());
		Assert.assertEquals(
			newKaleoInstanceToken,
			kaleoInstanceTokens.get(newKaleoInstanceToken.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoInstanceToken> kaleoInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoInstanceTokens.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoInstanceToken newKaleoInstanceToken = addKaleoInstanceToken();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoInstanceToken.getPrimaryKey());

		Map<Serializable, KaleoInstanceToken> kaleoInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoInstanceTokens.size());
		Assert.assertEquals(
			newKaleoInstanceToken,
			kaleoInstanceTokens.get(newKaleoInstanceToken.getPrimaryKey()));
	}

	protected KaleoInstanceToken addKaleoInstanceToken() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoInstanceToken kaleoInstanceToken = _persistence.create(pk);

		kaleoInstanceToken.setMvccVersion(RandomTestUtil.nextLong());

		kaleoInstanceToken.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoInstanceToken.setGroupId(RandomTestUtil.nextLong());

		kaleoInstanceToken.setCompanyId(RandomTestUtil.nextLong());

		kaleoInstanceToken.setUserId(RandomTestUtil.nextLong());

		kaleoInstanceToken.setUserName(RandomTestUtil.randomString());

		kaleoInstanceToken.setCreateDate(RandomTestUtil.nextDate());

		kaleoInstanceToken.setModifiedDate(RandomTestUtil.nextDate());

		kaleoInstanceToken.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoInstanceToken.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		kaleoInstanceToken.setKaleoInstanceId(RandomTestUtil.nextLong());

		kaleoInstanceToken.setParentKaleoInstanceTokenId(
			RandomTestUtil.nextLong());

		kaleoInstanceToken.setCurrentKaleoNodeId(RandomTestUtil.nextLong());

		kaleoInstanceToken.setCurrentKaleoNodeName(
			RandomTestUtil.randomString());

		kaleoInstanceToken.setClassName(RandomTestUtil.randomString());

		kaleoInstanceToken.setClassPK(RandomTestUtil.nextLong());

		kaleoInstanceToken.setCompleted(RandomTestUtil.randomBoolean());

		kaleoInstanceToken.setCompletionDate(RandomTestUtil.nextDate());

		_kaleoInstanceTokens.add(_persistence.update(kaleoInstanceToken));

		return kaleoInstanceToken;
	}

	private List<KaleoInstanceToken> _kaleoInstanceTokens =
		new ArrayList<KaleoInstanceToken>();
	private KaleoInstanceTokenPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
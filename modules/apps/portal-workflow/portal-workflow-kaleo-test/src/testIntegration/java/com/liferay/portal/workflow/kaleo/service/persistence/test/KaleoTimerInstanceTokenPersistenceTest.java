/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.workflow.kaleo.exception.NoSuchTimerInstanceTokenException;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTimerInstanceTokenPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTimerInstanceTokenUtil;

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
public class KaleoTimerInstanceTokenPersistenceTest {

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
		_persistence = KaleoTimerInstanceTokenUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoTimerInstanceToken> iterator =
			_kaleoTimerInstanceTokens.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTimerInstanceToken kaleoTimerInstanceToken = _persistence.create(
			pk);

		Assert.assertNotNull(kaleoTimerInstanceToken);

		Assert.assertEquals(kaleoTimerInstanceToken.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoTimerInstanceToken newKaleoTimerInstanceToken =
			addKaleoTimerInstanceToken();

		_persistence.remove(newKaleoTimerInstanceToken);

		KaleoTimerInstanceToken existingKaleoTimerInstanceToken =
			_persistence.fetchByPrimaryKey(
				newKaleoTimerInstanceToken.getPrimaryKey());

		Assert.assertNull(existingKaleoTimerInstanceToken);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoTimerInstanceToken();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTimerInstanceToken newKaleoTimerInstanceToken =
			_persistence.create(pk);

		newKaleoTimerInstanceToken.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setGroupId(RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setCompanyId(RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setUserId(RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setUserName(RandomTestUtil.randomString());

		newKaleoTimerInstanceToken.setCreateDate(RandomTestUtil.nextDate());

		newKaleoTimerInstanceToken.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoTimerInstanceToken.setKaleoClassName(
			RandomTestUtil.randomString());

		newKaleoTimerInstanceToken.setKaleoClassPK(RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setKaleoDefinitionId(
			RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setKaleoInstanceId(
			RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setKaleoInstanceTokenId(
			RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setKaleoTaskInstanceTokenId(
			RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setKaleoTimerId(RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setKaleoTimerName(
			RandomTestUtil.randomString());

		newKaleoTimerInstanceToken.setBlocking(RandomTestUtil.randomBoolean());

		newKaleoTimerInstanceToken.setCompletionUserId(
			RandomTestUtil.nextLong());

		newKaleoTimerInstanceToken.setCompleted(RandomTestUtil.randomBoolean());

		newKaleoTimerInstanceToken.setCompletionDate(RandomTestUtil.nextDate());

		newKaleoTimerInstanceToken.setWorkflowContext(
			RandomTestUtil.randomString());

		_kaleoTimerInstanceTokens.add(
			_persistence.update(newKaleoTimerInstanceToken));

		KaleoTimerInstanceToken existingKaleoTimerInstanceToken =
			_persistence.findByPrimaryKey(
				newKaleoTimerInstanceToken.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getMvccVersion(),
			newKaleoTimerInstanceToken.getMvccVersion());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getCtCollectionId(),
			newKaleoTimerInstanceToken.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoTimerInstanceTokenId(),
			newKaleoTimerInstanceToken.getKaleoTimerInstanceTokenId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getGroupId(),
			newKaleoTimerInstanceToken.getGroupId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getCompanyId(),
			newKaleoTimerInstanceToken.getCompanyId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getUserId(),
			newKaleoTimerInstanceToken.getUserId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getUserName(),
			newKaleoTimerInstanceToken.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoTimerInstanceToken.getCreateDate()),
			Time.getShortTimestamp(newKaleoTimerInstanceToken.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoTimerInstanceToken.getModifiedDate()),
			Time.getShortTimestamp(
				newKaleoTimerInstanceToken.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoClassName(),
			newKaleoTimerInstanceToken.getKaleoClassName());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoClassPK(),
			newKaleoTimerInstanceToken.getKaleoClassPK());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoDefinitionId(),
			newKaleoTimerInstanceToken.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoDefinitionVersionId(),
			newKaleoTimerInstanceToken.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoInstanceId(),
			newKaleoTimerInstanceToken.getKaleoInstanceId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoInstanceTokenId(),
			newKaleoTimerInstanceToken.getKaleoInstanceTokenId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoTaskInstanceTokenId(),
			newKaleoTimerInstanceToken.getKaleoTaskInstanceTokenId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoTimerId(),
			newKaleoTimerInstanceToken.getKaleoTimerId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getKaleoTimerName(),
			newKaleoTimerInstanceToken.getKaleoTimerName());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.isBlocking(),
			newKaleoTimerInstanceToken.isBlocking());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getCompletionUserId(),
			newKaleoTimerInstanceToken.getCompletionUserId());
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.isCompleted(),
			newKaleoTimerInstanceToken.isCompleted());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoTimerInstanceToken.getCompletionDate()),
			Time.getShortTimestamp(
				newKaleoTimerInstanceToken.getCompletionDate()));
		Assert.assertEquals(
			existingKaleoTimerInstanceToken.getWorkflowContext(),
			newKaleoTimerInstanceToken.getWorkflowContext());
	}

	@Test
	public void testCountByKaleoInstanceId() throws Exception {
		_persistence.countByKaleoInstanceId(RandomTestUtil.nextLong());

		_persistence.countByKaleoInstanceId(0L);
	}

	@Test
	public void testCountByKITI_KTI() throws Exception {
		_persistence.countByKITI_KTI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByKITI_KTI(0L, 0L);
	}

	@Test
	public void testCountByKITI_C() throws Exception {
		_persistence.countByKITI_C(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByKITI_C(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByKITI_B_C() throws Exception {
		_persistence.countByKITI_B_C(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByKITI_B_C(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoTimerInstanceToken newKaleoTimerInstanceToken =
			addKaleoTimerInstanceToken();

		KaleoTimerInstanceToken existingKaleoTimerInstanceToken =
			_persistence.findByPrimaryKey(
				newKaleoTimerInstanceToken.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTimerInstanceToken, newKaleoTimerInstanceToken);
	}

	@Test(expected = NoSuchTimerInstanceTokenException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoTimerInstanceToken>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"KaleoTimerInstanceToken", "mvccVersion", true, "ctCollectionId",
			true, "kaleoTimerInstanceTokenId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "kaleoClassName", true, "kaleoClassPK",
			true, "kaleoDefinitionId", true, "kaleoDefinitionVersionId", true,
			"kaleoInstanceId", true, "kaleoInstanceTokenId", true,
			"kaleoTaskInstanceTokenId", true, "kaleoTimerId", true,
			"kaleoTimerName", true, "blocking", true, "completionUserId", true,
			"completed", true, "completionDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoTimerInstanceToken newKaleoTimerInstanceToken =
			addKaleoTimerInstanceToken();

		KaleoTimerInstanceToken existingKaleoTimerInstanceToken =
			_persistence.fetchByPrimaryKey(
				newKaleoTimerInstanceToken.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTimerInstanceToken, newKaleoTimerInstanceToken);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTimerInstanceToken missingKaleoTimerInstanceToken =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoTimerInstanceToken);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoTimerInstanceToken newKaleoTimerInstanceToken1 =
			addKaleoTimerInstanceToken();
		KaleoTimerInstanceToken newKaleoTimerInstanceToken2 =
			addKaleoTimerInstanceToken();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTimerInstanceToken1.getPrimaryKey());
		primaryKeys.add(newKaleoTimerInstanceToken2.getPrimaryKey());

		Map<Serializable, KaleoTimerInstanceToken> kaleoTimerInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoTimerInstanceTokens.size());
		Assert.assertEquals(
			newKaleoTimerInstanceToken1,
			kaleoTimerInstanceTokens.get(
				newKaleoTimerInstanceToken1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoTimerInstanceToken2,
			kaleoTimerInstanceTokens.get(
				newKaleoTimerInstanceToken2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoTimerInstanceToken> kaleoTimerInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTimerInstanceTokens.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoTimerInstanceToken newKaleoTimerInstanceToken =
			addKaleoTimerInstanceToken();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTimerInstanceToken.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoTimerInstanceToken> kaleoTimerInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTimerInstanceTokens.size());
		Assert.assertEquals(
			newKaleoTimerInstanceToken,
			kaleoTimerInstanceTokens.get(
				newKaleoTimerInstanceToken.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoTimerInstanceToken> kaleoTimerInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTimerInstanceTokens.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoTimerInstanceToken newKaleoTimerInstanceToken =
			addKaleoTimerInstanceToken();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTimerInstanceToken.getPrimaryKey());

		Map<Serializable, KaleoTimerInstanceToken> kaleoTimerInstanceTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTimerInstanceTokens.size());
		Assert.assertEquals(
			newKaleoTimerInstanceToken,
			kaleoTimerInstanceTokens.get(
				newKaleoTimerInstanceToken.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KaleoTimerInstanceToken newKaleoTimerInstanceToken =
			addKaleoTimerInstanceToken();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newKaleoTimerInstanceToken.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		KaleoTimerInstanceToken kaleoTimerInstanceToken) {

		Assert.assertEquals(
			Long.valueOf(kaleoTimerInstanceToken.getKaleoInstanceTokenId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoTimerInstanceToken, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "kaleoInstanceTokenId"));
		Assert.assertEquals(
			Long.valueOf(kaleoTimerInstanceToken.getKaleoTimerId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoTimerInstanceToken, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "kaleoTimerId"));
	}

	protected KaleoTimerInstanceToken addKaleoTimerInstanceToken()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		KaleoTimerInstanceToken kaleoTimerInstanceToken = _persistence.create(
			pk);

		kaleoTimerInstanceToken.setMvccVersion(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setGroupId(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setCompanyId(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setUserId(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setUserName(RandomTestUtil.randomString());

		kaleoTimerInstanceToken.setCreateDate(RandomTestUtil.nextDate());

		kaleoTimerInstanceToken.setModifiedDate(RandomTestUtil.nextDate());

		kaleoTimerInstanceToken.setKaleoClassName(
			RandomTestUtil.randomString());

		kaleoTimerInstanceToken.setKaleoClassPK(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setKaleoInstanceId(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setKaleoInstanceTokenId(
			RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setKaleoTaskInstanceTokenId(
			RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setKaleoTimerId(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setKaleoTimerName(
			RandomTestUtil.randomString());

		kaleoTimerInstanceToken.setBlocking(RandomTestUtil.randomBoolean());

		kaleoTimerInstanceToken.setCompletionUserId(RandomTestUtil.nextLong());

		kaleoTimerInstanceToken.setCompleted(RandomTestUtil.randomBoolean());

		kaleoTimerInstanceToken.setCompletionDate(RandomTestUtil.nextDate());

		kaleoTimerInstanceToken.setWorkflowContext(
			RandomTestUtil.randomString());

		_kaleoTimerInstanceTokens.add(
			_persistence.update(kaleoTimerInstanceToken));

		return kaleoTimerInstanceToken;
	}

	private List<KaleoTimerInstanceToken> _kaleoTimerInstanceTokens =
		new ArrayList<KaleoTimerInstanceToken>();
	private KaleoTimerInstanceTokenPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
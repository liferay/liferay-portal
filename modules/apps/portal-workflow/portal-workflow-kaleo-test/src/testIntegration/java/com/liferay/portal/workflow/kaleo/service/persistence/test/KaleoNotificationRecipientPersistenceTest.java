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
import com.liferay.portal.workflow.kaleo.exception.NoSuchNotificationRecipientException;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNotificationRecipientPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNotificationRecipientUtil;

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
public class KaleoNotificationRecipientPersistenceTest {

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
		_persistence = KaleoNotificationRecipientUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoNotificationRecipient> iterator =
			_kaleoNotificationRecipients.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNotificationRecipient kaleoNotificationRecipient =
			_persistence.create(pk);

		Assert.assertNotNull(kaleoNotificationRecipient);

		Assert.assertEquals(kaleoNotificationRecipient.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoNotificationRecipient newKaleoNotificationRecipient =
			addKaleoNotificationRecipient();

		_persistence.remove(newKaleoNotificationRecipient);

		KaleoNotificationRecipient existingKaleoNotificationRecipient =
			_persistence.fetchByPrimaryKey(
				newKaleoNotificationRecipient.getPrimaryKey());

		Assert.assertNull(existingKaleoNotificationRecipient);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoNotificationRecipient();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNotificationRecipient newKaleoNotificationRecipient =
			_persistence.create(pk);

		newKaleoNotificationRecipient.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setCtCollectionId(
			RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setGroupId(RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setCompanyId(RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setUserId(RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setUserName(
			RandomTestUtil.randomString());

		newKaleoNotificationRecipient.setCreateDate(RandomTestUtil.nextDate());

		newKaleoNotificationRecipient.setModifiedDate(
			RandomTestUtil.nextDate());

		newKaleoNotificationRecipient.setKaleoDefinitionId(
			RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setKaleoNotificationId(
			RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setRecipientClassName(
			RandomTestUtil.randomString());

		newKaleoNotificationRecipient.setRecipientClassPK(
			RandomTestUtil.nextLong());

		newKaleoNotificationRecipient.setRecipientRoleType(
			RandomTestUtil.nextInt());

		newKaleoNotificationRecipient.setRecipientScript(
			RandomTestUtil.randomString());

		newKaleoNotificationRecipient.setRecipientScriptLanguage(
			RandomTestUtil.randomString());

		newKaleoNotificationRecipient.setRecipientScriptContexts(
			RandomTestUtil.randomString());

		newKaleoNotificationRecipient.setAddress(RandomTestUtil.randomString());

		newKaleoNotificationRecipient.setNotificationReceptionType(
			RandomTestUtil.randomString(3));

		_kaleoNotificationRecipients.add(
			_persistence.update(newKaleoNotificationRecipient));

		KaleoNotificationRecipient existingKaleoNotificationRecipient =
			_persistence.findByPrimaryKey(
				newKaleoNotificationRecipient.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoNotificationRecipient.getMvccVersion(),
			newKaleoNotificationRecipient.getMvccVersion());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getCtCollectionId(),
			newKaleoNotificationRecipient.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.
				getKaleoNotificationRecipientId(),
			newKaleoNotificationRecipient.getKaleoNotificationRecipientId());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getGroupId(),
			newKaleoNotificationRecipient.getGroupId());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getCompanyId(),
			newKaleoNotificationRecipient.getCompanyId());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getUserId(),
			newKaleoNotificationRecipient.getUserId());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getUserName(),
			newKaleoNotificationRecipient.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoNotificationRecipient.getCreateDate()),
			Time.getShortTimestamp(
				newKaleoNotificationRecipient.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoNotificationRecipient.getModifiedDate()),
			Time.getShortTimestamp(
				newKaleoNotificationRecipient.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getKaleoDefinitionId(),
			newKaleoNotificationRecipient.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getKaleoDefinitionVersionId(),
			newKaleoNotificationRecipient.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getKaleoNotificationId(),
			newKaleoNotificationRecipient.getKaleoNotificationId());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getRecipientClassName(),
			newKaleoNotificationRecipient.getRecipientClassName());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getRecipientClassPK(),
			newKaleoNotificationRecipient.getRecipientClassPK());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getRecipientRoleType(),
			newKaleoNotificationRecipient.getRecipientRoleType());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getRecipientScript(),
			newKaleoNotificationRecipient.getRecipientScript());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getRecipientScriptLanguage(),
			newKaleoNotificationRecipient.getRecipientScriptLanguage());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getRecipientScriptContexts(),
			newKaleoNotificationRecipient.getRecipientScriptContexts());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getAddress(),
			newKaleoNotificationRecipient.getAddress());
		Assert.assertEquals(
			existingKaleoNotificationRecipient.getNotificationReceptionType(),
			newKaleoNotificationRecipient.getNotificationReceptionType());
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
	public void testCountByKaleoNotificationId() throws Exception {
		_persistence.countByKaleoNotificationId(RandomTestUtil.nextLong());

		_persistence.countByKaleoNotificationId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoNotificationRecipient newKaleoNotificationRecipient =
			addKaleoNotificationRecipient();

		KaleoNotificationRecipient existingKaleoNotificationRecipient =
			_persistence.findByPrimaryKey(
				newKaleoNotificationRecipient.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoNotificationRecipient, newKaleoNotificationRecipient);
	}

	@Test(expected = NoSuchNotificationRecipientException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoNotificationRecipient>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"KaleoNotificationRecipient", "mvccVersion", true, "ctCollectionId",
			true, "kaleoNotificationRecipientId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "kaleoDefinitionId", true,
			"kaleoDefinitionVersionId", true, "kaleoNotificationId", true,
			"recipientClassName", true, "recipientClassPK", true,
			"recipientRoleType", true, "recipientScriptLanguage", true,
			"recipientScriptContexts", true, "address", true,
			"notificationReceptionType", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoNotificationRecipient newKaleoNotificationRecipient =
			addKaleoNotificationRecipient();

		KaleoNotificationRecipient existingKaleoNotificationRecipient =
			_persistence.fetchByPrimaryKey(
				newKaleoNotificationRecipient.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoNotificationRecipient, newKaleoNotificationRecipient);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNotificationRecipient missingKaleoNotificationRecipient =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoNotificationRecipient);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoNotificationRecipient newKaleoNotificationRecipient1 =
			addKaleoNotificationRecipient();
		KaleoNotificationRecipient newKaleoNotificationRecipient2 =
			addKaleoNotificationRecipient();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNotificationRecipient1.getPrimaryKey());
		primaryKeys.add(newKaleoNotificationRecipient2.getPrimaryKey());

		Map<Serializable, KaleoNotificationRecipient>
			kaleoNotificationRecipients = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, kaleoNotificationRecipients.size());
		Assert.assertEquals(
			newKaleoNotificationRecipient1,
			kaleoNotificationRecipients.get(
				newKaleoNotificationRecipient1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoNotificationRecipient2,
			kaleoNotificationRecipients.get(
				newKaleoNotificationRecipient2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoNotificationRecipient>
			kaleoNotificationRecipients = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(kaleoNotificationRecipients.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoNotificationRecipient newKaleoNotificationRecipient =
			addKaleoNotificationRecipient();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNotificationRecipient.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoNotificationRecipient>
			kaleoNotificationRecipients = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, kaleoNotificationRecipients.size());
		Assert.assertEquals(
			newKaleoNotificationRecipient,
			kaleoNotificationRecipients.get(
				newKaleoNotificationRecipient.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoNotificationRecipient>
			kaleoNotificationRecipients = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(kaleoNotificationRecipients.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoNotificationRecipient newKaleoNotificationRecipient =
			addKaleoNotificationRecipient();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNotificationRecipient.getPrimaryKey());

		Map<Serializable, KaleoNotificationRecipient>
			kaleoNotificationRecipients = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, kaleoNotificationRecipients.size());
		Assert.assertEquals(
			newKaleoNotificationRecipient,
			kaleoNotificationRecipients.get(
				newKaleoNotificationRecipient.getPrimaryKey()));
	}

	protected KaleoNotificationRecipient addKaleoNotificationRecipient()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		KaleoNotificationRecipient kaleoNotificationRecipient =
			_persistence.create(pk);

		kaleoNotificationRecipient.setMvccVersion(RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setGroupId(RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setCompanyId(RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setUserId(RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setUserName(RandomTestUtil.randomString());

		kaleoNotificationRecipient.setCreateDate(RandomTestUtil.nextDate());

		kaleoNotificationRecipient.setModifiedDate(RandomTestUtil.nextDate());

		kaleoNotificationRecipient.setKaleoDefinitionId(
			RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setKaleoNotificationId(
			RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setRecipientClassName(
			RandomTestUtil.randomString());

		kaleoNotificationRecipient.setRecipientClassPK(
			RandomTestUtil.nextLong());

		kaleoNotificationRecipient.setRecipientRoleType(
			RandomTestUtil.nextInt());

		kaleoNotificationRecipient.setRecipientScript(
			RandomTestUtil.randomString());

		kaleoNotificationRecipient.setRecipientScriptLanguage(
			RandomTestUtil.randomString());

		kaleoNotificationRecipient.setRecipientScriptContexts(
			RandomTestUtil.randomString());

		kaleoNotificationRecipient.setAddress(RandomTestUtil.randomString());

		kaleoNotificationRecipient.setNotificationReceptionType(
			RandomTestUtil.randomString(3));

		_kaleoNotificationRecipients.add(
			_persistence.update(kaleoNotificationRecipient));

		return kaleoNotificationRecipient;
	}

	private List<KaleoNotificationRecipient> _kaleoNotificationRecipients =
		new ArrayList<KaleoNotificationRecipient>();
	private KaleoNotificationRecipientPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
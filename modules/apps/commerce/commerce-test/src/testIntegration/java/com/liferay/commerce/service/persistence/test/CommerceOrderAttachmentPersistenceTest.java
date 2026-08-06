/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.DuplicateCommerceOrderAttachmentExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderAttachmentException;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalServiceUtil;
import com.liferay.commerce.service.persistence.CommerceOrderAttachmentPersistence;
import com.liferay.commerce.service.persistence.CommerceOrderAttachmentUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class CommerceOrderAttachmentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceOrderAttachmentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceOrderAttachment> iterator =
			_commerceOrderAttachments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderAttachment commerceOrderAttachment = _persistence.create(
			pk);

		Assert.assertNotNull(commerceOrderAttachment);

		Assert.assertEquals(commerceOrderAttachment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		_persistence.remove(newCommerceOrderAttachment);

		CommerceOrderAttachment existingCommerceOrderAttachment =
			_persistence.fetchByPrimaryKey(
				newCommerceOrderAttachment.getPrimaryKey());

		Assert.assertNull(existingCommerceOrderAttachment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceOrderAttachment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		newCommerceOrderAttachment.setUuid(RandomTestUtil.randomString());

		newCommerceOrderAttachment.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceOrderAttachment.setGroupId(RandomTestUtil.nextLong());

		newCommerceOrderAttachment.setCompanyId(RandomTestUtil.nextLong());

		newCommerceOrderAttachment.setUserId(RandomTestUtil.nextLong());

		newCommerceOrderAttachment.setUserName(RandomTestUtil.randomString());

		newCommerceOrderAttachment.setCreateDate(RandomTestUtil.nextDate());

		newCommerceOrderAttachment.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceOrderAttachment.setCommerceOrderId(
			RandomTestUtil.nextLong());

		newCommerceOrderAttachment.setFileEntryId(RandomTestUtil.nextLong());

		newCommerceOrderAttachment.setPriority(RandomTestUtil.nextDouble());

		newCommerceOrderAttachment.setRestricted(
			RandomTestUtil.randomBoolean());

		newCommerceOrderAttachment.setTitle(RandomTestUtil.randomString());

		newCommerceOrderAttachment.setType(RandomTestUtil.randomString());

		newCommerceOrderAttachment = _persistence.update(
			newCommerceOrderAttachment);

		_commerceOrderAttachments.add(newCommerceOrderAttachment);

		CommerceOrderAttachment existingCommerceOrderAttachment =
			_persistence.findByPrimaryKey(
				newCommerceOrderAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrderAttachment.getMvccVersion(),
			newCommerceOrderAttachment.getMvccVersion());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getUuid(),
			newCommerceOrderAttachment.getUuid());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getExternalReferenceCode(),
			newCommerceOrderAttachment.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getCommerceOrderAttachmentId(),
			newCommerceOrderAttachment.getCommerceOrderAttachmentId());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getGroupId(),
			newCommerceOrderAttachment.getGroupId());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getCompanyId(),
			newCommerceOrderAttachment.getCompanyId());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getUserId(),
			newCommerceOrderAttachment.getUserId());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getUserName(),
			newCommerceOrderAttachment.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceOrderAttachment.getCreateDate()),
			Time.getShortTimestamp(newCommerceOrderAttachment.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceOrderAttachment.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceOrderAttachment.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceOrderAttachment.getCommerceOrderId(),
			newCommerceOrderAttachment.getCommerceOrderId());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getFileEntryId(),
			newCommerceOrderAttachment.getFileEntryId());
		AssertUtils.assertEquals(
			existingCommerceOrderAttachment.getPriority(),
			newCommerceOrderAttachment.getPriority());
		Assert.assertEquals(
			existingCommerceOrderAttachment.isRestricted(),
			newCommerceOrderAttachment.isRestricted());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getTitle(),
			newCommerceOrderAttachment.getTitle());
		Assert.assertEquals(
			existingCommerceOrderAttachment.getType(),
			newCommerceOrderAttachment.getType());
	}

	@Test(
		expected = DuplicateCommerceOrderAttachmentExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceOrderAttachment commerceOrderAttachment =
			addCommerceOrderAttachment();

		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		newCommerceOrderAttachment.setCompanyId(
			commerceOrderAttachment.getCompanyId());

		newCommerceOrderAttachment = _persistence.update(
			newCommerceOrderAttachment);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceOrderAttachment);

		newCommerceOrderAttachment.setExternalReferenceCode(
			commerceOrderAttachment.getExternalReferenceCode());

		_persistence.update(newCommerceOrderAttachment);
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
	public void testCountByCommerceOrderId() throws Exception {
		_persistence.countByCommerceOrderId(RandomTestUtil.nextLong());

		_persistence.countByCommerceOrderId(0L);
	}

	@Test
	public void testCountByC_R() throws Exception {
		_persistence.countByC_R(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_R(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		CommerceOrderAttachment existingCommerceOrderAttachment =
			_persistence.findByPrimaryKey(
				newCommerceOrderAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrderAttachment, newCommerceOrderAttachment);
	}

	@Test(expected = NoSuchOrderAttachmentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceOrderAttachment>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceOrderAttachment", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceOrderAttachmentId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "commerceOrderId",
			true, "fileEntryId", true, "priority", true, "restricted", true,
			"title", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		CommerceOrderAttachment existingCommerceOrderAttachment =
			_persistence.fetchByPrimaryKey(
				newCommerceOrderAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrderAttachment, newCommerceOrderAttachment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderAttachment missingCommerceOrderAttachment =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceOrderAttachment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceOrderAttachment newCommerceOrderAttachment1 =
			addCommerceOrderAttachment();
		CommerceOrderAttachment newCommerceOrderAttachment2 =
			addCommerceOrderAttachment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderAttachment1.getPrimaryKey());
		primaryKeys.add(newCommerceOrderAttachment2.getPrimaryKey());

		Map<Serializable, CommerceOrderAttachment> commerceOrderAttachments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceOrderAttachments.size());
		Assert.assertEquals(
			newCommerceOrderAttachment1,
			commerceOrderAttachments.get(
				newCommerceOrderAttachment1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceOrderAttachment2,
			commerceOrderAttachments.get(
				newCommerceOrderAttachment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceOrderAttachment> commerceOrderAttachments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrderAttachments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderAttachment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceOrderAttachment> commerceOrderAttachments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrderAttachments.size());
		Assert.assertEquals(
			newCommerceOrderAttachment,
			commerceOrderAttachments.get(
				newCommerceOrderAttachment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceOrderAttachment> commerceOrderAttachments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrderAttachments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderAttachment.getPrimaryKey());

		Map<Serializable, CommerceOrderAttachment> commerceOrderAttachments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrderAttachments.size());
		Assert.assertEquals(
			newCommerceOrderAttachment,
			commerceOrderAttachments.get(
				newCommerceOrderAttachment.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommerceOrderAttachmentLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommerceOrderAttachment>() {

				@Override
				public void performAction(
					CommerceOrderAttachment commerceOrderAttachment) {

					Assert.assertNotNull(commerceOrderAttachment);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderAttachment.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceOrderAttachmentId",
				newCommerceOrderAttachment.getCommerceOrderAttachmentId()));

		List<CommerceOrderAttachment> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommerceOrderAttachment existingCommerceOrderAttachment = result.get(0);

		Assert.assertEquals(
			existingCommerceOrderAttachment, newCommerceOrderAttachment);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderAttachment.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceOrderAttachmentId", RandomTestUtil.nextLong()));

		List<CommerceOrderAttachment> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderAttachment.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceOrderAttachmentId"));

		Object newCommerceOrderAttachmentId =
			newCommerceOrderAttachment.getCommerceOrderAttachmentId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceOrderAttachmentId",
				new Object[] {newCommerceOrderAttachmentId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommerceOrderAttachmentId = result.get(0);

		Assert.assertEquals(
			existingCommerceOrderAttachmentId, newCommerceOrderAttachmentId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderAttachment.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceOrderAttachmentId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceOrderAttachmentId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceOrderAttachment.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		CommerceOrderAttachment newCommerceOrderAttachment =
			addCommerceOrderAttachment();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceOrderAttachment.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceOrderAttachmentId",
				newCommerceOrderAttachment.getCommerceOrderAttachmentId()));

		List<CommerceOrderAttachment> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CommerceOrderAttachment commerceOrderAttachment) {

		Assert.assertEquals(
			commerceOrderAttachment.getUuid(),
			ReflectionTestUtil.invoke(
				commerceOrderAttachment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(commerceOrderAttachment.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceOrderAttachment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			commerceOrderAttachment.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceOrderAttachment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceOrderAttachment.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceOrderAttachment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceOrderAttachment addCommerceOrderAttachment()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceOrderAttachment commerceOrderAttachment = _persistence.create(
			pk);

		commerceOrderAttachment.setUuid(RandomTestUtil.randomString());

		commerceOrderAttachment.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceOrderAttachment.setGroupId(RandomTestUtil.nextLong());

		commerceOrderAttachment.setCompanyId(RandomTestUtil.nextLong());

		commerceOrderAttachment.setUserId(RandomTestUtil.nextLong());

		commerceOrderAttachment.setUserName(RandomTestUtil.randomString());

		commerceOrderAttachment.setCreateDate(RandomTestUtil.nextDate());

		commerceOrderAttachment.setModifiedDate(RandomTestUtil.nextDate());

		commerceOrderAttachment.setCommerceOrderId(RandomTestUtil.nextLong());

		commerceOrderAttachment.setFileEntryId(RandomTestUtil.nextLong());

		commerceOrderAttachment.setPriority(RandomTestUtil.nextDouble());

		commerceOrderAttachment.setRestricted(RandomTestUtil.randomBoolean());

		commerceOrderAttachment.setTitle(RandomTestUtil.randomString());

		commerceOrderAttachment.setType(RandomTestUtil.randomString());

		_commerceOrderAttachments.add(
			_persistence.update(commerceOrderAttachment));

		return commerceOrderAttachment;
	}

	private List<CommerceOrderAttachment> _commerceOrderAttachments =
		new ArrayList<CommerceOrderAttachment>();
	private CommerceOrderAttachmentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1559361306
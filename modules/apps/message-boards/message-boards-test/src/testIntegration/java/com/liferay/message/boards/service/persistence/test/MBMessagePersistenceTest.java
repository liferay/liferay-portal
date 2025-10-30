/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.exception.DuplicateMBMessageExternalReferenceCodeException;
import com.liferay.message.boards.exception.NoSuchMessageException;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.persistence.MBMessagePersistence;
import com.liferay.message.boards.service.persistence.MBMessageUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class MBMessagePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.message.boards.service"));

	@Before
	public void setUp() {
		_persistence = MBMessageUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MBMessage> iterator = _mbMessages.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBMessage mbMessage = _persistence.create(pk);

		Assert.assertNotNull(mbMessage);

		Assert.assertEquals(mbMessage.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MBMessage newMBMessage = addMBMessage();

		_persistence.remove(newMBMessage);

		MBMessage existingMBMessage = _persistence.fetchByPrimaryKey(
			newMBMessage.getPrimaryKey());

		Assert.assertNull(existingMBMessage);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMBMessage();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBMessage newMBMessage = _persistence.create(pk);

		newMBMessage.setMvccVersion(RandomTestUtil.nextLong());

		newMBMessage.setCtCollectionId(RandomTestUtil.nextLong());

		newMBMessage.setUuid(RandomTestUtil.randomString());

		newMBMessage.setExternalReferenceCode(RandomTestUtil.randomString());

		newMBMessage.setGroupId(RandomTestUtil.nextLong());

		newMBMessage.setCompanyId(RandomTestUtil.nextLong());

		newMBMessage.setUserId(RandomTestUtil.nextLong());

		newMBMessage.setUserName(RandomTestUtil.randomString());

		newMBMessage.setCreateDate(RandomTestUtil.nextDate());

		newMBMessage.setModifiedDate(RandomTestUtil.nextDate());

		newMBMessage.setClassNameId(RandomTestUtil.nextLong());

		newMBMessage.setClassPK(RandomTestUtil.nextLong());

		newMBMessage.setCategoryId(RandomTestUtil.nextLong());

		newMBMessage.setThreadId(RandomTestUtil.nextLong());

		newMBMessage.setRootMessageId(RandomTestUtil.nextLong());

		newMBMessage.setParentMessageId(RandomTestUtil.nextLong());

		newMBMessage.setTreePath(RandomTestUtil.randomString());

		newMBMessage.setSubject(RandomTestUtil.randomString());

		newMBMessage.setUrlSubject(RandomTestUtil.randomString());

		newMBMessage.setBody(RandomTestUtil.randomString());

		newMBMessage.setFormat(RandomTestUtil.randomString());

		newMBMessage.setAnonymous(RandomTestUtil.randomBoolean());

		newMBMessage.setPriority(RandomTestUtil.nextDouble());

		newMBMessage.setAllowPingbacks(RandomTestUtil.randomBoolean());

		newMBMessage.setAnswer(RandomTestUtil.randomBoolean());

		newMBMessage.setLastPublishDate(RandomTestUtil.nextDate());

		newMBMessage.setStatus(RandomTestUtil.nextInt());

		newMBMessage.setStatusByUserId(RandomTestUtil.nextLong());

		newMBMessage.setStatusByUserName(RandomTestUtil.randomString());

		newMBMessage.setStatusDate(RandomTestUtil.nextDate());

		_mbMessages.add(_persistence.update(newMBMessage));

		MBMessage existingMBMessage = _persistence.findByPrimaryKey(
			newMBMessage.getPrimaryKey());

		Assert.assertEquals(
			existingMBMessage.getMvccVersion(), newMBMessage.getMvccVersion());
		Assert.assertEquals(
			existingMBMessage.getCtCollectionId(),
			newMBMessage.getCtCollectionId());
		Assert.assertEquals(
			existingMBMessage.getUuid(), newMBMessage.getUuid());
		Assert.assertEquals(
			existingMBMessage.getExternalReferenceCode(),
			newMBMessage.getExternalReferenceCode());
		Assert.assertEquals(
			existingMBMessage.getMessageId(), newMBMessage.getMessageId());
		Assert.assertEquals(
			existingMBMessage.getGroupId(), newMBMessage.getGroupId());
		Assert.assertEquals(
			existingMBMessage.getCompanyId(), newMBMessage.getCompanyId());
		Assert.assertEquals(
			existingMBMessage.getUserId(), newMBMessage.getUserId());
		Assert.assertEquals(
			existingMBMessage.getUserName(), newMBMessage.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBMessage.getCreateDate()),
			Time.getShortTimestamp(newMBMessage.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBMessage.getModifiedDate()),
			Time.getShortTimestamp(newMBMessage.getModifiedDate()));
		Assert.assertEquals(
			existingMBMessage.getClassNameId(), newMBMessage.getClassNameId());
		Assert.assertEquals(
			existingMBMessage.getClassPK(), newMBMessage.getClassPK());
		Assert.assertEquals(
			existingMBMessage.getCategoryId(), newMBMessage.getCategoryId());
		Assert.assertEquals(
			existingMBMessage.getThreadId(), newMBMessage.getThreadId());
		Assert.assertEquals(
			existingMBMessage.getRootMessageId(),
			newMBMessage.getRootMessageId());
		Assert.assertEquals(
			existingMBMessage.getParentMessageId(),
			newMBMessage.getParentMessageId());
		Assert.assertEquals(
			existingMBMessage.getTreePath(), newMBMessage.getTreePath());
		Assert.assertEquals(
			existingMBMessage.getSubject(), newMBMessage.getSubject());
		Assert.assertEquals(
			existingMBMessage.getUrlSubject(), newMBMessage.getUrlSubject());
		Assert.assertEquals(
			existingMBMessage.getBody(), newMBMessage.getBody());
		Assert.assertEquals(
			existingMBMessage.getFormat(), newMBMessage.getFormat());
		Assert.assertEquals(
			existingMBMessage.isAnonymous(), newMBMessage.isAnonymous());
		AssertUtils.assertEquals(
			existingMBMessage.getPriority(), newMBMessage.getPriority());
		Assert.assertEquals(
			existingMBMessage.isAllowPingbacks(),
			newMBMessage.isAllowPingbacks());
		Assert.assertEquals(
			existingMBMessage.isAnswer(), newMBMessage.isAnswer());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBMessage.getLastPublishDate()),
			Time.getShortTimestamp(newMBMessage.getLastPublishDate()));
		Assert.assertEquals(
			existingMBMessage.getStatus(), newMBMessage.getStatus());
		Assert.assertEquals(
			existingMBMessage.getStatusByUserId(),
			newMBMessage.getStatusByUserId());
		Assert.assertEquals(
			existingMBMessage.getStatusByUserName(),
			newMBMessage.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBMessage.getStatusDate()),
			Time.getShortTimestamp(newMBMessage.getStatusDate()));
	}

	@Test(expected = DuplicateMBMessageExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		MBMessage mbMessage = addMBMessage();

		MBMessage newMBMessage = addMBMessage();

		newMBMessage.setGroupId(mbMessage.getGroupId());

		newMBMessage = _persistence.update(newMBMessage);

		Session session = _persistence.getCurrentSession();

		session.evict(newMBMessage);

		newMBMessage.setExternalReferenceCode(
			mbMessage.getExternalReferenceCode());

		_persistence.update(newMBMessage);
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
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
	public void testCountByThreadId() throws Exception {
		_persistence.countByThreadId(RandomTestUtil.nextLong());

		_persistence.countByThreadId(0L);
	}

	@Test
	public void testCountByThreadIdReplies() throws Exception {
		_persistence.countByThreadIdReplies(RandomTestUtil.nextLong());

		_persistence.countByThreadIdReplies(0L);
	}

	@Test
	public void testCountByParentMessageId() throws Exception {
		_persistence.countByParentMessageId(RandomTestUtil.nextLong());

		_persistence.countByParentMessageId(0L);
	}

	@Test
	public void testCountByG_U() throws Exception {
		_persistence.countByG_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_U(0L, 0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_US() throws Exception {
		_persistence.countByG_US(RandomTestUtil.nextLong(), "");

		_persistence.countByG_US(0L, "null");

		_persistence.countByG_US(0L, (String)null);
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByU_C() throws Exception {
		_persistence.countByU_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_C(0L, 0L);
	}

	@Test
	public void testCountByU_CArrayable() throws Exception {
		_persistence.countByU_C(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByT_P() throws Exception {
		_persistence.countByT_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByT_P(0L, 0L);
	}

	@Test
	public void testCountByT_A() throws Exception {
		_persistence.countByT_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByT_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByT_S() throws Exception {
		_persistence.countByT_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByT_S(0L, 0);
	}

	@Test
	public void testCountByT_NotS() throws Exception {
		_persistence.countByT_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByT_NotS(0L, 0);
	}

	@Test
	public void testCountByTR_S() throws Exception {
		_persistence.countByTR_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByTR_S(0L, 0);
	}

	@Test
	public void testCountByP_S() throws Exception {
		_persistence.countByP_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByP_S(0L, 0);
	}

	@Test
	public void testCountByG_U_S() throws Exception {
		_persistence.countByG_U_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_U_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_T() throws Exception {
		_persistence.countByG_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_T(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_C_S() throws Exception {
		_persistence.countByG_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_S(0L, 0L, 0);
	}

	@Test
	public void testCountByU_C_C() throws Exception {
		_persistence.countByU_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByU_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByU_C_S() throws Exception {
		_persistence.countByU_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByU_C_S(0L, 0L, 0);
	}

	@Test
	public void testCountByU_C_SArrayable() throws Exception {
		_persistence.countByU_C_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByC_C_S() throws Exception {
		_persistence.countByC_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByC_C_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_T_A() throws Exception {
		_persistence.countByG_C_T_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_C_T_A(0L, 0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_C_T_S() throws Exception {
		_persistence.countByG_C_T_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_C_T_S(0L, 0L, 0L, 0);
	}

	@Test
	public void testCountByU_C_C_S() throws Exception {
		_persistence.countByU_C_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByU_C_C_S(0L, 0L, 0L, 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MBMessage newMBMessage = addMBMessage();

		MBMessage existingMBMessage = _persistence.findByPrimaryKey(
			newMBMessage.getPrimaryKey());

		Assert.assertEquals(existingMBMessage, newMBMessage);
	}

	@Test(expected = NoSuchMessageException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<MBMessage> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"MBMessage", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "messageId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "classNameId", true,
			"classPK", true, "categoryId", true, "threadId", true,
			"rootMessageId", true, "parentMessageId", true, "treePath", true,
			"subject", true, "urlSubject", true, "format", true, "anonymous",
			true, "priority", true, "allowPingbacks", true, "answer", true,
			"lastPublishDate", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MBMessage newMBMessage = addMBMessage();

		MBMessage existingMBMessage = _persistence.fetchByPrimaryKey(
			newMBMessage.getPrimaryKey());

		Assert.assertEquals(existingMBMessage, newMBMessage);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBMessage missingMBMessage = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMBMessage);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MBMessage newMBMessage1 = addMBMessage();
		MBMessage newMBMessage2 = addMBMessage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBMessage1.getPrimaryKey());
		primaryKeys.add(newMBMessage2.getPrimaryKey());

		Map<Serializable, MBMessage> mbMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, mbMessages.size());
		Assert.assertEquals(
			newMBMessage1, mbMessages.get(newMBMessage1.getPrimaryKey()));
		Assert.assertEquals(
			newMBMessage2, mbMessages.get(newMBMessage2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MBMessage> mbMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbMessages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MBMessage newMBMessage = addMBMessage();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBMessage.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MBMessage> mbMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbMessages.size());
		Assert.assertEquals(
			newMBMessage, mbMessages.get(newMBMessage.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MBMessage> mbMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbMessages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MBMessage newMBMessage = addMBMessage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBMessage.getPrimaryKey());

		Map<Serializable, MBMessage> mbMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbMessages.size());
		Assert.assertEquals(
			newMBMessage, mbMessages.get(newMBMessage.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		MBMessage newMBMessage = addMBMessage();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newMBMessage.getPrimaryKey()));
	}

	private void _assertOriginalValues(MBMessage mbMessage) {
		Assert.assertEquals(
			mbMessage.getUuid(),
			ReflectionTestUtil.invoke(
				mbMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(mbMessage.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(mbMessage.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			mbMessage.getUrlSubject(),
			ReflectionTestUtil.invoke(
				mbMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "urlSubject"));

		Assert.assertEquals(
			mbMessage.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				mbMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(mbMessage.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected MBMessage addMBMessage() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBMessage mbMessage = _persistence.create(pk);

		mbMessage.setMvccVersion(RandomTestUtil.nextLong());

		mbMessage.setCtCollectionId(RandomTestUtil.nextLong());

		mbMessage.setUuid(RandomTestUtil.randomString());

		mbMessage.setExternalReferenceCode(RandomTestUtil.randomString());

		mbMessage.setGroupId(RandomTestUtil.nextLong());

		mbMessage.setCompanyId(RandomTestUtil.nextLong());

		mbMessage.setUserId(RandomTestUtil.nextLong());

		mbMessage.setUserName(RandomTestUtil.randomString());

		mbMessage.setCreateDate(RandomTestUtil.nextDate());

		mbMessage.setModifiedDate(RandomTestUtil.nextDate());

		mbMessage.setClassNameId(RandomTestUtil.nextLong());

		mbMessage.setClassPK(RandomTestUtil.nextLong());

		mbMessage.setCategoryId(RandomTestUtil.nextLong());

		mbMessage.setThreadId(RandomTestUtil.nextLong());

		mbMessage.setRootMessageId(RandomTestUtil.nextLong());

		mbMessage.setParentMessageId(RandomTestUtil.nextLong());

		mbMessage.setTreePath(RandomTestUtil.randomString());

		mbMessage.setSubject(RandomTestUtil.randomString());

		mbMessage.setUrlSubject(RandomTestUtil.randomString());

		mbMessage.setBody(RandomTestUtil.randomString());

		mbMessage.setFormat(RandomTestUtil.randomString());

		mbMessage.setAnonymous(RandomTestUtil.randomBoolean());

		mbMessage.setPriority(RandomTestUtil.nextDouble());

		mbMessage.setAllowPingbacks(RandomTestUtil.randomBoolean());

		mbMessage.setAnswer(RandomTestUtil.randomBoolean());

		mbMessage.setLastPublishDate(RandomTestUtil.nextDate());

		mbMessage.setStatus(RandomTestUtil.nextInt());

		mbMessage.setStatusByUserId(RandomTestUtil.nextLong());

		mbMessage.setStatusByUserName(RandomTestUtil.randomString());

		mbMessage.setStatusDate(RandomTestUtil.nextDate());

		_mbMessages.add(_persistence.update(mbMessage));

		return mbMessage;
	}

	private List<MBMessage> _mbMessages = new ArrayList<MBMessage>();
	private MBMessagePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
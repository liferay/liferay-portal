/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.audit.storage.exception.NoSuchEventException;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalServiceUtil;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventPersistence;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventUtil;
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
public class AuditEventPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.security.audit.storage.service"));

	@Before
	public void setUp() {
		_persistence = AuditEventUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AuditEvent> iterator = _auditEvents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AuditEvent auditEvent = _persistence.create(pk);

		Assert.assertNotNull(auditEvent);

		Assert.assertEquals(auditEvent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AuditEvent newAuditEvent = addAuditEvent();

		_persistence.remove(newAuditEvent);

		AuditEvent existingAuditEvent = _persistence.fetchByPrimaryKey(
			newAuditEvent.getPrimaryKey());

		Assert.assertNull(existingAuditEvent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAuditEvent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AuditEvent newAuditEvent = addAuditEvent();

		newAuditEvent.setGroupId(RandomTestUtil.nextLong());

		newAuditEvent.setCompanyId(RandomTestUtil.nextLong());

		newAuditEvent.setUserId(RandomTestUtil.nextLong());

		newAuditEvent.setUserName(RandomTestUtil.randomString());

		newAuditEvent.setCreateDate(RandomTestUtil.nextDate());

		newAuditEvent.setAccountEntryId(RandomTestUtil.nextLong());

		newAuditEvent.setAdditionalInfo(RandomTestUtil.randomString());

		newAuditEvent.setClassName(RandomTestUtil.randomString());

		newAuditEvent.setClassPK(RandomTestUtil.randomString());

		newAuditEvent.setClientHost(RandomTestUtil.randomString());

		newAuditEvent.setClientIP(RandomTestUtil.randomString());

		newAuditEvent.setContextName(RandomTestUtil.randomString());

		newAuditEvent.setCorrelationId(RandomTestUtil.randomString());

		newAuditEvent.setEventType(RandomTestUtil.randomString());

		newAuditEvent.setHttpMethod(RandomTestUtil.randomString());

		newAuditEvent.setImpersonated(RandomTestUtil.randomBoolean());

		newAuditEvent.setImpersonatedUserEmailAddress(
			RandomTestUtil.randomString());

		newAuditEvent.setImpersonatedUserId(RandomTestUtil.nextLong());

		newAuditEvent.setImpersonatedUserName(RandomTestUtil.randomString());

		newAuditEvent.setMessage(RandomTestUtil.randomString());

		newAuditEvent.setObjectName(RandomTestUtil.randomString());

		newAuditEvent.setRequestId(RandomTestUtil.randomString());

		newAuditEvent.setRequestIdGenerated(RandomTestUtil.randomBoolean());

		newAuditEvent.setResourceAction(RandomTestUtil.randomString());

		newAuditEvent.setResourceType(RandomTestUtil.randomString());

		newAuditEvent.setRoles(RandomTestUtil.randomString());

		newAuditEvent.setServerName(RandomTestUtil.randomString());

		newAuditEvent.setServerPort(RandomTestUtil.nextInt());

		newAuditEvent.setSessionID(RandomTestUtil.randomString());

		newAuditEvent.setUserAgent(RandomTestUtil.randomString());

		newAuditEvent.setUserEmailAddress(RandomTestUtil.randomString());

		newAuditEvent = _persistence.update(newAuditEvent);

		_auditEvents.add(newAuditEvent);

		AuditEvent existingAuditEvent = _persistence.findByPrimaryKey(
			newAuditEvent.getPrimaryKey());

		Assert.assertEquals(
			existingAuditEvent.getAuditEventId(),
			newAuditEvent.getAuditEventId());
		Assert.assertEquals(
			existingAuditEvent.getGroupId(), newAuditEvent.getGroupId());
		Assert.assertEquals(
			existingAuditEvent.getCompanyId(), newAuditEvent.getCompanyId());
		Assert.assertEquals(
			existingAuditEvent.getUserId(), newAuditEvent.getUserId());
		Assert.assertEquals(
			existingAuditEvent.getUserName(), newAuditEvent.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAuditEvent.getCreateDate()),
			Time.getShortTimestamp(newAuditEvent.getCreateDate()));
		Assert.assertEquals(
			existingAuditEvent.getAccountEntryId(),
			newAuditEvent.getAccountEntryId());
		Assert.assertEquals(
			existingAuditEvent.getAdditionalInfo(),
			newAuditEvent.getAdditionalInfo());
		Assert.assertEquals(
			existingAuditEvent.getClassName(), newAuditEvent.getClassName());
		Assert.assertEquals(
			existingAuditEvent.getClassPK(), newAuditEvent.getClassPK());
		Assert.assertEquals(
			existingAuditEvent.getClientHost(), newAuditEvent.getClientHost());
		Assert.assertEquals(
			existingAuditEvent.getClientIP(), newAuditEvent.getClientIP());
		Assert.assertEquals(
			existingAuditEvent.getContextName(),
			newAuditEvent.getContextName());
		Assert.assertEquals(
			existingAuditEvent.getCorrelationId(),
			newAuditEvent.getCorrelationId());
		Assert.assertEquals(
			existingAuditEvent.getEventType(), newAuditEvent.getEventType());
		Assert.assertEquals(
			existingAuditEvent.getHttpMethod(), newAuditEvent.getHttpMethod());
		Assert.assertEquals(
			existingAuditEvent.isImpersonated(),
			newAuditEvent.isImpersonated());
		Assert.assertEquals(
			existingAuditEvent.getImpersonatedUserEmailAddress(),
			newAuditEvent.getImpersonatedUserEmailAddress());
		Assert.assertEquals(
			existingAuditEvent.getImpersonatedUserId(),
			newAuditEvent.getImpersonatedUserId());
		Assert.assertEquals(
			existingAuditEvent.getImpersonatedUserName(),
			newAuditEvent.getImpersonatedUserName());
		Assert.assertEquals(
			existingAuditEvent.getMessage(), newAuditEvent.getMessage());
		Assert.assertEquals(
			existingAuditEvent.getObjectName(), newAuditEvent.getObjectName());
		Assert.assertEquals(
			existingAuditEvent.getRequestId(), newAuditEvent.getRequestId());
		Assert.assertEquals(
			existingAuditEvent.isRequestIdGenerated(),
			newAuditEvent.isRequestIdGenerated());
		Assert.assertEquals(
			existingAuditEvent.getResourceAction(),
			newAuditEvent.getResourceAction());
		Assert.assertEquals(
			existingAuditEvent.getResourceType(),
			newAuditEvent.getResourceType());
		Assert.assertEquals(
			existingAuditEvent.getRoles(), newAuditEvent.getRoles());
		Assert.assertEquals(
			existingAuditEvent.getServerName(), newAuditEvent.getServerName());
		Assert.assertEquals(
			existingAuditEvent.getServerPort(), newAuditEvent.getServerPort());
		Assert.assertEquals(
			existingAuditEvent.getSessionID(), newAuditEvent.getSessionID());
		Assert.assertEquals(
			existingAuditEvent.getUserAgent(), newAuditEvent.getUserAgent());
		Assert.assertEquals(
			existingAuditEvent.getUserEmailAddress(),
			newAuditEvent.getUserEmailAddress());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AuditEvent newAuditEvent = addAuditEvent();

		AuditEvent existingAuditEvent = _persistence.findByPrimaryKey(
			newAuditEvent.getPrimaryKey());

		Assert.assertEquals(existingAuditEvent, newAuditEvent);
	}

	@Test(expected = NoSuchEventException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AuditEvent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Audit_AuditEvent", "auditEventId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "accountEntryId", true, "className", true, "classPK", true,
			"clientHost", true, "clientIP", true, "contextName", true,
			"correlationId", true, "eventType", true, "httpMethod", true,
			"impersonated", true, "impersonatedUserEmailAddress", true,
			"impersonatedUserId", true, "impersonatedUserName", true, "message",
			true, "objectName", true, "requestId", true, "requestIdGenerated",
			true, "resourceAction", true, "resourceType", true, "serverName",
			true, "serverPort", true, "sessionID", true, "userAgent", true,
			"userEmailAddress", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AuditEvent newAuditEvent = addAuditEvent();

		AuditEvent existingAuditEvent = _persistence.fetchByPrimaryKey(
			newAuditEvent.getPrimaryKey());

		Assert.assertEquals(existingAuditEvent, newAuditEvent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AuditEvent missingAuditEvent = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAuditEvent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AuditEvent newAuditEvent1 = addAuditEvent();
		AuditEvent newAuditEvent2 = addAuditEvent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAuditEvent1.getPrimaryKey());
		primaryKeys.add(newAuditEvent2.getPrimaryKey());

		Map<Serializable, AuditEvent> auditEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, auditEvents.size());
		Assert.assertEquals(
			newAuditEvent1, auditEvents.get(newAuditEvent1.getPrimaryKey()));
		Assert.assertEquals(
			newAuditEvent2, auditEvents.get(newAuditEvent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AuditEvent> auditEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(auditEvents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AuditEvent newAuditEvent = addAuditEvent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAuditEvent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AuditEvent> auditEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, auditEvents.size());
		Assert.assertEquals(
			newAuditEvent, auditEvents.get(newAuditEvent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AuditEvent> auditEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(auditEvents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AuditEvent newAuditEvent = addAuditEvent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAuditEvent.getPrimaryKey());

		Map<Serializable, AuditEvent> auditEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, auditEvents.size());
		Assert.assertEquals(
			newAuditEvent, auditEvents.get(newAuditEvent.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			AuditEventLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<AuditEvent>() {

				@Override
				public void performAction(AuditEvent auditEvent) {
					Assert.assertNotNull(auditEvent);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AuditEvent newAuditEvent = addAuditEvent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AuditEvent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"auditEventId", newAuditEvent.getAuditEventId()));

		List<AuditEvent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		AuditEvent existingAuditEvent = result.get(0);

		Assert.assertEquals(existingAuditEvent, newAuditEvent);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AuditEvent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"auditEventId", RandomTestUtil.nextLong()));

		List<AuditEvent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AuditEvent newAuditEvent = addAuditEvent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AuditEvent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("auditEventId"));

		Object newAuditEventId = newAuditEvent.getAuditEventId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"auditEventId", new Object[] {newAuditEventId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAuditEventId = result.get(0);

		Assert.assertEquals(existingAuditEventId, newAuditEventId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AuditEvent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("auditEventId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"auditEventId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected AuditEvent addAuditEvent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AuditEvent auditEvent = _persistence.create(pk);

		auditEvent.setGroupId(RandomTestUtil.nextLong());

		auditEvent.setCompanyId(RandomTestUtil.nextLong());

		auditEvent.setUserId(RandomTestUtil.nextLong());

		auditEvent.setUserName(RandomTestUtil.randomString());

		auditEvent.setCreateDate(RandomTestUtil.nextDate());

		auditEvent.setAccountEntryId(RandomTestUtil.nextLong());

		auditEvent.setAdditionalInfo(RandomTestUtil.randomString());

		auditEvent.setClassName(RandomTestUtil.randomString());

		auditEvent.setClassPK(RandomTestUtil.randomString());

		auditEvent.setClientHost(RandomTestUtil.randomString());

		auditEvent.setClientIP(RandomTestUtil.randomString());

		auditEvent.setContextName(RandomTestUtil.randomString());

		auditEvent.setCorrelationId(RandomTestUtil.randomString());

		auditEvent.setEventType(RandomTestUtil.randomString());

		auditEvent.setHttpMethod(RandomTestUtil.randomString());

		auditEvent.setImpersonated(RandomTestUtil.randomBoolean());

		auditEvent.setImpersonatedUserEmailAddress(
			RandomTestUtil.randomString());

		auditEvent.setImpersonatedUserId(RandomTestUtil.nextLong());

		auditEvent.setImpersonatedUserName(RandomTestUtil.randomString());

		auditEvent.setMessage(RandomTestUtil.randomString());

		auditEvent.setObjectName(RandomTestUtil.randomString());

		auditEvent.setRequestId(RandomTestUtil.randomString());

		auditEvent.setRequestIdGenerated(RandomTestUtil.randomBoolean());

		auditEvent.setResourceAction(RandomTestUtil.randomString());

		auditEvent.setResourceType(RandomTestUtil.randomString());

		auditEvent.setRoles(RandomTestUtil.randomString());

		auditEvent.setServerName(RandomTestUtil.randomString());

		auditEvent.setServerPort(RandomTestUtil.nextInt());

		auditEvent.setSessionID(RandomTestUtil.randomString());

		auditEvent.setUserAgent(RandomTestUtil.randomString());

		auditEvent.setUserEmailAddress(RandomTestUtil.randomString());

		_auditEvents.add(_persistence.update(auditEvent));

		return auditEvent;
	}

	private List<AuditEvent> _auditEvents = new ArrayList<AuditEvent>();
	private AuditEventPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1262735458
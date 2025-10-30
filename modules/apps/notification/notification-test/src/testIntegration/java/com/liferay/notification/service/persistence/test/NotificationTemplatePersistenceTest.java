/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.exception.DuplicateNotificationTemplateExternalReferenceCodeException;
import com.liferay.notification.exception.NoSuchNotificationTemplateException;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.persistence.NotificationTemplatePersistence;
import com.liferay.notification.service.persistence.NotificationTemplateUtil;
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
public class NotificationTemplatePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.notification.service"));

	@Before
	public void setUp() {
		_persistence = NotificationTemplateUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NotificationTemplate> iterator =
			_notificationTemplates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationTemplate notificationTemplate = _persistence.create(pk);

		Assert.assertNotNull(notificationTemplate);

		Assert.assertEquals(notificationTemplate.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NotificationTemplate newNotificationTemplate =
			addNotificationTemplate();

		_persistence.remove(newNotificationTemplate);

		NotificationTemplate existingNotificationTemplate =
			_persistence.fetchByPrimaryKey(
				newNotificationTemplate.getPrimaryKey());

		Assert.assertNull(existingNotificationTemplate);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNotificationTemplate();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationTemplate newNotificationTemplate = _persistence.create(pk);

		newNotificationTemplate.setMvccVersion(RandomTestUtil.nextLong());

		newNotificationTemplate.setUuid(RandomTestUtil.randomString());

		newNotificationTemplate.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newNotificationTemplate.setCompanyId(RandomTestUtil.nextLong());

		newNotificationTemplate.setUserId(RandomTestUtil.nextLong());

		newNotificationTemplate.setUserName(RandomTestUtil.randomString());

		newNotificationTemplate.setCreateDate(RandomTestUtil.nextDate());

		newNotificationTemplate.setModifiedDate(RandomTestUtil.nextDate());

		newNotificationTemplate.setObjectDefinitionId(
			RandomTestUtil.nextLong());

		newNotificationTemplate.setBody(RandomTestUtil.randomString());

		newNotificationTemplate.setDescription(RandomTestUtil.randomString());

		newNotificationTemplate.setEditorType(RandomTestUtil.randomString());

		newNotificationTemplate.setName(RandomTestUtil.randomString());

		newNotificationTemplate.setRecipientType(RandomTestUtil.randomString());

		newNotificationTemplate.setSubject(RandomTestUtil.randomString());

		newNotificationTemplate.setSystem(RandomTestUtil.randomBoolean());

		newNotificationTemplate.setType(RandomTestUtil.randomString());

		_notificationTemplates.add(
			_persistence.update(newNotificationTemplate));

		NotificationTemplate existingNotificationTemplate =
			_persistence.findByPrimaryKey(
				newNotificationTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationTemplate.getMvccVersion(),
			newNotificationTemplate.getMvccVersion());
		Assert.assertEquals(
			existingNotificationTemplate.getUuid(),
			newNotificationTemplate.getUuid());
		Assert.assertEquals(
			existingNotificationTemplate.getExternalReferenceCode(),
			newNotificationTemplate.getExternalReferenceCode());
		Assert.assertEquals(
			existingNotificationTemplate.getNotificationTemplateId(),
			newNotificationTemplate.getNotificationTemplateId());
		Assert.assertEquals(
			existingNotificationTemplate.getCompanyId(),
			newNotificationTemplate.getCompanyId());
		Assert.assertEquals(
			existingNotificationTemplate.getUserId(),
			newNotificationTemplate.getUserId());
		Assert.assertEquals(
			existingNotificationTemplate.getUserName(),
			newNotificationTemplate.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationTemplate.getCreateDate()),
			Time.getShortTimestamp(newNotificationTemplate.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationTemplate.getModifiedDate()),
			Time.getShortTimestamp(newNotificationTemplate.getModifiedDate()));
		Assert.assertEquals(
			existingNotificationTemplate.getObjectDefinitionId(),
			newNotificationTemplate.getObjectDefinitionId());
		Assert.assertEquals(
			existingNotificationTemplate.getBody(),
			newNotificationTemplate.getBody());
		Assert.assertEquals(
			existingNotificationTemplate.getDescription(),
			newNotificationTemplate.getDescription());
		Assert.assertEquals(
			existingNotificationTemplate.getEditorType(),
			newNotificationTemplate.getEditorType());
		Assert.assertEquals(
			existingNotificationTemplate.getName(),
			newNotificationTemplate.getName());
		Assert.assertEquals(
			existingNotificationTemplate.getRecipientType(),
			newNotificationTemplate.getRecipientType());
		Assert.assertEquals(
			existingNotificationTemplate.getSubject(),
			newNotificationTemplate.getSubject());
		Assert.assertEquals(
			existingNotificationTemplate.isSystem(),
			newNotificationTemplate.isSystem());
		Assert.assertEquals(
			existingNotificationTemplate.getType(),
			newNotificationTemplate.getType());
	}

	@Test(
		expected = DuplicateNotificationTemplateExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		NotificationTemplate notificationTemplate = addNotificationTemplate();

		NotificationTemplate newNotificationTemplate =
			addNotificationTemplate();

		newNotificationTemplate.setCompanyId(
			notificationTemplate.getCompanyId());

		newNotificationTemplate = _persistence.update(newNotificationTemplate);

		Session session = _persistence.getCurrentSession();

		session.evict(newNotificationTemplate);

		newNotificationTemplate.setExternalReferenceCode(
			notificationTemplate.getExternalReferenceCode());

		_persistence.update(newNotificationTemplate);
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
		NotificationTemplate newNotificationTemplate =
			addNotificationTemplate();

		NotificationTemplate existingNotificationTemplate =
			_persistence.findByPrimaryKey(
				newNotificationTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationTemplate, newNotificationTemplate);
	}

	@Test(expected = NoSuchNotificationTemplateException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NotificationTemplate> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"NotificationTemplate", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "notificationTemplateId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "objectDefinitionId", true,
			"description", true, "editorType", true, "name", true,
			"recipientType", true, "subject", true, "system", true, "type",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NotificationTemplate newNotificationTemplate =
			addNotificationTemplate();

		NotificationTemplate existingNotificationTemplate =
			_persistence.fetchByPrimaryKey(
				newNotificationTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationTemplate, newNotificationTemplate);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationTemplate missingNotificationTemplate =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingNotificationTemplate);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NotificationTemplate newNotificationTemplate1 =
			addNotificationTemplate();
		NotificationTemplate newNotificationTemplate2 =
			addNotificationTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationTemplate1.getPrimaryKey());
		primaryKeys.add(newNotificationTemplate2.getPrimaryKey());

		Map<Serializable, NotificationTemplate> notificationTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, notificationTemplates.size());
		Assert.assertEquals(
			newNotificationTemplate1,
			notificationTemplates.get(
				newNotificationTemplate1.getPrimaryKey()));
		Assert.assertEquals(
			newNotificationTemplate2,
			notificationTemplates.get(
				newNotificationTemplate2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NotificationTemplate> notificationTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(notificationTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NotificationTemplate newNotificationTemplate =
			addNotificationTemplate();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationTemplate.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NotificationTemplate> notificationTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, notificationTemplates.size());
		Assert.assertEquals(
			newNotificationTemplate,
			notificationTemplates.get(newNotificationTemplate.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NotificationTemplate> notificationTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(notificationTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NotificationTemplate newNotificationTemplate =
			addNotificationTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationTemplate.getPrimaryKey());

		Map<Serializable, NotificationTemplate> notificationTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, notificationTemplates.size());
		Assert.assertEquals(
			newNotificationTemplate,
			notificationTemplates.get(newNotificationTemplate.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		NotificationTemplate newNotificationTemplate =
			addNotificationTemplate();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newNotificationTemplate.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		NotificationTemplate notificationTemplate) {

		Assert.assertEquals(
			notificationTemplate.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				notificationTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(notificationTemplate.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				notificationTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected NotificationTemplate addNotificationTemplate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationTemplate notificationTemplate = _persistence.create(pk);

		notificationTemplate.setMvccVersion(RandomTestUtil.nextLong());

		notificationTemplate.setUuid(RandomTestUtil.randomString());

		notificationTemplate.setExternalReferenceCode(
			RandomTestUtil.randomString());

		notificationTemplate.setCompanyId(RandomTestUtil.nextLong());

		notificationTemplate.setUserId(RandomTestUtil.nextLong());

		notificationTemplate.setUserName(RandomTestUtil.randomString());

		notificationTemplate.setCreateDate(RandomTestUtil.nextDate());

		notificationTemplate.setModifiedDate(RandomTestUtil.nextDate());

		notificationTemplate.setObjectDefinitionId(RandomTestUtil.nextLong());

		notificationTemplate.setBody(RandomTestUtil.randomString());

		notificationTemplate.setDescription(RandomTestUtil.randomString());

		notificationTemplate.setEditorType(RandomTestUtil.randomString());

		notificationTemplate.setName(RandomTestUtil.randomString());

		notificationTemplate.setRecipientType(RandomTestUtil.randomString());

		notificationTemplate.setSubject(RandomTestUtil.randomString());

		notificationTemplate.setSystem(RandomTestUtil.randomBoolean());

		notificationTemplate.setType(RandomTestUtil.randomString());

		_notificationTemplates.add(_persistence.update(notificationTemplate));

		return notificationTemplate;
	}

	private List<NotificationTemplate> _notificationTemplates =
		new ArrayList<NotificationTemplate>();
	private NotificationTemplatePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
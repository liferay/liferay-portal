/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.DuplicateDDMTemplateExternalReferenceCodeException;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplatePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
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
public class DDMTemplatePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMTemplateUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMTemplate> iterator = _ddmTemplates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplate ddmTemplate = _persistence.create(pk);

		Assert.assertNotNull(ddmTemplate);

		Assert.assertEquals(ddmTemplate.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		_persistence.remove(newDDMTemplate);

		DDMTemplate existingDDMTemplate = _persistence.fetchByPrimaryKey(
			newDDMTemplate.getPrimaryKey());

		Assert.assertNull(existingDDMTemplate);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMTemplate();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplate newDDMTemplate = _persistence.create(pk);

		newDDMTemplate.setMvccVersion(RandomTestUtil.nextLong());

		newDDMTemplate.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMTemplate.setUuid(RandomTestUtil.randomString());

		newDDMTemplate.setExternalReferenceCode(RandomTestUtil.randomString());

		newDDMTemplate.setGroupId(RandomTestUtil.nextLong());

		newDDMTemplate.setCompanyId(RandomTestUtil.nextLong());

		newDDMTemplate.setUserId(RandomTestUtil.nextLong());

		newDDMTemplate.setUserName(RandomTestUtil.randomString());

		newDDMTemplate.setVersionUserId(RandomTestUtil.nextLong());

		newDDMTemplate.setVersionUserName(RandomTestUtil.randomString());

		newDDMTemplate.setCreateDate(RandomTestUtil.nextDate());

		newDDMTemplate.setModifiedDate(RandomTestUtil.nextDate());

		newDDMTemplate.setClassNameId(RandomTestUtil.nextLong());

		newDDMTemplate.setClassPK(RandomTestUtil.nextLong());

		newDDMTemplate.setResourceClassNameId(RandomTestUtil.nextLong());

		newDDMTemplate.setTemplateKey(RandomTestUtil.randomString());

		newDDMTemplate.setVersion(RandomTestUtil.randomString());

		newDDMTemplate.setName(RandomTestUtil.randomString());

		newDDMTemplate.setDescription(RandomTestUtil.randomString());

		newDDMTemplate.setType(RandomTestUtil.randomString());

		newDDMTemplate.setMode(RandomTestUtil.randomString());

		newDDMTemplate.setLanguage(RandomTestUtil.randomString());

		newDDMTemplate.setScript(RandomTestUtil.randomString());

		newDDMTemplate.setCacheable(RandomTestUtil.randomBoolean());

		newDDMTemplate.setSmallImage(RandomTestUtil.randomBoolean());

		newDDMTemplate.setSmallImageId(RandomTestUtil.nextLong());

		newDDMTemplate.setSmallImageURL(RandomTestUtil.randomString());

		newDDMTemplate.setLastPublishDate(RandomTestUtil.nextDate());

		_ddmTemplates.add(_persistence.update(newDDMTemplate));

		DDMTemplate existingDDMTemplate = _persistence.findByPrimaryKey(
			newDDMTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingDDMTemplate.getMvccVersion(),
			newDDMTemplate.getMvccVersion());
		Assert.assertEquals(
			existingDDMTemplate.getCtCollectionId(),
			newDDMTemplate.getCtCollectionId());
		Assert.assertEquals(
			existingDDMTemplate.getUuid(), newDDMTemplate.getUuid());
		Assert.assertEquals(
			existingDDMTemplate.getExternalReferenceCode(),
			newDDMTemplate.getExternalReferenceCode());
		Assert.assertEquals(
			existingDDMTemplate.getTemplateId(),
			newDDMTemplate.getTemplateId());
		Assert.assertEquals(
			existingDDMTemplate.getGroupId(), newDDMTemplate.getGroupId());
		Assert.assertEquals(
			existingDDMTemplate.getCompanyId(), newDDMTemplate.getCompanyId());
		Assert.assertEquals(
			existingDDMTemplate.getUserId(), newDDMTemplate.getUserId());
		Assert.assertEquals(
			existingDDMTemplate.getUserName(), newDDMTemplate.getUserName());
		Assert.assertEquals(
			existingDDMTemplate.getVersionUserId(),
			newDDMTemplate.getVersionUserId());
		Assert.assertEquals(
			existingDDMTemplate.getVersionUserName(),
			newDDMTemplate.getVersionUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMTemplate.getCreateDate()),
			Time.getShortTimestamp(newDDMTemplate.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMTemplate.getModifiedDate()),
			Time.getShortTimestamp(newDDMTemplate.getModifiedDate()));
		Assert.assertEquals(
			existingDDMTemplate.getClassNameId(),
			newDDMTemplate.getClassNameId());
		Assert.assertEquals(
			existingDDMTemplate.getClassPK(), newDDMTemplate.getClassPK());
		Assert.assertEquals(
			existingDDMTemplate.getResourceClassNameId(),
			newDDMTemplate.getResourceClassNameId());
		Assert.assertEquals(
			existingDDMTemplate.getTemplateKey(),
			newDDMTemplate.getTemplateKey());
		Assert.assertEquals(
			existingDDMTemplate.getVersion(), newDDMTemplate.getVersion());
		Assert.assertEquals(
			existingDDMTemplate.getName(), newDDMTemplate.getName());
		Assert.assertEquals(
			existingDDMTemplate.getDescription(),
			newDDMTemplate.getDescription());
		Assert.assertEquals(
			existingDDMTemplate.getType(), newDDMTemplate.getType());
		Assert.assertEquals(
			existingDDMTemplate.getMode(), newDDMTemplate.getMode());
		Assert.assertEquals(
			existingDDMTemplate.getLanguage(), newDDMTemplate.getLanguage());
		Assert.assertEquals(
			existingDDMTemplate.getScript(), newDDMTemplate.getScript());
		Assert.assertEquals(
			existingDDMTemplate.isCacheable(), newDDMTemplate.isCacheable());
		Assert.assertEquals(
			existingDDMTemplate.isSmallImage(), newDDMTemplate.isSmallImage());
		Assert.assertEquals(
			existingDDMTemplate.getSmallImageId(),
			newDDMTemplate.getSmallImageId());
		Assert.assertEquals(
			existingDDMTemplate.getSmallImageURL(),
			newDDMTemplate.getSmallImageURL());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMTemplate.getLastPublishDate()),
			Time.getShortTimestamp(newDDMTemplate.getLastPublishDate()));
	}

	@Test(expected = DuplicateDDMTemplateExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		DDMTemplate ddmTemplate = addDDMTemplate();

		DDMTemplate newDDMTemplate = addDDMTemplate();

		newDDMTemplate.setGroupId(ddmTemplate.getGroupId());

		newDDMTemplate = _persistence.update(newDDMTemplate);

		Session session = _persistence.getCurrentSession();

		session.evict(newDDMTemplate);

		newDDMTemplate.setExternalReferenceCode(
			ddmTemplate.getExternalReferenceCode());

		_persistence.update(newDDMTemplate);
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
	public void testCountByClassPK() throws Exception {
		_persistence.countByClassPK(RandomTestUtil.nextLong());

		_persistence.countByClassPK(0L);
	}

	@Test
	public void testCountByTemplateKey() throws Exception {
		_persistence.countByTemplateKey("");

		_persistence.countByTemplateKey("null");

		_persistence.countByTemplateKey((String)null);
	}

	@Test
	public void testCountByType() throws Exception {
		_persistence.countByType("");

		_persistence.countByType("null");

		_persistence.countByType((String)null);
	}

	@Test
	public void testCountByLanguage() throws Exception {
		_persistence.countByLanguage("");

		_persistence.countByLanguage("null");

		_persistence.countByLanguage((String)null);
	}

	@Test
	public void testCountBySmallImageId() throws Exception {
		_persistence.countBySmallImageId(RandomTestUtil.nextLong());

		_persistence.countBySmallImageId(0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_CPK() throws Exception {
		_persistence.countByG_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_CPK(0L, 0L);
	}

	@Test
	public void testCountByG_CPKArrayable() throws Exception {
		_persistence.countByG_CPK(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong());
	}

	@Test
	public void testCountByG_C_C() throws Exception {
		_persistence.countByG_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_C_CArrayable() throws Exception {
		_persistence.countByG_C_C(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());
	}

	@Test
	public void testCountByG_C_T() throws Exception {
		_persistence.countByG_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_T(0L, 0L, "null");

		_persistence.countByG_C_T(0L, 0L, (String)null);
	}

	@Test
	public void testCountByC_C_T() throws Exception {
		_persistence.countByC_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_C_T(0L, 0L, "null");

		_persistence.countByC_C_T(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_C_C_T() throws Exception {
		_persistence.countByG_C_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_C_T(0L, 0L, 0L, "null");

		_persistence.countByG_C_C_T(0L, 0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_C_C_T_M() throws Exception {
		_persistence.countByG_C_C_T_M(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_C_C_T_M(0L, 0L, 0L, "null", "null");

		_persistence.countByG_C_C_T_M(0L, 0L, 0L, (String)null, (String)null);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		DDMTemplate existingDDMTemplate = _persistence.findByPrimaryKey(
			newDDMTemplate.getPrimaryKey());

		Assert.assertEquals(existingDDMTemplate, newDDMTemplate);
	}

	@Test(expected = NoSuchTemplateException.class)
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

	protected OrderByComparator<DDMTemplate> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMTemplate", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "templateId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"versionUserId", true, "versionUserName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"resourceClassNameId", true, "templateKey", true, "version", true,
			"type", true, "mode", true, "language", true, "cacheable", true,
			"smallImage", true, "smallImageId", true, "smallImageURL", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		DDMTemplate existingDDMTemplate = _persistence.fetchByPrimaryKey(
			newDDMTemplate.getPrimaryKey());

		Assert.assertEquals(existingDDMTemplate, newDDMTemplate);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplate missingDDMTemplate = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMTemplate);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMTemplate newDDMTemplate1 = addDDMTemplate();
		DDMTemplate newDDMTemplate2 = addDDMTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplate1.getPrimaryKey());
		primaryKeys.add(newDDMTemplate2.getPrimaryKey());

		Map<Serializable, DDMTemplate> ddmTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmTemplates.size());
		Assert.assertEquals(
			newDDMTemplate1, ddmTemplates.get(newDDMTemplate1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMTemplate2, ddmTemplates.get(newDDMTemplate2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMTemplate> ddmTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMTemplate newDDMTemplate = addDDMTemplate();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplate.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMTemplate> ddmTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmTemplates.size());
		Assert.assertEquals(
			newDDMTemplate, ddmTemplates.get(newDDMTemplate.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMTemplate> ddmTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplate.getPrimaryKey());

		Map<Serializable, DDMTemplate> ddmTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmTemplates.size());
		Assert.assertEquals(
			newDDMTemplate, ddmTemplates.get(newDDMTemplate.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDMTemplate.getPrimaryKey()));
	}

	private void _assertOriginalValues(DDMTemplate ddmTemplate) {
		Assert.assertEquals(
			ddmTemplate.getUuid(),
			ReflectionTestUtil.invoke(
				ddmTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ddmTemplate.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(ddmTemplate.getSmallImageId()),
			ReflectionTestUtil.<Long>invoke(
				ddmTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "smallImageId"));

		Assert.assertEquals(
			Long.valueOf(ddmTemplate.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(ddmTemplate.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ddmTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			ddmTemplate.getTemplateKey(),
			ReflectionTestUtil.invoke(
				ddmTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "templateKey"));

		Assert.assertEquals(
			ddmTemplate.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				ddmTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(ddmTemplate.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DDMTemplate addDDMTemplate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplate ddmTemplate = _persistence.create(pk);

		ddmTemplate.setMvccVersion(RandomTestUtil.nextLong());

		ddmTemplate.setCtCollectionId(RandomTestUtil.nextLong());

		ddmTemplate.setUuid(RandomTestUtil.randomString());

		ddmTemplate.setExternalReferenceCode(RandomTestUtil.randomString());

		ddmTemplate.setGroupId(RandomTestUtil.nextLong());

		ddmTemplate.setCompanyId(RandomTestUtil.nextLong());

		ddmTemplate.setUserId(RandomTestUtil.nextLong());

		ddmTemplate.setUserName(RandomTestUtil.randomString());

		ddmTemplate.setVersionUserId(RandomTestUtil.nextLong());

		ddmTemplate.setVersionUserName(RandomTestUtil.randomString());

		ddmTemplate.setCreateDate(RandomTestUtil.nextDate());

		ddmTemplate.setModifiedDate(RandomTestUtil.nextDate());

		ddmTemplate.setClassNameId(RandomTestUtil.nextLong());

		ddmTemplate.setClassPK(RandomTestUtil.nextLong());

		ddmTemplate.setResourceClassNameId(RandomTestUtil.nextLong());

		ddmTemplate.setTemplateKey(RandomTestUtil.randomString());

		ddmTemplate.setVersion(RandomTestUtil.randomString());

		ddmTemplate.setName(RandomTestUtil.randomString());

		ddmTemplate.setDescription(RandomTestUtil.randomString());

		ddmTemplate.setType(RandomTestUtil.randomString());

		ddmTemplate.setMode(RandomTestUtil.randomString());

		ddmTemplate.setLanguage(RandomTestUtil.randomString());

		ddmTemplate.setScript(RandomTestUtil.randomString());

		ddmTemplate.setCacheable(RandomTestUtil.randomBoolean());

		ddmTemplate.setSmallImage(RandomTestUtil.randomBoolean());

		ddmTemplate.setSmallImageId(RandomTestUtil.nextLong());

		ddmTemplate.setSmallImageURL(RandomTestUtil.randomString());

		ddmTemplate.setLastPublishDate(RandomTestUtil.nextDate());

		_ddmTemplates.add(_persistence.update(ddmTemplate));

		return ddmTemplate;
	}

	private List<DDMTemplate> _ddmTemplates = new ArrayList<DDMTemplate>();
	private DDMTemplatePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
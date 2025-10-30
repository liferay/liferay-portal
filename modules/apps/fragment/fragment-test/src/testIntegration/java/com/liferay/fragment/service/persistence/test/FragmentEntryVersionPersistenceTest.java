/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.exception.NoSuchEntryVersionException;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.fragment.service.persistence.FragmentEntryVersionPersistence;
import com.liferay.fragment.service.persistence.FragmentEntryVersionUtil;
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
public class FragmentEntryVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.fragment.service"));

	@Before
	public void setUp() {
		_persistence = FragmentEntryVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FragmentEntryVersion> iterator =
			_fragmentEntryVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentEntryVersion fragmentEntryVersion = _persistence.create(pk);

		Assert.assertNotNull(fragmentEntryVersion);

		Assert.assertEquals(fragmentEntryVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FragmentEntryVersion newFragmentEntryVersion =
			addFragmentEntryVersion();

		_persistence.remove(newFragmentEntryVersion);

		FragmentEntryVersion existingFragmentEntryVersion =
			_persistence.fetchByPrimaryKey(
				newFragmentEntryVersion.getPrimaryKey());

		Assert.assertNull(existingFragmentEntryVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFragmentEntryVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentEntryVersion newFragmentEntryVersion = _persistence.create(pk);

		newFragmentEntryVersion.setMvccVersion(RandomTestUtil.nextLong());

		newFragmentEntryVersion.setCtCollectionId(RandomTestUtil.nextLong());

		newFragmentEntryVersion.setVersion(RandomTestUtil.nextInt());

		newFragmentEntryVersion.setUuid(RandomTestUtil.randomString());

		newFragmentEntryVersion.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newFragmentEntryVersion.setFragmentEntryId(RandomTestUtil.nextLong());

		newFragmentEntryVersion.setGroupId(RandomTestUtil.nextLong());

		newFragmentEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		newFragmentEntryVersion.setUserId(RandomTestUtil.nextLong());

		newFragmentEntryVersion.setUserName(RandomTestUtil.randomString());

		newFragmentEntryVersion.setCreateDate(RandomTestUtil.nextDate());

		newFragmentEntryVersion.setModifiedDate(RandomTestUtil.nextDate());

		newFragmentEntryVersion.setFragmentCollectionId(
			RandomTestUtil.nextLong());

		newFragmentEntryVersion.setFragmentEntryKey(
			RandomTestUtil.randomString());

		newFragmentEntryVersion.setName(RandomTestUtil.randomString());

		newFragmentEntryVersion.setCss(RandomTestUtil.randomString());

		newFragmentEntryVersion.setHtml(RandomTestUtil.randomString());

		newFragmentEntryVersion.setJs(RandomTestUtil.randomString());

		newFragmentEntryVersion.setCacheable(RandomTestUtil.randomBoolean());

		newFragmentEntryVersion.setConfiguration(RandomTestUtil.randomString());

		newFragmentEntryVersion.setIcon(RandomTestUtil.randomString());

		newFragmentEntryVersion.setPreviewFileEntryId(
			RandomTestUtil.nextLong());

		newFragmentEntryVersion.setMarketplace(RandomTestUtil.randomBoolean());

		newFragmentEntryVersion.setReadOnly(RandomTestUtil.randomBoolean());

		newFragmentEntryVersion.setType(RandomTestUtil.nextInt());

		newFragmentEntryVersion.setTypeOptions(RandomTestUtil.randomString());

		newFragmentEntryVersion.setLastPublishDate(RandomTestUtil.nextDate());

		newFragmentEntryVersion.setStatus(RandomTestUtil.nextInt());

		newFragmentEntryVersion.setStatusByUserId(RandomTestUtil.nextLong());

		newFragmentEntryVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		newFragmentEntryVersion.setStatusDate(RandomTestUtil.nextDate());

		_fragmentEntryVersions.add(
			_persistence.update(newFragmentEntryVersion));

		FragmentEntryVersion existingFragmentEntryVersion =
			_persistence.findByPrimaryKey(
				newFragmentEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingFragmentEntryVersion.getMvccVersion(),
			newFragmentEntryVersion.getMvccVersion());
		Assert.assertEquals(
			existingFragmentEntryVersion.getCtCollectionId(),
			newFragmentEntryVersion.getCtCollectionId());
		Assert.assertEquals(
			existingFragmentEntryVersion.getFragmentEntryVersionId(),
			newFragmentEntryVersion.getFragmentEntryVersionId());
		Assert.assertEquals(
			existingFragmentEntryVersion.getVersion(),
			newFragmentEntryVersion.getVersion());
		Assert.assertEquals(
			existingFragmentEntryVersion.getUuid(),
			newFragmentEntryVersion.getUuid());
		Assert.assertEquals(
			existingFragmentEntryVersion.getExternalReferenceCode(),
			newFragmentEntryVersion.getExternalReferenceCode());
		Assert.assertEquals(
			existingFragmentEntryVersion.getFragmentEntryId(),
			newFragmentEntryVersion.getFragmentEntryId());
		Assert.assertEquals(
			existingFragmentEntryVersion.getGroupId(),
			newFragmentEntryVersion.getGroupId());
		Assert.assertEquals(
			existingFragmentEntryVersion.getCompanyId(),
			newFragmentEntryVersion.getCompanyId());
		Assert.assertEquals(
			existingFragmentEntryVersion.getUserId(),
			newFragmentEntryVersion.getUserId());
		Assert.assertEquals(
			existingFragmentEntryVersion.getUserName(),
			newFragmentEntryVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingFragmentEntryVersion.getCreateDate()),
			Time.getShortTimestamp(newFragmentEntryVersion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingFragmentEntryVersion.getModifiedDate()),
			Time.getShortTimestamp(newFragmentEntryVersion.getModifiedDate()));
		Assert.assertEquals(
			existingFragmentEntryVersion.getFragmentCollectionId(),
			newFragmentEntryVersion.getFragmentCollectionId());
		Assert.assertEquals(
			existingFragmentEntryVersion.getFragmentEntryKey(),
			newFragmentEntryVersion.getFragmentEntryKey());
		Assert.assertEquals(
			existingFragmentEntryVersion.getName(),
			newFragmentEntryVersion.getName());
		Assert.assertEquals(
			existingFragmentEntryVersion.getCss(),
			newFragmentEntryVersion.getCss());
		Assert.assertEquals(
			existingFragmentEntryVersion.getHtml(),
			newFragmentEntryVersion.getHtml());
		Assert.assertEquals(
			existingFragmentEntryVersion.getJs(),
			newFragmentEntryVersion.getJs());
		Assert.assertEquals(
			existingFragmentEntryVersion.isCacheable(),
			newFragmentEntryVersion.isCacheable());
		Assert.assertEquals(
			existingFragmentEntryVersion.getConfiguration(),
			newFragmentEntryVersion.getConfiguration());
		Assert.assertEquals(
			existingFragmentEntryVersion.getIcon(),
			newFragmentEntryVersion.getIcon());
		Assert.assertEquals(
			existingFragmentEntryVersion.getPreviewFileEntryId(),
			newFragmentEntryVersion.getPreviewFileEntryId());
		Assert.assertEquals(
			existingFragmentEntryVersion.isMarketplace(),
			newFragmentEntryVersion.isMarketplace());
		Assert.assertEquals(
			existingFragmentEntryVersion.isReadOnly(),
			newFragmentEntryVersion.isReadOnly());
		Assert.assertEquals(
			existingFragmentEntryVersion.getType(),
			newFragmentEntryVersion.getType());
		Assert.assertEquals(
			existingFragmentEntryVersion.getTypeOptions(),
			newFragmentEntryVersion.getTypeOptions());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingFragmentEntryVersion.getLastPublishDate()),
			Time.getShortTimestamp(
				newFragmentEntryVersion.getLastPublishDate()));
		Assert.assertEquals(
			existingFragmentEntryVersion.getStatus(),
			newFragmentEntryVersion.getStatus());
		Assert.assertEquals(
			existingFragmentEntryVersion.getStatusByUserId(),
			newFragmentEntryVersion.getStatusByUserId());
		Assert.assertEquals(
			existingFragmentEntryVersion.getStatusByUserName(),
			newFragmentEntryVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingFragmentEntryVersion.getStatusDate()),
			Time.getShortTimestamp(newFragmentEntryVersion.getStatusDate()));
	}

	@Test
	public void testCountByFragmentEntryId() throws Exception {
		_persistence.countByFragmentEntryId(RandomTestUtil.nextLong());

		_persistence.countByFragmentEntryId(0L);
	}

	@Test
	public void testCountByFragmentEntryId_Version() throws Exception {
		_persistence.countByFragmentEntryId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByFragmentEntryId_Version(0L, 0);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_Version() throws Exception {
		_persistence.countByUuid_Version("", RandomTestUtil.nextInt());

		_persistence.countByUuid_Version("null", 0);

		_persistence.countByUuid_Version((String)null, 0);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUUID_G_Version() throws Exception {
		_persistence.countByUUID_G_Version(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByUUID_G_Version("null", 0L, 0);

		_persistence.countByUUID_G_Version((String)null, 0L, 0);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C_Version() throws Exception {
		_persistence.countByUuid_C_Version(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByUuid_C_Version("null", 0L, 0);

		_persistence.countByUuid_C_Version((String)null, 0L, 0);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupId_Version() throws Exception {
		_persistence.countByGroupId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByGroupId_Version(0L, 0);
	}

	@Test
	public void testCountByFragmentCollectionId() throws Exception {
		_persistence.countByFragmentCollectionId(RandomTestUtil.nextLong());

		_persistence.countByFragmentCollectionId(0L);
	}

	@Test
	public void testCountByFragmentCollectionId_Version() throws Exception {
		_persistence.countByFragmentCollectionId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByFragmentCollectionId_Version(0L, 0);
	}

	@Test
	public void testCountByType() throws Exception {
		_persistence.countByType(RandomTestUtil.nextInt());

		_persistence.countByType(0);
	}

	@Test
	public void testCountByType_Version() throws Exception {
		_persistence.countByType_Version(
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByType_Version(0, 0);
	}

	@Test
	public void testCountByG_FCI() throws Exception {
		_persistence.countByG_FCI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_FCI(0L, 0L);
	}

	@Test
	public void testCountByG_FCI_Version() throws Exception {
		_persistence.countByG_FCI_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_Version(0L, 0L, 0);
	}

	@Test
	public void testCountByG_FEK() throws Exception {
		_persistence.countByG_FEK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_FEK(0L, "null");

		_persistence.countByG_FEK(0L, (String)null);
	}

	@Test
	public void testCountByG_FEK_Version() throws Exception {
		_persistence.countByG_FEK_Version(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_FEK_Version(0L, "null", 0);

		_persistence.countByG_FEK_Version(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_FCI_LikeN() throws Exception {
		_persistence.countByG_FCI_LikeN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_FCI_LikeN(0L, 0L, "null");

		_persistence.countByG_FCI_LikeN(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_FCI_LikeN_Version() throws Exception {
		_persistence.countByG_FCI_LikeN_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_LikeN_Version(0L, 0L, "null", 0);

		_persistence.countByG_FCI_LikeN_Version(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByG_FCI_T() throws Exception {
		_persistence.countByG_FCI_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_T(0L, 0L, 0);
	}

	@Test
	public void testCountByG_FCI_T_Version() throws Exception {
		_persistence.countByG_FCI_T_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByG_FCI_T_Version(0L, 0L, 0, 0);
	}

	@Test
	public void testCountByG_FCI_S() throws Exception {
		_persistence.countByG_FCI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_FCI_S_Version() throws Exception {
		_persistence.countByG_FCI_S_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByG_FCI_S_Version(0L, 0L, 0, 0);
	}

	@Test
	public void testCountByG_FCI_LikeN_S() throws Exception {
		_persistence.countByG_FCI_LikeN_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_LikeN_S(0L, 0L, "null", 0);

		_persistence.countByG_FCI_LikeN_S(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByG_FCI_LikeN_S_Version() throws Exception {
		_persistence.countByG_FCI_LikeN_S_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByG_FCI_LikeN_S_Version(0L, 0L, "null", 0, 0);

		_persistence.countByG_FCI_LikeN_S_Version(0L, 0L, (String)null, 0, 0);
	}

	@Test
	public void testCountByG_FCI_T_S() throws Exception {
		_persistence.countByG_FCI_T_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByG_FCI_T_S(0L, 0L, 0, 0);
	}

	@Test
	public void testCountByG_FCI_T_S_Version() throws Exception {
		_persistence.countByG_FCI_T_S_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_T_S_Version(0L, 0L, 0, 0, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FragmentEntryVersion newFragmentEntryVersion =
			addFragmentEntryVersion();

		FragmentEntryVersion existingFragmentEntryVersion =
			_persistence.findByPrimaryKey(
				newFragmentEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingFragmentEntryVersion, newFragmentEntryVersion);
	}

	@Test(expected = NoSuchEntryVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FragmentEntryVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"FragmentEntryVersion", "mvccVersion", true, "ctCollectionId", true,
			"fragmentEntryVersionId", true, "version", true, "uuid", true,
			"externalReferenceCode", true, "fragmentEntryId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "fragmentCollectionId",
			true, "fragmentEntryKey", true, "name", true, "cacheable", true,
			"icon", true, "previewFileEntryId", true, "marketplace", true,
			"readOnly", true, "type", true, "lastPublishDate", true, "status",
			true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FragmentEntryVersion newFragmentEntryVersion =
			addFragmentEntryVersion();

		FragmentEntryVersion existingFragmentEntryVersion =
			_persistence.fetchByPrimaryKey(
				newFragmentEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingFragmentEntryVersion, newFragmentEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentEntryVersion missingFragmentEntryVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFragmentEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FragmentEntryVersion newFragmentEntryVersion1 =
			addFragmentEntryVersion();
		FragmentEntryVersion newFragmentEntryVersion2 =
			addFragmentEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentEntryVersion1.getPrimaryKey());
		primaryKeys.add(newFragmentEntryVersion2.getPrimaryKey());

		Map<Serializable, FragmentEntryVersion> fragmentEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, fragmentEntryVersions.size());
		Assert.assertEquals(
			newFragmentEntryVersion1,
			fragmentEntryVersions.get(
				newFragmentEntryVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newFragmentEntryVersion2,
			fragmentEntryVersions.get(
				newFragmentEntryVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FragmentEntryVersion> fragmentEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(fragmentEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FragmentEntryVersion newFragmentEntryVersion =
			addFragmentEntryVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentEntryVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FragmentEntryVersion> fragmentEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, fragmentEntryVersions.size());
		Assert.assertEquals(
			newFragmentEntryVersion,
			fragmentEntryVersions.get(newFragmentEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FragmentEntryVersion> fragmentEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(fragmentEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FragmentEntryVersion newFragmentEntryVersion =
			addFragmentEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentEntryVersion.getPrimaryKey());

		Map<Serializable, FragmentEntryVersion> fragmentEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, fragmentEntryVersions.size());
		Assert.assertEquals(
			newFragmentEntryVersion,
			fragmentEntryVersions.get(newFragmentEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		FragmentEntryVersion newFragmentEntryVersion =
			addFragmentEntryVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newFragmentEntryVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		FragmentEntryVersion fragmentEntryVersion) {

		Assert.assertEquals(
			Long.valueOf(fragmentEntryVersion.getFragmentEntryId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fragmentEntryId"));
		Assert.assertEquals(
			Integer.valueOf(fragmentEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				fragmentEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			fragmentEntryVersion.getUuid(),
			ReflectionTestUtil.invoke(
				fragmentEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(fragmentEntryVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Integer.valueOf(fragmentEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				fragmentEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(fragmentEntryVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			fragmentEntryVersion.getFragmentEntryKey(),
			ReflectionTestUtil.invoke(
				fragmentEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fragmentEntryKey"));
		Assert.assertEquals(
			Integer.valueOf(fragmentEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				fragmentEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected FragmentEntryVersion addFragmentEntryVersion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentEntryVersion fragmentEntryVersion = _persistence.create(pk);

		fragmentEntryVersion.setMvccVersion(RandomTestUtil.nextLong());

		fragmentEntryVersion.setCtCollectionId(RandomTestUtil.nextLong());

		fragmentEntryVersion.setVersion(RandomTestUtil.nextInt());

		fragmentEntryVersion.setUuid(RandomTestUtil.randomString());

		fragmentEntryVersion.setExternalReferenceCode(
			RandomTestUtil.randomString());

		fragmentEntryVersion.setFragmentEntryId(RandomTestUtil.nextLong());

		fragmentEntryVersion.setGroupId(RandomTestUtil.nextLong());

		fragmentEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		fragmentEntryVersion.setUserId(RandomTestUtil.nextLong());

		fragmentEntryVersion.setUserName(RandomTestUtil.randomString());

		fragmentEntryVersion.setCreateDate(RandomTestUtil.nextDate());

		fragmentEntryVersion.setModifiedDate(RandomTestUtil.nextDate());

		fragmentEntryVersion.setFragmentCollectionId(RandomTestUtil.nextLong());

		fragmentEntryVersion.setFragmentEntryKey(RandomTestUtil.randomString());

		fragmentEntryVersion.setName(RandomTestUtil.randomString());

		fragmentEntryVersion.setCss(RandomTestUtil.randomString());

		fragmentEntryVersion.setHtml(RandomTestUtil.randomString());

		fragmentEntryVersion.setJs(RandomTestUtil.randomString());

		fragmentEntryVersion.setCacheable(RandomTestUtil.randomBoolean());

		fragmentEntryVersion.setConfiguration(RandomTestUtil.randomString());

		fragmentEntryVersion.setIcon(RandomTestUtil.randomString());

		fragmentEntryVersion.setPreviewFileEntryId(RandomTestUtil.nextLong());

		fragmentEntryVersion.setMarketplace(RandomTestUtil.randomBoolean());

		fragmentEntryVersion.setReadOnly(RandomTestUtil.randomBoolean());

		fragmentEntryVersion.setType(RandomTestUtil.nextInt());

		fragmentEntryVersion.setTypeOptions(RandomTestUtil.randomString());

		fragmentEntryVersion.setLastPublishDate(RandomTestUtil.nextDate());

		fragmentEntryVersion.setStatus(RandomTestUtil.nextInt());

		fragmentEntryVersion.setStatusByUserId(RandomTestUtil.nextLong());

		fragmentEntryVersion.setStatusByUserName(RandomTestUtil.randomString());

		fragmentEntryVersion.setStatusDate(RandomTestUtil.nextDate());

		_fragmentEntryVersions.add(_persistence.update(fragmentEntryVersion));

		return fragmentEntryVersion;
	}

	private List<FragmentEntryVersion> _fragmentEntryVersions =
		new ArrayList<FragmentEntryVersion>();
	private FragmentEntryVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
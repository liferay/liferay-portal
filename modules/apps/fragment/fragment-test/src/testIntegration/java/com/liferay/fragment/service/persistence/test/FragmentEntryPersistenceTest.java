/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.exception.DuplicateFragmentEntryExternalReferenceCodeException;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.persistence.FragmentEntryPersistence;
import com.liferay.fragment.service.persistence.FragmentEntryUtil;
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
public class FragmentEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.fragment.service"));

	@Before
	public void setUp() {
		_persistence = FragmentEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FragmentEntry> iterator = _fragmentEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentEntry fragmentEntry = _persistence.create(pk);

		Assert.assertNotNull(fragmentEntry);

		Assert.assertEquals(fragmentEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FragmentEntry newFragmentEntry = addFragmentEntry();

		_persistence.remove(newFragmentEntry);

		FragmentEntry existingFragmentEntry = _persistence.fetchByPrimaryKey(
			newFragmentEntry.getPrimaryKey());

		Assert.assertNull(existingFragmentEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFragmentEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentEntry newFragmentEntry = _persistence.create(pk);

		newFragmentEntry.setMvccVersion(RandomTestUtil.nextLong());

		newFragmentEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newFragmentEntry.setUuid(RandomTestUtil.randomString());

		newFragmentEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newFragmentEntry.setHeadId(RandomTestUtil.nextLong());

		newFragmentEntry.setGroupId(RandomTestUtil.nextLong());

		newFragmentEntry.setCompanyId(RandomTestUtil.nextLong());

		newFragmentEntry.setUserId(RandomTestUtil.nextLong());

		newFragmentEntry.setUserName(RandomTestUtil.randomString());

		newFragmentEntry.setCreateDate(RandomTestUtil.nextDate());

		newFragmentEntry.setModifiedDate(RandomTestUtil.nextDate());

		newFragmentEntry.setFragmentCollectionId(RandomTestUtil.nextLong());

		newFragmentEntry.setFragmentEntryKey(RandomTestUtil.randomString());

		newFragmentEntry.setName(RandomTestUtil.randomString());

		newFragmentEntry.setCss(RandomTestUtil.randomString());

		newFragmentEntry.setHtml(RandomTestUtil.randomString());

		newFragmentEntry.setJs(RandomTestUtil.randomString());

		newFragmentEntry.setCacheable(RandomTestUtil.randomBoolean());

		newFragmentEntry.setConfiguration(RandomTestUtil.randomString());

		newFragmentEntry.setIcon(RandomTestUtil.randomString());

		newFragmentEntry.setPreviewFileEntryId(RandomTestUtil.nextLong());

		newFragmentEntry.setMarketplace(RandomTestUtil.randomBoolean());

		newFragmentEntry.setReadOnly(RandomTestUtil.randomBoolean());

		newFragmentEntry.setType(RandomTestUtil.nextInt());

		newFragmentEntry.setTypeOptions(RandomTestUtil.randomString());

		newFragmentEntry.setLastPublishDate(RandomTestUtil.nextDate());

		newFragmentEntry.setStatus(RandomTestUtil.nextInt());

		newFragmentEntry.setStatusByUserId(RandomTestUtil.nextLong());

		newFragmentEntry.setStatusByUserName(RandomTestUtil.randomString());

		newFragmentEntry.setStatusDate(RandomTestUtil.nextDate());

		_fragmentEntries.add(_persistence.update(newFragmentEntry));

		FragmentEntry existingFragmentEntry = _persistence.findByPrimaryKey(
			newFragmentEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFragmentEntry.getMvccVersion(),
			newFragmentEntry.getMvccVersion());
		Assert.assertEquals(
			existingFragmentEntry.getCtCollectionId(),
			newFragmentEntry.getCtCollectionId());
		Assert.assertEquals(
			existingFragmentEntry.getUuid(), newFragmentEntry.getUuid());
		Assert.assertEquals(
			existingFragmentEntry.getExternalReferenceCode(),
			newFragmentEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingFragmentEntry.getHeadId(), newFragmentEntry.getHeadId());
		Assert.assertEquals(
			existingFragmentEntry.getFragmentEntryId(),
			newFragmentEntry.getFragmentEntryId());
		Assert.assertEquals(
			existingFragmentEntry.getGroupId(), newFragmentEntry.getGroupId());
		Assert.assertEquals(
			existingFragmentEntry.getCompanyId(),
			newFragmentEntry.getCompanyId());
		Assert.assertEquals(
			existingFragmentEntry.getUserId(), newFragmentEntry.getUserId());
		Assert.assertEquals(
			existingFragmentEntry.getUserName(),
			newFragmentEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingFragmentEntry.getCreateDate()),
			Time.getShortTimestamp(newFragmentEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingFragmentEntry.getModifiedDate()),
			Time.getShortTimestamp(newFragmentEntry.getModifiedDate()));
		Assert.assertEquals(
			existingFragmentEntry.getFragmentCollectionId(),
			newFragmentEntry.getFragmentCollectionId());
		Assert.assertEquals(
			existingFragmentEntry.getFragmentEntryKey(),
			newFragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(
			existingFragmentEntry.getName(), newFragmentEntry.getName());
		Assert.assertEquals(
			existingFragmentEntry.getCss(), newFragmentEntry.getCss());
		Assert.assertEquals(
			existingFragmentEntry.getHtml(), newFragmentEntry.getHtml());
		Assert.assertEquals(
			existingFragmentEntry.getJs(), newFragmentEntry.getJs());
		Assert.assertEquals(
			existingFragmentEntry.isCacheable(),
			newFragmentEntry.isCacheable());
		Assert.assertEquals(
			existingFragmentEntry.getConfiguration(),
			newFragmentEntry.getConfiguration());
		Assert.assertEquals(
			existingFragmentEntry.getIcon(), newFragmentEntry.getIcon());
		Assert.assertEquals(
			existingFragmentEntry.getPreviewFileEntryId(),
			newFragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(
			existingFragmentEntry.isMarketplace(),
			newFragmentEntry.isMarketplace());
		Assert.assertEquals(
			existingFragmentEntry.isReadOnly(), newFragmentEntry.isReadOnly());
		Assert.assertEquals(
			existingFragmentEntry.getType(), newFragmentEntry.getType());
		Assert.assertEquals(
			existingFragmentEntry.getTypeOptions(),
			newFragmentEntry.getTypeOptions());
		Assert.assertEquals(
			Time.getShortTimestamp(existingFragmentEntry.getLastPublishDate()),
			Time.getShortTimestamp(newFragmentEntry.getLastPublishDate()));
		Assert.assertEquals(
			existingFragmentEntry.getStatus(), newFragmentEntry.getStatus());
		Assert.assertEquals(
			existingFragmentEntry.getStatusByUserId(),
			newFragmentEntry.getStatusByUserId());
		Assert.assertEquals(
			existingFragmentEntry.getStatusByUserName(),
			newFragmentEntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingFragmentEntry.getStatusDate()),
			Time.getShortTimestamp(newFragmentEntry.getStatusDate()));
	}

	@Test
	public void testCreateDraft() throws Exception {
		FragmentEntry fragmentEntry = addFragmentEntry();

		long pk = RandomTestUtil.nextLong();

		FragmentEntry draftFragmentEntry = _persistence.create(pk);

		draftFragmentEntry.setMvccVersion(fragmentEntry.getMvccVersion());
		draftFragmentEntry.setCtCollectionId(fragmentEntry.getCtCollectionId());
		draftFragmentEntry.setUuid(fragmentEntry.getUuid());
		draftFragmentEntry.setExternalReferenceCode(
			fragmentEntry.getExternalReferenceCode());
		draftFragmentEntry.setHeadId(-fragmentEntry.getHeadId());
		draftFragmentEntry.setGroupId(fragmentEntry.getGroupId());
		draftFragmentEntry.setCompanyId(fragmentEntry.getCompanyId());
		draftFragmentEntry.setUserId(fragmentEntry.getUserId());
		draftFragmentEntry.setUserName(fragmentEntry.getUserName());
		draftFragmentEntry.setCreateDate(fragmentEntry.getCreateDate());
		draftFragmentEntry.setModifiedDate(fragmentEntry.getModifiedDate());
		draftFragmentEntry.setFragmentCollectionId(
			fragmentEntry.getFragmentCollectionId());
		draftFragmentEntry.setFragmentEntryKey(
			fragmentEntry.getFragmentEntryKey());
		draftFragmentEntry.setName(fragmentEntry.getName());
		draftFragmentEntry.setCss(fragmentEntry.getCss());
		draftFragmentEntry.setHtml(fragmentEntry.getHtml());
		draftFragmentEntry.setJs(fragmentEntry.getJs());
		draftFragmentEntry.setCacheable(fragmentEntry.getCacheable());
		draftFragmentEntry.setConfiguration(fragmentEntry.getConfiguration());
		draftFragmentEntry.setIcon(fragmentEntry.getIcon());
		draftFragmentEntry.setPreviewFileEntryId(
			fragmentEntry.getPreviewFileEntryId());
		draftFragmentEntry.setMarketplace(fragmentEntry.getMarketplace());
		draftFragmentEntry.setReadOnly(fragmentEntry.getReadOnly());
		draftFragmentEntry.setType(fragmentEntry.getType());
		draftFragmentEntry.setTypeOptions(fragmentEntry.getTypeOptions());
		draftFragmentEntry.setLastPublishDate(
			fragmentEntry.getLastPublishDate());
		draftFragmentEntry.setStatus(fragmentEntry.getStatus());
		draftFragmentEntry.setStatusByUserId(fragmentEntry.getStatusByUserId());
		draftFragmentEntry.setStatusByUserName(
			fragmentEntry.getStatusByUserName());
		draftFragmentEntry.setStatusDate(fragmentEntry.getStatusDate());

		_fragmentEntries.add(_persistence.update(draftFragmentEntry));

		Assert.assertEquals(
			fragmentEntry.getMvccVersion(),
			draftFragmentEntry.getMvccVersion());
		Assert.assertEquals(
			fragmentEntry.getCtCollectionId(),
			draftFragmentEntry.getCtCollectionId());
		Assert.assertEquals(
			fragmentEntry.getUuid(), draftFragmentEntry.getUuid());
		Assert.assertEquals(
			fragmentEntry.getExternalReferenceCode(),
			draftFragmentEntry.getExternalReferenceCode());
		Assert.assertEquals(
			fragmentEntry.getHeadId(), -draftFragmentEntry.getHeadId());
		Assert.assertEquals(
			fragmentEntry.getGroupId(), draftFragmentEntry.getGroupId());
		Assert.assertEquals(
			fragmentEntry.getCompanyId(), draftFragmentEntry.getCompanyId());
		Assert.assertEquals(
			fragmentEntry.getUserId(), draftFragmentEntry.getUserId());
		Assert.assertEquals(
			fragmentEntry.getUserName(), draftFragmentEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(fragmentEntry.getCreateDate()),
			Time.getShortTimestamp(draftFragmentEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(fragmentEntry.getModifiedDate()),
			Time.getShortTimestamp(draftFragmentEntry.getModifiedDate()));
		Assert.assertEquals(
			fragmentEntry.getFragmentCollectionId(),
			draftFragmentEntry.getFragmentCollectionId());
		Assert.assertEquals(
			fragmentEntry.getFragmentEntryKey(),
			draftFragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(
			fragmentEntry.getName(), draftFragmentEntry.getName());
		Assert.assertEquals(
			fragmentEntry.getCss(), draftFragmentEntry.getCss());
		Assert.assertEquals(
			fragmentEntry.getHtml(), draftFragmentEntry.getHtml());
		Assert.assertEquals(fragmentEntry.getJs(), draftFragmentEntry.getJs());
		Assert.assertEquals(
			fragmentEntry.isCacheable(), draftFragmentEntry.isCacheable());
		Assert.assertEquals(
			fragmentEntry.getConfiguration(),
			draftFragmentEntry.getConfiguration());
		Assert.assertEquals(
			fragmentEntry.getIcon(), draftFragmentEntry.getIcon());
		Assert.assertEquals(
			fragmentEntry.getPreviewFileEntryId(),
			draftFragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(
			fragmentEntry.isMarketplace(), draftFragmentEntry.isMarketplace());
		Assert.assertEquals(
			fragmentEntry.isReadOnly(), draftFragmentEntry.isReadOnly());
		Assert.assertEquals(
			fragmentEntry.getType(), draftFragmentEntry.getType());
		Assert.assertEquals(
			fragmentEntry.getTypeOptions(),
			draftFragmentEntry.getTypeOptions());
		Assert.assertEquals(
			Time.getShortTimestamp(fragmentEntry.getLastPublishDate()),
			Time.getShortTimestamp(draftFragmentEntry.getLastPublishDate()));
		Assert.assertEquals(2, draftFragmentEntry.getStatus());
		Assert.assertEquals(
			fragmentEntry.getStatusByUserId(),
			draftFragmentEntry.getStatusByUserId());
		Assert.assertEquals(
			fragmentEntry.getStatusByUserName(),
			draftFragmentEntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(fragmentEntry.getStatusDate()),
			Time.getShortTimestamp(draftFragmentEntry.getStatusDate()));
	}

	@Test(expected = DuplicateFragmentEntryExternalReferenceCodeException.class)
	public void testCreateWithExistingExternalReferenceCodeHead()
		throws Exception {

		FragmentEntry fragmentEntry1 = addFragmentEntry();

		long pk = RandomTestUtil.nextLong();

		FragmentEntry fragmentEntry2 = _persistence.create(pk);

		fragmentEntry2.setMvccVersion(RandomTestUtil.nextLong());

		fragmentEntry2.setCtCollectionId(RandomTestUtil.nextLong());

		fragmentEntry2.setUuid(RandomTestUtil.randomString());

		fragmentEntry2.setExternalReferenceCode(
			fragmentEntry1.getExternalReferenceCode());

		fragmentEntry2.setHeadId(-RandomTestUtil.nextLong());

		fragmentEntry2.setGroupId(fragmentEntry1.getGroupId());

		fragmentEntry2.setCompanyId(RandomTestUtil.nextLong());

		fragmentEntry2.setUserId(RandomTestUtil.nextLong());

		fragmentEntry2.setUserName(RandomTestUtil.randomString());

		fragmentEntry2.setCreateDate(RandomTestUtil.nextDate());

		fragmentEntry2.setModifiedDate(RandomTestUtil.nextDate());

		fragmentEntry2.setFragmentCollectionId(RandomTestUtil.nextLong());

		fragmentEntry2.setFragmentEntryKey(RandomTestUtil.randomString());

		fragmentEntry2.setName(RandomTestUtil.randomString());

		fragmentEntry2.setCss(RandomTestUtil.randomString());

		fragmentEntry2.setHtml(RandomTestUtil.randomString());

		fragmentEntry2.setJs(RandomTestUtil.randomString());

		fragmentEntry2.setCacheable(RandomTestUtil.randomBoolean());

		fragmentEntry2.setConfiguration(RandomTestUtil.randomString());

		fragmentEntry2.setIcon(RandomTestUtil.randomString());

		fragmentEntry2.setPreviewFileEntryId(RandomTestUtil.nextLong());

		fragmentEntry2.setMarketplace(RandomTestUtil.randomBoolean());

		fragmentEntry2.setReadOnly(RandomTestUtil.randomBoolean());

		fragmentEntry2.setType(RandomTestUtil.nextInt());

		fragmentEntry2.setTypeOptions(RandomTestUtil.randomString());

		fragmentEntry2.setLastPublishDate(RandomTestUtil.nextDate());

		fragmentEntry2.setStatus(RandomTestUtil.nextInt());

		fragmentEntry2.setStatusByUserId(RandomTestUtil.nextLong());

		fragmentEntry2.setStatusByUserName(RandomTestUtil.randomString());

		fragmentEntry2.setStatusDate(RandomTestUtil.nextDate());

		_fragmentEntries.add(_persistence.update(fragmentEntry2));
	}

	@Test(expected = DuplicateFragmentEntryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		FragmentEntry fragmentEntry = addFragmentEntry();

		FragmentEntry newFragmentEntry = addFragmentEntry();

		newFragmentEntry.setGroupId(fragmentEntry.getGroupId());

		newFragmentEntry = _persistence.update(newFragmentEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newFragmentEntry);

		newFragmentEntry.setExternalReferenceCode(
			fragmentEntry.getExternalReferenceCode());

		_persistence.update(newFragmentEntry);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_Head() throws Exception {
		_persistence.countByUuid_Head("", RandomTestUtil.randomBoolean());

		_persistence.countByUuid_Head("null", RandomTestUtil.randomBoolean());

		_persistence.countByUuid_Head(
			(String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUUID_G_Head() throws Exception {
		_persistence.countByUUID_G_Head(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByUUID_G_Head(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByUUID_G_Head(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C_Head() throws Exception {
		_persistence.countByUuid_C_Head(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByUuid_C_Head(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByUuid_C_Head(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupId_Head() throws Exception {
		_persistence.countByGroupId_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByGroupId_Head(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByFragmentCollectionId() throws Exception {
		_persistence.countByFragmentCollectionId(RandomTestUtil.nextLong());

		_persistence.countByFragmentCollectionId(0L);
	}

	@Test
	public void testCountByFragmentCollectionId_Head() throws Exception {
		_persistence.countByFragmentCollectionId_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByFragmentCollectionId_Head(
			0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByType() throws Exception {
		_persistence.countByType(RandomTestUtil.nextInt());

		_persistence.countByType(0);
	}

	@Test
	public void testCountByType_Head() throws Exception {
		_persistence.countByType_Head(
			RandomTestUtil.nextInt(), RandomTestUtil.randomBoolean());

		_persistence.countByType_Head(0, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_FCI() throws Exception {
		_persistence.countByG_FCI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_FCI(0L, 0L);
	}

	@Test
	public void testCountByG_FCI_Head() throws Exception {
		_persistence.countByG_FCI_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_FCI_Head(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_FEK() throws Exception {
		_persistence.countByG_FEK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_FEK(0L, "null");

		_persistence.countByG_FEK(0L, (String)null);
	}

	@Test
	public void testCountByG_FEK_Head() throws Exception {
		_persistence.countByG_FEK_Head(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_FEK_Head(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_FEK_Head(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_FCI_LikeN() throws Exception {
		_persistence.countByG_FCI_LikeN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_FCI_LikeN(0L, 0L, "null");

		_persistence.countByG_FCI_LikeN(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_FCI_LikeN_Head() throws Exception {
		_persistence.countByG_FCI_LikeN_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.randomBoolean());

		_persistence.countByG_FCI_LikeN_Head(
			0L, 0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_FCI_LikeN_Head(
			0L, 0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_FCI_T() throws Exception {
		_persistence.countByG_FCI_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_T(0L, 0L, 0);
	}

	@Test
	public void testCountByG_FCI_T_Head() throws Exception {
		_persistence.countByG_FCI_T_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.randomBoolean());

		_persistence.countByG_FCI_T_Head(
			0L, 0L, 0, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_FCI_S() throws Exception {
		_persistence.countByG_FCI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_FCI_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_FCI_S_Head() throws Exception {
		_persistence.countByG_FCI_S_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.randomBoolean());

		_persistence.countByG_FCI_S_Head(
			0L, 0L, 0, RandomTestUtil.randomBoolean());
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
	public void testCountByG_FCI_LikeN_S_Head() throws Exception {
		_persistence.countByG_FCI_LikeN_S_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt(), RandomTestUtil.randomBoolean());

		_persistence.countByG_FCI_LikeN_S_Head(
			0L, 0L, "null", 0, RandomTestUtil.randomBoolean());

		_persistence.countByG_FCI_LikeN_S_Head(
			0L, 0L, (String)null, 0, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_FCI_T_S() throws Exception {
		_persistence.countByG_FCI_T_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt());

		_persistence.countByG_FCI_T_S(0L, 0L, 0, 0);
	}

	@Test
	public void testCountByG_FCI_T_S_Head() throws Exception {
		_persistence.countByG_FCI_T_S_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_FCI_T_S_Head(
			0L, 0L, 0, 0, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testCountByERC_G_Head() throws Exception {
		_persistence.countByERC_G_Head(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByERC_G_Head(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByERC_G_Head(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByHeadId() throws Exception {
		_persistence.countByHeadId(RandomTestUtil.nextLong());

		_persistence.countByHeadId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FragmentEntry newFragmentEntry = addFragmentEntry();

		FragmentEntry existingFragmentEntry = _persistence.findByPrimaryKey(
			newFragmentEntry.getPrimaryKey());

		Assert.assertEquals(existingFragmentEntry, newFragmentEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FragmentEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"FragmentEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "headId", true,
			"fragmentEntryId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "fragmentCollectionId", true,
			"fragmentEntryKey", true, "name", true, "cacheable", true, "icon",
			true, "previewFileEntryId", true, "marketplace", true, "readOnly",
			true, "type", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FragmentEntry newFragmentEntry = addFragmentEntry();

		FragmentEntry existingFragmentEntry = _persistence.fetchByPrimaryKey(
			newFragmentEntry.getPrimaryKey());

		Assert.assertEquals(existingFragmentEntry, newFragmentEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentEntry missingFragmentEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFragmentEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FragmentEntry newFragmentEntry1 = addFragmentEntry();
		FragmentEntry newFragmentEntry2 = addFragmentEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentEntry1.getPrimaryKey());
		primaryKeys.add(newFragmentEntry2.getPrimaryKey());

		Map<Serializable, FragmentEntry> fragmentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, fragmentEntries.size());
		Assert.assertEquals(
			newFragmentEntry1,
			fragmentEntries.get(newFragmentEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newFragmentEntry2,
			fragmentEntries.get(newFragmentEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FragmentEntry> fragmentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(fragmentEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FragmentEntry newFragmentEntry = addFragmentEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FragmentEntry> fragmentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, fragmentEntries.size());
		Assert.assertEquals(
			newFragmentEntry,
			fragmentEntries.get(newFragmentEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FragmentEntry> fragmentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(fragmentEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FragmentEntry newFragmentEntry = addFragmentEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentEntry.getPrimaryKey());

		Map<Serializable, FragmentEntry> fragmentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, fragmentEntries.size());
		Assert.assertEquals(
			newFragmentEntry,
			fragmentEntries.get(newFragmentEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		FragmentEntry newFragmentEntry = addFragmentEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newFragmentEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(FragmentEntry fragmentEntry) {
		Assert.assertEquals(
			fragmentEntry.getUuid(),
			ReflectionTestUtil.invoke(
				fragmentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(fragmentEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(fragmentEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			fragmentEntry.getFragmentEntryKey(),
			ReflectionTestUtil.invoke(
				fragmentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fragmentEntryKey"));

		Assert.assertEquals(
			fragmentEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				fragmentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(fragmentEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(fragmentEntry.getHeadId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "headId"));
	}

	protected FragmentEntry addFragmentEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentEntry fragmentEntry = _persistence.create(pk);

		fragmentEntry.setMvccVersion(RandomTestUtil.nextLong());

		fragmentEntry.setCtCollectionId(RandomTestUtil.nextLong());

		fragmentEntry.setUuid(RandomTestUtil.randomString());

		fragmentEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		fragmentEntry.setHeadId(-pk);

		fragmentEntry.setGroupId(RandomTestUtil.nextLong());

		fragmentEntry.setCompanyId(RandomTestUtil.nextLong());

		fragmentEntry.setUserId(RandomTestUtil.nextLong());

		fragmentEntry.setUserName(RandomTestUtil.randomString());

		fragmentEntry.setCreateDate(RandomTestUtil.nextDate());

		fragmentEntry.setModifiedDate(RandomTestUtil.nextDate());

		fragmentEntry.setFragmentCollectionId(RandomTestUtil.nextLong());

		fragmentEntry.setFragmentEntryKey(RandomTestUtil.randomString());

		fragmentEntry.setName(RandomTestUtil.randomString());

		fragmentEntry.setCss(RandomTestUtil.randomString());

		fragmentEntry.setHtml(RandomTestUtil.randomString());

		fragmentEntry.setJs(RandomTestUtil.randomString());

		fragmentEntry.setCacheable(RandomTestUtil.randomBoolean());

		fragmentEntry.setConfiguration(RandomTestUtil.randomString());

		fragmentEntry.setIcon(RandomTestUtil.randomString());

		fragmentEntry.setPreviewFileEntryId(RandomTestUtil.nextLong());

		fragmentEntry.setMarketplace(RandomTestUtil.randomBoolean());

		fragmentEntry.setReadOnly(RandomTestUtil.randomBoolean());

		fragmentEntry.setType(RandomTestUtil.nextInt());

		fragmentEntry.setTypeOptions(RandomTestUtil.randomString());

		fragmentEntry.setLastPublishDate(RandomTestUtil.nextDate());

		fragmentEntry.setStatus(RandomTestUtil.nextInt());

		fragmentEntry.setStatusByUserId(RandomTestUtil.nextLong());

		fragmentEntry.setStatusByUserName(RandomTestUtil.randomString());

		fragmentEntry.setStatusDate(RandomTestUtil.nextDate());

		_fragmentEntries.add(_persistence.update(fragmentEntry));

		return fragmentEntry;
	}

	private List<FragmentEntry> _fragmentEntries =
		new ArrayList<FragmentEntry>();
	private FragmentEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
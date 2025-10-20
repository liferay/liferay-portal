/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.repository.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.search.IndexStatusManagerThreadLocal;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.repository.liferayrepository.LiferayRepository;
import com.liferay.portal.test.rule.Inject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marco Galluzzi
 */
@RunWith(Arquillian.class)
public class RepositoryFactoryDBPartitionTest extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		BaseDBPartitionTestCase.setUpDBPartitions();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		BaseDBPartitionTestCase.tearDownDBPartitions();
	}

	@Test
	public void testCreateRepositoryWhenSameRepositoryIdExistsInDifferentPartition()
		throws Exception {

		IndexStatusManagerThreadLocal.setIndexReadOnly(true);

		long count = _getCount();

		long repositoryId = count + 1000;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[0])) {

			long groupId = count + 100;

			Repository repository = _createRepository(
				COMPANY_IDS[0], groupId, repositoryId);

			Assert.assertEquals(groupId, _getGroupId(repository));
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[1])) {

			long groupId = count + 200;

			Repository repository = _createRepository(
				COMPANY_IDS[1], groupId, repositoryId);

			Assert.assertEquals(groupId, _getGroupId(repository));
		}
	}

	private Repository _createRepository(
			long companyId, long groupId, long repositoryId)
		throws Exception {

		_counterLocalService.reset(Counter.class.getName(), groupId - 1);

		User user = _userLocalService.getGuestUser(companyId);
		String name = RandomTestUtil.randomString();

		Group group = _groupLocalService.addGroup(
			StringPool.BLANK, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			GroupConstants.TYPE_SITE_OPEN, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name), true,
			false, true, new ServiceContext());

		Assert.assertEquals(groupId, group.getGroupId());

		DLFolder dlFolder = _dlFolderLocalService.addFolder(
			null, user.getUserId(), group.getGroupId(), group.getGroupId(),
			false, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			new ServiceContext());

		_counterLocalService.reset(Counter.class.getName(), repositoryId - 1);

		com.liferay.portal.kernel.model.Repository repository =
			_repositoryLocalService.addRepository(
				null, user.getUserId(), group.getGroupId(),
				_portal.getClassNameId(LiferayRepository.class.getName()),
				dlFolder.getFolderId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), "Test Portlet",
				new UnicodeProperties(), true, new ServiceContext());

		Assert.assertEquals(repositoryId, repository.getRepositoryId());

		return _repositoryFactory.createRepository(
			repository.getRepositoryId());
	}

	private long _getCount() {
		long count1 = 1;
		long count2 = 1;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[0])) {

			count1 = _counterLocalService.increment();
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[1])) {

			count2 = _counterLocalService.increment();
		}

		return Math.max(count1, count2);
	}

	private long _getGroupId(Repository repository) {
		while (!(repository instanceof LiferayRepository)) {
			repository = ReflectionTestUtil.getFieldValue(
				repository, "_repository");
		}

		return ReflectionTestUtil.getFieldValue(repository, "_groupId");
	}

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private RepositoryFactory _repositoryFactory;

	@Inject
	private RepositoryLocalService _repositoryLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
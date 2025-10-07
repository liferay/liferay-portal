/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMFormConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class UserModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		User user = UserTestUtil.addUser();

		Folder folder1 = _addFolder(GroupTestUtil.addGroup(), user);
		Folder folder2 = _addFolder(GroupTestUtil.addGroup(), user);

		_userLocalService.deleteUser(user);

		folder1 = _dlAppLocalService.getFolder(folder1.getFolderId());

		Assert.assertEquals(
			user.getScreenName() + StringPool.SPACE + user.getUserId(),
			folder1.getName());

		folder2 = _dlAppLocalService.getFolder(folder2.getFolderId());

		Assert.assertEquals(
			user.getScreenName() + StringPool.SPACE + user.getUserId(),
			folder2.getName());
	}

	private Folder _addFolder(Group group, User user) throws Exception {
		Repository repository = _portletFileRepository.addPortletRepository(
			group.getGroupId(), DDMFormConstants.SERVICE_NAME,
			ServiceContextTestUtil.getServiceContext());

		Folder parentFolder = _portletFileRepository.addPortletFolder(
			TestPropsValues.getUserId(), repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			DDMFormConstants.DDM_FORM_UPLOADED_FILES_FOLDER_NAME,
			ServiceContextTestUtil.getServiceContext());

		return _dlAppLocalService.addFolder(
			null, user.getUserId(), repository.getRepositoryId(),
			parentFolder.getFolderId(), user.getScreenName(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private UserLocalService _userLocalService;

}
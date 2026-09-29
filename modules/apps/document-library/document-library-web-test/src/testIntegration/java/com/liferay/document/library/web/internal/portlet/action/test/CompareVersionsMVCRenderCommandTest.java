/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.RenderRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class CompareVersionsMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-107148")
	public void testCompareVersionsWhenBothFileVersionsViewable()
		throws Exception {

		User user = UserTestUtil.addUser();

		FileEntry fileEntry = _addFileEntry(user.getUserId(), true);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			ReflectionTestUtil.invoke(
				_compareVersionsMVCRenderCommand, "_compareVersions",
				new Class<?>[] {RenderRequest.class},
				_getMockLiferayPortletRenderRequest(
					fileEntry.getFileVersion(), fileEntry.getFileVersion()));
		}
	}

	@Test
	@TestInfo("LPD-107148")
	public void testCompareVersionsWhenTargetFileVersionNotViewable()
		throws Exception {

		User user = UserTestUtil.addUser();

		FileEntry sourceFileEntry = _addFileEntry(user.getUserId(), true);

		FileEntry targetFileEntry = _addFileEntry(
			TestPropsValues.getUserId(), false);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> ReflectionTestUtil.invoke(
					_compareVersionsMVCRenderCommand, "_compareVersions",
					new Class<?>[] {RenderRequest.class},
					_getMockLiferayPortletRenderRequest(
						sourceFileEntry.getFileVersion(),
						targetFileEntry.getFileVersion())));
		}
	}

	private FileEntry _addFileEntry(long userId, boolean viewable)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), userId);

		serviceContext.setAddGroupPermissions(viewable);
		serviceContext.setAddGuestPermissions(viewable);

		return _dlAppLocalService.addFileEntry(
			null, userId, _group.getGroupId(), 0, RandomTestUtil.randomString(),
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			RandomTestUtil.randomBytes(), null, null, null, serviceContext);
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
		FileVersion sourceFileVersion, FileVersion targetFileVersion) {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.addParameter(
			"sourceFileVersionId",
			String.valueOf(sourceFileVersion.getFileVersionId()));
		mockLiferayPortletRenderRequest.addParameter(
			"targetFileVersionId",
			String.valueOf(targetFileVersion.getFileVersionId()));

		return mockLiferayPortletRenderRequest;
	}

	@Inject(filter = "mvc.command.name=/document_library/compare_versions")
	private MVCRenderCommand _compareVersionsMVCRenderCommand;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private Group _group;

}
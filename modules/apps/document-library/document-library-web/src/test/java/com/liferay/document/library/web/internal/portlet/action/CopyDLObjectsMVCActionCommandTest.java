/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class CopyDLObjectsMVCActionCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCActionCommand, "_dlSizeLimitConfigurationProvider",
			_dlSizeLimitConfigurationProvider);
		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCActionCommand, "_groupLocalService",
			_groupLocalService);

		Mockito.when(
			_jsonFactory.createJSONObject()
		).thenReturn(
			new JSONObjectImpl()
		);

		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCActionCommand, "_jsonFactory", _jsonFactory);

		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCActionCommand, "_repositoryLocalService",
			_repositoryLocalService);
		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCActionCommand, "_siteConnectedGroupGroupProvider",
			_siteConnectedGroupGroupProvider);

		_jsonPortletResponseUtilMockedStatic.when(
			() -> JSONPortletResponseUtil.writeJSON(
				Mockito.any(ActionRequest.class),
				Mockito.any(ActionResponse.class),
				Mockito.any(JSONObject.class))
		).then(
			invocationOnMock -> null
		);

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any(PortletRequest.class))
		).thenReturn(
			new MockHttpServletRequest()
		);

		portalUtil.setPortal(portal);
	}

	@After
	public void tearDown() throws Exception {
		_jsonPortletResponseUtilMockedStatic.close();
	}

	@Test
	public void testRepositoryGroupIsUsed() throws Exception {
		long destinationGroupId = RandomTestUtil.randomLong();
		long destinationRepositoryId = RandomTestUtil.randomLong();

		_setUpRepository(destinationGroupId, destinationRepositoryId);

		long sourceGroupId = RandomTestUtil.randomLong();

		long sourceRepositoryId = sourceGroupId;

		_setUpRepository(sourceGroupId, sourceRepositoryId);

		_copyDLObjectsMVCActionCommand.doProcessAction(
			_getMockActionRequest(destinationRepositoryId, sourceRepositoryId),
			new MockActionResponse());

		Mockito.verify(
			_groupLocalService
		).fetchGroup(
			destinationRepositoryId
		);

		Mockito.verify(
			_repositoryLocalService
		).getRepository(
			destinationRepositoryId
		);

		Mockito.verify(
			_repositoryLocalService, Mockito.never()
		).getRepository(
			sourceRepositoryId
		);
	}

	private MockActionRequest _getMockActionRequest(
		long destinationRepositoryId, long sourceRepositoryId) {

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.addParameter(
			"destinationParentFolderId",
			String.valueOf(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
		mockActionRequest.addParameter(
			"destinationRepositoryId", String.valueOf(destinationRepositoryId));
		mockActionRequest.addParameter(
			"sourceRepositoryId", String.valueOf(sourceRepositoryId));

		return mockActionRequest;
	}

	private void _setUpRepository(long groupId, long repositoryId)
		throws Exception {

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_groupLocalService.fetchGroup(groupId)
		).thenReturn(
			group
		);

		Mockito.when(
			_groupLocalService.getGroup(groupId)
		).thenReturn(
			group
		);

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(groupId)
		).thenReturn(
			new long[] {groupId}
		);

		if (groupId == repositoryId) {
			return;
		}

		Repository repository = Mockito.mock(Repository.class);

		Mockito.when(
			repository.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_repositoryLocalService.getRepository(repositoryId)
		).thenReturn(
			repository
		);
	}

	private final CopyDLObjectsMVCActionCommand _copyDLObjectsMVCActionCommand =
		new CopyDLObjectsMVCActionCommand() {

			@Override
			protected void hideDefaultSuccessMessage(
				PortletRequest portletRequest) {
			}

		};

	private final DLSizeLimitConfigurationProvider
		_dlSizeLimitConfigurationProvider = Mockito.mock(
			DLSizeLimitConfigurationProvider.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final JSONFactory _jsonFactory = Mockito.mock(JSONFactory.class);
	private final MockedStatic<JSONPortletResponseUtil>
		_jsonPortletResponseUtilMockedStatic = Mockito.mockStatic(
			JSONPortletResponseUtil.class);
	private final RepositoryLocalService _repositoryLocalService = Mockito.mock(
		RepositoryLocalService.class);
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider = Mockito.mock(
			SiteConnectedGroupGroupProvider.class);

}
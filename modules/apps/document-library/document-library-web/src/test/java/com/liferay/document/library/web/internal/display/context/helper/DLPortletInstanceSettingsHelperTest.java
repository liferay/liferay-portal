/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.helper;

import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.web.internal.constants.DLWebKeys;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
public class DLPortletInstanceSettingsHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetSelectedRepositoryIdWhenScopeGroupIsNotStaging()
		throws Exception {

		Group liveGroup = Mockito.mock(Group.class);

		Mockito.when(
			liveGroup.getGroupId()
		).thenReturn(
			_LIVE_GROUP_ID
		);

		Group stagingGroup = Mockito.mock(Group.class);

		Mockito.when(
			stagingGroup.getLiveGroup()
		).thenReturn(
			liveGroup
		);

		Mockito.when(
			stagingGroup.isStagingGroup()
		).thenReturn(
			true
		);

		Group scopeGroup = Mockito.mock(Group.class);

		Mockito.when(
			scopeGroup.isStagingGroup()
		).thenReturn(
			false
		);

		try (MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class);
			MockedStatic<RepositoryLocalServiceUtil>
				repositoryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RepositoryLocalServiceUtil.class)) {

			Mockito.when(
				GroupLocalServiceUtil.getGroupByExternalReferenceCode(
					_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE, _COMPANY_ID)
			).thenReturn(
				stagingGroup
			);

			Repository repository = Mockito.mock(Repository.class);

			Mockito.when(
				repository.getRepositoryId()
			).thenReturn(
				_LIVE_REPOSITORY_ID
			);

			Mockito.when(
				RepositoryLocalServiceUtil.getRepositoryByExternalReferenceCode(
					_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE,
					_LIVE_GROUP_ID)
			).thenReturn(
				repository
			);

			DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper =
				_createDLPortletInstanceSettingsHelper(scopeGroup);

			Assert.assertEquals(
				_LIVE_REPOSITORY_ID,
				dlPortletInstanceSettingsHelper.getSelectedRepositoryId());
		}
	}

	@Test
	public void testGetSelectedRepositoryIdWhenScopeGroupIsStaging()
		throws Exception {

		Group stagingGroup = Mockito.mock(Group.class);

		Mockito.when(
			stagingGroup.getGroupId()
		).thenReturn(
			_STAGING_GROUP_ID
		);

		Mockito.when(
			stagingGroup.isStagingGroup()
		).thenReturn(
			true
		);

		Group scopeGroup = Mockito.mock(Group.class);

		Mockito.when(
			scopeGroup.isStagingGroup()
		).thenReturn(
			true
		);

		try (MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class);
			MockedStatic<RepositoryLocalServiceUtil>
				repositoryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RepositoryLocalServiceUtil.class)) {

			Mockito.when(
				GroupLocalServiceUtil.getGroupByExternalReferenceCode(
					_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE, _COMPANY_ID)
			).thenReturn(
				stagingGroup
			);

			Repository repository = Mockito.mock(Repository.class);

			Mockito.when(
				repository.getRepositoryId()
			).thenReturn(
				_STAGING_REPOSITORY_ID
			);

			Mockito.when(
				RepositoryLocalServiceUtil.getRepositoryByExternalReferenceCode(
					_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE,
					_STAGING_GROUP_ID)
			).thenReturn(
				repository
			);

			DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper =
				_createDLPortletInstanceSettingsHelper(scopeGroup);

			Assert.assertEquals(
				_STAGING_REPOSITORY_ID,
				dlPortletInstanceSettingsHelper.getSelectedRepositoryId());
		}
	}

	@Test
	public void testGetSelectedRepositoryIdWhenSelectedGroupIsNotStaging()
		throws Exception {

		Group liveGroup = Mockito.mock(Group.class);

		Mockito.when(
			liveGroup.getGroupId()
		).thenReturn(
			_LIVE_GROUP_ID
		);

		Mockito.when(
			liveGroup.isStagingGroup()
		).thenReturn(
			false
		);

		Group scopeGroup = Mockito.mock(Group.class);

		Mockito.when(
			scopeGroup.isStagingGroup()
		).thenReturn(
			false
		);

		try (MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class);
			MockedStatic<RepositoryLocalServiceUtil>
				repositoryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RepositoryLocalServiceUtil.class)) {

			Mockito.when(
				GroupLocalServiceUtil.getGroupByExternalReferenceCode(
					_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE, _COMPANY_ID)
			).thenReturn(
				liveGroup
			);

			Repository repository = Mockito.mock(Repository.class);

			Mockito.when(
				repository.getRepositoryId()
			).thenReturn(
				_LIVE_REPOSITORY_ID
			);

			Mockito.when(
				RepositoryLocalServiceUtil.getRepositoryByExternalReferenceCode(
					_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE,
					_LIVE_GROUP_ID)
			).thenReturn(
				repository
			);

			DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper =
				_createDLPortletInstanceSettingsHelper(scopeGroup);

			Assert.assertEquals(
				_LIVE_REPOSITORY_ID,
				dlPortletInstanceSettingsHelper.getSelectedRepositoryId());
		}
	}

	private DLPortletInstanceSettingsHelper
		_createDLPortletInstanceSettingsHelper(Group scopeGroup) {

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			scopeGroup
		);

		DLPortletInstanceSettings dlPortletInstanceSettings =
			_mockDLPortletInstanceSettings();

		Mockito.when(
			dlPortletInstanceSettings.getSelectedGroupExternalReferenceCode()
		).thenReturn(
			_SELECTED_GROUP_EXTERNAL_REFERENCE_CODE
		);

		Mockito.when(
			dlPortletInstanceSettings.
				getSelectedRepositoryExternalReferenceCode()
		).thenReturn(
			_SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			DLWebKeys.DOCUMENT_LIBRARY_PORTLET_INSTANCE_SETTINGS,
			dlPortletInstanceSettings);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return new DLPortletInstanceSettingsHelper(
			new DLRequestHelper(mockHttpServletRequest));
	}

	private DLPortletInstanceSettings _mockDLPortletInstanceSettings() {
		try (MockedStatic<DLUtil> dlUtilMockedStatic = Mockito.mockStatic(
				DLUtil.class)) {

			dlUtilMockedStatic.when(
				DLUtil::getAllMediaGalleryMimeTypes
			).thenReturn(
				Collections.emptySet()
			);

			return Mockito.mock(DLPortletInstanceSettings.class);
		}
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _LIVE_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _LIVE_REPOSITORY_ID = RandomTestUtil.randomLong();

	private static final String _SELECTED_GROUP_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final String _SELECTED_REPOSITORY_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _STAGING_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _STAGING_REPOSITORY_ID =
		RandomTestUtil.randomLong();

}
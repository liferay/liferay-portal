/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class DLAppServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_dlAppServiceImpl, "_assetCategoryLocalService",
			_assetCategoryLocalService);
		ReflectionTestUtil.setFieldValue(
			_dlAppServiceImpl, "_assetTagLocalService", _assetTagLocalService);
		ReflectionTestUtil.setFieldValue(
			_dlAppServiceImpl, "_ratingsEntryLocalService",
			_ratingsEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_dlAppServiceImpl, "_resourcePermissionLocalService",
			_resourcePermissionLocalService);
	}

	@After
	public void tearDown() {
		_dLValidatorUtilMockedStatic.close();
		_guestOrUserUtilMockedStatic.close();
		_repositoryUtilMockedStatic.close();
	}

	@Test
	public void testCopyResourcesWhenRepositoryIsNotExternal()
		throws Exception {

		_setUpDependencies();

		_dlAppServiceImpl.copyFileEntry(
			_destinationRepository, _sourceFileEntry,
			RandomTestUtil.randomLong(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			new long[0], new ServiceContext());

		Mockito.verify(
			_resourcePermissionLocalService,
			Mockito.times(ResourceConstants.SCOPES.length)
		).deleteResourcePermissions(
			Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(),
			Mockito.anyLong()
		);

		Mockito.verify(
			_resourcePermissionLocalService, Mockito.times(1)
		).copyModelResourcePermissions(
			Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
			Mockito.anyLong()
		);
	}

	@Test
	public void testSkipCopyResourcesWhenRepositoryIsExternal()
		throws Exception {

		_setUpDependencies();

		_repositoryUtilMockedStatic.when(
			() -> RepositoryUtil.isExternalRepository(Mockito.anyLong())
		).thenReturn(
			true
		);

		_dlAppServiceImpl.copyFileEntry(
			_destinationRepository, _sourceFileEntry,
			RandomTestUtil.randomLong(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			new long[0], new ServiceContext());

		_repositoryUtilMockedStatic.verify(
			() -> RepositoryUtil.isExternalRepository(Mockito.anyLong()),
			Mockito.times(1));

		Mockito.verifyNoInteractions(_resourcePermissionLocalService);
	}

	private void _setUpDependencies() throws Exception {
		Mockito.when(
			_sourceFileEntry.getFileVersions(WorkflowConstants.STATUS_ANY)
		).thenReturn(
			Arrays.asList(Mockito.mock(FileVersion.class))
		);

		Mockito.when(
			_assetCategoryLocalService.getCategoryIds(
				DLFileEntryConstants.getClassName(),
				_sourceFileEntry.getFileEntryId())
		).thenReturn(
			new long[0]
		);

		Mockito.when(
			_assetTagLocalService.getTagNames(
				DLFileEntryConstants.getClassName(),
				_sourceFileEntry.getFileEntryId())
		).thenReturn(
			new String[0]
		);

		_guestOrUserUtilMockedStatic.when(
			GuestOrUserUtil::getUserId
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_destinationRepository.addFileEntry(
				Mockito.isNull(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
				Mockito.isNull(), Mockito.isNull(), Mockito.anyString(),
				Mockito.isNull(), Mockito.anyLong(), Mockito.isNull(),
				Mockito.isNull(), Mockito.isNull(),
				Mockito.any(ServiceContext.class))
		).thenReturn(
			_destinationFileEntry
		);

		Mockito.when(
			_ratingsEntryLocalService.getEntries(
				DLFileEntryConstants.getClassName(),
				_sourceFileEntry.getFileEntryId())
		).thenReturn(
			Collections.emptyList()
		);
	}

	private final AssetCategoryLocalService _assetCategoryLocalService =
		Mockito.mock(AssetCategoryLocalService.class);
	private final AssetTagLocalService _assetTagLocalService = Mockito.mock(
		AssetTagLocalService.class);
	private final MockedStatic<DLValidatorUtil> _dLValidatorUtilMockedStatic =
		Mockito.mockStatic(DLValidatorUtil.class);
	private final FileEntry _destinationFileEntry = Mockito.mock(
		FileEntry.class);
	private final Repository _destinationRepository = Mockito.mock(
		Repository.class);
	private final DLAppServiceImpl _dlAppServiceImpl = new DLAppServiceImpl();
	private final MockedStatic<GuestOrUserUtil> _guestOrUserUtilMockedStatic =
		Mockito.mockStatic(GuestOrUserUtil.class);
	private final RatingsEntryLocalService _ratingsEntryLocalService =
		Mockito.mock(RatingsEntryLocalService.class);
	private final MockedStatic<RepositoryUtil> _repositoryUtilMockedStatic =
		Mockito.mockStatic(RepositoryUtil.class);
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService = Mockito.mock(
			ResourcePermissionLocalService.class);
	private final FileEntry _sourceFileEntry = Mockito.mock(FileEntry.class);

}
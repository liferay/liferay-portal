/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Marcela Cunha
 */
public class CMPObjectEntryModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_cmpObjectEntryModelDocumentContributor =
			new CMPObjectEntryModelDocumentContributor(
				_filterFactory, _groupLocalService,
				_objectDefinitionLocalService, _objectEntryFolderLocalService,
				_objectEntryLocalService);
	}

	@Test
	public void testContributeWhenCMPIsDisabled() {
		try (MockedStatic<LicenseManagerUtil> licenseManagerUtilMockedStatic =
				Mockito.mockStatic(LicenseManagerUtil.class)) {

			licenseManagerUtilMockedStatic.when(
				() -> LicenseManagerUtil.isAppEnabled(App.CMP)
			).thenReturn(
				false
			);

			_cmpObjectEntryModelDocumentContributor.contribute(
				Mockito.mock(Document.class), Mockito.mock(ObjectEntry.class));

			Mockito.verifyNoInteractions(_objectEntryFolderLocalService);
		}
	}

	@Test
	public void testContributeWithObjectEntryFolder() {
		try (MockedStatic<LicenseManagerUtil> licenseManagerUtilMockedStatic =
				Mockito.mockStatic(LicenseManagerUtil.class)) {

			licenseManagerUtilMockedStatic.when(
				() -> LicenseManagerUtil.isAppEnabled(App.CMP)
			).thenReturn(
				true
			);

			ObjectEntryFolder objectEntryFolder = Mockito.mock(
				ObjectEntryFolder.class);

			long childObjectEntryFolderId = RandomTestUtil.randomLong();
			long parentObjectEntryFolderId = RandomTestUtil.randomLong();
			long rootObjectEntryFolderId = RandomTestUtil.randomLong();

			Mockito.when(
				objectEntryFolder.getTreePath()
			).thenReturn(
				StringBundler.concat(
					StringPool.SLASH, rootObjectEntryFolderId, StringPool.SLASH,
					parentObjectEntryFolderId, StringPool.SLASH,
					childObjectEntryFolderId, StringPool.SLASH)
			);

			Mockito.when(
				_objectEntryFolderLocalService.fetchObjectEntryFolder(
					childObjectEntryFolderId)
			).thenReturn(
				objectEntryFolder
			);

			ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

			Mockito.when(
				objectEntry.getObjectEntryFolderId()
			).thenReturn(
				childObjectEntryFolderId
			);

			_cmpObjectEntryModelDocumentContributor.contribute(
				Mockito.mock(Document.class), objectEntry);

			Mockito.verify(
				_objectEntryFolderLocalService
			).fetchObjectEntryFolder(
				rootObjectEntryFolderId
			);

			Mockito.reset(_objectEntryFolderLocalService);

			Mockito.when(
				objectEntryFolder.getTreePath()
			).thenReturn(
				StringBundler.concat(
					StringPool.SLASH, rootObjectEntryFolderId, StringPool.SLASH)
			);

			Mockito.when(
				_objectEntryFolderLocalService.fetchObjectEntryFolder(
					rootObjectEntryFolderId)
			).thenReturn(
				objectEntryFolder
			);

			Mockito.when(
				objectEntry.getObjectEntryFolderId()
			).thenReturn(
				rootObjectEntryFolderId
			);

			_cmpObjectEntryModelDocumentContributor.contribute(
				Mockito.mock(Document.class), objectEntry);

			Mockito.verify(
				_objectEntryFolderLocalService
			).fetchObjectEntryFolder(
				rootObjectEntryFolderId
			);
		}
	}

	private CMPObjectEntryModelDocumentContributor
		_cmpObjectEntryModelDocumentContributor;
	private final FilterFactory<Predicate> _filterFactory = Mockito.mock(
		FilterFactory.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService =
		Mockito.mock(ObjectEntryFolderLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class PIMProductObjectEntryModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testContribute() {
		ReflectionTestUtil.setFieldValue(
			_pimProductObjectEntryModelDocumentContributor,
			"_objectEntryFolderLocalService", _objectEntryFolderLocalService);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				false
			);

			Document document = Mockito.mock(Document.class);

			_pimProductObjectEntryModelDocumentContributor.contribute(
				document, _mockObjectEntry(RandomTestUtil.randomLong()));

			Mockito.verifyNoInteractions(document);

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			document = Mockito.mock(Document.class);

			_pimProductObjectEntryModelDocumentContributor.contribute(
				document, _mockObjectEntry(RandomTestUtil.randomLong()));

			Mockito.verifyNoInteractions(document);

			_mockObjectEntryFolder(100L, true);

			document = Mockito.mock(Document.class);

			_pimProductObjectEntryModelDocumentContributor.contribute(
				document, _mockObjectEntry(100L));

			Mockito.verify(
				document
			).addKeyword(
				"cms_root", true
			);

			Mockito.verify(
				document
			).addKeyword(
				"cms_section", "products"
			);

			_mockObjectEntryFolder(200L, false);

			document = Mockito.mock(Document.class);

			_pimProductObjectEntryModelDocumentContributor.contribute(
				document, _mockObjectEntry(200L));

			Mockito.verify(
				document
			).addKeyword(
				"cms_root", false
			);

			Mockito.verify(
				document
			).addKeyword(
				"cms_section", "products"
			);
		}
	}

	private ObjectEntry _mockObjectEntry(long objectEntryFolderId) {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getObjectEntryFolderId()
		).thenReturn(
			objectEntryFolderId
		);

		return objectEntry;
	}

	private void _mockObjectEntryFolder(
		long objectEntryFolderId, boolean root) {

		ObjectEntryFolder objectEntryFolder = Mockito.mock(
			ObjectEntryFolder.class);

		Mockito.when(
			objectEntryFolder.isRoot()
		).thenReturn(
			root
		);

		Mockito.when(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolderId)
		).thenReturn(
			objectEntryFolder
		);
	}

	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService =
		Mockito.mock(ObjectEntryFolderLocalService.class);
	private final PIMProductObjectEntryModelDocumentContributor
		_pimProductObjectEntryModelDocumentContributor =
			new PIMProductObjectEntryModelDocumentContributor();

}
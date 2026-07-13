/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectEntryFolderConstants;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class PIMProductObjectEntryModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_pimProductObjectEntryModelDocumentContributor,
			"_objectEntryFolderLocalService", _objectEntryFolderLocalService);
	}

	@Test
	public void testContribute() {
		Document document = Mockito.mock(Document.class);

		_pimProductObjectEntryModelDocumentContributor.contribute(
			document, _mockObjectEntry(RandomTestUtil.randomLong()));

		Mockito.verifyNoInteractions(document);

		_mockObjectEntryFolder(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS, 100L,
			"/0/100/");

		document = Mockito.mock(Document.class);

		_pimProductObjectEntryModelDocumentContributor.contribute(
			document, _mockObjectEntry(100L));

		Mockito.verifyNoInteractions(document);

		_mockObjectEntryFolder(
			PIMObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCTS,
			200L, "/200/");

		document = Mockito.mock(Document.class);

		_pimProductObjectEntryModelDocumentContributor.contribute(
			document, _mockObjectEntry(200L));

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

		_mockObjectEntryFolder(
			RandomTestUtil.randomString(), 300L, "/200/300/");

		document = Mockito.mock(Document.class);

		_pimProductObjectEntryModelDocumentContributor.contribute(
			document, _mockObjectEntry(300L));

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

	private ObjectEntry _mockObjectEntry(long objectEntryFolderId) {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getObjectEntryFolderId()
		).thenReturn(
			objectEntryFolderId
		);

		return objectEntry;
	}

	private ObjectEntryFolder _mockObjectEntryFolder(
		String externalReferenceCode, long objectEntryFolderId,
		String treePath) {

		ObjectEntryFolder objectEntryFolder = Mockito.mock(
			ObjectEntryFolder.class);

		Mockito.when(
			objectEntryFolder.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			objectEntryFolder.getObjectEntryFolderId()
		).thenReturn(
			objectEntryFolderId
		);

		Mockito.when(
			objectEntryFolder.getTreePath()
		).thenReturn(
			treePath
		);

		Mockito.when(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolderId)
		).thenReturn(
			objectEntryFolder
		);

		return objectEntryFolder;
	}

	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService =
		Mockito.mock(ObjectEntryFolderLocalService.class);
	private final PIMProductObjectEntryModelDocumentContributor
		_pimProductObjectEntryModelDocumentContributor =
			new PIMProductObjectEntryModelDocumentContributor();

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class ExportFragmentCollectionsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_fragmentCollection.getFragmentCollectionId()
		).thenReturn(
			_FRAGMENT_COLLECTION_ID
		);

		Mockito.when(
			_fragmentCollection.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		_timeMockedStatic.when(
			Time::getTimestamp
		).thenReturn(
			String.valueOf(RandomTestUtil.randomLong())
		);

		File file = File.createTempFile("fragment-collections", ".zip");

		file.deleteOnExit();

		_zipWriterMockedConstruction = Mockito.mockConstruction(
			ZipWriter.class,
			(zipWriter, context) -> Mockito.when(
				zipWriter.getFile()
			).thenReturn(
				file
			));

		ReflectionTestUtil.setFieldValue(
			_exportFragmentCollectionsMVCResourceCommand,
			"_fragmentCollectionLocalService", _fragmentCollectionLocalService);
		ReflectionTestUtil.setFieldValue(
			_exportFragmentCollectionsMVCResourceCommand,
			"_portletResourcePermission", _portletResourcePermission);
	}

	@After
	public void tearDown() {
		_timeMockedStatic.close();
		_zipWriterMockedConstruction.close();
	}

	@Test
	@TestInfo("LPD-82487")
	public void testServeResourceWithEmptyFragmentCollections()
		throws Exception {

		_mockExportableFragmentCollections(List.of());

		_exportFragmentCollectionsMVCResourceCommand.serveResource(
			_getMockLiferayResourceRequest(),
			new MockLiferayResourceResponse());

		Mockito.verify(
			_fragmentCollection, Mockito.never()
		).populateZipWriter(
			Mockito.any()
		);
	}

	@Test
	@TestInfo("LPD-82487")
	public void testServeResourceWithFragmentCollections() throws Exception {
		_mockExportableFragmentCollections(List.of(_fragmentCollection));

		_exportFragmentCollectionsMVCResourceCommand.serveResource(
			_getMockLiferayResourceRequest(),
			new MockLiferayResourceResponse());

		List<ZipWriter> zipWriters = _zipWriterMockedConstruction.constructed();

		Assert.assertEquals(zipWriters.toString(), 1, zipWriters.size());

		Mockito.verify(
			_fragmentCollection
		).populateZipWriter(
			zipWriters.get(0)
		);
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest() {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyGroupId()
		).thenReturn(
			_COMPANY_GROUP_ID
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.isCompany()
		).thenReturn(
			false
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			themeDisplay.getScopeGroupId()
		).thenReturn(
			_GROUP_ID
		);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter(
			"fragmentCollectionId", String.valueOf(_FRAGMENT_COLLECTION_ID));

		return mockLiferayResourceRequest;
	}

	private void _mockExportableFragmentCollections(
		List<FragmentCollection> fragmentCollections) {

		Mockito.when(
			_fragmentCollectionLocalService.getExportableFragmentCollections(
				new long[] {_FRAGMENT_COLLECTION_ID})
		).thenReturn(
			fragmentCollections
		);
	}

	private static final long _COMPANY_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _FRAGMENT_COLLECTION_ID =
		RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final ExportFragmentCollectionsMVCResourceCommand
		_exportFragmentCollectionsMVCResourceCommand =
			new ExportFragmentCollectionsMVCResourceCommand();
	private final FragmentCollection _fragmentCollection = Mockito.mock(
		FragmentCollection.class);
	private final FragmentCollectionLocalService
		_fragmentCollectionLocalService = Mockito.mock(
			FragmentCollectionLocalService.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);
	private final MockedStatic<Time> _timeMockedStatic = Mockito.mockStatic(
		Time.class);
	private MockedConstruction<ZipWriter> _zipWriterMockedConstruction;

}
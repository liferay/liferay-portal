/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.nio.file.Files;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class ExportFragmentCollectionsMVCResourceCommandTest {

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

	@After
	public void tearDown() throws Exception {
		if (_fragmentCollection != null) {
			_fragmentCollectionLocalService.deleteFragmentCollection(
				_fragmentCollection.getFragmentCollectionId());
		}
	}

	@Test
	@TestInfo("LPD-83557")
	public void testExportFragmentCollectionWithResourceFromCompanyGroup()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		Group companyGroup = _groupLocalService.getCompanyGroup(
			company.getCompanyId());

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			companyGroup.getGroupId());

		String resourceFileName = RandomTestUtil.randomString() + ".png";

		PortletFileRepositoryUtil.addPortletFileEntry(
			null, companyGroup.getGroupId(), TestPropsValues.getUserId(),
			FragmentCollection.class.getName(),
			_fragmentCollection.getFragmentCollectionId(),
			FragmentPortletKeys.FRAGMENT,
			_fragmentCollection.getResourcesFolderId(),
			new ByteArrayInputStream(new byte[] {1, 2, 3, 4}), resourceFileName,
			ContentTypes.IMAGE_PNG, false);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter(
			"fragmentCollectionId",
			String.valueOf(_fragmentCollection.getFragmentCollectionId()));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		byte[] bytes = byteArrayOutputStream.toByteArray();

		Assert.assertTrue(bytes.length > 0);

		File tempFile = FileUtil.createTempFile(
			RandomTestUtil.randomString(), ".zip");

		try {
			Files.write(tempFile.toPath(), bytes);

			try (ZipReader zipReader = _zipReaderFactory.getZipReader(
					tempFile)) {

				boolean found = false;

				for (String entry : zipReader.getEntries()) {
					if (entry.contains("/resources/" + resourceFileName)) {
						found = true;

						break;
					}
				}

				Assert.assertTrue(found);
			}
		}
		finally {
			FileUtil.delete(tempFile);
		}
	}

	@Test
	@TestInfo("LPD-83557")
	public void testExportFragmentCollectionsWithoutPermissions()
		throws Exception {

		User user = UserTestUtil.addGroupUser(_group, RoleConstants.POWER_USER);

		FragmentCollection fragmentCollection1 =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection1.getFragmentCollectionId());

		FragmentCollection fragmentCollection2 =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection2.getFragmentCollectionId());

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			MockLiferayResourceRequest mockLiferayResourceRequest =
				_getMockLiferayResourceRequest(
					company, fragmentCollection1.getFragmentCollectionId(),
					user);

			PortletException portletException = Assert.assertThrows(
				PortletException.class,
				() -> _mvcResourceCommand.serveResource(
					mockLiferayResourceRequest,
					new MockLiferayResourceResponse()));

			Assert.assertTrue(
				portletException.getCause() instanceof PrincipalException);
		}
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			Company company, long fragmentCollectionId, User user)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter(
			"fragmentCollectionId", String.valueOf(fragmentCollectionId));

		return mockLiferayResourceRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(filter = "mvc.command.name=/fragment/export_fragment_collections")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}
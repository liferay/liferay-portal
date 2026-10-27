/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.StagingServiceUtil;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vendel Töreki
 */
@RunWith(Arquillian.class)
public class StagingServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_targetGroup = GroupTestUtil.addGroup();

		_targetLayout = LayoutTestUtil.addTypePortletLayout(_targetGroup);

		_user = UserTestUtil.addGroupAdminUser(_group);
	}

	@Test
	@TestInfo("LPD-110878")
	public void testCleanUpStagingRequest() throws Exception {
		_testCleanUpStagingRequest();
		_testCleanUpStagingRequestWithFolder();
	}

	@Test
	@TestInfo({"LPD-110192", "LPD-110878"})
	public void testPublishStagingRequest() throws Exception {
		_testPublishStagingRequest();
		_testPublishStagingRequestToAnotherGroup();
		_testPublishStagingRequestWithFolder();
		_testPublishStagingRequestWithPortlet();
		_testPublishStagingRequestWithPortletToControlPanelLayout();
		_testPublishStagingRequestWithPortletToLayoutInAnotherGroup();
	}

	@Test
	@TestInfo("LPD-110878")
	public void testUpdateStagingRequest() throws Exception {
		_testUpdateStagingRequestWithFolder();
	}

	private ExportImportConfiguration _addExportImportConfiguration(
			Group targetGroup)
		throws Exception {

		Map<String, Serializable> settingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildImportLayoutSettingsMap(
					_user, targetGroup.getGroupId(), false, new long[0],
					new HashMap<>());

		return ExportImportConfigurationLocalServiceUtil.
			addExportImportConfiguration(
				_user.getUserId(), targetGroup.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_REMOTE,
				settingsMap,
				ServiceContextTestUtil.getServiceContext(
					targetGroup.getGroupId(), _user.getUserId()));
	}

	private ExportImportConfiguration _addPortletExportImportConfiguration(
			long targetPlid)
		throws Exception {

		Map<String, Serializable> settingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildImportPortletSettingsMap(
					_user, targetPlid, _group.getGroupId(),
					DLPortletKeys.DOCUMENT_LIBRARY, new HashMap<>());

		return ExportImportConfigurationLocalServiceUtil.
			addExportImportConfiguration(
				_user.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ExportImportConfigurationConstants.TYPE_PUBLISH_PORTLET_REMOTE,
				settingsMap,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _user.getUserId()));
	}

	private long _addStagingRequest(File file) throws Exception {
		long stagingRequestId = StagingServiceUtil.createStagingRequest(
			_group.getGroupId(), FileUtil.getMD5Checksum(file));

		StagingServiceUtil.updateStagingRequest(
			stagingRequestId, file.getName(), FileUtil.getBytes(file));

		return stagingRequestId;
	}

	private void _testCleanUpStagingRequest() throws Exception {
		File file = ExportImportTestUtil.exportLayoutsAsFile(_group, _layout);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			long stagingRequestId = _addStagingRequest(file);

			StagingServiceUtil.cleanUpStagingRequest(stagingRequestId);

			Assert.assertThrows(
				NoSuchFolderException.class,
				() -> PortletFileRepositoryUtil.getPortletFolder(
					stagingRequestId));
		}
		finally {
			FileUtil.delete(file);
		}
	}

	private void _testCleanUpStagingRequestWithFolder() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		DLAppTestUtil.addFileEntry(folder);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> StagingServiceUtil.cleanUpStagingRequest(
					folder.getFolderId()));
		}

		Assert.assertEquals(
			1,
			DLFileEntryLocalServiceUtil.getFileEntriesCount(
				_group.getGroupId(), folder.getFolderId()));
	}

	private void _testPublishStagingRequest() throws Exception {
		File file = ExportImportTestUtil.exportLayoutsAsFile(_group, _layout);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertNotNull(
				StagingServiceUtil.publishStagingRequest(
					_addStagingRequest(file),
					_addExportImportConfiguration(_group)));
		}
		finally {
			FileUtil.delete(file);
		}
	}

	private void _testPublishStagingRequestToAnotherGroup() throws Exception {
		File file = ExportImportTestUtil.exportLayoutsAsFile(_group, _layout);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			long stagingRequestId = _addStagingRequest(file);
			ExportImportConfiguration exportImportConfiguration =
				_addExportImportConfiguration(_targetGroup);

			Assert.assertThrows(
				PrincipalException.class,
				() -> StagingServiceUtil.publishStagingRequest(
					stagingRequestId, exportImportConfiguration));
		}
		finally {
			FileUtil.delete(file);
		}

		Assert.assertEquals(
			1, LayoutLocalServiceUtil.getLayoutsCount(_targetGroup, false));
	}

	private void _testPublishStagingRequestWithFolder() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			ExportImportConfiguration exportImportConfiguration =
				_addExportImportConfiguration(_group);

			Assert.assertThrows(
				PrincipalException.class,
				() -> StagingServiceUtil.publishStagingRequest(
					folder.getFolderId(), exportImportConfiguration));
		}
	}

	private void _testPublishStagingRequestWithPortlet() throws Exception {
		File file = ExportImportTestUtil.exportPortletInfoAsFile(
			_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertNotNull(
				StagingServiceUtil.publishStagingRequest(
					_addStagingRequest(file),
					_addPortletExportImportConfiguration(_layout.getPlid())));
		}
		finally {
			FileUtil.delete(file);
		}
	}

	private void _testPublishStagingRequestWithPortletToControlPanelLayout()
		throws Exception {

		File file = ExportImportTestUtil.exportPortletInfoAsFile(
			_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertNotNull(
				StagingServiceUtil.publishStagingRequest(
					_addStagingRequest(file),
					_addPortletExportImportConfiguration(
						PortalUtil.getControlPanelPlid(
							_group.getCompanyId()))));
		}
		finally {
			FileUtil.delete(file);
		}
	}

	private void _testPublishStagingRequestWithPortletToLayoutInAnotherGroup()
		throws Exception {

		File file = ExportImportTestUtil.exportPortletInfoAsFile(
			_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			long stagingRequestId = _addStagingRequest(file);
			ExportImportConfiguration exportImportConfiguration =
				_addPortletExportImportConfiguration(_targetLayout.getPlid());

			Assert.assertThrows(
				PrincipalException.class,
				() -> StagingServiceUtil.publishStagingRequest(
					stagingRequestId, exportImportConfiguration));
		}
		finally {
			FileUtil.delete(file);
		}
	}

	private void _testUpdateStagingRequestWithFolder() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		DLAppTestUtil.addFileEntry(folder);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> StagingServiceUtil.updateStagingRequest(
					folder.getFolderId(), RandomTestUtil.randomString(),
					RandomTestUtil.randomBytes()));
		}

		Assert.assertEquals(
			1,
			DLFileEntryLocalServiceUtil.getFileEntriesCount(
				_group.getGroupId(), folder.getFolderId()));
	}

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@DeleteAfterTestRun
	private Group _targetGroup;

	private Layout _targetLayout;

	@DeleteAfterTestRun
	private User _user;

}
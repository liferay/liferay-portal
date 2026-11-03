/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class EditEntryMVCActionCommandTest {

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
	public void testCheckIn() throws PortalException, PortletException {
		FileEntry initialFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN, null, null,
			null, null, ServiceContextTestUtil.getServiceContext());

		_dlAppService.checkOutFileEntry(
			initialFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_mvcActionCommand.processAction(
			new MockActionRequest(
				company, _group,
				HashMapBuilder.putAll(
					_getParameters(initialFileEntry, Constants.CHECKIN)
				).put(
					"changeLog", new String[] {"New Version"}
				).put(
					"rowIdsFileEntry",
					new String[] {
						String.valueOf(initialFileEntry.getFileEntryId())
					}
				).put(
					"versionIncrease",
					new String[] {String.valueOf(DLVersionNumberIncrease.MAJOR)}
				).build()),
			new MockActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntry(
			initialFileEntry.getFileEntryId());

		FileVersion fileVersion = actualFileEntry.getFileVersion();

		Assert.assertEquals("New Version", fileVersion.getChangeLog());
	}

	@Test
	public void testCheckInAll() throws PortalException, PortletException {
		FileEntry initialFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN, null, null,
			null, null, ServiceContextTestUtil.getServiceContext());

		_dlAppService.checkOutFileEntry(
			initialFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_mvcActionCommand.processAction(
			new MockActionRequest(
				company, _group,
				HashMapBuilder.putAll(
					_getParameters(initialFileEntry, Constants.CHECKIN)
				).put(
					"changeLog", new String[] {"New Version"}
				).put(
					"selectAll", new String[] {String.valueOf(Boolean.TRUE)}
				).put(
					"versionIncrease",
					new String[] {String.valueOf(DLVersionNumberIncrease.MAJOR)}
				).build()),
			new MockActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntry(
			initialFileEntry.getFileEntryId());

		FileVersion fileVersion = actualFileEntry.getFileVersion();

		Assert.assertEquals("New Version", fileVersion.getChangeLog());
	}

	@Test
	public void testCheckOut() throws PortalException, PortletException {
		FileEntry initialFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN, null, null,
			null, null, ServiceContextTestUtil.getServiceContext());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_mvcActionCommand.processAction(
			new MockActionRequest(
				company, _group,
				HashMapBuilder.putAll(
					_getParameters(initialFileEntry, Constants.CHECKOUT)
				).put(
					"rowIdsFileEntry",
					new String[] {
						String.valueOf(initialFileEntry.getFileEntryId())
					}
				).build()),
			new MockActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntry(
			initialFileEntry.getFileEntryId());

		Assert.assertTrue(actualFileEntry.isCheckedOut());
	}

	@Test
	public void testCheckOutAll() throws PortalException, PortletException {
		FileEntry initialFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN, null, null,
			null, null, ServiceContextTestUtil.getServiceContext());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_mvcActionCommand.processAction(
			new MockActionRequest(
				company, _group,
				HashMapBuilder.putAll(
					_getParameters(initialFileEntry, Constants.CHECKOUT)
				).put(
					"selectAll", new String[] {String.valueOf(Boolean.TRUE)}
				).build()),
			new MockActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntry(
			initialFileEntry.getFileEntryId());

		Assert.assertTrue(actualFileEntry.isCheckedOut());
	}

	@Test
	@TestInfo("LPD-113588")
	public void testMoveEntries() throws Exception {
		FileEntry fileEntry1 = _addFileEntry();
		FileEntry fileEntry2 = _addFileEntry();
		FileEntry fileEntry3 = _addFileEntry();

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_mvcActionCommand.processAction(
			new MockActionRequest(
				company, _group,
				HashMapBuilder.putAll(
					_getParameters(fileEntry1, Constants.MOVE)
				).put(
					"newFolderId",
					new String[] {String.valueOf(folder.getFolderId())}
				).put(
					"rowIdsFileEntry",
					new String[] {
						StringBundler.concat(
							fileEntry1.getFileEntryId(), StringPool.COMMA,
							fileEntry2.getFileEntryId())
					}
				).build()),
			new MockActionResponse());

		Assert.assertEquals(folder.getFolderId(), _getFolderId(fileEntry1));
		Assert.assertEquals(folder.getFolderId(), _getFolderId(fileEntry2));
		Assert.assertEquals(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_getFolderId(fileEntry3));
	}

	private FileEntry _addFileEntry() throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN, null, null,
			null, null, ServiceContextTestUtil.getServiceContext());
	}

	private long _getFolderId(FileEntry fileEntry) throws Exception {
		fileEntry = _dlAppLocalService.getFileEntry(fileEntry.getFileEntryId());

		return fileEntry.getFolderId();
	}

	private Map<String, String[]> _getParameters(
		FileEntry tempFileEntry, String cmd) {

		return HashMapBuilder.put(
			Constants.CMD, new String[] {cmd}
		).put(
			"folderId",
			new String[] {String.valueOf(tempFileEntry.getFolderId())}
		).put(
			"repositoryId",
			new String[] {String.valueOf(tempFileEntry.getRepositoryId())}
		).build();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.portlet.action.EditEntryMVCActionCommand"
	)
	private MVCActionCommand _mvcActionCommand;

	private static class MockActionRequest
		extends MockLiferayPortletActionRequest {

		public MockActionRequest(
			Company company, Group group, Map<String, String[]> parameters) {

			_company = company;
			_group = group;
			_parameters = Collections.unmodifiableMap(parameters);
		}

		@Override
		public Object getAttribute(String name) {
			if (Objects.equals(name, WebKeys.THEME_DISPLAY)) {
				try {
					return _getThemeDisplay();
				}
				catch (PortalException portalException) {
					throw new AssertionError(portalException);
				}
			}

			return super.getAttribute(name);
		}

		@Override
		public String getParameter(String name) {
			String[] values = _parameters.get(name);

			if (values == null) {
				return StringPool.BLANK;
			}

			return values[0];
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return _parameters;
		}

		private ThemeDisplay _getThemeDisplay() throws PortalException {
			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(_company);
			themeDisplay.setPermissionChecker(
				PermissionThreadLocal.getPermissionChecker());
			themeDisplay.setRequest(new MockHttpServletRequest());

			long groupId = _group.getGroupId();

			themeDisplay.setScopeGroupId(groupId);

			themeDisplay.setServerName("localhost");
			themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
			themeDisplay.setSiteGroupId(groupId);
			themeDisplay.setUser(TestPropsValues.getUser());

			return themeDisplay;
		}

		private final Company _company;
		private Group _group;
		private final Map<String, String[]> _parameters;

	}

}
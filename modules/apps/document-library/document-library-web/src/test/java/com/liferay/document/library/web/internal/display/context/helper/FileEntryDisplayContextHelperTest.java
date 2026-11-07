/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.helper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Cristina González
 */
public class FileEntryDisplayContextHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testIsCancelCheckoutDocumentActionAvailableWithCheckedOutAndOverridePermissionAndMoreThat1Version()
		throws PortalException {

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry.getFileVersionsCount(Mockito.anyInt())
		).thenReturn(
			2
		);

		Mockito.when(
			fileEntry.isCheckedOut()
		).thenReturn(
			true
		);

		_configureDLFileEntryPermission(true, false);

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			new FileEntryDisplayContextHelper(null, fileEntry);

		Assert.assertTrue(
			fileEntryDisplayContextHelper.
				isCancelCheckoutDocumentActionAvailable());
	}

	@Test
	@TestInfo("LPD-113625")
	public void testIsCancelCheckoutDocumentActionAvailableWithCheckedOutAndWithoutOverridePermission()
		throws PortalException {

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry.getFileVersionsCount(Mockito.anyInt())
		).thenReturn(
			2
		);

		Mockito.when(
			fileEntry.isCheckedOut()
		).thenReturn(
			true
		);

		_configureDLFileEntryPermission(false, false);

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			new FileEntryDisplayContextHelper(null, fileEntry);

		Assert.assertFalse(
			fileEntryDisplayContextHelper.
				isCancelCheckoutDocumentActionAvailable());
	}

	@Test
	public void testIsCancelCheckoutDocumentActionAvailableWithLockedFileAnd1Version()
		throws PortalException {

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry.getFileVersionsCount(Mockito.anyInt())
		).thenReturn(
			1
		);

		Mockito.when(
			fileEntry.hasLock()
		).thenReturn(
			true
		);

		Mockito.when(
			fileEntry.isSupportsLocking()
		).thenReturn(
			true
		);

		_configureDLFileEntryPermission(true, true);

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			new FileEntryDisplayContextHelper(null, fileEntry);

		Assert.assertFalse(
			fileEntryDisplayContextHelper.
				isCancelCheckoutDocumentActionAvailable());
	}

	@Test
	public void testIsCancelCheckoutDocumentActionAvailableWithLockedFileAndMoreThan1Version()
		throws PortalException {

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry.getFileVersionsCount(Mockito.anyInt())
		).thenReturn(
			2
		);

		Mockito.when(
			fileEntry.hasLock()
		).thenReturn(
			true
		);

		Mockito.when(
			fileEntry.isSupportsLocking()
		).thenReturn(
			true
		);

		_configureDLFileEntryPermission(true, true);

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			new FileEntryDisplayContextHelper(null, fileEntry);

		Assert.assertTrue(
			fileEntryDisplayContextHelper.
				isCancelCheckoutDocumentActionAvailable());
	}

	@Test
	public void testIsCancelCheckoutDocumentActionAvailableWithNullFileEntry()
		throws PortalException {

		_configureDLFileEntryPermission(true, true);

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			new FileEntryDisplayContextHelper(null, null);

		Assert.assertFalse(
			fileEntryDisplayContextHelper.
				isCancelCheckoutDocumentActionAvailable());
	}

	@Test
	public void testIsCopyActionAvailable() throws PortalException {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.isSignedIn()
		).thenReturn(
			false
		);

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			new FileEntryDisplayContextHelper(permissionChecker, fileEntry);

		Assert.assertFalse(
			fileEntryDisplayContextHelper.isCopyActionAvailable());
	}

	@Test
	public void testIsHistoryActionAvailable() throws PortalException {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.isSignedIn()
		).thenReturn(
			false
		);

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			new FileEntryDisplayContextHelper(permissionChecker, fileEntry);

		Assert.assertFalse(
			fileEntryDisplayContextHelper.isHistoryActionAvailable());

		Mockito.when(
			permissionChecker.isSignedIn()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			fileEntryDisplayContextHelper.isHistoryActionAvailable());
	}

	@Test
	public void testIsViewUsagesActionAvailable() throws PortalException {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			permissionChecker.isGroupAdmin(fileEntry.getGroupId())
		).thenReturn(
			false
		);

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			new FileEntryDisplayContextHelper(permissionChecker, fileEntry);

		Assert.assertFalse(
			fileEntryDisplayContextHelper.isViewUsagesActionAvailable());

		Mockito.when(
			permissionChecker.isGroupAdmin(fileEntry.getGroupId())
		).thenReturn(
			true
		);

		Assert.assertTrue(
			fileEntryDisplayContextHelper.isViewUsagesActionAvailable());
	}

	public class MockModelResourcePermission
		implements ModelResourcePermission<FileEntry> {

		public MockModelResourcePermission(
			boolean overrideCheckOutPermission, boolean updatePermission) {

			_overrideCheckOutPermission = overrideCheckOutPermission;
			_updatePermission = updatePermission;
		}

		@Override
		public void check(
				PermissionChecker permissionChecker, FileEntry model,
				String actionId)
			throws PortalException {
		}

		@Override
		public void check(
				PermissionChecker permissionChecker, long primaryKey,
				String actionId)
			throws PortalException {
		}

		@Override
		public boolean contains(
				PermissionChecker permissionChecker, FileEntry model,
				String actionId)
			throws PortalException {

			return _hasPermission(actionId);
		}

		@Override
		public boolean contains(
				PermissionChecker permissionChecker, long primaryKey,
				String actionId)
			throws PortalException {

			return _hasPermission(actionId);
		}

		@Override
		public String getModelName() {
			return null;
		}

		@Override
		public PortletResourcePermission getPortletResourcePermission() {
			return null;
		}

		private boolean _hasPermission(String actionId) {
			if (Objects.equals(actionId, ActionKeys.OVERRIDE_CHECKOUT)) {
				return _overrideCheckOutPermission;
			}
			else if (Objects.equals(actionId, ActionKeys.UPDATE)) {
				return _updatePermission;
			}

			return false;
		}

		private final boolean _overrideCheckOutPermission;
		private final boolean _updatePermission;

	}

	private void _configureDLFileEntryPermission(
		boolean overrideCheckOutPermission, boolean updatePermission) {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistrations.add(
			bundleContext.registerService(
				ModelResourcePermission.class,
				new MockModelResourcePermission(
					overrideCheckOutPermission, updatePermission),
				MapUtil.singletonDictionary(
					"model.class.name",
					"com.liferay.document.library.kernel.model.DLFileEntry")));
		_serviceRegistrations.add(
			bundleContext.registerService(
				ModelResourcePermission.class,
				new MockModelResourcePermission(
					overrideCheckOutPermission, updatePermission),
				MapUtil.singletonDictionary(
					"model.class.name",
					"com.liferay.portal.kernel.repository.model.FileEntry")));
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}
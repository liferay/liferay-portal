/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.empty.model;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.report.service.persistence.ExportImportReportEntryPersistence;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Carlos Correa
 */
public class EmptyModelManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_emptyModelManager, "_classNameLocalService",
			_classNameLocalService);
		ReflectionTestUtil.setFieldValue(
			_emptyModelManager, "_exportImportReportEntryLocalService",
			_exportImportReportEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_emptyModelManager, "_exportImportReportEntryPersistence",
			_exportImportReportEntryPersistence);
		ReflectionTestUtil.setFieldValue(
			_emptyModelManager, "_groupLocalService", _groupLocalService);
		ReflectionTestUtil.setFieldValue(_emptyModelManager, "_log", _log);

		_stagingGroupHelperUtilMockedStatic = Mockito.mockStatic(
			StagingGroupHelperUtil.class);
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(
			_classNameLocalService, _exportImportReportEntryLocalService,
			_exportImportReportEntryPersistence, _group, _groupLocalService,
			_log, _user);

		_stagingGroupHelperUtilMockedStatic.close();
	}

	@Test
	public void testGetOrAddEmptyModelCompanyScopedWithDisabledLazyReferencingAndReturningItem()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(false)) {

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class, RandomTestUtil.randomLong(),
					() -> {
						Assert.fail();

						return null;
					},
					RandomTestUtil.randomString(),
					_toBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					_toUnsafeBiFunction(() -> _user), User.class.getName()));
		}
	}

	@Test
	public void testGetOrAddEmptyModelCompanyScopedWithDisabledLazyReferencingAndThrowingException()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(false)) {

			Assert.assertThrows(
				NoSuchUserException.class,
				() -> _emptyModelManager.getOrAddEmptyModel(
					User.class, RandomTestUtil.randomLong(),
					() -> {
						Assert.fail();

						return null;
					},
					RandomTestUtil.randomString(),
					_toBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					_toUnsafeBiFunction(
						() -> {
							throw new NoSuchUserException();
						}),
					User.class.getName()));
		}
	}

	@Test
	public void testGetOrAddEmptyModelCompanyScopedWithEnabledLazyReferencingAndAddingEmptyItem()
		throws Exception {

		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class);
			SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			long classNameId = RandomTestUtil.randomLong();

			Mockito.when(
				_classNameLocalService.getClassNameId(User.class.getName())
			).thenReturn(
				classNameId
			);

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				true
			);

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::getExportImportConfigurationId
			).thenReturn(
				exportImportConfigurationId
			);

			long companyId = RandomTestUtil.randomLong();
			String externalReferenceCode = RandomTestUtil.randomString();

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class, companyId, () -> _user, externalReferenceCode,
					_toBiFunction(() -> null),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					User.class.getName()));

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).getOrAddExportImportReportEntry(
				0L, companyId, externalReferenceCode, classNameId, 0,
				exportImportConfigurationId,
				ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
				User.class.getName()
			);
		}
	}

	@Test
	public void testGetOrAddEmptyModelCompanyScopedWithEnabledLazyReferencingAndReturningItem()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class, RandomTestUtil.randomLong(),
					() -> {
						Assert.fail();

						return null;
					},
					RandomTestUtil.randomString(), _toBiFunction(() -> _user),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					User.class.getName()));
		}
	}

	@Test
	public void testGetOrAddEmptyModelCompanyScopedWithEnabledLazyReferencingOutsideAnImportProcess()
		throws Exception {

		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class);
			SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				false
			);

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class, RandomTestUtil.randomLong(), () -> _user,
					RandomTestUtil.randomString(), _toBiFunction(() -> null),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					User.class.getName()));
		}
	}

	@Test
	public void testGetOrAddEmptyModelGroupScopedWithDisabledLazyReferencingAndReturningItem()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(false)) {

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class.getName(), null,
					() -> {
						Assert.fail();

						return null;
					},
					RandomTestUtil.randomString(),
					_toBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					_toUnsafeBiFunction(() -> _user),
					RandomTestUtil.randomLong(), User.class.getName()));
		}
	}

	@Test
	public void testGetOrAddEmptyModelGroupScopedWithDisabledLazyReferencingAndThrowingException()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(false)) {

			Assert.assertThrows(
				NoSuchUserException.class,
				() -> _emptyModelManager.getOrAddEmptyModel(
					User.class.getName(), null,
					() -> {
						Assert.fail();

						return null;
					},
					RandomTestUtil.randomString(),
					_toBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					_toUnsafeBiFunction(
						() -> {
							throw new NoSuchUserException();
						}),
					RandomTestUtil.randomLong(), User.class.getName()));
		}
	}

	@Test
	public void testGetOrAddEmptyModelGroupScopedWithEnabledLazyReferencingAndAddingEmptyItem()
		throws Exception {

		StagingGroupHelper stagingGroupHelper = Mockito.mock(
			StagingGroupHelper.class);

		Mockito.when(
			stagingGroupHelper.isCompanyGroup(_group)
		).thenReturn(
			false
		);

		_stagingGroupHelperUtilMockedStatic.when(
			StagingGroupHelperUtil::getStagingGroupHelper
		).thenReturn(
			stagingGroupHelper
		);

		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class);
			SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			long classNameId = RandomTestUtil.randomLong();

			Mockito.when(
				_classNameLocalService.getClassNameId(User.class.getName())
			).thenReturn(
				classNameId
			);

			long companyId = RandomTestUtil.randomLong();

			Mockito.when(
				_group.getCompanyId()
			).thenReturn(
				companyId
			);

			String groupExternalReferenceCode = RandomTestUtil.randomString();

			Mockito.when(
				_group.getExternalReferenceCode()
			).thenReturn(
				groupExternalReferenceCode
			);

			Mockito.when(
				_group.isCompany()
			).thenReturn(
				false
			);

			long groupId = RandomTestUtil.randomLong();

			Mockito.when(
				_groupLocalService.fetchGroup(groupId)
			).thenReturn(
				_group
			);

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				true
			);

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::getExportImportConfigurationId
			).thenReturn(
				exportImportConfigurationId
			);

			String userExternalReferenceCode = RandomTestUtil.randomString();

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class.getName(), companyId, () -> _user,
					userExternalReferenceCode, _toBiFunction(() -> null),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					groupId, User.class.getName()));

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_group
			).getCompanyId();

			Mockito.verify(
				_groupLocalService
			).fetchGroup(
				groupId
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).getOrAddExportImportReportEntry(
				groupId, companyId, userExternalReferenceCode, classNameId, 0,
				exportImportConfigurationId,
				ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
				User.class.getName()
			);
		}
	}

	@Test
	public void testGetOrAddEmptyModelGroupScopedWithEnabledLazyReferencingAndReturningItem()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			long companyId = RandomTestUtil.randomLong();

			Mockito.when(
				_group.getCompanyId()
			).thenReturn(
				companyId
			);

			long groupId = RandomTestUtil.randomLong();

			Mockito.when(
				_groupLocalService.fetchGroup(groupId)
			).thenReturn(
				_group
			);

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class.getName(), companyId,
					() -> {
						Assert.fail();

						return null;
					},
					RandomTestUtil.randomString(), _toBiFunction(() -> _user),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					groupId, User.class.getName()));
		}
	}

	@Test
	public void testGetOrAddEmptyModelGroupScopedWithEnabledLazyReferencingOutsideAnImportProcess()
		throws Exception {

		StagingGroupHelper stagingGroupHelper = Mockito.mock(
			StagingGroupHelper.class);

		Mockito.when(
			stagingGroupHelper.isCompanyGroup(_group)
		).thenReturn(
			false
		);

		_stagingGroupHelperUtilMockedStatic.when(
			StagingGroupHelperUtil::getStagingGroupHelper
		).thenReturn(
			stagingGroupHelper
		);

		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class);
			SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				false
			);

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class.getName(), RandomTestUtil.randomLong(),
					() -> _user, RandomTestUtil.randomString(),
					_toBiFunction(() -> null),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					RandomTestUtil.randomLong(), User.class.getName()));
		}
	}

	@Test
	public void testIsEmptyModel() {
		Assert.assertFalse(_emptyModelManager.isEmptyModel());
	}

	@Test
	public void testIsEmptyModelWhenAddingEmptyModel() throws Exception {
		Assert.assertFalse(_emptyModelManager.isEmptyModel());

		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class);
			SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			long classNameId = RandomTestUtil.randomLong();

			Mockito.when(
				_classNameLocalService.getClassNameId(User.class.getName())
			).thenReturn(
				classNameId
			);

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				true
			);

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::getExportImportConfigurationId
			).thenReturn(
				exportImportConfigurationId
			);

			long companyId = RandomTestUtil.randomLong();
			String externalReferenceCode = RandomTestUtil.randomString();

			_emptyModelManager.getOrAddEmptyModel(
				User.class, companyId,
				() -> {
					Assert.assertTrue(_emptyModelManager.isEmptyModel());

					return _user;
				},
				externalReferenceCode, _toBiFunction(() -> null),
				_toUnsafeBiFunction(
					() -> {
						Assert.fail();

						return null;
					}),
				User.class.getName());

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).getOrAddExportImportReportEntry(
				0L, companyId, externalReferenceCode, classNameId, 0,
				exportImportConfigurationId,
				ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
				User.class.getName()
			);
		}
	}

	@Test
	public void testReportMissingReference() {
		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class)) {

			long companyId = RandomTestUtil.randomLong();

			Mockito.when(
				_group.getCompanyId()
			).thenReturn(
				companyId
			);

			long groupId = RandomTestUtil.randomLong();

			Mockito.when(
				_groupLocalService.fetchGroup(groupId)
			).thenReturn(
				_group
			);

			long classNameId = RandomTestUtil.randomLong();

			Mockito.when(
				_classNameLocalService.getClassNameId(User.class.getName())
			).thenReturn(
				classNameId
			);

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::getExportImportConfigurationId
			).thenReturn(
				exportImportConfigurationId
			);

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				true
			);

			String externalReferenceCode = RandomTestUtil.randomString();

			_emptyModelManager.reportMissingReference(
				User.class.getName(), externalReferenceCode, groupId);

			Mockito.verify(
				_group
			).getCompanyId();

			Mockito.verify(
				_groupLocalService
			).fetchGroup(
				groupId
			);

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).getOrAddExportImportReportEntry(
				groupId, companyId, externalReferenceCode, classNameId, 0,
				exportImportConfigurationId,
				ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE, null,
				null, User.class.getName()
			);
		}
	}

	@Test
	public void testReportMissingReferenceOutsideImportProcess() {
		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class)) {

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				false
			);

			_emptyModelManager.reportMissingReference(
				User.class.getName(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLong());
		}
	}

	@Test
	public void testReportMissingReferenceWithMissingGroup() {
		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class)) {

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				true
			);

			long groupId = RandomTestUtil.randomLong();

			Mockito.when(
				_groupLocalService.fetchGroup(groupId)
			).thenReturn(
				null
			);

			_emptyModelManager.reportMissingReference(
				User.class.getName(), RandomTestUtil.randomString(), groupId);

			Mockito.verify(
				_groupLocalService
			).fetchGroup(
				groupId
			);
		}
	}

	@Test
	public void testResolveMissingReference() {
		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class)) {

			ExportImportReportEntry exportImportReportEntry =
				_mockResolveMissingReference(
					exportImportThreadLocalMockedStatic, true);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).deleteExportImportReportEntry(
				exportImportReportEntry
			);

			Mockito.verify(
				_log
			).isDebugEnabled();
		}
	}

	@Test
	public void testResolveMissingReferenceOutsideImportProcess() {
		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class)) {

			exportImportThreadLocalMockedStatic.when(
				ExportImportThreadLocal::isImportInProcess
			).thenReturn(
				false
			);

			_emptyModelManager.resolveMissingReference(
				User.class.getName(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString(), RandomTestUtil.randomLong());
		}
	}

	@Test
	public void testResolveMissingReferenceWithoutReportEntry() {
		try (MockedStatic<ExportImportThreadLocal>
				exportImportThreadLocalMockedStatic = Mockito.mockStatic(
					ExportImportThreadLocal.class)) {

			_mockResolveMissingReference(
				exportImportThreadLocalMockedStatic, false);
		}
	}

	@Test
	public void testSolveEmptyModel() throws Exception {
		long classNameId = RandomTestUtil.randomLong();

		Mockito.when(
			_classNameLocalService.getClassNameId(User.class.getName())
		).thenReturn(
			classNameId
		);

		long companyId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();
		int status = RandomTestUtil.randomInt();

		Assert.assertEquals(
			status,
			_emptyModelManager.solveEmptyModel(
				externalReferenceCode, User.class.getName(), companyId, groupId,
				WorkflowConstants.STATUS_EMPTY, () -> status));

		Mockito.verify(
			_classNameLocalService
		).getClassNameId(
			User.class.getName()
		);

		Mockito.verify(
			_exportImportReportEntryLocalService
		).resolveEmptyExportImportReportEntries(
			groupId, companyId, externalReferenceCode, classNameId
		);
	}

	@Test
	public void testSolveEmptyModelWithException() throws Exception {
		long classNameId = RandomTestUtil.randomLong();

		Mockito.when(
			_classNameLocalService.getClassNameId(User.class.getName())
		).thenReturn(
			classNameId
		);

		long companyId = RandomTestUtil.randomLong();
		String errorMessage = RandomTestUtil.randomString();
		String externalReferenceCode = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();
		PortalException portalException = new PortalException(errorMessage);
		int status = RandomTestUtil.randomInt();

		Mockito.doThrow(
			portalException
		).when(
			_exportImportReportEntryLocalService
		).resolveEmptyExportImportReportEntries(
			groupId, companyId, externalReferenceCode, classNameId
		);

		Assert.assertEquals(
			status,
			_emptyModelManager.solveEmptyModel(
				externalReferenceCode, User.class.getName(), companyId, groupId,
				WorkflowConstants.STATUS_EMPTY, () -> status));

		Mockito.verify(
			_classNameLocalService
		).getClassNameId(
			User.class.getName()
		);

		Mockito.verify(
			_exportImportReportEntryLocalService
		).resolveEmptyExportImportReportEntries(
			groupId, companyId, externalReferenceCode, classNameId
		);

		Mockito.verify(
			_log
		).error(
			StringBundler.concat(
				"Unable to resolve the export/import report entries for \"",
				"the class external reference code ", externalReferenceCode,
				"\" and class name \"com.liferay.portal.kernel.model.User\""),
			portalException
		);
	}

	@Test
	public void testSolveEmptyModelWithNotEmptyStatus() {
		long classNameId = RandomTestUtil.randomLong();

		Mockito.when(
			_classNameLocalService.getClassNameId(User.class.getName())
		).thenReturn(
			classNameId
		);

		long companyId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			_emptyModelManager.solveEmptyModel(
				externalReferenceCode, User.class.getName(), companyId, groupId,
				WorkflowConstants.STATUS_APPROVED, RandomTestUtil::randomInt));
	}

	private ExportImportReportEntry _mockResolveMissingReference(
		MockedStatic<ExportImportThreadLocal>
			exportImportThreadLocalMockedStatic,
		boolean reportEntryExists) {

		long classNameId = RandomTestUtil.randomLong();

		Mockito.when(
			_classNameLocalService.getClassNameId(User.class.getName())
		).thenReturn(
			classNameId
		);

		long exportImportConfigurationId = RandomTestUtil.randomLong();

		exportImportThreadLocalMockedStatic.when(
			ExportImportThreadLocal::getExportImportConfigurationId
		).thenReturn(
			exportImportConfigurationId
		);

		exportImportThreadLocalMockedStatic.when(
			ExportImportThreadLocal::isImportInProcess
		).thenReturn(
			true
		);

		ExportImportReportEntry exportImportReportEntry = null;

		if (reportEntryExists) {
			exportImportReportEntry = Mockito.mock(
				ExportImportReportEntry.class);
		}

		long companyId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_exportImportReportEntryPersistence.fetchByG_C_C_C_E_T(
				groupId, companyId, externalReferenceCode, classNameId,
				exportImportConfigurationId,
				ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE, false)
		).thenReturn(
			exportImportReportEntry
		);

		_emptyModelManager.resolveMissingReference(
			User.class.getName(), companyId, externalReferenceCode, groupId);

		Mockito.verify(
			_classNameLocalService
		).getClassNameId(
			User.class.getName()
		);

		Mockito.verify(
			_exportImportReportEntryPersistence
		).fetchByG_C_C_C_E_T(
			groupId, companyId, externalReferenceCode, classNameId,
			exportImportConfigurationId,
			ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE, false
		);

		return exportImportReportEntry;
	}

	private BiFunction<String, Long, User> _toBiFunction(
		Supplier<User> supplier) {

		return (externalReferenceCode, companyId) -> supplier.get();
	}

	private UnsafeBiFunction<String, Long, User, PortalException>
		_toUnsafeBiFunction(
			UnsafeSupplier<User, PortalException> unsafeSupplier) {

		return (externalReferenceCode, companyId) -> unsafeSupplier.get();
	}

	private static final Log _log = Mockito.mock(Log.class);

	private static final EmptyModelManager _emptyModelManager =
		new EmptyModelManagerImpl();

	private final ClassNameLocalService _classNameLocalService = Mockito.mock(
		ClassNameLocalService.class);
	private final ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService = Mockito.mock(
			ExportImportReportEntryLocalService.class);
	private final ExportImportReportEntryPersistence
		_exportImportReportEntryPersistence = Mockito.mock(
			ExportImportReportEntryPersistence.class);
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private MockedStatic<StagingGroupHelperUtil>
		_stagingGroupHelperUtilMockedStatic;
	private final User _user = Mockito.mock(User.class);

}
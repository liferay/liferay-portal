/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.empty.model;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void setUpClass() {
		_stagingGroupHelperUtilMockedStatic = Mockito.mockStatic(
			StagingGroupHelperUtil.class);
	}

	@AfterClass
	public static void tearDownClass() {
		_stagingGroupHelperUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_emptyModelManager, "_classNameLocalService",
			_classNameLocalService);
		ReflectionTestUtil.setFieldValue(
			_emptyModelManager, "_exportImportReportEntryLocalService",
			_exportImportReportEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_emptyModelManager, "_groupLocalService", _groupLocalService);

		_stagingGroupHelperUtilMockedStatic.reset();
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(
			_classNameLocalService, _exportImportReportEntryLocalService,
			_group, _groupLocalService, _user);
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
					_toUnsafeBiFunction(() -> _user)));
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
						})));
		}
	}

	@Test
	public void testGetOrAddEmptyModelCompanyScopedWithEnabledLazyReferencingAndAddingEmptyItem()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			long classNameId = RandomTestUtil.randomLong();

			Mockito.when(
				_classNameLocalService.getClassNameId(User.class.getName())
			).thenReturn(
				classNameId
			);

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			ExportImportThreadLocal.setExportImportConfigurationId(
				exportImportConfigurationId);

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
						})));

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).addEmptyExportImportReportEntry(
				0L, companyId, externalReferenceCode, classNameId,
				exportImportConfigurationId, User.class.getName(),
				ExportImportReportEntryConstants.ORIGIN_STAGING,
				ObjectDefinitionConstants.SCOPE_COMPANY, null
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
					User.class,
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
					RandomTestUtil.randomLong()));
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
					User.class,
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
					RandomTestUtil.randomLong()));
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
					User.class,
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
					RandomTestUtil.randomLong()));
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

		try (SafeCloseable safeCloseable =
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

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			ExportImportThreadLocal.setExportImportConfigurationId(
				exportImportConfigurationId);

			String userExternalReferenceCode = RandomTestUtil.randomString();

			Assert.assertSame(
				_user,
				_emptyModelManager.getOrAddEmptyModel(
					User.class, () -> _user, userExternalReferenceCode,
					_toBiFunction(() -> null),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					groupId));

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_group
			).getCompanyId();

			Mockito.verify(
				_group
			).getExternalReferenceCode();

			Mockito.verify(
				_group
			).isCompany();

			Mockito.verify(
				_groupLocalService
			).fetchGroup(
				groupId
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).addEmptyExportImportReportEntry(
				groupId, companyId, userExternalReferenceCode, classNameId,
				exportImportConfigurationId, User.class.getName(),
				ExportImportReportEntryConstants.ORIGIN_STAGING,
				ObjectDefinitionConstants.SCOPE_SITE, groupExternalReferenceCode
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
					User.class,
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
					groupId));
		}
	}

	@Test
	public void testIsEmptyModel() {
		Assert.assertFalse(_emptyModelManager.isEmptyModel());
	}

	@Test
	public void testIsEmptyModelWhenAddingEmptyModel() throws Exception {
		Assert.assertFalse(_emptyModelManager.isEmptyModel());

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			long classNameId = RandomTestUtil.randomLong();

			Mockito.when(
				_classNameLocalService.getClassNameId(User.class.getName())
			).thenReturn(
				classNameId
			);

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			ExportImportThreadLocal.setExportImportConfigurationId(
				exportImportConfigurationId);

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
					}));

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).addEmptyExportImportReportEntry(
				0L, companyId, externalReferenceCode, classNameId,
				exportImportConfigurationId, User.class.getName(),
				ExportImportReportEntryConstants.ORIGIN_STAGING,
				ObjectDefinitionConstants.SCOPE_COMPANY, null
			);
		}
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

	private static MockedStatic<StagingGroupHelperUtil>
		_stagingGroupHelperUtilMockedStatic;

	private final ClassNameLocalService _classNameLocalService = Mockito.mock(
		ClassNameLocalService.class);
	private final EmptyModelManager _emptyModelManager =
		new EmptyModelManagerImpl();
	private final ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService = Mockito.mock(
			ExportImportReportEntryLocalService.class);
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final User _user = Mockito.mock(User.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.incomplete.model;

import com.liferay.exportimport.kernel.incomplete.model.IncompleteModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carlos Correa
 */
public class IncompleteModelManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_incompleteModelManager, "_classNameLocalService",
			_classNameLocalService);
		ReflectionTestUtil.setFieldValue(
			_incompleteModelManager, "_exportImportReportEntryLocalService",
			_exportImportReportEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_incompleteModelManager, "_groupLocalService", _groupLocalService);
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(
			_classNameLocalService, _exportImportReportEntryLocalService,
			_group, _groupLocalService, _user);
	}

	@Test
	public void testGetOrAddIncompleteModelCompanyScopedWithDisabledLazyReferencingAndReturningItem()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(false)) {

			Assert.assertSame(
				_user,
				_incompleteModelManager.getOrAddIncompleteModel(
					User.class, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(),
					_toBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					_toUnsafeBiFunction(() -> _user),
					() -> {
						Assert.fail();

						return null;
					}));
		}
	}

	@Test
	public void testGetOrAddIncompleteModelCompanyScopedWithDisabledLazyReferencingAndThrowingException()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(false)) {

			Assert.assertThrows(
				NoSuchUserException.class,
				() -> _incompleteModelManager.getOrAddIncompleteModel(
					User.class, RandomTestUtil.randomLong(),
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
					() -> {
						Assert.fail();

						return null;
					}));
		}
	}

	@Test
	public void testGetOrAddIncompleteModelCompanyScopedWithEnabledLazyReferencingAndAddingIncompleteItem()
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
				_incompleteModelManager.getOrAddIncompleteModel(
					User.class, companyId, externalReferenceCode,
					_toBiFunction(() -> null),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					() -> _user));

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).addIncompleteExportImportReportEntry(
				0L, companyId, externalReferenceCode, classNameId,
				exportImportConfigurationId
			);
		}
	}

	@Test
	public void testGetOrAddIncompleteModelCompanyScopedWithEnabledLazyReferencingAndReturningItem()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			Assert.assertSame(
				_user,
				_incompleteModelManager.getOrAddIncompleteModel(
					User.class, RandomTestUtil.randomString(),
					_toBiFunction(() -> _user),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					RandomTestUtil.randomLong(),
					() -> {
						Assert.fail();

						return null;
					}));
		}
	}

	@Test
	public void testGetOrAddIncompleteModelGroupScopedWithDisabledLazyReferencingAndReturningItem()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(false)) {

			Assert.assertSame(
				_user,
				_incompleteModelManager.getOrAddIncompleteModel(
					User.class, RandomTestUtil.randomString(),
					_toBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					_toUnsafeBiFunction(() -> _user),
					RandomTestUtil.randomLong(),
					() -> {
						Assert.fail();

						return null;
					}));
		}
	}

	@Test
	public void testGetOrAddIncompleteModelGroupScopedWithDisabledLazyReferencingAndThrowingException()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(false)) {

			Assert.assertThrows(
				NoSuchUserException.class,
				() -> _incompleteModelManager.getOrAddIncompleteModel(
					User.class, RandomTestUtil.randomString(),
					_toBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					_toUnsafeBiFunction(
						() -> {
							throw new NoSuchUserException();
						}),
					RandomTestUtil.randomLong(),
					() -> {
						Assert.fail();

						return null;
					}));
		}
	}

	@Test
	public void testGetOrAddIncompleteModelGroupScopedWithEnabledLazyReferencingAndAddingIncompleteItem()
		throws Exception {

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

			long groupId = RandomTestUtil.randomLong();

			Mockito.when(
				_groupLocalService.fetchGroup(groupId)
			).thenReturn(
				_group
			);

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			ExportImportThreadLocal.setExportImportConfigurationId(
				exportImportConfigurationId);

			String externalReferenceCode = RandomTestUtil.randomString();

			Assert.assertSame(
				_user,
				_incompleteModelManager.getOrAddIncompleteModel(
					User.class, externalReferenceCode,
					_toBiFunction(() -> null),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					groupId, () -> _user));

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
			).addIncompleteExportImportReportEntry(
				groupId, companyId, externalReferenceCode, classNameId,
				exportImportConfigurationId
			);
		}
	}

	@Test
	public void testGetOrAddIncompleteModelGroupScopedWithEnabledLazyReferencingAndReturningItem()
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
				_incompleteModelManager.getOrAddIncompleteModel(
					User.class, RandomTestUtil.randomString(),
					_toBiFunction(() -> _user),
					_toUnsafeBiFunction(
						() -> {
							Assert.fail();

							return null;
						}),
					groupId,
					() -> {
						Assert.fail();

						return null;
					}));
		}
	}

	@Test
	public void testIsIncompleteModel() {
		Assert.assertFalse(_incompleteModelManager.isIncompleteModel());
	}

	@Test
	public void testIsIncompleteModelWhenAddingIncompleteModel()
		throws Exception {

		Assert.assertFalse(_incompleteModelManager.isIncompleteModel());

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

			_incompleteModelManager.getOrAddIncompleteModel(
				User.class, companyId, externalReferenceCode,
				_toBiFunction(() -> null),
				_toUnsafeBiFunction(
					() -> {
						Assert.fail();

						return null;
					}),
				() -> {
					Assert.assertTrue(
						_incompleteModelManager.isIncompleteModel());

					return _user;
				});

			Mockito.verify(
				_classNameLocalService
			).getClassNameId(
				User.class.getName()
			);

			Mockito.verify(
				_exportImportReportEntryLocalService
			).addIncompleteExportImportReportEntry(
				0L, companyId, externalReferenceCode, classNameId,
				exportImportConfigurationId
			);
		}
	}

	private BiFunction<String, Long, User> _toBiFunction(
		Supplier<User> supplier) {

		return (externalReferenceCode, companyId) -> supplier.get();
	}

	private UnsafeBiFunction<String, Long, User, Exception> _toUnsafeBiFunction(
		UnsafeSupplier<User, Exception> unsafeSupplier) {

		return (externalReferenceCode, companyId) -> unsafeSupplier.get();
	}

	private final ClassNameLocalService _classNameLocalService = Mockito.mock(
		ClassNameLocalService.class);
	private final ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService = Mockito.mock(
			ExportImportReportEntryLocalService.class);
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final IncompleteModelManager _incompleteModelManager =
		new IncompleteModelManagerImpl();
	private final User _user = Mockito.mock(User.class);

}
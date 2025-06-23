/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.incomplete.model;

import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carlos Correa
 */
public class IncompleteModelManagerUtilTest {

	@BeforeClass
	public static void setUpClass() {
		_incompleteModelManagerSnapshot =
			ReflectionTestUtil.getAndSetFieldValue(
				IncompleteModelManagerUtil.class,
				"_incompleteModelManagerSnapshot",
				new Snapshot<IncompleteModelManager>(
					IncompleteModelManagerUtil.class,
					IncompleteModelManager.class) {

					@Override
					public IncompleteModelManager get() {
						return _atomicReference.get();
					}

				});
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			IncompleteModelManagerUtil.class, "_incompleteModelManagerSnapshot",
			_incompleteModelManagerSnapshot);
	}

	@Before
	public void setUp() {
		Mockito.reset(_incompleteModelManager);
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(_incompleteModelManager);
	}

	@Test
	public void testGetOrAddIncompleteModelByCompanyId() throws Exception {
		_atomicReference.set(_incompleteModelManager);

		User user = Mockito.mock(User.class);

		Mockito.when(
			_incompleteModelManager.getOrAddIncompleteModel(
				Mockito.any(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.any(BiFunction.class),
				Mockito.any(UnsafeBiFunction.class),
				Mockito.any(UnsafeSupplier.class))
		).thenReturn(
			user
		);

		BiFunction<String, Long, User> biFunction = (a, b) -> Mockito.mock(
			User.class);
		long companyId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();
		UnsafeBiFunction<String, Long, User, Exception> unsafeBiFunction =
			(a, b) -> Mockito.mock(User.class);
		UnsafeSupplier<User, Exception> unsafeSupplier = () -> Mockito.mock(
			User.class);

		Assert.assertSame(
			user,
			IncompleteModelManagerUtil.getOrAddIncompleteModel(
				User.class, companyId, externalReferenceCode, biFunction,
				unsafeBiFunction, unsafeSupplier));

		Mockito.verify(
			_incompleteModelManager
		).getOrAddIncompleteModel(
			User.class, companyId, externalReferenceCode, biFunction,
			unsafeBiFunction, unsafeSupplier
		);
	}

	@Test
	public void testGetOrAddIncompleteModelByGroupId() throws Exception {
		_atomicReference.set(_incompleteModelManager);

		User user = Mockito.mock(User.class);

		Mockito.when(
			_incompleteModelManager.getOrAddIncompleteModel(
				Mockito.any(), Mockito.anyString(),
				Mockito.any(BiFunction.class),
				Mockito.any(UnsafeBiFunction.class), Mockito.anyLong(),
				Mockito.any(UnsafeSupplier.class))
		).thenReturn(
			user
		);

		BiFunction<String, Long, User> biFunction = (a, b) -> Mockito.mock(
			User.class);
		String externalReferenceCode = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();
		UnsafeBiFunction<String, Long, User, Exception> unsafeBiFunction =
			(a, b) -> Mockito.mock(User.class);
		UnsafeSupplier<User, Exception> unsafeSupplier = () -> Mockito.mock(
			User.class);

		Assert.assertSame(
			user,
			IncompleteModelManagerUtil.getOrAddIncompleteModel(
				User.class, externalReferenceCode, biFunction, unsafeBiFunction,
				groupId, unsafeSupplier));

		Mockito.verify(
			_incompleteModelManager
		).getOrAddIncompleteModel(
			User.class, externalReferenceCode, biFunction, unsafeBiFunction,
			groupId, unsafeSupplier
		);
	}

	@Test
	public void testGetOrAddIncompleteModelSnapshotReturnsNull() {
		_atomicReference.set(null);

		Assert.assertThrows(
			NullPointerException.class,
			() -> IncompleteModelManagerUtil.getOrAddIncompleteModel(
				null, 0L, null, null, null, null));
	}

	@Test
	public void testIsIncompleteModelFalse() {
		_atomicReference.set(_incompleteModelManager);

		Mockito.when(
			_incompleteModelManager.isIncompleteModel()
		).thenReturn(
			false
		);

		Assert.assertFalse(IncompleteModelManagerUtil.isIncompleteModel());

		Mockito.verify(
			_incompleteModelManager
		).isIncompleteModel();
	}

	@Test
	public void testIsIncompleteModelSnapshotReturnsNull() {
		_atomicReference.set(null);

		Assert.assertFalse(IncompleteModelManagerUtil.isIncompleteModel());
	}

	@Test
	public void testIsIncompleteModelTrue() {
		_atomicReference.set(_incompleteModelManager);

		Mockito.when(
			_incompleteModelManager.isIncompleteModel()
		).thenReturn(
			true
		);

		Assert.assertTrue(IncompleteModelManagerUtil.isIncompleteModel());

		Mockito.verify(
			_incompleteModelManager
		).isIncompleteModel();
	}

	private static final AtomicReference<IncompleteModelManager>
		_atomicReference = new AtomicReference<>(null);
	private static final IncompleteModelManager _incompleteModelManager =
		Mockito.mock(IncompleteModelManager.class);
	private static Snapshot<IncompleteModelManager>
		_incompleteModelManagerSnapshot;

}
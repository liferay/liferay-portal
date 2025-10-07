/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.empty.model;

import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.exception.PortalException;
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
public class EmptyModelManagerUtilTest {

	@BeforeClass
	public static void setUpClass() {
		_emptyModelManagerSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			EmptyModelManagerUtil.class, "_emptyModelManagerSnapshot",
			new Snapshot<EmptyModelManager>(
				EmptyModelManagerUtil.class, EmptyModelManager.class) {

				@Override
				public EmptyModelManager get() {
					return _atomicReference.get();
				}

			});
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			EmptyModelManagerUtil.class, "_emptyModelManagerSnapshot",
			_emptyModelManagerSnapshot);
	}

	@Before
	public void setUp() {
		Mockito.reset(_emptyModelManager);
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(_emptyModelManager);
	}

	@Test
	public void testGetOrAddEmptyModelByCompanyId() throws Exception {
		_atomicReference.set(_emptyModelManager);

		User user = Mockito.mock(User.class);

		Mockito.when(
			_emptyModelManager.getOrAddEmptyModel(
				Mockito.any(), Mockito.anyLong(),
				Mockito.any(UnsafeSupplier.class), Mockito.anyString(),
				Mockito.any(BiFunction.class),
				Mockito.any(UnsafeBiFunction.class))
		).thenReturn(
			user
		);

		BiFunction<String, Long, User> biFunction = (a, b) -> Mockito.mock(
			User.class);
		long companyId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();
		UnsafeBiFunction<String, Long, User, PortalException> unsafeBiFunction =
			(a, b) -> Mockito.mock(User.class);
		UnsafeSupplier<User, PortalException> unsafeSupplier =
			() -> Mockito.mock(User.class);

		Assert.assertSame(
			user,
			EmptyModelManagerUtil.getOrAddEmptyModel(
				User.class, companyId, externalReferenceCode, biFunction,
				unsafeBiFunction, unsafeSupplier));

		Mockito.verify(
			_emptyModelManager
		).getOrAddEmptyModel(
			User.class, companyId, unsafeSupplier, externalReferenceCode,
			biFunction, unsafeBiFunction
		);
	}

	@Test
	public void testGetOrAddEmptyModelByGroupId() throws Exception {
		_atomicReference.set(_emptyModelManager);

		User user = Mockito.mock(User.class);

		Mockito.when(
			_emptyModelManager.getOrAddEmptyModel(
				Mockito.any(), Mockito.any(UnsafeSupplier.class),
				Mockito.anyString(), Mockito.any(BiFunction.class),
				Mockito.any(UnsafeBiFunction.class), Mockito.anyLong())
		).thenReturn(
			user
		);

		BiFunction<String, Long, User> biFunction = (a, b) -> Mockito.mock(
			User.class);
		String externalReferenceCode = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();
		UnsafeBiFunction<String, Long, User, PortalException> unsafeBiFunction =
			(a, b) -> Mockito.mock(User.class);
		UnsafeSupplier<User, PortalException> unsafeSupplier =
			() -> Mockito.mock(User.class);

		Assert.assertSame(
			user,
			EmptyModelManagerUtil.getOrAddEmptyModel(
				User.class, unsafeSupplier, externalReferenceCode, biFunction,
				unsafeBiFunction, groupId));

		Mockito.verify(
			_emptyModelManager
		).getOrAddEmptyModel(
			User.class, unsafeSupplier, externalReferenceCode, biFunction,
			unsafeBiFunction, groupId
		);
	}

	@Test
	public void testGetOrAddEmptyModelSnapshotReturnsNull() {
		_atomicReference.set(null);

		Assert.assertThrows(
			NullPointerException.class,
			() -> EmptyModelManagerUtil.getOrAddEmptyModel(
				null, 0L, null, null, null, null));
	}

	@Test
	public void testIsEmptyModelFalse() {
		_atomicReference.set(_emptyModelManager);

		Mockito.when(
			_emptyModelManager.isEmptyModel()
		).thenReturn(
			false
		);

		Assert.assertFalse(EmptyModelManagerUtil.isEmptyModel());

		Mockito.verify(
			_emptyModelManager
		).isEmptyModel();
	}

	@Test
	public void testIsEmptyModelSnapshotReturnsNull() {
		_atomicReference.set(null);

		Assert.assertFalse(EmptyModelManagerUtil.isEmptyModel());
	}

	@Test
	public void testIsEmptyModelTrue() {
		_atomicReference.set(_emptyModelManager);

		Mockito.when(
			_emptyModelManager.isEmptyModel()
		).thenReturn(
			true
		);

		Assert.assertTrue(EmptyModelManagerUtil.isEmptyModel());

		Mockito.verify(
			_emptyModelManager
		).isEmptyModel();
	}

	private static final AtomicReference<EmptyModelManager> _atomicReference =
		new AtomicReference<>(null);
	private static final EmptyModelManager _emptyModelManager = Mockito.mock(
		EmptyModelManager.class);
	private static Snapshot<EmptyModelManager> _emptyModelManagerSnapshot;

}
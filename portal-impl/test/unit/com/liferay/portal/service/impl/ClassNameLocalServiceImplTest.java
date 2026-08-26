/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.exception.NoSuchClassNameException;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.service.persistence.ClassNamePersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ClassNameLocalServiceImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_classNameLocalServiceImpl = new ClassNameLocalServiceImpl();

		ReflectionTestUtil.setFieldValue(
			_classNameLocalServiceImpl, "classNameLocalService",
			_classNameLocalServiceImpl);
		ReflectionTestUtil.setFieldValue(
			_classNameLocalServiceImpl, "counterLocalService",
			Mockito.mock(CounterLocalService.class));

		_mockClassNamePersistence();
		_mockModelHintsUtil();

		_classNameLocalServiceImpl.checkClassNames();
	}

	@Test
	public void testGetClassNameEvictsPooledClassNameWithoutRow()
		throws Exception {

		ClassName className = _mockClassName(
			_CLASS_NAME_ID_3, RandomTestUtil.randomString());

		Mockito.when(
			_classNamePersistence.fetchByPrimaryKey(_CLASS_NAME_ID_3)
		).thenReturn(
			className
		).thenReturn(
			null
		);

		Mockito.when(
			_classNamePersistence.findByPrimaryKey(_CLASS_NAME_ID_3)
		).thenThrow(
			new NoSuchClassNameException()
		);

		Assert.assertNotNull(
			_classNameLocalServiceImpl.fetchByClassNameId(_CLASS_NAME_ID_3));

		Assert.assertThrows(
			NoSuchClassNameException.class,
			() -> _classNameLocalServiceImpl.getClassName(_CLASS_NAME_ID_3));

		Assert.assertNull(
			_classNameLocalServiceImpl.fetchByClassNameId(_CLASS_NAME_ID_3));
	}

	@Test
	public void testGetClassNameIdsSupplier() {
		Supplier<long[]> classNameIdsSupplier1 =
			_classNameLocalServiceImpl.getClassNameIdsSupplier(
				new String[] {_CLASS_NAME_VALUE1, _CLASS_NAME_VALUE2});

		Assert.assertArrayEquals(
			new long[] {_CLASS_NAME_ID_1, _CLASS_NAME_ID_2},
			classNameIdsSupplier1.get());

		_assertSupplier(
			() -> {
				Supplier<long[]> classNameIdsSupplier2 =
					_classNameLocalServiceImpl.getClassNameIdsSupplier(
						new String[] {RandomTestUtil.randomString()});

				Mockito.verify(
					_classNamePersistence, Mockito.never()
				).create(
					Mockito.anyLong()
				);

				classNameIdsSupplier2.get();
			});
	}

	@Test
	public void testGetClassNameIdSupplier() {
		Supplier<Long> classNameIdSupplier1 =
			_classNameLocalServiceImpl.getClassNameIdSupplier(
				_CLASS_NAME_VALUE1);

		Assert.assertEquals(_CLASS_NAME_ID_1, classNameIdSupplier1.get());

		_assertSupplier(
			() -> {
				Supplier<Long> classNameIdSupplier2 =
					_classNameLocalServiceImpl.getClassNameIdSupplier(
						RandomTestUtil.randomString());

				Mockito.verify(
					_classNamePersistence, Mockito.never()
				).create(
					Mockito.anyLong()
				);

				classNameIdSupplier2.get();
			});
	}

	private static ClassName _mockClassName(long classNameId, String value) {
		ClassName className = Mockito.mock(ClassName.class);

		Mockito.when(
			className.getClassNameId()
		).thenReturn(
			classNameId
		);

		Mockito.when(
			className.getValue()
		).thenReturn(
			value
		);

		return className;
	}

	private static void _mockClassNamePersistence() {
		_classNamePersistence = Mockito.mock(
			ClassNamePersistence.class, Answers.RETURNS_MOCKS);

		List<ClassName> classNames = Arrays.asList(
			_mockClassName(_CLASS_NAME_ID_1, _CLASS_NAME_VALUE1),
			_mockClassName(_CLASS_NAME_ID_2, _CLASS_NAME_VALUE2));

		Mockito.when(
			_classNamePersistence.findAll()
		).thenReturn(
			classNames
		);

		ReflectionTestUtil.setFieldValue(
			_classNameLocalServiceImpl, "classNamePersistence",
			_classNamePersistence);
	}

	private static void _mockModelHintsUtil() {
		MockedStatic<ModelHintsUtil> mockedStatic = Mockito.mockStatic(
			ModelHintsUtil.class);

		mockedStatic.when(
			ModelHintsUtil::getModels
		).thenReturn(
			Collections.emptyList()
		);
	}

	private void _assertSupplier(Runnable runnable) {
		Mockito.reset(_classNamePersistence);

		Mockito.when(
			_classNamePersistence.fetchByValue(Mockito.anyString())
		).thenReturn(
			null
		);

		runnable.run();

		Mockito.verify(
			_classNamePersistence
		).create(
			Mockito.anyLong()
		);
	}

	private static final Long _CLASS_NAME_ID_1 = 1L;

	private static final Long _CLASS_NAME_ID_2 = 2L;

	private static final long _CLASS_NAME_ID_3 = 3L;

	private static final String _CLASS_NAME_VALUE1 = "com.liferay.test1";

	private static final String _CLASS_NAME_VALUE2 = "com.liferay.test2";

	private static ClassNameLocalServiceImpl _classNameLocalServiceImpl;
	private static ClassNamePersistence _classNamePersistence;

}
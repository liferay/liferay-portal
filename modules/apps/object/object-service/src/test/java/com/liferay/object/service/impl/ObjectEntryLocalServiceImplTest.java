/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.internal.sort.SortDSLQueryVisitor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Method;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/**
 * @author Pedro Leite
 */
public class ObjectEntryLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_objectEntryLocalServiceImpl, "_objectFieldLocalService",
			Mockito.mock(ObjectFieldLocalService.class));

		Snapshot<ObjectRelationshipLocalService> snapshot = Mockito.mock(
			Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			Mockito.mock(ObjectRelationshipLocalService.class)
		);
	}

	@Test
	public void testApplyOrderBy() throws Exception {
		try (MockedConstruction<SortDSLQueryVisitor> mockedConstruction =
				Mockito.mockConstruction(
					SortDSLQueryVisitor.class,
					(sortDSLQueryVisitor, context) -> Mockito.when(
						sortDSLQueryVisitor.visit(Mockito.any(), Mockito.any())
					).thenReturn(
						Mockito.mock(DSLQuery.class)
					))) {

			_applyOrderBy(null);

			List<SortDSLQueryVisitor> sortDSLQueryVisitors =
				mockedConstruction.constructed();

			Assert.assertEquals(
				sortDSLQueryVisitors.toString(), 0,
				sortDSLQueryVisitors.size());

			_applyOrderBy(new Sort[] {new Sort(Field.CREATE_DATE, true)});

			sortDSLQueryVisitors = mockedConstruction.constructed();

			Assert.assertEquals(
				sortDSLQueryVisitors.toString(), 1,
				sortDSLQueryVisitors.size());

			_assertSort(Field.CREATE_DATE, true, sortDSLQueryVisitors.get(0));
		}
	}

	@Test
	public void testIsDefaultOrderBy() throws Exception {
		Assert.assertFalse(_isDefaultOrderBy(-1, null, -1));
		Assert.assertFalse(
			_isDefaultOrderBy(
				100, new Sort[] {new Sort(Field.CREATE_DATE, true)}, 0));
		Assert.assertTrue(_isDefaultOrderBy(100, null, 0));
	}

	private void _applyOrderBy(Sort[] sorts) throws Exception {
		Method method = ObjectEntryLocalServiceImpl.class.getDeclaredMethod(
			"_applyOrderBy", DSLQuery.class, ObjectDefinition.class,
			Sort[].class);

		method.setAccessible(true);

		method.invoke(
			_objectEntryLocalServiceImpl, Mockito.mock(DSLQuery.class),
			Mockito.mock(ObjectDefinition.class), sorts);
	}

	private void _assertSort(
			String expectedFieldName, boolean expectedReverse,
			SortDSLQueryVisitor sortDSLQueryVisitor)
		throws Exception {

		ArgumentCaptor<com.liferay.object.internal.sort.Sort> argumentCaptor =
			ArgumentCaptor.forClass(
				com.liferay.object.internal.sort.Sort.class);

		Mockito.verify(
			sortDSLQueryVisitor
		).visit(
			Mockito.any(), argumentCaptor.capture()
		);

		com.liferay.object.internal.sort.Sort sort = argumentCaptor.getValue();

		Assert.assertEquals(expectedFieldName, sort.getFieldName());
		Assert.assertEquals(expectedReverse, sort.isReverse());
	}

	private boolean _isDefaultOrderBy(int end, Sort[] sorts, int start)
		throws Exception {

		Method method = ObjectEntryLocalServiceImpl.class.getDeclaredMethod(
			"_isDefaultOrderBy", int.class, Sort[].class, int.class);

		method.setAccessible(true);

		return (boolean)method.invoke(
			_objectEntryLocalServiceImpl, end, sorts, start);
	}

	private final ObjectEntryLocalServiceImpl _objectEntryLocalServiceImpl =
		new ObjectEntryLocalServiceImpl();

}
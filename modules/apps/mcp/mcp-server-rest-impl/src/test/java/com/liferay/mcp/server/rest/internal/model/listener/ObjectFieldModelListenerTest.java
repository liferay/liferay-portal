/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.cache.MCPServerCacheManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class ObjectFieldModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_objectFieldModelListener, "_mcpServerCacheManager",
			_mcpServerCacheManager);
		ReflectionTestUtil.setFieldValue(
			_objectFieldModelListener, "_objectDefinitionLocalService",
			_objectDefinitionLocalService);
	}

	@Test
	public void testOnAfterCreate() {
		_mockObjectDefinition(true);

		_objectFieldModelListener.onAfterCreate(_mockObjectField());

		_assertCacheCleared();
	}

	@Test
	public void testOnAfterCreateWhenObjectDefinitionIsMissing() {
		_objectFieldModelListener.onAfterCreate(_mockObjectField());

		_assertCacheNotCleared();
	}

	@Test
	public void testOnAfterCreateWhenObjectDefinitionIsNotApproved() {
		_mockObjectDefinition(false);

		_objectFieldModelListener.onAfterCreate(_mockObjectField());

		_assertCacheNotCleared();
	}

	@Test
	public void testOnAfterRemove() {
		_mockObjectDefinition(true);

		_objectFieldModelListener.onAfterRemove(_mockObjectField());

		_assertCacheCleared();
	}

	@Test
	public void testOnAfterRemoveWhenObjectDefinitionIsNotApproved() {
		_mockObjectDefinition(false);

		_objectFieldModelListener.onAfterRemove(_mockObjectField());

		_assertCacheNotCleared();
	}

	@Test
	public void testOnAfterUpdate() {
		_mockObjectDefinition(true);

		_objectFieldModelListener.onAfterUpdate(
			_mockObjectField(), _mockObjectField());

		_assertCacheCleared();
	}

	@Test
	public void testOnAfterUpdateWhenObjectDefinitionIsNotApproved() {
		_mockObjectDefinition(false);

		_objectFieldModelListener.onAfterUpdate(
			_mockObjectField(), _mockObjectField());

		_assertCacheNotCleared();
	}

	private void _assertCacheCleared() {
		Mockito.verify(
			_mcpServerCacheManager
		).clearOpenAPIJSONObjectCache(
			_COMPANY_ID
		);
	}

	private void _assertCacheNotCleared() {
		Mockito.verify(
			_mcpServerCacheManager, Mockito.never()
		).clearOpenAPIJSONObjectCache(
			Mockito.anyLong()
		);
	}

	private void _mockObjectDefinition(boolean approved) {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.isApproved()
		).thenReturn(
			approved
		);

		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinition(
				_OBJECT_DEFINITION_ID)
		).thenReturn(
			objectDefinition
		);
	}

	private ObjectField _mockObjectField() {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			objectField.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		return objectField;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private final MCPServerCacheManager _mcpServerCacheManager = Mockito.mock(
		MCPServerCacheManager.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectFieldModelListener _objectFieldModelListener =
		new ObjectFieldModelListener();

}
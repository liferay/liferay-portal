/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.cache.MCPServerCacheManager;
import com.liferay.object.model.ObjectDefinition;
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
public class ObjectDefinitionModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_objectDefinitionModelListener, "_mcpServerCacheManager",
			_mcpServerCacheManager);
	}

	@Test
	public void testOnAfterCreate() {
		_objectDefinitionModelListener.onAfterCreate(
			_mockObjectDefinition(true));

		_assertCacheCleared();
	}

	@Test
	public void testOnAfterCreateWhenObjectDefinitionIsNotApproved() {
		_objectDefinitionModelListener.onAfterCreate(
			_mockObjectDefinition(false));

		_assertCacheNotCleared();
	}

	@Test
	public void testOnAfterRemove() {
		_objectDefinitionModelListener.onAfterRemove(
			_mockObjectDefinition(true));

		_assertCacheCleared();
	}

	@Test
	public void testOnAfterRemoveWhenObjectDefinitionIsNotApproved() {
		_objectDefinitionModelListener.onAfterRemove(
			_mockObjectDefinition(false));

		_assertCacheNotCleared();
	}

	@Test
	public void testOnAfterUpdate() {
		_objectDefinitionModelListener.onAfterUpdate(
			_mockObjectDefinition(false), _mockObjectDefinition(true));

		_assertCacheCleared();
	}

	@Test
	public void testOnAfterUpdateWhenObjectDefinitionIsNotApproved() {
		_objectDefinitionModelListener.onAfterUpdate(
			_mockObjectDefinition(false), _mockObjectDefinition(false));

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

	private ObjectDefinition _mockObjectDefinition(boolean approved) {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			objectDefinition.isApproved()
		).thenReturn(
			approved
		);

		return objectDefinition;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final MCPServerCacheManager _mcpServerCacheManager = Mockito.mock(
		MCPServerCacheManager.class);
	private final ObjectDefinitionModelListener _objectDefinitionModelListener =
		new ObjectDefinitionModelListener();

}
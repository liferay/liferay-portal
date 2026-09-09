/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.renderer;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Gomes
 */
public class ObjectEntryRowInfoItemRendererTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			_objectEntry.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry();

		objectEntry.setProperties(
			HashMapBuilder.<String, Object>put(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong()
			).put(
				_OBJECT_FIELD_NAME, _TITLE
			).build());

		Mockito.when(
			_objectEntryManager.getObjectEntry(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				Mockito.any(), Mockito.nullable(String.class))
		).thenReturn(
			objectEntry
		);

		Mockito.when(
			_objectFieldLocalService.getActiveObjectFields(Mockito.anyList())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(0)
		);

		List<ObjectField> objectFields = Arrays.asList(
			_createObjectField(false, _OBJECT_FIELD_NAME),
			_createObjectField(true, "createDate"));

		Mockito.when(
			_objectFieldLocalService.getObjectFields(_OBJECT_DEFINITION_ID)
		).thenReturn(
			objectFields
		);

		Mockito.when(
			_objectScopeProviderRegistry.getObjectScopeProvider(
				Mockito.nullable(String.class))
		).thenReturn(
			Mockito.mock(ObjectScopeProvider.class)
		);

		Mockito.when(
			_servletContext.getRequestDispatcher(Mockito.anyString())
		).thenReturn(
			Mockito.mock(RequestDispatcher.class)
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);
	}

	@Test
	public void testRender() {
		ObjectEntryRowInfoItemRenderer objectEntryRowInfoItemRenderer =
			new ObjectEntryRowInfoItemRenderer(
				Mockito.mock(AssetDisplayPageFriendlyURLProvider.class),
				_objectDefinition, _objectEntryManager,
				_objectFieldLocalService, _objectScopeProviderRegistry,
				_servletContext);

		objectEntryRowInfoItemRenderer.render(
			_objectEntry, _httpServletRequest,
			Mockito.mock(HttpServletResponse.class));

		Mockito.verify(
			_httpServletRequest
		).setAttribute(
			ObjectWebKeys.OBJECT_ENTRY_VALUES,
			Collections.singletonMap(_OBJECT_FIELD_NAME, _TITLE)
		);
	}

	private ObjectField _createObjectField(boolean metadata, String name) {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			objectField.isMetadata()
		).thenReturn(
			metadata
		);

		return objectField;
	}

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final String _OBJECT_FIELD_NAME =
		RandomTestUtil.randomString();

	private static final String _TITLE = RandomTestUtil.randomString();

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectEntry _objectEntry = Mockito.mock(ObjectEntry.class);
	private final ObjectEntryManager _objectEntryManager = Mockito.mock(
		ObjectEntryManager.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry =
		Mockito.mock(ObjectScopeProviderRegistry.class);
	private final ServletContext _servletContext = Mockito.mock(
		ServletContext.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
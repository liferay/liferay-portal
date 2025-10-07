/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.list.renderer;

import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.info.list.renderer.DefaultInfoListRendererContext;
import com.liferay.info.taglib.servlet.taglib.InfoListBasicTableTag;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Nathaly Gomes
 */
public class ObjectEntryTableInfoListRendererTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		Mockito.when(
			_objectFieldLocalService.getActiveObjectFields(Mockito.anyList())
		).thenReturn(
			Collections.singletonList(_objectField)
		);

		Mockito.when(
			_objectFieldLocalService.getObjectFields(
				Mockito.anyLong(), Mockito.eq(false))
		).thenReturn(
			Collections.singletonList(_objectField)
		);

		_setUpPortalUtil();
	}

	@Test
	public void testRender() {
		ObjectEntryTableInfoListRenderer objectEntryTableInfoListRenderer =
			Mockito.spy(
				new ObjectEntryTableInfoListRenderer(
					Mockito.mock(InfoItemRendererRegistry.class),
					Mockito.mock(ObjectDefinition.class),
					_objectFieldLocalService));

		InfoListBasicTableTag infoListBasicTableTag = Mockito.mock(
			InfoListBasicTableTag.class);

		Mockito.when(
			objectEntryTableInfoListRenderer.getInfoListBasicTableTag()
		).thenReturn(
			infoListBasicTableTag
		);

		objectEntryTableInfoListRenderer.render(
			Collections.singletonList(Mockito.mock(ObjectEntry.class)),
			new DefaultInfoListRendererContext(
				_httpServletRequest, Mockito.mock(HttpServletResponse.class)));

		Mockito.verify(
			_objectField
		).getLabel(
			LocaleUtil.US
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Mockito.when(
			_portal.getLocale(_httpServletRequest)
		).thenReturn(
			LocaleUtil.US
		);

		portalUtil.setPortal(_portal);
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final ObjectField _objectField = Mockito.mock(ObjectField.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final Portal _portal = Mockito.mock(Portal.class);

}
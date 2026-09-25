/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.renderer;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.StringWriter;
import java.io.Writer;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockSettings;
import org.mockito.Mockito;

/**
 * @author Juanjo Fernandez
 */
public class FDSRendererImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_fdsRendererImpl, "_fdsViewRegistry",
			Mockito.mock(FDSViewRegistry.class));

		Portal portal = Mockito.mock(Portal.class);

		MockSettings mockSettings = Mockito.withSettings();

		Mockito.when(
			portal.getControlPanelPortletURL(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.anyString())
		).thenReturn(
			Mockito.mock(
				PortletURL.class,
				mockSettings.extraInterfaces(ResourceURL.class))
		);

		ReflectionTestUtil.setFieldValue(_fdsRendererImpl, "_portal", portal);

		ReflectionTestUtil.setFieldValue(
			_fdsRendererImpl, "_reactRenderer", _reactRenderer);
		ReflectionTestUtil.setFieldValue(
			_fdsRendererImpl, "_serviceTrackerMap", _serviceTrackerMap);
		ReflectionTestUtil.setFieldValue(
			_fdsRendererImpl, "_systemFDSEntryRegistry",
			_systemFDSEntryRegistry);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			Mockito.mock(ThemeDisplay.class)
		);
	}

	@Test
	public void testRenderWhenDataSetIsCustom() throws Exception {
		_registerFDSSerializer(FDSSerializer.TYPE_CUSTOM);

		Map<String, Object> props = _render();

		Assert.assertTrue((boolean)props.get("searchAsYouType"));
		Assert.assertTrue((boolean)props.get("searchSuggestionsEnabled"));
	}

	@Test
	public void testRenderWhenDataSetIsSystem() throws Exception {
		_registerFDSSerializer(FDSSerializer.TYPE_SYSTEM);

		Mockito.when(
			_systemFDSEntryRegistry.getSystemFDSEntry(_FDS_NAME)
		).thenReturn(
			Mockito.mock(SystemFDSEntry.class)
		);

		Map<String, Object> props = _render();

		Assert.assertTrue((boolean)props.get("searchAsYouType"));
		Assert.assertTrue((boolean)props.get("searchSuggestionsEnabled"));
	}

	@Test
	public void testRenderWhenDataSetIsViewsOnly() throws Exception {
		_registerFDSSerializer(FDSSerializer.TYPE_SYSTEM);

		Map<String, Object> props = _render();

		Assert.assertFalse(props.containsKey("searchAsYouType"));
		Assert.assertFalse(props.containsKey("searchSuggestionsEnabled"));
	}

	private void _registerFDSSerializer(String type) {
		FDSSerializer fdsSerializer = Mockito.mock(FDSSerializer.class);

		Mockito.when(
			fdsSerializer.isAvailable(_FDS_NAME, _httpServletRequest)
		).thenReturn(
			true
		);

		Mockito.when(
			fdsSerializer.serializeSearchAsYouType(
				_FDS_NAME, _httpServletRequest)
		).thenReturn(
			true
		);

		Mockito.when(
			fdsSerializer.serializeSearchSuggestionsEnabled(
				_FDS_NAME, _httpServletRequest)
		).thenReturn(
			true
		);

		Mockito.when(
			_serviceTrackerMap.getService(type)
		).thenReturn(
			fdsSerializer
		);
	}

	private Map<String, Object> _render() throws Exception {
		_fdsRendererImpl.render(
			null, RandomTestUtil.randomString(), _FDS_NAME, _httpServletRequest,
			null, RandomTestUtil.randomBoolean(), null, new StringWriter());

		ArgumentCaptor<Map<String, Object>> argumentCaptor =
			ArgumentCaptor.forClass(Map.class);

		Mockito.verify(
			_reactRenderer
		).renderReact(
			Mockito.any(ComponentDescriptor.class), argumentCaptor.capture(),
			Mockito.eq(_httpServletRequest), Mockito.any(Writer.class)
		);

		return argumentCaptor.getValue();
	}

	private static final String _FDS_NAME = RandomTestUtil.randomString();

	private final FDSRendererImpl _fdsRendererImpl = new FDSRendererImpl();
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final ReactRenderer _reactRenderer = Mockito.mock(
		ReactRenderer.class);
	private final ServiceTrackerMap<String, FDSSerializer> _serviceTrackerMap =
		Mockito.mock(ServiceTrackerMap.class);
	private final SystemFDSEntryRegistry _systemFDSEntryRegistry = Mockito.mock(
		SystemFDSEntryRegistry.class);

}
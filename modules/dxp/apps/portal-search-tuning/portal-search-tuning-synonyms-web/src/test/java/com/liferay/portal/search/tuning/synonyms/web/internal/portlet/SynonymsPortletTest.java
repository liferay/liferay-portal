/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCCommandCache;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class SynonymsPortletTest extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		setUpPortletPreferencesFactoryUtil();

		_synonymsPortlet = new SynonymsPortlet();

		ReflectionTestUtil.setFieldValue(
			_synonymsPortlet, "_language", _language);
		ReflectionTestUtil.setFieldValue(_synonymsPortlet, "_portal", portal);
		ReflectionTestUtil.setFieldValue(
			_synonymsPortlet, "_queries", _queries);
		ReflectionTestUtil.setFieldValue(
			_synonymsPortlet, "_renderMVCCommandCache",
			Mockito.mock(MVCCommandCache.class));
		ReflectionTestUtil.setFieldValue(
			_synonymsPortlet, "_searchEngineAdapter", searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			_synonymsPortlet, "_searchEngineInformation",
			_searchEngineInformation);
		ReflectionTestUtil.setFieldValue(_synonymsPortlet, "_sorts", _sorts);
		ReflectionTestUtil.setFieldValue(
			_synonymsPortlet, "_synonymSetIndexNameBuilder",
			synonymSetIndexNameBuilder);
	}

	@Test
	public void testRender() throws Exception {
		_setUpPortletConfig();
		_setUpRenderMVCCommandCache();
		_setUpRenderRequest();

		setUpPortal(Mockito.mock(HttpServletRequest.class));
		setUpRenderResponse(_renderResponse);
		setUpSearchEngineAdapter(setUpSearchHits("car,automobile"));
		setUpSynonymSetIndexNameBuilder();

		_synonymsPortlet.render(_renderRequest, _renderResponse);
		Mockito.verify(
			_renderRequest, Mockito.times(1)
		).getWindowState();
	}

	private void _setUpPortletConfig() {
		ResourceBundle resourceBundle = _setUpResourceBundle();

		PortletConfig config = Mockito.mock(PortletConfig.class);

		Mockito.doReturn(
			resourceBundle
		).when(
			config
		).getResourceBundle(
			Mockito.any()
		);

		ReflectionTestUtil.setFieldValue(_synonymsPortlet, "config", config);
	}

	@SuppressWarnings("unchecked")
	private void _setUpRenderMVCCommandCache() {
		MVCCommandCache<MVCRenderCommand> mvcCommandCache = Mockito.mock(
			MVCCommandCache.class);

		Mockito.doReturn(
			MVCRenderCommand.EMPTY
		).when(
			mvcCommandCache
		).getMVCCommand(
			Mockito.anyString()
		);

		ReflectionTestUtil.setFieldValue(
			_synonymsPortlet, "_renderMVCCommandCache", mvcCommandCache);
	}

	private void _setUpRenderRequest() {
		Mockito.doReturn(
			WindowState.MINIMIZED
		).when(
			_renderRequest
		).getWindowState();
	}

	private ResourceBundle _setUpResourceBundle() {
		return new ResourceBundle() {

			@Override
			public Enumeration<String> getKeys() {
				return Collections.emptyEnumeration();
			}

			@Override
			protected Object handleGetObject(String key) {
				return "value";
			}

		};
	}

	private final Language _language = Mockito.mock(Language.class);
	private final Queries _queries = Mockito.mock(Queries.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final SearchEngineInformation _searchEngineInformation =
		Mockito.mock(SearchEngineInformation.class);
	private final Sorts _sorts = Mockito.mock(Sorts.class);
	private SynonymsPortlet _synonymsPortlet;

}
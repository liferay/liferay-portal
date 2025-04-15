/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet.action;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class EditSynonymSetsMVCRenderCommandTest
	extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_editSynonymSetsMVCRenderCommand =
			new EditSynonymSetsMVCRenderCommand();

		ReflectionTestUtil.setFieldValue(
			_editSynonymSetsMVCRenderCommand, "_portal", portal);
		ReflectionTestUtil.setFieldValue(
			_editSynonymSetsMVCRenderCommand, "_synonymSetIndexNameBuilder",
			synonymSetIndexNameBuilder);
	}

	@Test
	public void testRender() throws Exception {
		setUpPortal(_httpServletRequest);

		Assert.assertEquals(
			"/edit_synonym_sets.jsp",
			_editSynonymSetsMVCRenderCommand.render(
				_renderRequest, _renderResponse));
	}

	private EditSynonymSetsMVCRenderCommand _editSynonymSetsMVCRenderCommand;
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);

}
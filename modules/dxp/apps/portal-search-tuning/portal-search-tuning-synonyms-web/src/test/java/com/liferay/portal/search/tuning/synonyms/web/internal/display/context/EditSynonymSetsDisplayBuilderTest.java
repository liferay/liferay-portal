/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.display.context;

import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class EditSynonymSetsDisplayBuilderTest extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_editSynonymSetsDisplayBuilder = new EditSynonymSetsDisplayBuilder(
			_httpServletRequest, portal, _renderRequest, _renderResponse,
			_synonymSetIndexNameBuilder, synonymSetIndexReader);
	}

	@Test
	public void testBulder() {
		setUpHttpServletRequestParameterValue(
			_httpServletRequest, "redirect", "redirect");
		setUpPortletRequestParameterValue(_renderRequest, "synonymSetId", "id");
		setUpRenderResponse(_renderResponse);
		setUpSynonymSetIndexReader("id", "car,automobile");

		EditSynonymSetsDisplayContext editSynonymSetsDisplayContext =
			_editSynonymSetsDisplayBuilder.build();

		Assert.assertEquals(
			"id", editSynonymSetsDisplayContext.getSynonymSetId());
		Assert.assertEquals(
			"redirect", editSynonymSetsDisplayContext.getBackURL());
		Assert.assertEquals(
			"redirect", editSynonymSetsDisplayContext.getRedirect());
		Assert.assertEquals(
			"synonymSet", editSynonymSetsDisplayContext.getInputName());
		Assert.assertEquals(
			"synonymSetsForm", editSynonymSetsDisplayContext.getFormName());

		Map<String, Object> data = editSynonymSetsDisplayContext.getData();

		Assert.assertEquals("car,automobile", data.get("synonymSets"));
		Assert.assertEquals("namespace-synonymSet", data.get("inputName"));
		Assert.assertEquals("namespace-synonymSetsForm", data.get("formName"));
	}

	private EditSynonymSetsDisplayBuilder _editSynonymSetsDisplayBuilder;
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder =
		Mockito.mock(SynonymSetIndexNameBuilder.class);

}
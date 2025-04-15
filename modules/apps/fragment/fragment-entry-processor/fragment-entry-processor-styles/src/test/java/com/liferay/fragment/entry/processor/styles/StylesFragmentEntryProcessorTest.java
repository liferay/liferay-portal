/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.styles;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.layout.constants.LayoutWebKeys;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class StylesFragmentEntryProcessorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_setUpDocumentStylesFragmentEntryProcessor();
		_setUpStylesFragmentEntryValidator();
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLFragmentWithStylesAttribute() {
		FragmentEntryLink fragmentEntryLink = _getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		layoutStructure.addLayoutStructureItem(
			containerStyledLayoutStructureItem);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.addFragmentStyledLayoutStructureItem(
					fragmentEntryLink.getFragmentEntryLinkId(),
					containerStyledLayoutStructureItem.getItemId(), 0);

		fragmentStyledLayoutStructureItem.setCssClasses(Collections.emptySet());

		layoutStructure.addLayoutStructureItem(
			fragmentStyledLayoutStructureItem);

		String html = "<div data-lfr-styles><span>Test</span>Fragment</div>";

		Document document = _getDocument(html);

		_stylesDocumentFragmentEntryProcessor.processFragmentEntryLinkHTML(
			fragmentEntryLink, document,
			new DefaultFragmentEntryProcessorContext(
				_getMockHttpServletRequest(layoutStructure), null,
				FragmentEntryLinkConstants.EDIT,
				LocaleUtil.getMostRelevantLocale()));

		String layoutStructureItemUniqueCssClass =
			fragmentStyledLayoutStructureItem.getUniqueCssClass();

		Elements elements = document.getElementsByClass(
			layoutStructureItemUniqueCssClass);

		Assert.assertEquals(1, elements.size());
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testValidateFragmentEntryHTMLInvalidHTML() throws Exception {
		_stylesFragmentEntryValidator.validateFragmentEntryHTML(
			"<div data-lfr-styles><span data-lfr-styles>Test</span>Fragment" +
				"</div>",
			null, LocaleUtil.getDefault());
	}

	private static void _setUpDocumentStylesFragmentEntryProcessor() {
		_stylesDocumentFragmentEntryProcessor =
			new StylesDocumentFragmentEntryProcessor();

		ReflectionTestUtil.setFieldValue(
			_stylesDocumentFragmentEntryProcessor,
			"_layoutPageTemplateStructureLocalService",
			Mockito.mock(LayoutPageTemplateStructureLocalService.class));
	}

	private static void _setUpStylesFragmentEntryValidator() {
		_stylesFragmentEntryValidator = new StylesFragmentEntryValidator();

		ReflectionTestUtil.setFieldValue(
			_stylesFragmentEntryValidator, "_language",
			Mockito.mock(Language.class));
	}

	private Document _getDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		document.outputSettings(
			new Document.OutputSettings() {
				{
					prettyPrint(false);
				}
			});

		return document;
	}

	private FragmentEntryLink _getMockFragmentEntryLink() {
		FragmentEntryLink fragmentEntryLink = Mockito.mock(
			FragmentEntryLink.class);

		Mockito.when(
			fragmentEntryLink.getFragmentEntryLinkId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			fragmentEntryLink.getRendererKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return fragmentEntryLink;
	}

	private HttpServletRequest _getMockHttpServletRequest(
		LayoutStructure layoutStructure) {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(LayoutWebKeys.LAYOUT_STRUCTURE)
		).thenReturn(
			layoutStructure
		);

		return httpServletRequest;
	}

	private static StylesDocumentFragmentEntryProcessor
		_stylesDocumentFragmentEntryProcessor;
	private static StylesFragmentEntryValidator _stylesFragmentEntryValidator;

}
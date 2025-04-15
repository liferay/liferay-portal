/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.drop.zone;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.layout.constants.LayoutWebKeys;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class DropZoneFragmentEntryProcessorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_setUpDropZoneDocumentFragmentEntryProcessor();
		_setUpDropZoneFragmentEntryValidator();
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditAddingDropZone()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		String dropZoneId1 = RandomTestUtil.randomString();
		String dropZoneId2 = RandomTestUtil.randomString();

		FragmentDropZoneLayoutStructureItem[]
			fragmentDropZoneLayoutStructureItems =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItems(
						fragmentEntryLink, layoutStructure, dropZoneId1,
						dropZoneId2);

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem1 =
				fragmentDropZoneLayoutStructureItems[0];

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem2 =
				fragmentDropZoneLayoutStructureItems[1];

		String newDropZoneId = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getExpectedHTML(
				new KeyValuePair(
					dropZoneId1,
					fragmentDropZoneLayoutStructureItem1.getItemId()),
				new KeyValuePair(newDropZoneId, StringPool.BLANK),
				new KeyValuePair(
					dropZoneId2,
					fragmentDropZoneLayoutStructureItem2.getItemId())),
			_processFragmentEntryLinkHTML(
				fragmentEntryLink,
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					dropZoneId1, newDropZoneId, dropZoneId2),
				layoutStructure));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditMode() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItem(
						fragmentEntryLink, layoutStructure, StringPool.BLANK);

		Assert.assertEquals(
			_getExpectedHTML(
				StringPool.BLANK,
				fragmentDropZoneLayoutStructureItem.getItemId()),
			_processFragmentEntryLinkHTML(
				fragmentEntryLink,
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					StringPool.BLANK),
				layoutStructure));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditModeDifferentIds()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		FragmentEntryProcessorDropZoneTestUtil.
			addFragmentDropZoneLayoutStructureItem(
				fragmentEntryLink, layoutStructure,
				RandomTestUtil.randomString());

		String elementDropZoneId = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getExpectedHTML(elementDropZoneId, StringPool.BLANK),
			_processFragmentEntryLinkHTML(
				fragmentEntryLink,
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					elementDropZoneId),
				layoutStructure));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditModeSameIds()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		String fragmentDropZoneId = RandomTestUtil.randomString();

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItem(
						fragmentEntryLink, layoutStructure, fragmentDropZoneId);

		Assert.assertEquals(
			_getExpectedHTML(
				fragmentDropZoneId,
				fragmentDropZoneLayoutStructureItem.getItemId()),
			_processFragmentEntryLinkHTML(
				fragmentEntryLink,
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					fragmentDropZoneId),
				layoutStructure));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditMultipleDropZonesWithoutIds()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		FragmentDropZoneLayoutStructureItem[]
			fragmentDropZoneLayoutStructureItems =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItems(
						fragmentEntryLink, layoutStructure, StringPool.BLANK,
						StringPool.BLANK);

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem1 =
				fragmentDropZoneLayoutStructureItems[0];
		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem2 =
				fragmentDropZoneLayoutStructureItems[1];

		String dropZoneId1 = RandomTestUtil.randomString();
		String dropZoneId2 = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getExpectedHTML(
				new KeyValuePair(
					dropZoneId1,
					fragmentDropZoneLayoutStructureItem1.getItemId()),
				new KeyValuePair(
					dropZoneId2,
					fragmentDropZoneLayoutStructureItem2.getItemId())),
			_processFragmentEntryLinkHTML(
				fragmentEntryLink,
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					dropZoneId1, dropZoneId2),
				layoutStructure));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditRemovingDropZone()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		String dropZoneId1 = RandomTestUtil.randomString();
		String dropZoneId2 = RandomTestUtil.randomString();

		FragmentDropZoneLayoutStructureItem[]
			fragmentDropZoneLayoutStructureItems =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItems(
						fragmentEntryLink, layoutStructure, dropZoneId1,
						RandomTestUtil.randomString(), dropZoneId2);

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem1 =
				fragmentDropZoneLayoutStructureItems[0];

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem2 =
				fragmentDropZoneLayoutStructureItems[2];

		Assert.assertEquals(
			_getExpectedHTML(
				new KeyValuePair(
					dropZoneId1,
					fragmentDropZoneLayoutStructureItem1.getItemId()),
				new KeyValuePair(
					dropZoneId2,
					fragmentDropZoneLayoutStructureItem2.getItemId())),
			_processFragmentEntryLinkHTML(
				fragmentEntryLink,
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					dropZoneId1, dropZoneId2),
				layoutStructure));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditSwappingDropZones()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		String dropZoneId1 = RandomTestUtil.randomString();
		String dropZoneId2 = RandomTestUtil.randomString();
		String dropZoneId3 = RandomTestUtil.randomString();

		FragmentDropZoneLayoutStructureItem[]
			fragmentDropZoneLayoutStructureItems =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItems(
						fragmentEntryLink, layoutStructure, dropZoneId1,
						dropZoneId2, dropZoneId3);

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem1 =
				fragmentDropZoneLayoutStructureItems[0];

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem2 =
				fragmentDropZoneLayoutStructureItems[1];

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem3 =
				fragmentDropZoneLayoutStructureItems[2];

		Assert.assertEquals(
			_getExpectedHTML(
				new KeyValuePair(
					dropZoneId2,
					fragmentDropZoneLayoutStructureItem2.getItemId()),
				new KeyValuePair(
					dropZoneId3,
					fragmentDropZoneLayoutStructureItem3.getItemId()),
				new KeyValuePair(
					dropZoneId1,
					fragmentDropZoneLayoutStructureItem1.getItemId())),
			_processFragmentEntryLinkHTML(
				fragmentEntryLink,
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					dropZoneId2, dropZoneId3, dropZoneId1),
				layoutStructure));
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testValidateFragmentEntryHTMLDuplicatedId() throws Exception {
		String dropZoneId = RandomTestUtil.randomString();

		_dropZoneFragmentEntryValidator.validateFragmentEntryHTML(
			_getDocument(
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					dropZoneId, dropZoneId)),
			null, LocaleUtil.getDefault());
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testValidateFragmentEntryHTMLMissingId() throws Exception {
		_dropZoneFragmentEntryValidator.validateFragmentEntryHTML(
			_getDocument(
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					StringPool.BLANK, RandomTestUtil.randomString())),
			null, LocaleUtil.getDefault());
	}

	@Test
	public void testValidateFragmentEntryHTMLValidWithIds() throws Exception {
		_dropZoneFragmentEntryValidator.validateFragmentEntryHTML(
			_getDocument(
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())),
			null, LocaleUtil.getDefault());
	}

	@Test
	public void testValidateFragmentEntryHTMLValidWithoutIds()
		throws Exception {

		_dropZoneFragmentEntryValidator.validateFragmentEntryHTML(
			_getDocument(
				FragmentEntryProcessorDropZoneTestUtil.getHTML(
					StringPool.BLANK, StringPool.BLANK)),
			null, LocaleUtil.getDefault());
	}

	private static void _setUpDropZoneDocumentFragmentEntryProcessor() {
		_dropZoneDocumentFragmentEntryProcessor =
			new DropZoneDocumentFragmentEntryProcessor();

		ReflectionTestUtil.setFieldValue(
			_dropZoneDocumentFragmentEntryProcessor,
			"_layoutPageTemplateStructureLocalService",
			Mockito.mock(LayoutPageTemplateStructureLocalService.class));
	}

	private static void _setUpDropZoneFragmentEntryValidator() {
		_dropZoneFragmentEntryValidator = new DropZoneFragmentEntryValidator();

		ReflectionTestUtil.setFieldValue(
			_dropZoneFragmentEntryValidator, "_language",
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

	private String _getExpectedHTML(
		KeyValuePair... dropZoneIdItemIdKeyValuePairs) {

		StringBundler sb = new StringBundler("<div class=\"fragment_1\">");

		for (KeyValuePair keyValuePair : dropZoneIdItemIdKeyValuePairs) {
			sb.append("<lfr-drop-zone");

			String dropZoneId = keyValuePair.getKey();

			if (!Validator.isBlank(dropZoneId)) {
				sb.append(" data-lfr-drop-zone-id=\"");
				sb.append(dropZoneId);
				sb.append(StringPool.QUOTE);
			}

			String itemId = keyValuePair.getValue();

			if (!Validator.isBlank(itemId)) {
				sb.append(" uuid=\"");
				sb.append(itemId);
				sb.append(StringPool.QUOTE);
			}

			sb.append("></lfr-drop-zone>");
		}

		sb.append("</div>");

		return sb.toString();
	}

	private String _getExpectedHTML(String dropZoneId, String itemId) {
		return _getExpectedHTML(new KeyValuePair(dropZoneId, itemId));
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

	private String _processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink, String html,
			LayoutStructure layoutStructure)
		throws Exception {

		Document document = _getDocument(html);

		_dropZoneDocumentFragmentEntryProcessor.processFragmentEntryLinkHTML(
			fragmentEntryLink, document,
			new DefaultFragmentEntryProcessorContext(
				_getMockHttpServletRequest(layoutStructure), null,
				FragmentEntryLinkConstants.EDIT,
				LocaleUtil.getMostRelevantLocale()));

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	private static DropZoneDocumentFragmentEntryProcessor
		_dropZoneDocumentFragmentEntryProcessor;
	private static DropZoneFragmentEntryValidator
		_dropZoneFragmentEntryValidator;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.editor.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class ContentPageEditorDefaultEditorConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_themeDisplay = _getThemeDisplay();
	}

	@Test
	public void testCommentEditor() {
		_assertTextEditorConfigJSONObject(
			"pageEditorCommentEditor", _EXTRA_PLUGINS + ",autocomplete");
	}

	@Test
	public void testRichTextEditor() {
		JSONObject jsonObject = _getEditorConfigurationConfigJSONObject(
			"fragmentEntryLinkRichTextEditor");

		Assert.assertEquals(
			_ALLOWED_CONTENT, jsonObject.getString("allowedContent"));
		Assert.assertFalse(jsonObject.getBoolean("autoParagraph"));
		_assertItemSelectorURL(
			"selectItem", jsonObject.getString("documentBrowseLinkUrl"));
		Assert.assertEquals(
			_EXTRA_PLUGINS, jsonObject.getString("extraPlugins"));
		_assertItemSelectorURL(
			"selectItem",
			jsonObject.getString("filebrowserImageBrowseLinkUrl"));
		_assertItemSelectorURL(
			"selectItem", jsonObject.getString("filebrowserImageBrowseUrl"));
		Assert.assertEquals(
			_REMOVE_PLUGINS, jsonObject.getString("removePlugins"));
		Assert.assertEquals("moono-lisa", jsonObject.getString("skin"));
		Assert.assertEquals(
			_themeDisplay.getPathThemeSpritemap(),
			jsonObject.getString("spritemap"));
		_assertToolbarsJSONObject(jsonObject.getJSONObject("toolbars"));
	}

	@Test
	public void testTextEditor() {
		_assertTextEditorConfigJSONObject(
			"fragmentEntryLinkEditor", _EXTRA_PLUGINS);
	}

	private void _assertItemSelectorURL(String eventName, String url) {
		Assert.assertTrue(
			url,
			url.contains(
				"_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_" +
					"itemSelectedEventName=_EDITOR_NAME_" + eventName));
	}

	private void _assertTextEditorConfigJSONObject(
		String editorConfigKey, String extraPlugins) {

		JSONObject jsonObject = _getEditorConfigurationConfigJSONObject(
			editorConfigKey);

		Assert.assertEquals(
			StringPool.BLANK, jsonObject.getString("allowedContent"));
		Assert.assertEquals("br", jsonObject.getString("disallowedContent"));
		_assertItemSelectorURL(
			"selectItem", jsonObject.getString("documentBrowseLinkUrl"));
		Assert.assertEquals(2, jsonObject.getInt("enterMode"));
		Assert.assertEquals(extraPlugins, jsonObject.getString("extraPlugins"));
		_assertItemSelectorURL(
			"selectImage",
			jsonObject.getString("filebrowserImageBrowseLinkUrl"));
		_assertItemSelectorURL(
			"selectImage", jsonObject.getString("filebrowserImageBrowseUrl"));
		Assert.assertEquals(
			_REMOVE_PLUGINS, jsonObject.getString("removePlugins"));
		Assert.assertEquals("moono-lisa", jsonObject.getString("skin"));

		JSONObject toolbarsJSONObject = jsonObject.getJSONObject("toolbars");

		Assert.assertTrue(
			toolbarsJSONObject.toString(),
			JSONUtil.isEmpty(toolbarsJSONObject));
	}

	private void _assertToolbarsJSONObject(JSONObject jsonObject) {
		JSONObject addJSONObject = jsonObject.getJSONObject("add");

		JSONArray buttonsJSONArray = addJSONObject.getJSONArray("buttons");

		Assert.assertEquals(
			JSONUtil.putAll(
				"image", "hline"
			).toString(),
			buttonsJSONArray.toString());

		JSONObject stylesJSONObject = jsonObject.getJSONObject("styles");

		JSONArray selectionsStylesJSONArray = stylesJSONObject.getJSONArray(
			"selections");

		Assert.assertEquals(
			selectionsStylesJSONArray.toString(), 4,
			selectionsStylesJSONArray.length());

		JSONObject selectionsStylesJSONObject =
			selectionsStylesJSONArray.getJSONObject(0);

		Assert.assertEquals(
			JSONUtil.put(
				"buttons",
				JSONUtil.putAll("imageLeft", "imageCenter", "imageRight")
			).put(
				"name", "image"
			).put(
				"test", "AlloyEditor.SelectionTest.image"
			).toString(),
			selectionsStylesJSONObject.toString());

		selectionsStylesJSONObject = selectionsStylesJSONArray.getJSONObject(1);

		Assert.assertEquals(
			JSONUtil.put(
				"buttons", JSONUtil.put("linkEditBrowse")
			).put(
				"name", "link"
			).put(
				"test", "AlloyEditor.SelectionTest.link"
			).toString(),
			selectionsStylesJSONObject.toString());

		_assertToolbarStylesSelectionsTextJSONObject(
			selectionsStylesJSONArray.getJSONObject(2));

		selectionsStylesJSONObject = selectionsStylesJSONArray.getJSONObject(3);

		Assert.assertEquals(
			JSONUtil.put(
				"buttons",
				JSONUtil.putAll(
					"tableHeading", "tableRow", "tableColumn", "tableCell",
					"tableRemove")
			).put(
				"getArrowBoxClasses",
				"AlloyEditor.SelectionGetArrowBoxClasses.table"
			).put(
				"name", "table"
			).put(
				"setPosition", "AlloyEditor.SelectionSetPosition.table"
			).put(
				"test", "AlloyEditor.SelectionTest.table"
			).toString(),
			selectionsStylesJSONObject.toString());
	}

	private void _assertToolbarStylesSelectionsTextJSONObject(
		JSONObject jsonObject) {

		JSONArray buttonsJSONArray = jsonObject.getJSONArray("buttons");

		JSONObject styleFormatsJSONObject = buttonsJSONArray.getJSONObject(0);

		Assert.assertTrue(styleFormatsJSONObject.has("cfg"));
		Assert.assertEquals("styles", styleFormatsJSONObject.getString("name"));

		for (int i = 1; i < buttonsJSONArray.length(); i++) {
			ArrayUtil.contains(
				_STYLES_SELECTIONS_TEXT, buttonsJSONArray.getString(i));
		}
	}

	private JSONObject _getEditorConfigurationConfigJSONObject(
		String editorConfigKey) {

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
				editorConfigKey, StringPool.BLANK, Collections.emptyMap(),
				_themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(
					_themeDisplay.getRequest()));

		return editorConfiguration.getConfigJSONObject();
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			layout);

		themeDisplay.setPathThemeSpritemap(RandomTestUtil.randomString());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return themeDisplay;
	}

	private static final String _ALLOWED_CONTENT = StringBundler.concat(
		"b code em h1 h2 h3 h4 h5 h6 hr i p pre strong u [*](*){*}; a[*](*); ",
		"div[*](*){text-align}; img[*](*){*}; p[*](*); li ol ul ",
		"[*](*){*};table[border, cellpadding, cellspacing] {width}; tbody td ",
		"th[scope, colspan, rowspan]; td[colspan, rowspan]; thead tr[scope]; ",
		"span[*](*){*}; ");

	private static final String _EXTRA_PLUGINS = StringBundler.concat(
		"autolink,ae_dragresize,ae_addimages,ae_imagealignment,",
		"ae_placeholder,ae_selectionregion,ae_tableresize,",
		"ae_tabletools,ae_uicore,itemselector,media,adaptivemedia");

	private static final String _REMOVE_PLUGINS =
		"contextmenu,elementspath,floatingspace,image,liststyle," +
			"magicline,resize,tabletools,toolbar,ae_embed";

	private static final String[] _STYLES_SELECTIONS_TEXT = {
		"bold", "italic", "underline", "ol", "ul", "linkBrowse", "table",
		"paragraphLeft", "paragraphCenter", "paragraphRight",
		"paragraphJustify", "spacing", "color", "removeFormat"
	};

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private ThemeDisplay _themeDisplay;

}
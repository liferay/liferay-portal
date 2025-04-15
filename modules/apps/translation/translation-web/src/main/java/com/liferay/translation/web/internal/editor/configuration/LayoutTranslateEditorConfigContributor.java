/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.editor.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.translation.constants.TranslationPortletKeys;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge González
 */
@Component(
	property = {
		"editor.config.key=layoutTranslateEditor",
		"jakarta.portlet.name=" + TranslationPortletKeys.TRANSLATION
	},
	service = EditorConfigContributor.class
)
public class LayoutTranslateEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"allowedContent",
			StringBundler.concat(
				_ALLOWED_CONTENT_LIST, _ALLOWED_CONTENT_MISC,
				_ALLOWED_CONTENT_TABLE, _ALLOWED_CONTENT_TEXT)
		).put(
			"enterMode", 2
		).put(
			"extraPlugins",
			"addimages,autolink,filebrowser,itemselector,lfrpopup"
		).put(
			"height", "265"
		).put(
			"removePlugins",
			"autogrow,elementspath,floatingspace,magicline,resize,ae_embed"
		).put(
			"resize_enabled", false
		).put(
			"toolbar",
			JSONUtil.putAll(
				JSONUtil.putAll("Undo", "Redo"),
				JSONUtil.putAll("Bold", "Italic", "Underline"),
				JSONUtil.putAll("NumberedList", "BulletedList"),
				JSONUtil.putAll(
					"JustifyLeft", "JustifyCenter", "JustifyRight",
					"JustifyBlock"),
				JSONUtil.putAll("Link", "Unlink"),
				JSONUtil.putAll("Table", "ImageSelector", "HorizontalRule"),
				JSONUtil.putAll("RemoveFormat"),
				JSONUtil.putAll("Source", "Expand"))
		);
	}

	private static final String _ALLOWED_CONTENT_LIST = "li ol ul [*](*){*};";

	private static final String _ALLOWED_CONTENT_MISC =
		"a[*](*); div[*](*){text-align}; img[*](*){*}; p[*](*); span[*](*){*};";

	private static final String _ALLOWED_CONTENT_TABLE =
		"table[border, cellpadding, cellspacing] {width}; tbody td " +
			"th[scope]; thead tr[scope];";

	private static final String _ALLOWED_CONTENT_TEXT =
		"b code em h1 h2 h3 h4 h5 h6 hr i p pre strong u [*](*){*};";

}
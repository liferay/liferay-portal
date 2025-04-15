/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.editor.configuration.internal;

import com.liferay.portal.kernel.editor.configuration.EditorOptions;
import com.liferay.portal.kernel.editor.configuration.EditorOptionsContributor;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.wiki.constants.WikiPortletKeys;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"editor.config.key=contentEditor",
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY
	},
	service = EditorOptionsContributor.class
)
public class WikiAttachmentEditorOptionsContributor
	implements EditorOptionsContributor {

	@Override
	public void populateEditorOptions(
		EditorOptions editorOptions,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (Validator.isNull(portletDisplay.getId())) {
			return;
		}

		Map<String, String> fileBrowserParamsMap =
			(Map<String, String>)inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:fileBrowserParams");

		long wikiPageResourcePrimKey = 0;

		if (fileBrowserParamsMap != null) {
			wikiPageResourcePrimKey = GetterUtil.getLong(
				fileBrowserParamsMap.get("wikiPageResourcePrimKey"));
		}

		if (wikiPageResourcePrimKey == 0) {
			return;
		}

		editorOptions.setUploadURL(
			PortletURLBuilder.create(
				requestBackedPortletURLFactory.createActionURL(
					WikiPortletKeys.WIKI)
			).setActionName(
				"/wiki/upload_page_attachment"
			).setParameter(
				"mimeTypes", PropsValues.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES
			).setParameter(
				"resourcePrimKey", wikiPageResourcePrimKey
			).buildString());
	}

}
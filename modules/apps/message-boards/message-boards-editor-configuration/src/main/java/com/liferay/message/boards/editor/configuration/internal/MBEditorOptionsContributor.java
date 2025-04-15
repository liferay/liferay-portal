/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.editor.configuration.internal;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.portal.kernel.editor.configuration.EditorOptions;
import com.liferay.portal.kernel.editor.configuration.EditorOptionsContributor;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"editor.name=ckeditor", "editor.name=ckeditor_bbcode",
		"editor.name=ckeditor_classic",
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN
	},
	service = EditorOptionsContributor.class
)
public class MBEditorOptionsContributor implements EditorOptionsContributor {

	@Override
	public void populateEditorOptions(
		EditorOptions editorOptions,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		editorOptions.setUploadURL(
			PortletURLBuilder.create(
				requestBackedPortletURLFactory.createActionURL(
					PortletKeys.MESSAGE_BOARDS)
			).setActionName(
				"/message_boards/upload_temp_image"
			).setParameter(
				"categoryId",
				() -> {
					Map<String, String> fileBrowserParamsMap =
						(Map<String, String>)inputEditorTaglibAttributes.get(
							"liferay-ui:input-editor:fileBrowserParams");

					long categoryId = 0;

					if (fileBrowserParamsMap != null) {
						categoryId = GetterUtil.getLong(
							fileBrowserParamsMap.get("categoryId"));
					}

					return categoryId;
				}
			).buildString());
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.editor.config;

import com.liferay.content.marketing.platform.web.internal.util.ObjectEntryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Larissa Ribeiro
 */
@Component(
	property = {
		"editor.config.key=richTextFragmentCKEditor5",
		"service.ranking:Integer=100"
	},
	service = EditorConfigContributor.class
)
public class CMPRichTextFragmentCKEditor5EditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		if (httpServletRequest == null) {
			return;
		}

		ObjectEntry objectEntry = ObjectEntryUtil.getObjectEntry(
			httpServletRequest);

		if (objectEntry == null) {
			return;
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (objectDefinition == null) {
			return;
		}

		String externalReferenceCode =
			objectDefinition.getExternalReferenceCode();

		if (!Objects.equals(externalReferenceCode, "L_CMP_PROJECT") &&
			!Objects.equals(externalReferenceCode, "L_CMP_TASK")) {

			return;
		}

		jsonObject.put("itemSelectorAskForGroupId", true);
		jsonObject.remove("groupId");
	}

}
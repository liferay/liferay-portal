/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.editor.configuration.internal;

import com.liferay.document.library.kernel.processor.AudioProcessorUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.audio.criterion.AudioItemSelectorCriterion;
import com.liferay.item.selector.criteria.url.criterion.URLItemSelectorCriterion;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Lancha
 */
@Component(service = EditorConfigContributor.class)
public class AudioEditorConfigContributor extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		if (!AudioProcessorUtil.isEnabled()) {
			return;
		}

		List<ItemSelectorCriterion> itemSelectorCriteria = new ArrayList<>();

		boolean allowBrowseDocuments = GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:allowBrowseDocuments"));

		if (allowBrowseDocuments) {
			itemSelectorCriteria.add(new AudioItemSelectorCriterion());
		}

		itemSelectorCriteria.add(new URLItemSelectorCriterion());

		PortletURL itemSelectorURL = getItemSelectorPortletURL(
			inputEditorTaglibAttributes, requestBackedPortletURLFactory,
			itemSelectorCriteria.toArray(new ItemSelectorCriterion[0]));

		if (itemSelectorURL != null) {
			jsonObject.put(
				"filebrowserAudioBrowseLinkUrl", itemSelectorURL.toString()
			).put(
				"filebrowserAudioBrowseUrl", itemSelectorURL.toString()
			);
		}
	}

	@Override
	protected ItemSelector getItemSelector() {
		return _itemSelector;
	}

	@Reference
	private ItemSelector _itemSelector;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.editor.configuration.internal;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.item.selector.JournalItemSelectorCriterion;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"editor.name=alloyeditor", "editor.name=ckeditor",
		"editor.name=ckeditor_classic",
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"service.ranking:Integer=101"
	},
	service = EditorConfigContributor.class
)
public class JournalAMEditorConfigContributor
	extends BaseAMEditorConfigContributor {

	@Override
	protected boolean isItemSelectorCriterionOverridable(
		ItemSelectorCriterion itemSelectorCriterion) {

		if (itemSelectorCriterion instanceof FileItemSelectorCriterion ||
			itemSelectorCriterion instanceof ImageItemSelectorCriterion ||
			itemSelectorCriterion instanceof JournalItemSelectorCriterion ||
			itemSelectorCriterion instanceof UploadItemSelectorCriterion) {

			return true;
		}

		return false;
	}

}
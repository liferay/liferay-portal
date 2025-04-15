/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.service.TranslationEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TranslationPortletKeys.TRANSLATION,
		"mvc.command.name=/translation/delete_translation_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteTranslationEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long translationEntryId = ParamUtil.getLong(
			actionRequest, "translationEntryId");

		if (translationEntryId > 0) {
			_translationEntryService.deleteTranslationEntry(translationEntryId);
		}
		else {
			long[] deleteTranslationEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");

			for (long deleteTranslationEntryId : deleteTranslationEntryIds) {
				_translationEntryService.deleteTranslationEntry(
					deleteTranslationEntryId);
			}
		}
	}

	@Reference
	private TranslationEntryService _translationEntryService;

}
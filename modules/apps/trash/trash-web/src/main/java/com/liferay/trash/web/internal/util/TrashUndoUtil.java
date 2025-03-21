/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.util;

import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class TrashUndoUtil {

	public static void addRestoreData(
			ActionRequest actionRequest,
			List<ObjectValuePair<String, Long>> entries)
		throws Exception {

		if (ListUtil.isEmpty(entries)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<String> restoreClassNames = new ArrayList<>();
		List<String> restoreEntryLinks = new ArrayList<>();
		List<String> restoreEntryMessages = new ArrayList<>();
		List<String> restoreLinks = new ArrayList<>();
		List<String> restoreMessages = new ArrayList<>();

		for (ObjectValuePair<String, Long> entry : entries) {
			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(entry.getKey());

			String restoreLink = trashHandler.getRestoreContainerModelLink(
				actionRequest, entry.getValue());
			String restoreMessage = trashHandler.getRestoreMessage(
				actionRequest, entry.getValue());

			if (Validator.isNull(restoreLink) ||
				Validator.isNull(restoreMessage)) {

				continue;
			}

			restoreClassNames.add(trashHandler.getClassName());

			String restoreEntryLink = trashHandler.getRestoreContainedModelLink(
				actionRequest, entry.getValue());

			restoreEntryLinks.add(restoreEntryLink);

			TrashRenderer trashRenderer = trashHandler.getTrashRenderer(
				entry.getValue());

			String restoreEntryTitle = trashRenderer.getTitle(
				themeDisplay.getLocale());

			restoreEntryMessages.add(restoreEntryTitle);

			restoreLinks.add(restoreLink);
			restoreMessages.add(restoreMessage);
		}

		SessionMessages.add(
			actionRequest,
			PortalUtil.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_DELETE_SUCCESS_DATA,
			HashMapBuilder.<String, List<String>>put(
				"restoreClassNames", restoreClassNames
			).put(
				"restoreEntryLinks", restoreEntryLinks
			).put(
				"restoreEntryMessages", restoreEntryMessages
			).put(
				"restoreLinks", restoreLinks
			).put(
				"restoreMessages", restoreMessages
			).build());
	}

	public static void addRestoreData(
			ActionRequest actionRequest, String className, long classPK)
		throws Exception {

		List<ObjectValuePair<String, Long>> entries = new ArrayList<>();

		ObjectValuePair<String, Long> objectValuePair = new ObjectValuePair<>(
			className, classPK);

		entries.add(objectValuePair);

		addRestoreData(actionRequest, entries);
	}

}
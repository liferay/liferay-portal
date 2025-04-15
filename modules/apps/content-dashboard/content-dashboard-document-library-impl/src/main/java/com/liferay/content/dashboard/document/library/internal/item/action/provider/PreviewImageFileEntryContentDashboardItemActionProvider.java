/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.action.provider;

import com.liferay.content.dashboard.document.library.internal.item.action.PreviewImageFileEntryContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemActionProvider.class)
public class PreviewImageFileEntryContentDashboardItemActionProvider
	implements ContentDashboardItemActionProvider<FileEntry> {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
		FileEntry fileEntry, HttpServletRequest httpServletRequest) {

		if (!isShow(fileEntry, httpServletRequest)) {
			return null;
		}

		InfoItemFieldValuesProvider<FileEntry> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, FileEntry.class.getName());

		return new PreviewImageFileEntryContentDashboardItemAction(
			fileEntry, infoItemFieldValuesProvider, _language);
	}

	@Override
	public String getKey() {
		return "preview-image";
	}

	@Override
	public ContentDashboardItemAction.Type getType() {
		return ContentDashboardItemAction.Type.PREVIEW_IMAGE;
	}

	@Override
	public boolean isShow(
		FileEntry fileEntry, HttpServletRequest httpServletRequest) {

		if (ListUtil.isEmpty(
				fileEntry.getFileVersions(WorkflowConstants.STATUS_APPROVED))) {

			return false;
		}

		InfoItemFieldValuesProvider<FileEntry> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, FileEntry.class.getName());

		ContentDashboardItemAction contentDashboardItemAction =
			new PreviewImageFileEntryContentDashboardItemAction(
				fileEntry, infoItemFieldValuesProvider, _language);

		return Validator.isNotNull(contentDashboardItemAction.getURL());
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.uad.display;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.user.associated.data.display.UADDisplay;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = UADDisplay.class)
public class DLFolderUADDisplay extends BaseDLFolderUADDisplay {

	@Override
	public String getEditURL(
			DLFolder dlFolder, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		if (dlFolder.isInTrash()) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.createLiferayPortletURL(
			liferayPortletResponse,
			portal.getControlPanelPlid(liferayPortletRequest),
			DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, PortletRequest.RENDER_PHASE
		).setMVCRenderCommandName(
			"/document_library/edit_folder"
		).setRedirect(
			portal.getCurrentURL(liferayPortletRequest)
		).setParameter(
			"folderId", dlFolder.getFolderId()
		).setParameter(
			"repositoryId", dlFolder.getRepositoryId()
		).buildString();
	}

	@Override
	public Map<String, Object> getFieldValues(
		DLFolder dlFolder, String[] fieldNames, Locale locale) {

		Map<String, Object> fieldValues = super.getFieldValues(
			dlFolder, fieldNames, locale);

		List<String> fieldNamesList = Arrays.asList(fieldNames);

		if (fieldNamesList.contains("type")) {
			fieldValues.put("type", "--");
		}

		return fieldValues;
	}

	@Override
	public String getName(DLFolder dlFolder, Locale locale) {
		return dlFolder.getName();
	}

	@Override
	public Class<?> getParentContainerClass() {
		return DLFolder.class;
	}

	@Override
	public Serializable getParentContainerId(DLFolder dlFolder) {
		return dlFolder.getParentFolderId();
	}

	@Override
	public DLFolder getTopLevelContainer(
		Class<?> parentContainerClass, Serializable parentContainerId,
		Object childObject) {

		try {
			DLFolder childFolder = null;

			if (childObject instanceof DLFileEntry) {
				DLFileEntry dlFileEntry = (DLFileEntry)childObject;

				childFolder = dlFileEntry.getFolder();
			}
			else {
				childFolder = (DLFolder)childObject;
			}

			long parentFolderId = (long)parentContainerId;

			if ((childFolder.getFolderId() == parentFolderId) ||
				((parentFolderId !=
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) &&
				 !StringUtil.contains(
					 childFolder.getTreePath(), String.valueOf(parentFolderId),
					 "/"))) {

				return null;
			}

			if (childFolder.getParentFolderId() == parentFolderId) {
				return childFolder;
			}

			List<Long> ancestorFolderIds = childFolder.getAncestorFolderIds();

			if (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				return get(ancestorFolderIds.get(ancestorFolderIds.size() - 1));
			}

			if (ancestorFolderIds.contains(parentFolderId)) {
				return get(
					ancestorFolderIds.get(
						ancestorFolderIds.indexOf(parentFolderId) - 1));
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	@Override
	public boolean isUserOwned(DLFolder dlFolder, long userId) {
		if (dlFolder.getUserId() == userId) {
			return true;
		}

		return false;
	}

	@Override
	protected DynamicQuery doGetDynamicQuery() {
		DynamicQuery dlFolderDynamicQuery = super.doGetDynamicQuery();

		DynamicQuery repositoryDynamicQuery =
			_repositoryLocalService.dynamicQuery();

		repositoryDynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"name",
				new String[] {
					MBMessageConstants.TEMP_FOLDER_NAME,
					TempFileEntryUtil.class.getName()
				}));

		List<Repository> repositories = _repositoryLocalService.dynamicQuery(
			repositoryDynamicQuery);

		if (ListUtil.isNotEmpty(repositories)) {
			dlFolderDynamicQuery.add(
				RestrictionsFactoryUtil.not(
					RestrictionsFactoryUtil.in(
						"repositoryId",
						ListUtil.toList(
							repositories, Repository.REPOSITORY_ID_ACCESSOR))));
		}

		return dlFolderDynamicQuery;
	}

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderUADDisplay.class);

	@Reference
	private RepositoryLocalService _repositoryLocalService;

}
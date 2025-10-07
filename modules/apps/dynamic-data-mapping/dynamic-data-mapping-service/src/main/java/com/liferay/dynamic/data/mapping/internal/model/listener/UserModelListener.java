/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.model.listener;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMFormConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ModelListener.class)
public class UserModelListener extends BaseModelListener<User> {

	@Override
	public void onAfterRemove(User user) {
		for (Repository repository :
				_repositoryLocalService.getRepositories(
					DDMFormConstants.SERVICE_NAME)) {

			try {
				Folder parentFolder = _portletFileRepository.getPortletFolder(
					repository.getRepositoryId(),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					DDMFormConstants.DDM_FORM_UPLOADED_FILES_FOLDER_NAME);

				Folder folder = _dlAppLocalService.getFolder(
					repository.getRepositoryId(), parentFolder.getFolderId(),
					user.getScreenName());

				_dlAppLocalService.updateFolder(
					folder.getFolderId(), folder.getParentFolderId(),
					folder.getName() + StringPool.SPACE +
						String.valueOf(user.getUserId()),
					folder.getDescription(),
					ServiceContextThreadLocal.getServiceContext());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserModelListener.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

}
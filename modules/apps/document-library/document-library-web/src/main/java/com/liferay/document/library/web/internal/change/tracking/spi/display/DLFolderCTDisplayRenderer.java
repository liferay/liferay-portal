/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.temporaryrepository.TemporaryFileEntryRepository;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(service = CTDisplayRenderer.class)
public class DLFolderCTDisplayRenderer extends BaseCTDisplayRenderer<DLFolder> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, DLFolder dlFolder)
		throws Exception {

		Group group = _groupLocalService.getGroup(dlFolder.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_folder"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"folderId", dlFolder.getFolderId()
		).buildString();
	}

	@Override
	public Class<DLFolder> getModelClass() {
		return DLFolder.class;
	}

	@Override
	public String getTitle(Locale locale, DLFolder dlFolder) {
		if (dlFolder.isInTrash()) {
			return _trashHelper.getOriginalTitle(dlFolder.getName());
		}

		return dlFolder.getName();
	}

	@Override
	public boolean isHideable(DLFolder dlFolder) {
		Repository repository = _repositoryLocalService.fetchRepository(
			dlFolder.getRepositoryId());

		if (repository == null) {
			return false;
		}

		if (repository.getClassNameId() == _portal.getClassNameId(
				TemporaryFileEntryRepository.class)) {

			return true;
		}

		return false;
	}

	@Override
	protected void buildDisplay(DisplayBuilder<DLFolder> displayBuilder) {
		DLFolder dlFolder = displayBuilder.getModel();

		displayBuilder.display(
			"name", dlFolder.getName()
		).display(
			"description", dlFolder.getDescription()
		).display(
			"created-by",
			() -> {
				String userName = dlFolder.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", dlFolder.getCreateDate()
		).display(
			"last-modified", dlFolder.getModifiedDate()
		).display(
			"folders",
			() -> {
				try {
					return _dlAppService.getFoldersCount(
						dlFolder.getRepositoryId(), dlFolder.getFolderId());
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}

					return 0;
				}
			}
		).display(
			"documents",
			() -> {
				DisplayContext<DLFolder> displayContext =
					displayBuilder.getDisplayContext();

				HttpServletRequest httpServletRequest =
					displayContext.getHttpServletRequest();

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				int status = WorkflowConstants.STATUS_APPROVED;

				PermissionChecker permissionChecker =
					themeDisplay.getPermissionChecker();

				if (permissionChecker.isContentReviewer(
						themeDisplay.getCompanyId(),
						themeDisplay.getScopeGroupId())) {

					status = WorkflowConstants.STATUS_ANY;
				}

				return _dlAppService.getFileEntriesAndFileShortcutsCount(
					dlFolder.getRepositoryId(), dlFolder.getFolderId(), status);
			}
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderCTDisplayRenderer.class);

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private TrashHelper _trashHelper;

}
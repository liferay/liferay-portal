/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal.folder;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.item.selector.web.internal.constants.DLItemSelectorViewConstants;
import com.liferay.document.library.item.selector.web.internal.display.context.DLSelectFolderDisplayContext;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.PortletItemSelectorView;
import com.liferay.item.selector.criteria.FolderItemSelectorReturnType;
import com.liferay.item.selector.criteria.folder.criterion.FolderItemSelectorCriterion;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.language.LanguageResources;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"item.selector.view.key=" + DLItemSelectorViewConstants.DL_FOLDER_ITEM_SELECTOR_VIEW_KEY,
		"item.selector.view.order:Integer=100"
	},
	service = ItemSelectorView.class
)
public class DLFolderItemSelectorView
	implements PortletItemSelectorView<FolderItemSelectorCriterion> {

	@Override
	public Class<FolderItemSelectorCriterion> getItemSelectorCriterionClass() {
		return FolderItemSelectorCriterion.class;
	}

	@Override
	public List<String> getPortletIds() {
		return _portletIds;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		ResourceBundleLoader resourceBundleLoader =
			LanguageResources.PORTAL_RESOURCE_BUNDLE_LOADER;

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			locale);

		return ResourceBundleUtil.getString(
			resourceBundle, "documents-and-media");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FolderItemSelectorCriterion itemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/select_folder.jsp");

		ThemeDisplay themeDisplay = (ThemeDisplay)servletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long folderId = ParamUtil.getLong(
			(HttpServletRequest)servletRequest, "folderId",
			itemSelectorCriterion.getFolderId());
		long repositoryId = ParamUtil.getLong(
			(HttpServletRequest)servletRequest, "repositoryId",
			itemSelectorCriterion.getRepositoryId());

		if (themeDisplay.getScopeGroupId() != _getRepositoryGroupId(
				itemSelectorCriterion.getRepositoryId())) {

			Folder folder = _fetchFolder(folderId);

			if ((folder == null) ||
				(folder.getRepositoryId() != themeDisplay.getScopeGroupId())) {

				folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}

			repositoryId = themeDisplay.getScopeGroupId();
		}

		Folder folder = _fetchFolder(folderId);

		if (folder != null) {
			repositoryId = folder.getRepositoryId();
		}

		Group group = _getGroup(repositoryId);

		if ((group != null) && group.isDepot()) {
			List<Long> groupConnectedDepotEntries =
				_getGroupConnectedDepotEntries(themeDisplay);

			if (!groupConnectedDepotEntries.contains(group.getGroupId())) {
				repositoryId = themeDisplay.getRefererGroupId();

				if (repositoryId == 0) {
					repositoryId = themeDisplay.getScopeGroupId();
				}
				else {
					folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
				}
			}
		}

		servletRequest.setAttribute(
			DLSelectFolderDisplayContext.class.getName(),
			new DLSelectFolderDisplayContext(
				itemSelectorCriterion.getBlockedFolderId(), _dlAppService,
				_fetchFolder(folderId), _folderModelResourcePermission,
				(HttpServletRequest)servletRequest, portletURL, repositoryId,
				itemSelectorCriterion.getSelectedFolderId(),
				itemSelectorCriterion.getSelectedRepositoryId(),
				itemSelectorCriterion.isShowGroupSelector()));

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private Folder _fetchFolder(long folderId) {
		try {
			if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				return null;
			}

			return _dlAppService.getFolder(folderId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private Group _getGroup(long groupId) {
		try {
			return _groupService.getGroup(groupId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private List<Long> _getGroupConnectedDepotEntries(
		ThemeDisplay themeDisplay) {

		try {
			return ListUtil.toList(
				_depotEntryService.getCurrentAndGroupConnectedDepotEntries(
					themeDisplay.getRefererGroupId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				DepotEntry::getGroupId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return Collections.emptyList();
		}
	}

	private long _getRepositoryGroupId(long repositoryId) {
		Repository repository = _repositoryLocalService.fetchRepository(
			repositoryId);

		if (repository == null) {
			return repositoryId;
		}

		return repository.getGroupId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderItemSelectorView.class);

	private static final List<String> _portletIds = Arrays.asList(
		DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, DLPortletKeys.DOCUMENT_LIBRARY);
	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new FolderItemSelectorReturnType());

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DLAppService _dlAppService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private GroupService _groupService;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.item.selector.web)"
	)
	private ServletContext _servletContext;

}
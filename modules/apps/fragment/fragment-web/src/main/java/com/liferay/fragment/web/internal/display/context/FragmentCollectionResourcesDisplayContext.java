/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class FragmentCollectionResourcesDisplayContext {

	public FragmentCollectionResourcesDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse,
		FragmentDisplayContext fragmentDisplayContext) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_fragmentDisplayContext = fragmentDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getFolderId() throws PortalException {
		if (_folderId != null) {
			return _folderId;
		}

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
				_fragmentDisplayContext.getFragmentCollectionId());

		_folderId = fragmentCollection.getResourcesFolderId();

		return _folderId;
	}

	public long getRepositoryId() throws PortalException {
		if (_repositoryId != null) {
			return _repositoryId;
		}

		FragmentCollection fragmentCollection =
			_fragmentDisplayContext.getFragmentCollection();

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				fragmentCollection.getGroupId(), FragmentPortletKeys.FRAGMENT);

		if (repository == null) {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			repository = PortletFileRepositoryUtil.addPortletRepository(
				fragmentCollection.getGroupId(), FragmentPortletKeys.FRAGMENT,
				serviceContext);
		}

		_repositoryId = repository.getRepositoryId();

		return _repositoryId;
	}

	public SearchContainer<FileEntry> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setRedirect(
			_fragmentDisplayContext.getRedirect()
		).setTabs1(
			"resources"
		).setParameter(
			"fragmentCollectionId",
			_fragmentDisplayContext.getFragmentCollectionId()
		).buildPortletURL();

		SearchContainer<FileEntry> searchContainer = new SearchContainer(
			_renderRequest, portletURL, null, "there-are-no-resources");

		searchContainer.setResultsAndTotal(
			() -> PortletFileRepositoryUtil.getPortletFileEntries(
				_themeDisplay.getScopeGroupId(), getFolderId(),
				WorkflowConstants.STATUS_ANY, searchContainer.getStart(),
				searchContainer.getEnd(),
				searchContainer.getOrderByComparator()),
			PortletFileRepositoryUtil.getPortletFileEntriesCount(
				_themeDisplay.getScopeGroupId(), getFolderId()));
		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	private Long _folderId;
	private final FragmentDisplayContext _fragmentDisplayContext;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Long _repositoryId;
	private SearchContainer<FileEntry> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}
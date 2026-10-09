/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.model.impl;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelTitleComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.exception.PageAttachmentException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.service.WikiPageServiceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class WikiPageImpl extends WikiPageBaseImpl {

	@Override
	public Folder addAttachmentsFolder() throws PortalException {
		if (_attachmentsFolderId !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			return PortletFileRepositoryUtil.getPortletFolder(
				_attachmentsFolderId);
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			getGroupId(), WikiConstants.SERVICE_NAME, serviceContext);

		WikiNode node = getNode();

		Folder nodeFolder = node.addAttachmentsFolder();

		Folder folder = PortletFileRepositoryUtil.addPortletFolder(
			getUserId(), repository.getRepositoryId(), nodeFolder.getFolderId(),
			String.valueOf(getResourcePrimKey()), serviceContext);

		_attachmentsFolderId = folder.getFolderId();

		return folder;
	}

	@Override
	public WikiPage fetchParentPage() {
		if (Validator.isNull(getParentTitle())) {
			return null;
		}

		return WikiPageLocalServiceUtil.fetchPage(
			getNodeId(), getParentTitle());
	}

	@Override
	public WikiPage fetchRedirectPage() {
		if (Validator.isNull(getRedirectTitle())) {
			return null;
		}

		return WikiPageLocalServiceUtil.fetchPage(
			getNodeId(), getRedirectTitle());
	}

	@Override
	public List<FileEntry> getAttachmentsFileEntries() throws PortalException {
		return getAttachmentsFileEntries(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<FileEntry> getAttachmentsFileEntries(int start, int end)
		throws PortalException {

		return getAttachmentsFileEntries(
			start, end, new RepositoryModelTitleComparator(true));
	}

	@Override
	public List<FileEntry> getAttachmentsFileEntries(
			int start, int end, OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getAttachmentsFileEntries(null, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getAttachmentsFileEntries(
			String[] mimeTypes, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		long attachmentsFolderId = getAttachmentsFolderId();

		if (attachmentsFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return Collections.emptyList();
		}

		WikiPage latestWikiPage = WikiPageLocalServiceUtil.getLatestPage(
			getResourcePrimKey(), WorkflowConstants.STATUS_ANY, false);

		if (latestWikiPage.getPageId() == getPageId()) {
			return PortletFileRepositoryUtil.getPortletFileEntries(
				getGroupId(), attachmentsFolderId, mimeTypes,
				WorkflowConstants.STATUS_APPROVED, start, end,
				orderByComparator);
		}

		List<FileEntry> fileEntries = new ArrayList<>();

		for (FileEntry fileEntry :
				PortletFileRepositoryUtil.getPortletFileEntries(
					getGroupId(), attachmentsFolderId, mimeTypes,
					WorkflowConstants.STATUS_APPROVED, start, end,
					orderByComparator)) {

			int value = DateUtil.compareTo(
				fileEntry.getModifiedDate(), getStatusDate());

			if (value > 0) {
				continue;
			}

			fileEntries.add(fileEntry);
		}

		return fileEntries;
	}

	@Override
	public int getAttachmentsFileEntriesCount() throws PortalException {
		long attachmentsFolderId = getAttachmentsFolderId();

		if (attachmentsFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return 0;
		}

		return PortletFileRepositoryUtil.getPortletFileEntriesCount(
			getGroupId(), attachmentsFolderId,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getAttachmentsFileEntriesCount(String[] mimeTypes)
		throws PortalException {

		long attachmentsFolderId = getAttachmentsFolderId();

		if (attachmentsFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return 0;
		}

		return PortletFileRepositoryUtil.getPortletFileEntriesCount(
			getGroupId(), attachmentsFolderId, mimeTypes,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public FileEntry getAttachmentsFileEntryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		FileEntry portletFileEntryByExternalReferenceCode =
			PortletFileRepositoryUtil.
				getPortletFileEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		long attachmentsFolderId = getAttachmentsFolderId();

		if (attachmentsFolderId ==
				portletFileEntryByExternalReferenceCode.getFolderId()) {

			return portletFileEntryByExternalReferenceCode;
		}

		throw new PageAttachmentException();
	}

	@Override
	public long getAttachmentsFolderId() {
		if (_attachmentsFolderId !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			return _attachmentsFolderId;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				getGroupId(), WikiConstants.SERVICE_NAME);

		long nodeAttachmentsFolderId = getNodeAttachmentsFolderId();

		if ((repository == null) ||
			(nodeAttachmentsFolderId ==
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		try {
			Folder folder = PortletFileRepositoryUtil.getPortletFolder(
				repository.getRepositoryId(), nodeAttachmentsFolderId,
				String.valueOf(getResourcePrimKey()));

			_attachmentsFolderId = folder.getFolderId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return _attachmentsFolderId;
	}

	@Override
	public List<WikiPage> getChildPages() {
		try {
			return WikiPageLocalServiceUtil.getChildren(
				getNodeId(), true, getTitle());
		}
		catch (Exception exception) {
			_log.error(exception);

			return Collections.emptyList();
		}
	}

	@Override
	public List<FileEntry> getDeletedAttachmentsFileEntries()
		throws PortalException {

		return getDeletedAttachmentsFileEntries(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<FileEntry> getDeletedAttachmentsFileEntries(int start, int end)
		throws PortalException {

		long attachmentsFolderId = getAttachmentsFolderId();

		if (attachmentsFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return Collections.emptyList();
		}

		return PortletFileRepositoryUtil.getPortletFileEntries(
			getGroupId(), attachmentsFolderId,
			WorkflowConstants.STATUS_IN_TRASH, start, end,
			new RepositoryModelTitleComparator<>(true));
	}

	@Override
	public int getDeletedAttachmentsFileEntriesCount() throws PortalException {
		long attachmentsFolderId = getAttachmentsFolderId();

		if (attachmentsFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return 0;
		}

		return PortletFileRepositoryUtil.getPortletFileEntriesCount(
			getGroupId(), attachmentsFolderId,
			WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public WikiNode getNode() {
		try {
			return WikiNodeLocalServiceUtil.getNode(getNodeId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return new WikiNodeImpl();
		}
	}

	@Override
	public long getNodeAttachmentsFolderId() {
		WikiNode node = WikiNodeLocalServiceUtil.fetchWikiNode(getNodeId());

		if (node == null) {
			return 0;
		}

		return node.getAttachmentsFolderId();
	}

	@Override
	public WikiPage getParentPage() throws PortalException {
		if (Validator.isNull(getParentTitle())) {
			return null;
		}

		return WikiPageLocalServiceUtil.getPage(getNodeId(), getParentTitle());
	}

	@Override
	public List<WikiPage> getParentPages() {
		WikiPage parentPage = fetchParentPage();

		if (parentPage == null) {
			return Collections.emptyList();
		}

		List<WikiPage> parentPages = new ArrayList<>();

		parentPages.addAll(parentPage.getParentPages());
		parentPages.add(parentPage);

		return parentPages;
	}

	@Override
	public WikiPage getRedirectPage() throws PortalException {
		if (Validator.isNull(getRedirectTitle())) {
			return null;
		}

		return WikiPageLocalServiceUtil.getPage(
			getNodeId(), getRedirectTitle());
	}

	@Override
	public long getTrashEntryClassPK() {
		return getResourcePrimKey();
	}

	@Override
	public List<WikiPage> getViewableChildPages() {
		try {
			return WikiPageServiceUtil.getChildren(
				getGroupId(), getNodeId(), true, getTitle());
		}
		catch (Exception exception) {
			_log.error(exception);

			return Collections.emptyList();
		}
	}

	@Override
	public WikiPage getViewableParentPage() {
		if (Validator.isNull(getParentTitle())) {
			return null;
		}

		try {
			return WikiPageServiceUtil.getPage(
				getGroupId(), getNodeId(), getParentTitle());
		}
		catch (Exception exception) {
			_log.error(exception);

			return null;
		}
	}

	@Override
	public List<WikiPage> getViewableParentPages() {
		WikiPage page = getViewableParentPage();

		if (page == null) {
			return Collections.emptyList();
		}

		List<WikiPage> pages = new ArrayList<>();

		pages.addAll(page.getViewableParentPages());
		pages.add(page);

		return pages;
	}

	@Override
	public boolean isResourceMain() {
		return isHead();
	}

	@Override
	public void setAttachmentsFolderId(long attachmentsFolderId) {
		_attachmentsFolderId = attachmentsFolderId;
	}

	private static final Log _log = LogFactoryUtil.getLog(WikiPageImpl.class);

	private long _attachmentsFolderId;

}
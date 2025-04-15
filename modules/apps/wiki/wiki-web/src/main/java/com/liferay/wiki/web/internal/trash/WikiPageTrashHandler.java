/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.trash;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;
import com.liferay.trash.constants.TrashEntryConstants;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.kernel.model.TrashEntry;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.WikiPageResourceLocalService;
import com.liferay.wiki.service.WikiPageService;
import com.liferay.wiki.web.internal.asset.model.WikiPageAssetRenderer;
import com.liferay.wiki.web.internal.util.WikiPageAttachmentsUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implements trash handling for the wiki page entity.
 *
 * @author Eudaldo Alonso
 * @author Roberto Díaz
 */
@Component(
	property = "model.class.name=com.liferay.wiki.model.WikiPage",
	service = TrashHandler.class
)
public class WikiPageTrashHandler extends BaseWikiTrashHandler {

	@Override
	public SystemEvent addDeletionSystemEvent(
			long userId, long groupId, long classPK, String classUuid,
			String referrerClassName)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		return super.addDeletionSystemEvent(
			userId, groupId, page.getPageId(), classUuid, referrerClassName);
	}

	@Override
	public void checkRestorableEntry(
			long classPK, long containerModelId, String newName)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		checkRestorableEntry(
			classPK, 0, containerModelId, page.getTitle(), newName);
	}

	@Override
	public void checkRestorableEntry(
			TrashEntry trashEntry, long containerModelId, String newName)
		throws PortalException {

		checkRestorableEntry(
			trashEntry.getClassPK(), trashEntry.getEntryId(), containerModelId,
			trashEntry.getTypeSettingsProperty("title"), newName);
	}

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		_wikiPageLocalService.deletePage(page);
	}

	@Override
	public String getClassName() {
		return WikiPage.class.getName();
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		return getParentContainerModel(page);
	}

	@Override
	public ContainerModel getParentContainerModel(TrashedModel trashedModel) {
		WikiPage page = (WikiPage)trashedModel;

		if (Validator.isNotNull(page.getParentTitle())) {
			try {
				WikiPage parentPage = page.getParentPage();

				while (_trashHelper.isInTrashImplicitly(parentPage)) {
					parentPage = parentPage.getParentPage();
				}

				if (_trashHelper.isInTrashExplicitly(parentPage)) {
					return parentPage;
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return page.getNode();
	}

	@Override
	public List<ContainerModel> getParentContainerModels(long classPK)
		throws PortalException {

		return ListUtil.fromArray(getParentContainerModel(classPK));
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK, false)
		).setParameter(
			"nodeName",
			() -> {
				WikiNode node = page.getNode();

				return node.getName();
			}
		).setParameter(
			"title", HtmlUtil.unescape(page.getTitle())
		).buildString();
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK, true)
		).setParameter(
			"nodeId",
			() -> {
				WikiNode node = page.getNode();

				return node.getNodeId();
			}
		).buildString();
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		WikiNode node = page.getNode();

		return node.getName();
	}

	@Override
	public int getTrashContainedModelsCount(long classPK)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getPage(classPK);

		return _wikiPageLocalService.getChildrenCount(
			page.getNodeId(), true, page.getTitle(),
			WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public String getTrashContainerModelName() {
		return "children-pages";
	}

	@Override
	public int getTrashContainerModelsCount(long classPK)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getPage(classPK);

		return _wikiPageLocalService.getChildrenCount(
			page.getNodeId(), true, page.getTitle(),
			WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _wikiPageLocalService.fetchLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);
	}

	@Override
	public List<TrashedModel> getTrashModelTrashedModels(
			long classPK, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getPage(classPK);

		List<WikiPage> pages = _wikiPageLocalService.getChildren(
			page.getNodeId(), true, page.getTitle(),
			WorkflowConstants.STATUS_IN_TRASH, start, end,
			(OrderByComparator<WikiPage>)orderByComparator);

		return TransformUtil.transform(pages, curPage -> curPage);
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		return new WikiPageAssetRenderer(
			_htmlParser, _trashHelper, _wikiEngineRenderer, page);
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			WikiPage page = _wikiPageLocalService.fetchLatestPage(
				classPK, WorkflowConstants.STATUS_ANY, true);

			if (page != null) {
				_wikiPageModelResourcePermission.check(
					permissionChecker, page, ActionKeys.DELETE);

				classPK = page.getNodeId();
			}

			return _wikiNodeModelResourcePermission.contains(
				permissionChecker, classPK, ActionKeys.ADD_PAGE);
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isContainerModel() {
		return true;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(), page.getGroupId(),
				classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(page);
	}

	@Override
	public void restoreRelatedTrashEntry(String className, long classPK)
		throws PortalException {

		if (!className.equals(DLFileEntry.class.getName())) {
			return;
		}

		FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
			classPK);

		WikiPage page = WikiPageAttachmentsUtil.getPage(classPK);

		_wikiPageService.restorePageAttachmentFromTrash(
			page.getNodeId(), page.getTitle(), fileEntry.getTitle());
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		_wikiPageLocalService.restorePageFromTrash(userId, page);
	}

	@Override
	public void updateTitle(long classPK, String name) throws PortalException {
		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		page.setTitle(name);

		page = _wikiPageLocalService.updateWikiPage(page);

		WikiPageResource pageResource =
			_wikiPageResourceLocalService.getPageResource(
				page.getResourcePrimKey());

		pageResource.setTitle(name);

		_wikiPageResourceLocalService.updateWikiPageResource(pageResource);
	}

	protected void checkRestorableEntry(
			long classPK, long trashEntryId, long containerModelId,
			String originalTitle, String newName)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		if (containerModelId == TrashEntryConstants.DEFAULT_CONTAINER_ID) {
			containerModelId = page.getNodeId();
		}

		if (Validator.isNotNull(newName)) {
			originalTitle = newName;
		}

		WikiPageResource duplicatePageResource =
			_wikiPageResourceLocalService.fetchPageResource(
				containerModelId, originalTitle);

		if (duplicatePageResource != null) {
			RestoreEntryException restoreEntryException =
				new RestoreEntryException(RestoreEntryException.DUPLICATE);

			WikiPage duplicatePage = _wikiPageLocalService.getLatestPage(
				duplicatePageResource.getResourcePrimKey(),
				WorkflowConstants.STATUS_ANY, false);

			restoreEntryException.setDuplicateEntryId(
				duplicatePage.getResourcePrimKey());
			restoreEntryException.setOldName(duplicatePage.getTitle());

			restoreEntryException.setTrashEntryId(trashEntryId);

			throw restoreEntryException;
		}

		List<WikiPage> pages = _wikiPageLocalService.getDependentPages(
			page.getNodeId(), true, page.getTitle(),
			WorkflowConstants.STATUS_IN_TRASH);

		for (WikiPage curPage : pages) {
			checkRestorableEntry(
				curPage.getResourcePrimKey(), 0, containerModelId,
				curPage.getTitle(),
				_trashHelper.getOriginalTitle(curPage.getTitle()));
		}
	}

	protected PortletURL getRestoreURL(
			PortletRequest portletRequest, long classPK, boolean containerModel)
		throws PortalException {

		PortletURL portletURL = null;

		WikiPage page = _wikiPageLocalService.getLatestPage(
			classPK, WorkflowConstants.STATUS_ANY, false);

		long plid = _portal.getPlidFromPortletId(
			page.getGroupId(), WikiPortletKeys.WIKI);

		if (plid == LayoutConstants.DEFAULT_PLID) {
			portletURL = _portal.getControlPanelPortletURL(
				portletRequest, WikiPortletKeys.WIKI_ADMIN,
				PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = PortletURLFactoryUtil.create(
				portletRequest, WikiPortletKeys.WIKI, plid,
				PortletRequest.RENDER_PHASE);
		}

		if (containerModel) {
			portletURL.setParameter("mvcRenderCommandName", "/wiki/view_pages");
		}
		else {
			portletURL.setParameter("mvcRenderCommandName", "/wiki/view");
		}

		return portletURL;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _wikiPageModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	@Override
	protected boolean isInTrashExplicitly(WikiPage page) {
		return _trashHelper.isInTrashExplicitly(page);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageTrashHandler.class);

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiNode)")
	private ModelResourcePermission<WikiNode> _wikiNodeModelResourcePermission;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiPage)")
	private ModelResourcePermission<WikiPage> _wikiPageModelResourcePermission;

	@Reference
	private WikiPageResourceLocalService _wikiPageResourceLocalService;

	@Reference
	private WikiPageService _wikiPageService;

}
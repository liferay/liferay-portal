/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.trash;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.kernel.model.TrashEntry;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.web.internal.asset.WikiNodeTrashRenderer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implements trash handling for the wiki node entity.
 *
 * @author Eudaldo Alonso
 * @author Roberto Díaz
 */
@Component(
	property = "model.class.name=com.liferay.wiki.model.WikiNode",
	service = TrashHandler.class
)
public class WikiNodeTrashHandler extends BaseWikiTrashHandler {

	@Override
	public void checkRestorableEntry(
			TrashEntry trashEntry, long containerModelId, String newName)
		throws PortalException {

		WikiNode node = _wikiNodeLocalService.getNode(trashEntry.getClassPK());

		String originalTitle = trashEntry.getTypeSettingsProperty("title");

		if (Validator.isNotNull(newName)) {
			originalTitle = newName;
		}

		WikiNode duplicateNode = _wikiNodeLocalService.fetchNode(
			node.getGroupId(), originalTitle);

		if (duplicateNode != null) {
			RestoreEntryException restoreEntryException =
				new RestoreEntryException(RestoreEntryException.DUPLICATE);

			restoreEntryException.setDuplicateEntryId(
				duplicateNode.getNodeId());
			restoreEntryException.setOldName(duplicateNode.getName());
			restoreEntryException.setTrashEntryId(trashEntry.getEntryId());

			throw restoreEntryException;
		}
	}

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_wikiNodeLocalService.deleteNode(classPK);
	}

	@Override
	public String getClassName() {
		return WikiNode.class.getName();
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		WikiNode node = _wikiNodeLocalService.getNode(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK, false)
		).setParameter(
			"nodeId", node.getNodeId()
		).buildString();
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		PortletURL portletURL = getRestoreURL(portletRequest, classPK, true);

		return portletURL.toString();
	}

	@Override
	public String getRestoreMessage(
		PortletRequest portletRequest, long classPK) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.translate("wiki");
	}

	@Override
	public String getTrashContainedModelName() {
		return "wiki-pages";
	}

	@Override
	public int getTrashContainedModelsCount(long classPK) {
		return _wikiPageLocalService.getPagesCount(
			classPK, true, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _wikiNodeLocalService.fetchWikiNode(classPK);
	}

	@Override
	public int getTrashModelsCount(long classPK) {
		return _wikiPageLocalService.getPagesCount(
			classPK, true, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public List<TrashedModel> getTrashModelTrashedModels(
		long classPK, int start, int end,
		OrderByComparator<?> orderByComparator) {

		List<WikiPage> pages = _wikiPageLocalService.getPages(
			classPK, true, WorkflowConstants.STATUS_IN_TRASH, start, end,
			(OrderByComparator<WikiPage>)orderByComparator);

		List<TrashedModel> trashedModels = new ArrayList<>(pages.size());

		for (WikiPage page : pages) {
			trashedModels.add(page);
		}

		return trashedModels;
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		return new WikiNodeTrashRenderer(
			_wikiNodeLocalService.getNode(classPK), _trashHelper);
	}

	@Override
	public boolean isContainerModel() {
		return true;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		WikiNode node = _wikiNodeLocalService.getNode(classPK);

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(), node.getGroupId(),
				classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(node);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_wikiNodeLocalService.restoreNodeFromTrash(
			userId, _wikiNodeLocalService.getNode(classPK));
	}

	@Override
	public void updateTitle(long classPK, String name) throws PortalException {
		WikiNode node = _wikiNodeLocalService.getNode(classPK);

		node.setName(name);

		_wikiNodeLocalService.updateWikiNode(node);
	}

	protected PortletURL getRestoreURL(
			PortletRequest portletRequest, long classPK, boolean containerModel)
		throws PortalException {

		PortletURL portletURL = null;

		WikiNode node = _wikiNodeLocalService.getNode(classPK);

		long plid = _portal.getPlidFromPortletId(
			node.getGroupId(), WikiPortletKeys.WIKI);

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

		if (!containerModel) {
			portletURL.setParameter("mvcRenderCommandName", "/wiki/view_pages");
		}

		return portletURL;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _wikiNodeModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	@Override
	protected boolean isInTrashExplicitly(WikiPage page) {
		return _trashHelper.isInTrashExplicitly(page);
	}

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiNode)")
	private ModelResourcePermission<WikiNode> _wikiNodeModelResourcePermission;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}
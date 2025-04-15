/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.trash;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.BaseTrashHandler;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implements trash handling for the blogs entry entity.
 *
 * @author Zsolt Berentey
 */
@Component(
	property = "model.class.name=com.liferay.blogs.model.BlogsEntry",
	service = TrashHandler.class
)
public class BlogsEntryTrashHandler extends BaseTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_blogsEntryLocalService.deleteEntry(classPK);
	}

	@Override
	public String getClassName() {
		return BlogsEntry.class.getName();
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		BlogsEntry entry = _blogsEntryLocalService.getEntry(classPK);

		return PortletURLBuilder.create(
			_getRestoreURL(portletRequest, classPK, false)
		).setParameter(
			"entryId", entry.getEntryId()
		).setParameter(
			"urlTitle", entry.getUrlTitle()
		).buildString();
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		PortletURL portletURL = _getRestoreURL(portletRequest, classPK, true);

		return portletURL.toString();
	}

	@Override
	public String getRestoreMessage(
		PortletRequest portletRequest, long classPK) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.translate("blogs");
	}

	@Override
	public boolean isInTrash(long classPK) throws PortalException {
		BlogsEntry entry = _blogsEntryLocalService.getEntry(classPK);

		return entry.isInTrash();
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		BlogsEntry entry = _blogsEntryLocalService.getEntry(classPK);

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				entry.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(entry);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_blogsEntryLocalService.restoreEntryFromTrash(userId, classPK);
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _blogsEntryModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private PortletURL _getRestoreURL(
			PortletRequest portletRequest, long classPK, boolean containerModel)
		throws PortalException {

		PortletURL portletURL = null;

		BlogsEntry entry = _blogsEntryLocalService.getEntry(classPK);
		String portletId = PortletProviderUtil.getPortletId(
			BlogsEntry.class.getName(), PortletProvider.Action.VIEW);

		long plid = _portal.getPlidFromPortletId(entry.getGroupId(), portletId);

		if (plid == LayoutConstants.DEFAULT_PLID) {
			portletId = PortletProviderUtil.getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.MANAGE);

			portletURL = _portal.getControlPanelPortletURL(
				portletRequest, portletId, PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = PortletURLFactoryUtil.create(
				portletRequest, portletId, plid, PortletRequest.RENDER_PHASE);
		}

		if (!containerModel) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/blogs/view_entry");
		}

		return portletURL;
	}

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference(target = "(model.class.name=com.liferay.blogs.model.BlogsEntry)")
	private ModelResourcePermission<BlogsEntry>
		_blogsEntryModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}
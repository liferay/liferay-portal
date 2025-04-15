/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.configuration.icon;

import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"path=/admin/view_kb_folders.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class DeleteKBFolderPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "trash";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "delete");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/knowledge_base/delete_kb_folder"
		).setCMD(
			() -> {
				if (_trashHelper.isTrashEnabled(
						_portal.getScopeGroupId(portletRequest))) {

					return Constants.MOVE_TO_TRASH;
				}

				return Constants.DELETE;
			}
		).setRedirect(
			_portal.getControlPanelPortletURL(
				portletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setParameter(
			"kbFolderId",
			() -> {
				KBFolder kbFolder = (KBFolder)portletRequest.getAttribute(
					KBWebKeys.KNOWLEDGE_BASE_PARENT_KB_FOLDER);

				return kbFolder.getKbFolderId();
			}
		).buildString();
	}

	@Override
	public double getWeight() {
		return 102;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			KBFolder kbFolder = (KBFolder)portletRequest.getAttribute(
				KBWebKeys.KNOWLEDGE_BASE_PARENT_KB_FOLDER);

			return _kbFolderModelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), kbFolder,
				KBActionKeys.DELETE);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteKBFolderPortletConfigurationIcon.class);

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBFolder)"
	)
	private ModelResourcePermission<KBFolder> _kbFolderModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}
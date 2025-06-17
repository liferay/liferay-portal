/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"path=/admin/view_kb_folders.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class MoveKBFolderPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		KBFolder kbFolder = (KBFolder)portletRequest.getAttribute(
			KBWebKeys.KNOWLEDGE_BASE_PARENT_KB_FOLDER);

		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "moveFolder"
		).put(
			"globalAction", true
		).put(
			"kbObjectClassNameId", kbFolder.getClassNameId()
		).put(
			"kbObjectId", kbFolder.getKbFolderId()
		).put(
			"kbObjectTitle", kbFolder.getName()
		).put(
			"kbObjectType", KBFolder.class.getSimpleName()
		).put(
			"moveKBObjectActionURL",
			PortletURLBuilder.createActionURL(
				_getLiferayPortletResponse(portletRequest)
			).setActionName(
				"/knowledge_base/move_kb_object"
			).buildString()
		).put(
			"moveKBObjectModalURL",
			PortletURLBuilder.createRenderURL(
				_getLiferayPortletResponse(portletRequest)
			).setMVCPath(
				"/admin/common/move_kb_object_modal.jsp"
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"portletNamespace", getNamespace(portletRequest)
		).build();
	}

	@Override
	public String getIconCssClass() {
		return "move-folder";
	}

	@Override
	public String getJspPath() {
		return "/configuration/icon/move_kb_folder.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "move");
	}

	@Override
	public double getWeight() {
		return 103;
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
				KBActionKeys.MOVE_KB_FOLDER);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return false;
		}
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private LiferayPortletResponse _getLiferayPortletResponse(
		PortletRequest portletRequest) {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			portletRequest);

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		return _portal.getLiferayPortletResponse(portletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MoveKBFolderPortletConfigurationIcon.class);

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBFolder)"
	)
	private ModelResourcePermission<KBFolder> _kbFolderModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.knowledge.base.web)"
	)
	private ServletContext _servletContext;

}
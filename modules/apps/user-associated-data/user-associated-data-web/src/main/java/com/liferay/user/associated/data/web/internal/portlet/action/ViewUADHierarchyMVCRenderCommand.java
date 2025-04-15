/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.constants.UADConstants;
import com.liferay.user.associated.data.web.internal.constants.UADWebKeys;
import com.liferay.user.associated.data.web.internal.dao.search.UADHierarchyResultRowSplitter;
import com.liferay.user.associated.data.web.internal.display.UADHierarchyDisplay;
import com.liferay.user.associated.data.web.internal.display.UADInfoPanelDisplay;
import com.liferay.user.associated.data.web.internal.display.ViewUADEntitiesDisplay;
import com.liferay.user.associated.data.web.internal.helper.SelectedUserHelper;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;
import com.liferay.user.associated.data.web.internal.util.GroupUtil;
import com.liferay.user.associated.data.web.internal.util.UADSearchContainerBuilderUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/user_associated_data/view_uad_hierarchy"
	},
	service = MVCRenderCommand.class
)
public class ViewUADHierarchyMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			String applicationKey = ParamUtil.getString(
				renderRequest, "applicationKey");

			UADHierarchyDisplay uadHierarchyDisplay = _getUADHierarchyDisplay(
				applicationKey);

			renderRequest.setAttribute(
				UADWebKeys.UAD_HIERARCHY_DISPLAY, uadHierarchyDisplay);

			UADDisplay<Object> uadDisplay =
				(UADDisplay<Object>)_uadRegistry.getUADDisplay(
					ParamUtil.getString(
						renderRequest, "parentContainerTypeKey"));

			renderRequest.setAttribute(
				UADWebKeys.UAD_INFO_PANEL_DISPLAY,
				_getUADInfoPanelDisplay(uadDisplay));
			renderRequest.setAttribute(
				UADWebKeys.VIEW_UAD_ENTITIES_DISPLAY,
				_getViewUADEntitiesDisplay(
					applicationKey, renderRequest, renderResponse, uadDisplay,
					uadHierarchyDisplay));
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return "/view_uad_hierarchy.jsp";
	}

	private UADHierarchyDisplay _getUADHierarchyDisplay(String applicationKey) {
		return _uadRegistry.getUADHierarchyDisplay(applicationKey);
	}

	private UADInfoPanelDisplay _getUADInfoPanelDisplay(
		UADDisplay<Object> uadDisplay) {

		UADInfoPanelDisplay uadInfoPanelDisplay = new UADInfoPanelDisplay();

		uadInfoPanelDisplay.setHierarchyView(true);
		uadInfoPanelDisplay.setTopLevelView(false);
		uadInfoPanelDisplay.setUADDisplay(uadDisplay);

		return uadInfoPanelDisplay;
	}

	private ViewUADEntitiesDisplay _getViewUADEntitiesDisplay(
			String applicationKey, RenderRequest renderRequest,
			RenderResponse renderResponse, UADDisplay<?> uadDisplay,
			UADHierarchyDisplay uadHierarchyDisplay)
		throws Exception {

		ViewUADEntitiesDisplay viewUADEntitiesDisplay =
			new ViewUADEntitiesDisplay();

		viewUADEntitiesDisplay.setApplicationKey(applicationKey);

		String scope = ParamUtil.getString(
			renderRequest, "scope", UADConstants.SCOPE_PERSONAL_SITE);

		long[] groupIds = GroupUtil.getGroupIds(
			_groupLocalService,
			_selectedUserHelper.getSelectedUser(renderRequest), scope);

		viewUADEntitiesDisplay.setGroupIds(groupIds);

		viewUADEntitiesDisplay.setHierarchy(true);
		viewUADEntitiesDisplay.setResultRowSplitter(
			new UADHierarchyResultRowSplitter(
				LocaleThreadLocal.getThemeDisplayLocale(),
				uadHierarchyDisplay.getUADDisplays()));
		viewUADEntitiesDisplay.setScope(scope);
		viewUADEntitiesDisplay.setSearchContainer(
			UADSearchContainerBuilderUtil.getHierarchyUADEntitySearchContainer(
				_portal.getLiferayPortletResponse(renderResponse),
				renderRequest, applicationKey,
				PortletURLUtil.getCurrent(renderRequest, renderResponse),
				groupIds, uadDisplay.getTypeKey(),
				ParamUtil.getLong(renderRequest, "parentContainerId"),
				_selectedUserHelper.getSelectedUser(renderRequest),
				uadHierarchyDisplay));
		viewUADEntitiesDisplay.setTypeKeys(uadHierarchyDisplay.getTypeKeys());

		return viewUADEntitiesDisplay;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SelectedUserHelper _selectedUserHelper;

	@Reference
	private UADRegistry _uadRegistry;

}
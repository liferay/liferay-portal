/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.display.context.EditCollectionConfigurationDisplayContext;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/edit_collection_configuration"
	},
	service = MVCRenderCommand.class
)
public class EditCollectionConfigurationMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			EditCollectionConfigurationDisplayContext.class.getName(),
			new EditCollectionConfigurationDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_infoItemServiceRegistry, _itemSelector, renderResponse));

		LiferayRenderRequest liferayRenderRequest =
			(LiferayRenderRequest)renderRequest;

		DynamicServletRequest dynamicServletRequest =
			(DynamicServletRequest)liferayRenderRequest.getHttpServletRequest();

		dynamicServletRequest.setParameter("p_l_mode", Constants.EDIT);

		return "/edit_collection_configuration.jsp";
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}
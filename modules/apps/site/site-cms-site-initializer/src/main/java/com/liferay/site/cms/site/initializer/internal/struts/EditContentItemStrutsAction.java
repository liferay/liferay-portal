/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.manager.FormManager;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "path=/cms/edit_content_item", service = StrutsAction.class
)
public class EditContentItemStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));

		String editURL = ActionUtil.getEditURL(
			_formManager, _fragmentEntryLinkListenerRegistry,
			_fragmentEntryLinkService, _fragmentRendererRegistry,
			httpServletRequest, String.valueOf(objectEntry.getObjectEntryId()),
			_infoItemServiceRegistry, _infoSearchClassMapperRegistry,
			_objectDefinitionService.getObjectDefinition(
				objectEntry.getObjectDefinitionId()));

		if (Validator.isNotNull(redirect)) {
			editURL = HttpComponentsUtil.addParameter(
				editURL, "redirect", redirect);
		}

		String layoutMode = ParamUtil.getString(httpServletRequest, "p_l_mode");

		if (Validator.isNotNull(layoutMode) &&
			Objects.equals(layoutMode, Constants.READ)) {

			editURL = HttpComponentsUtil.addParameter(
				editURL, "p_l_mode", layoutMode);
		}

		String windowState = ParamUtil.getString(
			httpServletRequest, "p_p_state");

		if (Validator.isNotNull(windowState)) {
			editURL = HttpComponentsUtil.addParameter(
				editURL, "p_p_state", windowState);
		}

		int version = ParamUtil.getInteger(httpServletRequest, "version");

		if (version > 0) {
			editURL = HttpComponentsUtil.addParameter(
				editURL, "version", version);
		}

		httpServletResponse.sendRedirect(editURL);

		return null;
	}

	@Reference
	private FormManager _formManager;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryService _objectEntryService;

}
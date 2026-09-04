/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Veronica Gonzalez
 */
@Component(
	property = "path=/cms/compare_content_item", service = StrutsAction.class
)
public class CompareContentItemStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-56634")) {

			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));

		String compareURL = ActionUtil.getCompareURL(
			_formManager, _fragmentEntryLinkListenerRegistry,
			_fragmentEntryLinkService, _fragmentRendererRegistry,
			httpServletRequest, String.valueOf(objectEntry.getObjectEntryId()),
			_infoItemServiceRegistry, _infoSearchClassMapperRegistry,
			_objectDefinitionService.getObjectDefinition(
				objectEntry.getObjectDefinitionId()));

		String windowState = ParamUtil.getString(
			httpServletRequest, "p_p_state");

		if (Validator.isNotNull(windowState)) {
			compareURL = HttpComponentsUtil.addParameter(
				compareURL, "p_p_state", windowState);
		}

		int version = ParamUtil.getInteger(httpServletRequest, "version");

		if (version > 0) {
			compareURL = HttpComponentsUtil.addParameter(
				compareURL, "version", version);
		}

		httpServletResponse.sendRedirect(compareURL);

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
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "path=/cms/reset_translation_display_page",
	service = StrutsAction.class
)
public class ResetTranslationDisplayPageStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		_write(
			httpServletResponse, _jsonFactory.createJSONObject(),
			HttpServletResponse.SC_OK);

		ObjectDefinition objectDefinition =
			_objectDefinitionService.getObjectDefinition(
				ParamUtil.getLong(httpServletRequest, "objectDefinitionId"));

		if ((objectDefinition == null) || !objectDefinition.isApproved()) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = _groupLocalService.getGroup(
			themeDisplay.getCompanyId(), GroupConstants.CMS);

		long classNameId = _portal.getClassNameId(
			objectDefinition.getClassName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				group.getGroupId(),
				_TRANSLATION_LAYOUT_PAGE_TEMPLATE_ENTRY_KEY_PREFIX +
					classNameId);

		if (layoutPageTemplateEntry == null) {
			return null;
		}

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry);

		return null;
	}

	private void _write(
			HttpServletResponse httpServletResponse, JSONObject jsonObject,
			int status)
		throws Exception {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(status);

		ServletResponseUtil.write(
			httpServletResponse, JSONUtil.toString(jsonObject));
	}

	private static final String
		_TRANSLATION_LAYOUT_PAGE_TEMPLATE_ENTRY_KEY_PREFIX =
			"LFR_CMS_TRANSLATION_";

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private Portal _portal;

}
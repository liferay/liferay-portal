/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.util.DDMTemplateHelper;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.display.context.JournalEditDDMTemplateDisplayContext;
import com.liferay.journal.web.internal.helper.JournalDDMTemplateHelper;
import com.liferay.journal.web.internal.security.permission.resource.DDMTemplatePermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Shakir Shamim
 */
public abstract class BaseDDMTemplateMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			_checkDDMTemplatePermission(
				portal.getHttpServletRequest(renderRequest));

			renderRequest.setAttribute(
				JournalEditDDMTemplateDisplayContext.class.getName(),
				new JournalEditDDMTemplateDisplayContext(
					ddmTemplateHelper, journalDDMTemplateHelper, portal,
					renderRequest, renderResponse));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchTemplateException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return getPath();
	}

	protected abstract String getPath();

	@Reference
	protected DDMTemplateHelper ddmTemplateHelper;

	@Reference
	protected JournalDDMTemplateHelper journalDDMTemplateHelper;

	@Reference
	protected Portal portal;

	private void _checkDDMTemplatePermission(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long ddmTemplateId = ParamUtil.getLong(
			httpServletRequest, "ddmTemplateId");

		if (ddmTemplateId > 0) {
			if (!DDMTemplatePermission.contains(
					themeDisplay.getPermissionChecker(), ddmTemplateId,
					ActionKeys.UPDATE)) {

				throw new PrincipalException.MustHavePermission(
					themeDisplay.getPermissionChecker(),
					DDMTemplate.class.getName(), ddmTemplateId,
					ActionKeys.UPDATE);
			}

			return;
		}

		if (!DDMTemplatePermission.containsAddTemplatePermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				portal.getClassNameId(DDMStructure.class),
				portal.getClassNameId(JournalArticle.class))) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getPermissionChecker(),
				DDMActionKeys.ADD_TEMPLATE);
		}
	}

}
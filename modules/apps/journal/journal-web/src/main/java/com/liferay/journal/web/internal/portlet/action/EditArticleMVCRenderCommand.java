/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.web.internal.display.context.JournalEditArticleDisplayContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/edit_article"
	},
	service = MVCRenderCommand.class
)
public class EditArticleMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(renderRequest);

			renderRequest.setAttribute(
				JournalEditArticleDisplayContext.class.getName(),
				new JournalEditArticleDisplayContext(
					httpServletRequest,
					_portal.getLiferayPortletResponse(renderResponse),
					ActionUtil.getArticle(httpServletRequest),
					_depotEntryGroupRelLocalService, _depotEntryLocalService,
					_groupLocalService));

			return "/edit_article.jsp";
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else {
				_log.error(exception);
			}

			SessionErrors.add(renderRequest, exception.getClass());
		}

		return "/error.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditArticleMVCRenderCommand.class);

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Julio Camarero
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"mvc.command.name=/wiki/compare_versions"
	},
	service = MVCResourceCommand.class
)
public class CompareVersionsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		StringBundler sb = new StringBundler(3);

		double sourceVersion = ParamUtil.getDouble(
			resourceRequest, "filterSourceVersion");
		double targetVersion = ParamUtil.getDouble(
			resourceRequest, "filterTargetVersion");

		String htmlDiffResult = ActionUtil.getHtmlDiffResult(
			sourceVersion, targetVersion, resourceRequest, resourceResponse,
			_wikiEngineRenderer);

		if (Validator.isNotNull(htmlDiffResult)) {
			sb.append("<div class=\"taglib-diff-html\">");
			sb.append(htmlDiffResult);
			sb.append("</div>");
		}
		else {
			sb.append("<div class=\"alert alert-info\">");
			sb.append(
				_language.get(
					_portal.getHttpServletRequest(resourceRequest),
					"these-versions-are-not-comparable"));
			sb.append("</div>");
		}

		ServletResponseUtil.write(
			_portal.getHttpServletResponse(resourceResponse), sb.toString());
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.exception.DuplicatePageException;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.exception.PageTitleException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageService;
import com.liferay.wiki.validator.WikiPageTitleValidator;
import com.liferay.wiki.web.internal.util.WikiWebComponentProvider;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"mvc.command.name=/wiki/edit_page"
	},
	service = MVCRenderCommand.class
)
public class EditPageMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			renderRequest.setAttribute(
				WikiWebKeys.WIKI_ENGINE_RENDERER, _wikiEngineRenderer);

			renderRequest.setAttribute(
				WikiWebKeys.WIKI_PAGE_TITLE_VALIDATOR, _wikiPageTitleValidator);

			renderRequest.setAttribute(
				WikiWebKeys.WIKI_NODE, ActionUtil.getNode(renderRequest));

			if (!SessionErrors.contains(
					renderRequest, DuplicatePageException.class.getName())) {

				getPage(renderRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchNodeException ||
				exception instanceof PageTitleException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				if (exception instanceof PrincipalException) {
					return "/wiki/error.jsp";
				}
			}
			else if (exception instanceof NoSuchPageException) {

				// Let edit_page.jsp handle this case

			}
			else {
				throw new PortletException(exception);
			}
		}

		return "/wiki/edit_page.jsp";
	}

	protected void getPage(RenderRequest renderRequest) throws Exception {
		long nodeId = ParamUtil.getLong(renderRequest, "nodeId");
		String title = ParamUtil.getString(renderRequest, "title");
		double version = ParamUtil.getDouble(renderRequest, "version");
		boolean removeRedirect = ParamUtil.getBoolean(
			renderRequest, "removeRedirect");

		if (nodeId == 0) {
			WikiNode node = (WikiNode)renderRequest.getAttribute(
				WikiWebKeys.WIKI_NODE);

			if (node != null) {
				nodeId = node.getNodeId();
			}
		}

		WikiPage page = null;

		if (Validator.isNull(title)) {
			renderRequest.setAttribute(WikiWebKeys.WIKI_PAGE, page);

			return;
		}

		try {
			if (version == 0) {
				page = _wikiPageService.getPage(nodeId, title, null);
			}
			else {
				page = _wikiPageService.getPage(nodeId, title, version);
			}
		}
		catch (NoSuchPageException noSuchPageException1) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPageException1);
			}

			try {
				page = _wikiPageService.getPage(nodeId, title, false);
			}
			catch (NoSuchPageException noSuchPageException2) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchPageException2);
				}

				WikiWebComponentProvider wikiWebComponentProvider =
					WikiWebComponentProvider.getWikiWebComponentProvider();

				WikiGroupServiceConfiguration wikiGroupServiceConfiguration =
					wikiWebComponentProvider.getWikiGroupServiceConfiguration();

				if (title.equals(
						wikiGroupServiceConfiguration.frontPageName()) &&
					(version == 0)) {

					page = _wikiPageService.addPage(
						nodeId, title, null, WikiPageConstants.NEW, true,
						new ServiceContext());
				}
				else {
					throw noSuchPageException2;
				}
			}
		}

		if (removeRedirect) {
			page.setContent(StringPool.BLANK);
			page.setRedirectTitle(StringPool.BLANK);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_wikiPageModelResourcePermission.check(
			themeDisplay.getPermissionChecker(), page, ActionKeys.UPDATE);

		renderRequest.setAttribute(WikiWebKeys.WIKI_PAGE, page);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditPageMVCRenderCommand.class);

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiPage)")
	private volatile ModelResourcePermission<WikiPage>
		_wikiPageModelResourcePermission;

	@Reference
	private WikiPageService _wikiPageService;

	@Reference
	private WikiPageTitleValidator _wikiPageTitleValidator;

}
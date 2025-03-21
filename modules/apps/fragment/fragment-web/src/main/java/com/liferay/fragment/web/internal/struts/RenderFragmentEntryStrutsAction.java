/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.struts;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "path=/portal/fragment/render_fragment_entry",
	service = StrutsAction.class
)
public class RenderFragmentEntryStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

		_portletResourcePermission.check(
			themeDisplay.getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		httpServletRequest.setAttribute(
			FragmentCollectionContributorRegistry.class.getName(),
			_fragmentCollectionContributorRegistry);
		httpServletRequest.setAttribute(
			FragmentRendererController.class.getName(),
			_fragmentRendererController);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			groupId, false);

		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/render_fragment_entry.jsp");

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		requestDispatcher.include(httpServletRequest, pipingServletResponse);

		String content = ThemeUtil.include(
			httpServletRequest.getServletContext(), httpServletRequest,
			httpServletResponse, "portal_normal.ftl", layoutSet.getTheme(),
			false);

		if (Validator.isNull(content)) {
			ServletResponseUtil.write(
				httpServletResponse, unsyncStringWriter.toString());

			return null;
		}

		Document document = Jsoup.parse(content);

		Element bodyElement = document.body();

		bodyElement.html(unsyncStringWriter.toString());

		ServletResponseUtil.write(httpServletResponse, document.html());

		SessionErrors.clear(httpServletRequest);

		SessionMessages.add(
			httpServletRequest,
			FragmentPortletKeys.FRAGMENT +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);

		return null;
	}

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentRendererController _fragmentRendererController;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.fragment.web)")
	private ServletContext _servletContext;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.cms.site.initializer.internal.display.context.SpacesSectionDisplayContext;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = FragmentRenderer.class)
public class SpacesSectionFragmentRenderer extends BaseSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "Spaces");
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write("<div><span aria-hidden=\"true\" class=\"");
			printWriter.write("loading-animation\"></span>");

			ComponentTag componentTag = new ComponentTag();

			componentTag.setModule(
				"{SpacesNavigation} from site-cms-site-initializer");
			componentTag.setPageContext(
				PageContextFactoryUtil.create(
					httpServletRequest, httpServletResponse));

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			SpacesSectionDisplayContext spacesSectionDisplayContext =
				new SpacesSectionDisplayContext(
					_assetLibraryResourceFactory, httpServletRequest);

			if (PortalRunMode.isTestMode()) {
				httpServletRequest.setAttribute(
					SpacesSectionDisplayContext.class.getName(),
					spacesSectionDisplayContext);
			}

			Page<AssetLibrary> page = spacesSectionDisplayContext.getPage();

			componentTag.setProps(
				HashMapBuilder.<String, Object>put(
					"allSpacesURL",
					StringBundler.concat(
						themeDisplay.getPathFriendlyURLPublic(),
						GroupConstants.CMS_FRIENDLY_URL, "/all-spaces")
				).put(
					"assetLibraries",
					JSONUtil.toJSONArray(
						page.getItems(),
						assetLibrary -> JSONUtil.put(
							"id", assetLibrary.getId()
						).put(
							"name", assetLibrary.getName()
						).put(
							"url",
							StringBundler.concat(
								themeDisplay.getPathFriendlyURLPublic(),
								GroupConstants.CMS_FRIENDLY_URL, "/e/space/",
								_portal.getClassNameId(DepotEntry.class),
								StringPool.SLASH, assetLibrary.getId())
						))
				).put(
					"assetLibrariesCount", page.getTotalCount()
				).put(
					"newSpaceURL",
					StringBundler.concat(
						themeDisplay.getPathFriendlyURLPublic(),
						GroupConstants.CMS_FRIENDLY_URL, "/new-space")
				).put(
					"showAddButton",
					_portletResourcePermission.contains(
						themeDisplay.getPermissionChecker(),
						themeDisplay.getScopeGroupId(),
						DepotActionKeys.ADD_DEPOT_ENTRY)
				).build());

			componentTag.setServletContext(_servletContext);

			componentTag.doStartTag();

			componentTag.doEndTag();

			printWriter.write("</div>");
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	private AssetLibraryResource.Factory _assetLibraryResourceFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(target = "(resource.name=" + DepotConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.cms.site.initializer)"
	)
	private ServletContext _servletContext;

}
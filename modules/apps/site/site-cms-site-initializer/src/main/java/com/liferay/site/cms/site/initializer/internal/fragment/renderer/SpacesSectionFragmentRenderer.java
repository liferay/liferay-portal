/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;

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

			JSONArray assetLibrariesJSONArray = _jsonFactory.createJSONArray();
			long assetLibrariesCount = 0;

			try {
				AssetLibraryResource.Builder builder =
					_assetLibraryResourceFactory.create();

				AssetLibraryResource assetLibraryResource = builder.user(
					themeDisplay.getUser()
				).build();

				Page<AssetLibrary> page =
					assetLibraryResource.getAssetLibrariesPage(
						null, null, null, Pagination.of(1, 5), null);

				assetLibrariesJSONArray = JSONUtil.toJSONArray(
					page.getItems(),
					assetLibrary -> JSONUtil.put(
						"id", assetLibrary.getId()
					).put(
						"name", assetLibrary.getName()
					).put(
						"url",
						themeDisplay.getPathFriendlyURLPublic() +
							"/cms/asset-library/" + assetLibrary.getId()
					));
				assetLibrariesCount = page.getTotalCount();
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			componentTag.setProps(
				HashMapBuilder.<String, Object>put(
					"assetLibraries", assetLibrariesJSONArray
				).put(
					"assetLibrariesCount", assetLibrariesCount
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

	private static final Log _log = LogFactoryUtil.getLog(
		SpacesSectionFragmentRenderer.class);

	@Reference
	private AssetLibraryResource.Factory _assetLibraryResourceFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference(target = "(resource.name=" + DepotConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.cms.site.initializer)"
	)
	private ServletContext _servletContext;

}
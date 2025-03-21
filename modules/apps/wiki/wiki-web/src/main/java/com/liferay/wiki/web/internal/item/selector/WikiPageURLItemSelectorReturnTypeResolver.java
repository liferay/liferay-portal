/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.item.selector;

import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.escape.WikiEscapeUtil;
import com.liferay.wiki.item.selector.WikiPageURLItemSelectorReturnType;
import com.liferay.wiki.model.WikiPage;

import jakarta.portlet.PortletRequest;

import java.net.URL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "service.ranking:Integer=100",
	service = ItemSelectorReturnTypeResolver.class
)
public class WikiPageURLItemSelectorReturnTypeResolver
	implements WikiPageItemSelectorReturnTypeResolver
		<WikiPageURLItemSelectorReturnType, WikiPage> {

	@Override
	public Class<WikiPageURLItemSelectorReturnType>
		getItemSelectorReturnTypeClass() {

		return WikiPageURLItemSelectorReturnType.class;
	}

	@Override
	public Class<WikiPage> getModelClass() {
		return WikiPage.class;
	}

	@Override
	public String getTitle(WikiPage page, ThemeDisplay themeDisplay) {
		return StringPool.BLANK;
	}

	@Override
	public String getValue(WikiPage page, ThemeDisplay themeDisplay)
		throws Exception {

		String layoutFullURL = _portal.getLayoutFullURL(
			page.getGroupId(), WikiPortletKeys.WIKI);

		if (Validator.isNotNull(layoutFullURL)) {
			URL urlObject = new URL(layoutFullURL);

			String path = urlObject.getPath();

			if (Validator.isNotNull(path)) {
				return StringBundler.concat(
					path, Portal.FRIENDLY_URL_SEPARATOR, "wiki/",
					page.getNodeId(), StringPool.SLASH,
					URLCodec.encodeURL(
						WikiEscapeUtil.escapeName(page.getTitle())));
			}
		}

		return HttpComponentsUtil.removeDomain(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					themeDisplay.getRequest(), WikiPortletKeys.WIKI_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/wiki/view"
			).setParameter(
				"nodeId", page.getNodeId()
			).setParameter(
				"title", page.getTitle()
			).buildString());
	}

	@Reference
	private Portal _portal;

}
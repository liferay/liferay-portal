/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.DropdownMenuTag;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.web.internal.display.context.ChannelSelectorDisplayContext;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FaroChannelSelectorFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "liferay-data-platform";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "property-selector");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ChannelSelectorDisplayContext channelSelectorDisplayContext =
			new ChannelSelectorDisplayContext(httpServletRequest);

		FaroChannel faroChannel =
			channelSelectorDisplayContext.getFaroChannel();

		if (faroChannel == null) {
			return;
		}

		DropdownMenuTag dropdownMenuTag = new DropdownMenuTag();

		dropdownMenuTag.setBlock(true);
		dropdownMenuTag.setCssClass("mb-3");
		dropdownMenuTag.setDisplayType("secondary");
		dropdownMenuTag.setLabel(faroChannel.getName());

		try {
			dropdownMenuTag.setDropdownItems(
				channelSelectorDisplayContext.getDropdownItems());

			dropdownMenuTag.doTag(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	@Reference
	private Language _language;

}
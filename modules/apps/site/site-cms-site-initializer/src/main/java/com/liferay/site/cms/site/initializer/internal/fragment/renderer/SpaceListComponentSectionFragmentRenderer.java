/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSpaceStickerConstants;
import com.liferay.site.cms.site.initializer.internal.display.context.SpaceStickerDisplayContext;
import com.liferay.site.cms.site.initializer.internal.util.InfoItemUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Georgel Pop
 */
@Component(service = FragmentRenderer.class)
public class SpaceListComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "space-list";
	}

	@Override
	protected String getLabelKey() {
		return "space-list";
	}

	@Override
	protected String getModuleName() {
		return "SpaceList";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		SpaceStickerDisplayContext spaceStickerDisplayContext =
			new SpaceStickerDisplayContext(
				InfoItemUtil.getGroupId(httpServletRequest), groupLocalService,
				httpServletRequest, CMSSpaceStickerConstants.SM);

		if (PortalRunMode.isTestMode()) {
			httpServletRequest.setAttribute(
				SpaceStickerDisplayContext.class.getName(),
				spaceStickerDisplayContext);
		}

		return spaceStickerDisplayContext.getProps();
	}

}
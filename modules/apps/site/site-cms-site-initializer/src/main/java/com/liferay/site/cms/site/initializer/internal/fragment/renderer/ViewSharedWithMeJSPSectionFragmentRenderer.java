/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewSharedWithMeSectionDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(service = FragmentRenderer.class)
public class ViewSharedWithMeJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewSharedWithMeSectionDisplayContext> {

	@Override
	public String getLabelKey() {
		return "shared-with-me";
	}

	@Override
	protected ViewSharedWithMeSectionDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewSharedWithMeSectionDisplayContext(
			httpServletRequest, _objectDefinitionService, _portal);
	}

	@Override
	protected String getJSPPath() {
		return "/view_shared_with_me.jsp";
	}

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private Portal _portal;

}
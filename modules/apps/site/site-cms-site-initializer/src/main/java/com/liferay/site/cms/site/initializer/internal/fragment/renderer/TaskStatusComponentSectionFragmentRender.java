 /**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import jakarta.servlet.http.HttpServletRequest;
import org.osgi.service.component.annotations.Component;

import java.util.Map;

/**
 * @author Fabio Monaco
 */
@Component(service = FragmentRenderer.class)
public class TaskStatusComponentSectionFragmentRender
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {return "task-status"; }

	@Override
	protected String getLabelKey() {
		return "task-status";
	}

	@Override
	protected String getModuleName() {
		return "TaskStatusManager";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) throws Exception {
		return Map.of();
	}
}

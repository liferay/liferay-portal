/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cmp.site.initializer.internal.util.WorkflowTaskSearchURLUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jhosseph Gonzalez
 */
@Component(service = FragmentRenderer.class)
public class WorkflowTasksOverviewComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "WorkflowTasksOverview";
	}

	@Override
	protected String getLabelKey() {
		return "workflow-tasks-overview";
	}

	@Override
	protected String getModuleName() {
		return "site-cmp-site-initializer";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", themeDisplay.getCompanyId());

		if (objectDefinition == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"filterURL", WorkflowTaskSearchURLUtil.getSearchURL()
		).build();
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}
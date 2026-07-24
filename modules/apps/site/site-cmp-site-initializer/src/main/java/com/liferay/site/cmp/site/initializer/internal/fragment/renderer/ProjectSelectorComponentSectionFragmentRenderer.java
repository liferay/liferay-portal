/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.site.cmp.site.initializer.internal.util.ObjectEntryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fábio Alves
 */
@Component(service = FragmentRenderer.class)
public class ProjectSelectorComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "project-selector";
	}

	@Override
	public String getComponentName(HttpServletRequest httpServletRequest) {
		return "ProjectSelector";
	}

	@Override
	protected String getLabelKey() {
		return "project";
	}

	@Override
	protected String getModuleName() {
		return "site-cmp-site-initializer";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = ObjectEntryUtil.getObjectEntry(
			httpServletRequest);

		if (objectEntry == null) {
			return Collections.emptyMap();
		}

		ObjectEntry cmpProjectObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				MapUtil.getLong(
					objectEntry.getValues(),
					"r_cmpProjectToCMPTasks_c_cmpProjectId"));

		if (cmpProjectObjectEntry == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"items",
			() -> JSONUtil.putAll(
				JSONUtil.put(
					"label",
					MapUtil.getString(
						cmpProjectObjectEntry.getValues(), "title")
				).put(
					"value",
					String.valueOf(cmpProjectObjectEntry.getObjectEntryId())
				))
		).build();
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}
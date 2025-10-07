/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabio Monaco
 * @author Giamarco Brunialti Masera
 */
@Component(service = FragmentRenderer.class)
public class BulkActionsMonitorComponentSectionFragmentRender
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "bulk-actions";
	}

	@Override
	protected String getLabelKey() {
		return "bulk-actions-monitor";
	}

	@Override
	protected String getModuleName() {
		return "BulkActionsMonitor";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return HashMapBuilder.<String, Object>put(
			"bulkActionTaskClassNameId",
			() -> {
				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.getObjectDefinition(
						PortalUtil.getCompanyId(httpServletRequest),
						"BulkActionTask");

				return PortalUtil.getClassNameId(
					objectDefinition.getClassName());
			}
		).build();
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}
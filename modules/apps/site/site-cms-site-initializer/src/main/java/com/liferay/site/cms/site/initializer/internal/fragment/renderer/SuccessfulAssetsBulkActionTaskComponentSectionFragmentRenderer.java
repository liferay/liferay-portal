/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.site.cms.site.initializer.constants.BulkActionExecutionStatusConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ivica Cardic
 */
@Component(service = FragmentRenderer.class)
public class SuccessfulAssetsBulkActionTaskComponentSectionFragmentRenderer
	extends BaseBulkActionTaskComponentSectionFragmentRenderer {

	@Override
	protected String getLabelKey() {
		return "bulk-action-task-successful-assets";
	}

	@Override
	protected String getModuleName() {
		return "BulkActionTaskAssets";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return HashMapBuilder.<String, Object>put(
			"assetsCount",
			getBulkActionTaskItemsCount(
				BulkActionExecutionStatusConstants.COMPLETED,
				httpServletRequest)
		).put(
			"executionStatus", BulkActionExecutionStatusConstants.COMPLETED
		).build();
	}

}
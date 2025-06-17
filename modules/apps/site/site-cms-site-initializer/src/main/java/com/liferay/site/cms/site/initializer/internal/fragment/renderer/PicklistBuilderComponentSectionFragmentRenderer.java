/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.site.cms.site.initializer.internal.display.context.PicklistBuilderDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Verónica Gonzaléz
 */
@Component(service = FragmentRenderer.class)
public class PicklistBuilderComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "picklist-builder";
	}

	@Override
	protected String getLabelKey() {
		return "picklist-builder";
	}

	@Override
	protected String getModuleName() {
		return "PicklistBuilder";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		PicklistBuilderDisplayContext picklistBuilderDisplayContext =
			new PicklistBuilderDisplayContext(
				httpServletRequest, _jsonFactory,
				_listTypeDefinitionResourceFactory);

		return picklistBuilderDisplayContext.getProps();
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;

}
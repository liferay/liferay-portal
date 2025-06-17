/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.site.cms.site.initializer.internal.display.context.StructureBuilderDisplayContext;
import com.liferay.taglib.ui.SuccessTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sandro Chinea
 */
@Component(service = FragmentRenderer.class)
public class StructureBuilderComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "structure-builder";
	}

	@Override
	public String getLabelKey() {
		return "structure-builder";
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			SuccessTag successTag = new SuccessTag();

			successTag.setKey("displayPagePublished");
			successTag.setMessage("the-experience-was-updated-successfully");

			successTag.doTagAsString(httpServletRequest, httpServletResponse);

			super.render(
				fragmentRendererContext, httpServletRequest,
				httpServletResponse);
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	protected String getModuleName() {
		return "StructureBuilder";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		StructureBuilderDisplayContext structureBuilderDisplayContext =
			new StructureBuilderDisplayContext(
				httpServletRequest, _jsonFactory,
				_objectDefinitionResourceFactory);

		return structureBuilderDisplayContext.getProps();
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

}
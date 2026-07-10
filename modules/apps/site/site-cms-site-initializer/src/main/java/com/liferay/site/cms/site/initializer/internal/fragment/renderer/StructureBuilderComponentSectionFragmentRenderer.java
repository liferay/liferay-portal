/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;
import com.liferay.site.cms.site.initializer.internal.display.context.StructureBuilderDisplayContext;
import com.liferay.taglib.ui.SuccessTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

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
			successTag.setMessage("the-editor-was-updated-successfully");

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
	protected String getComponentName() {
		return "StructureBuilder";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		StructureBuilderDisplayContext structureBuilderDisplayContext =
			new StructureBuilderDisplayContext(
				_cmsStructureObjectFolderContributors, httpServletRequest,
				_jsonFactory, _objectDefinitionResourceFactory,
				_objectFieldBusinessTypeRegistry);

		return structureBuilderDisplayContext.getProps();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile List<CMSStructureObjectFolderContributor>
		_cmsStructureObjectFolderContributors;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

}
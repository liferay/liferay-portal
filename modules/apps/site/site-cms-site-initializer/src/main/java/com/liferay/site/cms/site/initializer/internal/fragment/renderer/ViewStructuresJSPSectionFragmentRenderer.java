/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewStructuresDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Sam Ziemer
 */
@Component(service = FragmentRenderer.class)
public class ViewStructuresJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewStructuresDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabelKey() {
		return "structures";
	}

	@Override
	protected ViewStructuresDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewStructuresDisplayContext(
			_cmsStructureObjectFolderContributors, httpServletRequest);
	}

	@Override
	protected String getJSPPath() {
		return "/view_structures.jsp";
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile List<CMSStructureObjectFolderContributor>
		_cmsStructureObjectFolderContributors;

}
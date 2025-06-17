/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrín Chaudhary
 */
@Component(service = FragmentRenderer.class)
public class EditFolderComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getLabelKey() {
		return "edit-folder";
	}

	@Override
	protected String getModuleName() {
		return "EditFolder";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"backURL", ParamUtil.getString(httpServletRequest, "redirect")
		).put(
			"folderId",
			() -> {
				Object object = httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM);

				if (object instanceof ObjectEntryFolder) {
					ObjectEntryFolder objectEntryFolder =
						(ObjectEntryFolder)object;

					return objectEntryFolder.getObjectEntryFolderId();
				}

				return null;
			}
		).build();
	}

}
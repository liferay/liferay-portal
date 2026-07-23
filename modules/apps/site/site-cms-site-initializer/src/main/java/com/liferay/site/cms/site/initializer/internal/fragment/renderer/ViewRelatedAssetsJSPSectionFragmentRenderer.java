/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewRelatedAssetsSectionDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fábio Alves
 */
@Component(service = FragmentRenderer.class)
public class ViewRelatedAssetsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewRelatedAssetsSectionDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabelKey() {
		return "related-assets";
	}

	@Override
	protected ViewRelatedAssetsSectionDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		Object object = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (!(object instanceof ObjectEntry)) {
			return null;
		}

		ObjectEntry objectEntry = (ObjectEntry)object;

		return new ViewRelatedAssetsSectionDisplayContext(
			_depotEntryLocalService, _dlConfiguration, groupLocalService,
			httpServletRequest, language,
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId()),
			_objectDefinitionService, objectEntry, _portal,
			translationInfoItemFieldValuesExporterRegistry);
	}

	@Override
	protected String getJSPPath() {
		return "/view_related_assets.jsp";
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private Portal _portal;

}
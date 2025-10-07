/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewVersionHistoryDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = FragmentRenderer.class)
public class ViewVersionHistoryJSPFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewVersionHistoryDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected ViewVersionHistoryDisplayContext getDisplayContext(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		return new ViewVersionHistoryDisplayContext(
			httpServletRequest, _language, objectDefinition, objectEntry);
	}

	@Override
	protected String getLabelKey() {
		return "view-version-history";
	}

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.portlet.internal.display.context;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Eudaldo Alonso
 */
public class PortletLayoutDisplayContext {

	public PortletLayoutDisplayContext(
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService) {

		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
	}

	public LayoutStructure getLayoutStructure(Layout layout) {
		if (_layoutStructure != null) {
			return _layoutStructure;
		}

		Layout masterLayout = null;

		long masterLayoutPlid = layout.getMasterLayoutPlid();

		if (masterLayoutPlid > 0) {
			masterLayout = _layoutLocalService.fetchLayout(masterLayoutPlid);
		}

		if (masterLayout == null) {
			_layoutStructure = _getDefaultMasterLayoutStructure();

			return _layoutStructure;
		}

		LayoutPageTemplateStructure masterLayoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					masterLayout.getGroupId(), masterLayout.getPlid());

		String data =
			masterLayoutPageTemplateStructure.
				getDefaultSegmentsExperienceData();

		if (Validator.isNull(data)) {
			_layoutStructure = _getDefaultMasterLayoutStructure();

			return _layoutStructure;
		}

		_layoutStructure = LayoutStructure.of(data);

		return _layoutStructure;
	}

	private LayoutStructure _getDefaultMasterLayoutStructure() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem(
				PortalUtil.getUniqueElementId());

		layoutStructure.addDropZoneLayoutStructureItem(
			PortalUtil.getUniqueElementId(),
			rootLayoutStructureItem.getItemId(), 0);

		return layoutStructure;
	}

	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private LayoutStructure _layoutStructure;

}
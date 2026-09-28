/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.display.context;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Eudaldo Alonso
 */
public class RenderMaxStateLayoutStructureDisplayContext {

	public LayoutStructure getLayoutStructure(Layout layout) {
		if (_layoutStructure != null) {
			return _layoutStructure;
		}

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					layout.getMasterLayoutPageTemplateEntryERC(),
					layout.getGroupId());

		if (masterLayoutPageTemplateEntry == null) {
			_layoutStructure = _getDefaultMasterLayoutStructure();

			return _layoutStructure;
		}

		LayoutPageTemplateStructure masterLayoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(
					masterLayoutPageTemplateEntry.getGroupId(),
					masterLayoutPageTemplateEntry.getPlid());

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

	private LayoutStructure _layoutStructure;

}
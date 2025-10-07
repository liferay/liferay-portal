/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.provider;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = LayoutStructureProvider.class)
public class LayoutStructureProviderImpl implements LayoutStructureProvider {

	@Override
	public LayoutStructure getLayoutStructure(
		long plid, long segmentsExperienceId) {

		try {
			Layout layout = _getLayout(plid);

			if (!layout.isTypeAssetDisplay() && !layout.isTypeContent() &&
				!layout.isTypeUtility()) {

				String masterLayoutData = _getMasterLayoutData(
					layout.getMasterLayoutPlid());

				if (Validator.isNotNull(masterLayoutData)) {
					return LayoutStructure.of(masterLayoutData);
				}

				return null;
			}

			JSONObject dataJSONObject = null;

			LayoutPageTemplateStructure layoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(
						layout.getGroupId(), layout.getPlid());

			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
				_layoutPageTemplateStructureRelLocalService.
					fetchLayoutPageTemplateStructureRel(
						layoutPageTemplateStructure.
							getLayoutPageTemplateStructureId(),
						segmentsExperienceId);

			if (layoutPageTemplateStructureRel != null) {
				dataJSONObject =
					layoutPageTemplateStructureRel.getDataJSONObject();
			}

			if (JSONUtil.isEmpty(dataJSONObject)) {
				return null;
			}

			String masterLayoutData = _getMasterLayoutData(
				layout.getMasterLayoutPlid());

			if (Validator.isNull(masterLayoutData)) {
				return LayoutStructure.of(dataJSONObject);
			}

			return _mergeLayoutStructure(dataJSONObject, masterLayoutData);
		}
		catch (Exception exception) {
			_log.error("Unable to get layout structure", exception);

			return null;
		}
	}

	private Layout _getLayout(long plid) {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			layout = virtualLayout.getSourceLayout();
		}

		return layout;
	}

	private String _getMasterLayoutData(long masterLayoutPlid) {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(masterLayoutPlid);

		if (masterLayoutPageTemplateEntry == null) {
			return null;
		}

		LayoutPageTemplateStructure masterLayoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					masterLayoutPageTemplateEntry.getGroupId(),
					masterLayoutPageTemplateEntry.getPlid());

		if (masterLayoutPageTemplateStructure == null) {
			return null;
		}

		return masterLayoutPageTemplateStructure.
			getDefaultSegmentsExperienceData();
	}

	private LayoutStructure _mergeLayoutStructure(
		JSONObject dataJSONObject, String masterLayoutData) {

		LayoutStructure masterLayoutStructure = LayoutStructure.of(
			masterLayoutData);

		LayoutStructure layoutStructure = LayoutStructure.of(dataJSONObject);

		for (LayoutStructureItem layoutStructureItem :
				layoutStructure.getLayoutStructureItems()) {

			masterLayoutStructure.addLayoutStructureItem(layoutStructureItem);
		}

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			(DropZoneLayoutStructureItem)
				masterLayoutStructure.getDropZoneLayoutStructureItem();

		dropZoneLayoutStructureItem.addChildrenItem(
			layoutStructure.getMainItemId());

		LayoutStructureItem rootStructureItem =
			masterLayoutStructure.getLayoutStructureItem(
				layoutStructure.getMainItemId());

		rootStructureItem.setParentItemId(
			dropZoneLayoutStructureItem.getItemId());

		return masterLayoutStructure;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutStructureProviderImpl.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util.layout.structure;

import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureServiceUtil;
import com.liferay.layout.util.structure.DeletedLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.List;

/**
 * @author Víctor Galán
 */
public class LayoutStructureUtil {

	public static void deleteMarkedForDeletionItems(
			long groupId, long plid, long userId)
		throws PortalException {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(groupId, plid);

		if (layoutPageTemplateStructure == null) {
			return;
		}

		FragmentEntryLinkLocalServiceUtil.deleteFragmentEntryLinks(
			groupId, plid, true);

		List<LayoutPageTemplateStructureRel> layoutPageTemplateStructureRels =
			LayoutPageTemplateStructureRelLocalServiceUtil.
				getLayoutPageTemplateStructureRels(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId());

		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				layoutPageTemplateStructureRels) {

			LayoutStructure layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructureRel.getData());

			for (DeletedLayoutStructureItem deletedLayoutStructureItem :
					layoutStructure.getDeletedLayoutStructureItems()) {

				layoutStructure.deleteLayoutStructureItem(
					deletedLayoutStructureItem.getItemId());
			}

			LayoutPageTemplateStructureLocalServiceUtil.
				updateLayoutPageTemplateStructureData(
					userId, groupId, plid,
					layoutPageTemplateStructureRel.getSegmentsExperienceId(),
					layoutStructure.toString());
		}
	}

	public static LayoutStructure getLayoutStructure(
			long groupId, long plid, long segmentsExperienceId)
		throws PortalException {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(groupId, plid);

		return LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));
	}

	public static LayoutStructure getLayoutStructure(
			long groupId, long plid, String segmentsExperienceKey)
		throws PortalException {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(groupId, plid);

		return LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceKey));
	}

	public static JSONObject updateLayoutPageTemplateData(
			long groupId, long segmentsExperienceId, long plid,
			UnsafeConsumer<LayoutStructure, PortalException> unsafeConsumer)
		throws PortalException {

		LayoutStructure layoutStructure = getLayoutStructure(
			groupId, plid, segmentsExperienceId);

		unsafeConsumer.accept(layoutStructure);

		JSONObject dataJSONObject = layoutStructure.toJSONObject();

		LayoutPageTemplateStructureServiceUtil.
			updateLayoutPageTemplateStructureData(
				groupId, plid, segmentsExperienceId, dataJSONObject.toString());

		return dataJSONObject;
	}

}
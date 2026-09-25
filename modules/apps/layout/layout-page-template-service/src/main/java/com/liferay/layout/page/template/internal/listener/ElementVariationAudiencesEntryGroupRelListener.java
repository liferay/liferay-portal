/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.listener;

import com.liferay.audiences.listener.AudiencesEntryGroupRelListener;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = AudiencesEntryGroupRelListener.class)
public class ElementVariationAudiencesEntryGroupRelListener
	implements AudiencesEntryGroupRelListener {

	@Override
	public void onUpdateAudiencesEntryGroupRels(
		long companyId, String audienceEntryERC, String[] groupERCs) {

		Set<Long> groupIds = new HashSet<>();

		for (String groupERC : groupERCs) {
			Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
				groupERC, companyId);

			if (group != null) {
				groupIds.add(group.getGroupId());
			}
		}

		for (LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel :
					_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
						getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByAudienceEntryERC(
							companyId, audienceEntryERC)) {

			if (groupIds.contains(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getGroupId())) {

				continue;
			}

			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
				deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

}
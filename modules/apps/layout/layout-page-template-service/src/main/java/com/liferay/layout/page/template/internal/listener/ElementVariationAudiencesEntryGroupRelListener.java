/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.listener;

import com.liferay.audiences.listener.AudiencesEntryGroupRelListener;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = AudiencesEntryGroupRelListener.class)
public class ElementVariationAudiencesEntryGroupRelListener
	implements AudiencesEntryGroupRelListener {

	@Override
	public void onDeleteAudiencesEntryGroupRels(
		long companyId, String audienceEntryERC, String[] groupERCs) {

		for (String groupERC : groupERCs) {
			Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
				groupERC, companyId);

			if (group == null) {
				continue;
			}

			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
				deleteGroupLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
					group.getGroupId(), audienceEntryERC);
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

}
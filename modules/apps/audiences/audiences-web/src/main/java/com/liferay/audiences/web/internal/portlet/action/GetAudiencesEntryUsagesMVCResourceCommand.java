/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.portlet.action;

import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalService;
import com.liferay.audiences.constants.AudiencesPortletKeys;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AudiencesPortletKeys.AUDIENCES,
		"mvc.command.name=/audiences/get_audiences_entry_usages"
	},
	service = MVCResourceCommand.class
)
public class GetAudiencesEntryUsagesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		AudiencesEntry audiencesEntry =
			_audiencesEntryService.getAudiencesEntry(
				ParamUtil.getLong(resourceRequest, "audiencesEntryId"));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"collectionVariationsCount",
				_assetListEntryAudienceEntryRelLocalService.
					getAssetListEntryAudienceEntryRelsCountByAudienceEntryERC(
						audiencesEntry.getCompanyId(),
						audiencesEntry.getExternalReferenceCode())
			).put(
				"elementVariationsCount",
				_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
					getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsCountByAudienceEntryERC(
						audiencesEntry.getCompanyId(),
						audiencesEntry.getExternalReferenceCode())
			));
	}

	@Reference
	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

	@Reference
	private AudiencesEntryService _audiencesEntryService;

	@Reference
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

}
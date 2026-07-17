/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.impl;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.base.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel",
	service = AopService.class
)
public class
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceImpl
		extends LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceBaseImpl {

	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				long userId, long groupId, String audienceEntryERC,
				String layoutPageTemplateStructureRelElementVariationERC,
				ServiceContext serviceContext)
		throws PortalException {

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence.
					create(counterLocalService.increment());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.setUuid(
			serviceContext.getUuid());
		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setGroupId(groupId);

		User user = _userLocalService.getUser(userId);

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setCompanyId(user.getCompanyId());
		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setUserId(user.getUserId());
		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setUserName(user.getFullName());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setAudienceEntryERC(audienceEntryERC);
		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC);

		return layoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence.
			update(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	@Override
	public void
		deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
			String layoutPageTemplateStructureRelElementVariationERC) {

		layoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence.
			removeByLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC);
	}

	@Override
	public void
		deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRelsByAudienceEntryERC(
			long companyId, String audienceEntryERC) {

		layoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence.
			removeByC_AEERC(companyId, audienceEntryERC);
	}

	@Override
	public List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
		getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
			String layoutPageTemplateStructureRelElementVariationERC) {

		return layoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence.
			findByLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC);
	}

	@Reference
	private UserLocalService _userLocalService;

}
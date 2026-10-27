/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.impl;

import com.liferay.layout.model.LayoutLocalization;
import com.liferay.layout.service.base.LayoutLocalizationLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.layout.model.LayoutLocalization",
	service = AopService.class
)
public class LayoutLocalizationLocalServiceImpl
	extends LayoutLocalizationLocalServiceBaseImpl {

	@Override
	public LayoutLocalization addLayoutLocalization(
		long groupId, String content, String languageId, long plid,
		ServiceContext serviceContext) {

		long layoutLocalizationId = counterLocalService.increment();

		LayoutLocalization layoutLocalization =
			layoutLocalizationPersistence.create(layoutLocalizationId);

		layoutLocalization.setUuid(serviceContext.getUuid());
		layoutLocalization.setGroupId(groupId);

		long companyId = serviceContext.getCompanyId();

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group != null) {
			companyId = group.getCompanyId();
		}

		layoutLocalization.setCompanyId(companyId);

		layoutLocalization.setContent(content);
		layoutLocalization.setLanguageId(languageId);
		layoutLocalization.setPlid(plid);

		return layoutLocalizationPersistence.update(layoutLocalization);
	}

	@Override
	public LayoutLocalization fetchLayoutLocalization(
		long groupId, String languageId, long plid) {

		return layoutLocalizationPersistence.fetchByG_L_P(
			groupId, languageId, plid);
	}

	@Override
	public LayoutLocalization getLayoutLocalization(
			String languageId, long plid)
		throws PortalException {

		return layoutLocalizationPersistence.findByL_P(languageId, plid);
	}

	@Override
	public LayoutLocalization updateLayoutLocalization(
		String content, String languageId, long plid,
		ServiceContext serviceContext) {

		Layout layout = _layoutLocalService.fetchLayout(plid);

		LayoutLocalization layoutLocalization =
			layoutLocalizationPersistence.fetchByG_L_P(
				layout.getGroupId(), languageId, layout.getPlid());

		if (layoutLocalization == null) {
			return addLayoutLocalization(
				layout.getGroupId(), content, languageId, plid, serviceContext);
		}

		layoutLocalization.setContent(content);

		return layoutLocalizationPersistence.update(layoutLocalization);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

}
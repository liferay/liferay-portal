/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.impl;

import com.liferay.layout.page.template.exception.LayoutPageTemplateStructureRelElementVariationAudienceEntryERCsException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateStructureRelElementVariationNameException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateStructureRelElementVariationTargetElementException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.layout.page.template.service.base.LayoutPageTemplateStructureRelElementVariationLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation",
	service = AopService.class
)
public class LayoutPageTemplateStructureRelElementVariationLocalServiceImpl
	extends LayoutPageTemplateStructureRelElementVariationLocalServiceBaseImpl {

	public LayoutPageTemplateStructureRelElementVariation
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long userId, long groupId,
				String[] audienceEntryERCs, boolean active, String hide,
				Map<Locale, String> htmlMap, Map<Locale, String> jsMap,
				String name, long plid, String segmentsExperienceERC,
				String targetElement, ServiceContext serviceContext)
		throws PortalException {

		_validate(audienceEntryERCs, name, targetElement);

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				layoutPageTemplateStructureRelElementVariationPersistence.
					fetchByERC_G(externalReferenceCode, groupId);

		if (layoutPageTemplateStructureRelElementVariation == null) {
			User user = _userLocalService.getUser(userId);

			layoutPageTemplateStructureRelElementVariation =
				layoutPageTemplateStructureRelElementVariationPersistence.
					create(counterLocalService.increment());

			layoutPageTemplateStructureRelElementVariation.setUuid(
				serviceContext.getUuid());
			layoutPageTemplateStructureRelElementVariation.
				setExternalReferenceCode(externalReferenceCode);
			layoutPageTemplateStructureRelElementVariation.setGroupId(groupId);
			layoutPageTemplateStructureRelElementVariation.setCompanyId(
				user.getCompanyId());
			layoutPageTemplateStructureRelElementVariation.setUserId(
				user.getUserId());
			layoutPageTemplateStructureRelElementVariation.setUserName(
				user.getFullName());
			layoutPageTemplateStructureRelElementVariation.setCreateDate(
				serviceContext.getCreateDate(new Date()));
		}

		layoutPageTemplateStructureRelElementVariation.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		layoutPageTemplateStructureRelElementVariation.setActive(active);
		layoutPageTemplateStructureRelElementVariation.setHide(hide);
		layoutPageTemplateStructureRelElementVariation.setHtmlMap(htmlMap);
		layoutPageTemplateStructureRelElementVariation.setJsMap(jsMap);
		layoutPageTemplateStructureRelElementVariation.setName(name);
		layoutPageTemplateStructureRelElementVariation.setPlid(plid);
		layoutPageTemplateStructureRelElementVariation.setSegmentsExperienceERC(
			segmentsExperienceERC);
		layoutPageTemplateStructureRelElementVariation.setTargetElement(
			targetElement);

		layoutPageTemplateStructureRelElementVariation =
			layoutPageTemplateStructureRelElementVariationPersistence.update(
				layoutPageTemplateStructureRelElementVariation);

		_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
				externalReferenceCode);

		for (String audienceEntryERC : audienceEntryERCs) {
			if (Validator.isNull(audienceEntryERC)) {
				continue;
			}

			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
					userId, groupId, audienceEntryERC, externalReferenceCode,
					serviceContext);
		}

		return layoutPageTemplateStructureRelElementVariation;
	}

	public void deleteLayoutPageTemplateStructureRelElementVariation(
		String externalReferenceCode, long groupId) {

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				layoutPageTemplateStructureRelElementVariationPersistence.
					fetchByERC_G(externalReferenceCode, groupId);

		if (layoutPageTemplateStructureRelElementVariation != null) {
			layoutPageTemplateStructureRelElementVariationPersistence.remove(
				layoutPageTemplateStructureRelElementVariation);

			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
				deleteLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
					externalReferenceCode);
		}
	}

	public List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(
			boolean active, long plid, String segmentsExperienceERC) {

		return layoutPageTemplateStructureRelElementVariationPersistence.
			findByA_P_SEERC(active, plid, segmentsExperienceERC);
	}

	public List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(long plid) {

		return layoutPageTemplateStructureRelElementVariationPersistence.
			findByPlid(plid);
	}

	public List<LayoutPageTemplateStructureRelElementVariation>
		getLayoutPageTemplateStructureRelElementVariations(
			long plid, String segmentsExperienceERC) {

		return layoutPageTemplateStructureRelElementVariationPersistence.
			findByP_SEERC(plid, segmentsExperienceERC);
	}

	private void _validate(
			String[] audienceEntryERCs, String name, String targetElement)
		throws PortalException {

		if (ArrayUtil.isEmpty(audienceEntryERCs)) {
			throw new LayoutPageTemplateStructureRelElementVariationAudienceEntryERCsException(
				"Audience entry external reference codes must not be empty");
		}

		if (Validator.isNull(name)) {
			throw new LayoutPageTemplateStructureRelElementVariationNameException(
				"Name must not be null");
		}

		if (Validator.isNull(targetElement)) {
			throw new LayoutPageTemplateStructureRelElementVariationTargetElementException(
				"Target element must not be null");
		}
	}

	@Reference
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
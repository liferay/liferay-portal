/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link LayoutPageTemplateStructureRelElementVariationService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationService
 * @generated
 */
public class LayoutPageTemplateStructureRelElementVariationServiceWrapper
	implements LayoutPageTemplateStructureRelElementVariationService,
			   ServiceWrapper
				   <LayoutPageTemplateStructureRelElementVariationService> {

	public LayoutPageTemplateStructureRelElementVariationServiceWrapper() {
		this(null);
	}

	public LayoutPageTemplateStructureRelElementVariationServiceWrapper(
		LayoutPageTemplateStructureRelElementVariationService
			layoutPageTemplateStructureRelElementVariationService) {

		_layoutPageTemplateStructureRelElementVariationService =
			layoutPageTemplateStructureRelElementVariationService;
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long groupId, boolean active,
				String hide, java.util.Map<java.util.Locale, String> htmlMap,
				java.util.Map<java.util.Locale, String> jsMap, String name,
				long plid, String segmentsExperienceERC, String targetElement,
				String[] audienceEntryERCs,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationService.
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, groupId, active, hide, htmlMap, jsMap,
				name, plid, segmentsExperienceERC, targetElement,
				audienceEntryERCs, serviceContext);
	}

	@Override
	public void deleteLayoutPageTemplateStructureRelElementVariation(
			String externalReferenceCode, long groupId, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutPageTemplateStructureRelElementVariationService.
			deleteLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, groupId, plid);
	}

	@Override
	public java.util.List<LayoutPageTemplateStructureRelElementVariation>
			getLayoutPageTemplateStructureRelElementVariations(long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationService.
			getLayoutPageTemplateStructureRelElementVariations(plid);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutPageTemplateStructureRelElementVariationService.
			getOSGiServiceIdentifier();
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
			updateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long groupId, long plid,
				boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateStructureRelElementVariationService.
			updateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, groupId, plid, active);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationService
		getWrappedService() {

		return _layoutPageTemplateStructureRelElementVariationService;
	}

	@Override
	public void setWrappedService(
		LayoutPageTemplateStructureRelElementVariationService
			layoutPageTemplateStructureRelElementVariationService) {

		_layoutPageTemplateStructureRelElementVariationService =
			layoutPageTemplateStructureRelElementVariationService;
	}

	private LayoutPageTemplateStructureRelElementVariationService
		_layoutPageTemplateStructureRelElementVariationService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1861794388
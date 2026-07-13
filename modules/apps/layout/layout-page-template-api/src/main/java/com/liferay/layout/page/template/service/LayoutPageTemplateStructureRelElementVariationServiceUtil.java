/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for LayoutPageTemplateStructureRelElementVariation. This utility wraps
 * <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureRelElementVariationServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationService
 * @generated
 */
public class LayoutPageTemplateStructureRelElementVariationServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateStructureRelElementVariationServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long groupId,
				String[] audienceEntryERCs, boolean active, String hide,
				Map<java.util.Locale, String> htmlMap,
				Map<java.util.Locale, String> jsMap, String name, long plid,
				String segmentsExperienceERC, String targetElement,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, groupId, audienceEntryERCs, active, hide,
				htmlMap, jsMap, name, plid, segmentsExperienceERC,
				targetElement, serviceContext);
	}

	public static void deleteLayoutPageTemplateStructureRelElementVariation(
			String externalReferenceCode, long groupId, long plid)
		throws PortalException {

		getService().deleteLayoutPageTemplateStructureRelElementVariation(
			externalReferenceCode, groupId, plid);
	}

	public static List<LayoutPageTemplateStructureRelElementVariation>
			getLayoutPageTemplateStructureRelElementVariations(long plid)
		throws PortalException {

		return getService().getLayoutPageTemplateStructureRelElementVariations(
			plid);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static LayoutPageTemplateStructureRelElementVariationService
		getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<LayoutPageTemplateStructureRelElementVariationService>
			_serviceSnapshot = new Snapshot<>(
				LayoutPageTemplateStructureRelElementVariationServiceUtil.class,
				LayoutPageTemplateStructureRelElementVariationService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1590048869
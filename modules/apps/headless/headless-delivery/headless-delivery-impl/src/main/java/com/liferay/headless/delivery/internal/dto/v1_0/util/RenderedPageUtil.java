/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.util;

import com.liferay.headless.delivery.dto.v1_0.RenderedPage;
import com.liferay.headless.delivery.internal.resource.v1_0.BaseSitePageResourceImpl;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.segments.model.SegmentsExperience;

import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class RenderedPageUtil {

	public static RenderedPage getRenderedPage(
			DTOConverterContext dtoConverterContext, Layout layout,
			LayoutPageTemplateEntryLocalService
				layoutPageTemplateEntryLocalService,
			Portal portal)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(
				layout, layoutPageTemplateEntryLocalService, portal);

		LayoutPageTemplateEntry masterLayout = _getMasterLayout(
			layout, layoutPageTemplateEntryLocalService);

		return new RenderedPage() {
			{
				setMasterPageId(
					() -> {
						if (masterLayout != null) {
							return masterLayout.getLayoutPageTemplateEntryKey();
						}

						return null;
					});

				setMasterPageName(
					() -> {
						if (masterLayout != null) {
							return masterLayout.getName();
						}

						return null;
					});

				setPageTemplateId(
					() -> {
						if (layoutPageTemplateEntry == null) {
							return null;
						}

						return layoutPageTemplateEntry.
							getLayoutPageTemplateEntryKey();
					});

				setPageTemplateName(
					() -> {
						if (layoutPageTemplateEntry != null) {
							return layoutPageTemplateEntry.getName();
						}

						return null;
					});
				setRenderedPageURL(
					() -> _getJaxRsLink(dtoConverterContext, layout));
			}
		};
	}

	private static String _getJaxRsLink(
			DTOConverterContext dtoConverterContext, Layout layout)
		throws PortalException {

		UriInfo uriInfo = dtoConverterContext.getUriInfo();

		if (uriInfo == null) {
			return null;
		}

		List<Object> arguments = new ArrayList<>();

		arguments.add(layout.getGroupId());

		String friendlyURL = layout.getFriendlyURL(
			dtoConverterContext.getLocale());

		arguments.add(friendlyURL.substring(1));

		boolean showSegmentsExperience = GetterUtil.getBoolean(
			dtoConverterContext.getAttribute("showExperience"));

		if (!showSegmentsExperience) {
			return JaxRsLinkUtil.getJaxRsLink(
				"headless-delivery", BaseSitePageResourceImpl.class,
				"getSiteSitePageRenderedPage", uriInfo,
				arguments.toArray(new Object[0]));
		}

		SegmentsExperience segmentsExperience =
			(SegmentsExperience)dtoConverterContext.getAttribute(
				"segmentsExperience");

		arguments.add(segmentsExperience.getSegmentsExperienceKey());

		return JaxRsLinkUtil.getJaxRsLink(
			"headless-delivery", BaseSitePageResourceImpl.class,
			"getSiteSitePageExperienceExperienceKeyRenderedPage", uriInfo,
			arguments.toArray(new Object[0]));
	}

	private static LayoutPageTemplateEntry _getLayoutPageTemplateEntry(
		Layout layout,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		Portal portal) {

		if (layout.getClassNameId() != portal.getClassNameId(
				LayoutPageTemplateEntry.class)) {

			return null;
		}

		return layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
			layout.getClassPK());
	}

	private static LayoutPageTemplateEntry _getMasterLayout(
		Layout layout,
		LayoutPageTemplateEntryLocalService
			layoutPageTemplateEntryLocalService) {

		Layout masterLayout = LayoutLocalServiceUtil.fetchLayout(
			layout.getMasterLayoutPlid());

		if (masterLayout == null) {
			return null;
		}

		return layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
			masterLayout.getClassPK());
	}

}
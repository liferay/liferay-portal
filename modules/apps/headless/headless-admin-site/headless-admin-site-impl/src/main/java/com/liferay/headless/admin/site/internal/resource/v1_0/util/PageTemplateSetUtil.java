/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class PageTemplateSetUtil {

	public static LayoutPageTemplateCollection addLayoutPageTemplateCollection(
			long groupId, HttpServletRequest httpServletRequest,
			PageTemplateSet pageTemplateSet)
		throws Exception {

		return LayoutPageTemplateCollectionServiceUtil.
			addLayoutPageTemplateCollection(
				pageTemplateSet.getExternalReferenceCode(), groupId,
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				pageTemplateSet.getKey(), pageTemplateSet.getName(),
				pageTemplateSet.getDescription(),
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				_getServiceContext(
					groupId, httpServletRequest, pageTemplateSet));
	}

	private static ServiceContext _getServiceContext(
		long groupId, HttpServletRequest httpServletRequest,
		PageTemplateSet pageTemplateSet) {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, httpServletRequest, null
		).build();

		serviceContext.setCreateDate(pageTemplateSet.getDateCreated());
		serviceContext.setModifiedDate(pageTemplateSet.getDateModified());
		serviceContext.setUuid(pageTemplateSet.getUuid());

		return serviceContext;
	}

}
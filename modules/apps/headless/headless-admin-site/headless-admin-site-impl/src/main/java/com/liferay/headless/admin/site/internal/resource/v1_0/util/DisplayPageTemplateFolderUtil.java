/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class DisplayPageTemplateFolderUtil {

	public static LayoutPageTemplateCollection addLayoutPageTemplateCollection(
			DisplayPageTemplateFolder displayPageTemplateFolder, long groupId,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return LayoutPageTemplateCollectionServiceUtil.
			addLayoutPageTemplateCollection(
				displayPageTemplateFolder.getExternalReferenceCode(), groupId,
				getParentLayoutPageTemplateCollectionId(
					displayPageTemplateFolder, groupId, httpServletRequest),
				displayPageTemplateFolder.getKey(),
				displayPageTemplateFolder.getName(),
				displayPageTemplateFolder.getDescription(),
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
				_getServiceContext(
					displayPageTemplateFolder, groupId, httpServletRequest));
	}

	public static long getParentLayoutPageTemplateCollectionId(
			DisplayPageTemplateFolder displayPageTemplateFolder, long groupId,
			HttpServletRequest httpServletRequest)
		throws Exception {

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			displayPageTemplateFolder.getParentDisplayPageTemplateFolder();

		if (Validator.isNull(
				displayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode())) {

			if (!LazyReferencingThreadLocal.isEnabled() ||
				(parentDisplayPageTemplateFolder == null) ||
				Validator.isNull(
					parentDisplayPageTemplateFolder.
						getExternalReferenceCode())) {

				return LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;
			}

			displayPageTemplateFolder.
				setParentDisplayPageTemplateFolderExternalReferenceCode(
					parentDisplayPageTemplateFolder::getExternalReferenceCode);
		}

		LayoutPageTemplateCollection parentLayoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				fetchLayoutPageTemplateCollection(
					displayPageTemplateFolder.
						getParentDisplayPageTemplateFolderExternalReferenceCode(),
					groupId);

		if ((parentLayoutPageTemplateCollection == null) &&
			(parentDisplayPageTemplateFolder != null) &&
			LazyReferencingThreadLocal.isEnabled()) {

			if (!Objects.equals(
					displayPageTemplateFolder.
						getParentDisplayPageTemplateFolderExternalReferenceCode(),
					parentDisplayPageTemplateFolder.
						getExternalReferenceCode())) {

				throw new UnsupportedOperationException();
			}

			parentLayoutPageTemplateCollection =
				addLayoutPageTemplateCollection(
					displayPageTemplateFolder.
						getParentDisplayPageTemplateFolder(),
					groupId, httpServletRequest);
		}

		if ((parentLayoutPageTemplateCollection == null) ||
			!Objects.equals(
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
				parentLayoutPageTemplateCollection.getType())) {

			throw new UnsupportedOperationException();
		}

		return parentLayoutPageTemplateCollection.
			getLayoutPageTemplateCollectionId();
	}

	private static ServiceContext _getServiceContext(
		DisplayPageTemplateFolder displayPageTemplateFolder, long groupId,
		HttpServletRequest httpServletRequest) {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, httpServletRequest, null
		).build();

		serviceContext.setCreateDate(
			displayPageTemplateFolder.getDateCreated());
		serviceContext.setModifiedDate(
			displayPageTemplateFolder.getDateModified());
		serviceContext.setUuid(displayPageTemplateFolder.getUuid());

		return serviceContext;
	}

}
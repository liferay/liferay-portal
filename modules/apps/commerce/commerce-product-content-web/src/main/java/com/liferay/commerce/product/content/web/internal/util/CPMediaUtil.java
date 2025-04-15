/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.util;

import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.media.CommerceCatalogDefaultImage;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.content.util.CPMedia;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.documentlibrary.lar.FileEntryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CPMediaUtil {

	public static List<CPMedia> getAttachmentCPMedias(
			long classNameId, long classPK,
			CPAttachmentFileEntryLocalService cpAttachmentFileEntryLocalService,
			DLFileEntryLocalService dlFileEntryLocalService,
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission,
			ThemeDisplay themeDisplay)
		throws PortalException {

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		return TransformUtil.transform(
			cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
				classNameId, classPK, CPAttachmentFileEntryConstants.TYPE_OTHER,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			cpAttachmentFileEntry -> {
				DLFileEntry dlFileEntry =
					dlFileEntryLocalService.fetchDLFileEntry(
						cpAttachmentFileEntry.getFileEntryId());

				if ((dlFileEntry != null) &&
					!cpAttachmentFileEntry.isCDNEnabled() &&
					!dlFileEntryModelResourcePermission.contains(
						PermissionThreadLocal.getPermissionChecker(),
						dlFileEntry, ActionKeys.VIEW)) {

					return null;
				}

				return new CPMediaImpl(
					CommerceUtil.getCommerceAccountId(
						(CommerceContext)httpServletRequest.getAttribute(
							CommerceWebKeys.COMMERCE_CONTEXT)),
					cpAttachmentFileEntry, themeDisplay);
			});
	}

	public static List<CPMedia> getImageCPMedias(
			AMImageHTMLTagFactory amImageHTMLTagFactory, long classNameId,
			long classPK,
			CommerceCatalogDefaultImage commerceCatalogDefaultImage,
			CommerceMediaResolver commerceMediaResolver,
			CPAttachmentFileEntryLocalService cpAttachmentFileEntryLocalService,
			boolean gallery, long groupId, ThemeDisplay themeDisplay)
		throws PortalException {

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		long commerceAccountId = CommerceUtil.getCommerceAccountId(
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT));

		List<CPMedia> cpMedias = new ArrayList<>();

		List<CPAttachmentFileEntry> cpAttachmentFileEntries = null;

		if (gallery) {
			cpAttachmentFileEntries =
				cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
					classNameId, classPK, true,
					CPAttachmentFileEntryConstants.TYPE_IMAGE,
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);
		}
		else {
			cpAttachmentFileEntries =
				cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
					classNameId, classPK,
					CPAttachmentFileEntryConstants.TYPE_IMAGE,
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);
		}

		for (CPAttachmentFileEntry cpAttachmentFileEntry :
				cpAttachmentFileEntries) {

			String originalImgTag = StringBundler.concat(
				"<img class=\"product-img\" src=\"",
				commerceMediaResolver.getURL(
					commerceAccountId,
					cpAttachmentFileEntry.getCPAttachmentFileEntryId()),
				"\" />");

			if (!cpAttachmentFileEntry.isCDNEnabled()) {
				originalImgTag = amImageHTMLTagFactory.create(
					originalImgTag, cpAttachmentFileEntry.fetchFileEntry());
			}

			cpMedias.add(
				new AdaptiveMediaCPMediaImpl(
					originalImgTag, commerceAccountId, cpAttachmentFileEntry,
					themeDisplay));
		}

		if (cpMedias.isEmpty()) {
			FileEntry fileEntry = FileEntryUtil.fetchByPrimaryKey(
				commerceCatalogDefaultImage.getDefaultCatalogFileEntryId(
					groupId));

			if (fileEntry != null) {
				cpMedias.add(new CPMediaImpl(fileEntry, themeDisplay));
			}
			else {
				cpMedias.add(new CPMediaImpl(themeDisplay.getCompanyGroupId()));
			}
		}

		return cpMedias;
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.account.constants.AccountConstants;
import com.liferay.commerce.frontend.model.ImageField;
import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.media.CommerceMediaResolverUtil;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.ProductMedia;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_ATTACHMENTS,
	service = FDSDataProvider.class
)
public class CommerceProductAttachmentFDSDataProvider
	implements FDSDataProvider<ProductMedia> {

	@Override
	public List<ProductMedia> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		return TransformUtil.transform(
			_cpAttachmentFileEntryService.getCPAttachmentFileEntries(
				_portal.getClassNameId(CPDefinition.class), cpDefinitionId,
				fdsKeywords.getKeywords(),
				CPAttachmentFileEntryConstants.TYPE_OTHER,
				WorkflowConstants.STATUS_ANY, fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			cpAttachmentFileEntry -> {
				long cpAttachmentFileEntryId =
					cpAttachmentFileEntry.getCPAttachmentFileEntryId();

				String title = cpAttachmentFileEntry.getTitle(
					themeDisplay.getLanguageId());

				String extension = StringPool.BLANK;

				FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

				if (fileEntry != null) {
					extension = HtmlUtil.escape(fileEntry.getExtension());
				}

				String statusDisplayStyle = StringPool.BLANK;

				if (cpAttachmentFileEntry.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) {

					statusDisplayStyle = "success";
				}

				return new ProductMedia(
					cpAttachmentFileEntryId,
					new ImageField(
						title, "rounded", "lg",
						CommerceMediaResolverUtil.getThumbnailURL(
							AccountConstants.ACCOUNT_ENTRY_ID_ADMIN,
							cpAttachmentFileEntryId)),
					title, extension, cpAttachmentFileEntry.getPriority(),
					dateTimeFormat.format(
						cpAttachmentFileEntry.getModifiedDate()),
					new LabelField(
						statusDisplayStyle,
						_language.get(
							httpServletRequest,
							WorkflowConstants.getStatusLabel(
								cpAttachmentFileEntry.getStatus()))));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		return _cpAttachmentFileEntryService.getCPAttachmentFileEntriesCount(
			_portal.getClassNameId(CPDefinition.class), cpDefinitionId,
			fdsKeywords.getKeywords(),
			CPAttachmentFileEntryConstants.TYPE_OTHER,
			WorkflowConstants.STATUS_ANY);
	}

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}
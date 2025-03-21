/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.frontend.data.set;

import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryService;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemService;
import com.liferay.commerce.product.type.virtual.web.internal.constants.CPDefinitionVirtualSettingFDSNames;
import com.liferay.commerce.product.type.virtual.web.internal.model.VirtualFile;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "fds.data.provider.key=" + CPDefinitionVirtualSettingFDSNames.VIRTUAL_ORDER_FILES,
	service = FDSDataProvider.class
)
public class CommerceVirtualOrderItemFileEntryFDSDataProvider
	implements FDSDataProvider<VirtualFile> {

	@Override
	public List<VirtualFile> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<VirtualFile> virtualFiles = new ArrayList<>();

		long commerceVirtualOrderItemId = ParamUtil.getLong(
			httpServletRequest, "commerceVirtualOrderItemId");

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			_commerceVirtualOrderItemService.fetchCommerceVirtualOrderItem(
				commerceVirtualOrderItemId);

		if (commerceVirtualOrderItem != null) {
			for (CommerceVirtualOrderItemFileEntry
					commerceVirtualOrderItemFileEntry :
						_commerceVirtualOrderItemFileEntryService.
							getCommerceVirtualOrderItemFileEntries(
								commerceVirtualOrderItemId,
								fdsPagination.getStartPosition(),
								fdsPagination.getEndPosition())) {

				virtualFiles.add(
					new VirtualFile(
						commerceVirtualOrderItemFileEntry.
							getCommerceVirtualOrderItemFileEntryId(),
						_getURL(commerceVirtualOrderItemFileEntry),
						commerceVirtualOrderItemFileEntry.getVersion()));
			}
		}

		return virtualFiles;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceVirtualOrderItemId = ParamUtil.getLong(
			httpServletRequest, "commerceVirtualOrderItemId");

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			_commerceVirtualOrderItemService.fetchCommerceVirtualOrderItem(
				commerceVirtualOrderItemId);

		if (commerceVirtualOrderItem != null) {
			return commerceVirtualOrderItem.
				getCommerceVirtualOrderItemFileEntriesCount();
		}

		return 0;
	}

	private String _getURL(
			CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry)
		throws PortalException {

		if (Validator.isNull(commerceVirtualOrderItemFileEntry.getUrl())) {
			FileEntry fileEntry = _dlAppService.getFileEntry(
				commerceVirtualOrderItemFileEntry.getFileEntryId());

			return DLURLHelperUtil.getDownloadURL(
				fileEntry, fileEntry.getLatestFileVersion(), null,
				StringPool.BLANK, true, true);
		}

		return commerceVirtualOrderItemFileEntry.getUrl();
	}

	@Reference
	private CommerceVirtualOrderItemFileEntryService
		_commerceVirtualOrderItemFileEntryService;

	@Reference
	private CommerceVirtualOrderItemService _commerceVirtualOrderItemService;

	@Reference
	private DLAppService _dlAppService;

}
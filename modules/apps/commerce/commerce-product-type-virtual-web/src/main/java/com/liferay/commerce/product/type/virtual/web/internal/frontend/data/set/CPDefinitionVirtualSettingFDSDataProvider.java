/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.frontend.data.set;

import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CPDefinitionVirtualSettingFDSNames.VIRTUAL_SETTING_FILES,
	service = FDSDataProvider.class
)
public class CPDefinitionVirtualSettingFDSDataProvider
	implements FDSDataProvider<VirtualFile> {

	@Override
	public List<VirtualFile> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<VirtualFile> virtualFiles = new ArrayList<>();

		String className = ParamUtil.getString(httpServletRequest, "className");
		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.fetchCPDefinitionVirtualSetting(
				className, classPK);

		if (cpDefinitionVirtualSetting != null) {
			for (CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry :
					_cpdVirtualSettingFileEntryService.
						getCPDVirtualSettingFileEntries(
							className, classPK,
							cpDefinitionVirtualSetting.
								getCPDefinitionVirtualSettingId(),
							fdsPagination.getStartPosition(),
							fdsPagination.getEndPosition())) {

				virtualFiles.add(
					new VirtualFile(
						cpdVirtualSettingFileEntry.
							getCPDefinitionVirtualSettingFileEntryId(),
						_getURL(cpdVirtualSettingFileEntry),
						cpdVirtualSettingFileEntry.getVersion()));
			}
		}

		return virtualFiles;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		String className = ParamUtil.getString(httpServletRequest, "className");
		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.fetchCPDefinitionVirtualSetting(
				className, classPK);

		if (cpDefinitionVirtualSetting != null) {
			return cpDefinitionVirtualSetting.
				getCPDVirtualSettingFileEntriesCount();
		}

		return 0;
	}

	private String _getURL(
			CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry)
		throws PortalException {

		if (Validator.isNull(cpdVirtualSettingFileEntry.getUrl())) {
			FileEntry fileEntry = _dlAppService.getFileEntry(
				cpdVirtualSettingFileEntry.getFileEntryId());

			return DLURLHelperUtil.getDownloadURL(
				fileEntry, fileEntry.getLatestFileVersion(), null,
				StringPool.BLANK, true, true);
		}

		return cpdVirtualSettingFileEntry.getUrl();
	}

	@Reference
	private CPDefinitionVirtualSettingService
		_cpDefinitionVirtualSettingService;

	@Reference
	private CPDVirtualSettingFileEntryService
		_cpdVirtualSettingFileEntryService;

	@Reference
	private DLAppService _dlAppService;

}
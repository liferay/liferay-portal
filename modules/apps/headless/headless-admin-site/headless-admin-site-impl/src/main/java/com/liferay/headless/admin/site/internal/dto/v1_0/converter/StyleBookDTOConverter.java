/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.admin.site.dto.v1_0.StyleBook;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(
	property = "dto.class.name=com.liferay.style.book.model.StyleBookEntry",
	service = DTOConverter.class
)
public class StyleBookDTOConverter
	implements DTOConverter<StyleBookEntry, StyleBook> {

	@Override
	public String getContentType() {
		return StyleBook.class.getSimpleName();
	}

	@Override
	public StyleBook toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		return toDTO(
			dtoConverterContext,
			_styleBookEntryLocalService.getStyleBookEntry(
				(Long)dtoConverterContext.getId()));
	}

	@Override
	public StyleBook toDTO(
			DTOConverterContext dtoConverterContext,
			StyleBookEntry styleBookEntry)
		throws Exception {

		return new StyleBook() {
			{
				setActions(dtoConverterContext::getActions);
				setCreator(
					() -> {
						User user = _userLocalService.fetchUser(
							styleBookEntry.getUserId());

						if (user == null) {
							return null;
						}

						return new Creator() {
							{
								setExternalReferenceCode(
									user::getExternalReferenceCode);
								setName(user::getFullName);
							}
						};
					});
				setDateCreated(styleBookEntry::getCreateDate);
				setDateModified(
					() -> GetterUtil.getObject(
						styleBookEntry.getModifiedDate(),
						styleBookEntry::getCreateDate));
				setDefaultStyleBook(styleBookEntry::getDefaultStyleBookEntry);
				setExternalReferenceCode(
					styleBookEntry::getExternalReferenceCode);
				setFrontendTokensValues(
					styleBookEntry::getFrontendTokensValues);
				setId(styleBookEntry::getStyleBookEntryId);
				setKey(styleBookEntry::getStyleBookEntryKey);
				setName(styleBookEntry::getName);
				setPreviewFileEntryExternalReferenceCode(
					() -> {
						long previewFileEntryId =
							styleBookEntry.getPreviewFileEntryId();

						if (previewFileEntryId == 0) {
							return null;
						}

						FileEntry fileEntry = _dlAppLocalService.fetchFileEntry(
							previewFileEntryId);

						if (fileEntry == null) {
							return null;
						}

						return fileEntry.getExternalReferenceCode();
					});
				setThemeId(styleBookEntry::getThemeId);
			}
		};
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.internal.dto.v1_0.converter;

import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.digital.signature.rest.dto.v1_0.FileEntryBrief;
import com.liferay.digital.signature.rest.dto.v1_0.SignatureRequest;
import com.liferay.digital.signature.rest.dto.v1_0.SignatureRequestRecipient;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "dto.class.name=com.liferay.digital.signature.model.DSRequest",
	service = DTOConverter.class
)
public class SignatureRequestDTOConverter
	implements DTOConverter<DSRequest, SignatureRequest> {

	@Override
	public String getContentType() {
		return SignatureRequest.class.getSimpleName();
	}

	@Override
	public SignatureRequest toDTO(
			DTOConverterContext dtoConverterContext, DSRequest dsRequest)
		throws Exception {

		return new SignatureRequest() {
			{
				setActions(dtoConverterContext::getActions);
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(dsRequest.getUserId())));
				setDateCreated(dsRequest::getCreateDate);
				setEmailMessage(dsRequest::getEmailMessage);
				setEmailSubject(dsRequest::getEmailSubject);
				setExpirationDate(dsRequest::getExpirationDate);
				setFileEntryBriefs(() -> _getFileEntryBriefs(dsRequest));
				setId(dsRequest::getDSRequestId);
				setSignatureRequestRecipients(
					() -> _getSignatureRequestRecipients(dsRequest));
				setStatus(dsRequest::getStatus);
				setStatusDate(dsRequest::getStatusDate);
			}
		};
	}

	private FileEntryBrief[] _getFileEntryBriefs(DSRequest dsRequest) {
		return TransformUtil.transformToArray(
			dsRequest.getFileEntryIds(),
			fileEntryId -> {
				FileEntry fileEntry = _dlAppLocalService.fetchFileEntry(
					fileEntryId);

				if (fileEntry == null) {
					return null;
				}

				return new FileEntryBrief() {
					{
						setId(fileEntry::getFileEntryId);
						setTitle(fileEntry::getTitle);
					}
				};
			},
			FileEntryBrief.class);
	}

	private SignatureRequestRecipient[] _getSignatureRequestRecipients(
		DSRequest dsRequest) {

		return TransformUtil.transformToArray(
			dsRequest.getDSRequestRecipients(),
			this::_toSignatureRequestRecipient,
			SignatureRequestRecipient.class);
	}

	private SignatureRequestRecipient _toSignatureRequestRecipient(
		DSRequestRecipient dsRequestRecipient) {

		return new SignatureRequestRecipient() {
			{
				setEmailAddress(dsRequestRecipient::getEmailAddress);
				setName(dsRequestRecipient::getName);
				setSentDate(dsRequestRecipient::getSentDate);
				setStatus(dsRequestRecipient::getStatus);
				setStatusDate(dsRequestRecipient::getStatusDate);
				setUserId(dsRequestRecipient::getUserId);
			}
		};
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
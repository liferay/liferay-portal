/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.google.cloud.storage.StorageException;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.customer.exception.FileServerUnavailableException;
import com.liferay.customer.exception.TicketAttachmentNotFoundException;
import com.liferay.customer.model.TicketAttachment;
import com.liferay.customer.service.GoogleCloudStorageService;
import com.liferay.customer.service.TicketAttachmentService;
import com.liferay.petra.function.UnsafeFunction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Amos Fong
 */
@RequestMapping("/ticket-attachments/")
@RestController
public class TicketAttachmentsDownloadRestController
	extends BaseRestController {

	@GetMapping("/by-external-reference-code/{externalReferenceCode}/download")
	public ResponseEntity<String> getByExternalReferenceCodeDownload(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable("externalReferenceCode") String externalReferenceCode) {

		return _getResponseEntity(
			jwt,
			authorization -> _ticketAttachmentService.getTicketAttachment(
				authorization, externalReferenceCode));
	}

	@GetMapping("/by-id/{id}/download")
	public ResponseEntity<String> getByIdDownload(
		@AuthenticationPrincipal Jwt jwt, @PathVariable("id") long id) {

		return _getResponseEntity(
			jwt,
			authorization -> _ticketAttachmentService.getTicketAttachment(
				authorization, id));
	}

	private ResponseEntity<String> _getResponseEntity(
		Jwt jwt,
		UnsafeFunction<String, TicketAttachment, Exception> unsafeFunction) {

		try {
			String authorization = "Bearer " + jwt.getTokenValue();

			TicketAttachment ticketAttachment = unsafeFunction.apply(
				authorization);

			return new ResponseEntity<>(
				_googleCloudStorageService.getDownloadURL(
					ticketAttachment.getGCSBucketName(),
					ticketAttachment.getGCSObjectName()),
				HttpStatus.OK);
		}
		catch (FileServerUnavailableException fileServerUnavailableException) {
			_log.error(
				fileServerUnavailableException, fileServerUnavailableException);

			return new ResponseEntity<>(
				"FILE_SERVER_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (StorageException storageException) {
			_log.error(storageException, storageException);

			if (storageException.getCode() == 404) {
				return new ResponseEntity<>(
					"FILE_NOT_FOUND_IN_STORAGE", HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(
				"FILE_SERVER_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (TicketAttachmentNotFoundException
					ticketAttachmentNotFoundException) {

			_log.error(
				ticketAttachmentNotFoundException,
				ticketAttachmentNotFoundException);

			return new ResponseEntity<>(
				"ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				"UNEXPECTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private static final Log _log = LogFactory.getLog(
		TicketAttachmentsDownloadRestController.class);

	@Autowired
	private GoogleCloudStorageService _googleCloudStorageService;

	@Autowired
	private TicketAttachmentService _ticketAttachmentService;

}
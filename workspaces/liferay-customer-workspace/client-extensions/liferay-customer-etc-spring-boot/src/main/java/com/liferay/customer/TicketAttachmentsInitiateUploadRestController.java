/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.customer.exception.FileServerUnavailableException;
import com.liferay.customer.exception.JiraIssueClosedException;
import com.liferay.customer.exception.JiraIssueNotFoundException;
import com.liferay.customer.exception.JiraOrganizationNotFoundException;
import com.liferay.customer.model.TicketAttachment;
import com.liferay.customer.service.GoogleCloudStorageService;
import com.liferay.customer.service.TicketAttachmentService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Amos Fong
 */
@RequestMapping("/ticket-attachments/initiate-upload")
@RestController
public class TicketAttachmentsInitiateUploadRestController
	extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json,
		@RequestHeader(name = HttpHeaders.ORIGIN) String origin) {

		try {
			JSONObject jsonObject = new JSONObject(json);

			String fileName = jsonObject.getString("fileName");
			String fileSize = jsonObject.getString("fileSize");
			String gcsSessionURL = jsonObject.optString("gcsSessionURL");
			String md5Checksum = jsonObject.optString("md5Checksum");

			String ticketId = jsonObject.getString("ticketId");

			String accountKey = getAccountKey(ticketId);

			TicketAttachment ticketAttachment =
				_ticketAttachmentService.fetchTicketAttachment(
					"Bearer " + jwt.getTokenValue(), fileName, ticketId,
					md5Checksum);

			if (ticketAttachment != null) {
				if (ticketAttachment.isApproved()) {
					return new ResponseEntity<>(
						"ATTACHMENT_ALREADY_EXISTS", HttpStatus.CONFLICT);
				}
			}
			else {
				String externalReferenceCode = jsonObject.optString(
					"externalReferenceCode");
				String type = jsonObject.optString("type");

				ticketAttachment = _ticketAttachmentService.addTicketAttachment(
					"Bearer " + jwt.getTokenValue(), accountKey,
					externalReferenceCode, fileName, fileSize, ticketId,
					md5Checksum, TicketAttachment.STATUS_DRAFT, type);
			}

			JSONObject responseJSONObject = new JSONObject();

			responseJSONObject.put(
				"accountKey", accountKey
			).put(
				"ticketAttachmentId", ticketAttachment.getTicketAttachmentId()
			);

			if (!gcsSessionURL.equals("")) {
				responseJSONObject.put("gcsSessionURL", gcsSessionURL);
			}
			else {
				responseJSONObject.put(
					"gcsSessionURL",
					_googleCloudStorageService.getUploadSessionURL(
						origin, ticketAttachment.getGCSBucketName(),
						ticketAttachment.getGCSObjectName(), fileSize));
			}

			return new ResponseEntity<>(
				responseJSONObject.toString(), HttpStatus.OK);
		}
		catch (FileServerUnavailableException fileServerUnavailableException) {
			_log.error(
				fileServerUnavailableException, fileServerUnavailableException);

			return new ResponseEntity<>(
				"FILE_SERVER_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (HttpClientErrorException.Forbidden httpClientErrorException) {
			_log.error(httpClientErrorException, httpClientErrorException);

			return new ResponseEntity<>(
				"FORBIDDEN_ACCESS", HttpStatus.FORBIDDEN);
		}
		catch (JiraIssueClosedException jiraIssueClosedException) {
			_log.error(jiraIssueClosedException, jiraIssueClosedException);

			return new ResponseEntity<>(
				"TICKET_IS_CLOSED", HttpStatus.BAD_REQUEST);
		}
		catch (JiraIssueNotFoundException jiraIssueNotFoundException) {
			_log.error(jiraIssueNotFoundException, jiraIssueNotFoundException);

			return new ResponseEntity<>(
				"INVALID_TICKET_NUMBER", HttpStatus.NOT_FOUND);
		}
		catch (JiraOrganizationNotFoundException
					jiraOrganizationNotFoundException) {

			_log.error(
				jiraOrganizationNotFoundException,
				jiraOrganizationNotFoundException);

			return new ResponseEntity<>(
				"JIRA_ORGANIZATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				"UNEXPECTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private static final Log _log = LogFactory.getLog(
		TicketAttachmentsInitiateUploadRestController.class);

	@Autowired
	private GoogleCloudStorageService _googleCloudStorageService;

	@Autowired
	private TicketAttachmentService _ticketAttachmentService;

}
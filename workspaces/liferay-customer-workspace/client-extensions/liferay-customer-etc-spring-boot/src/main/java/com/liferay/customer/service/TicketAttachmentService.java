/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.service;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.customer.exception.TicketAttachmentNotFoundException;
import com.liferay.customer.model.TicketAttachment;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Amos Fong
 */
@Component
public class TicketAttachmentService extends BaseService {

	public TicketAttachment addTicketAttachment(
			String authorization, String accountKey,
			String externalReferenceCode, String fileName, String fileSize,
			String jiraIssueKey, String md5Checksum, int statusCode,
			String type)
		throws Exception {

		JSONObject requestJSONObject = new JSONObject();

		requestJSONObject.put(
			"accountKey", accountKey
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"fileName", fileName
		).put(
			"fileSize", fileSize
		).put(
			"gcsBucketName", _gcsBucketName
		).put(
			"jiraIssueKey", jiraIssueKey
		).put(
			"r_accountEntryToTicketAttachment_accountEntryERC", accountKey
		).put(
			"storageProvider", TicketAttachment.STORAGE_PROVIDER_GCS
		).put(
			"type", type
		);

		if (!md5Checksum.equals("")) {
			requestJSONObject.put("md5Checksum", md5Checksum);
		}

		JSONObject statusJSONObject = new JSONObject();

		statusJSONObject.put("code", statusCode);

		requestJSONObject.put("status", statusJSONObject);

		JSONObject jsonObject = new JSONObject(
			post(
				authorization, requestJSONObject.toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/ticketattachments"
				).build(
				).toUri()));

		return new TicketAttachment(jsonObject);
	}

	public TicketAttachment approveTicketAttachment(
			String authorization, long ticketAttachmentId)
		throws Exception {

		JSONObject requestJSONObject = new JSONObject();

		JSONObject statusJSONObject = new JSONObject();

		statusJSONObject.put("code", TicketAttachment.STATUS_APPROVED);

		requestJSONObject.put("status", statusJSONObject);

		JSONObject jsonObject = new JSONObject(
			patch(
				authorization, requestJSONObject.toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/ticketattachments/" + ticketAttachmentId
				).build(
				).toUri()));

		return new TicketAttachment(jsonObject);
	}

	public void deleteTicketAttachment(
			String authorization, long ticketAttachmentId)
		throws Exception {

		delete(
			authorization, "",
			UriComponentsBuilder.fromPath(
				"/o/c/ticketattachments/" + ticketAttachmentId
			).build(
			).toUri());
	}

	public TicketAttachment fetchTicketAttachment(
			String authorization, String fileName, String jiraIssueKey,
			String md5Checksum)
		throws Exception {

		StringBundler sb = new StringBundler(7);

		sb.append("fileName eq '");
		sb.append(fileName);

		if (!md5Checksum.equals("")) {
			sb.append("' and md5Checksum eq '");
			sb.append(md5Checksum);
		}

		sb.append("' and jiraIssueKey eq '");
		sb.append(jiraIssueKey);
		sb.append("'");

		String response = get(
			authorization,
			UriComponentsBuilder.fromPath(
				"/o/c/ticketattachments"
			).queryParam(
				"filter", sb.toString()
			).build(
			).toUri());

		if (Validator.isNull(response)) {
			return null;
		}

		JSONObject jsonObject = new JSONObject(response);

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		if (jsonArray.length() > 0) {
			return new TicketAttachment(jsonArray.getJSONObject(0));
		}

		return null;
	}

	public TicketAttachment getTicketAttachment(
			String authorization, long ticketAttachmentId)
		throws TicketAttachmentNotFoundException {

		try {
			JSONObject jsonObject = new JSONObject(
				get(
					authorization,
					UriComponentsBuilder.fromPath(
						"/o/c/ticketattachments/" + ticketAttachmentId
					).build(
					).toUri()));

			if (jsonObject.isNull("id")) {
				throw new TicketAttachmentNotFoundException();
			}

			return new TicketAttachment(jsonObject);
		}
		catch (HttpClientErrorException.NotFound httpClientErrorException) {
			throw new TicketAttachmentNotFoundException(
				httpClientErrorException);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			throw exception;
		}
	}

	public TicketAttachment getTicketAttachment(
			String authorization, String externalReferenceCode)
		throws TicketAttachmentNotFoundException {

		try {
			JSONObject jsonObject = new JSONObject(
				get(
					authorization,
					UriComponentsBuilder.fromPath(
						"/o/c/ticketattachments/by-external-reference-code/" +
							externalReferenceCode
					).build(
					).toUri()));

			if (jsonObject.isNull("id")) {
				throw new TicketAttachmentNotFoundException();
			}

			return new TicketAttachment(jsonObject);
		}
		catch (HttpClientErrorException.NotFound httpClientErrorException) {
			throw new TicketAttachmentNotFoundException(
				httpClientErrorException);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			throw exception;
		}
	}

	public List<TicketAttachment> search(
			String authorization, String filter, int page, int pageSize)
		throws Exception {

		List<TicketAttachment> ticketAttachments = new ArrayList<>();

		JSONObject jsonObject = new JSONObject(
			get(
				authorization,
				UriComponentsBuilder.fromPath(
					"/o/c/ticketattachments"
				).queryParam(
					"filter", filter
				).queryParam(
					"page", page
				).queryParam(
					"pageSize", pageSize
				).build(
				).toUri()));

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		for (int i = 0; i < jsonArray.length(); i++) {
			ticketAttachments.add(
				new TicketAttachment(jsonArray.getJSONObject(i)));
		}

		return ticketAttachments;
	}

	public TicketAttachment updateTicketAttachmentDraftCommentBody(
			String authorization, long ticketAttachmentId,
			String draftCommentBody)
		throws Exception {

		JSONObject requestJSONObject = new JSONObject();

		requestJSONObject.put("draftCommentBody", draftCommentBody);

		JSONObject jsonObject = new JSONObject(
			patch(
				authorization, requestJSONObject.toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/ticketattachments/" + ticketAttachmentId
				).build(
				).toUri()));

		return new TicketAttachment(jsonObject);
	}

	public TicketAttachment updateTicketAttachmentState(
			String authorization, long ticketAttachmentId, long state)
		throws Exception {

		JSONObject requestJSONObject = new JSONObject();

		requestJSONObject.put("state", state);

		JSONObject jsonObject = new JSONObject(
			patch(
				authorization, requestJSONObject.toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/ticketattachments/" + ticketAttachmentId
				).build(
				).toUri()));

		return new TicketAttachment(jsonObject);
	}

	private static final Log _log = LogFactory.getLog(
		TicketAttachmentService.class);

	@Value("${liferay.customer.gcs.bucket.name}")
	private String _gcsBucketName;

}
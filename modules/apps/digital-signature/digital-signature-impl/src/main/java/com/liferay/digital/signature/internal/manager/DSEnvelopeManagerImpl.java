/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.internal.http.DSHttp;
import com.liferay.digital.signature.mail.DSEnvelopeEmailNotificationSender;
import com.liferay.digital.signature.manager.DSCustomFieldManager;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.model.DSCustomField;
import com.liferay.digital.signature.model.DSDocument;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = DSEnvelopeManager.class)
public class DSEnvelopeManagerImpl implements DSEnvelopeManager {

	@Override
	public DSEnvelope addDSEnvelope(
		long companyId, long groupId, DSEnvelope dsEnvelope) {

		String dsEnvelopeName = dsEnvelope.getName();
		String dsEnvelopeSenderEmailAddress =
			dsEnvelope.getSenderEmailAddress();

		_setDSRecipients(companyId, groupId, dsEnvelope);

		dsEnvelope = _toDSEnvelope(
			_dsHttp.post(
				companyId, groupId, "envelopes", dsEnvelope.toJSONObject()));

		if (Validator.isNull(dsEnvelope.getDSEnvelopeId())) {
			throw new SystemException(
				"DocuSign did not return an envelope ID for the created " +
					"envelope");
		}

		_dsCustomFieldManager.addDSCustomFields(
			companyId, groupId, dsEnvelope.getDSEnvelopeId(),
			new DSCustomField() {
				{
					name = "envelopeName";
					show = true;
					value = dsEnvelopeName;
				}
			},
			new DSCustomField() {
				{
					name = "envelopeSenderEmailAddress";
					show = true;
					value = dsEnvelopeSenderEmailAddress;
				}
			});

		dsEnvelope = getDSEnvelope(
			companyId, groupId, dsEnvelope.getDSEnvelopeId(),
			"custom_fields,recipients");

		_sendDSRecipientEmails(companyId, groupId, dsEnvelope);

		return dsEnvelope;
	}

	@Override
	public void deleteDSEnvelopes(
			long companyId, long groupId, String... dsEnvelopeIds)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) ||
			!permissionChecker.isCompanyAdmin(companyId)) {

			throw new PrincipalException.MustBeCompanyAdmin(permissionChecker);
		}

		_dsHttp.put(
			companyId, groupId, "folders/recyclebin",
			JSONUtil.put(
				"envelopeIds",
				JSONUtil.toJSONArray(
					dsEnvelopeIds, dsEnvelopeId -> dsEnvelopeId, _log)));
	}

	@Override
	public DSEnvelope getDSEnvelope(
		long companyId, long groupId, String dsEnvelopeId) {

		return getDSEnvelope(
			companyId, groupId, dsEnvelopeId,
			"custom_fields,documents,recipients");
	}

	@Override
	public DSEnvelope getDSEnvelope(
		long companyId, long groupId, String dsEnvelopeId, String include) {

		JSONObject jsonObject = _dsHttp.get(
			companyId, groupId,
			StringBundler.concat(
				"envelopes/", dsEnvelopeId, "?include=", include));

		return _toDSEnvelope(jsonObject);
	}

	@Override
	public Page<DSEnvelope> getDSEnvelopesPage(
		long companyId, long groupId, String fromDateString,
		boolean includeDocuments, String keywords, String order,
		Pagination pagination, String status) {

		Matcher matcher = _pattern.matcher(GetterUtil.get(keywords, ""));

		if (matcher.matches()) {
			DSEnvelope dsEnvelope = getDSEnvelope(companyId, groupId, keywords);

			if (Validator.isNull(dsEnvelope.getDSEnvelopeId())) {
				return Page.of(Collections.emptyList(), pagination, 0);
			}

			return Page.of(
				Collections.singletonList(dsEnvelope), pagination, 1);
		}

		String location = StringBundler.concat(
			"envelopes?count=", pagination.getPageSize(), "&from_date=",
			GetterUtil.get(fromDateString, "2000-01-01"),
			"&folder_types=sentitems&start_position=",
			pagination.getStartPosition(), "&include=",
			includeDocuments ? "custom_fields,documents,recipients" :
				"custom_fields,recipients",
			"&order=", GetterUtil.get(order, ""));

		if (!Validator.isBlank(keywords)) {
			location += "&search_text=" + keywords;
		}

		if (!Validator.isBlank(status)) {
			location += "&status=" + status;
		}

		JSONObject jsonObject = _dsHttp.get(companyId, groupId, location);

		return Page.of(
			JSONUtil.toList(
				jsonObject.getJSONArray("envelopes"),
				envelopeJSONObject -> _toDSEnvelope(envelopeJSONObject), _log),
			pagination, jsonObject.getInt("totalSetSize"));
	}

	@Override
	public byte[] getSignedDocument(
		long companyId, long groupId, String dsEnvelopeId) {

		return _dsHttp.getAsBytes(
			companyId, groupId,
			StringBundler.concat(
				"envelopes/", dsEnvelopeId, "/documents/combined"));
	}

	@Override
	public void voidDSEnvelope(
		long companyId, long groupId, String dsEnvelopeId, String reason) {

		_dsHttp.put(
			companyId, groupId, "envelopes/" + dsEnvelopeId,
			JSONUtil.put(
				"status", "voided"
			).put(
				"voidedReason", reason
			));
	}

	private List<DSDocument> _getDSDocuments(JSONArray jsonArray) {
		return JSONUtil.toList(
			jsonArray,
			jsonObject -> new DSDocument() {
				{
					dsDocumentId = jsonObject.getString("documentId");
					fileExtension = jsonObject.getString("fileExtension");
					name = jsonObject.getString("name");
					uri = jsonObject.getString("uri");
				}
			},
			_log);
	}

	private List<DSRecipient> _getDSRecipients(JSONObject jsonObject) {
		if (jsonObject == null) {
			return Collections.emptyList();
		}

		return JSONUtil.toList(
			jsonObject.getJSONArray("signers"),
			signerJSONObject -> new DSRecipient() {
				{
					dsClientUserId = signerJSONObject.getString("clientUserId");
					dsRecipientId = signerJSONObject.getString("recipientId");
					emailAddress = signerJSONObject.getString("email");
					name = signerJSONObject.getString("name");
					sentLocalDateTime = _toLocalDateTime(
						signerJSONObject.getString("sentDateTime"));
					status = signerJSONObject.getString("status");
					statusLocalDateTime = _getStatusLocalDateTime(
						signerJSONObject);
					tabsJSONObject = signerJSONObject.getJSONObject("tabs");
				}
			},
			_log);
	}

	private LocalDateTime _getStatusLocalDateTime(JSONObject signerJSONObject) {
		String signedDateTime = signerJSONObject.getString("signedDateTime");

		if (Validator.isNotNull(signedDateTime)) {
			return _toLocalDateTime(signedDateTime);
		}

		return _toLocalDateTime(signerJSONObject.getString("declinedDateTime"));
	}

	private void _sendDSRecipientEmails(
		long companyId, long groupId, DSEnvelope dsEnvelope) {

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				companyId, groupId);

		if (!digitalSignatureConfiguration.enabled() ||
			!digitalSignatureConfiguration.enableEmbeddedView() ||
			!Objects.equals(dsEnvelope.getStatus(), "sent")) {

			return;
		}

		List<DSRecipient> dsRecipients = dsEnvelope.getDSRecipients();

		for (DSRecipient dsRecipient : dsRecipients) {
			if (Validator.isNotNull(dsRecipient.getDSClientUserId())) {
				_dsEnvelopeEmailNotificationSender.sendNotification(
					companyId, groupId, dsEnvelope.getDSEnvelopeId(),
					dsRecipient, dsEnvelope.getEmailSubject(),
					dsEnvelope.getEmailBlurb());
			}
		}
	}

	private void _setDSEnvelopeCustomField(
		DSEnvelope dsEnvelope, JSONObject jsonObject) {

		String name = jsonObject.getString("name");
		String value = jsonObject.getString("value");

		if (Objects.equals(name, "envelopeName")) {
			dsEnvelope.setName(value);
		}
		else if (Objects.equals(name, "envelopeSenderEmailAddress")) {
			dsEnvelope.setSenderEmailAddress(value);
		}
	}

	private void _setDSEnvelopeCustomFields(
		DSEnvelope dsEnvelope, JSONObject jsonObject) {

		if (jsonObject == null) {
			return;
		}

		JSONArray jsonArray = jsonObject.getJSONArray("textCustomFields");

		jsonArray.forEach(
			element -> _setDSEnvelopeCustomField(
				dsEnvelope, (JSONObject)element));
	}

	private void _setDSRecipients(
		long companyId, long groupId, DSEnvelope dsEnvelope) {

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				companyId, groupId);

		if (!digitalSignatureConfiguration.enabled() ||
			!digitalSignatureConfiguration.enableEmbeddedView()) {

			return;
		}

		List<DSRecipient> dsRecipients = dsEnvelope.getDSRecipients();

		for (DSRecipient dsRecipient : dsRecipients) {
			User user = _userLocalService.fetchUserByEmailAddress(
				companyId, dsRecipient.getEmailAddress());

			if (user != null) {
				dsRecipient.setDSClientUserId(String.valueOf(user.getUserId()));
				dsRecipient.setName(user.getFullName());
			}
		}
	}

	private DSEnvelope _toDSEnvelope(JSONObject jsonObject) {
		if (jsonObject == null) {
			return new DSEnvelope();
		}

		DSEnvelope dsEnvelope = new DSEnvelope() {
			{
				createdLocalDateTime = _toLocalDateTime(
					jsonObject.getString("createdDateTime"));
				dsDocuments = _getDSDocuments(
					jsonObject.getJSONArray("envelopeDocuments"));
				dsEnvelopeId = jsonObject.getString("envelopeId");
				dsRecipients = _getDSRecipients(
					jsonObject.getJSONObject("recipients"));
				emailBlurb = jsonObject.getString("emailBlurb");
				emailSubject = jsonObject.getString("emailSubject");
				expireLocalDateTime = _toLocalDateTime(
					jsonObject.getString("expireDateTime"));
				status = jsonObject.getString("status");
				statusChangedLocalDateTime = _toLocalDateTime(
					jsonObject.getString("statusChangedDateTime"));
			}
		};

		_setDSEnvelopeCustomFields(
			dsEnvelope, jsonObject.getJSONObject("customFields"));

		return dsEnvelope;
	}

	private LocalDateTime _toLocalDateTime(String localDateTimeString) {
		if (Validator.isNull(localDateTimeString)) {
			return null;
		}

		try {
			return LocalDateTime.parse(
				localDateTimeString,
				DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX"));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid local date time " + localDateTimeString,
					exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DSEnvelopeManagerImpl.class);

	private static final Pattern _pattern = Pattern.compile(
		"[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}" +
			"\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}");

	@Reference
	private DSCustomFieldManager _dsCustomFieldManager;

	@Reference
	private DSEnvelopeEmailNotificationSender
		_dsEnvelopeEmailNotificationSender;

	@Reference
	private DSHttp _dsHttp;

	@Reference
	private UserLocalService _userLocalService;

}
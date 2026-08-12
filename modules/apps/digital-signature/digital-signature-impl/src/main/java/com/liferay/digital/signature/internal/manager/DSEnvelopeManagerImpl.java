/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.internal.http.DSHttp;
import com.liferay.digital.signature.manager.DSCustomFieldManager;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.model.DSCustomField;
import com.liferay.digital.signature.model.DSDocument;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
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

		dsEnvelope = _toDSEnvelope(
			_dsHttp.post(
				companyId, groupId, "envelopes", dsEnvelope.toJSONObject()));

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

		return getDSEnvelope(
			companyId, groupId, dsEnvelope.getDSEnvelopeId(),
			"custom_fields,recipients");
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
					dsRecipientId = signerJSONObject.getString("recipientId");
					emailAddress = signerJSONObject.getString("email");
					name = signerJSONObject.getString("name");
					status = signerJSONObject.getString("status");
					tabsJSONObject = signerJSONObject.getJSONObject("tabs");
				}
			},
			_log);
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
				status = jsonObject.getString("status");
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
	private DSHttp _dsHttp;

}
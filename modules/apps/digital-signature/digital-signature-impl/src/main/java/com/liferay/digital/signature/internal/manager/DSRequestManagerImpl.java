/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.constants.DSObjectDefinitionConstants;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.Date;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(service = DSRequestManager.class)
public class DSRequestManagerImpl implements DSRequestManager {

	@Override
	public void addDSRequest(
			long companyId, long groupId, long userId, DSEnvelope dsEnvelope,
			long[] fileEntryIds)
		throws PortalException {

		if (!_isEnabled(companyId, groupId)) {
			return;
		}

		_addDSRequest(companyId, groupId, userId, dsEnvelope, fileEntryIds);
	}

	@Override
	public void updateDSRequest(long dsRequestId) throws PortalException {
		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			dsRequestId);

		long companyId = objectEntry.getCompanyId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					companyId);

		if (objectDefinition.getObjectDefinitionId() !=
				objectEntry.getObjectDefinitionId()) {

			throw new NoSuchObjectEntryException(
				StringBundler.concat(
					"No ObjectEntry exists with the key {objectDefinitionId=",
					objectDefinition.getObjectDefinitionId(),
					", objectEntryId=", dsRequestId, "}"));
		}

		long groupId = MapUtil.getLong(objectEntry.getValues(), "siteGroupId");

		if (!_isEnabled(companyId, groupId)) {
			return;
		}

		try {
			DSEnvelope dsEnvelope = _dsEnvelopeManager.getDSEnvelope(
				companyId, groupId, objectEntry.getExternalReferenceCode());

			_objectEntryLocalService.partialUpdateObjectEntry(
				objectEntry.getUserId(), dsRequestId,
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					"requestExpirationDate",
					() -> _toDate(dsEnvelope.getExpirationLocalDateTime())
				).put(
					"requestStatus", () -> _getDSRequestStatus(dsEnvelope)
				).put(
					"requestStatusDate",
					() -> _toDate(dsEnvelope.getStatusLocalDateTime())
				).build(),
				_getServiceContext(
					companyId, groupId, objectEntry.getUserId()));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to update digital signature request " + dsRequestId,
				exception);
		}
	}

	private void _addDSRequest(
			long companyId, long groupId, long userId, DSEnvelope dsEnvelope,
			long[] fileEntryIds)
		throws PortalException {

		if (Validator.isNull(dsEnvelope.getDSEnvelopeId())) {
			throw new PortalException(
				"A digital signature request requires a digital signature " +
					"envelope ID");
		}

		ObjectDefinition dsRequestObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					companyId);

		ServiceContext serviceContext = _getServiceContext(
			companyId, groupId, userId);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, userId, dsRequestObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"emailSubject", dsEnvelope.getEmailSubject()
			).put(
				"externalReferenceCode", dsEnvelope.getDSEnvelopeId()
			).put(
				"requestExpirationDate",
				() -> _toDate(dsEnvelope.getExpirationLocalDateTime())
			).put(
				"requestStatus", () -> _getDSRequestStatus(dsEnvelope)
			).put(
				"siteGroupId", _getSiteGroupId(groupId)
			).build(),
			serviceContext);

		ObjectDefinition dsRequestDocumentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_DOCUMENT,
					companyId);

		for (long fileEntryId : fileEntryIds) {
			_objectEntryLocalService.addObjectEntry(
				0, userId,
				dsRequestDocumentObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"fileEntryId", fileEntryId
				).put(
					"r_dsRequestToDSRequestDocuments_l_dsRequestId",
					objectEntry.getObjectEntryId()
				).build(),
				serviceContext);
		}

		ObjectDefinition dsRequestRecipientObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_RECIPIENT,
					companyId);

		for (DSRecipient dsRecipient : dsEnvelope.getDSRecipients()) {
			_objectEntryLocalService.addObjectEntry(
				0, userId,
				dsRequestRecipientObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"emailAddress", dsRecipient.getEmailAddress()
				).put(
					"name", dsRecipient.getName()
				).put(
					"r_dsRequestToDSRequestRecipients_l_dsRequestId",
					objectEntry.getObjectEntryId()
				).put(
					"r_userToDSRequestRecipients_userId",
					_getUserId(companyId, dsRecipient.getEmailAddress())
				).put(
					"requestRecipientStatus",
					() -> _getDSRequestRecipientStatus(dsRecipient)
				).put(
					"sentDate",
					() -> _toDate(dsRecipient.getSentLocalDateTime())
				).build(),
				serviceContext);
		}
	}

	private String _getDSRequestRecipientStatus(DSRecipient dsRecipient) {
		String status = StringUtil.toLowerCase(dsRecipient.getStatus());

		if (Objects.equals(status, "delivered")) {
			return DSRequestConstants.STATUS_SENT;
		}

		if (ArrayUtil.contains(
				DSRequestConstants.STATUSES_REQUEST_RECIPIENT, status)) {

			return status;
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Unsupported digital signature recipient status \"", status,
					"\""));
		}

		return null;
	}

	private String _getDSRequestStatus(DSEnvelope dsEnvelope) {
		LocalDateTime expirationLocalDateTime =
			dsEnvelope.getExpirationLocalDateTime();
		String status = StringUtil.toLowerCase(dsEnvelope.getStatus());
		LocalDateTime statusLocalDateTime = dsEnvelope.getStatusLocalDateTime();

		if (Objects.equals(status, DSRequestConstants.STATUS_VOIDED) &&
			(expirationLocalDateTime != null) &&
			(statusLocalDateTime != null) &&
			!statusLocalDateTime.isBefore(expirationLocalDateTime)) {

			return DSRequestConstants.STATUS_EXPIRED;
		}

		if (Objects.equals(status, "delivered") ||
			Objects.equals(status, DSRequestConstants.STATUS_SIGNED)) {

			return DSRequestConstants.STATUS_SENT;
		}

		if (ArrayUtil.contains(DSRequestConstants.STATUSES_REQUEST, status)) {
			return status;
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Unsupported digital signature envelope status \"", status,
					"\""));
		}

		return null;
	}

	private ServiceContext _getServiceContext(
		long companyId, long groupId, long userId) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setUserId(userId);

		return serviceContext;
	}

	private long _getSiteGroupId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if ((group == null) || !group.isSite()) {
			return 0;
		}

		return groupId;
	}

	private long _getUserId(long companyId, String emailAddress) {
		if (Validator.isNull(emailAddress)) {
			return 0;
		}

		User user = _userLocalService.fetchUserByEmailAddress(
			companyId, emailAddress);

		if (user == null) {
			return 0;
		}

		return user.getUserId();
	}

	private boolean _isEnabled(long companyId, long groupId) {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-69290")) {
			return false;
		}

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				companyId, groupId);

		return digitalSignatureConfiguration.enabled();
	}

	private Date _toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DSRequestManagerImpl.class);

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
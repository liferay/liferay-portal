/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.request;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.request.DSRequestManager;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Kim
 */
@Component(service = DSRequestManager.class)
public class DSRequestManagerImpl implements DSRequestManager {

	@Override
	public void addDSRequest(
		long companyId, long groupId, long userId, DSEnvelope dsEnvelope,
		long[] fileEntryIds) {

		if (!_isEnabled(companyId, groupId) || (dsEnvelope == null) ||
			(fileEntryIds == null)) {

			return;
		}

		ObjectDefinition documentObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST_DOCUMENT");
		ObjectDefinition recipientObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST_RECIPIENT");
		ObjectDefinition requestObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST");

		if ((documentObjectDefinition == null) ||
			(recipientObjectDefinition == null) ||
			(requestObjectDefinition == null)) {

			return;
		}

		try {
			String documentFieldName = _getRelationshipFieldName(
				requestObjectDefinition, "dsRequestToDSRequestDocuments");
			String recipientFieldName = _getRelationshipFieldName(
				requestObjectDefinition, "dsRequestToDSRequestRecipients");

			if ((documentFieldName == null) || (recipientFieldName == null)) {
				return;
			}

			ServiceContext serviceContext = _createServiceContext(
				companyId, groupId, userId);

			String languageId = LocaleUtil.toLanguageId(
				LocaleUtil.getSiteDefault());

			ObjectEntry requestObjectEntry =
				_objectEntryLocalService.addObjectEntry(
					0, userId, requestObjectDefinition.getObjectDefinitionId(),
					0, languageId,
					HashMapBuilder.<String, Serializable>put(
						"emailSubject", dsEnvelope.getEmailSubject()
					).put(
						"expirationDateTime",
						_toDate(dsEnvelope.getExpireLocalDateTime())
					).put(
						"providerKey", _PROVIDER_KEY
					).put(
						"providerRequestId", dsEnvelope.getDSEnvelopeId()
					).put(
						"requestStatus",
						_toRequestStatus(dsEnvelope.getStatus())
					).build(),
					serviceContext);

			for (long fileEntryId : fileEntryIds) {
				_objectEntryLocalService.addObjectEntry(
					0, userId, documentObjectDefinition.getObjectDefinitionId(),
					0, languageId,
					HashMapBuilder.<String, Serializable>put(
						documentFieldName, requestObjectEntry.getObjectEntryId()
					).put(
						"fileEntryId", fileEntryId
					).build(),
					serviceContext);
			}

			for (DSRecipient dsRecipient : dsEnvelope.getDSRecipients()) {
				_objectEntryLocalService.addObjectEntry(
					0, userId,
					recipientObjectDefinition.getObjectDefinitionId(), 0,
					languageId,
					HashMapBuilder.<String, Serializable>put(
						recipientFieldName,
						requestObjectEntry.getObjectEntryId()
					).put(
						"emailAddress", dsRecipient.getEmailAddress()
					).put(
						"name", dsRecipient.getName()
					).put(
						"providerRecipientId", dsRecipient.getDSRecipientId()
					).put(
						"r_userToDSRequestRecipient_userId",
						_getRecipientUserId(
							companyId, dsRecipient.getEmailAddress())
					).put(
						"requestRecipientStatus",
						_toRecipientStatus(dsRecipient.getStatus())
					).put(
						"sentDate", _toDate(dsRecipient.getSentLocalDateTime())
					).build(),
					serviceContext);
			}

			for (long fileEntryId : fileEntryIds) {
				_reindexFileEntry(fileEntryId);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to record the signature request for envelope " +
					dsEnvelope.getDSEnvelopeId(),
				exception);
		}
	}

	@Override
	public Map<Long, String> getRequestStatusesByFileEntryId(
		long companyId, Collection<Long> fileEntryIds) {

		Map<Long, String> requestStatusesByFileEntryId = new HashMap<>();

		if (!_isEnabled(companyId, 0) || (fileEntryIds == null) ||
			fileEntryIds.isEmpty()) {

			return requestStatusesByFileEntryId;
		}

		ObjectDefinition documentObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST_DOCUMENT");
		ObjectDefinition requestObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST");

		if ((documentObjectDefinition == null) ||
			(requestObjectDefinition == null)) {

			return requestStatusesByFileEntryId;
		}

		try {
			Map<Long, Long> requestIdsByFileEntryId =
				_getRequestIdsByFileEntryId(
					companyId, documentObjectDefinition,
					requestObjectDefinition, fileEntryIds);

			for (Map.Entry<Long, Long> entry :
					requestIdsByFileEntryId.entrySet()) {

				ObjectEntry requestObjectEntry =
					_objectEntryLocalService.fetchObjectEntry(entry.getValue());

				if (requestObjectEntry == null) {
					continue;
				}

				Map<String, Serializable> requestValues =
					requestObjectEntry.getValues();

				requestStatusesByFileEntryId.put(
					entry.getKey(),
					GetterUtil.getString(requestValues.get("requestStatus")));
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to load signature request statuses for company " +
					companyId,
				exception);
		}

		return requestStatusesByFileEntryId;
	}

	@Override
	public void updateDSRequest(
		long companyId, long groupId, String providerRequestId) {

		if (!_isEnabled(companyId, groupId) ||
			Validator.isNull(providerRequestId)) {

			return;
		}

		ObjectDefinition documentObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST_DOCUMENT");
		ObjectDefinition recipientObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST_RECIPIENT");
		ObjectDefinition requestObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST");

		if ((documentObjectDefinition == null) ||
			(recipientObjectDefinition == null) ||
			(requestObjectDefinition == null)) {

			return;
		}

		try {
			DSEnvelope dsEnvelope = _dsEnvelopeManager.getDSEnvelope(
				companyId, groupId, providerRequestId);

			if (dsEnvelope == null) {
				return;
			}

			String fieldName = _getRelationshipFieldName(
				requestObjectDefinition, "dsRequestToDSRequestRecipients");

			if (fieldName == null) {
				return;
			}

			Map<String, DSRecipient> dsRecipients = new HashMap<>();

			for (DSRecipient dsRecipient : dsEnvelope.getDSRecipients()) {
				dsRecipients.put(dsRecipient.getDSRecipientId(), dsRecipient);
			}

			String requestStatus = _toRequestStatus(dsEnvelope.getStatus());

			for (Map<String, Serializable> requestValues :
					_getValuesList(
						companyId, requestObjectDefinition,
						StringBundler.concat(
							"(providerRequestId eq '", providerRequestId, "')"),
						null)) {

				long requestId = GetterUtil.getLong(
					requestValues.get(
						requestObjectDefinition.getPKObjectFieldName()));

				_updateRequestStatus(
					companyId, groupId, requestId, dsEnvelope, requestStatus);

				_updateRecipientStatuses(
					companyId, groupId, recipientObjectDefinition, fieldName,
					requestId, dsRecipients);

				_reindexRequestDocuments(
					companyId, documentObjectDefinition,
					requestObjectDefinition, requestId);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to sync the signature request for envelope " +
					providerRequestId,
				exception);
		}
	}

	private ServiceContext _createServiceContext(
		long companyId, long groupId, long userId) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setUserId(userId);

		return serviceContext;
	}

	private ObjectDefinition _fetchObjectDefinition(
		long companyId, String externalReferenceCode) {

		return _objectDefinitionLocalService.
			fetchObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	private long _getRecipientUserId(long companyId, String emailAddress) {
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

	private String _getRelationshipFieldName(
			ObjectDefinition requestObjectDefinition, String relationshipName)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				requestObjectDefinition.getObjectDefinitionId(),
				relationshipName);

		if (objectRelationship == null) {
			return null;
		}

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		return objectField.getName();
	}

	private Map<Long, Long> _getRequestIdsByFileEntryId(
			long companyId, ObjectDefinition documentObjectDefinition,
			ObjectDefinition requestObjectDefinition,
			Collection<Long> fileEntryIds)
		throws Exception {

		Map<Long, Long> requestIdsByFileEntryId = new HashMap<>();

		String documentFieldName = _getRelationshipFieldName(
			requestObjectDefinition, "dsRequestToDSRequestDocuments");

		if (documentFieldName == null) {
			return requestIdsByFileEntryId;
		}

		for (Map<String, Serializable> documentValues :
				_getValuesList(
					companyId, documentObjectDefinition,
					StringBundler.concat(
						"(fileEntryId in (",
						StringUtil.merge(fileEntryIds, ", "), "))"),
					new Sort[] {
						new Sort(Field.CREATE_DATE, Sort.LONG_TYPE, true)
					})) {

			requestIdsByFileEntryId.putIfAbsent(
				GetterUtil.getLong(documentValues.get("fileEntryId")),
				GetterUtil.getLong(documentValues.get(documentFieldName)));
		}

		return requestIdsByFileEntryId;
	}

	private List<Map<String, Serializable>> _getValuesList(
			long companyId, ObjectDefinition objectDefinition,
			String filterString, Sort[] sorts)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(null);

			return _objectEntryLocalService.getValuesList(
				0, companyId, objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create(filterString, objectDefinition), null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private boolean _isEnabled(long companyId, long groupId) {
		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				companyId, groupId);

		if (digitalSignatureConfiguration == null) {
			return false;
		}

		if (digitalSignatureConfiguration.enabled() &&
			digitalSignatureConfiguration.enableEmbeddedView()) {

			return true;
		}

		return false;
	}

	private void _putIfNotNull(
		Map<String, Serializable> values, String name, Serializable value) {

		if (value != null) {
			values.put(name, value);
		}
	}

	private void _reindexFileEntry(long fileEntryId) {
		try {
			Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				"com.liferay.document.library.kernel.model.DLFileEntry");

			indexer.reindex(
				"com.liferay.document.library.kernel.model.DLFileEntry",
				fileEntryId);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to reindex file entry " + fileEntryId, exception);
		}
	}

	private void _reindexRequestDocuments(
			long companyId, ObjectDefinition documentObjectDefinition,
			ObjectDefinition requestObjectDefinition, long requestId)
		throws Exception {

		String documentFieldName = _getRelationshipFieldName(
			requestObjectDefinition, "dsRequestToDSRequestDocuments");

		if (documentFieldName == null) {
			return;
		}

		for (Map<String, Serializable> documentValues :
				_getValuesList(
					companyId, documentObjectDefinition,
					StringBundler.concat(
						"(", documentFieldName, " eq '", requestId, "')"),
					null)) {

			_reindexFileEntry(
				GetterUtil.getLong(documentValues.get("fileEntryId")));
		}
	}

	private Date _toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	private Date _toDate(Serializable value) {
		if (value instanceof Date) {
			return (Date)value;
		}

		return null;
	}

	private String _toRecipientStatus(String status) {
		status = StringUtil.toLowerCase(GetterUtil.getString(status));

		if (ArrayUtil.contains(_DS_RECIPIENT_STATUSES, status)) {
			return status;
		}

		return "sent";
	}

	private String _toRequestStatus(String status) {
		status = StringUtil.toLowerCase(GetterUtil.getString(status));

		if (ArrayUtil.contains(_DS_ENVELOPE_STATUSES, status)) {
			return status;
		}

		return "sent";
	}

	private void _updateRecipientStatuses(
			long companyId, long groupId,
			ObjectDefinition recipientObjectDefinition, String fieldName,
			long requestId, Map<String, DSRecipient> dsRecipients)
		throws Exception {

		for (Map<String, Serializable> recipientValues :
				_getValuesList(
					companyId, recipientObjectDefinition,
					StringBundler.concat(
						"(", fieldName, " eq '", requestId, "')"),
					null)) {

			DSRecipient dsRecipient = dsRecipients.get(
				GetterUtil.getString(
					recipientValues.get("providerRecipientId")));

			if (dsRecipient == null) {
				continue;
			}

			long recipientId = GetterUtil.getLong(
				recipientValues.get(
					recipientObjectDefinition.getPKObjectFieldName()));

			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				recipientId);

			if (objectEntry == null) {
				continue;
			}

			Map<String, Serializable> values =
				HashMapBuilder.<String, Serializable>putAll(
					objectEntry.getValues()
				).put(
					"requestRecipientStatus",
					_toRecipientStatus(dsRecipient.getStatus())
				).build();

			_putIfNotNull(
				values, "sentDate",
				_toDate(dsRecipient.getSentLocalDateTime()));
			_putIfNotNull(
				values, "statusDateTime",
				_toDate(dsRecipient.getStatusLocalDateTime()));

			_objectEntryLocalService.updateObjectEntry(
				objectEntry.getUserId(), recipientId, 0, values,
				_createServiceContext(
					companyId, groupId, objectEntry.getUserId()));
		}
	}

	private void _updateRequestStatus(
			long companyId, long groupId, long requestId, DSEnvelope dsEnvelope,
			String requestStatus)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			requestId);

		if (objectEntry == null) {
			return;
		}

		Map<String, Serializable> values =
			HashMapBuilder.<String, Serializable>putAll(
				objectEntry.getValues()
			).put(
				"requestStatus", requestStatus
			).build();

		_putIfNotNull(
			values, "expirationDateTime",
			_toDate(dsEnvelope.getExpireLocalDateTime()));
		_putIfNotNull(
			values, "statusDateTime",
			_toDate(dsEnvelope.getStatusChangedLocalDateTime()));

		_objectEntryLocalService.updateObjectEntry(
			objectEntry.getUserId(), requestId, 0, values,
			_createServiceContext(companyId, groupId, objectEntry.getUserId()));
	}

	private static final String[] _DS_ENVELOPE_STATUSES = {
		"completed", "created", "declined", "sent", "voided"
	};

	private static final String[] _DS_RECIPIENT_STATUSES = {
		"completed", "created", "declined", "sent", "signed"
	};

	private static final String _PROVIDER_KEY = "docusign";

	private static final Log _log = LogFactoryUtil.getLog(
		DSRequestManagerImpl.class);

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
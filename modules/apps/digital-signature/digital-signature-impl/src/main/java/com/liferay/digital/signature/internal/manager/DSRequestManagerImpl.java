/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_addDSRequest(
						companyId, groupId, userId, dsEnvelope, fileEntryIds);

					return null;
				});
		}
		catch (Throwable throwable) {
			if (throwable instanceof PortalException) {
				throw (PortalException)throwable;
			}

			throw new PortalException(
				"Unable to record the signature request for envelope " +
					dsEnvelope.getDSEnvelopeId(),
				throwable);
		}
	}

	@Override
	public void updateDSRequest(long dsRequestId) throws PortalException {
		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			dsRequestId);

		long companyId = objectEntry.getCompanyId();

		ObjectDefinition documentObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST_DOCUMENT");
		ObjectDefinition requestObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST", companyId);

		if (requestObjectDefinition.getObjectDefinitionId() !=
				objectEntry.getObjectDefinitionId()) {

			throw new NoSuchObjectEntryException(
				"No signature request exists with the primary key " +
					dsRequestId);
		}

		if (documentObjectDefinition == null) {
			return;
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
					() -> _toDate(dsEnvelope.getExpireLocalDateTime())
				).put(
					"requestStatus", _getDSRequestStatus(dsEnvelope)
				).put(
					"requestStatusDate",
					() -> _toDate(dsEnvelope.getStatusChangedLocalDateTime())
				).build(),
				_getServiceContext(
					companyId, groupId, objectEntry.getUserId()));

			_reindexRequestDocuments(
				companyId, documentObjectDefinition, dsRequestId);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to update signature request " + dsRequestId, exception);
		}
	}

	private void _addDSRequest(
			long companyId, long groupId, long userId, DSEnvelope dsEnvelope,
			long[] fileEntryIds)
		throws Exception {

		if (Validator.isNull(dsEnvelope.getDSEnvelopeId())) {
			throw new PortalException(
				"A signature request requires a DocuSign envelope ID");
		}

		ObjectDefinition documentObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST_DOCUMENT");
		ObjectDefinition recipientObjectDefinition = _fetchObjectDefinition(
			companyId, "L_DS_REQUEST_RECIPIENT");
		ObjectDefinition requestObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST", companyId);

		if ((documentObjectDefinition == null) ||
			(recipientObjectDefinition == null) ||
			(requestObjectDefinition == null)) {

			return;
		}

		ServiceContext serviceContext = _getServiceContext(
			companyId, groupId, userId);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, userId, requestObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"emailSubject", dsEnvelope.getEmailSubject()
			).put(
				"externalReferenceCode", dsEnvelope.getDSEnvelopeId()
			).put(
				"requestExpirationDate",
				_toDate(dsEnvelope.getExpireLocalDateTime())
			).put(
				"requestStatus", _getDSRequestStatus(dsEnvelope)
			).put(
				"siteGroupId", _getSiteGroupId(groupId)
			).build(),
			serviceContext);

		for (long fileEntryId : fileEntryIds) {
			_objectEntryLocalService.addObjectEntry(
				0, userId, documentObjectDefinition.getObjectDefinitionId(),
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

		for (DSRecipient dsRecipient : dsEnvelope.getDSRecipients()) {
			_objectEntryLocalService.addObjectEntry(
				0, userId,
				recipientObjectDefinition.getObjectDefinitionId(),
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
					_getDSRequestRecipientStatus(dsRecipient)
				).put(
					"sentDate", _toDate(dsRecipient.getSentLocalDateTime())
				).build(),
				serviceContext);
		}

		for (long fileEntryId : fileEntryIds) {
			_reindexFileEntry(fileEntryId);
		}
	}

	private ObjectDefinition _fetchObjectDefinition(
		long companyId, String externalReferenceCode) {

		return _objectDefinitionLocalService.
			fetchObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	private String _getDSRequestRecipientStatus(DSRecipient dsRecipient) {
		String status = StringUtil.toLowerCase(dsRecipient.getStatus());

		if (ArrayUtil.contains(_REQUEST_RECIPIENT_STATUSES, status)) {
			return status;
		}

		return "sent";
	}

	private String _getDSRequestStatus(DSEnvelope dsEnvelope) {
		String status = StringUtil.toLowerCase(dsEnvelope.getStatus());

		if (ArrayUtil.contains(_REQUEST_STATUSES, status)) {
			return status;
		}

		return "sent";
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

	private List<Map<String, Serializable>> _getValuesList(
			long companyId, String filterString,
			ObjectDefinition objectDefinition)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(null);

			return _objectEntryLocalService.getValuesList(
				0, companyId, objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create(filterString, objectDefinition), null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
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

	private void _reindexFileEntry(long fileEntryId) {
		try {
			Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				DLFileEntry.class);

			indexer.reindex(DLFileEntry.class.getName(), fileEntryId);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to reindex file entry " + fileEntryId, exception);
		}
	}

	private void _reindexRequestDocuments(
			long companyId, ObjectDefinition objectDefinition, long dsRequestId)
		throws Exception {

		for (Map<String, Serializable> documentValues :
				_getValuesList(
					companyId,
					"(r_dsRequestToDSRequestDocuments_l_dsRequestId eq '" +
						dsRequestId + "')",
					objectDefinition)) {

			_reindexFileEntry(MapUtil.getLong(documentValues, "fileEntryId"));
		}
	}

	private Date _toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	private static final String[] _REQUEST_RECIPIENT_STATUSES = {
		"completed", "created", "declined", "sent", "signed"
	};

	private static final String[] _REQUEST_STATUSES = {
		"completed", "created", "declined", "sent", "voided"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		DSRequestManagerImpl.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
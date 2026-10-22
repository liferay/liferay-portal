/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.constants.DSObjectDefinitionConstants;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.exception.DSEnvelopeException;
import com.liferay.digital.signature.exception.DSRequestException;
import com.liferay.digital.signature.manager.DSDocumentManager;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSDocument;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.digital.signature.url.DSRequestURLProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.notification.context.NotificationContextBuilder;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.type.NotificationTypeServiceTracker;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(service = DSRequestManager.class)
public class DSRequestManagerImpl implements DSRequestManager {

	@Override
	public DSRequest addDSRequest(
			long companyId, DSEnvelope dsEnvelope, long[] fileEntryIds,
			long groupId, long userId)
		throws PortalException {

		Group group = _groupLocalService.fetchGroup(groupId);

		if ((group != null) && group.isStagingGroup()) {
			throw new DSRequestException.MustNotBeStagingGroup(groupId);
		}

		if (!_isEnabled(companyId, groupId)) {
			throw new DSRequestException.MustHaveEmbeddedViewEnabled(groupId);
		}

		if (ArrayUtil.isEmpty(fileEntryIds)) {
			throw new DSRequestException.MustHaveFileEntry();
		}

		if (dsEnvelope.getExpireAfter() < 0) {
			throw new DSRequestException.MustNotHaveNegativeExpireAfter();
		}

		Map<Long, DSRequest> dsRequests = _getLatestFileEntryDSRequests(
			companyId, fileEntryIds);

		for (Map.Entry<Long, DSRequest> entry : dsRequests.entrySet()) {
			DSRequest dsRequest = entry.getValue();

			if (dsRequest.isSyncable()) {
				try {
					updateDSRequest(dsRequest.getDSRequestId());
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to update digital signature request " +
								dsRequest.getDSRequestId(),
							portalException);
					}
				}

				dsRequest = fetchDSRequest(
					companyId, dsRequest.getDSRequestId());
			}

			if (!dsRequest.isReplaceable()) {
				throw new DSRequestException.MustHaveReplaceableFileEntry(
					entry.getKey(), dsRequest.getStatus());
			}
		}

		Set<Long> recipientUserIds = new HashSet<>();

		for (DSRecipient dsRecipient : dsEnvelope.getDSRecipients()) {
			User recipientUser = _userLocalService.fetchUserByEmailAddress(
				companyId, dsRecipient.getEmailAddress());

			if (recipientUser == null) {
				throw new DSRequestException.MustHaveExistingRecipient(
					dsRecipient.getEmailAddress());
			}

			if ((recipientUser.getType() != UserConstants.TYPE_REGULAR) ||
				!recipientUser.isActive()) {

				throw new DSRequestException.MustHaveValidRecipient(
					recipientUser.getUserId());
			}

			if (!recipientUserIds.add(recipientUser.getUserId())) {
				throw new DSRequestException.MustNotDuplicateRecipient(
					recipientUser.getUserId());
			}

			dsRecipient.setDSClientUserId(
				String.valueOf(recipientUser.getUserId()));
			dsRecipient.setName(recipientUser.getFullName());
			dsRecipient.setTabsJSONObject(
				JSONUtil.put(
					"signHereTabs",
					JSONUtil.putAll(
						JSONUtil.put(
							"anchorIgnoreIfNotPresent", "true"
						).put(
							"anchorString",
							"/sig" + recipientUserIds.size() + "/"
						).put(
							"anchorUnits", "pixels"
						).put(
							"anchorXOffset", "0"
						).put(
							"anchorYOffset", "0"
						))));
		}

		dsEnvelope.setDSDocuments(
			TransformUtil.transformToList(
				fileEntryIds, fileEntryId -> _toDSDocument(fileEntryId)));

		User user = _userLocalService.getUser(userId);

		dsEnvelope.setSenderEmailAddress(user.getEmailAddress());

		dsEnvelope.setStatus(DSRequestConstants.STATUS_SENT);

		DSEnvelope sentDSEnvelope = _dsEnvelopeManager.addDSEnvelope(
			companyId, groupId, dsEnvelope);

		try {
			return _addDSRequest(
				companyId, sentDSEnvelope, fileEntryIds, groupId, userId);
		}
		catch (Exception exception1) {
			try {
				_dsEnvelopeManager.voidDSEnvelope(
					companyId, sentDSEnvelope.getDSEnvelopeId(), groupId,
					"Unable to record the digital signature request");
			}
			catch (Exception exception2) {
				_log.error(
					"Unable to void digital signature envelope " +
						sentDSEnvelope.getDSEnvelopeId(),
					exception2);

				exception1.addSuppressed(exception2);
			}

			throw exception1;
		}
	}

	@Override
	public DSRequest fetchDSRequest(long companyId, long dsRequestId) {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			dsRequestId);

		if ((objectEntry == null) ||
			(objectEntry.getCompanyId() != companyId) ||
			!_isEnabled(
				companyId,
				MapUtil.getLong(objectEntry.getValues(), "siteGroupId"))) {

			return null;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					companyId);

		if ((objectDefinition == null) ||
			(objectEntry.getObjectDefinitionId() !=
				objectDefinition.getObjectDefinitionId())) {

			return null;
		}

		try {
			Map<Long, DSRequest> dsRequests = _getDSRequests(
				companyId, Collections.singleton(dsRequestId));

			return dsRequests.get(dsRequestId);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get digital signature request " + dsRequestId,
				exception);

			return null;
		}
	}

	@Override
	public DSRequest fetchLatestFileEntryDSRequest(
		long companyId, long fileEntryId) {

		Map<Long, DSRequest> dsRequests = getLatestFileEntryDSRequests(
			companyId, new long[] {fileEntryId});

		return dsRequests.get(fileEntryId);
	}

	@Override
	public List<DSRequest> getGroupDSRequests(
		long companyId, long groupId, String search, int start, int end) {

		if (!_isEnabled(companyId, groupId)) {
			return new ArrayList<>();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					companyId);

		if (objectDefinition == null) {
			return new ArrayList<>();
		}

		try {
			List<Long> dsRequestIds = _objectEntryLocalService.getPrimaryKeys(
				new Long[] {0L}, companyId, objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create(
					"siteGroupId eq " + groupId, objectDefinition),
				false, search, start, end,
				new Sort[] {
					new Sort(Field.CREATE_DATE, Sort.LONG_TYPE, true),
					new Sort("id", Sort.LONG_TYPE, true)
				});

			if (dsRequestIds.isEmpty()) {
				return new ArrayList<>();
			}

			Map<Long, DSRequest> dsRequests = _getDSRequests(
				companyId, new HashSet<>(dsRequestIds));

			return TransformUtil.transform(dsRequestIds, dsRequests::get);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get digital signature requests for site " + groupId,
				exception);

			return new ArrayList<>();
		}
	}

	@Override
	public int getGroupDSRequestsCount(
		long companyId, long groupId, String search) {

		if (!_isEnabled(companyId, groupId)) {
			return 0;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					companyId);

		if (objectDefinition == null) {
			return 0;
		}

		try {
			return _objectEntryLocalService.getValuesListCount(
				new Long[] {0L}, companyId, objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create(
					"siteGroupId eq " + groupId, objectDefinition),
				false, search);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to count digital signature requests for site " +
					groupId,
				exception);

			return 0;
		}
	}

	@Override
	public Map<Long, DSRequest> getLatestFileEntryDSRequests(
		long companyId, long[] fileEntryIds) {

		try {
			return _getLatestFileEntryDSRequests(companyId, fileEntryIds);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get latest digital signature requests for file " +
					"entries",
				portalException);

			return new HashMap<>();
		}
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, DSRequest dsRequest,
			String actionId)
		throws PortalException {

		if (Objects.equals(actionId, ActionKeys.VIEW)) {
			if (dsRequest.getUserId() == permissionChecker.getUserId()) {
				return true;
			}

			for (DSRequestRecipient dsRequestRecipient :
					dsRequest.getDSRequestRecipients()) {

				if (dsRequestRecipient.getUserId() ==
						permissionChecker.getUserId()) {

					return true;
				}
			}
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					dsRequest.getCompanyId());

		if (objectDefinition == null) {
			return false;
		}

		ModelResourcePermission<?> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				objectDefinition.getClassName());

		return modelResourcePermission.contains(
			permissionChecker, dsRequest.getDSRequestId(), actionId);
	}

	@Override
	public void sendDSRequestNotifications(long dsRequestId)
		throws PortalException {

		ObjectEntry objectEntry = _getDSRequestObjectEntry(dsRequestId);

		long companyId = objectEntry.getCompanyId();

		if (!_isEnabled(
				companyId,
				MapUtil.getLong(objectEntry.getValues(), "siteGroupId"))) {

			return;
		}

		Map<Long, DSRequest> dsRequests = null;

		try {
			dsRequests = _getDSRequests(
				companyId, Collections.singleton(dsRequestId));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get digital signature request " + dsRequestId,
				exception);

			return;
		}

		DSRequest dsRequest = dsRequests.get(dsRequestId);

		if ((dsRequest == null) || dsRequest.isTerminal()) {
			return;
		}

		_sendDSRequestNotifications(
			dsRequest, dsRequest.getDSRequestRecipients());
	}

	@Override
	public void updateDSRequest(long dsRequestId) throws PortalException {
		ObjectEntry objectEntry = _getDSRequestObjectEntry(dsRequestId);

		if (!_isEnabled(
				objectEntry.getCompanyId(),
				MapUtil.getLong(objectEntry.getValues(), "siteGroupId"))) {

			return;
		}

		List<DSRequestRecipient> dsRequestRecipients = _updateDSRequest(
			objectEntry);

		if (dsRequestRecipients.isEmpty()) {
			return;
		}

		DSRequest dsRequest = fetchDSRequest(
			objectEntry.getCompanyId(), dsRequestId);

		if ((dsRequest == null) || dsRequest.isTerminal()) {
			return;
		}

		_sendDSRequestNotifications(dsRequest, dsRequestRecipients);
	}

	@Override
	public void voidDSRequest(long dsRequestId, String voidedReason)
		throws PortalException {

		ObjectEntry objectEntry = _getDSRequestObjectEntry(dsRequestId);

		long companyId = objectEntry.getCompanyId();
		long groupId = MapUtil.getLong(objectEntry.getValues(), "siteGroupId");

		if (!_isEnabled(companyId, groupId)) {
			throw new DSRequestException.MustHaveEmbeddedViewEnabled(groupId);
		}

		if (ArrayUtil.contains(
				DSRequestConstants.STATUSES_REQUEST_TERMINAL,
				MapUtil.getString(objectEntry.getValues(), "requestStatus"))) {

			return;
		}

		try {
			_dsEnvelopeManager.voidDSEnvelope(
				companyId, objectEntry.getExternalReferenceCode(), groupId,
				voidedReason);
		}
		catch (DSEnvelopeException.MustBeVoidable dsEnvelopeException) {
			_updateDSRequest(objectEntry);

			objectEntry = _objectEntryLocalService.getObjectEntry(dsRequestId);

			String dsRequestStatus = MapUtil.getString(
				objectEntry.getValues(), "requestStatus");

			if (Objects.equals(
					DSRequestConstants.STATUS_VOIDED, dsRequestStatus)) {

				return;
			}

			if (ArrayUtil.contains(
					DSRequestConstants.STATUSES_REQUEST_TERMINAL,
					dsRequestStatus)) {

				throw new DSRequestException.MustBeActive(dsRequestId);
			}

			throw dsEnvelopeException;
		}

		_updateDSRequest(objectEntry);
	}

	private DSRequest _addDSRequest(
			long companyId, DSEnvelope dsEnvelope, long[] fileEntryIds,
			long groupId, long userId)
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

		String dsRequestStatus = _getDSRequestStatus(dsEnvelope);

		ServiceContext serviceContext = _getServiceContext(
			companyId, groupId, userId);

		ObjectEntry dsRequestObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, userId, dsRequestObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"emailMessage", dsEnvelope.getEmailBlurb()
				).put(
					"emailSubject", dsEnvelope.getEmailSubject()
				).put(
					"externalReferenceCode", dsEnvelope.getDSEnvelopeId()
				).put(
					"requestExpirationDate",
					() -> _toDate(dsEnvelope.getExpirationLocalDateTime())
				).put(
					"requestStatus", () -> dsRequestStatus
				).put(
					"requestStatusDate",
					() -> _toDate(dsEnvelope.getStatusLocalDateTime())
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
					dsRequestObjectEntry.getObjectEntryId()
				).build(),
				serviceContext);
		}

		ObjectDefinition dsRequestRecipientObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_RECIPIENT,
					companyId);

		return new DSRequest(
			dsRequestObjectEntry.getCompanyId(),
			dsRequestObjectEntry.getCreateDate(),
			dsRequestObjectEntry.getObjectEntryId(),
			TransformUtil.unsafeTransform(
				dsEnvelope.getDSRecipients(),
				dsRecipient -> _addDSRequestRecipient(
					dsRecipient, dsRequestObjectEntry,
					dsRequestRecipientObjectDefinition, dsRequestStatus,
					serviceContext)),
			dsRequestObjectEntry.getExternalReferenceCode(),
			ListUtil.fromArray(fileEntryIds), dsRequestObjectEntry.getUserId(),
			dsRequestObjectEntry.getUserName(),
			dsRequestObjectEntry.getValues());
	}

	private DSRequestRecipient _addDSRequestRecipient(
			DSRecipient dsRecipient, ObjectEntry dsRequestObjectEntry,
			ObjectDefinition dsRequestRecipientObjectDefinition,
			String dsRequestStatus, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry dsRequestRecipientObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, dsRequestObjectEntry.getUserId(),
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
					dsRequestObjectEntry.getObjectEntryId()
				).put(
					"r_userToDSRequestRecipients_userId",
					_getUserId(
						dsRequestObjectEntry.getCompanyId(),
						dsRecipient.getEmailAddress())
				).put(
					"requestRecipientStatus",
					() -> _getDSRequestRecipientStatus(
						dsRecipient, dsRequestStatus)
				).put(
					"requestRecipientStatusDate",
					() -> _toDate(dsRecipient.getStatusLocalDateTime())
				).put(
					"sentDate",
					() -> _toDate(dsRecipient.getSentLocalDateTime())
				).put(
					"signingOrder", Math.max(1, dsRecipient.getRoutingOrder())
				).build(),
				serviceContext);

		return new DSRequestRecipient(
			dsRequestRecipientObjectEntry.getValues());
	}

	private ObjectEntry _getDSRequestObjectEntry(long dsRequestId)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			dsRequestId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					objectEntry.getCompanyId());

		if (objectDefinition.getObjectDefinitionId() !=
				objectEntry.getObjectDefinitionId()) {

			throw new NoSuchObjectEntryException(
				StringBundler.concat(
					"No ObjectEntry exists with the key {objectDefinitionId=",
					objectDefinition.getObjectDefinitionId(),
					", objectEntryId=", dsRequestId, "}"));
		}

		return objectEntry;
	}

	private String _getDSRequestRecipientStatus(
		DSRecipient dsRecipient, String dsRequestStatus) {

		String status = StringUtil.toLowerCase(dsRecipient.getStatus());

		if ((Objects.equals(
				dsRequestStatus, DSRequestConstants.STATUS_EXPIRED) ||
			 Objects.equals(
				 dsRequestStatus, DSRequestConstants.STATUS_VOIDED)) &&
			!Objects.equals(status, DSRequestConstants.STATUS_COMPLETED) &&
			!Objects.equals(status, DSRequestConstants.STATUS_DECLINED) &&
			!Objects.equals(status, DSRequestConstants.STATUS_SIGNED)) {

			return dsRequestStatus;
		}

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

		if ((expirationLocalDateTime != null) &&
			Objects.equals(status, DSRequestConstants.STATUS_VOIDED) &&
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

	private Map<Long, DSRequest> _getDSRequests(
			long companyId, Set<Long> dsRequestIds)
		throws Exception {

		Map<Long, DSRequest> dsRequests = new HashMap<>();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_RECIPIENT,
					companyId);

		if (objectDefinition == null) {
			return dsRequests;
		}

		Map<Long, List<DSRequestRecipient>> dsRequestRecipientsMap =
			new HashMap<>();

		for (Map<String, Serializable> values :
				_getValuesList(
					companyId, "r_dsRequestToDSRequestRecipients_l_dsRequestId",
					ArrayUtil.toLongArray(dsRequestIds), objectDefinition, true,
					null)) {

			List<DSRequestRecipient> dsRequestRecipients =
				dsRequestRecipientsMap.computeIfAbsent(
					MapUtil.getLong(
						values,
						"r_dsRequestToDSRequestRecipients_l_dsRequestId"),
					dsRequestId -> new ArrayList<>());

			dsRequestRecipients.add(new DSRequestRecipient(values));
		}

		for (List<DSRequestRecipient> dsRequestRecipients :
				dsRequestRecipientsMap.values()) {

			dsRequestRecipients.sort(
				Comparator.comparingInt(DSRequestRecipient::getSigningOrder));
		}

		Map<Long, List<Long>> fileEntryIdsMap = _getFileEntryIdsMap(
			companyId, dsRequestIds);

		for (long dsRequestId : dsRequestIds) {
			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				dsRequestId);

			if (objectEntry == null) {
				continue;
			}

			dsRequests.put(
				dsRequestId,
				new DSRequest(
					objectEntry.getCompanyId(), objectEntry.getCreateDate(),
					dsRequestId,
					dsRequestRecipientsMap.getOrDefault(
						dsRequestId, Collections.emptyList()),
					objectEntry.getExternalReferenceCode(),
					fileEntryIdsMap.getOrDefault(
						dsRequestId, Collections.emptyList()),
					objectEntry.getUserId(), objectEntry.getUserName(),
					objectEntry.getValues()));
		}

		return dsRequests;
	}

	private String _getEmailMessage(String emailMessage, Locale locale) {
		if (Validator.isNull(emailMessage)) {
			return _language.get(locale, "you-have-a-document-to-sign");
		}

		return HtmlUtil.escape(emailMessage);
	}

	private Map<Long, List<Long>> _getFileEntryIdsMap(
			long companyId, Set<Long> dsRequestIds)
		throws Exception {

		Map<Long, List<Long>> fileEntryIdsMap = new HashMap<>();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_DOCUMENT,
					companyId);

		if (objectDefinition == null) {
			return fileEntryIdsMap;
		}

		for (Map<String, Serializable> values :
				_getValuesList(
					companyId, "r_dsRequestToDSRequestDocuments_l_dsRequestId",
					ArrayUtil.toLongArray(dsRequestIds), objectDefinition, true,
					null)) {

			List<Long> fileEntryIds = fileEntryIdsMap.computeIfAbsent(
				MapUtil.getLong(
					values, "r_dsRequestToDSRequestDocuments_l_dsRequestId"),
				dsRequestId -> new ArrayList<>());

			fileEntryIds.add(MapUtil.getLong(values, "fileEntryId"));
		}

		return fileEntryIdsMap;
	}

	private Map<Long, Long> _getLatestFileEntryDSRequestIds(
			long companyId, long[] fileEntryIds)
		throws Exception {

		Map<Long, Long> dsRequestIds = new HashMap<>();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_DOCUMENT,
					companyId);

		if (objectDefinition == null) {
			return dsRequestIds;
		}

		for (Map<String, Serializable> values :
				_getValuesList(
					companyId, "fileEntryId", fileEntryIds, objectDefinition,
					false,
					new Sort[] {
						new Sort(Field.CREATE_DATE, Sort.LONG_TYPE, true),
						new Sort("id", Sort.LONG_TYPE, true)
					})) {

			dsRequestIds.putIfAbsent(
				MapUtil.getLong(values, "fileEntryId"),
				MapUtil.getLong(
					values, "r_dsRequestToDSRequestDocuments_l_dsRequestId"));
		}

		return dsRequestIds;
	}

	private Map<Long, DSRequest> _getLatestFileEntryDSRequests(
			long companyId, long[] fileEntryIds)
		throws PortalException {

		Map<Long, DSRequest> fileEntryDSRequests = new HashMap<>();

		if (ArrayUtil.isEmpty(fileEntryIds) ||
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-69290")) {

			return fileEntryDSRequests;
		}

		try {
			Map<Long, Long> dsRequestIds = _getLatestFileEntryDSRequestIds(
				companyId, fileEntryIds);

			if (dsRequestIds.isEmpty()) {
				return fileEntryDSRequests;
			}

			Map<Long, DSRequest> dsRequests = _getDSRequests(
				companyId, new HashSet<>(dsRequestIds.values()));

			for (Map.Entry<Long, Long> entry : dsRequestIds.entrySet()) {
				DSRequest dsRequest = dsRequests.get(entry.getValue());

				if ((dsRequest == null) ||
					!_isEnabled(companyId, dsRequest.getSiteGroupId())) {

					continue;
				}

				fileEntryDSRequests.put(entry.getKey(), dsRequest);
			}
		}
		catch (Exception exception) {
			throw new PortalException(
				StringBundler.concat(
					"Unable to get latest digital signature requests for file ",
					"entries ", StringUtil.merge(fileEntryIds)),
				exception);
		}

		return fileEntryDSRequests;
	}

	private Locale _getLocale(long siteGroupId, long userId) throws Exception {
		User user = _userLocalService.fetchUser(userId);

		if (user != null) {
			return user.getLocale();
		}

		return _portal.getSiteDefaultLocale(siteGroupId);
	}

	private ServiceContext _getServiceContext(
		long companyId, long groupId, long userId) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setUserId(userId);

		return serviceContext;
	}

	private String _getSignedFileName(FileEntry fileEntry) {
		if (StringUtil.equalsIgnoreCase(fileEntry.getExtension(), "pdf")) {
			return fileEntry.getFileName();
		}

		String signedFileName =
			FileUtil.stripExtension(fileEntry.getFileName()) + ".pdf";

		DLFileEntry dlFileEntry =
			_dlFileEntryLocalService.fetchFileEntryByFileName(
				fileEntry.getGroupId(), fileEntry.getFolderId(),
				signedFileName);

		if (dlFileEntry == null) {
			return signedFileName;
		}

		return DLUtil.getUniqueFileName(
			fileEntry.getGroupId(), fileEntry.getFolderId(),
			FileUtil.appendParentheticalSuffix(signedFileName, "signed"), true);
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
			long companyId, String fieldName, long[] ids,
			ObjectDefinition objectDefinition, boolean quoted, Sort[] sorts)
		throws Exception {

		List<Map<String, Serializable>> valuesList = new ArrayList<>();

		String delimiter = StringPool.COMMA_AND_SPACE;
		String quote = StringPool.BLANK;

		if (quoted) {
			delimiter = "', '";
			quote = StringPool.APOSTROPHE;
		}

		for (int i = 0; i < ids.length; i += _IN_FILTER_MAX_SIZE) {
			valuesList.addAll(
				_getValuesList(
					companyId,
					StringBundler.concat(
						"(", fieldName, " in (", quote,
						StringUtil.merge(
							ArrayUtil.subset(
								ids, i,
								Math.min(ids.length, i + _IN_FILTER_MAX_SIZE)),
							delimiter),
						quote, "))"),
					objectDefinition, sorts));
		}

		return valuesList;
	}

	private List<Map<String, Serializable>> _getValuesList(
			long companyId, String filterString,
			ObjectDefinition objectDefinition, Sort[] sorts)
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
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-69290")) {
			return false;
		}

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				companyId, groupId);

		if (digitalSignatureConfiguration.embeddedViewEnabled() &&
			digitalSignatureConfiguration.enabled()) {

			return true;
		}

		return false;
	}

	private void _sendDSRequestNotification(
		DSRequest dsRequest, DSRequestRecipient dsRequestRecipient) {

		try {
			NotificationTemplate notificationTemplate =
				_notificationTemplateLocalService.
					fetchNotificationTemplateByExternalReferenceCode(
						"L_DS_REQUEST_SIGN_NOTIFICATION_TEMPLATE",
						dsRequest.getCompanyId());

			if (notificationTemplate == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to send notification for digital ",
							"signature request ", dsRequest.getDSRequestId(),
							" because notification template ",
							"\"L_DS_REQUEST_SIGN_NOTIFICATION_TEMPLATE\" does ",
							"not exist"));
				}

				return;
			}

			Locale locale = _getLocale(
				dsRequest.getSiteGroupId(), dsRequestRecipient.getUserId());

			String emailSubject = dsRequest.getEmailSubject();

			if (Validator.isNull(emailSubject)) {
				emailSubject = _language.get(
					locale, "you-have-a-document-to-sign");
			}

			NotificationType notificationType =
				_notificationTypeServiceTracker.getNotificationType(
					notificationTemplate.getType());

			notificationType.sendNotification(
				new NotificationContextBuilder(
				).className(
					DSRequest.class.getName()
				).classPK(
					dsRequest.getDSRequestId()
				).companyId(
					dsRequest.getCompanyId()
				).groupId(
					dsRequest.getSiteGroupId()
				).notificationTemplate(
					notificationTemplate
				).termValues(
					HashMapBuilder.<String, Object>put(
						"[%DS_REQUEST_EMAIL_MESSAGE%]",
						_getEmailMessage(dsRequest.getEmailMessage(), locale)
					).put(
						"[%DS_REQUEST_EMAIL_SUBJECT%]", emailSubject
					).put(
						"[%DS_REQUEST_FROM_ADDRESS%]",
						PrefsPropsUtil.getString(
							dsRequest.getCompanyId(),
							PropsKeys.ADMIN_EMAIL_FROM_ADDRESS)
					).put(
						"[%DS_REQUEST_FROM_NAME%]",
						PrefsPropsUtil.getString(
							dsRequest.getCompanyId(),
							PropsKeys.ADMIN_EMAIL_FROM_NAME)
					).put(
						"[%DS_REQUEST_SIGN_LABEL%]",
						_language.get(locale, "review-and-sign")
					).put(
						"[%DS_REQUEST_SIGN_URL%]",
						_dsRequestURLProvider.getSignURL(
							dsRequest.getCompanyId(),
							dsRequest.getDSRequestId(),
							dsRequest.getSiteGroupId())
					).put(
						"[%TO%]", dsRequestRecipient.getEmailAddress()
					).build()
				).userId(
					dsRequestRecipient.getUserId()
				).build());
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to send notification for digital signature ",
					"request ", dsRequest.getDSRequestId(), " to user ",
					dsRequestRecipient.getUserId()),
				exception);
		}
	}

	private void _sendDSRequestNotifications(
		DSRequest dsRequest, List<DSRequestRecipient> dsRequestRecipients) {

		for (DSRequestRecipient dsRequestRecipient : dsRequestRecipients) {
			if ((dsRequestRecipient.getUserId() <= 0) ||
				!Objects.equals(
					DSRequestConstants.STATUS_SENT,
					dsRequestRecipient.getStatus()) ||
				!Validator.isEmailAddress(
					dsRequestRecipient.getEmailAddress())) {

				continue;
			}

			_sendDSRequestNotification(dsRequest, dsRequestRecipient);
		}
	}

	private DSDocument _toDSDocument(long fileEntryId) throws Exception {
		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		return new DSDocument() {
			{
				setData(
					Base64.encode(
						FileUtil.getBytes(fileEntry.getContentStream())));
				setDSDocumentId(String.valueOf(fileEntryId));
				setFileExtension(fileEntry.getExtension());
				setName(fileEntry.getFileName());
			}
		};
	}

	private Date _toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	private List<DSRequestRecipient> _updateDSRequest(ObjectEntry objectEntry) {
		if (ArrayUtil.contains(
				DSRequestConstants.STATUSES_REQUEST_TERMINAL,
				MapUtil.getString(objectEntry.getValues(), "requestStatus"))) {

			return Collections.emptyList();
		}

		long companyId = objectEntry.getCompanyId();
		long dsRequestId = objectEntry.getObjectEntryId();
		long groupId = MapUtil.getLong(objectEntry.getValues(), "siteGroupId");

		try {
			DSEnvelope dsEnvelope = _dsEnvelopeManager.getDSEnvelope(
				companyId, groupId, objectEntry.getExternalReferenceCode());

			String dsRequestStatus = _getDSRequestStatus(dsEnvelope);

			if (Objects.equals(
					DSRequestConstants.STATUS_COMPLETED, dsRequestStatus) &&
				!Objects.equals(
					DSRequestConstants.STATUS_COMPLETED,
					MapUtil.getString(
						objectEntry.getValues(), "requestStatus"))) {

				_updateFileEntries(
					companyId, dsEnvelope, dsRequestId, groupId,
					objectEntry.getUserId());
			}

			List<DSRequestRecipient> dsRequestRecipients =
				_updateDSRequestRecipientStatuses(
					companyId, dsEnvelope, dsRequestId, dsRequestStatus,
					groupId);

			_objectEntryLocalService.partialUpdateObjectEntry(
				objectEntry.getUserId(), dsRequestId,
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					"requestExpirationDate",
					() -> _toDate(dsEnvelope.getExpirationLocalDateTime())
				).put(
					"requestStatus", () -> dsRequestStatus
				).put(
					"requestStatusDate",
					() -> _toDate(dsEnvelope.getStatusLocalDateTime())
				).build(),
				_getServiceContext(
					companyId, groupId, objectEntry.getUserId()));

			return dsRequestRecipients;
		}
		catch (Exception exception) {
			_log.error(
				"Unable to update digital signature request " + dsRequestId,
				exception);

			return Collections.emptyList();
		}
	}

	private List<DSRequestRecipient> _updateDSRequestRecipientStatuses(
			long companyId, DSEnvelope dsEnvelope, long dsRequestId,
			String dsRequestStatus, long groupId)
		throws Exception {

		List<DSRequestRecipient> dsRequestRecipients = new ArrayList<>();

		Map<String, DSRecipient> dsRecipients = new HashMap<>();

		for (DSRecipient dsRecipient : dsEnvelope.getDSRecipients()) {
			dsRecipients.put(dsRecipient.getDSClientUserId(), dsRecipient);
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_RECIPIENT,
					companyId);

		if (objectDefinition == null) {
			return dsRequestRecipients;
		}

		for (Map<String, Serializable> values :
				_getValuesList(
					companyId,
					"(r_dsRequestToDSRequestRecipients_l_dsRequestId eq '" +
						dsRequestId + "')",
					objectDefinition, null)) {

			DSRecipient dsRecipient = dsRecipients.get(
				String.valueOf(
					MapUtil.getLong(
						values, "r_userToDSRequestRecipients_userId")));

			if (dsRecipient == null) {
				continue;
			}

			long dsRequestRecipientId = MapUtil.getLong(
				values, objectDefinition.getPKObjectFieldName());

			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				dsRequestRecipientId);

			if (objectEntry == null) {
				continue;
			}

			String dsRequestRecipientStatus = _getDSRequestRecipientStatus(
				dsRecipient, dsRequestStatus);

			objectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
				objectEntry.getUserId(), dsRequestRecipientId,
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					"requestRecipientStatus", () -> dsRequestRecipientStatus
				).put(
					"requestRecipientStatusDate",
					() -> _toDate(dsRecipient.getStatusLocalDateTime())
				).put(
					"sentDate",
					() -> _toDate(dsRecipient.getSentLocalDateTime())
				).build(),
				_getServiceContext(
					companyId, groupId, objectEntry.getUserId()));

			if (!Objects.equals(
					DSRequestConstants.STATUS_CREATED,
					MapUtil.getString(values, "requestRecipientStatus")) ||
				!Objects.equals(
					DSRequestConstants.STATUS_SENT, dsRequestRecipientStatus)) {

				continue;
			}

			dsRequestRecipients.add(
				new DSRequestRecipient(objectEntry.getValues()));
		}

		return dsRequestRecipients;
	}

	private void _updateFileEntries(
			long companyId, DSEnvelope dsEnvelope, long dsRequestId,
			long groupId, long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_DOCUMENT,
					companyId);

		String changeLog =
			"Digital signature envelope " + dsEnvelope.getDSEnvelopeId();

		for (Map<String, Serializable> values :
				_getValuesList(
					companyId,
					"(r_dsRequestToDSRequestDocuments_l_dsRequestId eq '" +
						dsRequestId + "')",
					objectDefinition, null)) {

			long fileEntryId = MapUtil.getLong(values, "fileEntryId");

			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			FileVersion latestFileVersion = fileEntry.getLatestFileVersion(
				true);

			if (Objects.equals(latestFileVersion.getChangeLog(), changeLog)) {
				continue;
			}

			byte[] bytes = _dsDocumentManager.getDSDocumentAsBytes(
				companyId, groupId, String.valueOf(fileEntryId),
				dsEnvelope.getDSEnvelopeId());

			if ((bytes == null) || (bytes.length < 5) ||
				!Objects.equals(new String(bytes, 0, 5), "%PDF-")) {

				throw new PortalException(
					StringBundler.concat(
						"DocuSign did not return a signed PDF for file entry ",
						fileEntryId, " of digital signature envelope ",
						dsEnvelope.getDSEnvelopeId()));
			}

			String signedFileName = _getSignedFileName(fileEntry);

			String title = fileEntry.getTitle();

			if (Objects.equals(title, fileEntry.getFileName())) {
				title = signedFileName;
			}

			boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

			try {
				WorkflowThreadLocal.setEnabled(false);

				ServiceContext serviceContext = _getServiceContext(
					companyId, fileEntry.getGroupId(), userId);

				fileEntry = _dlAppLocalService.updateFileEntry(
					userId, fileEntryId, signedFileName,
					ContentTypes.APPLICATION_PDF, title, null,
					fileEntry.getDescription(), changeLog,
					DLVersionNumberIncrease.MAJOR, bytes,
					fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
					fileEntry.getReviewDate(), serviceContext);

				FileVersion fileVersion = fileEntry.getLatestFileVersion(true);

				if (fileVersion.getStatus() !=
						WorkflowConstants.STATUS_APPROVED) {

					_dlFileEntryLocalService.updateStatus(
						userId, fileVersion.getFileVersionId(),
						WorkflowConstants.STATUS_APPROVED, serviceContext,
						new HashMap<>());
				}
			}
			finally {
				WorkflowThreadLocal.setEnabled(workflowEnabled);
			}
		}
	}

	private static final int _IN_FILTER_MAX_SIZE = 1000;

	private static final Log _log = LogFactoryUtil.getLog(
		DSRequestManagerImpl.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DSDocumentManager _dsDocumentManager;

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

	@Reference
	private DSRequestURLProvider _dsRequestURLProvider;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private NotificationTypeServiceTracker _notificationTypeServiceTracker;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service;

import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceWrapper;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.cms.site.initializer.internal.util.CMSFileTypeUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ServiceWrapper.class)
public class CMSFileTypeObjectEntryLocalServiceWrapper
	extends ObjectEntryLocalServiceWrapper {

	@Override
	public ObjectEntry addObjectEntry(
			long groupId, long userId, long objectDefinitionId,
			long objectEntryFolderId, String defaultLanguageId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		ObjectDefinition objectDefinition = _fetchFileTypeObjectDefinition(
			objectDefinitionId);

		if (objectDefinition == null) {
			return super.addObjectEntry(
				groupId, userId, objectDefinitionId, objectEntryFolderId,
				defaultLanguageId, values, serviceContext);
		}

		String languageId = defaultLanguageId;

		if (Validator.isNull(languageId)) {
			languageId = _language.getLanguageId(
				_portal.getSiteDefaultLocale(groupId));
		}

		_validateFriendlyURL(
			groupId, languageId, objectDefinition, 0, serviceContext);

		ObjectEntry objectEntry = super.addObjectEntry(
			groupId, userId, objectDefinitionId, objectEntryFolderId,
			defaultLanguageId, values, serviceContext);

		CMSFileTypeUtil.updateFileEntryFriendlyURL(
			objectDefinition, objectEntry);

		return objectEntry;
	}

	@Override
	public ObjectEntry partialUpdateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry objectEntry = getObjectEntry(objectEntryId);

		ObjectDefinition objectDefinition = _fetchFileTypeObjectDefinition(
			objectEntry.getObjectDefinitionId());

		if (objectDefinition == null) {
			return super.partialUpdateObjectEntry(
				userId, objectEntryId, objectEntryFolderId, values,
				serviceContext);
		}

		_validateFriendlyURL(
			objectEntry.getNonzeroGroupId(), objectEntry.getDefaultLanguageId(),
			objectDefinition, objectEntryId, serviceContext);

		objectEntry = super.partialUpdateObjectEntry(
			userId, objectEntryId, objectEntryFolderId, values, serviceContext);

		CMSFileTypeUtil.updateFileEntryFriendlyURL(
			objectDefinition, objectEntry);

		return objectEntry;
	}

	@Override
	public ObjectEntry updateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry objectEntry = getObjectEntry(objectEntryId);

		ObjectDefinition objectDefinition = _fetchFileTypeObjectDefinition(
			objectEntry.getObjectDefinitionId());

		if (objectDefinition == null) {
			return super.updateObjectEntry(
				userId, objectEntryId, objectEntryFolderId, values,
				serviceContext);
		}

		_validateFriendlyURL(
			objectEntry.getNonzeroGroupId(), objectEntry.getDefaultLanguageId(),
			objectDefinition, objectEntryId, serviceContext);

		objectEntry = super.updateObjectEntry(
			userId, objectEntryId, objectEntryFolderId, values, serviceContext);

		CMSFileTypeUtil.updateFileEntryFriendlyURL(
			objectDefinition, objectEntry);

		return objectEntry;
	}

	private ObjectDefinition _fetchFileTypeObjectDefinition(
		long objectDefinitionId) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionId);

		if ((objectDefinition == null) ||
			!CMSFileTypeUtil.hasFileObjectField(objectDefinition)) {

			return null;
		}

		return objectDefinition;
	}

	private void _validateFriendlyURL(
			long groupId, String languageId, ObjectDefinition objectDefinition,
			long objectEntryId, ServiceContext serviceContext)
		throws PortalException {

		if (!objectDefinition.isEnableFriendlyURLCustomization()) {
			return;
		}

		Map<String, String> friendlyUrlMap =
			(Map<String, String>)serviceContext.getAttribute("friendlyUrlMap");

		if (friendlyUrlMap == null) {
			return;
		}

		String friendlyURL = friendlyUrlMap.get(languageId);

		if (Validator.isNull(friendlyURL)) {
			return;
		}

		friendlyURL = friendlyURL.replaceAll("^/+", StringPool.BLANK);

		friendlyURL = friendlyURL.replaceAll("/+", StringPool.SLASH);

		if (Validator.isNull(friendlyURL) ||
			CMSFileTypeUtil.isUrlTitleAvailable(
				groupId, objectDefinition, objectEntryId,
				_friendlyURLNormalizer.normalizeWithEncoding(friendlyURL))) {

			return;
		}

		throw new ModelListenerException(
			new ObjectValidationRuleEngineException(
				Collections.singletonList(
					new ObjectValidationRuleResult(
						_language.get(
							serviceContext.getLocale(),
							"the-friendly-url-is-already-in-use.-please-" +
								"enter-a-unique-friendly-url"),
						null, "objectEntryFriendlyURL"))));
	}

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

}
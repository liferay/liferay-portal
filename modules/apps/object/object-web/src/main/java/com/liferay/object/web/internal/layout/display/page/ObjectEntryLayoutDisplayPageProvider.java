/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.layout.display.page;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.asset.util.AssetHelper;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.BaseLayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

/**
 * @author Guilherme Camacho
 */
public class ObjectEntryLayoutDisplayPageProvider
	extends BaseLayoutDisplayPageProvider<ObjectEntry> {

	public ObjectEntryLayoutDisplayPageProvider(
		AssetHelper assetHelper,
		InfoItemFriendlyURLProvider<ObjectEntry> infoItemFriendlyURLProvider,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManager objectEntryManager,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		UserLocalService userLocalService) {

		_assetHelper = assetHelper;
		_infoItemFriendlyURLProvider = infoItemFriendlyURLProvider;
		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManager = objectEntryManager;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public String getDefaultURLSeparator() {
		return StringUtil.quote(
			_objectDefinition.getFriendlyURLSeparator(), CharPool.SLASH);
	}

	@Override
	public LayoutDisplayPageObjectProvider<ObjectEntry>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		if (FeatureFlagManagerUtil.isEnabled("LPD-21926")) {
			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				groupId, _objectDefinition, urlTitle);

			if (objectEntry != null) {
				return new ObjectEntryLayoutDisplayPageObjectProvider(
					_assetHelper, _infoItemFriendlyURLProvider,
					_objectDefinition, _objectDefinitionLocalService,
					objectEntry, _objectEntryLocalService,
					_objectRelationshipLocalService);
			}
		}

		if (!_objectDefinition.isDefaultStorageType()) {
			return getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					ObjectEntry.class.getName(),
					new ERCInfoItemIdentifier(urlTitle)));
		}

		return getLayoutDisplayPageObjectProvider(
			new InfoItemReference(
				ObjectEntry.class.getName(),
				new ClassPKInfoItemIdentifier(GetterUtil.getLong(urlTitle))));
	}

	@Override
	protected LayoutDisplayPageObjectProvider<ObjectEntry>
		doGetLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			return null;
		}

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				classPKInfoItemIdentifier.getClassPK());

			if (objectEntry == null) {
				return null;
			}

			if (infoItemIdentifier.getVersion() != null) {
				ObjectEntryVersion objectEntryVersion =
					ObjectEntryVersionLocalServiceUtil.fetchObjectEntryVersion(
						objectEntry.getObjectEntryId(),
						GetterUtil.getInteger(infoItemIdentifier.getVersion()));

				if (objectEntryVersion != null) {
					_setObjectEntryVersionValues(
						objectEntry, objectEntryVersion);
				}
			}

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectEntry.getObjectDefinitionId());

			return new ObjectEntryLayoutDisplayPageObjectProvider(
				_assetHelper, _infoItemFriendlyURLProvider, objectDefinition,
				_objectDefinitionLocalService, objectEntry,
				_objectEntryLocalService, _objectRelationshipLocalService);
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;

		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (serviceContext == null) {
				return null;
			}

			long userId = serviceContext.getUserId();

			if (userId == 0) {
				userId = PrincipalThreadLocal.getUserId();
			}

			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
				_objectEntryManager.getObjectEntry(
					serviceContext.getCompanyId(),
					new DefaultDTOConverterContext(
						false, null, null, null, null,
						serviceContext.getLocale(), null,
						_userLocalService.fetchUser(userId)),
					ercInfoItemIdentifier.getExternalReferenceCode(),
					_objectDefinition, null);

			if (objectEntry != null) {
				return new ObjectEntryLayoutDisplayPageObjectProvider(
					_assetHelper, _infoItemFriendlyURLProvider,
					_objectDefinition, _objectDefinitionLocalService,
					ObjectEntryUtil.toObjectEntry(
						_objectDefinition.getObjectDefinitionId(), objectEntry),
					_objectEntryLocalService, _objectRelationshipLocalService);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private JSONObject _getContentJSONObject(
		ObjectEntryVersion objectEntryVersion) {

		try {
			return JSONFactoryUtil.createJSONObject(
				objectEntryVersion.getContent());
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return JSONFactoryUtil.createJSONObject();
	}

	private Map<String, Serializable> _getValues(JSONObject jsonObject) {
		try {
			JSONObject propertiesJSONObject = jsonObject.getJSONObject(
				"properties");

			if (JSONUtil.isEmpty(propertiesJSONObject)) {
				return Collections.emptyMap();
			}

			ObjectMapper objectMapper = new ObjectMapper();

			return objectMapper.readValue(
				propertiesJSONObject.toString(),
				new TypeReference<Map<String, Serializable>>() {
				});
		}
		catch (JsonProcessingException jsonProcessingException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonProcessingException);
			}
		}

		return Collections.emptyMap();
	}

	private void _setObjectEntryVersionValues(
		ObjectEntry objectEntry, ObjectEntryVersion objectEntryVersion) {

		JSONObject contentJSONObject = _getContentJSONObject(
			objectEntryVersion);

		objectEntry.setUserId(objectEntryVersion.getUserId());
		objectEntry.setObjectEntryFolderId(
			contentJSONObject.getLong("objectEntryFolderId"));
		objectEntry.setVersion(objectEntryVersion.getVersion());
		objectEntry.setStatus(objectEntryVersion.getStatus());
		objectEntry.setValues(_getValues(contentJSONObject));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryLayoutDisplayPageProvider.class);

	private final AssetHelper _assetHelper;
	private final InfoItemFriendlyURLProvider<ObjectEntry>
		_infoItemFriendlyURLProvider;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManager _objectEntryManager;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final UserLocalService _userLocalService;

}
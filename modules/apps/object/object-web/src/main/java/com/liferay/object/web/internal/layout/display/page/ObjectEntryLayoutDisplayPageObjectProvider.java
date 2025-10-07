/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.layout.display.page;

import com.liferay.asset.util.AssetHelper;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Guilherme Camacho
 */
public class ObjectEntryLayoutDisplayPageObjectProvider
	implements LayoutDisplayPageObjectProvider<ObjectEntry> {

	public ObjectEntryLayoutDisplayPageObjectProvider(
		AssetHelper assetHelper,
		InfoItemFriendlyURLProvider<ObjectEntry> infoItemFriendlyURLProvider,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntry objectEntry,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		this(
			assetHelper, infoItemFriendlyURLProvider, objectDefinition,
			objectDefinitionLocalService, objectEntry, objectEntryLocalService,
			objectRelationshipLocalService, StringPool.BLANK);
	}

	public ObjectEntryLayoutDisplayPageObjectProvider(
		AssetHelper assetHelper,
		InfoItemFriendlyURLProvider<ObjectEntry> infoItemFriendlyURLProvider,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntry objectEntry,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		String parentExternalReferenceCode) {

		_assetHelper = assetHelper;
		_infoItemFriendlyURLProvider = infoItemFriendlyURLProvider;
		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntry = objectEntry;
		_objectEntryLocalService = objectEntryLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_parentExternalReferenceCode = parentExternalReferenceCode;
	}

	@Override
	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public long getClassNameId() {
		return PortalUtil.getClassNameId(_objectDefinition.getClassName());
	}

	@Override
	public long getClassPK() {
		return _objectEntry.getObjectEntryId();
	}

	@Override
	public long getClassTypeId() {
		return 0;
	}

	@Override
	public String getDescription(Locale locale) {
		return null;
	}

	@Override
	public ObjectEntry getDisplayObject() {
		return _objectEntry;
	}

	@Override
	public String getExternalReferenceCode() {
		return _objectEntry.getExternalReferenceCode();
	}

	@Override
	public long getGroupId() {
		return _objectEntry.getGroupId();
	}

	@Override
	public String getKeywords(Locale locale) {
		return _assetHelper.getAssetKeywords(
			_objectDefinition.getClassName(), _objectEntry.getObjectEntryId(),
			locale);
	}

	@Override
	public String getParentExternalReferenceCode() {
		return _parentExternalReferenceCode;
	}

	@Override
	public List<LayoutDisplayPageObjectProvider<ObjectEntry>>
		getRelatedLayoutDisplayPageObjectProviders(String contentType) {

		try {
			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.fetchObjectRelationship(
					_objectDefinition.getObjectDefinitionId(), contentType);

			if (objectRelationship == null) {
				return Collections.emptyList();
			}

			List<LayoutDisplayPageObjectProvider<ObjectEntry>>
				layoutDisplayPageObjectProviders = new ArrayList<>();

			List<ObjectEntry> objectEntries =
				_objectEntryLocalService.getOneToManyObjectEntries(
					getGroupId(), objectRelationship.getObjectRelationshipId(),
					null, true, _objectEntry.getObjectEntryId(), true, null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectRelationship.getObjectDefinitionId2());

			for (ObjectEntry objectEntry : objectEntries) {
				layoutDisplayPageObjectProviders.add(
					new ObjectEntryLayoutDisplayPageObjectProvider(
						_assetHelper, _infoItemFriendlyURLProvider,
						objectDefinition, _objectDefinitionLocalService,
						objectEntry, _objectEntryLocalService,
						_objectRelationshipLocalService,
						_objectEntry.getExternalReferenceCode()));
			}

			return layoutDisplayPageObjectProviders;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public String getTitle(Locale locale) {
		if (!_objectDefinition.isDefaultStorageType()) {
			return _objectEntry.getExternalReferenceCode();
		}

		try {
			String titleValue = _objectEntry.getTitleValue(
				LocaleUtil.toLanguageId(locale), true);

			if (Validator.isNotNull(titleValue)) {
				return titleValue;
			}

			return _objectDefinition.getLabel(locale);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getURLTitle(Locale locale) {
		return _infoItemFriendlyURLProvider.getFriendlyURL(
			_objectEntry, LanguageUtil.getLanguageId(locale));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryLayoutDisplayPageObjectProvider.class);

	private final AssetHelper _assetHelper;
	private final InfoItemFriendlyURLProvider<ObjectEntry>
		_infoItemFriendlyURLProvider;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntry _objectEntry;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private String _parentExternalReferenceCode;

}
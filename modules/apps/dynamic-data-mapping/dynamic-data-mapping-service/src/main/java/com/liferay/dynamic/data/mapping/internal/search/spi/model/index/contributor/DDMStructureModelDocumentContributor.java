/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.search.spi.model.index.contributor;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = "indexer.class.name=com.liferay.dynamic.data.mapping.model.DDMStructure",
	service = ModelDocumentContributor.class
)
public class DDMStructureModelDocumentContributor
	implements ModelDocumentContributor<DDMStructure> {

	@Override
	public void contribute(Document document, DDMStructure ddmStructure) {
		document.addKeyword(Field.CLASS_NAME_ID, ddmStructure.getClassNameId());

		String defaultLanguageId = ddmStructure.getDefaultLanguageId();

		document.addLocalizedText(
			Field.DESCRIPTION,
			_localization.populateLocalizationMap(
				ddmStructure.getDescriptionMap(), defaultLanguageId,
				ddmStructure.getGroupId()));
		document.addLocalizedText(
			Field.NAME,
			_localization.populateLocalizationMap(
				ddmStructure.getNameMap(), defaultLanguageId,
				ddmStructure.getGroupId()));

		try {
			DDMStructureVersion structureVersion =
				ddmStructureVersionLocalService.getStructureVersion(
					ddmStructure.getStructureId(), ddmStructure.getVersion());

			document.addKeyword(Field.STATUS, structureVersion.getStatus());
			document.addKeyword(Field.VERSION, structureVersion.getVersion());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		document.addLocalizedKeyword(
			"localized_name",
			_localization.populateLocalizationMap(
				ddmStructure.getNameMap(), defaultLanguageId,
				ddmStructure.getGroupId()),
			true, true);
		document.addKeyword(
			"resourceClassNameId", ddmStructure.getClassNameId());

		try {
			document.addKeyword(
				"resourcePermissionName",
				_ddmPermissionSupport.getStructureModelResourceName(
					ddmStructure.getClassNameId()));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		document.addKeyword("storageType", ddmStructure.getStorageType());
		document.addKeyword("structureKey", ddmStructure.getStructureKey());
		document.addKeyword("type", ddmStructure.getType());
	}

	protected String[] getLanguageIds(
		String defaultLanguageId, String content) {

		String[] languageIds = _localization.getAvailableLanguageIds(content);

		if (languageIds.length == 0) {
			languageIds = new String[] {defaultLanguageId};
		}

		return languageIds;
	}

	@Reference
	protected DDMStructureVersionLocalService ddmStructureVersionLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureModelDocumentContributor.class);

	@Reference
	private DDMPermissionSupport _ddmPermissionSupport;

	@Reference
	private Localization _localization;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.site.cmp.site.initializer.internal.util.CMPObjectEntryUtil;

/**
 * @author Pedro Leite
 */
public class CMPObjectEntryModelDocumentContributor
	implements ModelDocumentContributor<ObjectEntry> {

	public CMPObjectEntryModelDocumentContributor(
		FilterFactory<Predicate> filterFactory,
		GroupLocalService groupLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ObjectEntryLocalService objectEntryLocalService) {

		_filterFactory = filterFactory;
		_groupLocalService = groupLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	public void contribute(Document document, ObjectEntry objectEntry) {
		try {
			_contribute(document, objectEntry);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _addKeyword(
		Document document, String fieldName, long[] objectEntryIds) {

		if (objectEntryIds.length == 0) {
			return;
		}

		document.addKeyword(fieldName, objectEntryIds);
	}

	private void _contribute(Document document, ObjectEntry objectEntry)
		throws PortalException {

		if (!LicenseManagerUtil.isAppEnabled(App.CMP) ||
			!CMPObjectEntryUtil.isCMSAsset(
				objectEntry, _objectEntryFolderLocalService)) {

			return;
		}

		long[] cmpTaskObjectEntryIds =
			CMPObjectEntryUtil.getCMPTaskObjectEntryIds(
				_filterFactory, _groupLocalService,
				_objectDefinitionLocalService, objectEntry,
				_objectEntryLocalService);

		_addKeyword(
			document, "cmpProjectObjectEntryIds",
			CMPObjectEntryUtil.getCMPProjectObjectEntryIds(
				cmpTaskObjectEntryIds, _filterFactory, _groupLocalService,
				_objectDefinitionLocalService, objectEntry,
				_objectEntryLocalService));
		_addKeyword(document, "cmpTaskObjectEntryIds", cmpTaskObjectEntryIds);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMPObjectEntryModelDocumentContributor.class);

	private final FilterFactory<Predicate> _filterFactory;
	private final GroupLocalService _groupLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;

}
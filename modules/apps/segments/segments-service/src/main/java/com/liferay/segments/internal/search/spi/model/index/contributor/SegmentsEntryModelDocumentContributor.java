/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.search.spi.model.index.contributor;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.segments.internal.search.SegmentsEntryField;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRole;
import com.liferay.segments.service.SegmentsEntryRoleLocalService;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "indexer.class.name=com.liferay.segments.model.SegmentsEntry",
	service = ModelDocumentContributor.class
)
public class SegmentsEntryModelDocumentContributor
	implements ModelDocumentContributor<SegmentsEntry> {

	@Override
	public void contribute(Document document, SegmentsEntry segmentsEntry) {
		document.addLocalizedKeyword(
			Field.DESCRIPTION, segmentsEntry.getDescriptionMap(), true);
		document.addDate(Field.MODIFIED_DATE, segmentsEntry.getModifiedDate());
		document.addKeyword(
			SegmentsEntryField.ACTIVE, segmentsEntry.isActive());

		_searchLocalizationHelper.addLocalizedField(
			document, Field.NAME,
			_getSiteDefaultLocale(segmentsEntry.getGroupId()),
			segmentsEntry.getNameMap());

		document.addLocalizedKeyword(
			"localized_name",
			_localization.populateLocalizationMap(
				segmentsEntry.getNameMap(),
				segmentsEntry.getDefaultLanguageId(),
				segmentsEntry.getGroupId()),
			true, true);
		document.addKeyword(
			"roleIds",
			TransformUtil.transformToLongArray(
				_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
					segmentsEntry.getSegmentsEntryId()),
				SegmentsEntryRole::getRoleId));
		document.addKeyword(
			"source", StringUtil.toLowerCase(segmentsEntry.getSource()));
		document.addKeyword("type", segmentsEntry.getType());
	}

	private Locale _getSiteDefaultLocale(long groupId) {
		try {
			return _portal.getSiteDefaultLocale(groupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private SearchLocalizationHelper _searchLocalizationHelper;

	@Reference
	private SegmentsEntryRoleLocalService _segmentsEntryRoleLocalService;

}
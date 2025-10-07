/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.dto.v1_0.util;

import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Locale;
import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public class ListTypeEntryUtil {

	public static com.liferay.list.type.model.ListTypeEntry toListTypeEntry(
		ListTypeEntry listTypeEntry,
		ListTypeEntryLocalService listTypeEntryLocalService) {

		com.liferay.list.type.model.ListTypeEntry serviceBuilderListTypeEntry =
			listTypeEntryLocalService.createListTypeEntry(0L);

		serviceBuilderListTypeEntry.setExternalReferenceCode(
			listTypeEntry.getExternalReferenceCode());
		serviceBuilderListTypeEntry.setKey(listTypeEntry.getKey());

		Map<Locale, String> nameMap = LocalizedMapUtil.getLocalizedMap(
			listTypeEntry.getName_i18n());

		nameMap.computeIfAbsent(
			LocaleUtil.getSiteDefault(), key -> listTypeEntry.getName());

		serviceBuilderListTypeEntry.setNameMap(nameMap);

		serviceBuilderListTypeEntry.setSystem(
			GetterUtil.getBoolean(listTypeEntry.getSystem()));

		return serviceBuilderListTypeEntry;
	}

	public static ListTypeEntry toListTypeEntry(
		Map<String, Map<String, String>> actions, Locale locale,
		com.liferay.list.type.model.ListTypeEntry serviceBuilderListTypeEntry) {

		ListTypeEntry listTypeEntry = new ListTypeEntry() {
			{
				setDateCreated(serviceBuilderListTypeEntry::getCreateDate);
				setDateModified(serviceBuilderListTypeEntry::getModifiedDate);
				setExternalReferenceCode(
					serviceBuilderListTypeEntry::getExternalReferenceCode);
				setId(serviceBuilderListTypeEntry::getListTypeEntryId);
				setKey(serviceBuilderListTypeEntry::getKey);
				setName(() -> serviceBuilderListTypeEntry.getName(locale));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						serviceBuilderListTypeEntry.getNameMap()));
				setSystem(serviceBuilderListTypeEntry::getSystem);
				setType(serviceBuilderListTypeEntry::getType);
			}
		};

		listTypeEntry.setActions(() -> actions);

		return listTypeEntry;
	}

}
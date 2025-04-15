/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.dynamic.data.mapping.util;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.BaseDDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMDisplayTabItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.Portal;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
	service = DDMDisplay.class
)
public class DDLDDMDisplay extends BaseDDMDisplay {

	@Override
	public DDMDisplayTabItem getDefaultTabItem() {
		return new DDMDisplayTabItem() {

			@Override
			public String getTitle(
				LiferayPortletRequest liferayPortletRequest,
				LiferayPortletResponse liferayPortletResponse) {

				Locale locale = portal.getLocale(liferayPortletRequest);

				String viewStructuresTitle = DDLDDMDisplay.this.getTitle(
					locale);

				if (Objects.equals(
						viewStructuresTitle,
						portal.getPortletTitle(liferayPortletResponse))) {

					return viewStructuresTitle;
				}

				return getViewTemplatesTitle(null, locale);
			}

		};
	}

	@Override
	public String getPortletId() {
		return DDLPortletKeys.DYNAMIC_DATA_LISTS;
	}

	@Override
	public String getStorageType() {
		return StorageType.DEFAULT.getValue();
	}

	@Override
	public String getStructureName(Locale locale) {
		return language.get(getResourceBundle(locale), "data-definition");
	}

	@Override
	public String getStructureType() {
		return DDLRecordSet.class.getName();
	}

	@Override
	public long getTemplateHandlerClassNameId(
		DDMTemplate template, long classNameId) {

		return portal.getClassNameId(DDLRecordSet.class);
	}

	@Override
	public String getTitle(Locale locale) {
		return language.get(getResourceBundle(locale), "data-definitions");
	}

	@Override
	public boolean isShowBackURLInTitleBar() {
		return true;
	}

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

}
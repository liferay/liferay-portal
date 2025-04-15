/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.data.provider.display.DDMDataProviderDisplay;
import com.liferay.dynamic.data.mapping.util.DDMDisplayTabItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = "jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
	service = DDMDataProviderDisplay.class
)
public class DDMFormDDMDataProviderDisplay implements DDMDataProviderDisplay {

	@Override
	public List<DDMDisplayTabItem> getDDMDisplayTabItems() {
		return Arrays.asList(
			_ddmFormAdminTabItem, _ddmFormAdminFieldSetTabItem,
			_ddmFormAdminDataProviderTabItem);
	}

	@Override
	public DDMDisplayTabItem getDefaultDDMDisplayTabItem() {
		return _ddmFormAdminDataProviderTabItem;
	}

	@Override
	public String getTitle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, "forms");
	}

	@Reference(
		target = "(component.name=com.liferay.dynamic.data.mapping.form.web.internal.tab.item.DDMFormAdminDataProviderTabItem)"
	)
	private DDMDisplayTabItem _ddmFormAdminDataProviderTabItem;

	@Reference(
		target = "(component.name=com.liferay.dynamic.data.mapping.form.web.internal.tab.item.DDMFormAdminFieldSetTabItem)"
	)
	private DDMDisplayTabItem _ddmFormAdminFieldSetTabItem;

	@Reference(
		target = "(component.name=com.liferay.dynamic.data.mapping.form.web.internal.tab.item.DDMFormAdminTabItem)"
	)
	private DDMDisplayTabItem _ddmFormAdminTabItem;

	@Reference
	private Language _language;

}
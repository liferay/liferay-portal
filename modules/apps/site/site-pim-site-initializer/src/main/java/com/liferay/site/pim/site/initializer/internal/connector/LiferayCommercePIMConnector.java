/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorField;
import com.liferay.site.pim.site.initializer.constants.PIMConnectorFieldConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Andrea Sbarra
 */
@Component(service = PIMConnector.class)
public class LiferayCommercePIMConnector implements PIMConnector {

	public static final String KEY = "liferay-commerce";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(locale, KEY);
	}

	@Override
	public List<PIMConnectorField> getPIMConnectorFields(Locale locale) {
		return TransformUtil.transform(
			_pimConnectorFields,
			pimConnectorField -> new PIMConnectorField(
				LanguageUtil.get(locale, pimConnectorField.getLabel()),
				pimConnectorField.isMultiple(), pimConnectorField.getName(),
				pimConnectorField.isRequired(), pimConnectorField.getType()));
	}

	@Override
	public boolean isActive(long companyId) {
		return true;
	}

	private static final String _CHANNEL_FIELD_NAME_CATALOG_ID = "catalogId";

	private static final String _CHANNEL_FIELD_NAME_DESCRIPTION = "description";

	private static final String _CHANNEL_FIELD_NAME_NAME = "name";

	private static final String _CHANNEL_FIELD_NAME_SKU = "skus[].sku";

	private static final String _CHANNEL_FIELD_NAME_TAGS = "tags";

	private static final List<PIMConnectorField> _pimConnectorFields =
		Arrays.asList(
			new PIMConnectorField(
				"catalog-id", false, _CHANNEL_FIELD_NAME_CATALOG_ID, true,
				PIMConnectorFieldConstants.TYPE_LONG),
			new PIMConnectorField(
				_CHANNEL_FIELD_NAME_DESCRIPTION, false,
				_CHANNEL_FIELD_NAME_DESCRIPTION, false,
				PIMConnectorFieldConstants.TYPE_LOCALIZED_TEXT),
			new PIMConnectorField(
				_CHANNEL_FIELD_NAME_NAME, false, _CHANNEL_FIELD_NAME_NAME, true,
				PIMConnectorFieldConstants.TYPE_LOCALIZED_TEXT),
			new PIMConnectorField(
				"sku", false, _CHANNEL_FIELD_NAME_SKU, true,
				PIMConnectorFieldConstants.TYPE_TEXT),
			new PIMConnectorField(
				_CHANNEL_FIELD_NAME_TAGS, true, _CHANNEL_FIELD_NAME_TAGS, false,
				PIMConnectorFieldConstants.TYPE_TEXT));

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system.info.item.provider;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.provider.BaseInfoItemDetailsProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.system.SystemObjectEntry;
import com.liferay.portal.kernel.util.LocaleUtil;

/**
 * @author Carolina Barbosa
 */
public class SystemObjectEntryInfoItemDetailsProvider
	extends BaseInfoItemDetailsProvider<SystemObjectEntry> {

	public SystemObjectEntryInfoItemDetailsProvider(
		String itemClassName, ObjectDefinition objectDefinition) {

		_itemClassName = itemClassName;
		_objectDefinition = objectDefinition;
	}

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(
			_itemClassName,
			InfoLocalizedValue.<String>builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(
					_objectDefinition.getDefaultLanguageId())
			).values(
				_objectDefinition.getLabelMap()
			).build());
	}

	@Override
	protected InfoItemIdentifierFactory<SystemObjectEntry>
		getInfoItemIdentifierFactory() {

		return new InfoItemIdentifierFactory<>() {

			@Override
			public ClassPKInfoItemIdentifier createClassPKInfoItemIdentifier(
				SystemObjectEntry systemObjectEntry) {

				return new ClassPKInfoItemIdentifier(
					systemObjectEntry.getClassPK());
			}

			@Override
			public ERCInfoItemIdentifier createERCInfoItemIdentifier(
				String externalReferenceCode,
				String scopeExternalReferenceCode) {

				return new ERCInfoItemIdentifier(
					externalReferenceCode, scopeExternalReferenceCode);
			}

		};
	}

	private final String _itemClassName;
	private final ObjectDefinition _objectDefinition;

}
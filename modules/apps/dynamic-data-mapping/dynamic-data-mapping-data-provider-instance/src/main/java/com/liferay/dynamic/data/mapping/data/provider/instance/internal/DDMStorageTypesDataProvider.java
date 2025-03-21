/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.instance.internal;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.KeyValuePair;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.data.provider.instance.id=ddm-storage-types",
	service = DDMDataProvider.class
)
public class DDMStorageTypesDataProvider implements DDMDataProvider {

	@Override
	public DDMDataProviderResponse getData(
		DDMDataProviderRequest ddmDataProviderRequest) {

		List<KeyValuePair> keyValuePairs = new ArrayList<>();

		Set<String> storageTypes =
			ddmStorageAdapterRegistry.getDDMStorageAdapterTypes();

		HttpServletRequest httpServletRequest =
			ddmDataProviderRequest.getParameter(
				"httpServletRequest", HttpServletRequest.class);

		for (String storageType : storageTypes) {
			if (storageType.equals(StorageType.JSON.getValue())) {
				continue;
			}

			if (httpServletRequest == null) {
				keyValuePairs.add(new KeyValuePair(storageType, storageType));

				continue;
			}

			keyValuePairs.add(
				new KeyValuePair(
					storageType,
					_language.get(
						httpServletRequest, storageType + "[stands-for]",
						_language.get(httpServletRequest, storageType))));
		}

		DDMDataProviderResponse.Builder builder =
			DDMDataProviderResponse.Builder.newBuilder();

		builder.withOutput("Default-Output", keyValuePairs);

		return builder.build();
	}

	@Override
	public Class<?> getSettings() {
		throw new UnsupportedOperationException();
	}

	@Reference
	protected DDMStorageAdapterRegistry ddmStorageAdapterRegistry;

	@Reference
	private Language _language;

}
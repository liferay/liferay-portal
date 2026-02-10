/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.provider;

import com.liferay.info.item.InfoItemFormVariation;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Jorge Ferrer
 */
public interface InfoItemFormVariationsProvider<T> {

	public default InfoItemFormVariation getInfoItemFormVariation(
		long groupId, String formVariationKey) {

		for (InfoItemFormVariation infoItemFormVariation :
				getInfoItemFormVariations(groupId)) {

			if (Objects.equals(
					formVariationKey, infoItemFormVariation.getKey())) {

				return infoItemFormVariation;
			}
		}

		return null;
	}

	public default InfoItemFormVariation
		getInfoItemFormVariationByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		for (InfoItemFormVariation infoItemFormVariation :
				getInfoItemFormVariations(groupId)) {

			if (Objects.equals(
					externalReferenceCode,
					infoItemFormVariation.getExternalReferenceCode())) {

				return infoItemFormVariation;
			}
		}

		return null;
	}

	public Collection<InfoItemFormVariation> getInfoItemFormVariations(
		long groupId);

	public default Collection<InfoItemFormVariation> getInfoItemFormVariations(
		long[] groupIds) {

		return Collections.emptyList();
	}

	public default Collection<InfoItemFormVariation>
		getInfoItemFormVariationsByCompanyId(long companyId) {

		return Collections.emptyList();
	}

	public default String getSubtypeClassName() {
		return null;
	}

}
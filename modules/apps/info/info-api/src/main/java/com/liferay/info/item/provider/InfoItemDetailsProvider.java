/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.provider;

import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;

/**
 * @author Jorge Ferrer
 */
public interface InfoItemDetailsProvider<T> {

	public InfoItemClassDetails getInfoItemClassDetails();

	public default InfoItemDetails getInfoItemDetails(
		long groupId,
		Class<? extends InfoItemIdentifier> infoItemIdentifierClass, T t) {

		return getInfoItemDetails(t);
	}

	public InfoItemDetails getInfoItemDetails(T t);

}
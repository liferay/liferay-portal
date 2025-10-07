/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.InfoItemIdentifier;

/**
 * @author Jorge Ferrer
 */
public interface InfoItemObjectProvider<T> {

	public T getInfoItem(InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException;

	public default T getInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		return getInfoItem(infoItemIdentifier);
	}

}
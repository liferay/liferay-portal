/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;

/**
 * @author Raymond Augé
 */
public interface ServiceWrapper<T> extends IdentifiableOSGiService {

	@Override
	public default String getOSGiServiceIdentifier() {
		T t = getWrappedService();

		if (t instanceof IdentifiableOSGiService) {
			IdentifiableOSGiService identifiableOSGiService =
				(IdentifiableOSGiService)t;

			return identifiableOSGiService.getOSGiServiceIdentifier();
		}

		return null;
	}

	public T getWrappedService();

	public void setWrappedService(T service);

}
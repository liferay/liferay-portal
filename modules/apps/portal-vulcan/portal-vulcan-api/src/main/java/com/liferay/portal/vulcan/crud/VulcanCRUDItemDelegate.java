/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.crud;

import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.ws.rs.NotFoundException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Marco Leo
 * @author Carlos Correa
 */
@ProviderType
public interface VulcanCRUDItemDelegate<T> {

	public static final Log log = LogFactoryUtil.getLog(
		VulcanCRUDItemDelegate.class);

	public default T fetchItem(Long id) {
		try {
			return getItem(id);
		}
		catch (NoSuchModelException | NotFoundException exception) {
			if (log.isDebugEnabled()) {
				log.debug("Unable to find item with id " + id, exception);
			}
		}
		catch (Exception exception) {
			log.error("Unable to find item with id " + id, exception);
		}

		return null;
	}

	public T getItem(Long id) throws Exception;

}
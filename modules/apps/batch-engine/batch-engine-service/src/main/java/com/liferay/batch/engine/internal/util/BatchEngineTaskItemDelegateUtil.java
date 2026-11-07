/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.util;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateAdaptorFactory;

/**
 * @author Matija Petanjek
 */
public class BatchEngineTaskItemDelegateUtil {

	public static BatchEngineTaskItemDelegate<?> toBatchEngineTaskItemDelegate(
		Object service,
		VulcanBatchEngineTaskItemDelegateAdaptorFactory
			vulcanBatchEngineTaskItemDelegateAdaptorFactory) {

		if (service instanceof BatchEngineTaskItemDelegate) {
			return (BatchEngineTaskItemDelegate<?>)service;
		}

		if (service instanceof VulcanBatchEngineTaskItemDelegate) {
			return vulcanBatchEngineTaskItemDelegateAdaptorFactory.create(
				(VulcanBatchEngineTaskItemDelegate<?>)service);
		}

		throw new IllegalArgumentException("Unknown service :" + service);
	}

}
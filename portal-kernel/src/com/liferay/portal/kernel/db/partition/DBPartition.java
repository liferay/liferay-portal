/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.db.partition;

import com.liferay.counter.kernel.model.Counter;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Luis Ortiz
 */
public class DBPartition {

	public static boolean isPartitionedModel(Class<?> clazz) {
		if (PropsValues.DATABASE_PARTITION_ENABLED &&
			(ClassName.class.isAssignableFrom(clazz) ||
			 Counter.class.isAssignableFrom(clazz) ||
			 ResourceAction.class.isAssignableFrom(clazz) ||
			 ShardedModel.class.isAssignableFrom(clazz))) {

			return true;
		}

		return false;
	}

}
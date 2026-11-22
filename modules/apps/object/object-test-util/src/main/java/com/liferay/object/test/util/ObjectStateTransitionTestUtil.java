/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.test.util;

import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.service.persistence.ObjectStateTransitionUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class ObjectStateTransitionTestUtil {

	public static List<ObjectStateTransition>
			getObjectStateObjectStateTransitions(long objectStateId)
		throws Exception {

		return TransactionTestUtil.invoke(
			ObjectStateTransitionUtil.getPersistence(),
			objectStateTransitionPersistence ->
				objectStateTransitionPersistence.findBySourceObjectStateId(
					objectStateId));
	}

}
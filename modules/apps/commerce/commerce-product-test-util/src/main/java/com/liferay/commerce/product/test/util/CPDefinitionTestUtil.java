/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.test.util;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.persistence.CPDefinitionUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class CPDefinitionTestUtil {

	public static List<CPDefinition> getCProductCPDefinitions(
			long cProductId, int status, int start, int end)
		throws Exception {

		return TransactionTestUtil.invoke(
			CPDefinitionUtil.getPersistence(),
			cpDefinitionPersistence -> cpDefinitionPersistence.findByC_S(
				cProductId, status, start, end));
	}

	public static int getCProductCPDefinitionsCount(long cProductId, int status)
		throws Exception {

		return TransactionTestUtil.invoke(
			CPDefinitionUtil.getPersistence(),
			cpDefinitionPersistence -> cpDefinitionPersistence.countByC_S(
				cProductId, status));
	}

}
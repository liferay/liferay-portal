/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.test.util;

import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassCPDefinitionRelUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class CommercePricingClassCPDefinitionRelTestUtil {

	public static List<CommercePricingClassCPDefinitionRel>
			getCommercePricingClassCPDefinitionRels(long commercePricingClassId)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommercePricingClassCPDefinitionRelUtil.getPersistence(),
			commercePricingClassCPDefinitionRelPersistence ->
				commercePricingClassCPDefinitionRelPersistence.
					findByCommercePricingClassId(commercePricingClassId));
	}

	public static int getCommercePricingClassCPDefinitionRelsCount(
			long commercePricingClassId)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommercePricingClassCPDefinitionRelUtil.getPersistence(),
			commercePricingClassCPDefinitionRelPersistence ->
				commercePricingClassCPDefinitionRelPersistence.
					countByCommercePricingClassId(commercePricingClassId));
	}

}
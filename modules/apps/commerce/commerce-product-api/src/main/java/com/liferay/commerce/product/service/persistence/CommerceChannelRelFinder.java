/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Marco Leo
 * @generated
 */
@ProviderType
public interface CommerceChannelRelFinder {

	public int countByC_C(
		String className, long classPK, String name, boolean inlineSQLHelper);

	public java.util.List<com.liferay.commerce.product.model.CommerceChannelRel>
		findByC_C(
			String className, long classPK, String name, int start, int end);

	public java.util.List<com.liferay.commerce.product.model.CommerceChannelRel>
		findByC_C(
			String className, long classPK, String name, int start, int end,
			boolean inlineSQLHelper);

}
// LIFERAY-SERVICE-BUILDER-HASH:1025003548
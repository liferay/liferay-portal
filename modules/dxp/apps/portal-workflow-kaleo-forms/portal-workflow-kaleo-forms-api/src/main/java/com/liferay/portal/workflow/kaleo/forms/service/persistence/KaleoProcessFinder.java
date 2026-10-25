/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Marcellus Tavares
 * @generated
 */
@ProviderType
public interface KaleoProcessFinder {

	public int filterCountByKeywords(long groupId, String keywords);

	public java.util.List
		<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess>
			filterFindByKeywords(
				long groupId, String keywords, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess>
						orderByComparator);

	public java.util.List
		<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess>
			filterFindByG_N_D(
				long groupId, String[] names, String[] descriptions,
				boolean andOperator, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess>
						orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:1128976594
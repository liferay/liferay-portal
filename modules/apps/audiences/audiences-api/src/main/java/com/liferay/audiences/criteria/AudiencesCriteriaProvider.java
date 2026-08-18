/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.criteria;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eudaldo Alonso
 */
@ProviderType
public interface AudiencesCriteriaProvider {

	public List<AudiencesCriteriaType> getAudiencesCriteriaTypes(
		long companyId, Locale locale);

	public Set<String> getCustomAudiencesCriteriaKeys(long companyId);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.dto.v1_0.converter;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Riccardo Ferrari
 */
public class ChannelDTOConverterContext extends DefaultDTOConverterContext {

	public ChannelDTOConverterContext(
		String analyticsDataSourceId, Object id, Locale locale) {

		super(false, new HashMap<>(), null, id, locale, null, null);

		_analyticsDataSourceId = GetterUtil.getLong(analyticsDataSourceId);
	}

	public boolean isLocalAnalyticsDataSource(Long analyticsDataSourceId) {
		return Objects.equals(analyticsDataSourceId, _analyticsDataSourceId);
	}

	private final Long _analyticsDataSourceId;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.dto.v1_0.converter;

import com.liferay.analytics.settings.rest.dto.v1_0.DataSource;
import com.liferay.analytics.settings.rest.internal.client.model.AnalyticsDataSource;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = "dto.class.name=AnalyticsDataSource",
	service = DTOConverter.class
)
public class DataSourceDTOConverter
	implements DTOConverter<AnalyticsDataSource, DataSource> {

	@Override
	public String getContentType() {
		return DataSource.class.getSimpleName();
	}

	@Override
	public DataSource toDTO(
			DTOConverterContext dtoConverterContext,
			AnalyticsDataSource analyticsDataSource)
		throws Exception {

		return new DataSource() {
			{
				setDataSourceId(
					() -> String.valueOf(analyticsDataSource.getId()));
				setSiteIds(analyticsDataSource::getSiteIds);
			}
		};
	}

}
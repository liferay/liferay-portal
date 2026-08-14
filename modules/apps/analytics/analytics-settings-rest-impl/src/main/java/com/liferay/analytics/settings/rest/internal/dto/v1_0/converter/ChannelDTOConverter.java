/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.dto.v1_0.converter;

import com.liferay.analytics.settings.rest.dto.v1_0.Channel;
import com.liferay.analytics.settings.rest.dto.v1_0.DataSource;
import com.liferay.analytics.settings.rest.internal.client.model.AnalyticsChannel;
import com.liferay.analytics.settings.rest.internal.client.model.AnalyticsDataSource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = "dto.class.name=AnalyticsChannel", service = DTOConverter.class
)
public class ChannelDTOConverter
	implements DTOConverter<AnalyticsChannel, Channel> {

	@Override
	public String getContentType() {
		return Channel.class.getSimpleName();
	}

	@Override
	public Channel toDTO(
			DTOConverterContext dtoConverterContext,
			AnalyticsChannel analyticsChannel)
		throws Exception {

		ChannelDTOConverterContext channelDTOConverterContext =
			(ChannelDTOConverterContext)dtoConverterContext;

		return new Channel() {
			{
				setChannelId(() -> String.valueOf(analyticsChannel.getId()));
				setDataSources(
					() -> TransformUtil.transform(
						ArrayUtil.filter(
							analyticsChannel.getAnalyticsDataSources(),
							analyticsDataSource ->
								channelDTOConverterContext.
									isLocalAnalyticsDataSource(
										analyticsDataSource.getId())),
						analyticsDataSource -> _dataSourceDTOConverter.toDTO(
							analyticsDataSource),
						DataSource.class));
				setName(analyticsChannel::getName);
			}
		};
	}

	@Reference(
		target = "(component.name=com.liferay.analytics.settings.rest.internal.dto.v1_0.converter.DataSourceDTOConverter)"
	)
	private DTOConverter<AnalyticsDataSource, DataSource>
		_dataSourceDTOConverter;

}
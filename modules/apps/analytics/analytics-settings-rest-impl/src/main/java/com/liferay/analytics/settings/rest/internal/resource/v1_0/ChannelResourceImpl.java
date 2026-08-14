/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.resource.v1_0;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.dto.v1_0.Channel;
import com.liferay.analytics.settings.rest.dto.v1_0.DataSource;
import com.liferay.analytics.settings.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.settings.rest.internal.client.model.AnalyticsChannel;
import com.liferay.analytics.settings.rest.internal.client.model.AnalyticsDataSource;
import com.liferay.analytics.settings.rest.internal.dto.v1_0.converter.ChannelDTOConverterContext;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Objects;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Ferrari
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/channel.properties",
	scope = ServiceScope.PROTOTYPE, service = ChannelResource.class
)
public class ChannelResourceImpl extends BaseChannelResourceImpl {

	@Override
	public Page<Channel> getChannelsPage(
			String keywords, Pagination pagination, Sort[] sorts)
		throws Exception {

		com.liferay.analytics.settings.rest.internal.client.pagination.Page
			<AnalyticsChannel> analyticsChannelsPage =
				_analyticsCloudClient.getAnalyticsChannelsPage(
					_configurationProvider.getCompanyConfiguration(
						AnalyticsConfiguration.class,
						contextCompany.getCompanyId()),
					keywords, pagination.getPage() - 1,
					pagination.getPageSize(), sorts);
		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId());

		return Page.of(
			transform(
				analyticsChannelsPage.getItems(),
				analyticsChannel -> _channelDTOConverter.toDTO(
					new ChannelDTOConverterContext(
						analyticsConfiguration.liferayAnalyticsDataSourceId(),
						analyticsChannel.getId(),
						contextAcceptLanguage.getPreferredLocale()),
					analyticsChannel)),
			pagination, analyticsChannelsPage.getTotalCount());
	}

	@Override
	public Channel patchChannel(Channel channel) throws Exception {
		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId());

		DataSource[] dataSources = channel.getDataSources();

		if (ArrayUtil.isEmpty(dataSources)) {
			return _channelDTOConverter.toDTO(
				new ChannelDTOConverterContext(
					analyticsConfiguration.liferayAnalyticsDataSourceId(),
					channel.getChannelId(),
					contextAcceptLanguage.getPreferredLocale()),
				_analyticsCloudClient.updateAnalyticsChannel(
					channel.getChannelId(),
					transform(
						ArrayUtil.toArray(
							_analyticsSettingsManager.getCommerceChannelIds(
								contextUser.getCompanyId(),
								ArrayUtil.toArray(
									_analyticsSettingsManager.getSiteIds(
										channel.getChannelId(),
										contextUser.getCompanyId())))),
						commerceChannelId -> _groupLocalService.fetchGroup(
							contextUser.getCompanyId(),
							_commerceChannelClassNameIdSupplier.get(),
							commerceChannelId),
						Group.class),
					_configurationProvider.getCompanyConfiguration(
						AnalyticsConfiguration.class,
						contextUser.getCompanyId()),
					analyticsConfiguration.liferayAnalyticsDataSourceId(),
					contextAcceptLanguage.getPreferredLocale(),
					transform(
						_analyticsSettingsManager.getSiteIds(
							channel.getChannelId(),
							contextCompany.getCompanyId()),
						_groupLocalService::fetchGroup, Group.class)));
		}

		if (dataSources.length > 1) {
			throw new PortalException("Unable to update multiple data sources");
		}

		DataSource dataSource = dataSources[0];

		if (dataSource.getDataSourceId() == null) {
			dataSource.setDataSourceId(
				analyticsConfiguration::liferayAnalyticsDataSourceId);
		}
		else if (!Objects.equals(
					dataSource.getDataSourceId(),
					analyticsConfiguration.liferayAnalyticsDataSourceId())) {

			throw new PortalException("Invalid data source ID");
		}

		AnalyticsChannel analyticsChannel =
			_analyticsCloudClient.updateAnalyticsChannel(
				channel.getChannelId(),
				transform(
					ArrayUtil.toArray(
						_analyticsSettingsManager.getCommerceChannelIds(
							contextUser.getCompanyId(),
							ArrayUtil.toArray(dataSource.getSiteIds()))),
					commerceChannelId -> _groupLocalService.fetchGroup(
						contextUser.getCompanyId(),
						_commerceChannelClassNameIdSupplier.get(),
						commerceChannelId),
					Group.class),
				_configurationProvider.getCompanyConfiguration(
					AnalyticsConfiguration.class, contextUser.getCompanyId()),
				dataSource.getDataSourceId(),
				contextAcceptLanguage.getPreferredLocale(),
				transform(
					dataSource.getSiteIds(), _groupLocalService::fetchGroup,
					Group.class));

		AnalyticsDataSource analyticsDataSource = _getAnalyticsDataSource(
			GetterUtil.getLong(dataSource.getDataSourceId()),
			analyticsChannel.getAnalyticsDataSources());

		_analyticsSettingsManager.updateCompanyConfiguration(
			contextUser.getCompanyId(),
			HashMapBuilder.<String, Object>put(
				"syncedGroupIds",
				_analyticsSettingsManager.updateSiteIds(
					channel.getChannelId(), contextCompany.getCompanyId(),
					analyticsDataSource.getSiteIds())
			).build());

		_analyticsCloudClient.updateAnalyticsDataSourceDetails(
			null,
			_configurationProvider.getCompanyConfiguration(
				AnalyticsConfiguration.class, contextCompany.getCompanyId()),
			null);

		return _channelDTOConverter.toDTO(
			new ChannelDTOConverterContext(
				analyticsConfiguration.liferayAnalyticsDataSourceId(),
				channel.getChannelId(),
				contextAcceptLanguage.getPreferredLocale()),
			analyticsChannel);
	}

	@Override
	public Channel postChannel(Channel channel) throws Exception {
		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId());

		return _channelDTOConverter.toDTO(
			new ChannelDTOConverterContext(
				analyticsConfiguration.liferayAnalyticsDataSourceId(),
				channel.getChannelId(),
				contextAcceptLanguage.getPreferredLocale()),
			_analyticsCloudClient.addAnalyticsChannel(
				_configurationProvider.getCompanyConfiguration(
					AnalyticsConfiguration.class,
					contextCompany.getCompanyId()),
				channel.getName()));
	}

	@Activate
	protected void activate() {
		_analyticsCloudClient = new AnalyticsCloudClient(_http);
		_commerceChannelClassNameIdSupplier =
			_classNameLocalService.getClassNameIdSupplier(
				"com.liferay.commerce.product.model.CommerceChannel");
	}

	@Reference
	protected DTOConverterRegistry dtoConverterRegistry;

	private AnalyticsDataSource _getAnalyticsDataSource(
		long analyticsDataSourceId,
		AnalyticsDataSource[] analyticsDataSources) {

		for (AnalyticsDataSource analyticsDataSource : analyticsDataSources) {
			if (analyticsDataSource.getId() == analyticsDataSourceId) {
				return analyticsDataSource;
			}
		}

		throw new RuntimeException("Unable to get analytics data source");
	}

	private AnalyticsCloudClient _analyticsCloudClient;

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference(
		target = "(component.name=com.liferay.analytics.settings.rest.internal.dto.v1_0.converter.ChannelDTOConverter)"
	)
	private DTOConverter<AnalyticsChannel, Channel> _channelDTOConverter;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private Supplier<Long> _commerceChannelClassNameIdSupplier;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Http _http;

}
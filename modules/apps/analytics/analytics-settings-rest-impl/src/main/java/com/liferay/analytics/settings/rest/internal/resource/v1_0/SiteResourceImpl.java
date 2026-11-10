/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.resource.v1_0;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.dto.v1_0.Site;
import com.liferay.analytics.settings.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.settings.rest.internal.client.model.AnalyticsChannel;
import com.liferay.analytics.settings.rest.internal.dto.v1_0.converter.SiteDTOConverterContext;
import com.liferay.analytics.settings.rest.internal.util.SortUtil;
import com.liferay.analytics.settings.rest.resource.v1_0.SiteResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Ferrari
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site.properties",
	scope = ServiceScope.PROTOTYPE, service = SiteResource.class
)
public class SiteResourceImpl extends BaseSiteResourceImpl {

	@Override
	public Page<Site> getSitesPage(
			String keywords, Pagination pagination, Sort[] sorts)
		throws Exception {

		Map<Long, String> analyticsChannelsMap = new HashMap<>();

		com.liferay.analytics.settings.rest.internal.client.pagination.Page
			<AnalyticsChannel> page =
				_analyticsCloudClient.getAnalyticsChannelsPage(
					_configurationProvider.getCompanyConfiguration(
						AnalyticsConfiguration.class,
						contextCompany.getCompanyId()),
					null, 0, QueryUtil.ALL_POS, null);

		for (AnalyticsChannel analyticsChannel : page.getItems()) {
			analyticsChannelsMap.put(
				analyticsChannel.getId(), analyticsChannel.getName());
		}

		long[] classNameIds = _getClassNameIds(contextCompany.getCompanyId());

		return Page.of(
			transform(
				_groupService.search(
					contextCompany.getCompanyId(), classNameIds, keywords,
					_getParams(), pagination.getStartPosition(),
					pagination.getEndPosition(),
					SortUtil.getIgnoreCaseOrderByComparator(
						contextAcceptLanguage.getPreferredLocale(), sorts)),
				group -> _siteDTOConverter.toDTO(
					new SiteDTOConverterContext(
						group.getGroupId(),
						contextAcceptLanguage.getPreferredLocale(),
						analyticsChannelsMap),
					group)),
			pagination,
			_groupService.searchCount(
				contextCompany.getCompanyId(), classNameIds, keywords,
				_getParams()));
	}

	@Activate
	protected void activate() {
		_analyticsCloudClient = new AnalyticsCloudClient(_http);
		_classNameIdsSupplier = _classNameLocalService.getClassNameIdsSupplier(
			new String[] {Group.class.getName(), Organization.class.getName()});
	}

	private long[] _getClassNameIds(long companyId) {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", companyId);

		if (objectDefinition == null) {
			return _classNameIdsSupplier.get();
		}

		return ArrayUtil.append(
			_classNameIdsSupplier.get(),
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()));
	}

	private LinkedHashMap<String, Object> _getParams() {
		return LinkedHashMapBuilder.<String, Object>put(
			"active", Boolean.TRUE
		).put(
			"site", Boolean.TRUE
		).build();
	}

	private AnalyticsCloudClient _analyticsCloudClient;
	private Supplier<long[]> _classNameIdsSupplier;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupService _groupService;

	@Reference
	private Http _http;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(
		target = "(component.name=com.liferay.analytics.settings.rest.internal.dto.v1_0.converter.SiteDTOConverter)"
	)
	private DTOConverter<Group, Site> _siteDTOConverter;

}
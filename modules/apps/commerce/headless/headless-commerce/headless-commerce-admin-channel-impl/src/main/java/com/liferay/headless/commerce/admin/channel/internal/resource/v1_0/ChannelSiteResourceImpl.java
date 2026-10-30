/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ChannelSite;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ChannelSiteResource;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Michele Vigilante
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/channel-site.properties",
	scope = ServiceScope.PROTOTYPE, service = ChannelSiteResource.class
)
public class ChannelSiteResourceImpl extends BaseChannelSiteResourceImpl {

	@Override
	public Page<ChannelSite> getChannelAvailableSitesPage(
			Long channelId, String search, Pagination pagination)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(channelId);

		List<Group> groups = _groupService.search(
			commerceChannel.getCompanyId(),
			new long[] {
				_portal.getClassNameId(Group.class.getName()),
				_portal.getClassNameId(Organization.class.getName())
			},
			search,
			LinkedHashMapBuilder.<String, Object>put(
				"active", true
			).put(
				"excludedGroupIds", _getExcludedGroupIds(commerceChannel)
			).put(
				"site", true
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, new GroupNameComparator());

		return Page.of(
			transform(
				ListUtil.subList(
					groups, pagination.getStartPosition(),
					pagination.getEndPosition()),
				group -> _channelSiteDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						group.getGroupId(),
						contextAcceptLanguage.getPreferredLocale()))),
			pagination, groups.size());
	}

	@Override
	public Page<ChannelSite>
			getChannelByExternalReferenceCodeAvailableSitesPage(
				String externalReferenceCode, String search,
				Pagination pagination)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.fetchCommerceChannelByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceChannel == null) {
			throw new NoSuchChannelException(
				"Unable to find channel with external reference code " +
					externalReferenceCode);
		}

		return getChannelAvailableSitesPage(
			commerceChannel.getCommerceChannelId(), search, pagination);
	}

	private List<Long> _getExcludedGroupIds(CommerceChannel commerceChannel) {
		return transform(
			_commerceChannelLocalService.getCommerceChannels(
				commerceChannel.getCompanyId()),
			curCommerceChannel -> {
				if ((curCommerceChannel.getCommerceChannelId() ==
						commerceChannel.getCommerceChannelId()) ||
					(curCommerceChannel.getSiteGroupId() ==
						GroupConstants.DEFAULT_PARENT_GROUP_ID)) {

					return null;
				}

				return curCommerceChannel.getSiteGroupId();
			});
	}

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter.ChannelSiteDTOConverter)"
	)
	private DTOConverter<Group, ChannelSite> _channelSiteDTOConverter;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Portal _portal;

}
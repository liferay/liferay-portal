/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.headless.delivery.dto.v1_0.WikiNode;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.wiki.service.WikiPageService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Miguel Barcos
 */
@Component(
	property = {
		"default=true", "dto.class.name=com.liferay.wiki.model.WikiNode"
	},
	service = DTOConverter.class
)
public class WikiNodeDTOConverter
	implements DTOConverter<com.liferay.wiki.model.WikiNode, WikiNode> {

	@Override
	public String getContentType() {
		return WikiNode.class.getSimpleName();
	}

	@Override
	public WikiNode toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.wiki.model.WikiNode wikiNode)
		throws Exception {

		return new WikiNode() {
			{
				setActions(dtoConverterContext::getActions);
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(wikiNode.getUserId())));
				setDateCreated(wikiNode::getCreateDate);
				setDateModified(wikiNode::getModifiedDate);
				setDescription(wikiNode::getDescription);
				setExternalReferenceCode(wikiNode::getExternalReferenceCode);
				setId(wikiNode::getNodeId);
				setName(wikiNode::getName);
				setNumberOfWikiPages(
					() -> _wikiPageService.getPagesCount(
						wikiNode.getGroupId(), wikiNode.getNodeId(), true));
				setSiteId(wikiNode::getGroupId);
				setSubscribed(
					() -> _subscriptionLocalService.isSubscribed(
						wikiNode.getCompanyId(),
						dtoConverterContext.getUserId(),
						com.liferay.wiki.model.WikiNode.class.getName(),
						wikiNode.getNodeId()));
			}
		};
	}

	@Reference
	private Portal _portal;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WikiPageService _wikiPageService;

}
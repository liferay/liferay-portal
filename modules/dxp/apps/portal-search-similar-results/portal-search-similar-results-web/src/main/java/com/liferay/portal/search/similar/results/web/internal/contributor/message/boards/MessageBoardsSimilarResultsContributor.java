/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.contributor.message.boards;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.search.similar.results.web.internal.builder.AssetTypeUtil;
import com.liferay.portal.search.similar.results.web.internal.contributor.SimilarResultsContributor;
import com.liferay.portal.search.similar.results.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.similar.results.web.internal.util.URLParameterUtil;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteHelper;

import java.util.Arrays;
import java.util.List;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class MessageBoardsSimilarResultsContributor
	implements SimilarResultsContributor {

	public MessageBoardsSimilarResultsContributor(
		AssetEntryLocalService assetEntryLocalService,
		MBCategoryLocalService mbCategoryLocalService,
		MBMessageLocalService mbMessageLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
		_mbCategoryLocalService = mbCategoryLocalService;
		_mbMessageLocalService = mbMessageLocalService;
	}

	@Override
	public void detectRoute(
		RouteBuilder routeBuilder, RouteHelper routeHelper) {

		String[] parameters = URLParameterUtil.getFriendlyURLParameters(
			HttpComponentsUtil.decodePath(routeHelper.getURLString()));

		SearchStringUtil.requireEquals("message_boards", parameters[0]);

		_putAttribute(parameters[1], "type", routeBuilder);
		_putAttribute(Long.valueOf(parameters[2]), "id", routeBuilder);
	}

	@Override
	public void resolveCriteria(
		CriteriaBuilder inputBuilder, CriteriaHelper inputHelper) {

		String type = (String)inputHelper.getRouteParameter("type");
		Long id = (Long)inputHelper.getRouteParameter("id");

		List<?> list = _getMBMessageData(type, id, inputHelper.getGroupId());

		if (list == null) {
			list = _getMBCategoryData(type, id);
		}

		if (list == null) {
			return;
		}

		String className = (String)list.get(0);
		Long classPK = (Long)list.get(1);

		inputBuilder.type(
			className
		).uid(
			Field.getUID(className, String.valueOf(classPK))
		);
	}

	@Override
	public void writeDestination(
		DestinationBuilder destinationBuilder,
		DestinationHelper destinationHelper) {

		String type = (String)destinationHelper.getRouteParameter("type");
		Long id = (Long)destinationHelper.getRouteParameter("id");

		destinationBuilder.replace(
			type,
			AssetTypeUtil.getAssetTypeByClassName(
				destinationHelper.getClassName())
		).replace(
			String.valueOf(id), String.valueOf(destinationHelper.getClassPK())
		);
	}

	protected static final String CATEGORY = "category";

	protected static final String MESSAGE = "message";

	private List<?> _getMBCategoryData(String type, long id) {
		if (!CATEGORY.equals(type)) {
			return null;
		}

		MBCategory mbCategory = _mbCategoryLocalService.fetchMBCategory(id);

		if (mbCategory != null) {
			return Arrays.asList(
				MBCategory.class.getName(), mbCategory.getCategoryId());
		}

		return null;
	}

	private List<?> _getMBMessageData(String type, long id, long groupId) {
		if (!MESSAGE.equals(type)) {
			return null;
		}

		MBMessage mbMessage = _mbMessageLocalService.fetchMBMessage(id);

		if (mbMessage != null) {
			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				groupId, mbMessage.getUuid());

			if (assetEntry != null) {
				return Arrays.asList(
					assetEntry.getClassName(), mbMessage.getRootMessageId());
			}
		}

		return null;
	}

	private void _putAttribute(
		Object value, String name, RouteBuilder routeBuilder) {

		routeBuilder.addAttribute(name, value);
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final MBCategoryLocalService _mbCategoryLocalService;
	private final MBMessageLocalService _mbMessageLocalService;

}
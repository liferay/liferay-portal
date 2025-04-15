/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.search.experiences.SXPBlueprintTitleProvider;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class AddRankingDisplayContext {

	public AddRankingDisplayContext(
		ItemSelector itemSelector, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_itemSelector = itemSelector;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>put(
			"cancelURL", ParamUtil.getString(_renderRequest, "redirect")
		).put(
			"enterpriseSearchEnabled", getEnterpriseSearchEnabled()
		).put(
			"formName", "addResultRankingsFm"
		).put(
			"learnResources",
			LearnMessageUtil.getReactDataJSONObject(
				"portal-search-tuning-rankings-web")
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"selectSitesURL",
			() -> {
				ItemSelectorCriterion itemSelectorCriterion =
					new SiteItemSelectorCriterion();

				itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new GroupItemSelectorReturnType());

				return PortletURLBuilder.create(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_renderRequest),
						_renderResponse.getNamespace() + "selectSite",
						itemSelectorCriterion)
				).buildString();
			}
		).build();
	}

	protected boolean getEnterpriseSearchEnabled() {
		SXPBlueprintTitleProvider sxpBlueprintTitleProvider =
			_sxpBlueprintTitleProviderSnapshot.get();

		if (sxpBlueprintTitleProvider == null) {
			return false;
		}

		return true;
	}

	private static final Snapshot<SXPBlueprintTitleProvider>
		_sxpBlueprintTitleProviderSnapshot = new Snapshot<>(
			RankingPortletDisplayBuilder.class, SXPBlueprintTitleProvider.class,
			null, true);

	private final ItemSelector _itemSelector;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}
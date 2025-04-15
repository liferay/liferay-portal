/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.upgrade.registry;

import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.search.configuration.SemanticSearchConfiguration;
import com.liferay.portal.search.web.constants.SearchBarPortletKeys;
import com.liferay.portal.search.web.constants.SearchResultsPortletKeys;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.search.web.internal.custom.facet.constants.CustomFacetPortletKeys;
import com.liferay.portal.search.web.internal.custom.filter.constants.CustomFilterPortletKeys;
import com.liferay.portal.search.web.internal.folder.facet.constants.FolderFacetPortletKeys;
import com.liferay.portal.search.web.internal.modified.facet.constants.ModifiedFacetPortletKeys;
import com.liferay.portal.search.web.internal.site.facet.constants.SiteFacetPortletKeys;
import com.liferay.portal.search.web.internal.sort.constants.SortPortletKeys;
import com.liferay.portal.search.web.internal.tag.facet.constants.TagFacetPortletKeys;
import com.liferay.portal.search.web.internal.type.facet.constants.TypeFacetPortletKeys;
import com.liferay.portal.search.web.internal.upgrade.v1_0_0.UpgradePortletId;
import com.liferay.portal.search.web.internal.upgrade.v1_0_0.UpgradePortletPreferences;
import com.liferay.portal.search.web.internal.upgrade.v2_0_0.SearchPortletUpgradeProcess;
import com.liferay.portal.search.web.internal.upgrade.v2_1_0.CategoryFacetPortletUpgradeProcess;
import com.liferay.portal.search.web.internal.user.facet.constants.UserFacetPortletKeys;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Petteri Karttunen
 * @author Joshua Cords
 */
@Component(service = UpgradeStepRegistrator.class)
public class SearchWebUpgradeStepRegistrator implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register("0.0.1", "0.0.2", new UpgradePortletId());

		registry.register("0.0.2", "1.0.0", new UpgradePortletPreferences());

		registry.register("1.0.0", "2.0.0", new SearchPortletUpgradeProcess());

		registry.register(
			"2.0.0", "2.0.0.step-1",
			_configurationUpgradeStepFactory.createUpgradeStep(
				"com.liferay.search.experiences.configuration." +
					"SemanticSearchConfiguration",
				SemanticSearchConfiguration.class.getName()));

		registry.register(
			"2.0.0.step-1", "2.0.1",
			_configurationUpgradeStepFactory.createUpgradeStep(
				"com.liferay.search.experiences.configuration." +
					"SemanticSearchConfiguration.scoped",
				SemanticSearchConfiguration.class.getName() + ".scoped"));

		registry.register(
			"2.0.1", "2.0.2",
			new com.liferay.portal.search.web.internal.upgrade.v2_0_2.
				SearchPortletUpgradeProcess());

		registry.register(
			"2.0.2", "2.1.0",
			new CategoryFacetPortletUpgradeProcess(
				_assetVocabularyLocalService, _groupLocalService));

		registry.register(
			"2.1.0", "2.1.1",
			new BaseUpgradePortletPreferences() {

				@Override
				protected String[] getPortletIds() {
					return new String[] {
						CategoryFacetPortletKeys.CATEGORY_FACET + "_INSTANCE_%",
						CustomFacetPortletKeys.CUSTOM_FACET + "_INSTANCE_%",
						CustomFilterPortletKeys.CUSTOM_FILTER + "_INSTANCE_%",
						FolderFacetPortletKeys.FOLDER_FACET + "_INSTANCE_%",
						ModifiedFacetPortletKeys.MODIFIED_FACET + "_INSTANCE_%",
						SearchBarPortletKeys.SEARCH_BAR + "_INSTANCE_%",
						SearchResultsPortletKeys.SEARCH_RESULTS + "_INSTANCE_%",
						SiteFacetPortletKeys.SITE_FACET + "_INSTANCE_%",
						SortPortletKeys.SORT + "_INSTANCE_%",
						TagFacetPortletKeys.TAG_FACET + "_INSTANCE_%",
						TypeFacetPortletKeys.TYPE_FACET + "_INSTANCE_%",
						UserFacetPortletKeys.USER_FACET + "_INSTANCE_%"
					};
				}

				@Override
				protected void upgradePreferences(
						long companyId, long ownerId, int ownerType, long plid,
						String portletId, PortletPreferences portletPreferences)
					throws Exception {
				}

			});
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

	@Reference
	private GroupLocalService _groupLocalService;

}
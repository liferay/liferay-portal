/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.asset.validator;

import com.liferay.asset.kernel.validator.AssetEntryValidatorExclusionRule;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "model.class.name=com.liferay.wiki.model.WikiPage",
	service = AssetEntryValidatorExclusionRule.class
)
public class FrontPageAssetEntryValidatorExclusionRule
	implements AssetEntryValidatorExclusionRule {

	@Override
	public boolean isValidationExcluded(
		long groupId, String className, long classPK, long classTypePK,
		long[] assetCategoryIds, String[] assetTagNames) {

		WikiPage wikiPage = _wikiPageLocalService.fetchWikiPage(classPK);

		if (wikiPage == null) {
			wikiPage = _wikiPageLocalService.fetchPage(classPK);
		}

		if (wikiPage == null) {
			wikiPage = _wikiPageLocalService.fetchPage(classPK, false);
		}

		if ((wikiPage == null) ||
			(wikiPage.getVersion() != WikiPageConstants.VERSION_DEFAULT)) {

			return false;
		}

		try {
			WikiGroupServiceConfiguration wikiGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					WikiGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						wikiPage.getGroupId(), WikiConstants.SERVICE_NAME));

			return StringUtil.equals(
				wikiPage.getTitle(),
				wikiGroupServiceConfiguration.frontPageName());
		}
		catch (ConfigurationException configurationException) {
			throw new SystemException(configurationException);
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(unbind = "-")
	private WikiPageLocalService _wikiPageLocalService;

}
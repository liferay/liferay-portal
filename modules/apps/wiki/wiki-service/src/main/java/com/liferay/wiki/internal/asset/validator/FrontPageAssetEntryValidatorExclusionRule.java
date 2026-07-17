/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.asset.validator;

import com.liferay.asset.kernel.validator.AssetEntryValidatorExclusionRule;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiGroupServiceConfiguration",
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

		if (wikiPage == null) {
			return false;
		}

		if (StringUtil.equals(
				wikiPage.getTitle(),
				_wikiGroupServiceConfiguration.frontPageName()) &&
			(wikiPage.getVersion() == WikiPageConstants.VERSION_DEFAULT)) {

			return true;
		}

		return false;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_wikiGroupServiceConfiguration = ConfigurableUtil.createConfigurable(
			WikiGroupServiceConfiguration.class, properties);
	}

	private volatile WikiGroupServiceConfiguration
		_wikiGroupServiceConfiguration;

	@Reference(unbind = "-")
	private WikiPageLocalService _wikiPageLocalService;

}
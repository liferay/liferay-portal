/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.display.url.provider;

import com.liferay.asset.info.display.url.provider.AssetInfoEditURLProvider;
import com.liferay.info.display.url.provider.InfoEditURLProvider;
import com.liferay.journal.model.JournalArticle;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalArticle",
	service = InfoEditURLProvider.class
)
public class JournalAssetInfoEditURLProvider
	implements InfoEditURLProvider<JournalArticle> {

	@Override
	public String getURL(
			JournalArticle article, HttpServletRequest httpServletRequest)
		throws Exception {

		return _assetInfoEditURLProvider.getURL(
			JournalArticle.class.getName(), article.getResourcePrimKey(),
			httpServletRequest);
	}

	@Reference
	private AssetInfoEditURLProvider _assetInfoEditURLProvider;

}
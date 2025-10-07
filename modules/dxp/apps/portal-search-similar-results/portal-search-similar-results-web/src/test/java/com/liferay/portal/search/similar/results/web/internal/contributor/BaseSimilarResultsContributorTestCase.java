/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.contributor;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationHelper;
import com.liferay.portal.util.PortalImpl;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public abstract class BaseSimilarResultsContributorTestCase {

	protected AssetEntry setUpAssetEntry(long entryId) {
		AssetEntry assetEntry = Mockito.mock(AssetEntry.class);

		Mockito.doReturn(
			entryId
		).when(
			assetEntry
		).getEntryId();

		return assetEntry;
	}

	protected AssetEntry setUpAssetEntry(String className) {
		AssetEntry assetEntry = Mockito.mock(AssetEntry.class);

		Mockito.doReturn(
			className
		).when(
			assetEntry
		).getClassName();

		return assetEntry;
	}

	protected AssetEntry setUpAssetEntry(String className, long classPK) {
		AssetEntry assetEntry = setUpAssetEntry(className);

		Mockito.doReturn(
			classPK
		).when(
			assetEntry
		).getClassPK();

		return assetEntry;
	}

	protected void setUpAssetEntryLocalServiceFetchAssetEntry(
		AssetEntry assetEntry) {

		Mockito.doReturn(
			assetEntry
		).when(
			assetEntryLocalService
		).fetchAssetEntry(
			Mockito.anyLong()
		);
	}

	protected void setUpAssetEntryLocalServiceFetchEntry(
		AssetEntry assetEntry) {

		Mockito.doReturn(
			assetEntry
		).when(
			assetEntryLocalService
		).fetchEntry(
			Mockito.anyLong(), Mockito.nullable(String.class)
		);

		Mockito.doReturn(
			assetEntry
		).when(
			assetEntryLocalService
		).fetchEntry(
			Mockito.anyLong(), Mockito.anyLong()
		);
	}

	protected AssetRenderer<?> setUpAssetRenderer(WikiPage wikiPage) {
		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			wikiPage
		).when(
			assetRenderer
		).getAssetObject();

		return assetRenderer;
	}

	protected void setUpCriteriaHelper(long value) {
		Mockito.doReturn(
			value
		).when(
			criteriaHelper
		).getGroupId();
	}

	protected void setUpCriteriaHelper(
		String parameterName, long parameterValue) {

		Mockito.doReturn(
			parameterValue
		).when(
			criteriaHelper
		).getRouteParameter(
			Mockito.eq(parameterName)
		);
	}

	protected void setUpCriteriaHelper(
		String parameterName, String parameterValue) {

		Mockito.doReturn(
			parameterValue
		).when(
			criteriaHelper
		).getRouteParameter(
			Mockito.eq(parameterName)
		);
	}

	protected void setUpDestinationHelper(AssetEntry assetEntry) {
		Mockito.doReturn(
			assetEntry
		).when(
			destinationHelper
		).getAssetEntry();
	}

	protected void setUpDestinationHelper(AssetRenderer<?> assetRenderer) {
		Mockito.doReturn(
			assetRenderer
		).when(
			destinationHelper
		).getAssetRenderer();
	}

	protected void setUpDestinationHelper(String className) {
		Mockito.doReturn(
			className
		).when(
			destinationHelper
		).getClassName();
	}

	protected void setUpDestinationHelperGetRouteParameter(
		long entryId, String parameterName) {

		Mockito.doReturn(
			entryId
		).when(
			destinationHelper
		).getRouteParameter(
			Mockito.eq(parameterName)
		);
	}

	protected void setUpDestinationHelperGetRouteParameter(
		String parameterName, String parameterValue) {

		Mockito.doReturn(
			parameterValue
		).when(
			destinationHelper
		).getRouteParameter(
			Mockito.eq(parameterName)
		);
	}

	protected void setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	protected UIDFactory setUpUIDFactory(String uid) {
		Mockito.doReturn(
			uid
		).when(
			uidFactory
		).getUID(
			Mockito.nullable(ClassedModel.class)
		);

		return uidFactory;
	}

	protected WikiNode setUpWikiNode(String name) {
		WikiNode wikiNode = Mockito.mock(WikiNode.class);

		Mockito.doReturn(
			name
		).when(
			wikiNode
		).getName();

		return wikiNode;
	}

	protected void setUpWikiNodeLocalService(WikiNode wikiNode) {
		Mockito.doReturn(
			wikiNode
		).when(
			wikiNodeLocalService
		).fetchNode(
			Mockito.anyLong(), Mockito.nullable(String.class)
		);
	}

	protected WikiPage setUpWikiPage(String title, WikiNode wikiNode) {
		WikiPage wikiPage = Mockito.mock(WikiPage.class);

		Mockito.doReturn(
			wikiNode
		).when(
			wikiPage
		).getNode();

		Mockito.doReturn(
			title
		).when(
			wikiPage
		).getTitle();

		return wikiPage;
	}

	protected void setUpWikiPageLocalService(WikiPage wikiPage) {
		Mockito.doReturn(
			wikiPage
		).when(
			wikiPageLocalService
		).fetchPage(
			Mockito.anyLong(), Mockito.nullable(String.class),
			Mockito.anyDouble()
		);
	}

	protected AssetEntryLocalService assetEntryLocalService = Mockito.mock(
		AssetEntryLocalService.class);
	protected BlogsEntryLocalService blogsEntryLocalService = Mockito.mock(
		BlogsEntryLocalService.class);
	protected CriteriaHelper criteriaHelper = Mockito.mock(
		CriteriaHelper.class);
	protected DestinationHelper destinationHelper = Mockito.mock(
		DestinationHelper.class);
	protected DLFileEntryLocalService dlFileEntryLocalService = Mockito.mock(
		DLFileEntryLocalService.class);
	protected DLFolderLocalService dlFolderLocalService = Mockito.mock(
		DLFolderLocalService.class);
	protected MBCategoryLocalService mbCategoryLocalService = Mockito.mock(
		MBCategoryLocalService.class);
	protected MBMessageLocalService mbMessageLocalService = Mockito.mock(
		MBMessageLocalService.class);
	protected UIDFactory uidFactory = Mockito.mock(UIDFactory.class);
	protected WikiNodeLocalService wikiNodeLocalService = Mockito.mock(
		WikiNodeLocalService.class);
	protected WikiPageLocalService wikiPageLocalService = Mockito.mock(
		WikiPageLocalService.class);

}
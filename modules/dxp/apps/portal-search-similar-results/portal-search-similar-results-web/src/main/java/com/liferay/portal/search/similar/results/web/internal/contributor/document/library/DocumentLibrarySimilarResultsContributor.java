/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.contributor.document.library;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
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
public class DocumentLibrarySimilarResultsContributor
	implements SimilarResultsContributor {

	public DocumentLibrarySimilarResultsContributor(
		AssetEntryLocalService assetEntryLocalService,
		DLFileEntryLocalService dlFileEntryLocalService,
		DLFolderLocalService dlFolderLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_dlFolderLocalService = dlFolderLocalService;
	}

	@Override
	public void detectRoute(
		RouteBuilder routeBuilder, RouteHelper routeHelper) {

		String[] parameters = URLParameterUtil.getFriendlyURLParameters(
			HttpComponentsUtil.decodePath(routeHelper.getURLString()));

		SearchStringUtil.requireEquals("document_library", parameters[0]);

		routeBuilder.addAttribute("id", Long.valueOf(parameters[3]));
	}

	@Override
	public void resolveCriteria(
		CriteriaBuilder criteriaBuilder, CriteriaHelper criteriaHelper) {

		Long id = (Long)criteriaHelper.getRouteParameter("id");

		List<?> list = _getDLFileEntryData(id);

		if (list == null) {
			list = _getDLFolderData(id);
		}

		if (list == null) {
			return;
		}

		Long classPK = (Long)list.get(0);

		String uuid = (String)list.get(1);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			criteriaHelper.getGroupId(), uuid);

		if (assetEntry == null) {
			return;
		}

		criteriaBuilder.type(
			assetEntry.getClassName()
		).uid(
			Field.getUID(assetEntry.getClassName(), String.valueOf(classPK))
		);
	}

	@Override
	public void writeDestination(
		DestinationBuilder destinationBuilder,
		DestinationHelper destinationHelper) {

		AssetRenderer<?> assetRenderer = destinationHelper.getAssetRenderer();

		Long id1 = _getId(
			destinationHelper.getClassName(), assetRenderer.getAssetObject());

		if (id1 == null) {
			return;
		}

		Long id2 = (Long)destinationHelper.getRouteParameter("id");

		destinationBuilder.replace(String.valueOf(id2), String.valueOf(id1));
	}

	private List<?> _getDLFileEntryData(Long id) {
		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(id);

		if (dlFileEntry != null) {
			return Arrays.asList(
				dlFileEntry.getFileEntryId(), dlFileEntry.getUuid());
		}

		return null;
	}

	private List<?> _getDLFolderData(Long id) {
		DLFolder dlFolder = _dlFolderLocalService.fetchDLFolder(id);

		if (dlFolder != null) {
			return Arrays.asList(dlFolder.getFolderId(), dlFolder.getUuid());
		}

		return null;
	}

	private long _getFileEntryId(Object assetObject) {
		if (assetObject instanceof FileEntry) {
			FileEntry fileEntry = (FileEntry)assetObject;

			return fileEntry.getFileEntryId();
		}

		DLFileEntry dlFileEntry = (DLFileEntry)assetObject;

		return dlFileEntry.getFileEntryId();
	}

	private long _getFolderId(Object assetObject) {
		if (assetObject instanceof Folder) {
			Folder folder = (Folder)assetObject;

			return folder.getFolderId();
		}

		DLFolder dlFolder = (DLFolder)assetObject;

		return dlFolder.getFolderId();
	}

	private Long _getId(String className, Object assetObject) {
		if (className.equals(DLFileEntry.class.getName())) {
			return _getFileEntryId(assetObject);
		}

		if (className.equals(DLFolder.class.getName())) {
			return _getFolderId(assetObject);
		}

		return null;
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final DLFolderLocalService _dlFolderLocalService;

}
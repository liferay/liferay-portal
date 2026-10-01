/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.item.selector.internal;

import com.liferay.adaptive.media.image.item.selector.AMImageURLItemSelectorReturnType;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.media.query.MediaQueryProvider;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.util.RepositoryUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "service.ranking:Integer=100",
	service = ItemSelectorReturnTypeResolver.class
)
public class FileEntryAMImageURLItemSelectorReturnTypeResolver
	implements ItemSelectorReturnTypeResolver
		<AMImageURLItemSelectorReturnType, FileEntry> {

	@Override
	public Class<AMImageURLItemSelectorReturnType>
		getItemSelectorReturnTypeClass() {

		return AMImageURLItemSelectorReturnType.class;
	}

	@Override
	public Class<FileEntry> getModelClass() {
		return FileEntry.class;
	}

	@Override
	public String getValue(FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception {

		String previewURL = null;

		long repositoryId = fileEntry.getRepositoryId();

		if (RepositoryUtil.isExternalRepository(repositoryId) ||
			(fileEntry.getGroupId() == repositoryId)) {

			previewURL = _dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK, false, false);
		}
		else {
			previewURL = _portletFileRepository.getPortletFileEntryURL(
				themeDisplay, fileEntry, "&imagePreview=1", false);
		}

		return JSONUtil.put(
			"defaultSource", previewURL
		).put(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId())
		).put(
			"sources",
			JSONUtil.toJSONArray(
				_mediaQueryProvider.getMediaQueries(fileEntry),
				this::_getSourceJSONObject)
		).toString();
	}

	private JSONObject _getSourceJSONObject(MediaQuery mediaQuery) {
		return JSONUtil.put(
			"attributes",
			() -> {
				JSONObject attributesJSONObject =
					_jsonFactory.createJSONObject();

				for (Condition condition : mediaQuery.getConditions()) {
					attributesJSONObject.put(
						condition.getAttribute(), condition.getValue());
				}

				return attributesJSONObject;
			}
		).put(
			"src", mediaQuery.getSrc()
		);
	}

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MediaQueryProvider _mediaQueryProvider;

	@Reference
	private PortletFileRepository _portletFileRepository;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal.video;

import com.liferay.document.library.item.selector.web.internal.BaseDLItemSelectorView;
import com.liferay.document.library.item.selector.web.internal.constants.DLItemSelectorViewConstants;
import com.liferay.document.library.item.selector.web.internal.display.context.DLItemSelectorViewDisplayContext;
import com.liferay.document.library.kernel.processor.VideoProcessorUtil;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.VideoEmbeddableHTMLItemSelectorReturnType;
import com.liferay.item.selector.criteria.video.criterion.VideoItemSelectorCriterion;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"item.selector.view.key=" + DLItemSelectorViewConstants.DL_VIDEO_ITEM_SELECTOR_VIEW_KEY,
		"item.selector.view.order:Integer=100"
	},
	service = ItemSelectorView.class
)
public class DLVideoItemSelectorView
	extends BaseDLItemSelectorView<VideoItemSelectorCriterion> {

	@Override
	public Class<VideoItemSelectorCriterion> getItemSelectorCriterionClass() {
		return VideoItemSelectorCriterion.class;
	}

	@Override
	public String[] getMimeTypes() {
		String[] mimeTypes = {
			ContentTypes.APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML
		};

		if (VideoProcessorUtil.isAvailable()) {
			mimeTypes = ArrayUtil.append(
				mimeTypes, PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_MIME_TYPES);
		}

		return mimeTypes;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	protected void prepareDLItemSelectorViewDisplayContext(
		DLItemSelectorViewDisplayContext dlItemSelectorViewDisplayContext) {

		dlItemSelectorViewDisplayContext.setShowDragAndDropZone(
			VideoProcessorUtil.isAvailable());
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new FileEntryItemSelectorReturnType(),
				new URLItemSelectorReturnType(),
				new VideoEmbeddableHTMLItemSelectorReturnType()));

}
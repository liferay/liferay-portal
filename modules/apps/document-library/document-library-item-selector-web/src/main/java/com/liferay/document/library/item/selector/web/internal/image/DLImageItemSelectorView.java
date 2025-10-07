/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal.image;

import com.liferay.document.library.item.selector.web.internal.BaseDLItemSelectorView;
import com.liferay.document.library.item.selector.web.internal.configuration.DLImageItemSelectorViewConfiguration;
import com.liferay.document.library.item.selector.web.internal.constants.DLItemSelectorViewConstants;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.DownloadFileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.DownloadURLItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.document.library.item.selector.web.internal.configuration.DLImageItemSelectorViewConfiguration",
	property = {
		"item.selector.view.key=" + DLItemSelectorViewConstants.DL_IMAGE_ITEM_SELECTOR_VIEW_KEY,
		"item.selector.view.order:Integer=100"
	},
	service = ItemSelectorView.class
)
public class DLImageItemSelectorView
	extends BaseDLItemSelectorView<ImageItemSelectorCriterion> {

	@Override
	public String[] getExtensions() {
		return _dlImageItemSelectorViewConfiguration.validExtensions();
	}

	@Override
	public Class<ImageItemSelectorCriterion> getItemSelectorCriterionClass() {
		return ImageItemSelectorCriterion.class;
	}

	@Override
	public String[] getMimeTypes() {
		return _mimeTypes;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlImageItemSelectorViewConfiguration =
			ConfigurableUtil.createConfigurable(
				DLImageItemSelectorViewConfiguration.class, properties);

		_mimeTypes = ArrayUtil.append(
			PropsValues.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES,
			ContentTypes.IMAGE_SVG_XML);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new DownloadFileEntryItemSelectorReturnType(),
				new DownloadURLItemSelectorReturnType(),
				new FileEntryItemSelectorReturnType(),
				new URLItemSelectorReturnType()));

	private volatile DLImageItemSelectorViewConfiguration
		_dlImageItemSelectorViewConfiguration;
	private volatile String[] _mimeTypes;

}
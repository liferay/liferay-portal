/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.info.collection.provider.RepeatableFieldInfoItemCollectionProvider;
import com.liferay.info.collection.provider.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.RepeatableFieldInfoCollectionProviderVerticalCard;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;

import jakarta.portlet.RenderRequest;

import java.util.Locale;

/**
 * @author Víctor Galán
 */
public class RepeatableFieldInfoCollectionProviderItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public RepeatableFieldInfoCollectionProviderItemDescriptor(
		InfoFieldSetEntry infoFieldSetEntry, String itemType,
		String itemSubtype, Locale locale) {

		_infoFieldSetEntry = infoFieldSetEntry;
		_itemType = itemType;
		_itemSubtype = itemSubtype;
		_locale = locale;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"fieldName", _infoFieldSetEntry.getUniqueId()
		).put(
			"itemSubtype", _itemSubtype
		).put(
			"itemType", _itemType
		).put(
			"key", RepeatableFieldInfoItemCollectionProvider.class.getName()
		).put(
			"title", _infoFieldSetEntry.getLabel(_locale)
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return null;
	}

	@Override
	public String getTitle(Locale locale) {
		return null;
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new RepeatableFieldInfoCollectionProviderVerticalCard(
			renderRequest, _infoFieldSetEntry, rowChecker);
	}

	private final InfoFieldSetEntry _infoFieldSetEntry;
	private final String _itemSubtype;
	private final String _itemType;
	private final Locale _locale;

}
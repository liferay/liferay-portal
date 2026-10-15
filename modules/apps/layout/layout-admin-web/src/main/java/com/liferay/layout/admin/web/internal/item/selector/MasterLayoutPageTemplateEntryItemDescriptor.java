/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib.SelectMasterLayoutVerticalCard;
import com.liferay.layout.admin.web.internal.util.MasterLayoutUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import jakarta.portlet.RenderRequest;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class MasterLayoutPageTemplateEntryItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public MasterLayoutPageTemplateEntryItemDescriptor(
		boolean defaultTemplate,
		LayoutPageTemplateEntry layoutPageTemplateEntry, long scopeGroupId) {

		_defaultTemplate = defaultTemplate;
		_layoutPageTemplateEntry = layoutPageTemplateEntry;
		_scopeGroupId = scopeGroupId;
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
			"masterLayoutPageTemplateEntryERC",
			_layoutPageTemplateEntry.getExternalReferenceCode()
		).put(
			"masterLayoutPageTemplateEntryScopeERC",
			MasterLayoutUtil.getMasterLayoutPageTemplateEntryScopeERC(
				_scopeGroupId, _layoutPageTemplateEntry)
		).put(
			"masterLayoutPageTemplateEntryScopeLabel",
			MasterLayoutUtil.getMasterLayoutPageTemplateEntryScopeLabel(
				_scopeGroupId, _layoutPageTemplateEntry,
				LocaleThreadLocal.getThemeDisplayLocale())
		).put(
			"name", _layoutPageTemplateEntry.getName()
		).put(
			"plid", String.valueOf(_layoutPageTemplateEntry.getPlid())
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new SelectMasterLayoutVerticalCard(
			_defaultTemplate, _layoutPageTemplateEntry, renderRequest,
			_scopeGroupId);
	}

	private final boolean _defaultTemplate;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final long _scopeGroupId;

}
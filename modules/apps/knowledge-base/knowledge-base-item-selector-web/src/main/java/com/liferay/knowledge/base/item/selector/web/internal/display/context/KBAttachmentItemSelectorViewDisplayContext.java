/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.item.selector.web.internal.display.context;

import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.taglib.servlet.taglib.util.RepositoryEntryBrowserTagUtil;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.item.selector.KBAttachmentItemSelectorCriterion;
import com.liferay.knowledge.base.item.selector.web.internal.KBAttachmentItemSelectorView;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Roberto Díaz
 */
public class KBAttachmentItemSelectorViewDisplayContext {

	public KBAttachmentItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		ItemSelectorReturnTypeResolverHandler
			itemSelectorReturnTypeResolverHandler,
		KBAttachmentItemSelectorCriterion kbAttachmentItemSelectorCriterion,
		KBAttachmentItemSelectorView kbAttachmentItemSelectorView,
		PortletURL portletURL, boolean search) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_itemSelectorReturnTypeResolverHandler =
			itemSelectorReturnTypeResolverHandler;
		_kbAttachmentItemSelectorCriterion = kbAttachmentItemSelectorCriterion;
		_kbAttachmentItemSelectorView = kbAttachmentItemSelectorView;
		_portletURL = portletURL;
		_search = search;

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			httpServletRequest);
	}

	public long getAttachmentsFolderId() throws PortalException {
		KBArticle kbArticle = KBArticleLocalServiceUtil.getLatestKBArticle(
			_kbAttachmentItemSelectorCriterion.getResourcePrimKey(),
			WorkflowConstants.STATUS_ANY);

		return kbArticle.getAttachmentsFolderId();
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public ItemSelectorReturnTypeResolver<?, ?>
		getItemSelectorReturnTypeResolver() {

		return _itemSelectorReturnTypeResolverHandler.
			getItemSelectorReturnTypeResolver(
				_kbAttachmentItemSelectorCriterion,
				_kbAttachmentItemSelectorView, FileEntry.class);
	}

	public KBAttachmentItemSelectorCriterion
		getKBAttachmentItemSelectorCriterion() {

		return _kbAttachmentItemSelectorCriterion;
	}

	public OrderByComparator<FileEntry> getOrderByComparator() {
		return DLUtil.getRepositoryModelOrderByComparator(
			RepositoryEntryBrowserTagUtil.getOrderByCol(
				_httpServletRequest, _portalPreferences),
			RepositoryEntryBrowserTagUtil.getOrderByType(
				_httpServletRequest, _portalPreferences));
	}

	public PortletURL getPortletURL(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		return PortletURLBuilder.create(
			PortletURLUtil.clone(_portletURL, liferayPortletResponse)
		).setParameter(
			"selectedTab", getTitle(httpServletRequest.getLocale())
		).buildPortletURL();
	}

	public String getTitle(Locale locale) {
		return _kbAttachmentItemSelectorView.getTitle(locale);
	}

	public PortletURL getUploadURL(
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, KBPortletKeys.KNOWLEDGE_BASE_ADMIN
		).setActionName(
			"/knowledge_base/upload_kb_article_attachments"
		).setParameter(
			"resourcePrimKey",
			_kbAttachmentItemSelectorCriterion.getResourcePrimKey()
		).buildPortletURL();
	}

	public boolean isSearch() {
		return _search;
	}

	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private final ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;
	private final KBAttachmentItemSelectorCriterion
		_kbAttachmentItemSelectorCriterion;
	private final KBAttachmentItemSelectorView _kbAttachmentItemSelectorView;
	private final PortalPreferences _portalPreferences;
	private final PortletURL _portletURL;
	private final boolean _search;

}
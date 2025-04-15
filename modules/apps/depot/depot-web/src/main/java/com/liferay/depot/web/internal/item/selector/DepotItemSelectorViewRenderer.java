/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector;

import com.liferay.depot.web.internal.application.controller.DepotApplicationController;
import com.liferay.depot.web.internal.constants.DepotAdminWebKeys;
import com.liferay.depot.web.internal.display.context.DepotApplicationDisplayContext;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewRenderer;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.PortalIncludeUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

import java.util.List;

/**
 * @author Alicia García
 */
public class DepotItemSelectorViewRenderer implements ItemSelectorViewRenderer {

	public DepotItemSelectorViewRenderer(
		String className, DepotApplicationController depotApplicationController,
		ItemSelectorViewRenderer itemSelectorViewRenderer, Portal portal,
		List<String> portletIds, ServletContext servletContext) {

		_className = className;
		_depotApplicationController = depotApplicationController;
		_itemSelectorViewRenderer = itemSelectorViewRenderer;
		_portal = portal;
		_portletIds = portletIds;
		_servletContext = servletContext;
	}

	@Override
	public String getItemSelectedEventName() {
		return _itemSelectorViewRenderer.getItemSelectedEventName();
	}

	@Override
	public ItemSelectorCriterion getItemSelectorCriterion() {
		return _itemSelectorViewRenderer.getItemSelectorCriterion();
	}

	@Override
	public ItemSelectorView<ItemSelectorCriterion> getItemSelectorView() {
		return _itemSelectorViewRenderer.getItemSelectorView();
	}

	@Override
	public PortletURL getPortletURL() {
		return _itemSelectorViewRenderer.getPortletURL();
	}

	@Override
	public void renderHTML(PageContext pageContext)
		throws IOException, ServletException {

		PortalIncludeUtil.include(
			pageContext,
			(httpServletRequest, httpServletResponse) -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				Group scopeGroup = themeDisplay.getScopeGroup();

				if (!scopeGroup.isDepot()) {
					_itemSelectorViewRenderer.renderHTML(pageContext);

					return;
				}

				long groupId = scopeGroup.getGroupId();

				String portletId = _getPortletId(groupId);

				if (_depotApplicationController.isEnabled(portletId, groupId)) {
					_itemSelectorViewRenderer.renderHTML(pageContext);

					return;
				}

				RequestDispatcher requestDispatcher =
					_servletContext.getRequestDispatcher(
						"/item/selector/application_disabled.jsp");

				DepotApplicationDisplayContext depotApplicationDisplayContext =
					new DepotApplicationDisplayContext(
						httpServletRequest, _portal);

				depotApplicationDisplayContext.setPortletId(portletId);
				depotApplicationDisplayContext.setPortletURL(
					_itemSelectorViewRenderer.getPortletURL());

				httpServletRequest.setAttribute(
					DepotAdminWebKeys.DEPOT_APPLICATION_DISPLAY_CONTEXT,
					depotApplicationDisplayContext);

				requestDispatcher.include(
					httpServletRequest, httpServletResponse);
			});
	}

	private String _getPortletId(long groupId) {
		if (ListUtil.isEmpty(_portletIds)) {
			return _getPortletId(_className);
		}

		for (String portletId : _portletIds) {
			if (_depotApplicationController.isEnabled(portletId, groupId)) {
				return portletId;
			}
		}

		return StringPool.BLANK;
	}

	private String _getPortletId(String className) {
		if (className.equals(DLFileEntryConstants.getClassName()) ||
			className.equals(DLFolderConstants.getClassName()) ||
			className.equals(FileEntry.class.getName()) ||
			className.equals(Folder.class.getName())) {

			return DLPortletKeys.DOCUMENT_LIBRARY_ADMIN;
		}
		else if (className.equals(JournalArticle.class.getName()) ||
				 className.equals(JournalFolder.class.getName())) {

			return JournalPortletKeys.JOURNAL;
		}

		return StringPool.BLANK;
	}

	private final String _className;
	private final DepotApplicationController _depotApplicationController;
	private final ItemSelectorViewRenderer _itemSelectorViewRenderer;
	private final Portal _portal;
	private final List<String> _portletIds;
	private final ServletContext _servletContext;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal;

import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.item.selector.web.internal.constants.DLItemSelectorWebKeys;
import com.liferay.document.library.item.selector.web.internal.display.context.DLItemSelectorViewDisplayContext;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
public abstract class BaseDLItemSelectorView<T extends ItemSelectorCriterion>
	implements DLItemSelectorView<T> {

	@Override
	public String[] getExtensions() {
		return new String[0];
	}

	@Override
	public String[] getMimeTypes() {
		return new String[0];
	}

	@Override
	public List<String> getPortletIds() {
		return _portletIds;
	}

	@Override
	public String getTitle(Locale locale) {
		ResourceBundleLoader resourceBundleLoader =
			LanguageResources.PORTAL_RESOURCE_BUNDLE_LOADER;

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			locale);

		return ResourceBundleUtil.getString(
			resourceBundle, "documents-and-media");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse, T t,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/documents.jsp");

		DLItemSelectorViewDisplayContext dlItemSelectorViewDisplayContext =
			new DLItemSelectorViewDisplayContext<>(
				assetVocabularyService, classNameLocalService,
				dlFileEntryTypeLocalService, this,
				folderModelResourcePermission,
				(HttpServletRequest)servletRequest, t, itemSelectedEventName,
				itemSelectorReturnTypeResolverHandler, portletURL, search,
				stagingGroupHelper);

		prepareDLItemSelectorViewDisplayContext(
			dlItemSelectorViewDisplayContext);

		servletRequest.setAttribute(
			DLItemSelectorWebKeys.DL_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT,
			dlItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	protected void prepareDLItemSelectorViewDisplayContext(
		DLItemSelectorViewDisplayContext dlItemSelectorViewDisplayContext) {
	}

	@Reference
	protected AssetVocabularyService assetVocabularyService;

	@Reference
	protected ClassNameLocalService classNameLocalService;

	@Reference
	protected DLFileEntryTypeLocalService dlFileEntryTypeLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	protected ModelResourcePermission<Folder> folderModelResourcePermission;

	@Reference
	protected ItemSelectorReturnTypeResolverHandler
		itemSelectorReturnTypeResolverHandler;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.item.selector.web)"
	)
	protected ServletContext servletContext;

	@Reference
	protected StagingGroupHelper stagingGroupHelper;

	private static final List<String> _portletIds = Arrays.asList(
		DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, DLPortletKeys.DOCUMENT_LIBRARY);

}
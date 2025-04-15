/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.collection.filter.internal;

import com.liferay.fragment.collection.filter.FragmentCollectionFilterRegistry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.renderer.collection.filter.internal.display.context.CollectionAppliedFiltersFragmentRendererDisplayContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pablo Molina
 */
@Component(service = FragmentRenderer.class)
public class CollectionAppliedFiltersFragmentRenderer
	implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-display";
	}

	@Override
	public String getIcon() {
		return "filter";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "applied-filters");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		httpServletRequest.setAttribute(
			CollectionAppliedFiltersFragmentRendererDisplayContext.class.
				getName(),
			new CollectionAppliedFiltersFragmentRendererDisplayContext(
				_fragmentCollectionFilterRegistry,
				_fragmentEntryConfigurationParser,
				_fragmentEntryLinkLocalService, fragmentRendererContext,
				httpServletRequest));

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/applied_filters.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error("Unable to render applied filters fragment", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CollectionAppliedFiltersFragmentRenderer.class);

	@Reference
	private FragmentCollectionFilterRegistry _fragmentCollectionFilterRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.renderer.collection.filter.impl)"
	)
	private ServletContext _servletContext;

}
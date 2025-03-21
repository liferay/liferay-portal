/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.item.selector;

import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.PortletItemSelectorView;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.JournalArticleItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.constants.JournalWebConstants;
import com.liferay.journal.web.internal.display.context.JournalArticleItemSelectorViewDisplayContext;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.Portal;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.journal.web.internal.configuration.JournalWebConfiguration",
	property = "item.selector.view.order:Integer=100",
	service = ItemSelectorView.class
)
public class JournalArticleItemSelectorView
	implements InfoItemSelectorView,
			   PortletItemSelectorView<InfoItemItemSelectorCriterion> {

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public Class<InfoItemItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoItemItemSelectorCriterion.class;
	}

	@Override
	public List<String> getPortletIds() {
		return _portletIds;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "web-content");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				"/item/selector/select_articles.jsp");

		JournalArticleItemSelectorViewDisplayContext
			journalItemSelectorViewDisplayContext =
				new JournalArticleItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest,
					infoItemItemSelectorCriterion, itemSelectedEventName, this,
					_journalWebConfiguration, _portal, portletURL,
					_resourcePermissionLocalService, _roleLocalService, search,
					_stagingGroupHelper);

		servletRequest.setAttribute(
			JournalWebConstants.
				JOURNAL_ARTICLE_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT,
			journalItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_journalWebConfiguration = ConfigurableUtil.createConfigurable(
			JournalWebConfiguration.class, properties);
	}

	private static final List<String> _portletIds = Collections.singletonList(
		JournalPortletKeys.JOURNAL);
	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Arrays.asList(
			new InfoItemItemSelectorReturnType(),
			new JournalArticleItemSelectorReturnType());

	private volatile JournalWebConfiguration _journalWebConfiguration;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.journal.web)")
	private ServletContext _servletContext;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}
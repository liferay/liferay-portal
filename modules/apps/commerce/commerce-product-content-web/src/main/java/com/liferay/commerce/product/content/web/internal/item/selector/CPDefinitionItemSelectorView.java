/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.item.selector;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "item.selector.view.order:Integer=600",
	service = ItemSelectorView.class
)
public class CPDefinitionItemSelectorView
	implements InfoItemSelectorView,
			   ItemSelectorView<InfoItemItemSelectorCriterion> {

	@Override
	public String getClassName() {
		return CPDefinition.class.getName();
	}

	@Override
	public Class<InfoItemItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoItemItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "products");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, infoItemItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new CPDefinitionItemSelectorViewDescriptor(
				(HttpServletRequest)servletRequest,
				infoItemItemSelectorCriterion, portletURL));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new InfoItemItemSelectorReturnType());

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private ItemSelectorViewDescriptorRenderer<InfoItemItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private class CPDefinitionItemDescriptor
		implements ItemSelectorViewDescriptor.ItemDescriptor {

		public CPDefinitionItemDescriptor(
			CPDefinition cpDefinition, HttpServletRequest httpServletRequest) {

			_cpDefinition = cpDefinition;
			_httpServletRequest = httpServletRequest;
		}

		@Override
		public String getIcon() {
			return null;
		}

		@Override
		public String getImageURL() {
			try {
				return _cpDefinition.getDefaultImageThumbnailSrc(
					CommerceUtil.getCommerceAccountId(
						(CommerceContext)_httpServletRequest.getAttribute(
							CommerceWebKeys.COMMERCE_CONTEXT)));
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}

		@Override
		public Date getModifiedDate() {
			return _cpDefinition.getModifiedDate();
		}

		@Override
		public String getPayload() {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return JSONUtil.put(
				"className", CPDefinition.class.getName()
			).put(
				"classNameId",
				_portal.getClassNameId(CPDefinition.class.getName())
			).put(
				"classPK", _cpDefinition.getCPDefinitionId()
			).put(
				"title", _cpDefinition.getName(themeDisplay.getLanguageId())
			).put(
				"type",
				ResourceActionsUtil.getModelResource(
					themeDisplay.getLocale(), CPDefinition.class.getName())
			).toString();
		}

		@Override
		public String getSubtitle(Locale locale) {
			Date modifiedDate = _cpDefinition.getModifiedDate();

			return _language.format(
				locale, "x-ago-by-x",
				new Object[] {
					_language.getTimeDescription(
						locale,
						System.currentTimeMillis() - modifiedDate.getTime(),
						true),
					_cpDefinition.getUserName()
				});
		}

		@Override
		public String getTitle(Locale locale) {
			return _cpDefinition.getName(locale.getLanguage());
		}

		@Override
		public long getUserId() {
			return _cpDefinition.getUserId();
		}

		@Override
		public String getUserName() {
			return _cpDefinition.getUserName();
		}

		private final CPDefinition _cpDefinition;
		private HttpServletRequest _httpServletRequest;

	}

	private class CPDefinitionItemSelectorViewDescriptor
		implements ItemSelectorViewDescriptor<CPDefinition> {

		public CPDefinitionItemSelectorViewDescriptor(
			HttpServletRequest httpServletRequest,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL) {

			_httpServletRequest = httpServletRequest;
			_infoItemItemSelectorCriterion = infoItemItemSelectorCriterion;
			_portletURL = portletURL;
		}

		@Override
		public ItemDescriptor getItemDescriptor(CPDefinition cpDefinition) {
			return new CPDefinitionItemDescriptor(
				cpDefinition, _httpServletRequest);
		}

		@Override
		public ItemSelectorReturnType getItemSelectorReturnType() {
			return new InfoItemItemSelectorReturnType();
		}

		@Override
		public SearchContainer<CPDefinition> getSearchContainer() {
			SearchContainer<CPDefinition> entriesSearchContainer =
				new SearchContainer<>(
					(PortletRequest)_httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_REQUEST),
					_portletURL, null, "no-entries-were-found");

			entriesSearchContainer.setResultsAndTotal(
				() -> _cpDefinitionLocalService.getCPDefinitions(
					entriesSearchContainer.getStart(),
					entriesSearchContainer.getEnd()),
				_cpDefinitionLocalService.getCPDefinitionsCount());

			return entriesSearchContainer;
		}

		@Override
		public boolean isMultipleSelection() {
			return _infoItemItemSelectorCriterion.isMultiSelection();
		}

		private HttpServletRequest _httpServletRequest;
		private final InfoItemItemSelectorCriterion
			_infoItemItemSelectorCriterion;
		private final PortletURL _portletURL;

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.item.selector;

import com.liferay.info.collection.provider.item.selector.RepeatableFieldInfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.provider.RepeatableFieldsInfoItemFormProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Víctor Galán
 */
public class RepeatableFieldInfoCollectionProviderItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<InfoFieldSetEntry> {

	public RepeatableFieldInfoCollectionProviderItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		RepeatableFieldInfoCollectionProviderItemSelectorCriterion
			repeatableFieldInfoCollectionProviderItemSelectorCriterion,
		RepeatableFieldsInfoItemFormProvider<?>
			repeatableFieldsInfoItemFormProvider) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_repeatableFieldInfoCollectionProviderItemSelectorCriterion =
			repeatableFieldInfoCollectionProviderItemSelectorCriterion;
		_repeatableFieldsInfoItemFormProvider =
			repeatableFieldsInfoItemFormProvider;
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"icon"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		InfoFieldSetEntry infoFieldSetEntry) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return new RepeatableFieldInfoCollectionProviderItemDescriptor(
			infoFieldSetEntry,
			_repeatableFieldInfoCollectionProviderItemSelectorCriterion.
				getItemType(),
			_repeatableFieldInfoCollectionProviderItemSelectorCriterion.
				getItemSubtype(),
			themeDisplay.getLocale());
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new InfoListProviderItemSelectorReturnType();
	}

	@Override
	public SearchContainer<InfoFieldSetEntry> getSearchContainer() {
		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		SearchContainer<InfoFieldSetEntry> searchContainer =
			new SearchContainer<>(
				portletRequest, _portletURL, null,
				"there-are-no-repeatable-fields-collection-providers");

		searchContainer.setResultsAndTotal(_getRepeatableInfoFieldSetEntries());

		return searchContainer;
	}

	private List<InfoFieldSetEntry> _getRepeatableInfoFieldSetEntries() {
		try {
			if (_repeatableFieldsInfoItemFormProvider == null) {
				return Collections.emptyList();
			}

			InfoForm infoForm =
				_repeatableFieldsInfoItemFormProvider.
					getRepeatableFieldsInfoForm(
						_repeatableFieldInfoCollectionProviderItemSelectorCriterion.
							getItemSubtype());

			return ListUtil.filter(
				infoForm.getInfoFieldSetEntries(),
				infoFieldSetEntry -> {
					if (infoFieldSetEntry instanceof InfoField) {
						InfoField<?> infoField =
							(InfoField<?>)infoFieldSetEntry;

						return infoField.isRepeatable();
					}

					return true;
				});
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			_log.error(noSuchFormVariationException);

			return Collections.emptyList();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RepeatableFieldInfoCollectionProviderItemSelectorViewDescriptor.class);

	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final RepeatableFieldInfoCollectionProviderItemSelectorCriterion
		_repeatableFieldInfoCollectionProviderItemSelectorCriterion;
	private final RepeatableFieldsInfoItemFormProvider<?>
		_repeatableFieldsInfoItemFormProvider;

}
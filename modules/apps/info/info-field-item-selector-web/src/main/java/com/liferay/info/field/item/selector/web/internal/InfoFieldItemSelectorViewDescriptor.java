/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.field.item.selector.web.internal;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.item.selector.web.internal.search.InfoFieldItemSelectorChecker;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.localized.SingleValueInfoLocalizedValue;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.TableItemView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.ResultRowSplitterEntry;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class InfoFieldItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<InfoField<?>> {

	public InfoFieldItemSelectorViewDescriptor(
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
		HttpServletRequest httpServletRequest,
		InfoItemServiceRegistry infoItemServiceRegistry, PortletURL portletURL,
		RenderResponse renderResponse) {

		_fragmentEntryConfigurationParser = fragmentEntryConfigurationParser;
		_httpServletRequest = httpServletRequest;
		_infoItemServiceRegistry = infoItemServiceRegistry;
		_portletURL = portletURL;
		_renderResponse = renderResponse;

		_formItemId = ParamUtil.getString(httpServletRequest, "formItemId");
		_itemType = ParamUtil.getString(httpServletRequest, "itemType");
		_segmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "list";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[0];
	}

	@Override
	public ItemSelectorViewDescriptor.ItemDescriptor getItemDescriptor(
		InfoField<?> infoField) {

		return new InfoFieldItemDescriptor(_httpServletRequest, infoField);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new UUIDItemSelectorReturnType();
	}

	@Override
	public ResultRowSplitter getResultRowSplitter() {
		return resultRows -> {
			Map<InfoFieldSet, List<ResultRow>> resultRowsMap =
				new LinkedHashMap<>();

			Map<InfoField<?>, InfoFieldSet> infoFieldMap = _getInfoFieldMap();

			for (ResultRow resultRow : resultRows) {
				InfoField<?> infoField = (InfoField<?>)resultRow.getObject();

				InfoFieldSet infoFieldSet = infoFieldMap.get(infoField);

				List<ResultRow> infoFieldSetResultRows = resultRowsMap.get(
					infoFieldSet);

				if (infoFieldSetResultRows == null) {
					infoFieldSetResultRows = new ArrayList<>();
				}

				infoFieldSetResultRows.add(resultRow);

				resultRowsMap.putIfAbsent(infoFieldSet, infoFieldSetResultRows);
			}

			return TransformUtil.transform(
				resultRowsMap.keySet(),
				infoFieldSet -> new ResultRowSplitterEntry(
					infoFieldSet.getLabel(_themeDisplay.getLocale()),
					resultRowsMap.get(infoFieldSet)));
		};
	}

	@Override
	public SearchContainer<InfoField<?>> getSearchContainer()
		throws PortalException {

		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		SearchContainer<InfoField<?>> searchContainer = new SearchContainer<>(
			portletRequest, _portletURL, null, "there-are-no-info-fields");

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _itemType);

		if (infoItemFormProvider == null) {
			return searchContainer;
		}

		searchContainer.setRowChecker(
			new InfoFieldItemSelectorChecker(
				_renderResponse, _getCheckedUniqueInfoFieldIds()));

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			_itemType, _themeDisplay.getScopeGroupId());

		List<InfoField<?>> infoFields = ListUtil.filter(
			infoForm.getAllInfoFields(), InfoField::isEditable);

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			infoFields = ListUtil.filter(
				infoFields,
				infoField -> {
					String label = StringUtil.toLowerCase(
						infoField.getLabel(_themeDisplay.getLocale()));

					return label.contains(StringUtil.toLowerCase(keywords));
				});
		}

		searchContainer.setResultsAndTotal(infoFields);

		return searchContainer;
	}

	@Override
	public TableItemView getTableItemView(InfoField<?> infoField) {
		return new InfoFieldTableItemView(infoField);
	}

	@Override
	public boolean isMultipleSelection() {
		return true;
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private List<String> _getCheckedUniqueInfoFieldIds() {
		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.fetchSegmentsExperience(
				_segmentsExperienceId);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(
					segmentsExperience.getGroupId(),
					segmentsExperience.getPlid());

		if (layoutPageTemplateStructure == null) {
			return Collections.emptyList();
		}

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(_segmentsExperienceId));

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(_formItemId);

		if (!(layoutStructureItem instanceof FormStyledLayoutStructureItem)) {
			return Collections.emptyList();
		}

		List<String> checkedUniqueInfoFieldIds = new ArrayList<>();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)layoutStructureItem;

		for (String itemId :
				LayoutStructureItemUtil.getChildrenItemIds(
					formStyledLayoutStructureItem.getItemId(),
					layoutStructure)) {

			LayoutStructureItem curLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(itemId);

			if (!(curLayoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)curLayoutStructureItem;

			FragmentEntryLink fragmentEntryLink =
				FragmentEntryLinkLocalServiceUtil.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if (fragmentEntryLink == null) {
				continue;
			}

			String inputFieldId = GetterUtil.getString(
				_fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getEditableValues(),
					new FragmentConfigurationField(
						"inputFieldId", "string", "", false, "text"),
					_themeDisplay.getLocale()));

			if (Validator.isNotNull(inputFieldId)) {
				checkedUniqueInfoFieldIds.add(inputFieldId);
			}
		}

		return checkedUniqueInfoFieldIds;
	}

	private Map<InfoField<?>, InfoFieldSet> _getInfoFieldMap() {
		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _itemType);

		if (infoItemFormProvider == null) {
			return Collections.emptyMap();
		}

		InfoForm infoForm = null;

		try {
			infoForm = infoItemFormProvider.getInfoForm(
				StringPool.BLANK, _themeDisplay.getScopeGroupId());
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFormVariationException);
			}

			return Collections.emptyMap();
		}

		Map<InfoField<?>, InfoFieldSet> infoFieldsMap = new HashMap<>();

		for (InfoFieldSetEntry infoFieldSetEntry :
				infoForm.getInfoFieldSetEntries()) {

			if (infoFieldSetEntry instanceof InfoField) {
				InfoField<?> infoField = (InfoField<?>)infoFieldSetEntry;

				if (infoField.isEditable()) {
					infoFieldsMap.put(infoField, _DEFAULT_INFO_FIELD_SET);
				}
			}
			else if (infoFieldSetEntry instanceof InfoFieldSet) {
				InfoFieldSet infoFieldSet = (InfoFieldSet)infoFieldSetEntry;

				for (InfoField<?> infoField : infoFieldSet.getAllInfoFields()) {
					if (infoField.isEditable()) {
						infoFieldsMap.put(infoField, infoFieldSet);
					}
				}
			}
		}

		return infoFieldsMap;
	}

	private static final InfoFieldSet _DEFAULT_INFO_FIELD_SET =
		InfoFieldSet.builder(
		).labelInfoLocalizedValue(
			new SingleValueInfoLocalizedValue<>("default")
		).name(
			"default"
		).build();

	private static final Log _log = LogFactoryUtil.getLog(
		InfoFieldItemSelectorViewDescriptor.class);

	private final String _formItemId;
	private final FragmentEntryConfigurationParser
		_fragmentEntryConfigurationParser;
	private final HttpServletRequest _httpServletRequest;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final String _itemType;
	private final PortletURL _portletURL;
	private final RenderResponse _renderResponse;
	private final long _segmentsExperienceId;
	private final ThemeDisplay _themeDisplay;

}
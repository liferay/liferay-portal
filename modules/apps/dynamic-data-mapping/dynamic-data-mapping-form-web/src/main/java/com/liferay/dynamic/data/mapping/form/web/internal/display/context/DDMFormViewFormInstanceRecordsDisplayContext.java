/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.web.internal.search.DDMFormInstanceRecordSearch;
import com.liferay.dynamic.data.mapping.form.web.internal.security.permission.resource.DDMFormInstancePermission;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Leonardo Barros
 */
public class DDMFormViewFormInstanceRecordsDisplayContext {

	public DDMFormViewFormInstanceRecordsDisplayContext(
			RenderRequest renderRequest, RenderResponse renderResponse,
			DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService,
			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry)
		throws PortalException {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_ddmFormInstance = ddmFormInstance;
		_ddmFormInstanceRecordLocalService = ddmFormInstanceRecordLocalService;
		_ddmFormFieldTypeServicesRegistry = ddmFormFieldTypeServicesRegistry;

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);

		String redirect = ParamUtil.getString(renderRequest, "redirect");

		if (Validator.isNull(redirect)) {
			DDMFormAdminDisplayContext ddmFormAdminDisplayContext =
				(DDMFormAdminDisplayContext)renderRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT);

			redirect = String.valueOf(
				ddmFormAdminDisplayContext.getPortletURL());
		}

		portletDisplay.setURLBack(redirect);

		_setDDMFormFields();
	}

	public List<DropdownItem> getActionItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteRecords");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					getLocalizedMessage(_renderRequest.getLocale(), "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public List<String> getAvailableActions(PermissionChecker permissionChecker)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (DDMFormInstancePermission.contains(
				permissionChecker, getDDMFormInstance(), ActionKeys.DELETE)) {

			availableActions.add("deleteRecords");
		}

		return availableActions;
	}

	public int getAvailableLocalesCount() throws Exception {
		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		DDMForm ddmForm = ddmFormInstance.getDDMForm();

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		return availableLocales.size();
	}

	public String getClearResultsURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(getPortletURL(), _renderResponse)
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public String getColumnName(DDMFormField ddmFormField) {
		LocalizedValue localizedValue = ddmFormField.getLabel();

		return localizedValue.getString(_renderRequest.getLocale());
	}

	public String getColumnValue(
		DDMFormField ddmFormField, String ddmFormFieldName,
		List<DDMFormFieldValue> ddmFormFieldValues) {

		if ((ddmFormField == null) || (ddmFormFieldValues == null)) {
			return StringPool.BLANK;
		}

		String ddmFormFieldType = ddmFormField.getType();

		final DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldValueRenderer(
				ddmFormFieldType);

		List<String> renderedDDMFormFieldValues = ListUtil.toList(
			ddmFormFieldValues,
			new Function<DDMFormFieldValue, String>() {

				@Override
				public String apply(DDMFormFieldValue ddmFormFieldValue) {
					Value value = ddmFormFieldValue.getValue();

					if (ddmFormFieldType.equals(
							DDMFormFieldTypeConstants.SEARCH_LOCATION)) {

						return ddmFormFieldValueRenderer.render(
							ddmFormFieldName, ddmFormFieldValue,
							value.getDefaultLocale());
					}

					return ddmFormFieldValueRenderer.render(
						ddmFormFieldValue, value.getDefaultLocale());
				}

			});

		if (ddmFormFieldType.equals("select")) {
			renderedDDMFormFieldValues = _getOptionsRenderedFormFieldValues(
				ddmFormField.getDDMFormFieldOptions(),
				renderedDDMFormFieldValues);
		}

		return StringUtil.merge(
			renderedDDMFormFieldValues, StringPool.COMMA_AND_SPACE);
	}

	public DDMForm getDDMForm(DDMFormInstanceRecord ddmFormInstanceRecord)
		throws PortalException {

		DDMFormValues ddmFormValues = _getDDMFormValues(ddmFormInstanceRecord);

		return ddmFormValues.getDDMForm();
	}

	public List<DDMFormField> getDDMFormFields() {
		return _ddmFormFields;
	}

	public Map<String, List<DDMFormFieldValue>> getDDMFormFieldValues(
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws PortalException {

		DDMFormValues ddmFormValues = _getDDMFormValues(ddmFormInstanceRecord);

		return ddmFormValues.getDDMFormFieldValuesMap(true);
	}

	public DDMFormInstance getDDMFormInstance() {
		return _ddmFormInstance;
	}

	public List<Long> getDDMFormInstanceRecordIds() {
		return TransformUtil.transform(
			_ddmFormInstanceRecordLocalService.getFormInstanceRecords(
				_ddmFormInstance.getFormInstanceId()),
			DDMFormInstanceRecord::getFormInstanceRecordId);
	}

	public Locale getDefaultLocale(DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		return ddmFormValues.getDefaultLocale();
	}

	public String getDisplayStyle() {
		return "list";
	}

	public String getLocalizedColumnValues(String columnValues) {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return StringUtil.merge(
			TransformUtil.transformToArray(
				Arrays.asList(
					StringUtil.split(columnValues, StringPool.COMMA_AND_SPACE)),
				value -> getLocalizedMessage(
					themeDisplay.getLocale(), value.toLowerCase()),
				String.class),
			StringPool.COMMA_AND_SPACE);
	}

	public String getLocalizedMessage(Locale locale, String key) {
		return LanguageUtil.get(locale, key);
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(true);

				DDMFormInstance ddmFormInstance = getDDMFormInstance();

				navigationItem.setLabel(
					HtmlParserUtil.extractText(
						ddmFormInstance.getName(_renderRequest.getLocale())));
			}
		).build();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
			"view-entries-order-by-col", "modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
			"view-entries-order-by-type", "asc");

		return _orderByType;
	}

	public List<DropdownItem> getOrderItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				String orderByCol = "modified-date";

				dropdownItem.setActive(orderByCol.equals(getOrderByCol()));
				dropdownItem.setHref(getPortletURL(), "orderByCol", orderByCol);
				dropdownItem.setLabel(
					getLocalizedMessage(
						_renderRequest.getLocale(), orderByCol));
			}
		).build();
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLUtil.getCurrent(
			_renderRequest, _renderResponse);

		if (_ddmFormInstance == null) {
			return portletURL;
		}

		portletURL.setParameter(
			"mvcPath", "/admin/view_form_instance_records.jsp");
		portletURL.setParameter(
			"redirect", ParamUtil.getString(_renderRequest, "redirect"));
		portletURL.setParameter(
			"formInstanceId",
			String.valueOf(_ddmFormInstance.getFormInstanceId()));

		String delta = ParamUtil.getString(_renderRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String displayStyle = getDisplayStyle();

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", displayStyle);
		}

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public String getSearchActionURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		if (_ddmFormInstance == null) {
			return portletURL.toString();
		}

		portletURL.setParameter(
			"mvcPath", "/admin/view_form_instance_records.jsp");
		portletURL.setParameter(
			"redirect", ParamUtil.getString(_renderRequest, "redirect"));
		portletURL.setParameter(
			"formInstanceId",
			String.valueOf(_ddmFormInstance.getFormInstanceId()));

		return portletURL.toString();
	}

	public SearchContainer<?> getSearchContainer() {
		PortletURL portletURL = PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();

		DDMFormInstanceRecordSearch ddmFormInstanceRecordSearch =
			new DDMFormInstanceRecordSearch(
				_renderRequest, portletURL, _getHeaderNames());

		if (ddmFormInstanceRecordSearch.isSearch()) {
			ddmFormInstanceRecordSearch.setEmptyResultsMessage(
				"no-entries-were-found");
		}
		else {
			ddmFormInstanceRecordSearch.setEmptyResultsMessage(
				"there-are-no-entries");
		}

		ddmFormInstanceRecordSearch.setOrderByCol(getOrderByCol());
		ddmFormInstanceRecordSearch.setOrderByComparator(
			DDMFormInstanceRecordSearch.
				getDDMFormInstanceRecordOrderByComparator(
					getOrderByCol(), getOrderByType()));
		ddmFormInstanceRecordSearch.setOrderByType(getOrderByType());

		if (_ddmFormInstance == null) {
			ddmFormInstanceRecordSearch.setResultsAndTotal(
				Collections::emptyList, 0);
		}
		else if (Validator.isNull(getKeywords())) {
			ddmFormInstanceRecordSearch.setResultsAndTotal(
				() -> _ddmFormInstanceRecordLocalService.getFormInstanceRecords(
					_ddmFormInstance.getFormInstanceId(),
					WorkflowConstants.STATUS_ANY,
					ddmFormInstanceRecordSearch.getStart(),
					ddmFormInstanceRecordSearch.getEnd(),
					ddmFormInstanceRecordSearch.getOrderByComparator()),
				_ddmFormInstanceRecordLocalService.getFormInstanceRecordsCount(
					_ddmFormInstance.getFormInstanceId(),
					WorkflowConstants.STATUS_ANY));
		}
		else {
			ddmFormInstanceRecordSearch.setResultsAndTotal(
				_ddmFormInstanceRecordLocalService.searchFormInstanceRecords(
					_getSearchContext(WorkflowConstants.STATUS_ANY)));
		}

		return ddmFormInstanceRecordSearch;
	}

	public String getSearchContainerId() {
		return "ddmFormInstanceRecord";
	}

	public String getSortingURL() throws Exception {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(getPortletURL(), _renderResponse)
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = ParamUtil.getString(
					_renderRequest, "orderByType");

				if (orderByType.equals("asc")) {
					return "desc";
				}

				return "asc";
			}
		).buildString();
	}

	public int getTotalItems() {
		SearchContainer<?> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	public List<String> getVisibleFields(DDMFormField ddmFormField) {
		LocalizedValue visibleFields = (LocalizedValue)ddmFormField.getProperty(
			"visibleFields");

		return TransformUtil.transformToList(
			StringUtil.split(
				StringUtil.removeChars(
					visibleFields.getString(_renderRequest.getLocale()),
					CharPool.CLOSE_BRACKET, CharPool.OPEN_BRACKET,
					CharPool.QUOTE)),
			String::trim);
	}

	public boolean isDisabledManagementBar() {
		if (hasResults() || isSearch()) {
			return false;
		}

		return true;
	}

	protected String getKeywords() {
		return ParamUtil.getString(_renderRequest, "keywords");
	}

	protected boolean hasResults() {
		if (getTotalItems() > 0) {
			return true;
		}

		return false;
	}

	protected boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	private DDMFormValues _getDDMFormValues(
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws PortalException {

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			ddmFormInstanceRecord.getFormInstanceRecordVersion();

		return ddmFormInstanceRecordVersion.getDDMFormValues();
	}

	private List<String> _getHeaderNames() {
		List<String> headerNames = new ArrayList<>();

		List<DDMFormField> ddmFormFields = getDDMFormFields();

		int totalColumns = _MAX_COLUMNS;

		if (ddmFormFields.size() < totalColumns) {
			totalColumns = ddmFormFields.size();
		}

		for (int i = 0; i < totalColumns; i++) {
			DDMFormField ddmFormField = ddmFormFields.get(i);

			LocalizedValue localizedValue = ddmFormField.getLabel();

			headerNames.add(
				localizedValue.getString(_renderRequest.getLocale()));
		}

		return headerNames;
	}

	private List<DDMFormField> _getNontransientFormFields(DDMForm ddmForm) {
		List<DDMFormField> ddmFormFields = new ArrayList<>();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			if (ddmFormField.isTransient()) {
				continue;
			}

			ddmFormFields.add(ddmFormField);
		}

		return ddmFormFields;
	}

	private List<String> _getOptionsRenderedFormFieldValues(
		DDMFormFieldOptions ddmFormFieldOptions,
		List<String> renderedFormFieldValues) {

		List<String> values = new ArrayList<>();

		for (String renderedFormFieldValue : renderedFormFieldValues) {
			Collections.addAll(
				values,
				StringUtil.split(renderedFormFieldValue, CharPool.COMMA));
		}

		return TransformUtil.transform(
			values,
			value -> {
				LocalizedValue optionLabel =
					ddmFormFieldOptions.getOptionLabels(value.trim());

				if (optionLabel == null) {
					return value;
				}

				return optionLabel.getString(_renderRequest.getLocale());
			});
	}

	private SearchContext _getSearchContext(int status) {
		SearchContext searchContext = SearchContextFactory.getInstance(
			PortalUtil.getHttpServletRequest(_renderRequest));

		searchContext.setAttribute(Field.STATUS, status);
		searchContext.setAttribute(
			"formInstanceId", _ddmFormInstance.getFormInstanceId());
		searchContext.setEnd(searchContext.getEnd());
		searchContext.setKeywords(getKeywords());
		searchContext.setStart(searchContext.getStart());

		return searchContext;
	}

	private void _setDDMFormFields() throws PortalException {
		if (_ddmFormInstance == null) {
			return;
		}

		DDMStructure ddmStructure = _ddmFormInstance.getStructure();

		List<DDMFormField> ddmFormFields = _getNontransientFormFields(
			ddmStructure.getDDMForm());

		for (DDMFormField ddmFormField : ddmFormFields) {
			_ddmFormFields.add(ddmFormField);
		}
	}

	private static final int _MAX_COLUMNS = 5;

	private final List<DDMFormField> _ddmFormFields = new ArrayList<>();
	private final DDMFormFieldTypeServicesRegistry
		_ddmFormFieldTypeServicesRegistry;
	private final DDMFormInstance _ddmFormInstance;
	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}
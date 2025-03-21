/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextFactory;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.FieldSetPermissionCheckerHelper;
import com.liferay.dynamic.data.mapping.form.web.internal.search.FieldSetRowChecker;
import com.liferay.dynamic.data.mapping.form.web.internal.search.FieldSetSearch;
import com.liferay.dynamic.data.mapping.form.web.internal.search.FieldSetSearchTerms;
import com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesSerializer;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterRegistry;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.dynamic.data.mapping.util.comparator.StructureCreateDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureModifiedDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureNameComparator;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public class DDMFormAdminFieldSetDisplayContext
	extends DDMFormAdminDisplayContext {

	public DDMFormAdminFieldSetDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		DDMFormBuilderContextFactory ddmFormBuilderContextFactory,
		DDMFormBuilderSettingsRetriever ddmFormBuilderSettingsRetriever,
		DDMFormContextDeserializer<DDMFormValues> ddmFormContextToDDMFormValues,
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
		DDMFormFieldTypesSerializer ddmFormFieldTypesSerializer,
		DDMFormInstanceLocalService ddmFormInstanceLocalService,
		DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService,
		DDMFormInstanceRecordWriterRegistry ddmFormInstanceRecordWriterRegistry,
		DDMFormInstanceService ddmFormInstanceService,
		DDMFormInstanceVersionLocalService ddmFormInstanceVersionLocalService,
		DDMFormRenderer ddmFormRenderer,
		DDMFormTemplateContextFactory ddmFormTemplateContextFactory,
		DDMFormValuesFactory ddmFormValuesFactory,
		DDMFormValuesMerger ddmFormValuesMerger,
		DDMFormWebConfiguration ddmFormWebConfiguration,
		DDMStorageAdapterRegistry ddmStorageAdapterRegistry,
		DDMStructureLocalService ddmStructureLocalService,
		DDMStructureService ddmStructureService, JSONFactory jsonFactory,
		NPMResolver npmResolver,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		Portal portal) {

		super(
			renderRequest, renderResponse, ddmFormBuilderContextFactory,
			ddmFormBuilderSettingsRetriever, ddmFormContextToDDMFormValues,
			ddmFormFieldTypeServicesRegistry, ddmFormFieldTypesSerializer,
			ddmFormInstanceLocalService, ddmFormInstanceRecordLocalService,
			ddmFormInstanceRecordWriterRegistry, ddmFormInstanceService,
			ddmFormInstanceVersionLocalService, ddmFormRenderer,
			ddmFormTemplateContextFactory, ddmFormValuesFactory,
			ddmFormValuesMerger, ddmFormWebConfiguration,
			ddmStorageAdapterRegistry, ddmStructureLocalService,
			ddmStructureService, jsonFactory, npmResolver,
			objectDefinitionLocalService, portal);

		_fieldSetPermissionCheckerHelper = new FieldSetPermissionCheckerHelper(
			ddmFormAdminRequestHelper);
	}

	@Override
	public List<DropdownItem> getActionItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteStructures");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(
						ddmFormAdminRequestHelper.getRequest(), "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!_fieldSetPermissionCheckerHelper.isShowAddButton()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			_getAddElementSetDropdownItem()
		).build();
	}

	@Override
	public Map<String, Object> getDDMFormSettingsContext(
			PageContext pageContext)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(
			DDMFormInstanceSettings.class);

		ddmFormLayout.setPaginationMode(DDMFormLayout.TABBED_MODE);

		return ddmFormRenderer.getDDMFormTemplateContext(
			createSettingsDDMForm(0L, themeDisplay), ddmFormLayout,
			createDDMFormRenderingContext(pageContext, renderRequest));
	}

	@Override
	public DDMStructure getDDMStructure() {
		if (_structure != null) {
			return _structure;
		}

		long structureId = ParamUtil.getLong(renderRequest, "structureId");

		if (structureId > 0) {
			try {
				DDMStructureService ddmStructureService = getStructureService();

				_structure = ddmStructureService.getStructure(structureId);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return _structure;
	}

	@Override
	public List<DropdownItem> getEmptyResultMessageActionItemsDropdownItems() {
		if (!_fieldSetPermissionCheckerHelper.isShowAddButton() || isSearch()) {
			return null;
		}

		return DropdownItemListBuilder.add(
			_getAddElementSetDropdownItem()
		).build();
	}

	@Override
	public String getEmptyResultMessageDescription() {
		if (isSearch()) {
			return StringPool.BLANK;
		}

		return LanguageUtil.get(
			ddmFormAdminRequestHelper.getRequest(),
			"accelerate-form-creation-with-reusable-field-groupings");
	}

	@Override
	public String getFormDescription() {
		DDMStructure structure = getDDMStructure();

		if (structure != null) {
			return LocalizationUtil.getLocalization(
				structure.getDescription(), getDefaultLanguageId());
		}

		return getJSONObjectLocalizedPropertyFromRequest("description");
	}

	@Override
	public JSONObject getFormLocalizedDescriptionJSONObject() {
		DDMStructure structure = getDDMStructure();

		JSONObject jsonObject = jsonFactory.createJSONObject();

		if (structure == null) {
			jsonObject.put(getDefaultLanguageId(), "");
		}
		else {
			Map<Locale, String> descriptionMap = structure.getDescriptionMap();

			for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
				jsonObject.put(
					LocaleUtil.toLanguageId(entry.getKey()), entry.getValue());
			}
		}

		return jsonObject;
	}

	@Override
	public <T> JSONObject getFormLocalizedNameJSONObject(T object) {
		DDMStructure structure = (DDMStructure)object;

		JSONObject jsonObject = jsonFactory.createJSONObject();

		if (structure == null) {
			jsonObject.put(getDefaultLanguageId(), "");
		}
		else {
			Map<Locale, String> nameMap = structure.getNameMap();

			for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
				jsonObject.put(
					LocaleUtil.toLanguageId(entry.getKey()), entry.getValue());
			}
		}

		return jsonObject;
	}

	@Override
	public String getFormName() {
		DDMStructure structure = getDDMStructure();

		if (structure != null) {
			return LocalizationUtil.getLocalization(
				structure.getName(), getDefaultLanguageId());
		}

		return getJSONObjectLocalizedPropertyFromRequest("name");
	}

	@Override
	public <T> T getPermissionCheckerHelper() {
		return (T)_fieldSetPermissionCheckerHelper;
	}

	@Override
	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/admin/view.jsp"
		).setParameter(
			"currentTab", "element-set"
		).setParameter(
			"groupId", getScopeGroupId()
		).buildPortletURL();

		String delta = ParamUtil.getString(renderRequest, "delta");

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

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/admin/view.jsp"
		).setParameter(
			"currentTab", "element-set"
		).setParameter(
			"groupId", getScopeGroupId()
		).buildString();
	}

	@Override
	public SearchContainer<?> getSearchContainer() {
		PortletURL portletURL = PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();

		FieldSetSearch fieldSetSearch = new FieldSetSearch(
			renderRequest, portletURL);

		if (fieldSetSearch.isSearch()) {
			fieldSetSearch.setEmptyResultsMessage("no-element-sets-were-found");
		}
		else {
			fieldSetSearch.setEmptyResultsMessage("there-are-no-element-sets");
		}

		fieldSetSearch.setOrderByCol(getOrderByCol());
		fieldSetSearch.setOrderByComparator(
			_getDDMStructureOrderByComparator(
				getOrderByCol(), getOrderByType()));
		fieldSetSearch.setOrderByType(getOrderByType());

		FieldSetSearchTerms fieldSetSearchTerms =
			(FieldSetSearchTerms)fieldSetSearch.getSearchTerms();

		DDMStructureService ddmStructureService = getStructureService();

		fieldSetSearch.setResultsAndTotal(
			() -> ddmStructureService.search(
				getCompanyId(), new long[] {getScopeGroupId()},
				PortalUtil.getClassNameId(DDMFormInstance.class),
				fieldSetSearchTerms.getKeywords(),
				DDMStructureConstants.TYPE_FRAGMENT,
				WorkflowConstants.STATUS_ANY, fieldSetSearch.getStart(),
				fieldSetSearch.getEnd(), fieldSetSearch.getOrderByComparator()),
			ddmStructureService.searchCount(
				getCompanyId(), new long[] {getScopeGroupId()},
				PortalUtil.getClassNameId(DDMFormInstance.class),
				fieldSetSearchTerms.getKeywords(),
				DDMStructureConstants.TYPE_FRAGMENT,
				WorkflowConstants.STATUS_ANY));

		fieldSetSearch.setRowChecker(new FieldSetRowChecker(renderResponse));

		return fieldSetSearch;
	}

	@Override
	public String getSearchContainerId() {
		return "structure";
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAddElementSetDropdownItem() {

		return dropdownItem -> {
			HttpServletRequest httpServletRequest =
				PortalUtil.getHttpServletRequest(renderRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			dropdownItem.setHref(
				renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/admin/edit_element_set", "redirect",
				PortalUtil.getCurrentURL(httpServletRequest), "groupId",
				String.valueOf(themeDisplay.getScopeGroupId()));

			dropdownItem.setLabel(
				LanguageUtil.get(httpServletRequest, "new-element-set"));
		};
	}

	private OrderByComparator<DDMStructure> _getDDMStructureOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<DDMStructure> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator = StructureCreateDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("modified-date")) {
			orderByComparator = new StructureModifiedDateComparator(orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = new StructureNameComparator(orderByAsc);
		}

		return orderByComparator;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormAdminFieldSetDisplayContext.class);

	private final FieldSetPermissionCheckerHelper
		_fieldSetPermissionCheckerHelper;
	private DDMStructure _structure;

}
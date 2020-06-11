/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderTracker;
import com.liferay.dynamic.data.mapping.data.provider.web.internal.display.context.util.DDMDataProviderRequestHelper;
import com.liferay.dynamic.data.mapping.data.provider.web.internal.search.DDMDataProviderSearchTerms;
import com.liferay.dynamic.data.mapping.data.provider.web.internal.util.DDMDataProviderPortletUtil;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.dynamic.data.mapping.service.permission.DDMDataProviderInstancePermission;
import com.liferay.dynamic.data.mapping.service.permission.DDMDataProviderPermission;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Leonardo Barros
 */
public class DDMDataProviderDisplayContext {

	public DDMDataProviderDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		DDMDataProviderInstanceService ddmDataProviderInstanceService,
		DDMDataProviderTracker ddmDataProviderTracker,
		DDMFormRenderer ddmFormRenderer,
		DDMFormValuesJSONDeserializer ddmFormValuesJSONDeserializer,
		UserLocalService userLocalService) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_ddmDataProviderInstanceService = ddmDataProviderInstanceService;
		_ddmDataProviderTracker = ddmDataProviderTracker;
		_ddmFormRenderer = ddmFormRenderer;
		_ddmFormValuesJSONDeserializer = ddmFormValuesJSONDeserializer;
		_userLocalService = userLocalService;

		_ddmDataProviderRequestHelper = new DDMDataProviderRequestHelper(
			renderRequest);
	}

	public DDMDataProviderInstance fetchDataProviderInstance()
		throws PortalException {

		if (_ddmDataProviderInstance != null) {
			return _ddmDataProviderInstance;
		}

		long dataProviderInstanceId = ParamUtil.getLong(
			_renderRequest, "dataProviderInstanceId");

		_ddmDataProviderInstance =
			_ddmDataProviderInstanceService.fetchDataProviderInstance(
				dataProviderInstanceId);

		return _ddmDataProviderInstance;
	}

	public String getDataProviderInstanceDDMFormHTML() throws PortalException {
		DDMDataProviderInstance ddmDataProviderInstance =
			fetchDataProviderInstance();

		String type = BeanParamUtil.getString(
			ddmDataProviderInstance, _renderRequest, "type");

		DDMDataProvider ddmDataProvider =
			_ddmDataProviderTracker.getDDMDataProvider(type);

		Class<?> clazz = ddmDataProvider.getSettings();

		DDMForm ddmForm = DDMFormFactory.create(clazz);

		DDMFormRenderingContext ddmFormRenderingContext =
			createDDMFormRenderingContext();

		if (_ddmDataProviderInstance != null) {
			DDMFormValues ddmFormValues =
				_ddmFormValuesJSONDeserializer.deserialize(
					ddmForm, _ddmDataProviderInstance.getDefinition());

			Set<String> passwordDDMFormFieldNames =
				DDMDataProviderPortletUtil.getDDMFormFieldNamesByType(
					ddmForm, "password");

			obfuscateDDMFormFieldValues(
				passwordDDMFormFieldNames,
				ddmFormValues.getDDMFormFieldValues());

			ddmFormRenderingContext.setDDMFormValues(ddmFormValues);
		}

		DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(clazz);

		ddmFormLayout.setPaginationMode(DDMFormLayout.SINGLE_PAGE_MODE);

		return _ddmFormRenderer.render(
			ddmForm, ddmFormLayout, ddmFormRenderingContext);
	}

	public String getDataProviderInstanceDescription() throws PortalException {
		DDMDataProviderInstance ddmDataProviderInstance =
			fetchDataProviderInstance();

		if (ddmDataProviderInstance == null) {
			return StringPool.BLANK;
		}

		return ddmDataProviderInstance.getDescription(
			_renderRequest.getLocale());
	}

	public String getDataProviderInstanceName() throws PortalException {
		DDMDataProviderInstance ddmDataProviderInstance =
			fetchDataProviderInstance();

		if (ddmDataProviderInstance == null) {
			return StringPool.BLANK;
		}

		return ddmDataProviderInstance.getName(_renderRequest.getLocale());
	}

	public Set<String> getDDMDataProviderTypes() {
		return _ddmDataProviderTracker.getDDMDataProviderTypes();
	}

	public String getOrderByCol() {
		String orderByCol = ParamUtil.getString(
			_renderRequest, "orderByCol", "modified-date");

		return orderByCol;
	}

	public String getOrderByType() {
		String orderByType = ParamUtil.getString(
			_renderRequest, "orderByType", "asc");

		return orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		portletURL.setParameter("mvcPath", "/view.jsp");
		portletURL.setParameter(
			"groupId",
			String.valueOf(_ddmDataProviderRequestHelper.getScopeGroupId()));

		return portletURL;
	}

	public List<DDMDataProviderInstance> getSearchContainerResults(
			SearchContainer<DDMDataProviderInstance> searchContainer)
		throws PortalException {

		DDMDataProviderSearchTerms searchTerms =
			(DDMDataProviderSearchTerms)searchContainer.getSearchTerms();

		if (searchTerms.isAdvancedSearch()) {
			return _ddmDataProviderInstanceService.search(
				_ddmDataProviderRequestHelper.getCompanyId(),
				new long[] {_ddmDataProviderRequestHelper.getScopeGroupId()},
				searchTerms.getName(), searchTerms.getDescription(),
				searchTerms.isAndOperator(), searchContainer.getStart(),
				searchContainer.getEnd(),
				searchContainer.getOrderByComparator());
		}
		else {
			return _ddmDataProviderInstanceService.search(
				_ddmDataProviderRequestHelper.getCompanyId(),
				new long[] {_ddmDataProviderRequestHelper.getScopeGroupId()},
				searchTerms.getKeywords(), searchContainer.getStart(),
				searchContainer.getEnd(),
				searchContainer.getOrderByComparator());
		}
	}

	public int getSearchContainerTotal(
			SearchContainer<DDMDataProviderInstance> searchContainer)
		throws PortalException {

		DDMDataProviderSearchTerms searchTerms =
			(DDMDataProviderSearchTerms)searchContainer.getSearchTerms();

		if (searchTerms.isAdvancedSearch()) {
			return _ddmDataProviderInstanceService.searchCount(
				_ddmDataProviderRequestHelper.getCompanyId(),
				new long[] {_ddmDataProviderRequestHelper.getScopeGroupId()},
				searchTerms.getName(), searchTerms.getDescription(),
				searchTerms.isAndOperator());
		}
		else {
			return _ddmDataProviderInstanceService.searchCount(
				_ddmDataProviderRequestHelper.getCompanyId(),
				new long[] {_ddmDataProviderRequestHelper.getScopeGroupId()},
				searchTerms.getKeywords());
		}
	}

	public String getUserPortraitURL(long userId) throws PortalException {
		User user = _userLocalService.getUser(userId);

		return user.getPortraitURL(
			_ddmDataProviderRequestHelper.getThemeDisplay());
	}

	public boolean isShowAddDataProviderButton() {
		return DDMDataProviderPermission.contains(
			_ddmDataProviderRequestHelper.getPermissionChecker(),
			_ddmDataProviderRequestHelper.getScopeGroupId(),
			DDMActionKeys.ADD_DATA_PROVIDER_INSTANCE);
	}

	public boolean isShowDeleteDataProviderIcon(
			DDMDataProviderInstance dataProviderInstance)
		throws PortalException {

		return DDMDataProviderInstancePermission.contains(
			_ddmDataProviderRequestHelper.getPermissionChecker(),
			dataProviderInstance, ActionKeys.DELETE);
	}

	public boolean isShowEditDataProviderIcon(
			DDMDataProviderInstance dataProviderInstance)
		throws PortalException {

		return DDMDataProviderInstancePermission.contains(
			_ddmDataProviderRequestHelper.getPermissionChecker(),
			dataProviderInstance, ActionKeys.UPDATE);
	}

	public boolean isShowPermissionsIcon(
		DDMDataProviderInstance dataProviderInstance) {

		return DDMDataProviderInstancePermission.contains(
			_ddmDataProviderRequestHelper.getPermissionChecker(),
			dataProviderInstance, ActionKeys.PERMISSIONS);
	}

	protected DDMFormRenderingContext createDDMFormRenderingContext() {
		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setHttpServletRequest(
			PortalUtil.getHttpServletRequest(_renderRequest));
		ddmFormRenderingContext.setHttpServletResponse(
			PortalUtil.getHttpServletResponse(_renderResponse));
		ddmFormRenderingContext.setLocale(
			_ddmDataProviderRequestHelper.getLocale());
		ddmFormRenderingContext.setPortletNamespace(
			_renderResponse.getNamespace());
		ddmFormRenderingContext.setShowRequiredFieldsWarning(false);

		return ddmFormRenderingContext;
	}

	protected void obfuscateDDMFormFieldValue(
		DDMFormFieldValue ddmFormFieldValue) {

		Value value = ddmFormFieldValue.getValue();

		for (Locale availableLocale : value.getAvailableLocales()) {
			value.addString(availableLocale, Portal.TEMP_OBFUSCATION_VALUE);
		}
	}

	protected void obfuscateDDMFormFieldValues(
		Set<String> ddmFormFieldNamesToBeObfuscated,
		List<DDMFormFieldValue> ddmFormFieldValues) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (ddmFormFieldNamesToBeObfuscated.contains(
					ddmFormFieldValue.getName())) {

				obfuscateDDMFormFieldValue(ddmFormFieldValue);
			}

			obfuscateDDMFormFieldValues(
				ddmFormFieldNamesToBeObfuscated,
				ddmFormFieldValue.getNestedDDMFormFieldValues());
		}
	}

	private DDMDataProviderInstance _ddmDataProviderInstance;
	private final DDMDataProviderInstanceService
		_ddmDataProviderInstanceService;
	private final DDMDataProviderRequestHelper _ddmDataProviderRequestHelper;
	private final DDMDataProviderTracker _ddmDataProviderTracker;
	private final DDMFormRenderer _ddmFormRenderer;
	private final DDMFormValuesJSONDeserializer _ddmFormValuesJSONDeserializer;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final UserLocalService _userLocalService;

}
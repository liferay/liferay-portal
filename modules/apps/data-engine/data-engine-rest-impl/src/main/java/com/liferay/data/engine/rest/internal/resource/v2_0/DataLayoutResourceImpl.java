/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.internal.resource.v2_0;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import com.liferay.data.engine.constants.DataActionKeys;
import com.liferay.data.engine.content.type.DataDefinitionContentType;
import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutRenderingContext;
import com.liferay.data.engine.rest.dto.v2_0.util.DataDefinitionDDMFormUtil;
import com.liferay.data.engine.rest.internal.content.type.DataDefinitionContentTypeRegistryUtil;
import com.liferay.data.engine.rest.internal.dto.v2_0.util.DataDefinitionUtil;
import com.liferay.data.engine.rest.internal.dto.v2_0.util.DataLayoutUtil;
import com.liferay.data.engine.rest.internal.dto.v2_0.util.MapToDDMFormValuesConverterUtil;
import com.liferay.data.engine.rest.internal.odata.entity.v2_0.DataLayoutEntityModel;
import com.liferay.data.engine.rest.internal.security.permission.resource.util.DataDefinitionPermissionUtil;
import com.liferay.data.engine.rest.resource.exception.DataLayoutValidationException;
import com.liferay.data.engine.rest.resource.v2_0.DataLayoutResource;
import com.liferay.data.engine.service.DEDataDefinitionFieldLinkLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.form.builder.rule.DDMFormRuleDeserializer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.spi.converter.SPIDDMFormRuleConverter;
import com.liferay.dynamic.data.mapping.util.comparator.StructureLayoutCreateDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureLayoutModifiedDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureLayoutNameComparator;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidationException;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidator;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jeyvison Nascimento
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v2_0/data-layout.properties",
	scope = ServiceScope.PROTOTYPE, service = DataLayoutResource.class
)
@CTAware
public class DataLayoutResourceImpl extends BaseDataLayoutResourceImpl {

	@Override
	public void deleteDataDefinitionDataLayout(Long dataDefinitionId)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.getDDMStructure(
			dataDefinitionId);

		List<DDMStructureVersion> ddmStructureVersions =
			_ddmStructureVersionLocalService.getStructureVersions(
				dataDefinitionId);

		for (DDMStructureVersion ddmStructureVersion : ddmStructureVersions) {
			List<DDMStructureLayout> ddmStructureLayouts =
				_ddmStructureLayoutLocalService.getStructureLayouts(
					ddmStructure.getGroupId(), ddmStructure.getClassNameId(),
					ddmStructureVersion.getStructureVersionId());

			for (DDMStructureLayout ddmStructureLayout : ddmStructureLayouts) {
				_deleteDataLayout(ddmStructureLayout.getStructureLayoutId());
			}
		}
	}

	@Override
	public void deleteDataLayout(Long dataLayoutId) throws Exception {
		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.getStructureLayout(dataLayoutId);

		DataDefinitionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			ddmStructureLayout.getDDMStructure(), ActionKeys.DELETE);

		_deleteDataLayout(dataLayoutId);
	}

	@Override
	public Page<DataLayout> getDataDefinitionDataLayoutsPage(
			Long dataDefinitionId, String keywords, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getDataLayouts(dataDefinitionId, keywords, pagination, sorts);
	}

	@Override
	public DataLayout getDataLayout(Long dataLayoutId) throws Exception {
		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.getStructureLayout(dataLayoutId);

		DataDefinitionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			ddmStructureLayout.getDDMStructure(), ActionKeys.VIEW);

		return _getDataLayout(dataLayoutId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public DataLayout getSiteDataLayoutByContentTypeByDataLayoutKey(
			Long siteId, String contentType, String dataLayoutKey)
		throws Exception {

		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.getStructureLayout(
				siteId,
				DataDefinitionContentTypeRegistryUtil.getClassNameId(
					contentType),
				dataLayoutKey);

		DataDefinitionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			ddmStructureLayout.getDDMStructure(), ActionKeys.VIEW);

		return _getDataLayout(
			DataDefinitionContentTypeRegistryUtil.getClassNameId(contentType),
			dataLayoutKey, siteId);
	}

	@Override
	public DataLayout postDataDefinitionDataLayout(
			Long dataDefinitionId, DataLayout dataLayout)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			dataDefinitionId);

		DataDefinitionPermissionUtil.checkPortletPermission(
			PermissionThreadLocal.getPermissionChecker(), ddmStructure,
			DataActionKeys.ADD_DATA_DEFINITION);

		_validate(dataLayout, ddmStructure);

		return _addDataLayout(
			dataDefinitionId,
			DataLayoutUtil.serialize(
				dataLayout,
				DataDefinitionDDMFormUtil.toDDMForm(
					DataDefinitionUtil.toDataDefinition(
						_ddmFormFieldTypeServicesRegistry, ddmStructure,
						_ddmStructureLayoutLocalService,
						_ddmStructureLocalService, contextHttpServletRequest,
						_spiDDMFormRuleConverter),
					_ddmFormFieldTypeServicesRegistry),
				_ddmFormFieldTypeServicesRegistry, _ddmFormLayoutSerializer,
				_ddmFormRuleDeserializer),
			dataLayout.getDataLayoutKey(), dataLayout.getDescription(),
			dataLayout.getName());
	}

	@Override
	public Response postDataLayoutContext(
			Long dataLayoutId,
			DataLayoutRenderingContext dataLayoutRenderingContext)
		throws Exception {

		_initThemeDisplay(dataLayoutRenderingContext);

		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.getDDMStructureLayout(dataLayoutId);

		DDMStructure ddmStructure = ddmStructureLayout.getDDMStructure();

		DDMForm ddmForm = ddmStructure.getDDMForm();

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setContainerId(
			dataLayoutRenderingContext.getContainerId());
		ddmFormRenderingContext.setDDMFormValues(
			MapToDDMFormValuesConverterUtil.toDDMFormValues(
				dataLayoutRenderingContext.getDataRecordValues(), ddmForm,
				null));
		ddmFormRenderingContext.setEditOnlyInDefaultLanguage(true);
		ddmFormRenderingContext.setHttpServletRequest(
			contextHttpServletRequest);
		ddmFormRenderingContext.setHttpServletResponse(
			contextHttpServletResponse);
		ddmFormRenderingContext.setLocale(
			LocaleUtil.fromLanguageId(
				contextHttpServletRequest.getHeader(
					HttpHeaders.ACCEPT_LANGUAGE)));
		ddmFormRenderingContext.setPortletNamespace(
			dataLayoutRenderingContext.getNamespace());
		ddmFormRenderingContext.setReadOnly(
			dataLayoutRenderingContext.getReadOnly());
		ddmFormRenderingContext.setShowSubmitButton(false);
		ddmFormRenderingContext.setViewMode(true);

		if (LocaleThreadLocal.getThemeDisplayLocale() == null) {
			LocaleThreadLocal.setThemeDisplayLocale(
				contextAcceptLanguage.getPreferredLocale());
		}

		Map<String, Object> ddmFormTemplateContext =
			_ddmFormTemplateContextFactory.create(
				ddmForm, ddmStructureLayout.getDDMFormLayout(),
				ddmFormRenderingContext);

		ddmFormTemplateContext.put("editable", false);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ddmFormTemplateContext.put(
			"spritemap", themeDisplay.getPathThemeSpritemap());

		ddmFormTemplateContext.remove("fieldTypes");

		return Response.ok(
			_jsonFactory.looseSerializeDeep(ddmFormTemplateContext)
		).build();
	}

	@Override
	public DataLayout putDataLayout(Long dataLayoutId, DataLayout dataLayout)
		throws Exception {

		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.getStructureLayout(dataLayoutId);

		DataDefinitionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			ddmStructureLayout.getDDMStructure(), ActionKeys.UPDATE);

		_validate(dataLayout, ddmStructureLayout.getDDMStructure());

		return _updateDataLayout(
			dataLayoutId,
			DataLayoutUtil.serialize(
				dataLayout,
				DataDefinitionDDMFormUtil.toDDMForm(
					DataDefinitionUtil.toDataDefinition(
						_ddmFormFieldTypeServicesRegistry,
						_ddmStructureLocalService.getStructure(
							ddmStructureLayout.getDDMStructureId()),
						_ddmStructureLayoutLocalService,
						_ddmStructureLocalService, contextHttpServletRequest,
						_spiDDMFormRuleConverter),
					_ddmFormFieldTypeServicesRegistry),
				_ddmFormFieldTypeServicesRegistry, _ddmFormLayoutSerializer,
				_ddmFormRuleDeserializer),
			dataLayout.getDescription(), dataLayout.getName());
	}

	private void _addDataDefinitionFieldLinks(
			long dataLayoutId, DDMFormField ddmFormField, long siteId)
		throws Exception {

		long fieldSetDDMStructureId = GetterUtil.getLong(
			ddmFormField.getProperty("ddmStructureId"));

		if (fieldSetDDMStructureId != 0) {
			_deDataDefinitionFieldLinkLocalService.addDEDataDefinitionFieldLink(
				siteId, _portal.getClassNameId(DDMStructureLayout.class),
				dataLayoutId, fieldSetDDMStructureId, ddmFormField.getName());

			for (DDMFormField nestedDDMFormField :
					ddmFormField.getNestedDDMFormFields()) {

				_addDataDefinitionFieldLinks(
					dataLayoutId, nestedDDMFormField, siteId);
			}
		}
	}

	private void _addDataDefinitionFieldLinks(
			long dataDefinitionId, long dataLayoutId, DDMForm ddmForm,
			List<String> fieldNames, long siteId)
		throws Exception {

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (String fieldName : fieldNames) {
			_deDataDefinitionFieldLinkLocalService.addDEDataDefinitionFieldLink(
				siteId, _portal.getClassNameId(DDMStructureLayout.class),
				dataLayoutId, dataDefinitionId, fieldName);

			DDMFormField ddmFormField = ddmFormFieldsMap.get(fieldName);

			if (ddmFormField != null) {
				_addDataDefinitionFieldLinks(
					dataLayoutId, ddmFormField, siteId);
			}
		}
	}

	private DataLayout _addDataLayout(
			long dataDefinitionId, String content, String dataLayoutKey,
			Map<String, Object> description, Map<String, Object> name)
		throws Exception {

		content = _updateContent(content, "2.0");

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			dataDefinitionId);

		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.addStructureLayout(
				PrincipalThreadLocal.getUserId(), ddmStructure.getGroupId(),
				ddmStructure.getClassNameId(), dataLayoutKey,
				_getDDMStructureVersionId(dataDefinitionId),
				LocalizedValueUtil.toLocaleStringMap(name),
				LocalizedValueUtil.toLocaleStringMap(description), content,
				new ServiceContext());

		_addDataDefinitionFieldLinks(
			dataDefinitionId, ddmStructureLayout.getStructureLayoutId(),
			ddmStructure.getDDMForm(), _getFieldNames(content),
			ddmStructureLayout.getGroupId());

		return DataLayoutUtil.toDataLayout(
			_ddmFormFieldTypeServicesRegistry, ddmStructureLayout,
			_spiDDMFormRuleConverter);
	}

	private void _deleteDataLayout(long dataLayoutId) throws Exception {
		_ddmStructureLayoutLocalService.deleteDDMStructureLayout(dataLayoutId);

		_deDataDefinitionFieldLinkLocalService.deleteDEDataDefinitionFieldLinks(
			_portal.getClassNameId(DDMStructureLayout.class), dataLayoutId);
	}

	private DataLayout _getDataLayout(long dataLayoutId) throws Exception {
		return DataLayoutUtil.toDataLayout(
			_ddmFormFieldTypeServicesRegistry,
			_ddmStructureLayoutLocalService.getDDMStructureLayout(dataLayoutId),
			_spiDDMFormRuleConverter);
	}

	private DataLayout _getDataLayout(
			long classNameId, String dataLayoutKey, long siteId)
		throws Exception {

		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.getStructureLayout(
				siteId, classNameId, dataLayoutKey);

		return _getDataLayout(ddmStructureLayout.getStructureLayoutId());
	}

	private Page<DataLayout> _getDataLayouts(
			long dataDefinitionId, String keywords, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		if (ArrayUtil.isEmpty(sorts)) {
			sorts = new Sort[] {
				new Sort(
					Field.getSortableFieldName(Field.MODIFIED_DATE),
					Sort.STRING_TYPE, true)
			};
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			dataDefinitionId);

		DataDefinitionContentType dataDefinitionContentType =
			DataDefinitionContentTypeRegistryUtil.getDataDefinitionContentType(
				ddmStructure.getClassNameId());

		if (Validator.isNull(keywords)) {
			return Page.of(
				HashMapBuilder.<String, Map<String, String>>put(
					"createBatch",
					() -> {
						if (dataDefinitionContentType == null) {
							return null;
						}

						return addAction(
							DDMActionKeys.ADD_STRUCTURE,
							"postDataDefinitionDataLayoutBatch",
							dataDefinitionContentType.getPortletResourceName(),
							contextCompany.getCompanyId());
					}
				).build(),
				transform(
					_ddmStructureLayoutLocalService.getStructureLayouts(
						ddmStructure.getGroupId(),
						ddmStructure.getClassNameId(),
						_getDDMStructureVersionId(dataDefinitionId),
						pagination.getStartPosition(),
						pagination.getEndPosition(),
						_toOrderByComparator(
							(Sort)ArrayUtil.getValue(sorts, 0))),
					ddmStructureLayout -> DataLayoutUtil.toDataLayout(
						_ddmFormFieldTypeServicesRegistry, ddmStructureLayout,
						_spiDDMFormRuleConverter)),
				pagination,
				_ddmStructureLayoutLocalService.getStructureLayoutsCount(
					ddmStructure.getGroupId(), ddmStructure.getClassNameId(),
					_getDDMStructureVersionId(dataDefinitionId)));
		}

		return SearchUtil.search(
			HashMapBuilder.<String, Map<String, String>>put(
				"createBatch",
				() -> {
					if (dataDefinitionContentType == null) {
						return null;
					}

					return addAction(
						DDMActionKeys.ADD_STRUCTURE,
						"postDataDefinitionDataLayoutBatch",
						dataDefinitionContentType.getPortletResourceName(),
						contextCompany.getCompanyId());
				}
			).build(),
			booleanQuery -> {
			},
			null, DDMStructureLayout.class.getName(), keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					Field.CLASS_NAME_ID, ddmStructure.getClassNameId());
				searchContext.setAttribute(Field.DESCRIPTION, keywords);
				searchContext.setAttribute(Field.NAME, keywords);
				searchContext.setAttribute(
					"structureVersionId",
					_getDDMStructureVersionId(dataDefinitionId));
				searchContext.setCompanyId(ddmStructure.getCompanyId());
				searchContext.setGroupIds(
					new long[] {ddmStructure.getGroupId()});
			},
			sorts,
			document -> DataLayoutUtil.toDataLayout(
				_ddmFormFieldTypeServicesRegistry,
				_ddmStructureLayoutLocalService.getStructureLayout(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))),
				_spiDDMFormRuleConverter));
	}

	private long _getDDMStructureVersionId(long deDataDefinitionId)
		throws PortalException {

		DDMStructureVersion ddmStructureVersion =
			_ddmStructureVersionLocalService.getLatestStructureVersion(
				deDataDefinitionId);

		return ddmStructureVersion.getStructureVersionId();
	}

	private List<String> _getFieldNames(String content) {
		DocumentContext documentContext = JsonPath.parse(content);

		return documentContext.read(
			"$[\"pages\"][*][\"rows\"][*][\"columns\"][*][\"fieldNames\"][*]");
	}

	private void _initThemeDisplay(
			DataLayoutRenderingContext dataLayoutRenderingContext)
		throws Exception {

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setScopeGroupId(
			dataLayoutRenderingContext.getScopeGroupId());
		themeDisplay.setSiteGroupId(
			dataLayoutRenderingContext.getSiteGroupId());
	}

	private DataLayoutValidationException _toDataLayoutValidationException(
		DDMFormLayoutValidationException ddmFormLayoutValidationException) {

		if (ddmFormLayoutValidationException instanceof
				DDMFormLayoutValidationException.InvalidColumnSize) {

			return new DataLayoutValidationException.InvalidColumnSize();
		}

		if (ddmFormLayoutValidationException instanceof
				DDMFormLayoutValidationException.InvalidRowSize) {

			return new DataLayoutValidationException.InvalidRowSize();
		}

		if (ddmFormLayoutValidationException instanceof
				DDMFormLayoutValidationException.MustNotDuplicateFieldName) {

			DDMFormLayoutValidationException.MustNotDuplicateFieldName
				mustNotDuplicateFieldName =
					(DDMFormLayoutValidationException.MustNotDuplicateFieldName)
						ddmFormLayoutValidationException;

			return new DataLayoutValidationException.MustNotDuplicateFieldName(
				mustNotDuplicateFieldName.getDuplicatedFieldNames());
		}

		if (ddmFormLayoutValidationException instanceof
				DDMFormLayoutValidationException.MustSetDefaultLocale) {

			return new DataLayoutValidationException.MustSetDefaultLocale();
		}

		if (ddmFormLayoutValidationException instanceof
				DDMFormLayoutValidationException.
					MustSetEqualLocaleForLayoutAndTitle) {

			return new DataLayoutValidationException.
				MustSetEqualLocaleForLayoutAndTitle();
		}

		return new DataLayoutValidationException(
			ddmFormLayoutValidationException.getCause());
	}

	private DataLayoutValidationException _toDataLayoutValidationException(
		DDMFormValidationException ddmFormValidationException) {

		if (ddmFormValidationException instanceof
				DDMFormValidationException.MustSetValidFormRuleExpression) {

			return new DataLayoutValidationException.
				MustSetValidRuleExpression();
		}

		return new DataLayoutValidationException(
			ddmFormValidationException.getCause());
	}

	private OrderByComparator<DDMStructureLayout> _toOrderByComparator(
		Sort sort) {

		boolean ascending = !sort.isReverse();

		String sortFieldName = sort.getFieldName();

		if (StringUtil.startsWith(sortFieldName, "createDate")) {
			return StructureLayoutCreateDateComparator.getInstance(ascending);
		}
		else if (StringUtil.startsWith(sortFieldName, "localized_name")) {
			return StructureLayoutNameComparator.getInstance(ascending);
		}

		return new StructureLayoutModifiedDateComparator(ascending);
	}

	private String _updateContent(
			String content, String definitionSchemaVersion)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(content);

		jsonObject.put("definitionSchemaVersion", definitionSchemaVersion);

		return jsonObject.toString();
	}

	private DataLayout _updateDataLayout(
			long dataLayoutId, String content, Map<String, Object> description,
			Map<String, Object> name)
		throws Exception {

		DDMStructureLayout ddmStructureLayout =
			_ddmStructureLayoutLocalService.getStructureLayout(dataLayoutId);

		DDMFormLayout ddmFormLayout = ddmStructureLayout.getDDMFormLayout();

		content = _updateContent(
			content, ddmFormLayout.getDefinitionSchemaVersion());

		DDMStructure ddmStructure = ddmStructureLayout.getDDMStructure();

		ddmStructureLayout =
			_ddmStructureLayoutLocalService.updateStructureLayout(
				dataLayoutId,
				_getDDMStructureVersionId(ddmStructure.getStructureId()),
				LocalizedValueUtil.toLocaleStringMap(name),
				LocalizedValueUtil.toLocaleStringMap(description), content,
				new ServiceContext());

		_deDataDefinitionFieldLinkLocalService.deleteDEDataDefinitionFieldLinks(
			_portal.getClassNameId(DDMStructureLayout.class), dataLayoutId);

		_addDataDefinitionFieldLinks(
			ddmStructure.getStructureId(),
			ddmStructureLayout.getStructureLayoutId(),
			ddmStructure.getDDMForm(), _getFieldNames(content),
			ddmStructureLayout.getGroupId());

		return DataLayoutUtil.toDataLayout(
			_ddmFormFieldTypeServicesRegistry, ddmStructureLayout,
			_spiDDMFormRuleConverter);
	}

	private void _validate(DataLayout dataLayout, DDMStructure ddmStructure)
		throws Exception {

		try {
			_ddmFormLayoutValidator.validate(
				DataLayoutUtil.toDDMFormLayout(
					dataLayout, ddmStructure.getFullHierarchyDDMForm(),
					_ddmFormFieldTypeServicesRegistry,
					_ddmFormRuleDeserializer));
		}
		catch (DDMFormLayoutValidationException
					ddmFormLayoutValidationException) {

			throw _toDataLayoutValidationException(
				ddmFormLayoutValidationException);
		}
		catch (DDMFormValidationException ddmFormValidationException) {
			throw _toDataLayoutValidationException(ddmFormValidationException);
		}
		catch (Exception exception) {
			throw new DataLayoutValidationException(exception);
		}
	}

	private static final EntityModel _entityModel = new DataLayoutEntityModel();

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference(target = "(ddm.form.layout.serializer.type=json)")
	private DDMFormLayoutSerializer _ddmFormLayoutSerializer;

	@Reference
	private DDMFormLayoutValidator _ddmFormLayoutValidator;

	@Reference
	private DDMFormRuleDeserializer _ddmFormRuleDeserializer;

	@Reference
	private DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;

	@Reference
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Reference
	private DEDataDefinitionFieldLinkLocalService
		_deDataDefinitionFieldLinkLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private SPIDDMFormRuleConverter _spiDDMFormRuleConverter;

}
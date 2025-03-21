/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.resource.v1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.search.experiences.constants.SXPActionKeys;
import com.liferay.search.experiences.constants.SXPConstants;
import com.liferay.search.experiences.exception.DuplicateSXPBlueprintExternalReferenceCodeException;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.util.ElementInstanceUtil;
import com.liferay.search.experiences.rest.dto.v1_0.util.SXPBlueprintUtil;
import com.liferay.search.experiences.rest.internal.odata.entity.v1_0.SXPBlueprintEntityModel;
import com.liferay.search.experiences.rest.internal.resource.v1_0.util.DecodeSXPUtil;
import com.liferay.search.experiences.rest.internal.resource.v1_0.util.SearchUtil;
import com.liferay.search.experiences.rest.internal.resource.v1_0.util.TitleMapUtil;
import com.liferay.search.experiences.rest.resource.v1_0.SXPBlueprintResource;
import com.liferay.search.experiences.service.SXPBlueprintService;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	properties = "OSGI-INF/liferay/rest/v1_0/sxp-blueprint.properties",
	scope = ServiceScope.PROTOTYPE, service = SXPBlueprintResource.class
)
public class SXPBlueprintResourceImpl extends BaseSXPBlueprintResourceImpl {

	@Override
	public void deleteSXPBlueprint(Long sxpBlueprintId) throws Exception {
		_sxpBlueprintService.deleteSXPBlueprint(sxpBlueprintId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityEntityModel;
	}

	@Override
	public SXPBlueprint getSXPBlueprint(Long sxpBlueprintId) throws Exception {
		return _sxpBlueprintDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), new HashMap<>(),
				_dtoConverterRegistry, contextHttpServletRequest,
				sxpBlueprintId, contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser),
			_sxpBlueprintService.getSXPBlueprint(sxpBlueprintId));
	}

	@Override
	public SXPBlueprint getSXPBlueprintByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint =
			_sxpBlueprintService.getSXPBlueprintByExternalReferenceCode(
				contextCompany.getCompanyId(), externalReferenceCode);

		return _sxpBlueprintDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), new HashMap<>(),
				_dtoConverterRegistry, contextHttpServletRequest,
				sxpBlueprint.getSXPBlueprintId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			sxpBlueprint);
	}

	@Override
	public Response getSXPBlueprintExport(Long sxpBlueprintId)
		throws Exception {

		com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint =
			_sxpBlueprintService.getSXPBlueprint(sxpBlueprintId);

		return Response.ok(
		).encoding(
			"UTF-8"
		).entity(
			JSONUtil.put(
				"configuration",
				_jsonFactory.createJSONObject(
					sxpBlueprint.getConfigurationJSON())
			).put(
				"description_i18n",
				_jsonFactory.createJSONObject(
					_jsonFactory.looseSerialize(
						sxpBlueprint.getDescriptionMap()))
			).put(
				"elementInstances",
				_jsonFactory.createJSONArray(
					sxpBlueprint.getElementInstancesJSON())
			).put(
				"externalReferenceCode", sxpBlueprint.getExternalReferenceCode()
			).put(
				"schemaVersion", sxpBlueprint.getSchemaVersion()
			).put(
				"title_i18n",
				_jsonFactory.createJSONObject(
					_jsonFactory.looseSerialize(sxpBlueprint.getTitleMap()))
			)
		).header(
			"Content-Disposition",
			StringBundler.concat(
				"attachment; filename=\"",
				sxpBlueprint.getTitle(
					contextAcceptLanguage.getPreferredLocale(), true),
				".json\"")
		).build();
	}

	@Override
	public Page<SXPBlueprint> getSXPBlueprintsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (sorts == null) {
			sorts = new Sort[] {
				new Sort("modified_sortable", Sort.LONG_TYPE, true)
			};
		}

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> SearchUtil.processSXPBooleanQuery(
				contextAcceptLanguage, booleanQuery, search),
			filter,
			com.liferay.search.experiences.model.SXPBlueprint.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (!Validator.isBlank(search)) {
					searchContext.setKeywords("");
				}
			},
			sorts,
			document -> {
				long sxpBlueprintId = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				SXPBlueprint sxpBlueprint = _sxpBlueprintDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						new HashMap<>(), _dtoConverterRegistry,
						contextHttpServletRequest,
						document.get(Field.ENTRY_CLASS_PK),
						contextAcceptLanguage.getPreferredLocale(),
						contextUriInfo, contextUser),
					_sxpBlueprintService.getSXPBlueprint(sxpBlueprintId));

				String permissionName =
					com.liferay.search.experiences.model.SXPBlueprint.class.
						getName();

				sxpBlueprint.setActions(
					() -> HashMapBuilder.put(
						"create",
						() -> addAction(
							SXPActionKeys.ADD_SXP_BLUEPRINT, "postSXPBlueprint",
							SXPConstants.RESOURCE_NAME,
							contextCompany.getCompanyId())
					).put(
						"delete",
						() -> addAction(
							ActionKeys.DELETE, "deleteSXPBlueprint",
							permissionName, sxpBlueprintId)
					).put(
						"get",
						() -> addAction(
							ActionKeys.VIEW, "getSXPBlueprint", permissionName,
							sxpBlueprintId)
					).put(
						"update",
						() -> addAction(
							ActionKeys.UPDATE, "putSXPBlueprint",
							permissionName, sxpBlueprintId)
					).build());

				return sxpBlueprint;
			});
	}

	@Override
	public SXPBlueprint postSXPBlueprint(SXPBlueprint sxpBlueprint)
		throws Exception {

		SXPBlueprintUtil.unpack(sxpBlueprint);

		DecodeSXPUtil.decodeSXPBlueprint(sxpBlueprint);

		return _sxpBlueprintDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), new HashMap<>(),
				_dtoConverterRegistry, contextHttpServletRequest,
				sxpBlueprint.getId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			_sxpBlueprintService.addSXPBlueprint(
				sxpBlueprint.getExternalReferenceCode(),
				_getConfigurationJSON(sxpBlueprint),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					sxpBlueprint.getDescription(),
					sxpBlueprint.getDescription_i18n()),
				_getElementInstancesJSON(sxpBlueprint),
				_getSchemaVersion(sxpBlueprint),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					sxpBlueprint.getTitle(), sxpBlueprint.getTitle_i18n()),
				ServiceContextFactory.getInstance(contextHttpServletRequest)));
	}

	@Override
	public SXPBlueprint postSXPBlueprintCopy(Long sxpBlueprintId)
		throws Exception {

		com.liferay.search.experiences.model.SXPBlueprint sxpBlueprint =
			_sxpBlueprintService.getSXPBlueprint(sxpBlueprintId);

		return _sxpBlueprintDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), new HashMap<>(),
				_dtoConverterRegistry, contextHttpServletRequest,
				sxpBlueprint.getSXPBlueprintId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			_sxpBlueprintService.addSXPBlueprint(
				null, sxpBlueprint.getConfigurationJSON(),
				sxpBlueprint.getDescriptionMap(),
				sxpBlueprint.getElementInstancesJSON(),
				sxpBlueprint.getSchemaVersion(),
				TitleMapUtil.copy(sxpBlueprint.getTitleMap()),
				ServiceContextFactory.getInstance(contextHttpServletRequest)));
	}

	@Override
	public SXPBlueprint postSXPBlueprintValidate(String json) throws Exception {
		SXPBlueprint sxpBlueprint = SXPBlueprintUtil.toSXPBlueprint(json);

		_validateSXPBlueprintExternalReferenceCode(sxpBlueprint);

		return sxpBlueprint;
	}

	@Override
	public SXPBlueprint putSXPBlueprint(
			Long sxpBlueprintId, SXPBlueprint sxpBlueprint)
		throws Exception {

		SXPBlueprintUtil.unpack(sxpBlueprint);

		sxpBlueprint.setId(() -> sxpBlueprintId);

		com.liferay.search.experiences.model.SXPBlueprint
			serviceBuilderSXPBlueprint = _sxpBlueprintService.fetchSXPBlueprint(
				sxpBlueprintId);

		if (serviceBuilderSXPBlueprint != null) {
			return _updateSXPBlueprint(sxpBlueprintId, sxpBlueprint);
		}

		return postSXPBlueprint(sxpBlueprint);
	}

	@Override
	public SXPBlueprint putSXPBlueprintByExternalReferenceCode(
			String externalReferenceCode, SXPBlueprint sxpBlueprint)
		throws Exception {

		com.liferay.search.experiences.model.SXPBlueprint
			serviceBuilderSXPBlueprint =
				_sxpBlueprintService.fetchSXPBlueprintByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		sxpBlueprint.setExternalReferenceCode(() -> externalReferenceCode);

		if (serviceBuilderSXPBlueprint != null) {
			return _updateSXPBlueprint(
				serviceBuilderSXPBlueprint.getSXPBlueprintId(), sxpBlueprint);
		}

		return postSXPBlueprint(sxpBlueprint);
	}

	private String _getConfigurationJSON(SXPBlueprint sxpBlueprint) {
		if (sxpBlueprint.getConfiguration() == null) {
			return null;
		}

		return String.valueOf(sxpBlueprint.getConfiguration());
	}

	private String _getElementInstancesJSON(SXPBlueprint sxpBlueprint) {
		if (ArrayUtil.isEmpty(sxpBlueprint.getElementInstances())) {
			return null;
		}

		return Arrays.toString(
			ElementInstanceUtil.unpack(sxpBlueprint.getElementInstances()));
	}

	private String _getSchemaVersion(SXPBlueprint sxpBlueprint) {
		if (sxpBlueprint.getSchemaVersion() != null) {
			return sxpBlueprint.getSchemaVersion();
		}

		return "1.1";
	}

	private SXPBlueprint _updateSXPBlueprint(
			Long sxpBlueprintId, SXPBlueprint sxpBlueprint)
		throws Exception {

		DecodeSXPUtil.decodeSXPBlueprint(sxpBlueprint);

		return _sxpBlueprintDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), new HashMap<>(),
				_dtoConverterRegistry, contextHttpServletRequest,
				sxpBlueprint.getId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			_sxpBlueprintService.updateSXPBlueprint(
				sxpBlueprint.getExternalReferenceCode(), sxpBlueprintId,
				_getConfigurationJSON(sxpBlueprint),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					sxpBlueprint.getDescription(),
					sxpBlueprint.getDescription_i18n()),
				_getElementInstancesJSON(sxpBlueprint),
				_getSchemaVersion(sxpBlueprint),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					sxpBlueprint.getTitle(), sxpBlueprint.getTitle_i18n()),
				ServiceContextFactory.getInstance(contextHttpServletRequest)));
	}

	private void _validateSXPBlueprintExternalReferenceCode(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		if (Validator.isBlank(sxpBlueprint.getExternalReferenceCode())) {
			return;
		}

		com.liferay.search.experiences.model.SXPBlueprint
			serviceBuilderSXPBlueprint =
				_sxpBlueprintLocalService.
					fetchSXPBlueprintByExternalReferenceCode(
						sxpBlueprint.getExternalReferenceCode(),
						contextCompany.getCompanyId());

		if ((serviceBuilderSXPBlueprint != null) &&
			!Objects.equals(
				serviceBuilderSXPBlueprint.getSXPBlueprintId(),
				sxpBlueprint.getId())) {

			throw new DuplicateSXPBlueprintExternalReferenceCodeException();
		}
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	private final SXPBlueprintEntityModel _entityEntityModel =
		new SXPBlueprintEntityModel();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(component.name=com.liferay.search.experiences.rest.internal.dto.v1_0.converter.SXPBlueprintDTOConverter)"
	)
	private DTOConverter
		<com.liferay.search.experiences.model.SXPBlueprint, SXPBlueprint>
			_sxpBlueprintDTOConverter;

	@Reference
	private SXPBlueprintService _sxpBlueprintLocalService;

	@Reference
	private SXPBlueprintService _sxpBlueprintService;

}
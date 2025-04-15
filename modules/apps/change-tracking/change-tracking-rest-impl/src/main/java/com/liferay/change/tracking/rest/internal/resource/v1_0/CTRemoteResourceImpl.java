/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.resource.v1_0;

import com.liferay.change.tracking.rest.dto.v1_0.CTRemote;
import com.liferay.change.tracking.rest.internal.odata.entity.v1_0.CTRemoteEntityModel;
import com.liferay.change.tracking.rest.resource.v1_0.CTRemoteResource;
import com.liferay.change.tracking.service.CTRemoteLocalService;
import com.liferay.change.tracking.service.CTRemoteService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author David Truong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/ct-remote.properties",
	scope = ServiceScope.PROTOTYPE, service = CTRemoteResource.class
)
public class CTRemoteResourceImpl extends BaseCTRemoteResourceImpl {

	@Override
	public void deleteCTRemote(Long ctRemoteId) throws PortalException {
		com.liferay.change.tracking.model.CTRemote ctRemote =
			_ctRemoteLocalService.fetchCTRemote(ctRemoteId);

		if (ctRemote != null) {
			_ctRemoteService.deleteCTRemote(ctRemote);
		}
	}

	@Override
	public CTRemote getCTRemote(Long ctRemoteId) throws Exception {
		return _toCTRemote(ctRemoteId);
	}

	@Override
	public Page<CTRemote> getCTRemotesPage(
			String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), null,
			com.liferay.change.tracking.model.CTRemote.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> _toCTRemote(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public CTRemote postCTRemote(CTRemote ctRemote) throws Exception {
		return _toCTRemote(
			_ctRemoteService.addCTRemote(
				ctRemote.getName(), ctRemote.getDescription(),
				ctRemote.getUrl(), ctRemote.getClientId(),
				ctRemote.getClientSecret()));
	}

	@Override
	public Response postCTRemotesPageExportBatch(
		String search, Sort[] sorts, String callbackURL, String contentType,
		String fieldNames) {

		return null;
	}

	@Override
	public CTRemote putCTRemote(Long ctRemoteId, CTRemote ctRemote)
		throws Exception {

		return _toCTRemote(
			_ctRemoteService.updateCTRemote(
				ctRemoteId, ctRemote.getName(), ctRemote.getDescription(),
				ctRemote.getUrl(), ctRemote.getClientId(),
				ctRemote.getClientSecret()));
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
			com.liferay.change.tracking.model.CTRemote ctRemote)
		throws Exception {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.put(
				"delete",
				() -> addAction(
					ActionKeys.DELETE, ctRemote.getCtRemoteId(),
					"deleteCTRemote", _ctRemoteModelResourcePermission)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, ctRemote.getCtRemoteId(), "getCTRemote",
					_ctRemoteModelResourcePermission)
			).put(
				"permissions",
				() -> addAction(
					ActionKeys.PERMISSIONS, ctRemote.getCtRemoteId(),
					"patchCTRemote", _ctRemoteModelResourcePermission)
			).put(
				"update",
				() -> addAction(
					ActionKeys.UPDATE, ctRemote.getCtRemoteId(), "putCTRemote",
					_ctRemoteModelResourcePermission)
			).build(),
			null, contextHttpServletRequest, ctRemote.getCtRemoteId(),
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private CTRemote _toCTRemote(
			com.liferay.change.tracking.model.CTRemote ctRemote)
		throws Exception {

		if (ctRemote == null) {
			return null;
		}

		return _toCTRemote(ctRemote.getCtRemoteId());
	}

	private CTRemote _toCTRemote(Long ctRemoteId) throws Exception {
		com.liferay.change.tracking.model.CTRemote ctRemote =
			_ctRemoteLocalService.getCTRemote(ctRemoteId);

		return _ctRemoteDTOConverter.toDTO(
			_getDTOConverterContext(ctRemote), ctRemote);
	}

	private static final EntityModel _entityModel = new CTRemoteEntityModel();

	@Reference(
		target = "(component.name=com.liferay.change.tracking.rest.internal.dto.v1_0.converter.CTRemoteDTOConverter)"
	)
	private DTOConverter<com.liferay.change.tracking.model.CTRemote, CTRemote>
		_ctRemoteDTOConverter;

	@Reference
	private CTRemoteLocalService _ctRemoteLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.change.tracking.model.CTRemote)"
	)
	private volatile ModelResourcePermission
		<com.liferay.change.tracking.model.CTRemote>
			_ctRemoteModelResourcePermission;

	@Reference
	private CTRemoteService _ctRemoteService;

}
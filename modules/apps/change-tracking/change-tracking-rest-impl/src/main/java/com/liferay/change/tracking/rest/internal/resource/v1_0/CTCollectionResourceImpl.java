/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.resource.v1_0;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.mapping.CTMappingTableInfo;
import com.liferay.change.tracking.on.demand.user.ticket.generator.CTOnDemandUserTicketGenerator;
import com.liferay.change.tracking.rest.dto.v1_0.CTCollection;
import com.liferay.change.tracking.rest.internal.odata.entity.v1_0.CTCollectionEntityModel;
import com.liferay.change.tracking.rest.resource.v1_0.CTCollectionResource;
import com.liferay.change.tracking.scheduler.PublishScheduler;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTPreferencesService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author David Truong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/ct-collection.properties",
	scope = ServiceScope.PROTOTYPE, service = CTCollectionResource.class
)
@CTAware(onProduction = true)
public class CTCollectionResourceImpl extends BaseCTCollectionResourceImpl {

	@Override
	public void deleteCTCollection(Long ctCollectionId) throws PortalException {
		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.fetchCTCollection(ctCollectionId);

		if (ctCollection != null) {
			_ctCollectionService.deleteCTCollection(ctCollection);
		}
	}

	@Override
	public void deleteCTCollectionByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.fetchCTCollectionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (ctCollection != null) {
			_ctCollectionService.deleteCTCollection(ctCollection);
		}
	}

	@Override
	public CTCollection getCTCollection(Long ctCollectionId) throws Exception {
		return _toCTCollection(ctCollectionId);
	}

	@Override
	public CTCollection getCTCollectionByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toCTCollection(externalReferenceCode);
	}

	@Override
	public String getCTCollectionByExternalReferenceCodeShareLink(
			String externalReferenceCode)
		throws Exception {

		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.getCTCollectionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return _getShareLink(ctCollection.getCtCollectionId());
	}

	@Override
	public String getCTCollectionShareLink(Long ctCollectionId)
		throws Exception {

		return _getShareLink(ctCollectionId);
	}

	@Override
	public Page<CTCollection> getCTCollectionsPage(
			String search, Integer[] statuses, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), null,
			com.liferay.change.tracking.model.CTCollection.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute("statuses", statuses);
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> _toCTCollection(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public CTCollection patchCTCollectionByExternalReferenceCode(
			String externalReferenceCode, CTCollection ctCollection)
		throws Exception {

		com.liferay.change.tracking.model.CTCollection ctCollectionModel =
			_ctCollectionLocalService.fetchCTCollectionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return _toCTCollection(
			_ctCollectionService.updateCTCollection(
				contextUser.getUserId(), ctCollectionModel.getCtCollectionId(),
				ctCollection.getName(), ctCollection.getDescription()));
	}

	@Override
	public CTCollection postCTCollection(CTCollection ctCollection)
		throws Exception {

		return _toCTCollection(
			_ctCollectionService.addCTCollection(
				ctCollection.getExternalReferenceCode(),
				contextCompany.getCompanyId(), contextUser.getUserId(), 0,
				ctCollection.getName(), ctCollection.getDescription()));
	}

	@Override
	public void postCTCollectionByExternalReferenceCodePublish(
			String externalReferenceCode)
		throws Exception {

		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.getCTCollectionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		_ctCollectionService.publishCTCollection(
			contextUser.getUserId(), ctCollection.getCtCollectionId());
	}

	@Override
	public void postCTCollectionByExternalReferenceCodeSchedulePublish(
			String externalReferenceCode, Date publishDate)
		throws Exception {

		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.getCTCollectionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		_schedulePublish(ctCollection.getCtCollectionId(), publishDate);
	}

	@Override
	public void postCTCollectionCheckout(Long ctCollectionId)
		throws PortalException {

		_ctPreferencesService.checkoutCTCollection(
			contextCompany.getCompanyId(), contextUser.getUserId(),
			ctCollectionId);
	}

	@Override
	public void postCTCollectionPublish(Long ctCollectionId)
		throws PortalException {

		_ctCollectionService.publishCTCollection(
			contextUser.getUserId(), ctCollectionId);
	}

	@Override
	public void postCTCollectionSchedulePublish(
			Long ctCollectionId, Date publishDate)
		throws PortalException {

		_schedulePublish(ctCollectionId, publishDate);
	}

	@Override
	public Response postCTCollectionsPageExportBatch(
		String search, Integer[] status, Sort[] sorts, String callbackURL,
		String contentType, String fieldNames) {

		return null;
	}

	@Override
	public CTCollection putCTCollection(
			Long ctCollectionId, CTCollection ctCollection)
		throws Exception {

		return _toCTCollection(
			_ctCollectionService.updateCTCollection(
				contextUser.getUserId(), ctCollectionId, ctCollection.getName(),
				ctCollection.getDescription()));
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
			com.liferay.change.tracking.model.CTCollection ctCollection)
		throws Exception {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.put(
				"checkout",
				() -> {
					if ((ctCollection.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) ||
						(ctCollection.getCtCollectionId() ==
							CTCollectionThreadLocal.getCTCollectionId())) {

						return null;
					}

					return addAction(
						ActionKeys.UPDATE, ctCollection.getCtCollectionId(),
						"postCTCollectionCheckout",
						_ctCollectionModelResourcePermission);
				}
			).put(
				"delete",
				() -> addAction(
					ActionKeys.DELETE, ctCollection.getCtCollectionId(),
					"deleteCTCollection", _ctCollectionModelResourcePermission)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, ctCollection.getCtCollectionId(),
					"getCTCollection", _ctCollectionModelResourcePermission)
			).put(
				"permissions",
				() -> {
					if (ctCollection.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) {

						return null;
					}

					return addAction(
						ActionKeys.PERMISSIONS,
						ctCollection.getCtCollectionId(), "patchCTCollection",
						_ctCollectionModelResourcePermission);
				}
			).put(
				"publish",
				() -> {
					if (!_isPublishEnabled(ctCollection.getCtCollectionId())) {
						return null;
					}

					return addAction(
						CTActionKeys.PUBLISH, ctCollection.getCtCollectionId(),
						"postCTCollectionPublish",
						_ctCollectionModelResourcePermission);
				}
			).put(
				"reactivate",
				() -> {
					if (ctCollection.getStatus() !=
							WorkflowConstants.STATUS_EXPIRED) {

						return null;
					}

					return addAction(
						ActionKeys.UPDATE, ctCollection.getCtCollectionId(),
						"putCTCollection",
						_ctCollectionModelResourcePermission);
				}
			).put(
				"schedule",
				() -> {
					if (!_isPublishEnabled(ctCollection.getCtCollectionId()) ||
						!PropsValues.SCHEDULER_ENABLED) {

						return null;
					}

					return addAction(
						CTActionKeys.PUBLISH, ctCollection.getCtCollectionId(),
						"postCTCollectionSchedulePublish",
						_ctCollectionModelResourcePermission);
				}
			).put(
				"update",
				() -> {
					if (ctCollection.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) {

						return null;
					}

					return addAction(
						ActionKeys.UPDATE, ctCollection.getCtCollectionId(),
						"putCTCollection",
						_ctCollectionModelResourcePermission);
				}
			).build(),
			null, contextHttpServletRequest, ctCollection.getCtCollectionId(),
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private String _getShareLink(long ctCollectionId) throws Exception {
		Ticket ticket = _ctOnDemandUserTicketGenerator.generate(ctCollectionId);

		if (ticket == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(contextUriInfo.getBaseUri());
		sb.append(Portal.PATH_MODULE);
		sb.append("/change_tracking/review_changes?ticketKey=");
		sb.append(ticket.getKey());

		return sb.toString();
	}

	private boolean _isPublishEnabled(long ctCollectionId) {
		int count = _ctEntryLocalService.getCTCollectionCTEntriesCount(
			ctCollectionId);

		if (count > 0) {
			return true;
		}

		List<CTMappingTableInfo> mappingTableInfos =
			_ctCollectionLocalService.getCTMappingTableInfos(ctCollectionId);

		if (!mappingTableInfos.isEmpty()) {
			return true;
		}

		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.fetchCTCollection(ctCollectionId);

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_DRAFT) {
			return true;
		}

		return false;
	}

	private void _schedulePublish(long ctCollectionId, Date publishDate)
		throws PortalException {

		if (publishDate == null) {
			_ctCollectionService.publishCTCollection(
				contextUser.getUserId(), ctCollectionId);

			return;
		}

		Date currentDate = new Date(System.currentTimeMillis());

		if (!publishDate.after(currentDate)) {
			throw new IllegalArgumentException(
				"The publish time must be in the future");
		}

		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.fetchCTCollection(ctCollectionId);

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_SCHEDULED) {
			_publishScheduler.unschedulePublish(ctCollectionId);
		}

		_publishScheduler.schedulePublish(
			ctCollectionId, contextUser.getUserId(), publishDate);
	}

	private CTCollection _toCTCollection(
			com.liferay.change.tracking.model.CTCollection ctCollection)
		throws Exception {

		if (ctCollection == null) {
			return null;
		}

		return _toCTCollection(ctCollection.getCtCollectionId());
	}

	private CTCollection _toCTCollection(Long ctCollectionId) throws Exception {
		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.getCTCollection(ctCollectionId);

		return _ctCollectionDTOConverter.toDTO(
			_getDTOConverterContext(ctCollection), ctCollection);
	}

	private CTCollection _toCTCollection(String externalReferenceCode)
		throws Exception {

		com.liferay.change.tracking.model.CTCollection ctCollection =
			_ctCollectionLocalService.getCTCollectionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return _ctCollectionDTOConverter.toDTO(
			_getDTOConverterContext(ctCollection), ctCollection);
	}

	private static final EntityModel _entityModel =
		new CTCollectionEntityModel();

	@Reference(
		target = "(component.name=com.liferay.change.tracking.rest.internal.dto.v1_0.converter.CTCollectionDTOConverter)"
	)
	private DTOConverter
		<com.liferay.change.tracking.model.CTCollection, CTCollection>
			_ctCollectionDTOConverter;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private volatile ModelResourcePermission
		<com.liferay.change.tracking.model.CTCollection>
			_ctCollectionModelResourcePermission;

	@Reference
	private CTCollectionService _ctCollectionService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTOnDemandUserTicketGenerator _ctOnDemandUserTicketGenerator;

	@Reference
	private CTPreferencesService _ctPreferencesService;

	@Reference
	private PublishScheduler _publishScheduler;

}
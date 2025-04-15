/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.resource.v1_0;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.rest.dto.v1_0.CTProcess;
import com.liferay.change.tracking.rest.internal.odata.entity.v1_0.CTProcessEntityModel;
import com.liferay.change.tracking.rest.resource.v1_0.CTProcessResource;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.change.tracking.service.CTProcessService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
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

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Pei-Jung Lan
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/ct-process.properties",
	scope = ServiceScope.PROTOTYPE, service = CTProcessResource.class
)
public class CTProcessResourceImpl extends BaseCTProcessResourceImpl {

	@Override
	public void deleteCTProcess(Long ctProcessId) throws Exception {
		_ctProcessService.deleteCTProcess(ctProcessId);
	}

	@Override
	public CTProcess getCTProcess(Long ctProcessId) throws Exception {
		return _toCTProcess(ctProcessId);
	}

	@Override
	public Page<CTProcess> getCTProcessesPage(
			String search, Integer[] statuses, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			com.liferay.change.tracking.model.CTProcess.class.getName(), search,
			pagination,
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
			document -> _toCTProcess(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public void postCTProcessRevert(
			Long ctProcessId, String description, String name)
		throws Exception {

		com.liferay.change.tracking.model.CTProcess ctProcess =
			_ctProcessLocalService.getCTProcess(ctProcessId);

		if (Validator.isNull(name)) {
			CTCollection ctCollection =
				_ctCollectionLocalService.getCTCollection(
					ctProcess.getCtCollectionId());

			name = StringBundler.concat(
				_language.get(
					contextAcceptLanguage.getPreferredLocale(), "revert"),
				" \"", ctCollection.getName(), "\"");
		}

		_ctCollectionService.undoCTCollection(
			ctProcess.getCtCollectionId(), contextUser.getUserId(), name,
			description);
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
			com.liferay.change.tracking.model.CTProcess ctProcess)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				ctProcess.getBackgroundTaskId());

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			ctProcess.getCtCollectionId());

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.put(
				"delete",
				() -> addAction(
					ActionKeys.DELETE, ctProcess.getCtProcessId(),
					"deleteCTProcess", _ctProcessModelResourcePermission)
			).put(
				"get",
				() -> {
					if (backgroundTask.getStatus() !=
							BackgroundTaskConstants.STATUS_SUCCESSFUL) {

						return null;
					}

					return addAction(
						ActionKeys.VIEW, ctProcess.getCtCollectionId(),
						"getCTProcess", _ctCollectionModelResourcePermission);
				}
			).put(
				"revert",
				() -> {
					if ((backgroundTask.getStatus() !=
							BackgroundTaskConstants.STATUS_SUCCESSFUL) ||
						(ctCollection == null) ||
						!_ctSchemaVersionLocalService.isLatestCTSchemaVersion(
							ctCollection.getSchemaVersionId()) ||
						!_portletResourcePermission.contains(
							PermissionThreadLocal.getPermissionChecker(), null,
							CTActionKeys.ADD_PUBLICATION)) {

						return null;
					}

					return addAction(
						ActionKeys.VIEW, ctCollection.getCtCollectionId(),
						"postCTProcessRevert",
						_ctCollectionModelResourcePermission);
				}
			).build(),
			null, contextHttpServletRequest, ctProcess.getCtCollectionId(),
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private CTProcess _toCTProcess(Long ctProcessId) throws Exception {
		com.liferay.change.tracking.model.CTProcess ctProcess =
			_ctProcessLocalService.getCTProcess(ctProcessId);

		return _ctProcessDTOConverter.toDTO(
			_getDTOConverterContext(ctProcess), ctProcess);
	}

	private static final EntityModel _entityModel = new CTProcessEntityModel();

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private volatile ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Reference
	private CTCollectionService _ctCollectionService;

	@Reference(
		target = "(component.name=com.liferay.change.tracking.rest.internal.dto.v1_0.converter.CTProcessDTOConverter)"
	)
	private DTOConverter<com.liferay.change.tracking.model.CTProcess, CTProcess>
		_ctProcessDTOConverter;

	@Reference
	private CTProcessLocalService _ctProcessLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.change.tracking.model.CTProcess)"
	)
	private volatile ModelResourcePermission
		<com.liferay.change.tracking.model.CTProcess>
			_ctProcessModelResourcePermission;

	@Reference
	private CTProcessService _ctProcessService;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

	@Reference
	private Language _language;

	@Reference(target = "(resource.name=" + CTConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}
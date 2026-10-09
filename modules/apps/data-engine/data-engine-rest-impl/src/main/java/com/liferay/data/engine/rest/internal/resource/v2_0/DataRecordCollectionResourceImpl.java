/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.internal.resource.v2_0;

import com.liferay.data.engine.constants.DataActionKeys;
import com.liferay.data.engine.content.type.DataDefinitionContentType;
import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataRecordCollection;
import com.liferay.data.engine.rest.internal.content.type.DataDefinitionContentTypeRegistryUtil;
import com.liferay.data.engine.rest.internal.dto.v2_0.util.DataRecordCollectionUtil;
import com.liferay.data.engine.rest.internal.security.permission.resource.util.DataDefinitionPermissionUtil;
import com.liferay.data.engine.rest.internal.security.permission.resource.util.DataRecordCollectionPermissionUtil;
import com.liferay.data.engine.rest.resource.v2_0.DataRecordCollectionResource;
import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.permission.PermissionUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jeyvison Nascimento
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v2_0/data-record-collection.properties",
	scope = ServiceScope.PROTOTYPE, service = DataRecordCollectionResource.class
)
@CTAware
public class DataRecordCollectionResourceImpl
	extends BaseDataRecordCollectionResourceImpl {

	@Override
	public void deleteDataRecordCollection(Long dataRecordCollectionId)
		throws Exception {

		DataRecordCollectionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			_ddlRecordSetLocalService.getDDLRecordSet(dataRecordCollectionId),
			ActionKeys.DELETE);

		_deleteDataRecordCollection(dataRecordCollectionId);
	}

	@Override
	public DataRecordCollection getDataDefinitionDataRecordCollection(
			Long dataDefinitionId)
		throws Exception {

		DataDefinitionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			_ddmStructureLocalService.getDDMStructure(dataDefinitionId),
			ActionKeys.VIEW);

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			dataDefinitionId);

		return DataRecordCollectionUtil.toDataRecordCollection(
			_ddlRecordSetLocalService.getRecordSet(
				ddmStructure.getGroupId(), ddmStructure.getStructureKey()));
	}

	@Override
	public Page<DataRecordCollection>
			getDataDefinitionDataRecordCollectionsPage(
				Long dataDefinitionId, String keywords, Pagination pagination)
		throws Exception {

		DataDefinitionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			_ddmStructureLocalService.getDDMStructure(dataDefinitionId),
			ActionKeys.VIEW);

		return _getDataRecordCollections(
			dataDefinitionId, keywords, pagination);
	}

	@Override
	public DataRecordCollection getDataRecordCollection(
			Long dataRecordCollectionId)
		throws Exception {

		DataRecordCollectionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			_ddlRecordSetLocalService.getDDLRecordSet(dataRecordCollectionId),
			ActionKeys.VIEW);

		return _getDataRecordCollection(dataRecordCollectionId);
	}

	@Override
	public String getDataRecordCollectionPermissionByCurrentUser(
			Long dataRecordCollectionId)
		throws Exception {

		JSONArray actionIdsJSONArray = _jsonFactory.createJSONArray();

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.getRecordSet(
			dataRecordCollectionId);

		String resourceName = _getResourceName(ddlRecordSet);

		List<ResourceAction> resourceActions =
			resourceActionLocalService.getResourceActions(resourceName);

		for (ResourceAction resourceAction : resourceActions) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (!permissionChecker.hasPermission(
					ddlRecordSet.getGroupId(), resourceName,
					dataRecordCollectionId, resourceAction.getActionId())) {

				continue;
			}

			actionIdsJSONArray.put(resourceAction.getActionId());
		}

		return actionIdsJSONArray.toString();
	}

	@Override
	public Page<Permission> getDataRecordCollectionPermissionsPage(
			Long dataRecordCollectionId, String roleNames)
		throws Exception {

		DataRecordCollection dataRecordCollection = _getDataRecordCollection(
			dataRecordCollectionId);

		DataDefinitionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			_ddmStructureLocalService.getDDMStructure(
				dataRecordCollection.getDataDefinitionId()),
			ActionKeys.PERMISSIONS);

		String resourceName = getPermissionCheckerResourceName(
			dataRecordCollectionId);

		return Page.of(
			transform(
				PermissionUtil.getRoles(
					contextCompany, roleLocalService,
					StringUtil.split(roleNames)),
				role -> PermissionUtil.toPermission(
					contextCompany.getCompanyId(), dataRecordCollectionId,
					resourceActionLocalService.getResourceActions(resourceName),
					resourceName, resourcePermissionLocalService, role)));
	}

	@Override
	public DataRecordCollection
			getSiteDataRecordCollectionByDataRecordCollectionKey(
				Long siteId, String dataRecordCollectionKey)
		throws Exception {

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.getRecordSet(
			siteId, dataRecordCollectionKey);

		DataRecordCollectionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			_ddlRecordSetLocalService.getDDLRecordSet(
				ddlRecordSet.getRecordSetId()),
			ActionKeys.VIEW);

		return _getSiteDataRecordCollection(dataRecordCollectionKey, siteId);
	}

	@Override
	public DataRecordCollection postDataDefinitionDataRecordCollection(
			Long dataDefinitionId, DataRecordCollection dataRecordCollection)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.getDDMStructure(
			dataDefinitionId);

		DataDefinitionPermissionUtil.checkPortletPermission(
			PermissionThreadLocal.getPermissionChecker(), ddmStructure,
			DataActionKeys.ADD_DATA_RECORD_COLLECTION);

		String dataRecordCollectionKey =
			dataRecordCollection.getDataRecordCollectionKey();

		if (Validator.isNull(dataRecordCollectionKey)) {
			dataRecordCollectionKey = ddmStructure.getStructureKey();
		}

		return _addDataRecordCollection(
			dataDefinitionId, dataRecordCollectionKey,
			dataRecordCollection.getDescription(),
			dataRecordCollection.getName());
	}

	@Override
	public DataRecordCollection putDataRecordCollection(
			Long dataRecordCollectionId,
			DataRecordCollection dataRecordCollection)
		throws Exception {

		DataRecordCollectionPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			_ddlRecordSetLocalService.getDDLRecordSet(dataRecordCollectionId),
			ActionKeys.UPDATE);

		return _updateDataRecordCollection(
			dataRecordCollectionId, dataRecordCollection.getDescription(),
			dataRecordCollection.getName());
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.getRecordSet(
			(long)id);

		return ddlRecordSet.getGroupId();
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id)
		throws Exception {

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.getRecordSet(
			(long)id);

		return _getResourceName(ddlRecordSet);
	}

	private DataRecordCollection _addDataRecordCollection(
			long dataDefinitionId, String dataRecordCollectionKey,
			Map<String, Object> description, Map<String, Object> name)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			dataDefinitionId);

		ServiceContext serviceContext = new ServiceContext();

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.addRecordSet(
			PrincipalThreadLocal.getUserId(), ddmStructure.getGroupId(),
			dataDefinitionId, dataRecordCollectionKey,
			LocalizedValueUtil.toLocaleStringMap(name),
			LocalizedValueUtil.toLocaleStringMap(description), 0,
			DDLRecordSetConstants.SCOPE_DATA_ENGINE, serviceContext);

		if (_isDataRecordCollectionPermissionCheckingEnabled(ddmStructure)) {
			_resourceLocalService.addModelResources(
				ddmStructure.getCompanyId(), ddmStructure.getGroupId(),
				PrincipalThreadLocal.getUserId(),
				_getResourceName(ddlRecordSet), ddlRecordSet.getPrimaryKey(),
				serviceContext.getModelPermissions());
		}

		return DataRecordCollectionUtil.toDataRecordCollection(ddlRecordSet);
	}

	private void _deleteDataRecordCollection(Long dataRecordCollectionId)
		throws Exception {

		_ddlRecordSetLocalService.deleteRecordSet(dataRecordCollectionId);
	}

	private DataRecordCollection _getDataRecordCollection(
			long dataRecordCollectionId)
		throws Exception {

		return DataRecordCollectionUtil.toDataRecordCollection(
			_ddlRecordSetLocalService.getRecordSet(dataRecordCollectionId));
	}

	private Page<DataRecordCollection> _getDataRecordCollections(
			long dataDefinitionId, String keywords, Pagination pagination)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			dataDefinitionId);

		if (Validator.isNull(keywords)) {
			return Page.of(
				transform(
					_ddlRecordSetLocalService.search(
						ddmStructure.getCompanyId(), ddmStructure.getGroupId(),
						keywords, DDLRecordSetConstants.SCOPE_DATA_ENGINE,
						pagination.getStartPosition(),
						pagination.getEndPosition(), null),
					DataRecordCollectionUtil::toDataRecordCollection),
				pagination,
				_ddlRecordSetLocalService.searchCount(
					ddmStructure.getCompanyId(), ddmStructure.getGroupId(),
					keywords, DDLRecordSetConstants.SCOPE_DATA_ENGINE));
		}

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			null, DDLRecordSet.class.getName(), keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.DESCRIPTION, keywords);
				searchContext.setAttribute(Field.NAME, keywords);
				searchContext.setAttribute(
					"DDMStructureId", ddmStructure.getStructureId());
				searchContext.setAttribute(
					"scope", DDLRecordSetConstants.SCOPE_DATA_ENGINE);
				searchContext.setCompanyId(ddmStructure.getCompanyId());
				searchContext.setGroupIds(
					new long[] {ddmStructure.getGroupId()});
			},
			null,
			document -> DataRecordCollectionUtil.toDataRecordCollection(
				_ddlRecordSetLocalService.getRecordSet(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private String _getResourceName(DDLRecordSet ddlRecordSet)
		throws Exception {

		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		return ResourceActionsUtil.getCompositeModelName(
			_portal.getClassName(ddmStructure.getClassNameId()),
			DDLRecordSet.class.getName());
	}

	private DataRecordCollection _getSiteDataRecordCollection(
			String dataRecordCollectionKey, long siteId)
		throws Exception {

		return DataRecordCollectionUtil.toDataRecordCollection(
			_ddlRecordSetLocalService.getRecordSet(
				siteId, dataRecordCollectionKey));
	}

	private boolean _isDataRecordCollectionPermissionCheckingEnabled(
			DDMStructure ddmStructure)
		throws Exception {

		DataDefinitionContentType dataDefinitionContentType =
			DataDefinitionContentTypeRegistryUtil.getDataDefinitionContentType(
				ddmStructure.getClassNameId());

		return dataDefinitionContentType.
			isDataRecordCollectionPermissionCheckingEnabled();
	}

	private DataRecordCollection _updateDataRecordCollection(
			long dataRecordCollectionId, Map<String, Object> description,
			Map<String, Object> name)
		throws Exception {

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.getRecordSet(
			dataRecordCollectionId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(PrincipalThreadLocal.getUserId());

		return DataRecordCollectionUtil.toDataRecordCollection(
			_ddlRecordSetLocalService.updateRecordSet(
				dataRecordCollectionId, ddlRecordSet.getDDMStructureId(),
				LocalizedValueUtil.toLocaleStringMap(name),
				LocalizedValueUtil.toLocaleStringMap(description), 0,
				serviceContext));
	}

	@Reference
	private DDLRecordSetLocalService _ddlRecordSetLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

}
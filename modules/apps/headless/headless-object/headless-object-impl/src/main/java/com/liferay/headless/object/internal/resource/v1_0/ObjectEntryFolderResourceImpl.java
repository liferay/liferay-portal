/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.object.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.dto.v1_0.ParentObjectEntryFolderBrief;
import com.liferay.headless.object.internal.odata.entity.v1_0.ObjectEntryFolderEntityModel;
import com.liferay.headless.object.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.object.exception.NoSuchObjectEntryFolderException;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alicia García
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-entry-folder.properties",
	scope = ServiceScope.PROTOTYPE, service = ObjectEntryFolderResource.class
)
public class ObjectEntryFolderResourceImpl
	extends BaseObjectEntryFolderResourceImpl {

	@Override
	public void deleteObjectEntryFolder(Long objectEntryFolderId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_objectEntryFolderService.deleteObjectEntryFolder(objectEntryFolderId);
	}

	@Override
	public void deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_objectEntryFolderService.
			deleteObjectEntryFolderByExternalReferenceCode(
				externalReferenceCode, _getGroupId(scopeKey),
				contextCompany.getCompanyId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new ObjectEntryFolderEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(
					com.liferay.object.model.ObjectEntryFolder.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public ObjectEntryFolder getObjectEntryFolder(Long objectEntryFolderId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toObjectEntryFolder(
			_objectEntryFolderService.getObjectEntryFolder(
				objectEntryFolderId));
	}

	@Override
	public ObjectEntryFolder
			getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toObjectEntryFolder(
			_objectEntryFolderService.
				getObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, _getGroupId(scopeKey),
					contextCompany.getCompanyId()));
	}

	@Override
	public Page<ObjectEntryFolder> getScopeScopeKeyObjectEntryFoldersPage(
			String scopeKey, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry == null) {
			throw new NoSuchObjectEntryFolderException();
		}

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_FOLDER, "postScopeScopeKeyObjectEntryFolder",
					com.liferay.object.model.ObjectEntryFolder.class.getName(),
					groupId)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getScopeScopeKeyObjectEntryFoldersPage",
					com.liferay.object.model.ObjectEntryFolder.class.getName(),
					groupId)
			).build(),
			booleanQuery -> {
				if (!GetterUtil.getBoolean(flatten)) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					booleanFilter.add(
						new TermFilter(Field.GROUP_ID, scopeKey),
						BooleanClauseOccur.MUST);
				}
			},
			filter, com.liferay.object.model.ObjectEntryFolder.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}

				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> _toObjectEntryFolder(
				_objectEntryFolderService.getObjectEntryFolder(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectEntryFolder patchObjectEntryFolder(
			Long objectEntryFolderId, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _patchObjectEntryFolder(
			objectEntryFolder,
			_objectEntryFolderService.getObjectEntryFolder(
				objectEntryFolderId));
	}

	@Override
	public ObjectEntryFolder
			patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				String scopeKey, String externalReferenceCode,
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _patchObjectEntryFolder(
			objectEntryFolder,
			_objectEntryFolderService.
				getObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, _getGroupId(scopeKey),
					contextCompany.getCompanyId()));
	}

	@Override
	public ObjectEntryFolder postScopeScopeKeyObjectEntryFolder(
			String scopeKey, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		return _addObjectEntryFolder(
			groupId,
			_getParentObjectEntryFolderId(
				false, objectEntryFolder.getParentObjectEntryFolderBrief(),
				groupId),
			objectEntryFolder);
	}

	@Override
	public ObjectEntryFolder
			putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				String scopeKey, String externalReferenceCode,
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		com.liferay.object.model.ObjectEntryFolder persistedObjectEntryFolder =
			_objectEntryFolderService.
				fetchObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, groupId, contextUser.getCompanyId());

		if (persistedObjectEntryFolder == null) {
			return _addObjectEntryFolder(
				groupId,
				_getParentObjectEntryFolderId(
					true, objectEntryFolder.getParentObjectEntryFolderBrief(),
					groupId),
				objectEntryFolder);
		}

		return _toObjectEntryFolder(
			_objectEntryFolderService.updateObjectEntryFolder(
				persistedObjectEntryFolder.getObjectEntryFolderId(),
				_getParentObjectEntryFolderId(
					true, objectEntryFolder.getParentObjectEntryFolderBrief(),
					groupId),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					objectEntryFolder.getLabel(),
					objectEntryFolder.getLabel_i18n()),
				objectEntryFolder.getName()));
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			long groupId, long parentObjectEntryFolderId,
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return _toObjectEntryFolder(
			_objectEntryFolderService.addObjectEntryFolder(
				objectEntryFolder.getExternalReferenceCode(), groupId,
				parentObjectEntryFolderId,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					objectEntryFolder.getLabel(),
					objectEntryFolder.getLabel_i18n()),
				objectEntryFolder.getName(),
				ServiceContextBuilder.create(
					groupId, contextHttpServletRequest,
					objectEntryFolder.getViewableByAsString()
				).build()));
	}

	private long _getGroupId(String scopeKey) throws Exception {
		Long groupId = GroupUtil.getGroupId(
			contextCompany.getCompanyId(), scopeKey, _groupLocalService);

		if (groupId != null) {
			return groupId;
		}

		if (Objects.equals(scopeKey, "0")) {
			return 0;
		}

		throw new NoSuchGroupException();
	}

	private long _getParentObjectEntryFolderId(
			boolean addObjectEntryFolder,
			ParentObjectEntryFolderBrief parentObjectEntryFolderBrief,
			long groupId)
		throws Exception {

		long parentObjectEntryFolderId = 0;

		if (parentObjectEntryFolderBrief == null) {
			return parentObjectEntryFolderId;
		}

		parentObjectEntryFolderId = GetterUtil.getLong(
			parentObjectEntryFolderBrief.getId());

		if (parentObjectEntryFolderId > 0) {
			return parentObjectEntryFolderId;
		}

		if (Validator.isNotNull(
				parentObjectEntryFolderBrief.getExternalReferenceCode())) {

			com.liferay.object.model.ObjectEntryFolder
				objectEntryFolderPersistence =
					_objectEntryFolderService.
						fetchObjectEntryFolderByExternalReferenceCode(
							parentObjectEntryFolderBrief.
								getExternalReferenceCode(),
							groupId, contextUser.getCompanyId());

			if (objectEntryFolderPersistence != null) {
				return objectEntryFolderPersistence.getObjectEntryFolderId();
			}

			if (!addObjectEntryFolder) {
				throw new NoSuchModelException();
			}

			objectEntryFolderPersistence =
				_objectEntryFolderService.addObjectEntryFolder(
					parentObjectEntryFolderBrief.getExternalReferenceCode(),
					groupId, 0,
					LocalizedMapUtil.getLocalizedMap(
						contextAcceptLanguage.getPreferredLocale(),
						parentObjectEntryFolderBrief.getLabel(),
						parentObjectEntryFolderBrief.getLabel_i18n()),
					parentObjectEntryFolderBrief.getName(),
					ServiceContextBuilder.create(
						groupId, contextHttpServletRequest, null
					).build());

			parentObjectEntryFolderId =
				objectEntryFolderPersistence.getObjectEntryFolderId();
		}

		return parentObjectEntryFolderId;
	}

	private ObjectEntryFolder _patchObjectEntryFolder(
			ObjectEntryFolder objectEntryFolder,
			com.liferay.object.model.ObjectEntryFolder
				persistedObjectEntryFolder)
		throws Exception {

		Map<String, String> labelMap = objectEntryFolder.getLabel_i18n();

		if (labelMap == null) {
			labelMap = LocalizedMapUtil.getI18nMap(
				persistedObjectEntryFolder.getLabelMap());
		}

		long parentObjectEntryFolderId = _getParentObjectEntryFolderId(
			false, objectEntryFolder.getParentObjectEntryFolderBrief(),
			persistedObjectEntryFolder.getGroupId());

		if (parentObjectEntryFolderId == 0) {
			parentObjectEntryFolderId =
				persistedObjectEntryFolder.getObjectEntryFolderId();
		}

		return _toObjectEntryFolder(
			_objectEntryFolderService.updateObjectEntryFolder(
				persistedObjectEntryFolder.getObjectEntryFolderId(),
				parentObjectEntryFolderId,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					GetterUtil.getString(
						objectEntryFolder.getLabel(),
						persistedObjectEntryFolder.getLabel()),
					labelMap),
				GetterUtil.getString(
					objectEntryFolder.getName(),
					persistedObjectEntryFolder.getName())));
	}

	private ObjectEntryFolder _toObjectEntryFolder(
			com.liferay.object.model.ObjectEntryFolder
				persistedObjectEntryFolder)
		throws Exception {

		return _objectEntryFolderDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, persistedObjectEntryFolder,
						"deleteObjectEntryFolder")
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, persistedObjectEntryFolder,
						"getObjectEntryFolder")
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, persistedObjectEntryFolder,
						"patchObjectEntryFolder")
				).build(),
				_dtoConverterRegistry,
				persistedObjectEntryFolder.getObjectEntryFolderId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.object.internal.dto.v1_0.converter.ObjectEntryFolderDTOConverter)"
	)
	private DTOConverter
		<com.liferay.object.model.ObjectEntryFolder, ObjectEntryFolder>
			_objectEntryFolderDTOConverter;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

	@Reference
	private Portal _portal;

}
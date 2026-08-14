/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.engine;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.site.pim.site.initializer.engine.PIMLinkEngine;
import com.liferay.site.pim.site.initializer.internal.util.PIMLinkUtil;
import com.liferay.site.pim.site.initializer.link.PIMLink;
import com.liferay.site.pim.site.initializer.link.PIMLinkType;
import com.liferay.site.pim.site.initializer.link.PIMLinkTypeRegistry;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = PIMLinkEngine.class)
public class PIMLinkEngineImpl implements PIMLinkEngine {

	@Override
	public void addPIMLinks(
			ObjectEntry sourceObjectEntry,
			List<ObjectEntry> targetObjectEntries, String type)
		throws PortalException {

		_validate(sourceObjectEntry, type);

		for (ObjectEntry targetObjectEntry : targetObjectEntries) {
			if (sourceObjectEntry.getGroupId() !=
					targetObjectEntry.getGroupId()) {

				throw new UnsupportedOperationException();
			}

			PIMLinkUtil.checkPermission(targetObjectEntry, ActionKeys.UPDATE);
		}

		String clusterKey = PIMLinkUtil.getClusterKey(
			_filterFactory, sourceObjectEntry, type);

		if (Validator.isNull(clusterKey)) {
			clusterKey = PortalUUIDUtil.generate();

			_addObjectEntry(clusterKey, sourceObjectEntry, type);
		}

		for (ObjectEntry targetObjectEntry : targetObjectEntries) {
			String targetClusterKey = PIMLinkUtil.getClusterKey(
				_filterFactory, targetObjectEntry, type);

			if (Validator.isNull(targetClusterKey)) {
				_addObjectEntry(clusterKey, targetObjectEntry, type);
			}
			else if (!Objects.equals(clusterKey, targetClusterKey)) {
				_partialUpdateObjectEntryClusterKey(
					targetObjectEntry.getCompanyId(),
					targetObjectEntry.getGroupId(), targetClusterKey,
					clusterKey, type);
			}
		}
	}

	@Override
	public void deletePIMLink(ObjectEntry objectEntry, String type)
		throws PortalException {

		_validate(objectEntry, type);

		objectEntry = PIMLinkUtil.fetchPIMLinkObjectEntry(
			objectEntry.getCompanyId(), _filterFactory,
			objectEntry.getGroupId(), objectEntry.getExternalReferenceCode(),
			objectEntry.getModelClassName(), type);

		if (objectEntry != null) {
			_objectEntryLocalService.deleteObjectEntry(
				objectEntry.getObjectEntryId());
		}
	}

	@Override
	public List<PIMLink> getPIMLinks(
			String filterString, ObjectEntry objectEntry)
		throws PortalException {

		PIMLinkUtil.checkPermission(objectEntry, ActionKeys.VIEW);

		String predicateString = StringBundler.concat(
			"sourceClassExternalReferenceCode eq '",
			objectEntry.getExternalReferenceCode(),
			"' and sourceClassName eq '", objectEntry.getModelClassName(), "'");

		if (Validator.isNotNull(filterString)) {
			predicateString = StringBundler.concat(
				"(", predicateString, ") and (", filterString, ")");
		}

		List<Map<String, Serializable>> valuesList = PIMLinkUtil.getValuesList(
			objectEntry.getCompanyId(), _filterFactory, predicateString,
			objectEntry.getGroupId());

		List<PIMLink> pimLinks = new ArrayList<>(
			TransformUtil.transform(
				valuesList,
				values -> {
					if (Validator.isNotNull(
							MapUtil.getString(values, "clusterKey"))) {

						return null;
					}

					ObjectEntry pimLinkObjectEntry = _fetchObjectEntry(
						MapUtil.getString(values, "targetClassName"),
						objectEntry.getCompanyId(),
						MapUtil.getString(
							values, "targetClassExternalReferenceCode"),
						objectEntry.getGroupId());

					if (pimLinkObjectEntry == null) {
						return null;
					}

					return new PIMLink(
						pimLinkObjectEntry, MapUtil.getString(values, "type"));
				}));

		List<String> clusterKeys = TransformUtil.transform(
			valuesList,
			values -> {
				String clusterKey = MapUtil.getString(values, "clusterKey");

				if (Validator.isNull(clusterKey)) {
					return null;
				}

				return clusterKey;
			});

		ListUtil.distinct(clusterKeys);

		if (clusterKeys.isEmpty()) {
			return pimLinks;
		}

		return ListUtil.concat(
			pimLinks,
			TransformUtil.transform(
				PIMLinkUtil.getValuesList(
					objectEntry.getCompanyId(), _filterFactory,
					StringBundler.concat(
						"clusterKey in ('",
						StringUtil.merge(clusterKeys, "','"), "')"),
					objectEntry.getGroupId()),
				values -> {
					if (Objects.equals(
							MapUtil.getString(
								values, "sourceClassExternalReferenceCode"),
							objectEntry.getExternalReferenceCode()) &&
						Objects.equals(
							MapUtil.getString(values, "sourceClassName"),
							objectEntry.getModelClassName())) {

						return null;
					}

					ObjectEntry pimLinkObjectEntry = _fetchObjectEntry(
						MapUtil.getString(values, "sourceClassName"),
						objectEntry.getCompanyId(),
						MapUtil.getString(
							values, "sourceClassExternalReferenceCode"),
						objectEntry.getGroupId());

					if (pimLinkObjectEntry == null) {
						return null;
					}

					return new PIMLink(
						pimLinkObjectEntry, MapUtil.getString(values, "type"));
				}));
	}

	private void _addObjectEntry(
			String clusterKey, ObjectEntry objectEntry, String type)
		throws PortalException {

		ObjectDefinition objectDefinition =
			PIMLinkUtil.getPIMLinkObjectDefinition(objectEntry.getCompanyId());

		_objectEntryLocalService.addObjectEntry(
			objectEntry.getGroupId(), PrincipalThreadLocal.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"clusterKey", clusterKey
			).put(
				"sourceClassExternalReferenceCode",
				objectEntry.getExternalReferenceCode()
			).put(
				"sourceClassName", objectEntry.getModelClassName()
			).put(
				"type", type
			).build(),
			_getServiceContext(
				objectEntry.getCompanyId(), objectEntry.getGroupId()));
	}

	private ObjectEntry _fetchObjectEntry(
			String className, long companyId, String externalReferenceCode,
			long groupId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinitionByClassName(
				companyId, className);

		return _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, groupId,
			objectDefinition.getObjectDefinitionId());
	}

	private ServiceContext _getServiceContext(long companyId, long groupId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setUserId(PrincipalThreadLocal.getUserId());

		return serviceContext;
	}

	private void _partialUpdateObjectEntryClusterKey(
			long companyId, long groupId, String sourceClusterKey,
			String targetClusterKey, String type)
		throws PortalException {

		ObjectDefinition objectDefinition =
			PIMLinkUtil.getPIMLinkObjectDefinition(companyId);

		List<Map<String, Serializable>> valuesList = PIMLinkUtil.getValuesList(
			companyId, _filterFactory,
			StringBundler.concat(
				"clusterKey eq '", sourceClusterKey, "' and type eq '", type,
				"'"),
			groupId);

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			for (Map<String, Serializable> values : valuesList) {
				_objectEntryLocalService.partialUpdateObjectEntry(
					PrincipalThreadLocal.getUserId(),
					MapUtil.getLong(
						values, objectDefinition.getPKObjectFieldName()),
					0,
					HashMapBuilder.<String, Serializable>put(
						"clusterKey", targetClusterKey
					).build(),
					_getServiceContext(companyId, groupId));
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}
	}

	private void _validate(ObjectEntry objectEntry, String type)
		throws PortalException {

		PIMLinkType pimLinkType = _pimLinkTypeRegistry.getPIMLinkType(type);

		if ((pimLinkType == null) || !pimLinkType.isClustered()) {
			throw new UnsupportedOperationException();
		}

		PIMLinkUtil.checkPermission(objectEntry, ActionKeys.UPDATE);
	}

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private PIMLinkTypeRegistry _pimLinkTypeRegistry;

}
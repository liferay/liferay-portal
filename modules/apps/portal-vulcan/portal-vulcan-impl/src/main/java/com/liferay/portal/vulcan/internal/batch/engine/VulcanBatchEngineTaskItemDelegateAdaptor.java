/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.batch.engine.strategy.BatchEngineImportStrategy;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.util.GroupUtil;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Preston Crary
 */
public class VulcanBatchEngineTaskItemDelegateAdaptor<T>
	implements BatchEngineTaskItemDelegate<T> {

	public VulcanBatchEngineTaskItemDelegateAdaptor(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService,
		VulcanBatchEngineTaskItemDelegate<T>
			vulcanBatchEngineTaskItemDelegate) {

		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;
		_vulcanBatchEngineTaskItemDelegate = vulcanBatchEngineTaskItemDelegate;

		vulcanBatchEngineTaskItemDelegate.setGroupLocalService(
			groupLocalService);
		vulcanBatchEngineTaskItemDelegate.setResourceActionLocalService(
			resourceActionLocalService);
		vulcanBatchEngineTaskItemDelegate.setResourcePermissionLocalService(
			resourcePermissionLocalService);
		vulcanBatchEngineTaskItemDelegate.setRoleLocalService(roleLocalService);
	}

	@Override
	public void create(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		_vulcanBatchEngineTaskItemDelegate.create(
			items, _applyParamConverters(parameters));
	}

	@Override
	public void delete(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		_vulcanBatchEngineTaskItemDelegate.delete(
			items, _applyParamConverters(parameters));
	}

	@Override
	public Set<String> getAvailableCreateStrategies() {
		return _vulcanBatchEngineTaskItemDelegate.
			getAvailableCreateStrategies();
	}

	@Override
	public Set<String> getAvailableUpdateStrategies() {
		return _vulcanBatchEngineTaskItemDelegate.
			getAvailableUpdateStrategies();
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return _vulcanBatchEngineTaskItemDelegate.getEntityModel(
			multivaluedMap);
	}

	@Override
	public Class<T> getItemClass() {
		Class<? extends VulcanBatchEngineTaskItemDelegate> clazz =
			_vulcanBatchEngineTaskItemDelegate.getClass();

		Class<T> itemClass = _getItemClassFromGenericInterfaces(
			clazz.getGenericInterfaces());

		if (itemClass == null) {
			Class<?> superclass = clazz.getSuperclass();

			itemClass = _getItemClassFromGenericInterfaces(
				superclass.getGenericInterfaces());
		}

		return itemClass;
	}

	@Override
	public boolean hasCreateStrategy(String createStrategy) {
		Set<String> createStrategies =
			_vulcanBatchEngineTaskItemDelegate.getAvailableCreateStrategies();

		return createStrategies.contains(createStrategy);
	}

	@Override
	public boolean hasUpdateStrategy(String updateStrategy) {
		Set<String> updateStrategies =
			_vulcanBatchEngineTaskItemDelegate.getAvailableUpdateStrategies();

		return updateStrategies.contains(updateStrategy);
	}

	@Override
	public Page<T> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		com.liferay.portal.vulcan.pagination.Page<T> page =
			_vulcanBatchEngineTaskItemDelegate.read(
				filter,
				com.liferay.portal.vulcan.pagination.Pagination.of(
					pagination.getPage(), pagination.getPageSize()),
				sorts, _applyParamConverters(parameters), search);

		return Page.of(page.getItems(), pagination, page.getTotalCount());
	}

	@Override
	public void setBatchEngineImportStrategy(
		BatchEngineImportStrategy batchEngineImportStrategy) {

		_vulcanBatchEngineTaskItemDelegate.setContextBatchUnsafeBiConsumer(
			(collection, unsafeFunction) -> batchEngineImportStrategy.apply(
				this, collection, unsafeFunction));
	}

	@Override
	public void setContextCompany(Company contextCompany) {
		_company = contextCompany;
		_vulcanBatchEngineTaskItemDelegate.setContextCompany(contextCompany);
	}

	@Override
	public void setContextUriInfo(UriInfo uriInfo) {
		_vulcanBatchEngineTaskItemDelegate.setContextUriInfo(uriInfo);
	}

	@Override
	public void setContextUser(User contextUser) {
		_vulcanBatchEngineTaskItemDelegate.setContextUser(contextUser);
	}

	@Override
	public void setLanguageId(String languageId) {
		_vulcanBatchEngineTaskItemDelegate.setLanguageId(languageId);
	}

	@Override
	public void update(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		_vulcanBatchEngineTaskItemDelegate.update(
			items, _applyParamConverters(parameters));
	}

	private Map<String, Serializable> _applyParamConverters(
		Map<String, Serializable> parameters) {

		if (parameters == null) {
			return new HashMap<>();
		}

		for (Map.Entry<String, Serializable> entry : parameters.entrySet()) {
			String key = entry.getKey();
			Serializable value = entry.getValue();

			if (key.equals("assetLibraryId") && (value != null)) {
				parameters.put(
					key,
					GroupUtil.getDepotGroupId(
						String.valueOf(value), _company.getCompanyId(),
						_depotEntryLocalService, _groupLocalService));
			}
			else if (key.equals("siteId") && (value != null)) {
				parameters.put(
					key,
					GroupUtil.getGroupId(
						_company.getCompanyId(), String.valueOf(value),
						_groupLocalService));
			}
		}

		return parameters;
	}

	private Class<T> _getItemClassFromGenericInterfaces(
		Type[] genericInterfaceTypes) {

		for (Type genericInterfaceType : genericInterfaceTypes) {
			if (genericInterfaceType instanceof ParameterizedType) {
				ParameterizedType parameterizedType =
					(ParameterizedType)genericInterfaceType;

				if (parameterizedType.getRawType() !=
						VulcanBatchEngineTaskItemDelegate.class) {

					continue;
				}

				Type[] genericTypes =
					parameterizedType.getActualTypeArguments();

				return (Class<T>)genericTypes[0];
			}
		}

		return null;
	}

	private Company _company;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final VulcanBatchEngineTaskItemDelegate<T>
		_vulcanBatchEngineTaskItemDelegate;

}
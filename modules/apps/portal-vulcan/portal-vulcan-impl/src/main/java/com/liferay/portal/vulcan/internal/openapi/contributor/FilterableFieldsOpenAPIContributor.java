/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.openapi.contributor;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.openapi.contributor.OpenAPIContributor;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Magdalena Jedraszak
 */
@Component(service = OpenAPIContributor.class)
public class FilterableFieldsOpenAPIContributor implements OpenAPIContributor {

	@Override
	public void contribute(OpenAPI openAPI, OpenAPIContext openAPIContext)
		throws Exception {

		if ((openAPIContext == null) || (openAPI.getComponents() == null)) {
			return;
		}

		Map<String, Schema> schemas = openAPI.getComponents(
		).getSchemas();

		if (MapUtil.isEmpty(schemas)) {
			return;
		}

		for (Schema schema : schemas.values()) {
			schema.addExtension(
				"x-filterable",
				_getFilterableFieldNames(openAPIContext, schema));
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, null, "(osgi.jaxrs.resource=true)",
			(serviceReference, emitter) -> {
				try {
					if (!(_bundleContext.getService(serviceReference) instanceof
							EntityModelResource)) {

						return;
					}

					String apiVersion = (String)serviceReference.getProperty(
						"api.version");
					String entityClassName =
						(String)serviceReference.getProperty(
							"entity.class.name");
					Object companyIdObject = serviceReference.getProperty(
						"companyId");

					if (companyIdObject instanceof List) {
						for (Object object : (List<?>)companyIdObject) {
							emitter.emit(
								_encodeKey(
									entityClassName, GetterUtil.getLong(object),
									apiVersion));
						}

						return;
					}

					emitter.emit(
						_encodeKey(
							entityClassName,
							GetterUtil.getLong(companyIdObject), apiVersion));
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _encodeKey(
		String className, Long companyId, String version) {

		String key = StringBundler.concat(
			className, StringPool.POUND, GetterUtil.getString(version, "v1.0"));

		if (Validator.isNull(companyId)) {
			return key;
		}

		return key + StringPool.POUND + companyId;
	}

	private String _getClassName(String className, String schemaName) {
		if (schemaName != null) {
			return className + "#" + StringUtil.toLowerCase(schemaName);
		}

		return className;
	}

	private Map<String, EntityField> _getEntityFieldsMap(
			OpenAPIContext openAPIContext, Schema schema)
		throws Exception {

		Map<String, Schema> properties = schema.getProperties();

		if (properties == null) {
			return null;
		}

		Schema xClassNameSchema = properties.get("x-class-name");

		if (xClassNameSchema == null) {
			return null;
		}

		String xClassNameDefault = (String)xClassNameSchema.getDefault();

		if (Validator.isBlank(xClassNameDefault)) {
			return null;
		}

		Schema xSchemaNameSchema = properties.get("x-schema-name");

		String xSchemaName = null;

		if (xSchemaNameSchema != null) {
			xSchemaName = (String)xSchemaNameSchema.getDefault();
		}

		EntityModelResource entityModelResource = _getEntityModelResource(
			CompanyThreadLocal.getCompanyId(),
			_getClassName(xClassNameDefault, xSchemaName),
			openAPIContext.getVersion());

		if (entityModelResource == null) {
			return null;
		}

		entityModelResource.setContextCompany(
			_companyLocalService.getCompany(CompanyThreadLocal.getCompanyId()));

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return null;
		}

		return entityModel.getEntityFieldsMap();
	}

	private EntityModelResource _getEntityModelResource(
		long companyId, String className, String version) {

		String companyIdKey = _encodeKey(className, companyId, version);

		if (_serviceTrackerMap.containsKey(companyIdKey)) {
			return _serviceTrackerMap.getService(companyIdKey);
		}

		String key = _encodeKey(className, null, version);

		if (_serviceTrackerMap.containsKey(key)) {
			return _serviceTrackerMap.getService(key);
		}

		return null;
	}

	private List<String> _getFilterableFieldNames(
			OpenAPIContext openAPIContext, Schema schema)
		throws Exception {

		Map<String, EntityField> entityFieldsMap = _getEntityFieldsMap(
			openAPIContext, schema);

		if (MapUtil.isEmpty(entityFieldsMap)) {
			return new ArrayList<>();
		}

		List<String> filterableFieldNames = new ArrayList<>();

		Set<EntityField> visitedEntityFields = new HashSet<>();

		Queue<Map.Entry<String, EntityField>> queue = new LinkedList<>(
			entityFieldsMap.entrySet());

		while (!queue.isEmpty()) {
			Map.Entry<String, EntityField> entry1 = queue.poll();

			String fieldName = entry1.getKey();

			if (StringUtil.count(fieldName, '/') >= 5) {
				continue;
			}

			EntityField entityField = entry1.getValue();

			if (!(entityField instanceof ComplexEntityField)) {
				filterableFieldNames.add(fieldName);

				continue;
			}

			ComplexEntityField complexEntityField =
				(ComplexEntityField)entityField;

			if (!visitedEntityFields.add(complexEntityField)) {
				continue;
			}

			Map<String, EntityField> complexEntityFieldEntityFieldsMap =
				complexEntityField.getEntityFieldsMap();

			for (Map.Entry<String, EntityField> entry2 :
					complexEntityFieldEntityFieldsMap.entrySet()) {

				queue.add(
					new AbstractMap.SimpleEntry<>(
						entry1.getKey() + "/" + entry2.getKey(),
						entry2.getValue()));
			}
		}

		return ListUtil.sort(filterableFieldNames);
	}

	private BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	private ServiceTrackerMap<String, EntityModelResource> _serviceTrackerMap;

}
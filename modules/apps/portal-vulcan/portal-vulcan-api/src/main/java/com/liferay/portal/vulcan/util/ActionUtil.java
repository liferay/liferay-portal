/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryServiceUtil;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.liferay.OAuth2ProviderScopeLiferayAccessControlContext;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.graphql.util.GraphQLNamingUtil;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Javier Gamarra
 */
public class ActionUtil {

	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, GroupedModel groupedModel,
		String methodName, Object object, UriInfo uriInfo) {

		return addAction(
			actionName, clazz, (Long)groupedModel.getPrimaryKeyObj(),
			methodName, object, groupedModel.getUserId(),
			groupedModel.getModelClassName(), groupedModel.getGroupId(),
			uriInfo);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addAction(String, Class, Long, String, Object, Long, String,
	 *             Long, UriInfo)}
	 */
	@Deprecated
	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, GroupedModel groupedModel,
		String methodName, UriInfo uriInfo) {

		return addAction(
			actionName, clazz, (Long)groupedModel.getPrimaryKeyObj(),
			methodName, null, groupedModel.getUserId(),
			groupedModel.getModelClassName(), groupedModel.getGroupId(),
			uriInfo);
	}

	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, Long id, String methodName,
		ModelResourcePermission<?> modelResourcePermission, Long parameterId,
		UriInfo uriInfo) {

		try {
			return _addAction(
				actionName, clazz, id, methodName, modelResourcePermission,
				null, null, parameterId, null, null, null, uriInfo,
				() -> UriInfoUtil.getBaseUriBuilder(uriInfo));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, Long id, String methodName,
		Object object, Long ownerId, String permissionName, Long siteId,
		Supplier<UriBuilder> uriBuilderSupplier, UriInfo uriInfo) {

		try {
			return _addAction(
				actionName, clazz, id, methodName, null, object, ownerId, id,
				permissionName, siteId, null, uriInfo, uriBuilderSupplier);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addAction(String, Class, Long, String, Object,
	 *             ModelResourcePermission, UriInfo)}
	 */
	@Deprecated
	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, Long id, String methodName,
		Object object, Long ownerId, String permissionName, Long siteId,
		UriInfo uriInfo) {

		try {
			return addAction(
				actionName, clazz, id, methodName, object, ownerId,
				permissionName, siteId,
				() -> UriInfoUtil.getBaseUriBuilder(uriInfo), uriInfo);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, Long id, String methodName,
		Object object, ModelResourcePermission<?> modelResourcePermission,
		Map<String, String> templateParameterMap, UriInfo uriInfo) {

		try {
			return _addAction(
				actionName, clazz, id, methodName, modelResourcePermission,
				object, null, id, null, null, templateParameterMap, uriInfo,
				() -> UriInfoUtil.getBaseUriBuilder(uriInfo));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, Long id, String methodName,
		Object object, ModelResourcePermission<?> modelResourcePermission,
		UriInfo uriInfo) {

		try {
			return _addAction(
				actionName, clazz, id, methodName, modelResourcePermission,
				object, null, id, null, null, null, uriInfo,
				() -> UriInfoUtil.getBaseUriBuilder(uriInfo));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addAction(String, Class, Long, String, Object, Long, String,
	 *             Long, UriInfo)}
	 */
	@Deprecated
	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, Long id, String methodName,
		String permissionName, Long siteId, UriInfo uriInfo) {

		return addAction(
			actionName, clazz, id, methodName, null, null, permissionName,
			siteId, uriInfo);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addAction(String, Class, Long, String, Object, Long, String,
	 *             Long, UriInfo)}
	 */
	@Deprecated
	public static Map<String, String> addAction(
		String actionName, Class<?> clazz, Long id, String methodName,
		String permissionName, Object object, Long siteId, UriInfo uriInfo) {

		return addAction(
			actionName, clazz, id, methodName, object, null, permissionName,
			siteId, uriInfo);
	}

	private static Map<String, String> _addAction(
			String actionName, Class<?> clazz, Long id, String methodName,
			ModelResourcePermission<?> modelResourcePermission, Object object,
			Long ownerId, Long parameterId, String permissionName, Long siteId,
			Map<String, String> templateParameterMap, UriInfo uriInfo,
			Supplier<UriBuilder> uriBuilderSupplier)
		throws Exception {

		if (uriInfo == null) {
			return new HashMap<>();
		}

		MultivaluedMap<String, String> queryParameters =
			uriInfo.getQueryParameters();

		String restrictFields = queryParameters.getFirst("restrictFields");

		if (restrictFields != null) {
			List<String> strings = Arrays.asList(restrictFields.split(","));

			if (strings.contains("actions")) {
				return null;
			}
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((modelResourcePermission == null) && (id != null)) {
			List<String> modelResourceActions =
				ResourceActionsUtil.getModelResourceActions(permissionName);

			if (!modelResourceActions.contains(actionName) ||
				!_hasPermission(
					actionName, id, ownerId, permissionChecker, permissionName,
					siteId)) {

				return null;
			}
		}
		else if ((id != null) &&
				 !modelResourcePermission.contains(
					 permissionChecker, id, actionName)) {

			return null;
		}

		Method method = _getMethod(clazz, methodName);

		String httpMethodName = _getHttpMethodName(clazz, method);

		if ((object != null) &&
			OAuth2ProviderScopeLiferayAccessControlContext.
				isOAuth2AuthVerified()) {

			ScopeChecker scopeChecker = (ScopeChecker)object;

			if (!scopeChecker.checkScope(httpMethodName)) {
				return null;
			}
		}

		String basePath = UriInfoUtil.getBasePath(uriInfo);

		if (basePath.contains("/graphql")) {
			String operation = null;
			String type = null;

			if (httpMethodName.equals("GET")) {
				Class<?> returnType = method.getReturnType();

				operation = GraphQLNamingUtil.getGraphQLPropertyName(
					methodName, returnType.getName(),
					TransformUtil.transformToList(
						clazz.getMethods(), Method::getName));

				type = "query";
			}
			else {
				operation = GraphQLNamingUtil.getGraphQLMutationName(
					methodName);
				type = "mutation";
			}

			return HashMapBuilder.put(
				"operation", operation
			).put(
				"type", type
			).build();
		}

		return HashMapBuilder.put(
			"href",
			() -> {
				UriBuilder uriBuilder = uriBuilderSupplier.get();

				if (clazz.getSuperclass(
					).isAnnotationPresent(
						Path.class
					)) {

					uriBuilder = uriBuilder.path(clazz.getSuperclass());
				}

				uriBuilder = uriBuilder.path(clazz.getSuperclass(), methodName);

				if (parameterId != null) {
					uriBuilder = uriBuilder.resolveTemplates(
						_getParameterMap(
							clazz, parameterId, methodName, siteId,
							templateParameterMap, uriInfo),
						false);
				}

				return uriBuilder.toTemplate();
			}
		).put(
			"method", httpMethodName
		).build();
	}

	private static String _getFirstParameterNameFromPath(
		Class<?> clazz, String methodName) {

		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			if (Objects.equals(method.getName(), methodName)) {
				Path path = method.getAnnotation(Path.class);

				if (path == null) {
					return null;
				}

				Matcher matcher = _pattern.matcher(path.value());

				if (matcher.find()) {
					return matcher.group(1);
				}

				return null;
			}
		}

		return null;
	}

	private static String _getHttpMethodName(Class<?> clazz, Method method)
		throws Exception {

		Class<?> superClass = clazz.getSuperclass();

		Method superMethod = superClass.getMethod(
			method.getName(), method.getParameterTypes());

		for (Annotation annotation : superMethod.getAnnotations()) {
			Class<? extends Annotation> annotationType =
				annotation.annotationType();

			Annotation[] annotations = annotationType.getAnnotationsByType(
				HttpMethod.class);

			if (annotations.length > 0) {
				HttpMethod httpMethod = (HttpMethod)annotations[0];

				return httpMethod.value();
			}
		}

		return null;
	}

	private static Method _getMethod(Class<?> clazz, String methodName) {
		for (Method method : clazz.getMethods()) {
			if (!methodName.equals(method.getName())) {
				continue;
			}

			return method;
		}

		return null;
	}

	private static Map<String, Object> _getParameterMap(
			Class<?> clazz, Long id, String methodName, Long siteId,
			Map<String, String> templateParameterMap, UriInfo uriInfo)
		throws PortalException {

		Map<String, Object> parameterMap = new HashMap<>();

		MultivaluedMap<String, String> pathParameters =
			uriInfo.getPathParameters();

		for (Map.Entry<String, List<String>> entry :
				pathParameters.entrySet()) {

			List<String> value = entry.getValue();

			parameterMap.put(entry.getKey(), value.get(0));
		}

		if (templateParameterMap != null) {
			parameterMap.putAll(templateParameterMap);
		}

		String firstParameterName = _getFirstParameterNameFromPath(
			clazz.getSuperclass(), methodName);

		if (Validator.isNull(firstParameterName)) {
			return parameterMap;
		}

		if ((siteId != null) &&
			Objects.equals(firstParameterName, "assetLibraryId")) {

			DepotEntry depotEntry = DepotEntryServiceUtil.getGroupDepotEntry(
				siteId);

			parameterMap.put(firstParameterName, depotEntry.getDepotEntryId());
		}
		else if (Objects.equals(firstParameterName, "id")) {
			parameterMap.put(firstParameterName, id);
		}
		else if ((siteId != null) &&
				 Objects.equals(firstParameterName, "siteId")) {

			parameterMap.put(firstParameterName, siteId);
		}
		else if (StringUtil.endsWith(firstParameterName, "Id")) {
			parameterMap.put(firstParameterName, id);
		}

		return parameterMap;
	}

	private static boolean _hasPermission(
		String actionName, Long id, Long ownerId,
		PermissionChecker permissionChecker, String permissionName,
		Long siteId) {

		if (((ownerId != null) &&
			 permissionChecker.hasOwnerPermission(
				 permissionChecker.getCompanyId(), permissionName, id, ownerId,
				 actionName)) ||
			permissionChecker.hasPermission(
				siteId, permissionName, id, actionName)) {

			return true;
		}

		return false;
	}

	private static final Pattern _pattern = Pattern.compile("\\{(.*?)\\}");

}
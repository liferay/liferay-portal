/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.display;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.display.UADHierarchyDeclaration;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;
import com.liferay.user.associated.data.web.internal.util.UADLanguageUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class UADHierarchyDisplay {

	public UADHierarchyDisplay(
		UADHierarchyDeclaration uadHierarchyDeclaration,
		UADRegistry uadRegistry) {

		_uadHierarchyDeclaration = uadHierarchyDeclaration;
		_uadRegistry = uadRegistry;

		_containerUADDisplays =
			uadHierarchyDeclaration.getContainerUADDisplays();

		_containerTypeKeys = TransformUtil.transform(
			_containerUADDisplays, UADDisplay::getTypeKey, String.class);

		_uadDisplays = ArrayUtil.append(
			_containerUADDisplays,
			uadHierarchyDeclaration.getNoncontainerUADDisplays());

		_typeKeys = TransformUtil.transform(
			_uadDisplays, UADDisplay::getTypeKey, String.class);
	}

	public <T> void addPortletBreadcrumbEntries(
			HttpServletRequest httpServletRequest,
			RenderResponse renderResponse, Locale locale)
		throws Exception {

		PortletURL baseURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).setParameter(
			"applicationKey",
			ParamUtil.getString(httpServletRequest, "applicationKey")
		).setParameter(
			"p_u_i_d", ParamUtil.getString(httpServletRequest, "p_u_i_d")
		).setParameter(
			"scope",
			() -> {
				String scope = ParamUtil.getString(httpServletRequest, "scope");

				if (Validator.isNotNull(scope)) {
					return scope;
				}

				return null;
			}
		).buildPortletURL();

		String className = ParamUtil.getString(
			httpServletRequest, "parentContainerTypeKey");

		UADDisplay<Object> uadDisplay =
			(UADDisplay<Object>)_uadRegistry.getUADDisplay(className);

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest,
			UADLanguageUtil.getApplicationName(uadDisplay, locale),
			PortletURLBuilder.create(
				PortletURLUtil.clone(baseURL, renderResponse)
			).setMVCRenderCommandName(
				"/user_associated_data/review_uad_data"
			).buildString());

		List<KeyValuePair> parentBreadcrumbs = new ArrayList<>();

		String primaryKey = ParamUtil.getString(
			httpServletRequest, "parentContainerId");

		Object container = uadDisplay.get(primaryKey);

		String parentContainerTypeKey = uadDisplay.getParentContainerTypeKey();
		String parentContainerId = String.valueOf(
			uadDisplay.getParentContainerId(container));

		while (!parentContainerId.equals("0") &&
			   !parentContainerId.equals("-1")) {

			UADDisplay<Object> parentContainerUADDisplay =
				(UADDisplay<Object>)_uadRegistry.getUADDisplay(
					parentContainerTypeKey);

			String parentContainerName = parentContainerUADDisplay.getName(
				parentContainerUADDisplay.get(parentContainerId), locale);

			parentBreadcrumbs.add(
				new KeyValuePair(
					parentContainerName,
					PortletURLBuilder.create(
						PortletURLUtil.clone(baseURL, renderResponse)
					).setMVCRenderCommandName(
						"/user_associated_data/view_uad_hierarchy"
					).setParameter(
						"parentContainerId", parentContainerId
					).setParameter(
						"parentContainerTypeKey", parentContainerTypeKey
					).buildString()));

			parentContainerTypeKey =
				parentContainerUADDisplay.getParentContainerTypeKey();
			parentContainerId = String.valueOf(
				parentContainerUADDisplay.getParentContainerId(
					parentContainerUADDisplay.get(parentContainerId)));
		}

		Collections.reverse(parentBreadcrumbs);

		for (KeyValuePair keyValuePair : parentBreadcrumbs) {
			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, keyValuePair.getKey(),
				keyValuePair.getValue());
		}

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, uadDisplay.getName(container, locale), null);
	}

	public long countAll(long userId) {
		long count = 0;

		for (UADDisplay<?> uadDisplay : _uadDisplays) {
			count += uadDisplay.count(userId);
		}

		return count;
	}

	public String[] getColumnFieldNames() {
		return ArrayUtil.append(
			new String[] {"name", "count"},
			_uadHierarchyDeclaration.getExtraColumnNames());
	}

	public Map<String, List<Serializable>> getContainerItemPKsMap(
		String parentContainerTypeKey, Serializable parentContainerId,
		long userId) {

		Map<String, List<Serializable>> containerItemPKsMap =
			new LinkedHashMap<>();

		if (ArrayUtil.contains(_containerTypeKeys, parentContainerTypeKey)) {
			for (UADDisplay<?> containerItemUADDisplay : _uadDisplays) {
				String containerItemTypeKey =
					containerItemUADDisplay.getTypeKey();

				_addEntities(
					containerItemPKsMap,
					_getContainerItemPKs(
						parentContainerTypeKey, parentContainerId,
						containerItemTypeKey, userId),
					containerItemTypeKey);
			}
		}

		return containerItemPKsMap;
	}

	public <T> String getEditURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, T object)
		throws Exception {

		T unwrappedObject = unwrap(object);

		UADDisplay<T> uadDisplay =
			(UADDisplay<T>)_uadRegistry.getUADDisplayByObject(unwrappedObject);

		return uadDisplay.getEditURL(
			unwrappedObject, liferayPortletRequest, liferayPortletResponse);
	}

	public String getEntitiesTypeLabel(Locale locale) {
		return _uadHierarchyDeclaration.getEntitiesTypeLabel(locale);
	}

	public <T> Map<String, Object> getFieldValues(T object, Locale locale) {
		Map<String, Object> fieldValues = new LinkedHashMap<>();

		UADDisplay<T> uadDisplay =
			(UADDisplay<T>)_uadRegistry.getUADDisplayByObject(unwrap(object));

		if (uadDisplay != null) {
			if (object instanceof ContainerDisplay) {
				ContainerDisplay<T> containerDisplay =
					(ContainerDisplay<T>)object;

				T containerObject = containerDisplay.getContainer();

				fieldValues.put(
					"name", uadDisplay.getName(containerObject, locale));

				fieldValues.put("count", containerDisplay.getCount());

				fieldValues.putAll(
					uadDisplay.getFieldValues(
						containerObject,
						_uadHierarchyDeclaration.getExtraColumnNames(),
						locale));
			}
			else {
				fieldValues.put("name", uadDisplay.getName(object, locale));

				fieldValues.put("count", "--");

				fieldValues.putAll(
					uadDisplay.getFieldValues(
						object, _uadHierarchyDeclaration.getExtraColumnNames(),
						locale));
			}
		}

		return fieldValues;
	}

	public String getFirstContainerTypeKey() {
		return _containerTypeKeys[0];
	}

	public <T> String getParentContainerURL(
			ActionRequest actionRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		String className = ParamUtil.getString(
			actionRequest, "parentContainerTypeKey");

		if (Validator.isNull(className)) {
			return null;
		}

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setParameter(
			"applicationKey",
			ParamUtil.getString(actionRequest, "applicationKey")
		).setParameter(
			"p_u_i_d", ParamUtil.getString(actionRequest, "p_u_i_d")
		).setParameter(
			"scope",
			() -> {
				String scope = ParamUtil.getString(actionRequest, "scope");

				if (Validator.isNotNull(scope)) {
					return scope;
				}

				return null;
			}
		).buildPortletURL();

		UADDisplay<Object> uadDisplay =
			(UADDisplay<Object>)_uadRegistry.getUADDisplay(className);

		String primaryKey = ParamUtil.getString(
			actionRequest, "parentContainerId");

		Object container = uadDisplay.get(primaryKey);

		String parentContainerId = String.valueOf(
			uadDisplay.getParentContainerId(container));

		if (parentContainerId.equals("0") || parentContainerId.equals("-1")) {
			portletURL.setParameter(
				"mvcRenderCommandName",
				"/user_associated_data/review_uad_data");
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName",
				"/user_associated_data/view_uad_hierarchy");
			portletURL.setParameter(
				"parentContainerTypeKey", uadDisplay.getTypeKey());
			portletURL.setParameter("parentContainerId", parentContainerId);
		}

		return portletURL.toString();
	}

	public <T> Serializable getPrimaryKey(T object) {
		T unwrappedObject = unwrap(object);

		UADDisplay<T> uadDisplay =
			(UADDisplay<T>)_uadRegistry.getUADDisplayByObject(unwrappedObject);

		return uadDisplay.getPrimaryKey(unwrappedObject);
	}

	public String[] getSortingFieldNames() {
		return getColumnFieldNames();
	}

	public <T> String getTypeKey(T object) {
		UADDisplay<?> uadDisplay = _uadRegistry.getUADDisplayByObject(
			unwrap(object));

		return uadDisplay.getTypeKey();
	}

	public String[] getTypeKeys() {
		return _typeKeys;
	}

	public UADDisplay<?>[] getUADDisplays() {
		return _uadDisplays;
	}

	public <T> String getViewURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String applicationKey, T object, long selectedUserId)
		throws Exception {

		T unwrappedObject = unwrap(object);

		UADDisplay<T> uadDisplay =
			(UADDisplay<T>)_uadRegistry.getUADDisplayByObject(unwrappedObject);

		String typeKey = uadDisplay.getTypeKey();

		if (!ArrayUtil.contains(_containerTypeKeys, typeKey)) {
			return null;
		}

		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/user_associated_data/view_uad_hierarchy"
		).setParameter(
			"applicationKey", applicationKey
		).setParameter(
			"p_u_i_d", selectedUserId
		).setParameter(
			"parentContainerId", uadDisplay.getPrimaryKey(unwrappedObject)
		).setParameter(
			"parentContainerTypeKey", typeKey
		).setParameter(
			"scope", ParamUtil.getString(liferayPortletRequest, "scope")
		).buildString();
	}

	public <T> boolean isInTrash(T object)
		throws IllegalAccessException, InvocationTargetException {

		T unwrappedObject = unwrap(object);

		UADDisplay<T> uadDisplay =
			(UADDisplay<T>)_uadRegistry.getUADDisplayByObject(unwrappedObject);

		return uadDisplay.isInTrash(unwrappedObject);
	}

	public <T> boolean isUserOwned(T object, long userId) {
		T unwrappedObject = unwrap(object);

		UADDisplay<T> uadDisplay =
			(UADDisplay<T>)_uadRegistry.getUADDisplayByObject(unwrappedObject);

		return uadDisplay.isUserOwned(unwrappedObject, userId);
	}

	public List<Object> search(
			String parentContainerTypeKey, Serializable parentContainerId,
			long userId, long[] groupIds, String keywords, String orderByField,
			String orderByType, int start, int end)
		throws Exception {

		Objects.requireNonNull(parentContainerTypeKey);
		Objects.requireNonNull(parentContainerId);

		List<Object> searchResults = new ArrayList<>();

		List<Object> allUserItems = new ArrayList<>();

		for (UADDisplay<?> uadDisplay : _uadDisplays) {
			allUserItems.addAll(
				uadDisplay.search(
					userId, groupIds, keywords, orderByField, orderByType,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}

		UADDisplay<?> parentUADDisplay = _uadRegistry.getUADDisplay(
			parentContainerTypeKey);

		for (UADDisplay<?> containerUADDisplay :
				_uadHierarchyDeclaration.getContainerUADDisplays()) {

			searchResults.addAll(
				_getContainerDisplays(
					containerUADDisplay, parentUADDisplay, parentContainerId,
					allUserItems));
		}

		for (UADDisplay<Object> noncontainerUADDisplay :
				(UADDisplay<Object>[])
					_uadHierarchyDeclaration.getNoncontainerUADDisplays()) {

			for (Object userItem : allUserItems) {
				if ((userItem != null) &&
					noncontainerUADDisplay.isTypeEntity(userItem) &&
					parentContainerId.equals(
						noncontainerUADDisplay.getParentContainerId(
							userItem))) {

					searchResults.add(userItem);
				}
			}
		}

		return ListUtil.subList(searchResults, start, end);
	}

	public long searchCount(long userId, long[] groupIds, String keywords) {
		long count = 0;

		for (UADDisplay<?> uadDisplay : _uadDisplays) {
			count += uadDisplay.searchCount(userId, groupIds, keywords);
		}

		return count;
	}

	public <T> T unwrap(Object object) {
		if (object instanceof ContainerDisplay) {
			ContainerDisplay<T> containerDisplay = (ContainerDisplay<T>)object;

			return containerDisplay.getContainer();
		}

		return (T)object;
	}

	private void _addEntities(
		Map<String, List<Serializable>> entitiesMap,
		List<Serializable> entities, String typeKey) {

		if (!entitiesMap.containsKey(typeKey)) {
			entitiesMap.put(typeKey, new ArrayList<>());
		}

		List<Serializable> entitiesList = entitiesMap.get(typeKey);

		entitiesList.addAll(entities);
	}

	private <T> Collection<ContainerDisplay<T>> _getContainerDisplays(
		UADDisplay<T> containerUADDisplay,
		UADDisplay<?> parentContainerUADDisplay, Serializable parentContainerId,
		List<Object> allUserItems) {

		Map<Serializable, ContainerDisplay<T>> topLevelCategories =
			new HashMap<>();

		for (Object userItem : allUserItems) {
			if (userItem == null) {
				continue;
			}

			T topLevelContainer = containerUADDisplay.getTopLevelContainer(
				parentContainerUADDisplay.getTypeClass(), parentContainerId,
				userItem);

			if (topLevelContainer == null) {
				continue;
			}

			Serializable topLevelContainerId =
				containerUADDisplay.getPrimaryKey(topLevelContainer);

			if (containerUADDisplay.isTypeEntity(userItem) &&
				Objects.equals(
					containerUADDisplay.getParentContainerId((T)userItem),
					parentContainerId)) {

				topLevelCategories.putIfAbsent(
					topLevelContainerId,
					new ContainerDisplay<>(topLevelContainer));
			}
			else {
				topLevelCategories.compute(
					topLevelContainerId,
					(key, value) -> {
						if (value == null) {
							value = new ContainerDisplay<>(topLevelContainer);
						}

						value.increment();

						return value;
					});
			}
		}

		return topLevelCategories.values();
	}

	private <T> List<Serializable> _getContainerItemPKs(
		String parentContainerTypeKey, Serializable parentContainerId,
		String typeKey, long userId) {

		List<Serializable> containerItemPKs = new ArrayList<>();

		UADDisplay<Object> uadDisplay =
			(UADDisplay<Object>)_uadRegistry.getUADDisplay(typeKey);
		UADDisplay<Object> parentContainerUADDisplay =
			(UADDisplay<Object>)_uadRegistry.getUADDisplay(
				parentContainerTypeKey);

		List<Object> searchItems = uadDisplay.search(
			userId, null, null, null, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		for (Object searchItem : searchItems) {
			if (parentContainerId.equals(
					uadDisplay.getParentContainerId(searchItem))) {

				containerItemPKs.add(uadDisplay.getPrimaryKey(searchItem));
			}
			else {
				boolean containerItem = false;

				for (UADDisplay<?> containerUADDisplay :
						_containerUADDisplays) {

					Object topLevelContainer =
						containerUADDisplay.getTopLevelContainer(
							parentContainerUADDisplay.getTypeClass(),
							parentContainerId, searchItem);

					if (topLevelContainer != null) {
						containerItem = true;
					}
				}

				if (containerItem) {
					containerItemPKs.add(uadDisplay.getPrimaryKey(searchItem));
				}
			}
		}

		return containerItemPKs;
	}

	private final String[] _containerTypeKeys;
	private final UADDisplay<?>[] _containerUADDisplays;
	private final String[] _typeKeys;
	private final UADDisplay<?>[] _uadDisplays;
	private final UADHierarchyDeclaration _uadHierarchyDeclaration;
	private final UADRegistry _uadRegistry;

}
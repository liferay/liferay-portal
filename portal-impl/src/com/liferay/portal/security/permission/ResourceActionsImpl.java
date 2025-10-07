/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.ResourceActionsException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.DocumentType;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Daeyoung Song
 * @author Raymond Augé
 */
public class ResourceActionsImpl implements ResourceActions {

	@Override
	public void check(String portletName) {
		_check(portletName, getPortletResourceActions(portletName));
	}

	@Override
	public String getAction(
		HttpServletRequest httpServletRequest, String action) {

		String key = _ACTION_NAME_PREFIX + action;

		String value = LanguageUtil.get(httpServletRequest, key, null);

		if ((value == null) || value.equals(key)) {
			value = _getResourceBundlesString(httpServletRequest, key);
		}

		if (value == null) {
			value = key;
		}

		return value;
	}

	@Override
	public String getAction(Locale locale, String action) {
		String key = _ACTION_NAME_PREFIX + action;

		String value = LanguageUtil.get(locale, key, null);

		if ((value == null) || value.equals(key)) {
			value = _getResourceBundlesString(locale, key);
		}

		if (value == null) {
			value = key;
		}

		return value;
	}

	@Override
	public String getCompositeModelName(String... classNames) {
		if (ArrayUtil.isEmpty(classNames)) {
			return StringPool.BLANK;
		}

		Arrays.sort(classNames);

		StringBundler sb = new StringBundler(classNames.length * 2);

		for (String className : classNames) {
			sb.append(className);
			sb.append(getCompositeModelNameSeparator());
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	@Override
	public String getCompositeModelNameSeparator() {
		return _COMPOSITE_MODEL_NAME_SEPARATOR;
	}

	@Override
	public List<String> getModelNames() {
		List<String> modelNames = new ArrayList<>();

		for (String name : _resourceActionsBags.keySet()) {
			if (name.indexOf(CharPool.PERIOD) != -1) {
				modelNames.add(name);
			}
		}

		return modelNames;
	}

	@Override
	public List<String> getModelPortletResources(String name) {
		Set<String> resources = _resourceReferences.get(name);

		if (resources == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(resources);
	}

	@Override
	public String getModelResource(
		HttpServletRequest httpServletRequest, String name) {

		String key = getModelResourceNamePrefix() + name;

		String value = LanguageUtil.get(httpServletRequest, key, null);

		if ((value == null) || value.equals(key)) {
			value = _getResourceBundlesString(httpServletRequest, key);
		}

		if (value == null) {
			value = key;
		}

		return value;
	}

	@Override
	public String getModelResource(Locale locale, String name) {
		String key = getModelResourceNamePrefix() + name;

		String value = LanguageUtil.get(locale, key, null);

		if (value == null) {
			value = _getResourceBundlesString(locale, key);
		}

		if (value == null) {
			value = key;
		}

		return value;
	}

	@Override
	public List<String> getModelResourceActions(String name) {
		ResourceActionsBag modelResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(modelResourceActionsBag.getSupportsActions());
	}

	@Override
	public List<String> getModelResourceGroupDefaultActions(String name) {
		ResourceActionsBag modelResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			modelResourceActionsBag.getGroupDefaultActions());
	}

	@Override
	public List<String> getModelResourceGuestDefaultActions(String name) {
		ResourceActionsBag modelResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			modelResourceActionsBag.getGuestDefaultActions());
	}

	@Override
	public List<String> getModelResourceGuestUnsupportedActions(String name) {
		ResourceActionsBag modelResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			modelResourceActionsBag.getGuestUnsupportedActions());
	}

	@Override
	public String getModelResourceNamePrefix() {
		return _MODEL_RESOURCE_NAME_PREFIX;
	}

	@Override
	public List<String> getModelResourceOwnerDefaultActions(String name) {
		ResourceActionsBag modelResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			modelResourceActionsBag.getOwnerDefaultActions());
	}

	@Override
	public Double getModelResourceWeight(String name) {
		return _modelResourceWeights.get(name);
	}

	@Override
	public List<String> getPortletModelResources(String portletName) {
		portletName = PortletIdCodec.decodePortletName(portletName);

		Set<String> resources = _resourceReferences.get(portletName);

		if (resources == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(resources);
	}

	@Override
	public List<String> getPortletNames() {
		List<String> portletNames = new ArrayList<>();

		for (String name : _resourceActionsBags.keySet()) {
			if (name.indexOf(CharPool.PERIOD) == -1) {
				portletNames.add(name);
			}
		}

		return portletNames;
	}

	@Override
	public List<String> getPortletResourceActions(String name) {
		name = PortletIdCodec.decodePortletName(name);

		Portlet portlet = portletLocalService.getPortletById(name);

		return _getPortletResourceActions(name, portlet);
	}

	@Override
	public List<String> getPortletResourceGroupDefaultActions(String name) {

		// This method should always be called only after
		// _getPortletResourceActions has been called at least once to populate
		// the default group actions. Check to make sure this is the case.
		// However, if it is not, that means the methods
		// getPortletResourceGuestDefaultActions and
		// getPortletResourceGuestDefaultActions may not work either.

		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			portletResourceActionsBag.getGroupDefaultActions());
	}

	@Override
	public List<String> getPortletResourceGuestDefaultActions(String name) {
		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			portletResourceActionsBag.getGuestDefaultActions());
	}

	@Override
	public List<String> getPortletResourceGuestUnsupportedActions(String name) {
		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			portletResourceActionsBag.getGuestUnsupportedActions());
	}

	@Override
	public List<String> getPortletResourceLayoutManagerActions(String name) {
		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			portletResourceActionsBag.getLayoutManagerActions());
	}

	@Override
	public List<String> getPortletResourceOwnerDefaultActions(String name) {
		name = PortletIdCodec.decodePortletName(name);

		ResourceActionsBag portletResourceActionsBag = _getResourceActionsBag(
			name, false);

		return new ArrayList<>(
			portletResourceActionsBag.getOwnerDefaultActions());
	}

	@Override
	public String getPortletRootModelResource(String portletName) {
		return _portletRootModelResources.get(
			PortletIdCodec.decodePortletName(portletName));
	}

	@Override
	public List<String> getResourceActions(String name) {
		if (name.indexOf(CharPool.PERIOD) != -1) {
			return getModelResourceActions(name);
		}

		return getPortletResourceActions(name);
	}

	@Override
	public List<String> getResourceActions(
		String portletResource, String modelResource) {

		List<String> actions = null;

		if (Validator.isNull(modelResource)) {
			actions = getPortletResourceActions(portletResource);
		}
		else {
			actions = getModelResourceActions(modelResource);
		}

		return actions;
	}

	@Override
	public List<String> getResourceGuestUnsupportedActions(
		String portletResource, String modelResource) {

		if (Validator.isNull(modelResource)) {
			return getPortletResourceGuestUnsupportedActions(portletResource);
		}
		else if (Validator.isNull(portletResource)) {
			return getModelResourceGuestUnsupportedActions(modelResource);
		}
		else if (_resourceActionsBags.containsKey(modelResource)) {
			return getModelResourceGuestUnsupportedActions(modelResource);
		}
		else if (_resourceActionsBags.containsKey(portletResource)) {
			return getPortletResourceGuestUnsupportedActions(portletResource);
		}

		return Collections.emptyList();
	}

	@Override
	public List<Role> getRoles(
		long companyId, Group group, String modelResource, int[] roleTypes) {

		if (roleTypes == null) {
			roleTypes = _getRoleTypes(group, modelResource);
		}

		return roleLocalService.getRoles(companyId, roleTypes);
	}

	@Override
	public boolean isPortalModelResource(String modelResource) {
		return _portalModelResources.contains(modelResource);
	}

	@Override
	public boolean isRootModelResource(String modelResource) {
		Collection<String> rootModelResources =
			_portletRootModelResources.values();

		return rootModelResources.contains(modelResource);
	}

	@Override
	public void populateModelResources(
			ClassLoader classLoader, String... sources)
		throws ResourceActionsException {

		populateModelResources(classLoader, sources, true);
	}

	@Override
	public void populateModelResources(
			ClassLoader classLoader, String[] sources,
			boolean checkResourceActions)
		throws ResourceActionsException {

		if (ArrayUtil.isEmpty(sources)) {
			return;
		}

		Set<String> modelResourceNames = new HashSet<>();

		for (String source : sources) {
			_read(
				classLoader, source,
				rootElement -> _readModelResources(
					rootElement, modelResourceNames));
		}

		if (checkResourceActions) {
			for (String modelResourceName : modelResourceNames) {
				_checkResourceActions(
					getModelResourceActions(modelResourceName),
					modelResourceName);
			}
		}
	}

	@Override
	public void populateModelResources(Document document)
		throws ResourceActionsException {

		DocumentType documentType = document.getDocumentType();

		String publicId = GetterUtil.getString(documentType.getPublicId());

		if (publicId.equals(
				"-//Liferay//DTD Resource Action Mapping 6.0.0//EN")) {

			if (_log.isWarnEnabled()) {
				_log.warn("Please update document to use the 6.1.0 format");
			}
		}

		Set<String> modelResourceNames = new HashSet<>();

		_readModelResources(document.getRootElement(), modelResourceNames);

		for (String modelResourceName : modelResourceNames) {
			_checkResourceActions(
				getModelResourceActions(modelResourceName), modelResourceName);
		}
	}

	@Override
	public void populatePortletResource(
			Portlet portlet, ClassLoader classLoader, Document document)
		throws ResourceActionsException {

		if (portlet == null) {
			throw new IllegalArgumentException("Portlet must not be null");
		}

		_readPortletResource(document.getRootElement(), portlet);

		String portletResourceName = PortletIdCodec.decodePortletName(
			portlet.getPortletId());

		_checkResourceActions(
			_getPortletResourceActions(portletResourceName, portlet),
			portletResourceName);
	}

	@Override
	public void populatePortletResource(
			Portlet portlet, ClassLoader classLoader, String... sources)
		throws ResourceActionsException {

		if (portlet == null) {
			throw new IllegalArgumentException("Portlet must not be null");
		}

		if (ArrayUtil.isNotEmpty(sources) &&
			PropsValues.RESOURCE_ACTIONS_READ_PORTLET_RESOURCES) {

			for (String source : sources) {
				_read(
					classLoader, source,
					rootElement -> _readPortletResource(rootElement, portlet));
			}
		}

		String portletResourceName = PortletIdCodec.decodePortletName(
			portlet.getPortletId());

		_checkResourceActions(
			_getPortletResourceActions(portletResourceName, portlet),
			portletResourceName);
	}

	@Override
	public void populatePortletResources(
			ClassLoader classLoader, String... sources)
		throws ResourceActionsException {

		populatePortletResources(classLoader, sources, true);
	}

	@Override
	public void populatePortletResources(
			ClassLoader classLoader, String[] sources,
			boolean checkResourceActions)
		throws ResourceActionsException {

		if (ArrayUtil.isEmpty(sources) ||
			!PropsValues.RESOURCE_ACTIONS_READ_PORTLET_RESOURCES) {

			return;
		}

		Set<String> portletResourceNames = new HashSet<>();

		for (String source : sources) {
			_read(
				classLoader, source,
				rootElement -> _readPortletResources(
					rootElement, portletResourceNames));
		}

		if (checkResourceActions) {
			for (String portletResourceName : portletResourceNames) {
				_checkResourceActions(
					getPortletResourceActions(portletResourceName),
					portletResourceName);
			}
		}
	}

	public void readModelResources(ClassLoader classLoader, String source)
		throws ResourceActionsException {

		_read(
			classLoader, source,
			rootElement -> _readModelResources(rootElement, null));
	}

	@Override
	public void removeModelResource(String name, String action) {
		if (Validator.isNull(name) || Validator.isNull(action)) {
			return;
		}

		ResourceActionsBag resourceActionsBag = _getResourceActionsBag(
			name, false);

		Set<String> resourceActions =
			resourceActionsBag.getOwnerDefaultActions();

		resourceActions.remove(action);

		resourceActions = resourceActionsBag.getSupportsActions();

		resourceActions.remove(action);

		ResourceAction resourceAction =
			resourceActionLocalService.fetchResourceAction(name, action);

		if (resourceAction != null) {
			resourceActionLocalService.deleteResourceAction(resourceAction);
		}
	}

	@Override
	public void removeModelResources(Document document) {
		Element rootElement = document.getRootElement();

		for (Element modelResourceElement :
				rootElement.elements("model-resource")) {

			String modelName = modelResourceElement.elementTextTrim(
				"model-name");

			if (Validator.isNull(modelName)) {
				modelName = _getCompositeModelName(
					modelResourceElement.element("composite-model-name"));
			}

			Element portletRefElement = modelResourceElement.element(
				"portlet-ref");

			for (Element portletNameElement :
					portletRefElement.elements("portlet-name")) {

				String portletName = portletNameElement.getTextTrim();

				String portletRootModelResourceName =
					_portletRootModelResources.get(portletName);

				if (Objects.equals(portletRootModelResourceName, modelName)) {
					_portletRootModelResources.remove(portletName);
				}

				Set<String> portletResourceNames = _resourceReferences.get(
					modelName);

				if (portletResourceNames != null) {
					portletResourceNames.remove(portletName);
				}

				Set<String> modelResourceNames = _resourceReferences.get(
					portletName);

				if (modelResourceNames == null) {
					continue;
				}

				modelResourceNames.remove(modelName);

				if (modelResourceNames.isEmpty()) {
					_resourceReferences.remove(portletName);
				}
			}

			if (SetUtil.isNotEmpty(_resourceReferences.get(modelName))) {
				continue;
			}

			for (ResourceAction resourceAction :
					resourceActionLocalService.getResourceActions(modelName)) {

				resourceActionLocalService.deleteResourceAction(resourceAction);
			}

			synchronized (_resourceActionsBags) {
				_resourceActionsBags.remove(modelName);
			}

			_resourceReferences.remove(modelName);

			_organizationModelResources.remove(modelName);

			_modelResourceWeights.remove(modelName);

			_portalModelResources.remove(modelName);
		}
	}

	@Override
	public void removePortletResources(Document document) {
		Element rootElement = document.getRootElement();

		for (Element portletResourceElement :
				rootElement.elements("portlet-resource")) {

			String portletName = portletResourceElement.elementTextTrim(
				"portlet-name");

			List<ResourceAction> resourceActions =
				resourceActionLocalService.getResourceActions(portletName);

			for (ResourceAction resourceAction : resourceActions) {
				resourceActionLocalService.deleteResourceAction(resourceAction);
			}

			synchronized (_resourceActionsBags) {
				_resourceActionsBags.remove(portletName);
			}
		}
	}

	@BeanReference(type = PortletLocalService.class)
	protected PortletLocalService portletLocalService;

	@BeanReference(type = ResourceActionLocalService.class)
	protected ResourceActionLocalService resourceActionLocalService;

	@BeanReference(type = RoleLocalService.class)
	protected RoleLocalService roleLocalService;

	private void _check(
		String portletName, List<String> portletResourceActions) {

		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					ResourceActionLocalServiceUtil.checkResourceActions(
						portletName, portletResourceActions);

					for (String modelName :
							getPortletModelResources(portletName)) {

						ResourceActionLocalServiceUtil.checkResourceActions(
							modelName, getModelResourceActions(modelName));
					}
				});
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private void _checkGuestUnsupportedActions(
		Set<String> guestUnsupportedActions, Set<String> guestDefaultActions) {

		// Guest default actions cannot reference guest unsupported actions

		Iterator<String> iterator = guestDefaultActions.iterator();

		while (iterator.hasNext()) {
			String actionId = iterator.next();

			if (guestUnsupportedActions.contains(actionId)) {
				iterator.remove();
			}
		}
	}

	private void _checkPortletGuestUnsupportedActions(Set<String> actions) {
		actions.add(ActionKeys.CONFIGURATION);
		actions.add(ActionKeys.PERMISSIONS);
	}

	private void _checkPortletLayoutManagerActions(Set<String> actions) {
		if (!actions.contains(ActionKeys.ACCESS_IN_CONTROL_PANEL)) {
			actions.add(ActionKeys.ADD_TO_PAGE);
		}

		actions.add(ActionKeys.CONFIGURATION);
		actions.add(ActionKeys.PERMISSIONS);
		actions.add(ActionKeys.PREFERENCES);
		actions.add(ActionKeys.VIEW);
	}

	private void _checkResourceActions(List<String> actionIds, String name) {
		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> resourceActionLocalService.checkResourceActions(
					name, actionIds));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getCompositeModelName(Element compositeModelNameElement) {
		StringBundler sb = new StringBundler();

		List<Element> elements = new ArrayList<>(
			compositeModelNameElement.elements("model-name"));

		Collections.sort(
			elements,
			new Comparator<Element>() {

				@Override
				public int compare(Element element1, Element element2) {
					String textTrim1 = GetterUtil.getString(
						element1.getTextTrim());
					String textTrim2 = GetterUtil.getString(
						element2.getTextTrim());

					return textTrim1.compareTo(textTrim2);
				}

			});

		Iterator<Element> iterator = elements.iterator();

		while (iterator.hasNext()) {
			Element modelNameElement = iterator.next();

			sb.append(modelNameElement.getTextTrim());

			if (iterator.hasNext()) {
				sb.append(_COMPOSITE_MODEL_NAME_SEPARATOR);
			}
		}

		return sb.toString();
	}

	private Element _getPermissionsChildElement(
		Element parentElement, String childElementName) {

		Element permissionsElement = parentElement.element("permissions");

		if (permissionsElement != null) {
			return permissionsElement.element(childElementName);
		}

		return parentElement.element(childElementName);
	}

	private Set<String> _getPortletMimeTypeActions(
		String name, Portlet portlet) {

		Set<String> actions = new LinkedHashSet<>();

		if (portlet != null) {
			Map<String, Set<String>> portletModes = portlet.getPortletModes();

			Set<String> mimeTypePortletModes = portletModes.get(
				ContentTypes.TEXT_HTML);

			if (mimeTypePortletModes != null) {
				for (String actionId : mimeTypePortletModes) {
					if (StringUtil.equalsIgnoreCase(actionId, "edit")) {
						actions.add(ActionKeys.PREFERENCES);
					}
					else if (StringUtil.equalsIgnoreCase(
								actionId, "edit_guest")) {

						actions.add(ActionKeys.GUEST_PREFERENCES);
					}
					else {
						actions.add(StringUtil.toUpperCase(actionId));
					}
				}
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to obtain resource actions for unknown portlet " +
						name);
			}
		}

		return actions;
	}

	private List<String> _getPortletResourceActions(
		String name, Portlet portlet) {

		ResourceActionsBag portletResourceActionsBag = _getResourceActionsBag(
			name, true);

		Set<String> portletActions =
			portletResourceActionsBag.getSupportsActions();

		if (!portletActions.isEmpty()) {
			return new ArrayList<>(portletActions);
		}

		synchronized (this) {
			portletActions = _getPortletMimeTypeActions(name, portlet);

			if (!name.equals(PortletKeys.PORTAL)) {
				_checkPortletLayoutManagerActions(portletActions);

				portletActions.add(ActionKeys.ACCESS_IN_CONTROL_PANEL);
			}

			Set<String> groupDefaultActions =
				portletResourceActionsBag.getGroupDefaultActions();

			groupDefaultActions.add(ActionKeys.VIEW);

			Set<String> guestDefaultActions =
				portletResourceActionsBag.getGuestDefaultActions();

			guestDefaultActions.add(ActionKeys.VIEW);

			_checkPortletGuestUnsupportedActions(
				portletResourceActionsBag.getGuestUnsupportedActions());

			_checkPortletLayoutManagerActions(
				portletResourceActionsBag.getLayoutManagerActions());
		}

		return new ArrayList<>(portletActions);
	}

	private ResourceActionsBag _getResourceActionsBag(
		String name, boolean create) {

		ResourceActionsBag resourceActionsBag = _resourceActionsBags.get(name);

		if (resourceActionsBag != null) {
			return resourceActionsBag;
		}

		if (!create) {
			return _dummyResourceActionsBag;
		}

		synchronized (_resourceActionsBags) {
			resourceActionsBag = _resourceActionsBags.get(name);

			if (resourceActionsBag != null) {
				return resourceActionsBag;
			}

			resourceActionsBag = new ResourceActionsBag();

			_resourceActionsBags.put(name, resourceActionsBag);
		}

		return resourceActionsBag;
	}

	private String _getResourceBundlesString(
		HttpServletRequest httpServletRequest, String key) {

		Locale locale = null;

		HttpSession httpSession = httpServletRequest.getSession(false);

		if (httpSession != null) {
			locale = (Locale)httpSession.getAttribute(WebKeys.LOCALE);
		}

		if (locale == null) {
			locale = httpServletRequest.getLocale();
		}

		return _getResourceBundlesString(locale, key);
	}

	private String _getResourceBundlesString(Locale locale, String key) {
		if ((locale == null) || (key == null)) {
			return null;
		}

		for (ResourceBundleLoader resourceBundleLoader :
				ResourceBundleLoaderListHolder._resourceBundleLoaders) {

			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(locale);

			if (resourceBundle == null) {
				continue;
			}

			if (resourceBundle.containsKey(key)) {
				return ResourceBundleUtil.getString(resourceBundle, key);
			}
		}

		return null;
	}

	private int[] _getRoleTypes(Group group, String modelResource) {
		int[] types = RoleConstants.TYPES_REGULAR_AND_SITE;

		if (isPortalModelResource(modelResource)) {
			if (modelResource.equals(Organization.class.getName()) ||
				modelResource.equals(User.class.getName())) {

				types = RoleConstants.TYPES_ORGANIZATION_AND_REGULAR;
			}
			else {
				types = RoleConstants.TYPES_REGULAR;
			}
		}
		else {
			if (group != null) {
				if (group.isLayout()) {
					try {
						group = GroupServiceUtil.getGroup(
							group.getParentGroupId());
					}
					catch (Exception exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}
					}
				}

				if (group.isOrganization()) {
					types =
						RoleConstants.TYPES_ORGANIZATION_AND_REGULAR_AND_SITE;
				}
				else if (group.isCompany() || group.isUser() ||
						 group.isUserGroup()) {

					types = RoleConstants.TYPES_REGULAR;
				}
			}
		}

		return types;
	}

	private void _read(
			ClassLoader classLoader, String source,
			UnsafeConsumer<Element, ResourceActionsException>
				readResourceConsumer)
		throws ResourceActionsException {

		InputStream inputStream = classLoader.getResourceAsStream(source);

		if (inputStream == null) {
			if (_log.isInfoEnabled() && !source.endsWith("-ext.xml") &&
				!source.startsWith("META-INF/")) {

				_log.info("Cannot load " + source);
			}

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Loading " + source);
		}

		try {
			Document document = UnsecureSAXReaderUtil.read(inputStream, true);

			DocumentType documentType = document.getDocumentType();

			String publicId = GetterUtil.getString(documentType.getPublicId());

			if (publicId.equals(
					"-//Liferay//DTD Resource Action Mapping 6.0.0//EN")) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Please update " + source + " to use the 6.1.0 format");
				}
			}

			Element rootElement = document.getRootElement();

			for (Element resourceElement : rootElement.elements("resource")) {
				String file = StringUtil.trim(
					resourceElement.attributeValue("file"));

				_read(classLoader, file, readResourceConsumer);

				String extFileName = StringUtil.replace(
					file, ".xml", "-ext.xml");

				_read(classLoader, extFileName, readResourceConsumer);
			}

			readResourceConsumer.accept(rootElement);

			if (source.endsWith(".xml") && !source.endsWith("-ext.xml")) {
				String extFileName = StringUtil.replace(
					source, ".xml", "-ext.xml");

				_read(classLoader, extFileName, readResourceConsumer);
			}
		}
		catch (DocumentException documentException) {
			throw new ResourceActionsException(documentException);
		}
	}

	private void _readActionKeys(
		Collection<String> actions, Element parentElement) {

		for (Element actionKeyElement : parentElement.elements("action-key")) {
			String actionKey = actionKeyElement.getTextTrim();

			if (Validator.isNull(actionKey)) {
				continue;
			}

			actions.add(actionKey);
		}
	}

	private void _readModelResources(
			Element rootElement, Set<String> resourceNames)
		throws ResourceActionsException {

		for (Element modelResourceElement :
				rootElement.elements("model-resource")) {

			String modelName = modelResourceElement.elementTextTrim(
				"model-name");

			if (Validator.isNull(modelName)) {
				modelName = _getCompositeModelName(
					modelResourceElement.element("composite-model-name"));
			}

			if (GetterUtil.getBoolean(
					modelResourceElement.attributeValue("organization"))) {

				_organizationModelResources.add(modelName);
			}

			if (GetterUtil.getBoolean(
					modelResourceElement.attributeValue("portal"))) {

				_portalModelResources.add(modelName);
			}

			Element portletRefElement = modelResourceElement.element(
				"portlet-ref");

			for (Element portletNameElement :
					portletRefElement.elements("portlet-name")) {

				String portletName = portletNameElement.getTextTrim();

				// Reference for a portlet to child models

				Set<String> modelResources =
					_resourceReferences.computeIfAbsent(
						portletName, key -> ConcurrentHashMap.newKeySet());

				modelResources.add(modelName);

				// Reference for a model to parent portlets

				Set<String> portletResources =
					_resourceReferences.computeIfAbsent(
						modelName, key -> ConcurrentHashMap.newKeySet());

				portletResources.add(portletName);

				// Reference for a model to root portlets

				boolean root = GetterUtil.getBoolean(
					modelResourceElement.elementText("root"));

				if (root) {
					String existingModelName =
						_portletRootModelResources.putIfAbsent(
							portletName, modelName);

					if (Validator.isNotNull(existingModelName) &&
						!Objects.equals(existingModelName, modelName)) {

						throw new ResourceActionsException(
							StringBundler.concat(
								"Portlet ", portletName,
								" cannot be assigned to both ",
								existingModelName, " and ", modelName,
								" as root model resources. See LPS-135983."));
					}
				}
			}

			double weight = GetterUtil.getDouble(
				modelResourceElement.elementTextTrim("weight"), 100);

			_modelResourceWeights.put(modelName, weight);

			_readResource(
				modelResourceElement, modelName,
				Collections.singleton(ActionKeys.PERMISSIONS));

			if (resourceNames != null) {
				resourceNames.add(modelName);
			}
		}
	}

	private void _readPortletResource(Element rootElement, Portlet portlet)
		throws ResourceActionsException {

		String deployPortletName = PortletIdCodec.decodePortletName(
			portlet.getPortletId());

		for (Element portletResourceElement :
				rootElement.elements("portlet-resource")) {

			String portletName = portletResourceElement.elementTextTrim(
				"portlet-name");

			if (!portletName.equals(deployPortletName)) {
				continue;
			}

			Set<String> portletActions = _getPortletMimeTypeActions(
				portletName, portlet);

			if (!portletName.equals(PortletKeys.PORTAL)) {
				_checkPortletLayoutManagerActions(portletActions);
			}

			_readResource(portletResourceElement, portletName, portletActions);
		}
	}

	private void _readPortletResources(
			Element rootElement, Set<String> resourceNames)
		throws ResourceActionsException {

		if (PropsValues.RESOURCE_ACTIONS_READ_PORTLET_RESOURCES) {
			for (Element portletResourceElement :
					rootElement.elements("portlet-resource")) {

				String portletName = portletResourceElement.elementTextTrim(
					"portlet-name");

				Portlet portlet = portletLocalService.getPortletById(
					portletName);

				Set<String> portletActions = _getPortletMimeTypeActions(
					portletName, portlet);

				if (!portletName.equals(PortletKeys.PORTAL)) {
					_checkPortletLayoutManagerActions(portletActions);
				}

				_readResource(
					portletResourceElement, portletName, portletActions);

				if (resourceNames != null) {
					resourceNames.add(portletName);
				}
			}
		}
	}

	private void _readResource(
			Element resourceElement, String name,
			Set<String> defaultResourceActions)
		throws ResourceActionsException {

		ResourceActionsBag resourceActionsBag = _getResourceActionsBag(
			name, true);

		Set<String> resourceActions = resourceActionsBag.getSupportsActions();

		Element supportsElement = _getPermissionsChildElement(
			resourceElement, "supports");

		_readActionKeys(resourceActions, supportsElement);

		resourceActions.addAll(defaultResourceActions);

		if (resourceActions.size() > 64) {
			throw new ResourceActionsException(
				"There are more than 64 actions for resource " + name);
		}

		Element groupDefaultsElement = _getPermissionsChildElement(
			resourceElement, "site-member-defaults");

		if (groupDefaultsElement == null) {
			groupDefaultsElement = _getPermissionsChildElement(
				resourceElement, "community-defaults");

			if (_log.isWarnEnabled() && (groupDefaultsElement != null)) {
				_log.warn(
					"The community-defaults element is deprecated. Use the " +
						"site-member-defaults element instead.");
			}
		}

		if (groupDefaultsElement != null) {
			Set<String> groupDefaultActions =
				resourceActionsBag.getGroupDefaultActions();

			groupDefaultActions.clear();

			_readActionKeys(groupDefaultActions, groupDefaultsElement);
		}

		Set<String> guestDefaultActions =
			resourceActionsBag.getGuestDefaultActions();

		Element guestDefaultsElement = _getPermissionsChildElement(
			resourceElement, "guest-defaults");

		if (guestDefaultsElement != null) {
			guestDefaultActions.clear();

			_readActionKeys(guestDefaultActions, guestDefaultsElement);
		}

		Element guestUnsupportedElement = _getPermissionsChildElement(
			resourceElement, "guest-unsupported");

		if (guestUnsupportedElement != null) {
			Set<String> guestUnsupportedActions =
				resourceActionsBag.getGuestUnsupportedActions();

			guestUnsupportedActions.clear();

			_readActionKeys(guestUnsupportedActions, guestUnsupportedElement);

			String resourceElementName = resourceElement.getName();

			if (Objects.equals(resourceElementName, "portlet-resource")) {
				_checkPortletGuestUnsupportedActions(guestUnsupportedActions);
			}

			_checkGuestUnsupportedActions(
				guestUnsupportedActions, guestDefaultActions);
		}

		Set<String> ownerDefaultActions =
			resourceActionsBag.getOwnerDefaultActions();

		Element ownerDefaultsElement = _getPermissionsChildElement(
			resourceElement, "owner-defaults");

		if (ownerDefaultsElement != null) {
			ownerDefaultActions.clear();

			_readActionKeys(ownerDefaultActions, ownerDefaultsElement);
		}
		else {
			ownerDefaultActions.addAll(resourceActions);
		}

		Set<String> layoutManagerActions =
			resourceActionsBag.getLayoutManagerActions();

		Element layoutManagerElement = _getPermissionsChildElement(
			resourceElement, "layout-manager");

		if (layoutManagerElement == null) {
			layoutManagerActions.addAll(resourceActions);

			return;
		}

		layoutManagerActions.clear();

		_readActionKeys(layoutManagerActions, layoutManagerElement);
	}

	private static final String _ACTION_NAME_PREFIX = "action.";

	private static final String _COMPOSITE_MODEL_NAME_SEPARATOR =
		StringPool.DASH;

	private static final String _MODEL_RESOURCE_NAME_PREFIX = "model.resource.";

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceActionsImpl.class);

	private static final ResourceActionsBag _dummyResourceActionsBag =
		new ResourceActionsBag();

	private final Map<String, Double> _modelResourceWeights =
		new ConcurrentHashMap<>();
	private final Set<String> _organizationModelResources =
		ConcurrentHashMap.newKeySet();
	private final Set<String> _portalModelResources =
		ConcurrentHashMap.newKeySet();
	private final Map<String, String> _portletRootModelResources =
		new ConcurrentHashMap<>();
	private final Map<String, ResourceActionsBag> _resourceActionsBags =
		new ConcurrentHashMap<>();
	private final Map<String, Set<String>> _resourceReferences =
		new ConcurrentHashMap<>();

	private static class ResourceActionsBag {

		public Set<String> getGroupDefaultActions() {
			return _groupDefaultActions;
		}

		public Set<String> getGuestDefaultActions() {
			return _guestDefaultActions;
		}

		public Set<String> getGuestUnsupportedActions() {
			return _guestUnsupportedActions;
		}

		public Set<String> getLayoutManagerActions() {
			return _layoutManagerActions;
		}

		public Set<String> getOwnerDefaultActions() {
			return _ownerDefaultActions;
		}

		public Set<String> getSupportsActions() {
			return _supportsActions;
		}

		private final Set<String> _groupDefaultActions =
			ConcurrentHashMap.newKeySet();
		private final Set<String> _guestDefaultActions =
			ConcurrentHashMap.newKeySet();
		private final Set<String> _guestUnsupportedActions =
			ConcurrentHashMap.newKeySet();
		private final Set<String> _layoutManagerActions =
			ConcurrentHashMap.newKeySet();
		private final Set<String> _ownerDefaultActions =
			ConcurrentHashMap.newKeySet();
		private final Set<String> _supportsActions =
			ConcurrentHashMap.newKeySet();

	}

	private static class ResourceBundleLoaderListHolder {

		private static final ServiceTrackerList<ResourceBundleLoader>
			_resourceBundleLoaders = ServiceTrackerListFactory.open(
				SystemBundleUtil.getBundleContext(),
				ResourceBundleLoader.class);

	}

}
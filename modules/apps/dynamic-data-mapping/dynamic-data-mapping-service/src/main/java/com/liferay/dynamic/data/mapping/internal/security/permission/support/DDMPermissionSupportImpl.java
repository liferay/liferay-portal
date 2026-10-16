/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.security.permission.support;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.util.DDMStructurePermissionSupport;
import com.liferay.dynamic.data.mapping.util.DDMTemplatePermissionSupport;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = DDMPermissionSupport.class)
public class DDMPermissionSupportImpl implements DDMPermissionSupport {

	@Override
	public void checkAddStructurePermission(
			PermissionChecker permissionChecker, long groupId, long classNameId)
		throws PortalException {

		if (!containsAddStructurePermission(
				permissionChecker, groupId, classNameId)) {

			ServiceWrapper<DDMStructurePermissionSupport>
				structurePermissionSupportServiceWrapper =
					_ddmStructurePermissionSupportServiceTrackerMap.getService(
						_portal.getClassName(classNameId));

			throw new PrincipalException.MustHavePermission(
				permissionChecker,
				getResourceName(structurePermissionSupportServiceWrapper),
				groupId,
				_getAddStructureActionId(
					structurePermissionSupportServiceWrapper));
		}
	}

	@Override
	public void checkAddTemplatePermission(
			PermissionChecker permissionChecker, long groupId, long classNameId,
			long resourceClassNameId)
		throws PortalException {

		ServiceWrapper<DDMTemplatePermissionSupport>
			templatePermissionSupportServiceWrapper =
				_getDDMTemplatePermissionSupportServiceWrapper(
					_portal.getClassName(resourceClassNameId));

		_checkAddTemplatePermission(
			permissionChecker, groupId, classNameId,
			templatePermissionSupportServiceWrapper);
	}

	@Override
	public void checkAddTemplatePermission(
			PermissionChecker permissionChecker, long groupId, long classNameId,
			String resourceClassName)
		throws PortalException {

		ServiceWrapper<DDMTemplatePermissionSupport>
			templatePermissionSupportServiceWrapper =
				_getDDMTemplatePermissionSupportServiceWrapper(
					resourceClassName);

		_checkAddTemplatePermission(
			permissionChecker, groupId, classNameId,
			templatePermissionSupportServiceWrapper);
	}

	@Override
	public boolean containsAddStructurePermission(
			PermissionChecker permissionChecker, long groupId, long classNameId)
		throws PortalException {

		ServiceWrapper<DDMStructurePermissionSupport>
			structurePermissionSupportServiceWrapper =
				_ddmStructurePermissionSupportServiceTrackerMap.getService(
					_portal.getClassName(classNameId));

		return _contains(
			permissionChecker,
			getResourceName(structurePermissionSupportServiceWrapper), groupId,
			_getAddStructureActionId(structurePermissionSupportServiceWrapper));
	}

	@Override
	public boolean containsAddTemplatePermission(
			PermissionChecker permissionChecker, long groupId, long classNameId,
			long resourceClassNameId)
		throws PortalException {

		ServiceWrapper<DDMTemplatePermissionSupport>
			templatePermissionSupportServiceWrapper =
				_getDDMTemplatePermissionSupportServiceWrapper(
					_portal.getClassName(resourceClassNameId));

		return _containsAddTemplatePermission(
			permissionChecker, groupId, classNameId,
			templatePermissionSupportServiceWrapper);
	}

	@Override
	public boolean containsAddTemplatePermission(
			PermissionChecker permissionChecker, long groupId, long classNameId,
			String resourceClassName)
		throws PortalException {

		ServiceWrapper<DDMTemplatePermissionSupport>
			templatePermissionSupportServiceWrapper =
				_getDDMTemplatePermissionSupportServiceWrapper(
					resourceClassName);

		return _containsAddTemplatePermission(
			permissionChecker, groupId, classNameId,
			templatePermissionSupportServiceWrapper);
	}

	@Override
	public String getStructureModelResourceName(long classNameId)
		throws PortalException {

		return getStructureModelResourceName(_portal.getClassName(classNameId));
	}

	@Override
	public String getStructureModelResourceName(String classNameString)
		throws PortalException {

		ClassName className = _classNameLocalService.fetchClassName(
			classNameString);

		if (className.getClassNameId() == 0) {
			throw new PortalException(
				StringBundler.concat(
					"Class name ", classNameString, " is not valid"));
		}

		ServiceWrapper<DDMStructurePermissionSupport>
			structurePermissionSupportServiceWrapper =
				_ddmStructurePermissionSupportServiceTrackerMap.getService(
					classNameString);

		if (structurePermissionSupportServiceWrapper == null) {
			return ResourceActionsUtil.getCompositeModelName(
				classNameString, DDMStructure.class.getName());
		}

		boolean defaultModelResourceName = MapUtil.getBoolean(
			structurePermissionSupportServiceWrapper.getProperties(),
			"default.model.resource.name");

		if (defaultModelResourceName) {
			return DDMStructure.class.getName();
		}

		return ResourceActionsUtil.getCompositeModelName(
			classNameString, DDMStructure.class.getName());
	}

	@Override
	public String getTemplateModelResourceName(long resourceClassNameId)
		throws PortalException {

		String resourceClassName = _portal.getClassName(resourceClassNameId);

		return getTemplateModelResourceName(resourceClassName);
	}

	@Override
	public String getTemplateModelResourceName(String resourceClassName)
		throws PortalException {

		ServiceWrapper<DDMTemplatePermissionSupport>
			templatePermissionSupportServiceWrapper =
				_getDDMTemplatePermissionSupportServiceWrapper(
					resourceClassName);

		return _getTemplateModelResourceName(
			resourceClassName, templatePermissionSupportServiceWrapper);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_ddmStructurePermissionSupportServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DDMStructurePermissionSupport.class,
				"model.class.name",
				ServiceTrackerCustomizerFactory.
					<DDMStructurePermissionSupport>serviceWrapper(
						bundleContext));

		_ddmTemplatePermissionSupportServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DDMTemplatePermissionSupport.class,
				"model.class.name",
				ServiceTrackerCustomizerFactory.
					<DDMTemplatePermissionSupport>serviceWrapper(
						bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_ddmStructurePermissionSupportServiceTrackerMap.close();

		_ddmTemplatePermissionSupportServiceTrackerMap.close();
	}

	protected String getResourceName(
		ServiceWrapper<DDMStructurePermissionSupport>
			structurePermissionSupportServiceWrapper) {

		DDMStructurePermissionSupport structurePermissionSupport =
			structurePermissionSupportServiceWrapper.getService();

		return structurePermissionSupport.getResourceName();
	}

	protected String getResourceName(
		ServiceWrapper<DDMTemplatePermissionSupport>
			templatePermissionSupportServiceWrapper,
		long classNameId) {

		DDMTemplatePermissionSupport templatePermissionSupport =
			templatePermissionSupportServiceWrapper.getService();

		return templatePermissionSupport.getResourceName(classNameId);
	}

	private void _checkAddTemplatePermission(
			PermissionChecker permissionChecker, long groupId, long classNameId,
			ServiceWrapper<DDMTemplatePermissionSupport>
				templatePermissionSupportServiceWrapper)
		throws PortalException {

		if (!_containsAddTemplatePermission(
				permissionChecker, groupId, classNameId,
				templatePermissionSupportServiceWrapper)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker,
				getResourceName(
					templatePermissionSupportServiceWrapper, classNameId),
				groupId,
				_getAddTemplateActionId(
					templatePermissionSupportServiceWrapper));
		}
	}

	private boolean _contains(
		PermissionChecker permissionChecker, String name, long classPK,
		String actionId) {

		Group group = _groupLocalService.fetchGroup(classPK);

		if ((group != null) && group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		return permissionChecker.hasPermission(group, name, classPK, actionId);
	}

	private boolean _containsAddTemplatePermission(
			PermissionChecker permissionChecker, long groupId, long classNameId,
			ServiceWrapper<DDMTemplatePermissionSupport>
				templatePermissionSupportServiceWrapper)
		throws PortalException {

		String resourceName = getResourceName(
			templatePermissionSupportServiceWrapper, classNameId);

		List<String> portletNames = ResourceActionsUtil.getPortletNames();

		if (portletNames.contains(resourceName)) {
			return PortletPermissionUtil.contains(
				permissionChecker, groupId, null, resourceName,
				_getAddTemplateActionId(
					templatePermissionSupportServiceWrapper));
		}

		return _contains(
			permissionChecker, resourceName, groupId,
			_getAddTemplateActionId(templatePermissionSupportServiceWrapper));
	}

	private String _getAddStructureActionId(
		ServiceWrapper<DDMStructurePermissionSupport>
			structurePermissionSupportServiceWrapper) {

		return MapUtil.getString(
			structurePermissionSupportServiceWrapper.getProperties(),
			"add.structure.action.id", DDMActionKeys.ADD_STRUCTURE);
	}

	private String _getAddTemplateActionId(
		ServiceWrapper<DDMTemplatePermissionSupport>
			templatePermissionSupportServiceWrapper) {

		return MapUtil.getString(
			templatePermissionSupportServiceWrapper.getProperties(),
			"add.template.action.id", DDMActionKeys.ADD_TEMPLATE);
	}

	private ServiceWrapper<DDMTemplatePermissionSupport>
			_getDDMTemplatePermissionSupportServiceWrapper(
				String resourceClassName)
		throws PortalException {

		ServiceWrapper<DDMTemplatePermissionSupport>
			ddmTemplatePermissionSupportServiceWrapper =
				_ddmTemplatePermissionSupportServiceTrackerMap.getService(
					resourceClassName);

		if (ddmTemplatePermissionSupportServiceWrapper == null) {
			throw new PortalException(
				"The model does not support DDMTemplate permission checking " +
					resourceClassName);
		}

		return ddmTemplatePermissionSupportServiceWrapper;
	}

	private String _getTemplateModelResourceName(
		String resourceClassName,
		ServiceWrapper<DDMTemplatePermissionSupport>
			templatePermissionSupportServiceWrapper) {

		boolean defaultModelResourceName = MapUtil.getBoolean(
			templatePermissionSupportServiceWrapper.getProperties(),
			"default.model.resource.name");

		if (defaultModelResourceName) {
			return DDMTemplate.class.getName();
		}

		return ResourceActionsUtil.getCompositeModelName(
			resourceClassName, DDMTemplate.class.getName());
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private ServiceTrackerMap
		<String, ServiceWrapper<DDMStructurePermissionSupport>>
			_ddmStructurePermissionSupportServiceTrackerMap;
	private ServiceTrackerMap
		<String, ServiceWrapper<DDMTemplatePermissionSupport>>
			_ddmTemplatePermissionSupportServiceTrackerMap;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}
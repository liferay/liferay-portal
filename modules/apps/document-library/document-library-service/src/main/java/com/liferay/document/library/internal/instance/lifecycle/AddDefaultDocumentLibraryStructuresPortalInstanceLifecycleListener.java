/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.instance.lifecycle;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.internal.util.DDMFormUtil;
import com.liferay.document.library.kernel.processor.RawMetadataProcessor;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.osgi.util.configuration.ConfigurationPersistenceUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Miguel Pastor
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = PortalInstanceLifecycleListener.class
)
public class AddDefaultDocumentLibraryStructuresPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public long getLastModifiedTime() {
		return _lastModifiedTime;
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!_dlConfiguration.addDefaultStructures()) {
			return;
		}

		_addDLRawMetadataStructures(company.getCompanyId());
	}

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		_lastModifiedTime = ConfigurationPersistenceUtil.update(
			this, properties);

		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	private void _addDLRawMetadataStructures(long companyId) throws Exception {
		String name =
			com.liferay.portal.kernel.metadata.RawMetadataProcessor.
				TIKA_RAW_METADATA;

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Group group = _groupLocalService.getCompanyGroup(companyId);

		serviceContext.setScopeGroupId(group.getGroupId());

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		serviceContext.setUserId(guestUserId);

		if (!_ddmStructureLocalService.hasStructure(
				group.getGroupId(),
				_portal.getClassNameId(RawMetadataProcessor.class), name)) {

			Locale locale = _portal.getSiteDefaultLocale(group.getGroupId());

			Map<Locale, String> nameMap = HashMapBuilder.put(
				locale, name
			).build();

			Map<Locale, String> descriptionMap = HashMapBuilder.put(
				locale, name
			).build();

			DDMForm ddmForm = DDMFormUtil.buildDDMForm(locale);

			DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

			_ddmStructureLocalService.addStructure(
				null, guestUserId, group.getGroupId(),
				DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
				_portal.getClassNameId(RawMetadataProcessor.class), name,
				nameMap, descriptionMap, ddmForm, ddmFormLayout,
				StorageType.DEFAULT.toString(),
				DDMStructureConstants.TYPE_DEFAULT, serviceContext);
		}
	}

	@Reference
	private DDM _ddm;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private GroupLocalService _groupLocalService;

	private long _lastModifiedTime;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.instance.lifecycle;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.web.internal.info.collection.provider.DDMStructureRelatedInfoCollectionProvider;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(enabled = false, service = ModelListener.class)
public class DDMStructureModelListener extends BaseModelListener<DDMStructure> {

	@Override
	public void onAfterCreate(DDMStructure ddmStructure)
		throws ModelListenerException {

		if (ddmStructure.getClassNameId() != _portal.getClassNameId(
				JournalArticle.class.getName())) {

			return;
		}

		Map<Long, ServiceRegistration<?>> serviceRegistrations =
			_serviceRegistrations.computeIfAbsent(
				ddmStructure.getCompanyId(), key -> new HashMap<>());

		serviceRegistrations.put(
			ddmStructure.getStructureId(),
			_bundleContext.registerService(
				RelatedInfoItemCollectionProvider.class,
				new DDMStructureRelatedInfoCollectionProvider(
					ddmStructure, _journalArticleLocalService),
				null));
	}

	@Override
	public void onBeforeRemove(DDMStructure ddmStructure)
		throws ModelListenerException {

		if (ddmStructure.getClassNameId() != _portal.getClassNameId(
				JournalArticle.class.getName())) {

			return;
		}

		Map<Long, ServiceRegistration<?>> serviceRegistrations =
			_serviceRegistrations.get(ddmStructure.getCompanyId());

		if (MapUtil.isNotEmpty(serviceRegistrations)) {
			ServiceRegistration<?> serviceRegistration =
				serviceRegistrations.remove(ddmStructure.getStructureId());

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceRegistration = bundleContext.registerService(
			PortalInstanceLifecycleListener.class,
			new DDMStructurePortalInstanceLifecycleListener(), null);
	}

	@Deactivate
	protected void deactivate() {
		_companyLocalService.forEachCompanyId(
			companyId -> _unregisterCompanyDDMStructures(companyId));

		_serviceRegistration.unregister();
	}

	private void _unregisterCompanyDDMStructures(long companyId) {
		Map<Long, ServiceRegistration<?>> serviceRegistrations =
			_serviceRegistrations.remove(companyId);

		if (serviceRegistrations == null) {
			return;
		}

		for (Map.Entry<Long, ServiceRegistration<?>> entry :
				serviceRegistrations.entrySet()) {

			ServiceRegistration<?> serviceRegistration = entry.getValue();

			if (serviceRegistration == null) {
				continue;
			}

			serviceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Portal _portal;

	private ServiceRegistration<PortalInstanceLifecycleListener>
		_serviceRegistration;
	private final Map<Long, Map<Long, ServiceRegistration<?>>>
		_serviceRegistrations = Collections.synchronizedMap(
			new LinkedHashMap<>());

	private class DDMStructurePortalInstanceLifecycleListener
		implements PortalInstanceLifecycleListener {

		@Override
		public void portalInstanceRegistered(Company company) {
			Map<Long, ServiceRegistration<?>> serviceRegistrations =
				new HashMap<>();

			List<DDMStructure> ddmStructures =
				_ddmStructureLocalService.getClassStructures(
					company.getCompanyId(),
					_portal.getClassNameId(JournalArticle.class.getName()));

			for (DDMStructure ddmStructure : ddmStructures) {
				serviceRegistrations.put(
					ddmStructure.getStructureId(),
					_bundleContext.registerService(
						RelatedInfoItemCollectionProvider.class,
						new DDMStructureRelatedInfoCollectionProvider(
							ddmStructure, _journalArticleLocalService),
						null));
			}

			if (MapUtil.isNotEmpty(serviceRegistrations)) {
				_serviceRegistrations.put(
					company.getCompanyId(), serviceRegistrations);
			}
		}

		@Override
		public void portalInstanceUnregistered(Company company) {
			_unregisterCompanyDDMStructures(company.getCompanyId());
		}

	}

}
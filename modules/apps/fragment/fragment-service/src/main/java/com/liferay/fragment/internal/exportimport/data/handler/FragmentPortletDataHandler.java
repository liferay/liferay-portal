/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.portlet.data.handler.helper.PortletDataHandlerHelper;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
	service = PortletDataHandler.class
)
public class FragmentPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "fragments";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public String getServiceName() {
		return FragmentConstants.SERVICE_NAME;
	}

	@Override
	public boolean isConfigurationEnabled() {
		return false;
	}

	@Override
	public boolean validateSchemaVersion(String schemaVersion) {
		return _portletDataHandlerHelper.validateSchemaVersion(
			schemaVersion, getSchemaVersion());
	}

	@Activate
	protected void activate() {
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(FragmentCollection.class),
			new StagedModelType(FragmentEntry.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "entries", true, false, null,
				FragmentEntry.class.getName()));
		setPublishToLiveByDefault(true);
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				FragmentPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_fragmentEntryStagedModelRepository.deleteStagedModels(
			portletDataContext);
		_fragmentCollectionStagedModelRepository.deleteStagedModels(
			portletDataContext);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "entries")) {
			return getExportDataRootElementString(rootElement);
		}

		portletDataContext.addPortletPermissions(
			FragmentConstants.RESOURCE_NAME);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		ActionableDynamicQuery fragmentCollectionExportActionableDynamicQuery =
			_fragmentCollectionStagedModelRepository.
				getExportActionableDynamicQuery(portletDataContext);

		fragmentCollectionExportActionableDynamicQuery.performActions();

		ActionableDynamicQuery fragmentEntryActionableDynamicQuery =
			_fragmentEntryStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		fragmentEntryActionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "entries")) {
			return null;
		}

		portletDataContext.importPortletPermissions(
			FragmentConstants.RESOURCE_NAME);

		Element fragmentCollectionsElement =
			portletDataContext.getImportDataGroupElement(
				FragmentCollection.class);

		List<Element> fragmentCollectionElements =
			fragmentCollectionsElement.elements();

		for (Element fragmentCollectionElement : fragmentCollectionElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, fragmentCollectionElement);
		}

		Element fragmentEntriesElement =
			portletDataContext.getImportDataGroupElement(FragmentEntry.class);

		List<Element> fragmentEntryElements = fragmentEntriesElement.elements();

		if (ListUtil.isEmpty(fragmentEntryElements)) {
			return null;
		}

		FragmentServiceConfiguration fragmentServiceConfiguration =
			_configurationProvider.getCompanyConfiguration(
				FragmentServiceConfiguration.class,
				portletDataContext.getCompanyId());

		if (!fragmentServiceConfiguration.propagateChanges() ||
			(ExportImportThreadLocal.isStagingInProcess() &&
			 _stagingGroupHelper.isStagedPortlet(
				 portletDataContext.getGroupId(),
				 FragmentPortletKeys.FRAGMENT))) {

			for (Element fragmentEntryElement : fragmentEntryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, fragmentEntryElement);
			}

			return null;
		}

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					_companyLocalService.getCompany(
						portletDataContext.getCompanyId()))) {

			for (Element fragmentEntryElement : fragmentEntryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, fragmentEntryElement);
			}
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			_staging.populateLastPublishDateCounts(
				portletDataContext,
				new StagedModelType[] {
					new StagedModelType(FragmentCollection.class.getName()),
					new StagedModelType(FragmentEntry.class.getName())
				});

			return;
		}

		ActionableDynamicQuery fragmentCollectionExportActionableDynamicQuery =
			_fragmentCollectionStagedModelRepository.
				getExportActionableDynamicQuery(portletDataContext);

		fragmentCollectionExportActionableDynamicQuery.performCount();

		ActionableDynamicQuery fragmentEntryExportActionableDynamicQuery =
			_fragmentEntryStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		fragmentEntryExportActionableDynamicQuery.performCount();
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(model.class.name=com.liferay.fragment.model.FragmentCollection)",
		unbind = "-"
	)
	private StagedModelRepository<FragmentCollection>
		_fragmentCollectionStagedModelRepository;

	@Reference(
		target = "(model.class.name=com.liferay.fragment.model.FragmentEntry)",
		unbind = "-"
	)
	private StagedModelRepository<FragmentEntry>
		_fragmentEntryStagedModelRepository;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private PortletDataHandlerHelper _portletDataHandlerHelper;

	@Reference
	private Staging _staging;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}
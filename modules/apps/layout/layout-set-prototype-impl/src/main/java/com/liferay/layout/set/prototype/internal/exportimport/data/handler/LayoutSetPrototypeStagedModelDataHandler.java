/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.ExportImportService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.xml.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniela Zapata Riesco
 */
@Component(service = StagedModelDataHandler.class)
public class LayoutSetPrototypeStagedModelDataHandler
	extends BaseStagedModelDataHandler<LayoutSetPrototype> {

	public static final String[] CLASS_NAMES = {
		LayoutSetPrototype.class.getName()
	};

	@Override
	public void deleteStagedModel(LayoutSetPrototype layoutSetPrototype)
		throws PortalException {

		_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
			layoutSetPrototype);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		LayoutSetPrototype layoutSetPrototype =
			fetchStagedModelByUuidAndGroupId(uuid, group.getCompanyId());

		if (layoutSetPrototype != null) {
			deleteStagedModel(layoutSetPrototype);
		}
	}

	@Override
	public List<LayoutSetPrototype> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_layoutSetPrototypeLocalService.
				fetchLayoutSetPrototypeByUuidAndCompanyId(uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	public String getLayoutSetPrototypeLARFileName(
		LayoutSetPrototype layoutSetPrototype) {

		return layoutSetPrototype.getLayoutSetPrototypeId() + ".lar";
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			LayoutSetPrototype layoutSetPrototype)
		throws Exception {

		Element layoutSetPrototypeElement =
			portletDataContext.getExportDataElement(layoutSetPrototype);

		portletDataContext.addClassedModel(
			layoutSetPrototypeElement,
			ExportImportPathUtil.getModelPath(layoutSetPrototype),
			layoutSetPrototype);

		_exportLayouts(layoutSetPrototype, portletDataContext);

		_exportLayoutPrototypes(
			portletDataContext, layoutSetPrototype, layoutSetPrototypeElement);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			LayoutSetPrototype layoutSetPrototype)
		throws Exception {

		long userId = portletDataContext.getUserId(
			layoutSetPrototype.getUserUuid());

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		boolean layoutsUpdateable = GetterUtil.getBoolean(
			settingsUnicodeProperties.getProperty("layoutsUpdateable"), true);
		boolean readyForPropagation = GetterUtil.getBoolean(
			settingsUnicodeProperties.getProperty("readyForPropagation"), true);

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			layoutSetPrototype);

		serviceContext.setAttribute("addDefaultLayout", Boolean.FALSE);

		LayoutSetPrototype importedLayoutSetPrototype = null;

		if (portletDataContext.isDataStrategyMirror()) {
			LayoutSetPrototype existingLayoutSetPrototype =
				_layoutSetPrototypeLocalService.
					fetchLayoutSetPrototypeByUuidAndCompanyId(
						layoutSetPrototype.getUuid(),
						portletDataContext.getCompanyId());

			if (existingLayoutSetPrototype == null) {
				serviceContext.setUuid(layoutSetPrototype.getUuid());

				importedLayoutSetPrototype =
					_layoutSetPrototypeLocalService.addLayoutSetPrototype(
						userId, portletDataContext.getCompanyId(),
						layoutSetPrototype.getNameMap(),
						layoutSetPrototype.getDescriptionMap(),
						layoutSetPrototype.isActive(), layoutsUpdateable,
						readyForPropagation, serviceContext);
			}
			else {
				importedLayoutSetPrototype =
					_layoutSetPrototypeLocalService.updateLayoutSetPrototype(
						existingLayoutSetPrototype.getLayoutSetPrototypeId(),
						layoutSetPrototype.getNameMap(),
						layoutSetPrototype.getDescriptionMap(),
						layoutSetPrototype.isActive(), layoutsUpdateable,
						readyForPropagation, serviceContext);
			}
		}
		else {
			importedLayoutSetPrototype =
				_layoutSetPrototypeLocalService.addLayoutSetPrototype(
					userId, portletDataContext.getCompanyId(),
					layoutSetPrototype.getNameMap(),
					layoutSetPrototype.getDescriptionMap(),
					layoutSetPrototype.isActive(), layoutsUpdateable,
					readyForPropagation, serviceContext);
		}

		_importLayoutPrototypes(portletDataContext, layoutSetPrototype);
		_importLayouts(
			portletDataContext, layoutSetPrototype, importedLayoutSetPrototype,
			serviceContext);

		portletDataContext.importClassedModel(
			layoutSetPrototype, importedLayoutSetPrototype);
	}

	@Override
	protected boolean isSkipImportReferenceStagedModels() {
		return true;
	}

	private void _exportLayoutPrototypes(
			PortletDataContext portletDataContext,
			LayoutSetPrototype layoutSetPrototype,
			Element layoutSetPrototypeElement)
		throws Exception {

		DynamicQuery dynamicQuery = _layoutLocalService.dynamicQuery();

		Property groupIdProperty = PropertyFactoryUtil.forName("groupId");

		dynamicQuery.add(groupIdProperty.eq(layoutSetPrototype.getGroupId()));

		Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

		Property layoutPrototypeUuidProperty = PropertyFactoryUtil.forName(
			"layoutPrototypeUuid");

		conjunction.add(layoutPrototypeUuidProperty.isNotNull());
		conjunction.add(layoutPrototypeUuidProperty.ne(StringPool.BLANK));

		dynamicQuery.add(conjunction);

		List<Layout> layouts = _layoutLocalService.dynamicQuery(dynamicQuery);

		boolean exportLayoutPrototypes = portletDataContext.getBooleanParameter(
			"layout_set_prototypes", "page-templates");

		for (Layout layout : layouts) {
			LayoutPrototype layoutPrototype =
				_layoutPrototypeLocalService.
					getLayoutPrototypeByUuidAndCompanyId(
						layout.getLayoutPrototypeUuid(),
						portletDataContext.getCompanyId());

			portletDataContext.addReferenceElement(
				layout, layoutSetPrototypeElement, layoutPrototype,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY,
				!exportLayoutPrototypes);

			if (exportLayoutPrototypes) {
				StagedModelDataHandlerUtil.exportStagedModel(
					portletDataContext, layoutPrototype);
			}
		}
	}

	private void _exportLayouts(
			LayoutSetPrototype layoutSetPrototype,
			PortletDataContext portletDataContext)
		throws Exception {

		File file = null;

		try {
			file = _exportLayoutSetPrototype(
				layoutSetPrototype, new ServiceContext());

			try (InputStream inputStream = new FileInputStream(file)) {
				String layoutSetPrototypeLARPath =
					ExportImportPathUtil.getModelPath(
						layoutSetPrototype,
						getLayoutSetPrototypeLARFileName(layoutSetPrototype));

				portletDataContext.addZipEntry(
					layoutSetPrototypeLARPath, inputStream);
			}

			List<Layout> layoutSetPrototypeLayouts =
				_layoutLocalService.getLayouts(
					layoutSetPrototype.getGroupId(), true);

			Element layoutSetPrototypeElement =
				portletDataContext.getExportDataElement(layoutSetPrototype);

			for (Layout layoutSetPrototypeLayout : layoutSetPrototypeLayouts) {
				portletDataContext.addReferenceElement(
					layoutSetPrototype, layoutSetPrototypeElement,
					layoutSetPrototypeLayout,
					PortletDataContext.REFERENCE_TYPE_EMBEDDED, false);
			}
		}
		finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	private File _exportLayoutSetPrototype(
			LayoutSetPrototype layoutSetPrototype,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.fetchUser(serviceContext.getUserId());

		if (user == null) {
			BackgroundTask backgroundTask =
				_backgroundTaskManager.fetchBackgroundTask(
					BackgroundTaskThreadLocal.getBackgroundTaskId());

			if (backgroundTask != null) {
				user = _userLocalService.getUser(backgroundTask.getUserId());
			}
		}

		if (user == null) {
			user = _userLocalService.getUser(
				GetterUtil.getLong(PrincipalThreadLocal.getName()));
		}

		LayoutSet layoutSet = layoutSetPrototype.getLayoutSet();

		List<Layout> layoutSetPrototypeLayouts = _layoutLocalService.getLayouts(
			layoutSet.getGroupId(), layoutSet.isPrivateLayout());

		Map<String, String[]> parameterMap = _getLayoutSetPrototypeParameters();

		parameterMap.put(
			PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT,
			new String[] {Boolean.FALSE.toString()});

		Map<String, Serializable> exportLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportLayoutSettingsMap(
					user, layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
					_exportImportHelper.getLayoutIds(layoutSetPrototypeLayouts),
					parameterMap);

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					exportLayoutSettingsMap);

		return _exportImportLocalService.exportLayoutsAsFile(
			exportImportConfiguration);
	}

	private Map<String, String[]> _getLayoutSetPrototypeParameters() {
		return LinkedHashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR}
		).put(
			PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.DELETE_PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.FAVICON,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
			new String[] {
				PortletDataHandlerKeys.
					LAYOUTS_IMPORT_MODE_CREATED_FROM_PROTOTYPE
			}
		).put(
			PortletDataHandlerKeys.LOGO, new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.THEME_REFERENCE,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.USER_ID_STRATEGY,
			new String[] {UserIdStrategy.CURRENT_USER_ID}
		).build();
	}

	private void _importLayoutPrototypes(
			PortletDataContext portletDataContext,
			LayoutSetPrototype layoutSetPrototype)
		throws Exception {

		List<Element> layoutPrototypeElements =
			portletDataContext.getReferenceDataElements(
				layoutSetPrototype, LayoutPrototype.class);

		for (Element layoutPrototypeElement : layoutPrototypeElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, layoutPrototypeElement);
		}
	}

	private void _importLayouts(
			PortletDataContext portletDataContext,
			LayoutSetPrototype layoutSetPrototype,
			LayoutSetPrototype importedLayoutSetPrototype,
			ServiceContext serviceContext)
		throws Exception {

		String layoutSetPrototypeLARPath = ExportImportPathUtil.getModelPath(
			layoutSetPrototype,
			getLayoutSetPrototypeLARFileName(layoutSetPrototype));

		try (InputStream inputStream =
				portletDataContext.getZipEntryAsInputStream(
					layoutSetPrototypeLARPath)) {

			_importLayoutSetPrototype(
				importedLayoutSetPrototype, inputStream, serviceContext);
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}
	}

	private void _importLayoutSetPrototype(
			LayoutSetPrototype layoutSetPrototype, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSet layoutSet = layoutSetPrototype.getLayoutSet();

		Map<String, String[]> parameterMap = _getLayoutSetPrototypeParameters();

		parameterMap.put(
			PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT,
			new String[] {Boolean.FALSE.toString()});

		_setLayoutSetPrototypeLinkEnabledParameter(
			parameterMap, layoutSet, serviceContext);

		User user = _userLocalService.fetchUser(serviceContext.getUserId());

		if (user == null) {
			BackgroundTask backgroundTask =
				_backgroundTaskManager.fetchBackgroundTask(
					BackgroundTaskThreadLocal.getBackgroundTaskId());

			if (backgroundTask != null) {
				user = _userLocalService.getUser(backgroundTask.getUserId());
			}
		}

		if (user == null) {
			user = _userLocalService.getUser(
				GetterUtil.getLong(PrincipalThreadLocal.getName()));
		}

		Map<String, Serializable> importLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildImportLayoutSettingsMap(
					user.getUserId(), layoutSet.getGroupId(),
					layoutSet.isPrivateLayout(), null, parameterMap,
					user.getLocale(), user.getTimeZone());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					importLayoutSettingsMap);

		_exportImportService.importLayouts(
			exportImportConfiguration, inputStream);
	}

	private void _setLayoutSetPrototypeLinkEnabledParameter(
		Map<String, String[]> parameterMap, LayoutSet targetLayoutSet,
		ServiceContext serviceContext) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) ||
			!PortalPermissionUtil.contains(
				permissionChecker, ActionKeys.UNLINK_LAYOUT_SET_PROTOTYPE)) {

			return;
		}

		if (targetLayoutSet.isPrivateLayout()) {
			boolean privateLayoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
				serviceContext, "privateLayoutSetPrototypeLinkEnabled", true);

			if (!privateLayoutSetPrototypeLinkEnabled) {
				privateLayoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
					serviceContext, "layoutSetPrototypeLinkEnabled");
			}

			parameterMap.put(
				PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED,
				new String[] {
					String.valueOf(privateLayoutSetPrototypeLinkEnabled)
				});
		}
		else {
			boolean publicLayoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
				serviceContext, "publicLayoutSetPrototypeLinkEnabled");

			if (!publicLayoutSetPrototypeLinkEnabled) {
				publicLayoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
					serviceContext, "layoutSetPrototypeLinkEnabled", true);
			}

			parameterMap.put(
				PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED,
				new String[] {
					String.valueOf(publicLayoutSetPrototypeLinkEnabled)
				});
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypeStagedModelDataHandler.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	@Reference
	private ExportImportService _exportImportService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
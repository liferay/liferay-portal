/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.configuration;

import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactory;
import com.liferay.exportimport.kernel.exception.ExportImportRuntimeException;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(service = ExportImportConfigurationParameterMapFactory.class)
public class ExportImportConfigurationParameterMapFactoryImpl
	implements ExportImportConfigurationParameterMapFactory {

	@Override
	public Map<String, String[]> buildFullPublishParameterMap() {
		return buildParameterMap(
			PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE, true, true,
			false, true, true, false, true, true, true, true, true, null, true,
			true, null, true, null, ExportImportDateUtil.RANGE_ALL, true, true,
			UserIdStrategy.CURRENT_USER_ID);
	}

	@Override
	public Map<String, String[]> buildParameterMap() {
		return buildParameterMap(
			PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE, true, true,
			false, true, false, false, true, true, true, true, true, null, true,
			true, null, true, null,
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE, true, true,
			UserIdStrategy.CURRENT_USER_ID);
	}

	@Override
	public Map<String, String[]> buildParameterMap(
		PortletRequest portletRequest) {

		Map<String, String[]> parameterMap = new LinkedHashMap<>(
			portletRequest.getParameterMap());

		if (ExportImportDateUtil.isRangeFromLastPublishDate(parameterMap)) {
			_replaceParameterMap(parameterMap);
		}

		if (!parameterMap.containsKey(PortletDataHandlerKeys.DATA_STRATEGY)) {
			parameterMap.put(
				PortletDataHandlerKeys.DATA_STRATEGY,
				new String[] {
					PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE
				});
		}

		/*if (!parameterMap.containsKey(
				PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS)) {

			parameterMap.put(
				PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
				new String[] {Boolean.TRUE.toString()});
		}*/

		if (!parameterMap.containsKey(PortletDataHandlerKeys.DELETE_LAYOUTS)) {
			parameterMap.put(
				PortletDataHandlerKeys.DELETE_LAYOUTS,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(
				PortletDataHandlerKeys.DELETE_PORTLET_DATA)) {

			parameterMap.put(
				PortletDataHandlerKeys.DELETE_PORTLET_DATA,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(PortletDataHandlerKeys.FAVICON)) {
			parameterMap.put(
				PortletDataHandlerKeys.FAVICON,
				new String[] {Boolean.TRUE.toString()});
		}

		if (!parameterMap.containsKey(
				PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED)) {

			parameterMap.put(
				PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(
				PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS)) {

			parameterMap.put(
				PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(
				PortletDataHandlerKeys.LAYOUT_SET_SETTINGS)) {

			parameterMap.put(
				PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(PortletDataHandlerKeys.LOGO)) {
			parameterMap.put(
				PortletDataHandlerKeys.LOGO,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(
				PortletDataHandlerKeys.PORTLET_CONFIGURATION)) {

			parameterMap.put(
				PortletDataHandlerKeys.PORTLET_CONFIGURATION,
				new String[] {Boolean.TRUE.toString()});
		}

		if (!parameterMap.containsKey(PortletDataHandlerKeys.PORTLET_DATA)) {
			parameterMap.put(
				PortletDataHandlerKeys.PORTLET_DATA,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(
				PortletDataHandlerKeys.PORTLET_DATA_ALL)) {

			parameterMap.put(
				PortletDataHandlerKeys.PORTLET_DATA_ALL,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(PortletDataHandlerKeys.THEME_REFERENCE)) {
			parameterMap.put(
				PortletDataHandlerKeys.THEME_REFERENCE,
				new String[] {Boolean.FALSE.toString()});
		}

		if (!parameterMap.containsKey(
				PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE)) {

			parameterMap.put(
				PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE,
				new String[] {Boolean.TRUE.toString()});
		}

		if (!parameterMap.containsKey(
				PortletDataHandlerKeys.USER_ID_STRATEGY)) {

			parameterMap.put(
				PortletDataHandlerKeys.USER_ID_STRATEGY,
				new String[] {UserIdStrategy.CURRENT_USER_ID});
		}

		return parameterMap;
	}

	@Override
	public Map<String, String[]> buildParameterMap(
		String dataStrategy, Boolean deleteLayouts,
		Boolean deleteMissingLayouts, Boolean deletePortletData,
		Boolean deletions, Boolean ignoreLastPublishDate,
		Boolean layoutSetPrototypeLinkEnabled, Boolean layoutSetSettings,
		Boolean logo, Boolean permissions, Boolean portletConfiguration,
		Boolean portletConfigurationAll,
		List<String> portletConfigurationPortletIds, Boolean portletData,
		Boolean portletDataAll, List<String> portletDataPortletIds,
		Boolean portletSetupAll, List<String> portletSetupPortletIds,
		String range, Boolean themeReference, Boolean updateLastPublishDate,
		String userIdStrategy) {

		Map<String, String[]> parameterMap = new LinkedHashMap<>();

		if (Validator.isNotNull(dataStrategy)) {
			parameterMap.put(
				PortletDataHandlerKeys.DATA_STRATEGY,
				new String[] {
					PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE
				});
		}

		boolean deleteLayoutsParameter = false;

		if (deleteLayouts != null) {
			deleteLayoutsParameter = deleteLayouts.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.DELETE_LAYOUTS,
			new String[] {String.valueOf(deleteLayoutsParameter)});

		boolean deleteMissingLayoutsParameter = true;

		if (deleteMissingLayouts != null) {
			deleteMissingLayoutsParameter = deleteMissingLayouts.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
			new String[] {String.valueOf(deleteMissingLayoutsParameter)});

		boolean deletePortletDataParameter = false;

		if (deletePortletData != null) {
			deletePortletDataParameter = deletePortletData.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.DELETE_PORTLET_DATA,
			new String[] {String.valueOf(deletePortletDataParameter)});

		boolean deletionsParameter = false;

		if (deletions != null) {
			deletionsParameter = deletions.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {String.valueOf(deletionsParameter)});

		parameterMap.put(
			PortletDataHandlerKeys.FAVICON,
			new String[] {Boolean.TRUE.toString()});

		boolean ignoreLastPublishDateParameter = true;

		if (ignoreLastPublishDate != null) {
			ignoreLastPublishDateParameter =
				ignoreLastPublishDate.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.IGNORE_LAST_PUBLISH_DATE,
			new String[] {String.valueOf(ignoreLastPublishDateParameter)});

		boolean layoutSetPrototypeLinkEnabledParameter = false;

		if (layoutSetPrototypeLinkEnabled != null) {
			layoutSetPrototypeLinkEnabledParameter =
				layoutSetPrototypeLinkEnabled.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED,
			new String[] {
				String.valueOf(layoutSetPrototypeLinkEnabledParameter)
			});

		boolean layoutSetPrototypeSettingsParameter = false;

		if (layoutSetPrototypeLinkEnabled != null) {
			layoutSetPrototypeSettingsParameter =
				layoutSetPrototypeLinkEnabled.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS,
			new String[] {String.valueOf(layoutSetPrototypeSettingsParameter)});

		boolean layoutSetSettingsParameter = false;

		if (layoutSetSettings != null) {
			layoutSetSettingsParameter = layoutSetSettings.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
			new String[] {String.valueOf(layoutSetSettingsParameter)});

		boolean logoParameter = false;

		if (logo != null) {
			logoParameter = logo.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.LOGO,
			new String[] {String.valueOf(logoParameter)});

		boolean permissionsParameter = true;

		if (permissions != null) {
			permissionsParameter = permissions.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {String.valueOf(permissionsParameter)});

		boolean portletConfigurationParameter = true;

		if (portletConfiguration != null) {
			portletConfigurationParameter = portletConfiguration.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {String.valueOf(portletConfigurationParameter)});

		boolean portletConfigurationAllParameter = true;

		if (portletConfigurationAll != null) {
			portletConfigurationAllParameter =
				portletConfigurationAll.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {String.valueOf(portletConfigurationAllParameter)});

		if (portletConfigurationPortletIds != null) {
			for (String portletId : portletConfigurationPortletIds) {
				parameterMap.put(
					PortletDataHandlerKeys.PORTLET_CONFIGURATION +
						StringPool.UNDERLINE + portletId,
					new String[] {Boolean.TRUE.toString()});
			}
		}

		boolean portletDataParameter = false;

		if (portletData != null) {
			portletDataParameter = portletData.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {String.valueOf(portletDataParameter)});

		boolean portletDataAllParameter = false;

		if (portletDataAll != null) {
			portletDataAllParameter = portletDataAll.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {String.valueOf(portletDataAllParameter)});

		if (portletDataPortletIds != null) {
			for (String portletId : portletDataPortletIds) {
				parameterMap.put(
					PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
						portletId,
					new String[] {Boolean.TRUE.toString()});
			}
		}

		boolean portletSetupAllParameter = true;

		if (portletSetupAll != null) {
			portletSetupAllParameter = portletSetupAll.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {String.valueOf(portletSetupAllParameter)});

		if (portletSetupPortletIds != null) {
			for (String portletId : portletSetupPortletIds) {
				parameterMap.put(
					PortletDataHandlerKeys.PORTLET_SETUP +
						StringPool.UNDERLINE + portletId,
					new String[] {Boolean.TRUE.toString()});
			}
		}

		String rangeParameter =
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE;

		if (Validator.isNotNull(range)) {
			rangeParameter = range;
		}

		parameterMap.put(
			ExportImportDateUtil.RANGE, new String[] {rangeParameter});

		boolean themeReferenceParameter = false;

		if (themeReference != null) {
			themeReferenceParameter = themeReference.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.THEME_REFERENCE,
			new String[] {String.valueOf(themeReferenceParameter)});

		boolean updateLastPublishDateParameter = true;

		if (updateLastPublishDate != null) {
			updateLastPublishDateParameter =
				updateLastPublishDate.booleanValue();
		}

		parameterMap.put(
			PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE,
			new String[] {String.valueOf(updateLastPublishDateParameter)});

		String userIdStrategyParameter = UserIdStrategy.CURRENT_USER_ID;

		if (Validator.isNotNull(userIdStrategy)) {
			userIdStrategyParameter = userIdStrategy;
		}

		parameterMap.put(
			PortletDataHandlerKeys.USER_ID_STRATEGY,
			new String[] {userIdStrategyParameter});

		if (ExportImportDateUtil.isRangeFromLastPublishDate(parameterMap)) {
			_replaceParameterMap(parameterMap);
		}

		return parameterMap;
	}

	@Override
	public Map<String, String[]> buildParameterMap(
		String dataStrategy, Boolean deleteMissingLayouts,
		Boolean deletePortletData, Boolean deletions,
		Boolean ignoreLastPublishDate, Boolean layoutSetPrototypeLinkEnabled,
		Boolean layoutSetSettings, Boolean logo, Boolean permissions,
		Boolean portletConfiguration, Boolean portletConfigurationAll,
		List<String> portletConfigurationPortletIds, Boolean portletData,
		Boolean portletDataAll, List<String> portletDataPortletIds,
		Boolean portletSetupAll, List<String> portletSetupPortletIds,
		String range, Boolean themeReference, Boolean updateLastPublishDate,
		String userIdStrategy) {

		return buildParameterMap(
			dataStrategy, null, deleteMissingLayouts, deletePortletData,
			deletions, ignoreLastPublishDate, layoutSetPrototypeLinkEnabled,
			layoutSetSettings, logo, permissions, portletConfiguration,
			portletConfigurationAll, portletConfigurationPortletIds,
			portletData, portletDataAll, portletDataPortletIds, portletSetupAll,
			portletSetupPortletIds, range, themeReference,
			updateLastPublishDate, userIdStrategy);
	}

	private void _addModelParameter(
			Map<String, String[]> parameterMap, Portlet dataSiteLevelPortlet,
			boolean portletDataAll)
		throws PortletDataException {

		PortletDataHandler portletDataHandlerInstance =
			dataSiteLevelPortlet.getPortletDataHandlerInstance();

		PortletDataHandlerControl[] exportControls =
			portletDataHandlerInstance.getExportControls();

		for (PortletDataHandlerControl exportControl : exportControls) {
			if (!(exportControl instanceof PortletDataHandlerBoolean)) {
				continue;
			}

			PortletDataHandlerBoolean portletDataHandlerBoolean =
				(PortletDataHandlerBoolean)exportControl;

			boolean controlValue = portletDataHandlerBoolean.getDefaultState();

			if (!portletDataHandlerBoolean.isDisabled()) {
				controlValue = MapUtil.getBoolean(
					parameterMap,
					portletDataHandlerBoolean.getNamespacedControlName(), true);
			}

			if ((portletDataAll || controlValue) &&
				(portletDataHandlerBoolean.getClassName() != null)) {

				String referrerClassName =
					portletDataHandlerBoolean.getReferrerClassName();

				if (referrerClassName == null) {
					parameterMap.put(
						portletDataHandlerBoolean.getClassName(),
						new String[] {Boolean.TRUE.toString()});
				}
				else {
					parameterMap.put(
						portletDataHandlerBoolean.getClassName() +
							StringPool.POUND + referrerClassName,
						new String[] {Boolean.TRUE.toString()});
				}
			}
		}
	}

	private void _populatePortletResourceNames(
		Map<String, String[]> parameterMap, Portlet dataSiteLevelPortlet) {

		PortletDataHandler portletDataHandler =
			dataSiteLevelPortlet.getPortletDataHandlerInstance();

		String resourceName = portletDataHandler.getResourceName();

		if (resourceName == null) {
			return;
		}

		if (!parameterMap.containsKey("portletResourceNames")) {
			parameterMap.put("portletResourceNames", new String[0]);
		}

		String[] portletResourceNames = parameterMap.get(
			"portletResourceNames");

		portletResourceNames = ArrayUtil.append(
			portletResourceNames, resourceName);

		parameterMap.put("portletResourceNames", portletResourceNames);
	}

	private void _populateStagedModelTypes(
		Map<String, String[]> parameterMap, Portlet dataSiteLevelPortlet) {

		if (!parameterMap.containsKey("stagedModelTypes")) {
			parameterMap.put("stagedModelTypes", new String[0]);
		}

		PortletDataHandler portletDataHandler =
			dataSiteLevelPortlet.getPortletDataHandlerInstance();

		List<StagedModelType> stagedModelTypes = ListUtil.fromArray(
			portletDataHandler.getDeletionSystemEventStagedModelTypes());

		if (ListUtil.isEmpty(stagedModelTypes)) {
			return;
		}

		List<String> parameterStagedModelTypes = ListUtil.fromArray(
			parameterMap.get("stagedModelTypes"));

		for (StagedModelType stagedModelType : stagedModelTypes) {
			String stagedModelTypeString = stagedModelType.toString();

			if (parameterStagedModelTypes.contains(stagedModelTypeString)) {
				continue;
			}

			parameterStagedModelTypes.add(stagedModelTypeString);
		}

		parameterMap.put(
			"stagedModelTypes",
			parameterStagedModelTypes.toArray(new String[0]));
	}

	/**
	 * Completes different actions depending on the following cases:
	 *
	 * <p>
	 * Layout Staging:
	 * </p>
	 *
	 * <ol>
	 * <li>
	 * Removes the <code>PORTLET_DATA_portletId</code> and
	 * <code>PORTLET_DATA_ALL</code> parameters in the parameter map and
	 * replaces them with <code>PORTLET_DATA_changesetPortletId</code>.
	 * </li>
	 * <li>
	 * Adds model specific parameters to be able to decide whether a model needs
	 * to be exported in the changeset portlet data handler. For example:
	 * <code>com.liferay.journal.model.JournalArticle, <code>true</code></code>.
	 * </li>
	 * <li>
	 * Adds the original portlet ID parameter in case of portlet publication.
	 * </li>
	 * </ol>
	 *
	 * <p>
	 * Portlet Staging:
	 * </p>
	 *
	 * <ol>
	 * <li>
	 * The <code>PORTLET_DATA_portletId</code> remains the same.
	 * </li>
	 * <li>
	 * The <code>PortletExportControllerImpl</code> and
	 * <code>PortletImportControllerImpl</code> calls the changeset portlet data
	 * handler directly.
	 * </li>
	 * </ol>
	 *
	 * @param parameterMap the parameter map
	 */
	private void _replaceParameterMap(Map<String, String[]> parameterMap) {
		try {
			List<Portlet> dataSiteAndInstanceLevelPortlets =
				_exportImportHelper.getDataSiteAndInstanceLevelPortlets(
					CompanyThreadLocal.getCompanyId());

			boolean portletDataAll = MapUtil.getBoolean(
				parameterMap, PortletDataHandlerKeys.PORTLET_DATA_ALL);

			for (Portlet dataSiteAndInstanceLevelPortlet :
					dataSiteAndInstanceLevelPortlets) {

				String portletDataKey =
					PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
						dataSiteAndInstanceLevelPortlet.getRootPortletId();

				String[] portletDataValues = parameterMap.get(portletDataKey);

				if (portletDataAll ||
					((portletDataValues != null) &&
					 GetterUtil.getBoolean(portletDataValues[0]))) {

					_populatePortletResourceNames(
						parameterMap, dataSiteAndInstanceLevelPortlet);

					_populateStagedModelTypes(
						parameterMap, dataSiteAndInstanceLevelPortlet);

					_addModelParameter(
						parameterMap, dataSiteAndInstanceLevelPortlet,
						portletDataAll);
				}
			}

			parameterMap.put(
				PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
					ChangesetPortletKeys.CHANGESET,
				new String[] {StringPool.TRUE});
		}
		catch (Exception exception) {
			throw new ExportImportRuntimeException(exception);
		}
	}

	@Reference
	private ExportImportHelper _exportImportHelper;

}
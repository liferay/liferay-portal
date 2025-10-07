/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.PortletPreferences;

/**
 * A <code>PortletDataHandler</code> is a special class capable of exporting and
 * importing portlet specific data to a Liferay Archive file (LAR) when a site's
 * layouts are exported or imported.
 *
 * @author Raymond Augé
 * @author Joel Kozikowski
 * @author Bruno Farache
 */
public interface PortletDataHandler {

	/**
	 * Returns the portlet's preferences with default data added.
	 *
	 * @param  portletDataContext the context of the data addition
	 * @param  portletId the portlet ID of the portlet
	 * @param  portletPreferences the portlet preferences of the portlet
	 * @return a modified version of the portlet preferences that should be
	 *         saved, or <code>null</code> if the data handler made no changes
	 *         to the portlet preferences
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public PortletPreferences addDefaultData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException;

	/**
	 * Deletes the data created by the portlet. It can optionally return a
	 * modified version of the portlet preferences if it contains references to
	 * data that no longer exists.
	 *
	 * @param  portletDataContext the context of the data deletion
	 * @param  portletId the portlet ID of the portlet
	 * @param  portletPreferences the portlet preferences of the portlet
	 * @return a modified version of the portlet preferences that should be
	 *         saved, or <code>null</code> if the data handler made no changes
	 *         to the portlet preferences
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public PortletPreferences deleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException;

	/**
	 * Returns a string of data to be placed in the &lt;portlet-data&gt; section
	 * of the LAR file. This data will be passed as the <code>data</code>
	 * parameter of <code>importData()</code>.
	 *
	 * @param  portletDataContext the context of the data export
	 * @param  portletId the portlet ID of the portlet
	 * @param  portletPreferences the portlet preferences of the portlet
	 * @return a string of data to be placed in the LAR, which can be, but not
	 *         limited to XML, or <code>null</code> if no portlet data is to be
	 *         written out
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public String exportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException;

	public default String[] getClassNames() {
		return null;
	}

	public DataLevel getDataLevel();

	/**
	 * Returns an array of the portlet preferences that reference data. These
	 * preferences should only be updated if the referenced data is imported.
	 *
	 * @return an array of the portlet preferences that reference data
	 */
	public String[] getDataPortletPreferences();

	public StagedModelType[] getDeletionSystemEventStagedModelTypes();

	public PortletDataHandlerControl[] getExportConfigurationControls(
			long companyId, long groupId, Portlet portlet,
			boolean privateLayout)
		throws Exception;

	public PortletDataHandlerControl[] getExportConfigurationControls(
			long companyId, long groupId, Portlet portlet, long plid,
			boolean privateLayout)
		throws Exception;

	/**
	 * Returns an array of the controls defined for this data handler. These
	 * controls enable the developer to create fine grained controls over export
	 * behavior. The controls are rendered in the export UI.
	 *
	 * @return an array of the controls defined for this data handler
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public PortletDataHandlerControl[] getExportControls()
		throws PortletDataException;

	/**
	 * Returns an array of the metadata controls defined for this data handler.
	 * These controls enable the developer to create fine grained controls over
	 * export behavior of metadata such as tags, categories, ratings or
	 * comments. The controls are rendered in the export UI.
	 *
	 * @return an array of the metadata controls defined for this data handler
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public PortletDataHandlerControl[] getExportMetadataControls()
		throws PortletDataException;

	/**
	 * Returns the number of entities defined for this data handler that are
	 * available for export according to the provided manifest summary, or
	 * <code>-1</code> if no entities are included in the manifest summary.
	 *
	 * @param  manifestSummary the manifest summary listing the number of
	 *         exportable entities
	 * @return the number of entities that are available for export according to
	 *         the manifest summary, or <code>-1</code> if no entities are
	 *         included in the manifest summary
	 */
	public long getExportModelCount(ManifestSummary manifestSummary);

	public PortletDataHandlerControl[] getImportConfigurationControls(
		Portlet portlet, ManifestSummary manifestSummary);

	public PortletDataHandlerControl[] getImportConfigurationControls(
		String[] configurationPortletOptions);

	/**
	 * Returns an array of the controls defined for this data handler. These
	 * controls enable the developer to create fine grained controls over import
	 * behavior. The controls are rendered in the import UI.
	 *
	 * @return an array of the controls defined for this data handler
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public PortletDataHandlerControl[] getImportControls()
		throws PortletDataException;

	/**
	 * Returns an array of the metadata controls defined for this data handler.
	 * These controls enable the developer to create fine grained controls over
	 * import behavior of metadata such as tags, categories, ratings or
	 * comments. The controls are rendered in the export UI.
	 *
	 * @return an array of the metadata controls defined for this data handler
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public PortletDataHandlerControl[] getImportMetadataControls()
		throws PortletDataException;

	public default String getName() {
		Class<? extends PortletDataHandler> clazz = getClass();

		return clazz.getName();
	}

	public default String getNamespace() {
		return StringPool.BLANK;
	}

	public String getPortletId();

	public int getRank();

	public String getResourceName();

	/**
	 * Returns the schema version for this data handler, which represents the
	 * staging and export/import aspect of a component. The schema version is
	 * used to perform component related validation before importing data.
	 * Validating the schema version avoids broken data when importing, which is
	 * typically caused by import failures due to data schema inconsistency.
	 *
	 * <p>
	 * Schema versions follow the semantic versioning format
	 * <code>major.minor.bugfix</code>. The schema version is added to the LAR
	 * file for each application being processed. During import, the current
	 * schema version in an environment is compared to the schema version in the
	 * LAR file. The generic semantic versioning rules apply during this
	 * comparison:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * The major version has to be an exact match
	 * </li>
	 * <li>
	 * The minor version is backwards compatible for the same major version
	 * </li>
	 * <li>
	 * The bug fix is always compatible in the context of the two other version
	 * numbers
	 * </li>
	 * </ul>
	 *
	 * <p>
	 * Examples:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * Importing 2.0.0 into 3.0.0 is <em>not</em> compatible
	 * </li>
	 * <li>
	 * Importing 3.0.0 into 3.0.0 is compatible
	 * </li>
	 * <li>
	 * Importing 4.6.2 into 4.9.0 is compatible
	 * </li>
	 * <li>
	 * Importing 2.1.3 into 2.1.6 is compatible
	 * </li>
	 * </ul>
	 *
	 * @return the portlet data handler's schema version
	 */
	public String getSchemaVersion();

	public String getServiceName();

	/**
	 * Returns an array of the controls defined for this data handler. These
	 * controls enable the developer to create fine grained controls over
	 * staging publication behavior. The controls are rendered in the publish
	 * UI.
	 *
	 * @return an array of the controls defined for this data handler
	 */
	public default PortletDataHandlerControl[] getStagingControls() {
		return new PortletDataHandlerControl[0];
	}

	/**
	 * Handles any special processing of the data when the portlet is imported
	 * into a new layout. Can optionally return a modified version of
	 * <code>preferences</code> to be saved in the new portlet.
	 *
	 * @param  portletDataContext the context of the data import
	 * @param  portletId the portlet ID of the portlet
	 * @param  portletPreferences the portlet preferences of the portlet
	 * @param  data the string data that was returned by
	 *         <code>exportData()</code>
	 * @return a modified version of the portlet preferences that should be
	 *         saved, or <code>null</code> if the data handler made no changes
	 *         to the portlet preferences
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public PortletPreferences importData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws PortletDataException;

	public default boolean isBatch() {
		return false;
	}

	public default boolean isConfigurationEnabled() {
		return true;
	}

	public boolean isDataAlwaysStaged();

	public boolean isDataDepotLevel();

	public boolean isDataLocalized();

	public boolean isDataPortalLevel();

	public boolean isDataPortletInstanceLevel();

	public boolean isDataSiteLevel();

	public boolean isDisplayPortlet();

	public boolean isEmptyControlsAllowed();

	public default boolean isEnabled(long companyId) {
		return true;
	}

	/**
	 * Returns whether the data exported by this handler should be included by
	 * default when publishing to live. This should only be <code>true</code>
	 * for data that is meant to be managed in an staging environment such as
	 * CMS content, but not for data meant to be input by users such as wiki
	 * pages or message board posts.
	 *
	 * @return <code>true</code> if the data exported by this handler should be
	 *         included by default when publishing to live; <code>false</code>
	 *         otherwise
	 */
	public boolean isPublishToLiveByDefault();

	/**
	 * Returns <code>true</code> if the data handler stops operations and rolls
	 * back their transactions on operations throwing exceptions.
	 *
	 * @return <code>true</code> if the data handler stops operations and rolls
	 *         back their transactions on operations throwing exceptions;
	 *         <code>false</code> otherwise
	 */
	public boolean isRollbackOnException();

	public default boolean isStaged() {
		return true;
	}

	public default boolean isSupportsDataStrategyCopyAsNew() {
		return true;
	}

	public default boolean isSupportsDataStrategyMirrorWithOverwriting() {
		return false;
	}

	public void prepareManifestSummary(PortletDataContext portletDataContext)
		throws PortletDataException;

	public void prepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException;

	public void setPortletId(String portletId);

	public void setRank(int rank);

	public boolean validateSchemaVersion(String schemaVersion);

}
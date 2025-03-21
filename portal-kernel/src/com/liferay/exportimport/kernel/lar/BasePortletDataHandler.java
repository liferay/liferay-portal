/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.plugin.Version;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import jakarta.portlet.PortletPreferences;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BasePortletDataHandler implements PortletDataHandler {

	@Override
	public PortletPreferences addDefaultData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(portletDataContext))) {
			return null;
		}

		long startTime = 0;

		if (_log.isInfoEnabled()) {
			_log.info("Adding default data to portlet " + portletId);

			startTime = System.currentTimeMillis();
		}

		try {
			return doAddDefaultData(
				portletDataContext, portletId, portletPreferences);
		}
		catch (PortletDataException portletDataException) {
			throw portletDataException;
		}
		catch (Exception exception) {
			throw new PortletDataException(exception);
		}
		finally {
			if (_log.isInfoEnabled()) {
				long duration = System.currentTimeMillis() - startTime;

				_log.info(
					"Added default data to portlet in " +
						Time.getDuration(duration));
			}
		}
	}

	@Override
	public PortletPreferences deleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(portletDataContext))) {
			return null;
		}

		long startTime = 0;

		if (_log.isInfoEnabled()) {
			_log.info("Deleting portlet " + portletId);

			startTime = System.currentTimeMillis();
		}

		try {
			return doDeleteData(
				portletDataContext, portletId, portletPreferences);
		}
		catch (Exception exception) {
			throw _handleException(
				exception, PortletDataException.DELETE_PORTLET_DATA, portletId);
		}
		finally {
			if (_log.isInfoEnabled()) {
				long duration = System.currentTimeMillis() - startTime;

				_log.info("Deleted portlet in " + Time.getDuration(duration));
			}
		}
	}

	@Override
	public String exportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(portletDataContext))) {
			return null;
		}

		long startTime = 0;

		if (_log.isInfoEnabled()) {
			_log.info("Exporting portlet " + portletId);

			startTime = System.currentTimeMillis();
		}

		Element rootElement = null;

		try {
			rootElement = portletDataContext.getExportDataRootElement();

			portletDataContext.addDeletionSystemEventStagedModelTypes(
				getDeletionSystemEventStagedModelTypes());

			for (PortletDataHandlerControl portletDataHandlerControl :
					getExportControls()) {

				addUncheckedModelAdditionCount(
					portletDataContext, portletDataHandlerControl);
			}

			return doExportData(
				portletDataContext, portletId, portletPreferences);
		}
		catch (Exception exception) {
			throw _handleException(
				exception, PortletDataException.EXPORT_PORTLET_DATA, portletId);
		}
		finally {
			portletDataContext.setExportDataRootElement(rootElement);

			if (_log.isInfoEnabled()) {
				long duration = System.currentTimeMillis() - startTime;

				_log.info("Exported portlet in " + Time.getDuration(duration));
			}
		}
	}

	@Override
	public DataLevel getDataLevel() {
		return _dataLevel;
	}

	@Override
	public String[] getDataPortletPreferences() {
		return _dataPortletPreferences;
	}

	@Override
	public StagedModelType[] getDeletionSystemEventStagedModelTypes() {
		return _deletionSystemEventStagedModelTypes;
	}

	@Override
	public PortletDataHandlerControl[] getExportConfigurationControls(
			long companyId, long groupId, Portlet portlet,
			boolean privateLayout)
		throws Exception {

		return getExportConfigurationControls(
			companyId, groupId, portlet, -1, privateLayout);
	}

	@Override
	public PortletDataHandlerControl[] getExportConfigurationControls(
			long companyId, long groupId, Portlet portlet, long plid,
			boolean privateLayout)
		throws Exception {

		List<PortletDataHandlerBoolean> configurationControls =
			new ArrayList<>();

		// Setup

		long count1 =
			PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portlet, false);
		long count2 =
			PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
				groupId, PortletKeys.PREFS_OWNER_TYPE_GROUP,
				portlet.getRootPortletId(), false);
		long count3 =
			PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
				companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
				portlet.getRootPortletId(), false);

		if ((count1 > 0) || (count2 > 0) || (count3 > 0)) {
			PortletDataHandlerControl[] portletDataHandlerControls = null;

			if (isDisplayPortlet()) {
				portletDataHandlerControls = getExportControls();
			}

			configurationControls.add(
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_SETUP, "setup", true,
					false, portletDataHandlerControls, null, null));
		}

		// Archived setups

		count1 = PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
			-1, PortletKeys.PREFS_OWNER_TYPE_ARCHIVED,
			portlet.getRootPortletId(), false);

		if (count1 > 0) {
			configurationControls.add(
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS,
					"configuration-templates", true, false, null, null, null));
		}

		// User preferences

		count1 = PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
			-1, PortletKeys.PREFS_OWNER_TYPE_USER, plid, portlet, false);
		count2 = PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
			groupId, PortletKeys.PREFS_OWNER_TYPE_USER,
			PortletKeys.PREFS_PLID_SHARED, portlet, false);

		if ((count1 > 0) || (count2 > 0)) {
			configurationControls.add(
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_USER_PREFERENCES,
					"user-preferences", true, false, null, null, null));
		}

		return configurationControls.toArray(new PortletDataHandlerBoolean[0]);
	}

	@Override
	public PortletDataHandlerControl[] getExportControls() {
		return _exportControls;
	}

	@Override
	public PortletDataHandlerControl[] getExportMetadataControls() {
		return _exportMetadataControls;
	}

	@Override
	public long getExportModelCount(ManifestSummary manifestSummary) {
		return getExportModelCount(manifestSummary, getExportControls());
	}

	@Override
	public PortletDataHandlerControl[] getImportConfigurationControls(
		Portlet portlet, ManifestSummary manifestSummary) {

		return getImportConfigurationControls(
			manifestSummary.getConfigurationPortletOptions(
				portlet.getRootPortletId()));
	}

	@Override
	public PortletDataHandlerControl[] getImportConfigurationControls(
		String[] configurationPortletOptions) {

		List<PortletDataHandlerBoolean> configurationControls =
			new ArrayList<>();

		// Setup

		if (ArrayUtil.contains(configurationPortletOptions, "setup")) {
			PortletDataHandlerControl[] portletDataHandlerControls = null;

			if (isDisplayPortlet()) {
				portletDataHandlerControls = getExportControls();
			}

			configurationControls.add(
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_SETUP, "setup", true,
					false, portletDataHandlerControls, null, null));
		}

		// Archived setups

		if (ArrayUtil.contains(
				configurationPortletOptions, "archived-setups")) {

			configurationControls.add(
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS,
					"configuration-templates", true, false, null, null, null));
		}

		// User preferences

		if (ArrayUtil.contains(
				configurationPortletOptions, "user-preferences")) {

			configurationControls.add(
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_USER_PREFERENCES,
					"user-preferences", true, false, null, null, null));
		}

		return configurationControls.toArray(new PortletDataHandlerBoolean[0]);
	}

	@Override
	public PortletDataHandlerControl[] getImportControls() {
		return _importControls;
	}

	@Override
	public PortletDataHandlerControl[] getImportMetadataControls() {
		return _importMetadataControls;
	}

	@Override
	public String getPortletId() {
		return _portletId;
	}

	@Override
	public int getRank() {
		return _rank;
	}

	@Override
	public String getResourceName() {
		return null;
	}

	@Override
	public String getSchemaVersion() {
		return "1.0.0";
	}

	@Override
	public String getServiceName() {
		return null;
	}

	@Override
	public PortletDataHandlerControl[] getStagingControls() {
		return _stagingControls;
	}

	@Override
	public PortletPreferences importData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(portletDataContext))) {
			return null;
		}

		long startTime = 0;

		if (_log.isInfoEnabled()) {
			_log.info("Importing portlet " + portletId);

			startTime = System.currentTimeMillis();
		}

		long sourceGroupId = portletDataContext.getSourceGroupId();

		Element rootElement = null;

		try {
			if (Validator.isXml(data)) {
				rootElement = portletDataContext.getImportDataRootElement();

				addImportDataRootElement(portletDataContext, data);
			}

			portletDataContext.setImportDataElementCacheEnabled(true);

			return doImportData(
				portletDataContext, portletId, portletPreferences, data);
		}
		catch (Exception exception) {
			throw _handleException(
				exception, PortletDataException.IMPORT_PORTLET_DATA, portletId);
		}
		finally {
			portletDataContext.setImportDataElementCacheEnabled(false);
			portletDataContext.setImportDataRootElement(rootElement);
			portletDataContext.setSourceGroupId(sourceGroupId);

			if (_log.isInfoEnabled()) {
				long duration = System.currentTimeMillis() - startTime;

				_log.info("Imported portlet in " + Time.getDuration(duration));
			}
		}
	}

	@Override
	public boolean isDataAlwaysStaged() {
		return _dataAlwaysStaged;
	}

	@Override
	public boolean isDataLocalized() {
		return _dataLocalized;
	}

	@Override
	public boolean isDataPortalLevel() {
		return _dataLevel.equals(DataLevel.PORTAL);
	}

	@Override
	public boolean isDataPortletInstanceLevel() {
		return _dataLevel.equals(DataLevel.PORTLET_INSTANCE);
	}

	@Override
	public boolean isDataSiteLevel() {
		return _dataLevel.equals(DataLevel.SITE);
	}

	@Override
	public boolean isDisplayPortlet() {
		if (isDataPortletInstanceLevel() &&
			ArrayUtil.isNotEmpty(getDataPortletPreferences())) {

			return true;
		}

		return false;
	}

	public boolean isEmptyControlsAllowed() {
		return _emptyControlsAllowed;
	}

	@Override
	public boolean isModelCountSupported() {
		return true;
	}

	@Override
	public boolean isPublishToLiveByDefault() {
		return _publishToLiveByDefault;
	}

	@Override
	public boolean isRollbackOnException() {

		// For now, we are going to throw an exception if one portlet data
		// handler has an exception to ensure that the transaction is rolled
		// back for data integrity. We may decide that this is not the best
		// behavior in the future because a bad plugin could prevent deletion of
		// groups.

		return true;
	}

	@Override
	public void prepareManifestSummary(PortletDataContext portletDataContext)
		throws PortletDataException {

		prepareManifestSummary(portletDataContext, null);
	}

	@Override
	public void prepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!isEnabled(_getCompanyId(portletDataContext))) {
			return;
		}

		try {
			doPrepareManifestSummary(portletDataContext, portletPreferences);
		}
		catch (Exception exception) {
			throw _handleException(
				exception, PortletDataException.PREPARE_MANIFEST_SUMMARY,
				portletDataContext.getPortletId());
		}
	}

	@Override
	public void setPortletId(String portletId) {
		_portletId = portletId;
	}

	@Override
	public void setRank(int rank) {
		_rank = rank;
	}

	@Override
	public boolean validateSchemaVersion(String schemaVersion) {
		if (!isEnabled(CompanyThreadLocal.getCompanyId())) {
			return true;
		}

		try {
			return doValidateSchemaVersion(schemaVersion);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	protected Element addExportDataRootElement(
		PortletDataContext portletDataContext) {

		Document document = SAXReaderUtil.createDocument();

		Class<?> clazz = getClass();

		Element rootElement = document.addElement(clazz.getSimpleName());

		rootElement.addAttribute(
			"self-path",
			ExportImportPathUtil.getPortletDataPath(portletDataContext));

		portletDataContext.setExportDataRootElement(rootElement);

		return rootElement;
	}

	protected Element addImportDataRootElement(
			PortletDataContext portletDataContext, String data)
		throws DocumentException {

		Document document = SAXReaderUtil.read(data);

		Element rootElement = document.getRootElement();

		portletDataContext.setImportDataRootElement(rootElement);

		long groupId = GetterUtil.getLong(
			rootElement.attributeValue("group-id"));

		if (groupId != 0) {
			portletDataContext.setSourceGroupId(groupId);
		}

		return rootElement;
	}

	protected void addUncheckedModelAdditionCount(
		PortletDataContext portletDataContext,
		PortletDataHandlerControl portletDataHandlerControl) {

		if (!(portletDataHandlerControl instanceof PortletDataHandlerBoolean)) {
			return;
		}

		PortletDataHandlerBoolean portletDataHandlerBoolean =
			(PortletDataHandlerBoolean)portletDataHandlerControl;

		PortletDataHandlerControl[] childPortletDataHandlerControls =
			portletDataHandlerBoolean.getChildren();

		if (childPortletDataHandlerControls != null) {
			for (PortletDataHandlerControl childPortletDataHandlerControl :
					childPortletDataHandlerControls) {

				addUncheckedModelAdditionCount(
					portletDataContext, childPortletDataHandlerControl);
			}
		}

		if (Validator.isNull(portletDataHandlerControl.getClassName())) {
			return;
		}

		boolean checkedControl = GetterUtil.getBoolean(
			portletDataContext.getBooleanParameter(
				portletDataHandlerControl.getNamespace(),
				portletDataHandlerControl.getControlName(), false));

		if (!checkedControl) {
			ManifestSummary manifestSummary =
				portletDataContext.getManifestSummary();

			StagedModelType stagedModelType = new StagedModelType(
				portletDataHandlerControl.getClassName(),
				portletDataHandlerBoolean.getReferrerClassName());

			manifestSummary.addModelAdditionCount(
				ManifestSummary.getManifestSummaryKey(stagedModelType), 0);
		}
	}

	protected PortletPreferences doAddDefaultData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		return portletPreferences;
	}

	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		return portletPreferences;
	}

	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		return null;
	}

	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		return null;
	}

	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {
	}

	protected boolean doValidateSchemaVersion(String schemaVersion)
		throws Exception {

		// Major version has to be identical

		Version currentVersion = Version.getInstance(getSchemaVersion());
		Version importedVersion = Version.getInstance(schemaVersion);

		if (!Objects.equals(
				currentVersion.getMajor(), importedVersion.getMajor())) {

			return false;
		}

		// Imported minor version should be less than or equal to the current
		// minor version

		int currentMinorVersion = GetterUtil.getInteger(
			currentVersion.getMinor(), -1);
		int importedMinorVersion = GetterUtil.getInteger(
			importedVersion.getMinor(), -1);

		if (((currentMinorVersion == -1) && (importedMinorVersion == -1)) ||
			(currentMinorVersion < importedMinorVersion)) {

			return false;
		}

		// Import should be compatible with all minor versions if previous
		// validations pass

		return true;
	}

	protected String getExportDataRootElementString(Element rootElement) {
		if (rootElement == null) {
			return StringPool.BLANK;
		}

		try {
			Document document = rootElement.getDocument();

			return document.formattedString();
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return StringPool.BLANK;
		}
	}

	protected long getExportModelCount(
		ManifestSummary manifestSummary,
		PortletDataHandlerControl[] portletDataHandlerControls) {

		long totalModelCount = -1;

		for (PortletDataHandlerControl portletDataHandlerControl :
				portletDataHandlerControls) {

			StagedModelType stagedModelType = new StagedModelType(
				portletDataHandlerControl.getClassName(),
				portletDataHandlerControl.getReferrerClassName());

			long modelAdditionCount = manifestSummary.getModelAdditionCount(
				stagedModelType);

			if (portletDataHandlerControl instanceof
					PortletDataHandlerBoolean) {

				PortletDataHandlerBoolean portletDataHandlerBoolean =
					(PortletDataHandlerBoolean)portletDataHandlerControl;

				PortletDataHandlerControl[] childPortletDataHandlerControls =
					portletDataHandlerBoolean.getChildren();

				if (childPortletDataHandlerControls != null) {
					long childModelCount = getExportModelCount(
						manifestSummary, childPortletDataHandlerControls);

					if (childModelCount != -1) {
						if (modelAdditionCount == -1) {
							modelAdditionCount = childModelCount;
						}
						else {
							modelAdditionCount += childModelCount;
						}
					}
				}
			}

			if (modelAdditionCount == -1) {
				continue;
			}

			if (totalModelCount == -1) {
				totalModelCount = modelAdditionCount;
			}
			else {
				totalModelCount += modelAdditionCount;
			}
		}

		return totalModelCount;
	}

	protected StagedModelType[] getStagedModelTypes(
		Supplier<List<StagedModelType>> stagedModelTypesSupplier) {

		return _stagedModelTypesMap.computeIfAbsent(
			DBPartition.isPartitionEnabled() ?
				CompanyThreadLocal.getCompanyId() : CompanyConstants.SYSTEM,
			companyId -> {
				List<StagedModelType> stagedModelTypes =
					stagedModelTypesSupplier.get();

				return stagedModelTypes.toArray(new StagedModelType[0]);
			});
	}

	protected void setDataAlwaysStaged(boolean dataAlwaysStaged) {
		_dataAlwaysStaged = dataAlwaysStaged;
	}

	protected void setDataLevel(DataLevel dataLevel) {
		_dataLevel = dataLevel;
	}

	protected void setDataLocalized(boolean dataLocalized) {
		_dataLocalized = dataLocalized;
	}

	protected void setDataPortletPreferences(String... dataPortletPreferences) {
		_dataPortletPreferences = dataPortletPreferences;
	}

	protected void setDeletionSystemEventStagedModelTypes(
		StagedModelType... deletionSystemEventStagedModelTypes) {

		_deletionSystemEventStagedModelTypes =
			deletionSystemEventStagedModelTypes;
	}

	protected void setEmptyControlsAllowed(boolean emptyControlsAllowed) {
		_emptyControlsAllowed = emptyControlsAllowed;
	}

	protected void setExportControls(
		PortletDataHandlerControl... exportControls) {

		_exportControls = exportControls;

		setImportControls(exportControls);
	}

	protected void setExportMetadataControls(
		PortletDataHandlerControl... exportMetadataControls) {

		_exportMetadataControls = exportMetadataControls;

		setImportMetadataControls(exportMetadataControls);
	}

	protected void setImportControls(
		PortletDataHandlerControl... importControls) {

		_importControls = importControls;
	}

	protected void setImportMetadataControls(
		PortletDataHandlerControl... importMetadataControls) {

		_importMetadataControls = importMetadataControls;
	}

	protected void setPublishToLiveByDefault(boolean publishToLiveByDefault) {
		_publishToLiveByDefault = publishToLiveByDefault;
	}

	protected void setStagingControls(
		PortletDataHandlerControl... stagingControls) {

		_stagingControls = stagingControls;
	}

	private long _getCompanyId(PortletDataContext portletDataContext) {
		if (portletDataContext != null) {
			return portletDataContext.getCompanyId();
		}

		return CompanyThreadLocal.getCompanyId();
	}

	private PortletDataException _handleException(
		Exception exception, int type, String portletId) {

		PortletDataException portletDataException = null;

		if (exception instanceof PortletDataException) {
			portletDataException = (PortletDataException)exception;
		}
		else {
			portletDataException = new PortletDataException(
				exception.getMessage(), exception);
		}

		if (Validator.isNull(portletDataException.getPortletId())) {
			portletDataException.setPortletId(portletId);
		}

		if (portletDataException.getType() == PortletDataException.DEFAULT) {
			portletDataException.setType(type);
		}

		return portletDataException;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasePortletDataHandler.class);

	private boolean _dataAlwaysStaged;
	private DataLevel _dataLevel = DataLevel.SITE;
	private boolean _dataLocalized;
	private String[] _dataPortletPreferences = StringPool.EMPTY_ARRAY;
	private StagedModelType[] _deletionSystemEventStagedModelTypes =
		new StagedModelType[0];
	private boolean _emptyControlsAllowed;
	private PortletDataHandlerControl[] _exportControls =
		new PortletDataHandlerControl[0];
	private PortletDataHandlerControl[] _exportMetadataControls =
		new PortletDataHandlerControl[0];
	private PortletDataHandlerControl[] _importControls =
		new PortletDataHandlerControl[0];
	private PortletDataHandlerControl[] _importMetadataControls =
		new PortletDataHandlerControl[0];
	private String _portletId;
	private boolean _publishToLiveByDefault;
	private int _rank = 100;
	private final Map<Long, StagedModelType[]> _stagedModelTypesMap =
		new ConcurrentHashMap<>();
	private PortletDataHandlerControl[] _stagingControls =
		new PortletDataHandlerControl[0];

}
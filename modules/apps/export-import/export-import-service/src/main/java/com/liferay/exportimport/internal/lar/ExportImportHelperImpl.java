/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.exportimport.constants.ExportImportBackgroundTaskContextMapConstants;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.DefaultConfigurationPortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.MissingReference;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.portlet.data.handler.util.ExportImportGroupedModelUtil;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorRegistryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.ElementHandler;
import com.liferay.portal.kernel.xml.ElementProcessor;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.staging.configuration.StagingConfiguration;

import jakarta.portlet.PortletRequest;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author Zsolt Berentey
 * @author Levente Hudák
 * @author Julio Camarero
 * @author Máté Thurzó
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "com.liferay.staging.configuration.StagingConfiguration",
	service = ExportImportHelper.class
)
public class ExportImportHelperImpl implements ExportImportHelper {

	@Override
	public long[] getAllLayoutIds(long groupId, boolean privateLayout) {
		return getLayoutIds(
			_layoutLocalService.getLayouts(groupId, privateLayout));
	}

	@Override
	public Map<Long, Boolean> getAllLayoutIdsMap(
		long groupId, boolean privateLayout) {

		Map<Long, Boolean> layoutIdMap = new HashMap<>();

		for (Layout layout :
				_layoutLocalService.getLayouts(
					groupId, privateLayout,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)) {

			layoutIdMap.put(layout.getPlid(), true);
		}

		return layoutIdMap;
	}

	@Override
	public List<Portlet> getDataSiteAndInstanceLevelPortlets(long companyId)
		throws Exception {

		return getDataSiteAndInstanceLevelPortlets(companyId, false);
	}

	@Override
	public List<Portlet> getDataSiteAndInstanceLevelPortlets(
			long companyId, boolean excludeDataAlwaysStaged)
		throws Exception {

		return _getPortlets(
			companyId,
			new DataLevel[] {DataLevel.PORTLET_INSTANCE, DataLevel.SITE},
			excludeDataAlwaysStaged);
	}

	@Override
	public List<Portlet> getDataSiteLevelPortlets(long companyId)
		throws Exception {

		return getDataSiteLevelPortlets(companyId, false);
	}

	@Override
	public List<Portlet> getDataSiteLevelPortlets(
			long companyId, boolean excludeDataAlwaysStaged)
		throws Exception {

		return _getPortlets(
			companyId, new DataLevel[] {DataLevel.SITE},
			excludeDataAlwaysStaged);
	}

	@Override
	public List<Portlet> getExportablePortlets(
		long companyId, boolean excludeDataAlwaysStaged, long groupId) {

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (stagingGroupHelper.isCompanyGroup(companyId, groupId)) {
			return _getPortlets(
				companyId, new DataLevel[] {DataLevel.PORTAL},
				excludeDataAlwaysStaged);
		}

		if (stagingGroupHelper.isDepotGroup(groupId)) {
			return ListUtil.filter(
				_getPortlets(
					companyId,
					new DataLevel[] {DataLevel.DEPOT, DataLevel.SITE},
					excludeDataAlwaysStaged),
				portlet -> {
					PortletDataHandler portletDataHandler =
						portlet.getPortletDataHandlerInstance();

					return portletDataHandler.isDataDepotLevel() ||
						   (portletDataHandler.isDataSiteLevel() &&
							!portletDataHandler.isBatch());
				});
		}

		return _getPortlets(
			companyId, new DataLevel[] {DataLevel.SITE},
			excludeDataAlwaysStaged);
	}

	@Override
	public String getExportableRootPortletId(long companyId, String portletId)
		throws Exception {

		Portlet portlet = _portletLocalService.getPortletById(
			companyId, portletId);

		if ((portlet == null) || portlet.isUndeployedPortlet()) {
			return null;
		}

		return PortletIdCodec.decodePortletName(portletId);
	}

	@Override
	public Map<String, Boolean> getExportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap)
		throws Exception {

		return getExportPortletControlsMap(
			companyId, portletId, parameterMap, "layout-set");
	}

	@Override
	public Map<String, Boolean> getExportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, String type)
		throws Exception {

		return HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			_getExportPortletData(companyId, portletId, parameterMap)
		).putAll(
			_getExportPortletSetupControlsMap(
				companyId, portletId, parameterMap, type)
		).build();
	}

	@Override
	public Map<String, Boolean> getImportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, Element portletDataElement,
			ManifestSummary manifestSummary)
		throws Exception {

		return HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			_isImportPortletData(
				companyId, portletId, parameterMap, portletDataElement)
		).putAll(
			_getImportPortletSetupControlsMap(
				companyId, portletId, parameterMap, manifestSummary)
		).build();
	}

	@Override
	public Map<Long, Boolean> getLayoutIdMap(PortletRequest portletRequest)
		throws PortalException {

		String layoutIdsJSON = GetterUtil.getString(
			portletRequest.getAttribute("layoutIdMap"));

		if (Validator.isNull(layoutIdsJSON)) {
			return Collections.emptyMap();
		}

		Map<Long, Boolean> layoutIdMap = new LinkedHashMap<>();

		JSONArray jsonArray = _jsonFactory.createJSONArray(layoutIdsJSON);

		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			layoutIdMap.put(
				jsonObject.getLong("plid"),
				jsonObject.getBoolean("includeChildren"));
		}

		return layoutIdMap;
	}

	@Override
	public long[] getLayoutIds(List<Layout> layouts) {
		return TransformUtil.transformToLongArray(layouts, Layout::getLayoutId);
	}

	@Override
	public long[] getLayoutIds(Map<Long, Boolean> layoutIdMap)
		throws PortalException {

		return getLayoutIds(layoutIdMap, GroupConstants.DEFAULT_LIVE_GROUP_ID);
	}

	@Override
	public long[] getLayoutIds(
			Map<Long, Boolean> layoutIdMap, long targetGroupId)
		throws PortalException {

		if (MapUtil.isEmpty(layoutIdMap)) {
			return new long[0];
		}

		List<Layout> layouts = new ArrayList<>();

		Set<Map.Entry<Long, Boolean>> entries = layoutIdMap.entrySet();

		for (Map.Entry<Long, Boolean> entry : entries) {
			long plid = GetterUtil.getLong(String.valueOf(entry.getKey()));

			Layout layout = null;

			try {
				layout = getLayoutOrCreateDummyRootLayout(plid);
			}
			catch (NoSuchLayoutException noSuchLayoutException) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to publish deleted layout " + plid);
				}

				// See LPS-36174

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchLayoutException);
				}

				entries.remove(plid);

				continue;
			}

			if (!layouts.contains(layout)) {
				layouts.add(layout);
			}

			if (layout.getPlid() == LayoutConstants.DEFAULT_PLID) {
				continue;
			}

			List<Layout> parentLayouts = Collections.emptyList();

			if (targetGroupId != GroupConstants.DEFAULT_LIVE_GROUP_ID) {
				parentLayouts = getMissingParentLayouts(layout, targetGroupId);
			}

			if (FeatureFlagManagerUtil.isEnabled("LPS-199086")) {
				try {
					StagingConfiguration stagingConfiguration =
						_configurationProvider.getCompanyConfiguration(
							StagingConfiguration.class,
							CompanyThreadLocal.getCompanyId());

					if (stagingConfiguration.publishParentLayoutsByDefault()) {
						for (Layout parentLayout : parentLayouts) {
							if (!layouts.contains(parentLayout)) {
								layouts.add(parentLayout);
							}
						}
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
			else {
				for (Layout parentLayout : parentLayouts) {
					if (!layouts.contains(parentLayout)) {
						layouts.add(parentLayout);
					}
				}
			}

			boolean includeChildren = entry.getValue();

			if (!includeChildren) {
				continue;
			}

			for (Layout childLayout : layout.getAllChildren()) {
				if (!layouts.contains(childLayout)) {
					layouts.add(childLayout);
				}
			}
		}

		return getLayoutIds(layouts);
	}

	@Override
	public long[] getLayoutIds(PortletRequest portletRequest)
		throws PortalException {

		return getLayoutIds(
			getLayoutIdMap(portletRequest),
			GroupConstants.DEFAULT_LIVE_GROUP_ID);
	}

	@Override
	public long[] getLayoutIds(
			PortletRequest portletRequest, long targetGroupId)
		throws PortalException {

		return getLayoutIds(getLayoutIdMap(portletRequest), targetGroupId);
	}

	@Override
	public long getLayoutModelDeletionCount(
			PortletDataContext portletDataContext, boolean privateLayout)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_systemEventLocalService.getActionableDynamicQuery();

		StagedModelType stagedModelType = new StagedModelType(Layout.class);

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				doAddCriteria(
					portletDataContext, stagedModelType, dynamicQuery);

				Property extraDataProperty = PropertyFactoryUtil.forName(
					"extraData");

				dynamicQuery.add(
					extraDataProperty.like(
						"%\"privateLayout\":\"" + privateLayout + "\"%"));
			});

		actionableDynamicQuery.setCompanyId(portletDataContext.getCompanyId());

		return actionableDynamicQuery.performCount();
	}

	@Override
	public Layout getLayoutOrCreateDummyRootLayout(long plid)
		throws PortalException {

		Layout layout = new LayoutImpl();

		if (plid == 0) {
			layout.setPlid(LayoutConstants.DEFAULT_PLID);
			layout.setLayoutId(LayoutConstants.DEFAULT_PLID);
			layout.setParentLayoutId(LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);
		}
		else {
			layout = _layoutLocalService.getLayout(plid);
		}

		return layout;
	}

	@Override
	public ZipWriter getLayoutSetZipWriter(long groupId) {
		String fileName = _getZipWriterFileName(String.valueOf(groupId));

		return getZipWriter(fileName);
	}

	@Override
	public ManifestSummary getManifestSummary(
			long userId, long groupId, Map<String, String[]> parameterMap,
			FileEntry fileEntry)
		throws Exception {

		ManifestSummary manifestSummary = null;

		File file = FileUtil.createTempFile("lar");

		ZipReader zipReader = null;

		try (InputStream inputStream = _dlFileEntryLocalService.getFileAsStream(
				fileEntry.getFileEntryId(), fileEntry.getVersion(), false)) {

			FileUtil.write(file, inputStream);

			Group group = _groupLocalService.getGroup(groupId);
			String userIdStrategy = MapUtil.getString(
				parameterMap, PortletDataHandlerKeys.USER_ID_STRATEGY);

			zipReader = _zipReaderFactory.getZipReader(file);

			PortletDataContext portletDataContext =
				_portletDataContextFactory.createImportPortletDataContext(
					group.getCompanyId(), groupId, parameterMap,
					getUserIdStrategy(userId, userIdStrategy), zipReader);

			manifestSummary = getManifestSummary(portletDataContext);
		}
		finally {
			if (zipReader != null) {
				zipReader.close();
			}

			FileUtil.delete(file);
		}

		return manifestSummary;
	}

	@Override
	public ManifestSummary getManifestSummary(
			PortletDataContext portletDataContext)
		throws Exception {

		ManifestSummary manifestSummary = new ManifestSummary();

		XMLReader xmlReader = SecureXMLFactoryProviderUtil.newXMLReader();

		Group group = _groupLocalService.getGroup(
			portletDataContext.getGroupId());

		ElementHandler elementHandler = new ElementHandler(
			new ManifestSummaryElementProcessor(group, manifestSummary),
			new String[] {"header", "portlet", "staged-model"});

		xmlReader.setContentHandler(elementHandler);

		xmlReader.parse(
			new InputSource(
				portletDataContext.getZipEntryAsInputStream("/manifest.xml")));

		return manifestSummary;
	}

	/**
	 * @see com.liferay.exportimport.kernel.backgroundtask.LayoutRemoteStagingBackgroundTaskExecutor#getMissingRemoteParentLayouts(
	 *      HttpPrincipal, Layout, long)
	 */
	@Override
	public List<Layout> getMissingParentLayouts(Layout layout, long liveGroupId)
		throws PortalException {

		List<Layout> missingParentLayouts = new ArrayList<>();

		long parentLayoutId = layout.getParentLayoutId();

		Layout parentLayout = null;

		while (parentLayoutId > 0) {
			parentLayout = _layoutLocalService.getLayout(
				layout.getGroupId(), layout.isPrivateLayout(), parentLayoutId);

			if (_layoutLocalService.hasLayout(
					parentLayout.getUuid(), liveGroupId,
					parentLayout.isPrivateLayout())) {

				// If one parent is found, all others are assumed to exist

				break;
			}

			missingParentLayouts.add(parentLayout);

			parentLayoutId = parentLayout.getParentLayoutId();
		}

		return missingParentLayouts;
	}

	@Override
	public long getModelDeletionCount(
			PortletDataContext portletDataContext,
			StagedModelType stagedModelType)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_systemEventLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> doAddCriteria(
				portletDataContext, stagedModelType, dynamicQuery));
		actionableDynamicQuery.setCompanyId(portletDataContext.getCompanyId());

		return actionableDynamicQuery.performCount();
	}

	@Override
	public String getPortletExportFileName(Portlet portlet) {
		return StringBundler.concat(
			StringUtil.replace(portlet.getDisplayName(), ' ', '_'), "-",
			Time.getShortTimestamp(), ".portlet.lar");
	}

	@Override
	public ZipWriter getPortletZipWriter(String portletId) {
		String fileName = _getZipWriterFileName(portletId);

		return getZipWriter(fileName);
	}

	@Override
	public String getSelectedLayoutsJSON(
		long groupId, boolean privateLayout, String selectedNodes) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		long[] selectedLayoutIds = StringUtil.split(selectedNodes, 0L);

		for (Layout layout :
				_layoutLocalService.getLayouts(
					groupId, privateLayout,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)) {

			_populateLayoutsJSON(jsonArray, layout, selectedLayoutIds);
		}

		if (ArrayUtil.contains(selectedLayoutIds, 0)) {
			jsonArray.put(
				JSONUtil.put(
					"includeChildren", true
				).put(
					"plid", 0
				));
		}

		return jsonArray.toString();
	}

	@Override
	public FileEntry getTempFileEntry(
			long groupId, long userId, String folderName)
		throws PortalException {

		String[] tempFileNames = _layoutService.getTempFileNames(
			groupId, folderName);

		if (tempFileNames.length == 0) {
			return null;
		}

		return TempFileEntryUtil.getTempFileEntry(
			groupId, userId,
			DigesterUtil.digestHex(DigesterUtil.SHA_256, folderName),
			tempFileNames[0]);
	}

	@Override
	public UserIdStrategy getUserIdStrategy(long userId, String userIdStrategy)
		throws PortalException {

		User user = _userLocalService.getUserById(userId);

		if (UserIdStrategy.ALWAYS_CURRENT_USER_ID.equals(userIdStrategy)) {
			return new AlwaysCurrentUserIdStrategy(user);
		}

		return new CurrentUserIdStrategy(user);
	}

	@Override
	public boolean isAlwaysIncludeReference(
		PortletDataContext portletDataContext,
		StagedModel referenceStagedModel) {

		String rootPortletId = portletDataContext.getRootPortletId();

		if (Validator.isBlank(rootPortletId)) {
			return true;
		}

		return isAlwaysIncludeReference(
			portletDataContext, referenceStagedModel, rootPortletId);
	}

	@Override
	public boolean isAlwaysIncludeReference(
		PortletDataContext portletDataContext, StagedModel referenceStagedModel,
		String rootPortletId) {

		Portlet portlet = _portletLocalService.getPortletById(rootPortletId);

		if (portlet == null) {
			return true;
		}

		PortletDataHandler portletDataHandler =
			portlet.getPortletDataHandlerInstance();

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		String[] referencedContentBehaviorArray = parameterMap.get(
			PortletDataHandlerControl.getNamespacedControlName(
				portletDataHandler.getNamespace(),
				"referenced-content-behavior"));

		String referencedContentBehavior = "include-always";

		if (ArrayUtil.isNotEmpty(referencedContentBehaviorArray)) {
			referencedContentBehavior = referencedContentBehaviorArray[0];
		}

		if (referencedContentBehavior.equals("include-always") ||
			(referencedContentBehavior.equals("include-if-modified") &&
			 portletDataContext.isWithinDateRange(
				 referenceStagedModel.getModifiedDate()))) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isExportPortletData(PortletDataContext portletDataContext) {
		if (((portletDataContext.getScopeGroupId() ==
				portletDataContext.getGroupId()) ||
			 (portletDataContext.getScopeGroupId() ==
				 portletDataContext.getCompanyGroupId())) &&
			(ExportImportThreadLocal.isLayoutExportInProcess() ||
			 ExportImportThreadLocal.isLayoutStagingInProcess())) {

			return false;
		}

		if (ExportImportThreadLocal.isLayoutStagingInProcess() &&
			!_isStagedPortlet(portletDataContext)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isLayoutRevisionInReview(Layout layout) {
		if (ListUtil.exists(
				_layoutRevisionLocalService.getLayoutRevisions(
					layout.getPlid()),
				layoutRevision ->
					WorkflowConstants.STATUS_PENDING ==
						layoutRevision.getStatus())) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isPublishDisplayedContent(
		PortletDataContext portletDataContext, Portlet portlet) {

		try {
			if (!ExportImportThreadLocal.isStagingInProcess()) {
				return true;
			}

			_stagingConfiguration =
				_configurationProvider.getCompanyConfiguration(
					StagingConfiguration.class,
					portletDataContext.getCompanyId());

			if (_stagingConfiguration.publishDisplayedContent()) {
				return true;
			}

			ExportImportPortletPreferencesProcessor
				exportImportPortletPreferencesProcessor =
					ExportImportPortletPreferencesProcessorRegistryUtil.
						getExportImportPortletPreferencesProcessor(
							portlet.getRootPortletId());

			if (exportImportPortletPreferencesProcessor != null) {
				return exportImportPortletPreferencesProcessor.
					isPublishDisplayedContent();
			}

			return true;
		}
		catch (Exception exception) {
			_log.error(exception);

			return true;
		}
	}

	@Override
	public boolean isReferenceWithinExportScope(
		PortletDataContext portletDataContext, StagedModel stagedModel) {

		if (!(stagedModel instanceof GroupedModel)) {
			return true;
		}

		GroupedModel groupedModel = (GroupedModel)stagedModel;

		if (portletDataContext.getGroupId() == groupedModel.getGroupId()) {
			return true;
		}

		return ExportImportGroupedModelUtil.
			isReferenceInLayoutGroupWithinExportScope(
				portletDataContext, groupedModel);
	}

	@Override
	public void processBackgroundTaskManifestSummary(
			long userId, long sourceGroupId, BackgroundTask backgroundTask,
			File file)
		throws PortalException {

		FileEntry fileEntry = null;

		try {
			fileEntry = TempFileEntryUtil.addTempFileEntry(
				sourceGroupId, userId, ExportImportHelper.TEMP_FOLDER_NAME,
				file.getName(), file, MimeTypesUtil.getContentType(file));

			ManifestSummary manifestSummary = getManifestSummary(
				userId, sourceGroupId, new HashMap<>(), fileEntry);

			Map<String, Serializable> taskContextMap =
				backgroundTask.getTaskContextMap();

			taskContextMap.put(
				ExportImportBackgroundTaskContextMapConstants.ASSET_TITLES,
				new HashMap<>(manifestSummary.getStagedModelAssetTitles()));
			taskContextMap.put(
				ExportImportBackgroundTaskContextMapConstants.
					MODEL_ADDITION_COUNTERS,
				new HashMap<>(manifestSummary.getModelAdditionCounters()));
			taskContextMap.put(
				ExportImportBackgroundTaskContextMapConstants.
					MODEL_DELETION_COUNTERS,
				new HashMap<>(manifestSummary.getModelDeletionCounters()));
			taskContextMap.put(
				ExportImportBackgroundTaskContextMapConstants.
					MANIFEST_SUMMARY_KEYS,
				new HashSet<>(manifestSummary.getManifestSummaryKeys()));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to process manifest for the process summary screen",
					exception);
			}
		}
		finally {
			if (fileEntry != null) {
				TempFileEntryUtil.deleteTempFileEntry(
					fileEntry.getFileEntryId());
			}
		}
	}

	@Override
	public void setPortletScope(
		PortletDataContext portletDataContext, Element portletElement) {

		// Portlet data scope

		String scopeLayoutUuid = GetterUtil.getString(
			portletElement.attributeValue("scope-layout-uuid"));
		String scopeLayoutType = GetterUtil.getString(
			portletElement.attributeValue("scope-layout-type"));

		portletDataContext.setScopeLayoutUuid(scopeLayoutUuid);
		portletDataContext.setScopeType(scopeLayoutType);

		// Layout scope

		try {
			Group scopeGroup = null;

			if (scopeLayoutType.equals("company")) {
				scopeGroup = _groupLocalService.getCompanyGroup(
					portletDataContext.getCompanyId());
			}
			else if (Validator.isNotNull(scopeLayoutUuid)) {
				Layout scopeLayout =
					_layoutLocalService.getLayoutByUuidAndGroupId(
						scopeLayoutUuid, portletDataContext.getGroupId(),
						portletDataContext.isPrivateLayout());

				scopeGroup = _groupLocalService.checkScopeGroup(
					scopeLayout, portletDataContext.getUserId(null));

				Group group = scopeLayout.getGroup();

				if (group.isStaged() && !group.isStagedRemotely()) {
					try {
						boolean privateLayout = GetterUtil.getBoolean(
							portletElement.attributeValue("private-layout"));

						Layout oldLayout =
							_layoutLocalService.getLayoutByUuidAndGroupId(
								scopeLayoutUuid,
								portletDataContext.getSourceGroupId(),
								privateLayout);

						Group oldScopeGroup = oldLayout.getScopeGroup();

						if (group.isStagingGroup()) {
							scopeGroup.setLiveGroupId(
								oldScopeGroup.getGroupId());

							scopeGroup = _groupLocalService.updateGroup(
								scopeGroup);
						}
						else {
							oldScopeGroup.setLiveGroupId(
								scopeGroup.getGroupId());

							_groupLocalService.updateGroup(oldScopeGroup);
						}
					}
					catch (NoSuchLayoutException noSuchLayoutException) {
						if (_log.isWarnEnabled()) {
							_log.warn(noSuchLayoutException);
						}
					}
				}

				if (!ExportImportThreadLocal.isStagingInProcess() &&
					group.isStagingGroup() &&
					!group.isStagedPortlet(portletDataContext.getPortletId())) {

					scopeGroup = group.getLiveGroup();

					Layout scopeLiveLayout =
						_layoutLocalService.fetchLayoutByUuidAndGroupId(
							scopeLayoutUuid, group.getLiveGroupId(),
							portletDataContext.isPrivateLayout());

					if (scopeLiveLayout != null) {
						scopeGroup = _groupLocalService.checkScopeGroup(
							scopeLiveLayout,
							portletDataContext.getUserId(null));
					}
				}
			}
			else {
				Group group = _groupLocalService.getGroup(
					portletDataContext.getGroupId());

				if (!ExportImportThreadLocal.isStagingInProcess() &&
					group.isStagingGroup()) {

					if (group.isStagedPortlet(
							portletDataContext.getPortletId())) {

						scopeGroup = group;
					}
					else {
						scopeGroup = group.getLiveGroup();
					}
				}
			}

			if (scopeGroup != null) {
				portletDataContext.setScopeGroupId(scopeGroup.getGroupId());

				Map<Long, Long> groupIds =
					(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
						Group.class);

				long oldScopeGroupId = GetterUtil.getLong(
					portletElement.attributeValue("scope-group-id"));

				groupIds.put(oldScopeGroupId, scopeGroup.getGroupId());
			}
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public MissingReferences validateMissingReferences(
			final PortletDataContext portletDataContext)
		throws Exception {

		final MissingReferences missingReferences = new MissingReferences();

		XMLReader xmlReader = SecureXMLFactoryProviderUtil.newXMLReader();

		ElementHandler elementHandler = new ElementHandler(
			new ElementProcessor() {

				@Override
				public void processElement(Element element) {
					MissingReference missingReference =
						_validateMissingReference(portletDataContext, element);

					if (missingReference != null) {
						missingReferences.add(missingReference);
					}
				}

			},
			new String[] {"missing-reference"});

		xmlReader.setContentHandler(elementHandler);

		xmlReader.parse(
			new InputSource(
				portletDataContext.getZipEntryAsInputStream("/manifest.xml")));

		return missingReferences;
	}

	@Override
	public void writeManifestSummary(
		Document document, ManifestSummary manifestSummary) {

		Element rootElement = document.getRootElement();

		Element manifestSummaryElement = rootElement.addElement(
			"manifest-summary");

		for (String manifestSummaryKey :
				manifestSummary.getManifestSummaryKeys()) {

			Element element = manifestSummaryElement.addElement("staged-model");

			element.addAttribute("manifest-summary-key", manifestSummaryKey);

			long modelAdditionCount = manifestSummary.getModelAdditionCount(
				manifestSummaryKey);

			if (modelAdditionCount > 0) {
				element.addAttribute(
					"addition-count", String.valueOf(modelAdditionCount));
			}

			String stagedModelAssetTitle =
				manifestSummary.getStagedModelAssetTitle(manifestSummaryKey);

			if (Validator.isNotNull(stagedModelAssetTitle)) {
				element.addAttribute("asset-title", stagedModelAssetTitle);
			}

			long modelDeletionCount = manifestSummary.getModelDeletionCount(
				manifestSummaryKey);

			if (modelDeletionCount > 0) {
				element.addAttribute(
					"deletion-count", String.valueOf(modelDeletionCount));
			}
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_stagingConfiguration = ConfigurableUtil.createConfigurable(
			StagingConfiguration.class, properties);
	}

	protected void addCreateDateProperty(
		PortletDataContext portletDataContext, DynamicQuery dynamicQuery) {

		if (!portletDataContext.hasDateRange()) {
			return;
		}

		Property createDateProperty = PropertyFactoryUtil.forName("createDate");

		dynamicQuery.add(
			createDateProperty.ge(portletDataContext.getStartDate()));

		dynamicQuery.add(
			createDateProperty.le(portletDataContext.getEndDate()));
	}

	protected void doAddCriteria(
		PortletDataContext portletDataContext, StagedModelType stagedModelType,
		DynamicQuery dynamicQuery) {

		Property groupIdProperty = PropertyFactoryUtil.forName("groupId");

		if (portletDataContext.getScopeGroupId() !=
				portletDataContext.getCompanyGroupId()) {

			dynamicQuery.add(
				groupIdProperty.eq(portletDataContext.getScopeGroupId()));
		}
		else {
			dynamicQuery.add(groupIdProperty.eq(0L));
		}

		Property classNameIdProperty = PropertyFactoryUtil.forName(
			"classNameId");

		dynamicQuery.add(
			classNameIdProperty.eq(stagedModelType.getClassNameId()));

		long referrerClassNameId = stagedModelType.getReferrerClassNameId();

		Property referrerClassNameIdProperty = PropertyFactoryUtil.forName(
			"referrerClassNameId");

		if ((referrerClassNameId !=
				StagedModelType.REFERRER_CLASS_NAME_ID_ALL) &&
			(referrerClassNameId !=
				StagedModelType.REFERRER_CLASS_NAME_ID_ANY)) {

			dynamicQuery.add(
				referrerClassNameIdProperty.eq(
					stagedModelType.getReferrerClassNameId()));
		}
		else if (referrerClassNameId ==
					StagedModelType.REFERRER_CLASS_NAME_ID_ANY) {

			dynamicQuery.add(referrerClassNameIdProperty.isNotNull());
		}

		Property typeProperty = PropertyFactoryUtil.forName("type");

		dynamicQuery.add(typeProperty.eq(SystemEventConstants.TYPE_DELETE));

		addCreateDateProperty(portletDataContext, dynamicQuery);
	}

	protected ZipWriter getZipWriter(String fileName) {
		long companyId = CompanyThreadLocal.getCompanyId();

		try {
			_stagingConfiguration =
				_configurationProvider.getCompanyConfiguration(
					StagingConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(configurationException);
			}
		}

		if (!ExportImportThreadLocal.isStagingInProcess() ||
			(_stagingConfiguration.stagingDeleteTempLAROnFailure() &&
			 _stagingConfiguration.stagingDeleteTempLAROnSuccess())) {

			return _zipWriterFactory.getZipWriter();
		}

		return _zipWriterFactory.getZipWriter(
			new File(
				SystemProperties.get(SystemProperties.TMP_DIR) +
					StringPool.SLASH + fileName));
	}

	private Map<String, Boolean> _createAllPortletSetupControlsMap(
		Map<String, String[]> parameterMap, boolean portletConfiguration) {

		return _createPortletConfigurablePortletSetupControlsMap(
			parameterMap, portletConfiguration,
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL,
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL);
	}

	private Map<String, Boolean>
		_createPortletConfigurablePortletSetupControlsMap(
			Map<String, String[]> parameterMap, boolean portletConfiguration,
			String portletArchivedSetupKey, String portletSetupKey,
			String portletUserPreferencesKey) {

		boolean portletArchivedSetups = false;
		boolean portletSetup = false;
		boolean portletUserPreferences = false;

		if (portletConfiguration) {
			if (MapUtil.getBoolean(parameterMap, portletArchivedSetupKey)) {
				portletArchivedSetups = true;
			}

			if (MapUtil.getBoolean(parameterMap, portletSetupKey)) {
				portletSetup = true;
			}

			if (MapUtil.getBoolean(parameterMap, portletUserPreferencesKey)) {
				portletUserPreferences = true;
			}
		}

		return _createPortletSetupControlsMap(
			portletArchivedSetups, portletConfiguration, portletSetup,
			portletUserPreferences);
	}

	private Map<String, Boolean> _createPortletSetupControlsMap(
		boolean portletArchivedSetups, boolean portletConfiguration,
		boolean portletSetup, boolean portletUserPreferences) {

		return HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS,
			portletArchivedSetups
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION, portletConfiguration
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP, portletSetup
		).put(
			PortletDataHandlerKeys.PORTLET_USER_PREFERENCES,
			portletUserPreferences
		).build();
	}

	private Map<String, Boolean> _createRootPortletSetupControlsMap(
		Map<String, String[]> parameterMap, boolean portletConfiguration,
		String rootPortletId) {

		portletConfiguration =
			portletConfiguration &&
			MapUtil.getBoolean(
				parameterMap,
				PortletDataHandlerKeys.PORTLET_CONFIGURATION +
					StringPool.UNDERLINE + rootPortletId);

		String portletArchivedSetupKey =
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId;
		String portletSetupKey =
			PortletDataHandlerKeys.PORTLET_SETUP + StringPool.UNDERLINE +
				rootPortletId;
		String portletUserPreferencesKey =
			PortletDataHandlerKeys.PORTLET_USER_PREFERENCES +
				StringPool.UNDERLINE + rootPortletId;

		return _createPortletConfigurablePortletSetupControlsMap(
			parameterMap, portletConfiguration, portletArchivedSetupKey,
			portletSetupKey, portletUserPreferencesKey);
	}

	private boolean _getExportPortletData(
			long companyId, String portletId,
			Map<String, String[]> parameterMap)
		throws Exception {

		boolean exportPortletData = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PORTLET_DATA);
		boolean exportPortletDataAll = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PORTLET_DATA_ALL);

		if (_log.isDebugEnabled()) {
			_log.debug("Export portlet data " + exportPortletData);
			_log.debug("Export all portlet data " + exportPortletDataAll);
		}

		if (!exportPortletData) {
			return false;
		}

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(companyId, portletId);

		if (portletDataHandler == null) {
			return false;
		}

		if (exportPortletDataAll) {
			return true;
		}

		return MapUtil.getBoolean(
			parameterMap,
			PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
				PortletIdCodec.decodePortletName(portletId));
	}

	private Map<String, Boolean> _getExportPortletSetupControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, String type)
		throws Exception {

		boolean exportPortletConfiguration = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PORTLET_CONFIGURATION);
		boolean exportPortletConfigurationAll = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Export portlet configuration " + exportPortletConfiguration);
		}

		String rootPortletId = getExportableRootPortletId(companyId, portletId);

		Map<String, Boolean> exportPortletSetupControlsMap =
			_createPortletSetupControlsMap(
				exportPortletConfiguration, exportPortletConfiguration,
				exportPortletConfiguration, exportPortletConfiguration);

		if (exportPortletConfigurationAll ||
			(exportPortletConfiguration && type.equals("layout-prototype"))) {

			exportPortletSetupControlsMap = _createAllPortletSetupControlsMap(
				parameterMap, true);
		}
		else if (rootPortletId != null) {
			exportPortletSetupControlsMap = _createRootPortletSetupControlsMap(
				parameterMap, exportPortletConfiguration, rootPortletId);
		}

		return exportPortletSetupControlsMap;
	}

	private Map<String, Boolean> _getImportPortletSetupControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, ManifestSummary manifestSummary)
		throws Exception {

		boolean importPortletConfiguration = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PORTLET_CONFIGURATION);
		boolean importPortletConfigurationAll = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Import portlet configuration " + importPortletConfiguration);
		}

		String rootPortletId = getExportableRootPortletId(companyId, portletId);

		Map<String, Boolean> importPortletSetupControlsMap =
			_createPortletSetupControlsMap(
				importPortletConfiguration, importPortletConfiguration,
				importPortletConfiguration, importPortletConfiguration);

		if (importPortletConfigurationAll) {
			boolean importCurPortletConfiguration = true;

			if (manifestSummary != null) {
				String[] configurationPortletOptions =
					manifestSummary.getConfigurationPortletOptions(
						rootPortletId);

				if (configurationPortletOptions == null) {
					importCurPortletConfiguration = false;
				}
			}

			importPortletSetupControlsMap = _createAllPortletSetupControlsMap(
				parameterMap, importCurPortletConfiguration);
		}
		else if (rootPortletId != null) {
			importPortletSetupControlsMap = _createRootPortletSetupControlsMap(
				parameterMap, importPortletConfiguration, rootPortletId);
		}

		return importPortletSetupControlsMap;
	}

	private List<Portlet> _getPortlets(
		long companyId, DataLevel[] dataLevels,
		boolean excludeDataAlwaysStaged) {

		List<Portlet> portlets = new ArrayList<>();

		Map<Integer, List<Portlet>> rankedPortletsMap = new TreeMap<>();

		for (Portlet portlet : _portletLocalService.getPortlets(companyId)) {
			if (!portlet.isActive()) {
				continue;
			}

			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			if ((portletDataHandler == null) ||
				!ArrayUtil.contains(
					dataLevels, portletDataHandler.getDataLevel()) ||
				(!portletDataHandler.isBatch() &&
				 portletDataHandler.isDataPortalLevel()) ||
				(excludeDataAlwaysStaged &&
				 portletDataHandler.isDataAlwaysStaged())) {

				continue;
			}

			List<Portlet> rankedPortlets = rankedPortletsMap.get(
				portletDataHandler.getRank());

			if (rankedPortlets == null) {
				rankedPortlets = new ArrayList<>();
			}

			rankedPortlets.add(portlet);

			rankedPortletsMap.put(portletDataHandler.getRank(), rankedPortlets);
		}

		for (List<Portlet> rankedPortlets : rankedPortletsMap.values()) {
			portlets.addAll(rankedPortlets);
		}

		return portlets;
	}

	private String _getZipWriterFileName(String id) {
		return StringBundler.concat(
			id, StringPool.DASH, Time.getTimestamp(), ".lar");
	}

	private boolean _isImportPortletData(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, Element portletDataElement)
		throws Exception {

		boolean importPortletData = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PORTLET_DATA);
		boolean importPortletDataAll = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PORTLET_DATA_ALL);

		if (_log.isDebugEnabled()) {
			_log.debug("Import portlet data " + importPortletData);
			_log.debug("Import all portlet data " + importPortletDataAll);
		}

		if (!importPortletData) {
			return false;
		}

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(companyId, portletId);

		if ((portletDataHandler == null) ||
			((portletDataElement == null) &&
			 !portletDataHandler.isDisplayPortlet())) {

			return false;
		}

		if (importPortletDataAll) {
			return true;
		}

		return MapUtil.getBoolean(
			parameterMap,
			PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
				PortletIdCodec.decodePortletName(portletId));
	}

	private boolean _isStagedPortlet(PortletDataContext portletDataContext) {
		Group group = _groupLocalService.fetchGroup(
			portletDataContext.getGroupId());

		if (group == null) {
			return false;
		}

		return group.isStagedPortlet(portletDataContext.getPortletId());
	}

	private boolean _populateLayoutsJSON(
		JSONArray layoutsJSONArray, Layout layout, long[] selectedLayoutIds) {

		List<Layout> childLayouts = layout.getChildren();
		JSONArray childLayoutsJSONArray = null;
		boolean includeChildren = true;

		if (ListUtil.isNotEmpty(childLayouts)) {
			childLayoutsJSONArray = _jsonFactory.createJSONArray();

			for (Layout childLayout : childLayouts) {
				if (!_populateLayoutsJSON(
						childLayoutsJSONArray, childLayout,
						selectedLayoutIds)) {

					includeChildren = false;
				}
			}
		}

		boolean checked = ArrayUtil.contains(
			selectedLayoutIds, layout.getLayoutId());

		if (checked) {
			layoutsJSONArray.put(
				JSONUtil.put(
					"includeChildren", includeChildren
				).put(
					"plid", layout.getPlid()
				));
		}

		if (checked && includeChildren) {
			return true;
		}

		if (childLayoutsJSONArray != null) {

			// We want a 1 level array and not an array of arrays

			for (int i = 0; i < childLayoutsJSONArray.length(); i++) {
				layoutsJSONArray.put(childLayoutsJSONArray.getJSONObject(i));
			}
		}

		return false;
	}

	private MissingReference _validateMissingReference(
		PortletDataContext portletDataContext, Element element) {

		// Missing reference is exported after added as missing

		if (Validator.isNotNull(element.attributeValue("element-path"))) {
			return null;
		}

		String className = element.attributeValue("class-name");

		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				className);

		if ((stagedModelDataHandler != null) &&
			stagedModelDataHandler.validateReference(
				portletDataContext, element)) {

			return null;
		}

		MissingReference missingReference = new MissingReference(element);

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		missingReference.setGroupId(
			MapUtil.getLong(
				groupIds,
				GetterUtil.getLong(element.attributeValue("group-id"))));

		return missingReference;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportHelperImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private PortletLocalService _portletLocalService;

	private volatile StagingConfiguration _stagingConfiguration;

	@Reference
	private SystemEventLocalService _systemEventLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

	private class ManifestSummaryElementProcessor implements ElementProcessor {

		public ManifestSummaryElementProcessor(
			Group group, ManifestSummary manifestSummary) {

			_group = group;
			_manifestSummary = manifestSummary;
		}

		@Override
		public void processElement(Element element) {
			String elementName = element.getName();

			if (elementName.equals("header")) {
				String exportDateString = element.attributeValue("export-date");

				_manifestSummary.setExportDate(
					GetterUtil.getDate(
						exportDateString,
						DateFormatFactoryUtil.getSimpleDateFormat(
							Time.RFC822_FORMAT)));
			}
			else if (elementName.equals("portlet")) {
				String portletId = element.attributeValue("portlet-id");

				Portlet portlet = null;

				try {
					portlet = _portletLocalService.getPortletById(
						_group.getCompanyId(), portletId);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					return;
				}

				PortletDataHandler portletDataHandler =
					_portletDataHandlerProvider.provide(portlet);

				if (portletDataHandler == null) {
					return;
				}

				String[] configurationPortletOptions = StringUtil.split(
					element.attributeValue("portlet-configuration"));

				if (!(portletDataHandler instanceof
						DefaultConfigurationPortletDataHandler) &&
					((portletDataHandler.isBatch() &&
					  portletDataHandler.isDataPortalLevel()) ||
					 portletDataHandler.isDataSiteLevel()) &&
					GetterUtil.getBoolean(
						element.attributeValue("portlet-data"))) {

					_manifestSummary.addDataPortlet(
						portlet, configurationPortletOptions);
				}
				else {
					_manifestSummary.addLayoutPortlet(
						portlet, configurationPortletOptions);
				}
			}
			else if (elementName.equals("staged-model")) {
				String manifestSummaryKey = element.attributeValue(
					"manifest-summary-key");

				if (Validator.isNull(manifestSummaryKey)) {
					return;
				}

				long modelAdditionCount = GetterUtil.getLong(
					element.attributeValue("addition-count"));

				_manifestSummary.addModelAdditionCount(
					manifestSummaryKey, modelAdditionCount);

				String assetTitle = GetterUtil.getString(
					element.attributeValue("asset-title"));

				_manifestSummary.addAssetTitle(manifestSummaryKey, assetTitle);

				long modelDeletionCount = GetterUtil.getLong(
					element.attributeValue("deletion-count"));

				_manifestSummary.addModelDeletionCount(
					manifestSummaryKey, modelDeletionCount);
			}
		}

		private final Group _group;
		private final ManifestSummary _manifestSummary;

	}

}
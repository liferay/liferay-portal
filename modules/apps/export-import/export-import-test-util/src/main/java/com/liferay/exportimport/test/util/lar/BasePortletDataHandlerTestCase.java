/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.lar;

import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portlet.PortletPreferencesImpl;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Zsolt Berentey
 * @author Zoltan Csaszi
 */
public abstract class BasePortletDataHandlerTestCase {

	@Before
	public void setUp() throws Exception {
		stagingGroup = GroupTestUtil.addGroup();

		portletId = getPortletId();

		portletDataHandler = getPortletDataHandler(portletId);
	}

	@Test
	public void testAddDefaultData() throws Exception {
		initContext();

		PortletPreferences portletPreferences =
			portletDataHandler.addDefaultData(
				portletDataContext, portletId, new PortletPreferencesImpl());

		validateDefaultData(portletPreferences);
	}

	@Test
	public void testDataLevel() {
		Assert.assertEquals(getDataLevel(), portletDataHandler.getDataLevel());
	}

	@Test
	public void testDeleteData() throws Exception {
		if (!isDeleteDataTested()) {
			return;
		}

		initContext();

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		addStagedModels();

		portletDataContext.setEndDate(getEndDate());

		portletDataHandler.deleteData(
			portletDataContext, portletId, portletPreferences);

		List<StagedModel> stagedModels = getStagedModels();

		Assert.assertEquals(stagedModels.toString(), 0, stagedModels.size());

		for (String preferenceKey :
				portletDataHandler.getDataPortletPreferences()) {

			String portletPreference = portletPreferences.getValue(
				preferenceKey, StringPool.BLANK);

			Assert.assertEquals(StringPool.BLANK, portletPreference);
		}
	}

	@Test
	public void testExportImportData() throws Exception {
		if (!isExportImportDataTested()) {
			return;
		}

		initContext();

		addStagedModels();

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletDataContext.setEndDate(getEndDate());

		String exportData = portletDataHandler.exportData(
			portletDataContext, portletId, portletPreferences);

		Document document = _saxReader.read(exportData);

		Element rootElement = document.getRootElement();

		List<StagedModel> exportedStagedModels = getStagedModels();

		for (StagedModel stagedModel : exportedStagedModels) {
			Class<?> modelClass = stagedModel.getModelClass();

			Element element = rootElement.element(modelClass.getSimpleName());

			List<Node> stagedModelNodes = element.content();

			boolean contains = false;

			for (Node node : stagedModelNodes) {
				if (node instanceof Element) {
					Element nodeElement = (Element)node;

					Attribute uuidAttribute = nodeElement.attribute("uuid");

					String uuid = uuidAttribute.getValue();

					if (Objects.equals(stagedModel.getUuid(), uuid)) {
						contains = true;
					}
				}
			}

			Assert.assertTrue(contains);
		}

		ZipWriter exportZipWriter = portletDataContext.getZipWriter();

		initContext();

		Group cleanGroup = GroupTestUtil.addGroup();

		portletDataContext.setDataStrategy(
			PortletDataHandlerKeys.DATA_STRATEGY_MIRROR);
		portletDataContext.setGroupId(cleanGroup.getGroupId());
		portletDataContext.setScopeGroupId(cleanGroup.getGroupId());
		portletDataContext.setUserIdStrategy(
			userUuid -> {
				try {
					return TestPropsValues.getUserId();
				}
				catch (Exception exception) {
					return 0;
				}
			});
		portletDataContext.setZipReader(
			_zipReaderFactory.getZipReader(exportZipWriter.getFile()));

		portletDataContext.clearScopedPrimaryKeys();

		portletDataHandler.importData(
			portletDataContext, portletId, portletPreferences, exportData);

		List<StagedModel> importedStagedModels = getStagedModels();

		Set<String> exportedUuids = new HashSet<>();
		Set<String> importedUuids = new HashSet<>();

		for (StagedModel stagedModel : exportedStagedModels) {
			exportedUuids.add(stagedModel.getUuid());
		}

		for (StagedModel stagedModel : importedStagedModels) {
			importedUuids.add(stagedModel.getUuid());
		}

		Assert.assertEquals(exportedUuids, importedUuids);
	}

	@Test
	public void testGetDataPortletPreferences() {
		Assert.assertArrayEquals(
			getDataPortletPreferences(),
			portletDataHandler.getDataPortletPreferences());
	}

	@Test
	public void testGetExportConfigurationControls() throws Exception {
		if (!isGetExportConfigurationControlsTested()) {
			return;
		}

		Portlet portlet = _portletLocalService.getPortletById(portletId);

		initContext();

		PortletDataHandlerControl[] portletDataHandlerControls = null;

		if (portletDataHandler.isDisplayPortlet()) {
			portletDataHandlerControls = portletDataHandler.getExportControls();
		}

		_assertPortletDataHandlerControls(
			getExportConfigurationControls(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), portletDataHandlerControls, -1,
				false),
			portletDataHandler.getExportConfigurationControls(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), portlet, false));

		_assertPortletDataHandlerControls(
			getExportConfigurationControls(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), portletDataHandlerControls, -1,
				true),
			portletDataHandler.getExportConfigurationControls(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), portlet, true));

		_assertPortletDataHandlerControls(
			getExportConfigurationControls(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), portletDataHandlerControls,
				portletDataContext.getPlid(), false),
			portletDataHandler.getExportConfigurationControls(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), portlet,
				portletDataContext.getPlid(), false));

		_assertPortletDataHandlerControls(
			getExportConfigurationControls(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), portletDataHandlerControls,
				portletDataContext.getPlid(), true),
			portletDataHandler.getExportConfigurationControls(
				portletDataContext.getCompanyId(),
				portletDataContext.getGroupId(), portlet,
				portletDataContext.getPlid(), true));
	}

	@Test
	public void testGetExportControls() throws Exception {
		_assertPortletDataHandlerControls(
			getExportControls(), portletDataHandler.getExportControls());
	}

	@Test
	public void testGetExportMetadataControls() throws Exception {
		_assertPortletDataHandlerControls(
			getExportMetadataControls(),
			portletDataHandler.getExportMetadataControls());
	}

	@Test
	public void testGetExportModelCount() throws Exception {
		if (!isGetExportModelCountTested()) {
			return;
		}

		initContext();

		addStagedModels();

		portletDataContext.setEndDate(getEndDate());

		portletDataHandler.prepareManifestSummary(portletDataContext);

		Assert.assertEquals(
			getExportModelCount(),
			portletDataHandler.getExportModelCount(
				portletDataContext.getManifestSummary()));
	}

	@Test
	public void testGetImportConfigurationControls() {
		_assertPortletDataHandlerControls(
			new PortletDataHandlerControl[] {
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_SETUP, "setup", true,
					false, null, null, null)
			},
			portletDataHandler.getImportConfigurationControls(
				new String[] {"setup"}));

		_assertPortletDataHandlerControls(
			new PortletDataHandlerControl[] {
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS,
					"configuration-templates", true, false, null, null, null)
			},
			portletDataHandler.getImportConfigurationControls(
				new String[] {"archived-setups"}));

		_assertPortletDataHandlerControls(
			new PortletDataHandlerControl[] {
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_USER_PREFERENCES,
					"user-preferences", true, false, null, null, null)
			},
			portletDataHandler.getImportConfigurationControls(
				new String[] {"user-preferences"}));
	}

	@Test
	public void testGetImportControls() throws Exception {
		_assertPortletDataHandlerControls(
			getImportControls(), portletDataHandler.getImportControls());
	}

	@Test
	public void testGetImportMetadataControls() throws Exception {
		_assertPortletDataHandlerControls(
			getImportMetadataControls(),
			portletDataHandler.getImportMetadataControls());
	}

	@Test
	public void testGetStagingControls() {
		_assertPortletDataHandlerControls(
			getStagingControls(), portletDataHandler.getStagingControls());
	}

	@Test
	public void testIsDataPortalLevel() {
		Assert.assertEquals(
			isDataPortalLevel(), portletDataHandler.isDataPortalLevel());
	}

	@Test
	public void testIsDataPortletInstanceLevel() {
		Assert.assertEquals(
			isDataPortletInstanceLevel(),
			portletDataHandler.isDataPortletInstanceLevel());
	}

	@Test
	public void testIsDataSiteLevel() {
		Assert.assertEquals(
			isDataSiteLevel(), portletDataHandler.isDataSiteLevel());
	}

	@Test
	public void testIsDisplayPortlet() {
		Assert.assertEquals(
			isDisplayPortlet(), portletDataHandler.isDisplayPortlet());
	}

	@Test
	public void testPrepareManifestSummary() throws Exception {
		initContext();

		addStagedModels();

		portletDataContext.setEndDate(getEndDate());

		portletDataHandler.prepareManifestSummary(portletDataContext);

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		ManifestSummary expectedManifestSummary =
			(ManifestSummary)manifestSummary.clone();

		manifestSummary.resetCounters();

		portletDataHandler.exportData(
			portletDataContext, portletId, new PortletPreferencesImpl());

		checkManifestSummary(expectedManifestSummary);
	}

	@Test
	public void testValidateSchemaVersion() {
		Assert.assertTrue(
			portletDataHandler.validateSchemaVersion(getSchemaVersion()));
	}

	protected void addBooleanParameter(
		Map<String, String[]> parameterMap, String namespace, String name,
		boolean value) {

		PortletDataHandlerBoolean portletDataHandlerBoolean =
			new PortletDataHandlerBoolean(namespace, name);

		parameterMap.put(
			portletDataHandlerBoolean.getNamespacedControlName(),
			new String[] {String.valueOf(value)});
	}

	protected void addParameters(Map<String, String[]> parameterMap) {
	}

	protected abstract void addStagedModels() throws Exception;

	protected void checkManifestSummary(
		ManifestSummary expectedManifestSummary) {

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		checkManifestSummaryReferrerClassNames(manifestSummary);

		for (String manifestSummaryKey :
				expectedManifestSummary.getManifestSummaryKeys()) {

			String[] keyParts = StringUtil.split(
				manifestSummaryKey, StringPool.POUND);

			long expectedModelAdditionCount =
				expectedManifestSummary.getModelAdditionCount(
					manifestSummaryKey);

			StagedModelType stagedModelType = new StagedModelType(keyParts[0]);

			if (keyParts.length > 1) {
				stagedModelType = new StagedModelType(keyParts[0], keyParts[1]);
			}

			long modelAdditionCount = manifestSummary.getModelAdditionCount(
				stagedModelType);

			if (expectedModelAdditionCount == 0) {
				Assert.assertFalse(modelAdditionCount > 0);
			}
			else {
				Assert.assertEquals(
					expectedModelAdditionCount, modelAdditionCount);
			}
		}
	}

	protected void checkManifestSummaryReferrerClassNames(
		ManifestSummary manifestSummary) {

		for (String manifestSummaryKey :
				manifestSummary.getManifestSummaryKeys()) {

			Assert.assertFalse(
				manifestSummaryKey.endsWith(
					StagedModelType.REFERRER_CLASS_NAME_ALL));
			Assert.assertFalse(
				manifestSummaryKey.endsWith(
					StagedModelType.REFERRER_CLASS_NAME_ANY));
		}
	}

	protected DataLevel getDataLevel() {
		return DataLevel.SITE;
	}

	protected String[] getDataPortletPreferences() {
		return StringPool.EMPTY_ARRAY;
	}

	protected Date getEndDate() {
		return new Date();
	}

	protected PortletDataHandlerControl[] getExportConfigurationControls(
		long companyId, long groupId,
		PortletDataHandlerControl[] portletDataHandlerControls, long plid,
		boolean privateLayout) {

		if (plid < 0) {
			return new PortletDataHandlerControl[] {
				new PortletDataHandlerBoolean(
					null, PortletDataHandlerKeys.PORTLET_SETUP, "setup", true,
					false, portletDataHandlerControls, null, null)
			};
		}

		return new PortletDataHandlerControl[0];
	}

	protected PortletDataHandlerControl[] getExportControls() {
		return new PortletDataHandlerControl[0];
	}

	protected PortletDataHandlerControl[] getExportMetadataControls() {
		return new PortletDataHandlerControl[0];
	}

	protected long getExportModelCount() {
		List<StagedModel> stagedModels = getStagedModels();

		return stagedModels.size();
	}

	protected PortletDataHandlerControl[] getImportControls() {
		return new PortletDataHandlerControl[0];
	}

	protected PortletDataHandlerControl[] getImportMetadataControls() {
		return new PortletDataHandlerControl[0];
	}

	protected PortletDataHandler getPortletDataHandler(String portletId) {
		try {
			Bundle bundle = FrameworkUtil.getBundle(getClass());

			BundleContext bundleContext = bundle.getBundleContext();

			Collection<ServiceReference<PortletDataHandler>>
				portletDataHandlerServiceReferences =
					bundleContext.getServiceReferences(
						PortletDataHandler.class,
						"(jakarta.portlet.name=" + portletId + ")");

			Iterator<ServiceReference<PortletDataHandler>> iterator =
				portletDataHandlerServiceReferences.iterator();

			return bundleContext.getService(iterator.next());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected abstract String getPortletId();

	protected String getSchemaVersion() {
		return "4.0.0";
	}

	protected List<StagedModel> getStagedModels() {
		return new ArrayList<>();
	}

	protected PortletDataHandlerControl[] getStagingControls() {
		return new PortletDataHandlerControl[0];
	}

	protected Date getStartDate() {
		return new Date(System.currentTimeMillis() - Time.HOUR);
	}

	protected void initContext() throws Exception {
		Map<String, String[]> parameterMap = new LinkedHashMap<>();

		addParameters(parameterMap);

		zipWriter = _zipWriterFactory.getZipWriter();

		portletDataContext =
			_portletDataContextFactory.createExportPortletDataContext(
				stagingGroup.getCompanyId(), stagingGroup.getGroupId(),
				parameterMap, getStartDate(), getEndDate(), zipWriter);

		rootElement = _saxReader.createElement("root");

		portletDataContext.setExportDataRootElement(rootElement);

		missingReferencesElement = _saxReader.createElement(
			"missing-references");

		portletDataContext.setMissingReferencesElement(
			missingReferencesElement);

		portletDataContext.setPortletId(portletId);
	}

	protected boolean isDataPortalLevel() {
		DataLevel dataLevel = getDataLevel();

		return dataLevel.equals(DataLevel.PORTAL);
	}

	protected boolean isDataPortletInstanceLevel() {
		DataLevel dataLevel = getDataLevel();

		return dataLevel.equals(DataLevel.PORTLET_INSTANCE);
	}

	protected boolean isDataSiteLevel() {
		DataLevel dataLevel = getDataLevel();

		return dataLevel.equals(DataLevel.SITE);
	}

	protected boolean isDeleteDataTested() {
		return false;
	}

	protected boolean isDisplayPortlet() {
		if (isDataPortletInstanceLevel() &&
			ArrayUtil.isNotEmpty(
				portletDataHandler.getDataPortletPreferences())) {

			return true;
		}

		return false;
	}

	protected boolean isExportImportDataTested() {
		return false;
	}

	protected boolean isGetExportConfigurationControlsTested() {
		return false;
	}

	protected boolean isGetExportModelCountTested() {
		return false;
	}

	protected void validateDefaultData(PortletPreferences portletPreferences) {
	}

	protected Element missingReferencesElement;
	protected PortletDataContext portletDataContext;
	protected PortletDataHandler portletDataHandler;
	protected String portletId;
	protected Element rootElement;

	@DeleteAfterTestRun
	protected Group stagingGroup;

	protected ZipWriter zipWriter;

	private void _assertPortletDataHandlerControls(
		PortletDataHandlerControl[] expectedPortletDataHandlerControls,
		PortletDataHandlerControl[] portletDataHandlerControls) {

		for (PortletDataHandlerControl expectedPortletDataHandlerControl :
				expectedPortletDataHandlerControls) {

			boolean contains = false;

			for (PortletDataHandlerControl portletDataHandlerControl :
					portletDataHandlerControls) {

				if (Objects.equals(
						expectedPortletDataHandlerControl.getControlName(),
						portletDataHandlerControl.getControlName()) &&
					Objects.equals(
						expectedPortletDataHandlerControl.getControlLabel(),
						portletDataHandlerControl.getControlLabel()) &&
					(expectedPortletDataHandlerControl.isDisabled() ==
						portletDataHandlerControl.isDisabled())) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(contains);
		}
	}

	@Inject
	private PortletDataContextFactory _portletDataContextFactory;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private SAXReader _saxReader;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}
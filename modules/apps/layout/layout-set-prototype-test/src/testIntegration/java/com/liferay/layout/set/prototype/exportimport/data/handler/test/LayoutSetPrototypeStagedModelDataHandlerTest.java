/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.layout.page.template.kernel.provider.util.LayoutPageTemplateEntryLayoutProviderUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Daniela Zapata Riesco
 */
@RunWith(Arquillian.class)
public class LayoutSetPrototypeStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_layoutSetPrototype != null) {
			_layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.
					fetchLayoutSetPrototypeByUuidAndCompanyId(
						_layoutSetPrototype.getUuid(),
						_layoutSetPrototype.getCompanyId());

			if (_layoutSetPrototype != null) {
				LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
					_layoutSetPrototype);
			}
		}

		if (_layoutPrototype != null) {
			_layoutPrototype =
				LayoutPrototypeLocalServiceUtil.
					fetchLayoutPrototypeByUuidAndCompanyId(
						_layoutPrototype.getUuid(),
						_layoutPrototype.getCompanyId());

			if (_layoutPrototype != null) {
				LayoutPrototypeLocalServiceUtil.deleteLayoutPrototype(
					_layoutPrototype);
			}
		}
	}

	protected void addLayout(Class<?> clazz, Layout layout) throws Exception {
		List<Layout> layouts = _layouts.get(clazz.getSimpleName());

		if (layouts == null) {
			layouts = new ArrayList<>();

			_layouts.put(clazz.getSimpleName(), layouts);
		}

		layouts.add(layout);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			LayoutSetPrototypeStagedModelDataHandlerTest.class.getName(),
			Boolean.TRUE.toString());

		LayoutLocalServiceUtil.updateLayout(layout);
	}

	protected void addLayoutFriendlyURLs(Class<?> clazz, long plid)
		throws Exception {

		List<LayoutFriendlyURL> layoutFriendlyURLs = _layoutFriendlyURLs.get(
			clazz.getSimpleName());

		if (layoutFriendlyURLs == null) {
			layoutFriendlyURLs = new ArrayList<>();

			_layoutFriendlyURLs.put(clazz.getSimpleName(), layoutFriendlyURLs);
		}

		List<LayoutFriendlyURL> layoutLayoutFriendlyURLs =
			LayoutFriendlyURLLocalServiceUtil.getLayoutFriendlyURLs(plid);

		Assert.assertEquals(
			layoutLayoutFriendlyURLs.toString(), 1,
			layoutLayoutFriendlyURLs.size());

		layoutFriendlyURLs.add(layoutLayoutFriendlyURLs.get(0));
	}

	protected LayoutPrototype addLayoutPrototype(
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		_layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			RandomTestUtil.randomString());

		addDependentStagedModel(
			dependentStagedModelsMap, LayoutPrototype.class, _layoutPrototype);

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			_layoutPrototype.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		Assert.assertEquals(layouts.toString(), 1, layouts.size());

		Layout layout = layouts.get(0);

		addDependentStagedModel(dependentStagedModelsMap, Layout.class, layout);

		addLayout(LayoutPrototype.class, layout);

		List<LayoutFriendlyURL> layoutFriendlyURLs =
			LayoutFriendlyURLLocalServiceUtil.getLayoutFriendlyURLs(
				layout.getPlid());

		Assert.assertEquals(
			layoutFriendlyURLs.toString(), 1, layoutFriendlyURLs.size());

		addDependentStagedModel(
			dependentStagedModelsMap, LayoutFriendlyURL.class,
			layoutFriendlyURLs.get(0));

		addLayoutFriendlyURLs(LayoutPrototype.class, layout.getPlid());

		return _layoutPrototype;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			_layoutSetPrototype.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		Assert.assertEquals(layouts.toString(), 1, layouts.size());

		Layout layout = layouts.get(0);

		addLayout(LayoutSetPrototype.class, layout);
		addLayoutFriendlyURLs(LayoutSetPrototype.class, layout.getPlid());

		LayoutPrototype layoutPrototype = addLayoutPrototype(
			dependentStagedModelsMap);

		Layout prototypedLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototype.getGroupId(), true, layoutPrototype, true);

		addLayout(LayoutSetPrototype.class, prototypedLayout);
		addLayoutFriendlyURLs(
			LayoutSetPrototype.class, prototypedLayout.getPlid());

		return _layoutSetPrototype;
	}

	@Override
	protected void deleteStagedModel(
			StagedModel stagedModel,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			(LayoutSetPrototype)stagedModel);
	}

	protected LayoutPrototype getImportedLayoutPrototype(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> dependentLayoutPrototypeStagedModels =
			dependentStagedModelsMap.get(LayoutPrototype.class.getSimpleName());

		Assert.assertEquals(
			dependentLayoutPrototypeStagedModels.toString(), 1,
			dependentLayoutPrototypeStagedModels.size());

		LayoutPrototype layoutPrototype =
			(LayoutPrototype)dependentLayoutPrototypeStagedModels.get(0);

		LayoutPrototype importedLayoutPrototype =
			LayoutPrototypeLocalServiceUtil.
				fetchLayoutPrototypeByUuidAndCompanyId(
					layoutPrototype.getUuid(), group.getCompanyId());

		Assert.assertNotNull(importedLayoutPrototype);

		return importedLayoutPrototype;
	}

	protected List<LayoutFriendlyURL> getLayoutFriendlyURLs(Class<?> clazz) {
		return _layoutFriendlyURLs.get(clazz.getSimpleName());
	}

	protected List<Layout> getLayouts(Class<?> clazz) {
		return _layouts.get(clazz.getSimpleName());
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return LayoutSetPrototypeLocalServiceUtil.
			getLayoutSetPrototypeByUuidAndCompanyId(uuid, group.getCompanyId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return LayoutSetPrototype.class;
	}

	protected Layout importLayoutFromLAR(StagedModel stagedModel)
		throws DocumentException, IOException {

		LayoutSetPrototype layoutSetPrototype = (LayoutSetPrototype)stagedModel;

		String fileName = layoutSetPrototype.getLayoutSetPrototypeId() + ".lar";

		String modelPath = ExportImportPathUtil.getModelPath(
			stagedModel, fileName);

		try (InputStream inputStream =
				portletDataContext.getZipEntryAsInputStream(modelPath)) {

			try (ZipReader zipReader = _zipReaderFactory.getZipReader(
					inputStream)) {

				Document document = UnsecureSAXReaderUtil.read(
					zipReader.getEntryAsString("manifest.xml"));

				Element rootElement = document.getRootElement();

				Element layoutElement = rootElement.element("Layout");

				List<Element> elements = layoutElement.elements();

				List<Layout> importedLayouts = new ArrayList<>(elements.size());

				for (Element element : elements) {
					String layoutPrototypeUuid = element.attributeValue(
						"layout-prototype-uuid");

					if (Validator.isNotNull(layoutPrototypeUuid)) {
						String path = element.attributeValue("path");

						Layout layout = (Layout)portletDataContext.fromXML(
							zipReader.getEntryAsString(path));

						importedLayouts.add(layout);
					}
				}

				Assert.assertEquals(
					importedLayouts.toString(), 1, importedLayouts.size());

				return importedLayouts.get(0);
			}
		}
	}

	@Override
	protected void validateImport(
			StagedModel stagedModel, StagedModelAssets stagedModelAssets,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		LayoutSetPrototype importedLayoutSetPrototype =
			(LayoutSetPrototype)getStagedModel(stagedModel.getUuid(), group);

		Assert.assertNotNull(importedLayoutSetPrototype);

		LayoutPrototype importedLayoutPrototype = getImportedLayoutPrototype(
			dependentStagedModelsMap, group);

		Layout importedLayout = importLayoutFromLAR(stagedModel);

		validateLayouts(
			importedLayoutSetPrototype, importedLayoutPrototype,
			importedLayout);
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		Assert.assertEquals(
			stagedModel.getUuid(), importedStagedModel.getUuid());

		LayoutSetPrototype layoutSetPrototype = (LayoutSetPrototype)stagedModel;
		LayoutSetPrototype importedLayoutSetPrototype =
			(LayoutSetPrototype)importedStagedModel;

		Assert.assertEquals(
			layoutSetPrototype.getName(), importedLayoutSetPrototype.getName());
		Assert.assertEquals(
			StringUtil.trim(layoutSetPrototype.getDescription()),
			StringUtil.trim(importedLayoutSetPrototype.getDescription()));
		Assert.assertEquals(
			layoutSetPrototype.isActive(),
			importedLayoutSetPrototype.isActive());
	}

	protected void validateLayouts(
			LayoutSetPrototype importedLayoutSetPrototype,
			LayoutPrototype importedLayoutPrototype,
			Layout layoutSetPrototypeLayout)
		throws Exception {

		validatePrototypedLayouts(
			LayoutSetPrototype.class, importedLayoutSetPrototype.getGroupId());
		validatePrototypedLayouts(
			LayoutPrototype.class, importedLayoutPrototype.getGroupId());

		Assert.assertNotNull(
			layoutSetPrototypeLayout.getPortletLayoutPageTemplateEntryERC());

		Layout importedLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layoutSetPrototypeLayout.getUuid(),
				importedLayoutSetPrototype.getGroupId(), true);

		Assert.assertNotNull(importedLayout);
		Assert.assertEquals(
			importedLayoutSetPrototype.getGroupId(),
			importedLayout.getGroupId());

		LayoutPrototype layoutPrototype =
			LayoutPageTemplateEntryLayoutProviderUtil.
				getLayoutPageTemplateEntryLayoutPrototype(
					importedLayout.getCompanyId(),
					importedLayout.getPortletLayoutPageTemplateEntryERC(),
					importedLayout.getPortletLayoutPageTemplateEntryScopeERC(),
					importedLayout.getGroupId());

		Assert.assertEquals(
			importedLayoutPrototype.getUuid(), layoutPrototype.getUuid());
	}

	protected void validatePrototypedLayouts(Class<?> clazz, long groupId)
		throws Exception {

		List<Layout> layouts = getLayouts(clazz);

		for (Layout layout : layouts) {
			Layout importedLayout =
				LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
					layout.getUuid(), groupId, layout.isPrivateLayout());

			Assert.assertNotNull(importedLayout);
			Assert.assertEquals(
				layout.getTypeSettingsProperty(
					LayoutSetPrototypeStagedModelDataHandlerTest.class.
						getName()),
				importedLayout.getTypeSettingsProperty(
					LayoutSetPrototypeStagedModelDataHandlerTest.class.
						getName()));
		}

		List<LayoutFriendlyURL> layoutFriendlyURLs = getLayoutFriendlyURLs(
			clazz);

		for (LayoutFriendlyURL layoutFriendlyURL : layoutFriendlyURLs) {
			LayoutFriendlyURL importedLayoutFriendlyURL =
				LayoutFriendlyURLLocalServiceUtil.
					fetchLayoutFriendlyURLByUuidAndGroupId(
						layoutFriendlyURL.getUuid(), groupId);

			Assert.assertNotNull(importedLayoutFriendlyURL);
			Assert.assertEquals(
				layoutFriendlyURL.getFriendlyURL(),
				importedLayoutFriendlyURL.getFriendlyURL());
		}
	}

	private final Map<String, List<LayoutFriendlyURL>> _layoutFriendlyURLs =
		new HashMap<>();
	private LayoutPrototype _layoutPrototype;
	private LayoutSetPrototype _layoutSetPrototype;
	private final Map<String, List<Layout>> _layouts = new HashMap<>();

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.test.util.BookmarksTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class PortletDataContextReferencesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		UserTestUtil.setUser(TestPropsValues.getUser());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_portletDataContext =
			PortletDataContextFactoryUtil.createExportPortletDataContext(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				new HashMap<String, String[]>(), null, null, new ZipWriter());

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute("self-path", "dummyPath");

		Element element = rootElement.addElement("PortletDataRootElement");

		element.addAttribute("self-path", "dummyPortletDataPath");

		_portletDataContext.setExportDataRootElement(element);
		_portletDataContext.setImportDataRootElement(element);

		_portletDataContext.setMissingReferencesElement(
			rootElement.addElement("missing-references"));

		_bookmarksFolder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		_bookmarksEntry = BookmarksTestUtil.addEntry(
			_bookmarksFolder.getFolderId(), true, _serviceContext);
	}

	@Test
	public void testCleanUpMissingReferences() throws Exception {
		_portletDataContext.setPortletId(
			JournalContentPortletKeys.JOURNAL_CONTENT);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			JournalContentPortletKeys.JOURNAL_CONTENT);

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_portletDataContext.addReferenceElement(
			portlet, _portletDataContext.getExportDataRootElement(),
			assetCategory, PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertFalse(
			missingReferenceElements.toString(),
			missingReferenceElements.isEmpty());
		Assert.assertEquals(
			missingReferenceElements.toString(), 1,
			missingReferenceElements.size());

		StagedModelDataHandlerUtil.exportStagedModel(
			_portletDataContext, assetCategory);

		missingReferenceElements = missingReferencesElement.elements();

		Assert.assertFalse(
			missingReferenceElements.toString(),
			missingReferenceElements.isEmpty());
		Assert.assertEquals(
			missingReferenceElements.toString(), 1,
			missingReferenceElements.size());

		Element missingReferenceElement = missingReferenceElements.get(0);

		Assert.assertNull(missingReferenceElement.attribute("missing"));
		Assert.assertFalse(
			Validator.isBlank(
				missingReferenceElement.attributeValue("element-path")));
	}

	@Test
	public void testMissingNotMissingReference() throws Exception {
		_portletDataContext.setPortletId(BookmarksPortletKeys.BOOKMARKS);

		Element bookmarksEntryElement =
			_portletDataContext.getExportDataElement(_bookmarksEntry);

		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, true);
		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, false);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertFalse(
			missingReferenceElements.toString(),
			missingReferenceElements.isEmpty());
		Assert.assertEquals(
			missingReferenceElements.toString(), 1,
			missingReferenceElements.size());

		Element missingReferenceElement = missingReferenceElements.get(0);

		Assert.assertNull(missingReferenceElement.attribute("missing"));
		Assert.assertFalse(
			Validator.isBlank(
				missingReferenceElement.attributeValue("element-path")));
	}

	@Test
	public void testMissingReference() throws Exception {
		Element bookmarksEntryElement =
			_portletDataContext.getExportDataElement(_bookmarksEntry);

		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, true);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertEquals(
			missingReferenceElements.toString(), 1,
			missingReferenceElements.size());

		Element missingReferenceElement = missingReferenceElements.get(0);

		Assert.assertEquals(
			_bookmarksFolder.getModelClassName(),
			missingReferenceElement.attributeValue("class-name"));
		Assert.assertEquals(
			String.valueOf(_bookmarksFolder.getPrimaryKeyObj()),
			missingReferenceElement.attributeValue("class-pk"));
	}

	@Test
	public void testMultipleMissingNotMissingReference() throws Exception {
		_portletDataContext.setPortletId(BookmarksPortletKeys.BOOKMARKS);

		Element bookmarksEntryElement1 =
			_portletDataContext.getExportDataElement(_bookmarksEntry);

		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement1, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, true);
		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement1, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, false);

		BookmarksEntry bookmarksEntry = BookmarksTestUtil.addEntry(
			_bookmarksFolder.getFolderId(), true, _serviceContext);

		Element bookmarksEntryElement2 =
			_portletDataContext.getExportDataElement(bookmarksEntry);

		_portletDataContext.addReferenceElement(
			bookmarksEntry, bookmarksEntryElement2, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, true);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertFalse(
			missingReferenceElements.toString(),
			missingReferenceElements.isEmpty());
		Assert.assertEquals(
			missingReferenceElements.toString(), 1,
			missingReferenceElements.size());

		Element missingReferenceElement = missingReferenceElements.get(0);

		Assert.assertNull(missingReferenceElement.attribute("missing"));
		Assert.assertFalse(
			Validator.isBlank(
				missingReferenceElement.attributeValue("element-path")));
	}

	@Test
	public void testMultipleMissingReferences() throws Exception {
		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			JournalContentPortletKeys.JOURNAL_CONTENT);

		_portletDataContext.addReferenceElement(
			portlet, _portletDataContext.getExportDataRootElement(),
			_bookmarksEntry, PortletDataContext.REFERENCE_TYPE_DEPENDENCY,
			true);
		_portletDataContext.addReferenceElement(
			portlet, _portletDataContext.getExportDataRootElement(),
			_bookmarksEntry, PortletDataContext.REFERENCE_TYPE_DEPENDENCY,
			true);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertFalse(
			missingReferenceElements.toString(),
			missingReferenceElements.isEmpty());
		Assert.assertEquals(
			missingReferenceElements.toString(), 1,
			missingReferenceElements.size());

		Element missingReferenceElement = missingReferenceElements.get(0);

		Assert.assertEquals(
			BookmarksEntry.class.getName(),
			missingReferenceElement.attributeValue("class-name"));
		Assert.assertEquals(
			String.valueOf(_bookmarksEntry.getPrimaryKeyObj()),
			missingReferenceElement.attributeValue("class-pk"));
	}

	@Test
	public void testNotMissingMissingReference() throws Exception {
		Element bookmarksEntryElement =
			_portletDataContext.getExportDataElement(_bookmarksEntry);

		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, false);
		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, true);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertEquals(
			missingReferenceElements.toString(), 0,
			missingReferenceElements.size());
	}

	@Test
	public void testNotMissingReference() throws Exception {
		Element bookmarksEntryElement =
			_portletDataContext.getExportDataElement(_bookmarksEntry);

		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_PARENT, false);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertEquals(
			missingReferenceElements.toString(), 0,
			missingReferenceElements.size());
	}

	@Test
	public void testNotReferenceMissingReference() throws Exception {
		_portletDataContext.setZipWriter(new ZipWriter());

		Element bookmarksEntryElement =
			_portletDataContext.getExportDataElement(_bookmarksEntry);

		_portletDataContext.addClassedModel(
			bookmarksEntryElement,
			ExportImportPathUtil.getModelPath(_bookmarksEntry),
			_bookmarksEntry);

		Element bookmarksFolderElement =
			_portletDataContext.getExportDataElement(_bookmarksFolder);

		_portletDataContext.addReferenceElement(
			_bookmarksFolder, bookmarksFolderElement, _bookmarksEntry,
			PortletDataContext.REFERENCE_TYPE_CHILD, true);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertTrue(
			missingReferenceElements.toString(),
			missingReferenceElements.isEmpty());
	}

	@Test
	public void testSameMissingReferenceMultipleTimes() throws Exception {
		Element bookmarksEntryElement =
			_portletDataContext.getExportDataElement(_bookmarksEntry);

		bookmarksEntryElement.addAttribute(
			"path", ExportImportPathUtil.getModelPath(_bookmarksEntry));

		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
		_portletDataContext.addReferenceElement(
			_bookmarksEntry, bookmarksEntryElement, _bookmarksFolder,
			PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);

		Element missingReferencesElement =
			_portletDataContext.getMissingReferencesElement();

		List<Element> missingReferenceElements =
			missingReferencesElement.elements();

		Assert.assertEquals(
			missingReferenceElements.toString(), 1,
			missingReferenceElements.size());

		List<Element> referenceElements =
			_portletDataContext.getReferenceElements(
				_bookmarksEntry, BookmarksFolder.class);

		Assert.assertEquals(
			referenceElements.toString(), 1, referenceElements.size());

		for (Element referenceElement : referenceElements) {
			Assert.assertTrue(
				GetterUtil.getBoolean(
					referenceElement.attributeValue("missing")));
		}
	}

	@Test
	public void testSetImportDataElementCacheEnabled() throws Exception {
		_portletDataContext.setImportDataElementCacheEnabled(true);

		_portletDataContext.addClassedModel(
			_portletDataContext.getExportDataElement(_bookmarksEntry),
			ExportImportPathUtil.getModelPath(_bookmarksEntry), _bookmarksEntry,
			BookmarksEntry.class);

		Assert.assertTrue(
			_portletDataContext.getImportDataElement(_bookmarksEntry) ==
				_portletDataContext.getImportDataElement(_bookmarksEntry));

		_portletDataContext.addClassedModel(
			_portletDataContext.getExportDataElement(_bookmarksFolder),
			ExportImportPathUtil.getModelPath(_bookmarksFolder),
			_bookmarksFolder, BookmarksFolder.class);

		Assert.assertTrue(
			_portletDataContext.getImportDataElement(_bookmarksFolder) ==
				_portletDataContext.getImportDataElement(_bookmarksFolder));

		_portletDataContext.setImportDataElementCacheEnabled(false);

		Assert.assertFalse(
			_portletDataContext.getImportDataElement(_bookmarksEntry) ==
				_portletDataContext.getImportDataElement(_bookmarksEntry));
		Assert.assertFalse(
			_portletDataContext.getImportDataElement(_bookmarksFolder) ==
				_portletDataContext.getImportDataElement(_bookmarksFolder));
	}

	private BookmarksEntry _bookmarksEntry;
	private BookmarksFolder _bookmarksFolder;

	@DeleteAfterTestRun
	private Group _group;

	private PortletDataContext _portletDataContext;
	private ServiceContext _serviceContext;

}
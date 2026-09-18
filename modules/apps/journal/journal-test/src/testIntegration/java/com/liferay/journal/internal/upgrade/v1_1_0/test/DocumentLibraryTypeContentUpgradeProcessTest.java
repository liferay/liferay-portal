/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ben Demetrius
 */
@RunWith(Arquillian.class)
public class DocumentLibraryTypeContentUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());
	}

	@Test
	@TestInfo("LPD-104003")
	public void testConvertContentWithDocumentLibraryType() throws Exception {
		Element dynamicElementElement = _convertContent(
			"document_library",
			StringBundler.concat(
				"/c/document_library/get_file?uuid=", _fileEntry.getUuid(),
				"&groupId=", _fileEntry.getGroupId()));

		_assertConverted(dynamicElementElement);
	}

	@Test
	@TestInfo("LPD-104003")
	public void testConvertContentWithImageGalleryType() throws Exception {
		Element dynamicElementElement = _convertContent(
			"image_gallery", _getImageGalleryURL(_fileEntry.getUuid()));

		_assertConverted(dynamicElementElement);
	}

	@Test
	@TestInfo("LPD-104003")
	public void testConvertContentWithImageGalleryTypeAndMissingFileEntry()
		throws Exception {

		Element dynamicElementElement = _convertContent(
			"image_gallery",
			_getImageGalleryURL(RandomTestUtil.randomString()));

		Assert.assertEquals(
			"document_library", dynamicElementElement.attributeValue("type"));

		Element dynamicContentElement = dynamicElementElement.element(
			"dynamic-content");

		Assert.assertEquals(StringPool.BLANK, dynamicContentElement.getText());
	}

	@Test
	@TestInfo("LPD-104003")
	public void testConvertContentWithImageGalleryTypeAndSomeMissingFileEntries()
		throws Exception {

		Element dynamicElementElement = _convertContent(
			"image_gallery", _getImageGalleryURL(_fileEntry.getUuid()),
			_getImageGalleryURL(RandomTestUtil.randomString()));

		Assert.assertEquals(
			"document_library", dynamicElementElement.attributeValue("type"));

		List<Element> dynamicContentElements = dynamicElementElement.elements(
			"dynamic-content");

		Element dynamicContentElement = dynamicContentElements.get(0);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			dynamicContentElement.getText());

		Assert.assertEquals(_fileEntry.getUuid(), jsonObject.getString("uuid"));

		dynamicContentElement = dynamicContentElements.get(1);

		Assert.assertEquals(StringPool.BLANK, dynamicContentElement.getText());
	}

	private void _assertConverted(Element dynamicElementElement)
		throws Exception {

		Assert.assertEquals(
			"document_library", dynamicElementElement.attributeValue("type"));

		Element dynamicContentElement = dynamicElementElement.element(
			"dynamic-content");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			dynamicContentElement.getText());

		Assert.assertEquals(
			_fileEntry.getGroupId(), jsonObject.getLong("groupId"));
		Assert.assertEquals(
			_fileEntry.getTitle(), jsonObject.getString("title"));
		Assert.assertEquals("document", jsonObject.getString("type"));
		Assert.assertEquals(_fileEntry.getUuid(), jsonObject.getString("uuid"));
	}

	private Element _convertContent(String type, String... urls)
		throws Exception {

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.journal.internal.upgrade.v1_1_0." +
				"DocumentLibraryTypeContentUpgradeProcess");

		Document document = SAXReaderUtil.read(
			ReflectionTestUtil.<String>invoke(
				upgradeProcess, "_convertContent",
				new Class<?>[] {String.class}, _getContent(type, urls)));

		Element rootElement = document.getRootElement();

		return rootElement.element("dynamic-element");
	}

	private String _getContent(String type, String... urls) {
		StringBundler sb = new StringBundler();

		sb.append("<?xml version=\"1.0\"?><root><dynamic-element name=\"");
		sb.append(RandomTestUtil.randomString());
		sb.append("\" type=\"");
		sb.append(type);
		sb.append("\" index-type=\"\">");

		for (String url : urls) {
			sb.append("<dynamic-content><![CDATA[");
			sb.append(url);
			sb.append("]]></dynamic-content>");
		}

		sb.append("</dynamic-element></root>");

		return sb.toString();
	}

	private String _getImageGalleryURL(String uuid) {
		return StringBundler.concat(
			"/image/image_gallery?uuid=", uuid, "&groupId=",
			_fileEntry.getGroupId(), "&t=", RandomTestUtil.randomLong());
	}

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "(&(component.name=com.liferay.journal.internal.upgrade.registry.JournalServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
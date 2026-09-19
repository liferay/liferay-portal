/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.upload.AttachmentContentUpdater;

import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class EntryAttachmentContentUpdaterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testKeepsTheOriginalTagAttributes() throws Exception {
		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_getInputStream(), ContentTypes.IMAGE_JPEG);

		String tempFileEntryImgTag = _getTempEntryAttachmentFileEntryImgTag(
			tempFileEntry.getFileEntryId(), _TEMP_FILE_ENTRY_IMAGE_URL,
			HashMapBuilder.put(
				"alt", "A big image"
			).put(
				"class", "image-big"
			).build());

		String originalContent =
			"<p>Sample Text</p><a href=\"www.liferay.com\">" +
				tempFileEntryImgTag + "</a>";

		FileEntry newFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_getInputStream(), ContentTypes.IMAGE_JPEG);

		String content = _attachmentContentUpdater.updateContent(
			originalContent, ContentTypes.TEXT_HTML,
			fileEntry -> {
				if (fileEntry.getFileEntryId() ==
						tempFileEntry.getFileEntryId()) {

					return newFileEntry;
				}

				return null;
			});

		String fileEntryURL = PortletFileRepositoryUtil.getPortletFileEntryURL(
			null, newFileEntry, StringPool.BLANK);

		String expectedContent = StringBundler.concat(
			"<p>Sample Text</p><a href=\"www.liferay.com\">",
			"<img alt=\"A big image\" class=\"image-big\" src=\"", fileEntryURL,
			"\" /></a>");

		_assertEquals(_parseHtml(expectedContent), _parseHtml(content));
	}

	@Test
	public void testUpdateContentWitImgTag() throws Exception {
		StringBundler sb = new StringBundler(2);

		sb.append("<p>Sample Text</p><a href=\"www.liferay.com\">");
		sb.append("<span><img src=\"www.liferay.com/pic1.jpg\" /></span></a>");

		String content = _attachmentContentUpdater.updateContent(
			sb.toString(), ContentTypes.TEXT_HTML, tempFileEntry -> null);

		String expectedContent = sb.toString();

		_assertEquals(_parseHtml(expectedContent), _parseHtml(content));
	}

	@Test
	public void testUpdateContentWithMultipleImgTags() throws Exception {
		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_getInputStream(), ContentTypes.IMAGE_JPEG);

		String tempFileEntryImgTag = _getTempEntryAttachmentFileEntryImgTag(
			tempFileEntry.getFileEntryId(), _TEMP_FILE_ENTRY_IMAGE_URL);

		StringBundler sb = new StringBundler(4);

		sb.append("<p>Sample Text</p><a href=\"www.liferay.com\">");
		sb.append("<span><img src=\"www.liferay.com/pic1.jpg\" /></span>");
		sb.append(tempFileEntryImgTag);
		sb.append("<img src=\"www.liferay.com/pic2.jpg\" /></a>");

		FileEntry newFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_getInputStream(), ContentTypes.IMAGE_JPEG);

		String content = _attachmentContentUpdater.updateContent(
			sb.toString(), ContentTypes.TEXT_HTML,
			fileEntry -> {
				if (fileEntry.getFileEntryId() ==
						tempFileEntry.getFileEntryId()) {

					return newFileEntry;
				}

				return null;
			});

		String fileEntryURL = PortletFileRepositoryUtil.getPortletFileEntryURL(
			null, newFileEntry, StringPool.BLANK);

		sb = new StringBundler(6);

		sb.append("<p>Sample Text</p><a href=\"www.liferay.com\">");
		sb.append("<span><img src=\"www.liferay.com/pic1.jpg\" /></span>");
		sb.append("<img src=\"");
		sb.append(fileEntryURL);
		sb.append("\" />");
		sb.append("<img src=\"www.liferay.com/pic2.jpg\" /></a>");

		String expectedContent = sb.toString();

		_assertEquals(_parseHtml(expectedContent), _parseHtml(content));
	}

	@Test(expected = NoSuchFileEntryException.class)
	public void testUpdateContentWithNonexistingFileEntryReference()
		throws Exception {

		long tempFileEntryId = RandomTestUtil.randomLong();

		String tempFileEntryImgTag = _getTempEntryAttachmentFileEntryImgTag(
			tempFileEntryId, _TEMP_FILE_ENTRY_IMAGE_URL);

		String originalContent =
			"<p>Sample Text</p><a href=\"www.liferay.com\">" +
				tempFileEntryImgTag + "</a>";

		_attachmentContentUpdater.updateContent(
			originalContent, ContentTypes.TEXT_HTML, tempFileEntry -> null);
	}

	@Test
	public void testUpdateContentWithSingleImgTag() throws Exception {
		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_getInputStream(), ContentTypes.IMAGE_JPEG);

		String tempFileEntryImgTag = _getTempEntryAttachmentFileEntryImgTag(
			tempFileEntry.getFileEntryId(), _TEMP_FILE_ENTRY_IMAGE_URL);

		String originalContent =
			"<p>Sample Text</p><a href=\"www.liferay.com\">" +
				tempFileEntryImgTag + "</a>";

		FileEntry newFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_getInputStream(), ContentTypes.IMAGE_JPEG);

		String content = _attachmentContentUpdater.updateContent(
			originalContent, ContentTypes.TEXT_HTML,
			fileEntry -> {
				if (fileEntry.getFileEntryId() ==
						tempFileEntry.getFileEntryId()) {

					return newFileEntry;
				}

				return null;
			});

		String fileEntryURL = PortletFileRepositoryUtil.getPortletFileEntryURL(
			null, newFileEntry, StringPool.BLANK);

		String expectedContent =
			"<p>Sample Text</p><a href=\"www.liferay.com\"><img src=\"" +
				fileEntryURL + "\" /></a>";

		_assertEquals(_parseHtml(expectedContent), _parseHtml(content));
	}

	@Test
	public void testUpdateContentWithoutImgTag() throws Exception {
		String originalContent =
			"<p>Sample Text</p><a href=\"www.liferay.com\"></a>";

		String content = _attachmentContentUpdater.updateContent(
			originalContent, ContentTypes.TEXT_HTML, tempFileEntry -> null);

		String expectedContent = originalContent;

		Assert.assertEquals(expectedContent, content);
	}

	private void _assertEquals(Element expectedElement, Element actualElement) {
		Assert.assertEquals(
			String.format("Unexpected tag name for element %s", actualElement),
			expectedElement.getName(), actualElement.getName());

		for (Attribute expectedAttribute : expectedElement.attributes()) {
			Attribute actualAttribute = actualElement.attribute(
				expectedAttribute.getName());

			Assert.assertNotNull(
				String.format(
					"Missing attribute %s on element %s",
					expectedAttribute.getName(), actualElement),
				actualAttribute);

			Assert.assertEquals(
				String.format(
					"Unexpected value for attribute %s on element %s",
					expectedAttribute.getName(), actualElement),
				expectedAttribute.getValue(), actualAttribute.getValue());
		}

		if (Objects.equals(actualElement.getName(), "img")) {
			Assert.assertNull(
				"The editor attribute must not be included in the final image",
				actualElement.attribute(
					EditorConstants.ATTRIBUTE_DATA_IMAGE_ID));
		}

		_assertEquals(expectedElement.elements(), actualElement.elements());
	}

	private void _assertEquals(
		List<Element> expectedElements, List<Element> actualElements) {

		Assert.assertEquals(
			"Wrong number of elements", expectedElements.size(),
			actualElements.size());

		for (int i = 0; i < expectedElements.size(); i++) {
			_assertEquals(expectedElements.get(i), actualElements.get(i));
		}
	}

	private InputStream _getInputStream() {
		Class<?> clazz = getClass();

		return clazz.getResourceAsStream(
			"com/liferay/upload/dependencies/liferay.png");
	}

	private String _getTempEntryAttachmentFileEntryImgTag(
		long dataImageId, String url) {

		return _getTempEntryAttachmentFileEntryImgTag(
			dataImageId, url, new HashMap<>());
	}

	private String _getTempEntryAttachmentFileEntryImgTag(
		long dataImageId, String url, Map<String, String> attributes) {

		StringBundler sb = new StringBundler(7 + attributes.size());

		sb.append("<img ");

		attributes.forEach(
			(name, value) -> sb.append(
				String.format("%s=\"%s\" ", name, value)));

		sb.append(EditorConstants.ATTRIBUTE_DATA_IMAGE_ID);
		sb.append("=\"");
		sb.append(dataImageId);
		sb.append("\" src=\"");
		sb.append(url);
		sb.append("\"/>");

		return sb.toString();
	}

	private List<Element> _parseHtml(String html) throws Exception {
		Document document = SAXReaderUtil.read("<div>" + html + "</div>");

		Element rootElement = document.getRootElement();

		return rootElement.elements();
	}

	private static final String _TEMP_FILE_ENTRY_IMAGE_URL =
		"www.liferay.com/temp_logo";

	@Inject
	private AttachmentContentUpdater _attachmentContentUpdater;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}
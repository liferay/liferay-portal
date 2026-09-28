/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.external.shortcut.resolver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.resolver.DLVideoExternalShortcutResolver;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.frontend.editor.embed.EditorEmbedProvider;
import com.liferay.frontend.editor.embed.constants.EditorEmbedProviderTypeConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class DLVideoExternalShortcutResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testResolveFromAnEditorEmbedProvider() {
		Bundle bundle = FrameworkUtil.getBundle(
			DLVideoExternalShortcutResolverTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<EditorEmbedProvider>
			editorEmbedProviderServiceRegistration =
				bundleContext.registerService(
					EditorEmbedProvider.class,
					new EditorEmbedProvider() {

						@Override
						public String getId() {
							return "test";
						}

						@Override
						public String getTpl() {
							return "<iframe>{embedId}</iframe>";
						}

						@Override
						public String[] getURLSchemes() {
							return new String[] {
								"http:\\/\\/test\\.example\\/(.*)"
							};
						}

					},
					MapUtil.singletonDictionary(
						"type", EditorEmbedProviderTypeConstants.VIDEO));

		try {
			Assert.assertEquals(
				"<iframe>VIDEO_ID</iframe>",
				_renderHTML("http://test.example/VIDEO_ID"));
			Assert.assertEquals(
				"<iframe>VIDEO_ID&quot;&gt;&lt;b&gt;</iframe>",
				_renderHTML("http://test.example/VIDEO_ID\"><b>"));
		}
		finally {
			editorEmbedProviderServiceRegistration.unregister();
		}
	}

	@Test
	public void testResolveFromFacebook() {
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowFullScreen=\"true\" allowTransparency=\"true\" ",
				"frameborder=\"0\" height=\"315\" ",
				"src=\"https://www.facebook.com/plugins/video.php?height=315&",
				"href=https%3A%2F%2Fwww.facebook.com%2Fwatch%2F%3Fv%3DVIDEO_ID",
				"&show_text=0&width=560\" scrolling=\"no\" style=\"border: ",
				"none; overflow: hidden;\" width=\"560\"></iframe>"),
			_renderHTML("https://www.facebook.com/watch/?v=VIDEO_ID"));
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowFullScreen=\"true\" allowTransparency=\"true\" ",
				"frameborder=\"0\" height=\"315\" ",
				"src=\"https://www.facebook.com/plugins/video.php?height=315&",
				"href=https%3A%2F%2Fwww.facebook.com%2FUSER_ID%2Fvideos%2F",
				"VIDEO_ID&show_text=0&width=560\" scrolling=\"no\" ",
				"style=\"border: none; overflow: hidden;\" width=\"560\">",
				"</iframe>"),
			_renderHTML("https://www.facebook.com/USER_ID/videos/VIDEO_ID"));
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowFullScreen=\"true\" allowTransparency=\"true\" ",
				"frameborder=\"0\" height=\"315\" ",
				"src=\"https://www.facebook.com/plugins/video.php?height=315&",
				"href=https%3A%2F%2Fm.facebook.com%2Fwatch%2F%3Fv%3DVIDEO_ID&",
				"show_text=0&width=560\" scrolling=\"no\" style=\"border: ",
				"none; overflow: hidden;\" width=\"560\"></iframe>"),
			_renderHTML("https://m.facebook.com/watch/?v=VIDEO_ID"));
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowFullScreen=\"true\" allowTransparency=\"true\" ",
				"frameborder=\"0\" height=\"315\" ",
				"src=\"https://www.facebook.com/plugins/video.php?height=315&",
				"href=https%3A%2F%2Ffb.watch%2FVIDEO_ID%2F&",
				"show_text=0&width=560\" scrolling=\"no\" style=\"border: ",
				"none; overflow: hidden;\" width=\"560\"></iframe>"),
			_renderHTML("https://fb.watch/VIDEO_ID/"));
	}

	@Test
	public void testResolveFromFileVersion() throws Exception {
		_group = GroupTestUtil.addGroup();

		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowfullscreen frameborder=\"0\" height=\"315\" ",
				"mozallowfullscreen src=\"https://player.vimeo.com/video",
				"/VIDEO_ID\" webkitallowfullscreen width=\"560\"></iframe>"),
			_renderHTML(
				_addFileEntry("<b>HTML</b>", "https://vimeo.com/VIDEO_ID")));
		Assert.assertEquals(
			StringPool.BLANK,
			_renderHTML(
				_addFileEntry("<b>HTML</b>", "https://test.example/VIDEO_ID")));
	}

	@Test
	public void testResolveFromTwitch() {
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowfullscreen=\"true\" frameborder=\"0\" ",
				"height=\"315\" src=\"https://player.twitch.tv",
				"/?autoplay=false&video=VIDEO_ID&parent=", _HOST,
				"\" scrolling=\"no\" width=\"560\" ></iframe>"),
			_renderHTML("https://www.twitch.tv/videos/VIDEO_ID"));
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowfullscreen=\"true\" frameborder=\"0\" ",
				"height=\"315\" src=\"https://player.twitch.tv",
				"/?autoplay=false&channel=CHANNEL_ID&parent=", _HOST,
				"\" scrolling=\"no\" width=\"560\" ></iframe>"),
			_renderHTML("https://www.twitch.tv/CHANNEL_ID"));
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowfullscreen=\"true\" frameborder=\"0\" ",
				"height=\"315\" src=\"https://player.twitch.tv",
				"/?autoplay=false&video=VIDEO_ID&quot;&gt;&lt;b&gt;&parent=",
				_HOST, "\" scrolling=\"no\" width=\"560\" ></iframe>"),
			_renderHTML("https://www.twitch.tv/videos/VIDEO_ID\"><b>"));
	}

	@Test
	public void testResolveFromVimeo() {
		String expectedIframe = StringBundler.concat(
			"<iframe allowfullscreen frameborder=\"0\" height=\"315\" ",
			"mozallowfullscreen src=\"https://player.vimeo.com/video",
			"/VIDEO_ID\" webkitallowfullscreen width=\"560\"></iframe>");

		Assert.assertEquals(
			expectedIframe, _renderHTML("https://vimeo.com/VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe,
			_renderHTML("https://vimeo.com/album/ALBUM_ID/video/VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe,
			_renderHTML("https://vimeo.com/channels/CHANNEL_ID/VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe,
			_renderHTML("https://vimeo.com/groups/GROUP_ID/videos/VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe,
			_renderHTML(
				"https://vimeo.com/showcase/SHOWCASE_ID/video/VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe,
			_renderHTML("https://player.vimeo.com/video/VIDEO_ID"));

		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allowfullscreen frameborder=\"0\" height=\"315\" ",
				"mozallowfullscreen src=\"https://player.vimeo.com/video",
				"/VIDEO_ID&quot;&gt;&lt;b&gt;\" webkitallowfullscreen ",
				"width=\"560\"></iframe>"),
			_renderHTML("https://vimeo.com/VIDEO_ID\"><b>"));
	}

	@Test
	public void testResolveFromYouTube() {
		String expectedIframe = StringBundler.concat(
			"<iframe allow=\"autoplay; encrypted-media\" allowfullscreen ",
			"height=\"315\" frameborder=\"0\" ",
			"src=\"https://www.youtube.com/embed",
			"/VIDEO_ID?rel=0\" width=\"560\"></iframe>");

		Assert.assertEquals(
			expectedIframe,
			_renderHTML("https://www.youtube.com/watch?v=VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe, _renderHTML("https://youtu.be/VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe,
			_renderHTML(
				"https://www.youtube.com/watch?v=VIDEO_ID&ab_channel=" +
					"CHANNEL_ID"));
		Assert.assertEquals(
			expectedIframe,
			_renderHTML(
				"https://www.youtube.com/watch?feature=player_embedded&v=" +
					"VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe, _renderHTML("https://youtube.com/e/VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe, _renderHTML("https://youtube.com/v/VIDEO_ID"));
		Assert.assertEquals(
			expectedIframe,
			_renderHTML("https://www.youtube.com/embed/VIDEO_ID?rel=0"));

		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allow=\"autoplay; encrypted-media\" allowfullscreen ",
				"height=\"315\" frameborder=\"0\" ",
				"src=\"https://www.youtube.com/embed",
				"/VIDEO_ID?rel=0&start=61\" width=\"560\"></iframe>"),
			_renderHTML("https://www.youtube.com/watch?v=VIDEO_ID&t=61"));
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allow=\"autoplay; encrypted-media\" allowfullscreen ",
				"height=\"315\" frameborder=\"0\" ",
				"src=\"https://www.youtube.com/embed",
				"/VIDEO_ID?rel=0&start=61%22%3E%3Cb%3E\" width=\"560\">",
				"</iframe>"),
			_renderHTML("https://www.youtube.com/watch?v=VIDEO_ID&t=61\"><b>"));
		Assert.assertEquals(
			StringBundler.concat(
				"<iframe allow=\"autoplay; encrypted-media\" allowfullscreen ",
				"height=\"315\" frameborder=\"0\" ",
				"src=\"https://www.youtube.com/embed",
				"/VIDEO_ID&quot;&gt;&lt;b&gt;?rel=0\" width=\"560\"></iframe>"),
			_renderHTML("https://www.youtube.com/watch?v=VIDEO_ID\"><b>"));
	}

	private FileEntry _addFileEntry(String html, String url) throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getFileEntryType(
				companyGroup.getGroupId(), "DL_VIDEO_EXTERNAL_SHORTCUT");

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		DDMStructure ddmStructure = ddmStructures.get(0);

		DDMFormValues ddmFormValues = new DDMFormValues(
			_ddmBeanTranslator.translate(ddmStructure.getDDMForm()));

		ddmFormValues.addAvailableLocale(LocaleUtil.getSiteDefault());
		ddmFormValues.addDDMFormFieldValue(
			_createDDMFormFieldValue("HTML", html));
		ddmFormValues.addDDMFormFieldValue(
			_createDDMFormFieldValue("URL", url));
		ddmFormValues.setDefaultLocale(LocaleUtil.getSiteDefault());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute(
			DDMFormValues.class.getName() + StringPool.POUND +
				ddmStructure.getStructureId(),
			ddmFormValues);
		serviceContext.setAttribute(
			"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, TestDataConstants.TEST_BYTE_ARRAY, null, null,
			null, serviceContext);
	}

	private DDMFormFieldValue _createDDMFormFieldValue(
		String name, String value) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName(name);

		LocalizedValue localizedValue = new LocalizedValue(
			LocaleUtil.getSiteDefault());

		localizedValue.addString(LocaleUtil.getSiteDefault(), value);

		ddmFormFieldValue.setValue(localizedValue);

		return ddmFormFieldValue;
	}

	private String _renderHTML(FileEntry fileEntry) throws Exception {
		DLVideoExternalShortcut dlVideoExternalShortcut =
			_dlVideoExternalShortcutResolver.resolve(
				fileEntry.getFileVersion());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader("Host", _HOST);

		return dlVideoExternalShortcut.renderHTML(mockHttpServletRequest);
	}

	private String _renderHTML(String url) {
		DLVideoExternalShortcut dlVideoExternalShortcut =
			_dlVideoExternalShortcutResolver.resolve(url);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader("Host", _HOST);

		return dlVideoExternalShortcut.renderHTML(mockHttpServletRequest);
	}

	private static final String _HOST = "localhost";

	@Inject
	private DDMBeanTranslator _ddmBeanTranslator;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private DLVideoExternalShortcutResolver _dlVideoExternalShortcutResolver;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

}
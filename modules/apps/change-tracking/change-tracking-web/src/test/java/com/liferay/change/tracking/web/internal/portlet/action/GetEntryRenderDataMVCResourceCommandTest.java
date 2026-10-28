/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Noor Najjar
 */
public class GetEntryRenderDataMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.isValidPortalDomain(
				Mockito.anyLong(), Mockito.eq("localhost"))
		).thenReturn(
			true
		);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.doAnswer(
			invocation -> new String[] {
				invocation.getArgument(0, String.class), StringPool.BLANK
			}
		).when(
			portal
		).stripURLAnchor(
			Mockito.anyString(), Mockito.anyString()
		);

		portalUtil.setPortal(portal);

		ReflectionTestUtil.setFieldValue(
			_getEntryRenderDataMVCResourceCommand, "_portal", portal);
	}

	@Test
	public void testAddPreviewCTCollectionId() {
		String html = "<p><img src=\"/documents/d/guest/image\" /></p>";

		Assert.assertEquals(
			StringBundler.concat(
				"<p><img src=\"/documents/d/guest/image?p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID, "\" /></p>"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
		Assert.assertEquals(
			StringBundler.concat(
				"<p><img src=\"/documents/d/guest/image?p_l_mode=preview",
				"&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION, "\" /></p>"),
			_addPreviewCTCollectionId(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, html));

		html =
			"<a href=\"http://localhost:8080/documents/20/0/image/uuid?t=1\">" +
				"image</a>";

		Assert.assertEquals(
			StringBundler.concat(
				"<a href=\"http://localhost:8080/documents/20/0/image/uuid?t=1",
				"&p_l_mode=preview&previewCTCollectionId=", _CT_COLLECTION_ID,
				"\">image</a>"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
		Assert.assertEquals(
			StringBundler.concat(
				"<a href=\"http://localhost:8080/documents/20/0/image/uuid?t=1",
				"&p_l_mode=preview&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION, "\">image</a>"),
			_addPreviewCTCollectionId(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, html));

		html =
			"<img src=\"/documents/portlet_file_entry/20/image.png/uuid\" />";

		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"/documents/portlet_file_entry/20/image.png/uuid",
				"?p_l_mode=preview&previewCTCollectionId=", _CT_COLLECTION_ID,
				"\" />"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"/documents/portlet_file_entry/20/image.png/uuid",
				"?p_l_mode=preview&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION, "\" />"),
			_addPreviewCTCollectionId(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, html));

		html = "<img src=\"/documents/20/uuid\" />";

		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"/documents/20/uuid?p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID, "\" />"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));

		html = "<a href=\"/documents/30?download=true\">image</a>";

		Assert.assertEquals(
			StringBundler.concat(
				"<a href=\"/documents/30?download=true&p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID, "\">image</a>"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
	}

	@Test
	public void testAddPreviewCTCollectionIdWithAdaptiveMediaURL() {
		String html = StringBundler.concat(
			"<picture><source media=\"(max-width:1000px)\" srcset=\"/o",
			"/adaptive-media/image/20/Preview-1000x0/image.png?t=1\" />",
			"<img src=\"/documents/20/0/image.png/uuid\" /></picture>");

		Assert.assertEquals(
			StringBundler.concat(
				"<picture><source media=\"(max-width:1000px)\" srcset=\"/o",
				"/adaptive-media/image/20/Preview-1000x0/image.png?t=1",
				"&p_l_mode=preview&previewCTCollectionId=", _CT_COLLECTION_ID,
				"\" /><img src=\"/documents/20/0/image.png/uuid",
				"?p_l_mode=preview&previewCTCollectionId=", _CT_COLLECTION_ID,
				"\" /></picture>"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
		Assert.assertEquals(
			StringBundler.concat(
				"<picture><source media=\"(max-width:1000px)\" srcset=\"/o",
				"/adaptive-media/image/20/Preview-1000x0/image.png?t=1",
				"&p_l_mode=preview&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION, "\" />",
				"<img src=\"/documents/20/0/image.png/uuid",
				"?p_l_mode=preview&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION, "\" /></picture>"),
			_addPreviewCTCollectionId(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, html));

		html =
			"<img srcset=\"https://example.com/o/adaptive-media/image/20" +
				"/Preview-1000x0/image.png\" />";

		Assert.assertEquals(
			html, _addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
	}

	@Test
	public void testAddPreviewCTCollectionIdWithDocumentsInURL() {
		String html = "<a href=\"/web/guest/documents/reports\">reports</a>";

		Assert.assertEquals(
			html, _addPreviewCTCollectionId(_CT_COLLECTION_ID, html));

		html = "<a href=\"/web/guest/documents/d/reports\">reports</a>";

		Assert.assertEquals(
			html, _addPreviewCTCollectionId(_CT_COLLECTION_ID, html));

		html = "<a href=\"/documents/reports\">reports</a>";

		Assert.assertEquals(
			html, _addPreviewCTCollectionId(_CT_COLLECTION_ID, html));

		html = "<a href=\"/documents/dreports\">reports</a>";

		Assert.assertEquals(
			html, _addPreviewCTCollectionId(_CT_COLLECTION_ID, html));

		html = "<a href=\"/documents/2024reports\">reports</a>";

		Assert.assertEquals(
			html, _addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
	}

	@Test
	public void testAddPreviewCTCollectionIdWithExistingParameter() {
		String html =
			"<img src=\"/documents/d/guest/image?previewCTCollectionId=1\" />";

		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"/documents/d/guest/image?p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID, "\" />"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"/documents/d/guest/image?p_l_mode=preview",
				"&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION, "\" />"),
			_addPreviewCTCollectionId(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, html));

		html =
			"<img src=\"/documents/d/guest/image?previewCTCollectionId=1&" +
				"t=1\" />";

		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"/documents/d/guest/image?t=1&p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID, "\" />"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));

		html =
			"<img src=\"/documents/d/guest/image?t=1&" +
				"previewCTCollectionId=1\" />";

		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"/documents/d/guest/image?t=1&p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID, "\" />"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));

		html =
			"<img src=\"/documents/d/guest/image?t=1&amp;" +
				"previewCTCollectionId=1\" />";

		Assert.assertEquals(
			StringBundler.concat(
				"<img src=\"/documents/d/guest/image?t=1&p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID, "\" />"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
	}

	@Test
	public void testAddPreviewCTCollectionIdWithExternalURL() {
		String html =
			"<img src=\"https://example.com/documents/20/0/image/uuid\" />";

		Assert.assertEquals(
			html, _addPreviewCTCollectionId(_CT_COLLECTION_ID, html));

		html = "<img src=\"//example.com/documents/20/0/image/uuid\" />";

		Assert.assertEquals(
			html, _addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
	}

	@Test
	public void testAddPreviewCTCollectionIdWithHDAdaptiveMediaURL() {
		String html = StringBundler.concat(
			"<source srcset=\"/o/adaptive-media/image/20/Preview-1000x0",
			"/image.png, /o/adaptive-media/image/20/Preview-1000x0-2x",
			"/image.png 2x\" />");

		Assert.assertEquals(
			StringBundler.concat(
				"<source srcset=\"/o/adaptive-media/image/20/Preview-1000x0",
				"/image.png?p_l_mode=preview&previewCTCollectionId=",
				_CT_COLLECTION_ID,
				", /o/adaptive-media/image/20/Preview-1000x0-2x/image.png",
				"?p_l_mode=preview&previewCTCollectionId=", _CT_COLLECTION_ID,
				" 2x\" />"),
			_addPreviewCTCollectionId(_CT_COLLECTION_ID, html));
		Assert.assertEquals(
			StringBundler.concat(
				"<source srcset=\"/o/adaptive-media/image/20/Preview-1000x0",
				"/image.png?p_l_mode=preview&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION,
				", /o/adaptive-media/image/20/Preview-1000x0-2x/image.png",
				"?p_l_mode=preview&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION, " 2x\" />"),
			_addPreviewCTCollectionId(
				CTConstants.CT_COLLECTION_ID_PRODUCTION, html));
	}

	@Test
	public void testAddPreviewCTCollectionIdWithNullHTML() {
		Assert.assertNull(_addPreviewCTCollectionId(_CT_COLLECTION_ID, null));
	}

	@Test
	public void testAddUnifiedPreviewCTCollectionId() {
		String html = StringBundler.concat(
			"<p><span class=\"diff-html-removed\"><img ",
			"src=\"/documents/d/guest/x\" changeType=\"diff-removed-image\" />",
			"</span><span class=\"diff-html-added\"><img ",
			"src=\"/documents/d/guest/y\" changeType=\"diff-added-image\" />",
			"</span><img src=\"/documents/d/guest/z\" /></p>");

		Assert.assertEquals(
			StringBundler.concat(
				"<p><span class=\"diff-html-removed\"><img ",
				"src=\"/documents/d/guest/x?p_l_mode=preview",
				"&previewCTCollectionId=",
				CTConstants.CT_COLLECTION_ID_PRODUCTION,
				"\" changeType=\"diff-removed-image\" /></span><span ",
				"class=\"diff-html-added\"><img ",
				"src=\"/documents/d/guest/y?p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID,
				"\" changeType=\"diff-added-image\" /></span><img ",
				"src=\"/documents/d/guest/z?p_l_mode=preview",
				"&previewCTCollectionId=", _CT_COLLECTION_ID, "\" /></p>"),
			ReflectionTestUtil.invoke(
				_getEntryRenderDataMVCResourceCommand,
				"_addUnifiedPreviewCTCollectionId",
				new Class<?>[] {
					long.class, String.class, long.class, long.class
				},
				_COMPANY_ID, html, CTConstants.CT_COLLECTION_ID_PRODUCTION,
				_CT_COLLECTION_ID));
	}

	private String _addPreviewCTCollectionId(long ctCollectionId, String html) {
		return ReflectionTestUtil.invoke(
			_getEntryRenderDataMVCResourceCommand, "_addPreviewCTCollectionId",
			new Class<?>[] {long.class, long.class, String.class}, _COMPANY_ID,
			ctCollectionId, html);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _CT_COLLECTION_ID = RandomTestUtil.randomLong();

	private final GetEntryRenderDataMVCResourceCommand
		_getEntryRenderDataMVCResourceCommand =
			new GetEntryRenderDataMVCResourceCommand();

}
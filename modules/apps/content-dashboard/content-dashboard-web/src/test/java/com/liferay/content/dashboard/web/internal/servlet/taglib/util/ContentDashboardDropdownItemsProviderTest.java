/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.servlet.taglib.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Cristina González
 */
public class ContentDashboardDropdownItemsProviderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_language = new LanguageImpl();

		_mockResourceURL();

		_portal = Mockito.mock(Portal.class);

		Mockito.when(
			_portal.getCurrentURL(Mockito.any(PortletRequest.class))
		).thenReturn(
			StringPool.BLANK
		);
	}

	@Test
	public void testGetEditURL() {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.LOCALE, LocaleUtil.US);
		mockLiferayPortletRenderRequest.setAttribute(
			"null-" + WebKeys.CURRENT_PORTLET_URL, new MockLiferayPortletURL());

		ContentDashboardDropdownItemsProvider
			contentDashboardDropdownItemsProvider =
				new ContentDashboardDropdownItemsProvider(
					_language, mockLiferayPortletRenderRequest,
					new MockLiferayPortletRenderResponse(), _portal);

		DropdownItem dropdownItem = _findFirstDropdownItem(
			contentDashboardDropdownItemsProvider.getDropdownItems(
				_getContentDashboardItem(
					Collections.singletonList(
						_getContentDashboardItemAction(
							"edit", ContentDashboardItemAction.Type.EDIT,
							"validURL")))),
			curDropdownItem -> Objects.equals(
				String.valueOf(curDropdownItem.get("label")), "edit"));

		Assert.assertNotNull(dropdownItem);
		Assert.assertEquals(
			"validURL", String.valueOf(dropdownItem.get("href")));
	}

	@Test
	public void testGetViewInPanelURL() {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.LOCALE, LocaleUtil.US);
		mockLiferayPortletRenderRequest.setAttribute(
			"null-" + WebKeys.CURRENT_PORTLET_URL, new MockLiferayPortletURL());

		ContentDashboardDropdownItemsProvider
			contentDashboardDropdownItemsProvider =
				new ContentDashboardDropdownItemsProvider(
					_language, mockLiferayPortletRenderRequest,
					new MockLiferayPortletRenderResponse(), _portal);

		DropdownItem dropdownItem = _findFirstDropdownItem(
			contentDashboardDropdownItemsProvider.getDropdownItems(
				_getContentDashboardItem(
					Collections.singletonList(
						_getContentDashboardItemAction(
							"viewInPanel",
							ContentDashboardItemAction.Type.VIEW_IN_PANEL,
							"validURL")))),
			curDropdownItem -> Objects.equals(
				String.valueOf(curDropdownItem.get("label")), "viewInPanel"));

		Assert.assertNotNull(dropdownItem);

		Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
			"data");

		Assert.assertEquals("showMetrics", String.valueOf(data.get("action")));
		Assert.assertEquals("validURL", String.valueOf(data.get("fetchURL")));
	}

	@Test
	public void testGetViewURL() {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.LOCALE, LocaleUtil.US);
		mockLiferayPortletRenderRequest.setAttribute(
			"null-" + WebKeys.CURRENT_PORTLET_URL, new MockLiferayPortletURL());

		ContentDashboardDropdownItemsProvider
			contentDashboardDropdownItemsProvider =
				new ContentDashboardDropdownItemsProvider(
					_language, mockLiferayPortletRenderRequest,
					new MockLiferayPortletRenderResponse(), _portal);

		DropdownItem dropdownItems = _findFirstDropdownItem(
			contentDashboardDropdownItemsProvider.getDropdownItems(
				_getContentDashboardItem(
					Collections.singletonList(
						_getContentDashboardItemAction(
							"view", ContentDashboardItemAction.Type.VIEW,
							"validURL")))),
			curDropdownItem -> Objects.equals(
				String.valueOf(curDropdownItem.get("label")), "view"));

		Assert.assertNotNull(dropdownItems);
		Assert.assertEquals(
			"validURL", String.valueOf(dropdownItems.get("href")));
	}

	private static void _mockResourceURL() {
		Mockito.mockStatic(ResourceURLBuilder.class);

		ResourceURLBuilder.ResourceURLStep resourceURLStep = Mockito.mock(
			ResourceURLBuilder.ResourceURLStep.class);

		Mockito.when(
			ResourceURLBuilder.createResourceURL(
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			resourceURLStep
		);

		ResourceURLBuilder.AfterBackURLStep afterBackURLStep = Mockito.mock(
			ResourceURLBuilder.AfterBackURLStep.class);

		Mockito.when(
			resourceURLStep.setBackURL(Mockito.anyString())
		).thenReturn(
			afterBackURLStep
		);

		Mockito.when(
			resourceURLStep.setBackURL(
				Mockito.any(ResourceURLBuilder.UnsafeSupplier.class))
		).thenReturn(
			afterBackURLStep
		);

		ResourceURLBuilder.AfterParameterStep afterParameterStep = Mockito.mock(
			ResourceURLBuilder.AfterParameterStep.class);

		Mockito.when(
			afterBackURLStep.setParameter(
				Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			afterParameterStep
		);

		Mockito.when(
			afterParameterStep.setParameter(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			afterParameterStep
		);

		ResourceURLBuilder.AfterResourceIDStep afterResourceIDStep =
			Mockito.mock(ResourceURLBuilder.AfterResourceIDStep.class);

		Mockito.when(
			afterParameterStep.setResourceID(Mockito.anyString())
		).thenReturn(
			afterResourceIDStep
		);

		Mockito.when(
			afterResourceIDStep.buildString()
		).thenReturn(
			StringPool.BLANK
		);
	}

	private DropdownItem _findFirstDropdownItem(
		List<DropdownItem> dropdownItems, Predicate<DropdownItem> predicate) {

		for (DropdownItem curDropdownItem : dropdownItems) {
			if (predicate.test(curDropdownItem)) {
				return curDropdownItem;
			}
		}

		return null;
	}

	private ContentDashboardItem _getContentDashboardItem(
		List<ContentDashboardItemAction> contentDashboardItemActions) {

		return new ContentDashboardItem() {

			@Override
			public List<AssetCategory> getAssetCategories() {
				return Collections.emptyList();
			}

			@Override
			public List<AssetCategory> getAssetCategories(long vocabularyId) {
				return Collections.emptyList();
			}

			@Override
			public List<AssetTag> getAssetTags() {
				return Collections.emptyList();
			}

			@Override
			public List<Locale> getAvailableLocales() {
				return null;
			}

			@Override
			public List<ContentDashboardItemAction>
				getContentDashboardItemActions(
					HttpServletRequest httpServletRequest,
					ContentDashboardItemAction.Type... types) {

				return ListUtil.filter(
					contentDashboardItemActions,
					contentDashboardItemAction -> ArrayUtil.contains(
						types, contentDashboardItemAction.getType()));
			}

			@Override
			public ContentDashboardItemSubtype
				getContentDashboardItemSubtype() {

				return null;
			}

			@Override
			public Date getCreateDate() {
				return null;
			}

			@Override
			public ContentDashboardItemAction
				getDefaultContentDashboardItemAction(
					HttpServletRequest httpServletRequest) {

				for (ContentDashboardItemAction contentDashboardItemAction :
						contentDashboardItemActions) {

					if (contentDashboardItemAction != null) {
						return contentDashboardItemAction;
					}
				}

				return null;
			}

			@Override
			public Locale getDefaultLocale() {
				return null;
			}

			@Override
			public String getDescription(Locale locale) {
				return "Description";
			}

			@Override
			public long getId() {
				return 123456;
			}

			@Override
			public InfoItemReference getInfoItemReference() {
				return new InfoItemReference(
					RandomTestUtil.randomString(), RandomTestUtil.randomLong());
			}

			@Override
			public List<ContentDashboardItemVersion>
				getLatestContentDashboardItemVersions(Locale locale) {

				return null;
			}

			@Override
			public Date getModifiedDate() {
				return null;
			}

			@Override
			public String getScopeName(Locale locale) {
				return null;
			}

			@Override
			public List<SpecificInformation<?>> getSpecificInformationList(
				Locale locale) {

				return Collections.emptyList();
			}

			@Override
			public String getTitle(Locale locale) {
				return null;
			}

			@Override
			public String getTypeLabel(Locale locale) {
				return "Web Content";
			}

			@Override
			public long getUserId() {
				return 0;
			}

			@Override
			public String getUserName() {
				return RandomTestUtil.randomString();
			}

			@Override
			public boolean isViewable(HttpServletRequest httpServletRequest) {
				return false;
			}

		};
	}

	private ContentDashboardItemAction _getContentDashboardItemAction(
		String label, ContentDashboardItemAction.Type type, String url) {

		return new ContentDashboardItemAction() {

			@Override
			public String getIcon() {
				return null;
			}

			@Override
			public String getLabel(Locale locale) {
				return label;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public Type getType() {
				return type;
			}

			@Override
			public String getURL() {
				return url;
			}

			@Override
			public String getURL(Locale locale) {
				return getURL();
			}

		};
	}

	private static Language _language;
	private static Portal _portal;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorCriterionHandler;
import com.liferay.item.selector.ItemSelectorRendering;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewRenderer;
import com.liferay.item.selector.constants.ItemSelectorPortletKeys;
import com.liferay.item.selector.web.internal.item.selector.FlickrItemSelectorCriterion;
import com.liferay.item.selector.web.internal.item.selector.MediaItemSelectorCriterion;
import com.liferay.item.selector.web.internal.util.ItemSelectorCriterionSerializerImpl;
import com.liferay.item.selector.web.internal.util.ItemSelectorKeyUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Iván Zaera
 * @author Roberto Díaz
 */
public class ItemSelectorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_flickrItemSelectorCriterion = new FlickrItemSelectorCriterion();

		_flickrItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			_testURLItemSelectorReturnType);

		_stubItemSelectorCriterionSerializerImpl.activate(_bundleContext);

		_stubItemSelectorCriterionSerializerImpl.addItemSelectorReturnType(
			_testFileEntryItemSelectorReturnType);
		_stubItemSelectorCriterionSerializerImpl.addItemSelectorReturnType(
			_testStringItemSelectorReturnType);
		_stubItemSelectorCriterionSerializerImpl.addItemSelectorReturnType(
			_testURLItemSelectorReturnType);

		ReflectionTestUtil.setFieldValue(
			_stubItemSelectorCriterionSerializerImpl, "_jsonFactory",
			new JSONFactoryImpl());

		_itemSelectorImpl = new ItemSelectorImpl();

		ReflectionTestUtil.setFieldValue(
			_itemSelectorImpl, "_itemSelectionCriterionSerializer",
			_stubItemSelectorCriterionSerializerImpl);
		ReflectionTestUtil.setFieldValue(
			_itemSelectorImpl, "_portal", new PortalImpl());

		_mediaItemSelectorCriterion = new MediaItemSelectorCriterion();

		_mediaItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new TestFileEntryItemSelectorReturnType(),
			_testURLItemSelectorReturnType);
		_mediaItemSelectorCriterion.setFileExtension("jpg");
		_mediaItemSelectorCriterion.setMaxSize(2048);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		_flickrItemSelectorCriterionHandlerServiceRegistration =
			_bundleContext.registerService(
				(Class<ItemSelectorCriterionHandler<?>>)
					(Class<?>)ItemSelectorCriterionHandler.class,
				new FlickrItemSelectorCriterionHandler(), null);
		_mediaItemSelectorCriterionHandlerServiceRegistration =
			_bundleContext.registerService(
				(Class<ItemSelectorCriterionHandler<?>>)
					(Class<?>)ItemSelectorCriterionHandler.class,
				new MediaItemSelectorCriterionHandler(), null);

		_itemSelectorImpl.activate(_bundleContext);
	}

	@AfterClass
	public static void tearDownClass() {
		_flickrItemSelectorCriterionHandlerServiceRegistration.unregister();
		_mediaItemSelectorCriterionHandlerServiceRegistration.unregister();

		_itemSelectorImpl.deactivate();
	}

	@Test
	public void testGetItemSelectedEventName() {
		String itemSelectorURL = getItemSelectorURL(
			"testItemSelectedEventName", _mediaItemSelectorCriterion,
			_flickrItemSelectorCriterion);

		_setUpItemSelectionCriterionHandlers();

		Assert.assertEquals(
			"testItemSelectedEventName",
			_itemSelectorImpl.getItemSelectedEventName(itemSelectorURL));
	}

	@Test
	public void testGetItemSelectorCriteriaFromItemSelectorURL() {
		String itemSelectorURL = getItemSelectorURL(
			StringUtil.randomString(), _mediaItemSelectorCriterion,
			_flickrItemSelectorCriterion);

		_setUpItemSelectionCriterionHandlers();

		List<ItemSelectorCriterion> itemSelectorCriteria =
			_itemSelectorImpl.getItemSelectorCriteria(itemSelectorURL);

		Assert.assertEquals(
			itemSelectorCriteria.toString(), 2, itemSelectorCriteria.size());

		MediaItemSelectorCriterion mediaItemSelectorCriterion =
			(MediaItemSelectorCriterion)itemSelectorCriteria.get(0);

		Assert.assertEquals(
			"jpg", mediaItemSelectorCriterion.getFileExtension());
		Assert.assertEquals(2048, mediaItemSelectorCriterion.getMaxSize());

		List<ItemSelectorReturnType> desiredItemSelectorReturnTypes =
			mediaItemSelectorCriterion.getDesiredItemSelectorReturnTypes();

		Assert.assertEquals(
			desiredItemSelectorReturnTypes.toString(), 2,
			desiredItemSelectorReturnTypes.size());
		Assert.assertTrue(
			desiredItemSelectorReturnTypes.get(0) instanceof
				TestFileEntryItemSelectorReturnType);
		Assert.assertTrue(
			desiredItemSelectorReturnTypes.get(1) instanceof
				TestURLItemSelectorReturnType);

		Assert.assertTrue(
			itemSelectorCriteria.get(1) instanceof FlickrItemSelectorCriterion);
	}

	@Test
	public void testGetItemSelectorParameters() {
		Map<String, String[]> parameters =
			_itemSelectorImpl.getItemSelectorParameters(
				"itemSelectedEventName", _mediaItemSelectorCriterion,
				_flickrItemSelectorCriterion);

		Assert.assertEquals(
			"itemSelectedEventName",
			parameters.get(ItemSelectorImpl.PARAMETER_ITEM_SELECTED_EVENT_NAME)
				[0]);

		String mediaItemSelectorCriterionKey =
			ItemSelectorKeyUtil.getItemSelectorCriterionKey(
				MediaItemSelectorCriterion.class);
		String flickrItemSelectorCriterionKey =
			ItemSelectorKeyUtil.getItemSelectorCriterionKey(
				FlickrItemSelectorCriterion.class);

		Assert.assertEquals(
			mediaItemSelectorCriterionKey + StringPool.COMMA +
				flickrItemSelectorCriterionKey,
			parameters.get(ItemSelectorImpl.PARAMETER_CRITERIA)[0]);

		Assert.assertNull(parameters.get("0_desiredItemSelectorReturnTypes"));
		Assert.assertNotNull(parameters.get("0_json")[0]);
		Assert.assertNotNull(parameters.get("1_json")[0]);
		Assert.assertEquals(parameters.toString(), 4, parameters.size());
	}

	@Test
	public void testGetItemSelectorRendering() {
		_setUpItemSelectionCriterionHandlers();

		ItemSelectorRendering itemSelectorRendering =
			getItemSelectorRendering();

		Assert.assertEquals(
			"itemSelectedEventName",
			itemSelectorRendering.getItemSelectedEventName());

		List<ItemSelectorViewRenderer> itemSelectorViewRenderers =
			itemSelectorRendering.getItemSelectorViewRenderers();

		ItemSelectorViewRenderer mediaItemSelectorViewRenderer =
			itemSelectorViewRenderers.get(0);

		MediaItemSelectorCriterion mediaItemSelectorCriterion =
			(MediaItemSelectorCriterion)
				mediaItemSelectorViewRenderer.getItemSelectorCriterion();

		Assert.assertEquals(
			_mediaItemSelectorCriterion.getFileExtension(),
			mediaItemSelectorCriterion.getFileExtension());
		Assert.assertEquals(
			_mediaItemSelectorCriterion.getMaxSize(),
			mediaItemSelectorCriterion.getMaxSize());

		Assert.assertTrue(
			(ItemSelectorView<?>)
				mediaItemSelectorViewRenderer.getItemSelectorView() instanceof
					MediaItemSelectorView);

		ItemSelectorViewRenderer flickrItemSelectorViewRenderer =
			itemSelectorViewRenderers.get(1);

		FlickrItemSelectorCriterion flickrItemSelectorCriterion =
			(FlickrItemSelectorCriterion)
				flickrItemSelectorViewRenderer.getItemSelectorCriterion();

		Assert.assertEquals(
			_flickrItemSelectorCriterion.getUser(),
			flickrItemSelectorCriterion.getUser());

		Assert.assertTrue(
			(ItemSelectorView<?>)
				flickrItemSelectorViewRenderer.getItemSelectorView() instanceof
					FlickrItemSelectorView);
		Assert.assertEquals(
			itemSelectorViewRenderers.toString(), 2,
			itemSelectorViewRenderers.size());
	}

	protected ItemSelectorRendering getItemSelectorRendering() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			Mockito.mock(RequestBackedPortletURLFactory.class);

		LiferayPortletURL mockLiferayPortletURL = Mockito.mock(
			LiferayPortletURL.class);

		Mockito.when(
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				Mockito.anyString(), Mockito.any(Group.class),
				Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			mockLiferayPortletURL
		);

		Map<String, String[]> parameters =
			_itemSelectorImpl.getItemSelectorParameters(
				"itemSelectedEventName", _mediaItemSelectorCriterion,
				_flickrItemSelectorCriterion);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		themeDisplay.setScopeGroupId(12345);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			new GroupImpl()
		);

		return _itemSelectorImpl.getItemSelectorRendering(
			requestBackedPortletURLFactory, parameters, themeDisplay);
	}

	protected String getItemSelectorURL(
		String itemSelectedEventName,
		ItemSelectorCriterion... itemSelectorCriteria) {

		Map<String, String[]> itemSelectorParameters =
			_itemSelectorImpl.getItemSelectorParameters(
				itemSelectedEventName, itemSelectorCriteria);

		String itemSelectorURL = StringBundler.concat(
			"http://localhost/select/",
			StringUtil.merge(
				TransformUtil.transform(
					itemSelectorCriteria,
					itemSelectorParameter ->
						ItemSelectorKeyUtil.getItemSelectorCriterionKey(
							itemSelectorParameter.getClass()),
					String.class),
				StringPool.COMMA),
			StringPool.SLASH, itemSelectedEventName,
			"?p_p_state=popup&p_p_mode=view");

		String namespace = PortalUtil.getPortletNamespace(
			ItemSelectorPortletKeys.ITEM_SELECTOR);

		for (Map.Entry<String, String[]> entry :
				itemSelectorParameters.entrySet()) {

			itemSelectorURL = HttpComponentsUtil.addParameter(
				itemSelectorURL, namespace + entry.getKey(),
				entry.getValue()[0]);
		}

		return itemSelectorURL;
	}

	private void _setUpItemSelectionCriterionHandlers() {
		_flickrItemSelectorCriterionHandlerServiceRegistration =
			_bundleContext.registerService(
				(Class<ItemSelectorCriterionHandler<?>>)
					(Class<?>)ItemSelectorCriterionHandler.class,
				new FlickrItemSelectorCriterionHandler(), null);
		_mediaItemSelectorCriterionHandlerServiceRegistration =
			_bundleContext.registerService(
				(Class<ItemSelectorCriterionHandler<?>>)
					(Class<?>)ItemSelectorCriterionHandler.class,
				new MediaItemSelectorCriterionHandler(), null);

		_itemSelectorImpl.activate(_bundleContext);
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static FlickrItemSelectorCriterion _flickrItemSelectorCriterion;
	private static ServiceRegistration<ItemSelectorCriterionHandler<?>>
		_flickrItemSelectorCriterionHandlerServiceRegistration;
	private static ItemSelectorImpl _itemSelectorImpl;
	private static MediaItemSelectorCriterion _mediaItemSelectorCriterion;
	private static ServiceRegistration<ItemSelectorCriterionHandler<?>>
		_mediaItemSelectorCriterionHandlerServiceRegistration;
	private static final StubItemSelectorCriterionSerializerImpl
		_stubItemSelectorCriterionSerializerImpl =
			new StubItemSelectorCriterionSerializerImpl();
	private static final ItemSelectorReturnType
		_testFileEntryItemSelectorReturnType =
			new TestFileEntryItemSelectorReturnType();
	private static final ItemSelectorReturnType
		_testStringItemSelectorReturnType =
			new TestStringItemSelectorReturnType();
	private static final ItemSelectorReturnType _testURLItemSelectorReturnType =
		new TestURLItemSelectorReturnType();

	private static class StubItemSelectorCriterionSerializerImpl
		extends ItemSelectorCriterionSerializerImpl {

		@Override
		public void addItemSelectorReturnType(
			ItemSelectorReturnType itemSelectorReturnType) {

			super.addItemSelectorReturnType(itemSelectorReturnType);
		}

		@Override
		protected void activate(BundleContext bundleContext) {
			super.activate(bundleContext);
		}

	}

}
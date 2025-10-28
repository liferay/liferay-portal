/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.FragmentLink;
import com.liferay.headless.admin.site.dto.v1_0.FragmentLinkInlineValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentLinkMappedValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentMappedValueItemContextReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentMappedValueItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentMappedValueItemReference;
import com.liferay.headless.admin.site.dto.v1_0.Mapping;
import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class FragmentLinkUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		_mockGroup(
			_ITEM_GROUP_EXTERNAL_REFERENCE_CODE, _ITEM_GROUP_ID,
			GroupConstants.TYPE_DEPOT);
		_mockGroup(
			_SCOPE_EXTERNAL_REFERENCE_CODE, _SCOPE_GROUP_ID,
			GroupConstants.TYPE_SITE_OPEN);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassNameId(Mockito.anyString())
		).thenReturn(
			_CLASS_NAME_ID
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
		_layoutLocalServiceUtilMockedStatic.close();
		_localizedValueUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	public void testToFragmentLinkInlineValue() throws Exception {
		Map<String, String> localizedValues = HashMapBuilder.put(
			LocaleUtil.SPAIN.toString(), "https://www.liferay.es"
		).put(
			LocaleUtil.US.toString(), "https://www.liferay.com"
		).build();

		_localizedValueUtilMockedStatic.when(
			() -> LocalizedValueUtil.toLocalizedValues(
				Mockito.any(JSONObject.class))
		).thenReturn(
			localizedValues
		);

		Assert.assertEquals(
			_getFragmentLink(null, null, null, localizedValues, null),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"href",
					JSONUtil.put(
						LocaleUtil.SPAIN.toString(), "www.liferay.es"
					).put(
						LocaleUtil.US.toString(), "www.liferay.com"
					)
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedField() throws Exception {
		Assert.assertEquals(
			_getFragmentLink(null, null, "FileEntry_fileName", null, null),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"mappedField", "FileEntry_fileName"
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedJournalArticle() throws Exception {
		String journalArticleExternalReferenceCode =
			RandomTestUtil.randomString();

		InfoItemReference infoItemReference = _mockInfoItemReference();

		ERCInfoItemIdentifier ercInfoItemIdentifier = Mockito.mock(
			ERCInfoItemIdentifier.class);

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			ercInfoItemIdentifier
		);

		Mockito.when(
			ercInfoItemIdentifier.getExternalReferenceCode()
		).thenReturn(
			journalArticleExternalReferenceCode
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.any(long.class))
		).thenReturn(
			JournalArticle.class.getName()
		);

		Assert.assertEquals(
			_getFragmentLink(
				JournalArticle.class.getName(),
				journalArticleExternalReferenceCode, "JournalArticle_title",
				null, null),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId", RandomTestUtil.randomLong()
				).put(
					"classPK", RandomTestUtil.randomLong()
				).put(
					"externalReferenceCode", journalArticleExternalReferenceCode
				).put(
					"fieldId", "JournalArticle_title"
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedJournalArticleDifferentScope()
		throws Exception {

		ERCInfoItemIdentifier ercInfoItemIdentifier = Mockito.mock(
			ERCInfoItemIdentifier.class);

		String journalArticleExternalReferenceCode =
			RandomTestUtil.randomString();

		Mockito.when(
			ercInfoItemIdentifier.getExternalReferenceCode()
		).thenReturn(
			journalArticleExternalReferenceCode
		);

		Mockito.when(
			ercInfoItemIdentifier.getScopeExternalReferenceCode()
		).thenReturn(
			_ITEM_GROUP_EXTERNAL_REFERENCE_CODE
		);

		InfoItemReference infoItemReference = _mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			ercInfoItemIdentifier
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.any(long.class))
		).thenReturn(
			JournalArticle.class.getName()
		);

		Assert.assertEquals(
			_getFragmentLink(
				JournalArticle.class.getName(),
				journalArticleExternalReferenceCode, "JournalArticle_title",
				null,
				new Scope() {
					{
						setExternalReferenceCode(
							_ITEM_GROUP_EXTERNAL_REFERENCE_CODE);
						setType(Type.ASSET_LIBRARY);
					}
				}),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId", RandomTestUtil.randomLong()
				).put(
					"classPK", RandomTestUtil.randomLong()
				).put(
					"externalReferenceCode", journalArticleExternalReferenceCode
				).put(
					"fieldId", "JournalArticle_title"
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedLayout() throws Exception {
		ERCInfoItemIdentifier ercInfoItemIdentifier = Mockito.mock(
			ERCInfoItemIdentifier.class);

		InfoItemReference infoItemReference = _mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			ercInfoItemIdentifier
		);

		String layoutExternalReferenceCode = RandomTestUtil.randomString();

		Mockito.when(
			ercInfoItemIdentifier.getExternalReferenceCode()
		).thenReturn(
			layoutExternalReferenceCode
		);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			_SCOPE_GROUP_ID
		);

		Mockito.when(
			layout.getExternalReferenceCode()
		).thenReturn(
			layoutExternalReferenceCode
		);

		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchLayout(
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong())
		).thenReturn(
			layout
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.any(long.class))
		).thenReturn(
			Layout.class.getName()
		);

		Assert.assertEquals(
			_getFragmentLink(
				Layout.class.getName(), layoutExternalReferenceCode, null, null,
				null),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"externalReferenceCode", layoutExternalReferenceCode
					).put(
						"groupId", RandomTestUtil.randomLong()
					).put(
						"layoutId", RandomTestUtil.randomLong()
					).put(
						"privateLayout", true
					)
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedLayoutDifferentScope()
		throws Exception {

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			_ITEM_GROUP_ID
		);

		long layoutId = RandomTestUtil.randomLong();

		Mockito.when(
			layout.getLayoutId()
		).thenReturn(
			layoutId
		);

		String layoutExternalReferenceCode = RandomTestUtil.randomString();

		Mockito.when(
			layout.getExternalReferenceCode()
		).thenReturn(
			layoutExternalReferenceCode
		);

		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchLayout(
				_ITEM_GROUP_ID, false, layoutId)
		).thenReturn(
			layout
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.any(long.class))
		).thenReturn(
			Layout.class.getName()
		);

		Assert.assertEquals(
			_getFragmentLink(
				Layout.class.getName(), layoutExternalReferenceCode, null, null,
				new Scope() {
					{
						setExternalReferenceCode(
							_ITEM_GROUP_EXTERNAL_REFERENCE_CODE);
						setType(Type.ASSET_LIBRARY);
					}
				}),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"externalReferenceCode", layoutExternalReferenceCode
					).put(
						"groupId", _ITEM_GROUP_ID
					).put(
						"layoutId", layoutId
					).put(
						"privateLayout", false
					).put(
						"scopeExternalReferenceCode",
						_SCOPE_EXTERNAL_REFERENCE_CODE
					)
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedNonexistingJournalArticle()
		throws Exception {

		InfoItemReference infoItemReference = _mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			null
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.any(long.class))
		).thenReturn(
			JournalArticle.class.getName()
		);

		String journalArticleExternalReferenceCode =
			RandomTestUtil.randomString();

		Assert.assertEquals(
			_getFragmentLink(
				JournalArticle.class.getName(),
				journalArticleExternalReferenceCode, "JournalArticle_title",
				null, null),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId", RandomTestUtil.randomLong()
				).put(
					"classPK", RandomTestUtil.randomLong()
				).put(
					"externalReferenceCode", journalArticleExternalReferenceCode
				).put(
					"fieldId", "JournalArticle_title"
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedNonexistingJournalArticleDifferentScope()
		throws Exception {

		InfoItemReference infoItemReference = _mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			null
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.any(long.class))
		).thenReturn(
			JournalArticle.class.getName()
		);

		String journalArticleExternalReferenceCode =
			RandomTestUtil.randomString();

		Assert.assertEquals(
			_getFragmentLink(
				JournalArticle.class.getName(),
				journalArticleExternalReferenceCode, "JournalArticle_title",
				null,
				new Scope() {
					{
						setExternalReferenceCode(
							_ITEM_GROUP_EXTERNAL_REFERENCE_CODE);
						setType(Type.ASSET_LIBRARY);
					}
				}),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId", RandomTestUtil.randomLong()
				).put(
					"classPK", RandomTestUtil.randomLong()
				).put(
					"externalReferenceCode", journalArticleExternalReferenceCode
				).put(
					"fieldId", "JournalArticle_title"
				).put(
					"scopeExternalReferenceCode",
					_ITEM_GROUP_EXTERNAL_REFERENCE_CODE
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedNonexistingLayout() throws Exception {
		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchLayout(
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong())
		).thenReturn(
			null
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.any(long.class))
		).thenReturn(
			Layout.class.getName()
		);

		String layoutExternalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getFragmentLink(
				Layout.class.getName(), layoutExternalReferenceCode, null, null,
				null),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"externalReferenceCode", layoutExternalReferenceCode)
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToFragmentLinkMappedNonexistingLayoutDifferentScope()
		throws Exception {

		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchLayout(
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong())
		).thenReturn(
			null
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.any(long.class))
		).thenReturn(
			Layout.class.getName()
		);

		String layoutExternalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			_getFragmentLink(
				Layout.class.getName(), layoutExternalReferenceCode, null, null,
				new Scope() {
					{
						setExternalReferenceCode(
							_ITEM_GROUP_EXTERNAL_REFERENCE_CODE);
						setType(Type.ASSET_LIBRARY);
					}
				}),
			FragmentLinkUtil.toFragmentLink(
				_COMPANY_ID, _infoItemServiceRegistry,
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"externalReferenceCode", layoutExternalReferenceCode
					).put(
						"scopeExternalReferenceCode",
						_ITEM_GROUP_EXTERNAL_REFERENCE_CODE
					)
				).put(
					"target", "_blank"
				),
				_SCOPE_GROUP_ID));
	}

	@Test
	public void testToJSONObjectInlineValue() throws Exception {
		JSONObject jsonObject = JSONUtil.put(
			LocaleUtil.SPAIN.toString(), "www.liferay.es"
		).put(
			LocaleUtil.US.toString(), "www.liferay.com"
		);

		_localizedValueUtilMockedStatic.when(
			() -> LocalizedValueUtil.toJSONObject(Mockito.any(Map.class))
		).thenReturn(
			jsonObject
		);

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"href", jsonObject
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					null, null, null,
					HashMapBuilder.put(
						LocaleUtil.SPAIN.toString(), "https://www.liferay.es"
					).put(
						LocaleUtil.US.toString(), "https://www.liferay.com"
					).build(),
					null),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedField() throws PortalException {
		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"mappedField", "FileEntry_fileName"
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(null, null, "FileEntry_fileName", null, null),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedJournalArticle() throws Exception {
		ClassPKInfoItemIdentifier classPKInfoItemIdentifier = Mockito.mock(
			ClassPKInfoItemIdentifier.class);

		Long classPK = RandomTestUtil.randomLong();

		Mockito.when(
			classPKInfoItemIdentifier.getClassPK()
		).thenReturn(
			classPK
		);

		InfoItemReference infoItemReference = _mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			classPKInfoItemIdentifier
		);

		String journalArticleExternalReferenceCode =
			RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId", _CLASS_NAME_ID
				).put(
					"classPK", classPK
				).put(
					"externalReferenceCode", journalArticleExternalReferenceCode
				).put(
					"fieldId", "JournalArticle_title"
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					JournalArticle.class.getName(),
					journalArticleExternalReferenceCode, "JournalArticle_title",
					null, null),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedJournalArticleDifferentScope()
		throws Exception {

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier = Mockito.mock(
			ClassPKInfoItemIdentifier.class);

		Long classPK = RandomTestUtil.randomLong();

		Mockito.when(
			classPKInfoItemIdentifier.getClassPK()
		).thenReturn(
			classPK
		);

		InfoItemReference infoItemReference = _mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			classPKInfoItemIdentifier
		);

		String journalArticleExternalReferenceCode =
			RandomTestUtil.randomString();
		String journalArticleScopeExternalReferenceCode =
			RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId", _CLASS_NAME_ID
				).put(
					"classPK", classPK
				).put(
					"externalReferenceCode", journalArticleExternalReferenceCode
				).put(
					"fieldId", "JournalArticle_title"
				).put(
					"scopeExternalReferenceCode",
					journalArticleScopeExternalReferenceCode
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					JournalArticle.class.getName(),
					journalArticleExternalReferenceCode, "JournalArticle_title",
					null,
					new Scope() {
						{
							setExternalReferenceCode(
								journalArticleScopeExternalReferenceCode);
							setType(Type.SITE);
						}
					}),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedLayout() throws Exception {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			_SCOPE_GROUP_ID
		);

		long layoutId = RandomTestUtil.randomLong();

		Mockito.when(
			layout.getLayoutId()
		).thenReturn(
			layoutId
		);

		String layoutUuid = RandomTestUtil.randomString();

		Mockito.when(
			layout.getUuid()
		).thenReturn(
			layoutUuid
		);

		Mockito.when(
			layout.isPrivateLayout()
		).thenReturn(
			true
		);

		String title = RandomTestUtil.randomString();

		Mockito.when(
			layout.getName(Mockito.any(Locale.class))
		).thenReturn(
			title
		);

		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			layout
		);

		String layoutExternalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"externalReferenceCode", layoutExternalReferenceCode
					).put(
						"groupId", _SCOPE_GROUP_ID
					).put(
						"layoutId", layoutId
					).put(
						"layoutUuid", layoutUuid
					).put(
						"privateLayout", true
					).put(
						"title", title
					)
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					Layout.class.getName(), layoutExternalReferenceCode, null,
					null, null),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedLayoutDifferentScope() throws Exception {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			_ITEM_GROUP_ID
		);

		long layoutId = RandomTestUtil.randomLong();

		Mockito.when(
			layout.getLayoutId()
		).thenReturn(
			layoutId
		);

		String layoutUuid = RandomTestUtil.randomString();

		Mockito.when(
			layout.getUuid()
		).thenReturn(
			layoutUuid
		);

		Mockito.when(
			layout.isPrivateLayout()
		).thenReturn(
			true
		);

		String title = RandomTestUtil.randomString();

		Mockito.when(
			layout.getName(Mockito.any(Locale.class))
		).thenReturn(
			title
		);

		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			layout
		);

		String layoutExternalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"externalReferenceCode", layoutExternalReferenceCode
					).put(
						"groupId", _ITEM_GROUP_ID
					).put(
						"layoutId", layoutId
					).put(
						"layoutUuid", layoutUuid
					).put(
						"privateLayout", true
					).put(
						"scopeExternalReferenceCode",
						_ITEM_GROUP_EXTERNAL_REFERENCE_CODE
					).put(
						"title", title
					)
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					Layout.class.getName(), layoutExternalReferenceCode, null,
					null,
					new Scope() {
						{
							setExternalReferenceCode(
								_ITEM_GROUP_EXTERNAL_REFERENCE_CODE);
							setType(Type.ASSET_LIBRARY);
						}
					}),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedNonexistingJournalArticle()
		throws Exception {

		InfoItemReference infoItemReference = _mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			null
		);

		String journalArticleExternalReferenceCode =
			RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId", _CLASS_NAME_ID
				).put(
					"externalReferenceCode", journalArticleExternalReferenceCode
				).put(
					"fieldId", "JournalArticle_title"
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					JournalArticle.class.getName(),
					journalArticleExternalReferenceCode, "JournalArticle_title",
					null, null),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedNonexistingJournalArticleDifferentScope()
		throws Exception {

		InfoItemReference infoItemReference = _mockInfoItemReference();

		Mockito.when(
			infoItemReference.getInfoItemIdentifier()
		).thenReturn(
			null
		);

		String journalArticleExternalReferenceCode =
			RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId", _CLASS_NAME_ID
				).put(
					"externalReferenceCode", journalArticleExternalReferenceCode
				).put(
					"fieldId", "JournalArticle_title"
				).put(
					"scopeExternalReferenceCode",
					_ITEM_GROUP_EXTERNAL_REFERENCE_CODE
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					JournalArticle.class.getName(),
					journalArticleExternalReferenceCode, "JournalArticle_title",
					null,
					new Scope() {
						{
							setExternalReferenceCode(
								_ITEM_GROUP_EXTERNAL_REFERENCE_CODE);
							setType(Type.ASSET_LIBRARY);
						}
					}),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedNonexistingLayout() throws Exception {
		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			null
		);

		String layoutExternalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"externalReferenceCode", layoutExternalReferenceCode)
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					Layout.class.getName(), layoutExternalReferenceCode, null,
					null, null),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	@Test
	public void testToJSONObjectMappedNonexistingLayoutDifferentScope()
		throws Exception {

		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			null
		);

		String layoutExternalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"externalReferenceCode", layoutExternalReferenceCode
					).put(
						"scopeExternalReferenceCode",
						_ITEM_GROUP_EXTERNAL_REFERENCE_CODE
					)
				).put(
					"target", "_blank"
				)
			).toString(),
			FragmentLinkUtil.toJSONObject(
				_COMPANY_ID,
				_getFragmentLink(
					Layout.class.getName(), layoutExternalReferenceCode, null,
					null,
					new Scope() {
						{
							setExternalReferenceCode(
								_ITEM_GROUP_EXTERNAL_REFERENCE_CODE);
							setType(Type.ASSET_LIBRARY);
						}
					}),
				_infoItemServiceRegistry, _SCOPE_GROUP_ID
			).toString());
	}

	private static void _mockGroup(
		String externalReferenceCode, long groupId, int type) {

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.getType()
		).thenReturn(
			type
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(groupId)
		).thenReturn(
			group
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				externalReferenceCode, _COMPANY_ID)
		).thenReturn(
			group
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.getGroup(groupId)
		).thenReturn(
			group
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.getGroupByExternalReferenceCode(
				externalReferenceCode, _COMPANY_ID)
		).thenReturn(
			group
		);
	}

	private FragmentLink _getFragmentLink(
		String className, String externalReferenceCode, String fieldKey,
		Map<String, String> localizedValues, Scope scope) {

		return new FragmentLink() {
			{
				setTarget(Target.BLANK);
				setValue(
					() -> {
						if (localizedValues != null) {
							return _getFragmentLinkInlineValue(localizedValues);
						}

						return _getFragmentLinkMappedValue(
							className, externalReferenceCode, fieldKey, scope);
					});
			}
		};
	}

	private FragmentLinkInlineValue _getFragmentLinkInlineValue(
		Map<String, String> localizedValues) {

		return new FragmentLinkInlineValue() {
			{
				setValue_i18n(localizedValues);
			}
		};
	}

	private FragmentLinkMappedValue _getFragmentLinkMappedValue(
		String className, String externalReferenceCode, String fieldKey,
		Scope scope) {

		FragmentLinkMappedValue fragmentLinkMappedValue =
			new FragmentLinkMappedValue();

		Mapping mapping = new Mapping();

		mapping.setFieldKey(() -> fieldKey);
		mapping.setItemReference(
			() -> _getFragmentMappedValueItemReference(
				className, externalReferenceCode, scope));

		fragmentLinkMappedValue.setMapping(mapping);

		return fragmentLinkMappedValue;
	}

	private FragmentMappedValueItemReference
		_getFragmentMappedValueItemReference(
			String className, String externalReferenceCode, Scope scope) {

		if (Validator.isNull(className)) {
			return new FragmentMappedValueItemContextReference() {
				{
					setContextSource(() -> ContextSource.DISPLAY_PAGE_ITEM);
				}
			};
		}

		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference =
				new FragmentMappedValueItemExternalReference();

		fragmentMappedValueItemExternalReference.setClassName(className);
		fragmentMappedValueItemExternalReference.setExternalReferenceCode(
			externalReferenceCode);

		if (scope != null) {
			fragmentMappedValueItemExternalReference.setScope(scope);
		}

		return fragmentMappedValueItemExternalReference;
	}

	private InfoItemReference _mockInfoItemReference() throws Exception {
		InfoItemObjectProvider<Object> infoItemObjectProvider = Mockito.mock(
			InfoItemObjectProvider.class);

		InfoItemDetailsProvider<Object> infoItemDetailsProvider = Mockito.mock(
			InfoItemDetailsProvider.class);

		Mockito.when(
			_infoItemServiceRegistry.getFirstInfoItemService(
				Mockito.any(), Mockito.any(), Mockito.any())
		).thenReturn(
			infoItemObjectProvider, infoItemDetailsProvider
		);

		Mockito.when(
			infoItemObjectProvider.getInfoItem(
				Mockito.any(InfoItemIdentifier.class))
		).thenReturn(
			Mockito.mock(Object.class)
		);

		InfoItemDetails infoItemDetails = Mockito.mock(InfoItemDetails.class);

		Mockito.when(
			infoItemDetailsProvider.getInfoItemDetails(
				Mockito.eq(_SCOPE_GROUP_ID), Mockito.any(Class.class),
				Mockito.any())
		).thenReturn(
			infoItemDetails
		);

		InfoItemReference infoItemReference = Mockito.mock(
			InfoItemReference.class);

		Mockito.when(
			infoItemDetails.getInfoItemReference()
		).thenReturn(
			infoItemReference
		);

		return infoItemReference;
	}

	private static final long _CLASS_NAME_ID = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _ITEM_GROUP_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _ITEM_GROUP_ID = RandomTestUtil.randomLong();

	private static final String _SCOPE_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _SCOPE_GROUP_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private static final InfoItemServiceRegistry _infoItemServiceRegistry =
		Mockito.mock(InfoItemServiceRegistry.class);
	private static final MockedStatic<LayoutLocalServiceUtil>
		_layoutLocalServiceUtilMockedStatic = Mockito.mockStatic(
			LayoutLocalServiceUtil.class);
	private static final MockedStatic<LocalizedValueUtil>
		_localizedValueUtilMockedStatic = Mockito.mockStatic(
			LocalizedValueUtil.class);
	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

}
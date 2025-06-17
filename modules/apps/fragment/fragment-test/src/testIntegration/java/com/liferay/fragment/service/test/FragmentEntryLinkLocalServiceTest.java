/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.exception.DuplicateFragmentEntryLinkExternalReferenceCodeException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.InputStream;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_serviceContext.setRequest(_getMockHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_fragmentEntry = _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), null,
			"Fragment Name", StringPool.BLANK, "<div>test</div>",
			StringPool.BLANK, false, _read("configuration-light.json"), null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_fragmentEntryWithFreeMarker =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", StringPool.BLANK,
				_read("fragment-freemarker.html"), StringPool.BLANK, false,
				_read("configuration-light.json"), null, 0, false, false,
				FragmentConstants.TYPE_SECTION, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			}
		};
	}

	@After
	public void tearDown() throws Exception {
		_configurationProvider.saveCompanyConfiguration(
			FragmentServiceConfiguration.class, _group.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"propagateChanges", false
			).build());
	}

	@Test
	public void testAddFragmentEntryLink() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				_read("editable-values-light-modified.json"), StringPool.BLANK,
				0, null, _fragmentEntry.getType(), _serviceContext);

		Assert.assertNotNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));

		Assert.assertTrue(
			Validator.isNotNull(fragmentEntryLink.getExternalReferenceCode()));

		Assert.assertEquals(_layout.getPlid(), fragmentEntryLink.getPlid());

		Assert.assertEquals(
			_fragmentEntry.getCss(), fragmentEntryLink.getCss());

		Assert.assertEquals(_fragmentEntry.getJs(), fragmentEntryLink.getJs());

		Assert.assertEquals(
			_fragmentEntry.getConfiguration(),
			fragmentEntryLink.getConfiguration());

		Assert.assertEquals(
			_fragmentEntry.getHtml(), fragmentEntryLink.getHtml());

		Assert.assertEquals(
			_read("expected-editable-values-light-modified.json"),
			fragmentEntryLink.getEditableValues());
	}

	@Test(
		expected = DuplicateFragmentEntryLinkExternalReferenceCodeException.class
	)
	public void testAddFragmentEntryLinkWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), 0, _fragmentEntry.getFragmentEntryId(),
			_defaultSegmentsExperienceId, _layout.getPlid(),
			_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
			_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
			_read("editable-values-light-modified.json"), StringPool.BLANK, 0,
			null, _fragmentEntry.getType(), _serviceContext);
		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), 0, _fragmentEntry.getFragmentEntryId(),
			_defaultSegmentsExperienceId, _layout.getPlid(),
			_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
			_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
			_read("editable-values-light-modified.json"), StringPool.BLANK, 0,
			null, _fragmentEntry.getType(), _serviceContext);
	}

	@Test
	public void testAddFragmentEntryLinkWithFreeMarkerDisabledEmptyRendererKey()
		throws Exception {

		try (AutoCloseable autoCloseable =
				_getFreeMarkerDisabledAutoCloseable()) {

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
					_fragmentEntryWithFreeMarker.getFragmentEntryId(),
					_defaultSegmentsExperienceId, _layout.getPlid(),
					_fragmentEntryWithFreeMarker.getCss(),
					_fragmentEntryWithFreeMarker.getHtml(),
					_fragmentEntryWithFreeMarker.getJs(),
					_fragmentEntryWithFreeMarker.getConfiguration(),
					StringPool.BLANK, StringPool.BLANK, 0, null,
					_fragmentEntryWithFreeMarker.getType(), _serviceContext);

			Assert.assertNotNull(
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId()));

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				fragmentEntryLink.getEditableValues());

			JSONObject editableJSONObject = jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

			Assert.assertEquals(1, editableJSONObject.length());
		}
	}

	@Test
	public void testAddFragmentEntryLinkWithFreeMarkerDisabledNotEmptyRendererKey()
		throws Exception {

		try (AutoCloseable autoCloseable =
				_getFreeMarkerDisabledAutoCloseable()) {

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
					_fragmentEntryWithFreeMarker.getFragmentEntryId(),
					_defaultSegmentsExperienceId, _layout.getPlid(),
					_fragmentEntryWithFreeMarker.getCss(),
					_fragmentEntryWithFreeMarker.getHtml(),
					_fragmentEntryWithFreeMarker.getJs(),
					_fragmentEntryWithFreeMarker.getConfiguration(),
					StringPool.BLANK, StringPool.BLANK, 0, "TABS",
					_fragmentEntryWithFreeMarker.getType(), _serviceContext);

			Assert.assertNotNull(
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId()));

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				fragmentEntryLink.getEditableValues());

			JSONObject editableJSONObject = jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

			Assert.assertEquals(3, editableJSONObject.length());
		}
	}

	@Test
	public void testAddFragmentEntryLinkWithFreeMarkerEnabledEmptyRendererKey()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntryWithFreeMarker.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntryWithFreeMarker.getCss(),
				_fragmentEntryWithFreeMarker.getHtml(),
				_fragmentEntryWithFreeMarker.getJs(),
				_fragmentEntryWithFreeMarker.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				_fragmentEntryWithFreeMarker.getType(), _serviceContext);

		Assert.assertNotNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject editableJSONObject = jsonObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertEquals(3, editableJSONObject.length());
	}

	@Test
	public void testAddFragmentEntryLinkWithFreeMarkerEnabledNotEmptyRendererKey()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntryWithFreeMarker.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntryWithFreeMarker.getCss(),
				_fragmentEntryWithFreeMarker.getHtml(),
				_fragmentEntryWithFreeMarker.getJs(),
				_fragmentEntryWithFreeMarker.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				_fragmentEntryWithFreeMarker.getType(), _serviceContext);

		Assert.assertNotNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject editableJSONObject = jsonObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertEquals(3, editableJSONObject.length());
	}

	@Test
	public void testAddMultipleFragmentEntryLinks() throws PortalException {
		List<FragmentEntryLink> originalFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinks(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			_fragmentEntry.getFragmentEntryId(), _defaultSegmentsExperienceId,
			_layout.getPlid(), _fragmentEntry.getCss(),
			_fragmentEntry.getHtml(), _fragmentEntry.getJs(),
			_fragmentEntry.getConfiguration(), StringPool.BLANK,
			StringPool.BLANK, 0, null, _fragmentEntry.getType(),
			_serviceContext);

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			_fragmentEntry.getFragmentEntryId(), _defaultSegmentsExperienceId,
			_layout.getPlid(), _fragmentEntry.getCss(),
			_fragmentEntry.getHtml(), _fragmentEntry.getJs(),
			_fragmentEntry.getConfiguration(), StringPool.BLANK,
			StringPool.BLANK, 1, null, _fragmentEntry.getType(),
			_serviceContext);

		List<FragmentEntryLink> actualFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinks(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			actualFragmentEntryLinks.toString(),
			originalFragmentEntryLinks.size() + 2,
			actualFragmentEntryLinks.size());
	}

	@Test
	public void testDeleteFragmentEntryLink() throws PortalException {
		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				_fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));
	}

	@Test
	public void testDeleteFragmentEntryLinkByExternalReferenceCode()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				_read("editable-values-light-modified.json"), StringPool.BLANK,
				0, null, _fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink.getExternalReferenceCode(),
			fragmentEntryLink.getGroupId());

		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));
	}

	@Test
	public void testDeleteFragmentEntryLinks() throws PortalException {
		FragmentEntryLink fragmentEntryLink1 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				_fragmentEntry.getType(), _serviceContext);
		FragmentEntryLink fragmentEntryLink2 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				_fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLinkLocalService.deleteFragmentEntryLinks(
			_group.getGroupId());

		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink1.getFragmentEntryLinkId()));
		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink2.getFragmentEntryLinkId()));
	}

	@Test
	public void testFragmentEntryLinksDeleted() throws PortalException {
		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				_fragmentEntry.getType(), _serviceContext);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(fragmentEntryLinks.contains(fragmentEntryLink));

		_fragmentEntryLinkLocalService.updateDeleted(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(), true);

		fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(fragmentEntryLinks.contains(fragmentEntryLink));

		_fragmentEntryLinkLocalService.updateDeleted(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(), false);

		fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(fragmentEntryLinks.contains(fragmentEntryLink));
	}

	@Test
	public void testGetAllFragmentEntryLinksByFragmentEntryId()
		throws Exception {

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLinkToLayout();
		FragmentEntryLink fragmentEntryLink2 =
			_addFragmentEntryLinkToLayoutPageTemplateEntry();

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(fragmentEntryLinks.contains(fragmentEntryLink1));
		Assert.assertTrue(fragmentEntryLinks.contains(fragmentEntryLink2));
	}

	@Test
	public void testGetAllFragmentEntryLinksCountByFragmentEntryId()
		throws Exception {

		_addFragmentEntryLinkToLayout();
		_addFragmentEntryLinkToLayoutPageTemplateEntry();

		Assert.assertEquals(
			2,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId()));
	}

	@Test
	public void testGetLayoutFragmentEntryLinksByFragmentEntryId()
		throws Exception {

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLinkToLayout();
		FragmentEntryLink fragmentEntryLink2 =
			_addFragmentEntryLinkToLayoutPageTemplateEntry();

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getLayoutFragmentEntryLinksByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(fragmentEntryLinks.contains(fragmentEntryLink1));
		Assert.assertFalse(fragmentEntryLinks.contains(fragmentEntryLink2));
	}

	@Test
	public void testGetLayoutFragmentEntryLinksCountByFragmentEntryId()
		throws Exception {

		_addFragmentEntryLinkToLayout();
		_addFragmentEntryLinkToLayoutPageTemplateEntry();

		Assert.assertEquals(
			1,
			_fragmentEntryLinkLocalService.
				getLayoutFragmentEntryLinksCountByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId()));
	}

	@Test
	public void testGetLayoutPageTemplateFragmentEntryLinksByFragmentEntryId()
		throws Exception {

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLinkToLayout();
		FragmentEntryLink fragmentEntryLink2 =
			_addFragmentEntryLinkToLayoutPageTemplateEntry();

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getLayoutPageTemplateFragmentEntryLinksByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId(),
					LayoutPageTemplateEntryTypeConstants.BASIC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(fragmentEntryLinks.contains(fragmentEntryLink1));
		Assert.assertTrue(fragmentEntryLinks.contains(fragmentEntryLink2));
	}

	@Test
	public void testGetLayoutPageTemplateFragmentEntryLinksCountByFragmentEntryId()
		throws Exception {

		_addFragmentEntryLinkToLayout();
		_addFragmentEntryLinkToLayoutPageTemplateEntry();

		Assert.assertEquals(
			1,
			_fragmentEntryLinkLocalService.
				getLayoutPageTemplateFragmentEntryLinksCountByFragmentEntryId(
					_group.getGroupId(), _fragmentEntry.getFragmentEntryId(),
					LayoutPageTemplateEntryTypeConstants.BASIC));
	}

	@Test
	public void testUpdateFragmentEntryLinkPosition() throws PortalException {
		FragmentEntryLink fragmentEntryLink1 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 1, null,
				_fragmentEntry.getType(), _serviceContext);
		FragmentEntryLink fragmentEntryLink2 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 2, null,
				_fragmentEntry.getType(), _serviceContext);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), _layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 2, fragmentEntryLinks.size());
		Assert.assertEquals(fragmentEntryLink1, fragmentEntryLinks.get(0));
		Assert.assertEquals(fragmentEntryLink2, fragmentEntryLinks.get(1));

		fragmentEntryLink1 =
			_fragmentEntryLinkLocalService.updateFragmentEntryLink(
				TestPropsValues.getUserId(),
				fragmentEntryLink1.getFragmentEntryLinkId(), 3);

		Assert.assertEquals(3, fragmentEntryLink1.getPosition());

		fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), _layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 2, fragmentEntryLinks.size());
		Assert.assertEquals(fragmentEntryLink2, fragmentEntryLinks.get(0));
		Assert.assertEquals(fragmentEntryLink1, fragmentEntryLinks.get(1));
	}

	@Test
	public void testUpdateFragmentEntryLinkWithoutPropagation()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			FragmentServiceConfiguration.class, _group.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"propagateChanges", false
			).build());

		String configuration = _read("configuration-light.json");

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", RandomTestUtil.randomString(),
				"<div>test</div>", RandomTestUtil.randomString(), false,
				configuration, null, 0, false, false,
				FragmentConstants.TYPE_SECTION, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				_read("editable-values-light-modified.json"), StringPool.BLANK,
				0, null, fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			StringPool.BLANK, StringUtil.randomString(),
			StringUtil.randomString(), false, _read("configuration-dark.json"),
			StringPool.BLANK, fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		fragmentEntryLink = _fragmentEntryLinkLocalService.getFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(
			fragmentEntry.getHtml(), fragmentEntryLink.getHtml());
		Assert.assertEquals(fragmentEntry.getCss(), fragmentEntryLink.getCss());
		Assert.assertEquals(fragmentEntry.getJs(), fragmentEntryLink.getJs());
		Assert.assertEquals(
			fragmentEntry.getConfiguration(),
			fragmentEntryLink.getConfiguration());

		Assert.assertEquals(
			configuration, fragmentEntryLink.getConfiguration());

		Assert.assertEquals(
			_read("expected-editable-values-light-modified.json"),
			fragmentEntryLink.getEditableValues());
	}

	@Test
	public void testUpdateFragmentEntryLinkWithPropagation() throws Exception {
		_configurationProvider.saveCompanyConfiguration(
			FragmentServiceConfiguration.class, _group.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"propagateChanges", true
			).build());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", StringPool.BLANK, StringUtil.randomString(),
				StringPool.BLANK, false, _read("configuration-light.json"),
				null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				_read("editable-values-light-modified.json"), StringPool.BLANK,
				0, null, fragmentEntry.getType(), _serviceContext);

		String newCSS = StringUtil.randomString();
		String newHTML = StringUtil.randomString();
		String newJS = StringUtil.randomString();

		String newConfiguration = _read("configuration-dark.json");

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), "Fragment Name", newCSS,
			newHTML, newJS, false, newConfiguration, StringPool.BLANK,
			fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		fragmentEntryLink = _fragmentEntryLinkLocalService.getFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(newCSS, fragmentEntryLink.getCss());
		Assert.assertEquals(newHTML, fragmentEntryLink.getHtml());
		Assert.assertEquals(newJS, fragmentEntryLink.getJs());
		Assert.assertEquals(
			newConfiguration, fragmentEntryLink.getConfiguration());

		JSONObject editableValuesJSONObject = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		Assert.assertEquals(
			_objectMapper.readTree(
				_read("expected-editable-values-light-modified.json")),
			_objectMapper.readTree(editableValuesJSONObject.toString()));
	}

	@Test
	@TestInfo("LPS-102130")
	public void testUpdateFragmentEntryLinkWithPropagationAndFreeMarkerVariables()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", StringPool.BLANK,
				_read("fragment-freemarker-variables.html"), StringPool.BLANK,
				false, StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(),
			_read("updated-fragment-freemarker-variables.html"),
			fragmentEntry.getJs(), false, fragmentEntry.getConfiguration(),
			StringPool.BLANK, fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		_fragmentEntryLinkLocalService.updateLatestChanges(
			fragmentEntryLink.getFragmentEntryLinkId());

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(
			_objectMapper.readTree(
				_read(
					"expected-updated-editable-values-freemarker-variables-" +
						"update-latest-changes.json")),
			_objectMapper.readTree(fragmentEntryLink.getEditableValues()));
	}

	@Test
	public void testUpdateFragmentEntryLinkWithPropagationAndNewConfigurationValues()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", StringPool.BLANK,
				_read("fragment-configuration.html"), StringPool.BLANK, false,
				_read("configuration-new-field.json"), null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(),
			_read("updated-fragment-configuration.html"), fragmentEntry.getJs(),
			false, _read("updated-configuration-new-field.json"),
			StringPool.BLANK, fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		_fragmentEntryLinkLocalService.updateLatestChanges(
			fragmentEntryLink.getFragmentEntryLinkId());

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(
			_objectMapper.readTree(
				_read("updated-configuration-new-field.json")),
			_objectMapper.readTree(fragmentEntryLink.getConfiguration()));
	}

	@Test
	@TestInfo("LPS-118241")
	public void testUpdateFragmentEntryLinkWithPropagationAndNewEditableItems()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", StringPool.BLANK,
				_read("fragment-editable.html"), StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(), _read("updated-fragment-editable.html"),
			fragmentEntry.getJs(), false, fragmentEntry.getConfiguration(),
			StringPool.BLANK, fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		_fragmentEntryLinkLocalService.updateLatestChanges(
			fragmentEntryLink.getFragmentEntryLinkId());

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(
			_objectMapper.readTree(
				_read("expected-editable-values-update-latest-changes.json")),
			_objectMapper.readTree(fragmentEntryLink.getEditableValues()));
	}

	@Test
	@TestInfo("LPD-56902")
	public void testUpdateFragmentEntryLinkWithPropagationAndUpdatedDefaultValues()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", StringPool.BLANK,
				_read("fragment-editable-default-values.html"),
				StringPool.BLANK, false, StringPool.BLANK, null, 0, false,
				false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			_read(
				"updated-editable-default-values-update-latest-changes.json"));

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(),
			_read("updated-fragment-editable-default-values.html"),
			fragmentEntry.getJs(), false, fragmentEntry.getConfiguration(),
			StringPool.BLANK, fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		_fragmentEntryLinkLocalService.updateLatestChanges(
			fragmentEntryLink.getFragmentEntryLinkId());

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(
			_objectMapper.readTree(
				_read(
					"expected-editable-default-values-update-latest-" +
						"changes.json")),
			_objectMapper.readTree(fragmentEntryLink.getEditableValues()));
	}

	@Test
	@TestInfo("LPS-118241")
	public void testUpdateFragmentEntryLinkWithPropagationAndUpdatedEditableItems()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", StringPool.BLANK,
				_read("fragment-editable.html"), StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			_read("updated-editable-values-update-latest-changes.json"));

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(), _read("updated-fragment-editable.html"),
			fragmentEntry.getJs(), false, fragmentEntry.getConfiguration(),
			StringPool.BLANK, fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		_fragmentEntryLinkLocalService.updateLatestChanges(
			fragmentEntryLink.getFragmentEntryLinkId());

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(
			_objectMapper.readTree(
				_read(
					"expected-updated-editable-values-update-latest-changes." +
						"json")),
			_objectMapper.readTree(fragmentEntryLink.getEditableValues()));
	}

	@Test
	@TestInfo("LPS-128652")
	public void testUpdateLatestChanges() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				RandomTestUtil.randomString(), StringPool.BLANK,
				_read("fragment-freemarker-with-configuration.html"),
				StringPool.BLANK, false, _read("configuration-tabs.json"), null,
				0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink1 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"title4",
						JSONUtil.put(
							"defaultValue", "title4"
						).put(
							"en_US", RandomTestUtil.randomString()
						))
				).put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put("numberOfTabs", "4")
				).toString(),
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		FragmentEntryLink fragmentEntryLink2 =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLinkLocalService.updateLatestChanges(
			fragmentEntryLink1.getFragmentEntryLinkId());

		fragmentEntryLink2 =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink2.getFragmentEntryLinkId());

		JSONObject configurationValuesJSONObject =
			_jsonFactory.createJSONObject(
				fragmentEntryLink2.getEditableValues());

		JSONObject editableJSONObject =
			configurationValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNull(editableJSONObject.getJSONObject("title4"));
	}

	@Test
	@TestInfo("LPD-50062")
	public void testUpdateLatestChangesWithFragmentEntryResources()
		throws Exception {

		Class<?> clazz = getClass();

		PortletFileRepositoryUtil.addPortletFileEntry(
			null, _fragmentCollection.getGroupId(), TestPropsValues.getUserId(),
			FragmentCollection.class.getName(),
			_fragmentCollection.getFragmentCollectionId(),
			FragmentPortletKeys.FRAGMENT,
			_fragmentCollection.getResourcesFolderId(),
			clazz.getResourceAsStream("dependencies/liferay.png"),
			"liferay.png", ContentTypes.IMAGE_PNG, false);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				"Fragment Name", StringPool.BLANK,
				"<div><img src=\"[resources:liferay]\" /></div>",
				StringPool.BLANK, false, StringPool.BLANK, null, 0, false,
				false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_defaultSegmentsExperienceId, _layout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		Date modifiedDate = _layout.getModifiedDate();

		_fragmentEntryLinkLocalService.updateLatestChanges(
			fragmentEntry, fragmentEntryLink);

		Layout layout = _layoutLocalService.fetchLayout(_layout.getPlid());

		Assert.assertEquals(modifiedDate, layout.getModifiedDate());
	}

	private FragmentEntryLink _addFragmentEntryLinkToLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			_fragmentEntry.getFragmentEntryId(), _defaultSegmentsExperienceId,
			layout.getPlid(), _fragmentEntry.getCss(), _fragmentEntry.getHtml(),
			_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
			StringPool.BLANK, StringPool.BLANK, 0, null,
			_fragmentEntry.getType(), _serviceContext);
	}

	private FragmentEntryLink _addFragmentEntryLinkToLayoutPageTemplateEntry()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(), LayoutPageTemplateEntryTypeConstants.BASIC,
				WorkflowConstants.STATUS_APPROVED);

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			_fragmentEntry.getFragmentEntryId(), _defaultSegmentsExperienceId,
			layoutPageTemplateEntry.getPlid(), _fragmentEntry.getCss(),
			_fragmentEntry.getHtml(), _fragmentEntry.getJs(),
			_fragmentEntry.getConfiguration(), StringPool.BLANK,
			StringPool.BLANK, 0, null, _fragmentEntry.getType(),
			_serviceContext);
	}

	private AutoCloseable _getFreeMarkerDisabledAutoCloseable()
		throws Exception {

		return new CompanyConfigurationTemporarySwapper(
			_group.getCompanyId(),
			"com.liferay.fragment.entry.processor.freemarker.internal." +
				"configuration.FreeMarkerFragmentEntryProcessorConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"enable.freemarker", false
			).build());
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());

		return mockHttpServletRequest;
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationProvider _configurationProvider;

	private long _defaultSegmentsExperienceId;
	private FragmentCollection _fragmentCollection;
	private FragmentEntry _fragmentEntry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private FragmentEntry _fragmentEntryWithFreeMarker;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	private ObjectMapper _objectMapper;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.sales.room.constants.DSRFragmentRendererConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.IndexStatusManagerThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class DSRInitialRequestPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testPortalInstanceRegistered() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group group = _groupLocalService.fetchGroup(
			company.getCompanyId(), GroupConstants.DSR);

		if (group != null) {
			Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/home");

			if (layout != null) {
				_layoutLocalService.deleteLayout(layout);
			}
		}

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		group = _groupLocalService.fetchGroup(
			company.getCompanyId(), GroupConstants.DSR);

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/home"));
		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/rooms"));

		_layoutLocalService.deleteLayout(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/rooms"));

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/home"));
		Assert.assertNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/rooms"));

		_layoutLocalService.deleteLayout(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/home"));

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/home"));
		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/rooms"));

		List<FragmentEntryLink> fragmentEntryLinks = _getFragmentEntryLinks(
			group);

		Assert.assertTrue(
			ListUtil.exists(
				fragmentEntryLinks,
				fragmentEntryLink -> Objects.equals(
					fragmentEntryLink.getRendererKey(),
					DSRFragmentRendererConstants.
						FRAGMENT_RENDERER_KEY_DSR_VIEW_ROOMS)));

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
				fragmentEntryLink);
		}

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		Assert.assertTrue(
			ListUtil.exists(
				_getFragmentEntryLinks(group),
				fragmentEntryLink -> Objects.equals(
					fragmentEntryLink.getRendererKey(),
					DSRFragmentRendererConstants.
						FRAGMENT_RENDERER_KEY_DSR_VIEW_ROOMS)));

		boolean indexReadOnly = IndexStatusManagerThreadLocal.isIndexReadOnly();

		IndexStatusManagerThreadLocal.setIndexReadOnly(true);

		_company = CompanyTestUtil.addCompany(true);

		Assert.assertNotNull(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", _company.getCompanyId()));

		group = _groupLocalService.fetchGroup(
			_company.getCompanyId(), GroupConstants.DSR);

		_layoutLocalService.deleteLayout(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/home"));

		CompanyTestUtil.resetCompanyLocales(
			_company.getCompanyId(),
			ListUtil.fromArray(
				LocaleUtil.ITALY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.ITALY);

		_portalInstanceLifecycleListener.portalInstanceRegistered(_company);

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/home");

		Assert.assertEquals(
			LanguageUtil.get(LocaleUtil.ITALY, "home"),
			layout.getName(LocaleUtil.ITALY));
		Assert.assertEquals(
			LanguageUtil.get(LocaleUtil.SPAIN, "home"),
			layout.getName(LocaleUtil.SPAIN));

		Assert.assertEquals(
			ListUtil.fromArray(
				LanguageUtil.get(LocaleUtil.SPAIN, "home"),
				LanguageUtil.get(LocaleUtil.SPAIN, "digital-sales-rooms")),
			_getEditableFragmentEntryValues(
				"element-text", layout.fetchDraftLayout(), LocaleUtil.SPAIN));
		Assert.assertEquals(
			ListUtil.fromArray(LanguageUtil.get(LocaleUtil.SPAIN, "view-all")),
			_getEditableFragmentEntryValues(
				"link", layout.fetchDraftLayout(), LocaleUtil.SPAIN));

		layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/rooms");

		Assert.assertEquals(
			LanguageUtil.get(LocaleUtil.SPAIN, "rooms"),
			layout.getName(LocaleUtil.SPAIN));

		Assert.assertNotNull(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", _company.getCompanyId()));

		IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly);
	}

	private List<String> _getEditableFragmentEntryValues(
		String key, Layout layout, Locale locale) {

		return TransformUtil.transform(
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid()),
			fragmentEntryLink -> {
				JSONObject jsonObject = JSONUtil.getValueAsJSONObject(
					fragmentEntryLink.getEditableValuesJSONObject(),
					"JSONObject/" +
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					"JSONObject/" + key);

				if (jsonObject == null) {
					return null;
				}

				return jsonObject.getString(LocaleUtil.toLanguageId(locale));
			});
	}

	private List<FragmentEntryLink> _getFragmentEntryLinks(Group group) {
		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/home");

		return _fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
			group.getGroupId(), layout.getPlid());
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(
		filter = "component.name=com.liferay.digital.sales.room.web.internal.instance.lifecycle.DSRInitialRequestPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

}
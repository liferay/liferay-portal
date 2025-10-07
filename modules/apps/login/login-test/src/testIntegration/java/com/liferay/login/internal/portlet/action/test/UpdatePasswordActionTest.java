/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.net.URL;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alvaro Saugar
 */
@FeatureFlag("LPD-6378")
@RunWith(Arquillian.class)
public class UpdatePasswordActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_company = CompanyLocalServiceUtil.getCompany(group.getCompanyId());

		_user = UserTestUtil.addGroupUser(group, RoleConstants.POWER_USER);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		_layoutUtilityPageEntry1 = _addLayoutUtilityPageEntry(
			_FRAGMENT_ENTRY_HTML_1, group, serviceContext);

		_layout1 = _layoutLocalService.fetchLayout(
			_layoutUtilityPageEntry1.getPlid());

		Group guestGroup = _groupLocalService.getGroup(
			_company.getCompanyId(), GroupConstants.GUEST);

		_layoutUtilityPageEntry2 = _addLayoutUtilityPageEntry(
			_FRAGMENT_ENTRY_HTML_2, guestGroup, serviceContext);
	}

	@Test
	public void test() throws Exception {
		_test(false, false, false, false, true);
		_test(false, true, false, true, false);
		_test(true, false, false, false, false);
		_test(true, false, true, false, true);
	}

	private LayoutUtilityPageEntry _addLayoutUtilityPageEntry(
			String fragmentEntryHTML, Group group,
			ServiceContext serviceContext)
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, serviceContext.getUserId(), group.getGroupId(), 0, 0,
				true, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_FORGOT_PASSWORD, 0,
				serviceContext);

		Layout layout = _layoutLocalService.fetchLayout(
			layoutUtilityPageEntry.getPlid());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.addFragmentCollection(
				null, group.getGroupId(), "Fragment Collection",
				StringPool.BLANK, serviceContext);

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), "Fragment Entry", null,
			"<div>" + fragmentEntryHTML + "</div>", null, false, null, null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, group.getGroupId(), 0, fragmentEntry.getFragmentEntryId(),
				defaultSegmentsExperienceId, layout.getPlid(), StringPool.BLANK,
				fragmentEntry.getHtml(), StringPool.BLANK, "{fieldSets: []}",
				StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), serviceContext);

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			(ContainerStyledLayoutStructureItem)
				layoutStructure.addContainerStyledLayoutStructureItem(
					layoutStructure.getMainItemId(), 0);

		containerStyledLayoutStructureItem.setWidthType("fixed");

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			containerStyledLayoutStructureItem.getItemId(), 0);

		JSONObject dataJSONObject = layoutStructure.toJSONObject();

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				serviceContext.getUserId(), group.getGroupId(),
				layout.getPlid(), defaultSegmentsExperienceId,
				dataJSONObject.toString());

		return layoutUtilityPageEntry;
	}

	private boolean _isFragmentRendered(String expectedText, boolean usePlid)
		throws Exception {

		Ticket ticket = _ticketLocalService.addDistinctTicket(
			_user.getCompanyId(), User.class.getName(), _user.getUserId(),
			TicketConstants.TYPE_PASSWORD, null,
			new Date(System.currentTimeMillis() + 3600000),
			new ServiceContext());

		String ticketId = String.valueOf(ticket.getTicketId());
		String ticketKey = ticket.getKey();

		URL url = null;

		if (usePlid) {
			url = new URL(
				StringBundler.concat(
					"http://", _company.getVirtualHostname(),
					":8080/c/portal/update_password?p_l_id=",
					_layout1.getPlid(), "&ticketId=", ticketId, "&ticketId=",
					ticketKey));
		}
		else {
			url = new URL(
				StringBundler.concat(
					"http://", _company.getVirtualHostname(),
					":8080/c/portal/update_password?ticketId=", ticketId,
					"&ticketId=", ticketKey));
		}

		ticket.setKey(PasswordEncryptorUtil.encrypt(ticket.getKey()));

		_ticketLocalService.updateTicket(ticket);

		String content = URLUtil.toString(url);

		return content.contains(expectedText);
	}

	private void _test(
			boolean defaultLayoutUtilityPageEntry1,
			boolean defaultLayoutUtilityPageEntry2,
			boolean expectedFragmentRendered1,
			boolean expectedFragmentRendered2, boolean usePlid)
		throws Exception {

		_layoutUtilityPageEntry1.setDefaultLayoutUtilityPageEntry(
			defaultLayoutUtilityPageEntry1);

		_layoutUtilityPageEntry1 =
			_layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
				_layoutUtilityPageEntry1);

		Assert.assertEquals(
			expectedFragmentRendered1,
			_isFragmentRendered(_FRAGMENT_ENTRY_HTML_1, usePlid));

		_layoutUtilityPageEntry2.setDefaultLayoutUtilityPageEntry(
			defaultLayoutUtilityPageEntry2);

		_layoutUtilityPageEntry2 =
			_layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
				_layoutUtilityPageEntry2);

		Assert.assertEquals(
			expectedFragmentRendered2,
			_isFragmentRendered(_FRAGMENT_ENTRY_HTML_2, usePlid));
	}

	private static final String _FRAGMENT_ENTRY_HTML_1 =
		RandomTestUtil.randomString();

	private static final String _FRAGMENT_ENTRY_HTML_2 =
		RandomTestUtil.randomString();

	private Company _company;

	@Inject
	private FragmentCollectionService _fragmentCollectionService;

	@Inject
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Inject
	private FragmentEntryService _fragmentEntryService;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout1;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	private LayoutUtilityPageEntry _layoutUtilityPageEntry1;
	private LayoutUtilityPageEntry _layoutUtilityPageEntry2;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private TicketLocalService _ticketLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
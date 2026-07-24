/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

import org.junit.Before;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
public abstract class BaseAssigneeSectionDisplayContextTestCase {

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(
			BaseAssigneeSectionDisplayContextTestCase.class);

		httpServletRequest = new MockHttpServletRequest();

		cmpProjectObjectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, cmpProjectObjectEntry);

		themeDisplay = new ThemeDisplay() {
			{
				setCompany(
					_companyLocalService.fetchCompany(
						TestPropsValues.getCompanyId()));
				setLocale(LocaleUtil.US);
				setPathImage(_portal.getPathImage());
				setScopeGroupId(TestPropsValues.getGroupId());
			}
		};

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
	}

	protected User addUser() throws Exception {
		User user = UserTestUtil.addUser();

		user.setPortraitId(RandomTestUtil.nextLong());

		return _userLocalService.updateUser(user);
	}

	protected void assertAssigneeFieldValue(
			Map<String, Object> expectedValueMap, ObjectEntry objectEntry,
			Map<String, Serializable> values)
		throws Exception {

		objectEntry = objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), values,
			ServiceContextTestUtil.getServiceContext());

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, objectEntry);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_jsonFactory.looseSerializeDeep(getProperties()));

		JSONObject valueJSONObject = jsonObject.getJSONObject("value");

		AssertUtils.assertEquals(expectedValueMap, valueJSONObject.toMap());
	}

	protected Map<String, Object> getProperties() throws Exception {
		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(httpServletRequest), "getProperties",
			new Class<?>[0]);
	}

	protected abstract Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception;

	protected ObjectEntry cmpProjectObjectEntry;
	protected HttpServletRequest httpServletRequest;

	@Inject
	protected ObjectEntryLocalService objectEntryLocalService;

	protected ThemeDisplay themeDisplay;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}
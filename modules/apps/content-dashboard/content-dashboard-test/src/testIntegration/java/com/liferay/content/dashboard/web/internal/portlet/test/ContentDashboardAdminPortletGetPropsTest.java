/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletPreferences;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class ContentDashboardAdminPortletGetPropsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.fetchCompany(_group.getCompanyId());
	}

	@Test
	public void testGetPropsWithAssetCategoriesSortedByKey() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category-1",
			assetVocabulary.getVocabularyId(), serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category-2",
			assetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory1.getCategoryId(),
						assetCategory2.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory2.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						String.valueOf(assetVocabulary.getVocabularyId())
					}));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"key", String.valueOf(assetCategory1.getCategoryId())
					).put(
						"name", "category-1"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					),
					JSONUtil.put(
						"key", String.valueOf(assetCategory2.getCategoryId())
					).put(
						"name", "category-2"
					).put(
						"value", 2L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				String.valueOf(props.get("vocabularies")));
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithChildAssetCategoriesSortedByKey()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory1 =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category-1", childAssetVocabulary.getVocabularyId(),
				serviceContext);
		AssetCategory childAssetCategory2 =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category-2", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory1.getCategoryId(),
						childAssetCategory2.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory2.getCategoryId()
					}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						String.valueOf(assetVocabulary.getVocabularyId()),
						String.valueOf(childAssetVocabulary.getVocabularyId())
					}));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"categories",
						JSONUtil.putAll(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory1.getCategoryId())
							).put(
								"name", "child-category-1"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							),
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory2.getCategoryId())
							).put(
								"name", "child-category-2"
							).put(
								"value", 2L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 2L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				String.valueOf(props.get("vocabularies")));
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithChildNoneAssetCategory() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory.getCategoryId()}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory.getCategoryId()
					}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						String.valueOf(assetVocabulary.getVocabularyId()),
						String.valueOf(childAssetVocabulary.getVocabularyId())
					}));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"categories",
						JSONUtil.putAll(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							),
							JSONUtil.put(
								"key", "none"
							).put(
								"name", "No child-vocabulary Specified"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 2L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				String.valueOf(props.get("vocabularies")));
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithMissingAssetVocabulary() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		_assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {childAssetCategory.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						String.valueOf(assetVocabulary.getVocabularyId()),
						String.valueOf(childAssetVocabulary.getVocabularyId())
					}));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertEquals(
				JSONUtil.put(
					JSONUtil.put(
						"key",
						String.valueOf(childAssetCategory.getCategoryId())
					).put(
						"name", "child-category"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "child-vocabulary"
					)
				).toString(),
				String.valueOf(props.get("vocabularies")));
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithMissingChildAssetVocabulary() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						String.valueOf(assetVocabulary.getVocabularyId()),
						String.valueOf(childAssetVocabulary.getVocabularyId())
					}));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertEquals(
				JSONUtil.put(
					JSONUtil.put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				String.valueOf(props.get("vocabularies")));
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithNoneAssetCategory() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {childAssetCategory.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						String.valueOf(assetVocabulary.getVocabularyId()),
						String.valueOf(childAssetVocabulary.getVocabularyId())
					}));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"categories",
						JSONUtil.put(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).put(
					JSONUtil.put(
						"categories",
						JSONUtil.put(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", "none"
					).put(
						"name", "No vocabulary Specified"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				String.valueOf(props.get("vocabularies")));
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	private Map<String, Object> _getData(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminDisplayContext"),
			"getData", new Class<?>[0]);
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			String[] assetVocabularyIds)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());
		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(), "-",
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);
		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		PortletPreferences portletPreferences =
			mockLiferayPortletRenderRequest.getPreferences();

		portletPreferences.setValues("assetVocabularyIds", assetVocabularyIds);

		return mockLiferayPortletRenderRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);

		Locale locale = LocaleUtil.getSiteDefault();

		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLocale(locale);

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setUser(_company.getGuestUser());

		return themeDisplay;
	}

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Company _company;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.web.internal.portlet.ContentDashboardAdminPortlet"
	)
	private Portlet _portlet;

}
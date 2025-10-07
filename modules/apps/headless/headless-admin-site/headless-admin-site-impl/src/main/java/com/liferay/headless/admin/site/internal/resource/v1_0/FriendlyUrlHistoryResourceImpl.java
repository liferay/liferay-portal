/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.friendly.url.model.FriendlyURLEntryLocalizationModel;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.friendly.url.util.comparator.FriendlyURLEntryLocalizationComparator;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.resource.v1_0.FriendlyUrlHistoryResource;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/friendly-url-history.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = FriendlyUrlHistoryResource.class
)
public class FriendlyUrlHistoryResourceImpl
	extends BaseFriendlyUrlHistoryResourceImpl {

	@NestedField(
		parentClass = DisplayPageTemplate.class, value = "friendlyUrlHistory"
	)
	@Override
	public FriendlyUrlHistory getSiteDisplayPageTemplateFriendlyUrlHistory(
			String siteExternalReferenceCode,
			@NestedFieldId(value = "externalReferenceCode") String
				displayPageTemplateExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplateExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (layoutPageTemplateEntry.getType() !=
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

			throw new UnsupportedOperationException();
		}

		return _toFriendlyUrlHistory(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()));
	}

	@NestedField(parentClass = SitePage.class, value = "friendlyUrlHistory")
	@Override
	public FriendlyUrlHistory getSiteSitePageFriendlyUrlHistory(
			String siteExternalReferenceCode,
			@NestedFieldId(value = "externalReferenceCode") String
				sitePageExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout.isDraftLayout() || layout.isTypeAssetDisplay() ||
			layout.isTypeUtility()) {

			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry != null) {
			throw new UnsupportedOperationException();
		}

		return _toFriendlyUrlHistory(layout);
	}

	@NestedField(parentClass = UtilityPage.class, value = "friendlyUrlHistory")
	@Override
	public FriendlyUrlHistory getSiteUtilityPageFriendlyUrlHistory(
			String siteExternalReferenceCode,
			@NestedFieldId(value = "externalReferenceCode") String
				utilityPageExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPageExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		return _toFriendlyUrlHistory(
			_layoutLocalService.getLayout(layoutUtilityPageEntry.getPlid()));
	}

	private JSONObject _getFriendlyUrlPathJSONObject(Layout layout)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		if (!LayoutUtil.isPublished(layout)) {
			return jsonObject;
		}

		long classNameId = _layoutFriendlyURLEntryHelper.getClassNameId(
			layout.isPrivateLayout());

		for (String languageId : layout.getAvailableLanguageIds()) {
			JSONArray jsonArray = JSONUtil.toJSONArray(
				_friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
					layout.getGroupId(), classNameId, layout.getPlid(),
					languageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					_friendlyURLEntryLocalizationComparator),
				FriendlyURLEntryLocalizationModel::getUrlTitle);

			if (jsonArray.length() == 0) {
				continue;
			}

			jsonObject.put(LocaleUtil.toBCP47LanguageId(languageId), jsonArray);
		}

		return jsonObject;
	}

	private FriendlyUrlHistory _toFriendlyUrlHistory(Layout layout)
		throws Exception {

		return new FriendlyUrlHistory() {
			{
				setFriendlyUrlPath_i18n(
					() -> _getFriendlyUrlPathJSONObject(layout));
			}
		};
	}

	private final FriendlyURLEntryLocalizationComparator
		_friendlyURLEntryLocalizationComparator =
			FriendlyURLEntryLocalizationComparator.getInstance(false);

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

}
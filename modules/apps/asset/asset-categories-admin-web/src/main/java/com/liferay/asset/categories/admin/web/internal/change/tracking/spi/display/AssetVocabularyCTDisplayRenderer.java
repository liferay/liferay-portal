/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.change.tracking.spi.display;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class AssetVocabularyCTDisplayRenderer
	extends BaseCTDisplayRenderer<AssetVocabulary> {

	@Override
	public String[] getAvailableLanguageIds(AssetVocabulary assetVocabulary) {
		return assetVocabulary.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(AssetVocabulary assetVocabulary) {
		return assetVocabulary.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			AssetVocabulary assetVocabulary)
		throws Exception {

		Group group = _groupLocalService.getGroup(assetVocabulary.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group,
				AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_asset_vocabulary.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"vocabularyId", assetVocabulary.getVocabularyId()
		).buildString();
	}

	@Override
	public Class<AssetVocabulary> getModelClass() {
		return AssetVocabulary.class;
	}

	@Override
	public String getTitle(Locale locale, AssetVocabulary assetVocabulary) {
		return assetVocabulary.getTitle(locale);
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<AssetVocabulary> displayBuilder) {

		AssetVocabulary assetVocabulary = displayBuilder.getModel();

		displayBuilder.display(
			"name",
			HtmlUtil.escape(
				assetVocabulary.getTitle(displayBuilder.getLocale()))
		).display(
			"description",
			assetVocabulary.getDescription(displayBuilder.getLocale())
		).display(
			"create-date", assetVocabulary.getCreateDate()
		).display(
			"asset-type", _getAssetType(displayBuilder, assetVocabulary)
		).display(
			"number-of-categories", assetVocabulary.getCategoriesCount()
		);
	}

	private String _getAssetType(
		DisplayBuilder<AssetVocabulary> displayBuilder,
		AssetVocabulary assetVocabulary) {

		long[] selectedClassNameIds = assetVocabulary.getSelectedClassNameIds();
		long[] selectedClassTypePKs = assetVocabulary.getSelectedClassTypePKs();

		StringBundler sb = new StringBundler(4 * selectedClassNameIds.length);

		for (int i = 0; i < selectedClassNameIds.length; i++) {
			long classNameId = selectedClassNameIds[i];
			long classTypePK = selectedClassTypePKs[i];

			String name = null;

			if (classNameId == AssetCategoryConstants.ALL_CLASS_NAME_ID) {
				name = _language.get(
					displayBuilder.getLocale(), "all-asset-types");
			}
			else if (classTypePK == AssetCategoryConstants.ALL_CLASS_TYPE_PK) {
				name = ResourceActionsUtil.getModelResource(
					displayBuilder.getLocale(),
					_portal.getClassName(classNameId));
			}
			else {
				AssetRendererFactory<?> assetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassNameId(classNameId);

				ClassTypeReader classTypeReader =
					assetRendererFactory.getClassTypeReader();

				try {
					ClassType classType = classTypeReader.getClassType(
						classTypePK, displayBuilder.getLocale());

					name = classType.getName();
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}

					continue;
				}
			}

			sb.append(name);

			if (assetVocabulary.isRequired(
					classNameId, classTypePK, assetVocabulary.getGroupId())) {

				sb.append(StringPool.SPACE);
				sb.append(StringPool.STAR);
			}

			sb.append(StringPool.COMMA_AND_SPACE);
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetVocabularyCTDisplayRenderer.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}
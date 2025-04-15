/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.portlet.action;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.NoSuchClassTypeException;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Diego Hu
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/asset_categories_admin/edit_asset_vocabulary"
	},
	service = MVCActionCommand.class
)
public class EditAssetVocabularyMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long vocabularyId = ParamUtil.getLong(actionRequest, "vocabularyId");

		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		AssetVocabulary vocabulary = null;

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			AssetVocabulary.class.getName(), actionRequest);

		if (vocabularyId <= 0) {

			// Add vocabulary

			int visibilityType = ParamUtil.getInteger(
				actionRequest, "visibilityType",
				AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

			vocabulary = _assetVocabularyService.addVocabulary(
				serviceContext.getScopeGroupId(), StringPool.BLANK, titleMap,
				descriptionMap, _getSettings(actionRequest), visibilityType,
				serviceContext);
		}
		else {

			// Update vocabulary

			vocabulary = _assetVocabularyService.updateVocabulary(
				vocabularyId, StringPool.BLANK, titleMap, descriptionMap,
				_getSettings(actionRequest), serviceContext);
		}

		actionRequest.setAttribute(
			WebKeys.REDIRECT, _getRedirectURL(actionResponse, vocabulary));
	}

	private String _getRedirectURL(
		ActionResponse actionResponse, AssetVocabulary vocabulary) {

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setMVCPath(
			"/view.jsp"
		).setParameter(
			"vocabularyId", vocabulary.getVocabularyId()
		).buildString();
	}

	private String _getSettings(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		int[] indexes = StringUtil.split(
			ParamUtil.getString(actionRequest, "indexes"), 0);

		long[] classNameIds = new long[indexes.length];
		long[] classTypePKs = new long[indexes.length];
		boolean[] depotRequireds = new boolean[indexes.length];
		boolean[] requireds = new boolean[indexes.length];

		for (int i = 0; i < indexes.length; i++) {
			int index = indexes[i];

			classNameIds[i] = ParamUtil.getLong(
				actionRequest, "classNameId" + index);

			classTypePKs[i] = ParamUtil.getLong(
				actionRequest,
				StringBundler.concat(
					"subtype", classNameIds[i], "-classNameId", index),
				AssetCategoryConstants.ALL_CLASS_TYPE_PK);

			if (classTypePKs[i] != -1) {
				AssetRendererFactory<?> assetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassNameId(classNameIds[i]);

				ClassTypeReader classTypeReader =
					assetRendererFactory.getClassTypeReader();

				try {
					classTypeReader.getClassType(
						classTypePKs[i], themeDisplay.getLocale());
				}
				catch (NoSuchModelException noSuchModelException) {
					throw new NoSuchClassTypeException(noSuchModelException);
				}
			}

			Group scopeGroup = themeDisplay.getScopeGroup();

			if (scopeGroup.isDepot()) {
				String required = ParamUtil.getString(
					actionRequest, "required" + index);

				if (Objects.equals(required, "depot-required")) {
					depotRequireds[i] = true;
					requireds[i] = false;
				}
				else if (Objects.equals(required, "required")) {
					depotRequireds[i] = false;
					requireds[i] = true;
				}
				else {
					depotRequireds[i] = false;
					requireds[i] = false;
				}
			}
			else {
				boolean required = ParamUtil.getBoolean(
					actionRequest, "required" + index);

				depotRequireds[i] = false;
				requireds[i] = required;
			}
		}

		AssetVocabularySettingsHelper assetVocabularySettingsHelper = null;

		long vocabularyId = ParamUtil.getLong(actionRequest, "vocabularyId");

		AssetVocabulary assetVocabulary =
			_assetVocabularyService.fetchVocabulary(vocabularyId);

		if (assetVocabulary != null) {
			assetVocabularySettingsHelper = new AssetVocabularySettingsHelper(
				assetVocabulary.getSettings());
		}
		else {
			assetVocabularySettingsHelper = new AssetVocabularySettingsHelper();
		}

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			classNameIds, classTypePKs, depotRequireds, requireds);
		assetVocabularySettingsHelper.setMultiValued(
			ParamUtil.getBoolean(actionRequest, "multiValued"));

		return assetVocabularySettingsHelper.toString();
	}

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}
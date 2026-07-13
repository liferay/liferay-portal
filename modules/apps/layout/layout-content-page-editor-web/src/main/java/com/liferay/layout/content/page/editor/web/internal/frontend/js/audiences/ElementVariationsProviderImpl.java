/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.frontend.js.audiences;

import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.frontend.js.audiences.ElementVariations;
import com.liferay.frontend.js.audiences.ElementVariationsProvider;
import com.liferay.layout.content.page.editor.web.internal.frontend.js.audiences.util.ElementVariationsJSUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ElementVariationsProvider.class)
public class ElementVariationsProviderImpl
	implements ElementVariationsProvider {

	@Override
	public ElementVariations getElementVariations(long plid) {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if ((layout == null) ||
			!FeatureFlagManagerUtil.isEnabled(
				layout.getCompanyId(), "LPD-85746")) {

			return null;
		}

		ElementVariations elementVariations = _portalCache.get(plid);

		if (elementVariations != null) {
			return elementVariations;
		}

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				plid);

		if (segmentsExperience == null) {
			return null;
		}

		String content = ElementVariationsJSUtil.getContent(
			_getElementVariationsJS(
				plid, segmentsExperience.getExternalReferenceCode()),
			_getSortedAudienceEntryERCs(layout.getCompanyId()));

		elementVariations = new ElementVariations(
			content, HashedFilesUtil.computeHash(content));

		_portalCache.put(plid, elementVariations);

		return elementVariations;
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<Long, ElementVariations>)_multiVMPool.getPortalCache(
				LayoutPageTemplateStructureRelElementVariation.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(
			LayoutPageTemplateStructureRelElementVariation.class.getName());
	}

	private String _getDefaultLanguageId(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		for (String xml :
				new String[] {
					layoutPageTemplateStructureRelElementVariation.getHide(),
					layoutPageTemplateStructureRelElementVariation.getHtml(),
					layoutPageTemplateStructureRelElementVariation.getJs()
				}) {

			if (Validator.isNotNull(xml)) {
				return _localization.getDefaultLanguageId(xml);
			}
		}

		return LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());
	}

	private String _getElementVariationJS(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation,
		List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels) {

		JSONArray audienceEntryERCsJSONArray = _jsonFactory.createJSONArray();

		for (LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel :
					layoutPageTemplateStructureRelElementVariationAudienceEntryRels) {

			audienceEntryERCsJSONArray.put(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getAudienceEntryERC());
		}

		return StringUtil.replace(
			JSONUtil.put(
				"audienceEntryERCs", audienceEntryERCsJSONArray
			).put(
				"defaultLanguageId",
				_getDefaultLanguageId(
					layoutPageTemplateStructureRelElementVariation)
			).put(
				"hide",
				_getLocalizedValuesJSONObject(
					layoutPageTemplateStructureRelElementVariation.getHideMap())
			).put(
				"html",
				_getLocalizedValuesJSONObject(
					layoutPageTemplateStructureRelElementVariation.getHtmlMap())
			).put(
				"js", "[$ELEMENT_VARIATION_JS$]"
			).put(
				"targetElement",
				layoutPageTemplateStructureRelElementVariation.
					getTargetElement()
			).toString(),
			"\"[$ELEMENT_VARIATION_JS$]\"",
			_getJSFunctions(layoutPageTemplateStructureRelElementVariation));
	}

	private String _getElementVariationsJS(
		long plid, String segmentsExperienceERC) {

		List<String> elementVariationsJS = TransformUtil.transform(
			_layoutPageTemplateStructureRelElementVariationLocalService.
				getLayoutPageTemplateStructureRelElementVariations(
					plid, segmentsExperienceERC),
			layoutPageTemplateStructureRelElementVariation ->
				_getElementVariationJS(
					layoutPageTemplateStructureRelElementVariation,
					_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
						getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
							layoutPageTemplateStructureRelElementVariation.
								getExternalReferenceCode())));

		return StringBundler.concat(
			CharPool.OPEN_BRACKET,
			StringUtil.merge(elementVariationsJS, StringPool.COMMA),
			CharPool.CLOSE_BRACKET);
	}

	private String _getJSFunctions(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		Map<Locale, String> jsMap =
			layoutPageTemplateStructureRelElementVariation.getJsMap();

		List<String> jsFunctions = TransformUtil.transform(
			jsMap.entrySet(),
			entry -> {
				if (Validator.isNull(entry.getValue())) {
					return null;
				}

				return StringBundler.concat(
					StringPool.QUOTE, LocaleUtil.toLanguageId(entry.getKey()),
					"\": function (element) {\n", entry.getValue(), "\n}");
			});

		return StringBundler.concat(
			CharPool.OPEN_CURLY_BRACE,
			StringUtil.merge(jsFunctions, StringPool.COMMA_AND_SPACE),
			CharPool.CLOSE_CURLY_BRACE);
	}

	private JSONObject _getLocalizedValuesJSONObject(
		Map<Locale, String> valuesMap) {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (Map.Entry<Locale, String> entry : valuesMap.entrySet()) {
			if (Validator.isNotNull(entry.getValue())) {
				jsonObject.put(
					LocaleUtil.toLanguageId(entry.getKey()), entry.getValue());
			}
		}

		return jsonObject;
	}

	private List<String> _getSortedAudienceEntryERCs(long companyId) {
		return TransformUtil.transform(
			_audiencesEntryLocalService.getAudiencesEntries(
				companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				OrderByComparatorFactoryUtil.create(
					"AudiencesEntry", "createDate", true)),
			AudiencesEntry::getExternalReferenceCode);
	}

	@Reference
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

	@Reference
	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<Long, ElementVariations> _portalCache;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}
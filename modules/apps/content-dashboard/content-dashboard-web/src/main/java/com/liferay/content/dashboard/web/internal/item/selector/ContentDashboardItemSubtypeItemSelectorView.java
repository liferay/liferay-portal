/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.item.selector;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.content.dashboard.info.item.ClassNameClassPKInfoItemIdentifier;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtypeFactory;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryRegistry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ItemSelectorView.class)
public class ContentDashboardItemSubtypeItemSelectorView
	implements ItemSelectorView
		<ContentDashboardItemSubtypeItemSelectorCriterion> {

	@Override
	public Class<ContentDashboardItemSubtypeItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return ContentDashboardItemSubtypeItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, getClass());

		return ResourceBundleUtil.getString(resourceBundle, "subtype");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			ContentDashboardItemSubtypeItemSelectorCriterion
				contentDashboardItemSubtypeItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException {

		PrintWriter printWriter = servletResponse.getWriter();

		printWriter.write("<section class=\"h-100\">");

		_reactRenderer.renderReact(
			new ComponentDescriptor(
				"{SelectTypeAndSubtype} from content-dashboard-web"),
			HashMapBuilder.<String, Object>put(
				"contentDashboardItemTypes",
				_getContentDashboardItemTypesJSONArray(servletRequest)
			).put(
				"itemSelectorSaveEvent", itemSelectedEventName
			).build(),
			(HttpServletRequest)servletRequest, printWriter);

		printWriter.write("</section>");
	}

	private Set<InfoItemReference>
		_getCheckedContentDashboardItemSubtypesInfoItemReferences(
			ServletRequest servletRequest) {

		String[] parameterValues = servletRequest.getParameterValues(
			"checkedContentDashboardItemSubtypesPayload");

		if (ArrayUtil.isEmpty(parameterValues)) {
			return Collections.emptySet();
		}

		Set<InfoItemReference> infoItemReferences = new HashSet<>();

		for (String parameterValue : parameterValues) {
			try {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					parameterValue);

				infoItemReferences.add(
					new InfoItemReference(
						jsonObject.getString("className"),
						new ClassNameClassPKInfoItemIdentifier(
							jsonObject.getString("entryClassName"),
							jsonObject.getLong("classPK"))));
			}
			catch (JSONException jsonException) {
				_log.error(jsonException);
			}
		}

		return infoItemReferences;
	}

	private JSONArray _getContentDashboardItemTypesJSONArray(
		ServletRequest servletRequest) {

		JSONArray contentDashboardItemTypesJSONArray =
			_jsonFactory.createJSONArray();

		Set<InfoItemReference>
			checkedContentDashboardItemSubtypesInfoItemReferences =
				_getCheckedContentDashboardItemSubtypesInfoItemReferences(
					servletRequest);
		ThemeDisplay themeDisplay = (ThemeDisplay)servletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (String className :
				_contentDashboardItemFactoryRegistry.getClassNames()) {

			ContentDashboardItemFactory<?> contentDashboardItemFactory =
				_contentDashboardItemFactoryRegistry.
					getContentDashboardItemFactory(className);

			if (contentDashboardItemFactory == null) {
				continue;
			}

			ContentDashboardItemSubtypeFactory<?>
				contentDashboardItemSubtypeFactory =
					contentDashboardItemFactory.
						getContentDashboardItemSubtypeFactory();

			if (contentDashboardItemSubtypeFactory != null) {
				_populateContentDashboardItemTypesJSONArray(
					className, contentDashboardItemSubtypeFactory,
					checkedContentDashboardItemSubtypesInfoItemReferences,
					contentDashboardItemTypesJSONArray, themeDisplay);
			}
		}

		return contentDashboardItemTypesJSONArray;
	}

	private String _getIcon(String className) {
		String searchClassName =
			_infoSearchClassMapperRegistry.getSearchClassName(className);

		if (searchClassName == null) {
			return null;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				searchClassName);

		if (assetRendererFactory == null) {
			return null;
		}

		return assetRendererFactory.getIconCssClass();
	}

	private String _getInfoItemFormVariationLabel(
		InfoItemFormVariation infoItemFormVariation, Locale locale) {

		InfoLocalizedValue<String> labelInfoLocalizedValue =
			infoItemFormVariation.getLabelInfoLocalizedValue();

		String label = labelInfoLocalizedValue.getValue(locale);

		Group group = _groupLocalService.fetchGroup(
			infoItemFormVariation.getGroupId());

		if (group == null) {
			return label;
		}

		String value = null;

		try {
			value = _language.format(
				locale, "x-group-x",
				new String[] {label, group.getDescriptiveName(locale)});
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			value = _language.format(
				locale, "x-group-x",
				new String[] {label, group.getName(locale)});
		}

		if (value != null) {
			return value;
		}

		return label;
	}

	private void _populateContentDashboardItemTypesJSONArray(
		String className,
		ContentDashboardItemSubtypeFactory<?>
			contentDashboardItemSubtypeFactory,
		Set<InfoItemReference>
			checkedContentDashboardItemSubtypeInfoItemReferences,
		JSONArray contentDashboardItemTypesJSONArray,
		ThemeDisplay themeDisplay) {

		InfoItemClassDetails infoItemClassDetails = new InfoItemClassDetails(
			className);

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				infoItemClassDetails.getClassName());

		if (infoItemFormVariationsProvider == null) {
			contentDashboardItemTypesJSONArray.put(
				JSONUtil.put(
					"entryClassName", className
				).put(
					"icon", _getIcon(className)
				).put(
					"itemSubtypes", _jsonFactory.createJSONArray()
				).put(
					"label",
					() -> {
						InfoLocalizedValue<String>
							infoItemClassDetailsLabelInfoLocalizedValue =
								infoItemClassDetails.
									getLabelInfoLocalizedValue();

						return infoItemClassDetailsLabelInfoLocalizedValue.
							getValue(themeDisplay.getLocale());
					}
				).put(
					"selected",
					() -> {
						for (InfoItemReference infoItemReference :
								checkedContentDashboardItemSubtypeInfoItemReferences) {

							if (Objects.equals(
									infoItemReference.getClassName(),
									className)) {

								return true;
							}
						}

						return false;
					}
				));

			return;
		}

		Collection<InfoItemFormVariation> infoItemFormVariations =
			infoItemFormVariationsProvider.getInfoItemFormVariationsByCompanyId(
				themeDisplay.getCompanyId());

		JSONArray itemSubtypesJSONArray = _jsonFactory.createJSONArray();

		for (InfoItemFormVariation infoItemFormVariation :
				infoItemFormVariations) {

			try {
				ContentDashboardItemSubtype<?> contentDashboardItemSubtype =
					contentDashboardItemSubtypeFactory.create(
						Long.valueOf(infoItemFormVariation.getKey()));

				Company company = _companyLocalService.getCompany(
					themeDisplay.getCompanyId());

				DLFileEntryType googleDocsDLFileEntryType =
					_dlFileEntryTypeLocalService.fetchFileEntryType(
						company.getGroupId(), "GOOGLE_DOCS");

				if (googleDocsDLFileEntryType != null) {
					String fileEntryTypeIdString = String.valueOf(
						googleDocsDLFileEntryType.getFileEntryTypeId());

					if (StringUtil.equalsIgnoreCase(
							fileEntryTypeIdString,
							infoItemFormVariation.getKey())) {

						continue;
					}
				}

				InfoItemReference infoItemReference =
					contentDashboardItemSubtype.getInfoItemReference();

				ClassNameClassPKInfoItemIdentifier
					classNameClassPKInfoItemIdentifier =
						(ClassNameClassPKInfoItemIdentifier)
							infoItemReference.getInfoItemIdentifier();

				itemSubtypesJSONArray.put(
					JSONUtil.put(
						"className",
						classNameClassPKInfoItemIdentifier.getClassName()
					).put(
						"classPK",
						String.valueOf(
							classNameClassPKInfoItemIdentifier.getClassPK())
					).put(
						"entryClassName", infoItemReference.getClassName()
					).put(
						"label",
						_getInfoItemFormVariationLabel(
							infoItemFormVariation, themeDisplay.getLocale())
					).put(
						"selected",
						checkedContentDashboardItemSubtypeInfoItemReferences.
							contains(infoItemReference)
					));
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		contentDashboardItemTypesJSONArray.put(
			JSONUtil.put(
				"icon", _getIcon(className)
			).put(
				"itemSubtypes", itemSubtypesJSONArray
			).put(
				"label",
				() -> {
					InfoLocalizedValue<String>
						infoItemClassDetailsLabelInfoLocalizedValue =
							infoItemClassDetails.getLabelInfoLocalizedValue();

					return infoItemClassDetailsLabelInfoLocalizedValue.getValue(
						themeDisplay.getLocale());
				}
			));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardItemSubtypeItemSelectorView.class);

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ContentDashboardItemFactoryRegistry
		_contentDashboardItemFactoryRegistry;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ReactRenderer _reactRenderer;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.fragment.contributor.util.FragmentCollectionContributorRegistryUtil;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.util.FragmentRendererRegistryUtil;
import com.liferay.headless.admin.site.client.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.DefaultFragmentReference;
import com.liferay.headless.admin.site.client.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentDropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ModulePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ModuleViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.ModuleViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.TemplateListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class PageElementsTestUtil {

	public static FragmentInstancePageElementDefinition
		getFragmentInstancePageElementDefinition(
			Map<String, Object> configurationValuesMap,
			FragmentEntry fragmentEntry, long scopeGroupId) {

		return new FragmentInstancePageElementDefinition() {
			{
				setConfiguration(fragmentEntry::getConfiguration);
				setCss(fragmentEntry::getCss);
				setCssClasses(
					() -> new String[] {RandomTestUtil.randomString()});
				setCustomCSS(RandomTestUtil::randomString);
				setDatePropagated(RandomTestUtil::nextDate);
				setFragmentConfigurationFieldValues(
					() ->
						FragmentConfigurationFieldValueTestUtil.
							getFragmentConfigurationFieldValuesMap(
								JSONFactoryUtil.createJSONObject(
									fragmentEntry.getConfiguration()),
								configurationValuesMap, scopeGroupId));
				setFragmentInstanceExternalReferenceCode(
					RandomTestUtil::randomString);
				setFragmentReference(
					() -> {
						if (fragmentEntry.getFragmentEntryId() == 0) {
							return new DefaultFragmentReference() {
								{
									setDefaultFragmentKey(
										fragmentEntry::getFragmentEntryKey);
									setFragmentReferenceType(
										() ->
											FragmentReferenceType.
												DEFAULT_FRAGMENT_REFERENCE);
								}
							};
						}

						return new FragmentItemExternalReference() {
							{
								setExternalReferenceCode(
									fragmentEntry::getExternalReferenceCode);
								setFragmentReferenceType(
									() ->
										FragmentReferenceType.
											FRAGMENT_ITEM_EXTERNAL_REFERENCE);
								setScope(
									() -> ScopeTestUtil.getItemScope(
										fragmentEntry.getGroupId(),
										scopeGroupId));
							}
						};
					});
				setFragmentType(FragmentType.BASIC);
				setHtml(fragmentEntry::getHtml);
				setIndexed(RandomTestUtil::randomBoolean);
				setJs(fragmentEntry::getJs);
				setName(RandomTestUtil::randomString);
				setNamespace(RandomTestUtil::randomString);
				setType(Type.FRAGMENT);
				setUuid(RandomTestUtil::randomString);
			}
		};
	}

	public static FragmentInstancePageElementDefinition
		getFragmentInstancePageElementDefinition(
			Map<String, Object> configurationValuesMap,
			FragmentRenderer fragmentRenderer, long scopeGroupId) {

		JSONObject configurationJSONObject =
			fragmentRenderer.getConfigurationJSONObject(
				new DefaultFragmentRendererContext(null));

		return new FragmentInstancePageElementDefinition() {
			{
				setConfiguration(
					() -> GetterUtil.getString(
						JSONFactoryUtil.toString(configurationJSONObject)));
				setCss(() -> StringPool.BLANK);
				setCssClasses(
					() -> new String[] {RandomTestUtil.randomString()});
				setCustomCSS(RandomTestUtil::randomString);
				setDatePropagated(RandomTestUtil::nextDate);
				setFragmentConfigurationFieldValues(
					() ->
						FragmentConfigurationFieldValueTestUtil.
							getFragmentConfigurationFieldValuesMap(
								configurationJSONObject, configurationValuesMap,
								scopeGroupId));
				setFragmentInstanceExternalReferenceCode(
					RandomTestUtil::randomString);
				setFragmentReference(
					() -> new DefaultFragmentReference() {
						{
							setDefaultFragmentKey(fragmentRenderer::getKey);
							setFragmentReferenceType(
								() ->
									FragmentReferenceType.
										DEFAULT_FRAGMENT_REFERENCE);
						}
					});
				setFragmentType(FragmentType.BASIC);
				setHtml(() -> StringPool.BLANK);
				setIndexed(RandomTestUtil::randomBoolean);
				setJs(() -> StringPool.BLANK);
				setName(RandomTestUtil::randomString);
				setNamespace(RandomTestUtil::randomString);
				setType(Type.FRAGMENT);
				setUuid(RandomTestUtil::randomString);
			}
		};
	}

	public static FragmentInstancePageElementDefinition
		getFragmentInstancePageElementDefinition(
			Map<String, Object> configurationValuesMap, String key,
			long scopeGroupId) {

		FragmentEntry fragmentEntry =
			FragmentCollectionContributorRegistryUtil.getFragmentEntry(key);

		if (fragmentEntry != null) {
			return getFragmentInstancePageElementDefinition(
				configurationValuesMap, fragmentEntry, scopeGroupId);
		}

		FragmentRenderer fragmentRenderer =
			FragmentRendererRegistryUtil.getFragmentRenderer(key);

		if (fragmentRenderer != null) {
			return getFragmentInstancePageElementDefinition(
				configurationValuesMap, fragmentRenderer, scopeGroupId);
		}

		return null;
	}

	public static PageElementDefinition getPageElementDefinition(
		PageElementDefinition.Type type, long scopeGroupId) {

		if (Objects.equals(
				type, PageElementDefinition.Type.COLLECTION_DISPLAY)) {

			ClassNameReference classNameReference = new ClassNameReference();

			classNameReference.setClassName(
				"com.liferay.asset.internal.info.collection.provider." +
					"RecentContentInfoCollectionProvider");
			classNameReference.setCollectionType(
				CollectionReference.CollectionType.COLLECTION_PROVIDER);

			return new CollectionDisplayPageElementDefinition() {
				{
					setCollectionDisplayListStyle(
						_getCollectionDisplayListStyle());
					setCollectionDisplayViewports(
						new CollectionDisplayViewport[0]);
					setCollectionSettings(
						() -> new CollectionSettings() {
							{
								setCollectionReference(
									() -> classNameReference);
							}
						});
					setDisplayAllItems(Boolean.FALSE);
					setDisplayAllPages(Boolean.TRUE);
					setHidden(Boolean.FALSE);
					setNumberOfItems(5);
					setNumberOfItemsPerPage(5);
					setNumberOfPages(20);
					setPaginationType(PaginationType.NONE);
					setType(Type.COLLECTION_DISPLAY);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.COLLECTION_ITEM)) {
			return new CollectionItemPageElementDefinition() {
				{
					setType(Type.COLLECTION_ITEM);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.CONTAINER)) {
			return new ContainerPageElementDefinition() {
				{
					setContentVisibility(ContentVisibility.AUTO);
					setIndexed(Boolean.FALSE);
					setType(Type.CONTAINER);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.DROP_ZONE)) {
			return new DropZonePageElementDefinition() {
				{
					setType(Type.DROP_ZONE);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.FORM_CONTAINER)) {
			return new FormContainerPageElementDefinition() {
				{
					setIndexed(Boolean.TRUE);
					setType(Type.FORM_CONTAINER);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.FORM_STEP)) {
			return new FormStepPageElementDefinition() {
				{
					setType(Type.FORM_STEP);
				}
			};
		}

		if (Objects.equals(
				type, PageElementDefinition.Type.FORM_STEP_CONTAINER)) {

			return new FormStepContainerPageElementDefinition() {
				{
					setType(Type.FORM_STEP_CONTAINER);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.FRAGMENT)) {
			return getFragmentInstancePageElementDefinition(
				Collections.emptyMap(), "BASIC_COMPONENT-heading",
				scopeGroupId);
		}

		if (Objects.equals(
				type, PageElementDefinition.Type.FRAGMENT_DROP_ZONE)) {

			return new FragmentDropZonePageElementDefinition() {
				{
					setType(Type.FRAGMENT_DROP_ZONE);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.MODULE)) {
			return new ModulePageElementDefinition() {
				{
					setSize(1);
					setType(Type.MODULE);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.GRID)) {
			return new GridPageElementDefinition() {
				{
					setGutters(Boolean.TRUE);
					setIndexed(Boolean.TRUE);
					setModulesPerRow(0);
					setNumberOfModules(0);
					setReverseOrder(Boolean.FALSE);
					setType(Type.GRID);
					setVerticalAlignment(VerticalAlignment.TOP);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.WIDGET)) {
			return new WidgetInstancePageElementDefinition() {
				{
					setIndexed(true);
					setName(RandomTestUtil.randomString());
					setType(PageElementDefinition.Type.WIDGET);
					setWidgetInstance(PageElementsTestUtil::_getWidgetInstance);
					setWidgetInstanceExternalReferenceCode(
						RandomTestUtil.randomString());
				}
			};
		}

		return null;
	}

	public static PageElement[] getPageElements(
		int count, String parentExternalReferenceCode, long scopeGroupId) {

		PageElement[] pageElements = new PageElement[count];

		for (int i = 0; i < count; i++) {
			PageElement pageElement = new PageElement();

			pageElement.setExternalReferenceCode(RandomTestUtil::randomString);
			pageElement.setPageElementDefinition(
				getPageElementDefinition(_getRandomType(), scopeGroupId));
			pageElement.setPosition(i);

			if (_isParentablePageElementDefinitionType(
					pageElement.getPageElementDefinition()) &&
				RandomTestUtil.randomBoolean()) {

				pageElement.setPageElements(
					getPageElements(
						RandomTestUtil.randomInt(1, 2),
						pageElement.getExternalReferenceCode(), scopeGroupId));
			}

			pageElement.setParentExternalReferenceCode(
				parentExternalReferenceCode);

			pageElements[i] = pageElement;
		}

		return pageElements;
	}

	public static PageElement[] getPageElements(long scopeGroupId) {
		List<PageElement> pageElements = new ArrayList<>();

		int position = 0;

		pageElements.add(
			_getCollectionDisplayPageElement(position++, scopeGroupId));
		pageElements.add(
			_getPageElement(
				getPageElementDefinition(
					PageElementDefinition.Type.CONTAINER, scopeGroupId),
				StringPool.BLANK, position++));
		pageElements.add(_getGridPageElement(position++));

		pageElements.add(
			_getPageElement(
				getPageElementDefinition(
					PageElementDefinition.Type.WIDGET, scopeGroupId),
				StringPool.BLANK, position));

		return pageElements.toArray(new PageElement[0]);
	}

	private static CollectionDisplayListStyle _getCollectionDisplayListStyle() {
		TemplateListStyle templateListStyle = new TemplateListStyle();

		templateListStyle.setCollectionDisplayListStyleType(
			CollectionDisplayListStyle.CollectionDisplayListStyleType.TEMPLATE);
		templateListStyle.setListItemStyleClassName(
			"com.liferay.asset.internal.info.renderer." +
				"AssetEntryFullContentInfoItemRenderer");
		templateListStyle.setListStyleClassName(
			"com.liferay.asset.info.internal.list.renderer." +
				"NumberedAssetEntryBasicInfoListRenderer");
		templateListStyle.setTemplateKey(RandomTestUtil.randomString());

		return templateListStyle;
	}

	private static PageElement _getCollectionDisplayPageElement(
		int position, long scopeGroupId) {

		PageElement collectionDisplayPageElement = _getPageElement(
			getPageElementDefinition(
				PageElementDefinition.Type.COLLECTION_DISPLAY, scopeGroupId),
			StringPool.BLANK, position);

		collectionDisplayPageElement.setPageElements(
			new PageElement[] {
				_getPageElement(
					getPageElementDefinition(
						PageElementDefinition.Type.COLLECTION_ITEM,
						scopeGroupId),
					collectionDisplayPageElement.getExternalReferenceCode(), 0)
			});

		return collectionDisplayPageElement;
	}

	private static PageElement _getGridPageElement(int position) {
		String externalReferenceCode = RandomTestUtil.randomString();

		ModuleViewport[] moduleViewports = {
			new ModuleViewport() {
				{
					setId(Id.LANDSCAPE_MOBILE);
					setModuleViewportDefinition(
						() -> new ModuleViewportDefinition() {
							{
								setSize(12);
							}
						});
				}
			}
		};

		return _getPageElement(
			externalReferenceCode,
			new GridPageElementDefinition() {
				{
					setGridViewports(
						new GridViewport[] {
							_getGridViewport(
								GridViewportDefinition.VerticalAlignment.BOTTOM,
								GridViewport.Id.LANDSCAPE_MOBILE),
							_getGridViewport(
								GridViewportDefinition.VerticalAlignment.TOP,
								GridViewport.Id.PORTRAIT_MOBILE),
							_getGridViewport(
								GridViewportDefinition.VerticalAlignment.MIDDLE,
								GridViewport.Id.TABLET)
						});
					setGutters(Boolean.TRUE);
					setIndexed(Boolean.TRUE);
					setModulesPerRow(3);
					setNumberOfModules(1);
					setReverseOrder(Boolean.FALSE);
					setType(Type.GRID);
					setVerticalAlignment(VerticalAlignment.TOP);
				}
			},
			new PageElement[] {
				_getPageElement(
					_getModulePageElementDefinition(moduleViewports),
					externalReferenceCode, 0),
				_getPageElement(
					_getModulePageElementDefinition(moduleViewports),
					externalReferenceCode, 1),
				_getPageElement(
					_getModulePageElementDefinition(moduleViewports),
					externalReferenceCode, 2)
			},
			StringPool.BLANK, position);
	}

	private static GridViewport _getGridViewport(
		GridViewportDefinition.VerticalAlignment verticalAlignment,
		GridViewport.Id id) {

		GridViewport gridViewport = new GridViewport();

		gridViewport.setCustomCSS(RandomTestUtil.randomString());

		GridViewportDefinition gridViewportDefinition =
			new GridViewportDefinition();

		gridViewportDefinition.setModulesPerRow(RandomTestUtil.randomInt());
		gridViewportDefinition.setVerticalAlignment(verticalAlignment);

		gridViewport.setGridViewportDefinition(() -> gridViewportDefinition);

		gridViewport.setId(id);

		return gridViewport;
	}

	private static ModulePageElementDefinition _getModulePageElementDefinition(
		ModuleViewport[] moduleViewports) {

		ModulePageElementDefinition modulePageElementDefinition =
			new ModulePageElementDefinition();

		modulePageElementDefinition.setModuleViewports(moduleViewports);
		modulePageElementDefinition.setSize(4);
		modulePageElementDefinition.setType(PageElementDefinition.Type.MODULE);

		return modulePageElementDefinition;
	}

	private static PageElement _getPageElement(
		PageElementDefinition pageElementDefinition,
		String parentExternalReferenceCode, int position) {

		return _getPageElement(
			RandomTestUtil.randomString(), pageElementDefinition,
			new PageElement[0], parentExternalReferenceCode, position);
	}

	private static PageElement _getPageElement(
		String externalReferenceCode,
		PageElementDefinition pageElementDefinition, PageElement[] pageElements,
		String parentExternalReferenceCode, int position) {

		PageElement pageElement = new PageElement();

		pageElement.setExternalReferenceCode(externalReferenceCode);
		pageElement.setPageElementDefinition(pageElementDefinition);
		pageElement.setPageElements(pageElements);
		pageElement.setParentExternalReferenceCode(parentExternalReferenceCode);
		pageElement.setPosition(position);

		return pageElement;
	}

	private static PageElementDefinition.Type _getRandomType() {
		return _types.get(RandomTestUtil.randomInt(0, _types.size() - 1));
	}

	private static WidgetInstance _getWidgetInstance() {
		WidgetInstance widgetInstance = new WidgetInstance();

		widgetInstance.setWidgetConfig(new HashMap<>());
		widgetInstance.setWidgetInstanceId(RandomTestUtil.randomString());
		widgetInstance.setWidgetName(AssetPublisherPortletKeys.ASSET_PUBLISHER);
		widgetInstance.setWidgetPermissions(new WidgetPermission[0]);

		return widgetInstance;
	}

	private static boolean _isParentablePageElementDefinitionType(
		PageElementDefinition pageElementDefinition) {

		if (Objects.equals(
				pageElementDefinition.getType(),
				PageElementDefinition.Type.COLLECTION_ITEM)) {

			return true;
		}
		else if (Objects.equals(
					pageElementDefinition.getType(),
					PageElementDefinition.Type.CONTAINER)) {

			return true;
		}
		else if (Objects.equals(
					pageElementDefinition.getType(),
					PageElementDefinition.Type.MODULE)) {

			return true;
		}

		return false;
	}

	private static final List<PageElementDefinition.Type> _types =
		Arrays.asList(
			PageElementDefinition.Type.CONTAINER,
			PageElementDefinition.Type.FRAGMENT);

}
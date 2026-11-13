/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.fragment.contributor.util.FragmentCollectionContributorRegistryUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.util.FragmentRendererRegistryUtil;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.headless.admin.site.client.dto.v1_0.BackgroundImageValue;
import com.liferay.headless.admin.site.client.dto.v1_0.BasicFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.DefaultFragmentReference;
import com.liferay.headless.admin.site.client.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentDropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElement;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkTextValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemContextReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentReference;
import com.liferay.headless.admin.site.client.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.Mapping;
import com.liferay.headless.admin.site.client.dto.v1_0.ModulePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ModuleViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.ModuleViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.RepeatableFieldsCollectionProviderReference;
import com.liferay.headless.admin.site.client.dto.v1_0.TemplateListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentMappedValue;
import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentValue;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.client.scope.Scope;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalServiceUtil;
import com.liferay.template.test.util.TemplateTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;

/**
 * @author Lourdes Fernández Besada
 */
public class PageElementsTestUtil {

	public static void assertFieldKeysWithTemplateEntries(
			PageSpecification[] actualPageSpecifications,
			PageSpecification[] expectedPageSpecifications)
		throws PortalException {

		Assert.assertEquals(
			Arrays.toString(actualPageSpecifications), 2,
			actualPageSpecifications.length);
		Assert.assertEquals(
			Arrays.toString(expectedPageSpecifications),
			actualPageSpecifications.length, expectedPageSpecifications.length);

		ContentPageSpecification actualPublishedPageSpecification =
			(ContentPageSpecification)actualPageSpecifications[0];
		ContentPageSpecification expectedPublishedPageSpecification =
			(ContentPageSpecification)expectedPageSpecifications[0];

		_assertPageExperience(
			actualPublishedPageSpecification.getPageExperiences()[0],
			expectedPublishedPageSpecification.getPageExperiences()[0]);

		ContentPageSpecification actualDraftPageSpecification =
			(ContentPageSpecification)actualPageSpecifications[1];
		ContentPageSpecification expectedDraftPageSpecification =
			(ContentPageSpecification)expectedPageSpecifications[1];

		_assertPageExperience(
			actualDraftPageSpecification.getPageExperiences()[0],
			expectedDraftPageSpecification.getPageExperiences()[0]);
	}

	public static void assertRenderedLayoutHTMLWithTemplateEntries(
		String renderLayoutHTML) {

		Assert.assertNotNull(renderLayoutHTML);

		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("companyGroupTemplateEntry1"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("companyGroupTemplateEntry2"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("companyGroupTemplateEntry3"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("companyGroupTemplateEntry4"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("companyGroupTemplateEntry5"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("companyGroupTemplateEntry6"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("scopeGroupTemplateEntry1"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("scopeGroupTemplateEntry2"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("scopeGroupTemplateEntry3"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("scopeGroupTemplateEntry4"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("scopeGroupTemplateEntry5"));
		Assert.assertTrue(
			renderLayoutHTML,
			renderLayoutHTML.contains("scopeGroupTemplateEntry6"));
	}

	public static BasicFragmentInstancePageElementDefinition
		getBasicFragmentInstancePageElementDefinition(
			BackgroundImageValue backgroundImageValue,
			Map<String, Object> configurationValuesMap,
			FragmentEditableElement[] fragmentEditableElements,
			FragmentEntry fragmentEntry, Boolean hidden, long scopeGroupId) {

		return getBasicFragmentInstancePageElementDefinition(
			backgroundImageValue, configurationValuesMap,
			fragmentEditableElements, fragmentEntry,
			RandomTestUtil.randomString(), hidden,
			RandomTestUtil.randomString(), scopeGroupId,
			RandomTestUtil.randomString(), null);
	}

	public static BasicFragmentInstancePageElementDefinition
		getBasicFragmentInstancePageElementDefinition(
			BackgroundImageValue backgroundImageValue,
			Map<String, Object> configurationValuesMap,
			FragmentEditableElement[] fragmentEditableElements,
			FragmentEntry fragmentEntry, long scopeGroupId) {

		return getBasicFragmentInstancePageElementDefinition(
			backgroundImageValue, configurationValuesMap,
			fragmentEditableElements, fragmentEntry,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			scopeGroupId, RandomTestUtil.randomString(), null);
	}

	public static BasicFragmentInstancePageElementDefinition
		getBasicFragmentInstancePageElementDefinition(
			BackgroundImageValue backgroundImageValue,
			Map<String, Object> configurationValuesMap,
			FragmentEditableElement[] fragmentEditableElements,
			FragmentEntry fragmentEntry,
			String fragmentInstanceExternalReferenceCode, Boolean hidden,
			String namespace, long scopeGroupId, String uuid,
			WidgetInstance[] widgetInstances) {

		return new BasicFragmentInstancePageElementDefinition() {
			{
				setFragmentInstance(
					_getFragmentInstance(
						configurationValuesMap, fragmentEditableElements,
						fragmentEntry, backgroundImageValue,
						fragmentInstanceExternalReferenceCode, hidden,
						namespace, scopeGroupId, uuid, widgetInstances));
				setType(() -> Type.BASIC_FRAGMENT);
			}
		};
	}

	public static BasicFragmentInstancePageElementDefinition
		getBasicFragmentInstancePageElementDefinition(
			BackgroundImageValue backgroundImageValue,
			Map<String, Object> configurationValuesMap,
			FragmentEditableElement[] fragmentEditableElements,
			FragmentEntry fragmentEntry,
			String fragmentInstanceExternalReferenceCode, String namespace,
			long scopeGroupId, String uuid, WidgetInstance[] widgetInstances) {

		return getBasicFragmentInstancePageElementDefinition(
			backgroundImageValue, configurationValuesMap,
			fragmentEditableElements, fragmentEntry,
			fragmentInstanceExternalReferenceCode,
			RandomTestUtil.randomBoolean(), namespace, scopeGroupId, uuid,
			widgetInstances);
	}

	public static BasicFragmentInstancePageElementDefinition
		getBasicFragmentInstancePageElementDefinition(
			BackgroundImageValue backgroundImageValue,
			Map<String, Object> configurationValuesMap,
			FragmentEditableElement[] fragmentEditableElements,
			FragmentRenderer fragmentRenderer, long scopeGroupId) {

		return new BasicFragmentInstancePageElementDefinition() {
			{
				setFragmentInstance(
					_getFragmentInstance(
						configurationValuesMap, fragmentEditableElements,
						backgroundImageValue, fragmentRenderer, scopeGroupId));
				setType(() -> Type.BASIC_FRAGMENT);
			}
		};
	}

	public static BasicFragmentInstancePageElementDefinition
		getBasicFragmentInstancePageElementDefinition(
			BackgroundImageValue backgroundImageValue,
			Map<String, Object> configurationValuesMap, String key,
			FragmentEditableElement[] fragmentEditableElements, Boolean hidden,
			long scopeGroupId) {

		FragmentEntry fragmentEntry =
			FragmentCollectionContributorRegistryUtil.getFragmentEntry(key);

		if (fragmentEntry == null) {
			Company company = null;

			try {
				company = CompanyLocalServiceUtil.getCompany(
					TestPropsValues.getCompanyId());
			}
			catch (Exception exception) {
			}

			if (company != null) {
				fragmentEntry =
					FragmentEntryLocalServiceUtil.fetchFragmentEntry(
						company.getGroupId(), key);
			}
		}

		if (fragmentEntry != null) {
			return getBasicFragmentInstancePageElementDefinition(
				backgroundImageValue, configurationValuesMap,
				fragmentEditableElements, fragmentEntry, hidden, scopeGroupId);
		}

		FragmentRenderer fragmentRenderer =
			FragmentRendererRegistryUtil.getFragmentRenderer(key);

		if (fragmentRenderer != null) {
			return getBasicFragmentInstancePageElementDefinition(
				backgroundImageValue, configurationValuesMap,
				fragmentEditableElements, fragmentRenderer, scopeGroupId);
		}

		return null;
	}

	public static BasicFragmentInstancePageElementDefinition
		getBasicFragmentInstancePageElementDefinition(
			BackgroundImageValue backgroundImageValue,
			Map<String, Object> configurationValuesMap, String key,
			long scopeGroupId) {

		return getBasicFragmentInstancePageElementDefinition(
			backgroundImageValue, configurationValuesMap, key,
			new FragmentEditableElement[0], RandomTestUtil.randomBoolean(),
			scopeGroupId);
	}

	public static PageElement[] getDisplayPageTemplatePageElements(
			Company company, String fragmentKey, JournalArticle journalArticle,
			long scopeGroupId)
		throws Exception {

		ServiceContext companyGroupServiceContext =
			ServiceContextTestUtil.getServiceContext(company.getGroupId());

		ServiceContext scopeGroupServiceContext =
			ServiceContextTestUtil.getServiceContext(scopeGroupId);

		int position = 0;

		return new PageElement[] {
			_getBasicFragmentInstancePageElement(
				_getCompanyGroupTemplateEntryExternalUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "companyGroupTemplateEntry1",
						companyGroupServiceContext)),
				fragmentKey, journalArticle, position++, scopeGroupId),
			_getBasicFragmentInstancePageElement(
				_getScopeGroupTemplateEntryExternalUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "scopeGroupTemplateEntry1",
						scopeGroupServiceContext)),
				fragmentKey, journalArticle, position++, scopeGroupId),
			_getBasicFragmentInstancePageElement(
				_getTemplateEntryUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "companyGroupTemplateEntry2",
						companyGroupServiceContext)),
				fragmentKey, journalArticle, position++, scopeGroupId),
			_getBasicFragmentInstancePageElement(
				_getTemplateEntryUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "scopeGroupTemplateEntry2",
						scopeGroupServiceContext)),
				fragmentKey, journalArticle, position++, scopeGroupId),
			_getCollectionDisplayPageElement(
				_getCompanyGroupTemplateEntryExternalUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "companyGroupTemplateEntry3",
						companyGroupServiceContext)),
				fragmentKey, journalArticle, position++, scopeGroupId,
				companyGroupServiceContext),
			_getCollectionDisplayPageElement(
				_getScopeGroupTemplateEntryExternalUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "scopeGroupTemplateEntry3",
						scopeGroupServiceContext)),
				fragmentKey, journalArticle, position++, scopeGroupId,
				companyGroupServiceContext),
			_getCollectionDisplayPageElement(
				_getTemplateEntryUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "companyGroupTemplateEntry4",
						companyGroupServiceContext)),
				fragmentKey, journalArticle, position++, scopeGroupId,
				companyGroupServiceContext),
			_getCollectionDisplayPageElement(
				_getTemplateEntryUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "scopeGroupTemplateEntry4",
						scopeGroupServiceContext)),
				fragmentKey, journalArticle, position++, scopeGroupId,
				companyGroupServiceContext),
			_getDisplayPageItemPageElement(
				_getCompanyGroupTemplateEntryExternalUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "companyGroupTemplateEntry5",
						companyGroupServiceContext)),
				fragmentKey, position++, scopeGroupId),
			_getDisplayPageItemPageElement(
				_getScopeGroupTemplateEntryExternalUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "scopeGroupTemplateEntry5",
						scopeGroupServiceContext)),
				fragmentKey, position++, scopeGroupId),
			_getDisplayPageItemPageElement(
				_getTemplateEntryUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "companyGroupTemplateEntry6",
						companyGroupServiceContext)),
				fragmentKey, position++, scopeGroupId),
			_getDisplayPageItemPageElement(
				_getTemplateEntryUniqueIdFieldKey(
					_getTemplateEntry(
						journalArticle, "scopeGroupTemplateEntry6",
						scopeGroupServiceContext)),
				fragmentKey, position, scopeGroupId)
		};
	}

	public static PageElement getDropZonePageElement(
			String externalReferenceCode, long groupId, long... itemGroupIds)
		throws PortalException {

		DropZonePageElementDefinition dropZonePageElementDefinition =
			new DropZonePageElementDefinition();

		dropZonePageElementDefinition.setAddNewFragmentEntries(true);
		dropZonePageElementDefinition.setAllowedFragmentReferences(
			_getFragmentReferences(groupId, itemGroupIds));
		dropZonePageElementDefinition.setType(
			PageElementDefinition.Type.DROP_ZONE);

		return _getPageElement(
			externalReferenceCode, dropZonePageElementDefinition);
	}

	public static PageElementDefinition getPageElementDefinition(
		Boolean hidden, PageElementDefinition.Type type, long scopeGroupId) {

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
						() -> _getDesktopCollectionDisplayViewports(hidden));
					setCollectionSettings(
						() -> _getCollectionSettings(classNameReference));
					setDisplayAllItems(Boolean.FALSE);
					setDisplayAllPages(Boolean.TRUE);
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

		if (Objects.equals(type, PageElementDefinition.Type.BASIC_FRAGMENT)) {
			return getBasicFragmentInstancePageElementDefinition(
				null, Collections.emptyMap(), "BASIC_COMPONENT-heading",
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
					setType(Type.MODULE);
				}
			};
		}

		if (Objects.equals(type, PageElementDefinition.Type.GRID)) {
			return new GridPageElementDefinition() {
				{
					setGutters(Boolean.TRUE);
					setIndexed(Boolean.TRUE);
					setNumberOfModules(0);
					setReverseOrder(Boolean.FALSE);
					setType(Type.GRID);
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

	public static PageElementDefinition getPageElementDefinition(
		PageElementDefinition.Type type, long scopeGroupId) {

		return getPageElementDefinition(
			RandomTestUtil.randomBoolean(), type, scopeGroupId);
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
			else {
				pageElement.setPageElements(new PageElement[0]);
			}

			pageElement.setParentExternalReferenceCode(
				parentExternalReferenceCode);

			pageElements[i] = pageElement;
		}

		return pageElements;
	}

	public static PageElement[] getPageElements(long scopeGroupId)
		throws Exception {

		List<PageElement> pageElements = new ArrayList<>();

		int position = 0;

		pageElements.add(
			_getCollectionDisplayPageElement(
				new PageElement[0], position++, scopeGroupId));
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
				StringPool.BLANK, position++));
		pageElements.add(
			_getRepeatableFieldsCollectionDisplayPageElement(
				position, scopeGroupId));

		return pageElements.toArray(new PageElement[0]);
	}

	private static AssetListEntry _addAssetListEntry(
			JournalArticle journalArticle, ServiceContext serviceContext)
		throws Exception {

		AssetListEntry assetListEntry =
			AssetListEntryLocalServiceUtil.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				JournalArticle.class.getName());

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		AssetListEntryLocalServiceUtil.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry.getEntryId()},
			SegmentsEntryConstants.ID_DEFAULT, serviceContext);

		return assetListEntry;
	}

	private static DefaultFragmentReference _addDefaultFragmentReference(
		String fragmentEntryKey) {

		DefaultFragmentReference defaultFragmentReference =
			new DefaultFragmentReference();

		defaultFragmentReference.setDefaultFragmentKey(
			() -> {
				FragmentEntry fragmentEntry =
					FragmentCollectionContributorRegistryUtil.getFragmentEntry(
						fragmentEntryKey);

				return fragmentEntry.getFragmentEntryKey();
			});
		defaultFragmentReference.setFragmentReferenceType(
			FragmentReference.FragmentReferenceType.DEFAULT_FRAGMENT_REFERENCE);

		return defaultFragmentReference;
	}

	private static FragmentItemExternalReference
		_addFragmentItemExternalReference(
			FragmentEntry fragmentEntry, Scope scope) {

		FragmentItemExternalReference fragmentItemExternalReference =
			new FragmentItemExternalReference();

		fragmentItemExternalReference.setExternalReferenceCode(
			fragmentEntry.getExternalReferenceCode());
		fragmentItemExternalReference.setFragmentReferenceType(
			FragmentReference.FragmentReferenceType.
				FRAGMENT_ITEM_EXTERNAL_REFERENCE);
		fragmentItemExternalReference.setScope(scope);

		return fragmentItemExternalReference;
	}

	private static void _assertBasicFragmentInstancePageElementDefinition(
			BasicFragmentInstancePageElementDefinition
				actualBasicFragmentInstancePageElementDefinition,
			BasicFragmentInstancePageElementDefinition
				expectedBasicFragmentInstancePageElementDefinition)
		throws PortalException {

		FragmentInstance actualFragmentInstance =
			actualBasicFragmentInstancePageElementDefinition.
				getFragmentInstance();

		FragmentEditableElement[] actualFragmentEditableElements =
			actualFragmentInstance.getFragmentEditableElements();

		FragmentEditableElementValue actualFragmentEditableElementValue =
			actualFragmentEditableElements[0].getFragmentEditableElementValue();

		TextFragmentEditableElementValue
			actualTextFragmentEditableElementValue =
				(TextFragmentEditableElementValue)
					actualFragmentEditableElementValue;

		FragmentLinkTextValue actualFragmentLinkTextValue =
			actualTextFragmentEditableElementValue.getFragmentLinkTextValue();

		TextFragmentValue actualTextFragmentValue =
			actualFragmentLinkTextValue.getTextFragmentValue();

		TextFragmentMappedValue actualTextFragmentMappedValue =
			(TextFragmentMappedValue)actualTextFragmentValue;

		FragmentMappedValue actualFragmentMappedValue =
			actualTextFragmentMappedValue.getFragmentMappedValue();

		Mapping actualMapping = actualFragmentMappedValue.getMapping();

		String actualFieldKey = actualMapping.getFieldKey();

		FragmentInstance expectedFragmentInstance =
			expectedBasicFragmentInstancePageElementDefinition.
				getFragmentInstance();

		FragmentEditableElement[] expectedFragmentEditableElements =
			expectedFragmentInstance.getFragmentEditableElements();

		FragmentEditableElementValue expectedFragmentEditableElementValue =
			expectedFragmentEditableElements[0].
				getFragmentEditableElementValue();

		TextFragmentEditableElementValue
			expectedTextFragmentEditableElementValue =
				(TextFragmentEditableElementValue)
					expectedFragmentEditableElementValue;

		FragmentLinkTextValue expectedFragmentLinkTextValue =
			expectedTextFragmentEditableElementValue.getFragmentLinkTextValue();

		TextFragmentValue expectedTextFragmentValue =
			expectedFragmentLinkTextValue.getTextFragmentValue();

		TextFragmentMappedValue expectedTextFragmentMappedValue =
			(TextFragmentMappedValue)expectedTextFragmentValue;

		FragmentMappedValue expectedFragmentMappedValue =
			expectedTextFragmentMappedValue.getFragmentMappedValue();

		Mapping expectedMapping = expectedFragmentMappedValue.getMapping();

		String expectedFieldKey = expectedMapping.getFieldKey();

		if (expectedFieldKey.contains("__ERC__")) {
			Assert.assertEquals(expectedFieldKey, actualFieldKey);

			return;
		}

		long templateEntryId = GetterUtil.getLong(
			expectedFieldKey.substring("ddmTemplate__ddmTemplate_".length()));

		TemplateEntry templateEntry =
			TemplateEntryLocalServiceUtil.getTemplateEntry(templateEntryId);

		Company company = CompanyLocalServiceUtil.getCompany(
			TestPropsValues.getCompanyId());

		if (templateEntry.getGroupId() == company.getGroupId()) {
			Assert.assertEquals(
				_getCompanyGroupTemplateEntryExternalUniqueIdFieldKey(
					templateEntry),
				actualFieldKey);

			return;
		}

		Assert.assertEquals(
			_getScopeGroupTemplateEntryExternalUniqueIdFieldKey(templateEntry),
			actualFieldKey);
	}

	private static void _assertPageElement(
			PageElement actualPageElement, PageElement expectedPageElement)
		throws PortalException {

		PageElementDefinition actualPageElementDefinition =
			actualPageElement.getPageElementDefinition();

		if (actualPageElementDefinition instanceof
				BasicFragmentInstancePageElementDefinition) {

			PageElementDefinition expectedPageElementDefinition =
				expectedPageElement.getPageElementDefinition();

			BasicFragmentInstancePageElementDefinition
				actualBasicFragmentInstancePageElementDefinition =
					(BasicFragmentInstancePageElementDefinition)
						actualPageElementDefinition;
			BasicFragmentInstancePageElementDefinition
				expectedBasicFragmentInstancePageElementDefinition =
					(BasicFragmentInstancePageElementDefinition)
						expectedPageElementDefinition;

			_assertBasicFragmentInstancePageElementDefinition(
				actualBasicFragmentInstancePageElementDefinition,
				expectedBasicFragmentInstancePageElementDefinition);

			return;
		}

		if (actualPageElementDefinition instanceof
				CollectionDisplayPageElementDefinition ||
			actualPageElementDefinition instanceof
				CollectionItemPageElementDefinition) {

			_assertPageElement(
				actualPageElement.getPageElements()[0],
				expectedPageElement.getPageElements()[0]);
		}
	}

	private static void _assertPageExperience(
			PageExperience actualPageExperience,
			PageExperience expectedPageExperience)
		throws PortalException {

		PageElement[] expectedPageElements =
			expectedPageExperience.getPageElements();

		Assert.assertEquals(
			Arrays.toString(expectedPageElements), 12,
			expectedPageElements.length);

		PageElement[] actualPageElements =
			actualPageExperience.getPageElements();

		Assert.assertEquals(
			Arrays.toString(actualPageElements), expectedPageElements.length,
			actualPageElements.length);

		for (int i = 0; i < expectedPageElements.length; i++) {
			_assertPageElement(actualPageElements[i], expectedPageElements[i]);
		}
	}

	private static PageElement _getBasicFragmentInstancePageElement(
		String className,
		FragmentMappedValueItemContextReference.ContextSource contextSource,
		String externalReferenceCode, String fieldKey, String fragmentKey,
		String scopeExternalReferenceCode, long scopeGroupId) {

		FragmentEditableElement[] fragmentEditableElements = {
			FragmentEditableElementTestUtil.getTextFragmentEditableElement(
				className, contextSource, externalReferenceCode, fieldKey,
				scopeExternalReferenceCode)
		};

		return _getPageElement(
			RandomTestUtil.randomString(),
			getBasicFragmentInstancePageElementDefinition(
				null, Collections.emptyMap(), fragmentKey,
				fragmentEditableElements, Boolean.FALSE, scopeGroupId));
	}

	private static PageElement _getBasicFragmentInstancePageElement(
		String fieldKey, String fragmentKey, JournalArticle journalArticle,
		int position, long scopeGroupId) {

		PageElement pageElement = _getBasicFragmentInstancePageElement(
			"com.liferay.journal.model.JournalArticle", null,
			journalArticle.getExternalReferenceCode(), fieldKey, fragmentKey,
			"L_GLOBAL", scopeGroupId);

		pageElement.setPosition(position);

		return pageElement;
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
		PageElement[] collectionItemPageElements, int position,
		long scopeGroupId) {

		PageElement collectionDisplayPageElement = _getPageElement(
			getPageElementDefinition(
				PageElementDefinition.Type.COLLECTION_DISPLAY, scopeGroupId),
			StringPool.BLANK, position);

		collectionDisplayPageElement.setPageElements(
			new PageElement[] {
				_getPageElement(
					RandomTestUtil.randomString(),
					getPageElementDefinition(
						PageElementDefinition.Type.COLLECTION_ITEM,
						scopeGroupId),
					collectionItemPageElements,
					collectionDisplayPageElement.getExternalReferenceCode(),
					position)
			});

		return collectionDisplayPageElement;
	}

	private static PageElement _getCollectionDisplayPageElement(
			String fieldKey, String fragmentKey, JournalArticle journalArticle,
			int position, long scopeGroupId, ServiceContext serviceContext)
		throws Exception {

		PageElement[] pageElements = {
			_getBasicFragmentInstancePageElement(
				null,
				FragmentMappedValueItemContextReference.ContextSource.
					COLLECTION_ITEM,
				null, fieldKey, fragmentKey, null, scopeGroupId)
		};

		AssetListEntry assetListEntry = _addAssetListEntry(
			journalArticle, serviceContext);

		CollectionItemExternalReference collectionItemExternalReference =
			new CollectionItemExternalReference();

		collectionItemExternalReference.setCollectionType(
			CollectionReference.CollectionType.COLLECTION);
		collectionItemExternalReference.setExternalReferenceCode(
			assetListEntry.getExternalReferenceCode());
		collectionItemExternalReference.setScope(
			new Scope() {
				{
					setExternalReferenceCode("L_GLOBAL");
					setType(Type.SITE);
				}
			});

		PageElement pageElement = _getPageElement(
			_getCollectionDisplayPageElementDefinition(
				collectionItemExternalReference, Boolean.FALSE),
			StringPool.BLANK, position);

		pageElement.setPageElements(
			new PageElement[] {
				_getPageElement(
					RandomTestUtil.randomString(),
					getPageElementDefinition(
						Boolean.FALSE,
						PageElementDefinition.Type.COLLECTION_ITEM,
						scopeGroupId),
					pageElements, pageElement.getExternalReferenceCode(), 0)
			});

		pageElement.setPosition(position);

		return pageElement;
	}

	private static CollectionDisplayPageElementDefinition
		_getCollectionDisplayPageElementDefinition(
			CollectionReference collectionReference, Boolean hidden) {

		return new CollectionDisplayPageElementDefinition() {
			{
				setCollectionDisplayListStyle(_getCollectionDisplayListStyle());
				setCollectionDisplayViewports(
					() -> _getDesktopCollectionDisplayViewports(hidden));
				setCollectionSettings(
					() -> _getCollectionSettings(collectionReference));
				setDisplayAllItems(Boolean.FALSE);
				setDisplayAllPages(Boolean.TRUE);
				setNumberOfItems(5);
				setNumberOfItemsPerPage(5);
				setNumberOfPages(20);
				setPaginationType(PaginationType.NONE);
				setType(Type.COLLECTION_DISPLAY);
			}
		};
	}

	private static CollectionSettings _getCollectionSettings(
		CollectionReference collectionReference) {

		CollectionSettings collectionSettings = new CollectionSettings();

		collectionSettings.setCollectionReference(collectionReference);

		return collectionSettings;
	}

	private static String _getCompanyGroupTemplateEntryExternalUniqueIdFieldKey(
		TemplateEntry templateEntry) {

		return "ddmTemplate__ddmTemplate__ERC__" +
			templateEntry.getExternalReferenceCode() + "__SERC__L_GLOBAL";
	}

	private static CollectionDisplayViewport[]
		_getDesktopCollectionDisplayViewports(Boolean hidden) {

		CollectionDisplayViewportDefinition
			collectionDisplayViewportDefinition =
				new CollectionDisplayViewportDefinition();

		collectionDisplayViewportDefinition.setHidden(hidden);
		collectionDisplayViewportDefinition.setNumberOfColumns(1);

		CollectionDisplayViewport collectionDisplayViewport =
			new CollectionDisplayViewport() {
				{
					setId(Id.DESKTOP);
				}
			};

		collectionDisplayViewport.setCollectionDisplayViewportDefinition(
			collectionDisplayViewportDefinition);

		return new CollectionDisplayViewport[] {collectionDisplayViewport};
	}

	private static PageElement _getDisplayPageItemPageElement(
		String fieldKey, String fragmentKey, int position, long scopeGroupId) {

		PageElement pageElement = _getBasicFragmentInstancePageElement(
			null,
			FragmentMappedValueItemContextReference.ContextSource.
				DISPLAY_PAGE_ITEM,
			null, fieldKey, fragmentKey, null, scopeGroupId);

		pageElement.setPosition(position);

		return pageElement;
	}

	private static FragmentInstance _getFragmentInstance(
		Map<String, Object> configurationValuesMap,
		FragmentEditableElement[] curFragmentEditableElements,
		BackgroundImageValue fragmentInstanceBackgroundImageValue,
		FragmentRenderer fragmentRenderer, long scopeGroupId) {

		JSONObject configurationJSONObject =
			fragmentRenderer.getConfigurationJSONObject(
				new DefaultFragmentRendererContext(null));

		return new FragmentInstance() {
			{
				setBackgroundImageValue(
					() -> fragmentInstanceBackgroundImageValue);
				setConfiguration(
					() -> GetterUtil.getString(
						JSONFactoryUtil.toString(configurationJSONObject)));
				setCss(() -> StringPool.BLANK);
				setCssClasses(
					() -> new String[] {RandomTestUtil.randomString()});
				setDatePropagated(RandomTestUtil::nextDate);
				setFragmentConfigurationFieldValues(
					() ->
						FragmentConfigurationFieldValueTestUtil.
							getFragmentConfigurationFieldValuesMap(
								configurationJSONObject, configurationValuesMap,
								scopeGroupId));
				setFragmentEditableElements(() -> curFragmentEditableElements);
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
				setHtml(() -> StringPool.BLANK);
				setIndexed(RandomTestUtil::randomBoolean);
				setJs(() -> StringPool.BLANK);
				setName(RandomTestUtil::randomString);
				setNamespace(RandomTestUtil::randomString);
				setUuid(RandomTestUtil::randomString);
			}
		};
	}

	private static FragmentInstance _getFragmentInstance(
		Map<String, Object> configurationValuesMap,
		FragmentEditableElement[] fragmentEditableElements,
		FragmentEntry fragmentEntry,
		BackgroundImageValue fragmentInstanceBackgroundImageValue,
		String fragmentInstanceExternalReferenceCode, Boolean hidden,
		String namespace, long scopeGroupId, String uuid,
		WidgetInstance[] widgetInstances) {

		FragmentInstance fragmentInstance = new FragmentInstance();

		fragmentInstance.setBackgroundImageValue(
			() -> fragmentInstanceBackgroundImageValue);
		fragmentInstance.setConfiguration(fragmentEntry::getConfiguration);
		fragmentInstance.setCss(fragmentEntry::getCss);
		fragmentInstance.setCssClasses(
			() -> new String[] {RandomTestUtil.randomString()});
		fragmentInstance.setDatePropagated(RandomTestUtil::nextDate);
		fragmentInstance.setFragmentConfigurationFieldValues(
			() ->
				FragmentConfigurationFieldValueTestUtil.
					getFragmentConfigurationFieldValuesMap(
						JSONFactoryUtil.createJSONObject(
							fragmentEntry.getConfiguration()),
						configurationValuesMap, scopeGroupId));
		fragmentInstance.setFragmentEditableElements(
			() -> fragmentEditableElements);
		fragmentInstance.setFragmentInstanceExternalReferenceCode(
			fragmentInstanceExternalReferenceCode);
		fragmentInstance.setFragmentReference(
			() -> {
				if (fragmentEntry.getFragmentEntryId() == 0) {
					return _addDefaultFragmentReference(
						fragmentEntry.getFragmentEntryKey());
				}

				return _addFragmentItemExternalReference(
					fragmentEntry,
					ScopeTestUtil.getItemScope(
						fragmentEntry.getGroupId(), scopeGroupId));
			});
		fragmentInstance.setFragmentViewports(
			FragmentViewportTestUtil.getFragmentViewports(hidden));
		fragmentInstance.setHtml(fragmentEntry::getHtml);
		fragmentInstance.setIndexed(RandomTestUtil::randomBoolean);
		fragmentInstance.setJs(fragmentEntry::getJs);
		fragmentInstance.setName(RandomTestUtil::randomString);
		fragmentInstance.setNamespace(namespace);
		fragmentInstance.setUuid(uuid);
		fragmentInstance.setWidgetInstances(() -> widgetInstances);

		return fragmentInstance;
	}

	private static List<FragmentReference> _getFragmentItemExternalReferences(
			long groupId, long scopeGroupId)
		throws PortalException {

		List<FragmentReference> fragmentReferences = new ArrayList<>();

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.addFragmentCollection(
				null, TestPropsValues.getUserId(), groupId,
				StringUtil.randomString(), StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(groupId));

		Scope scope = ScopeTestUtil.getItemScope(groupId, scopeGroupId);

		for (int i = 0; i < 3; i++) {
			fragmentReferences.add(
				_addFragmentItemExternalReference(
					FragmentEntryTestUtil.addFragmentEntry(
						fragmentCollection.getFragmentCollectionId(),
						fragmentCollection.getGroupId()),
					scope));
		}

		return fragmentReferences;
	}

	private static FragmentReference[] _getFragmentReferences(
			long groupId, long... itemGroupIds)
		throws PortalException {

		List<FragmentReference> fragmentReferences = new ArrayList<>();

		fragmentReferences.add(
			_addDefaultFragmentReference("BASIC_COMPONENT-button"));
		fragmentReferences.add(
			_addDefaultFragmentReference("INPUTS-date-input"));

		for (long itemGroupId : itemGroupIds) {
			fragmentReferences.addAll(
				_getFragmentItemExternalReferences(itemGroupId, groupId));
		}

		return fragmentReferences.toArray(new FragmentReference[0]);
	}

	private static PageElement _getGridPageElement(int position) {
		String externalReferenceCode = RandomTestUtil.randomString();

		ModuleViewport[] moduleViewports = {
			new ModuleViewport() {
				{
					setId(Id.DESKTOP);
					setModuleViewportDefinition(
						() -> new ModuleViewportDefinition() {
							{
								setSize(4);
							}
						});
				}
			},
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
								GridViewport.Id.DESKTOP),
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
					setNumberOfModules(1);
					setReverseOrder(Boolean.FALSE);
					setType(Type.GRID);
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
		PageElementDefinition pageElementDefinition) {

		PageElement pageElement = new PageElement();

		pageElement.setExternalReferenceCode(externalReferenceCode);
		pageElement.setPageElementDefinition(pageElementDefinition);
		pageElement.setPageElements(new PageElement[0]);
		pageElement.setParentExternalReferenceCode(StringPool.BLANK);
		pageElement.setPosition(0);

		return pageElement;
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

		for (PageElement childPageElement : pageElements) {
			childPageElement.setParentExternalReferenceCode(
				externalReferenceCode);
		}

		return pageElement;
	}

	private static PageElementDefinition.Type _getRandomType() {
		return _types.get(RandomTestUtil.randomInt(0, _types.size() - 1));
	}

	private static PageElement _getRepeatableFieldsCollectionDisplayPageElement(
			int position, long scopeGroupId)
		throws Exception {

		String fieldName = RandomTestUtil.randomString();

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			fieldName, "string", "text", true, DDMFormFieldTypeConstants.TEXT,
			new Locale[] {LocaleUtil.getSiteDefault()},
			LocaleUtil.getSiteDefault());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			scopeGroupId, JournalArticle.class.getName(), 0, ddmForm,
			LocaleUtil.getSiteDefault(),
			ServiceContextTestUtil.getServiceContext(scopeGroupId));

		RepeatableFieldsCollectionProviderReference
			repeatableFieldsCollectionProviderReference =
				new RepeatableFieldsCollectionProviderReference();

		repeatableFieldsCollectionProviderReference.setClassName(
			JournalArticle.class::getName);
		repeatableFieldsCollectionProviderReference.setCollectionType(
			() ->
				CollectionReference.CollectionType.
					REPEATABLE_FIELDS_COLLECTION_PROVIDER);
		repeatableFieldsCollectionProviderReference.setFieldName(fieldName);
		repeatableFieldsCollectionProviderReference.setSubTypeExternalReference(
			() -> new ItemExternalReference() {
				{
					setClassName(DDMStructure.class::getName);
					setExternalReferenceCode(ddmStructure.getStructureKey());
				}
			});

		PageElement pageElement = _getPageElement(
			_getCollectionDisplayPageElementDefinition(
				repeatableFieldsCollectionProviderReference, Boolean.FALSE),
			StringPool.BLANK, position);

		pageElement.setPageElements(
			new PageElement[] {
				_getPageElement(
					RandomTestUtil.randomString(),
					getPageElementDefinition(
						Boolean.FALSE,
						PageElementDefinition.Type.COLLECTION_ITEM,
						scopeGroupId),
					new PageElement[0], pageElement.getExternalReferenceCode(),
					0)
			});

		pageElement.setPosition(position);

		return pageElement;
	}

	private static String _getScopeGroupTemplateEntryExternalUniqueIdFieldKey(
		TemplateEntry templateEntry) {

		return "ddmTemplate__ddmTemplate__ERC__" +
			templateEntry.getExternalReferenceCode();
	}

	private static TemplateEntry _getTemplateEntry(
			JournalArticle journalArticle, String text,
			ServiceContext serviceContext)
		throws Exception {

		return TemplateTestUtil.addTemplateEntry(
			JournalArticle.class.getName(),
			String.valueOf(journalArticle.getDDMStructureId()), text,
			RandomTestUtil.randomString(),
			TemplateTestUtil.getSampleScriptFTL("JournalArticle_title", text),
			serviceContext);
	}

	private static String _getTemplateEntryUniqueIdFieldKey(
		TemplateEntry templateEntry) {

		return "ddmTemplate__ddmTemplate_" + templateEntry.getTemplateEntryId();
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
			PageElementDefinition.Type.BASIC_FRAGMENT,
			PageElementDefinition.Type.CONTAINER);

}
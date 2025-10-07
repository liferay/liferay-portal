/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.exportimport.data.handler;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.controller.PortletExportController;
import com.liferay.exportimport.controller.PortletImportController;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportProcessCallbackRegistry;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerStatusMessageSender;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManager;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.lar.PermissionImporter;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.configuration.LayoutExportImportConfiguration;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.model.LayoutLocalization;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOSite;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.service.LayoutLocalizationLocalService;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutRevisionConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutStagingHandler;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.adapter.StagedTheme;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutBranchLocalService;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutTemplateLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.PortletInstanceSettingsLocator;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.model.adapter.impl.StagedThemeImpl;
import com.liferay.portal.service.impl.LayoutLocalServiceHelper;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.sites.kernel.util.Sites;
import com.liferay.staging.configuration.StagingConfiguration;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	configurationPid = "com.liferay.layout.configuration.LayoutExportImportConfiguration",
	service = StagedModelDataHandler.class
)
public class LayoutStagedModelDataHandler
	extends BaseStagedModelDataHandler<Layout> {

	public static final String[] CLASS_NAMES = {Layout.class.getName()};

	@Override
	public void deleteStagedModel(Layout layout) throws PortalException {
		_layoutLocalService.deleteLayout(layout);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		JSONObject extraDataJSONObject = _jsonFactory.createJSONObject(
			extraData);

		boolean privateLayout = extraDataJSONObject.getBoolean("privateLayout");

		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			uuid, groupId, privateLayout);

		if ((layout == null) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				uuid, groupId, !privateLayout);
		}

		if (layout != null) {
			deleteStagedModel(layout);
		}
	}

	@Override
	public List<Layout> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _layoutLocalService.getLayoutsByUuidAndCompanyId(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new StagedModelModifiedDateComparator<Layout>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(Layout layout) {
		try {
			List<Layout> ancestorLayouts = layout.getAncestors();

			StringBundler sb = new StringBundler(
				(4 * ancestorLayouts.size()) + 1);

			Collections.reverse(ancestorLayouts);

			for (Layout ancestorLayout : ancestorLayouts) {
				sb.append(ancestorLayout.getNameCurrentValue());
				sb.append(StringPool.SPACE);
				sb.append(StringPool.GREATER_THAN);
				sb.append(StringPool.SPACE);
			}

			sb.append(layout.getNameCurrentValue());

			return sb.toString();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return layout.getNameCurrentValue();
	}

	@Override
	public int[] getExportableStatuses() {
		return new int[] {
			WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_DRAFT
		};
	}

	@Override
	public Map<String, String> getReferenceAttributes(
		PortletDataContext portletDataContext, Layout layout) {

		return HashMapBuilder.put(
			"layout-id", String.valueOf(layout.getLayoutId())
		).put(
			"private-layout", String.valueOf(layout.isPrivateLayout())
		).build();
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		validateMissingGroupReference(portletDataContext, referenceElement);

		String uuid = GetterUtil.getString(
			referenceElement.attributeValue("uuid"));

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		groupId = MapUtil.getLong(groupIds, groupId);

		boolean privateLayout = GetterUtil.getBoolean(
			referenceElement.attributeValue("private-layout"));

		Layout existingLayout = fetchMissingReference(
			uuid, groupId, privateLayout);

		if (existingLayout == null) {
			return false;
		}

		return true;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_layoutExportImportConfiguration = ConfigurableUtil.createConfigurable(
			LayoutExportImportConfiguration.class, properties);
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Layout layout)
		throws Exception {

		if (!_layoutExportImportConfiguration.exportDraftLayout() &&
			!layout.isPublished()) {

			return;
		}

		Element layoutElement = portletDataContext.getExportDataElement(layout);

		_populateElementLayoutMetadata(layout, layoutElement);

		layoutElement.addAttribute(Constants.ACTION, Constants.ADD);

		portletDataContext.setPlid(layout.getPlid());

		long parentLayoutId = layout.getParentLayoutId();

		if (parentLayoutId != LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
			Layout parentLayout = _layoutLocalService.fetchLayout(
				layout.getGroupId(), layout.isPrivateLayout(), parentLayoutId);

			String parentLayoutUuid = layoutElement.attributeValue(
				"parent-layout-uuid");

			if ((parentLayout != null) && Validator.isNull(parentLayoutUuid)) {
				if (_isExportParentLayout(
						portletDataContext.getLayoutIds(), parentLayoutId)) {

					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, layout, parentLayout,
						PortletDataContext.REFERENCE_TYPE_PARENT);
				}
				else {
					portletDataContext.addReferenceElement(
						layout, layoutElement, parentLayout,
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
				}

				layoutElement.addAttribute(
					"parent-layout-friendly-url",
					parentLayout.getFriendlyURL());
				layoutElement.addAttribute(
					"parent-layout-uuid", parentLayout.getUuid());
			}
		}

		if (_layoutExportImportConfiguration.exportDraftLayout()) {
			_exportDraftLayout(layout, layoutElement, portletDataContext);
		}

		_exportMasterLayout(layout, layoutElement, portletDataContext);

		_exportLayoutPageTemplateEntry(
			layout, layoutElement, portletDataContext);

		_exportLayoutFriendlyURLs(layout, portletDataContext);

		_exportLayoutSEOEntries(layout, portletDataContext);

		_exportLayoutClassedModelUsages(layout, portletDataContext);

		if (layout.isIconImage()) {
			_exportLayoutIconImage(layout, layoutElement, portletDataContext);
		}

		_exportLayoutPageTemplateStructure(layout, portletDataContext);

		if (layout.isTypeLinkToLayout()) {
			_exportLinkedLayout(layout, layoutElement, portletDataContext);
		}
		else if (layout.isTypePortlet() || layout.isTypeAssetDisplay() ||
				 layout.isTypeContent()) {

			_exportLayoutPortlets(layout, layoutElement, portletDataContext);
		}
		else if (layout.isTypeURL()) {
			_exportLinkedURL(layout, layoutElement, portletDataContext);
		}

		_exportFaviconFileEntry(layout, layoutElement, portletDataContext);

		_exportClientExtensionEntryRels(layout, portletDataContext);

		_exportLayoutLocalizations(layout, portletDataContext);

		_fixExportTypeSettings(layout);

		_exportTheme(layout, portletDataContext);

		_exportStyleBookEntry(layout, portletDataContext);

		portletDataContext.addClassedModel(
			layoutElement, ExportImportPathUtil.getModelPath(layout),
			LayoutStagingUtil.mergeLayoutRevisionIntoLayout(layout));
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, Element referenceElement)
		throws PortletDataException {

		importMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		long targetGroupId = MapUtil.getLong(groupIds, groupId);

		boolean privateLayout = GetterUtil.getBoolean(
			referenceElement.attributeValue("private-layout"));

		Layout existingLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			uuid, targetGroupId, privateLayout);

		if (existingLayout == null) {
			return;
		}

		Map<Long, Long> layoutPlids =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Layout.class);

		long plid = GetterUtil.getLong(
			referenceElement.attributeValue("class-pk"));

		layoutPlids.put(plid, existingLayout.getPlid());

		if ((groupId != portletDataContext.getSourceGroupId()) ||
			(privateLayout != portletDataContext.isPrivateLayout())) {

			return;
		}

		Map<Long, Layout> layouts =
			(Map<Long, Layout>)portletDataContext.getNewPrimaryKeysMap(
				Layout.class + ".layout");

		long layoutId = GetterUtil.getLong(
			referenceElement.attributeValue("layout-id"));

		layouts.put(layoutId, existingLayout);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Layout layout)
		throws Exception {

		long groupId = portletDataContext.getGroupId();
		long userId = portletDataContext.getUserId(layout.getUserUuid());

		Element layoutElement =
			portletDataContext.getImportDataStagedModelElement(layout);

		long layoutId = GetterUtil.getLong(
			layoutElement.attributeValue("layout-id"));

		long oldLayoutId = layoutId;

		boolean privateLayout = false;

		if ((portletDataContext.isOriginalPrivateLayout() &&
			 !layout.isTypeAssetDisplay()) ||
			GetterUtil.getBoolean(
				layoutElement.attributeValue("layout-content-page-template")) ||
			GetterUtil.getBoolean(
				layoutElement.attributeValue("layout-layout-prototype")) ||
			GetterUtil.getBoolean(
				layoutElement.attributeValue("layout-master-page-template"))) {

			privateLayout = true;
		}

		portletDataContext.setPrivateLayout(privateLayout);

		Map<Long, Layout> layouts =
			(Map<Long, Layout>)portletDataContext.getNewPrimaryKeysMap(
				Layout.class + ".layout");
		Map<Long, Long> layoutPlids =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Layout.class);

		Layout existingLayout = null;

		String uuid = layout.getUuid();
		String friendlyURL = layout.getFriendlyURL();

		String layoutsImportMode = MapUtil.getString(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_UUID);

		if (layoutsImportMode.equals(
				PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_ADD_AS_NEW)) {

			layoutId = _layoutLocalService.getNextLayoutId(
				groupId, privateLayout);

			friendlyURL = StringPool.SLASH + layoutId;
		}
		else if (layoutsImportMode.equals(
					PortletDataHandlerKeys.
						LAYOUTS_IMPORT_MODE_ADD_AS_NEW_PROTOTYPE)) {

			layoutId = _layoutLocalService.getNextLayoutId(
				groupId, privateLayout);

			uuid = PortalUUIDUtil.generate();
		}
		else if (layoutsImportMode.equals(
					PortletDataHandlerKeys.
						LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_NAME)) {

			Locale locale = LocaleUtil.getSiteDefault();

			String localizedName = layout.getName(locale);

			List<Layout> previousLayouts = _layoutLocalService.getLayouts(
				groupId, privateLayout);

			for (Layout curLayout : previousLayouts) {
				if (localizedName.equals(curLayout.getName(locale)) ||
					friendlyURL.equals(curLayout.getFriendlyURL())) {

					existingLayout = curLayout;

					break;
				}
			}

			if (existingLayout == null) {
				layoutId = _layoutLocalService.getNextLayoutId(
					groupId, privateLayout);
			}
		}
		else if (layoutsImportMode.equals(
					PortletDataHandlerKeys.
						LAYOUTS_IMPORT_MODE_CREATED_FROM_PROTOTYPE)) {

			existingLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				uuid, groupId, privateLayout);

			if (existingLayout != null) {
				layoutPlids.put(layout.getPlid(), existingLayout.getPlid());
			}

			if (_sites.isLayoutModifiedSinceLastMerge(existingLayout) ||
				!_isLayoutOutdated(existingLayout, layout)) {

				layouts.put(oldLayoutId, existingLayout);

				return;
			}

			LayoutFriendlyURL layoutFriendlyURL =
				_layoutFriendlyURLLocalService.fetchFirstLayoutFriendlyURL(
					groupId, privateLayout, friendlyURL);

			if ((layoutFriendlyURL != null) && (existingLayout == null)) {
				Layout mergeFailFriendlyURLLayout =
					_layoutLocalService.getLayout(layoutFriendlyURL.getPlid());

				_addMergeFailFriendlyURLLayout(mergeFailFriendlyURLLayout);

				if (!_log.isWarnEnabled()) {
					return;
				}

				_log.warn(
					StringBundler.concat(
						"Layout with layout ID ", layout.getLayoutId(),
						" cannot be propagated because the friendly URL ",
						"conflicts with the friendly URL of layout with ",
						"layout ID ",
						mergeFailFriendlyURLLayout.getLayoutId()));

				return;
			}
		}
		else {

			// The default behavior of import mode is
			// PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_UUID

			existingLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				uuid, groupId, privateLayout);

			if (existingLayout == null) {
				existingLayout = _layoutLocalService.fetchLayoutByFriendlyURL(
					groupId, privateLayout, friendlyURL);

				if ((existingLayout != null) &&
					!Objects.equals(
						existingLayout.getType(), layout.getType())) {

					_log.warn(
						StringBundler.concat(
							"The layout with friendly URL ", friendlyURL,
							" and type ", layout.getType(),
							" has the same friendly URL as an existing layout ",
							"of type ", existingLayout.getType(),
							". It will be imported as a new layout modifying ",
							"its URL to ensure uniqueness."));

					existingLayout = null;
				}
			}

			if ((existingLayout == null) && (layout.getClassPK() > 0) &&
				layoutPlids.containsKey(layout.getClassPK())) {

				Layout curLayout = _layoutLocalService.fetchLayout(
					layoutPlids.get(layout.getClassPK()));

				if (curLayout != null) {
					existingLayout = curLayout.fetchDraftLayout();
				}
			}

			if (existingLayout == null) {
				layoutId = _layoutLocalService.getNextLayoutId(
					groupId, privateLayout);
			}
		}

		if (existingLayout != null) {
			_addMasterLayoutRevision(existingLayout);
		}

		if (_log.isDebugEnabled()) {
			StringBundler sb = new StringBundler(7);

			sb.append("Layout with {groupId=");
			sb.append(groupId);
			sb.append(",privateLayout=");
			sb.append(privateLayout);
			sb.append(",layoutId=");
			sb.append(layoutId);

			if (existingLayout == null) {
				sb.append("} does not exist");

				_log.debug(sb.toString());
			}
			else {
				sb.append("} exists");

				_log.debug(sb.toString());
			}
		}

		Layout importedLayout = null;

		if (existingLayout == null) {
			importedLayout = _layoutLocalService.createLayout(
				_layoutLocalServiceHelper.getUniquePlid());

			if (layoutsImportMode.equals(
					PortletDataHandlerKeys.
						LAYOUTS_IMPORT_MODE_CREATED_FROM_PROTOTYPE)) {

				importedLayout.setLayoutSetPrototypeLayoutERC(
					layout.getExternalReferenceCode());

				layoutId = _layoutLocalService.getNextLayoutId(
					groupId, privateLayout);
			}
			else {
				importedLayout.setCreateDate(layout.getCreateDate());
				importedLayout.setModifiedDate(layout.getModifiedDate());
				importedLayout.setLayoutPrototypeUuid(
					layout.getLayoutPrototypeUuid());
				importedLayout.setLayoutPrototypeLinkEnabled(
					layout.isLayoutPrototypeLinkEnabled());
				importedLayout.setLayoutSetPrototypeLayoutERC(
					layout.getLayoutSetPrototypeLayoutERC());
			}

			importedLayout.setUuid(uuid);
			importedLayout.setGroupId(groupId);
			importedLayout.setUserId(userId);
			importedLayout.setPrivateLayout(privateLayout);
			importedLayout.setLayoutId(layoutId);

			_initNewLayoutPermissions(
				portletDataContext.getCompanyId(), groupId, userId, layout,
				importedLayout, privateLayout);

			importedLayout.setLayoutSet(
				_layoutSetLocalService.getLayoutSet(groupId, privateLayout));
		}
		else {
			importedLayout = existingLayout;
		}

		layoutPlids.put(layout.getPlid(), importedLayout.getPlid());

		layouts.put(oldLayoutId, importedLayout);

		portletDataContext.setPlid(importedLayout.getPlid());

		String draftLayoutUuid = layoutElement.attributeValue(
			"draft-layout-uuid");

		if (Validator.isNotNull(draftLayoutUuid)) {
			Element draftLayoutElement =
				portletDataContext.getReferenceDataElement(
					layout, Layout.class, layout.getGroupId(), draftLayoutUuid);

			long originalPlid = portletDataContext.getPlid();

			try {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, draftLayoutElement);
			}
			finally {
				portletDataContext.setPlid(originalPlid);
				portletDataContext.setPrivateLayout(privateLayout);
			}

			long draftLayoutPlid = layoutPlids.get(
				GetterUtil.getLong(
					layoutElement.attributeValue("draft-layout-plid")));

			Layout draftLayout = _layoutLocalService.getLayout(draftLayoutPlid);

			draftLayout.setClassNameId(_portal.getClassNameId(Layout.class));
			draftLayout.setClassPK(importedLayout.getPlid());

			draftLayout = _layoutLocalService.updateLayout(draftLayout);

			importedLayout.setPublishDate(draftLayout.getModifiedDate());
		}

		String masterLayoutUuid = layoutElement.attributeValue(
			"master-layout-uuid");

		if (Validator.isNotNull(masterLayoutUuid)) {
			Element masterLayoutElement =
				portletDataContext.getReferenceDataElement(
					layout, Layout.class, layout.getGroupId(),
					masterLayoutUuid);

			if (masterLayoutElement != null) {
				long originalPlid = portletDataContext.getPlid();

				try {
					StagedModelDataHandlerUtil.importStagedModel(
						portletDataContext, masterLayoutElement);
				}
				finally {
					portletDataContext.setPlid(originalPlid);
					portletDataContext.setPrivateLayout(privateLayout);
				}
			}

			long masterLayoutPlid = GetterUtil.getLong(
				layoutElement.attributeValue("master-layout-plid"));

			long importedMasterLayoutPlid = MapUtil.getLong(
				layoutPlids, masterLayoutPlid, masterLayoutPlid);

			importedLayout.setMasterLayoutPlid(importedMasterLayoutPlid);
		}
		else {
			importedLayout.setMasterLayoutPlid(0);
		}

		long parentPlid = layout.getParentPlid();
		long parentLayoutId = layout.getParentLayoutId();

		String parentLayoutUuid = GetterUtil.getString(
			layoutElement.attributeValue("parent-layout-uuid"));

		Element parentLayoutElement =
			portletDataContext.getReferenceDataElement(
				layout, Layout.class, layout.getGroupId(), parentLayoutUuid);

		if ((parentLayoutId != LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) &&
			(parentLayoutElement != null)) {

			String action = GetterUtil.getString(
				parentLayoutElement.attributeValue("action"));

			if (!action.equals(Constants.SKIP)) {
				long originalPlid = portletDataContext.getPlid();

				try {
					StagedModelDataHandlerUtil.importStagedModel(
						portletDataContext, parentLayoutElement);
				}
				finally {
					portletDataContext.setPlid(originalPlid);
					portletDataContext.setPrivateLayout(privateLayout);
				}
			}

			Layout importedParentLayout =
				_layoutLocalService.fetchLayoutByUuidAndGroupId(
					parentLayoutUuid, groupId, privateLayout);

			if (importedParentLayout == null) {
				String parentLayoutFriendlyURL = GetterUtil.getString(
					layoutElement.attributeValue("parent-layout-friendly-url"));

				importedParentLayout =
					_layoutLocalService.fetchLayoutByFriendlyURL(
						groupId, privateLayout, parentLayoutFriendlyURL);
			}

			if ((importedParentLayout == null) ||
				(importedParentLayout.getPlid() == importedLayout.getPlid())) {

				importedParentLayout = layouts.get(parentLayoutId);
			}

			if (importedParentLayout != null) {
				parentPlid = importedParentLayout.getPlid();
				parentLayoutId = importedParentLayout.getLayoutId();
			}
			else {
				parentPlid = 0;
				parentLayoutId = 0;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Importing layout with layout ID ", layoutId,
					" and parent layout ID ", parentLayoutId));
		}

		importedLayout.setCompanyId(portletDataContext.getCompanyId());

		if (layout.getLayoutPrototypeUuid() != null) {
			importedLayout.setModifiedDate(new Date());
		}

		importedLayout.setParentPlid(parentPlid);
		importedLayout.setParentLayoutId(parentLayoutId);
		importedLayout.setName(layout.getName());
		importedLayout.setTitle(layout.getTitle());
		importedLayout.setDescription(layout.getDescription());
		importedLayout.setKeywords(layout.getKeywords());
		importedLayout.setRobots(layout.getRobots());
		importedLayout.setType(layout.getType());

		String portletsMergeMode = MapUtil.getString(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.PORTLETS_MERGE_MODE,
			PortletDataHandlerKeys.PORTLETS_MERGE_MODE_REPLACE);

		if (layout.isTypePortlet() &&
			Validator.isNotNull(layout.getTypeSettings()) &&
			!portletsMergeMode.equals(
				PortletDataHandlerKeys.PORTLETS_MERGE_MODE_REPLACE)) {

			_mergePortlets(
				importedLayout, layout.getTypeSettings(), portletsMergeMode);
		}
		else if (layout.isTypeLinkToLayout()) {
			_importLinkedLayout(
				importedLayout, layout, layoutElement, portletDataContext);

			_updateTypeSettings(importedLayout, layout);
		}
		else if (layout.isTypeURL()) {
			_importLinkedURL(
				importedLayout, layout, layoutElement, portletDataContext);

			_updateTypeSettings(importedLayout, layout);
		}
		else {
			_updateTypeSettings(importedLayout, layout);
		}

		if (layoutsImportMode.equals(
				PortletDataHandlerKeys.
					LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_UUID) &&
			(layout.getLayoutPrototypeUuid() != null)) {

			_resetLastMergeTime(importedLayout, layout);
		}

		importedLayout.setHidden(layout.isHidden());
		importedLayout.setSystem(layout.isSystem());
		importedLayout.setFriendlyURL(
			_getUniqueFriendlyURL(
				importedLayout, friendlyURL, portletDataContext));

		if (layout.getIconImageId() > 0) {
			_importLayoutIconImage(
				importedLayout, layoutElement, portletDataContext);
		}
		else if (importedLayout.getIconImageId() > 0) {
			_imageLocalService.deleteImage(importedLayout.getIconImageId());

			importedLayout.setIconImageId(0);
		}

		_importStyleBookEntry(importedLayout, layout, portletDataContext);

		if (existingLayout == null) {
			try {
				int priority = layout.getPriority();

				if (!ExportImportThreadLocal.
						isInitialLayoutStagingInProcess()) {

					long finalParentLayoutId = parentLayoutId;
					boolean finalPrivateLayout = privateLayout;

					priority = TransactionInvokerUtil.invoke(
						_transactionConfig,
						() -> _layoutLocalServiceHelper.getNextPriority(
							groupId, null, finalPrivateLayout,
							finalParentLayoutId, -1));
				}

				importedLayout.setPriority(priority);
			}
			catch (Throwable throwable) {
				ReflectionUtil.throwException(throwable);
			}
		}

		importedLayout.setLayoutPrototypeUuid(
			_getLayoutPrototypeUuid(
				portletDataContext.getCompanyId(), layout, layoutElement));
		importedLayout.setLayoutPrototypeLinkEnabled(
			layout.isLayoutPrototypeLinkEnabled());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			layout);

		importedLayout.setExpandoBridgeAttributes(serviceContext);

		_importFaviconFileEntry(
			layout, layoutElement, importedLayout, portletDataContext);

		_importClientExtensionEntryRels(
			importedLayout, layout, portletDataContext);

		_importLayoutLocalizations(layout, portletDataContext);

		_staging.updateLastImportSettings(
			layoutElement, importedLayout, portletDataContext);

		_fixImportTypeSettings(importedLayout);

		_updateLastMergeLayoutModifiedTime(
			layoutElement, importedLayout, portletDataContext);

		_importTheme(importedLayout, layout, portletDataContext);

		importedLayout = _layoutLocalService.updateLayout(importedLayout);

		_importLayoutPageTemplateEntry(
			layout, layoutElement, portletDataContext);

		_importLayoutFriendlyURLs(importedLayout, layout, portletDataContext);

		_importAssets(importedLayout, layout, portletDataContext);

		_importLayoutPageTemplateStructures(
			importedLayout, layout, portletDataContext);

		// Import layout portlets after layout page template structures
		// have been imported to ensure portlet preferences are not deleted.
		// See LPD-16349.

		if ((layout.isTypePortlet() &&
			 Validator.isNotNull(layout.getTypeSettings())) ||
			layout.isTypeAssetDisplay() || layout.isTypeContent()) {

			_importLayoutPortlets(
				importedLayout, layoutElement, portletDataContext);
		}

		_importLayoutSEOEntries(layout, portletDataContext);

		_importLayoutClassedModelUsages(layout, portletDataContext);

		importedLayout = _layoutLocalService.getLayout(
			importedLayout.getPlid());

		importedLayout.setStatus(layout.getStatus());

		importedLayout = _layoutLocalService.updateLayout(importedLayout);

		if (existingLayout == null) {
			_addMasterLayoutRevision(importedLayout);
		}
		else if (!_layoutExportImportConfiguration.exportDraftLayout() &&
				 Validator.isNull(draftLayoutUuid)) {

			_discardDraftLayout(importedLayout);
		}

		privateLayout = portletDataContext.isPrivateLayout();

		try {
			if (layout.isTypeAssetDisplay()) {
				portletDataContext.setPrivateLayout(false);
			}

			portletDataContext.importClassedModel(layout, importedLayout);
		}
		finally {
			portletDataContext.setPrivateLayout(privateLayout);
		}
	}

	protected Layout fetchMissingReference(
		String uuid, long groupId, boolean privateLayout) {

		// Try to fetch the existing layout from the importing group

		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			uuid, groupId, privateLayout);

		if (layout != null) {
			return layout;
		}

		try {

			// Try to fetch the existing layout from the parent sites

			Group originalGroup = _groupLocalService.getGroup(groupId);

			Group group = originalGroup.getParentGroup();

			while (group != null) {
				layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
					uuid, group.getGroupId(), privateLayout);

				if (layout != null) {
					break;
				}

				group = group.getParentGroup();
			}

			if (layout == null) {
				List<Layout> layouts = fetchStagedModelsByUuidAndCompanyId(
					uuid, originalGroup.getCompanyId());

				if (ListUtil.isEmpty(layouts)) {
					return null;
				}

				layout = layouts.get(0);
			}

			return layout;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to fetch missing reference layout from group " +
						groupId);
			}

			return null;
		}
	}

	@Override
	protected boolean isSkipImportReferenceStagedModels() {
		return true;
	}

	@Override
	protected void validateExport(
			PortletDataContext portletDataContext, Layout layout)
		throws PortletDataException {

		if (!layout.isPending()) {
			super.validateExport(portletDataContext, layout);
		}
	}

	private void _addMasterLayoutRevision(Layout layout) throws Exception {
		if (ExportImportThreadLocal.isStagingInProcess() ||
			!LayoutStagingUtil.isBranchingLayout(layout)) {

			return;
		}

		LayoutSetBranch masterLayoutSetBranch =
			_layoutSetBranchLocalService.getMasterLayoutSetBranch(
				layout.getGroupId(), layout.isPrivateLayout());

		LayoutBranch masterLayoutBranch =
			_layoutBranchLocalService.getMasterLayoutBranch(
				masterLayoutSetBranch.getLayoutSetBranchId(), layout.getPlid(),
				ServiceContextThreadLocal.getServiceContext());

		LayoutRevision latestMasterLayoutRevision =
			_layoutRevisionLocalService.fetchLatestLayoutRevision(
				masterLayoutSetBranch.getLayoutSetBranchId(),
				masterLayoutBranch.getLayoutBranchId(), layout.getPlid());

		long parentLayoutRevisionId =
			LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID;
		long portletPreferencesPlid = layout.getPlid();

		if (latestMasterLayoutRevision != null) {
			parentLayoutRevisionId =
				latestMasterLayoutRevision.getLayoutRevisionId();
			portletPreferencesPlid =
				latestMasterLayoutRevision.getLayoutRevisionId();
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		int workflowAction = serviceContext.getWorkflowAction();

		try {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			_layoutRevisionLocalService.addLayoutRevision(
				layout.getUserId(),
				masterLayoutSetBranch.getLayoutSetBranchId(),
				masterLayoutBranch.getLayoutBranchId(), parentLayoutRevisionId,
				false, layout.getPlid(), portletPreferencesPlid,
				layout.isPrivateLayout(), layout.getName(), layout.getTitle(),
				layout.getDescription(), layout.getKeywords(),
				layout.getRobots(), layout.getTypeSettings(),
				layout.isIconImage(), layout.getIconImageId(),
				layout.getThemeId(), layout.getColorSchemeId(), layout.getCss(),
				serviceContext);
		}
		finally {
			serviceContext.setWorkflowAction(workflowAction);
		}
	}

	private void _addMergeFailFriendlyURLLayout(Layout layout)
		throws Exception {

		LayoutSet layoutSet = layout.getLayoutSet();

		layoutSet = _layoutSetLocalService.getLayoutSet(
			layoutSet.getGroupId(), layoutSet.isPrivateLayout());

		UnicodeProperties settingsUnicodeProperties =
			layoutSet.getSettingsProperties();

		String oldMergeFailFriendlyURLLayouts =
			settingsUnicodeProperties.getProperty(
				Sites.MERGE_FAIL_FRIENDLY_URL_LAYOUTS, StringPool.BLANK);

		String newMergeFailFriendlyURLLayouts = StringUtil.add(
			oldMergeFailFriendlyURLLayouts, layout.getUuid());

		if (!oldMergeFailFriendlyURLLayouts.equals(
				newMergeFailFriendlyURLLayouts)) {

			settingsUnicodeProperties.setProperty(
				Sites.MERGE_FAIL_FRIENDLY_URL_LAYOUTS,
				newMergeFailFriendlyURLLayouts);

			_layoutSetLocalService.updateLayoutSet(layoutSet);
		}
	}

	private String[] _appendPortletIds(
		String[] portletIds, String[] newPortletIds, String portletsMergeMode) {

		for (String portletId : newPortletIds) {
			if (ArrayUtil.contains(portletIds, portletId)) {
				continue;
			}

			if (portletsMergeMode.equals(
					PortletDataHandlerKeys.PORTLETS_MERGE_MODE_ADD_TO_BOTTOM)) {

				portletIds = ArrayUtil.append(portletIds, portletId);
			}
			else {
				portletIds = ArrayUtil.append(
					new String[] {portletId}, portletIds);
			}
		}

		return portletIds;
	}

	private void _deleteMissingFriendlyURLEntries(
		Layout layout, PortletDataContext portletDataContext) {

		Map<Long, Long> friendlyURLEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				FriendlyURLEntry.class);

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				layout.getGroupId(),
				_layoutFriendlyURLEntryHelper.getClassNameId(
					portletDataContext.isPrivateLayout()),
				layout.getPlid());

		for (FriendlyURLEntry friendlyURLEntry : friendlyURLEntries) {
			if (!friendlyURLEntryIds.containsValue(
					friendlyURLEntry.getFriendlyURLEntryId())) {

				_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
					friendlyURLEntry);
			}
		}
	}

	private void _deleteMissingLayoutFriendlyURLs(
		Layout layout, PortletDataContext portletDataContext) {

		Map<Long, Long> layoutFriendlyURLIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				LayoutFriendlyURL.class);

		List<LayoutFriendlyURL> layoutFriendlyURLs =
			_layoutFriendlyURLLocalService.getLayoutFriendlyURLs(
				layout.getPlid());

		for (LayoutFriendlyURL layoutFriendlyURL : layoutFriendlyURLs) {
			if (!layoutFriendlyURLIds.containsValue(
					layoutFriendlyURL.getLayoutFriendlyURLId())) {

				_layoutFriendlyURLLocalService.deleteLayoutFriendlyURL(
					layoutFriendlyURL);
			}
		}
	}

	private void _discardDraftLayout(Layout layout) throws Exception {
		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return;
		}

		boolean published = layout.isPublished();

		draftLayout = _layoutLocalService.copyLayoutContent(
			layout, draftLayout);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		serviceContext.setAttribute(
			LayoutTypeSettingsConstants.KEY_PUBLISHED, published);

		_layoutLocalService.updateStatus(
			draftLayout.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _exportClientExtensionEntryRels(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		List<ClientExtensionEntryRel> clientExtensionEntryRels =
			_clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
				_portal.getClassNameId(Layout.class), layout.getPlid());

		for (ClientExtensionEntryRel clientExtensionEntryRel :
				clientExtensionEntryRels) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, clientExtensionEntryRel,
				PortletDataContext.REFERENCE_TYPE_STRONG);
		}
	}

	private void _exportDraftLayout(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, draftLayout,
				PortletDataContext.REFERENCE_TYPE_PARENT);

			layoutElement.addAttribute(
				"draft-layout-uuid", draftLayout.getUuid());
			layoutElement.addAttribute(
				"draft-layout-id", String.valueOf(draftLayout.getLayoutId()));
			layoutElement.addAttribute(
				"draft-layout-plid", String.valueOf(draftLayout.getPlid()));
		}
	}

	private void _exportFaviconFileEntry(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		if (layout.getFaviconFileEntryId() <= 0) {
			return;
		}

		FileEntry faviconFileEntry = null;

		try {
			faviconFileEntry = _dlAppLocalService.getFileEntry(
				layout.getFaviconFileEntryId());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return;
		}

		if (Validator.isNull(
				layoutElement.attributeValue("favicon-file-entry-uuid"))) {

			layoutElement.addAttribute(
				"favicon-file-entry-uuid", faviconFileEntry.getUuid());
			layoutElement.addAttribute(
				"favicon-file-entry-group-id",
				String.valueOf(faviconFileEntry.getGroupId()));
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, layout, faviconFileEntry,
			PortletDataContext.REFERENCE_TYPE_STRONG);
	}

	private void _exportLayoutClassedModelUsages(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(layout.getPlid());

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, layoutClassedModelUsage,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportLayoutFriendlyURLs(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		List<LayoutFriendlyURL> layoutFriendlyURLs =
			_layoutFriendlyURLLocalService.getLayoutFriendlyURLs(
				layout.getPlid());

		for (LayoutFriendlyURL layoutFriendlyURL : layoutFriendlyURLs) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, layoutFriendlyURL,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);

			List<FriendlyURLEntry> friendlyURLEntries = _getFriendlyURLEntries(
				layoutFriendlyURL);

			for (FriendlyURLEntry friendlyURLEntry : friendlyURLEntries) {
				StagedModelDataHandlerUtil.exportStagedModel(
					portletDataContext, friendlyURLEntry);

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, layout, friendlyURLEntry,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
			}
		}
	}

	private void _exportLayoutIconImage(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		Image image = _imageLocalService.getImage(layout.getIconImageId());

		if ((image != null) && (image.getTextObj() != null)) {
			String iconPath = ExportImportPathUtil.getModelPath(
				layout,
				image.getImageId() + StringPool.PERIOD + image.getType());

			layoutElement.addAttribute("icon-image-path", iconPath);

			portletDataContext.addZipEntry(iconPath, image.getTextObj());
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to export icon image ", layout.getIconImageId(),
						" to layout ", layout.getLayoutId()));
			}

			layout.setIconImageId(0);
		}
	}

	private void _exportLayoutLocalizations(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		List<LayoutLocalization> layoutLocalizations =
			_layoutLocalizationLocalService.getLayoutLocalizations(
				layout.getPlid());

		for (LayoutLocalization layoutLocalization : layoutLocalizations) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, layoutLocalization,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportLayoutPageTemplateEntry(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if ((layoutPageTemplateEntry != null) &&
			(layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, layoutPageTemplateEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);

			layoutElement.addAttribute(
				"master-layout-layout-page-template-entry-uuid",
				layoutPageTemplateEntry.getUuid());
		}
	}

	private void _exportLayoutPageTemplateStructure(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			return;
		}

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, layout, layoutPageTemplateStructure,
			PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
	}

	private void _exportLayoutPortlets(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		boolean permissions = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PERMISSIONS);

		Element portletsElement = layoutElement.addElement("portlets");

		long previousScopeGroupId = portletDataContext.getScopeGroupId();

		Map<String, Object[]> portletIds = _getPortletIds(
			layout, portletDataContext);

		for (Map.Entry<String, Object[]> portletIdsEntry :
				portletIds.entrySet()) {

			String portletId = (String)portletIdsEntry.getValue()[0];
			long scopeGroupId = (Long)portletIdsEntry.getValue()[1];
			String scopeType = (String)portletIdsEntry.getValue()[2];
			String scopeLayoutUuid = (String)portletIdsEntry.getValue()[3];

			portletDataContext.setPlid(layout.getPlid());
			portletDataContext.setPortletId(portletId);
			portletDataContext.setScopeGroupId(scopeGroupId);
			portletDataContext.setScopeType(scopeType);
			portletDataContext.setScopeLayoutUuid(scopeLayoutUuid);

			Map<String, Boolean> exportPortletControlsMap =
				_exportImportHelper.getExportPortletControlsMap(
					layout.getCompanyId(), portletId, parameterMap,
					portletDataContext.getType());

			try {
				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.EVENT_PORTLET_EXPORT_STARTED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext));

				_portletExportController.exportPortlet(
					portletDataContext, layout.getPlid(), portletsElement,
					permissions,
					exportPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS),
					exportPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_DATA),
					exportPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_SETUP),
					exportPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_USER_PREFERENCES));

				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_PORTLET_EXPORT_SUCCEEDED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext));
			}
			catch (Throwable throwable) {
				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.EVENT_PORTLET_EXPORT_FAILED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext),
					throwable);

				throw throwable;
			}
		}

		portletDataContext.setScopeGroupId(previousScopeGroupId);
	}

	private void _exportLayoutSEOEntries(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		LayoutSEOSite layoutSEOSite =
			_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
				portletDataContext.getScopeGroupId());

		if (layoutSEOSite != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, layoutSEOSite,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}

		LayoutSEOEntry layoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId());

		if (layoutSEOEntry != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, layoutSEOEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportLinkedLayout(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		long linkToLayoutId = GetterUtil.getLong(
			typeSettingsUnicodeProperties.getProperty(
				"linkToLayoutId", StringPool.BLANK));

		if (linkToLayoutId > 0) {
			try {
				Layout linkedToLayout = _layoutLocalService.getLayout(
					portletDataContext.getScopeGroupId(),
					layout.isPrivateLayout(), linkToLayoutId);

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, layout, linkedToLayout,
					PortletDataContext.REFERENCE_TYPE_STRONG);

				layoutElement.addAttribute(
					"linked-to-layout-uuid", linkedToLayout.getUuid());
			}
			catch (NoSuchLayoutException noSuchLayoutException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchLayoutException);
				}
			}
		}
	}

	private void _exportLinkedURL(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String url = GetterUtil.getString(
			typeSettingsUnicodeProperties.getProperty("url", StringPool.BLANK));

		if (Validator.isNotNull(url)) {
			layoutElement.addAttribute(
				"linked-to-url",
				_defaultTextExportImportContentProcessor.
					replaceExportContentReferences(
						portletDataContext, layout, url, true, false));
		}
	}

	private void _exportMasterLayout(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		if (layout.getMasterLayoutPlid() <= 0) {
			return;
		}

		Layout masterLayout = _layoutLocalService.fetchLayout(
			layout.getMasterLayoutPlid());

		if ((masterLayout == null) ||
			Validator.isNotNull(
				layoutElement.attributeValue("master-layout-uuid"))) {

			return;
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, layout, masterLayout,
			PortletDataContext.REFERENCE_TYPE_PARENT);

		layoutElement.addAttribute(
			"master-layout-uuid", masterLayout.getUuid());
		layoutElement.addAttribute(
			"master-layout-plid", String.valueOf(masterLayout.getPlid()));
	}

	private void _exportStyleBookEntry(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		if (layout.getStyleBookEntryId() == 0) {
			return;
		}

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.fetchStyleBookEntry(
				layout.getStyleBookEntryId());

		if (styleBookEntry != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, layout, styleBookEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportTheme(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		boolean exportThemeSettings = MapUtil.getBoolean(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.THEME_REFERENCE);

		if (_log.isDebugEnabled()) {
			_log.debug("Export theme settings " + exportThemeSettings);
		}

		if (exportThemeSettings &&
			!portletDataContext.isPerformDirectBinaryImport()) {

			String css =
				_dlReferencesExportImportContentProcessor.
					replaceExportContentReferences(
						portletDataContext, layout, layout.getCss(), true,
						false);

			layout.setCss(css);

			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			String javaScript = GetterUtil.getString(
				typeSettingsUnicodeProperties.getProperty(
					"javascript", StringPool.BLANK));

			if (Validator.isNotNull(javaScript)) {
				typeSettingsUnicodeProperties.setProperty(
					"javascript",
					_dlReferencesExportImportContentProcessor.
						replaceExportContentReferences(
							portletDataContext, layout, javaScript, true,
							false));

				layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);
			}

			if (!layout.isInheritLookAndFeel()) {
				StagedTheme stagedTheme = new StagedThemeImpl(
					layout.getTheme());

				Element layoutElement = portletDataContext.getExportDataElement(
					layout);

				portletDataContext.addReferenceElement(
					layout, layoutElement, stagedTheme,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
			}
		}
	}

	private Object[] _extractFriendlyURLInfo(Layout layout) {
		if (!layout.isTypeURL()) {
			return null;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String url = GetterUtil.getString(
			typeSettingsUnicodeProperties.getProperty("url"));

		String friendlyURLPrivateGroupPath =
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING;
		String friendlyURLPrivateUserPath =
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING;
		String friendlyURLPublicPath =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING;

		if (!url.startsWith(friendlyURLPrivateGroupPath) &&
			!url.startsWith(friendlyURLPrivateUserPath) &&
			!url.startsWith(friendlyURLPublicPath)) {

			return null;
		}

		int x = url.indexOf(CharPool.SLASH, 1);

		if (x == -1) {
			return null;
		}

		int y = url.indexOf(CharPool.SLASH, x + 1);

		if (y == -1) {
			return null;
		}

		return new Object[] {url.substring(x, y), url, x, y};
	}

	private void _fixExportTypeSettings(Layout layout) throws Exception {
		Object[] friendlyURLInfo = _extractFriendlyURLInfo(layout);

		if (friendlyURLInfo == null) {
			return;
		}

		String friendlyURL = (String)friendlyURLInfo[0];

		Group group = layout.getGroup();

		String groupFriendlyURL = group.getFriendlyURL();

		if (!friendlyURL.equals(groupFriendlyURL)) {
			return;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String url = (String)friendlyURLInfo[1];

		int x = (Integer)friendlyURLInfo[2];
		int y = (Integer)friendlyURLInfo[3];

		typeSettingsUnicodeProperties.setProperty(
			"url",
			url.substring(0, x) + _SAME_GROUP_FRIENDLY_URL + url.substring(y));
	}

	private void _fixImportTypeSettings(Layout layout) throws Exception {
		Object[] friendlyURLInfo = _extractFriendlyURLInfo(layout);

		if (friendlyURLInfo == null) {
			return;
		}

		String friendlyURL = (String)friendlyURLInfo[0];

		if (!friendlyURL.equals(_SAME_GROUP_FRIENDLY_URL)) {
			return;
		}

		Group group = layout.getGroup();

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String url = (String)friendlyURLInfo[1];

		int x = (Integer)friendlyURLInfo[2];
		int y = (Integer)friendlyURLInfo[3];

		typeSettingsUnicodeProperties.setProperty(
			"url",
			url.substring(0, x) + group.getFriendlyURL() + url.substring(y));
	}

	private List<FriendlyURLEntry> _getFriendlyURLEntries(
		LayoutFriendlyURL layoutFriendlyURL) {

		return _friendlyURLEntryLocalService.getFriendlyURLEntries(
			layoutFriendlyURL.getGroupId(),
			_layoutFriendlyURLEntryHelper.getClassNameId(
				layoutFriendlyURL.isPrivateLayout()),
			layoutFriendlyURL.getPlid());
	}

	private String _getLayoutPrototypeUuid(
		long companyId, Layout layout, Element layoutElement) {

		boolean preloaded = GetterUtil.getBoolean(
			layoutElement.attributeValue("preloaded"));

		if (preloaded) {
			String layoutPrototypeName = GetterUtil.getString(
				layoutElement.attributeValue("layout-prototype-name"));

			LayoutPrototype layoutPrototype =
				_layoutPrototypeLocalService.fetchLayoutProtoype(
					companyId, layoutPrototypeName);

			if (layoutPrototype != null) {
				return layoutPrototype.getUuid();
			}
		}

		return layout.getLayoutPrototypeUuid();
	}

	private Map<String, Object[]> _getPortletIds(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		if (!LayoutStagingUtil.prepareLayoutStagingHandler(
				portletDataContext, layout) ||
			!layout.isSupportsEmbeddedPortlets()) {

			// Only portlet type layouts support page scoping

			return Collections.emptyMap();
		}

		Map<String, Object[]> portletIds = new HashMap<>();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		// The getAllPortlets method returns all effective nonsystem portlets
		// for any layout type, including embedded portlets, or in the case of
		// panel type layout, selected portlets

		for (Portlet portlet : layoutTypePortlet.getAllPortlets(false)) {
			String portletId = portlet.getPortletId();

			Settings portletInstanceSettings =
				FallbackKeysSettingsUtil.getSettings(
					new PortletInstanceSettingsLocator(layout, portletId));

			String scopeType = portletInstanceSettings.getValue(
				"lfrScopeType", null);
			String scopeLayoutUuid = portletInstanceSettings.getValue(
				"lfrScopeLayoutUuid", null);

			long scopeGroupId = portletDataContext.getScopeGroupId();

			if (Validator.isNotNull(scopeType)) {
				Group scopeGroup = null;

				if (scopeType.equals("company")) {
					scopeGroup = _groupLocalService.getCompanyGroup(
						layout.getCompanyId());
				}
				else if (scopeType.equals("layout")) {
					Layout scopeLayout =
						_layoutLocalService.fetchLayoutByUuidAndGroupId(
							scopeLayoutUuid, portletDataContext.getGroupId(),
							portletDataContext.isPrivateLayout());

					if (scopeLayout == null) {
						continue;
					}

					scopeGroup = scopeLayout.getScopeGroup();
				}
				else {
					throw new IllegalArgumentException(
						"Scope type " + scopeType + " is invalid");
				}

				if (scopeGroup != null) {
					scopeGroupId = scopeGroup.getGroupId();
				}
			}

			String key = PortletPermissionUtil.getPrimaryKey(
				layout.getPlid(), portletId);

			portletIds.put(
				key,
				new Object[] {
					portletId, scopeGroupId, scopeType, scopeLayoutUuid
				});
		}

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				layout.getPlid());

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			String portletId = portletPreferences.getPortletId();

			String key = PortletPermissionUtil.getPrimaryKey(
				layout.getPlid(), portletId);

			if (portletIds.containsKey(key)) {
				continue;
			}

			long scopeGroupId = portletDataContext.getScopeGroupId();

			Settings portletInstanceSettings =
				FallbackKeysSettingsUtil.getSettings(
					new PortletInstanceSettingsLocator(layout, portletId));

			String scopeType = portletInstanceSettings.getValue(
				"lfrScopeType", null);
			String scopeLayoutUuid = portletInstanceSettings.getValue(
				"lfrScopeLayoutUuid", null);

			if (Validator.isNotNull(scopeType)) {
				Group scopeGroup = null;

				if (scopeType.equals("company")) {
					scopeGroup = _groupLocalService.getCompanyGroup(
						layout.getCompanyId());
				}
				else if (scopeType.equals("layout")) {
					Layout scopeLayout =
						_layoutLocalService.fetchLayoutByUuidAndGroupId(
							scopeLayoutUuid, portletDataContext.getGroupId(),
							portletDataContext.isPrivateLayout());

					if (scopeLayout != null) {
						scopeGroup = scopeLayout.getScopeGroup();
					}
				}

				if (scopeGroup != null) {
					scopeGroupId = scopeGroup.getGroupId();
				}
			}

			portletIds.put(
				key,
				new Object[] {
					portletId, scopeGroupId, scopeType, scopeLayoutUuid
				});
		}

		return portletIds;
	}

	private String _getUniqueFriendlyURL(
		Layout existingLayout, String friendlyURL,
		PortletDataContext portletDataContext) {

		for (int i = 1;; i++) {
			Layout duplicateFriendlyURLLayout =
				_layoutLocalService.fetchLayoutByFriendlyURL(
					portletDataContext.getGroupId(),
					portletDataContext.isPrivateLayout(), friendlyURL);

			if ((duplicateFriendlyURLLayout == null) ||
				(duplicateFriendlyURLLayout.getPlid() ==
					existingLayout.getPlid())) {

				break;
			}

			friendlyURL = friendlyURL + i;
		}

		return friendlyURL;
	}

	private void _importAssets(
			Layout importedLayout, Layout layout,
			PortletDataContext portletDataContext)
		throws Exception {

		_layoutLocalService.updateAsset(
			portletDataContext.getUserId(layout.getUserUuid()), importedLayout,
			portletDataContext.getAssetCategoryIds(
				Layout.class, layout.getPlid()),
			portletDataContext.getAssetTagNames(
				Layout.class, layout.getPlid()));
	}

	private void _importClientExtensionEntryRels(
			Layout importedLayout, Layout layout,
			PortletDataContext portletDataContext)
		throws Exception {

		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			_portal.getClassNameId(Layout.class), importedLayout.getPlid(),
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS);
		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			_portal.getClassNameId(Layout.class), importedLayout.getPlid(),
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS);
		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			_portal.getClassNameId(Layout.class), importedLayout.getPlid(),
			ClientExtensionEntryConstants.TYPE_THEME_CSS);

		List<Element> clientExtensionEntryRelsElements =
			portletDataContext.getReferenceDataElements(
				layout, ClientExtensionEntryRel.class);

		for (Element clientExtensionEntryRelsElement :
				clientExtensionEntryRelsElements) {

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, clientExtensionEntryRelsElement);
		}
	}

	private void _importFaviconFileEntry(
		Layout layout, Element layoutElement, Layout importedLayout,
		PortletDataContext portletDataContext) {

		Map<Long, Long> fileEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				FileEntry.class);

		long faviconFileEntryId = MapUtil.getLong(
			fileEntryIds, layout.getFaviconFileEntryId(), 0);

		String faviconFileEntryUuid = layoutElement.attributeValue(
			"favicon-file-entry-uuid");

		if ((faviconFileEntryId == 0) &&
			Validator.isNotNull(faviconFileEntryUuid)) {

			long faviconFileEntryGroupId = GetterUtil.getLong(
				layoutElement.attributeValue("favicon-file-entry-group-id"));

			try {
				FileEntry faviconFileEntry =
					_dlAppLocalService.getFileEntryByUuidAndGroupId(
						faviconFileEntryUuid, faviconFileEntryGroupId);

				faviconFileEntryId = faviconFileEntry.getFileEntryId();
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		importedLayout.setFaviconFileEntryId(faviconFileEntryId);
	}

	private void _importFriendlyURLEntries(
			Layout importedLayout, Layout layout,
			PortletDataContext portletDataContext)
		throws Exception {

		List<Element> friendlyURLEntryElements =
			portletDataContext.getReferenceDataElements(
				layout, FriendlyURLEntry.class);

		Map<Long, Long> layoutNewPrimaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				_layoutFriendlyURLEntryHelper.getClassName(
					portletDataContext.isPrivateLayout()));

		layoutNewPrimaryKeys.put(
			layout.getPrimaryKey(), importedLayout.getPrimaryKey());

		for (Element friendlyURLEntryElement : friendlyURLEntryElements) {
			friendlyURLEntryElement.addAttribute(
				"resource-class-name",
				_layoutFriendlyURLEntryHelper.getClassName(
					portletDataContext.isPrivateLayout()));

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, friendlyURLEntryElement);
		}

		_deleteMissingFriendlyURLEntries(importedLayout, portletDataContext);
	}

	private void _importLayoutClassedModelUsages(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		List<Element> layoutClassedModelUsageElements =
			portletDataContext.getReferenceDataElements(
				layout, LayoutClassedModelUsage.class);

		for (Element layoutClassedModelUsageElement :
				layoutClassedModelUsageElements) {

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, layoutClassedModelUsageElement);
		}
	}

	private void _importLayoutFriendlyURLs(
			Layout importedLayout, Layout layout,
			PortletDataContext portletDataContext)
		throws Exception {

		List<Element> layoutFriendlyURLElements =
			portletDataContext.getReferenceDataElements(
				layout, LayoutFriendlyURL.class);

		for (Element layoutFriendlyURLElement : layoutFriendlyURLElements) {
			String layoutFriendlyURLPath =
				layoutFriendlyURLElement.attributeValue("path");

			LayoutFriendlyURL layoutFriendlyURL =
				(LayoutFriendlyURL)portletDataContext.getZipEntryAsObject(
					layoutFriendlyURLPath);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, layoutFriendlyURL);
		}

		_importFriendlyURLEntries(importedLayout, layout, portletDataContext);

		_deleteMissingLayoutFriendlyURLs(importedLayout, portletDataContext);
	}

	private void _importLayoutIconImage(
			Layout importedLayout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		String iconImagePath = layoutElement.attributeValue("icon-image-path");

		byte[] iconBytes = portletDataContext.getZipEntryAsByteArray(
			iconImagePath);

		if (ArrayUtil.isNotEmpty(iconBytes)) {
			if (importedLayout.getIconImageId() == 0) {
				importedLayout.setIconImageId(_counterLocalService.increment());
			}

			_imageLocalService.updateImage(
				importedLayout.getCompanyId(), importedLayout.getIconImageId(),
				iconBytes);
		}
	}

	private void _importLayoutLocalizations(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		List<Element> layoutLocalizationsElements =
			portletDataContext.getReferenceDataElements(
				layout, LayoutLocalization.class);

		for (Element layoutLocalizationsElement : layoutLocalizationsElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, layoutLocalizationsElement);
		}
	}

	private void _importLayoutPageTemplateEntry(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		String masterLayoutLayoutPageTemplateEntryUuid =
			layoutElement.attributeValue(
				"master-layout-layout-page-template-entry-uuid");

		if (Validator.isNotNull(masterLayoutLayoutPageTemplateEntryUuid)) {
			Element masterLayoutLayoutPageTemplateEntryElement =
				portletDataContext.getReferenceDataElement(
					layout, LayoutPageTemplateEntry.class, layout.getGroupId(),
					masterLayoutLayoutPageTemplateEntryUuid);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, masterLayoutLayoutPageTemplateEntryElement);
		}
	}

	private void _importLayoutPageTemplateStructure(
			PortletDataContext portletDataContext, long classNameId,
			long classPK, Element layoutPageTemplateStructureElement)
		throws Exception {

		layoutPageTemplateStructureElement.addAttribute(
			"className", String.valueOf(_portal.getClassName(classNameId)));
		layoutPageTemplateStructureElement.addAttribute(
			"classPK", String.valueOf(classPK));

		long originalPlid = portletDataContext.getPlid();

		try {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, layoutPageTemplateStructureElement);
		}
		finally {
			portletDataContext.setPlid(originalPlid);
		}

		Map<Long, Long> layoutPageTemplateStructureIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				LayoutPageTemplateStructure.class);

		String path = layoutPageTemplateStructureElement.attributeValue("path");

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			(LayoutPageTemplateStructure)portletDataContext.getZipEntryAsObject(
				path);

		long layoutPageTemplateStructureId = MapUtil.getLong(
			layoutPageTemplateStructureIds,
			layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
			layoutPageTemplateStructure.getLayoutPageTemplateStructureId());

		LayoutPageTemplateStructure existingLayoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(layoutPageTemplateStructureId);

		if (existingLayoutPageTemplateStructure == null) {
			return;
		}

		List<LayoutPageTemplateStructureRel>
			existingLayoutPageTemplateStructureRels =
				_layoutPageTemplateStructureRelLocalService.
					getLayoutPageTemplateStructureRels(
						layoutPageTemplateStructureId);

		for (LayoutPageTemplateStructureRel
				existingLayoutPageTemplateStructureRel :
					existingLayoutPageTemplateStructureRels) {

			_updateSegmentsExperiences(
				classPK, existingLayoutPageTemplateStructureRel);
		}
	}

	private void _importLayoutPageTemplateStructures(
			Layout importedLayout, Layout layout,
			PortletDataContext portletDataContext)
		throws Exception {

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			return;
		}

		_segmentsExperienceLocalService.deleteSegmentsExperiences(
			portletDataContext.getScopeGroupId(), importedLayout.getPlid());

		List<Element> layoutPageTemplateStructureElements =
			portletDataContext.getReferenceDataElements(
				layout, LayoutPageTemplateStructure.class);

		for (Element layoutPageTemplateStructureElement :
				layoutPageTemplateStructureElements) {

			_importLayoutPageTemplateStructure(
				portletDataContext,
				_portal.getClassNameId(Layout.class.getName()),
				importedLayout.getPlid(), layoutPageTemplateStructureElement);
		}
	}

	private void _importLayoutPortlets(
			Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		boolean layoutSetPrototypeLinkEnabled = MapUtil.getBoolean(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED);

		if (layoutSetPrototypeLinkEnabled &&
			Validator.isNotNull(
				portletDataContext.getLayoutSetPrototypeUuid()) &&
			_sites.isLayoutModifiedSinceLastMerge(layout)) {

			return;
		}

		_permissionImporter.clearCache();

		Element portletsElement = layoutElement.element("portlets");

		if (portletsElement == null) {

			// See LPS-75448

			return;
		}

		long originalPlid = portletDataContext.getPlid();
		String originalPortletId = portletDataContext.getPortletId();

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		boolean permissions = MapUtil.getBoolean(
			parameterMap, PortletDataHandlerKeys.PERMISSIONS);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		for (Element portletElement : portletsElement.elements()) {
			String portletId = portletElement.attributeValue("portlet-id");

			Portlet portlet = _portletLocalService.getPortletById(
				portletDataContext.getCompanyId(), portletId);

			if (!portlet.isActive() || portlet.isUndeployedPortlet()) {
				continue;
			}

			portletDataContext.setPlid(layout.getPlid());
			portletDataContext.setPortletId(portletId);

			if (BackgroundTaskThreadLocal.hasBackgroundTask()) {
				_portletDataHandlerStatusMessageSender.sendStatusMessage(
					"portlet", portletId,
					portletDataContext.getManifestSummary());
			}

			String portletPath = portletElement.attributeValue("path");

			Document portletDocument = SAXReaderUtil.read(
				portletDataContext.getZipEntryAsString(portletPath));

			portletElement = portletDocument.getRootElement();

			// The order of the import is important. You must always import the
			// portlet preferences first, then the portlet data, then the
			// portlet permissions. The import of the portlet data assumes that
			// portlet preferences already exist.

			_exportImportHelper.setPortletScope(
				portletDataContext, portletElement);

			Element portletDataElement = portletElement.element("portlet-data");

			ManifestSummary manifestSummary =
				portletDataContext.getManifestSummary();

			Collection<String> manifestSummaryKeys =
				manifestSummary.getManifestSummaryKeys();

			if (!manifestSummaryKeys.contains(
					_exportImportHelper.getExportableRootPortletId(
						portletDataContext.getCompanyId(), portletId))) {

				manifestSummary = null;
			}

			Map<String, Boolean> importPortletControlsMap =
				_exportImportHelper.getImportPortletControlsMap(
					portletDataContext.getCompanyId(), portletId, parameterMap,
					portletDataElement, manifestSummary);

			long portletPreferencesGroupId = layout.getGroupId();

			try {
				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.EVENT_PORTLET_IMPORT_STARTED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext));

				// Portlet preferences

				_portletImportController.importPortletPreferences(
					portletDataContext, portletDataContext.getCompanyId(),
					portletPreferencesGroupId, layout, portletElement, false,
					importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS),
					importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_DATA),
					importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_SETUP),
					importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_USER_PREFERENCES));

				// Portlet data

				if (importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_DATA)) {

					_portletImportController.importPortletData(
						portletDataContext, portletDataElement);
				}

				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_PORTLET_IMPORT_SUCCEEDED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext));
			}
			catch (Throwable throwable) {
				_exportImportLifecycleManager.fireExportImportLifecycleEvent(
					ExportImportLifecycleConstants.EVENT_PORTLET_IMPORT_FAILED,
					getProcessFlag(),
					portletDataContext.getExportImportProcessId(),
					_portletDataContextFactory.clonePortletDataContext(
						portletDataContext),
					throwable);

				throw throwable;
			}
			finally {
				_portletImportController.resetPortletScope(
					portletDataContext, portletPreferencesGroupId);
			}

			// Portlet permissions

			if (permissions) {
				_permissionImporter.importPortletPermissions(
					portletDataContext.getCompanyId(),
					portletDataContext.getGroupId(), serviceContext.getUserId(),
					layout, portletElement, portletId);
			}

			// Archived setups

			try {
				_portletImportController.importPortletPreferences(
					portletDataContext, portletDataContext.getCompanyId(),
					portletDataContext.getGroupId(), null, portletElement,
					false,
					importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS),
					importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_DATA),
					importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_SETUP),
					importPortletControlsMap.get(
						PortletDataHandlerKeys.PORTLET_USER_PREFERENCES));
			}
			catch (Throwable throwable) {
				throw throwable;
			}
			finally {
				_portletImportController.resetPortletScope(
					portletDataContext, portletPreferencesGroupId);
			}
		}

		portletDataContext.setPlid(originalPlid);
		portletDataContext.setPortletId(originalPortletId);
	}

	private void _importLayoutSEOEntries(
			Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		List<Element> layoutSEOSiteElements =
			portletDataContext.getReferenceDataElements(
				layout, LayoutSEOSite.class);

		for (Element layoutSEOSiteElement : layoutSEOSiteElements) {
			LayoutSEOSite layoutSEOSite =
				(LayoutSEOSite)portletDataContext.getZipEntryAsObject(
					layoutSEOSiteElement.attributeValue("path"));

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, layoutSEOSite);
		}

		List<Element> layoutSEOEntryElements =
			portletDataContext.getReferenceDataElements(
				layout, LayoutSEOEntry.class);

		for (Element layoutSEOEntryElement : layoutSEOEntryElements) {
			String layoutSEOEntryPath = layoutSEOEntryElement.attributeValue(
				"path");

			LayoutSEOEntry layoutSEOEntry =
				(LayoutSEOEntry)portletDataContext.getZipEntryAsObject(
					layoutSEOEntryPath);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, layoutSEOEntry);
		}
	}

	private void _importLinkedLayout(
			Layout importedLayout, Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		long linkToLayoutId = GetterUtil.getLong(
			typeSettingsUnicodeProperties.getProperty(
				"linkToLayoutId", StringPool.BLANK));

		String linkedToLayoutUuid = layoutElement.attributeValue(
			"linked-to-layout-uuid");

		if (Validator.isNull(linkedToLayoutUuid) || (linkToLayoutId <= 0)) {
			return;
		}

		long scopeGroupId = portletDataContext.getScopeGroupId();
		boolean privateLayout = portletDataContext.isPrivateLayout();

		Layout existingLayout = _layoutLocalService.fetchLayout(
			linkedToLayoutUuid, scopeGroupId, privateLayout);

		if (existingLayout != null) {
			typeSettingsUnicodeProperties.setProperty(
				"linkToLayoutId", String.valueOf(existingLayout.getLayoutId()));
		}

		_exportImportProcessCallbackRegistry.registerCallback(
			portletDataContext.getExportImportProcessId(),
			new ImportLinkedLayoutCallable(
				scopeGroupId, privateLayout, importedLayout.getUuid(),
				linkedToLayoutUuid));
	}

	private void _importLinkedURL(
			Layout importedLayout, Layout layout, Element layoutElement,
			PortletDataContext portletDataContext)
		throws Exception {

		String linkedToURL = layoutElement.attributeValue("linked-to-url");

		if (Validator.isNull(linkedToURL)) {
			return;
		}

		long scopeGroupId = portletDataContext.getScopeGroupId();
		boolean privateLayout = portletDataContext.isPrivateLayout();

		_exportImportProcessCallbackRegistry.registerCallback(
			portletDataContext.getExportImportProcessId(),
			new ImportLinkedURLCallable(
				scopeGroupId, privateLayout, importedLayout.getUuid(),
				_defaultTextExportImportContentProcessor.
					replaceImportContentReferences(
						portletDataContext, layout, linkedToURL)));
	}

	private void _importStyleBookEntry(
			Layout importedLayout, Layout layout,
			PortletDataContext portletDataContext)
		throws Exception {

		if (layout.getStyleBookEntryId() == 0) {
			return;
		}

		StagedModelDataHandlerUtil.importReferenceStagedModel(
			portletDataContext, layout, StyleBookEntry.class,
			layout.getStyleBookEntryId());

		Map<Long, Long> styleBooksEntryNewPrimaryKeysMap =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				StyleBookEntry.class);

		importedLayout.setStyleBookEntryId(
			MapUtil.getLong(
				styleBooksEntryNewPrimaryKeysMap, layout.getStyleBookEntryId(),
				layout.getStyleBookEntryId()));
	}

	private void _importTheme(
			Layout importedLayout, Layout layout,
			PortletDataContext portletDataContext)
		throws Exception {

		boolean importThemeSettings = MapUtil.getBoolean(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.THEME_REFERENCE);

		if (_log.isDebugEnabled()) {
			_log.debug("Import theme settings " + importThemeSettings);
		}

		if (importThemeSettings) {
			importedLayout.setThemeId(layout.getThemeId());
			importedLayout.setColorSchemeId(layout.getColorSchemeId());
			importedLayout.setCss(
				_dlReferencesExportImportContentProcessor.
					replaceImportContentReferences(
						portletDataContext, layout, layout.getCss()));

			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			String javaScript = GetterUtil.getString(
				typeSettingsUnicodeProperties.getProperty(
					"javascript", StringPool.BLANK));

			if (Validator.isNotNull(javaScript)) {
				UnicodeProperties importedLayoutTypeSettingsUnicodeProperties =
					importedLayout.getTypeSettingsProperties();

				importedLayoutTypeSettingsUnicodeProperties.setProperty(
					"javascript",
					_dlReferencesExportImportContentProcessor.
						replaceImportContentReferences(
							portletDataContext, layout, javaScript));

				importedLayout.setTypeSettingsProperties(
					importedLayoutTypeSettingsUnicodeProperties);
			}
		}
	}

	private void _initNewLayoutPermissions(
			long companyId, long groupId, long userId, Layout layout,
			Layout importedLayout, boolean privateLayout)
		throws Exception {

		boolean addGroupPermissions = true;

		Group group = importedLayout.getGroup();

		if (privateLayout && group.isUser()) {
			addGroupPermissions = false;
		}

		boolean addGuestPermissions = false;

		if (!privateLayout || layout.isTypeControlPanel() ||
			group.isLayoutSetPrototype()) {

			addGuestPermissions = true;
		}

		_resourceLocalService.addResources(
			companyId, groupId, userId, Layout.class.getName(),
			importedLayout.getPlid(), false, addGroupPermissions,
			addGuestPermissions);
	}

	private boolean _isExportParentLayout(
		long[] layoutIds, long parentLayoutId) {

		if (FeatureFlagManagerUtil.isEnabled("LPS-199086")) {
			try {
				StagingConfiguration stagingConfiguration =
					_configurationProvider.getCompanyConfiguration(
						StagingConfiguration.class,
						CompanyThreadLocal.getCompanyId());

				if (ArrayUtil.contains(layoutIds, parentLayoutId) ||
					stagingConfiguration.publishParentLayoutsByDefault()) {

					return true;
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return false;
		}

		if (ArrayUtil.contains(layoutIds, parentLayoutId) ||
			!ExportImportThreadLocal.isLayoutStagingInProcess()) {

			return true;
		}

		try {
			StagingConfiguration stagingConfiguration =
				_configurationProvider.getCompanyConfiguration(
					StagingConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return stagingConfiguration.publishParentLayoutsByDefault();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return true;
	}

	private boolean _isLayoutOutdated(Layout existingLayout, Layout layout) {
		if ((existingLayout == null) || (layout == null)) {
			return true;
		}

		Date existingLayoutModifiedDate = existingLayout.getModifiedDate();
		long lastMergeLayoutModifiedTime = GetterUtil.getLong(
			existingLayout.getTypeSettingsProperty(
				Sites.LAST_MERGE_LAYOUT_MODIFIED_TIME));
		Date layoutModifiedDate = layout.getModifiedDate();

		if ((existingLayoutModifiedDate == null) ||
			(layoutModifiedDate == null) ||
			(layoutModifiedDate.getTime() > lastMergeLayoutModifiedTime)) {

			return true;
		}

		return false;
	}

	private void _mergePortlets(
		Layout layout, String newTypeSettings, String portletsMergeMode) {

		UnicodeProperties previousTypeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		LayoutTypePortlet previousLayoutType =
			(LayoutTypePortlet)layout.getLayoutType();

		LayoutTemplate previousLayoutTemplate =
			previousLayoutType.getLayoutTemplate();

		List<String> previousColumns = previousLayoutTemplate.getColumns();

		UnicodeProperties newTypeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).load(
				newTypeSettings
			).build();

		String layoutTemplateId = newTypeSettingsUnicodeProperties.getProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID);

		previousTypeSettingsUnicodeProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, layoutTemplateId);

		String nestedColumnIds = newTypeSettingsUnicodeProperties.getProperty(
			LayoutTypePortletConstants.NESTED_COLUMN_IDS);

		if (Validator.isNotNull(nestedColumnIds)) {
			previousTypeSettingsUnicodeProperties.setProperty(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS, nestedColumnIds);

			String[] nestedColumnIdsArray = StringUtil.split(nestedColumnIds);

			for (String nestedColumnId : nestedColumnIdsArray) {
				String nestedColumnValue =
					newTypeSettingsUnicodeProperties.getProperty(
						nestedColumnId);

				previousTypeSettingsUnicodeProperties.setProperty(
					nestedColumnId, nestedColumnValue);
			}
		}

		LayoutTemplate newLayoutTemplate =
			_layoutTemplateLocalService.getLayoutTemplate(
				layoutTemplateId, false, null);

		String[] newPortletIds = new String[0];

		for (String columnId : newLayoutTemplate.getColumns()) {
			String columnValue = newTypeSettingsUnicodeProperties.getProperty(
				columnId);

			String[] portletIds = StringUtil.split(columnValue);

			if (!previousColumns.contains(columnId)) {
				newPortletIds = ArrayUtil.append(newPortletIds, portletIds);
			}
			else {
				String[] previousPortletIds = StringUtil.split(
					previousTypeSettingsUnicodeProperties.getProperty(
						columnId));

				portletIds = _appendPortletIds(
					previousPortletIds, portletIds, portletsMergeMode);

				previousTypeSettingsUnicodeProperties.setProperty(
					columnId, StringUtil.merge(portletIds));
			}
		}

		// Add portlets in nonexistent column to the first column

		String columnId = previousColumns.get(0);

		String[] portletIds = StringUtil.split(
			previousTypeSettingsUnicodeProperties.getProperty(columnId));

		_appendPortletIds(portletIds, newPortletIds, portletsMergeMode);

		previousTypeSettingsUnicodeProperties.setProperty(
			columnId, StringUtil.merge(portletIds));

		layout.setTypeSettings(
			previousTypeSettingsUnicodeProperties.toString());
	}

	private void _populateElementLayoutMetadata(
			Layout layout, Element layoutElement)
		throws Exception {

		if (layout.isTypeContent()) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

			if (layoutPageTemplateEntry == null) {
				layoutPageTemplateEntry =
					_layoutPageTemplateEntryLocalService.
						fetchLayoutPageTemplateEntryByPlid(layout.getClassPK());
			}

			if (layoutPageTemplateEntry != null) {
				if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.BASIC) {

					layoutElement.addAttribute(
						"layout-content-page-template",
						Boolean.TRUE.toString());
				}

				if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

					layoutElement.addAttribute(
						"layout-master-page-template", Boolean.TRUE.toString());
				}
			}
		}

		LayoutStagingHandler layoutStagingHandler =
			LayoutStagingUtil.getLayoutStagingHandler(layout);

		if (layoutStagingHandler != null) {
			LayoutRevision layoutRevision =
				layoutStagingHandler.getLayoutRevision();

			if (layoutRevision != null) {
				layoutElement.addAttribute(
					"layout-revision-id",
					String.valueOf(layoutRevision.getLayoutRevisionId()));
				layoutElement.addAttribute(
					"layout-branch-id",
					String.valueOf(layoutRevision.getLayoutBranchId()));

				LayoutBranch layoutBranch = layoutRevision.getLayoutBranch();

				layoutElement.addAttribute(
					"layout-branch-name",
					String.valueOf(layoutBranch.getName()));
			}
		}

		layoutElement.addAttribute("layout-uuid", layout.getUuid());
		layoutElement.addAttribute(
			"layout-id", String.valueOf(layout.getLayoutId()));
		layoutElement.addAttribute(
			"layout-parent-layout-id",
			String.valueOf(layout.getParentLayoutId()));
		layoutElement.addAttribute(
			"layout-priority", String.valueOf(layout.getPriority()));

		String layoutPrototypeUuid = layout.getLayoutPrototypeUuid();

		if (Validator.isNull(layoutPrototypeUuid)) {
			return;
		}

		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.getLayoutPrototypeByUuidAndCompanyId(
				layoutPrototypeUuid, layout.getCompanyId());

		long guestUserId = _userLocalService.getGuestUserId(
			layout.getCompanyId());

		if (guestUserId == layoutPrototype.getUserId()) {
			layoutElement.addAttribute("preloaded", "true");
		}

		layoutElement.addAttribute(
			"layout-prototype-uuid", layoutPrototypeUuid);
		layoutElement.addAttribute(
			"layout-prototype-name",
			layoutPrototype.getName(LocaleUtil.getDefault()));

		boolean layoutPrototypeGlobal = false;

		Group companyGroup = _groupLocalService.getCompanyGroup(
			layoutPrototype.getCompanyId());

		if (layoutPrototype.getGroupId() == companyGroup.getGroupId()) {
			layoutPrototypeGlobal = true;
		}

		layoutElement.addAttribute(
			"layout-prototype-global", String.valueOf(layoutPrototypeGlobal));
	}

	private void _resetLastMergeTime(Layout importedLayout, Layout layout)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			importedLayout.getTypeSettingsProperties();

		long lastMergeTime = GetterUtil.getLong(
			typeSettingsUnicodeProperties.getProperty(Sites.LAST_MERGE_TIME));

		Date existingLayoutModifiedDate = layout.getModifiedDate();

		if ((lastMergeTime != 0) && (existingLayoutModifiedDate != null) &&
			(existingLayoutModifiedDate.getTime() <= lastMergeTime)) {

			typeSettingsUnicodeProperties.remove(Sites.LAST_MERGE_TIME);
		}
	}

	private void _updateLastMergeLayoutModifiedTime(
		Element exportedLayoutElement, Layout importedLayout,
		PortletDataContext portletDataContext) {

		String layoutsImportMode = MapUtil.getString(
			portletDataContext.getParameterMap(),
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_UUID);

		if (!layoutsImportMode.equals(
				PortletDataHandlerKeys.
					LAYOUTS_IMPORT_MODE_CREATED_FROM_PROTOTYPE)) {

			return;
		}

		Layout exportedLayout = (Layout)portletDataContext.getZipEntryAsObject(
			exportedLayoutElement.attributeValue("path"));

		UnicodeProperties importedLayoutTypeSettingsUnicodeProperties =
			importedLayout.getTypeSettingsProperties();

		Date exportedLayoutModifiedDate = exportedLayout.getModifiedDate();

		importedLayoutTypeSettingsUnicodeProperties.setProperty(
			Sites.LAST_MERGE_LAYOUT_MODIFIED_TIME,
			String.valueOf(exportedLayoutModifiedDate.getTime()));

		importedLayout.setTypeSettingsProperties(
			importedLayoutTypeSettingsUnicodeProperties);
	}

	private void _updateSegmentsExperiences(
		long classPK,
		LayoutPageTemplateStructureRel existingLayoutPageTemplateStructureRel) {

		SegmentsExperience existingSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				existingLayoutPageTemplateStructureRel.
					getSegmentsExperienceId());

		if (existingSegmentsExperience == null) {
			return;
		}

		existingSegmentsExperience.setPlid(classPK);

		_segmentsExperienceLocalService.updateSegmentsExperience(
			existingSegmentsExperience);
	}

	private void _updateTypeSettings(Layout importedLayout, Layout layout)
		throws Exception {

		long groupId = layout.getGroupId();

		try {
			LayoutTypePortlet importedLayoutType =
				(LayoutTypePortlet)importedLayout.getLayoutType();

			List<String> importedPortletIds =
				importedLayoutType.getPortletIds();

			layout.setGroupId(importedLayout.getGroupId());

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			importedPortletIds.removeAll(layoutTypePortlet.getPortletIds());

			if (!importedPortletIds.isEmpty()) {
				_portletLocalService.deletePortlets(
					importedLayout.getCompanyId(),
					importedPortletIds.toArray(new String[0]),
					importedLayout.getPlid());
			}

			UnicodeProperties prototypeTypeSettingsUnicodeProperties =
				layoutTypePortlet.getTypeSettingsProperties();

			UnicodeProperties newTypeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.create(
					prototypeTypeSettingsUnicodeProperties.isSafe()
				).fastLoad(
					prototypeTypeSettingsUnicodeProperties.toString()
				).build();

			newTypeSettingsUnicodeProperties.remove(Sites.LAST_MERGE_TIME);
			newTypeSettingsUnicodeProperties.remove(Sites.MERGE_FAIL_COUNT);

			importedLayout.setTypeSettingsProperties(
				newTypeSettingsUnicodeProperties);
		}
		finally {
			layout.setGroupId(groupId);
		}
	}

	private static final String _SAME_GROUP_FRIENDLY_URL =
		"/[$SAME_GROUP_FRIENDLY_URL$]";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutStagedModelDataHandler.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.SUPPORTS,
			new Class<?>[] {PortalException.class, SystemException.class});

	@Reference
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference(target = "(model.class.name=java.lang.String)")
	private ExportImportContentProcessor<String>
		_defaultTextExportImportContentProcessor;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference(target = "(content.processor.type=DLReferences)")
	private ExportImportContentProcessor<String>
		_dlReferencesExportImportContentProcessor;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLifecycleManager _exportImportLifecycleManager;

	@Reference
	private ExportImportProcessCallbackRegistry
		_exportImportProcessCallbackRegistry;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutBranchLocalService _layoutBranchLocalService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	private volatile LayoutExportImportConfiguration
		_layoutExportImportConfiguration;

	@Reference
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Reference
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Reference
	private LayoutLocalizationLocalService _layoutLocalizationLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutLocalServiceHelper _layoutLocalServiceHelper;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Reference
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Reference
	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

	@Reference
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutTemplateLocalService _layoutTemplateLocalService;

	@Reference
	private PermissionImporter _permissionImporter;

	@Reference
	private Portal _portal;

	@Reference
	private PortletDataContextFactory _portletDataContextFactory;

	@Reference
	private PortletDataHandlerStatusMessageSender
		_portletDataHandlerStatusMessageSender;

	@Reference
	private PortletExportController _portletExportController;

	@Reference
	private PortletImportController _portletImportController;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private Sites _sites;

	@Reference
	private Staging _staging;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

	private class ImportLinkedLayoutCallable implements Callable<Void> {

		public ImportLinkedLayoutCallable(
			long groupId, boolean privateLayout, String layoutUuid,
			String linkedToLayoutUuid) {

			_groupId = groupId;
			_privateLayout = privateLayout;
			_layoutUuid = layoutUuid;
			_linkedToLayoutUuid = linkedToLayoutUuid;
		}

		@Override
		public Void call() throws Exception {
			Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				_layoutUuid, _groupId, _privateLayout);

			if (layout == null) {
				return null;
			}

			Layout linkedToLayout =
				_layoutLocalService.fetchLayoutByUuidAndGroupId(
					_linkedToLayoutUuid, _groupId, _privateLayout);

			if (linkedToLayout == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to link layout with friendly URL ",
							layout.getFriendlyURL(), " and layout ID ",
							layout.getLayoutId(),
							" to layout with layout UUID ",
							_linkedToLayoutUuid));
				}

				return null;
			}

			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.setProperty(
				"privateLayout",
				String.valueOf(linkedToLayout.isPrivateLayout()));
			typeSettingsUnicodeProperties.setProperty(
				"linkToLayoutId", String.valueOf(linkedToLayout.getLayoutId()));

			layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

			_layoutLocalService.updateLayout(layout);

			return null;
		}

		private final long _groupId;
		private final String _layoutUuid;
		private final String _linkedToLayoutUuid;
		private final boolean _privateLayout;

	}

	private class ImportLinkedURLCallable implements Callable<Void> {

		public ImportLinkedURLCallable(
			long groupId, boolean privateLayout, String layoutUuid,
			String linkedToURL) {

			_groupId = groupId;
			_privateLayout = privateLayout;
			_layoutUuid = layoutUuid;
			_linkedToURL = linkedToURL;
		}

		@Override
		public Void call() throws Exception {
			Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				_layoutUuid, _groupId, _privateLayout);

			if (layout == null) {
				return null;
			}

			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.setProperty("url", _linkedToURL);

			layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

			_layoutLocalService.updateLayout(layout);

			return null;
		}

		private final long _groupId;
		private final String _layoutUuid;
		private final String _linkedToURL;
		private final boolean _privateLayout;

	}

}
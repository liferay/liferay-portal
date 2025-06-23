/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.exception.CPAttachmentFileEntryCDNURLException;
import com.liferay.commerce.product.exception.CPAttachmentFileEntryDisplayDateException;
import com.liferay.commerce.product.exception.CPAttachmentFileEntryExpirationDateException;
import com.liferay.commerce.product.exception.DuplicateCPAttachmentFileEntryException;
import com.liferay.commerce.product.internal.util.CPDefinitionLocalServiceCircularDependencyUtil;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPAttachmentFileEntryTable;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.base.CPAttachmentFileEntryLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.RepositoryProvider;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPAttachmentFileEntry",
	service = AopService.class
)
public class CPAttachmentFileEntryLocalServiceImpl
	extends CPAttachmentFileEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPAttachmentFileEntry addCPAttachmentFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long fileEntryId,
			boolean cdnEnabled, String cdnURL, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			Map<Locale, String> titleMap, String json, double priority,
			int type, ServiceContext serviceContext)
		throws PortalException {

		// Commerce product attachment file entry

		User user = _userLocalService.getUser(userId);

		FileEntry fileEntry = null;

		if (!cdnEnabled) {
			fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			fileEntryId = _getFileEntryId(
				fileEntry, userId, groupId, _portal.getClassName(classNameId),
				classPK, serviceContext);
		}

		_validate(
			classNameId, classPK, fileEntryId, cdnEnabled, cdnURL, 0, null,
			false);

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CPAttachmentFileEntryDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CPAttachmentFileEntryExpirationDateException.class);
		}

		if ((expirationDate != null) &&
			(expirationDate.before(date) ||
			 ((displayDate != null) && expirationDate.before(displayDate)))) {

			throw new CPAttachmentFileEntryExpirationDateException(
				"Expiration date " + expirationDate + " is in the past");
		}

		long cpAttachmentFileEntryId = counterLocalService.increment();

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntryPersistence.create(cpAttachmentFileEntryId);

		if ((classNameId == _classNameLocalService.getClassNameId(
				CPDefinition.class)) &&
			CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				classPK)) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					classPK);

			classPK = newCPDefinition.getCPDefinitionId();
		}

		cpAttachmentFileEntry.setExternalReferenceCode(externalReferenceCode);
		cpAttachmentFileEntry.setGroupId(groupId);
		cpAttachmentFileEntry.setCompanyId(user.getCompanyId());
		cpAttachmentFileEntry.setUserId(user.getUserId());
		cpAttachmentFileEntry.setUserName(user.getFullName());
		cpAttachmentFileEntry.setClassNameId(classNameId);
		cpAttachmentFileEntry.setClassPK(classPK);
		cpAttachmentFileEntry.setFileEntryId(fileEntryId);
		cpAttachmentFileEntry.setCDNEnabled(cdnEnabled);
		cpAttachmentFileEntry.setCDNURL(cdnURL);
		cpAttachmentFileEntry.setDisplayDate(displayDate);
		cpAttachmentFileEntry.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			cpAttachmentFileEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			cpAttachmentFileEntry.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		cpAttachmentFileEntry.setGalleryEnabled(galleryEnabled);
		cpAttachmentFileEntry.setTitleMap(
			_getValidLocalizedMap(
				LocaleUtil.getSiteDefault(), fileEntry, titleMap));
		cpAttachmentFileEntry.setJson(json);
		cpAttachmentFileEntry.setPriority(priority);
		cpAttachmentFileEntry.setType(type);
		cpAttachmentFileEntry.setExpandoBridgeAttributes(serviceContext);

		cpAttachmentFileEntry = cpAttachmentFileEntryPersistence.update(
			cpAttachmentFileEntry);

		_reindex(classNameId, classPK);

		// Asset

		updateAsset(
			user.getUserId(), cpAttachmentFileEntry,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Workflow

		return _startWorkflowInstance(
			user.getUserId(), cpAttachmentFileEntry, serviceContext);
	}

	@Override
	public CPAttachmentFileEntry addOrUpdateCPAttachmentFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long cpAttachmentFileEntryId,
			long fileEntryId, boolean cdnEnabled, String cdnURL,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			Map<Locale, String> titleMap, String json, double priority,
			int type, ServiceContext serviceContext)
		throws PortalException {

		CPAttachmentFileEntry cpAttachmentFileEntry = null;

		if (cpAttachmentFileEntryId != 0) {
			cpAttachmentFileEntry =
				cpAttachmentFileEntryPersistence.fetchByPrimaryKey(
					cpAttachmentFileEntryId);
		}
		else if (Validator.isNotNull(externalReferenceCode)) {
			cpAttachmentFileEntry =
				cpAttachmentFileEntryLocalService.
					fetchCPAttachmentFileEntryByExternalReferenceCode(
						externalReferenceCode, serviceContext.getCompanyId());
		}

		if ((cpAttachmentFileEntry != null) &&
			(cpAttachmentFileEntry.getClassPK() != classPK)) {

			throw new DuplicateCPAttachmentFileEntryException();
		}

		if (cpAttachmentFileEntry == null) {
			cpAttachmentFileEntry =
				cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
					externalReferenceCode, userId, groupId, classNameId,
					classPK, fileEntryId, cdnEnabled, cdnURL, displayDateMonth,
					displayDateDay, displayDateYear, displayDateHour,
					displayDateMinute, expirationDateMonth, expirationDateDay,
					expirationDateYear, expirationDateHour,
					expirationDateMinute, neverExpire, galleryEnabled, titleMap,
					json, priority, type, serviceContext);
		}
		else {
			cpAttachmentFileEntry =
				cpAttachmentFileEntryLocalService.updateCPAttachmentFileEntry(
					userId, cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
					fileEntryId, cdnEnabled, cdnURL, displayDateMonth,
					displayDateDay, displayDateYear, displayDateHour,
					displayDateMinute, expirationDateMonth, expirationDateDay,
					expirationDateYear, expirationDateHour,
					expirationDateMinute, neverExpire, galleryEnabled, titleMap,
					json, priority, type, serviceContext);
		}

		return cpAttachmentFileEntry;
	}

	@Override
	public void checkCPAttachmentFileEntries() throws PortalException {
		_checkCPAttachmentFileEntriesByDisplayDate();
		_checkCPAttachmentFileEntriesByExpirationDate();
	}

	@Override
	public void checkCPAttachmentFileEntriesByDisplayDate(
			long classNameId, long classPK)
		throws PortalException {

		List<CPAttachmentFileEntry> cpAttachmentFileEntries = null;

		if (classPK > 0) {
			cpAttachmentFileEntries =
				cpAttachmentFileEntryPersistence.findByC_C_LtD_S(
					classNameId, classPK, new Date(),
					WorkflowConstants.STATUS_SCHEDULED);
		}
		else {
			cpAttachmentFileEntries =
				cpAttachmentFileEntryPersistence.findByLtD_S(
					new Date(), WorkflowConstants.STATUS_SCHEDULED);
		}

		for (CPAttachmentFileEntry cpAttachmentFileEntry :
				cpAttachmentFileEntries) {

			long userId = _portal.getValidUserId(
				cpAttachmentFileEntry.getCompanyId(),
				cpAttachmentFileEntry.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);
			serviceContext.setScopeGroupId(cpAttachmentFileEntry.getGroupId());

			cpAttachmentFileEntryLocalService.updateStatus(
				userId, cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<String, Serializable>());
		}
	}

	@Override
	public void deleteCPAttachmentFileEntries(long fileEntryId)
		throws PortalException {

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			cpAttachmentFileEntryPersistence.findByFileEntryId(fileEntryId);

		for (CPAttachmentFileEntry cpAttachmentFileEntry :
				cpAttachmentFileEntries) {

			cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntry(
				cpAttachmentFileEntry);
		}
	}

	@Override
	public void deleteCPAttachmentFileEntries(String className, long classPK)
		throws PortalException {

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			cpAttachmentFileEntryPersistence.findByC_C(
				_classNameLocalService.getClassNameId(className), classPK);

		for (CPAttachmentFileEntry cpAttachmentFileEntry :
				cpAttachmentFileEntries) {

			cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntry(
				cpAttachmentFileEntry);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPAttachmentFileEntry deleteCPAttachmentFileEntry(
			CPAttachmentFileEntry cpAttachmentFileEntry)
		throws PortalException {

		long cpDefinitionClassNameId = _classNameLocalService.getClassNameId(
			CPDefinition.class);

		if ((cpAttachmentFileEntry.getClassNameId() ==
				cpDefinitionClassNameId) &&
			CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpAttachmentFileEntry.getClassPK())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpAttachmentFileEntry.getClassPK());

			if (cpAttachmentFileEntry.isCDNEnabled()) {
				cpAttachmentFileEntry =
					cpAttachmentFileEntryPersistence.findByC_C_C_First(
						cpDefinitionClassNameId,
						newCPDefinition.getCPDefinitionId(),
						cpAttachmentFileEntry.getCDNURL(), null);
			}
			else {
				cpAttachmentFileEntry =
					cpAttachmentFileEntryPersistence.findByC_C_F_First(
						cpDefinitionClassNameId,
						newCPDefinition.getCPDefinitionId(),
						cpAttachmentFileEntry.getFileEntryId(), null);
			}
		}

		cpAttachmentFileEntryPersistence.remove(cpAttachmentFileEntry);

		FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

		if ((fileEntry != null) &&
			(fileEntry.getGroupId() == cpAttachmentFileEntry.getGroupId())) {

			List<CPAttachmentFileEntry> cpAttachmentFileEntries =
				cpAttachmentFileEntryPersistence.findByG_C_F(
					cpAttachmentFileEntry.getGroupId(), cpDefinitionClassNameId,
					cpAttachmentFileEntry.getFileEntryId());

			if (ListUtil.isEmpty(cpAttachmentFileEntries)) {
				_dlAppLocalService.deleteFileEntry(
					cpAttachmentFileEntry.getFileEntryId());
			}
		}

		_expandoRowLocalService.deleteRows(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId());

		_reindex(
			cpAttachmentFileEntry.getClassNameId(),
			cpAttachmentFileEntry.getClassPK());

		return cpAttachmentFileEntry;
	}

	@Override
	public CPAttachmentFileEntry deleteCPAttachmentFileEntry(
			long cpAttachmentFileEntryId)
		throws PortalException {

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntryPersistence.findByPrimaryKey(
				cpAttachmentFileEntryId);

		return cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntry(
			cpAttachmentFileEntry);
	}

	@Override
	public Folder getAttachmentsFolder(
			long userId, long groupId, String className, long classPK)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			groupId, CPConstants.SERVICE_NAME_PRODUCT, serviceContext);

		Folder classNameFolder = PortletFileRepositoryUtil.addPortletFolder(
			userId, repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, className,
			serviceContext);

		return PortletFileRepositoryUtil.addPortletFolder(
			userId, repository.getRepositoryId(), classNameFolder.getFolderId(),
			String.valueOf(classPK), serviceContext);
	}

	@Override
	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long cpDefinitionId, Boolean galleryEnabled,
			String serializedDDMFormValues, int type, int start, int end)
		throws Exception {

		List<CPAttachmentFileEntry> cpAttachmentFileEntries = new ArrayList<>();

		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		if (JSONUtil.isJSONArray(serializedDDMFormValues)) {
			jsonArray = _jsonFactory.createJSONArray(serializedDDMFormValues);
		}

		Indexer<CPAttachmentFileEntry> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CPAttachmentFileEntry.class);

		SearchContext searchContext = new SearchContext();

		Map<String, Serializable> attributes =
			HashMapBuilder.<String, Serializable>put(
				CPField.GALLERY_ENABLED, galleryEnabled
			).put(
				CPField.RELATED_ENTITY_CLASS_NAME_ID,
				_portal.getClassNameId(CPDefinition.class)
			).put(
				CPField.RELATED_ENTITY_CLASS_PK, cpDefinitionId
			).put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				Field.TYPE, type
			).build();

		List<String> optionsKeys = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray valueJSONArray = CPJSONUtil.getJSONArray(
				jsonObject, "value");

			String[] values = new String[valueJSONArray.length()];

			if (values.length == 0) {
				continue;
			}

			for (int j = 0; j < valueJSONArray.length(); j++) {
				values[j] = valueJSONArray.getString(j);
			}

			String key = jsonObject.getString("key");

			String fieldName = "ATTRIBUTE_" + key + "_VALUES_IDS";

			attributes.put(fieldName, values);

			optionsKeys.add(fieldName);
		}

		if (!optionsKeys.isEmpty()) {
			attributes.put("OPTIONS", ArrayUtil.toStringArray(optionsKeys));
		}

		searchContext.setAttributes(attributes);
		searchContext.setCompanyId(cpDefinition.getCompanyId());
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {cpDefinition.getGroupId()});

		Sort prioritySort = SortFactoryUtil.create(Field.PRIORITY, false);

		searchContext.setSorts(prioritySort);

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		queryConfig.addSelectedFieldNames(Field.ENTRY_CLASS_PK);

		Hits hits = indexer.search(searchContext);

		Document[] documents = hits.getDocs();

		for (Document document : documents) {
			long classPK = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			cpAttachmentFileEntries.add(getCPAttachmentFileEntry(classPK));
		}

		return cpAttachmentFileEntries;
	}

	@Override
	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, boolean galleryEnabled, int type,
			int status, int start, int end)
		throws PortalException {

		if (status == WorkflowConstants.STATUS_ANY) {
			return cpAttachmentFileEntryPersistence.findByC_C_G_T_NotST(
				classNameId, classPK, galleryEnabled, type,
				WorkflowConstants.STATUS_IN_TRASH, start, end);
		}

		return cpAttachmentFileEntryPersistence.findByC_C_G_T_ST(
			classNameId, classPK, galleryEnabled, type, status, start, end);
	}

	@Override
	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, int type, int status, int start,
			int end)
		throws PortalException {

		if (status == WorkflowConstants.STATUS_ANY) {
			return cpAttachmentFileEntryPersistence.findByC_C_T_NotST(
				classNameId, classPK, type, WorkflowConstants.STATUS_IN_TRASH,
				start, end);
		}

		return cpAttachmentFileEntryPersistence.findByC_C_T_ST(
			classNameId, classPK, type, status, start, end);
	}

	@Override
	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, int type, int status, int start,
			int end, OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws PortalException {

		if (status == WorkflowConstants.STATUS_ANY) {
			return cpAttachmentFileEntryPersistence.findByC_C_T_NotST(
				classNameId, classPK, type, WorkflowConstants.STATUS_IN_TRASH,
				start, end, orderByComparator);
		}

		return cpAttachmentFileEntryPersistence.findByC_C_T_ST(
			classNameId, classPK, type, status, start, end, orderByComparator);
	}

	@Override
	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, String keywords, int type,
			int status, int start, int end)
		throws PortalException {

		return TransformUtil.transform(
			dslQuery(
				_getGroupByStep(
					DSLQueryFactoryUtil.selectDistinct(
						CPAttachmentFileEntryTable.INSTANCE.
							CPAttachmentFileEntryId
					).from(
						CPAttachmentFileEntryTable.INSTANCE
					),
					classNameId, classPK, keywords, type, status,
					CPAttachmentFileEntryTable.INSTANCE.title
				).limit(
					start, end
				)),
			cpAttachmentFileEntryId ->
				cpAttachmentFileEntryLocalService.getCPAttachmentFileEntry(
					(Long)cpAttachmentFileEntryId));
	}

	@Override
	public int getCPAttachmentFileEntriesCount(
		long classNameId, long classPK, int type, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return cpAttachmentFileEntryPersistence.countByC_C_T_NotST(
				classNameId, classPK, type, WorkflowConstants.STATUS_IN_TRASH);
		}

		return cpAttachmentFileEntryPersistence.countByC_C_T_ST(
			classNameId, classPK, type, status);
	}

	@Override
	public int getCPAttachmentFileEntriesCount(
			long classNameId, long classPK, String keywords, int type,
			int status)
		throws PortalException {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CPAttachmentFileEntryTable.INSTANCE.CPAttachmentFileEntryId
				).from(
					CPAttachmentFileEntryTable.INSTANCE
				),
				classNameId, classPK, keywords, type, status,
				CPAttachmentFileEntryTable.INSTANCE.title));
	}

	@Override
	public void updateAsset(
			long userId, CPAttachmentFileEntry cpAttachmentFileEntry,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			cpAttachmentFileEntry.getCompanyId());

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, companyGroup.getGroupId(),
			cpAttachmentFileEntry.getCreateDate(),
			cpAttachmentFileEntry.getModifiedDate(),
			CPAttachmentFileEntry.class.getName(),
			cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
			cpAttachmentFileEntry.getUuid(), 0, assetCategoryIds, assetTagNames,
			true, true, null, null, cpAttachmentFileEntry.getCreateDate(), null,
			ContentTypes.TEXT_PLAIN, cpAttachmentFileEntry.getTitle(),
			StringPool.BLANK, null, null, null, 0, 0, priority);

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPAttachmentFileEntry updateCPAttachmentFileEntry(
			long userId, long cpAttachmentFileEntryId, long fileEntryId,
			boolean cdnEnabled, String cdnURL, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			Map<Locale, String> titleMap, String json, double priority,
			int type, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntryPersistence.findByPrimaryKey(
				cpAttachmentFileEntryId);

		long cpDefinitionClassNameId = _classNameLocalService.getClassNameId(
			CPDefinition.class);

		if ((cpAttachmentFileEntry.getClassNameId() ==
				cpDefinitionClassNameId) &&
			CPDefinitionLocalServiceCircularDependencyUtil.isVersionable(
				cpAttachmentFileEntry.getClassPK())) {

			CPDefinition newCPDefinition =
				CPDefinitionLocalServiceCircularDependencyUtil.copyCPDefinition(
					cpAttachmentFileEntry.getClassPK());

			if (cdnEnabled) {
				cpAttachmentFileEntry =
					cpAttachmentFileEntryPersistence.findByC_C_C_First(
						cpDefinitionClassNameId,
						newCPDefinition.getCPDefinitionId(),
						cpAttachmentFileEntry.getCDNURL(), null);
			}
			else {
				cpAttachmentFileEntry =
					cpAttachmentFileEntryPersistence.findByC_C_F_First(
						cpDefinitionClassNameId,
						newCPDefinition.getCPDefinitionId(),
						cpAttachmentFileEntry.getFileEntryId(), null);
			}
		}

		FileEntry fileEntry = null;

		if (!cdnEnabled) {
			fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			fileEntryId = _getFileEntryId(
				fileEntry, user.getUserId(), cpAttachmentFileEntry.getGroupId(),
				cpAttachmentFileEntry.getClassName(),
				cpAttachmentFileEntry.getClassPK(), serviceContext);
		}

		_validate(
			cpAttachmentFileEntry.getClassNameId(),
			cpAttachmentFileEntry.getClassPK(), fileEntryId, cdnEnabled, cdnURL,
			cpAttachmentFileEntry.getFileEntryId(),
			cpAttachmentFileEntry.getCDNURL(), true);

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CPAttachmentFileEntryDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CPAttachmentFileEntryExpirationDateException.class);
		}

		if ((expirationDate != null) &&
			(expirationDate.before(date) ||
			 ((displayDate != null) && expirationDate.before(displayDate)))) {

			throw new CPAttachmentFileEntryExpirationDateException(
				"Expiration date " + expirationDate + " is in the past");
		}

		cpAttachmentFileEntry.setDisplayDate(displayDate);
		cpAttachmentFileEntry.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			cpAttachmentFileEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			cpAttachmentFileEntry.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		cpAttachmentFileEntry.setFileEntryId(fileEntryId);
		cpAttachmentFileEntry.setCDNEnabled(cdnEnabled);
		cpAttachmentFileEntry.setCDNURL(cdnURL);
		cpAttachmentFileEntry.setGalleryEnabled(galleryEnabled);
		cpAttachmentFileEntry.setTitleMap(
			_getValidLocalizedMap(
				LocaleUtil.getSiteDefault(), fileEntry, titleMap));
		cpAttachmentFileEntry.setJson(json);
		cpAttachmentFileEntry.setPriority(priority);
		cpAttachmentFileEntry.setType(type);
		cpAttachmentFileEntry.setExpandoBridgeAttributes(serviceContext);

		cpAttachmentFileEntry = cpAttachmentFileEntryPersistence.update(
			cpAttachmentFileEntry);

		// Asset

		updateAsset(
			user.getUserId(), cpAttachmentFileEntry,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Workflow

		return _startWorkflowInstance(
			user.getUserId(), cpAttachmentFileEntry, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPAttachmentFileEntry updateStatus(
			long userId, long cpAttachmentFileEntryId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CPAttachmentFileEntry cpAttachmentFileEntry =
			cpAttachmentFileEntryPersistence.findByPrimaryKey(
				cpAttachmentFileEntryId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(cpAttachmentFileEntry.getDisplayDate() != null) &&
			date.before(cpAttachmentFileEntry.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		Date modifiedDate = serviceContext.getModifiedDate(date);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = cpAttachmentFileEntry.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				cpAttachmentFileEntry.setExpirationDate(null);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			cpAttachmentFileEntry.setExpirationDate(date);
		}

		cpAttachmentFileEntry.setStatus(status);
		cpAttachmentFileEntry.setStatusByUserId(user.getUserId());
		cpAttachmentFileEntry.setStatusByUserName(user.getFullName());
		cpAttachmentFileEntry.setStatusDate(modifiedDate);

		cpAttachmentFileEntry = cpAttachmentFileEntryPersistence.update(
			cpAttachmentFileEntry);

		_reindex(
			cpAttachmentFileEntry.getClassNameId(),
			cpAttachmentFileEntry.getClassPK());

		return cpAttachmentFileEntry;
	}

	private void _checkCPAttachmentFileEntriesByDisplayDate()
		throws PortalException {

		checkCPAttachmentFileEntriesByDisplayDate(0, 0);
	}

	private void _checkCPAttachmentFileEntriesByExpirationDate()
		throws PortalException {

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			cpAttachmentFileEntryFinder.findByExpirationDate(
				new Date(),
				new QueryDefinition<>(WorkflowConstants.STATUS_APPROVED));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + cpAttachmentFileEntries.size() +
					" commerce product attachment file entries");
		}

		if ((cpAttachmentFileEntries != null) &&
			!cpAttachmentFileEntries.isEmpty()) {

			for (CPAttachmentFileEntry cpAttachmentFileEntry :
					cpAttachmentFileEntries) {

				long userId = _portal.getValidUserId(
					cpAttachmentFileEntry.getCompanyId(),
					cpAttachmentFileEntry.getUserId());

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCommand(Constants.UPDATE);
				serviceContext.setScopeGroupId(
					cpAttachmentFileEntry.getGroupId());

				cpAttachmentFileEntryLocalService.updateStatus(
					userId, cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
					WorkflowConstants.STATUS_EXPIRED, serviceContext,
					new HashMap<String, Serializable>());
			}
		}
	}

	private long _getFileEntryId(
		FileEntry fileEntry, long userId, long groupId, String className,
		long classPK, ServiceContext serviceContext) {

		boolean tempFile = fileEntry.isRepositoryCapabilityProvided(
			TemporaryFileEntriesCapability.class);

		if (!tempFile) {
			return fileEntry.getFileEntryId();
		}

		try {
			com.liferay.portal.kernel.repository.Repository repository =
				_repositoryProvider.getRepository(groupId);

			Folder folder =
				cpAttachmentFileEntryLocalService.getAttachmentsFolder(
					userId, groupId, className, classPK);

			String uniqueFileName = PortletFileRepositoryUtil.getUniqueFileName(
				groupId, folder.getFolderId(), fileEntry.getFileName());

			ServiceContext newServiceContext =
				(ServiceContext)serviceContext.clone();

			newServiceContext.setAddGroupPermissions(true);
			newServiceContext.setAddGuestPermissions(true);

			FileEntry newFileEntry = _dlAppLocalService.addFileEntry(
				null, userId, repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, uniqueFileName,
				MimeTypesUtil.getContentType(uniqueFileName), uniqueFileName,
				null, null, null, fileEntry.getContentStream(),
				fileEntry.getSize(), null, null, null, newServiceContext);

			TempFileEntryUtil.deleteTempFileEntry(fileEntry.getFileEntryId());

			return newFileEntry.getFileEntryId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return 0;
	}

	private GroupByStep _getGroupByStep(
			JoinStep joinStep, long classNameId, long classPK, String keywords,
			int type, int status,
			Expression<String> keywordsPredicateExpression)
		throws PortalException {

		return joinStep.where(
			CPAttachmentFileEntryTable.INSTANCE.classNameId.eq(
				classNameId
			).and(
				CPAttachmentFileEntryTable.INSTANCE.classPK.eq(classPK)
			).and(
				CPAttachmentFileEntryTable.INSTANCE.type.eq(type)
			).and(
				() -> {
					if (status == WorkflowConstants.STATUS_ANY) {
						return CPAttachmentFileEntryTable.INSTANCE.status.neq(
							WorkflowConstants.STATUS_IN_TRASH);
					}

					return CPAttachmentFileEntryTable.INSTANCE.status.eq(
						status);
				}
			).and(
				() -> {
					if (Validator.isNull(keywords)) {
						return null;
					}

					return Predicate.withParentheses(
						_customSQL.getKeywordsPredicate(
							DSLFunctionFactoryUtil.lower(
								keywordsPredicateExpression),
							_customSQL.keywords(keywords, true)));
				}
			));
	}

	private Map<Locale, String> _getValidLocalizedMap(
		Locale defaultLocale, FileEntry fileEntry,
		Map<Locale, String> titleMap) {

		String defaultTitle = StringPool.BLANK;

		if (fileEntry != null) {
			defaultTitle = fileEntry.getFileName();
		}

		if (Validator.isNotNull(titleMap.get(defaultLocale))) {
			return titleMap;
		}

		return HashMapBuilder.create(
			titleMap
		).put(
			defaultLocale, defaultTitle
		).build();
	}

	private void _reindex(long classNameId, long classPK)
		throws PortalException {

		ClassName className = _classNameLocalService.getClassName(classNameId);

		String classNameValue = className.getValue();

		if (classNameValue.equals(CPDefinition.class.getName())) {
			Indexer<CPDefinition> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(CPDefinition.class);

			indexer.reindex(CPDefinition.class.getName(), classPK);
		}
	}

	private CPAttachmentFileEntry _startWorkflowInstance(
			long userId, CPAttachmentFileEntry cpAttachmentFileEntry,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext = new HashMap<>();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			cpAttachmentFileEntry.getCompanyId(),
			cpAttachmentFileEntry.getGroupId(), userId,
			CPAttachmentFileEntry.class.getName(),
			cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
			cpAttachmentFileEntry, serviceContext, workflowContext);
	}

	private void _validate(
			long classNameId, long classPK, long fileEntryId,
			boolean cdnEnabled, String cdnURL, long oldFileEntryId,
			String oldCDNURL, boolean old)
		throws PortalException {

		if ((fileEntryId == 0) && !cdnEnabled) {
			throw new NoSuchFileEntryException();
		}

		if (old) {
			if (!cdnEnabled) {
				if (fileEntryId == oldFileEntryId) {
					return;
				}
			}
			else if (Validator.isNull(cdnURL) && Validator.isNull(oldCDNURL)) {
				throw new CPAttachmentFileEntryCDNURLException();
			}
			else if (Objects.equals(cdnURL, oldCDNURL)) {
				return;
			}
		}

		CPAttachmentFileEntry existingCPAttachmentFileEntry = null;

		if (cdnEnabled) {
			if (Validator.isUrl(cdnURL)) {
				existingCPAttachmentFileEntry =
					cpAttachmentFileEntryPersistence.fetchByC_C_C_First(
						classNameId, classPK, cdnURL, null);
			}
			else {
				throw new CPAttachmentFileEntryCDNURLException();
			}
		}
		else {
			existingCPAttachmentFileEntry =
				cpAttachmentFileEntryPersistence.fetchByC_C_F_First(
					classNameId, classPK, fileEntryId, null);
		}

		if (existingCPAttachmentFileEntry != null) {
			throw new DuplicateCPAttachmentFileEntryException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPAttachmentFileEntryLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CPDefinitionPersistence _cpDefinitionPersistence;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryProvider _repositoryProvider;

	@Reference
	private UserLocalService _userLocalService;

}
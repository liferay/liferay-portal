/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.DuplicateFragmentEntryKeyException;
import com.liferay.fragment.exception.FragmentEntryNameException;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.exception.RequiredFragmentEntryException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.base.FragmentEntryLocalServiceBaseImpl;
import com.liferay.fragment.service.persistence.FragmentCollectionPersistence;
import com.liferay.fragment.service.persistence.FragmentEntryLinkPersistence;
import com.liferay.fragment.validator.FragmentEntryValidator;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.UniqueUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.fragment.model.FragmentEntry",
	service = AopService.class
)
public class FragmentEntryLocalServiceImpl
	extends FragmentEntryLocalServiceBaseImpl {

	@Override
	public FragmentEntry addFragmentEntry(
			String externalReferenceCode, long userId, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status, ServiceContext serviceContext)
		throws PortalException {

		// Fragment entry

		User user = _userLocalService.getUser(userId);

		long companyId = user.getCompanyId();

		if (serviceContext != null) {
			companyId = serviceContext.getCompanyId();
		}
		else {
			serviceContext = new ServiceContext();
		}

		if (Validator.isNull(fragmentEntryKey)) {
			fragmentEntryKey = generateFragmentEntryKey(groupId, name);
		}

		fragmentEntryKey = _getFragmentEntryKey(fragmentEntryKey);

		_validate(name);
		_validateFragmentEntryKey(groupId, fragmentEntryKey);

		if (WorkflowConstants.STATUS_APPROVED == status) {
			JSONObject configurationJSONObject =
				_jsonFactory.safeCreateJSONObject(configuration, true);

			_fragmentEntryValidator.validateConfiguration(
				configurationJSONObject);

			_fragmentEntryValidator.validateTypeOptions(type, typeOptions);
			_validateContent(html, configurationJSONObject);
		}

		FragmentEntry draftFragmentEntry = create();

		draftFragmentEntry.setUuid(serviceContext.getUuid());
		draftFragmentEntry.setExternalReferenceCode(externalReferenceCode);
		draftFragmentEntry.setGroupId(groupId);
		draftFragmentEntry.setCompanyId(companyId);
		draftFragmentEntry.setUserId(user.getUserId());
		draftFragmentEntry.setUserName(user.getFullName());
		draftFragmentEntry.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		draftFragmentEntry.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		draftFragmentEntry.setFragmentCollectionId(fragmentCollectionId);
		draftFragmentEntry.setFragmentEntryKey(fragmentEntryKey);
		draftFragmentEntry.setName(name);
		draftFragmentEntry.setCss(css);
		draftFragmentEntry.setHtml(html);
		draftFragmentEntry.setJs(js);
		draftFragmentEntry.setCacheable(cacheable);
		draftFragmentEntry.setConfiguration(configuration);
		draftFragmentEntry.setIcon(icon);
		draftFragmentEntry.setPreviewFileEntryId(previewFileEntryId);
		draftFragmentEntry.setMarketplace(marketplace);
		draftFragmentEntry.setReadOnly(readOnly);
		draftFragmentEntry.setType(type);
		draftFragmentEntry.setTypeOptions(typeOptions);
		draftFragmentEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		draftFragmentEntry.setStatusByUserId(userId);
		draftFragmentEntry.setStatusByUserName(user.getFullName());
		draftFragmentEntry.setStatusDate(new Date());

		FragmentEntry updatedDraftFragmentEntry = updateDraft(
			draftFragmentEntry);

		if (WorkflowConstants.STATUS_APPROVED == status) {
			return publishDraft(updatedDraftFragmentEntry);
		}

		return updatedDraftFragmentEntry;
	}

	@Override
	public FragmentEntry copyFragmentEntry(
			long userId, long groupId, long sourceFragmentEntryId,
			long fragmentCollectionId, ServiceContext serviceContext)
		throws PortalException {

		FragmentEntry sourceFragmentEntry = getFragmentEntry(
			sourceFragmentEntryId);

		FragmentEntry draftFragmentEntry = null;
		FragmentEntry publishedFragmentEntry = null;

		if (sourceFragmentEntry.isDraft()) {
			draftFragmentEntry = sourceFragmentEntry;

			if (draftFragmentEntry.getFragmentEntryId() !=
					draftFragmentEntry.getHeadId()) {

				publishedFragmentEntry = fetchFragmentEntry(
					draftFragmentEntry.getHeadId());
			}
		}
		else {
			publishedFragmentEntry = sourceFragmentEntry;

			draftFragmentEntry =
				publishedFragmentEntry.fetchDraftFragmentEntry();
		}

		String name = UniqueUtil.getCopyValue(
			copyValue -> {
				FragmentEntry existingFragmentEntry =
					fragmentEntryPersistence.fetchByG_FCI_LikeN_First(
						sourceFragmentEntry.getGroupId(),
						sourceFragmentEntry.getFragmentCollectionId(),
						copyValue, null);

				if (existingFragmentEntry == null) {
					return true;
				}

				return false;
			},
			sourceFragmentEntry.getName());

		FragmentEntry copyPublishedFragmentEntry = null;

		long originalFragmentCollectionId =
			sourceFragmentEntry.getFragmentCollectionId();

		if (publishedFragmentEntry != null) {
			copyPublishedFragmentEntry = addFragmentEntry(
				null, userId, groupId, fragmentCollectionId, null, name,
				publishedFragmentEntry.getCss(),
				publishedFragmentEntry.getHtml(),
				publishedFragmentEntry.getJs(),
				publishedFragmentEntry.isCacheable(),
				publishedFragmentEntry.getConfiguration(),
				publishedFragmentEntry.getIcon(), 0, false,
				publishedFragmentEntry.isReadOnly(),
				publishedFragmentEntry.getType(),
				publishedFragmentEntry.getTypeOptions(),
				WorkflowConstants.STATUS_APPROVED, serviceContext);

			_copyFragmentEntryPreviewFileEntry(
				userId, groupId, publishedFragmentEntry,
				copyPublishedFragmentEntry);

			_copyFragmentEntryResources(
				publishedFragmentEntry, originalFragmentCollectionId,
				fragmentCollectionId);
		}

		FragmentEntry targetDraftFragmentEntry = null;

		if ((draftFragmentEntry != null) &&
			(copyPublishedFragmentEntry == null)) {

			targetDraftFragmentEntry = addFragmentEntry(
				null, userId, groupId, fragmentCollectionId, null, name,
				draftFragmentEntry.getCss(), draftFragmentEntry.getHtml(),
				draftFragmentEntry.getJs(), draftFragmentEntry.isCacheable(),
				draftFragmentEntry.getConfiguration(),
				draftFragmentEntry.getIcon(), 0, false,
				draftFragmentEntry.isReadOnly(), draftFragmentEntry.getType(),
				draftFragmentEntry.getTypeOptions(),
				WorkflowConstants.STATUS_DRAFT, serviceContext);

			_copyFragmentEntryPreviewFileEntry(
				userId, groupId, draftFragmentEntry, targetDraftFragmentEntry);

			_copyFragmentEntryResources(
				draftFragmentEntry, originalFragmentCollectionId,
				fragmentCollectionId);
		}

		if ((draftFragmentEntry != null) &&
			(copyPublishedFragmentEntry != null)) {

			targetDraftFragmentEntry = getDraft(copyPublishedFragmentEntry);

			targetDraftFragmentEntry.setCss(draftFragmentEntry.getCss());
			targetDraftFragmentEntry.setHtml(draftFragmentEntry.getHtml());
			targetDraftFragmentEntry.setJs(draftFragmentEntry.getJs());
			targetDraftFragmentEntry.setCacheable(
				draftFragmentEntry.isCacheable());
			targetDraftFragmentEntry.setConfiguration(
				draftFragmentEntry.getConfiguration());
			targetDraftFragmentEntry.setIcon(draftFragmentEntry.getIcon());
			targetDraftFragmentEntry.setTypeOptions(
				draftFragmentEntry.getTypeOptions());

			updateDraft(targetDraftFragmentEntry);
		}

		if (copyPublishedFragmentEntry != null) {
			return copyPublishedFragmentEntry;
		}

		return targetDraftFragmentEntry;
	}

	@Override
	public FragmentEntry createFragmentEntry(long fragmentEntryId) {
		FragmentEntry draftFragmentEntry = fragmentEntryPersistence.create(
			fragmentEntryId);

		draftFragmentEntry.setHeadId(fragmentEntryId);

		return draftFragmentEntry;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public FragmentEntry deleteFragmentEntry(FragmentEntry fragmentEntry)
		throws PortalException {

		long fragmentEntryLinkCount = _fragmentEntryLinkPersistence.countByF_D(
			fragmentEntry.getFragmentEntryId(), false);

		if (fragmentEntryLinkCount > 0) {
			throw new RequiredFragmentEntryException();
		}

		_resourceLocalService.deleteResource(
			fragmentEntry.getCompanyId(), FragmentEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			fragmentEntry.getFragmentEntryId());

		_fragmentEntryLinkLocalService.
			deleteFragmentEntryLinksByFragmentEntryId(
				fragmentEntry.getFragmentEntryId(), true);

		if (fragmentEntry.getPreviewFileEntryId() > 0) {
			boolean deletePreviewFileEntry = true;

			if (fragmentEntry.isDraft() &&
				(fragmentEntry.getHeadId() !=
					fragmentEntry.getFragmentEntryId())) {

				FragmentEntry publishedFragmentEntry = fetchFragmentEntry(
					fragmentEntry.getHeadId());

				if ((publishedFragmentEntry != null) &&
					(fragmentEntry.getPreviewFileEntryId() ==
						publishedFragmentEntry.getPreviewFileEntryId())) {

					deletePreviewFileEntry = false;
				}
			}

			if (deletePreviewFileEntry) {
				PortletFileRepositoryUtil.deletePortletFileEntry(
					fragmentEntry.getPreviewFileEntryId());
			}
		}

		if (fragmentEntry.isDraft()) {
			return deleteDraft(fragmentEntry);
		}

		return delete(fragmentEntry);
	}

	@Override
	public FragmentEntry deleteFragmentEntry(long fragmentEntryId)
		throws PortalException {

		return fragmentEntryLocalService.deleteFragmentEntry(
			getFragmentEntry(fragmentEntryId));
	}

	@Override
	public FragmentEntry deleteFragmentEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		FragmentEntry fragmentEntry = fragmentEntryPersistence.findByERC_G_Head(
			externalReferenceCode, groupId, true);

		return fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);
	}

	@Override
	public FragmentEntry fetchFragmentEntry(
		long groupId, String fragmentEntryKey) {

		FragmentEntry fragmentEntry =
			fragmentEntryPersistence.fetchByG_FEK_First(
				groupId, _getFragmentEntryKey(fragmentEntryKey), null);

		if (fragmentEntry == null) {
			return null;
		}

		if (!fragmentEntry.isDraft()) {
			return fragmentEntry;
		}

		return fetchFragmentEntryByUuidAndGroupId(
			fragmentEntry.getUuid(), groupId);
	}

	@Override
	public FragmentEntry fetchFragmentEntryByUuidAndGroupId(
		String uuid, long groupId) {

		FragmentEntry fragmentEntry =
			fragmentEntryPersistence.fetchByUUID_G_Head(uuid, groupId, true);

		if (fragmentEntry != null) {
			return fragmentEntry;
		}

		return fragmentEntryPersistence.fetchByUUID_G_Head(
			uuid, groupId, false);
	}

	@Override
	public String generateFragmentEntryKey(long groupId, String name) {
		String fragmentEntryKey = _getFragmentEntryKey(name);

		fragmentEntryKey = StringUtil.replace(
			fragmentEntryKey, CharPool.SPACE, CharPool.DASH);

		String curFragmentEntryKey = fragmentEntryKey;

		int count = 0;

		while (true) {
			FragmentEntry fragmentEntry = fetchFragmentEntry(
				groupId, curFragmentEntryKey);

			if (fragmentEntry == null) {
				return curFragmentEntryKey;
			}

			curFragmentEntryKey = fragmentEntryKey + CharPool.DASH + count++;
		}
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(long fragmentCollectionId) {
		return fragmentEntryPersistence.findByFragmentCollectionId(
			fragmentCollectionId);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(
		long fragmentCollectionId, int start, int end) {

		return fragmentEntryPersistence.findByFragmentCollectionId(
			fragmentCollectionId, start, end);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int status) {

		return fragmentEntryPersistence.findByG_FCI_S(
			groupId, fragmentCollectionId, status);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return fragmentEntryPersistence.findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return fragmentEntryPersistence.findByG_FCI(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		if (Validator.isNull(name)) {
			return fragmentEntryPersistence.findByG_FCI_S(
				groupId, fragmentCollectionId, status, start, end,
				orderByComparator);
		}

		return fragmentEntryPersistence.findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], status,
			start, end, orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentEntry> orderByComparator) {

		if (Validator.isNull(name)) {
			return fragmentEntryPersistence.findByG_FCI(
				groupId, fragmentCollectionId, start, end, orderByComparator);
		}

		return fragmentEntryPersistence.findByG_FCI_LikeN(
			groupId, fragmentCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return fragmentEntryPersistence.findByUuid_C(uuid, companyId);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return fragmentEntryPersistence.findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	@Override
	public int getFragmentEntriesCount(long fragmentCollectionId) {
		return fragmentEntryPersistence.countByFragmentCollectionId(
			fragmentCollectionId);
	}

	@Override
	public FragmentEntry getFragmentEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		FragmentEntry fragmentEntry = fetchFragmentEntryByUuidAndGroupId(
			uuid, groupId);

		if (fragmentEntry == null) {
			throw new NoSuchEntryException();
		}

		return fragmentEntry;
	}

	@Override
	public String[] getTempFileNames(
			long userId, long groupId, String folderName)
		throws PortalException {

		return TempFileEntryUtil.getTempFileNames(groupId, userId, folderName);
	}

	@Override
	public String getUniqueFragmentEntryName(
		long groupId, long fragmentCollectionId, String name) {

		FragmentEntry fragmentEntry =
			fragmentEntryPersistence.fetchByG_FCI_LikeN_First(
				groupId, fragmentCollectionId, name, null);

		if (fragmentEntry == null) {
			return name;
		}

		int count = 1;

		while (true) {
			String newName = StringUtil.appendParentheticalSuffix(
				name, count++);

			fragmentEntry = fragmentEntryPersistence.fetchByG_FCI_LikeN_First(
				groupId, fragmentCollectionId, newName, null);

			if (fragmentEntry == null) {
				return newName;
			}
		}
	}

	@Override
	public FragmentEntry moveFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId)
		throws PortalException {

		FragmentEntry fragmentEntry = fragmentEntryPersistence.findByPrimaryKey(
			fragmentEntryId);

		if (fragmentEntry.getFragmentCollectionId() == fragmentCollectionId) {
			return fragmentEntry;
		}

		long originalFragmentCollectionId =
			fragmentEntry.getFragmentCollectionId();

		fragmentEntry.setFragmentCollectionId(fragmentCollectionId);

		_copyFragmentEntryResources(
			fragmentEntry, originalFragmentCollectionId, fragmentCollectionId);

		return fragmentEntryPersistence.update(fragmentEntry);
	}

	@Override
	public FragmentEntry publishDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		FragmentEntry publishedFragmentEntry = fetchFragmentEntry(
			draftFragmentEntry.getHeadId());

		if (publishedFragmentEntry != null) {
			draftFragmentEntry.setCacheable(
				publishedFragmentEntry.isCacheable());
			draftFragmentEntry.setPreviewFileEntryId(
				publishedFragmentEntry.getPreviewFileEntryId());
		}

		return _publishDraft(draftFragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(FragmentEntry fragmentEntry)
		throws PortalException {

		FragmentEntry draftFragmentEntry = null;

		if (fragmentEntry.isDraft()) {
			draftFragmentEntry = fragmentEntry;
		}
		else {
			draftFragmentEntry = fragmentEntryPersistence.fetchByHeadId(
				fragmentEntry.getFragmentEntryId());

			if (draftFragmentEntry == null) {
				draftFragmentEntry = getDraft(fragmentEntry);
			}
		}

		FragmentEntry updatedDraftFragmentEntry =
			fragmentEntryPersistence.update(draftFragmentEntry);

		if (fragmentEntry.isDraft()) {
			return updatedDraftFragmentEntry;
		}

		return publishDraft(updatedDraftFragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, boolean cacheable)
		throws PortalException {

		FragmentEntry fragmentEntry = fragmentEntryPersistence.findByPrimaryKey(
			fragmentEntryId);

		fragmentEntry.setCacheable(cacheable);

		return fragmentEntryPersistence.update(fragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long previewFileEntryId)
		throws PortalException {

		FragmentEntry fragmentEntry = fragmentEntryPersistence.findByPrimaryKey(
			fragmentEntryId);

		fragmentEntry.setPreviewFileEntryId(previewFileEntryId);

		return fragmentEntryPersistence.update(fragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long userId, long fragmentEntryId, long fragmentCollectionId,
			String name, String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean readOnly, String typeOptions, int status)
		throws PortalException {

		FragmentEntry fragmentEntry = fragmentEntryPersistence.findByPrimaryKey(
			fragmentEntryId);

		_validate(name);

		if (WorkflowConstants.STATUS_APPROVED == status) {
			JSONObject configurationJSONObject =
				_jsonFactory.safeCreateJSONObject(configuration, true);

			_fragmentEntryValidator.validateConfiguration(
				configurationJSONObject);

			_fragmentEntryValidator.validateTypeOptions(
				fragmentEntry.getType(), typeOptions);

			_validateContent(html, configurationJSONObject);
		}

		User user = _userLocalService.getUser(userId);

		fragmentEntry.setModifiedDate(new Date());
		fragmentEntry.setFragmentCollectionId(fragmentCollectionId);
		fragmentEntry.setName(name);
		fragmentEntry.setCss(css);
		fragmentEntry.setHtml(html);
		fragmentEntry.setJs(js);
		fragmentEntry.setCacheable(cacheable);
		fragmentEntry.setConfiguration(configuration);
		fragmentEntry.setIcon(icon);
		fragmentEntry.setPreviewFileEntryId(previewFileEntryId);
		fragmentEntry.setReadOnly(readOnly);
		fragmentEntry.setTypeOptions(typeOptions);
		fragmentEntry.setStatus(status);
		fragmentEntry.setStatusByUserId(userId);
		fragmentEntry.setStatusByUserName(user.getFullName());
		fragmentEntry.setStatusDate(new Date());

		fragmentEntry = getDraft(fragmentEntry);

		if (!Objects.equals(name, fragmentEntry.getName())) {
			fragmentEntry.setName(name);
		}

		if (status != WorkflowConstants.STATUS_APPROVED) {
			return fragmentEntry;
		}

		return _publishDraft(fragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(long fragmentEntryId, String name)
		throws PortalException {

		FragmentEntry fragmentEntry = fragmentEntryPersistence.findByPrimaryKey(
			fragmentEntryId);

		if (Objects.equals(fragmentEntry.getName(), name)) {
			return fragmentEntry;
		}

		_validate(name);

		fragmentEntry.setName(name);

		return fragmentEntryPersistence.update(fragmentEntry);
	}

	private void _addFragmentCollectionResourcesWithFolders(
			FragmentEntry fragmentEntry, ServiceContext serviceContext,
			Map<String, FileEntry> fileEntries,
			FragmentCollection targetFragmentCollection)
		throws Exception {

		Repository repository = _getRepository(
			targetFragmentCollection.getGroupId());

		Map<String, Long> folderIdMap = HashMapBuilder.put(
			StringPool.BLANK, targetFragmentCollection.getResourcesFolderId()
		).build();

		for (Map.Entry<String, FileEntry> entry : fileEntries.entrySet()) {
			String fileName = entry.getKey();
			String folderPath = StringPool.BLANK;

			int index = fileName.lastIndexOf(StringPool.SLASH);

			if (index != -1) {
				folderPath = fileName.substring(0, index);
				fileName = fileName.substring(index + 1);
			}

			long folderId = _getOrCreateFolderId(
				folderIdMap, folderPath, repository.getRepositoryId(),
				fragmentEntry.getUserId());

			FileEntry existingFileEntry =
				PortletFileRepositoryUtil.fetchPortletFileEntry(
					fragmentEntry.getGroupId(), folderId, fileName);

			if (existingFileEntry == null) {
				FileEntry fileEntry = entry.getValue();

				PortletFileRepositoryUtil.addPortletFileEntry(
					null, serviceContext.getScopeGroupId(),
					serviceContext.getUserId(),
					FragmentCollection.class.getName(),
					targetFragmentCollection.getFragmentCollectionId(),
					FragmentPortletKeys.FRAGMENT, folderId,
					fileEntry.getContentStream(), fileName,
					fileEntry.getMimeType(), false);
			}
		}
	}

	private void _copyFragmentEntryPreviewFileEntry(
			long userId, long groupId, FragmentEntry sourceFragmentEntry,
			FragmentEntry targetFragmentEntry)
		throws PortalException {

		if (sourceFragmentEntry.getPreviewFileEntryId() != 0) {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(
				sourceFragmentEntry.getPreviewFileEntryId());

			Repository repository =
				PortletFileRepositoryUtil.fetchPortletRepository(
					groupId, FragmentPortletKeys.FRAGMENT);

			if (repository == null) {
				ServiceContext addPortletRepositoryServiceContext =
					new ServiceContext();

				addPortletRepositoryServiceContext.setAddGroupPermissions(true);
				addPortletRepositoryServiceContext.setAddGuestPermissions(true);

				repository = PortletFileRepositoryUtil.addPortletRepository(
					groupId, FragmentPortletKeys.FRAGMENT,
					addPortletRepositoryServiceContext);
			}

			String fileName =
				targetFragmentEntry.getFragmentEntryId() + "_preview." +
					fileEntry.getExtension();

			fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
				null, groupId, userId, FragmentEntry.class.getName(),
				targetFragmentEntry.getFragmentEntryId(),
				FragmentPortletKeys.FRAGMENT, repository.getDlFolderId(),
				fileEntry.getContentStream(), fileName, fileEntry.getMimeType(),
				false);

			updateFragmentEntry(
				targetFragmentEntry.getFragmentEntryId(),
				fileEntry.getFileEntryId());
		}
	}

	private void _copyFragmentEntryResources(
		FragmentEntry fragmentEntry, long sourceFragmentCollectionId,
		long targetFragmentCollectionId) {

		if (sourceFragmentCollectionId == targetFragmentCollectionId) {
			return;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Map<String, FileEntry> fileEntries = _getFileEntries(
			sourceFragmentCollectionId, fragmentEntry);

		if ((serviceContext == null) || fileEntries.isEmpty()) {
			return;
		}

		FragmentCollection targetFragmentCollection =
			_fragmentCollectionPersistence.fetchByPrimaryKey(
				targetFragmentCollectionId);

		try {
			_addFragmentCollectionResourcesWithFolders(
				fragmentEntry, serviceContext, fileEntries,
				targetFragmentCollection);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private Map<String, FileEntry> _getFileEntries(
		long fragmentCollectionId, FragmentEntry fragmentEntry) {

		Map<String, FileEntry> fileEntries = new HashMap<>();

		FragmentCollection fragmentCollection =
			_fragmentCollectionPersistence.fetchByPrimaryKey(
				fragmentCollectionId);

		Matcher matcher = _pattern.matcher(fragmentEntry.getHtml());

		while (matcher.find()) {
			FileEntry fileEntry = fragmentCollection.getResource(
				matcher.group(1));

			if (fileEntry != null) {
				fileEntries.put(matcher.group(1), fileEntry);
			}
		}

		return fileEntries;
	}

	private String _getFragmentEntryKey(String fragmentEntryKey) {
		if (fragmentEntryKey != null) {
			fragmentEntryKey = fragmentEntryKey.trim();

			return StringUtil.toLowerCase(fragmentEntryKey);
		}

		return StringPool.BLANK;
	}

	private long _getOrCreateFolderId(
			Map<String, Long> folderIdMap, String folderPath, long repositoryId,
			long userId)
		throws Exception {

		if (folderIdMap.containsKey(folderPath)) {
			return folderIdMap.get(folderPath);
		}

		String folderName = folderPath;

		String parentFolderPath = StringPool.BLANK;

		int index = folderName.lastIndexOf(StringPool.SLASH);

		if (index != -1) {
			folderName = folderName.substring(index + 1);

			parentFolderPath = folderPath.substring(0, index);
		}

		Folder folder = PortletFileRepositoryUtil.addPortletFolder(
			userId, repositoryId,
			_getOrCreateFolderId(
				folderIdMap, parentFolderPath, repositoryId, userId),
			folderName, ServiceContextThreadLocal.getServiceContext());

		folderIdMap.put(folderPath, folder.getFolderId());

		return folder.getFolderId();
	}

	private Repository _getRepository(long groupId) throws Exception {
		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				groupId, FragmentPortletKeys.FRAGMENT);

		if (repository == null) {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			repository = PortletFileRepositoryUtil.addPortletRepository(
				groupId, FragmentPortletKeys.FRAGMENT, serviceContext);
		}

		return repository;
	}

	private void _propagateChanges(long fragmentEntryId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_fragmentEntryLinkLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property fragmentEntryIdProperty = PropertyFactoryUtil.forName(
					"fragmentEntryId");

				dynamicQuery.add(fragmentEntryIdProperty.eq(fragmentEntryId));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(FragmentEntryLink fragmentEntryLink) ->
				_fragmentEntryLinkLocalService.updateLatestChanges(
					fragmentEntryLink.getFragmentEntryLinkId()));

		actionableDynamicQuery.performActions();
	}

	private FragmentEntry _publishDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		FragmentEntry publishedFragmentEntry = fetchFragmentEntry(
			draftFragmentEntry.getHeadId());

		if ((publishedFragmentEntry == null) ||
			!Objects.equals(
				publishedFragmentEntry.getName(),
				draftFragmentEntry.getName())) {

			_validate(draftFragmentEntry.getName());
		}

		JSONObject configurationJSONObject = _jsonFactory.safeCreateJSONObject(
			draftFragmentEntry.getConfiguration(), true);

		_fragmentEntryValidator.validateConfiguration(configurationJSONObject);

		_fragmentEntryValidator.validateTypeOptions(
			draftFragmentEntry.getType(), draftFragmentEntry.getTypeOptions());
		_validateContent(draftFragmentEntry.getHtml(), configurationJSONObject);

		draftFragmentEntry.setStatus(WorkflowConstants.STATUS_APPROVED);

		FragmentEntry updatedPublishedFragmentEntry = super.publishDraft(
			draftFragmentEntry);

		FragmentServiceConfiguration fragmentServiceConfiguration =
			_configurationProvider.getCompanyConfiguration(
				FragmentServiceConfiguration.class,
				draftFragmentEntry.getCompanyId());

		if (fragmentServiceConfiguration.propagateChanges() &&
			!ExportImportThreadLocal.isLayoutImportInProcess() &&
			!ExportImportThreadLocal.isStagingInProcess()) {

			_propagateChanges(
				updatedPublishedFragmentEntry.getFragmentEntryId());
		}

		return updatedPublishedFragmentEntry;
	}

	private void _validate(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new FragmentEntryNameException("Name must not be null");
		}

		if (name.contains(StringPool.PERIOD) ||
			name.contains(StringPool.SLASH)) {

			throw new FragmentEntryNameException(
				"Name contains invalid characters");
		}

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			FragmentEntry.class.getName(), "name");

		if (name.length() > nameMaxLength) {
			throw new FragmentEntryNameException(
				"Maximum length of name exceeded");
		}
	}

	private void _validateContent(
			String html, JSONObject configurationJSONObject)
		throws PortalException {

		_fragmentEntryProcessorRegistry.validateFragmentEntryHTML(
			html, configurationJSONObject);
	}

	private void _validateFragmentEntryKey(
			long groupId, String fragmentEntryKey)
		throws PortalException {

		fragmentEntryKey = _getFragmentEntryKey(fragmentEntryKey);

		FragmentEntry fragmentEntry = fetchFragmentEntry(
			groupId, fragmentEntryKey);

		if (fragmentEntry != null) {
			throw new DuplicateFragmentEntryKeyException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryLocalServiceImpl.class);

	private static final Pattern _pattern = Pattern.compile(
		"\\[resources:(.+?)\\]");

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private FragmentCollectionPersistence _fragmentCollectionPersistence;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLinkPersistence _fragmentEntryLinkPersistence;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private FragmentEntryValidator _fragmentEntryValidator;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.service.impl;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.translation.exception.XLIFFFileException;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.importer.TranslationInfoItemFieldValuesImporter;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.translation.model.TranslationEntryTable;
import com.liferay.translation.service.base.TranslationEntryLocalServiceBaseImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "model.class.name=com.liferay.translation.model.TranslationEntry",
	service = AopService.class
)
public class TranslationEntryLocalServiceImpl
	extends TranslationEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public TranslationEntry addOrUpdateTranslationEntry(
			long groupId, String className, long classPK, String content,
			String contentType, String languageId,
			ServiceContext serviceContext)
		throws PortalException {

		_validateInfoItemGroup(groupId, className, classPK);

		TranslationEntry translationEntry =
			translationEntryPersistence.fetchByC_C_L(
				_portal.getClassNameId(className), classPK, languageId);

		if (translationEntry == null) {
			translationEntry = translationEntryPersistence.create(
				counterLocalService.increment());

			translationEntry.setUuid(serviceContext.getUuid());
			translationEntry.setGroupId(groupId);
			translationEntry.setCompanyId(serviceContext.getCompanyId());
			translationEntry.setUserId(serviceContext.getUserId());
			translationEntry.setClassName(className);
			translationEntry.setClassPK(classPK);
			translationEntry.setLanguageId(languageId);
			translationEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}

		translationEntry.setContent(content);
		translationEntry.setContentType(contentType);

		if (!translationEntry.isDraft() && !translationEntry.isPending()) {
			translationEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}

		User user = _userLocalService.getUser(serviceContext.getUserId());

		translationEntry.setStatusByUserId(user.getUserId());
		translationEntry.setStatusByUserName(user.getFullName());

		translationEntry.setStatusDate(
			serviceContext.getModifiedDate(new Date()));

		translationEntry = translationEntryPersistence.update(translationEntry);

		_assetEntryLocalService.updateEntry(
			translationEntry.getUserId(), translationEntry.getGroupId(),
			translationEntry.getCreateDate(),
			translationEntry.getModifiedDate(),
			TranslationEntry.class.getName(),
			translationEntry.getTranslationEntryId(),
			translationEntry.getUuid(), 0, null, null, false, false, null, null,
			null, null, null, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, null, null, 0, 0, 0D);

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			translationEntry.getCompanyId(), translationEntry.getGroupId(),
			serviceContext.getUserId(), TranslationEntry.class.getName(),
			translationEntry.getTranslationEntryId(), translationEntry,
			serviceContext, new HashMap<>());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public TranslationEntry addOrUpdateTranslationEntry(
			long groupId, String sourceLanguageId, String targetLanguageId,
			InfoItemReference infoItemReference,
			InfoItemFieldValues infoItemFieldValues,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			infoItemReference = infoItemFieldValues.getInfoItemReference();

			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
				throw new NoSuchInfoItemException(
					"Unable to add or update a translation entry without a " +
						"class PK info item identifier");
			}

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			return addOrUpdateTranslationEntry(
				groupId, infoItemReference.getClassName(),
				classPKInfoItemIdentifier.getClassPK(),
				StreamUtil.toString(
					_xliffTranslationInfoItemFieldValuesExporter.
						exportInfoItemFieldValues(
							infoItemFieldValues,
							LocaleUtil.fromLanguageId(sourceLanguageId),
							LocaleUtil.fromLanguageId(targetLanguageId))),
				_xliffTranslationInfoItemFieldValuesExporter.getMimeType(),
				targetLanguageId, serviceContext);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	@Override
	public void deleteTranslationEntries(long classNameId, long classPK)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			translationEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq("classNameId", classNameId));
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq("classPK", classPK));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(TranslationEntry translationEntry) -> {
				translationEntryLocalService.deleteTranslationEntry(
					translationEntry);

				_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
					translationEntry.getCompanyId(),
					translationEntry.getGroupId(),
					TranslationEntry.class.getName(),
					translationEntry.getTranslationEntryId());
			});

		actionableDynamicQuery.performActions();
	}

	@Override
	public void deleteTranslationEntries(String className, long classPK)
		throws PortalException {

		translationEntryLocalService.deleteTranslationEntries(
			_portal.getClassNameId(className), classPK);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public TranslationEntry deleteTranslationEntry(long translationEntryId)
		throws PortalException {

		TranslationEntry translationEntry = translationEntryPersistence.remove(
			translationEntryId);

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
			translationEntry.getCompanyId(), translationEntry.getGroupId(),
			TranslationEntry.class.getName(),
			translationEntry.getTranslationEntryId());

		return translationEntry;
	}

	@Override
	public TranslationEntry fetchTranslationEntry(
		String className, long classPK, String languageId) {

		return translationEntryPersistence.fetchByC_C_L(
			_portal.getClassNameId(className), classPK, languageId);
	}

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(
			long groupId, String className, long classPK, String content)
		throws PortalException {

		try {
			return _xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					groupId,
					new InfoItemReference(
						StringUtil.replace(
							className, CharPool.UNDERLINE, CharPool.POUND),
						classPK),
					new ByteArrayInputStream(content.getBytes()));
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	@Override
	public int getTranslationEntriesCount(
		String className, long classPK, int[] statuses, boolean exclude) {

		return translationEntryPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				TranslationEntryTable.INSTANCE
			).where(
				TranslationEntryTable.INSTANCE.classNameId.eq(
					_portal.getClassNameId(className)
				).and(
					TranslationEntryTable.INSTANCE.classPK.eq(classPK)
				).and(
					() -> {
						if (exclude) {
							return TranslationEntryTable.INSTANCE.status.notIn(
								ArrayUtil.toArray(statuses));
						}

						return TranslationEntryTable.INSTANCE.status.in(
							ArrayUtil.toArray(statuses));
					}
				)
			));
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public TranslationEntry updateStatus(
			long userId, long translationEntryId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		TranslationEntry translationEntry =
			translationEntryPersistence.findByPrimaryKey(translationEntryId);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			_updateInfoItem(translationEntry);
		}

		translationEntry.setStatus(status);

		User user = _userLocalService.getUser(userId);

		translationEntry.setStatusByUserId(user.getUserId());
		translationEntry.setStatusByUserName(user.getFullName());

		translationEntry.setStatusDate(
			serviceContext.getModifiedDate(new Date()));

		return translationEntryPersistence.update(translationEntry);
	}

	private void _updateInfoItem(TranslationEntry translationEntry)
		throws PortalException {

		try {
			InfoItemFieldValuesUpdater<Object> infoItemFieldValuesUpdater =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesUpdater.class,
					translationEntry.getClassName());

			InfoItemObjectProvider<Object> infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					translationEntry.getClassName(),
					ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

			String content = translationEntry.getContent();

			infoItemFieldValuesUpdater.updateFromInfoItemFieldValues(
				infoItemObjectProvider.getInfoItem(
					new ClassPKInfoItemIdentifier(
						translationEntry.getClassPK())),
				_xliffTranslationInfoItemFieldValuesImporter.
					importInfoItemFieldValues(
						translationEntry.getGroupId(),
						new InfoItemReference(
							translationEntry.getClassName(),
							translationEntry.getClassPK()),
						new ByteArrayInputStream(
							content.getBytes(StandardCharsets.UTF_8))));
		}
		catch (PortalException | RuntimeException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private void _validateInfoItemGroup(
			long groupId, String className, long classPK)
		throws XLIFFFileException {

		try {
			InfoItemObjectProvider<Object> infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, className);

			Object object = infoItemObjectProvider.getInfoItem(
				new ClassPKInfoItemIdentifier(classPK));

			if (object == null) {
				throw new XLIFFFileException.MustHaveValidModel(
					"Unable to get info item for class PK " + classPK);
			}

			if (object instanceof GroupedModel) {
				GroupedModel groupedModel = (GroupedModel)object;

				if (groupedModel.getGroupId() != groupId) {
					throw new XLIFFFileException.MustHaveValidModel(
						StringBundler.concat(
							"Can not import translation for a model in group ",
							groupedModel.getGroupId(), " into group ",
							groupId));
				}
			}
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			throw new XLIFFFileException.MustHaveValidModel(
				noSuchInfoItemException);
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Reference(target = "(content.type=application/xliff+xml)")
	private TranslationInfoItemFieldValuesExporter
		_xliffTranslationInfoItemFieldValuesExporter;

	@Reference(target = "(content.type=application/xliff+xml)")
	private TranslationInfoItemFieldValuesImporter
		_xliffTranslationInfoItemFieldValuesImporter;

}
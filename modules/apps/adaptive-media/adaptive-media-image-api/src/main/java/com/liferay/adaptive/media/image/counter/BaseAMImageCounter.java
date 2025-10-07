/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.counter;

import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.trash.model.TrashEntryTable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseAMImageCounter implements AMImageCounter {

	@Override
	public int countExpectedAMImageEntries(long companyId) {
		long previewableProcessorMaxSize =
			dlFileEntryConfigurationProvider.
				getCompanyPreviewableProcessorMaxSize(companyId);

		if (previewableProcessorMaxSize == 0) {
			return 0;
		}

		AtomicInteger counter = new AtomicInteger(0);

		try {
			_forEachGroup(
				companyId,
				_getCountDLFileEntryConsumer(
					companyId, counter, previewableProcessorMaxSize));
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return 0;
		}

		return counter.get();
	}

	protected abstract String getClassName();

	@Reference
	protected AMImageMimeTypeProvider amImageMimeTypeProvider;

	@Reference
	protected AMImageValidator amImageValidator;

	@Reference
	protected ClassNameLocalService classNameLocalService;

	@Reference
	protected DLFileEntryConfigurationProvider dlFileEntryConfigurationProvider;

	@Reference
	protected DLFileEntryLocalService dlFileEntryLocalService;

	private void _forEachGroup(long companyId, Consumer<Group> consumer)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			GroupLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setCompanyId(companyId);
		actionableDynamicQuery.setModelClass(Group.class);
		actionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<Group>)
				consumer::accept);

		actionableDynamicQuery.performActions();
	}

	private Consumer<Group> _getCountDLFileEntryConsumer(
		long companyId, AtomicInteger counter,
		long previewableProcessorMaxSize) {

		Map<Long, Long> groupPreviewableProcessorMaxSizeMap =
			dlFileEntryConfigurationProvider.
				getGroupPreviewableProcessorMaxSizeMap();

		return group -> {
			long groupId = group.getGroupId();

			long previewableGroupProcessorMaxSize =
				groupPreviewableProcessorMaxSizeMap.getOrDefault(
					groupId, previewableProcessorMaxSize);

			DSLQuery dslQuery = DSLQueryFactoryUtil.count(
			).from(
				DLFileEntryTable.INSTANCE
			).leftJoinOn(
				TrashEntryTable.INSTANCE,
				TrashEntryTable.INSTANCE.classPK.eq(
					DLFileEntryTable.INSTANCE.fileEntryId)
			).where(
				DLFileEntryTable.INSTANCE.groupId.eq(
					groupId
				).and(
					() -> {
						if (Validator.isNotNull(getClassName())) {
							return DLFileEntryTable.INSTANCE.classNameId.eq(
								classNameLocalService.getClassNameId(
									getClassName()));
						}

						return DLFileEntryTable.INSTANCE.repositoryId.eq(
							groupId);
					}
				).and(
					DLFileEntryTable.INSTANCE.companyId.eq(companyId)
				).and(
					DLFileEntryTable.INSTANCE.mimeType.in(_getMimeTypes())
				).and(
					_getPredicate(previewableGroupProcessorMaxSize)
				).and(
					TrashEntryTable.INSTANCE.entryId.isNull()
				)
			);

			int count = dlFileEntryLocalService.dslQueryCount(dslQuery);

			counter.addAndGet(count);
		};
	}

	private String[] _getMimeTypes() {
		return ArrayUtil.filter(
			amImageMimeTypeProvider.getSupportedMimeTypes(),
			amImageValidator::isProcessingSupported);
	}

	private Predicate _getPredicate(long previewableGroupProcessorMaxSize) {
		if (previewableGroupProcessorMaxSize > 0) {
			return DLFileEntryTable.INSTANCE.size.lte(
				previewableGroupProcessorMaxSize);
		}

		return Predicate.withParentheses(null);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAMImageCounter.class);

}
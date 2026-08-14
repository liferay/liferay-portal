/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.dispatch.executor;

import com.liferay.analytics.settings.rest.constants.FieldProductConstants;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.Category;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.Product;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.ProductChannel;
import com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0.CategoryBatchEngineTaskItemDelegate;
import com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0.ProductBatchEngineTaskItemDelegate;
import com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0.ProductChannelBatchEngineTaskItemDelegate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + AnalyticsUploadProductDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + AnalyticsUploadProductDispatchTaskExecutor.KEY
	},
	service = DispatchTaskExecutor.class
)
public class AnalyticsUploadProductDispatchTaskExecutor
	extends BaseDispatchTaskExecutor {

	public static final String KEY = "analytics-upload-product";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws Exception {

		DispatchLog dispatchLog =
			dispatchLogLocalService.fetchLatestDispatchLog(
				dispatchTrigger.getDispatchTriggerId(),
				DispatchTaskStatus.IN_PROGRESS);
		String filterString = getCommerceChannelFilterString(
			dispatchTrigger.getCompanyId(),
			commerceChannelId -> {
				Group group = _groupLocalService.fetchGroup(
					dispatchTrigger.getCompanyId(),
					_commerceChannelClassNameIdSupplier.get(),
					commerceChannelId);

				return "commerceChannelGroupIds/any(c:contains(c,'" +
					group.getGroupId() + "'))";
			});

		if (Objects.equals(StringPool.BLANK, filterString)) {
			updateDispatchLog(
				dispatchLog.getDispatchLogId(), dispatchTaskExecutorOutput,
				"No Commerce Channels enabled for synchronisation");

			return;
		}

		Date resourceLastModifiedDate = getLatestSuccessfulDispatchLogEndDate(
			dispatchTrigger.getDispatchTriggerId());

		analyticsBatchExportImportManager.exportToAnalyticsCloud(
			CategoryBatchEngineTaskItemDelegate.KEY,
			dispatchTrigger.getCompanyId(),
			Arrays.asList(FieldProductConstants.FIELD_CATEGORY_NAMES), null,
			message -> updateDispatchLog(
				dispatchLog.getDispatchLogId(), dispatchTaskExecutorOutput,
				message),
			resourceLastModifiedDate, Category.class.getName(),
			dispatchTrigger.getUserId());

		analyticsBatchExportImportManager.exportToAnalyticsCloud(
			ProductBatchEngineTaskItemDelegate.KEY,
			dispatchTrigger.getCompanyId(),
			Arrays.asList(FieldProductConstants.FIELD_PRODUCT_NAMES),
			filterString,
			message -> updateDispatchLog(
				dispatchLog.getDispatchLogId(), dispatchTaskExecutorOutput,
				message),
			resourceLastModifiedDate, Product.class.getName(),
			dispatchTrigger.getUserId());

		analyticsBatchExportImportManager.exportToAnalyticsCloud(
			ProductChannelBatchEngineTaskItemDelegate.KEY,
			dispatchTrigger.getCompanyId(),
			Arrays.asList(FieldProductConstants.FIELD_PRODUCT_CHANNEL_NAMES),
			getCommerceChannelFilterString(
				dispatchTrigger.getCompanyId(),
				commerceChannelId ->
					"entryClassPK eq '" + commerceChannelId + "'"),
			message -> updateDispatchLog(
				dispatchLog.getDispatchLogId(), dispatchTaskExecutorOutput,
				message),
			resourceLastModifiedDate, ProductChannel.class.getName(),
			dispatchTrigger.getUserId());
	}

	@Override
	public String getName() {
		return KEY;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_commerceChannelClassNameIdSupplier =
			_classNameLocalService.getClassNameIdSupplier(
				"com.liferay.commerce.product.model.CommerceChannel");
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private Supplier<Long> _commerceChannelClassNameIdSupplier;

	@Reference
	private GroupLocalService _groupLocalService;

}
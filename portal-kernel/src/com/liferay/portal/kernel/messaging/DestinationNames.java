/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.messaging;

/**
 * @author Brian Wing Shun Chan
 */
public interface DestinationNames {

	public static final String ASYNC_SERVICE = "liferay/async_service";

	public static final String BACKGROUND_TASK = "liferay/background_task";

	public static final String BACKGROUND_TASK_STATUS =
		"liferay/background_task_status";

	public static final String COMMERCE_BASE_PRICE_LIST =
		"liferay/commerce_base_price_list";

	public static final String COMMERCE_ORDER_STATUS =
		"liferay/commerce_order_status";

	public static final String COMMERCE_PAYMENT_STATUS =
		"liferay/commerce_payment_status";

	public static final String COMMERCE_SHIPMENT_STATUS =
		"liferay/commerce_shipment_status";

	public static final String COMMERCE_SUBSCRIPTION_STATUS =
		"liferay/commerce_subscription_status";

	public static final String CONVERT_PROCESS = "liferay/convert_process";

	public static final String DOCUMENT_LIBRARY_AUDIO_PROCESSOR =
		"liferay/document_library_audio_processor";

	public static final String DOCUMENT_LIBRARY_DELETION =
		"liferay/document_library_deletion";

	public static final String DOCUMENT_LIBRARY_IMAGE_PROCESSOR =
		"liferay/document_library_image_processor";

	public static final String DOCUMENT_LIBRARY_PDF_PROCESSOR =
		"liferay/document_library_pdf_processor";

	public static final String DOCUMENT_LIBRARY_RAW_METADATA_PROCESSOR =
		"liferay/document_library_raw_metadata_processor";

	public static final String DOCUMENT_LIBRARY_VIDEO_PROCESSOR =
		"liferay/document_library_video_processor";

	public static final String EXPORT_IMPORT_LIFECYCLE_EVENT_ASYNC =
		"liferay/export_import_lifecycle_event_async";

	public static final String EXPORT_IMPORT_LIFECYCLE_EVENT_SYNC =
		"liferay/export_import_lifecycle_event_sync";

	public static final String FLAGS = "liferay/flags";

	public static final String LAYOUTS_LOCAL_PUBLISHER =
		"liferay/layouts_local_publisher";

	public static final String LAYOUTS_REMOTE_PUBLISHER =
		"liferay/layouts_remote_publisher";

	public static final String LIVE_USERS = "liferay/live_users";

	public static final String MAIL = "liferay/mail";

	public static final String MESSAGE_BOARDS_MAILING_LIST =
		"liferay/message_boards_mailing_list";

	public static final String MONITORING = "liferay/monitoring";

	public static final String OBJECT_ENTRY_ATTACHMENT_DOWNLOAD =
		"liferay/object_entry_attachment_download";

	public static final String SCHEDULER_DISPATCH =
		"liferay/scheduler_dispatch";

	public static final String SCHEDULER_SCRIPTING =
		"liferay/scheduler_scripting";

	public static final String SUBSCRIPTION_SENDER =
		"liferay/subscription_sender";

}
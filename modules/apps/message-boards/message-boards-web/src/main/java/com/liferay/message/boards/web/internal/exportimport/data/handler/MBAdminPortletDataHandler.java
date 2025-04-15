/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryRegistryUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBBan;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBThreadFlag;
import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBThreadFlagLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Daniel Kocsis
 * @author Gergely Mathe
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
	service = PortletDataHandler.class
)
public class MBAdminPortletDataHandler extends BasePortletDataHandler {

	public static final String[] CLASS_NAMES = {
		MBBan.class.getName(), MBCategory.class.getName(),
		MBMessage.class.getName(), MBThread.class.getName(),
		MBThreadFlag.class.getName()
	};

	public static final String NAMESPACE = "message_boards";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getResourceName() {
		return MBConstants.RESOURCE_NAME;
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public String getServiceName() {
		return MBConstants.SERVICE_NAME;
	}

	@Activate
	protected void activate() {
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(MBBan.class),
			new StagedModelType(MBCategory.class),
			new StagedModelType(MBMessage.class),
			new StagedModelType(MBThread.class),
			new StagedModelType(MBThreadFlag.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "categories", true, false, null,
				MBCategory.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "messages", true, false, null,
				MBMessage.class.getName(),
				StagedModelType.REFERRER_CLASS_NAME_ALL),
			new PortletDataHandlerBoolean(
				NAMESPACE, "thread-flags", true, false, null,
				MBThreadFlag.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "user-bans", true, false, null,
				MBBan.class.getName()));
		setPublishToLiveByDefault(
			PropsValues.MESSAGE_BOARDS_PUBLISH_TO_LIVE_BY_DEFAULT);
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				MBPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_mbBanLocalService.deleteBansByGroupId(
			portletDataContext.getScopeGroupId());

		_mbCategoryLocalService.deleteCategories(
			portletDataContext.getScopeGroupId());

		_mbThreadLocalService.deleteThreads(
			portletDataContext.getScopeGroupId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortletPermissions(MBConstants.RESOURCE_NAME);

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		if (portletDataContext.getBooleanParameter(NAMESPACE, "categories") ||
			portletDataContext.getBooleanParameter(NAMESPACE, "messages")) {

			ActionableDynamicQuery categoryActionableDynamicQuery =
				_mbCategoryLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			categoryActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "messages")) {
			StagedModelRepository<?> mbMessageStagedModelRepository =
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					MBMessage.class.getName());

			ActionableDynamicQuery messageActionableDynamicQuery =
				mbMessageStagedModelRepository.getExportActionableDynamicQuery(
					portletDataContext);

			messageActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "thread-flags")) {
			ActionableDynamicQuery threadFlagActionableDynamicQuery =
				_mbThreadFlagLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			threadFlagActionableDynamicQuery.performActions();
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "user-bans")) {
			ActionableDynamicQuery banActionableDynamicQuery =
				_mbBanLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			banActionableDynamicQuery.performActions();
		}

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortletPermissions(MBConstants.RESOURCE_NAME);

		if (portletDataContext.getBooleanParameter(NAMESPACE, "categories") ||
			portletDataContext.getBooleanParameter(NAMESPACE, "messages")) {

			Element categoriesElement =
				portletDataContext.getImportDataGroupElement(MBCategory.class);

			List<Element> categoryElements = categoriesElement.elements();

			for (Element categoryElement : categoryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, categoryElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "messages")) {
			Element messagesElement =
				portletDataContext.getImportDataGroupElement(MBMessage.class);

			List<Element> messageElements = messagesElement.elements();

			for (Element messageElement : messageElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, messageElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "thread-flags")) {
			Element threadFlagsElement =
				portletDataContext.getImportDataGroupElement(
					MBThreadFlag.class);

			List<Element> threadFlagElements = threadFlagsElement.elements();

			for (Element threadFlagElement : threadFlagElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, threadFlagElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "user-bans")) {
			Element userBansElement =
				portletDataContext.getImportDataGroupElement(MBBan.class);

			List<Element> userBanElements = userBansElement.elements();

			for (Element userBanElement : userBanElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, userBanElement);
			}
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			_staging.populateLastPublishDateCounts(
				portletDataContext,
				new StagedModelType[] {
					new StagedModelType(MBBan.class.getName()),
					new StagedModelType(MBCategory.class.getName()),
					new StagedModelType(MBMessage.class.getName()),
					new StagedModelType(MBThread.class.getName()),
					new StagedModelType(MBThreadFlag.class.getName())
				});

			return;
		}

		ActionableDynamicQuery banActionableDynamicQuery =
			_mbBanLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		banActionableDynamicQuery.performCount();

		ActionableDynamicQuery categoryActionableDynamicQuery =
			_mbCategoryLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		categoryActionableDynamicQuery.performCount();

		StagedModelRepository<?> mbMessageStagedModelRepository =
			StagedModelRepositoryRegistryUtil.getStagedModelRepository(
				MBMessage.class.getName());

		ActionableDynamicQuery messageActionableDynamicQuery =
			mbMessageStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		messageActionableDynamicQuery.performCount();

		ActionableDynamicQuery threadActionableDynamicQuery =
			_mbThreadLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		threadActionableDynamicQuery.performCount();

		ActionableDynamicQuery threadFlagActionableDynamicQuery =
			_mbThreadFlagLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		threadFlagActionableDynamicQuery.performCount();
	}

	@Reference
	private MBBanLocalService _mbBanLocalService;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference
	private MBThreadFlagLocalService _mbThreadFlagLocalService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference
	private Staging _staging;

}
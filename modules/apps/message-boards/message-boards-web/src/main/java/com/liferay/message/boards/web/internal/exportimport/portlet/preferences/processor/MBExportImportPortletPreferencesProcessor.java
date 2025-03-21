/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryRegistryUtil;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBBan;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThreadFlag;
import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBThreadFlagLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Mathe
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
	service = ExportImportPortletPreferencesProcessor.class
)
public class MBExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(_mbRatingsExporterImporterCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(_mbRatingsExporterImporterCapability);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!_exportImportHelper.isExportPortletData(portletDataContext)) {
			return portletPreferences;
		}

		try {
			portletDataContext.addPortletPermissions(MBConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(MBPortletKeys.MESSAGE_BOARDS);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_PERMISSIONS);

			throw portletDataException;
		}

		try {
			String namespace = _mbPortletDataHandler.getNamespace();

			String portletId = portletDataContext.getPortletId();

			if (portletDataContext.getBooleanParameter(
					namespace, "categories") ||
				portletDataContext.getBooleanParameter(namespace, "messages")) {

				ActionableDynamicQuery categoryActionableDynamicQuery =
					_mbCategoryLocalService.getExportActionableDynamicQuery(
						portletDataContext);

				categoryActionableDynamicQuery.setPerformActionMethod(
					(MBCategory mbCategory) ->
						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext, portletId, mbCategory));

				categoryActionableDynamicQuery.performActions();
			}

			if (portletDataContext.getBooleanParameter(namespace, "messages")) {
				StagedModelRepository<?> mbMessageStagedModelRepository =
					StagedModelRepositoryRegistryUtil.getStagedModelRepository(
						MBMessage.class.getName());

				ActionableDynamicQuery messageActionableDynamicQuery =
					mbMessageStagedModelRepository.
						getExportActionableDynamicQuery(portletDataContext);

				messageActionableDynamicQuery.setPerformActionMethod(
					(MBMessage mbMessage) ->
						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext, portletId, mbMessage));

				messageActionableDynamicQuery.performActions();
			}

			if (portletDataContext.getBooleanParameter(
					namespace, "thread-flags")) {

				ActionableDynamicQuery threadFlagActionableDynamicQuery =
					_mbThreadFlagLocalService.getExportActionableDynamicQuery(
						portletDataContext);

				threadFlagActionableDynamicQuery.setPerformActionMethod(
					(MBThreadFlag mbThreadFlag) ->
						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext, portletId, mbThreadFlag));

				threadFlagActionableDynamicQuery.performActions();
			}

			if (portletDataContext.getBooleanParameter(
					namespace, "user-bans")) {

				ActionableDynamicQuery banActionableDynamicQuery =
					_mbBanLocalService.getExportActionableDynamicQuery(
						portletDataContext);

				banActionableDynamicQuery.setPerformActionMethod(
					(MBBan mbBan) ->
						StagedModelDataHandlerUtil.exportReferenceStagedModel(
							portletDataContext, portletId, mbBan));

				banActionableDynamicQuery.performActions();
			}
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(MBPortletKeys.MESSAGE_BOARDS);
			portletDataException.setType(
				PortletDataException.EXPORT_PORTLET_DATA);

			throw portletDataException;
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			portletDataContext.importPortletPermissions(
				MBConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			PortletDataException portletDataException =
				new PortletDataException(portalException);

			portletDataException.setPortletId(MBPortletKeys.MESSAGE_BOARDS);
			portletDataException.setType(
				PortletDataException.IMPORT_PORTLET_PERMISSIONS);

			throw portletDataException;
		}

		String namespace = _mbPortletDataHandler.getNamespace();

		if (portletDataContext.getBooleanParameter(namespace, "categories") ||
			portletDataContext.getBooleanParameter(namespace, "messages")) {

			Element categoriesElement =
				portletDataContext.getImportDataGroupElement(MBCategory.class);

			List<Element> categoryElements = categoriesElement.elements();

			for (Element categoryElement : categoryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, categoryElement);
			}
		}

		if (portletDataContext.getBooleanParameter(namespace, "messages")) {
			Element messagesElement =
				portletDataContext.getImportDataGroupElement(MBMessage.class);

			List<Element> messageElements = messagesElement.elements();

			for (Element messageElement : messageElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, messageElement);
			}
		}

		if (portletDataContext.getBooleanParameter(namespace, "thread-flags")) {
			Element threadFlagsElement =
				portletDataContext.getImportDataGroupElement(
					MBThreadFlag.class);

			List<Element> threadFlagElements = threadFlagsElement.elements();

			for (Element threadFlagElement : threadFlagElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, threadFlagElement);
			}
		}

		if (portletDataContext.getBooleanParameter(namespace, "user-bans")) {
			Element userBansElement =
				portletDataContext.getImportDataGroupElement(MBBan.class);

			List<Element> userBanElements = userBansElement.elements();

			for (Element userBanElement : userBanElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, userBanElement);
			}
		}

		return portletPreferences;
	}

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private MBBanLocalService _mbBanLocalService;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference(
		target = "(jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS + ")"
	)
	private PortletDataHandler _mbPortletDataHandler;

	@Reference(
		target = "(component.name=com.liferay.message.boards.web.internal.exportimport.portlet.preferences.processor.MBRatingsExporterImporterCapability)"
	)
	private Capability _mbRatingsExporterImporterCapability;

	@Reference
	private MBThreadFlagLocalService _mbThreadFlagLocalService;

}
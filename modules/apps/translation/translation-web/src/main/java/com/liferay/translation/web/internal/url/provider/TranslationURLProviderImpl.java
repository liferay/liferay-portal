/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.url.provider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.url.provider.TranslationURLProvider;

import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = TranslationURLProvider.class)
public class TranslationURLProviderImpl implements TranslationURLProvider {

	@Override
	public PortletURL getExportTranslationURL(
		long groupId, long classNameId, long classPK,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				TranslationPortletKeys.TRANSLATION)
		).setMVCRenderCommandName(
			"/translation/export_translation"
		).setParameter(
			"classNameId", classNameId
		).setParameter(
			"classPK", classPK
		).setParameter(
			"groupId", groupId
		).buildPortletURL();
	}

	@Override
	public PortletURL getExportTranslationURL(
		long groupId, long classNameId,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				TranslationPortletKeys.TRANSLATION)
		).setMVCRenderCommandName(
			"/translation/export_translation"
		).setParameter(
			"classNameId", classNameId
		).setParameter(
			"groupId", groupId
		).buildPortletURL();
	}

	@Override
	public PortletURL getImportTranslationURL(
			long groupId, long classNameId, long classPK,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory)
		throws PortalException {

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				TranslationPortletKeys.TRANSLATION,
				_groupLocalService.getGroup(groupId), 0, 0)
		).setMVCRenderCommandName(
			"/translation/import_translation"
		).setParameter(
			"classNameId", classNameId
		).setParameter(
			"classPK", classPK
		).setParameter(
			"groupId", groupId
		).buildPortletURL();
	}

	@Override
	public PortletURL getImportTranslationURL(
			long groupId, long classNameId,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory)
		throws PortalException {

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				TranslationPortletKeys.TRANSLATION,
				_groupLocalService.getGroup(groupId), 0, 0)
		).setMVCRenderCommandName(
			"/translation/import_translation"
		).setParameter(
			"classNameId", classNameId
		).setParameter(
			"groupId", groupId
		).buildPortletURL();
	}

	@Override
	public PortletURL getTranslateURL(
			long groupId, long classNameId, long classPK,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory)
		throws PortalException {

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				TranslationPortletKeys.TRANSLATION,
				_groupLocalService.getGroup(groupId), 0, 0)
		).setMVCRenderCommandName(
			"/translation/translate"
		).setParameter(
			"classNameId", classNameId
		).setParameter(
			"classPK", classPK
		).buildPortletURL();
	}

	@Override
	public PortletURL getTranslateURL(
		long classNameId, long classPK,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				TranslationPortletKeys.TRANSLATION)
		).setMVCRenderCommandName(
			"/translation/translate"
		).setParameter(
			"classNameId", classNameId
		).setParameter(
			"classPK", classPK
		).buildPortletURL();
	}

	@Reference
	private GroupLocalService _groupLocalService;

}
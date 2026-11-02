/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service.impl;

import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink;
import com.liferay.portal.workflow.kaleo.forms.service.base.KaleoProcessLinkLocalServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the local service for accessing, adding, deleting, and updating
 * Kaleo process links.
 *
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink",
	service = AopService.class
)
public class KaleoProcessLinkLocalServiceImpl
	extends KaleoProcessLinkLocalServiceBaseImpl {

	/**
	 * Adds a Kaleo process link referencing the Kaleo process.
	 *
	 * @param  kaleoProcessId the primary key of the Kaleo process
	 * @param  workflowTaskName the name of the Kaleo process link's workflow
	 *         task
	 * @param  ddmTemplateId the primary key of the Kaleo process link's DDM
	 *         template
	 * @return the Kaleo process link
	 */
	@Override
	public KaleoProcessLink addKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName, long ddmTemplateId) {

		long kaleoProcessLinkId = counterLocalService.increment();

		KaleoProcessLink kaleoProcessLink = kaleoProcessLinkPersistence.create(
			kaleoProcessLinkId);

		kaleoProcessLink.setKaleoProcessId(kaleoProcessId);
		kaleoProcessLink.setWorkflowTaskName(workflowTaskName);
		kaleoProcessLink.setDDMTemplateId(ddmTemplateId);

		kaleoProcessLink = kaleoProcessLinkPersistence.update(kaleoProcessLink);

		_ddmTemplateLinkLocalService.addTemplateLink(
			_classNameLocalService.getClassNameId(KaleoProcessLink.class),
			kaleoProcessLinkId, ddmTemplateId);

		return kaleoProcessLink;
	}

	/**
	 * Deletes the Kaleo process links associated with the Kaleo process and
	 * their resources.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process from which to
	 *        delete Kaleo process links
	 */
	@Override
	public void deleteKaleoProcessLinks(long kaleoProcessId) {
		List<KaleoProcessLink> kaleoProcessLinks =
			kaleoProcessLinkPersistence.findByKaleoProcessId(kaleoProcessId);

		for (KaleoProcessLink kaleoProcessLink : kaleoProcessLinks) {
			deleteKaleoProcessLink(kaleoProcessLink);

			_ddmTemplateLinkLocalService.deleteTemplateLink(
				_classNameLocalService.getClassNameId(KaleoProcessLink.class),
				kaleoProcessLink.getKaleoProcessLinkId());
		}
	}

	/**
	 * Returns the Kaleo process link matching the Kaleo process and workflow
	 * task name.
	 *
	 * @param  kaleoProcessId the primary key of the Kaleo process link's Kaleo
	 *         process
	 * @param  workflowTaskName the name of the Kaleo process link's workflow
	 *         task
	 * @return the Kaleo process link matching the Kaleo process and workflow
	 *         task name, or <code>null</code> if a matching Kaleo process link
	 *         could not be found
	 */
	@Override
	public KaleoProcessLink fetchKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName) {

		return kaleoProcessLinkPersistence.fetchByKPI_WTN(
			kaleoProcessId, workflowTaskName);
	}

	/**
	 * Returns the Kaleo process links matching the Kaleo process.
	 *
	 * @param  kaleoProcessId the primary key of the Kaleo process link's Kaleo
	 *         process
	 * @return the Kaleo process links matching the Kaleo process, or
	 *         <code>null</code> if a matching Kaleo process link could not be
	 *         found
	 */
	@Override
	public List<KaleoProcessLink> getKaleoProcessLinks(long kaleoProcessId) {
		return kaleoProcessLinkPersistence.findByKaleoProcessId(kaleoProcessId);
	}

	/**
	 * Updates the Kaleo process link. If no Kaleo process link is found
	 * matching the primary key of the Kaleo process and the workflow task name,
	 * a new link is created.
	 *
	 * @param  kaleoProcessId the primary key of the Kaleo process link's Kaleo
	 *         process
	 * @param  workflowTaskName the name of the Kaleo process link's workflow
	 *         task
	 * @param  ddmTemplateId the primary key of the Kaleo process link's DDM
	 *         template
	 * @return the Kaleo process link
	 */
	@Override
	public KaleoProcessLink updateKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName, long ddmTemplateId) {

		KaleoProcessLink kaleoProcessLink =
			kaleoProcessLinkPersistence.fetchByKPI_WTN(
				kaleoProcessId, workflowTaskName);

		if (kaleoProcessLink == null) {
			return addKaleoProcessLink(
				kaleoProcessId, workflowTaskName, ddmTemplateId);
		}

		kaleoProcessLink.setDDMTemplateId(ddmTemplateId);

		kaleoProcessLink = kaleoProcessLinkPersistence.update(kaleoProcessLink);

		_ddmTemplateLinkLocalService.updateTemplateLink(
			_classNameLocalService.getClassNameId(KaleoProcessLink.class),
			kaleoProcessLink.getKaleoProcessLinkId(), ddmTemplateId);

		return kaleoProcessLink;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

}
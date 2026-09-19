/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(category = "infrastructure")
@Meta.OCD(
	id = "com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration",
	localization = "content/Language", name = "monitoring-configuration-name"
)
public interface MonitoringConfiguration {

	@Meta.AD(deflt = "true", name = "monitor-enabled", required = false)
	public boolean monitorEnabled();

	@Meta.AD(deflt = "true", name = "monitor-portal-request", required = false)
	public boolean monitorPortalRequest();

	@Meta.AD(
		deflt = "true", name = "monitor-portlet-action-request",
		required = false
	)
	public boolean monitorPortletActionRequest();

	@Meta.AD(
		deflt = "true", name = "monitor-portlet-event-request", required = false
	)
	public boolean monitorPortletEventRequest();

	@Meta.AD(
		deflt = "true", name = "monitor-portlet-header-request",
		required = false
	)
	public boolean monitorPortletHeaderRequest();

	@Meta.AD(
		deflt = "true", name = "monitor-portlet-render-request",
		required = false
	)
	public boolean monitorPortletRenderRequest();

	@Meta.AD(
		deflt = "true", name = "monitor-portlet-resource-request",
		required = false
	)
	public boolean monitorPortletResourceRequest();

	@Meta.AD(
		deflt = "false", name = "monitor-service-request", required = false
	)
	public boolean monitorServiceRequest();

	@Meta.AD(
		deflt = "200", name = "monitoring-message-max-queue-size",
		required = false
	)
	public int monitoringMessageMaxQueueSize();

	@Meta.AD(
		deflt = "false", name = "show-per-request-data-sample", required = false
	)
	public boolean showPerRequestDataSample();

}
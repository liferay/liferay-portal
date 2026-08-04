/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.util.filter;

import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowNode;
import com.liferay.portal.kernel.workflow.WorkflowTransition;

import java.io.InputStream;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public class WorkflowDefinitionImpl implements WorkflowDefinition {

	public WorkflowDefinitionImpl(boolean active) {
		this(null, null, null, null, active);
	}

	public WorkflowDefinitionImpl(Date modifiedDate) {
		this(null, null, null, modifiedDate, false);
	}

	public WorkflowDefinitionImpl(String name, String title) {
		this(name, title, null, null, false);
	}

	public WorkflowDefinitionImpl(
		String name, String title, String description) {

		this(name, title, description, null, false);
	}

	public WorkflowDefinitionImpl(
		String name, String title, String description, Date modifiedDate,
		boolean active) {

		_name = name;
		_title = title;
		_description = description;
		_modifiedDate = modifiedDate;
		_active = active;
	}

	@Override
	public String getContent() {
		return null;
	}

	@Override
	public String getContentAsXML() {
		return null;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public InputStream getInputStream() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Map<String, Object> getOptionalAttributes() {
		return null;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public String getTitle(String languageId) {
		return _title;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public List<WorkflowNode> getWorkflowNodes() {
		return null;
	}

	@Override
	public List<WorkflowTransition> getWorkflowTransitions() {
		return null;
	}

	@Override
	public boolean isActive() {
		return _active;
	}

	@Override
	public boolean isSystem() {
		return false;
	}

	private final boolean _active;
	private final String _description;
	private final Date _modifiedDate;
	private final String _name;
	private final String _title;

}
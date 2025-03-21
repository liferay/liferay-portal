/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.changeset.taglib.servlet.taglib;

import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.ChangesetManager;
import com.liferay.exportimport.changeset.ChangesetManagerUtil;
import com.liferay.exportimport.changeset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Máté Thurzó
 */
public class PublishEntityMenuItemTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		Changeset.RawBuilder rawBuilder = Changeset.createRaw(_uuid);

		String className = _className;

		if (Validator.isNull(className)) {
			className = PortalUtil.getClassName(_classNameId);
		}

		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				className);

		Changeset changeset = rawBuilder.addStagedModel(
			stagedModelDataHandler.fetchStagedModelByUuidAndGroupId(
				_uuid, _groupId)
		).build();

		ChangesetManager changesetManager =
			ChangesetManagerUtil.getChangesetManager();

		changesetManager.addChangeset(changeset);

		_changesetUuid = changeset.getUuid();

		return EVAL_BODY_INCLUDE;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassNameId() {
		return _classNameId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public String getUuid() {
		return _uuid;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassNameId(long classNameId) {
		_classNameId = classNameId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_changesetUuid = StringPool.BLANK;
		_className = StringPool.BLANK;
		_classNameId = 0;
		_groupId = 0;
		_uuid = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-export-import-changeset:publish-entity-menu-item:" +
				"changesetUuid",
			_changesetUuid);
		httpServletRequest.setAttribute(
			"liferay-export-import-changeset:publish-entity-menu-item:" +
				"className",
			_className);
		httpServletRequest.setAttribute(
			"liferay-export-import-changeset:publish-entity-menu-item:groupId",
			_groupId);
		httpServletRequest.setAttribute(
			"liferay-export-import-changeset:publish-entity-menu-item:uuid",
			_uuid);
	}

	private static final String _PAGE = "/publish_entity_menu_item/page.jsp";

	private String _changesetUuid = StringPool.BLANK;
	private String _className = StringPool.BLANK;
	private long _classNameId;
	private long _groupId;
	private String _uuid = StringPool.BLANK;

}
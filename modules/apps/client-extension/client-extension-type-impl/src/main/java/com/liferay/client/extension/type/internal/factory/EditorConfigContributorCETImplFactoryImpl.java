/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.EditorConfigContributorCET;
import com.liferay.client.extension.type.internal.EditorConfigContributorCETImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.Properties;

/**
 * @author Daniel Sanz
 */
public class EditorConfigContributorCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<EditorConfigContributorCET> {

	public EditorConfigContributorCETImplFactoryImpl() {
		super(EditorConfigContributorCET.class);
	}

	@Override
	public EditorConfigContributorCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new EditorConfigContributorCETImpl(
			baseURL, companyId, createDate, description, externalReferenceCode,
			modifiedDate, name, properties, readOnly, sourceCodeURL, status,
			typeSettingsUnicodeProperties);
	}

	@Override
	public UnicodeProperties getUnicodeProperties(
		PortletRequest portletRequest) {

		return UnicodePropertiesBuilder.create(
			true
		).put(
			"editorConfigKeys",
			StringUtil.merge(
				ParamUtil.getStringValues(portletRequest, "editorConfigKeys"),
				StringPool.NEW_LINE)
		).put(
			"editorNames",
			StringUtil.merge(
				ParamUtil.getStringValues(portletRequest, "editorNames"),
				StringPool.NEW_LINE)
		).put(
			"portletNames",
			StringUtil.merge(
				ParamUtil.getStringValues(portletRequest, "portletNames"),
				StringPool.NEW_LINE)
		).put(
			"url", ParamUtil.getString(portletRequest, "url")
		).build();
	}

	@Override
	public void validate(
			EditorConfigContributorCET newEditorConfigContributorCET,
			EditorConfigContributorCET oldEditorConfigContributorCET)
		throws PortalException {

		if (Validator.isNull(newEditorConfigContributorCET.getURL())) {
			throw new ClientExtensionEntryTypeSettingsException(
				"At least one JavaScript URL is required",
				"please-enter-at-least-one-javascript-url");
		}
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.field.expression.handler;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.saml.opensaml.integration.processor.context.UserProcessorContext;

import java.util.List;
import java.util.Locale;

/**
 * @author Stian Sigvartsen
 */
@ProviderType
public interface UserFieldExpressionHandler
	extends FieldExpressionHandler<User, UserProcessorContext> {

	public User getLdapUser(
			long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws Exception;

	public String getSectionLabel(Locale locale);

	public User getUser(
			long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws PortalException;

	public List<String> getValidFieldExpressions();

	public boolean isSupportedForUserMatching(String userIdentifier);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Péter Borkuti
 */
public class ProcessErrorTag extends IncludeTag {

	public boolean isAuthException() {
		return _authException;
	}

	public boolean isDuplicateLockException() {
		return _duplicateLockException;
	}

	public boolean isIllegalArgumentException() {
		return _illegalArgumentException;
	}

	public boolean isLayoutPrototypeException() {
		return _layoutPrototypeException;
	}

	public boolean isNoSuchExceptions() {
		return _noSuchExceptions;
	}

	public boolean isRemoteExportException() {
		return _remoteExportException;
	}

	public boolean isRemoteOptionsException() {
		return _remoteOptionsException;
	}

	public boolean isSystemException() {
		return _systemException;
	}

	public void setAuthException(boolean authException) {
		_authException = authException;
	}

	public void setDuplicateLockException(boolean duplicateLockException) {
		_duplicateLockException = duplicateLockException;
	}

	public void setIllegalArgumentException(boolean illegalArgumentException) {
		_illegalArgumentException = illegalArgumentException;
	}

	public void setLayoutPrototypeException(boolean layoutPrototypeException) {
		_layoutPrototypeException = layoutPrototypeException;
	}

	public void setNoSuchExceptions(boolean noSuchExceptions) {
		_noSuchExceptions = noSuchExceptions;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRemoteExportException(boolean remoteExportException) {
		_remoteExportException = remoteExportException;
	}

	public void setRemoteOptionsException(boolean remoteOptionsException) {
		_remoteOptionsException = remoteOptionsException;
	}

	public void setSystemException(boolean systemException) {
		_systemException = systemException;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_authException = false;
		_duplicateLockException = false;
		_illegalArgumentException = false;
		_layoutPrototypeException = false;
		_noSuchExceptions = false;
		_remoteExportException = false;
		_remoteOptionsException = false;
		_systemException = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:process-error:authException", _authException);
		httpServletRequest.setAttribute(
			"liferay-staging:process-error:duplicateLockException",
			_duplicateLockException);
		httpServletRequest.setAttribute(
			"liferay-staging:process-error:illegalArgumentException",
			_illegalArgumentException);
		httpServletRequest.setAttribute(
			"liferay-staging:process-error:layoutPrototypeException",
			_layoutPrototypeException);
		httpServletRequest.setAttribute(
			"liferay-staging:process-error:noSuchExceptions",
			_noSuchExceptions);
		httpServletRequest.setAttribute(
			"liferay-staging:process-error:remoteExportException",
			_remoteExportException);
		httpServletRequest.setAttribute(
			"liferay-staging:process-error:remoteOptionsException",
			_remoteOptionsException);
		httpServletRequest.setAttribute(
			"liferay-staging:process-error:systemException", _systemException);
	}

	private static final String _PAGE = "/process_error/page.jsp";

	private boolean _authException;
	private boolean _duplicateLockException;
	private boolean _illegalArgumentException;
	private boolean _layoutPrototypeException;
	private boolean _noSuchExceptions;
	private boolean _remoteExportException;
	private boolean _remoteOptionsException;
	private boolean _systemException;

}
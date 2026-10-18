/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.internal.messaging;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ServiceContext;

import java.io.Serializable;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
public class FlagsRequest implements Serializable {

	public FlagsRequest() {
	}

	public FlagsRequest(
		String className, long classPK, String reporterEmailAddress,
		long reportedUserId, String contentTitle, String contentURL,
		String reason, ServiceContext serviceContext) {

		_className = className;
		_classPK = classPK;
		_reporterEmailAddress = reporterEmailAddress;
		_reportedUserId = reportedUserId;
		_contentTitle = contentTitle;
		_contentURL = contentURL;
		_reason = reason;
		_serviceContext = serviceContext;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getComments() {
		return _comments;
	}

	public String getContentTitle() {
		return _contentTitle;
	}

	public String getContentURL() {
		return _contentURL;
	}

	public String getReason() {
		return _reason;
	}

	public long getReportedUserId() {
		return _reportedUserId;
	}

	public String getReportedUserURL() {
		return _reportedUserURL;
	}

	public String getReporterEmailAddress() {
		return _reporterEmailAddress;
	}

	public ServiceContext getServiceContext() {
		return _serviceContext;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setComments(String comments) {
		_comments = comments;
	}

	public void setContentTitle(String contentTitle) {
		_contentTitle = contentTitle;
	}

	public void setContentURL(String contentURL) {
		_contentURL = contentURL;
	}

	public void setReason(String reason) {
		_reason = reason;
	}

	public void setReportedUserId(long reportedUserId) {
		_reportedUserId = reportedUserId;
	}

	public void setReportedUserURL(String reportedUserURL) {
		_reportedUserURL = reportedUserURL;
	}

	public void setReporterEmailAddress(String reporterEmailAddress) {
		_reporterEmailAddress = reporterEmailAddress;
	}

	public void setServiceContext(ServiceContext serviceContext) {
		_serviceContext = serviceContext;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{className=", _className, ", classPK=", _classPK, ", comments=",
			_comments, ", contentTitle=", _contentTitle, ", contentURL=",
			_contentURL, ", reason=", _reason, ", reportedUserId=",
			_reportedUserId, ", reportedUserURL=", _reportedUserURL,
			", reporterEmailAddress=", _reporterEmailAddress,
			", serviceContext=", _serviceContext, "}");
	}

	private String _className;
	private long _classPK;
	private String _comments;
	private String _contentTitle;
	private String _contentURL;
	private String _reason;
	private long _reportedUserId;
	private String _reportedUserURL = StringPool.BLANK;
	private String _reporterEmailAddress;
	private ServiceContext _serviceContext;

}
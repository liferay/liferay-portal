/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.social.kernel.model.SocialRequest;
import com.liferay.social.kernel.model.SocialRequestFeedEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SocialRequestInterpreter. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see SocialRequestInterpreterLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SocialRequestInterpreterLocalService extends BaseLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.social.service.impl.SocialRequestInterpreterLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the social request interpreter local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SocialRequestInterpreterLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * Creates a human readable request feed entry for the social request using
	 * an available compatible request interpreter.
	 *
	 * <p>
	 * This method finds the appropriate interpreter for the request by going
	 * through the available interpreters to find one that can handle the asset
	 * type of the request.
	 * </p>
	 *
	 * @param request the social request to be translated to human readable
	 form
	 * @param themeDisplay the theme display needed by interpreters to create
	 links and get localized text fragments
	 * @return the social request feed entry
	 */
	public SocialRequestFeedEntry interpret(
		SocialRequest request, ThemeDisplay themeDisplay);

	/**
	 * Processes the confirmation of the social request.
	 *
	 * <p>
	 * Confirmations are handled by finding the appropriate social request
	 * interpreter and calling its processConfirmation() method. To find the
	 * appropriate interpreter this method goes through the available
	 * interpreters to find one that can handle the asset type of the request.
	 * </p>
	 *
	 * @param request the social request being confirmed
	 * @param themeDisplay the theme display needed by interpreters to create
	 links and get localized text fragments
	 */
	public void processConfirmation(
		SocialRequest request, ThemeDisplay themeDisplay);

	/**
	 * Processes the rejection of the social request.
	 *
	 * <p>
	 * Rejections are handled by finding the appropriate social request
	 * interpreters and calling their processRejection() methods. To find the
	 * appropriate interpreters this method goes through the available
	 * interpreters and asks them if they can handle the asset type of the
	 * request.
	 * </p>
	 *
	 * @param request the social request being rejected
	 * @param themeDisplay the theme display needed by interpreters to create
	 links and get localized text fragments
	 */
	public void processRejection(
		SocialRequest request, ThemeDisplay themeDisplay);

}
// LIFERAY-SERVICE-BUILDER-HASH:-96726058
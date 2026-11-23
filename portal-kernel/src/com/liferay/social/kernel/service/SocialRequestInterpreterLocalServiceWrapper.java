/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link SocialRequestInterpreterLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SocialRequestInterpreterLocalService
 * @generated
 */
public class SocialRequestInterpreterLocalServiceWrapper
	implements ServiceWrapper<SocialRequestInterpreterLocalService>,
			   SocialRequestInterpreterLocalService {

	public SocialRequestInterpreterLocalServiceWrapper() {
		this(null);
	}

	public SocialRequestInterpreterLocalServiceWrapper(
		SocialRequestInterpreterLocalService
			socialRequestInterpreterLocalService) {

		_socialRequestInterpreterLocalService =
			socialRequestInterpreterLocalService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _socialRequestInterpreterLocalService.getOSGiServiceIdentifier();
	}

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
	@Override
	public void processConfirmation(
		com.liferay.social.kernel.model.SocialRequest request,
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		_socialRequestInterpreterLocalService.processConfirmation(
			request, themeDisplay);
	}

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
	@Override
	public void processRejection(
		com.liferay.social.kernel.model.SocialRequest request,
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		_socialRequestInterpreterLocalService.processRejection(
			request, themeDisplay);
	}

	@Override
	public SocialRequestInterpreterLocalService getWrappedService() {
		return _socialRequestInterpreterLocalService;
	}

	@Override
	public void setWrappedService(
		SocialRequestInterpreterLocalService
			socialRequestInterpreterLocalService) {

		_socialRequestInterpreterLocalService =
			socialRequestInterpreterLocalService;
	}

	private SocialRequestInterpreterLocalService
		_socialRequestInterpreterLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2010092001
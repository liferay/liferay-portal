/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

/**
 * Provides a wrapper for {@link SocialActivityInterpreterLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityInterpreterLocalService
 * @generated
 */
public class SocialActivityInterpreterLocalServiceWrapper
	implements ServiceWrapper<SocialActivityInterpreterLocalService>,
			   SocialActivityInterpreterLocalService {

	public SocialActivityInterpreterLocalServiceWrapper() {
		this(null);
	}

	public SocialActivityInterpreterLocalServiceWrapper(
		SocialActivityInterpreterLocalService
			socialActivityInterpreterLocalService) {

		_socialActivityInterpreterLocalService =
			socialActivityInterpreterLocalService;
	}

	@Override
	public java.util.List<SocialActivityInterpreter> getActivityInterpreters(
		String selector) {

		return _socialActivityInterpreterLocalService.getActivityInterpreters(
			selector);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _socialActivityInterpreterLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * Creates a human readable activity feed entry for the activity using an
	 * available compatible activity interpreter.
	 *
	 * <p>
	 * This method finds the appropriate interpreter for the activity by going
	 * through the available interpreters and asking them if they can handle the
	 * asset type of the activity.
	 * </p>
	 *
	 * @param selector the context in which the activity interpreter is used
	 * @param activity the activity to be translated to human readable form
	 * @param serviceContext the service context to be applied
	 * @return the activity feed that is a human readable form of the activity
	 record or <code>null</code> if a compatible interpreter is not
	 found
	 */
	@Override
	public com.liferay.social.kernel.model.SocialActivityFeedEntry interpret(
		String selector,
		com.liferay.social.kernel.model.SocialActivity activity,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _socialActivityInterpreterLocalService.interpret(
			selector, activity, serviceContext);
	}

	@Override
	public com.liferay.social.kernel.model.SocialActivityFeedEntry interpret(
		String selector,
		com.liferay.social.kernel.model.SocialActivitySet activitySet,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _socialActivityInterpreterLocalService.interpret(
			selector, activitySet, serviceContext);
	}

	@Override
	public void updateActivitySet(long activityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivityInterpreterLocalService.updateActivitySet(activityId);
	}

	@Override
	public SocialActivityInterpreterLocalService getWrappedService() {
		return _socialActivityInterpreterLocalService;
	}

	@Override
	public void setWrappedService(
		SocialActivityInterpreterLocalService
			socialActivityInterpreterLocalService) {

		_socialActivityInterpreterLocalService =
			socialActivityInterpreterLocalService;
	}

	private SocialActivityInterpreterLocalService
		_socialActivityInterpreterLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2060971088
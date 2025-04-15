/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.social.service.base.SocialActivityInterpreterLocalServiceBaseImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityFeedEntry;
import com.liferay.social.kernel.model.SocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivitySet;
import com.liferay.social.kernel.model.impl.SocialActivityInterpreterImpl;
import com.liferay.social.kernel.model.impl.SocialRequestInterpreterImpl;
import com.liferay.social.kernel.service.SocialActivityLocalService;
import com.liferay.social.kernel.service.SocialActivitySetLocalService;
import com.liferay.social.kernel.service.persistence.SocialActivityPersistence;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The social activity interpreter local service. Activity interpreters are
 * classes responsible for translating activity records into human readable
 * form. This service holds a list of interpreters and provides methods to add
 * or remove items from this list.
 *
 * <p>
 * Activity interpreters use the language files to get text fragments based on
 * the activity's type and the type of asset on which the activity was done.
 * Interpreters are created for specific asset types and are only capable of
 * translating activities done on assets of those types. As an example, there is
 * an interpreter BlogsActivityInterpreter that can only translate activity
 * records for blog entries.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
public class SocialActivityInterpreterLocalServiceImpl
	extends SocialActivityInterpreterLocalServiceBaseImpl {

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			_bundleContext, SocialActivityInterpreter.class,
			"(jakarta.portlet.name=*)",
			ServiceReferenceMapperFactory.createFromFunction(
				_bundleContext, SocialActivityInterpreter::getSelector),
			new ServiceTrackerCustomizer
				<SocialActivityInterpreter, SocialActivityInterpreter>() {

				@Override
				public SocialActivityInterpreter addingService(
					ServiceReference<SocialActivityInterpreter>
						serviceReference) {

					SocialActivityInterpreter activityInterpreter =
						_bundleContext.getService(serviceReference);

					if (!(activityInterpreter instanceof
							SocialRequestInterpreterImpl)) {

						String portletId = (String)serviceReference.getProperty(
							"jakarta.portlet.name");

						activityInterpreter = new SocialActivityInterpreterImpl(
							portletId, activityInterpreter);
					}

					return activityInterpreter;
				}

				@Override
				public void modifiedService(
					ServiceReference<SocialActivityInterpreter>
						serviceReference,
					SocialActivityInterpreter socialActivityInterpreter) {
				}

				@Override
				public void removedService(
					ServiceReference<SocialActivityInterpreter>
						serviceReference,
					SocialActivityInterpreter socialActivityInterpreter) {

					_bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Override
	public void destroy() {
		super.destroy();

		_serviceTrackerMap.close();
	}

	@Override
	public Map<String, List<SocialActivityInterpreter>>
		getActivityInterpreters() {

		Map<String, List<SocialActivityInterpreter>> map = new HashMap<>();

		for (String selector : _serviceTrackerMap.keySet()) {
			map.put(selector, _serviceTrackerMap.getService(selector));
		}

		return map;
	}

	@Override
	public List<SocialActivityInterpreter> getActivityInterpreters(
		String selector) {

		return _serviceTrackerMap.getService(selector);
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
	 * @param  selector the context in which the activity interpreter is used
	 * @param  activity the activity to be translated to human readable form
	 * @param  serviceContext the service context to be applied
	 * @return the activity feed that is a human readable form of the activity
	 *         record or <code>null</code> if a compatible interpreter is not
	 *         found
	 */
	@Override
	public SocialActivityFeedEntry interpret(
		String selector, SocialActivity activity,
		ServiceContext serviceContext) {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (activity.getUserId() == themeDisplay.getGuestUserId()) {
				return null;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (activity.getMirrorActivityId() > 0) {
			SocialActivity mirrorActivity = null;

			try {
				mirrorActivity = _socialActivityLocalService.getActivity(
					activity.getMirrorActivityId());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			if (mirrorActivity != null) {
				activity = mirrorActivity;
			}
		}

		List<SocialActivityInterpreter> activityInterpreters =
			_serviceTrackerMap.getService(selector);

		if (activityInterpreters == null) {
			return null;
		}

		String className = PortalUtil.getClassName(activity.getClassNameId());

		for (SocialActivityInterpreter activityInterpreter :
				activityInterpreters) {

			SocialActivityInterpreterImpl socialActivityInterpreterImpl =
				(SocialActivityInterpreterImpl)activityInterpreter;

			if (socialActivityInterpreterImpl.hasClassName(className)) {
				SocialActivityFeedEntry activityFeedEntry =
					socialActivityInterpreterImpl.interpret(
						activity, serviceContext);

				if (activityFeedEntry != null) {
					activityFeedEntry.setPortletId(
						socialActivityInterpreterImpl.getPortletId());

					return activityFeedEntry;
				}
			}
		}

		return null;
	}

	@Override
	public SocialActivityFeedEntry interpret(
		String selector, SocialActivitySet activitySet,
		ServiceContext serviceContext) {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (activitySet.getUserId() == themeDisplay.getGuestUserId()) {
				return null;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		List<SocialActivityInterpreter> activityInterpreters =
			_serviceTrackerMap.getService(selector);

		if (activityInterpreters == null) {
			return null;
		}

		String className = PortalUtil.getClassName(
			activitySet.getClassNameId());

		for (SocialActivityInterpreter activityInterpreter :
				activityInterpreters) {

			SocialActivityInterpreterImpl socialActivityInterpreterImpl =
				(SocialActivityInterpreterImpl)activityInterpreter;

			if (socialActivityInterpreterImpl.hasClassName(className)) {
				SocialActivityFeedEntry activityFeedEntry =
					socialActivityInterpreterImpl.interpret(
						activitySet, serviceContext);

				if (activityFeedEntry != null) {
					activityFeedEntry.setPortletId(
						socialActivityInterpreterImpl.getPortletId());

					return activityFeedEntry;
				}
			}
		}

		return null;
	}

	@Override
	public void updateActivitySet(long activityId) throws PortalException {
		if (!PropsValues.SOCIAL_ACTIVITY_SETS_BUNDLING_ENABLED) {
			_socialActivitySetLocalService.addActivitySet(activityId);

			return;
		}

		List<SocialActivityInterpreter> activityInterpreters =
			_serviceTrackerMap.getService(
				PropsValues.SOCIAL_ACTIVITY_SETS_SELECTOR);

		if (activityInterpreters != null) {
			SocialActivity activity =
				_socialActivityPersistence.findByPrimaryKey(activityId);

			String className = PortalUtil.getClassName(
				activity.getClassNameId());

			for (SocialActivityInterpreter activityInterpreter :
					activityInterpreters) {

				SocialActivityInterpreterImpl socialActivityInterpreterImpl =
					(SocialActivityInterpreterImpl)activityInterpreter;

				if (socialActivityInterpreterImpl.hasClassName(className)) {
					socialActivityInterpreterImpl.updateActivitySet(activityId);

					return;
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SocialActivityInterpreterLocalServiceImpl.class);

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private ServiceTrackerMap<String, List<SocialActivityInterpreter>>
		_serviceTrackerMap;

	@BeanReference(type = SocialActivityLocalService.class)
	private SocialActivityLocalService _socialActivityLocalService;

	@BeanReference(type = SocialActivityPersistence.class)
	private SocialActivityPersistence _socialActivityPersistence;

	@BeanReference(type = SocialActivitySetLocalService.class)
	private SocialActivitySetLocalService _socialActivitySetLocalService;

}
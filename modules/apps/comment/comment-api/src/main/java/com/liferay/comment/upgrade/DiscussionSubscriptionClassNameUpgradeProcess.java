/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.upgrade;

import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.sql.Connection;

/**
 * @author Roberto Díaz
 */
public class DiscussionSubscriptionClassNameUpgradeProcess
	extends UpgradeProcess {

	public DiscussionSubscriptionClassNameUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		SubscriptionLocalService subscriptionLocalService,
		String oldSubscriptionClassName, DeletionMode deletionMode) {

		this(
			classNameLocalService, subscriptionLocalService,
			oldSubscriptionClassName, deletionMode, null);
	}

	public DiscussionSubscriptionClassNameUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		SubscriptionLocalService subscriptionLocalService,
		String oldSubscriptionClassName,
		UnsafeBiFunction<String, Connection, Boolean, Exception>
			unsafeBiFunction) {

		this(
			classNameLocalService, subscriptionLocalService,
			oldSubscriptionClassName, null, unsafeBiFunction);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public DiscussionSubscriptionClassNameUpgradeProcess(
		SubscriptionLocalService subscriptionLocalService,
		String oldSubscriptionClassName, DeletionMode deletionMode) {

		this(
			ClassNameLocalServiceUtil.getService(), subscriptionLocalService,
			oldSubscriptionClassName, deletionMode);
	}

	public enum DeletionMode {

		ADD_NEW, DELETE_OLD, UPDATE

	}

	@Override
	protected void doUpgrade() throws Exception {
		if (_unsafeBiFunction != null) {
			_unsafeBiFunction.apply(_oldSubscriptionClassName, connection);
		}
		else if (_deletionMode == DeletionMode.ADD_NEW) {
			_addSubscriptions();
		}
		else if (_deletionMode == DeletionMode.DELETE_OLD) {
			_deleteSubscriptions();
		}
		else {
			_updateSubscriptions();
		}
	}

	private DiscussionSubscriptionClassNameUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		SubscriptionLocalService subscriptionLocalService,
		String oldSubscriptionClassName, DeletionMode deletionMode,
		UnsafeBiFunction<String, Connection, Boolean, Exception>
			unsafeBiFunction) {

		_classNameLocalService = classNameLocalService;
		_subscriptionLocalService = subscriptionLocalService;
		_oldSubscriptionClassName = oldSubscriptionClassName;
		_deletionMode = deletionMode;
		_unsafeBiFunction = unsafeBiFunction;
	}

	private void _addSubscriptions() throws Exception {
		String newSubscriptionClassName =
			MBDiscussion.class.getName() + StringPool.UNDERLINE +
				_oldSubscriptionClassName;

		ActionableDynamicQuery actionableDynamicQuery =
			_subscriptionLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"classNameId",
					_classNameLocalService.getClassNameId(
						_oldSubscriptionClassName))));
		actionableDynamicQuery.setPerformActionMethod(
			(Subscription subscription) ->
				_subscriptionLocalService.addSubscription(
					subscription.getUserId(), subscription.getGroupId(),
					newSubscriptionClassName, subscription.getClassPK()));

		actionableDynamicQuery.performActions();
	}

	private void _deleteSubscriptions() throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_subscriptionLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"classNameId",
					_classNameLocalService.getClassNameId(
						_oldSubscriptionClassName))));
		actionableDynamicQuery.setPerformActionMethod(
			(Subscription subscription) ->
				_subscriptionLocalService.deleteSubscription(
					subscription.getSubscriptionId()));

		actionableDynamicQuery.performActions();
	}

	private void _updateSubscriptions() throws Exception {
		String newSubscriptionClassName =
			MBDiscussion.class.getName() + StringPool.UNDERLINE +
				_oldSubscriptionClassName;

		runSQL(
			StringBundler.concat(
				"update Subscription set classNameId = ",
				ClassNameLocalServiceUtil.getClassNameId(
					newSubscriptionClassName),
				" where classNameId = ",
				ClassNameLocalServiceUtil.getClassNameId(
					_oldSubscriptionClassName)));
	}

	private final ClassNameLocalService _classNameLocalService;
	private final DeletionMode _deletionMode;
	private final String _oldSubscriptionClassName;
	private final SubscriptionLocalService _subscriptionLocalService;
	private final UnsafeBiFunction<String, Connection, Boolean, Exception>
		_unsafeBiFunction;

}
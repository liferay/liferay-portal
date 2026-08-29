/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class UserGroupModelListenerTest extends BaseModelListenerTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_userGroup = UserGroupTestUtil.addUserGroup();
	}

	@Test
	public void testOnAfterAddAssociation() throws Exception {
		_userGroupLocalService.setGroupUserGroups(
			cmpProjectObjectEntry.getGroupId(),
			new long[] {_userGroup.getUserGroupId()});

		assertAuditMessage("CMP_ADD_MEMBER");
	}

	@Test
	public void testOnAfterRemoveAssociation() throws Exception {
		_userGroupLocalService.setGroupUserGroups(
			cmpProjectObjectEntry.getGroupId(),
			new long[] {_userGroup.getUserGroupId()});

		assertAuditMessage("CMP_ADD_MEMBER");

		_userGroupLocalService.unsetGroupUserGroups(
			cmpProjectObjectEntry.getGroupId(),
			new long[] {_userGroup.getUserGroupId()});

		assertAuditMessage("CMP_REMOVE_MEMBER");
	}

	@Override
	protected ModelListener<UserGroup> getModelListener() {
		return _userGroupModelListener;
	}

	private UserGroup _userGroup;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.model.listener.UserGroupModelListener"
	)
	private ModelListener<UserGroup> _userGroupModelListener;

}
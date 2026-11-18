/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalService;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CSDiagramEntryServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testDeleteCSDiagramEntry() throws Exception {
		CommerceCatalog commerceCatalog1 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog1.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog1.getCommerceCatalogId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		CSDiagramEntry csDiagramEntry1 = _addCSDiagramEntry(
			commerceCatalog1.getGroupId());

		CommerceCatalog commerceCatalog2 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		CSDiagramEntry csDiagramEntry2 = _addCSDiagramEntry(
			commerceCatalog2.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			PrincipalException.MustHavePermission principalException =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> _csDiagramEntryService.deleteCSDiagramEntry(
						_createCSDiagramEntry(
							csDiagramEntry1.getCPDefinitionId(),
							csDiagramEntry2.getCSDiagramEntryId())));

			Assert.assertEquals(
				commerceCatalog2.getCommerceCatalogId(),
				principalException.resourceId);

			Assert.assertNotNull(
				_csDiagramEntryLocalService.fetchCSDiagramEntry(
					csDiagramEntry2.getCSDiagramEntryId()));

			_csDiagramEntryService.deleteCSDiagramEntry(csDiagramEntry1);

			Assert.assertNull(
				_csDiagramEntryLocalService.fetchCSDiagramEntry(
					csDiagramEntry1.getCSDiagramEntryId()));
		}
	}

	private CSDiagramEntry _addCSDiagramEntry(long groupId) throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(groupId);

		return _csDiagramEntryLocalService.addCSDiagramEntry(
			TestPropsValues.getUserId(), cpDefinition.getCPDefinitionId(), 0,
			cpDefinition.getCProductId(), false, RandomTestUtil.randomInt(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private CSDiagramEntry _createCSDiagramEntry(
		long cpDefinitionId, long csDiagramEntryId) {

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryLocalService.createCSDiagramEntry(csDiagramEntryId);

		csDiagramEntry.setCPDefinitionId(cpDefinitionId);

		return csDiagramEntry;
	}

	@Inject
	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

	@Inject
	private CSDiagramEntryService _csDiagramEntryService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
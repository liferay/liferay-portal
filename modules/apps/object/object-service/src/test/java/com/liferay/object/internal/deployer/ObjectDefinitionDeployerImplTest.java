/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.deployer;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectLayoutTabLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.sharing.security.permission.resource.SharingModelResourcePermissionConfigurator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Jhosseph Gonzalez
 */
public class ObjectDefinitionDeployerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDeployReadsSettingWithEdgeObjectRelationship() {

		// ObjectDefinitionDeployerImpl.deploy reads the
		// rootObjectDefinitionIds setting table whenever the company has at
		// least one edge object relationship

		ObjectRelationship objectRelationship = Mockito.mock(
			ObjectRelationship.class);

		Mockito.when(
			objectRelationship.isEdge()
		).thenReturn(
			true
		);

		ObjectDefinitionSettingLocalService
			objectDefinitionSettingLocalService = Mockito.mock(
				ObjectDefinitionSettingLocalService.class);

		ObjectDefinitionDeployerImpl objectDefinitionDeployerImpl =
			_createObjectDefinitionDeployerImpl(
				objectDefinitionSettingLocalService,
				Collections.singletonMap(
					RandomTestUtil.randomLong(), List.of(objectRelationship)));

		objectDefinitionDeployerImpl.deploy(
			RandomTestUtil.randomLong(), Collections.emptyList());

		Mockito.verify(
			objectDefinitionSettingLocalService
		).getObjectDefinitionSettingsMap(
			Mockito.anyLong(), Mockito.anyString()
		);
	}

	@Test
	public void testDeploySkipsSettingReadWithoutEdgeObjectRelationship() {

		// ObjectDefinitionDeployerImpl.deploy skips the
		// rootObjectDefinitionIds setting table read entirely when the
		// company has no edge object relationship (LPD-X). That
		// optimization is only correct because the setting is otherwise
		// written exclusively through ObjectDefinitionTreeUtil's bind and
		// unbind paths, both gated on an edge relationship. If this
		// assertion ever needs to change to expect the setting table to be
		// read here, that is a signal the invariant backing the
		// optimization has changed and _hasEdgeObjectRelationship needs to
		// be revisited alongside it

		ObjectRelationship objectRelationship = Mockito.mock(
			ObjectRelationship.class);

		Mockito.when(
			objectRelationship.isEdge()
		).thenReturn(
			false
		);

		ObjectDefinitionSettingLocalService
			objectDefinitionSettingLocalService = Mockito.mock(
				ObjectDefinitionSettingLocalService.class);

		ObjectDefinitionDeployerImpl objectDefinitionDeployerImpl =
			_createObjectDefinitionDeployerImpl(
				objectDefinitionSettingLocalService,
				Collections.singletonMap(
					RandomTestUtil.randomLong(), List.of(objectRelationship)));

		objectDefinitionDeployerImpl.deploy(
			RandomTestUtil.randomLong(), Collections.emptyList());

		Mockito.verify(
			objectDefinitionSettingLocalService, Mockito.never()
		).getObjectDefinitionSettingsMap(
			Mockito.anyLong(), Mockito.anyString()
		);
	}

	private ObjectDefinitionDeployerImpl _createObjectDefinitionDeployerImpl(
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		Map<Long, List<ObjectRelationship>> objectRelationshipsMap) {

		ObjectRelationshipLocalService objectRelationshipLocalService =
			Mockito.mock(ObjectRelationshipLocalService.class);

		Mockito.when(
			objectRelationshipLocalService.getObjectRelationshipsMap(
				Mockito.anyLong())
		).thenReturn(
			objectRelationshipsMap
		);

		return new ObjectDefinitionDeployerImpl(
			Mockito.mock(AccountEntryLocalService.class),
			Mockito.mock(AccountEntryOrganizationRelLocalService.class),
			Mockito.mock(AssetEntryLocalService.class),
			Mockito.mock(BundleContext.class),
			Mockito.mock(DepotEntryGroupRelLocalService.class),
			Mockito.mock(DepotEntryLocalService.class),
			Mockito.mock(DLFileEntryLocalService.class),
			Mockito.mock(GroupLocalService.class),
			Mockito.mock(KaleoDefinitionLocalService.class),
			Mockito.mock(ListTypeLocalService.class),
			Mockito.mock(ObjectActionLocalService.class),
			Mockito.mock(ObjectDefinitionLocalService.class),
			objectDefinitionSettingLocalService,
			Mockito.mock(ObjectEntryFolderLocalService.class),
			Mockito.mock(ObjectEntryLocalService.class),
			Mockito.mock(ObjectEntryService.class),
			Mockito.mock(ObjectFieldBusinessTypeRegistry.class),
			Mockito.mock(ObjectFieldLocalService.class),
			Mockito.mock(ObjectFolderLocalService.class),
			Mockito.mock(ObjectLayoutLocalService.class),
			Mockito.mock(ObjectLayoutTabLocalService.class),
			objectRelationshipLocalService,
			Mockito.mock(ObjectScopeProviderRegistry.class),
			Mockito.mock(ObjectViewLocalService.class),
			Mockito.mock(OrganizationLocalService.class),
			Mockito.mock(Portal.class), Mockito.mock(PortletLocalService.class),
			Mockito.mock(ResourceActions.class),
			Mockito.mock(UserLocalService.class),
			Mockito.mock(ResourcePermissionLocalService.class),
			Mockito.mock(SearchLocalizationHelper.class),
			Mockito.mock(SharingModelResourcePermissionConfigurator.class),
			Mockito.mock(SystemEventLocalService.class),
			Mockito.mock(TextEmbeddingDocumentContributor.class),
			Mockito.mock(WorkflowDefinitionLinkLocalService.class),
			Mockito.mock(ModelPreFilterContributor.class),
			Mockito.mock(UserGroupRoleLocalService.class));
	}

}
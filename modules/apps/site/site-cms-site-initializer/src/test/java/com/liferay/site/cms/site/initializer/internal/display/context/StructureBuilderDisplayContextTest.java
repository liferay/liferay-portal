/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Stefano Motta
 */
public class StructureBuilderDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetBaseObjectDefinition() throws Exception {
		String objectFolderExternalReferenceCode = StringUtil.randomString();

		Mockito.when(
			_cmsStructureObjectFolderContributor.
				getObjectFolderExternalReferenceCode()
		).thenReturn(
			objectFolderExternalReferenceCode
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			new ThemeDisplay() {
				{
					setUser(Mockito.mock(User.class));
				}
			});
		mockHttpServletRequest.setParameter(
			"objectFolderExternalReferenceCode",
			objectFolderExternalReferenceCode);

		Mockito.when(
			_builder.user(Mockito.any(User.class))
		).thenReturn(
			_builder
		);

		Mockito.when(
			_builder.build()
		).thenReturn(
			_objectDefinitionResource
		);

		Mockito.when(
			_objectDefinitionResourceFactory.create()
		).thenReturn(
			_builder
		);

		StructureBuilderDisplayContext structureBuilderDisplayContext =
			new StructureBuilderDisplayContext(
				List.of(_cmsStructureObjectFolderContributor),
				mockHttpServletRequest, null, _objectDefinitionResourceFactory,
				null);

		Assert.assertNull(
			ReflectionTestUtil.invoke(
				structureBuilderDisplayContext, "_getBaseObjectDefinition",
				new Class<?>[0]));

		String baseObjectDefinitionExternalReferenceCode =
			StringUtil.randomString();

		Mockito.when(
			_cmsStructureObjectFolderContributor.
				getBaseObjectDefinitionExternalReferenceCode()
		).thenReturn(
			baseObjectDefinitionExternalReferenceCode
		);

		Mockito.when(
			_objectDefinitionResource.getObjectDefinitionsPage(
				Mockito.isNull(), Mockito.isNull(), Mockito.any(),
				Mockito.isNull(), Mockito.isNull())
		).thenReturn(
			Page.of(Collections.emptyList())
		);

		Assert.assertNull(
			ReflectionTestUtil.invoke(
				structureBuilderDisplayContext, "_getBaseObjectDefinition",
				new Class<?>[0]));

		ObjectDefinition objectDefinition = new ObjectDefinition();

		objectDefinition.setExternalReferenceCode(
			baseObjectDefinitionExternalReferenceCode);

		Mockito.when(
			_objectDefinitionResource.getObjectDefinitionsPage(
				Mockito.isNull(), Mockito.isNull(), Mockito.any(),
				Mockito.isNull(), Mockito.isNull())
		).thenReturn(
			Page.of(Collections.singletonList(objectDefinition))
		);

		Assert.assertSame(
			objectDefinition,
			ReflectionTestUtil.invoke(
				structureBuilderDisplayContext, "_getBaseObjectDefinition",
				new Class<?>[0]));
	}

	private final ObjectDefinitionResource.Builder _builder = Mockito.mock(
		ObjectDefinitionResource.Builder.class);
	private final CMSStructureObjectFolderContributor
		_cmsStructureObjectFolderContributor = Mockito.mock(
			CMSStructureObjectFolderContributor.class);
	private final ObjectDefinitionResource _objectDefinitionResource =
		Mockito.mock(ObjectDefinitionResource.class);
	private final ObjectDefinitionResource.Factory
		_objectDefinitionResourceFactory = Mockito.mock(
			ObjectDefinitionResource.Factory.class);

}

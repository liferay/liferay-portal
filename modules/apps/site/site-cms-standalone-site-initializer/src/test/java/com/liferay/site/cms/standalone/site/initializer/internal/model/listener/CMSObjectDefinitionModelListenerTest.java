/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.model.listener;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.exception.ObjectDefinitionObjectFolderIdException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class CMSObjectDefinitionModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeCreateInCMSFoldersWithDepotScope() {
		_testOnBeforeCreateInCMSFoldersWithDepotScope(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);
		_testOnBeforeCreateInCMSFoldersWithDepotScope(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES);
		_testOnBeforeCreateInCMSFoldersWithDepotScope(
			ObjectFolderConstants.
				EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS);
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeCreateOfEmptyObjectDefinitionInDefaultFolder() {
		_cmsObjectDefinitionModelListener.onBeforeCreate(
			_mockObjectDefinition(
				RandomTestUtil.randomString(), 1,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_EMPTY, false));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeCreateOfEmptyObjectDefinitionWhenScopeIsNotDepot() {
		_assertScopeFailure(
			() -> _cmsObjectDefinitionModelListener.onBeforeCreate(
				_mockObjectDefinition(
					RandomTestUtil.randomString(), 1,
					ObjectDefinitionConstants.SCOPE_SITE,
					WorkflowConstants.STATUS_EMPTY, false)));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeCreateOfSystemObjectDefinition() {

		// System flag

		_cmsObjectDefinitionModelListener.onBeforeCreate(
			_mockObjectDefinition(
				RandomTestUtil.randomString(), 1,
				ObjectDefinitionConstants.SCOPE_SITE,
				WorkflowConstants.STATUS_APPROVED, true));

		// External reference code prefix

		ObjectDefinition objectDefinition = _mockObjectDefinition(
			RandomTestUtil.randomString(), 1,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED, false);

		Mockito.when(
			objectDefinition.getExternalReferenceCode()
		).thenReturn(
			ObjectDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_OBJECT_DEFINITION +
					RandomTestUtil.randomString()
		);

		_cmsObjectDefinitionModelListener.onBeforeCreate(objectDefinition);
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeCreateWhenFolderIsNotCMS() {
		_assertObjectFolderFailure(
			() -> _cmsObjectDefinitionModelListener.onBeforeCreate(
				_mockObjectDefinition(
					RandomTestUtil.randomString(), 1,
					ObjectDefinitionConstants.SCOPE_DEPOT,
					WorkflowConstants.STATUS_DRAFT, false)));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeCreateWhenScopeIsNotDepot() {
		_assertScopeFailure(
			() -> _cmsObjectDefinitionModelListener.onBeforeCreate(
				_mockObjectDefinition(
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
					1, ObjectDefinitionConstants.SCOPE_SITE,
					WorkflowConstants.STATUS_DRAFT, false)));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeUpdateFillingEmptyObjectDefinitionInCMSFolder() {
		_cmsObjectDefinitionModelListener.onBeforeUpdate(
			_mockObjectDefinition(
				RandomTestUtil.randomString(), 1,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_EMPTY, false),
			_mockObjectDefinition(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES, 2,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_DRAFT, false));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeUpdateFillingEmptyObjectDefinitionInDefaultFolder() {
		String objectFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		_assertObjectFolderFailure(
			() -> _cmsObjectDefinitionModelListener.onBeforeUpdate(
				_mockObjectDefinition(
					objectFolderExternalReferenceCode, 1,
					ObjectDefinitionConstants.SCOPE_DEPOT,
					WorkflowConstants.STATUS_EMPTY, false),
				_mockObjectDefinition(
					objectFolderExternalReferenceCode, 1,
					ObjectDefinitionConstants.SCOPE_DEPOT,
					WorkflowConstants.STATUS_DRAFT, false)));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeUpdateKeepingEmptyObjectDefinitionInDefaultFolder() {
		String objectFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		_cmsObjectDefinitionModelListener.onBeforeUpdate(
			_mockObjectDefinition(
				objectFolderExternalReferenceCode, 1,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_EMPTY, false),
			_mockObjectDefinition(
				objectFolderExternalReferenceCode, 1,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_EMPTY, false));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeUpdateMovingToCMSFolders() {
		_testOnBeforeUpdateMovingToCMSFolders(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);
		_testOnBeforeUpdateMovingToCMSFolders(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES);
		_testOnBeforeUpdateMovingToCMSFolders(
			ObjectFolderConstants.
				EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS);
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeUpdateOfSystemObjectDefinition() {
		_cmsObjectDefinitionModelListener.onBeforeUpdate(
			_mockObjectDefinition(
				RandomTestUtil.randomString(), 1,
				ObjectDefinitionConstants.SCOPE_SITE,
				WorkflowConstants.STATUS_APPROVED, true),
			_mockObjectDefinition(
				RandomTestUtil.randomString(), 2,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				WorkflowConstants.STATUS_APPROVED, true));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeUpdateWhenFolderAndScopeAreUnchanged() {
		String objectFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		_cmsObjectDefinitionModelListener.onBeforeUpdate(
			_mockObjectDefinition(
				objectFolderExternalReferenceCode, 1,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_DRAFT, false),
			_mockObjectDefinition(
				objectFolderExternalReferenceCode, 1,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_APPROVED, false));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeUpdateWhenFolderIsNotCMS() {
		_assertObjectFolderFailure(
			() -> _cmsObjectDefinitionModelListener.onBeforeUpdate(
				_mockObjectDefinition(
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
					1, ObjectDefinitionConstants.SCOPE_DEPOT,
					WorkflowConstants.STATUS_APPROVED, false),
				_mockObjectDefinition(
					RandomTestUtil.randomString(), 2,
					ObjectDefinitionConstants.SCOPE_DEPOT,
					WorkflowConstants.STATUS_APPROVED, false)));
	}

	@Test
	@TestInfo("LPD-110251")
	public void testOnBeforeUpdateWhenScopeIsNotDepot() {
		_assertScopeFailure(
			() -> _cmsObjectDefinitionModelListener.onBeforeUpdate(
				_mockObjectDefinition(
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS,
					1, ObjectDefinitionConstants.SCOPE_DEPOT,
					WorkflowConstants.STATUS_APPROVED, false),
				_mockObjectDefinition(
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS,
					1, ObjectDefinitionConstants.SCOPE_COMPANY,
					WorkflowConstants.STATUS_APPROVED, false)));
	}

	private void _assertObjectFolderFailure(Runnable runnable) {
		AssertUtils.assertFailure(
			ModelListenerException.class,
			ObjectDefinitionObjectFolderIdException.class.getName() +
				": Custom object definitions must be in a CMS object folder",
			runnable::run);
	}

	private void _assertScopeFailure(Runnable runnable) {
		AssertUtils.assertFailure(
			ModelListenerException.class,
			ObjectDefinitionScopeException.class.getName() +
				": Custom object definitions must have scope \"depot\"",
			runnable::run);
	}

	private ObjectDefinition _mockObjectDefinition(
		String objectFolderExternalReferenceCode, long objectFolderId,
		String scope, int status, boolean system) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			objectDefinition.getObjectFolderExternalReferenceCode()
		).thenReturn(
			objectFolderExternalReferenceCode
		);

		Mockito.when(
			objectDefinition.getObjectFolderId()
		).thenReturn(
			objectFolderId
		);

		Mockito.when(
			objectDefinition.getScope()
		).thenReturn(
			scope
		);

		Mockito.when(
			objectDefinition.getStatus()
		).thenReturn(
			status
		);

		Mockito.when(
			objectDefinition.isSystem()
		).thenReturn(
			system
		);

		return objectDefinition;
	}

	private void _testOnBeforeCreateInCMSFoldersWithDepotScope(
		String objectFolderExternalReferenceCode) {

		_cmsObjectDefinitionModelListener.onBeforeCreate(
			_mockObjectDefinition(
				objectFolderExternalReferenceCode, 1,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_DRAFT, false));
	}

	private void _testOnBeforeUpdateMovingToCMSFolders(
		String objectFolderExternalReferenceCode) {

		_cmsObjectDefinitionModelListener.onBeforeUpdate(
			_mockObjectDefinition(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES, 1,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_APPROVED, false),
			_mockObjectDefinition(
				objectFolderExternalReferenceCode, 2,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_APPROVED, false));
	}

	private final CMSObjectDefinitionModelListener
		_cmsObjectDefinitionModelListener =
			new CMSObjectDefinitionModelListener();

}
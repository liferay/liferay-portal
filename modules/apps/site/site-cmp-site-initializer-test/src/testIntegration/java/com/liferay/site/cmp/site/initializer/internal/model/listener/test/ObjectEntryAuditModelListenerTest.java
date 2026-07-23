/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.io.Serializable;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Stefano Motta
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class ObjectEntryAuditModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_auditRouter = (AuditRouter)ReflectionTestUtil.getAndSetFieldValue(
			_objectEntryModelListener, "_auditRouter",
			ProxyUtil.newProxyInstance(
				AuditRouter.class.getClassLoader(),
				new Class<?>[] {AuditRouter.class},
				(proxy, method, arguments) -> {
					_auditMessages.add((AuditMessage)arguments[0]);

					return null;
				}));

		CMPTestUtil.getOrAddGroup(ObjectEntryAuditModelListenerTest.class);

		_cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());
	}

	@After
	public void tearDown() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_objectEntryModelListener, "_auditRouter", _auditRouter);

		_objectEntryLocalService.deleteObjectEntry(_cmpTaskObjectEntry);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		String title = RandomTestUtil.randomString();

		_addCMPTaskLinkObjectEntry(_addCMSBasicWebContentObjectEntry(title));

		_assertAuditMessage("CMP_ADD_ASSET", title);
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		String title = RandomTestUtil.randomString();

		ObjectEntry cmpTaskLinkObjectEntry = _addCMPTaskLinkObjectEntry(
			_addCMSBasicWebContentObjectEntry(title));

		_assertAuditMessage("CMP_ADD_ASSET", title);

		_objectEntryLocalService.deleteObjectEntry(cmpTaskLinkObjectEntry);

		_assertAuditMessage("CMP_REMOVE_ASSET", title);
	}

	private ObjectEntry _addCMPTaskLinkObjectEntry(
			ObjectEntry linkedObjectEntry)
		throws Exception {

		ObjectDefinition cmpTaskLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK_LINK", _cmpTaskObjectEntry.getCompanyId());

		Group group = _groupLocalService.getGroup(
			linkedObjectEntry.getGroupId());

		return _objectEntryLocalService.addObjectEntry(
			_cmpTaskObjectEntry.getGroupId(), _cmpTaskObjectEntry.getUserId(),
			cmpTaskLinkObjectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"classExternalReferenceCode",
				linkedObjectEntry.getExternalReferenceCode()
			).put(
				"className", linkedObjectEntry.getModelClassName()
			).put(
				"groupExternalReferenceCode", group.getExternalReferenceCode()
			).put(
				"r_cmpTaskToCMPTaskLinks_c_cmpTaskId",
				_cmpTaskObjectEntry.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addCMSBasicWebContentObjectEntry(String title)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", _depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), _depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, "en_US",
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", title
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertAuditMessage(
			String expectedEventType, String expectedTitle)
		throws Exception {

		AuditMessage auditMessage = _auditMessages.poll();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"attributes",
				JSONUtil.putAll(JSONUtil.put("name", expectedTitle))
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT_ORDER);
		Assert.assertEquals(
			_cmpTaskObjectEntry.getModelClassName(),
			auditMessage.getClassName());
		Assert.assertEquals(
			_cmpTaskObjectEntry.getObjectEntryId(),
			GetterUtil.getLong(auditMessage.getClassPK()));
		Assert.assertEquals(expectedEventType, auditMessage.getEventType());

		_auditMessages.clear();
	}

	private final Queue<AuditMessage> _auditMessages = new LinkedList<>();
	private AuditRouter _auditRouter;
	private ObjectEntry _cmpTaskObjectEntry;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.model.listener.ObjectEntryModelListener"
	)
	private ModelListener<ObjectEntry> _objectEntryModelListener;

}
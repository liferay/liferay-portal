/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.List;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
public class AuditMessageBuilder {

	public static AuditMessage buildAuditMessage(
		ClassedModel classedModel, String eventType,
		List<Attribute> attributes) {

		long groupId = 0;

		if (classedModel instanceof GroupedModel) {
			GroupedModel groupedModel = (GroupedModel)classedModel;

			groupId = groupedModel.getGroupId();
		}

		return buildAuditMessage(
			groupId, 0, classedModel.getModelClassName(),
			String.valueOf(classedModel.getPrimaryKeyObj()), null, eventType,
			attributes);
	}

	public static AuditMessage buildAuditMessage(
		long groupId, long accountEntryId, String className, long classPK,
		String contextName, String eventType, List<Attribute> attributes) {

		return buildAuditMessage(
			groupId, accountEntryId, className, String.valueOf(classPK),
			contextName, eventType, attributes);
	}

	public static AuditMessage buildAuditMessage(
		long groupId, long accountEntryId, String className, String classPK,
		String contextName, String eventType, List<Attribute> attributes) {

		return buildAuditMessage(
			groupId, accountEntryId, null, className, classPK, contextName,
			eventType, null, attributes);
	}

	public static AuditMessage buildAuditMessage(
		long groupId, long accountEntryId, String action, String className,
		String classPK, String contextName, String eventType,
		String resourceType, List<Attribute> attributes) {

		long companyId = CompanyThreadLocal.getCompanyId();

		long userId = 0;

		if (PrincipalThreadLocal.getName() != null) {
			userId = GetterUtil.getLong(PrincipalThreadLocal.getName());
		}

		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		long realUserId = auditRequestContext.getRealUserId();

		String realUserName = PortalUtil.getUserName(
			realUserId, StringPool.BLANK);

		JSONObject additionalInfoJSONObject =
			JSONFactoryUtil.createJSONObject();

		if ((realUserId > 0) && (userId != realUserId)) {
			additionalInfoJSONObject.put(
				"doAsUserEmailAddress", PortalUtil.getUserEmailAddress(userId)
			).put(
				"doAsUserId", String.valueOf(userId)
			).put(
				"doAsUserName", PortalUtil.getUserName(userId, StringPool.BLANK)
			);
		}

		if (attributes != null) {
			additionalInfoJSONObject.put(
				"attributes", _getAttributesJSONArray(attributes));
		}

		return new AuditMessage(
			groupId, companyId, realUserId, realUserName, null, accountEntryId,
			action, additionalInfoJSONObject, className, classPK, contextName,
			eventType, null, resourceType);
	}

	public static AuditMessage buildAuditMessage(
		long accountEntryId, String className, long classPK, String contextName,
		String eventType, List<Attribute> attributes) {

		return buildAuditMessage(
			0, accountEntryId, className, classPK, contextName, eventType,
			attributes);
	}

	public static AuditMessage buildAuditMessage(
		String className, long classPK, String eventType,
		List<Attribute> attributes) {

		return buildAuditMessage(
			0, 0, className, classPK, null, eventType, attributes);
	}

	public static AuditMessage buildAuditMessage(
		String action, String className, long classPK, String eventType,
		String resourceType, List<Attribute> attributes) {

		return buildAuditMessage(
			0, 0, action, className, String.valueOf(classPK), null, eventType,
			resourceType, attributes);
	}

	private static JSONArray _getAttributesJSONArray(
		List<Attribute> attributes) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Attribute attribute : attributes) {
			jsonArray.put(
				JSONUtil.put(
					"name", attribute.getName()
				).put(
					"newValue", attribute.getNewValue()
				).put(
					"oldValue", attribute.getOldValue()
				));
		}

		return jsonArray;
	}

}
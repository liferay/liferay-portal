/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.fasterxml.jackson.core.type.TypeReference;

import com.liferay.osb.faro.contacts.model.ContactsCardTemplate;
import com.liferay.osb.faro.contacts.model.constants.ContactsCardTemplateConstants;
import com.liferay.osb.faro.contacts.service.ContactsCardTemplateLocalService;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.card.template.type.ActivityHistoryContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.AssociatedSegmentsContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.ClosedWonContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.ContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.ConversionHealthContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.CoworkersContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.EmployeesContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.InterestContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.LifetimeValueContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.NetSalesContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.ProfileContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.RecentActivitiesContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.SegmentDistributionContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.SegmentMembershipContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.SimilarContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.card.template.type.TouchpointAttributionContactsCardTemplateType;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.model.display.contacts.card.template.ContactsCardTemplateDisplay;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.lang.reflect.Constructor;

import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Matthew Kong
 */
@Component(service = ContactsCardTemplateManagerHelper.class)
public class ContactsCardTemplateManagerHelper {

	public ContactsCardTemplateDisplay getContactsCardTemplateDisplay(
		ContactsCardTemplate contactsCardTemplate) {

		return getContactsCardTemplateDisplay(contactsCardTemplate, 0);
	}

	public ContactsCardTemplateDisplay getContactsCardTemplateDisplay(
		ContactsCardTemplate contactsCardTemplate, int size) {

		try {
			ContactsCardTemplateType contactsCardTemplateType =
				_contactsCardTemplateTypes.get(contactsCardTemplate.getType());

			Map<String, Object> settings = JSONUtil.readValue(
				contactsCardTemplate.getSettings(),
				new TypeReference<Map<String, Object>>() {
				});

			setSettings(
				contactsCardTemplate, settings,
				contactsCardTemplateType.getDefaultSettings());

			return getContactsCardTemplateDisplay(
				contactsCardTemplate, contactsCardTemplateType, size);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	public ContactsCardTemplateDisplay getContactsCardTemplateDisplay(
			long groupId, Map<String, Object> settings, int type, int size)
		throws Exception {

		ContactsCardTemplate contactsCardTemplate =
			_contactsCardTemplateLocalService.createContactsCardTemplate(0);

		contactsCardTemplate.setGroupId(groupId);

		ContactsCardTemplateType contactsCardTemplateType =
			_contactsCardTemplateTypes.get(type);

		setSettings(
			contactsCardTemplate, settings,
			contactsCardTemplateType.getDefaultSettings());

		contactsCardTemplate.setType(type);

		return getContactsCardTemplateDisplay(
			contactsCardTemplate, contactsCardTemplateType, size);
	}

	public Collection<ContactsCardTemplateType> getContactsCardTemplateTypes() {
		return _contactsCardTemplateTypes.values();
	}

	public int[][] getDefaultContactsCardTemplateTypes(
		int contactsLayoutTemplateType) {

		return _defaultContactsCardTemplateTypes.get(
			contactsLayoutTemplateType);
	}

	public int[] getDefaultHeaderContactsCardTemplateTypes(
		int contactsLayoutTemplateType) {

		return _defaultHeaderContactsCardTemplateTypes.get(
			contactsLayoutTemplateType);
	}

	public String getDefaultName(int type) {
		ContactsCardTemplateType contactsCardTemplateType =
			_contactsCardTemplateTypes.get(type);

		return contactsCardTemplateType.getDefaultName();
	}

	public String getDefaultSettings(int type) {
		ContactsCardTemplateType contactsCardTemplateType =
			_contactsCardTemplateTypes.get(type);

		try {
			return JSONUtil.writeValueAsString(
				contactsCardTemplateType.getDefaultSettings());
		}
		catch (Exception exception) {
			_log.error(exception);

			return StringPool.BLANK;
		}
	}

	protected ContactsCardTemplateDisplay getContactsCardTemplateDisplay(
			ContactsCardTemplate contactsCardTemplate,
			ContactsCardTemplateType contactsCardTemplateType, int size)
		throws Exception {

		Class<? extends ContactsCardTemplateDisplay> clazz =
			contactsCardTemplateType.getDisplayClass();

		Constructor<? extends ContactsCardTemplateDisplay> constructor =
			clazz.getConstructor(
				FaroProject.class, ContactsCardTemplate.class, int.class,
				ContactsEngineClient.class);

		return constructor.newInstance(
			_faroProjectLocalService.getFaroProjectByGroupId(
				contactsCardTemplate.getGroupId()),
			contactsCardTemplate, size, _contactsEngineClient);
	}

	protected void removeContactsCardTemplateType(
		ContactsCardTemplateType contactsCardTemplateType) {

		_contactsCardTemplateTypes.remove(contactsCardTemplateType.getType());
	}

	protected void setSettings(
			ContactsCardTemplate contactsCardTemplate,
			Map<String, Object> settings, Map<String, Object> defaultSettings)
		throws Exception {

		for (Map.Entry<String, Object> entry : defaultSettings.entrySet()) {
			settings.putIfAbsent(entry.getKey(), entry.getValue());
		}

		contactsCardTemplate.setSettings(JSONUtil.writeValueAsString(settings));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContactsCardTemplateManagerHelper.class);

	private static final Map<Integer, ContactsCardTemplateType>
		_contactsCardTemplateTypes =
			HashMapBuilder.<Integer, ContactsCardTemplateType>put(
				ContactsCardTemplateConstants.TYPE_ACTIVITY_HISTORY,
				new ActivityHistoryContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_ASSOCIATED_SEGMENTS,
				new AssociatedSegmentsContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_CLOSED_WON,
				new ClosedWonContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_CONVERSION_HEALTH,
				new ConversionHealthContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_COWORKERS,
				new CoworkersContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_EMPLOYEES,
				new EmployeesContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_INTEREST,
				new InterestContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_LIFETIME_VALUE,
				new LifetimeValueContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_NET_SALES,
				new NetSalesContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_PROFILE,
				new ProfileContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_RECENT_ACTIVITIES,
				new RecentActivitiesContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_SEGMENT_DISTRIBUTION,
				new SegmentDistributionContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_SEGMENT_MEMBERSHIP,
				new SegmentMembershipContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_SIMILAR,
				new SimilarContactsCardTemplateType()
			).put(
				ContactsCardTemplateConstants.TYPE_TOUCHPOINT_ATTRIBUTION,
				new TouchpointAttributionContactsCardTemplateType()
			).build();
	private static final Map<Integer, int[][]>
		_defaultContactsCardTemplateTypes =
			HashMapBuilder.<Integer, int[][]>put(
				FaroConstants.TYPE_ACCOUNT,
				new int[][] {
					{
						ContactsCardTemplateConstants.TYPE_RECENT_ACTIVITIES,
						ContactsCardTemplateConstants.TYPE_INTEREST,
						ContactsCardTemplateConstants.TYPE_ASSOCIATED_SEGMENTS
					},
					{ContactsCardTemplateConstants.TYPE_ACTIVITY_HISTORY},
					{
						ContactsCardTemplateConstants.TYPE_EMPLOYEES,
						ContactsCardTemplateConstants.TYPE_SIMILAR
					}
				}
			).put(
				FaroConstants.TYPE_INDIVIDUAL,
				new int[][] {
					{
						ContactsCardTemplateConstants.TYPE_RECENT_ACTIVITIES,
						ContactsCardTemplateConstants.TYPE_INTEREST,
						ContactsCardTemplateConstants.TYPE_ASSOCIATED_SEGMENTS
					},
					{ContactsCardTemplateConstants.TYPE_ACTIVITY_HISTORY},
					{
						ContactsCardTemplateConstants.TYPE_COWORKERS,
						ContactsCardTemplateConstants.TYPE_SIMILAR
					}
				}
			).put(
				FaroConstants.TYPE_SEGMENT_ACCOUNTS,
				new int[][] {
					{
						ContactsCardTemplateConstants.TYPE_CONVERSION_HEALTH,
						ContactsCardTemplateConstants.TYPE_INTEREST,
						ContactsCardTemplateConstants.TYPE_SEGMENT_DISTRIBUTION
					}
				}
			).put(
				FaroConstants.TYPE_SEGMENT_INDIVIDUALS,
				new int[][] {
					{
						ContactsCardTemplateConstants.TYPE_CONVERSION_HEALTH,
						ContactsCardTemplateConstants.TYPE_INTEREST,
						ContactsCardTemplateConstants.TYPE_SEGMENT_DISTRIBUTION
					}
				}
			).build();
	private static final Map<Integer, int[]>
		_defaultHeaderContactsCardTemplateTypes =
			HashMapBuilder.<Integer, int[]>put(
				FaroConstants.TYPE_ACCOUNT,
				new int[] {
					ContactsCardTemplateConstants.TYPE_PROFILE,
					ContactsCardTemplateConstants.TYPE_NET_SALES
				}
			).put(
				FaroConstants.TYPE_INDIVIDUAL,
				new int[] {
					ContactsCardTemplateConstants.TYPE_PROFILE,
					ContactsCardTemplateConstants.TYPE_NET_SALES
				}
			).put(
				FaroConstants.TYPE_SEGMENT_ACCOUNTS,
				new int[] {
					ContactsCardTemplateConstants.TYPE_SEGMENT_MEMBERSHIP,
					ContactsCardTemplateConstants.TYPE_LIFETIME_VALUE,
					ContactsCardTemplateConstants.TYPE_CLOSED_WON
				}
			).put(
				FaroConstants.TYPE_SEGMENT_INDIVIDUALS,
				new int[] {
					ContactsCardTemplateConstants.TYPE_SEGMENT_MEMBERSHIP,
					ContactsCardTemplateConstants.TYPE_LIFETIME_VALUE,
					ContactsCardTemplateConstants.TYPE_CLOSED_WON
				}
			).build();

	@Reference
	private ContactsCardTemplateLocalService _contactsCardTemplateLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}
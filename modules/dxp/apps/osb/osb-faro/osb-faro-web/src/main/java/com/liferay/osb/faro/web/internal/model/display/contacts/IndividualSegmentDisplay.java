/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

import com.liferay.osb.faro.engine.client.model.Asset;
import com.liferay.osb.faro.engine.client.model.Author;
import com.liferay.osb.faro.engine.client.model.CustomEvent;
import com.liferay.osb.faro.engine.client.model.DXPGroup;
import com.liferay.osb.faro.engine.client.model.DXPRole;
import com.liferay.osb.faro.engine.client.model.DXPTeam;
import com.liferay.osb.faro.engine.client.model.DXPUser;
import com.liferay.osb.faro.engine.client.model.DXPUserGroup;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership;
import com.liferay.osb.faro.engine.client.model.Organization;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.model.display.main.FaroEntityDisplay;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Shinn Lok
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class IndividualSegmentDisplay implements FaroEntityDisplay {

	public IndividualSegmentDisplay() {
	}

	public IndividualSegmentDisplay(IndividualSegment individualSegment) {
		_activeIndividualCount = individualSegment.getActiveIndividualCount();
		_activitiesCount = individualSegment.getActivitiesCount();
		_anonymousIndividualCount =
			individualSegment.getAnonymousIndividualCount();
		_channelId = individualSegment.getChannelId();
		_dateCreated = individualSegment.getDateCreated();
		_dateModified = individualSegment.getDateModified();
		_filter = individualSegment.getFilter();
		_id = individualSegment.getId();
		_includeAnonymousUsers = individualSegment.isIncludeAnonymousUsers();

		Map<String, Object> embeddedResources =
			individualSegment.getEmbeddedResources();

		if (MapUtil.isNotEmpty(embeddedResources)) {
			IndividualSegmentMembership individualSegmentMembership =
				JSONUtil.convertValue(
					embeddedResources.get("active-membership"),
					new TypeReference<IndividualSegmentMembership>() {
					});

			if (individualSegmentMembership != null) {
				_individualAddedDate =
					individualSegmentMembership.getDateCreated();
			}

			Map<String, Object> referencedObjects = JSONUtil.convertValue(
				embeddedResources.get("referenced-objects"),
				new TypeReference<Map<String, Object>>() {
				});

			if (referencedObjects != null) {
				_addReferencedFieldMappings(referencedObjects);
				_addReferencedObject(
					referencedObjects, Asset::getId, AssetDisplay::new,
					"assets",
					new TypeReference<List<Asset>>() {
					});
				_addReferencedObject(
					referencedObjects, CustomEvent::getName,
					CustomEventDisplay::new, "custom-events",
					new TypeReference<List<CustomEvent>>() {
					});
				_addReferencedObject(
					referencedObjects, DXPGroup::getId, DXPGroupDisplay::new,
					"groups",
					new TypeReference<List<DXPGroup>>() {
					});
				_addReferencedObject(
					referencedObjects, Organization::getId,
					OrganizationDisplay::new, "organizations",
					new TypeReference<List<Organization>>() {
					});
				_addReferencedObject(
					referencedObjects, DXPRole::getId, DXPRoleDisplay::new,
					"roles",
					new TypeReference<List<DXPRole>>() {
					});
				_addReferencedObject(
					referencedObjects, DXPTeam::getId, DXPTeamDisplay::new,
					"teams",
					new TypeReference<List<DXPTeam>>() {
					});
				_addReferencedObject(
					referencedObjects, DXPUserGroup::getId,
					DXPUserGroupDisplay::new, "user-groups",
					new TypeReference<List<DXPUserGroup>>() {
					});
				_addReferencedObject(
					referencedObjects, DXPUser::getId, DXPUserDisplay::new,
					"users",
					new TypeReference<List<DXPUser>>() {
					});
			}
		}

		_individualCount = individualSegment.getIndividualCount();
		_knownIndividualCount = individualSegment.getKnownIndividualCount();
		_lastActivityDate = individualSegment.getLastActivityDate();
		_lastMembershipUpdateDate =
			individualSegment.getLastMembershipUpdateDate();
		_name = individualSegment.getName();
		_segmentType = individualSegment.getSegmentType();
		_state = individualSegment.getState();
		_status = individualSegment.getStatus();
		_type = FaroConstants.TYPE_SEGMENT_INDIVIDUALS;

		Author author = individualSegment.getAuthor();

		_userName = author.getName();
	}

	@Override
	public void addProperties(List<String> propertyNames) {
	}

	@Override
	public String getId() {
		return _id;
	}

	public long getIndividualCount() {
		return _individualCount;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Map<String, Object> getProperties() {
		return Collections.emptyMap();
	}

	@Override
	public int getType() {
		return _type;
	}

	private void _addReferencedFieldMappings(
		Map<String, Object> referencedObjects) {

		Map<String, Map<String, Map<String, Object>>> fieldMappingMaps =
			new HashMap<>();

		List<FieldMapping> fieldMappings = JSONUtil.convertValue(
			referencedObjects.get("field-mappings"),
			new TypeReference<List<FieldMapping>>() {
			});

		for (FieldMapping fieldMapping : fieldMappings) {
			Map<String, Map<String, Object>> ownerFieldMappingMap =
				fieldMappingMaps.computeIfAbsent(
					fieldMapping.getOwnerType(), key -> new HashMap<>());

			Map<String, Object> fieldMappingMap =
				ownerFieldMappingMap.computeIfAbsent(
					fieldMapping.getContext(), key -> new HashMap<>());

			fieldMappingMap.put(
				fieldMapping.getFieldName(),
				new FieldMappingDisplay(fieldMapping));
		}

		_referencedObjects.put("fieldMappings", fieldMappingMaps);
	}

	private <T, R> void _addReferencedObject(
		Map<String, Object> referencedObjects, Function<T, String> keyMapper,
		Function<T, R> valueMapper, String name,
		TypeReference<List<T>> typeReference) {

		List<T> objects = JSONUtil.convertValue(
			referencedObjects.get(name), typeReference);

		Map<String, R> objectsMap = new HashMap<>();

		for (T object : objects) {
			objectsMap.put(
				(String)keyMapper.apply(object), valueMapper.apply(object));
		}

		_referencedObjects.put(name, objectsMap);
	}

	private long _activeIndividualCount;
	private long _activitiesCount;
	private long _anonymousIndividualCount;
	private String _channelId;
	private Date _dateCreated;
	private Date _dateModified;
	private String _filter;
	private String _id;
	private boolean _includeAnonymousUsers;
	private Date _individualAddedDate;
	private long _individualCount;

	@JsonIgnore
	private Map<String, List<Field>> _interestsFieldMap;

	private long _knownIndividualCount;
	private Date _lastActivityDate;
	private Date _lastMembershipUpdateDate;
	private String _name;
	private final Map<String, Object> _referencedObjects = new HashMap<>();
	private String _segmentType;
	private String _state;
	private String _status;
	private int _type;
	private String _userName;

}
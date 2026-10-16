/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.base.DDMStructureLayoutLocalServiceBaseImpl;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMStructureLayout",
	service = AopService.class
)
public class DDMStructureLayoutLocalServiceImpl
	extends DDMStructureLayoutLocalServiceBaseImpl {

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addStructureLayout(long, long, long, String, long,
	 *             DDMFormLayout, ServiceContext)}
	 */
	@Deprecated
	@Override
	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long structureVersionId,
			DDMFormLayout ddmFormLayout, ServiceContext serviceContext)
		throws PortalException {

		return addStructureLayout(
			userId, groupId, 0, null, structureVersionId, ddmFormLayout,
			serviceContext);
	}

	@Override
	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long classNameId,
			String structureLayoutKey, long structureVersionId,
			DDMFormLayout ddmFormLayout, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(ddmFormLayout);

		long structureLayoutId = counterLocalService.increment();

		DDMStructureLayout structureLayout =
			ddmStructureLayoutPersistence.create(structureLayoutId);

		structureLayout.setUuid(serviceContext.getUuid());
		structureLayout.setGroupId(groupId);
		structureLayout.setCompanyId(user.getCompanyId());
		structureLayout.setUserId(user.getUserId());
		structureLayout.setUserName(user.getFullName());
		structureLayout.setClassNameId(classNameId);
		structureLayout.setStructureLayoutKey(
			GetterUtil.getString(
				structureLayoutKey,
				String.valueOf(counterLocalService.increment())));
		structureLayout.setStructureVersionId(structureVersionId);
		structureLayout.setDefinition(_serialize(ddmFormLayout));

		return ddmStructureLayoutPersistence.update(structureLayout);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructureLayout addStructureLayout(
			long userId, long groupId, long classNameId,
			String structureLayoutKey, long structureVersionId,
			Map<Locale, String> name, Map<Locale, String> description,
			String definition, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long structureLayoutId = counterLocalService.increment();

		DDMStructureLayout structureLayout =
			ddmStructureLayoutPersistence.create(structureLayoutId);

		structureLayout.setUuid(serviceContext.getUuid());
		structureLayout.setGroupId(groupId);
		structureLayout.setCompanyId(user.getCompanyId());
		structureLayout.setUserId(user.getUserId());
		structureLayout.setUserName(user.getFullName());
		structureLayout.setCreateDate(new Date());
		structureLayout.setModifiedDate(new Date());
		structureLayout.setClassNameId(classNameId);
		structureLayout.setStructureLayoutKey(
			GetterUtil.getString(
				structureLayoutKey,
				String.valueOf(counterLocalService.increment())));
		structureLayout.setStructureVersionId(structureVersionId);
		structureLayout.setNameMap(name);
		structureLayout.setDescriptionMap(description);
		structureLayout.setDefinition(definition);

		return ddmStructureLayoutPersistence.update(structureLayout);
	}

	@Override
	public void deleteDDMStructureLayouts(
			long classNameId, DDMStructureVersion ddmStructureVersion)
		throws PortalException {

		for (DDMStructureLayout ddmStructureLayout :
				ddmStructureLayoutPersistence.findByG_C_SV(
					ddmStructureVersion.getGroupId(), classNameId,
					ddmStructureVersion.getStructureVersionId())) {

			deleteDDMStructureLayout(ddmStructureLayout);
		}
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteStructureLayout(DDMStructureLayout structureLayout) {
		deleteDDMStructureLayout(structureLayout);
	}

	@Override
	public void deleteStructureLayout(long structureLayoutId)
		throws PortalException {

		deleteDDMStructureLayout(structureLayoutId);
	}

	@Override
	public DDMStructureLayout fetchStructureLayout(long structureLayoutId) {
		return ddmStructureLayoutPersistence.fetchByPrimaryKey(
			structureLayoutId);
	}

	@Override
	public DDMStructureLayout fetchStructureLayout(
		long groupId, long classNameId, String structureLayoutKey) {

		return ddmStructureLayoutPersistence.fetchByG_C_S(
			groupId, classNameId, GetterUtil.getString(structureLayoutKey));
	}

	@Override
	public DDMStructureLayout getStructureLayout(long structureLayoutId)
		throws PortalException {

		return ddmStructureLayoutPersistence.findByPrimaryKey(
			structureLayoutId);
	}

	@Override
	public DDMStructureLayout getStructureLayout(
			long groupId, long classNameId, String structureLayoutKey)
		throws PortalException {

		return ddmStructureLayoutPersistence.findByG_C_S(
			groupId, classNameId, GetterUtil.getString(structureLayoutKey));
	}

	@Override
	public DDMStructureLayout getStructureLayoutByStructureVersionId(
			long structureVersionId)
		throws PortalException {

		return ddmStructureLayoutPersistence.findByStructureVersionId(
			structureVersionId);
	}

	@Override
	public DDMFormLayout getStructureLayoutDDMFormLayout(
		DDMStructureLayout structureLayout) {

		DDMFormLayoutDeserializerDeserializeRequest.Builder builder =
			DDMFormLayoutDeserializerDeserializeRequest.Builder.newBuilder(
				structureLayout.getDefinition());

		DDMFormLayoutDeserializerDeserializeResponse
			ddmFormLayoutDeserializerDeserializeResponse =
				_jsonDDMFormLayoutDeserializer.deserialize(builder.build());

		return ddmFormLayoutDeserializerDeserializeResponse.getDDMFormLayout();
	}

	@Override
	public List<DDMStructureLayout> getStructureLayouts(
			long groupId, int start, int end)
		throws PortalException {

		return ddmStructureLayoutPersistence.findByGroupId(groupId, start, end);
	}

	@Override
	public List<DDMStructureLayout> getStructureLayouts(
		long groupId, long classNameId, long structureVersionId) {

		return ddmStructureLayoutPersistence.findByG_C_SV(
			groupId, classNameId, structureVersionId);
	}

	@Override
	public List<DDMStructureLayout> getStructureLayouts(
		long groupId, long classNameId, long structureVersionId, int start,
		int end, OrderByComparator<DDMStructureLayout> orderByComparator) {

		return ddmStructureLayoutPersistence.findByG_C_SV(
			groupId, classNameId, structureVersionId, start, end,
			orderByComparator);
	}

	@Override
	public int getStructureLayoutsCount(long groupId) {
		return ddmStructureLayoutPersistence.countByGroupId(groupId);
	}

	@Override
	public int getStructureLayoutsCount(
		long groupId, long classNameId, long structureVersionId) {

		return ddmStructureLayoutPersistence.countByG_C_SV(
			groupId, classNameId, structureVersionId);
	}

	@Override
	public DDMStructureLayout updateStructureLayout(
			long structureLayoutId, DDMFormLayout ddmFormLayout,
			ServiceContext serviceContext)
		throws PortalException {

		DDMStructureLayout structureLayout =
			ddmStructureLayoutPersistence.findByPrimaryKey(structureLayoutId);

		_validate(ddmFormLayout);

		structureLayout.setDefinition(_serialize(ddmFormLayout));

		return ddmStructureLayoutPersistence.update(structureLayout);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DDMStructureLayout updateStructureLayout(
			long structureLayoutId, long structureVersionId,
			Map<Locale, String> name, Map<Locale, String> description,
			String definition, ServiceContext serviceContext)
		throws PortalException {

		DDMStructureLayout structureLayout =
			ddmStructureLayoutPersistence.findByPrimaryKey(structureLayoutId);

		structureLayout.setModifiedDate(new Date());
		structureLayout.setStructureVersionId(structureVersionId);
		structureLayout.setNameMap(name);
		structureLayout.setDescriptionMap(description);
		structureLayout.setDefinition(definition);

		return ddmStructureLayoutPersistence.update(structureLayout);
	}

	private String _serialize(DDMFormLayout ddmFormLayout) {
		DDMFormLayoutSerializerSerializeRequest.Builder builder =
			DDMFormLayoutSerializerSerializeRequest.Builder.newBuilder(
				ddmFormLayout);

		DDMFormLayoutSerializerSerializeResponse
			ddmFormLayoutSerializerSerializeResponse =
				_jsonDDMFormLayoutSerializer.serialize(builder.build());

		return ddmFormLayoutSerializerSerializeResponse.getContent();
	}

	private void _validate(DDMFormLayout ddmFormLayout) throws PortalException {
		_ddmFormLayoutValidator.validate(ddmFormLayout);
	}

	@Reference
	private DDMFormLayoutValidator _ddmFormLayoutValidator;

	@Reference(target = "(ddm.form.layout.deserializer.type=json)")
	private DDMFormLayoutDeserializer _jsonDDMFormLayoutDeserializer;

	@Reference(target = "(ddm.form.layout.serializer.type=json)")
	private DDMFormLayoutSerializer _jsonDDMFormLayoutSerializer;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.util;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoValueLocalServiceUtil;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.kernel.service.ListTypeLocalServiceUtil;
import com.liferay.portal.kernel.service.PhoneLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.WebsiteLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.scim.rest.dto.v1_0.Operation;
import com.liferay.scim.rest.dto.v1_0.PatchOp;
import com.liferay.scim.rest.internal.configuration.ScimClientOAuth2ApplicationConfiguration;
import com.liferay.scim.rest.internal.model.ScimUser;

import jakarta.ws.rs.core.Response;

import java.io.File;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.Predicate;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.config.SCIMUserSchemaExtensionBuilder;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.objects.plainobjects.ScimAddress;
import org.wso2.charon3.core.objects.plainobjects.ScimName;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.utils.AttributeUtil;

/**
 * @author Rafael Praxedes
 * @author Olivér Kecskeméty
 */
public class ScimUtil {

	public static final String LIFERAY_USER_SCHEMA_EXTENSION_URI =
		"urn:ietf:params:scim:schemas:extension:liferay:2.0:User";

	public static Response buildResponse(SCIMResponse scimResponse) {
		Response.ResponseBuilder responseBuilder = Response.status(
			scimResponse.getResponseStatus());

		if (scimResponse.getResponseMessage() != null) {
			responseBuilder.entity(scimResponse.getResponseMessage());
		}

		Map<String, String> map = scimResponse.getHeaderParamMap();

		if (MapUtil.isNotEmpty(map)) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				responseBuilder.header(entry.getKey(), entry.getValue());
			}
		}

		return responseBuilder.build();
	}

	public static Map<String, String> getHeaders(String resourceName)
		throws NotFoundException {

		return HashMapBuilder.put(
			SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON
		).put(
			SCIMConstants.LOCATION_HEADER,
			AbstractResourceManager.getResourceEndpointURL(resourceName)
		).build();
	}

	public static ScimClientOAuth2ApplicationConfiguration
		getScimClientOAuth2ApplicationConfiguration(
			long companyId, ConfigurationAdmin configurationAdmin) {

		try {
			Configuration[] configurations =
				configurationAdmin.listConfigurations(
					StringBundler.concat(
						"(&(", ConfigurationAdmin.SERVICE_FACTORYPID,
						"=com.liferay.scim.rest.internal.configuration.",
						"ScimClientOAuth2ApplicationConfiguration)(companyId=",
						companyId, "))"));

			if (ArrayUtil.isEmpty(configurations)) {
				throw new NotFoundException(
					"SCIM is not configured for company " + companyId);
			}

			Configuration configuration = configurations[0];

			return ConfigurableUtil.createConfigurable(
				ScimClientOAuth2ApplicationConfiguration.class,
				configuration.getProperties());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get the SCIM client OAuth 2 application ",
						"configuration for company ", companyId),
					exception);
			}

			return ReflectionUtil.throwException(exception);
		}
	}

	public static Group toGroup(List<ScimUser> scimUsers, UserGroup userGroup)
		throws Exception {

		Group group = new Group();

		group.replaceDisplayName(userGroup.getName());

		Date createDate = _truncateDate(userGroup.getCreateDate());

		group.setCreatedInstant(createDate.toInstant());

		group.setExternalId(userGroup.getExternalReferenceCode());
		group.setId(String.valueOf(userGroup.getPrimaryKey()));

		Date modifiedDate = _truncateDate(userGroup.getModifiedDate());

		group.setLastModifiedInstant(modifiedDate.toInstant());

		group.setLocation(
			StringBundler.concat(
				AbstractResourceManager.getResourceEndpointURL(
					SCIMConstants.GROUP_ENDPOINT),
				CharPool.FORWARD_SLASH, userGroup.getPrimaryKey()));

		for (ScimUser scimUser : scimUsers) {
			group.setMember(toUser(Collections.emptyList(), scimUser));
		}

		group.setResourceType(SCIMConstants.GROUP);
		group.setSchemas();

		return group;
	}

	public static ScimUser toScimUser(long companyId, Locale locale, User user)
		throws Exception {

		ScimUser scimUser = new ScimUser();

		scimUser.setActive(_isActive(user));
		scimUser.setAddresses(user.getAddresses());
		scimUser.setAutoScreenName(
			PrefsPropsUtil.getBoolean(
				companyId, PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE));
		scimUser.setAutoPassword(user.getPassword() == null);
		scimUser.setBirthday(_getBirthday(user));
		scimUser.setCompanyId(companyId);
		scimUser.setDisplayName(user.getDisplayName());
		scimUser.setEmailAddresses(
			_getEmailAddresses(
				user.getEmails(), MultiValuedComplexType::getValue,
				MultiValuedComplexType::isPrimary));
		scimUser.setEntitlements(_getEntitlements(user));
		scimUser.setExternalReferenceCode(user.getExternalId());

		ScimName scimName = user.getName();

		scimUser.setFirstName(scimName.getGivenName());

		scimUser.setId(user.getId());
		scimUser.setIMs(_getIMs(user.getInstantMessagingAddresses()));
		scimUser.setJobTitle(user.getTitle());
		scimUser.setLastName(scimName.getFamilyName());
		scimUser.setLocale(locale);
		scimUser.setMale(_isMale(user));
		scimUser.setMiddleName(scimName.getMiddleName());
		scimUser.setNickName(user.getNickName());
		scimUser.setPassword(user.getPassword());
		scimUser.setPhoneNumberMultiValuedComplexTypes(user.getPhoneNumbers());
		scimUser.setPhotos(_getScimValues(user.getPhotos()));
		scimUser.setPreferredLanguage(user.getPreferredLanguage());
		scimUser.setPrefix(
			_getListTypeId(
				scimUser.getCompanyId(), scimName.getHonorificPrefix(),
				Contact.class.getName() + ".prefix"));
		scimUser.setProfileUrl(user.getProfileUrl());
		scimUser.setRoleIds(
			TransformUtil.transformToLongArray(
				user.getRoles(),
				multiValuedComplexType -> {
					Role portalRole = RoleLocalServiceUtil.fetchRole(
						companyId, multiValuedComplexType.getValue());

					if (portalRole == null) {
						return null;
					}

					return portalRole.getRoleId();
				}));
		scimUser.setScreenName(user.getUserName());
		scimUser.setSuffix(
			_getListTypeId(
				scimUser.getCompanyId(), scimName.getHonorificSuffix(),
				Contact.class.getName() + ".suffix"));
		scimUser.setTimeZoneId(_getTimeZoneId(user.getTimezone()));
		scimUser.setUserType(user.getUserType());
		scimUser.setX509Certificates(
			_getScimValues(user.getX509Certificates()));

		_validate(scimUser);

		return scimUser;
	}

	public static ScimUser toScimUser(
			com.liferay.portal.kernel.model.User portalUser)
		throws Exception {

		ScimUser scimUser = new ScimUser();

		scimUser.setActive(portalUser.isActive());
		scimUser.setAddresses(_getScimAddresses(portalUser));
		scimUser.setBirthday(portalUser.getBirthday());
		scimUser.setCompanyId(portalUser.getCompanyId());
		scimUser.setCreateDate(_truncateDate(portalUser.getCreateDate()));

		if (FeatureFlagManagerUtil.isEnabled("LPD-56434")) {
			scimUser.setEmailAddresses(
				_getEmailAddresses(
					EmailAddressLocalServiceUtil.getEmailAddresses(
						portalUser.getCompanyId(), Contact.class.getName(),
						portalUser.getContactId()),
					EmailAddress::getAddress, EmailAddress::isPrimary));
		}
		else {
			scimUser.setEmailAddresses(
				new String[] {portalUser.getEmailAddress()});
		}

		scimUser.setExternalReferenceCode(
			portalUser.getExternalReferenceCode());
		scimUser.setFirstName(portalUser.getFirstName());
		scimUser.setId(String.valueOf(portalUser.getUserId()));

		Map<String, String> ims = new HashMap<>();

		Contact contact = portalUser.getContact();

		if (contact.getJabberSn() != null) {
			ims.put("Jabber", contact.getJabberSn());
		}

		if (contact.getSkypeSn() != null) {
			ims.put("Skype", contact.getSkypeSn());
		}

		scimUser.setIMs(ims);

		scimUser.setJobTitle(portalUser.getJobTitle());
		scimUser.setLastName(portalUser.getLastName());
		scimUser.setLocale(portalUser.getLocale());
		scimUser.setMale(portalUser.isMale());
		scimUser.setMiddleName(portalUser.getMiddleName());
		scimUser.setModifiedDate(_truncateDate(portalUser.getModifiedDate()));
		scimUser.setPhoneNumberMultiValuedComplexTypes(
			TransformUtil.transform(
				PhoneLocalServiceUtil.getPhones(
					contact.getCompanyId(), Contact.class.getName(),
					contact.getContactId()),
				phone -> {
					MultiValuedComplexType multiValuedComplexType =
						new MultiValuedComplexType();

					multiValuedComplexType.setPrimary(phone.isPrimary());

					ListType listType = ListTypeLocalServiceUtil.fetchListType(
						phone.getListTypeId());

					multiValuedComplexType.setType(listType.getName());

					multiValuedComplexType.setValue(phone.getNumber());

					return multiValuedComplexType;
				}));
		scimUser.setPrefix(contact.getPrefixListTypeId());
		scimUser.setProfileUrl(_getProfileURL(contact));
		scimUser.setRoleIds(portalUser.getRoleIds());
		scimUser.setScreenName(portalUser.getScreenName());
		scimUser.setSuffix(contact.getSuffixListTypeId());
		scimUser.setTimeZoneId(portalUser.getTimeZoneId());

		_setExpandoValues(scimUser);

		return scimUser;
	}

	public static User toUser(List<Group> groups, ScimUser scimUser)
		throws Exception {

		User user = new User();

		user.replaceActive(scimUser.isActive());
		user.replaceEmails(
			Collections.singletonList(
				new MultiValuedComplexType(
					"work", true, null, scimUser.getEmailAddresses()[0],
					null)));

		ScimName scimName = new ScimName();

		scimName.setFamilyName(scimUser.getLastName());
		scimName.setGivenName(scimUser.getFirstName());
		scimName.setMiddleName(scimUser.getMiddleName());

		user.replaceName(scimName);

		user.replaceTitle(scimUser.getJobTitle());

		SCIMResourceSchemaManager scimResourceSchemaManager =
			SCIMResourceSchemaManager.getInstance();

		user.setAttribute(
			_createLiferayUserExtensionComplexAttribute(scimUser),
			scimResourceSchemaManager.getUserResourceSchema());

		Date createDate = scimUser.getCreateDate();

		user.setCreatedInstant(createDate.toInstant());

		user.setExternalId(scimUser.getExternalReferenceCode());

		for (Group group : groups) {
			user.setGroup("direct", group);
		}

		user.setId(scimUser.getId());

		Date modifiedDate = scimUser.getModifiedDate();

		user.setLastModifiedInstant(modifiedDate.toInstant());

		user.setLocation(
			StringBundler.concat(
				AbstractResourceManager.getResourceEndpointURL(
					SCIMConstants.USER_ENDPOINT),
				CharPool.FORWARD_SLASH, scimUser.getId()));
		user.setResourceType(SCIMConstants.USER);
		user.setSchemas();
		user.setUserName(scimUser.getScreenName());

		return user;
	}

	public static String transformGroupPatchOp(PatchOp patchOp) {
		JSONArray operationsJSONArray = JSONFactoryUtil.createJSONArray();

		for (Operation operation : patchOp.getOperations()) {
			JSONObject operationJSONObject = JSONUtil.put(
				SCIMConstants.OperationalConstants.OP, operation.getOp());

			operationsJSONArray.put(operationJSONObject);

			if (SCIMConstants.OperationalConstants.ADD.equalsIgnoreCase(
					operation.getOp())) {

				if (!SCIMConstants.GroupSchemaConstants.MEMBERS.
						equalsIgnoreCase(operation.getPath())) {

					continue;
				}

				Object value = operation.getValue();

				if (!(value instanceof List)) {
					operationJSONObject.put(
						SCIMConstants.OperationalConstants.VALUE,
						JSONUtil.put(
							SCIMConstants.GroupSchemaConstants.MEMBERS, value));

					continue;
				}

				JSONArray valueJSONArray = JSONFactoryUtil.createJSONArray(
					(ArrayList)value);

				JSONObject valueJSONObject = valueJSONArray.getJSONObject(0);

				if (!valueJSONObject.has(
						SCIMConstants.GroupSchemaConstants.DISPLAY)) {

					valueJSONObject.put(
						SCIMConstants.GroupSchemaConstants.DISPLAY,
						StringPool.BLANK);
				}

				operationJSONObject.put(
					SCIMConstants.OperationalConstants.VALUE,
					JSONUtil.put(
						SCIMConstants.GroupSchemaConstants.MEMBERS,
						valueJSONArray));
			}
			else if (SCIMConstants.OperationalConstants.REMOVE.equalsIgnoreCase(
						operation.getOp())) {

				Object value = operation.getValue();

				if (value == null) {
					operationJSONObject.put(
						SCIMConstants.OperationalConstants.PATH,
						operation.getPath());

					continue;
				}

				if (value instanceof ArrayList) {
					JSONArray valueJSONArray = JSONFactoryUtil.createJSONArray(
						(ArrayList)value);

					JSONObject valueJSONObject = valueJSONArray.getJSONObject(
						0);

					value = valueJSONObject.get(
						SCIMConstants.OperationalConstants.VALUE);
				}
				else if (value instanceof Map) {
					value = MapUtil.getString(
						(Map)value, SCIMConstants.OperationalConstants.VALUE);
				}

				operationJSONObject.put(
					SCIMConstants.OperationalConstants.PATH,
					StringBundler.concat(
						operation.getPath(), StringPool.OPEN_BRACKET,
						SCIMConstants.OperationalConstants.VALUE, " eq \"",
						value, StringPool.QUOTE, StringPool.CLOSE_BRACKET));
			}
			else {
				operationJSONObject.put(
					SCIMConstants.OperationalConstants.PATH,
					operation.getPath());

				Object value = operation.getValue();

				if (value instanceof List) {
					operationJSONObject.put(
						SCIMConstants.OperationalConstants.VALUE,
						JSONFactoryUtil.createJSONArray((ArrayList)value));
				}
				else if (value instanceof Map valueMap) {
					valueMap.remove(SCIMConstants.CommonSchemaConstants.ID);

					operationJSONObject.put(
						SCIMConstants.OperationalConstants.VALUE, valueMap);
				}
				else {
					operationJSONObject.put(
						SCIMConstants.OperationalConstants.VALUE, value);
				}
			}
		}

		return JSONUtil.put(
			SCIMConstants.CommonSchemaConstants.SCHEMAS, patchOp.getSchemas()
		).put(
			SCIMConstants.OperationalConstants.OPERATIONS, operationsJSONArray
		).toString();
	}

	private static AttributeSchema _createAttributeSchema() {
		SCIMUserSchemaExtensionBuilder scimUserSchemaExtensionBuilder =
			SCIMUserSchemaExtensionBuilder.getInstance();

		String json = JSONUtil.putAll(
			JSONUtil.put(
				"attributeName", "birthday"
			).put(
				"attributeURI", LIFERAY_USER_SCHEMA_EXTENSION_URI + ":birthday"
			).put(
				"canonicalValues", JSONFactoryUtil.createJSONArray()
			).put(
				"caseExact", "false"
			).put(
				"dataType", "string"
			).put(
				"description", "User's birthday"
			).put(
				"multiValued", "false"
			).put(
				"mutability", "readWrite"
			).put(
				"referenceTypes", JSONFactoryUtil.createJSONArray()
			).put(
				"required", "false"
			).put(
				"returned", "default"
			).put(
				"subAttributes", "null"
			).put(
				"uniqueness", "none"
			),
			JSONUtil.put(
				"attributeName", "male"
			).put(
				"attributeURI", LIFERAY_USER_SCHEMA_EXTENSION_URI + ":male"
			).put(
				"canonicalValues", JSONFactoryUtil.createJSONArray()
			).put(
				"caseExact", "false"
			).put(
				"dataType", "boolean"
			).put(
				"description", "User's gender"
			).put(
				"multiValued", "false"
			).put(
				"mutability", "readWrite"
			).put(
				"referenceTypes", JSONFactoryUtil.createJSONArray()
			).put(
				"required", "false"
			).put(
				"returned", "default"
			).put(
				"subAttributes", "null"
			).put(
				"uniqueness", "none"
			),
			JSONUtil.put(
				"attributeName", LIFERAY_USER_SCHEMA_EXTENSION_URI
			).put(
				"attributeURI", LIFERAY_USER_SCHEMA_EXTENSION_URI
			).put(
				"canonicalValues", JSONFactoryUtil.createJSONArray()
			).put(
				"caseExact", "false"
			).put(
				"dataType", "complex"
			).put(
				"description", "Liferay's User Schema Extension"
			).put(
				"multiValued", "false"
			).put(
				"mutability", "readWrite"
			).put(
				"referenceTypes", JSONUtil.put("external")
			).put(
				"required", "false"
			).put(
				"returned", "default"
			).put(
				"subAttributes", "birthday male"
			).put(
				"uniqueness", "none"
			)
		).toString();

		try {
			File file = FileUtil.createTempFile(json.getBytes());

			scimUserSchemaExtensionBuilder.buildUserSchemaExtension(
				file.getPath());
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}

		return scimUserSchemaExtensionBuilder.getExtensionSchema();
	}

	private static ComplexAttribute _createLiferayUserExtensionComplexAttribute(
			ScimUser scimUser)
		throws Exception {

		AttributeSchema attributeSchema =
			_attributeSchemaDCLSingleton.getSingleton(
				ScimUtil::_createAttributeSchema);

		ComplexAttribute complexAttribute = new ComplexAttribute(
			attributeSchema.getName());

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			_PATTERN);

		complexAttribute.setSubAttributesList(
			HashMapBuilder.<String, Attribute>put(
				"birthday",
				_createSimpleAttribute(
					attributeSchema.getSubAttributeSchema("birthday"),
					dateFormat.format(scimUser.getBirthday()))
			).put(
				"male",
				_createSimpleAttribute(
					attributeSchema.getSubAttributeSchema("male"),
					scimUser.isMale())
			).build());

		return (ComplexAttribute)DefaultAttributeFactory.createAttribute(
			attributeSchema, complexAttribute);
	}

	private static SimpleAttribute _createSimpleAttribute(
			AttributeSchema attributeSchema, Object attributeValue)
		throws Exception {

		return (SimpleAttribute)DefaultAttributeFactory.createAttribute(
			attributeSchema,
			new SimpleAttribute(
				attributeSchema.getName(),
				AttributeUtil.getAttributeValueFromString(
					attributeValue, attributeSchema.getType())));
	}

	private static Date _getBirthday() {
		Calendar birthdayCalendar = CalendarFactoryUtil.getCalendar(
			1970, Calendar.JANUARY, 1);

		return birthdayCalendar.getTime();
	}

	private static Date _getBirthday(User user) {
		try {
			ComplexAttribute complexAttribute =
				(ComplexAttribute)user.getAttribute(
					LIFERAY_USER_SCHEMA_EXTENSION_URI);

			if (complexAttribute == null) {
				return _getBirthday();
			}

			SimpleAttribute simpleAttribute =
				(SimpleAttribute)complexAttribute.getSubAttribute("birthday");

			if (simpleAttribute == null) {
				return _getBirthday();
			}

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				_PATTERN);

			return dateFormat.parse(simpleAttribute.getStringValue());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return _getBirthday();
		}
	}

	private static <T> String[] _getEmailAddresses(
		List<T> emailAddresses, Function<T, String> function,
		Predicate<T> predicate) {

		if (ListUtil.isEmpty(emailAddresses)) {
			return new String[0];
		}

		List<String> list = new ArrayList<>(emailAddresses.size());

		for (T emailAddress : emailAddresses) {
			if (predicate.test(emailAddress)) {
				list.add(0, function.apply(emailAddress));
			}
			else {
				list.add(function.apply(emailAddress));
			}
		}

		return list.toArray(new String[0]);
	}

	private static String[] _getEntitlements(User user) {
		try {
			List<String> values = new ArrayList<>();

			MultiValuedAttribute entitlements =
				(MultiValuedAttribute)user.getAttribute("entitlements");

			for (Attribute attribute : entitlements.getAttributeValues()) {
				SimpleAttribute simpleAttribute =
					(SimpleAttribute)attribute.getSubAttribute("value");

				values.add(simpleAttribute.getStringValue());
			}

			return ArrayUtil.toStringArray(values);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get entitlements", exception);
			}

			return null;
		}
	}

	private static String _getExpandoValue(
			long expandoTableId, String name, String userIdString)
		throws Exception {

		ExpandoColumn expandoColumn = ExpandoColumnLocalServiceUtil.fetchColumn(
			expandoTableId, name);

		if (expandoColumn == null) {
			return null;
		}

		ExpandoValue expandoValue = ExpandoValueLocalServiceUtil.getValue(
			expandoTableId, expandoColumn.getColumnId(),
			GetterUtil.getLong(userIdString));

		if (expandoValue != null) {
			return expandoValue.getString();
		}

		return null;
	}

	private static Map<String, String> _getIMs(
		List<MultiValuedComplexType> multiValuedComplexTypes) {

		if (multiValuedComplexTypes == null) {
			return null;
		}

		Map<String, String> ims = new HashMap<>();

		for (MultiValuedComplexType multiValuedComplexType :
				multiValuedComplexTypes) {

			ims.put(
				multiValuedComplexType.getType(),
				multiValuedComplexType.getValue());
		}

		return ims;
	}

	private static long _getListTypeId(
		long companyId, String name, String type) {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-56434")) {
			return 0;
		}

		ListType listType = ListTypeLocalServiceUtil.getListType(
			companyId, StringUtil.toLowerCase(name), type);

		if (listType == null) {
			return 0;
		}

		return listType.getListTypeId();
	}

	private static String _getProfileURL(Contact contact) {
		long listTypeId = ListTypeLocalServiceUtil.getListTypeId(
			contact.getCompanyId(), "personal",
			Contact.class.getName() + ".website");

		for (Website website :
				WebsiteLocalServiceUtil.getWebsites(
					contact.getCompanyId(), Contact.class.getName(),
					contact.getContactId())) {

			if (website.isPrimary() &&
				(website.getListTypeId() == listTypeId)) {

				return website.getUrl();
			}
		}

		return null;
	}

	private static List<ScimAddress> _getScimAddresses(
		com.liferay.portal.kernel.model.User portalUser) {

		List<ScimAddress> scimAddresses = new ArrayList<>();

		for (Address address : portalUser.getAddresses()) {
			StringBundler streetAddressSB = new StringBundler(6);

			streetAddressSB.append(address.getStreet1());
			streetAddressSB.append("\n");

			if (Validator.isNotNull(address.getStreet2())) {
				streetAddressSB.append(address.getStreet2());
				streetAddressSB.append("\n");
			}

			if (Validator.isNotNull(address.getStreet3())) {
				streetAddressSB.append(address.getStreet3());
				streetAddressSB.append("\n");
			}

			Country country = address.getCountry();
			ListType listType = address.getListType();
			Region region = address.getRegion();

			scimAddresses.add(
				new ScimAddress(
					StringBundler.concat(
						streetAddressSB, address.getCity(),
						StringPool.COMMA_AND_SPACE, region, StringPool.SPACE,
						address.getZip(), "\n", country),
					listType.getName(), streetAddressSB.toString(),
					address.getCity(), region.getName(), address.getZip(),
					country.getA2(), address.isPrimary()));
		}

		return scimAddresses;
	}

	private static String[] _getScimValues(
		List<MultiValuedComplexType> multiValuedComplexTypes) {

		if (multiValuedComplexTypes == null) {
			return null;
		}

		return TransformUtil.transformToArray(
			multiValuedComplexTypes,
			multiValuedComplexType -> multiValuedComplexType.getValue(),
			String.class);
	}

	private static String _getTimeZoneId(String timeZoneId) {
		if (timeZoneId == null) {
			return null;
		}

		TimeZone timeZone = TimeZoneUtil.getTimeZone(timeZoneId);

		return timeZone.getID();
	}

	private static boolean _isActive(User user) {
		SimpleAttribute simpleAttribute = (SimpleAttribute)user.getAttribute(
			"active");

		if (simpleAttribute != null) {
			return GetterUtil.getBoolean(simpleAttribute.getValue());
		}

		return user.getActive();
	}

	private static boolean _isMale(User user) {
		try {
			ComplexAttribute complexAttribute =
				(ComplexAttribute)user.getAttribute(
					LIFERAY_USER_SCHEMA_EXTENSION_URI);

			if (complexAttribute == null) {
				return true;
			}

			SimpleAttribute simpleAttribute =
				(SimpleAttribute)complexAttribute.getSubAttribute("male");

			if (simpleAttribute == null) {
				return true;
			}

			return simpleAttribute.getBooleanValue();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return true;
		}
	}

	private static void _setExpandoValues(ScimUser scimUser) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-56434")) {
			return;
		}

		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchTable(
			scimUser.getCompanyId(),
			ClassNameLocalServiceUtil.getClassNameId(
				com.liferay.portal.kernel.model.User.class.getName()),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable == null) {
			return;
		}

		scimUser.setDisplayName(
			_getExpandoValue(
				expandoTable.getTableId(), "scimDisplayName",
				scimUser.getId()));
		scimUser.setEntitlements(
			StringUtil.split(
				_getExpandoValue(
					expandoTable.getTableId(), "scimEntitlements",
					scimUser.getId()),
				StringPool.NEW_LINE));
		scimUser.setNickName(
			_getExpandoValue(
				expandoTable.getTableId(), "scimNickName", scimUser.getId()));
		scimUser.setPhotos(
			StringUtil.split(
				_getExpandoValue(
					expandoTable.getTableId(), "scimPhotos", scimUser.getId()),
				StringPool.NEW_LINE));
		scimUser.setPreferredLanguage(
			_getExpandoValue(
				expandoTable.getTableId(), "scimPreferredLanguage",
				scimUser.getId()));
		scimUser.setUserType(
			_getExpandoValue(
				expandoTable.getTableId(), "scimUserType", scimUser.getId()));
		scimUser.setX509Certificates(
			StringUtil.split(
				_getExpandoValue(
					expandoTable.getTableId(), "scimX509Certificates",
					scimUser.getId()),
				StringPool.NEW_LINE));
	}

	private static Date _truncateDate(Date date) {
		if (date == null) {
			return null;
		}

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	private static void _validate(ScimUser scimUser) throws Exception {
		if (!scimUser.isAutoScreenName() &&
			Validator.isNull(scimUser.getScreenName())) {

			throw new UserScreenNameException.MustNotBeNull(
				ContactConstants.getFullName(
					scimUser.getFirstName(), scimUser.getMiddleName(),
					scimUser.getLastName()));
		}

		if (Validator.isNull(scimUser.getEmailAddresses()[0]) &&
			PrefsPropsUtil.getBoolean(
				scimUser.getCompanyId(),
				PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED)) {

			throw new UserEmailAddressException.MustNotBeNull(
				ContactConstants.getFullName(
					scimUser.getFirstName(), scimUser.getMiddleName(),
					scimUser.getLastName()));
		}
	}

	private static final String _PATTERN = "yyyy-MM-dd'T'HH:mm:ssXX";

	private static final Log _log = LogFactoryUtil.getLog(ScimUtil.class);

	private static final DCLSingleton<AttributeSchema>
		_attributeSchemaDCLSingleton = new DCLSingleton<>();

}
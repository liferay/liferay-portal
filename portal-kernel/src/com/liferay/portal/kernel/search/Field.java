/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Bruno Farache
 * @author Brian Wing Shun Chan
 * @author Allen Chiang
 * @author Alex Wallace
 */
public class Field implements Serializable {

	public static final String ANY = StringPool.STAR;

	public static final String ARTICLE_ID = "articleId";

	public static final String ASSET_CATEGORY_ID = "assetCategoryId";

	public static final String ASSET_CATEGORY_IDS = "assetCategoryIds";

	public static final String ASSET_CATEGORY_TITLE = "assetCategoryTitle";

	public static final String ASSET_CATEGORY_TITLES = "assetCategoryTitles";

	public static final String ASSET_ENTRY_ID = "assetEntryId";

	public static final String ASSET_ENTRY_IDS = "assetEntryIds";

	public static final String ASSET_INTERNAL_CATEGORY_ID =
		"assetInternalCategoryId";

	public static final String ASSET_INTERNAL_CATEGORY_IDS =
		"assetInternalCategoryIds";

	public static final String ASSET_INTERNAL_CATEGORY_TITLE =
		"assetInternalCategoryTitle";

	public static final String ASSET_INTERNAL_CATEGORY_TITLES =
		"assetInternalCategoryTitles";

	public static final String ASSET_INTERNAL_VOCABULARY_IDS =
		"assetInternalVocabularyIds";

	public static final String ASSET_PARENT_CATEGORY_ID = "parentCategoryId";

	public static final String ASSET_PARENT_CATEGORY_IDS = "parentCategoryIds";

	public static final String ASSET_TAG_IDS = "assetTagIds";

	public static final String ASSET_TAG_NAMES = "assetTagNames";

	public static final String ASSET_VOCABULARY_ID = "assetVocabularyId";

	public static final String ASSET_VOCABULARY_IDS = "assetVocabularyIds";

	public static final String CAPTION = "caption";

	public static final String CATEGORY_ID = "categoryId";

	public static final String CLASS_NAME_ID = "classNameId";

	public static final String CLASS_PK = "classPK";

	public static final String CLASS_TYPE_ID = "classTypeId";

	public static final String COMMENTS = "comments";

	public static final String COMPANY_ID = "companyId";

	public static final String CONTENT = "content";

	public static final String CREATE_DATE = "createDate";

	public static final String DEFAULT_LANGUAGE_ID = "defaultLanguageId";

	public static final String DESCRIPTION = "description";

	public static final String DISPLAY_DATE = "displayDate";

	public static final String ENTRY_CLASS_NAME = "entryClassName";

	public static final String ENTRY_CLASS_PK = "entryClassPK";

	public static final String EXPIRATION_DATE = "expirationDate";

	public static final String FOLDER_ID = "folderId";

	public static final String GEO_LOCATION = "geoLocation";

	public static final String GROUP_ID = "groupId";

	public static final String GROUP_ROLE_ID = "groupRoleId";

	public static final String HIDDEN = "hidden";

	public static final String KEYWORD_SEARCH = "keywordSearch";

	public static final String[] KEYWORDS = {
		ASSET_CATEGORY_TITLES, ASSET_TAG_NAMES, COMMENTS, CONTENT, DESCRIPTION,
		Field.PROPERTIES, Field.TITLE, Field.URL, Field.USER_NAME
	};

	public static final String LANGUAGE_ID = "languageId";

	public static final String LAYOUT_UUID = "layoutUuid";

	public static final String MODIFIED_DATE = "modified";

	public static final String NAME = "name";

	public static final String NODE_ID = "nodeId";

	public static final String ORGANIZATION_ID = "organizationId";

	public static final String PRIORITY = "priority";

	public static final String PROPERTIES = "properties";

	public static final String PUBLISH_DATE = "publishDate";

	public static final String RELATED_ENTRY = "relatedEntry";

	public static final String REMOVED_BY_USER_NAME = "removedByUserName";

	public static final String REMOVED_DATE = "removedDate";

	public static final String ROLE_ID = "roleId";

	public static final String ROLE_IDS = "roleIds";

	public static final String ROOT_ENTRY_CLASS_NAME = "rootEntryClassName";

	public static final String ROOT_ENTRY_CLASS_PK = "rootEntryClassPK";

	public static final String SCOPE_GROUP_ID = "scopeGroupId";

	public static final String SENT_DATE = "sentDate";

	public static final String SNIPPET = "snippet";

	public static final String SORTABLE_FIELD_SUFFIX = "sortable";

	public static final String SPELL_CHECK_WORD = "spellCheckWord";

	public static final String STAGING_GROUP = "stagingGroup";

	public static final String STATUS = "status";

	public static final String SUBTITLE = "subtitle";

	public static final String TITLE = "title";

	public static final String TREE_PATH = "treePath";

	public static final String TYPE = "type";

	public static final String UID = "uid";

	public static final String[] UNSCORED_FIELD_NAMES = {
		ASSET_CATEGORY_IDS, COMPANY_ID, ENTRY_CLASS_NAME, ENTRY_CLASS_PK,
		FOLDER_ID, GROUP_ID, GROUP_ROLE_ID, ROLE_ID, SCOPE_GROUP_ID,
		Field.USER_ID
	};

	public static final String URL = "url";

	public static final String USER_GROUP_ID = "userGroupId";

	public static final String USER_ID = "userId";

	public static final String USER_NAME = "userName";

	public static final String UUID = "uuid";

	public static final String VERSION = "version";

	public static final String VIEW_ACTION_ID = "viewActionId";

	public static final String VISIBILITY_TYPE = "visibilityType";

	public static String getLocalizedName(Locale locale, String name) {
		if (locale == null) {
			return name;
		}

		String languageId = LocaleUtil.toLanguageId(locale);

		return getLocalizedName(languageId, name);
	}

	public static String getLocalizedName(String languageId, String name) {
		return LocalizationUtil.getLocalizedName(name, languageId);
	}

	public static String getSortableFieldName(String name) {
		return StringBundler.concat(
			name, StringPool.UNDERLINE, SORTABLE_FIELD_SUFFIX);
	}

	public static String getSortFieldName(Sort sort, String scoreFieldName) {
		if (sort.getType() == Sort.SCORE_TYPE) {
			return scoreFieldName;
		}

		String fieldName = sort.getFieldName();

		if (isSortableFieldName(fieldName)) {
			return fieldName;
		}

		if ((sort.getType() == Sort.STRING_TYPE) &&
			!DocumentImpl.isSortableTextField(fieldName)) {

			return scoreFieldName;
		}

		if (fieldName.equals(Field.ENTRY_CLASS_PK)) {
			return fieldName;
		}

		return getSortableFieldName(fieldName);
	}

	public static String getUID(String portletId, String field1) {
		return getUID(portletId, field1, null);
	}

	public static String getUID(
		String portletId, String field1, String field2) {

		return getUID(portletId, field1, field2, null);
	}

	public static String getUID(
		String portletId, String field1, String field2, String field3) {

		return getUID(portletId, field1, field2, field3, null);
	}

	public static String getUID(
		String portletId, String field1, String field2, String field3,
		String field4) {

		String uid = portletId + _UID_PORTLET + field1;

		if (field2 != null) {
			uid += _UID_FIELD + field2;
		}

		if (field3 != null) {
			uid += _UID_FIELD + field3;
		}

		if (field4 != null) {
			uid += _UID_FIELD + field4;
		}

		return uid;
	}

	public static boolean isSortableFieldName(String name) {
		return name.endsWith(_SORTABLE_FIELD_SUFFIX);
	}

	public static boolean validateFieldName(String name) {
		if (name.contains(StringPool.COMMA) ||
			name.contains(StringPool.PERIOD) ||
			name.contains(StringPool.POUND) ||
			name.contains(StringPool.SLASH) || name.contains(StringPool.STAR) ||
			name.startsWith(StringPool.UNDERLINE)) {

			return false;
		}

		return true;
	}

	public Field(String name) {
		validate(name);

		_name = name;
	}

	public Field(String name, Map<Locale, String> localizedValues) {
		validate(name);

		_name = name;
		_localizedValues = localizedValues;
	}

	public Field(String name, String value) {
		this(name, new String[] {value});
	}

	public Field(String name, String[] values) {
		validate(name);

		_name = name;
		_values = values;
	}

	public void addField(Field field) {
		_fields.add(field);
	}

	public Date[] getDates() {
		return _dates;
	}

	public List<Field> getFields() {
		return _fields;
	}

	public GeoLocationPoint getGeoLocationPoint() {
		return _geoLocationPoint;
	}

	public Map<Locale, String> getLocalizedValues() {
		return _localizedValues;
	}

	public String getName() {
		return _name;
	}

	public Class<? extends Number> getNumericClass() {
		return _numericClass;
	}

	public Field getParentField() {
		return _parentField;
	}

	public String getValue() {
		if (ArrayUtil.isNotEmpty(_values)) {
			return _values[0];
		}

		return null;
	}

	public String[] getValues() {
		return _values;
	}

	public boolean hasChildren() {
		return !getFields().isEmpty();
	}

	public boolean isArray() {
		return false;
	}

	public boolean isDate() {
		if (_dates != null) {
			return true;
		}

		return false;
	}

	public boolean isLocalized() {
		if (_localizedValues != null) {
			return true;
		}

		return false;
	}

	public boolean isNested() {
		if (getParentField() != null) {
			return true;
		}

		return false;
	}

	public boolean isNumeric() {
		return _numeric;
	}

	public boolean isSortable() {
		return _sortable;
	}

	public boolean isTokenized() {
		return _tokenized;
	}

	public void setDates(Date[] dates) {
		_dates = dates;
	}

	public void setGeoLocationPoint(GeoLocationPoint geoLocationPoint) {
		_geoLocationPoint = geoLocationPoint;

		if (geoLocationPoint == null) {
			_values = null;
		}
		else {
			setValue(
				StringBundler.concat(
					"lat: ", geoLocationPoint.getLatitude(), ", lon: ",
					geoLocationPoint.getLongitude()));
		}
	}

	public void setLocalizedValues(Map<Locale, String> localizedValues) {
		_localizedValues = localizedValues;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setNumeric(boolean numeric) {
		_numeric = numeric;
	}

	public void setNumericClass(Class<? extends Number> numericClass) {
		_numericClass = numericClass;
	}

	public void setParentField(Field parentField) {
		_parentField = parentField;
	}

	public void setSortable(boolean sortable) {
		_sortable = sortable;
	}

	public void setTokenized(boolean tokenized) {
		_tokenized = tokenized;
	}

	public void setValue(String value) {
		setValues(new String[] {value});
	}

	public void setValues(String[] values) {
		_values = values;
	}

	public static class NestedFieldBuilder {

		public NestedFieldBuilder addNestedField(
			String name, String... values) {

			Field field = new Field(name);

			field.addField(new Field("value", values));

			_addField(field);

			return this;
		}

		public NestedFieldBuilder endArray() {
			return endField();
		}

		public NestedFieldBuilder endField() {
			if (_nestedFieldsBuilderFields.size() > 1) {
				_nestedFieldsBuilderFields.removeLast();
			}

			return this;
		}

		public Field getField() {
			if (!_nestedFieldsBuilderFields.isEmpty()) {
				return _nestedFieldsBuilderFields.getLast();
			}

			return null;
		}

		public NestedFieldBuilder startArray(String name) {
			FieldArray fieldArray = new FieldArray(name);

			return _startField(fieldArray);
		}

		public NestedFieldBuilder startField() {
			return startField(null);
		}

		public NestedFieldBuilder startField(String name) {
			Field field = new Field(name);

			return _startField(field);
		}

		private void _addField(Field field) {
			Field lastField = _nestedFieldsBuilderFields.getLast();

			lastField.addField(field);
		}

		private NestedFieldBuilder _startField(Field field) {
			if (!_nestedFieldsBuilderFields.isEmpty()) {
				_addField(field);
			}

			_nestedFieldsBuilderFields.add(field);

			return this;
		}

		private final LinkedList<Field> _nestedFieldsBuilderFields =
			new LinkedList<>();

	}

	protected void validate(String name) {
		if (name.contains(StringPool.COMMA)) {
			throw new IllegalArgumentException(
				"Name must not contain ,: " + name);
		}

		if (name.contains(StringPool.POUND)) {
			throw new IllegalArgumentException(
				"Name must not contain #: " + name);
		}

		if (name.contains(StringPool.SLASH)) {
			throw new IllegalArgumentException(
				"Name must not contain /: " + name);
		}

		if (name.contains(StringPool.STAR)) {
			throw new IllegalArgumentException(
				"Name must not contain *: " + name);
		}

		if (name.startsWith(StringPool.UNDERLINE)) {
			throw new IllegalArgumentException(
				"Name must not start with _: " + name);
		}
	}

	private static final String _SORTABLE_FIELD_SUFFIX = "sortable";

	private static final String _UID_FIELD = "_FIELD_";

	private static final String _UID_PORTLET = "_PORTLET_";

	private Date[] _dates;
	private final List<Field> _fields = new ArrayList<>();
	private GeoLocationPoint _geoLocationPoint;
	private Map<Locale, String> _localizedValues;
	private String _name;
	private boolean _numeric;
	private Class<? extends Number> _numericClass;
	private Field _parentField;
	private boolean _sortable;
	private boolean _tokenized;
	private String[] _values;

}